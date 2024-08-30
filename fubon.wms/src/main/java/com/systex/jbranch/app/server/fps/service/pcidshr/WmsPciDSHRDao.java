package com.systex.jbranch.app.server.fps.service.pcidshr;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBIOT_PREMATCHVO;
import com.systex.jbranch.app.server.fps.iot110.IOT110;
import com.systex.jbranch.app.server.fps.iot110.IOT110InputVO;
import com.systex.jbranch.app.server.fps.iot910.IOT910;
import com.systex.jbranch.app.server.fps.iot910.IOT910InputVO;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.app.server.fps.iot920.InsFundListInputVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("WmsPciDSHRDao")
public class WmsPciDSHRDao extends BizLogic implements WmsPciDSHRDaoInf{
	
	/**
	 * 保險作業管理系統(產險Property and Casualty Insurance)與北富銀保險資料檢核需求Web Service
	 * @throws Exception 
	 */
	public Map<String , Object> validatePciData(GenericMap paramMap) throws Exception {
		Map<String , Object> map = new HashMap<String , Object>();
		map.put("validateInsTerm", insTerminateChk(paramMap)); 		//行內保單解約檢核
		map.put("validateLoan", insLoanChk(paramMap)); 				//貸款或保單借款檢核
		map.put("validateCDTerm_A2", CDTermChkA2(paramMap));		//A2定存解約利息免打折檢核
		
		return map;
	}	
	
	/**
	 * 行內保單解約檢核
	 * 檢核客戶ID(要保人或被保人)93天內是否有保單解約
	 * 若要保人舊保單提領保額/保價日為要保書申請日前3個月，則行內解約也為Y
	 * 搜尋其他送件登錄作業，若符合以下條件，則行內保險單解約檢核也為Y
	 * 		A.文件種類=契變-解約/縮小保額
	 * 		B.ID符合其他送件案件的要保人ID
	 * 		C.新契約要保書申請日-93天<文件申請日<=系統日
	 *  @param paramMap
	 * @return N：要保人及被保人都沒有：投保前三個月有辦理契約終止(解約)
	 * 		   Y：要保人或被保人任一有：投保前三個月有辦理契約終止(解約)
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	private String insTerminateChk(GenericMap paramMap) throws DAOException, JBranchException, ParseException {
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		
		String custYN = iot920.insTerminateChk(custId, applyDate);
		String insuredYN = iot920.insTerminateChk(insuredId, applyDate);
		
		if("Y".equals(custYN) || "Y".equals(insuredYN)) {
			//要保人或被保人任一有：投保前三個月有辦理契約終止(解約)
			return "Y";
		} else {
			return "N";
		}
	}
	
	/***
	 * 要保人、被保人，投保前三個月內是否有辦理貸款或保險單借款的情形
	 * @param paramMap
	 * @return  N：要保人或被保人都沒有：辦理貸款或保險單借款的情形
	 * 			Y：要保人或被保人任一：有辦理貸款或保險單借款的情形
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws ParseException
	 */
	private String insLoanChk(GenericMap paramMap) throws DAOException, JBranchException, ParseException {
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		
		//貸款檢核(透過本行送件)
		String custYN1 = iot920.insLoanChk(custId, applyDate);
		String insuredYN1 = iot920.insLoanChk(insuredId, applyDate);
		//行內貸款檢核
		String custYN2 = (String)iot920.inHouseLoanChk(custId, applyDate).get("isInHouseLoan");
		String insuredYN2 = (String)iot920.inHouseLoanChk(insuredId, applyDate).get("isInHouseLoan");
		//93天內是否有行內貸款申請
		String custYN3 = (String)iot920.getCustLoanDate(custId, applyDate).get("isLoanApply");
		String insuredYN3 = (String)iot920.getCustLoanDate(insuredId, applyDate).get("isLoanApply");
		
		if("Y".equals(custYN1) || "Y".equals(insuredYN1) ||
				"Y".equals(custYN2) || "Y".equals(insuredYN2) ||
				"Y".equals(custYN3) || "Y".equals(insuredYN3)) {
			//要保人或被保人任一：有辦理貸款或保險單借款的情形
			return "Y";
		} else {
			return "N";
		}
	}	
	
	/***
	 * A2定存解約利息免打折檢核
	 * @param paramMap
	 * @return 	"N": 要保人及被保人，都沒有定存解約免打折
	 * 			"Y": 要保人、被保人，任一有定存解約免打折
	 * @throws ParseException
	 * @throws JBranchException
	 */
	private String CDTermChkA2(GenericMap paramMap) throws ParseException, JBranchException {
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		
		//要保人、被保人，任一有定存解約免打折，表示有定存解約免打折
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		String intCDChkYN = (StringUtils.equals("Y", iot920.intCDChk(custId, applyDate)) || 
							 StringUtils.equals("Y", iot920.intCDChk(insuredId, applyDate))) ? "Y" : "N";
		
		return intCDChkYN;
	}
	
}
