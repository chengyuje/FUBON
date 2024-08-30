package com.systex.jbranch.app.server.fps.sot312;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot707.BondGTCDataVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707;
import com.systex.jbranch.app.server.fps.sot707.SOT707InputVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/09/13
 * 
 */
@Component("sot312")
@Scope("request")
public class SOT312 extends FubonWmsBizLogic {

	private DataAccessManager dam;
	
	public void query(Object body, IPrimitiveMap header) throws Exception {

		SOT312InputVO inputVO = (SOT312InputVO) body;
		SOT312OutputVO outputVO = new SOT312OutputVO();

		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707 = new SOT707OutputVO();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		boolean openOpt = false;
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
			!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sb.append("SELECT COUNT(1) AS COUNTS ");
			sb.append("FROM TBCRM_CUST_MAST CM ");
			sb.append("WHERE EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE CM.AO_CODE = U.UHRM_CODE) ");
			sb.append("AND CM.CUST_ID = :custID ");
			
			queryCondition.setObject("custID", inputVO.getCustID());
			queryCondition.setQueryString(sb.toString().replaceAll("\\s+", " "));

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			if (((BigDecimal) list.get(0).get("COUNTS")).compareTo(BigDecimal.ONE) >= 0) {
				openOpt = true;
			}
		} else {
			openOpt = true;
		}
		
		if (openOpt) {
			inputVO_707.setCustId(inputVO.getCustID());
			inputVO_707.setProdType(StringUtils.equals("1", inputVO.getTradeType()) ? "B" : (StringUtils.equals("2", inputVO.getTradeType()) ? "S" : ""));
			inputVO_707.setProdId(inputVO.getProdID());
			inputVO_707.setStartDate(inputVO.getsDate());
			inputVO_707.setEndDate(inputVO.geteDate());
			inputVO_707.setIsOBU(isOBU(inputVO.getCustID()));

			SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
			outputVO_707 = sot707.getBondGTCData(inputVO_707);
			this.maskCustID(outputVO_707.getBondGTCDataList());
			outputVO.setGtcList(outputVO_707.getBondGTCDataList());
		}

		this.sendRtnObject(outputVO);
	}
	
	private void maskCustID(List<BondGTCDataVO> dataList) {
		for (BondGTCDataVO vo : dataList) {
			vo.setCustId(DataFormat.getCustIdMaskForHighRisk(vo.getCustId()));
		}
	}

	public void queryDetails(Object body, IPrimitiveMap header) throws Exception {

		SOT312InputVO inputVO = (SOT312InputVO) body;
		SOT312OutputVO outputVO = new SOT312OutputVO();

		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707 = new SOT707OutputVO();

		inputVO_707.setGtcNo(inputVO.getGtcNo());
		inputVO_707.setIsOBU(isOBU(inputVO.getCustID()));
		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
		outputVO_707 = sot707.getBondGTCDataDetail(inputVO_707);

		outputVO.setGtcDetailList(outputVO_707.getBondGTCDataDetailList());

		this.sendRtnObject(outputVO);
	}

	/***
	 * 是否為OBU客戶
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	private String isOBU(String custId) throws Exception {
		String rtnVal = "N";
		
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO outputVO_701 = new FP032675DataVO();

		inputVO_701.setCustID(custId);
		
		try {
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			outputVO_701 = sot701.getFP032675Data(inputVO_701);
			rtnVal = outputVO_701.getObuFlag();
		} catch(Exception e) {
		}
		
		return rtnVal;
	}
}
