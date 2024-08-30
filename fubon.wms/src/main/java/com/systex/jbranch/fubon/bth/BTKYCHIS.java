package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * 1.BTKYC001 001+002
 * 
 * 3.BTKYC003 003+004
 * 
 * @author 1500637
 * @date 2017/10/19
 *
 **/

@Repository("btkychis")
@Scope("prototype")
public class BTKYCHIS extends BizLogic {
	private DataAccessManager dam = null;
	private BthFtpJobUtil bthUtil = null;
	private AuditLogUtil audit = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private GenFileTools gft=new GenFileTools();
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	public void BTKYC001(Object body, IPrimitiveMap<?> header) throws Exception {
		// 紀錄排程監控log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		// 取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String ftpCode = (String) jobParam.get("ftpCode");
		String fileName = (String) jobParam.get("fileName");
		String dateParam = (String) jobParam.get("dateParam");
		String jobName="PS_FP_INVESTORRISK_D";
		int dataCount=0;
		if(ftpCode==null)
		{
			ftpCode="BTKYC001HIS";
		}
		
		fileName="INVESTORRISK";
		String fileDate=null;
		//String fileDate=gft.getFtpFileDate(ftpCode, true, "DES");
		if(dateParam!=null)
		{
			fileDate=dateParam;
		}
		System.out.println("fileDate:"+fileDate);
		StringBuffer sb = new StringBuffer();

		sb.append(getKYC001(fileDate));

		ResultSet rs=null;
		Connection con=gft.getConnection();
		Statement s = null;
		try{
		s = con.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
		s.setFetchSize(3000);
		rs=gft.getRS(sb,s);
		gft.writeFile(fileName, "",(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), getKYC001Order(), rs, ",", false, true);
		rs.beforeFirst();
		while(rs.next())
		{
			dataCount++;
		}
		gft.writeZFile("Z"+fileName, "", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataCount, "", jobName,-1);
	} finally {
		if (rs != null) try { rs.close(); } catch (Exception e) {}
		if (s != null) try { s.close(); } catch (Exception e) {}
		if (con != null) try { con.close(); } catch (Exception e) {}
	}
	}
	private String[] getKYC001Order()
	{
		String[] txtHeader=
			{
				 "PROFILE_TEST_ID",
				 "CUST_ID",
				 "EMP_ID",
				 "REC_TYPE",
				 "RISKRANGE",
				 "TEST_DATE",
				 "BRANCH_ID",
				 "RANGEKEY",
				 "RANGEID",
				 "FCCHECK",
				 "VERSION",
				 "REASON",
				 "BRANCH_KEYIN",
				 "GET_DATE",
				 "AO_CODE",
				 "AUM_DATE",
				 "PVALUE",
				 "CVALUE",
				 "TVALUE",
				 "XVALUE",
				 "LAST_RANGEID",
				 "AGE",
				 "EDUCTION",
				 "CAREER",
				 "MARRIAGE",
				 "CHILD",
				 "HEALTH",
				 "INVEST",
				 "AUM",
				 "CHECKED",
				 "CHECKED_EMP_ID",
				 "CHECKED_DATE",
				 "APPR_REASON",
				 "CREL_REC_SEQ",
				 "INLINEEMPID",
				 "CTIEMPID",
				 "SCORE_ORI_TOT",
				 "CRR_ORI",
				 "SCORE_C",
				 "SCORE_W",
				 "SCORE_CW_TOT",
				 "CRR_MATRIX",
				 "RISK_LOSS_RATE",
				 "RISK_LOSS_LEVEL"};
		return txtHeader;
	}
	private StringBuffer getKYC001(String fileDate) {
		StringBuffer sb=new StringBuffer();
		String params="BETWEEN TRUNC(SYSDATE-7) AND TRUNC(SYSDATE-1) ";
		if(fileDate!=null)
		{
		if(fileDate.split("-").length==2)
		{
			params="BETWEEN TO_DATE('"+fileDate.split("-")[0]+"','YYYYMMDD') AND TO_DATE('"+fileDate.split("-")[1]+"','YYYYMMDD') ";
		}
		else
		{
			params=">= TO_DATE('"+fileDate+"','YYYYMMDD') ";
		}
		}
		sb.append("SELECT DISTINCT TO_CHAR(A.SEQ) PROFILE_TEST_ID, ");
		sb.append("TO_CHAR(A.CUST_ID) CUST_ID, ");
		sb.append("TO_CHAR(A.EMP_ID) EMP_ID, ");
		sb.append("TO_CHAR(A.REC_TYPE) REC_TYPE, ");
		sb.append("TO_CHAR(NVL(A.RISKRANGE,'0')) AS RISKRANGE, ");
		sb.append("TO_CHAR(A.CREATE_DATE,'YYYY-MM-DD HH24:MI:SS' ) TEST_DATE, ");
		sb.append("TO_CHAR(A.INVEST_BRANCH_NBR ) AS BRANCH_ID, ");
		sb.append("TO_CHAR(D.RL_VERSION) RANGEKEY, ");
		sb.append("TO_CHAR(A.CUST_RISK_AFR) RANGEID, ");
		sb.append("'' FCCHECK, ");
		sb.append("TO_CHAR(NVL(A.VERSION,'')) AS VERSION, ");
		sb.append("'' REASON, ");
		sb.append("TO_CHAR(A.INVEST_BRANCH_NBR) BRANCH_KEYIN , ");
		sb.append("TO_CHAR(A.CREATE_DATE,'YYYY-MM-DD HH24:MI:SS') GET_DATE, ");
		sb.append("TO_CHAR(A.AO_CODE) AO_CODE, ");
		sb.append("TO_CHAR(ADD_MONTHS(A.CREATE_DATE,-1),'YYYYMM') AUM_DATE , ");
		sb.append("'0' PVALUE, ");
		sb.append("'0' CVALUE, ");
		sb.append("'0' TVALUE, ");
		sb.append("'0' XVALUE, ");
		sb.append("TO_CHAR(A.CUST_RISK_BEF) LAST_RANGEID, ");
		sb.append("TO_CHAR(ROUND((SYSDATE - B.BIRTH_DATE) / 365, 0)) AGE , ");
		sb.append("TO_CHAR(C.CUST_EDUCTION_AFTER) EDUCTION, ");
		sb.append("TO_CHAR(C.CUST_CAREER_AFTER) CAREER, ");
		sb.append("TO_CHAR(C.CUST_MARRIAGE_AFTER) MARRIAGE, ");
		sb.append("TO_CHAR(C.CUST_CHILDREN_AFTER) CHILD, ");
		sb.append("TO_CHAR(C.CUST_HEALTH_AFTER) HEALTH , ");
		//sb.append("ROUND(H.INVEST/E.AVG_AUM_AMT,4) INVEST, ");
		sb.append("'0' INVEST, ");
		sb.append("TO_CHAR(NVL(E.AVG_AUM_AMT,'0')) AUM, ");
		sb.append("DECODE(A.STATUS,'01','1','03','2') CHECKED, ");
		sb.append("TO_CHAR(A.SIGNOFF_ID) CHECKED_EMP_ID, ");
		sb.append("TO_CHAR(A.SIGNOFF_DATE,'YYYY-MM-DD HH24:MI:SS') CHECKED_DATE, ");
		sb.append("TO_CHAR(A.OUT_ACCESS) APPR_REASON, ");
		sb.append("TO_CHAR(F.REC_SEQ) AS CREL_REC_SEQ, ");		//冷靜期解鎖錄音序號
		sb.append("TO_CHAR(G.INLINEEMPID) AS INLINEEMPID, ");	//冷靜期解鎖錄音帶進線員編
		sb.append("TO_CHAR(G.CTIEMPID) AS CTIEMPID, ");			//冷靜期解鎖錄音客服人員員編
		sb.append("TO_CHAR(A.SCORE_ORI_TOT) AS SCORE_ORI_TOT, ");
		sb.append("TO_CHAR(A.CRR_ORI) AS CRR_ORI, ");
		sb.append("TO_CHAR(A.SCORE_C) AS SCORE_C, ");
		sb.append("TO_CHAR(A.SCORE_W) AS SCORE_W, ");
		sb.append("TO_CHAR(A.SCORE_CW_TOT) AS SCORE_CW_TOT, ");
		sb.append("TO_CHAR(A.CRR_MATRIX) AS CRR_MATRIX, ");
		sb.append("TO_CHAR(A.RISK_LOSS_RATE) AS RISK_LOSS_RATE, ");
		sb.append("TO_CHAR(A.RISK_LOSS_LEVEL) AS RISK_LOSS_LEVEL ");
		sb.append("FROM TBKYC_INVESTOREXAM_M A ");
		sb.append("LEFT JOIN (SELECT CUST_ID,BIRTH_DATE FROM TBCRM_CUST_MAST) B ON A.CUST_ID = B.CUST_ID ");
		sb.append("LEFT JOIN TBKYC_INVESTOREXAM_D C ON A.SEQ = C.SEQ ");
		sb.append("LEFT JOIN (SELECT * FROM TBSYS_QUESTIONNAIRE WHERE RL_VERSION IS NOT NULL) D ON A.EXAM_VERSION = D.EXAM_VERSION ");
		sb.append("LEFT JOIN TBCRM_CUST_AUM_MONTHLY_HIST E ON A.CUST_ID = E.CUST_ID AND TO_CHAR(ADD_MONTHS(A.LASTUPDATE,-1),'YYYYMM') = E.DATA_YEAR || E.DATA_MONTH ");
		sb.append("LEFT JOIN TBKYC_COOLING_PERIOD F ON F.SEQ = A.SEQ AND F.SIGNOFF_ID IS NOT NULL ");	//主管解鎖有錄音序號資料
		sb.append("LEFT JOIN TBSYS_REC_LOG G ON G.TRANSSEQ = F.REC_SEQ ");
		sb.append("WHERE TRUNC(A.LASTUPDATE) ");
		sb.append(params);
		sb.append(" ");
//		sb.append("group by A.SEQ, A.CUST_ID, A.EMP_ID, A.REC_TYPE, A.RISKRANGE, TO_CHAR(A.CREATE_DATE,'YYYY-MM-DD HH24:MI:SS' ), A.INVEST_BRANCH_NBR, D.RL_VERSION, A.CUST_RISK_AFR, '', A.VERSION, '', A.INVEST_BRANCH_NBR, TO_CHAR(A.CREATE_DATE,'YYYY-MM-DD HH24:MI:SS'),A.AO_CODE,TO_CHAR(ADD_MONTHS(A.CREATE_DATE,-1),'YYYYMM'), '0', '0', '0', '0', A.CUST_RISK_BEF, ROUND((SYSDATE - B.BIRTH_DATE) / 365, 0), C.CUST_EDUCTION_AFTER, C.CUST_CAREER_AFTER, C.CUST_MARRIAGE_AFTER, C.CUST_CHILDREN_AFTER, C.CUST_HEALTH_AFTER,E.AVG_AUM_AMT, A.STATUS, A.SIGNOFF_ID, TO_CHAR(A.SIGNOFF_DATE,'YYYY-MM-DD HH24:MI:SS'), A.OUT_ACCESS ");
		System.out.println("params:"+params);
		return sb;
	}
	public void BTKYC003(Object body,IPrimitiveMap<?> header)throws Exception{
		audit = (AuditLogUtil)((Map<?,?>)body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jp = (Map<String,Object>)inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		GenFileTools gft=new GenFileTools();
		String ftpCode = (String)jp.get("ftpCode");
		String fileName = (String)jp.get("ftpName");
		String dateParam = (String)jp.get("dateParam");
		String jobName="PS_FP_INVESTOR_D";
		if(ftpCode==null)
		{
			ftpCode="BTKYC003HIS";
		}
		fileName="INVESTOR_D";
		String fileDate=null;

		//String fileDate=gft.getFtpFileDate(ftpCode, true, "DES");
		if(dateParam!=null)
		{
			fileDate=dateParam;
		}
		String[] txtHeader = { "SEQ" ,"EXAM_VERSION" ,"COL_1", "QUESTION_VERSION", "QST_NO", "RESULTA"};
		StringBuffer sb = new StringBuffer();
        int dataCount=0;
		sb.append(getKYC003(fileDate));
		ResultSet rs=null;
		Connection con=gft.getConnection();
		Statement s = null;
		try{
		s = con.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
		s.setFetchSize(3000);
		rs=gft.getRS(sb,s);
		gft.writeFile(fileName, "",(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), txtHeader, rs, ",", false, true);
		rs.beforeFirst();
		while(rs.next())
		{
			dataCount++;
		}
		gft.writeZFile("Z"+fileName, "", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), dataCount, "", jobName,-1);
	} finally {
		if (rs != null) try { rs.close(); } catch (Exception e) {}
		if (s != null) try { s.close(); } catch (Exception e) {}
		if (con != null) try { con.close(); } catch (Exception e) {}
	}
		
//		ftpUpload(ftpCode);
	}
	public StringBuffer getKYC003(String fileDate)
	{

		String params="BETWEEN TRUNC(SYSDATE-7) AND TRUNC(SYSDATE-1) ";
		if(fileDate!=null)
		{
		if(fileDate.split("-").length==2)
		{
			params="BETWEEN TO_DATE('"+fileDate.split("-")[0]+"','YYYYMMDD') AND TO_DATE('"+fileDate.split("-")[1]+"','YYYYMMDD') ";
		}
		else
		{
			params=">= TO_DATE('"+fileDate+"','YYYYMMDD') ";
		}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT a.SEQ AS SEQ, ");
		sb.append("C.EXAM_VERSION AS EXAM_VERSION, ");
		sb.append("'1' COL_1, ");
		sb.append("C.QUESTION_VERSION, ");
		sb.append("C.QST_NO AS QST_NO, ");
		sb.append("REPLACE(REPLACE(CASE ");
		sb.append("WHEN DECODE(C.QST_NO, 1, 0, INSTR(B.ANSWER_2, ';', 1 , C.QST_NO - 1 ) + 1) - ");
		sb.append("DECODE(C.QST_NO, 1, -1, 0) + INSTR(B.ANSWER_2, ';', 1, C.QST_NO) - (DECODE(C.QST_NO, 1, 0, INSTR(B.ANSWER_2, ';', 1, C.QST_NO-1)+1)) ");
		sb.append("= 0 ");
		sb.append("THEN TRIM(REPLACE(ANSWER_2 , REGEXP_SUBSTR(ANSWER_2 , '.*;') , '') ) ");
		sb.append("ELSE ");
		sb.append("TRIM(SUBSTR( ");
		sb.append("B.ANSWER_2, ");
		sb.append("DECODE(C.QST_NO, 1, 0, INSTR(B.ANSWER_2, ';', 1 , C.QST_NO - 1 ) + 1), ");
		sb.append("DECODE(C.QST_NO, 1, -1, 0) + INSTR(B.ANSWER_2, ';', 1, C.QST_NO) - (DECODE(C.QST_NO, 1, 0, INSTR(B.ANSWER_2, ';', 1, C.QST_NO-1)+1)) ");
		sb.append(")) ");
		sb.append("END,',','|'),' ','') RESULTA ");
		sb.append("FROM tbkyc_investorexam_m_his a, ");
		sb.append("tbkyc_investorexam_d_his B, ");
		sb.append("TBSYS_QUESTIONNAIRE C, ");
		sb.append("TBSYS_QST_QUESTION D ");
		sb.append("WHERE a.SEQ = B.SEQ ");
		sb.append("AND TRUNC(a.LASTUPDATE)   ");
		sb.append(params);	
		sb.append("AND a.EXAM_VERSION = C.EXAM_VERSION ");
		sb.append("AND C.QUESTION_VERSION = D.QUESTION_VERSION ");
		sb.append("ORDER BY a.SEQ, C.QST_NO ");
		System.out.println("params:"+params);
		return sb;
	}
	public void BTKYC002(Object body,IPrimitiveMap<?> header)throws Exception{
	
		
	}
	public void BTKYC004(Object body,IPrimitiveMap<?> header)throws Exception{
	
		
	}	

}
