package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 2022/08/25 - Ocean.Lin - WMS-CR-20220722-01_新增高齡客戶評量表維護查詢功能
 */

@Repository("btcrm512")
@Scope("prototype")
public class BTCRM512 extends BizLogic {

	DataAccessManager dam;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void createFileBth(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {

		String arg = null;
		if (null != ((Map) ((Map) body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("arg")) {
			arg = sdfYYYYMMDD.format(sdfYYYYMMDD.parse((String) (((Map) ((Map) body).get(SchedulerHelper.JOB_PARAMETER_KEY)).get("arg"))));
		}

		ODS_QUS_BANK(arg);
		ODS_CUS_REPLY(arg);
		CORE_TODO(arg);
		CORE_CUS_REPLY(arg);
	}

	// ODS_高齡客戶評估題庫_CSV
	public void ODS_QUS_BANK(String arg) throws JBranchException, Exception {

		Context intitCtx = new InitialContext();
		Context envCtx = (Context) intitCtx.lookup("java:comp/env");
		DataSource dataSource = (DataSource) envCtx.lookup("jdbc/esoaf_mysql");
		java.sql.Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT QN.EXAM_VERSION, ");
		sb.append("       QN.QUESTION_VERSION, ");
		sb.append("       QN.QST_NO || '' AS QST_NO, ");
		sb.append("       P_QUS_C.PARAM_NAME AS QUESTION_CLASS_NAME, ");
		sb.append("       P_QUS_N.PARAM_NAME AS QUESTION_NAME_NAME, ");
		sb.append("       Q.QUESTION_DESCR, ");
		sb.append("       Q.QUESTION_REMARK, ");
		sb.append("       Q.QUESTION_TYPE, ");
		sb.append("       Q.REQUIRED_YN, ");
		sb.append("       Q.CAN_MODIFY_PRI_LIST, ");
		sb.append("       Q.SYSTEM_CHK_YN, ");
		sb.append("       A.ANSWER_SEQ || '' AS ANSWER_SEQ, ");
		sb.append("       A.ANSWER_DESC ");
		sb.append("FROM WMSHC.TBCRM_OLD_AGE_QUESTIONNAIRE QN ");
		sb.append("LEFT JOIN WMSHC.TBCRM_OLD_AGE_QST_QUESTION Q ON Q.QUESTION_VERSION = QN.QUESTION_VERSION ");
		sb.append("LEFT JOIN WMSHC.TBCRM_OLD_AGE_QST_ANSWER A ON Q.QUESTION_VERSION = A.QUESTION_VERSION ");
		sb.append("LEFT JOIN WMSHC.TBSYSPARAMETER P_QUS_C ON P_QUS_C.PARAM_TYPE = 'CRM.OLD_QUS_CLASS' AND Q.QUESTION_CLASS = P_QUS_C.PARAM_CODE ");
		sb.append("LEFT JOIN WMSHC.TBSYSPARAMETER P_QUS_N ON P_QUS_N.PARAM_TYPE = 'CRM.OLD_QUS_NAME' AND Q.QUESTION_NAME = P_QUS_N.PARAM_CODE ");
		sb.append("WHERE EXISTS (SELECT 1 FROM WMSHC.TBCRM_OLD_AGE_QUESTIONNAIRE OAQ WHERE QN.EXAM_VERSION = OAQ.EXAM_VERSION GROUP BY OAQ.EXAM_VERSION) ");
		sb.append("ORDER BY QN.QST_NO, A.ANSWER_SEQ ");
		
		ResultSet result = stmt.executeQuery(sb.toString());
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (result.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("EXAM_VERSION", result.getString(1));
			map.put("QUESTION_VERSION", result.getString(2));
			map.put("QST_NO", result.getString(3));
			map.put("QUESTION_CLASS_NAME", result.getString(4));
			map.put("QUESTION_NAME_NAME", result.getString(5));
			map.put("QUESTION_DESCR", result.getString(6));
			map.put("QUESTION_REMARK", result.getString(7));
			map.put("QUESTION_TYPE", result.getString(8));
			map.put("REQUIRED_YN", result.getString(9));
			map.put("CAN_MODIFY_PRI_LIST", result.getString(10));
			map.put("SYSTEM_CHK_YN", result.getString(11));
			map.put("ANSWER_SEQ", result.getString(12));
			map.put("ANSWER_DESC", result.getString(13));
			
			list.add(map);
		}
		
		result.close();
		stmt.close();
		conn.close();

		String[] order = { "EXAM_VERSION", "QUESTION_VERSION", "QST_NO", "QUESTION_CLASS_NAME", "QUESTION_NAME_NAME", "QUESTION_DESCR", "QUESTION_REMARK", "QUESTION_TYPE", "REQUIRED_YN", "CAN_MODIFY_PRI_LIST", "SYSTEM_CHK_YN", "ANSWER_SEQ", "ANSWER_DESC" };

		toSpecifiedLocation("EVALUATE_SENIOR_QUES_BANK_" + (null != arg ? arg : sdfYYYYMMDD.format(new Date())), "CSV", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), list, order, ",");
	}

	// ODS_高齡客戶評估回覆記錄_CSV (產出前一日異動)
	public void ODS_CUS_REPLY(String arg) throws JBranchException, Exception {

		Context intitCtx = new InitialContext();
		Context envCtx = (Context) intitCtx.lookup("java:comp/env");
		DataSource dataSource = (DataSource) envCtx.lookup("jdbc/esoaf_mysql");
		java.sql.Connection conn = dataSource.getConnection();
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DATE_FORMAT(CHG_DATE, '%Y/%m/%d %H:%i:%s') AS DATETIME, ");
		sb.append("       IFNULL(CHG_DEPT_ID, ' ') AS BRANCH_NBR, ");
		sb.append("       IFNULL(CHG_EMP_ID, ' ') AS EMP_ID, ");
		sb.append("       IFNULL(CHG_EMP_NAME, ' ') AS EMP_NAME, ");
		sb.append("       IFNULL(CHG_CUST_ID, ' ') AS CUST_ID, ");
		sb.append("       IFNULL(EXAM_VERSION, ' ') AS EXAM_VERSION, ");
		sb.append("       IFNULL(QUESTION_VERSION, ' ') AS QUESTION_VERSION, ");
		sb.append("       IFNULL(QUESTION_NAME, ' ') AS QUESTION_NAME, ");
		sb.append("       IFNULL(QUESTION_TYPE, ' ') AS QUESTION_TYPE, ");
		sb.append("       IFNULL(ANSWER_SEQ_LIST, ' ') AS ANSWER_SEQ_LIST, ");
		sb.append("       IFNULL(ANSWER_SEQ_REMARK, ' ') AS ANSWER_SEQ_REMARK, ");
		sb.append("       IFNULL(REMARK, ' ') AS REMARK ");
		sb.append("FROM WMSHC.TBCRM_EVALUATE_SENIOR_CUST_HIS ");
		sb.append("WHERE 1 = 1 ");
		
		if (null != arg) {
			sb.append("AND DATE_FORMAT(CHG_DATE, '%Y%m%d') = '").append(arg).append("' ");
		} else {
			sb.append("AND DATE_FORMAT(CHG_DATE, '%Y%m%d') = DATE_FORMAT(DATE_SUB(SYSDATE(), INTERVAL 1 DAY), '%Y%m%d') ");
		}

		sb.append("AND (CHG_CUST_ID, EXAM_VERSION, QUESTION_VERSION, CHG_DATE) IN ( ");
		sb.append("  SELECT CHG_CUST_ID, EXAM_VERSION, QUESTION_VERSION, MAX(CHG_DATE) AS MAX_CHG_DATE ");
		sb.append("  FROM TBCRM_EVALUATE_SENIOR_CUST_HIS ");
		sb.append("  GROUP BY CHG_CUST_ID, EXAM_VERSION, QUESTION_VERSION ");
		sb.append(") ");

		PreparedStatement pstmt = conn.prepareStatement(sb.toString());
		
		ResultSet result = pstmt.executeQuery(sb.toString());
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (result.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("DATETIME", result.getString(1));
			map.put("BRANCH_NBR", result.getString(2));
			map.put("EMP_ID", result.getString(3));
			map.put("EMP_NAME", result.getString(4));
			map.put("CUST_ID", result.getString(5));
			map.put("EXAM_VERSION", result.getString(6));
			map.put("QUESTION_VERSION", result.getString(7));
			map.put("QUESTION_NAME", result.getString(8));
			map.put("QUESTION_TYPE", result.getString(9));
			map.put("ANSWER_SEQ_LIST", result.getString(10));
			map.put("ANSWER_SEQ_REMARK", result.getString(11));
			map.put("REMARK", result.getString(12));
			
			list.add(map);
		}
		
		result.close();
		pstmt.close();
		conn.close();
		
		String[] order = { "DATETIME", "BRANCH_NBR", "EMP_ID", "EMP_NAME", "CUST_ID", "EXAM_VERSION", "QUESTION_VERSION", "QUESTION_NAME", "QUESTION_TYPE", "ANSWER_SEQ_LIST", "ANSWER_SEQ_REMARK", "REMARK" };

		toSpecifiedLocation("EVALUATE_SENIOR_HIS_" + (null != arg ? arg : sdfYYYYMMDD.format(new Date())), "CSV", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), list, order, ",");
	}

	// 核心_待辦事項_TXT
	public void CORE_TODO(String arg) throws JBranchException, Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT ");

		if (null != arg) {
			sb.append("TO_CHAR(TO_DATE(:arg, 'YYYYMMDD'), 'YYYYMMDD') AS REP_DATE, ");
			queryCondition.setObject("arg", arg);
		} else {
			sb.append("TO_CHAR(SYSDATE, 'YYYYMMDD') AS REP_DATE, ");
		}

		sb.append("       CM.CUST_ID AS CHK_CUST_ID, ");
		
		sb.append("       '1' AS TRAD_CHANNEL, ");
		sb.append("       'C' AS FUNC, ");
		sb.append("       RPAD(' ', 1, ' ') AS PROC_RESULT, ");
		sb.append("       RPAD(' ', 4, ' ') AS TRAD_RESULT_TYPE, ");
		sb.append("       CASE WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][0-7][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '11' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][8-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '12' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][A-Z][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '12' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][A-Z][A-Z])') IS NOT NULL THEN '13' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '19' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 7  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '19' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 8  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '21' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 8  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][A-Z][A-Z][A-Z][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '22' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 8  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][0-9A-Z][0-9A-Z])') IS NOT NULL THEN '23' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 11 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][0-9A-Z][0-9A-Z][0-9A-Z][0-9A-Z][0-9A-Z])') IS NOT NULL THEN '23' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 8  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][A-Z][A-Z][0-9][0-9][0-9])') IS NOT NULL THEN '29' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 11 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([G][R][P][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '31' ");
		sb.append("            WHEN LENGTH(CM.CUST_ID) = 11 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([8][8][A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][A-Z])') IS NOT NULL THEN '32' ");
		sb.append("       ELSE '39' END AS ID_TYPE, ");
		sb.append("       RPAD(CM.CUST_ID, 14, ' ') AS CUST_ID, ");
		sb.append("       '91' AS EVENT_TYPE, ");
		sb.append("       '04' AS EVENT_TYPE_CLASS, ");
		sb.append("       RPAD('0', 16, '0') AS ACCT_NUM, ");
		sb.append("       LPAD(TBCRM_EVALUATE_SENIOR_TODO_SEQ.NEXTVAL, 7, '0') AS SEQ, ");

		if (null != arg) {
			sb.append("       TO_CHAR(TO_DATE(:arg, 'YYYYMMDD') + 1, 'YYYYMMDD') AS START_DATE, ");
			sb.append("       TO_CHAR(TO_DATE(:arg, 'YYYYMMDD') + 1, 'YYYYMMDD') AS END_DATE, ");
			queryCondition.setObject("arg", arg);
		} else {
			sb.append("       TO_CHAR(SYSDATE + 1, 'YYYYMMDD') AS START_DATE, ");
			sb.append("       TO_CHAR(SYSDATE + 1, 'YYYYMMDD') AS END_DATE, ");
		}

		sb.append("       'N' AS TRACK_IDENTIFY, ");
		sb.append("       SUBSTR(RPAD('客戶屬高齡客戶，請至業管系統維護高齡客戶資訊觀察表', 100, '　'),  1, 25) AS TODO1, ");
		sb.append("       SUBSTR(RPAD('客戶屬高齡客戶，請至業管系統維護高齡客戶資訊觀察表', 100, '　'), 26, 50) AS TODO2, ");
		sb.append("       RPAD('0', 8, '0') AS TODO_TO_TELLER1, ");
		sb.append("       RPAD(' ', 8, ' ') AS TODO_TO_TYPE1, ");
		sb.append("       RPAD('0', 8, '0') AS TODO_TO_TELLER2, ");
		sb.append("       RPAD(' ', 8, ' ') AS TODO_TO_TYPE2, ");
		sb.append("       RPAD('0', 2, '0') AS TODO_TO_TELLER_TYPE1, ");
		sb.append("       RPAD('0', 2, '0') AS TODO_TO_TELLER_TYPE2, ");
		sb.append("       RPAD('0', 2, '0') AS TODO_TO_TELLER_TYPE3, ");
		sb.append("       RPAD('0', 5, '0') AS TODO_TO_BRH1, ");
		sb.append("       RPAD('0', 5, '0') AS TODO_TO_BRH2, ");
		sb.append("       RPAD('0', 5, '0') AS TODO_TO_BRH3 ");
		sb.append("FROM TBCRM_CUST_MAST CM ");

		// 20220927 偲偲以信件通知：排除mvcrm_ast_amt的aum_amt <=1000的客戶
		sb.append("INNER JOIN MVCRM_AST_AMT MVCRM ON CM.CUST_ID = MVCRM.CUST_ID AND MVCRM.AUM_AMT > 1000 ");

		sb.append("WHERE 1 = 1 ");

		// 20220927 偲偲以信件通知：排除死亡戶
		sb.append("AND NOT EXISTS (SELECT 'X' FROM TBCRM_CUST_NOTE CN WHERE CN.DEATH_YN = 'Y' AND CN.CUST_ID = CM.CUST_ID) ");

		if (null != arg) {
			sb.append("AND TRUNC((TRUNC(TO_DATE(:arg, 'YYYYMMDD')) - CM.BIRTH_DATE) / 365) >= 65 ");
			queryCondition.setObject("arg", arg);
		} else {
			sb.append("AND TRUNC((TRUNC(SYSDATE) - CM.BIRTH_DATE) / 365) >= 65 ");
		}

		sb.append("AND CASE WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][0-7][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '01' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][8-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '01' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][A-Z][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '01' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][A-Z][A-Z])') IS NOT NULL THEN '01' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 10 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '01' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 7  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '01' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 8  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '02' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 8  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][A-Z][A-Z][A-Z][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '02' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 8  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][0-9A-Z][0-9A-Z])') IS NOT NULL THEN '02' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 11 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][0-9A-Z][0-9A-Z][0-9A-Z][0-9A-Z])') IS NOT NULL THEN '02' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 8  AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([0-9][0-9][0-9][A-Z][A-Z][0-9][0-9][0-9])') IS NOT NULL THEN '02' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 11 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([G][R][P][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9])') IS NOT NULL THEN '09' ");
		sb.append("         WHEN LENGTH(CM.CUST_ID) = 11 AND REGEXP_SUBSTR(UPPER(CM.CUST_ID), '([8][8][A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][A-Z][A-Z])') IS NOT NULL THEN '09' ");
		sb.append("    ELSE '09' END = '01' ");

		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> dataList = dam.exeQuery(queryCondition);
		
		// ===== 交易檢核資料庫 START =====
		Context intitCtx = new InitialContext();
		Context envCtx = (Context) intitCtx.lookup("java:comp/env");
		DataSource dataSource = (DataSource) envCtx.lookup("jdbc/esoaf_mysql");
		java.sql.Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();
		
		sb = new StringBuffer();
		sb.append("SELECT ANS_BASE.CUST_ID, ");
		sb.append("       SUBSTR(CASE WHEN ANS_BASE.TODO IS NULL THEN RPAD('客戶屬高齡客戶，請至業管系統維護高齡客戶資訊觀察表', 100, '　') ELSE RPAD(REPLACE(REPLACE(REPLACE(ANS_BASE.TODO, 'OO；XX', '健康情況和能力表現觀察正常，無需提供特別協助。'), 'OO', ''), '；XX', ''), 100, '　') END, 1, 25) AS TODO1, ");
		sb.append("       SUBSTR(CASE WHEN ANS_BASE.TODO IS NULL THEN RPAD('客戶屬高齡客戶，請至業管系統維護高齡客戶資訊觀察表', 100, '　') ELSE RPAD(REPLACE(REPLACE(REPLACE(ANS_BASE.TODO, 'OO；XX', '健康情況和能力表現觀察正常，無需提供特別協助。'), 'OO', ''), '；XX', ''), 100, '　') END, 26, 50) AS TODO2 ");
		sb.append("FROM ( ");
		sb.append("  SELECT BASE.CUST_ID, ");
		sb.append("         CASE WHEN CHAR_LENGTH(IFNULL(BASE.ANSWER_DESC_TODO, '')) > 0 AND CHAR_LENGTH(IFNULL(BASE.ANSWER_TODO, '')) <= 50 THEN CONCAT(BASE.ANSWER_TODO) ");
		sb.append("              WHEN CHAR_LENGTH(IFNULL(BASE.ANSWER_DESC_TODO, '')) > 0 AND CHAR_LENGTH(IFNULL(BASE.ANSWER_TODO, '')) > 50  THEN CONCAT(SUBSTRING_INDEX(SUBSTR(ANSWER_TODO, 1, 39), '；', (LENGTH(SUBSTR(ANSWER_TODO, 1, 39)) - LENGTH(REPLACE(SUBSTR(ANSWER_TODO, 1, 39), '；', ''))) / LENGTH('；')), '；…等請至業管系統查詢') ");
		sb.append("              WHEN CHAR_LENGTH(IFNULL(BASE.ANSWER_DESC_TODO, '')) = 0 AND CHAR_LENGTH(IFNULL(BASE.ANSWER_TODO, '')) <= 50 THEN CONCAT(BASE.ANSWER_TODO) ");
		sb.append("              WHEN CHAR_LENGTH(IFNULL(BASE.ANSWER_DESC_TODO, '')) = 0 AND CHAR_LENGTH(IFNULL(BASE.ANSWER_TODO, '')) > 50  THEN CONCAT(SUBSTRING_INDEX(SUBSTR(ANSWER_TODO, 1, 39), '；', (LENGTH(SUBSTR(ANSWER_TODO, 1, 39)) - LENGTH(REPLACE(SUBSTR(ANSWER_TODO, 1, 39), '；', ''))) / LENGTH('；')), '；…等請至業管系統查詢') ");
		sb.append("         ELSE BASE.ANSWER_TODO END AS TODO ");
		sb.append("  FROM ( ");
		sb.append("    SELECT CUST_A.CUST_ID, ");
		sb.append("  		   REPLACE(GROUP_CONCAT((CASE WHEN LENGTH(CUST_A.REMARK) > 0 THEN A.TODO ELSE NULL END) ORDER BY QN.QST_NO, A.ANSWER_SEQ), ',', '；') AS ANSWER_DESC_TODO, ");
		sb.append("  		   REPLACE(GROUP_CONCAT(A.TODO ORDER BY QN.QST_NO, A.ANSWER_SEQ), ',', '；') AS ANSWER_TODO ");
		sb.append("    FROM WMSHC.TBCRM_OLD_AGE_QUESTIONNAIRE QN ");
		sb.append("    LEFT JOIN WMSHC.TBCRM_OLD_AGE_QST_QUESTION Q ON Q.QUESTION_VERSION = QN.QUESTION_VERSION ");
		sb.append("    LEFT JOIN WMSHC.TBCRM_OLD_AGE_QST_ANSWER A ON A.QUESTION_VERSION = Q.QUESTION_VERSION ");
		sb.append("    INNER JOIN ( ");
		sb.append("  	SELECT CUST.CUST_ID, CUST.EXAM_VERSION, CUST_D.QUESTION_VERSION, CUST_D.RECORD_SEQ, CUST_D.ANSWER_SEQ, CUST_D.REMARK ");
		sb.append("  	FROM WMSHC.TBCRM_EVALUATE_SENIOR_CUST CUST ");
		sb.append("  	LEFT JOIN WMSHC.TBCRM_EVALUATE_SENIOR_CUST_D CUST_D ON CUST.RECORD_SEQ = CUST_D.RECORD_SEQ ");
		sb.append("    ) CUST_A ON QN.EXAM_VERSION = CUST_A.EXAM_VERSION AND Q.QUESTION_VERSION = CUST_A.QUESTION_VERSION AND A.ANSWER_SEQ = CUST_A.ANSWER_SEQ ");
		sb.append("    WHERE 1 = 1 ");
		sb.append("    AND A.TODO IS NOT NULL ");
		sb.append("    GROUP BY CUST_A.CUST_ID ");
		sb.append("  ) BASE ");
		sb.append(") ANS_BASE ");
		
		ResultSet result = stmt.executeQuery(sb.toString());
		
		for (Map<String, Object> map : dataList) {
			while (result.next()) {
				if (StringUtils.equals((String) map.get("CHK_CUST_ID"), result.getString(1))) {
					map.put("TODO1", result.getString(2));
					map.put("TODO2", result.getString(3));
					break;
				}
			}
		}
		
		result.close();
		stmt.close();
		conn.close();
		// ===== 交易檢核資料庫 END =====
		
		String[] order = { 	"REP_DATE", "TRAD_CHANNEL", "FUNC", "PROC_RESULT", "TRAD_RESULT_TYPE", 
							"ID_TYPE", "CUST_ID", "EVENT_TYPE", "EVENT_TYPE_CLASS", "ACCT_NUM", 
							"SEQ", "START_DATE", "END_DATE", "TRACK_IDENTIFY", 
							"TODO1", "TODO2", 
							"TODO_TO_TELLER1", "TODO_TO_TYPE1", 
							"TODO_TO_TELLER2", "TODO_TO_TYPE2", 
							"TODO_TO_TELLER_TYPE1", "TODO_TO_TELLER_TYPE2", "TODO_TO_TELLER_TYPE3", // "TODO_TO_TELLER_TYPE4", 
							"TODO_TO_BRH1", "TODO_TO_BRH2", "TODO_TO_BRH3" };

		toSpecifiedLocation("EVALUATE_SENIOR_" + (null != arg ? arg : sdfYYYYMMDD.format(new Date())), "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataList, order, "");
	}

	// 核心_高齡客戶評估回覆記錄_TXT
	public void CORE_CUS_REPLY(String arg) throws JBranchException, Exception {

		Context intitCtx = new InitialContext();
		Context envCtx = (Context) intitCtx.lookup("java:comp/env");
		DataSource dataSource = (DataSource) envCtx.lookup("jdbc/esoaf_mysql");
		java.sql.Connection conn = dataSource.getConnection();
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT RPAD(DATE_FORMAT(CHG_DATE, '%Y/%m/%d %H:%i:%s'), 20, ' ') AS DATETIME, ");
		sb.append("       RPAD(IFNULL(CHG_DEPT_ID, ' '), 15, ' ') AS BRANCH_NBR, ");
		sb.append("       RPAD(IFNULL(CHG_EMP_ID, ' '), 11, ' ') AS EMP_ID, ");
		sb.append("       RPAD(IFNULL(CHG_EMP_NAME, ' '), 180, ' ') AS EMP_NAME, ");
		sb.append("       RPAD(IFNULL(CHG_CUST_ID, ' '), 16, ' ') AS CUST_ID, ");
		sb.append("       RPAD(IFNULL(EXAM_VERSION, ' '), 15,' ') AS EXAM_VERSION, ");
		sb.append("       RPAD(IFNULL(QUESTION_VERSION, ' '), 12, ' ') AS QUESTION_VERSION, ");
		sb.append("       RPAD(IFNULL(QUESTION_NAME, ' '), 200, ' ') AS QUESTION_NAME, ");
		sb.append("       RPAD(IFNULL(QUESTION_TYPE, ' '), 1, ' ') AS QUESTION_TYPE, ");
		sb.append("       RPAD(IFNULL(ANSWER_SEQ_LIST, ' '), 2000, ' ') AS ANSWER_SEQ_LIST, ");
		sb.append("       RPAD(IFNULL(ANSWER_SEQ_REMARK, ' '), 2000, ' ') AS ANSWER_SEQ_REMARK, ");
		sb.append("       RPAD(IFNULL(REMARK, ' '), 500, ' ') AS REMARK ");
		sb.append("FROM WMSHC.TBCRM_EVALUATE_SENIOR_CUST_HIS ");
		sb.append("WHERE 1 = 1 ");
		
		if (null != arg) {
			sb.append("AND DATE_FORMAT(CHG_DATE, '%Y%m%d') = '").append(arg).append("' ");
		} else {
			sb.append("AND DATE_FORMAT(CHG_DATE, '%Y%m%d') = DATE_FORMAT(DATE_SUB(SYSDATE(), INTERVAL 1 DAY), '%Y%m%d') ");
		}

		sb.append("AND (CHG_CUST_ID, CHG_DATE) IN (SELECT CHG_CUST_ID, MAX(CHG_DATE) AS MAX_CHG_DATE FROM TBCRM_EVALUATE_SENIOR_CUST_HIS GROUP BY CHG_CUST_ID) ");

		PreparedStatement pstmt = conn.prepareStatement(sb.toString());
		
		ResultSet result = pstmt.executeQuery(sb.toString());
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (result.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("DATETIME", result.getString(1));
			map.put("BRANCH_NBR", result.getString(2));
			map.put("EMP_ID", result.getString(3));
			map.put("EMP_NAME", result.getString(4));
			map.put("CUST_ID", result.getString(5));
			map.put("EXAM_VERSION", result.getString(6));
			map.put("QUESTION_VERSION", result.getString(7));
			map.put("QUESTION_NAME", result.getString(8));
			map.put("QUESTION_TYPE", result.getString(9));
			map.put("ANSWER_SEQ_LIST", result.getString(10));
			map.put("ANSWER_SEQ_REMARK", result.getString(11));
			map.put("REMARK", result.getString(12));
			
			list.add(map);
		}
		
		result.close();
		pstmt.close();
		conn.close();

		String[] order = { "DATETIME", "BRANCH_NBR", "EMP_ID", "EMP_NAME", "CUST_ID", "EXAM_VERSION", "QUESTION_VERSION", "QUESTION_NAME", "QUESTION_TYPE", "ANSWER_SEQ_LIST", "ANSWER_SEQ_REMARK", "REMARK" };

		toSpecifiedLocation("EVALUATE_SENIOR_HIS_" + (null != arg ? arg : sdfYYYYMMDD.format(new Date())), "TXT", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), list, order, "");
	}

	// 匯出至指定路徑
	public void toSpecifiedLocation(String file_name, String attached_name, String path, List<Map<String, Object>> list, String[] order, String separated) throws JBranchException, IOException {

		String fileName = file_name + "." + attached_name;

		File file1 = new File(path + "reports\\" + fileName);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, false), "MS950"));

		for (Map<String, Object> datas : list) {
			int j = 1;
			for (Integer i = 0; i < order.length; i++) {
				if (StringUtils.equals(separated, "")) {
					writer.append((datas.get(order[i])).toString());
				} else {
					writer.append(StringUtils.isBlank((String) datas.get(order[i])) ? "" : (String) datas.get(order[i]));
				}

				if (j++ == order.length) {
				} else {
					writer.append(separated);
				}
			}

			writer.append("\r\n");
		}

		writer.flush();
		writer.close();
	}

}
