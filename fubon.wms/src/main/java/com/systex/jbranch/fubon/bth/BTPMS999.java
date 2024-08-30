package com.systex.jbranch.fubon.bth;

import java.util.ArrayList;
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

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("btpms999")
@Scope("prototype")
public class BTPMS999 extends BizLogic {
	
	private DataAccessManager dam;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 分行已於電子函證「對帳單內容是否符合」回覆不符(查詢SQL)
	private List<Map<String, Object>> secondMailListSQL() throws Exception {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT TO_CHAR(TRUNC(RPT.RESPOND_DATE), 'YYYY/MM/DD') AS REPORT_DATE, RPT.BRANCH_NBR || '-' || RPT.BRANCH_NAME AS BRANCH_STR, PAR.PARAM_NAME AS REPORT_NAME, RPT.COUNTS ");
		sb.append("FROM VWPMS_INTERNAL_CONTROL_INFO RPT ");
		sb.append("LEFT JOIN TBSYSPARAMETER PAR ON RPT.REPORT_TYPE = PAR.PARAM_CODE AND PAR.PARAM_TYPE = 'PMS.REPORT_TO_TABLE' ");
		sb.append("WHERE RPT.STATUS = 'ABNORMAL' ");
		sb.append("AND TRUNC(RPT.RESPOND_DATE) = (TRUNC(SYSDATE) - 1) ");

		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}
	
	/***
	 * 系統應自動寄送通知信予客群業管部/分行內控品管科窗口，通知信範圍為：營業單位前一日於各項內控報表回覆初判異常。
	 * 主旨：【異常通報】分行於內控報表回覆異常
	 */
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
					mail.setSubject("【異常通報】分行於內控報表回覆異常");
					//設定信件內容
					String content = "";
					content += "以下為前一個日曆日分行於內控報表回覆異常明細：";
					content += "<br/><br/>";
					
					content += "<table border=\"1\" style=\"text-align:center;\">";
					
					content += "<tr>";
					content += "<td>分行別</td>";
					content += "<td>報表名稱</td>";
					content += "<td>分行回覆日期</td>";
					content += "<td>筆數</td>";
					content += "</tr>";
					
					for (Map<String, Object> map : list) {
						content += "<tr>";
						content += "<td>" + map.get("BRANCH_STR") + "</td>";
						content += "<td>" + map.get("REPORT_NAME") + "</td>";
						content += "<td>" + map.get("REPORT_DATE") + "</td>";
						content += "<td>" + map.get("COUNTS") + "</td>";
						content += "</tr>";
					}
					
					content += "</table>";
					
					content += "<br/><br/>";
					
					content += "請總行單位盡速與分行聯繫進行後續作業。";
					
					mail.setContent(content);
					sendMail.sendMail(mail, annexData);
				}
			}
		} else {
			logger.info("本日無前一個日曆日分行於內控報表回覆異常。");
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
			case "HEAD" :
			case "ALL" :
				sb.append("  AND EXISTS (SELECT 1 FROM TBSYSPARAMETER P WHERE P.PARAM_TYPE = 'PMS.INTERNAL_CONTROL_HEAD_BOSS' AND P.PARAM_CODE = INFO.EMP_ID) ");
			
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