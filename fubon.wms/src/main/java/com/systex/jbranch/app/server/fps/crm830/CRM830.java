package com.systex.jbranch.app.server.fps.crm830;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm831.CRM831;
import com.systex.jbranch.app.server.fps.crm831.CRM831InputVO;
import com.systex.jbranch.app.server.fps.crm831.CRM831OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 31/5/2016
 * 
 */
@Component("crm830")
@Scope("request")
public class CRM830 extends FubonWmsBizLogic {
	private Logger logger = LoggerFactory.getLogger(CRM830.class);
	DataAccessManager dam_obj = null;
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM830InputVO inputVO = (CRM830InputVO) body;
		CRM830OutputVO return_VO = new CRM830OutputVO();
		
		return_VO.setResultList(this.getCustInsAUM(inputVO.getCust_id()));
		return_VO.setResultListJSB(this.getCustJSBInsAUM(inputVO.getCust_id()));
		
		sendRtnObject(return_VO);
	}
	
	/***
	 * 富壽保險庫存資料
	 * @param custID
	 * @return
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getCustInsAUM(String custID)  throws JBranchException {
		dam_obj = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COALESCE(PRD.INS_TYPE,'尚未定義') AS INS_TYPE, ");
		//sql.append("       SUM(ACUM_INS_AMT_ORGD) as TOTAL_INS_AMT_ORG, ");
		//sql.append("       SUM(CASE WHEN AST_INS.CRCY_TYPE = 'TWD' THEN ACUM_INS_AMT_ORGD ELSE ACUM_INS_AMT_ORGD * NVL(IQ.BUY_RATE,1) END) as TOTAL_INS_AMT_TWD  ");//要換算持台幣
		sql.append("        SUM(A8.PAID_PREM) as TOTAL_INS_AMT_ORG, ");
		sql.append("        SUM(CASE WHEN M.CRCY_TYPE = 'TWD' THEN A8.PAID_PREM ELSE A8.PAID_PREM * NVL(IQ.BUY_RATE,1) END) as TOTAL_INS_AMT_TWD ");
		sql.append("FROM TBCRM_AST_INS_MAST M ");
		//sql.append("JOIN TBCRM_NPOLM TN ON M.POLICY_NBR = TN.POLICY_NO AND M.POLICY_SEQ = TN.POLICY_SEQ AND M.ID_DUP = TN.ID_DUP ");
		sql.append("LEFT JOIN TBPMS_IQ053 IQ ON M.CRCY_TYPE = IQ.CUR_COD AND IQ.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sql.append("LEFT JOIN TBPRD_INSINFO PRD ON M.INS_TYPE = PRD.PRD_ID ");
		sql.append("LEFT JOIN TBCRM_NPOLD D ON M.POLICY_NBR = D.POLICY_NO AND  M.POLICY_SEQ = D.POLICY_SEQ AND M.ID_DUP = D.ID_DUP ");
		sql.append("LEFT JOIN ( ");
		sql.append("	      SELECT T.*, ROW_NUMBER()OVER (PARTITION BY POLICY_NO, POLICY_SEQ, ID_DUP, ITEM, ID_NO ORDER BY CONF_DATE DESC) RN  "
				+ "FROM TBCRM_FBRNY8A0 T "
				+ "where snap_date = (select max(snap_date) from TBCRM_FBRNY8A0) ");  
		sql.append("        ) A8 ON D.POLICY_No = A8.POLICY_NO AND D.POLICY_SEQ = A8.POLICY_SEQ AND D.ID_DUP = A8.ID_DUP AND D.INS_ITEM = A8.ITEM AND D.BELONG_ID = A8.ID_NO AND A8.RN = 1 "); 
		sql.append("WHERE ");
		sql.append("CUST_ID = :cust_id ");
		sql.append("AND M.CONSENT_YN = 'Y' ");
		//sql.append("AND TN.POLICY_STATUS NOT IN ('06','11','12','13','14','15','17','20','21','22','23','24','25','26','27','28','29','30','31','32') ");
		//sql.append("AND TN.IS_CONSENT = 'Y' ");
		sql.append("AND M.CONTRACT_STATUS NOT IN ('06','11','12','13','14','15','17','20','21','22','23','24','25','26','27','28','29','30','31','32') ");
		//sql.append("AND IQ.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sql.append("GROUP BY PRD.INS_TYPE");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", custID);
		
		List<Map<String, Object>> resultList = dam_obj.exeQuery(queryCondition);
		
		return resultList;
	}
	
	/***
	 * 日盛保險庫存資料
	 * @param custID
	 * @return
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getCustJSBInsAUM(String custID)  throws JBranchException {
		dam_obj = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COALESCE(PRD.PRODUCTCATEGORY, '尚未定義') AS INS_TYPE, ");
		sql.append("        SUM(M.ACUM_INS_AMT_ORGD) as TOTAL_INS_AMT_ORG, ");
		sql.append("        SUM(CASE WHEN M.CRCY_TYPE = 'TWD' THEN M.ACUM_INS_AMT_ORGD ELSE M.ACUM_INS_AMT_ORGD * NVL(IQ.BUY_RATE,1) END) as TOTAL_INS_AMT_TWD ");
		sql.append(" FROM TBJSB_AST_INS_MAST M ");
		sql.append(" LEFT JOIN TBPMS_IQ053 IQ ON M.CRCY_TYPE = IQ.CUR_COD AND IQ.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sql.append(" LEFT JOIN TBJSB_INS_PROD_MAIN PRD ON PRD.INSURANCECOSERIALNUM = M.COM_ID AND PRD.PRODUCTID = M.INS_TYPE "); 
		sql.append(" WHERE M.CUST_ID = :cust_id AND M.TX_SRC IN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.JSB_TX_SRC') ");
		sql.append(" AND M.BILL_REMRK IS NULL "); //BILL_REMRK空白才為有效保單
		sql.append(" GROUP BY PRD.PRODUCTCATEGORY ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", custID);
		
		List<Map<String, Object>> resultList = dam_obj.exeQuery(queryCondition);
		
		return resultList;
	}
}