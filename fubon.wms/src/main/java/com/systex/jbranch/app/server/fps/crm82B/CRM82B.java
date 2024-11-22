package com.systex.jbranch.app.server.fps.crm82B;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;


@Component("crm82b")
@Scope("request")
public class CRM82B extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
    /**
     * 取得客戶金市SN債資料
     * @param body CRM82BInputVO
     * @param header
     * @throws Exception
     */
	public void getGoldSnAsset(Object body, IPrimitiveMap header) throws Exception {
		CRM82BInputVO inputVO = (CRM82BInputVO) body;
		CRM82BOutputVO outputVO = new CRM82BOutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		BigDecimal sumTrustVal = BigDecimal.ZERO; //庫存總金額
		
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT A.ISIN_Code, A.PROD_ID, A.PROD_NAME, A.CCY, A.M_TP_NOMINAL, ");
		sb.append(" A.M_TP_DTETRN, A.M_TP_DTEPMT, A.LAST_INTEREST_DATE, ");
		sb.append(" A.LAST_INTEREST_RATE, A.TOT_INTEREST_RATE, A.REF_NAV, A.DIV_RTN, ");
		sb.append(" SUM(NVL(A.BAL_AMT_TWD, 0)) AS SUMTrustVal, A.CUST_ID ");
		sb.append(" FROM TBCRM_AST_INV_VPSN A ");
		sb.append(" WHERE A.CUST_ID = :cust_id ");
		sb.append(" GROUP BY A.ISIN_Code, A.PROD_ID, A.PROD_NAME, A.CCY, A.M_TP_NOMINAL, ");
		sb.append("  A.M_TP_DTETRN, A.M_TP_DTEPMT, A.LAST_INTEREST_DATE, ");
		sb.append("  A.LAST_INTEREST_RATE, A.TOT_INTEREST_RATE, A.REF_NAV, A.DIV_RTN,A.CUST_ID ");
		
		qc.setObject("cust_id", inputVO.getCust_id());
		qc.setQueryString(sb.toString());
		resultList = dam.exeQuery(qc);
        
		if(!resultList.isEmpty()){
			sumTrustVal = (BigDecimal)resultList.get(0).get("SUMTrustVal"); //庫存總金額
		}
		
		outputVO.setResultList(resultList);
		outputVO.setSUMTrustVal(sumTrustVal);
		
		this.sendRtnObject(outputVO);
	}
	/**
     * 取得金市SN債-交易明細資料
     * @param body CRM82BInputVO
     * @param header
     * @throws Exception
     */
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM82BInputVO inputVO = (CRM82BInputVO) body;
		CRM82BOutputVO outputVO = new CRM82BOutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT A.INTEREST_DATE, A.TXN_TYPE, A.TXN_CCY, A.TXN_AMT ");
		sb.append(" FROM TBCRM_AST_INV_VPSN_TXN A ");
		sb.append(" WHERE A.PROD_ID =:prd_id AND TXN_TYPE = '2' ");

		qc.setObject("prd_id", inputVO.getPrd_id());
		qc.setQueryString(sb.toString());
		resultList = dam.exeQuery(qc);
		
		outputVO.setTxnList(resultList);
		this.sendRtnObject(outputVO);
	}
}