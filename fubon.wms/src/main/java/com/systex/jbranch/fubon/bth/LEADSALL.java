/**
 * 
 */
package com.systex.jbranch.fubon.bth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSFTPVO;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 1. 讀取DB產出 .yyyymmdd
 * 2. 將.yyyymmdd 上傳FTP
 * 
 * @author 1600216
 * @date 2016/11/16
 *
 */
@Repository("leadsall")
@Scope("prototype")
public class LEADSALL extends BizLogic {
	 
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	private BthFtpJobUtil ftpJobUtil = new BthFtpJobUtil();
	
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	
	public void createFileBth (Object body, IPrimitiveMap<?> header) throws Exception {
		dam=this.getDataAccessManager();
		GenFileTools gft=new GenFileTools();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		//System.out.println("btpms413_out go!!");
		String  writeFileName="LEADS_ALL";
		String dataStr = gft.getFtpFileDate("LEADS_ALL", true, "DES");
		StringBuffer sb = new StringBuffer();		
		//先取得總筆數
		sb.append("SELECT COUNT(1) TOTAL_COUNT FROM ( ");
		sb.append(genSql(dataStr).toString());
		sb.append(")");
		//System.out.println("SQL:"+sb.toString());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> listCount = dam.exeQuery(queryCondition);	
		int dataCount=Integer.parseInt(listCount.get(0).get("TOTAL_COUNT").toString());
		System.out.println("count:"+dataCount);
//	    String[] writeFilerOrder = {"SFA_LEAD_ID","LEAD_NAME","EMP_ID","BRANCH_ID","AO_CODE","CUST_ID","CUST_NAME","LEAD_SOURCE_ID","LEAD_STATUS","PC_LE_STATUS","COL_1", 
//	    		"COL_2","COL_3","COL_4","COL_4_1","CAMPAIGN_ID","STEP_ID","COL_5","COL_6","COL_7","COL_8","COL_9", "LEAD_MEMO","COL_10","CREATOR","COL_11",
//	    		"MODIFIER","COL_12", "COL_13","COL_14","COL_15","COL_16","COL_17","COL_18","COL_19","COL_20","COL_21","COL_22","COL_23",
//	    		"COL_24","COL_25","COL_26","COL_27","COL_28","COL_29","COL_30", "COL_31","COL_32","COL_33","COL_34","COL_35","COL_36","COL_37",
//	    		"COL_38","COL_39","COL_40","COL_41","COL_42","COL_43","COL_44","COL_45","COL_46","COL_47","COL_48","COL_49","COL_50",
//	    		"COL_51","COL_52","COL_53"};	
		System.out.println("newOrder");
	    String[] writeFilerOrder = {"SFA_LEAD_ID","LEAD_NAME","EMP_ID","BRANCH_ID","AO_CODE","CUST_ID","CUST_NAME","LEAD_SOURCE_ID","LEAD_STATUS","PC_LE_STATUS","COL_1", 
	    		"COL_2","COL_3","COL_4","COL_4_1","CAMPAIGN_ID","STEP_ID","COL_5","COL_6","COL_7","COL_8","COL_9", "LEAD_MEMO","COL_10","CREATOR","COL_11",
	    		"MODIFIER","COL_12", "COL_13","COL_14","COL_15","COL_16","COL_17","COL_18","COL_19","COL_20","COL_21","COL_22","COL_23",
	    		"COL_24","COL_25","COL_26","COL_27","COL_28","COL_29","COL_30", "COL_31","COL_32","COL_33"
	    		,"PC_LE_STATUS","RESPONSE_NAME"
	    		};
	    ResultSet rs=null;
	    Connection con=gft.getConnection();
		Statement s = null;
		try {
			s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
			s.setFetchSize(3000);
	    rs=gft.getRS(genSql(dataStr),s);
	    gft.writeFile(writeFileName, "", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), writeFilerOrder, rs, ", ", false, true);
		
	    //chk檔格式
		StringBuffer chkLayout=new StringBuffer();
		chkLayout.append(gft.addBlankForString("PS_SA_LEADS_ALL", 30));
		chkLayout.append(gft.addZeroForNum(String.valueOf(dataCount),15));
		chkLayout.append(dataStr);
		gft.writeFileByText("Z"+writeFileName, "", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), chkLayout, false);	    
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception e) {}
			if (s != null) try { s.close(); } catch (Exception e) {}
			if (con != null) try { con.close(); } catch (Exception e) {}
		}
				
	}
	public StringBuffer genSql(String dateStr) {
		
		StringBuffer sb=new StringBuffer();
		sb.append("WITH QUESTION AS ( ");
		sb.append("  select ca.campaign_id, ca.step_id, ex.cust_id, qu.question_desc, NVL(QN.QST_NO, 1) AS question_NO  ");
		sb.append("  from tbcam_sfa_campaign ca "); 
		sb.append("  left join TBCAM_EXAMRECORD ex on ex.exam_version = ca.exam_id ");  
		sb.append("  left join tbcam_examrecord_detail exd on ex.record_seq = exd.record_seq "); 
		sb.append("  left join TBSYS_QST_ANSWER an on an.question_version = exd.question_version and an.answer_seq=exd.answer_seq "); 
		sb.append("  left join TBSYS_QST_QUESTION qu on qu.question_version = an.question_version ");  
		sb.append("  LEFT JOIN TBSYS_QUESTIONNAIRE QN ON exd.QUESTION_VERSION = QN.QUESTION_VERSION AND ex.EXAM_VERSION = QN.EXAM_VERSION "); 
		sb.append("  where QN.QUEST_TYPE = '01' "); 
		sb.append("  AND qu.MODULE_CATEGORY = 'CAM' "); 
		sb.append(")  ");
		sb.append(", ANSWER AS ( "); 
		sb.append("  select ca.campaign_id, ca.step_id, ex.cust_id, TRIM (exd.remark || an.answer_desc) AS answer, NVL(QN.QST_NO, 1) AS answer_NO "); 
		sb.append("  from tbcam_sfa_campaign ca  ");
		sb.append("  left join TBCAM_EXAMRECORD ex on ex.exam_version = ca.exam_id   ");
		sb.append("  left join tbcam_examrecord_detail exd on ex.record_seq = exd.record_seq  ");
		sb.append("  left join TBSYS_QST_ANSWER an on an.question_version = exd.question_version and an.answer_seq=exd.answer_seq  ");
		sb.append("  left join TBSYS_QST_QUESTION qu on qu.question_version = an.question_version   ");
		sb.append("  LEFT JOIN TBSYS_QUESTIONNAIRE QN ON exd.QUESTION_VERSION = QN.QUESTION_VERSION AND ex.EXAM_VERSION = QN.EXAM_VERSION  ");
		sb.append("  where QN.QUEST_TYPE = '01'  ");
		sb.append("  AND qu.MODULE_CATEGORY = 'CAM'  ");
		sb.append(") , ");
        sb.append("CAMP_RESPONSE AS  ( ");
        sb.append("  SELECT CAMPAIGN_ID, LEAD_STATUS, RESPONSE_NAME FROM TBCAM_SFA_CAMP_RESPONSE  ");
        sb.append(") 		  ");
		sb.append("SELECT  ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(LE.SFA_LEAD_ID ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') as SFA_LEAD_ID,  ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(LE.LEAD_NAME ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') as LEAD_NAME,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(LE.EMP_ID ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') as EMP_ID,   ");
		sb.append("LPAD(REPLACE(REPLACE(REPLACE(REPLACE(NVL(LE.BRANCH_ID, '000'), ',', '，'), CHR(10), ''), CHR(13), ' '), '\"', '`'), 5, '0') AS BRANCH_ID, ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(LE.AO_CODE ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') as AO_CODE,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(LE.CUST_ID ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') as CUST_ID,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(CU.CUST_NAME ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') as CUST_NAME,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(CA.LEAD_SOURCE_ID ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') as LEAD_SOURCE_ID,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(CASE WHEN LE.LEAD_STATUS = '01' THEN 'NP'     ");
		sb.append("      WHEN LE.LEAD_STATUS = '02' THEN 'PR'     ");
		sb.append("   	 WHEN LE.LEAD_STATUS = '03A' THEN 'CL'    ");
		sb.append("      WHEN LE.LEAD_STATUS = '03B' THEN 'CL'    ");
		sb.append("      WHEN LE.LEAD_STATUS = '03C' THEN 'CN'    ");
		sb.append("      WHEN LE.LEAD_STATUS = '03D' THEN 'CX'    ");
		sb.append("  ELSE ''     ");
		sb.append("  END ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') ");
		sb.append(" AS LEAD_STATUS,     ");
		sb.append("' ' AS PC_LE_STATUS, ");			
		sb.append("'0' COL_1, ");
		sb.append("'0' COL_2, ");		
		sb.append("'0' COL_3,   ");
		sb.append("'0' COL_4,   ");
		sb.append("' ' COL_4_1,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(LE.CAMPAIGN_ID ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') AS CAMPAIGN_ID,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(LE.STEP_ID ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')AS STEP_ID,   ");
		sb.append("TO_CHAR(LE.START_DATE,'yyyy/mm/dd') COL_5,   ");
		sb.append("TO_CHAR(LE.START_DATE,'yyyy/mm/dd') COL_6,   ");
		sb.append("TO_CHAR(LE.END_DATE,'yyyy/mm/dd') COL_7,   ");
		sb.append("TO_CHAR(LE.RESP_DATE,'yyyy/mm/dd') COL_8,   ");
		sb.append("'' COL_9,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(substrb(trim(RE.visit_memo), 1, 254) ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') AS LEAD_MEMO,     ");
		sb.append("TO_CHAR(LE.CREATETIME,'yyyy/mm/dd') COL_10,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(IM.CREATOR ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') AS CREATOR,   ");
		sb.append("TO_CHAR(LE.LASTUPDATE,'yyyy/mm/dd') COL_11,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(LE.MODIFIER ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') AS MODIFIER, ");
        sb.append("REPLACE(REPLACE(REPLACE(REPLACE( ");
        sb.append("substrb( replace( VA.var_field_label1 || ':' || VA.var_field_value1 || ',' ||   ");
		sb.append("   VA.var_field_label2 || ':' || VA.var_field_value2 || ',' ||   ");
		sb.append("   VA.var_field_label3 || ':' || VA.var_field_value3 || ',' ||   ");
		sb.append("   VA.var_field_label4 || ':' || VA.var_field_value4 || ',' ||   ");
		sb.append("   VA.var_field_label5 || ':' || VA.var_field_value5 || ',' ||   ");
		sb.append("   VA.var_field_label6 || ':' || VA.var_field_value6 || ',' ||   ");
		sb.append("   VA.var_field_label7 || ':' || VA.var_field_value7 || ',' ||   ");
		sb.append("   VA.var_field_label8 || ':' || VA.var_field_value8 || ',' ||   ");
		sb.append("   VA.var_field_label9 || ':' || VA.var_field_value9 || ',' ||   ");
		sb.append("   VA.var_field_label10 || ':' || VA.var_field_value10 || ',' ||   ");
		sb.append("   VA.var_field_label11 || ':' || VA.var_field_value11 || ',' ||   ");
		sb.append("   VA.var_field_label12 || ':' || VA.var_field_value12 || ',' ||   ");
		sb.append("   VA.var_field_label13 || ':' || VA.var_field_value13 || ',' ||   ");
		sb.append("   VA.var_field_label14 || ':' || VA.var_field_value14 || ',' ||   ");
		sb.append("   VA.var_field_label15 || ':' || VA.var_field_value15 || ',' ||   ");
		sb.append(" ',', ':,', ''), 1, 254 ) ");
		sb.append(" ");
		sb.append(",',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')		 ");
		sb.append(" as COL_12, ");
        sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q1, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') AS COL_13,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(NVL(QST.A1, ' '), ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') AS COL_14,  ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q2, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_15,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A2, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_16,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q3, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_17,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A3, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_18,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q4, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_19,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A4, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_20,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q5, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_21,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A5, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_22,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q6, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_23,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A6, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_24,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q7, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_25,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A7, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_26,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q8, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_27,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A8, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_28,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q9, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_29,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A9, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`')  AS COL_30,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.Q10, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') AS COL_31,   ");
		sb.append("REPLACE(REPLACE(REPLACE(REPLACE(NVL(QST.A10, ' ') ,',' ,'，') ,CHR(10) ,'') ,CHR(13) ,' ') ,'\"' ,'`') AS COL_32,   ");
		sb.append("CASE WHEN CA.LEAD_SOURCE_ID='01' THEN 'UNICA' ELSE '' END COL_33, ");
		sb.append("nvl(LE.LEAD_STATUS,' ') AS PC_LE_STATUS,      ");
		sb.append("nvl(CR.RESPONSE_NAME,' ') AS RESPONSE_NAME   ");
		sb.append("FROM TBCAM_SFA_LEADS LE   ");
		sb.append("LEFT JOIN tbcam_sfa_le_imp_temp IM ON le.sfa_lead_id = im.lead_id   ");
		sb.append("LEFT JOIN TBCAM_SFA_CAMPAIGN CA ON CA.CAMPAIGN_ID = LE.CAMPAIGN_ID AND CA.STEP_ID = LE.STEP_ID   ");
		sb.append("LEFT JOIN TBCAM_SFA_LEADS_VAR VA ON VA.SFA_LEAD_ID = LE.SFA_LEAD_ID   ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CU ON LE.CUST_ID = CU.CUST_ID   ");
		sb.append("LEFT JOIN tbcrm_cust_visit_record RE ON le.cust_memo_seq = re.visit_seq ");
        sb.append("LEFT JOIN CAMP_RESPONSE CR ON (CA.CAMPAIGN_ID = CR.CAMPAIGN_ID OR CA.LEAD_RESPONSE_CODE = CR.CAMPAIGN_ID) AND LE.LEAD_STATUS = CR.LEAD_STATUS ");
		sb.append("LEFT JOIN (  ");
		sb.append("  SELECT Q.CAMPAIGN_ID, Q.STEP_ID, Q.CUST_ID, Q.Q1, Q.Q2, Q.Q3, Q.Q4, Q.Q5, Q.Q6, Q.Q7, Q.Q8, Q.Q9, Q.Q10, A.A1, A.A2, A.A3, A.A4, A.A5, A.A6, A.A7, A.A8, A.A9, A.A10  ");
		sb.append("  FROM (  ");
		sb.append("    SELECT CAMPAIGN_ID, STEP_ID, CUST_ID, Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Q9, Q10  ");
		sb.append("    FROM QUESTION  ");
		sb.append("    PIVOT (MIN(QUESTION_DESC) FOR (QUESTION_NO) IN ('1' AS Q1, '2' AS Q2, '3' AS Q3, '4' AS Q4, '5' AS Q5, '6' AS Q6, '7' AS Q7, '8' AS Q8, '9' AS Q9, '10' AS Q10))  ");
		sb.append("  ) Q  ");
		sb.append("  LEFT JOIN (  ");
		sb.append("    SELECT CAMPAIGN_ID, STEP_ID, CUST_ID, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10  ");
		sb.append("    FROM ANSWER  ");
		sb.append("    PIVOT (MAX(answer) FOR answer_NO IN ('1' AS A1, '2' AS A2, '3' AS A3, '4' AS A4, '5' AS A5, '6' AS A6, '7' AS A7, '8' AS A8, '9' AS A9, '10' AS A10))  ");
		sb.append("  ) A ON Q.CAMPAIGN_ID = A.CAMPAIGN_ID AND Q.STEP_ID = A.STEP_ID AND Q.CUST_ID = A.CUST_ID  ");
		sb.append(") QST ON LE.CAMPAIGN_ID = QST.CAMPAIGN_ID AND LE.STEP_ID = QST.STEP_ID AND LE.CUST_ID = QST.CUST_ID  "); 	
		sb.append("WHERE ");
		sb.append("LE.LASTUPDATE >= TRUNC(SYSDATE - 1) OR LE.DISP_DATE >= TRUNC(SYSDATE - 1)");

		return sb;
	}
		
	private String genDate(String ftpsettingid) throws Exception {
		String pattern2 = "\\{(([yY]{2}|[cC])?[yY]{2})?[-_]?([mM]{2})?[-_]?([dD]{2})?[-_]?([H]{2})?([,]+[-]?\\d+)?[EHM]?\\}.*";
		TBSYSFTPVO vo = (TBSYSFTPVO) this.dam.findByPKey(TBSYSFTPVO.TABLE_UID, ftpsettingid);
		if (vo == null) {
			logger.info("{} not defined in TBSYSFTP", ftpsettingid);
			return "TO_CHAR(SYSDATE,'yyyy/mm/dd')";
		}
		String desfilename = vo.getDESFILENAME();
		logger.info("{} desfilename = {}", ftpsettingid, desfilename);
		String[] sa = desfilename.split("\\.");
		if (sa.length != 2) return "TO_CHAR(SYSDATE,'yyyy/mm/dd')";
		String dateStr = sa[1].trim();
		
		if (dateStr.startsWith("{") && dateStr.endsWith("}")) {
			String splitStr = dateStr.replaceAll("[\\{\\}]", "");
			//日期是否需做加減
            Integer days = 0;
            char sign = 0;
            logger.info("splitStr={}, days={}", splitStr, days);

			if (splitStr.contains(",")) {
				int commaIdx = splitStr.indexOf(",");
				String sDay = splitStr.substring(commaIdx + 1);
				if (sDay.matches(".+[EHM]$")) {
					sign = sDay.charAt(sDay.length()-1);
					days = Integer.parseInt(sDay.substring(0, sDay.length()-1));
				} else {
					days = Integer.valueOf(splitStr.substring(commaIdx + 1));
				}
				if (splitStr.contains("SYSDATE")) {
					// 系統日期
					Date date = new Date();
					//判斷是否需做天數增減
	    			date = (days != 0)? dateModified(date, days, sign) : date;
	    			return "'" + sdfYYYYMMDD.format(date) + "'";
				} else {
					if (splitStr.matches(pattern2)) {
						// 批次日期
						QueryConditionIF condition = this.dam
		    					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		    			StringBuffer sql = new StringBuffer()
		    					.append("SELECT PABTH_BATCHCTRL.FC_GetData_Date BatchDate FROM DUAL");
		    			condition.setQueryString(sql.toString());

		    			List<Map<String, Timestamp>> result = this.dam.exeQuery(condition);
		    			Timestamp stamp = result.get(0).get("BATCHDATE");
		    			Date date = new Date(stamp.getTime());
		    			date = (days != 0)? dateModified(date, days, sign) : date;
		    			return "'" + sdfYYYYMMDD.format(date) + "'";
					} else {
						return "TO_CHAR(SYSDATE,'yyyy/mm/dd')";
					}
				}
			} else {
				if (splitStr.contains("SYSDATE")) {
					// 系統日期
					return "TO_CHAR(SYSDATE,'yyyy/mm/dd')";
				} else {
					if (splitStr.matches(pattern2)) {
						// 批次日期
						QueryConditionIF condition = this.dam
		    					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);

		    			StringBuffer sql = new StringBuffer()
		    					.append("SELECT PABTH_BATCHCTRL.FC_GetData_Date BatchDate FROM DUAL");
		    			condition.setQueryString(sql.toString());

		    			List<Map<String, Timestamp>> result = this.dam.exeQuery(condition);
		    			Timestamp stamp = result.get(0).get("BATCHDATE");
		    			Date date = new Date(stamp.getTime());
		    			return "'" + sdfYYYYMMDD.format(date) + "'";
					} else {
						return "TO_CHAR(SYSDATE,'yyyy/mm/dd')";
					}
				}
			}
		} else {
			if (dateStr.length() >= 8) {
				try {
					Date date = sdfYYYYMMDD.parse(dateStr.substring(0, 8));
				} catch (Exception e) {
					return "TO_CHAR(SYSDATE,'yyyy/mm/dd')";
				}
			}
			return "TO_CHAR(SYSDATE,'yyyy/mm/dd')";
		}
	}
	
    /**
     * 針對檔案日期做天數加/減
     *
     * @param date
     * @param days
     * @return
     */
    private Date dateModified(Date date, Integer days, char sign){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (sign) {
        case 0:		// 預設單位為天數
        	cal.add(Calendar.DATE, days);
        	break;
        case 'M':	// 單位為月數
        	cal.add(Calendar.MONTH, days);
        	break;
        case 'E':	// 單位為月數，回傳月底日期
        	cal.add(Calendar.MONTH, days);
        	cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        	break;
        case 'H':	// 單位為時數
        	cal.add(Calendar.HOUR_OF_DAY, days);
        	break;
        }
        
        return new Timestamp(cal.getTimeInMillis());
    }
    
    private Date yearModified(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, -1911);
        return new Timestamp(cal.getTimeInMillis());
    }
}


