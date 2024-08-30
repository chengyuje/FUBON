/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1. 讀取DB產出 .csv
 * 2. 將.csv 上傳FTP
 * 
 * @author 1500617
 * @date 8/11/2016
 *
 * @author pcc761
 * @date 2016/11/14
 * 增加CAM028更新回應日期之方法
 */
@Repository("pabth")
@Scope("prototype")
public class PABTH extends BizLogic {
	
	private AuditLogUtil audit = null;
	private DataAccessManager dam = null;
	private CSVUtil csvUtil = null;
//	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	public void BTREF001(Object body, IPrimitiveMap<?> header) throws Exception {
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		// 取得傳入參數
		@SuppressWarnings("unchecked")
		Map<String, Object> inputMap = (Map<String, Object>) body;
		@SuppressWarnings("unchecked")
		Map<String, Object> jobParam = (Map<String, Object>) inputMap
				.get(SchedulerHelper.JOB_PARAMETER_KEY);
		
		String ftpCode = (String)jobParam.get("ftpCode"); // TBSYSFTP.FTPSETTINGID
		String fileName = (String)jobParam.get("fileName");
		
		String[] csvHeader = { "SEQ", "REF_CON_ID", "TERRITORY_ID", "TXN_DATE",
				"REF_SRC ", "REF_ORG_ID", "CUST_ID", "CUST_NAME",
				"SALES_PERSON ", "SALES_NAME", "REF_PROD", "CASE_PROPERTY",
				"LOAN_PRJ_ID", "CONTACT_DTTM", "CONT_RSLT", "COMMENTS",
				"ROW_LASTMANT_DTTM", "ROW_LASTMANT_OPRID", "CASEID",
				"CANCEL_FLAG", "SALES_ROLE", "USERID", "USERNAME", "USERROLE",
				"BUY_PROD_TYPE", "MEMO", "NON_GRANT_REASON", "REF_FEE",
				"IF_APPROVAL_FLAG", "BUY_PROD_TYPE_D" };
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQ,");
		sb.append(" REF_CON_ID,");
		sb.append(" TERRITORY_ID,");
		sb.append(" TXN_DATE,");
		sb.append(" REF_SRC,");
		sb.append(" REF_ORG_ID,");
		sb.append(" CUST_ID,");
		sb.append(" CUST_NAME,");
		sb.append(" SALES_PERSON,");
		sb.append(" SALES_NAME,");
		sb.append(" REF_PROD,");
		sb.append(" CASE_PROPERTY,");
		sb.append(" LOAN_PRJ_ID,");
		sb.append(" CONTACT_DTTM,");
		sb.append(" CONT_RSLT,");
		sb.append(" COMMENTS,");
		sb.append(" LASTUPDATE AS ROW_LASTMANT_DTTM,");
		sb.append(" MODIFIER AS ROW_LASTMANT_OPRID,");
		sb.append(" CASEID,");
		sb.append(" CANCEL_FLAG,");
		sb.append(" SALES_ROLE,");
		sb.append(" USERID,");
		sb.append(" USERNAME,");
		sb.append(" USERROLE,");
		sb.append(" BUY_PROD_TYPE,");
		sb.append(" MEMO,");
		sb.append(" NON_GRANT_REASON,");
		sb.append(" REF_FEE,");
		sb.append(" IF_APPROVAL_FLAG,");
		sb.append(" BUY_PROD_TYPE_D");
		sb.append(" FROM TBCAM_LOAN_SALEREC");
		sb.append(" WHERE TRUNC(LASTUPDATE) BETWEEN ");
		sb.append("  TRUNC(SYSDATE - INTERVAL '30' DAY) and TRUNC(SYSDATE) ");
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sb.toString());
		@SuppressWarnings("unchecked")
		List<Map<String, String>> dataList = dam.exeQuery(queryCondition);
		
		genCsv(fileName, csvHeader, dataList);
		
		ftpUpload(ftpCode);
	}
	
	public void BTCAM028(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		// 取得傳入參數
		@SuppressWarnings("unchecked")
		Map<String, Object> inputMap = (Map<String, Object>) body;
		@SuppressWarnings("unchecked")
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String paraDate	= (String) jobParam.get("paraDate");
		
		// 2017/10/6 russle add
		this.insertLeadsRes(dam, paraDate);
		
		// 主檔
		String ftpCode = (String) jobParam.get("ftpCode"); // TBSYSFTP.FTPSETTINGID
		String fileName = (String) jobParam.get("fileName");
		// 檢核檔
		String ftpCode2 = (String) jobParam.get("ftpCode2");
		String fileName2 = (String) jobParam.get("fileName2");
		
		String[] csvHeader = { "SFA_LEAD_ID", "CUST_ID", "CAMP_RESP_CODE",
				"SFA_RESP_CODE", "RESP_DATE", "LEAD_TYPE" };
		
		StringBuffer sb = new StringBuffer();	
		sb.append(" SELECT SFA_LEAD_ID, CUST_ID, CAMP_RESP_CODE, SFA_RESP_CODE,  ");
		sb.append(" TO_CHAR(RESP_DATE, 'yyyymmddhh24miss') AS RESP_DATE, LEAD_TYPE  ");
		sb.append(" FROM TBCAM_SFA_LEADS_RES  ");
		sb.append(" WHERE LEAD_TYPE <> '07' AND TRUNC(CREATETIME) = TRUNC(NVL(TO_DATE( :setDate, 'yyyyMMdd'), sysdate))  ");
		sb.append(" UNION  ");
		sb.append(" SELECT A.SFA_LEAD_ID, substr(B.VAR_FIELD_VALUE2, INSTR(B.VAR_FIELD_VALUE2, '-', -1, 1)+1) AS CUST_ID,  ");
		sb.append(" A.CAMP_RESP_CODE, A.SFA_RESP_CODE, TO_CHAR(A.RESP_DATE, 'yyyymmddhh24miss') AS RESP_DATE, A.LEAD_TYPE  ");
		sb.append(" FROM TBCAM_SFA_LEADS_RES A  ");
		sb.append(" LEFT JOIN TBCAM_SFA_LEADS_VAR B ON B.SFA_LEAD_ID = A.SFA_LEAD_ID  ");
		sb.append(" WHERE A.LEAD_TYPE = '07' AND TRUNC(A.CREATETIME) = TRUNC(NVL(TO_DATE( :setDate, 'yyyyMMdd'), sysdate))  ");
				
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("setDate", paraDate);
		
		@SuppressWarnings("unchecked")
		List<Map<String, String>> dataList = dam.exeQuery(queryCondition);

		updateRespDate(dataList);

		// 主檔
		genCsv(fileName, csvHeader, dataList);
		ftpUpload(ftpCode);
		
		// 檢核檔
		genCsv2(fileName2, dataList.size());
		ftpUpload(ftpCode2);
	}
	
	private void  insertLeadsRes(DataAccessManager dam, String paraDate) throws Exception {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.LEAD_ID, A.CUST_ID, A.LEAD_TYPE, B.REL_EMP_ID, B.REL_DATETIME FROM TBCAM_SFA_LE_IMP_TEMP A ");
		sql.append("LEFT JOIN tbcam_sfa_leads_imp B ON B.seqno = A.imp_seqno ");
		sql.append("WHERE A.IMP_SEQNO IN ( ");
		sql.append("SELECT SEQNO FROM tbcam_sfa_leads_imp ");
		sql.append("WHERE trunc(rel_datetime) = TRUNC(NVL(TO_DATE( :setDate, 'yyyyMMdd'), sysdate)) ");
		sql.append("AND check_status = '03' AND imp_status = 'IN' AND LEAD_SOURCE_ID = '01' ");
		sql.append(") ");
		sql.append("MINUS ");
		sql.append("SELECT SFA_LEAD_ID, CUST_ID, LEAD_TYPE, CREATOR AS REL_EMP_ID, createtime AS REL_DATETIME FROM tbcam_sfa_leads_res ");
		sql.append("WHERE camp_resp_code = 'C' ");
		sql.append("AND TRUNC(createtime) = TRUNC(NVL(TO_DATE( :setDate, 'yyyyMMdd'), sysdate)) ");
		queryCondition.setObject("setDate", paraDate);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		dam.setAutoCommit(true);
		for(Map<String, Object> map : list) {
			QueryConditionIF sq_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sq_con.setQueryString("SELECT SQ_TBCAM_SFA_LEADS_RES.nextval AS SEQ FROM DUAL");
			List<Map<String, Object>> SEQLIST = dam.exeQuery(sq_con);
			BigDecimal seqNo = new BigDecimal(ObjectUtils.toString(SEQLIST.get(0).get("SEQ")));
			
			QueryConditionIF ins_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			ins_con.setQueryString("INSERT INTO TBCAM_SFA_LEADS_RES (SEQNO, SFA_LEAD_ID, CUST_ID, CAMP_RESP_CODE, SFA_RESP_CODE, LEAD_TYPE, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) VALUES (:seq, :lead_id, :cust_id, 'C', '', :lead_type, 0, :user_dt, :user_id, :user_id, :user_dt) ");
			ins_con.setObject("seq", seqNo);
			ins_con.setObject("lead_id", map.get("LEAD_ID"));
			ins_con.setObject("cust_id", map.get("CUST_ID"));
			ins_con.setObject("lead_type", map.get("LEAD_TYPE"));
			ins_con.setObject("user_id", map.get("REL_EMP_ID"));
			ins_con.setObject("user_dt", map.get("REL_DATETIME"));
			dam.exeUpdate(ins_con);
		}
		dam.setAutoCommit(false);
	}
	
	private String genCsv(String fileName, String[] csvHeader,
			List<Map<String, String>> dataList) throws JBranchException {
		List<Object[]> csvData = new ArrayList<Object[]>();
		for (Map<String, String> map : dataList) {
			String[] records = new String[csvHeader.length];
			
			for (int i = 0; i < csvHeader.length; i++) {
				records[i] = checkIsNull(map, csvHeader[i]);
			}
			csvData.add(records);
		}
		
		csvUtil = new CSVUtil();
//		csvUtil.setPath(srcDir);
		csvUtil.setFileName(fileName);
		
		if (csvHeader != null && csvHeader.length > 0) {
			csvUtil.setHeader(csvHeader);
		}
			  
		csvUtil.addRecordList(csvData);
		
		return csvUtil.generateCSV();
	}
	
	private String genCsv2(String fileName, int cnt) throws JBranchException {
		Object[] data = new Object[1];
		data[0] = cnt;
		
		
		csvUtil = new CSVUtil();
//		csvUtil.setPath(srcDir);
		
		String[] csvHeader = { "QUANTITY" };
		csvUtil.setHeader(csvHeader);
		
		csvUtil.setFileName(fileName);
		csvUtil.addRecord(data);
		
		return csvUtil.generateCSV();
	}
	
	private void ftpUpload(String ftpCode) throws Exception {
		try {
			// 使用時在 new 出來用就好，否則會有全域變數控管問題
			new BthFtpJobUtil().ftpPutFile(ftpCode);
		} catch (Exception e) {
			audit.audit(ExceptionUtils.getStackTrace(e));
		}
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull(Map<String, String> map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 回壓回應日期
	 *
	 * @param dataList
	 * @throws JBranchException
     */
	private void updateRespDate(List<Map<String, String>> dataList) throws JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//updated tables
		//String[] tables = {"TBCAM_SFA_LEADS", "TBCAM_SFA_LEADS_RES"};
		String[] tables = {"TBCAM_SFA_LEADS"};
		
		for(Map<?, ?> map : dataList) {
			String sfaLeadId = (String) map.get("SFA_LEAD_ID");

			for (String table : tables) {
				String sql = new StringBuffer("UPDATE ")
						.append(table)
						.append(" SET RESP_DATE = SYSDATE WHERE SFA_LEAD_ID IN (:SFA_LEAD_ID)").toString();

				condition.setObject("SFA_LEAD_ID", sfaLeadId);
				condition.setQueryString(sql);
				dam.exeUpdate(condition);
			}
		}
	}
	
}
