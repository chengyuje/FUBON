package com.systex.jbranch.fubon.bth;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.bth.ftp.BthFtpJobUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.scheduler.AuditLogUtil;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * WMS-CR-20220420-03_2022年調換換手系統管控需求
 * @date 2022/06/10
 */

@Repository("btpms429")
@Scope("prototype")
public class BTPMS429 extends BizLogic {
	
	private DataAccessManager dam;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil bthUtil = new BthFtpJobUtil();
	private AuditLogUtil audit = null;
	
	/**
	 * 產生經營五年以上客戶名單給世璋(之後另外的批次：世璋會回傳該名單中的客戶對帳單寄送分行代碼、客戶對帳單寄送方式)
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTPMS429_C_SEND(Object body, IPrimitiveMap<?> header) throws Exception {
		
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT * FROM TBPMS_ROTATION_5YCUST A WHERE NOT EXISTS (SELECT 1 FROM TBPMS_ROTATION_MAIN WHERE PRJ_ID = A.PRJ_ID) "); //尚未產生帳務報表的名單資料
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = tempPath + "reports\\WMS_CBILL_COMFM_C_SEND.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "big5"));
		
		if(CollectionUtils.isNotEmpty(list)){
			for(Map<String, Object> map : list) {
				String temp = "";
				//營五年以上客戶名單，介接欄位為固定長度
				//客戶ID(X11)、對帳單基準日(X8)
				temp += String.format("%-11s",ObjectUtils.toString(map.get("CUST_ID")));
				temp += String.format("%-8s",ObjectUtils.toString(map.get("STATEMENT_DATE")));
				bw.write(temp+"\r\n");
			}
		}
		
		bw.flush();
		bw.close();
	}
	
	/**
	 * 產生電子函證名單給Bill Hunter
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTPMS429_E_SEND(Object body, IPrimitiveMap<?> header) throws Exception {
		
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT A.STATEMENT_DATE, A.SM_BRANCH_NBR, ");
		sql.append(" A.CUST_ID, A.SM_BRANCH_NBR, C.EMAIL, A.STATEMENT_DATE, B.DEPT_NAME, A.CBILL_MATCH_YN ");
		sql.append(" FROM TBPMS_ROTATION_MAIN A ");
		sql.append(" LEFT JOIN TBORG_DEFN B ON B.DEPT_ID = NVL(TRIM(A.SM_BRANCH_NBR), '999') ");
		sql.append(" LEFT JOIN TBCRM_CUST_CONTACT C ON TRIM(C.CUST_ID) = TRIM(A.CUST_ID) ");
		sql.append(" WHERE A.STATEMENT_SEND_TYPE = '2' "); //電子函證
		sql.append(" AND A.PROCESS_STATUS IS NULL "); //尚未處理過資料
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = tempPath + "reports\\WMS_EBILL_COMFM_C_SEND.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "big5"));
		
		if(CollectionUtils.isNotEmpty(list)){
			for(Map<String, Object> map : list) {
				//透過Bill Hunter發送函證，介接欄位為固定長度
				//客戶ID(X11)、客戶對帳單寄送分行代碼(X5)、Email(X50)、對帳單基準日(X8)、客戶對帳單寄送分行名稱(X32)
				if(StringUtils.equals("Y", ObjectUtils.toString(map.get("CBILL_MATCH_YN")))) {
					//符合產出對帳單條件，才需給Bill Hunter
					String temp = "";
					temp += String.format("%-11s",ObjectUtils.toString(map.get("CUST_ID")));
					temp += String.format("%-5s",ObjectUtils.toString(map.get("SM_BRANCH_NBR")));
					temp += String.format("%-50s",ObjectUtils.toString(map.get("EMAIL")));
					temp += String.format("%-8s",ObjectUtils.toString(map.get("STATEMENT_DATE")));
					temp += String.format("%-32s",ObjectUtils.toString(map.get("DEPT_NAME")));
					
					bw.write(temp+"\r\n");
				}
				
				//將狀態改為已交付電子/實體通路
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    		StringBuffer sb = new StringBuffer();
	    		sb.append("UPDATE TBPMS_ROTATION_MAIN ");
	    		sb.append("SET PROCESS_STATUS = '1' "); //已交付電子/實體通路
	    		sb.append("	  ,LASTUPDATE = SYSDATE, MODIFIER = 'BTPMS429.java' ");
	    		sb.append("WHERE STATEMENT_DATE = :statementDate ");
	    		sb.append(" AND  TRIM(CUST_ID) = TRIM(:custID) ");
	    		sb.append(" AND  STATEMENT_SEND_TYPE = '2' "); //電子函證
	    		if(StringUtils.isBlank(ObjectUtils.toString(map.get("SM_BRANCH_NBR")))) {
	    			sb.append(" AND  NVL(TRIM(SM_BRANCH_NBR), '999') = '999' ");
	    		} else {
	    			sb.append(" AND  TRIM(SM_BRANCH_NBR) = TRIM(:smBranchNbr) ");
	    			queryCondition.setObject("smBranchNbr", ObjectUtils.toString(map.get("SM_BRANCH_NBR")));
	    		}
	    		queryCondition.setQueryString(sb.toString());
	    		queryCondition.setObject("statementDate", ObjectUtils.toString(map.get("STATEMENT_DATE")));
	    		queryCondition.setObject("custID", ObjectUtils.toString(map.get("CUST_ID")));
	    		
	    		dam.exeUpdate(queryCondition);
			}
		}
		
		bw.flush();
		bw.close();
	}
	
	/**
	 * 產生函證名單提供實體寄送
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void BTPMS429_M_SEND(Object body, IPrimitiveMap<?> header) throws Exception {
		
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT A.CUST_ID, A.SM_BRANCH_NBR, C.EMAIL, A.STATEMENT_DATE, B.DEPT_NAME ");
		sql.append(" FROM TBPMS_ROTATION_MAIN A ");
		sql.append(" LEFT JOIN TBORG_DEFN B ON B.DEPT_ID = NVL(TRIM(A.SM_BRANCH_NBR), '999') ");
		sql.append(" LEFT JOIN TBCRM_CUST_CONTACT C ON TRIM(C.CUST_ID) = TRIM(A.CUST_ID) ");
		sql.append(" WHERE A.STATEMENT_SEND_TYPE = '1' "); //實體函證
		sql.append(" AND A.PROCESS_STATUS IS NULL "); //尚未處理過資料
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = tempPath + "reports\\WMS_MBILL_COMFM_C_SEND.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		
		if(CollectionUtils.isNotEmpty(list)) {
			for(Map<String, Object> map : list) {
				String temp = "";
				//透過實體信件發送函證，介接欄位為固定長度
				//客戶ID(X11)、客戶對帳單寄送分行代碼(X5)、對帳單基準日(X8)
				temp += String.format("%-11s",ObjectUtils.toString(map.get("CUST_ID")));
				temp += String.format("%-5s",ObjectUtils.toString(map.get("SM_BRANCH_NBR")));
				temp += String.format("%-8s",ObjectUtils.toString(map.get("STATEMENT_DATE")));
				
				bw.write(temp+"\r\n");
				
				//將狀態改為已交付電子/實體通路
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    		StringBuffer sb = new StringBuffer();
	    		sb.append("UPDATE TBPMS_ROTATION_MAIN ");
	    		sb.append("SET PROCESS_STATUS = '1' "); //已交付電子/實體通路
	    		sb.append("	  ,LASTUPDATE = SYSDATE, MODIFIER = 'BTPMS429.java' ");
	    		sb.append("WHERE STATEMENT_DATE = :statementDate ");
	    		sb.append(" AND  TRIM(CUST_ID) = TRIM(:custID) ");
	    		sb.append(" AND  STATEMENT_SEND_TYPE = '1' "); //實體函證
	    		if(StringUtils.isBlank(ObjectUtils.toString(map.get("SM_BRANCH_NBR")))) {
	    			sb.append(" AND  NVL(TRIM(SM_BRANCH_NBR), '999') = '999' ");
	    		} else {
	    			sb.append(" AND  TRIM(SM_BRANCH_NBR) = TRIM(:smBranchNbr) ");
	    			queryCondition.setObject("smBranchNbr", ObjectUtils.toString(map.get("SM_BRANCH_NBR")));
	    		}
	    		queryCondition.setQueryString(sb.toString());
	    		queryCondition.setObject("statementDate", ObjectUtils.toString(map.get("STATEMENT_DATE")));
	    		queryCondition.setObject("custID", ObjectUtils.toString(map.get("CUST_ID")));
	    		dam.exeUpdate(queryCondition);
			}
		}
		
		bw.flush();
		bw.close();
		
		//產生檢核檔ZWMS_AO10C5.yyyymmdd(理專年資10年轄下經營滿5年客戶)
		//搭配WMS_MBILL_COMFM_C_SEND.txt配合新版檔案加解密，調整檢核檔格式
		String contenStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileNameAO10Y = tempPath + "reports\\ZWMS_AO10C5." + sdf.format(new Date());
		BufferedWriter bwAO10Y = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNameAO10Y), "UTF-8"));
		contenStr += LenString("WMS_AO10C5", 10);
		contenStr += LenString("WMS_MBILL_COMFM_C_SEND.TXT", 40);
		contenStr += LenString("PUT", 10);
		contenStr += LenString("WMS_MBILL_COMFM_C_SEND.TXT", 40);
		bwAO10Y.write(contenStr+"\r\n");
		
		bwAO10Y.flush();
		bwAO10Y.close();
	}
	
	private String LenString(String inputStr, int StrLen) {
		
		String returnString = "";
	    int LenCtrl = 0;
	    int inputStrLen;
	    int i = 0;
	    
	    if (StringUtils.isBlank(inputStr)) {
	    	inputStrLen = 0;
	    	inputStr = "";
	    } else
	    	inputStrLen = inputStr.length();
	    
	    while (inputStrLen > i) {
	    	//判斷該字元是否為雙位元資料
	    	if (Integer.parseInt(Integer.toString(inputStr.charAt(i), 16), 16) >= 128) {
	    		if ( (StrLen - LenCtrl - 1) >= 2) {
	    			returnString = returnString + inputStr.substring(i, i + 1);
	    			LenCtrl = LenCtrl + 2;
	    		}
	        } else {
	    		if ( (StrLen - LenCtrl) >= 1 ) {
	    			returnString = returnString + inputStr.substring(i, i + 1);
	    			LenCtrl++;
	    		}
	        }
	    	i++;
	    }
	    
	    // 現有字串以後補空白
	    while(LenCtrl < StrLen) {
	    	returnString = returnString + " ";
	    	LenCtrl++;
	    }
	    
	    // 表示該字串中含有雙位元資料
	    if (returnString.length() < StrLen)
	    	StrLen = returnString.length();
	    
	    return returnString.substring(0, StrLen);
	}
	
	
	// 確認單回函(實體/電子)回覆異常報表給內控品管科
	private List<Map<String, Object>> mail175BListSQL() throws Exception {
		
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT B.BRA_NBR || '-' || C.DEPT_NAME AS BRANCH_STR, COUNT(*) AS CNT ");
		sb.append(" FROM (SELECT DISTINCT CUST_ID FROM TBPMS_ROTATION_MAIN M ");
		sb.append(" 		INNER JOIN TBCRM_TRS_PRJ_MAST P ON P.PRJ_ID = M.PRJ_ID ");
		sb.append(" 		WHERE M.BRN_MGM_YN = 'N' AND TRUNC(P.PRJ_EXE_DATE) = TRUNC(SYSDATE)) A "); //分行主管回覆異常//專案執行日期生效
		sb.append(" INNER JOIN TBCRM_CUST_MAST B ON B.CUST_ID = A.CUST_ID ");
		sb.append(" LEFT JOIN TBORG_DEFN C ON C.DEPT_ID = B.BRA_NBR ");
		sb.append(" GROUP BY B.BRA_NBR, C.DEPT_NAME ");
		
		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}
		
	// 確認單回函(實體/電子)回覆異常報表給內控品管科
	public void sendMailTo175B(Object body, IPrimitiveMap<?> header) throws Exception {
		
		List<String> errorMap = new ArrayList<String>();
		List<String> errorMap2 = new ArrayList<String>();
		
		List<Map<String, Object>> list = this.mail175BListSQL();
		List<Map<String, Object>> empList = this.get175BMAIL_LIST();
		
		if (list.size() > 0) {
			for (Map<String, Object> empMap : empList) {
				// mail
				String mail_address = ObjectUtils.toString(empMap.get("EMP_EMAIL_ADDRESS"));
				
				// 無E-mail
				if(StringUtils.isBlank(mail_address))
					errorMap.add(ObjectUtils.toString(empMap.get("EMP_ID")));
				
				// Email格式錯誤
				else if(isEmail(mail_address) == false)
					errorMap2.add(ObjectUtils.toString(empMap.get("EMP_ID")));
				
				else {
					List<Map<String, String>> mailList = new ArrayList<Map<String,String>>();
					Map<String, String> mailMap = new HashMap<String, String>();
					mailMap.put(FubonSendJavaMail.MAIL, mail_address);
					mailList.add(mailMap);
					
					FubonSendJavaMail sendMail = new FubonSendJavaMail();
					FubonMail mail = new FubonMail();
					Map<String, Object> annexData = new HashMap<String, Object>();
					mail.setLstMailTo(mailList);
					//設定信件主旨
					mail.setSubject("【異常通報】確認單回函(實體/電子)回覆異常通知信");
					//設定信件內容
					String content = "";
					content += "以下為分行照會客戶回覆異常內容筆數";
					content += "<br/><br/>";
					
					content += "<table border=\"1\" style=\"text-align:center;\">";
					
					content += "<tr>";
					content += "<td>分行別</td>";
					content += "<td>筆數</td>";
					content += "</tr>";
					
					for (Map<String, Object> map : list) {
						content += "<tr>";
						content += "<td>" + map.get("BRANCH_STR") + "</td>";
						content += "<td>" + map.get("CNT") + "</td>";
						content += "</tr>";
					}
					
					content += "</table>";
					
					content += "<br/><br/>";
					
					content += "請客群業管部內控品管科接手後續之調查作業。";
					
					logger.info("sendMailTo175B mail_address:" + mail_address);
					logger.info("sendMailTo175B content:" + content);
					
					mail.setContent(content);
					sendMail.sendMail(mail, annexData);
				}
			}
			
			//將狀態改為異常名單已通報內控管理科
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			StringBuffer sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_ROTATION_MAIN ");
			sb.append(" SET PROCESS_STATUS = '6' "); //異常名單通報內控管理科
			sb.append("	   ,LASTUPDATE = SYSDATE, MODIFIER = 'BTPMS429_SEND175BMAIL.java' ");
			sb.append(" WHERE (CUST_ID, PRJ_ID) IN ");
			sb.append(" 		(SELECT A.CUST_ID, A.PRJ_ID FROM TBPMS_ROTATION_MAIN A INNER JOIN TBCRM_TRS_PRJ_MAST P ON P.PRJ_ID = A.PRJ_ID ");
			sb.append(" 			WHERE A.BRN_MGM_YN = 'N' AND TRUNC(P.PRJ_EXE_DATE) = TRUNC(SYSDATE)) ");			
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);
		} else {
			logger.info("無分行照會客戶回覆異常內容的筆數");
		}
		
		
		if(errorMap.size() > 0)
			logger.info("該人員無E-mail:" + errorMap);
		
		if(errorMap2.size() > 0)
			logger.info("該人員Email格式錯誤:" + errorMap2);
	}
	
	// 內控品管科人員
	private List<Map<String, Object>> get175BMAIL_LIST() throws Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT EMP_ID, EMP_EMAIL_ADDRESS ");
		sb.append(" FROM TBORG_MEMBER ");
		sb.append(" WHERE DEPT_ID = '175B' ");
		sb.append(" AND SERVICE_FLAG = 'A' AND CHANGE_FLAG IN ('A', 'M', 'P') ");
		
		queryCondition.setQueryString(sb.toString());

		return dam.exeQuery(queryCondition);
	}
	
	// 確認單回函(實體/電子)主管回覆正常，但之後的回函為帳務有異常或有異常行為，寄送分行管理課 
	private List<Map<String, Object>> mail175CListSQL() throws Exception {
		
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT B.BRA_NBR || '-' || C.DEPT_NAME AS BRANCH_STR, B.CUST_NAME AS CUST_STR, ");
		sb.append(" TO_CHAR(A.RECEIVE_DATE, 'YYYY-MM-DD') AS RECEIVE_DATE, ");
		sb.append(" CASE A.RTN_STATUS_AST WHEN 'Y' THEN '相符' WHEN 'N' THEN '不符' ELSE '' END AS RTN_STATUS_AST, ");
		sb.append(" CASE A.RTN_STATUS_NP WHEN 'Y' THEN '有' WHEN 'N' THEN '無' ELSE '' END AS RTN_STATUS_NP, ");
		sb.append(" D.PARAM_NAME AS CONTACT_STATUS ");
		sb.append(" FROM TBPMS_ROTATION_MAIN A ");
		sb.append(" INNER JOIN TBCRM_TRS_PRJ_MAST P ON P.PRJ_ID = A.PRJ_ID ");
		sb.append(" LEFT JOIN TBCRM_CUST_MAST B ON B.CUST_ID = A.CUST_ID ");
		sb.append(" LEFT JOIN TBORG_DEFN C ON C.DEPT_ID = B.BRA_NBR ");
		sb.append(" LEFT JOIN TBSYSPARAMETER D ON D.PARAM_TYPE ='PMS.ROTATION_BRMGR_STATUS' AND D.PARAM_CODE = A.CONTACT_STATUS ");
		sb.append(" WHERE A.BRN_MGM_YN = 'Y' "); //分行主管回覆正常
		sb.append("		AND A.STATEMENT_SEND_TYPE IN ('1', '2') "); //電子/實體函證
		sb.append("		AND TRUNC(A.REC_DATE) <= TRUNC(A.RECEIVE_DATE) "); //錄音日期<=回函日期
		sb.append("		AND (A.RTN_STATUS_AST = 'N' OR A.RTN_STATUS_NP = 'Y') "); //回函為:帳務不符或有不當情事
		sb.append("		AND A.PROCESS_STATUS NOT IN ('4', '5', '6', '7')  "); //已在專案執行日處理過的資料(狀態已改)，不須再寄信 4:正常名單結案 5:未回函名單拔CODE 6:異常名單通報內控品管科 7:異常名單通報分行人員管
//		sb.append("     AND TRUNC(P.PRJ_EXE_DATE) = TRUNC(SYSDATE) "); //專案執行日期生效 //每周送EMAIL，所以不用等執行日
		
		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}
		
	// 確認單回函(實體/電子)主管回覆正常，但之後的回函為帳務有異常或有異常行為，寄送分行管理課 
	public void sendMailTo175C(Object body, IPrimitiveMap<?> header) throws Exception {
		
		List<String> errorMap = new ArrayList<String>();
		List<String> errorMap2 = new ArrayList<String>();
		
		List<Map<String, Object>> list = this.mail175CListSQL();
		List<Map<String, Object>> empList = this.get175CMAIL_LIST();
		
		if (list.size() > 0) {
			for (Map<String, Object> empMap : empList) {
				// mail
				String mail_address = ObjectUtils.toString(empMap.get("EMP_EMAIL_ADDRESS"));
				
				// 無E-mail
				if(StringUtils.isBlank(mail_address))
					errorMap.add(ObjectUtils.toString(empMap.get("EMP_ID")));
				
				// Email格式錯誤
				else if(isEmail(mail_address) == false)
					errorMap2.add(ObjectUtils.toString(empMap.get("EMP_ID")));
				
				else {
					List<Map<String, String>> mailList = new ArrayList<Map<String,String>>();
					Map<String, String> mailMap = new HashMap<String, String>();
					mailMap.put(FubonSendJavaMail.MAIL, mail_address);
					mailList.add(mailMap);
					
					FubonSendJavaMail sendMail = new FubonSendJavaMail();
					FubonMail mail = new FubonMail();
					Map<String, Object> annexData = new HashMap<String, Object>();
					mail.setLstMailTo(mailList);
					//設定信件主旨
					mail.setSubject("【異常通報】分行主管已完成帳務及個金RM行為態樣確認，惟客戶實體/電子回函紀錄有異常通知");
					//設定信件內容
					String content = "";
					content += "以下為分行主管回覆正常但回函為帳務不符或有不當情事內容：";
					content += "<br/><br/>";
					
					content += "<table border=\"1\" style=\"text-align:center;\">";
					
					content += "<tr>";
					content += "<td>分行</td>";
					content += "<td>客戶</td>";
					content += "<td>回函日期</td>";
					content += "<td>回函結果_帳務是否相符</td>";
					content += "<td>回函結果_是否有不當情事</td>";
					content += "<td>錄音訪談紀錄</td>";
					content += "</tr>";
					
					for (Map<String, Object> map : list) {
						content += "<tr>";
						content += "<td>" + ObjectUtils.toString(map.get("BRANCH_STR")) + "</td>";
						content += "<td>" + ObjectUtils.toString(map.get("CUST_STR")) + "</td>";
						content += "<td>" + ObjectUtils.toString(map.get("RECEIVE_DATE")) + "</td>";
						content += "<td>" + ObjectUtils.toString(map.get("RTN_STATUS_AST")) + "</td>";
						content += "<td>" + ObjectUtils.toString(map.get("RTN_STATUS_NP")) + "</td>";
						content += "<td>" + ObjectUtils.toString(map.get("CONTACT_STATUS")) + "</td>";
						content += "</tr>";
					}
					
					content += "</table>";
					
					content += "<br/><br/>";
					
					logger.info("sendMailTo175C mail_address:" + mail_address);
					logger.info("sendMailTo175C content:" + content);
					
					mail.setContent(content);
					sendMail.sendMail(mail, annexData);
				}
			}
			
			//將狀態改為異常名單已通報分行人員管理科
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
			StringBuffer sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_ROTATION_MAIN ");
			sb.append(" SET PROCESS_STATUS = '7' "); //異常名單通報分行人員管理科
			sb.append(" 	  ,LASTUPDATE = SYSDATE, MODIFIER = 'BTPMS429_SEND175CMAIL.java' ");
			sb.append(" WHERE (CUST_ID, PRJ_ID) IN (SELECT A.CUST_ID, A.PRJ_ID FROM TBPMS_ROTATION_MAIN A INNER JOIN TBCRM_TRS_PRJ_MAST P ON P.PRJ_ID = A.PRJ_ID ");
			sb.append("			WHERE A.BRN_MGM_YN = 'Y' "); //分行主管回覆正常
			sb.append("			AND A.STATEMENT_SEND_TYPE IN ('1', '2') "); //電子/實體函證
			sb.append("			AND TRUNC(A.REC_DATE) <= TRUNC(A.RECEIVE_DATE) "); //錄音日期<=回函日期
			sb.append("			AND (A.RTN_STATUS_AST = 'N' OR A.RTN_STATUS_NP = 'Y') "); //回函為:帳務不符或有不當情事	
			sb.append("			AND TRUNC(P.PRJ_EXE_DATE) = TRUNC(SYSDATE)) "); //專案執行日期生效 //還是等執行日才押上狀態
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);
		} else {
			logger.info("無照會分行人員管理科人員異常內容的筆數");
		}
		
		
		if(errorMap.size() > 0)
			logger.info("該人員無E-mail:" + errorMap);
		
		if(errorMap2.size() > 0)
			logger.info("該人員Email格式錯誤:" + errorMap2);
	}
		
	// 分行人員管理科人員
	private List<Map<String, Object>> get175CMAIL_LIST() throws Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT EMP_ID, EMP_EMAIL_ADDRESS ");
		sb.append(" FROM TBORG_MEMBER ");
		sb.append(" WHERE EMP_ID IN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.2022CMDT_175C_EMPID') ");
		
		queryCondition.setQueryString(sb.toString());

		return dam.exeQuery(queryCondition);
	}
		
	public static boolean isEmail(String email) {
		
		Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		
		if (matcher.find())
			return true;
		
		return false;
	}
	
}


