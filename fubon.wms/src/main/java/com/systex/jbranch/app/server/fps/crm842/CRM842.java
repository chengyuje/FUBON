package com.systex.jbranch.app.server.fps.crm842;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.SC120100Service;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100DetailOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm842")
@Scope("request")
public class CRM842 extends FubonWmsBizLogic {
	
	@Autowired
	private CBSService cbsservice;
	
	@Autowired
	private SC120100Service sc120100Service;

	public DataAccessManager dam = null;
	
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		
		CRM842OutputVO outputVO = new CRM842OutputVO();
		CRM842InputVO inputVO = (CRM842InputVO) body;
		dam = this.getDataAccessManager();	
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
		StringBuffer sb = new StringBuffer();	
		
		String custId = inputVO.getCust_id().trim();
		
		//發送電文
		List<SC120100OutputVO> vos = sc120100Service.searchCirculation(custId);
		List<CRM842LoanCmkVO> results = new ArrayList<>();
		List<SC120100DetailOutputVO> results1 = new ArrayList<>();
		List<SC120100DetailOutputVO> results2 = new ArrayList<>();
		List<SC120100DetailOutputVO> results3 = new ArrayList<>();
		List<SC120100DetailOutputVO> results4 = new ArrayList<>();
		List<SC120100DetailOutputVO> results5 = new ArrayList<>();

		for (SC120100OutputVO sc120100OutputVO : vos) {
			List<SC120100DetailOutputVO> details = sc120100OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<SC120100DetailOutputVO>() : details;

			for (SC120100DetailOutputVO data : details) {
				String acctType = data.getWA_X_ATYPE().trim();
				
				CRM842LoanCmkVO cmkVO = new CRM842LoanCmkVO();
				
				cmkVO.setRATE(cbsservice.amountFormat(data.getRATE(), 7));
				cmkVO.setACNO(data.getACNO());
				cmkVO.setACNO_SA(data.getACNO_SA());
				cmkVO.setCUR_COD(data.getCUR_COD());
				cmkVO.setINT_CYCLE(cbsservice.changeDateView(data.getINT_CYCLE(), "2"));
				cmkVO.setDUE_DATE(cbsservice.changeDateView(data.getDUE_DATE(), "3"));

				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
				sb = new StringBuffer();	
				sb.append("SELECT ACHIVE_ORG_NM FROM TBCRM_LOAN_CASE WHERE ACNO_LN = :ACNO_LN ");
				queryCondition.setQueryString(sb.toString());
				queryCondition.setObject("ACNO_LN", data.getACNO());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
				if (list.size() > 0) {
					cmkVO.setACHIVE_ORG_NM((String) list.get(0).get("ACHIVE_ORG_NM"));
				}
				
				//循環型信貸(額度式)
				if (cbsservice.isHomeCycleLoan(acctType)) {
					if (Arrays.asList(new String[] { "5101", "5102" }).contains(acctType)) {
						cmkVO.setTYPE("循環型信貸(額度式)");
					} else {
						cmkVO.setTYPE("循環型房貸(額度式)");
					}
					
					cmkVO.setORI_LOAN_BAL(data.getORI_LOAN_BAL());
					cmkVO.setINS_AMT(data.getINS_AMT());
					cmkVO.setACT_BAL(data.getACT_BAL_NT());
					cmkVO.setK_RATE(getKRATE(custId, data.getACNO()));
					
					results.add(cmkVO);
				}

				//循環型房貸(回復式)
				if (cbsservice.isHomeRecLoan(acctType)) {
					cmkVO.setORI_LOAN_BAL(data.getORI_LOAN_BAL());
					cmkVO.setINS_AMT(data.getINS_AMT());
					cmkVO.setACT_BAL(data.getACT_BAL_NT());
					cmkVO.setAVAL_AMT(cbsservice.amountFormat(data.getAVAL_AMT()));
					
					results1.add(cmkVO);
				}
			}
			
			//綜存質借
			for (SC120100DetailOutputVO data : details) {
				String acctType = data.getWA_X_ATYPE().trim();
				
				CRM842LoanCmkVO cmkVO = new CRM842LoanCmkVO();
				
				cmkVO.setRATE(cbsservice.amountFormat(data.getRATE(), 7));
				cmkVO.setORI_LOAN_BAL(data.getORI_LOAN_BAL());
				cmkVO.setINS_AMT(data.getINS_AMT());
				cmkVO.setACT_BAL(data.getACT_BAL_NT());
				cmkVO.setAVAL_AMT(cbsservice.amountFormat(data.getAVAL_AMT()));
				cmkVO.setACNO(data.getACNO());
				cmkVO.setACNO_SA(data.getACNO_SA());
				cmkVO.setCUR_COD(data.getCUR_COD());
				cmkVO.setINT_CYCLE(cbsservice.changeDateView(data.getINT_CYCLE(), "2"));
				cmkVO.setDUE_DATE(cbsservice.changeDateView(data.getDUE_DATE(), "3"));

				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
				sb = new StringBuffer();	
				sb.append("SELECT ACHIVE_ORG_NM FROM TBCRM_LOAN_CASE WHERE ACNO_LN = :ACNO_LN ");
				queryCondition.setQueryString(sb.toString());
				queryCondition.setObject("ACNO_LN", data.getACNO());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
				if (list.size() > 0) {
					cmkVO.setACHIVE_ORG_NM((String) list.get(0).get("ACHIVE_ORG_NM"));
				}
				
				//綜存質借(台幣)
				if (cbsservice.isCreditLoan(acctType) && "TWD".equals(data.getCUR_COD())) {
					results2.add(cmkVO);
				}
				
				//綜存質借(非台幣　一本萬利外幣)
				if (cbsservice.isCreditLoan(acctType) && !"TWD".equals(data.getCUR_COD()) & cbsservice.is168(data.getACNO())) {
					results3.add(cmkVO);
				}
				
				//綜存質借(非台幣　一般外幣外幣)
				if (cbsservice.isCreditLoan(acctType) && !"TWD".equals(data.getCUR_COD()) & !cbsservice.is168(data.getACNO())) {
					results4.add(cmkVO);
				}

				//信託質借(循環型) 在CRM847
				//				if(Integer.parseInt(data.getWA_X_ATYPE().trim()) >= 5500 & Integer.parseInt(data.getWA_X_ATYPE().trim()) < 5600){
				//					cmkVO.setK_RATE(getKRATE(IDNO, data.getACNO()));
				//					results5.add(cmkVO);
				//				}
			}
		}
		
		outputVO.setResultList(results);
		outputVO.setResultList1(results1);
		outputVO.setResultList2(results2);
		outputVO.setResultList3(results3);
		outputVO.setResultList4(results4);
		outputVO.setResultList5(results5);

		this.sendRtnObject(outputVO);
	}

	/**
	 * 取得維持率，以客戶ID以及帳號前14碼勾稽
	 */
	public BigDecimal getKRATE(String custID, String acno) throws JBranchException {
		
		BigDecimal krate = BigDecimal.ZERO;
		String acno14 = acno.trim();

		if (StringUtils.isNotBlank(acno14) && acno14.length() < 14) {
			DecimalFormat df = new DecimalFormat("00000000000000");
			acno14 = df.format(Long.valueOf(acno14));
		}

		if (StringUtils.isNotBlank(acno14)) {
			DataAccessManager dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT K_RATE ");
			sql.append("FROM TBCRM_LM5800Q2 ");
			sql.append("WHERE CUST_ID =:cust_id ");
			sql.append("AND (CASE WHEN LENGTH(TRIM(LM_ACNO)) < 14 THEN LPAD(TRIM(LM_ACNO), 14, 0) ELSE SUBSTR(TRIM(LM_ACNO), 1, 14) END) =:acno ");

			queryCondition.setObject("cust_id", custID);
			queryCondition.setObject("acno", acno14.substring(0, 14));

			queryCondition.setQueryString(sql.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (CollectionUtils.isNotEmpty(list) && list.get(0).get("K_RATE") != null) {
				krate = (BigDecimal) list.get(0).get("K_RATE");
			}
		}

		return krate;
	}
}