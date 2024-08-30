package com.systex.jbranch.app.server.fps.mao1421;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_MD_MASTVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/08/08
 * 
 * modify by ocean 2016/11/16
 */
@Component("mao1421")
@Scope("prototype")
public class MAO1421 extends BizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(MAO1421.class);
	
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");

	//每天 08:55、12:55、16:55執行一次
	//取即將領用而尚未領用的資料。
	public void AlertToTake(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
						
		sql.append("SELECT BRANCH_NBR, AO_CODE, EMP_NAME, USE_DATE, USE_PERIOD_S_TIME, USE_PERIOD_E_TIME, START_TIME, END_TIME, START_DATETIME, END_DATETIME, DEV_NBR, DEV_STATUS, EMP_EMAIL_ADDRESS ");
		sql.append("FROM ( ");
		sql.append("  SELECT E.BRANCH_NBR, E.AO_CODE, E.EMP_NAME, P.USE_DATE,  ");
		sql.append("         P.USE_PERIOD_S_TIME, P.USE_PERIOD_E_TIME, SUBSTR(P.USE_PERIOD_S_TIME, 1, 2) || ':00' AS START_TIME, SUBSTR(P.USE_PERIOD_E_TIME, 1, 2) || ':00' AS END_TIME,  ");
		sql.append("         RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_S_TIME || '00', 14, '0') AS START_DATETIME, ");
		sql.append("         CASE WHEN P.USE_PERIOD_E_TIME < USE_PERIOD_S_TIME THEN RPAD(TO_CHAR(P.USE_DATE + 1, 'yyyyMMdd')  || P.USE_PERIOD_E_TIME || '00', 14, '0')  ");
		sql.append("         ELSE RPAD(TO_CHAR(P.USE_DATE, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0') END AS END_DATETIME, ");
		sql.append("         P.DEV_NBR, P.DEV_STATUS, EMP.EMP_EMAIL_ADDRESS   ");
		sql.append("  FROM TBMAO_DEV_APL_PLIST P, VWORG_BRANCH_EMP_DETAIL_INFO E, TBORG_MEMBER EMP   ");
		sql.append("  WHERE P.APL_EMP_ID = EMP.EMP_ID  ");
		sql.append("  AND E.EMP_ID = EMP.EMP_ID   ");
		sql.append("  AND P.DEV_STATUS = 'C05'   ");
		sql.append(") ");
		sql.append("WHERE START_DATETIME-TO_CHAR(CURRENT_TIMESTAMP, 'yyyyMMddHH24MISS') <= 10000 AND START_DATETIME-TO_CHAR(CURRENT_TIMESTAMP, 'yyyyMMddHH24MISS') >= 0 ");
		sql.append("ORDER BY USE_DATE DESC ");
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		for (Map<String, Object> map : list) {
			if (isEmail(map.get("EMP_EMAIL_ADDRESS").toString()) == false) {
				logger.error(map.get("EMP_NAME").toString() + "E-mail格式錯誤");
			} else {
				List<Map<String, String>> mailList = new ArrayList<Map<String,String>>();
				Map<String, String> mailMap = new HashMap<String, String>();
				mailMap.put(FubonSendJavaMail.MAIL, map.get("EMP_EMAIL_ADDRESS").toString());
				mailList.add(mailMap);
				
				FubonSendJavaMail sendMail = new FubonSendJavaMail();
				FubonMail mail = new FubonMail();
				Map<String, Object> annexData = new HashMap<String, Object>();
				
				mail.setLstMailTo(mailList);
				mail.setSender("from");
				//設定信件主旨
				mail.setSubject("行動載具領用通知");
				//設定信件內容
				//您申請13:00-17:00借用行動載具ipad-704-02，請記得領用。
				mail.setContent("您申請" + map.get("START_TIME") + "~" + map.get("END_TIME") + "借用行動載具" + map.get("DEV_NBR").toString() + "，請記得領用。");
				//寄出信件-無附件
				sendMail.sendMail(mail,annexData);
			}
		}
	}
	
	//每天 09:00、13:00、17:00
	//執行一次處理逾時未領用，將狀態更新為未領用A03(代表釋放設備)
	//執行一次處理逾時未核可，將狀態更新為已退回A01(代表釋放設備)
	public void ReleaseDev(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {
		dam = this.getDataAccessManager();
		
		//執行一次處理逾時未領用，將狀態更新為未領用A03(代表釋放設備)
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
						
		sql.append("UPDATE TBMAO_DEV_APL_PLIST P SET P.DEV_STATUS = 'A03'  ");
		sql.append("WHERE P.DEV_STATUS = 'C05' ");
		sql.append("AND (		 ");
		sql.append("	(USE_PERIOD_E_TIME > USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0')) OR ");
		sql.append("	(USE_PERIOD_E_TIME < USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE + 1, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0'))  ");
		sql.append(") ");
		
		queryCondition.setQueryString(sql.toString());
		
		dam.exeUpdate(queryCondition);
		
		//執行一次處理逾時未核可，將狀態更新為已退回A01(代表釋放設備)
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		
		sql.append("UPDATE TBMAO_DEV_APL_PLIST P SET P.DEV_STATUS = 'A01'  ");
		sql.append("WHERE P.DEV_STATUS = 'B04' ");
		sql.append("AND (		 ");
		sql.append("	(USE_PERIOD_E_TIME > USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0')) OR ");
		sql.append("	(USE_PERIOD_E_TIME < USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE + 1, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0'))  ");
		sql.append(") ");
		
		queryCondition.setQueryString(sql.toString());
		
		dam.exeUpdate(queryCondition);
	}
	
	//取尚未歸還的資料。
	//每天 08:55～17:55 每隔30分鐘執行一次
	public void AlertToReturn(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql_s = new StringBuffer();
		
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
	    Date now = new Date();
	    
    	//IF 時間點 = 08:55、12:55、16:55 執行
    	if (sdf.format(now).equals(sdf.format(sdf.parse("08:55"))) || sdf.format(now).equals(sdf.format(sdf.parse("12:55"))) || sdf.format(now).equals(sdf.format(sdf.parse("16:55")))) {
    		sql_s.append("SELECT E.DEPT_ID AS BRANCH_NBR, AO.AO_CODE, E.EMP_ID, E.EMP_NAME, P.USE_DATE, P.USE_PERIOD_S_TIME, P.USE_PERIOD_E_TIME, P.DEV_NBR, P.DEV_STATUS, E.EMP_EMAIL_ADDRESS, PR.PRIVILEGEID ");
    		sql_s.append("FROM TBMAO_DEV_APL_PLIST P, TBORG_MEMBER E ");
    		sql_s.append("LEFT JOIN TBORG_SALES_AOCODE AO ON E.EMP_ID = AO.EMP_ID AND AO.TYPE = '1' ");
    		sql_s.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON E.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
    		sql_s.append("LEFT JOIN TBSYSSECUROLPRIASS PR ON MR.ROLE_ID = PR.ROLEID ");
    		sql_s.append("WHERE P.APL_EMP_ID = E.EMP_ID ");
    		sql_s.append("AND P.DEV_STATUS = 'D06' ");
    	} else {
    		sql_s.append("SELECT E.DEPT_ID AS BRANCH_NBR, AO.AO_CODE, E.EMP_ID, E.EMP_NAME, P.USE_DATE, P.USE_PERIOD_S_TIME, P.USE_PERIOD_E_TIME, P.DEV_NBR, P.DEV_STATUS, E.EMP_EMAIL_ADDRESS, PR.PRIVILEGEID "); 
    		sql_s.append("FROM TBMAO_DEV_APL_PLIST P, TBORG_MEMBER E ");
    		sql_s.append("LEFT JOIN TBORG_SALES_AOCODE AO ON E.EMP_ID = AO.EMP_ID AND AO.TYPE = '1' ");
    		sql_s.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON E.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
    		sql_s.append("LEFT JOIN TBSYSSECUROLPRIASS PR ON MR.ROLE_ID = PR.ROLEID ");
    		sql_s.append("WHERE P.APL_EMP_ID = E.EMP_ID ");
    		sql_s.append("AND P.DEV_STATUS = 'E07' ");
    	}

    	queryCondition.setQueryString(sql_s.toString());
    	List<Map<String, Object>> list = dam.exeQuery(queryCondition);
    	
    	queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql_s = new StringBuffer(); 
		sql_s.append("DELETE FROM TBCRM_WKPG_MD_MAST WHERE DISPLAY_NO = '199' ");
		queryCondition.setQueryString(sql_s.toString());
		dam.exeUpdate(queryCondition);
    	
    	//理專信件部分
		for (Map<String, Object> map : list) {
			TBCRM_WKPG_MD_MASTVO msvo = new TBCRM_WKPG_MD_MASTVO();
			msvo.setSEQ(getSN());
			msvo.setPRIVILEGEID(String.valueOf(map.get("PRIVILEGEID")));
			msvo.setEMP_ID(String.valueOf(map.get("EMP_ID")));
			msvo.setROLE_LINK_YN("N");
			msvo.setFRQ_TYPE("D");
			msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
			msvo.setDISPLAY_NO("199");
			msvo.setCLICK_YN("N");
			msvo.setRPT_NAME("iPad歸還通知-" + map.get("DEV_NBR"));
			msvo.setRPT_PROG_URL("MAO121");
			msvo.setPASS_PARAMS(null);
			msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));
			dam.create(msvo);
			
			String bra_nbr = map.get("BRANCH_NBR").toString();
			String emp_name = map.get("EMP_NAME").toString();
			String email = map.get("EMP_EMAIL_ADDRESS").toString();
			String dev_nbr = map.get("DEV_NBR").toString();
			Date use_date = (Date) map.get("USE_DATE") ;
			Calendar cal = Calendar.getInstance();
			cal.setTime(use_date);
			
			if (isEmail(email) == false) {
				logger.error(emp_name + "E-mail格式錯誤");
			} else {
				// MAIL 通知
				if((!StringUtils.equals(ObjectUtils.toString(map.get("USE_PERIOD_E_TIME")), "0800")) && StringUtils.equals(sdfYYYYMMDD.format((Timestamp) map.get("USE_DATE")), sdfYYYYMMDD.format(new Date())) ||
					(StringUtils.equals(ObjectUtils.toString(map.get("USE_PERIOD_E_TIME")), "0800")) && StringUtils.equals(sdfYYYYMMDD.format(((Timestamp) map.get("USE_DATE")).getTime()+ 24 * 60 * 60 * 1000), sdfYYYYMMDD.format(new Date()))){
					
					List<Map<String, String>> mailList = new ArrayList<Map<String,String>>();
					Map<String, String> mailMap = new HashMap<String, String>();
					mailMap.put(FubonSendJavaMail.MAIL, email);
					mailList.add(mailMap);
					
					FubonSendJavaMail sendMail = new FubonSendJavaMail();
					FubonMail mail = new FubonMail();
					Map<String, Object> annexData = new HashMap<String, Object>();
					
					mail.setLstMailTo(mailList);
					mail.setSender("from");
					//設定信件主旨
					mail.setSubject("iPad歸還通知" + dev_nbr);
					//設定信件內容
					mail.setContent(emp_name + "專員您好：\n\n" + "提醒您！ " +
							        cal.get(Calendar.YEAR) + "年" + 
							        (cal.get(Calendar.MONTH) + 1) + "月" + 
							        cal.get(Calendar.DAY_OF_MONTH) + "日" + 
							        cal.get(Calendar.HOUR_OF_DAY) + "時，申請的iPad(" + dev_nbr + ")使用期間已過期，請儘速歸還，謝謝。");
					//寄出信件-無附件
					sendMail.sendMail(mail,annexData);
				}
			}
			
			
			//主管信件部分
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    		StringBuffer sql = new StringBuffer();

    		sql.append("SELECT E.EMP_ID, E.EMP_NAME, EMP.EMP_EMAIL_ADDRESS ");
    		sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO E, TBORG_MEMBER EMP ");
    		sql.append("WHERE E.BRANCH_NBR = :bra_nbr ");
    		sql.append("AND E.EMP_ID = EMP.EMP_ID ");
    		sql.append("AND E.ROLE_ID IN (SELECT DISTINCT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = '006') ");
    		
    		queryCondition.setQueryString(sql.toString());
    		queryCondition.setObject("bra_nbr", bra_nbr);
    		
    		List<Map<String, Object>> data = dam.exeQuery(queryCondition);
    		
			for (Map<String, Object> bossMap : data) {
	    		// 內部事件通知
				msvo = new TBCRM_WKPG_MD_MASTVO();
				msvo.setSEQ(getSN());
				msvo.setPRIVILEGEID("006");
				msvo.setEMP_ID(bossMap.get("EMP_ID").toString());
				msvo.setROLE_LINK_YN("N");
				msvo.setFRQ_TYPE("D");
				msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
				msvo.setDISPLAY_NO("199");
				msvo.setCLICK_YN("N");
				msvo.setRPT_NAME("iPad歸還通知-" + map.get("DEV_NBR"));
				msvo.setRPT_PROG_URL("MAO142");
				msvo.setPASS_PARAMS(null);
				msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));
				dam.create(msvo);
			}
			
    		String manage_name = data.get(0).get("EMP_NAME").toString();
    		String manage_email = data.get(0).get("EMP_EMAIL_ADDRESS").toString();
    		
    		if (isEmail(manage_email) == false) {
				logger.error(manage_name + "E-mail格式錯誤");
			} else {
				if((!StringUtils.equals(ObjectUtils.toString(map.get("USE_PERIOD_E_TIME")), "0800")) && StringUtils.equals(sdfYYYYMMDD.format((Timestamp) map.get("USE_DATE")), sdfYYYYMMDD.format(new Date())) ||
					(StringUtils.equals(ObjectUtils.toString(map.get("USE_PERIOD_E_TIME")), "0800")) && StringUtils.equals(sdfYYYYMMDD.format(((Timestamp) map.get("USE_DATE")).getTime()+ 24 * 60 * 60 * 1000), sdfYYYYMMDD.format(new Date()))){
						
					List<Map<String, String>> mailList2 = new ArrayList<Map<String,String>>();
					Map<String, String> mailMap2 = new HashMap<String, String>();
					mailMap2.put(FubonSendJavaMail.MAIL, email);
					mailList2.add(mailMap2);
					
					FubonSendJavaMail sendMail2 = new FubonSendJavaMail();
					FubonMail mail2 = new FubonMail();
					Map<String, Object> annexData2 = new HashMap<String, Object>();
					
					mail2.setLstMailTo(mailList2);
					mail2.setSender("from");
					//設定信件主旨
					mail2.setSubject("iPad歸還通知" + dev_nbr);
					//設定信件內容
					//設定信件內容
					mail2.setContent("主管您好：\n\n" + "提醒您！ " +
							         cal.get(Calendar.YEAR) + "年" + 
							         (cal.get(Calendar.MONTH) + 1) + "月" + 
							         cal.get(Calendar.DAY_OF_MONTH) + "日" + 
							         cal.get(Calendar.HOUR_OF_DAY) + "時，" + emp_name + "，申請的iPad(" + dev_nbr + ")使用期間已過期，請提醒歸還，謝謝。");
					//寄出信件-無附件
					sendMail2.sendMail(mail2,annexData2);
				}
			}
		}
	}
	
	//找出逾時未歸還設備，更新設備狀態為E07
	//每天 09:00、13:00、17:00執行一次
	public void SetDev(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
						
		sql.append("UPDATE TBMAO_DEV_APL_PLIST P  ");
		sql.append("SET P.DEV_STATUS = 'E07'  ");
		sql.append("WHERE P.DEV_STATUS = 'D06'  ");
		sql.append("AND( ");
		sql.append("	(USE_PERIOD_E_TIME > USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0')) OR ");
		sql.append("	(USE_PERIOD_E_TIME < USE_PERIOD_S_TIME AND TO_CHAR(CURRENT_TIMESTAMP,'yyyyMMddHH24MISS') >= RPAD(TO_CHAR(USE_DATE + 1, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00', 14, '0'))  ");
		sql.append(")  ");
		
		queryCondition.setQueryString(sql.toString());
		
		dam.exeUpdate(queryCondition);
	}
	
	//信箱Email格式檢查
	public static boolean isEmail(String email) {
		Pattern emailPattern = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find()) {
			return true;
		}
		return false;
	}
	
	// 流水號
	private String getSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCRM_WKPG_MD_MAST.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
}