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
 * 1. 抽樣之客戶透過Bill Hunter發送一封函證
 * 
 * @date 2019/12/03
 *
 */

@Repository("btpms421")
@Scope("prototype")
public class BTPMS421 extends BizLogic {
	
	private DataAccessManager dam;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private BthFtpJobUtil bthUtil = new BthFtpJobUtil();
	private AuditLogUtil audit = null;
	
	public void BTPMS421_SEND(Object body, IPrimitiveMap<?> header) throws Exception {
		
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT B.CUST_ID, TO_CHAR(B.DATA_DATE,'YYYYMMDD') AS DATA_DATE, C.EMAIL, B.BRANCH_NBR, B.EMP_NAME ");
		sql.append(" FROM TBPMS_10CMDT_EBILL_COMFIRM B ");
		sql.append(" LEFT JOIN TBCRM_CUST_CONTACT C ON B.CUST_ID = C.CUST_ID ");
		sql.append(" WHERE B.CONFIRM_FLAG IN ('A','B') AND TO_CHAR(B.DATA_DATE,'YYYYMM') = TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(!list.isEmpty()){
			String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			String fileName = tempPath + "reports\\WMS_EBILL_COMFM_Q_SEND.txt";
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "big5"));

			for(Map<String, Object> map : list) {
				String temp = "";
				//抽樣之客戶透過Bill Hunter發送一封函證，介接欄位為固定長度
				//客戶ID(X11)、Email(X50)、對帳單基準日(X8)、客戶歸屬行代碼(X5)、理專姓名(X32)
				temp += String.format("%-11s",ObjectUtils.toString(map.get("CUST_ID")));
				temp += String.format("%-50s",ObjectUtils.toString(map.get("EMAIL")));
				temp += String.format("%-8s",ObjectUtils.toString(map.get("DATA_DATE")));
				temp += String.format("%-5s",ObjectUtils.toString(map.get("BRANCH_NBR")));
				temp += String.format("%-32s",ObjectUtils.toString(map.get("EMP_NAME")));
				
				bw.write(temp+"\r\n");
			}
			
			bw.flush();
			bw.close();
		}
	}
	
	public void BTPMS421_AO7Y_SEND(Object body, IPrimitiveMap<?> header) throws Exception {
		
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT B.CUST_ID, TO_CHAR(B.DATA_DATE,'YYYYMMDD') AS DATA_DATE, C.EMAIL, B.BRANCH_NBR, B.EMP_NAME ");
		sql.append(" FROM TBPMS_10CMDT_EBILL_COMFIRM B ");
		sql.append(" LEFT JOIN TBCRM_CUST_CONTACT C ON B.CUST_ID = C.CUST_ID ");
		sql.append(" WHERE B.CONFIRM_FLAG ='C' AND TO_CHAR(B.DATA_DATE,'YYYYMM') = TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMM') ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = tempPath + "reports\\WMS_EBILL_COMFM_Q_SEND.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "big5"));
		
		for(Map<String, Object> map : list) {
			String temp = "";
			//抽樣之客戶透過Bill Hunter發送一封函證，介接欄位為固定長度
			//客戶ID(X11)、Email(X50)、對帳單基準日(X8)、客戶歸屬行代碼(X5)、理專姓名(X32)
			temp += String.format("%-11s",ObjectUtils.toString(map.get("CUST_ID")));
			temp += String.format("%-50s",ObjectUtils.toString(map.get("EMAIL")));
			temp += String.format("%-8s",ObjectUtils.toString(map.get("DATA_DATE")));
			temp += String.format("%-5s",ObjectUtils.toString(map.get("BRANCH_NBR")));
			temp += String.format("%-32s",ObjectUtils.toString(map.get("EMP_NAME")));
			
			bw.write(temp+"\r\n");
		}
		
		bw.flush();
		bw.close();
	}
	
	public void BTPMS421_AO7Y_390HOST(Object body, IPrimitiveMap<?> header) throws Exception {
		
		// 記錄排程監控的log
		audit = (AuditLogUtil) ((Map<?, ?>) body).get(SchedulerHelper.AUDIT_PARAMETER_KEY);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();

	    sql.append(" SELECT CUST.BRANCH_NBR , CUST.CUST_ID ");
	    sql.append(" FROM TBPMS_10CMDT_EBILL_COMFIRM_NY A ");
	    sql.append(" INNER JOIN TBPMS_SALES_AOCODE_REC AOCODE ");
	    sql.append("   ON A.EMP_ID = AOCODE.EMP_ID ");
	    sql.append("   AND TO_DATE(A.YYYYMM || '01','YYYYMMDD')-1 BETWEEN AOCODE.START_TIME AND AOCODE.END_TIME ");
	    sql.append(" INNER JOIN TBPMS_CUST_REC_N CUST ");
	    sql.append("   ON  AOCODE.AO_CODE = CUST.AO_CODE ");
	    sql.append("   AND TO_DATE(A.YYYYMM || '01','YYYYMMDD')-1 BETWEEN CUST.START_TIME AND CUST.END_TIME ");
	    sql.append(" WHERE A.YYYYMM = TO_CHAR(SYSDATE, 'YYYYMM') ");
	    sql.append("   AND CUST.BRANCH_NBR IS NOT NULL ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = tempPath + "reports\\WMS_EBILL_COMFM_AO_SEND.TXT";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "big5"));
		
		for(Map<String, Object> map : list) {
			String temp = "";
			//抽樣之客戶透過Bill Hunter發送一封函證，介接欄位為固定長度
			//內容 (固定長度) ：理專歸屬行代碼(X5)、理專姓名(X32)
			temp += String.format("%-5s",ObjectUtils.toString(map.get("BRANCH_NBR")));
			temp += String.format("%-32s",ObjectUtils.toString(map.get("CUST_ID")));
			
			bw.write(temp+"\r\n");
		}
		
		bw.flush();
		bw.close();
		
		// 20200331:Steven 產生檢核檔ZWMS_AO7YAD.yyyymmdd 搭配 WMS_EBILL_COMFM_AO_SEND.txt 配合新版檔案加解密，調整檢核檔格式
		String contenStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileNameAO7Y = tempPath + "reports\\ZWMS_AO7YSD." + sdf.format(new Date());
		BufferedWriter bwAO7Y = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNameAO7Y), "UTF-8"));
		contenStr += LenString("WMS_AO7YSD", 10);
		contenStr += LenString("WMS_EBILL_COMFM_AO_SEND.TXT", 40);
		contenStr += LenString("PUT", 10);
		contenStr += LenString("WMS_EBILL_COMFM_AO_SEND.TXT", 40);
		bwAO7Y.write(contenStr+"\r\n");
		
		bwAO7Y.flush();
		bwAO7Y.close();
	}
	
	// 2017/4/17
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
	
	// 客戶已回覆不相符後7個營業日尚未照會名單(分行/總行共用查詢SQL)
	private List<Map<String, Object>> firstMailListSQL() throws Exception {
		
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT  BRANCH_NBR, ");
		sb.append("        BRANCH_NBR || '-' || BRANCH_NAME AS BRANCH_STR, ");
		sb.append("        REPLACE(SUBSTR(RECV_DATE, 1, 10), '/', '') AS CUST_REPLY_DATE, ");
		sb.append("        COUNT(*) AS CNT ");
		sb.append("FROM TBPMS_10CMDT_EBILL_COMFIRM ");
		sb.append("WHERE FIRSTUPDATE_TIME IS NULL ");
		sb.append("AND RECV_DATE IS NOT NULL ");
		sb.append("AND CONFIRM_FLAG = 'N' ");
		sb.append("AND TO_DATE(REPLACE(SUBSTR(RECV_DATE, 1, 10), '/', ''), 'YYYYMMDD') >= PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', -8) ");
		sb.append("GROUP BY BRANCH_NBR, BRANCH_NAME, REPLACE(SUBSTR(RECV_DATE,  1, 10), '/', '') ");

		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}
	
	// 客戶已回覆不相符後7個營業日尚未照會名單(分行)
	public void sendMailToBranch(Object body, IPrimitiveMap<?> header) throws Exception {
		
		List<String> errorMap = new ArrayList<String>();
		List<String> errorMap2 = new ArrayList<String>();
		
		List<Map<String, Object>> list = this.firstMailListSQL();
		
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				List<Map<String, Object>> empList = this.getMAIL_LIST("BRANCH", (String) map.get("BRANCH_NBR"));
				
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
						mail.setSubject("【通知" + map.get("BRANCH_STR") + "】對帳單函證回函(電子/簡訊)回覆異常報表");
						//設定信件內容
						String content = "";
						
						content += "Dear 作業主管、有權：";
						content += "<br /><br />";
						content += "一、本行接獲分行客戶函證回覆結果為「不符」，請與客戶聯繫確認<u><font color=\"red\">對帳單之交易明細及餘額是否正確</font></u>，並將連繫結果回覆於業管系統「對帳單函證回函(電子/簡訊)回覆異常報表」。";
						content += "<br />";
						content += "※路徑：個金分行業管系統→「績效管理」→「內控管理報表_每日」→「對帳單函證回函(電子/簡訊)回覆異常報表」";
						content += "<br/><br/>";
						content += "二、若客戶反應查無對帳單，請<u><font color=\"red\">帶客戶進線客服核對帳務或補寄對帳單，或請客戶使用網路/行動銀行申請補寄對帳單，惟分行仍須確認對帳單之交易明細及餘額是否正確</font></u>，並將連繫結果回覆於報表上。<br />";
						content += "※網路/行動銀行補寄申請路徑如下：網路/行動銀行>個人服務>電子對帳單設定>補寄";
						content += "<br /><br />";
						content += "三、以下為分行尚未照會筆數：";						
						content += "<table border=\"1\" style=\"text-align:center;\">";
						content += "<tr>";
						content += "<td>分行別</td>";
						content += "<td>客戶回覆日期</td>";
						content += "<td>筆數</td>";
						content += "</tr>";
						content += "<tr>";
						content += "<td>" + map.get("BRANCH_STR") + "</td>";
						content += "<td>" + map.get("CUST_REPLY_DATE") + "</td>";
						content += "<td>" + map.get("CNT") + "</td>";
						content += "</tr>";
						content += "</table>";
						content += "<br/><br/>";
						content += "如有上述報表回覆問題請聯繫，客群業管部/分行內控品管科<br />";

						mail.setContent(content);
						sendMail.sendMail(mail,annexData);
					}
				}
			}
		} else {
			logger.info("本日無客戶已回覆不相符後7個營業日尚未照會名單_分行");
		}
		
		if(errorMap.size() > 0)
			logger.info("該人員無E-mail:"+errorMap);
		
		if(errorMap2.size() > 0)
			logger.info("該人員Email格式錯誤:"+errorMap2);
	}

	// 客戶已回覆不相符後7個營業日尚未照會名單(總行)
	public void sendMailToHead(Object body, IPrimitiveMap<?> header) throws Exception {
		
		List<String> errorMap = new ArrayList<String>();
		List<String> errorMap2 = new ArrayList<String>();
		
		List<Map<String, Object>> list = this.firstMailListSQL();
		List<Map<String, Object>> empList = this.getMAIL_LIST("HEAD", null);
		
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
					mail.setSubject("【通知總行】對帳單函證回函(電子/簡訊)回覆異常報表");
					//設定信件內容
					String content = "";
					content += "接獲分行客戶於電子對帳單函證回覆結果為「不符」，以下為分行尚未照會筆數：";
					content += "<br/><br/>";
					content += "<table border=\"1\" style=\"text-align:center;\">";
					content += "<tr>";
					content += "<td>分行別</td>";
					content += "<td>客戶回覆日期</td>";
					content += "<td>筆數</td>";
					content += "</tr>";
					
					for (Map<String, Object> map : list) {
						content += "<tr>";
						content += "<td>" + map.get("BRANCH_STR") + "</td>";
						content += "<td>" + map.get("CUST_REPLY_DATE") + "</td>";
						content += "<td>" + map.get("CNT") + "</td>";
						content += "</tr>";
					}
					
					content += "</table>";
					
					mail.setContent(content);
					sendMail.sendMail(mail, annexData);
				}
			}
		} else {
			logger.info("本日無客戶已回覆不相符後7個營業日尚未照會名單(總行)");
		}
		
		if(errorMap.size() > 0)
			logger.info("該人員無E-mail:" + errorMap);
		
		if(errorMap2.size() > 0)
			logger.info("該人員Email格式錯誤:" + errorMap2);
	}
	
	// 分行已於電子函證「對帳單內容是否符合」回覆不符(查詢SQL)
	private List<Map<String, Object>> secondMailListSQL() throws Exception {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT BRANCH_NBR || '-' || BRANCH_NAME AS BRANCH_STR, COUNT(*) AS CNT ");
		sb.append("FROM TBPMS_10CMDT_EBILL_COMFIRM ");
		sb.append("WHERE FIRSTUPDATE_TIME IS NOT NULL ");
		sb.append("AND RECV_DATE IS NOT NULL ");
		sb.append("AND CONFIRM_FLAG = 'N' ");
		sb.append("AND EBILL_CONTENT_FLAG = 'Y' ");
		sb.append("AND TRUNC(FIRSTUPDATE_TIME) = TRUNC(SYSDATE) - 1 ");
		sb.append("GROUP BY BRANCH_NBR, BRANCH_NAME ");

		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}
	
	// 分行已於電子函證「對帳單內容是否符合」回覆不符
	public void sendMailToALL(Object body, IPrimitiveMap<?> header) throws Exception {
		
		List<String> errorMap = new ArrayList<String>();
		List<String> errorMap2 = new ArrayList<String>();
		
		List<Map<String, Object>> list = this.secondMailListSQL();
		List<Map<String, Object>> empList = this.getMAIL_LIST("ALL", null);
		
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
					mail.setSubject("【異常通報】分行於對帳單函證報表回覆異常");
					//設定信件內容
					String content = "";
					content += "以下為前一個日曆日分行回覆不符對帳單內容的筆數";
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
					
					content += "請總行人員盡速完成內控後續作業。";
					
					mail.setContent(content);
					sendMail.sendMail(mail, annexData);
				}
			}
		} else {
			logger.info("本日無前一個日曆日分行回覆不符對帳單內容的筆數");
		}
		
		
		if(errorMap.size() > 0)
			logger.info("該人員無E-mail:" + errorMap);
		
		if(errorMap2.size() > 0)
			logger.info("該人員Email格式錯誤:" + errorMap2);
	}
	
	private List<Map<String, Object>> getMAIL_LIST(String type, String branchNbr) throws Exception {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT ISPRI.EMP_ID, ISPRI.EMP_ID AS NOTICT_EMP_ID, ISPRI.EMP_NAME, ISPRI.ROLE_ID, ISPRI.ROLE_NAME, 'Y' AS IS_PRIMARY_ROLE ");
		sb.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO ISPRI ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON ISPRI.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN DEFN ON MEM.DEPT_ID = DEFN.DEPT_ID ");
		sb.append("  WHERE ISPRI.ROLE_ID IS NOT NULL ");
		sb.append("  UNION ALL ");
		sb.append("  SELECT NOTPRI.EMP_ID, NOTPRI.EMP_ID AS NOTICT_EMP_ID, NOTPRI.EMP_NAME, NOTPRI.ROLE_ID, NOTPRI.ROLE_NAME, 'N' AS IS_PRIMARY_ROLE ");
		sb.append("  FROM VWORG_EMP_PLURALISM_INFO NOTPRI ");
		sb.append("  WHERE NOTPRI.ROLE_ID IS NOT NULL ");
		sb.append("  UNION ALL ");
		sb.append("  SELECT AG.AGENT_ID AS EMP_ID, AG.EMP_ID AS NOTICT_EMP_ID, AGENT_DTL.EMP_NAME, AGENT_DTL.ROLE_ID, AGENT_DTL.ROLE_NAME, 'A' AS IS_PRIMARY_ROLE ");
		sb.append("  FROM TBORG_AGENT AG ");
		sb.append("  LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO AGENT_DTL ON AG.EMP_ID = AGENT_DTL.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON AGENT_DTL.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN DEFN ON MEM.DEPT_ID = DEFN.DEPT_ID ");
		sb.append("  WHERE AG.AGENT_STATUS = 'S' ");
		sb.append("  AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("  AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("  AND AG.DEPT_ID = MEM.DEPT_ID ");
		sb.append("  UNION ALL ");
		sb.append("  SELECT AG.AGENT_ID AS EMP_ID, AG.EMP_ID AS NOTICT_EMP_ID, AGENT_DTL.EMP_NAME, AGENT_DTL.ROLE_ID, AGENT_DTL.ROLE_NAME || '(兼)', 'AN' AS IS_PRIMARY_ROLE ");
		sb.append("  FROM TBORG_AGENT AG ");
		sb.append("  LEFT JOIN VWORG_EMP_PLURALISM_INFO AGENT_DTL ON AG.EMP_ID = AGENT_DTL.EMP_ID AND AG.DEPT_ID = AGENT_DTL.DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER_PLURALISM MEM ON AGENT_DTL.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_DEFN DEFN ON MEM.DEPT_ID = DEFN.DEPT_ID ");
		sb.append("  WHERE AG.AGENT_STATUS = 'S' ");
		sb.append("  AND (TRUNC(MEM.TERDTE) >= TRUNC(SYSDATE) OR MEM.TERDTE IS NULL) ");
		sb.append("  AND MEM.ACTION <> 'D' ");
		sb.append("  AND AG.DEPT_ID = MEM.DEPT_ID ");
		sb.append(") ");

		sb.append("SELECT BASE.EMP_ID, MEM.EMP_EMAIL_ADDRESS ");
		sb.append("FROM BASE ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON BASE.EMP_ID = MEM.EMP_ID ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND EXISTS ( ");
		sb.append("  SELECT 1 ");
		sb.append("  FROM VWORG_EMP_INFO INFO ");
		sb.append("  WHERE 1 = 1 ");
		
		switch (type) {
			case "BRANCH" :
				sb.append("  AND EXISTS (SELECT * FROM TBSYSPARAMETER P WHERE P.PARAM_TYPE = 'PMS.BRAN_BOSS_ROLE' AND INFO.ROLE_ID = P.PARAM_CODE) ");
				sb.append("  AND INFO.BRANCH_NBR = :branchNbr ");
				
				queryCondition.setObject("branchNbr", branchNbr);
				
				break;
			case "HEAD" :
			case "ALL" :
				sb.append("  AND EXISTS (SELECT 1 FROM TBSYSPARAMETER P WHERE P.PARAM_TYPE = 'PMS.HEAD_BOSS' AND P.PARAM_CODE = INFO.EMP_ID) ");
			
				break;
		}
		
		sb.append("  AND BASE.NOTICT_EMP_ID = INFO.EMP_ID ");
		sb.append(") ");

		
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


