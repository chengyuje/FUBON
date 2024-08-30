package com.systex.jbranch.app.server.fps.crm831;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm831.CRM831;
import com.systex.jbranch.app.server.fps.crm831.CRM831InputVO;
import com.systex.jbranch.app.server.fps.crm831.CRM831OutputVO;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/06/01
 * 
 */
@Component("crm831")
@Scope("request")
public class CRM831 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM831.class);
	
	//北富銀保險資料
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM831InputVO inputVO = (CRM831InputVO) body;
		CRM831OutputVO return_VO = new CRM831OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT M.CONTRACT_STATUS, M.CUST_ID, COALESCE(PRD.INS_TYPE,'') AS INS_TYPE, ");
		sql.append(" CASE WHEN IS_NUMBER(SUBSTR(M.POLICY_NBR,1,2))=0 AND TRIM(M.ID_DUP) IS NULL THEN M.POLICY_NBR||'-'||M.POLICY_SEQ ");
		sql.append(" WHEN IS_NUMBER(SUBSTR(M.POLICY_NBR,1,2))=0 AND TRIM(M.ID_DUP) IS NOT NULL THEN M.POLICY_NBR||'-'||M.POLICY_SEQ||'-'||M.ID_DUP ");
		sql.append(" ELSE M.POLICY_NBR END POLICY_NBR_STR, ");
		sql.append("      M.POLICY_SIMP_NAME, M.POLICY_NBR, M.POLICY_SEQ, M.ID_DUP, M.INS_ID, M.INV_DATE_START, M.CRCY_TYPE, ");
		sql.append("      M.POLICY_ASSURE_AMT, M.UNIT, ");
		//sql.append("      M.POLICY_ASSURE_AMT_DIV10K, ");
		sql.append("      M.ACUM_INS_AMT_ORGD ");
		//sql.append("      SUM(A8.PAID_PREM) as ACUM_INS_AMT_ORGD ");
		sql.append(" FROM TBCRM_AST_INS_MAST M ");
		//sql.append(" INNER JOIN TBCRM_NPOLM TN ON M.POLICY_NBR = TN.POLICY_NO AND M.POLICY_SEQ = TN.POLICY_SEQ AND M.ID_DUP = TN.ID_DUP ");
		//sql.append(" LEFT JOIN TBCRM_NPOLD D ON M.POLICY_NBR = D.POLICY_NO AND  M.POLICY_SEQ = D.POLICY_SEQ AND M.ID_DUP = D.ID_DUP ");
		//sql.append(" LEFT JOIN ( ");
		//sql.append("	      SELECT T.*, ROW_NUMBER()OVER (PARTITION BY POLICY_NO, POLICY_SEQ, ID_DUP, ITEM, ID_NO ORDER BY CONF_DATE DESC) RN  FROM TBCRM_FBRNY8A0_SG T ");  
		//sql.append("        ) A8 ON D.POLICY_NO = A8.POLICY_NO AND D.POLICY_SEQ = A8.POLICY_SEQ AND D.ID_DUP = A8.ID_DUP AND D.INS_ITEM = A8.ITEM AND D.BELONG_ID = A8.ID_NO AND A8.RN = 1 "); 
		sql.append(" LEFT JOIN TBPRD_INSINFO PRD ON M.INS_TYPE = PRD.PRD_ID ");
		//sql.append(" WHERE TN.IS_CONSENT = 'Y' ");
		sql.append(" WHERE M.CONSENT_YN = 'Y' ");
		// TN.POLICY_STATUS
		sql.append("   AND M.CONTRACT_STATUS NOT IN ('06','11','12','13','14','15','17','20','21','22','23','24','25','26','27','28','29','30','31','32') ");
		
		//傳入all則查詢全部
		if(!StringUtils.equals(inputVO.getIns_type(), "all")){
			if(StringUtils.isNotEmpty(inputVO.getIns_type())){
				sql.append("and PRD.INS_TYPE = :ins_type ");
				queryCondition.setObject("ins_type", inputVO.getIns_type());
			}else{
				sql.append("and COALESCE(PRD.INS_TYPE,'') = '' ");
			}
		}
				
		sql.append("and M.CUST_ID = :cust_id ");
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		//sql.append(" GROUP BY M.CUST_ID,COALESCE(PRD.INS_TYPE,''),CASE WHEN IS_NUMBER(SUBSTR(M.POLICY_NBR,1,2))=0 AND TRIM(M.ID_DUP) IS NULL THEN M.POLICY_NBR||'-'||M.POLICY_SEQ ") 
		//   .append("    WHEN IS_NUMBER(SUBSTR(M.POLICY_NBR,1,2))=0 AND TRIM(M.ID_DUP) IS NOT NULL THEN M.POLICY_NBR||'-'||M.POLICY_SEQ||'-'||M.ID_DUP ")
		//   .append("     ELSE M.POLICY_NBR END,M.POLICY_SIMP_NAME, M.POLICY_NBR, M.POLICY_SEQ, M.ID_DUP, M.INS_ID, M.INV_DATE_START, M.CRCY_TYPE, ")
		//   .append("      M.POLICY_ASSURE_AMT, M.UNIT ")
		sql.append(" ORDER BY POLICY_NBR_STR ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	//日盛保代保險資料
	public void inquireJSB(Object body, IPrimitiveMap header) throws JBranchException {
		CRM831InputVO inputVO = (CRM831InputVO) body;
		CRM831OutputVO return_VO = new CRM831OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT M.*, COALESCE(PRD.PRODUCTCATEGORY,'') AS INS_TYPE_STR, M.POLICY_NBR AS POLICY_NBR_STR, NVL(IQ.BUY_RATE,1) AS BUY_RATE, ");
		sql.append(" CASE WHEN EXISTS (SELECT 1 FROM TBJSB_INS_BILL B ");
		sql.append(" 		WHERE B.YEARMONTH = (SELECT MAX(YEARMONTH) FROM TBJSB_INS_BILL) AND M.CUST_ID = B.APPL_ID AND M.POLICY_NBR = B.POLICY_NO) ");
		sql.append("		THEN 'Y' ELSE 'N' END AS INS_BILL_YN ");
		sql.append(" FROM TBJSB_AST_INS_MAST M ");
		sql.append(" LEFT JOIN TBJSB_INS_PROD_MAIN PRD ON PRD.INSURANCECOSERIALNUM = M.COM_ID AND PRD.PRODUCTID = M.INS_TYPE "); 
		sql.append(" LEFT JOIN TBPMS_IQ053 IQ ON M.CRCY_TYPE = IQ.CUR_COD AND IQ.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sql.append(" WHERE M.CUST_ID = :cust_id AND M.TX_SRC IN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.JSB_TX_SRC') ");
		sql.append(" AND M.BILL_REMRK IS NULL "); //BILL_REMRK空白才為有效保單
		
		//傳入all則查詢全部
		if(!StringUtils.equals(inputVO.getIns_type(), "all")){
			if(StringUtils.isNotEmpty(inputVO.getIns_type())){
				sql.append(" AND PRD.PRODUCTCATEGORY = :ins_type ");
				queryCondition.setObject("ins_type", inputVO.getIns_type());
			}
		}
		sql.append(" ORDER BY POLICY_NBR_STR ");
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());			
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		CRM831InputVO inputVO = (CRM831InputVO) body;
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//#0003081 : 繳費滿期日  = 保單生效日  * 繳費年期 - 1天
//		sql.append("SELECT AST.*,add_months(POLICY_ACTIVE_DATE,PAY_TERM_YEAR*12) as DUE_DATE FROM TBCRM_AST_INS_MAST AST where 1=1 ");
		
		sql.append(" SELECT M.CUST_ID, M.POLICY_NBR, M.POLICY_SEQ, M.ID_DUP, M.CONTRACT_STATUS, M.SYS_YEAR, M.SYS_MONTH, M.SYS_DAY,  ");
		sql.append(" D.INSURED_NAME INS_NAME, M.CRCY_TYPE, D.BELONG_ID INS_ID, D.PAY_TYPE, D.PROD_NAME_S POLICY_SIMP_NAME, COALESCE(PRD.INS_TYPE,'尚未定義') AS INS_TYPE, D.INS_ITEM AS PRD_ID, ");
		sql.append(" TFS.CUR_PERD TERM_CNT, CASE WHEN D.INS_NO='00' THEN 'Y' ELSE 'N' END MAST_SLA_TYPE,  ");
		sql.append(" M.COMM_PRJ_NAME, D.EX_INS_AMOUNT POLICY_ASSURE_AMT, D.EX_UNIT UNIT, M.INV_INS_AMT, D.EFF_DATE POLICY_ACTIVE_DATE, M.EXCH_RATE,    ");
		sql.append(" D.PAY_DUE_DATE AS DUE_DATE, M.AGMT_CRCY_TYPE_FORD, D.PREM_PAYABLE POLICY_FEE, TFS.SURR_AMT TERMI_AMT,  ");
		sql.append(" P.PAY_YEAR, P.PAY_TIME, TO_CHAR(TFS.CONF_DATE, 'YYYY') CAL_VALUE_YEAR,  TO_CHAR(TFS.CONF_DATE, 'MM') CAL_VALUE_MONTH, TO_CHAR(TFS.CONF_DATE, 'DD') CAL_VALUE_DAY,   ");
		sql.append(" M.PAID_TIMES, TFS.PAID_PREM ACUM_PAID_POLICY_FEE,M.POLICY_YEAR POLICY_YEAR,D.INS_YEAR_STR PAY_TERM_YEAR, ");
		sql.append(" m.SECRET_YN ");
		sql.append(" FROM TBCRM_AST_INS_MAST M ");
		sql.append("     INNER JOIN TBCRM_NPOLD D ON M.POLICY_NBR = D.POLICY_NO AND  M.POLICY_SEQ = D.POLICY_SEQ AND M.ID_DUP = D.ID_DUP ");
		sql.append("     LEFT JOIN ( ");
		sql.append("          SELECT T.*, ROW_NUMBER()OVER (PARTITION BY POLICY_NO, POLICY_SEQ, ID_DUP, ITEM, ID_NO ORDER BY CONF_DATE DESC) RN  "
				+ "FROM TBCRM_FBRNY8A0 T "
				+ "where snap_date = (select max(snap_date) from TBCRM_FBRNY8A0)  ");
		sql.append("          ) TFS ON D.POLICY_NO = TFS.POLICY_NO AND D.POLICY_SEQ = TFS.POLICY_SEQ AND D.ID_DUP = TFS.ID_DUP AND D.INS_ITEM = TFS.ITEM AND D.BELONG_ID = TFS.ID_NO AND TFS.RN = 1 ");
		sql.append("     LEFT JOIN ( ");
		sql.append("         SELECT * FROM (  ");
		sql.append("             SELECT P.*, ROW_NUMBER() OVER (PARTITION BY POLICY_NO, POLICY_SEQ, ID_DUP, INS_NO ORDER BY PAY_YEAR DESC, PAY_TIME DESC) RN  ");
		sql.append("             FROM TBCRM_AST_INS_PAYMENT P ");
		sql.append("             WHERE P.POLICY_NO = :policy_nbr AND P.POLICY_SEQ = :policy_seq AND P.ID_DUP = :id_dup ");
		sql.append("             )P WHERE P.RN=1 ");
		sql.append("          ) P ON D.POLICY_NO = P.POLICY_NO AND D.POLICY_SEQ = P.POLICY_SEQ AND D.ID_DUP = P.ID_DUP AND D.INS_NO = P.INS_NO ");
		sql.append("     LEFT JOIN TBPRD_INSINFO PRD ON M.INS_TYPE = PRD.PRD_ID ");
		sql.append(" WHERE 1 = 1 ");
		
		// where
		//客戶篩選
//		sql.append("and INS_TYPE = :ins_type ");
		sql.append(" AND M.CUST_ID = :cust_id ");
		sql.append(" AND M.POLICY_NBR = :policy_nbr ");
		sql.append(" AND M.POLICY_SEQ = :policy_seq ");
		sql.append(" AND M.ID_DUP = :id_dup ");
		sql.append(" ORDER BY D.INS_NO ");
		
		queryCondition.setQueryString(sql.toString());
		// where 2
		//客戶篩選
//		queryCondition.setObject("ins_type", inputVO.getIns_type());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("policy_nbr", inputVO.getPolicy_nbr());
		queryCondition.setObject("policy_seq", inputVO.getPolicy_seq());
		queryCondition.setObject("id_dup", inputVO.getId_dup());
		
		List list = dam.exeQuery(queryCondition);
		
		CRM831OutputVO outputVO = new CRM831OutputVO();
		outputVO.setResultList(list);
			
		
		//保單號碼最近一次繳費方式
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sql.append("SELECT PAY_WAY FROM TBCRM_AST_INS_PAYMENT WHERE 1=1 ");
		//問題單應該繳 0003755    保單基本資訊─保單主檔(TBCRM_NPOLM)     RENEWAL_FEE_TYPE 續期繳費方式  保單號碼最近一次繳費方式
		sql.append("SELECT RENEWAL_FEE_TYPE AS PAY_WAY FROM TBCRM_NPOLM WHERE 1=1 ");
		sql.append("AND POLICY_NO = :policy_nbr ");
		sql.append("AND POLICY_SEQ = :policy_seq ");
		sql.append("AND ID_DUP = :id_dup ");
//		sql.append("AND ROWNUM <= 1 ORDER BY RECORDED_DATE DESC ");
		sql.append("AND ROWNUM <= 1 ORDER BY EFF_DATE DESC ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("policy_nbr", inputVO.getPolicy_nbr());
		queryCondition.setObject("policy_seq", inputVO.getPolicy_seq());
		queryCondition.setObject("id_dup", inputVO.getId_dup());
		List list2 = dam.exeQuery(queryCondition);
		outputVO.setResultList2(list2);
		
		sendRtnObject(outputVO);
	}
	
	public void inquire3(Object body, IPrimitiveMap header) throws JBranchException {
		CRM831InputVO inputVO = (CRM831InputVO) body;
		CRM831OutputVO return_VO = new CRM831OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT POLICY_NO, POLICY_SEQ, ID_DUP , SUBSTR(RECEIPT_NO, 1, 9) RECEIPT_NO, ");
		sql.append("POLICY_CUR, APPL_DATE, RECORDED_DATE, PAY_YEAR, PAY_TIME, PAY_TYPE, PAY_WAY, SUM(ACCU_CUR_PREM) ACCU_CUR_PREM, CREDIT_CARD_NUM ");
		sql.append("FROM TBCRM_AST_INS_PAYMENT ");
		sql.append("WHERE POLICY_NO = :policy_nbr ");
		sql.append("AND POLICY_SEQ = :policy_seq ");
		sql.append("AND ID_DUP = :id_dup ");
		sql.append("GROUP BY POLICY_NO, POLICY_SEQ, ID_DUP , SUBSTR(RECEIPT_NO, 1, 9), POLICY_CUR, APPL_DATE, RECORDED_DATE, PAY_YEAR, PAY_TIME, PAY_TYPE, PAY_WAY, CREDIT_CARD_NUM ");
		sql.append("ORDER BY RECORDED_DATE DESC ");
		queryCondition.setQueryString(sql.toString());
		// where 2
		queryCondition.setObject("policy_nbr", inputVO.getPolicy_nbr());
		queryCondition.setObject("policy_seq", inputVO.getPolicy_seq());
		queryCondition.setObject("id_dup", inputVO.getId_dup());
		
		ResultIF list = dam.executePaging(queryCondition, inputVO
				.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList2(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(return_VO);
	}
	
	//日盛保代對帳單資料
	public void inquireBillJSB(Object body, IPrimitiveMap header) throws JBranchException {
		CRM831InputVO inputVO = (CRM831InputVO) body;
		CRM831OutputVO return_VO = new CRM831OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBJSB_INS_BILL ");
		sql.append(" WHERE APPL_ID = :cust_id AND YEARMONTH = :yearMonth ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("yearMonth", inputVO.getYear_mon());
		
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數

		this.sendRtnObject(return_VO);
	}
	
	public void inquireBillYearMon(Object body, IPrimitiveMap header) throws JBranchException {
		CRM831InputVO inputVO = (CRM831InputVO) body;
		CRM831OutputVO return_VO = new CRM831OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT YEARMONTH AS DATA, YEARMONTH AS LABEL FROM TBJSB_INS_BILL WHERE APPL_ID = :cust_id ");
		sql.append(" ORDER BY YEARMONTH DESC ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);

		this.sendRtnObject(return_VO);
	}
	
}