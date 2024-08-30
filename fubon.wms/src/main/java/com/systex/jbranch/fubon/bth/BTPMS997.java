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

@Repository("btpms997")
@Scope("prototype")
public class BTPMS997 extends BizLogic {
	
	private DataAccessManager dam;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 日報-七日內產出筆數
	private List<Map<String, Object>> secondMailListSQL() throws Exception {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT START_YYYYMMDD, ");
		sb.append("       END_YYYYMMDD, ");
		sb.append("       MONTHS_YYYYMM, ");
		sb.append("       REPORT_TYPE, ");
		sb.append("       REPORT_NAME, ");
		sb.append("       REPORT_YM_TYPE, ");
		sb.append("       REPORT_ORDER, ");
		sb.append("       COUNTS ");
		sb.append("FROM VWPMS_PERIOD_STATISTICS_INFO RPT ");
		sb.append("WHERE REPORT_YM_TYPE = 'D' ");
		sb.append("ORDER BY REPORT_ORDER ");

		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}
	
	public void sendMailToALL(Object body, IPrimitiveMap<?> header) throws Exception {
		
		List<String> errorMap = new ArrayList<String>();
		List<String> errorMap2 = new ArrayList<String>();
		
		List<Map<String, Object>> list = this.secondMailListSQL();
		List<Map<String, Object>> empList = this.getMAIL_LIST("ALL", null);
		
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
				mail.setSubject("內控報表產出控管表_日報");
				
				//設定信件內容
				String content = "";
				content += "以下為前一周於內控報表產出之筆數：";
				content += "<br/><br/>";
				
				content += "<table border=\"1\" style=\"text-align:center;\">";
				
				content += "<tr>";
				content += "<td>報表名稱</td>";
				content += "<td>統計期間</td>";
				content += "<td>筆數</td>";
				content += "</tr>";
				
				for (Map<String, Object> map : list) {
					content += "<tr>";
					content += "<td>" + map.get("REPORT_NAME") + "</td>";
					content += "<td>" + map.get("START_YYYYMMDD") + "~" + map.get("END_YYYYMMDD") + "</td>";
					content += "<td>" + map.get("COUNTS") + "</td>";
					content += "</tr>";
				}
				
				content += "</table>";
				
				content += "<br/><br/>";
				
//					content += "請總行單位盡速與分行聯繫進行後續作業。";
				
				mail.setContent(content);
				sendMail.sendMail(mail, annexData);
			}
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
		
		sb.append("SELECT BASE.PARAM_CODE AS EMP_ID, MEM.EMP_EMAIL_ADDRESS ");
		sb.append("FROM TBSYSPARAMETER BASE ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON BASE.PARAM_CODE = MEM.EMP_ID ");
		sb.append("WHERE PARAM_TYPE = 'PMS.PERIOD_STATISTICS_MAIL_LIST' ");

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