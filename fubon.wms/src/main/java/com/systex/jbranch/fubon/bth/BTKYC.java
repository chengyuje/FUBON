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
 * 1.BTKYC001初版
 * 2.BTKYC002初版
 * 3.BTKYC003初版
 * 4.BTKYC004初版
 * @author 1500637
 * @date 2016/12/12
 *
 **/

@Repository("btkyc")
@Scope("prototype")
public class BTKYC extends BizLogic {
	private DataAccessManager dam = null;
	private BthFtpJobUtil bthUtil = null;
	private AuditLogUtil audit = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	public void BTKYC001(Object body, IPrimitiveMap<?> header) throws Exception {
		// 紀錄排程監控log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		// 取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParam = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String ftpCode = (String) jobParam.get("ftpCode");
		String fileName = (String) jobParam.get("fileName");
		/*String[] txtHeader = { 
			"SEQ",
			"CUST_ID",
			"EMP_ID",
			"REC_TYPE",
			"RISKRANGE",
			"TEST_DATE",
			"BRANCH_ID",//承作分行
			"RANGEKEY",
			"RANGEID",
			"RCCHECK",
			"VERSION",
			"REASON",
			"BRANCH_NBR",//收件分行
			"GET_DATE",
			"AO_CODE",
			"AUM_DATE",
			"pvalue",
			"cvalue",
			"tvalue",
			"xvalue",
			"PVALUE",
			"CVALUE",
			"TVALUE",
			"XVALUE",
			"CUST_RISK_BEF",
			"AGE",
			"CUST_EDUCTION_AFTER",
			"CUST_CAREER_AFTER",
			"CUST_MARRIAGE_AFTER",
			"CUST_CHILDREN_AFTER",
			"CUST_HEALTH_AFTER",
			"TOTAL_ASSET_BAL",
			"INVEST",
			"total_asset_bal",
			"SIGN_YN",
			"SIGNOFF_ID",
			"SIGNOFF_DATE",
			"OUT_ACCESS" 
		};
		*/
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
				 "CTIEMPID"};
		StringBuffer sb = new StringBuffer();
		//sb.append(" SELECT ");
		//sb.append("		a.SEQ, ");		
		//sb.append("		a.CUST_ID, ");
		//sb.append("		a.EMP_ID, ");
		//sb.append("		1 REC_TYPE, ");
		//sb.append("		riskrange RISKRANGE, ");
		//sb.append("		to_char(a.createtime,'YYYY-MM-DD HH24:MI:SS' ) TEST_DATE, ");
		////sb.append("CUST_BRANCH_NBR, ");//客戶歸屬分行
		//sb.append("		a.INVEST_BRANCH_NBR, ");//客戶承作問卷分行
		//sb.append("		(select distinct RL_VERSION from TBSYS_QUESTIONNAIRE where exam_version = a.exam_version) RANGEKEY, ");
		//sb.append("		a.CUST_RISK_AFR, ");
		//sb.append("		1 RCCHECK, ");
		//sb.append("		1 VERSION, ");
		//sb.append("		null REASON, ");
		//sb.append("		CASE  ");
		//sb.append("			WHEN a.INVEST_BRANCH_NBR = '999' AND a.INVEST_IP IS NOT NULL ");
		//sb.append("			THEN ");
		//sb.append("				(select BRANCH_NBR from TBPMS_BRANCH_IP where IPAddress = a.INVEST_IP and com_type = '1') ");
		//sb.append("		ELSE   ");
		//sb.append("			a.INVEST_BRANCH_NBR ");
		//sb.append("		END BRANCH_NBR , ");
		//sb.append("		TO_CHAR(CREATE_DATE,'YYYY-MM-DD HH24:MI:SS') GET_DATE, ");
		//sb.append("		a.AO_CODE, ");
		//sb.append("		c.DATA_YEAR||c.DATA_MONTH AUM_DATE, ");
		//sb.append("		0 pvalue, ");
		//sb.append("		0 cvalue, ");
		//sb.append("		0 tvalue, ");
		//sb.append("		0 xvalue,");
		//sb.append("		CUST_RISK_BEF, ");
		//sb.append("		(round((sysdate - (select b.birth_date from tbcrm_cust_mast b where cust_id = a.cust_id)) / 365, 0))AGE, ");
		//sb.append("		b.CUST_EDUCTION_AFTER , ");
		//sb.append("		b.CUST_CAREER_AFTER, ");
		//sb.append("		b.CUST_MARRIAGE_AFTER, ");
		//sb.append("		b.CUST_CHILDREN_AFTER, ");
		//sb.append("		b.CUST_HEALTH_AFTER, ");
		//sb.append("		'??' INVEST, ");
		//sb.append("		c.AVG_AUM_AMT total_asset_bal, ");
		//sb.append("		decode(signoff_id, null, 'N', 'Y') SIGN_YN, ");
		//sb.append("		a.SIGNOFF_ID, ");
		//sb.append("		to_char(a.signoff_date,'YYYY-MM-DD HH24:MI:SS') SIGNOFF_DATE, ");
		//sb.append("		OUT_ACCESS ");
		//sb.append(" FROM tbkyc_investorexam_m_his a ");
		//sb.append(" left join tbkyc_investorexam_d_his b on a.seq = b.seq ");
		//sb.append(" left join TBCRM_CUST_AUM_MONTHLY c on  a.cust_id = c.cust_id ");
		//sb.append(" WHERE trunc(a.CREATE_DATE) >= trunc(sysdate-7) ");
		GenFileTools gft=new GenFileTools();
		sb.append(getKYC001());
		//System.out.println("sb:"+sb.toString());
		ResultSet rs=null;
		Connection con=gft.getConnection();
		Statement s = null;
		try{
		s = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
	                java.sql.ResultSet.CONCUR_READ_ONLY);
		s.setFetchSize(3000);
		rs=gft.getRS(sb,s);
		
		//dam = this.getDataAccessManager();
		//QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//qc.setQueryString(sb.toString());
		
		/*
		List<Map<String, String>> datalist = dam.exeQuery(qc);
		for(Map<String, String> map:datalist){
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			 
			sb.append("SELECT CUST_ID , ");
			sb.append("TRUNC(NVL(P1,0)/(CASE WHEN (NVL(P1,0)+NVL(P2,0)+NVL(P3,0)+NVL(P4,0))=0 THEN 1 ELSE (NVL(P1,0)+NVL(P2,0)+NVL(P3,0)+NVL(P4,0)) END)*100) AS PVALUE, ");
			sb.append("TRUNC(NVL(P2,0)/(CASE WHEN (NVL(P1,0)+NVL(P2,0)+NVL(P3,0)+NVL(P4,0))=0 THEN 1 ELSE (NVL(P1,0)+NVL(P2,0)+NVL(P3,0)+NVL(P4,0)) END)*100) AS CVALUE, ");
			sb.append("TRUNC(NVL(P3,0)/(CASE WHEN (NVL(P1,0)+NVL(P2,0)+NVL(P3,0)+NVL(P4,0))=0 THEN 1 ELSE (NVL(P1,0)+NVL(P2,0)+NVL(P3,0)+NVL(P4,0)) END)*100) AS TVALUE, ");
			sb.append("TRUNC(NVL(P4,0)/(CASE WHEN (NVL(P1,0)+NVL(P2,0)+NVL(P3,0)+NVL(P4,0))=0 THEN 1 ELSE (NVL(P1,0)+NVL(P2,0)+NVL(P3,0)+NVL(P4,0)) END)*100) AS XVALUE ");
			sb.append("FROM ");
			sb.append("(SELECT CUST_ID,P1,P2,P3,P4 FROM ");
			sb.append("(SELECT SUM(NOW_AMT_TWD) AS SUM_NOW_AMT_TWD ,  ");
			sb.append("RISKCATE_ID,CUST_ID FROM ( ");
			sb.append("SELECT (V.NOW_AMT_TWD) as NOW_AMT_TWD ,  ");
			sb.append("P.RISKCATE_ID ,V.CUST_ID ");
			sb.append("FROM VWCRM_AST_ALLPRD_DETAIL V,  ");
			sb.append("VWPRD_MASTER P  ");
			sb.append("WHERE V.PROD_ID = P.PRD_ID  ");
			sb.append("AND V.AST_TYPE in ('07', '08', '09', '10', '12', '15')  ");
			sb.append("AND V.CUST_ID = :custId			 ");
			sb.append("UNION ALL  ");
			//sb.append("------DCI 固定 P4------ ");
			sb.append("SELECT (M.PRCH_AMT_TWD) as NOW_AMT_TWD,  ");
			//sb.append("------不保本 固定P4------ ");
			sb.append("CASE WHEN NVL(MO.GRNT_TYPE,'0') not in ('1','2','3') THEN 'P4'   ");
			//sb.append("------保本且幣別為南非幣 固定 P3------ ");
			sb.append("WHEN NVL(MO.GRNT_TYPE,'0') in ('1','2','3') AND MO.VALU_CRCY_TYPE = 'ZAR' THEN 'P3'   ");
			//sb.append("------保本且幣別不為南非幣 固定 P2------ ");
			sb.append("WHEN NVL(MO.GRNT_TYPE,'0') in ('1','2','3') AND MO.VALU_CRCY_TYPE != 'ZAR' THEN 'P2'   ");
			//sb.append("------剩餘固定 P4------ ");
			sb.append("ELSE 'P4' END as RISKCATE_ID ,M.CUST_ID   ");
			sb.append("FROM TBCRM_AST_INV_DCI M, TBCRM_AST_INV_DCI_OTH MO  ");
			sb.append("WHERE 1=1  ");
			sb.append("AND M.PROD_ID = MO.PROD_ID  ");
			sb.append("AND M.CUST_ID = MO.CUST_ID  ");
			sb.append("AND M.ACC_NBR = MO.ACC_NBR  ");
			sb.append("AND M.CD_NBR = TO_NUMBER(MO.CD_NBR)  ");
			sb.append("AND M.CUST_ID = :custId	 ");
			//sb.append("  ------黃金存摺 固定 P3------ ");
			sb.append("UNION ALL  ");
			sb.append("SELECT (V.NOW_AMT_TWD) as NOW_AMT_TWD, 'P3' as RISKCATE_ID ,V.CUST_ID ");
			sb.append("FROM VWCRM_AST_ALLPRD_DETAIL V  ");
			sb.append("WHERE 1=1  ");
			sb.append("AND V.AST_TYPE = '17'  ");
			sb.append("AND V.CUST_ID = :custId	  ");
			sb.append(")  ");
			sb.append("GROUP BY RISKCATE_ID ,CUST_ID ");
			sb.append(") ");
			sb.append("PIVOT ");
			sb.append("( ");
			sb.append("SUM(SUM_NOW_AMT_TWD) FOR RISKCATE_ID IN('P1' AS P1 ,'P2' AS P2,'P3' AS P3,'P4' AS P4) ");
			sb.append(")) ");	
			qc1.setQueryString(sb.toString());
			qc1.setObject("custId", (String)map.get("CUST_ID"));
			
			List<Map<String,Object>> list_Risk = dam.exeQuery(qc1);
			if(list_Risk.size()!=0)
			{
			for(Map<String,Object> tMap : list_Risk){
				System.out.println(tMap);

				
					map.put("PVALUE", String.valueOf(tMap.get("PVALUE")));
				
					map.put("CVALUE", String.valueOf(tMap.get("CVALUE")));
				
					map.put("TVALUE", String.valueOf(tMap.get("TVALUE")));
				
					map.put("XVALUE", String.valueOf(tMap.get("XVALUE")));
					
				
			}
			}
			else
			{
				map.put("PVALUE", "");
				
				map.put("CVALUE", "");
			
				map.put("TVALUE", "");
			
				map.put("XVALUE", "");
			}
             
			
			
		}
		*/
		gft.writeFile("INVESTORRISK", "", (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), txtHeader, rs, ",", false, true);
		
	} finally {
		if (rs != null) try { rs.close(); } catch (Exception e) {}
		if (s != null) try { s.close(); } catch (Exception e) {}
		if (con != null) try { con.close(); } catch (Exception e) {}
	}
		//	    OutTxt((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), "INVESTORRISK", new Date(), datalist, txtHeader, txtHeader, true);
//		ftpUpload(ftpCode);
	}
	
	private StringBuffer getKYC001() {
		StringBuffer sb=new StringBuffer();
		sb.append("SELECT TO_CHAR(A.SEQ) PROFILE_TEST_ID, ");
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
		sb.append("TO_CHAR(G.CTIEMPID) AS CTIEMPID ");			//冷靜期解鎖錄音客服人員員編
		sb.append("FROM TBKYC_INVESTOREXAM_M A ");
		sb.append("LEFT JOIN (SELECT CUST_ID,BIRTH_DATE FROM TBCRM_CUST_MAST) B ON A.CUST_ID = B.CUST_ID ");
		sb.append("LEFT JOIN TBKYC_INVESTOREXAM_D C ON A.SEQ = C.SEQ ");
		sb.append("LEFT JOIN (SELECT * FROM TBSYS_QUESTIONNAIRE WHERE RL_VERSION IS NOT NULL) D ON A.EXAM_VERSION = D.EXAM_VERSION ");
		sb.append("LEFT JOIN TBCRM_CUST_AUM_MONTHLY_HIST E ON A.CUST_ID = E.CUST_ID AND TO_CHAR(ADD_MONTHS(A.CREATE_DATE,-1),'YYYYMM') = E.DATA_YEAR || E.DATA_MONTH ");
		sb.append("LEFT JOIN TBKYC_COOLING_PERIOD F ON F.SEQ = A.SEQ AND SIGNOFF_ID IS NOT NULL ");	//主管解鎖有錄音序號資料
		sb.append("LEFT JOIN TBSYS_REC_LOG G ON G.TRANSSEQ = F.REC_SEQ ");
		/*
		sb.append("LEFT JOIN ");
		sb.append("(select H.CUST_ID,  SUM( ");
		sb.append("H.FDCD_AUM+  ");
		sb.append("H.BFOND_AUM+  ");
		sb.append("H.DOM_FUND_AUM+  ");
		sb.append("H.OVS_COMM_AUM+ "); 
		sb.append("H.OVS_STK_AUM+ "); 
		sb.append("H.OVS_FUND_AUM+ "); 
		sb.append("H.CMA_AUM+ ");  
		sb.append("H.ENTRUST_AUM+ ");
		sb.append("H.PORTFOLIO_AUM+ "); 
		sb.append("H.MNY_TRST_AUM+ ");
		sb.append("H.GOLD_BOOK_AUM ");
		sb.append(") INVEST ");
		sb.append("from TBCRM_CUST_AUM_MONTHLY_HIST H ");
		sb.append("GROUP BY CUST_ID) H  ");
		sb.append("ON A.CUST_ID=H.CUST_ID ");
		*/
		sb.append("WHERE TO_CHAR(A.CREATE_DATE,'yyyymmdd')>=TO_CHAR(SYSDATE-7,'yyyymmdd') ");
		sb.append("group by A.SEQ, A.CUST_ID, A.EMP_ID, A.REC_TYPE, A.RISKRANGE, TO_CHAR(A.CREATE_DATE,'YYYY-MM-DD HH24:MI:SS' ), A.INVEST_BRANCH_NBR, D.RL_VERSION, A.CUST_RISK_AFR, '', A.VERSION, '', A.INVEST_BRANCH_NBR, TO_CHAR(A.CREATE_DATE,'YYYY-MM-DD HH24:MI:SS'),A.AO_CODE,TO_CHAR(ADD_MONTHS(A.CREATE_DATE,-1),'YYYYMM'), '0', '0', '0', '0', A.CUST_RISK_BEF, ROUND((SYSDATE - B.BIRTH_DATE) / 365, 0), C.CUST_EDUCTION_AFTER, C.CUST_CAREER_AFTER, C.CUST_MARRIAGE_AFTER, C.CUST_CHILDREN_AFTER, C.CUST_HEALTH_AFTER,E.AVG_AUM_AMT, A.STATUS, A.SIGNOFF_ID, TO_CHAR(A.SIGNOFF_DATE,'YYYY-MM-DD HH24:MI:SS'), A.OUT_ACCESS ");
		//System.out.println("sql:"+sb.toString());
		return sb;
	}

	public void BTKYC002(Object body,IPrimitiveMap<?> header) throws Exception {
		audit = (AuditLogUtil)((Map<?,?>)body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		Map<String,Object> inputMap = (Map<String,Object>) body;
		Map<String,Object> jP = (Map<String,Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String ftpCode = (String) jP.get("ftpcode");
		String fileName = (String) jP.get("ftpName");
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT rpad( 'PS_FP_INVESTORRISK_D', 30, ' ' )||lpad(count(1), 15, '0')||to_char(sysdate-1, 'yyyymmdd') as TITLE ");
		sb.append(" FROM ( ");
		
	    sb.append(getKYC001()+" ");        
		
		sb.append(") ");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		qc.setQueryString(sb.toString());
		List<Map<String,String>> dataList = dam.exeQuery(qc);
		
		String[] txtColumn = {"TITLE"};
		OutTxt((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), "ZINVESTORRISK", new Date(), dataList, null, txtColumn, false);
//		ftpUpload(ftpCode);
	}
	public void BTKYC003(Object body,IPrimitiveMap<?> header)throws Exception{
		audit = (AuditLogUtil)((Map<?,?>)body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jp = (Map<String,Object>)inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String ftpCode = (String)jp.get("ftpCode");
		String fileName = (String)jp.get("ftpName");
		String[] txtHeader = { "SEQ" ,"EXAM_VERSION" ,"COL_1", "QUESTION_VERSION", "QST_NO", "RESULTA"};
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
		sb.append("AND TRUNC(a.CREATE_DATE) >= TRUNC(sysdate - 7) ");
		sb.append("AND a.EXAM_VERSION = C.EXAM_VERSION ");
		sb.append("AND C.QUESTION_VERSION = D.QUESTION_VERSION ");
		sb.append("ORDER BY a.SEQ, C.QST_NO ");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		qc.setQueryString(sb.toString());
		
		List<Map<String, String>> datalist = dam.exeQuery(qc);
		OutTxt((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), "INVESTOR_D", new Date(), datalist, txtHeader, txtHeader, true);
//		ftpUpload(ftpCode);
	}
	public void BTKYC004(Object body,IPrimitiveMap<?>header) throws Exception{
		audit = (AuditLogUtil)((Map<?,?>)body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		Map<String,Object> inputMap = (Map<String,Object>) body;
		Map<String, Object> jp = (Map<String,Object>)inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		String ftpCode = (String)jp.get("ftpCode");
		String fileName = (String)jp.get("fileName");
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT rpad( 'PS_FP_INVESTOR_D', 30, ' ' )||lpad(count(1), 15, '0')||to_char(sysdate-1, 'yyyymmdd') as TITLE ");
		sb.append("FROM tbkyc_investorexam_m_his a, ");
		sb.append("tbkyc_investorexam_d_his B, ");
		sb.append("TBSYS_QUESTIONNAIRE C, ");
		sb.append("TBSYS_QST_QUESTION D ");
		sb.append("WHERE a.SEQ = B.SEQ ");
		sb.append("AND TRUNC(a.CREATE_DATE) >= TRUNC(sysdate - 7) ");
		sb.append("AND a.EXAM_VERSION = C.EXAM_VERSION ");
		sb.append("AND C.QUESTION_VERSION = D.QUESTION_VERSION ");
		sb.append("ORDER BY C.QST_NO ");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		qc.setQueryString(sb.toString());
		
		List dataList = dam.exeQuery(qc);
		String[] txtColumn = {"TITLE"};
		OutTxt((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), "ZINVESTOR_D", new Date(), dataList, null, txtColumn, false);
//		ftpUpload(ftpCode);
	}
	
	public void OutTxt(String path, String file_name, Date file_Date, List<Map<String,String>> datalist, String[] txtHeader, String[] txtColumn, boolean quoter)throws JBranchException, IOException {

		List<Object[]>  csvData = new ArrayList<Object[]>();
		
//		檔案路徑+檔案名稱
		path = path + "reports\\";
		String fullfile_name2 = path + file_name;// + "." + sdf.format(file_Date);
//		寫檔開始
		File file = new File(fullfile_name2);
		BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false),"MS950"));		

//		寫入標頭 
		/*
		if(txtHeader != null){
			for(int i = 0; i < txtHeader.length; i++){
				fw.append(txtHeader[i]);
				
				if (i != (txtHeader.length - 1)) {
					fw.append(",");
				} else {
					fw.append("\n");
				}
			}		
		}
		*/
			
		String[] records = new String[(null != txtHeader ? txtHeader.length : 1)];
		//筆數
		
		for (Integer i = 0; i < datalist.size(); i++) {
			//欄位數
			for (Integer j = 0; j < datalist.get(i).size(); j++) {
				//System.out.println("datalist.get(i).size():"+datalist.get(i).size()+" txtColumn.length:"+txtColumn.length);
				fw.append(checkIsNull(datalist.get(i), txtColumn[j], quoter));
//				最後一欄不要逗點
				if (j != (records.length - 1)) {
					fw.append(",");
				}
			}
			fw.append("\r\n");
		}
		fw.flush();
		fw.close();
		
	}
/**
* 檢查Map取出欄位是否為Null
*/
	private String checkIsNull(Map<String, String> map, String key, boolean quoter) {
		//判斷資料是否為null && 判斷是否資料轉成字串null && 傳入的map不能是null
		if (StringUtils.isNotEmpty(String.valueOf(map.get(key))) && 
			!StringUtils.equals((String.valueOf(map.get(key))),"null") && 
			map != null) {
			
			String str = String.valueOf(map.get(key));
//			 if(str.contains(",")){                
//		            //如果資料本身有雙引號，先將雙引號轉譯，避免兩邊加了雙引號轉譯錯誤。(資料本身有雙引號，外面還要再加一組雙引號)  
//		            if(str.contains("\"")){  
//		            	str=str.replace("\"", "\"\"");  
//		            } 
//		        }
			 //再將逗號轉譯 
			if (quoter){
	            str = "\""+str+"\""; 
			};
			return str;
		} else {
			return "";
		}
	}
//	private void ftpUpload(String ftpCode) throws Exception {
//		try {
//			bthUtil = new BthFtpJobUtil();
//			bthUtil.ftpPutFile(ftpCode);
//		} catch (Exception e) {
//			audit.audit(ExceptionUtils.getStackTrace(e));
//		}
//	}

}
