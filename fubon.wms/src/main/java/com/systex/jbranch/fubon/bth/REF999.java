package com.systex.jbranch.fubon.bth;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.systex.jbranch.app.common.fps.table.TBCAM_LOAN_SALEREC_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_MD_MASTVO;
import com.systex.jbranch.app.server.fps.ref900.REF900;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ref999")
@Scope("prototype")
public class REF999 extends BizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	public void checkAction(Object body, IPrimitiveMap<?> header) throws Exception {
		
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);
		Map<String, String> ref_prod = xmlInfo.doGetVariable("CAM.REF_PROD", FormatHelper.FORMAT_3);
		Map<String, String> ref_status = xmlInfo.doGetVariable("CAM.REF_STATUS", FormatHelper.FORMAT_3);
		
		// 每日發佈內部事件通知
		// 1. 若一直未接受則發佈通知
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT * ");
		sql.append("FROM TBCAM_LOAN_SALEREC_REVIEW ");
		sql.append("WHERE STATUS IN ('W') ");
		sql.append("AND TRUNC(sysdate) - PABTH_UTIL.FC_getBusiDay(TRUNC(TXN_DATE), 'TWD', 5) < 0 ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> noAcceptList = dam.exeQuery(queryCondition);
		
		for(Map<String, Object> map : noAcceptList) {
			// 內部事件通知
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT PRI.PRIVILEGEID FROM TBORG_MEMBER M ");
			sql.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
			sql.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
			sql.append("WHERE M.EMP_ID = :empID ");
			queryCondition.setObject("empID", map.get("USERID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> empList = dam.exeQuery(queryCondition);
			if (empList.size() == 0)
				logger.debug("該沒有設定權限群組:" + map.get("USERID"));
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("INSERT INTO TBCRM_WKPG_MD_MAST(SEQ, PRIVILEGEID, EMP_ID, ROLE_LINK_YN, FRQ_TYPE, FRQ_MWD, DISPLAY_NO, ");
			sql.append("                               CLICK_YN, CLICK_DATE, RPT_NAME, RPT_PROG_URL, PASS_PARAMS, FRQ_YEAR, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
			sql.append("VALUES(:SEQ, :PRIVILEGEID, :EMP_ID, 'Y', 'D', :FRQ_MWD, '104', ");
			sql.append("       'N', sysdate, :RPT_NAME, 'REF900', :PASS_PARAMS, :FRQ_YEAR, 0, sysdate, :CREATOR, :MODIFIER, sysdate) ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("SEQ", getSeqNum());
			queryCondition.setObject("PRIVILEGEID", empList.get(0).get("PRIVILEGEID"));
			queryCondition.setObject("EMP_ID", map.get("USERID"));
			queryCondition.setObject("FRQ_MWD", String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
			queryCondition.setObject("RPT_NAME", "是否接受轉介案件-" + map.get("SEQ"));
			queryCondition.setObject("PASS_PARAMS", map.get("SEQ"));
			queryCondition.setObject("FRQ_YEAR", String.valueOf(calendar.get(Calendar.YEAR)));
			queryCondition.setObject("CREATOR", map.get("USERID"));
			queryCondition.setObject("MODIFIER", map.get("USERID"));
			dam.exeUpdate(queryCondition);
		}
		
		
		// 2. 若一直未仲裁則發佈通知
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT * ");
		sql.append("FROM TBCAM_LOAN_SALEREC_REVIEW ");
		sql.append("WHERE STATUS in ('A') ");
		sql.append("AND TRUNC(sysdate) - PABTH_UTIL.FC_getBusiDay(TRUNC(ARBITRATE_DATE), 'TWD', 5) < 0 ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> noArbitrationList = dam.exeQuery(queryCondition);
		
		for(Map<String, Object> map : noArbitrationList) {
			List<Map<String, Object>> emp_List = this.getMAIL_LIST((String) map.get("SEQ"));
			for (Map<String, Object> empMap : emp_List) {
				// 內部事件通知, 只通知主管
				if (!((String) map.get("SALES_PERSON")).equals(ObjectUtils.toString(empMap.get("EMP_ID"))) && 
					!((String) map.get("USERID")).equals(ObjectUtils.toString(empMap.get("EMP_ID"))) && 
					!((String) map.get("CREATOR")).equals(ObjectUtils.toString(empMap.get("EMP_ID")))) {
					TBCRM_WKPG_MD_MASTVO msvo = new TBCRM_WKPG_MD_MASTVO();
					msvo.setSEQ(getSeqNum());
					msvo.setPRIVILEGEID(ObjectUtils.toString(empMap.get("PRIVILEGEID")));
					msvo.setEMP_ID(ObjectUtils.toString(empMap.get("EMP_ID")));
					msvo.setROLE_LINK_YN("Y");
					msvo.setFRQ_TYPE("D");
					msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
					msvo.setDISPLAY_NO("102");
					msvo.setCLICK_YN("N");
					msvo.setCLICK_DATE(new Timestamp(System.currentTimeMillis()));
					msvo.setRPT_NAME("待仲裁轉介案件-" + ((String) empMap.get("SEQ")));
					msvo.setRPT_PROG_URL("REF900");
					msvo.setPASS_PARAMS((String) empMap.get("SEQ"));
					msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));
					dam.create(msvo);
				}
			}
		}
		
		// 受轉介人和主管若5工作日內未處理視為接受
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT * ");
		sql.append("FROM TBCAM_LOAN_SALEREC_REVIEW ");
		sql.append("WHERE STATUS in ('A') ");
		sql.append("AND TRUNC(sysdate) - PABTH_UTIL.FC_getBusiDay(TRUNC(ARBITRATE_DATE), 'TWD', 5) >= 0 ");
		sql.append("UNION ");
		sql.append("SELECT * ");
		sql.append("FROM TBCAM_LOAN_SALEREC_REVIEW ");
		sql.append("WHERE STATUS in ('W') ");
		sql.append("AND TRUNC(sysdate) - PABTH_UTIL.FC_getBusiDay(TRUNC(TXN_DATE), 'TWD', 5) >= 0 ");

		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map :list) {
			QueryConditionIF update_cn = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			update_cn.setQueryString("UPDATE TBCAM_LOAN_SALEREC_REVIEW SET STATUS = 'Y' WHERE SEQ = :seq");
			update_cn.setObject("seq", map.get("SEQ"));
			dam.exeUpdate(update_cn);
			
			QueryConditionIF insert_cn = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer insert_sql = new StringBuffer();
			insert_sql.append("INSERT INTO TBCAM_LOAN_SALEREC(SEQ, TERRITORY_ID, TXN_DATE, REF_ORG_ID, CUST_ID, CUST_NAME, SALES_PERSON, SALES_NAME, REF_PROD, ");
			insert_sql.append("                               CASE_PROPERTY, SALES_ROLE, USERID, USERNAME, USERROLE, NEW_CUST_FLAG, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
			insert_sql.append("SELECT SEQ, TERRITORY_ID, TXN_DATE, REF_ORG_ID, CUST_ID, CUST_NAME, SALES_PERSON, SALES_NAME, REF_PROD, '1' AS CASE_PROPERTY, ");
			insert_sql.append("       SALES_ROLE, USERID, USERNAME, USERROLE, NEW_CUST_FLAG, 0, sysdate, CREATOR, MODIFIER, sysdate ");
			insert_sql.append("FROM TBCAM_LOAN_SALEREC_REVIEW WHERE SEQ = :seq ");
			insert_cn.setQueryString(insert_sql.toString());
			insert_cn.setObject("seq", map.get("SEQ"));
			dam.exeUpdate(insert_cn);
			
			// 內部事件通知
			QueryConditionIF wk_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer wk_sql = new StringBuffer();
			wk_sql.append("SELECT PRI.PRIVILEGEID FROM TBORG_MEMBER M ");
			wk_sql.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
			wk_sql.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
			wk_sql.append("WHERE M.EMP_ID = :empID ");
			wk_con.setObject("empID", map.get("USERID"));
			wk_con.setQueryString(wk_sql.toString());
			List<Map<String, Object>> empList = dam.exeQuery(wk_con);
			if (empList.size() == 0)
				logger.debug("該沒有設定權限群組:" + map.get("USERID"));
			
			QueryConditionIF insert_cn2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer insert_sql2 = new StringBuffer();
			insert_sql2.append("INSERT INTO TBCRM_WKPG_MD_MAST(SEQ, PRIVILEGEID, EMP_ID, ROLE_LINK_YN, FRQ_TYPE, FRQ_MWD, DISPLAY_NO, ");
			insert_sql2.append("                               CLICK_YN, CLICK_DATE, RPT_NAME, RPT_PROG_URL, PASS_PARAMS, FRQ_YEAR, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
			insert_sql2.append("VALUES(:SEQ, :PRIVILEGEID, :EMP_ID, 'Y', 'D', :FRQ_MWD, '103', ");
			insert_sql2.append("       'N', sysdate, :RPT_NAME, 'REF120', :PASS_PARAMS, :FRQ_YEAR, 0, sysdate, :CREATOR, :MODIFIER, sysdate) ");
			insert_cn2.setQueryString(insert_sql2.toString());
			insert_cn2.setObject("SEQ", getSeqNum());
			insert_cn2.setObject("PRIVILEGEID", empList.get(0).get("PRIVILEGEID"));
			insert_cn2.setObject("EMP_ID", map.get("USERID"));
			insert_cn2.setObject("FRQ_MWD", String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
			insert_cn2.setObject("RPT_NAME", "待回報轉介案件-" + map.get("SEQ"));
			insert_cn2.setObject("PASS_PARAMS", map.get("SEQ"));
			insert_cn2.setObject("FRQ_YEAR", String.valueOf(calendar.get(Calendar.YEAR)));
			insert_cn2.setObject("CREATOR", map.get("USERID"));
			insert_cn2.setObject("MODIFIER", map.get("USERID"));
			dam.exeUpdate(insert_cn2);
		}
		
		// 轉介發動後 三日未處理, 發MAIL
		List<String> errorMap = new ArrayList<String>();
		List<String> errorMap2 = new ArrayList<String>();
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT SEQ FROM TBCAM_LOAN_SALEREC_REVIEW WHERE STATUS = 'W' AND TRUNC(sysdate) - PABTH_UTIL.FC_getBusiDay(TRUNC(ARBITRATE_DATE), 'TWD', 3) = 0 ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		for(Map<String, Object> map :list2) {
			TBCAM_LOAN_SALEREC_REVIEWVO rvo = new TBCAM_LOAN_SALEREC_REVIEWVO();
			rvo = (TBCAM_LOAN_SALEREC_REVIEWVO) dam.findByPKey(TBCAM_LOAN_SALEREC_REVIEWVO.TABLE_UID, map.get("SEQ").toString());
			if (rvo != null) {
				List<Map<String, Object>> emp_List = this.getMAIL_LIST(rvo.getSEQ());
				for(Map<String, Object> map2 : emp_List) {
					// mail
					String mail_address = ObjectUtils.toString(map2.get("EMP_EMAIL_ADDRESS"));
					// 無E-mail
					if(StringUtils.isBlank(mail_address))
						errorMap.add(ObjectUtils.toString(map2.get("EMP_ID")));
					// Email格式錯誤
					else if(isEmail(mail_address) == false)
						errorMap2.add(ObjectUtils.toString(map2.get("EMP_ID")));
					else {
						List<Map<String, String>> mailList = new ArrayList<Map<String,String>>();
						Map<String, String> mailMap = new HashMap<String, String>();
						mailMap.put(FubonSendJavaMail.MAIL, mail_address);
						mailList.add(mailMap);
						
						QueryConditionIF salecon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						salecon.setQueryString("select BRANCH_NBR, BRANCH_NAME from VWORG_DEFN_INFO where BRANCH_NBR = :bra_nbr ");
						salecon.setObject("bra_nbr", rvo.getREF_ORG_ID());
						List<Map<String, String>> sale_data = dam.exeQuery(salecon);
						String BRANCH_NAME = sale_data.size() > 0 ? sale_data.get(0).get("BRANCH_NAME") : "";
						
						FubonSendJavaMail sendMail = new FubonSendJavaMail();
						FubonMail mail = new FubonMail();
						Map<String, Object> annexData = new HashMap<String, Object>();
						mail.setLstMailTo(mailList);
						//設定信件主旨
						mail.setSubject("個金業務管理系統轉介客戶逾期通知");
						//設定信件內容
						String content = "<table border=\"1\" style=\"text-align:center;\">";
						content += "<tr><td>案件編號</td>";
						content += "<td>轉介日期</td>";
						content += "<td>轉介人分行</td>";
						content += "<td>轉介人員編</td>";
						content += "<td>轉介人姓名</td></tr>";
						content += "<tr><td>" + rvo.getSEQ() + "</td>";
						content += "<td>" + sdf.format(rvo.getTXN_DATE()) + "</td>";
						content += "<td>" + rvo.getREF_ORG_ID() + "-" + BRANCH_NAME + "</td>";
						content += "<td>" + rvo.getSALES_PERSON() + "</td>";
						content += "<td>" + rvo.getSALES_NAME() + "</td></tr>";
						content += "<tr><td>客戶姓名</td>";
						content += "<td>轉介商品</td>";
						content += "<td>受轉介人員編</td>";
						content += "<td>受轉介人姓名</td>";
						content += "<td>回覆狀態</td></tr>";
						content += "<tr><td>" + rvo.getCUST_NAME() + "</td>";
						content += "<td>" + ref_prod.get(rvo.getREF_PROD()) + "</td>";
						content += "<td>" + rvo.getUSERID() + "</td>";
						content += "<td>" + rvo.getUSERNAME() + "</td>";
						content += "<td>" + ref_status.get(rvo.getSTATUS()) + "</td></tr></table>";
						SimpleDateFormat sdf2 = new SimpleDateFormat("yyy/MM/dd");
						Calendar calendar2 = Calendar.getInstance();
						calendar2.setTimeInMillis(getBusiDay(rvo.getTXN_DATE()).getTime());
						calendar2.add(Calendar.YEAR, -1911);
						content += "<br>您有一筆轉介案件逾三工作日尚未確認，提醒您請至個金分行業務管理系統確認是否接受該筆轉介，若" + sdf2.format(calendar2.getTime()) + "前未進行回覆";
						content += "，視為同意接受該筆轉介。";
						mail.setContent(content);
						sendMail.sendMail(mail,annexData);
					}
				}
			} else
				logger.debug("TBCAM_LOAN_SALEREC_REVIEW無此SEQ:"+map.get("SEQ"));
		}
		
		if(errorMap.size() > 0)
			logger.debug("該人員無E-mail:"+errorMap);
		if(errorMap2.size() > 0)
			logger.debug("該人員Email格式錯誤:"+errorMap2);
	}
	
	private List<Map<String, Object>> getMAIL_LIST(String seq) throws Exception {

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("WITH SALEREC_LIST AS ( ");
		sql.append("  SELECT  A.SEQ, A.EMP_ID, REL.ROLE_ID, ");
		sql.append("          CASE  WHEN PRIVILEGEID = '012' THEN 'BRANCH_AREA_EMP' ");
		sql.append("                WHEN ROLE_ID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NOT NULL) THEN 'BRANCH_EMP' ");
		sql.append("                WHEN PRIVILEGEID = '033' THEN 'REGION_CENTER_EMP' ");
		sql.append("                WHEN PRIVILEGEID = '045' THEN 'HEAD_EMP' ");
		sql.append("          ELSE NULL END AS EMP_BOSS, ");
		sql.append("          CASE  WHEN PRIVILEGEID = '012' THEN REL.BRANCH_AREA_ID ");
		sql.append("                WHEN ROLE_ID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NOT NULL) THEN REL.BRANCH_NBR ");
		sql.append("                WHEN PRIVILEGEID = '033' THEN REL.REGION_CENTER_ID ");
		sql.append("                WHEN PRIVILEGEID = '045' THEN NULL ");
		sql.append("          ELSE NULL END AS EMP_DEPT ");
		sql.append("  FROM ( ");
		sql.append("    SELECT SEQ, USERID AS EMP_ID ");
		sql.append("    FROM TBCAM_LOAN_SALEREC_REVIEW ");
		sql.append("  ) A ");
		sql.append("  LEFT JOIN VWORG_EMP_INFO REL ON A.EMP_ID = REL.EMP_ID ");
		sql.append("  WHERE SEQ = :seq ");
		sql.append(") ");
		sql.append(", BASE_BOSS AS ( ");
		sql.append("  SELECT SALEREC.SEQ, SALEREC.EMP_ID, SALEREC.PRIVILEGEID, M.EMP_EMAIL_ADDRESS ");
		sql.append("  FROM ( ");
		sql.append("    SELECT SEQ, EMP_ID, PRIVILEGEID ");
		sql.append("    FROM ( ");
		sql.append("      SELECT SL.SEQ, SL.EMP_BOSS, SL.EMP_ID AS SALES_PERSON, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID ");
		sql.append("      FROM SALEREC_LIST SL ");
		sql.append("      LEFT JOIN VWORG_EMP_INFO REL ON REL.PRIVILEGEID = '011' AND SL.EMP_DEPT = REL.BRANCH_NBR ");
		sql.append("      WHERE SL.EMP_BOSS = 'BRANCH_EMP' ");
		sql.append("      UNION ");
		sql.append("      SELECT SL.SEQ,  SL.EMP_BOSS, SL.EMP_ID AS SALES_PERSON, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID ");
		sql.append("      FROM SALEREC_LIST SL ");
		sql.append("      LEFT JOIN VWORG_EMP_INFO REL ON REL.PRIVILEGEID = '012' AND SL.EMP_DEPT = REL.BRANCH_AREA_ID ");
		sql.append("      WHERE SL.EMP_BOSS = 'BRANCH_AREA_EMP' ");
		sql.append("      UNION ");
		sql.append("      SELECT SL.SEQ,  SL.EMP_BOSS, SL.EMP_ID AS SALES_PERSON, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID ");
		sql.append("      FROM SALEREC_LIST SL ");
		sql.append("      LEFT JOIN VWORG_EMP_INFO REL ON REL.PRIVILEGEID = '013' AND SL.EMP_DEPT = REL.REGION_CENTER_ID ");
		sql.append("      WHERE SL.EMP_BOSS = 'REGION_CENTER_EMP' ");
		sql.append("      UNION ");
		sql.append("      SELECT SL.SEQ,  SL.EMP_BOSS, SL.EMP_ID AS SALES_PERSON, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID ");
		sql.append("      FROM SALEREC_LIST SL ");
		sql.append("      LEFT JOIN VWORG_EMP_INFO REL ON REL.PRIVILEGEID = '046' ");
		sql.append("      WHERE SL.EMP_BOSS = 'HEAD_EMP' ");
		sql.append("    ) UNPIVOT (EMP_ID FOR EMP_LIST IN (SALES_PERSON, BOSS_ID)) ");
		sql.append("  ) SALEREC ");
		sql.append("  LEFT JOIN TBORG_MEMBER M ON SALEREC.EMP_ID = M.EMP_ID ");
		sql.append(") ");
		
		sql.append("SELECT SEQ, EMP_ID, PRIVILEGEID, EMP_EMAIL_ADDRESS ");
		sql.append("FROM BASE_BOSS ");
		
		queryCondition.setObject("seq", seq);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list;
	}
	
	private String getSeqNum() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT SQ_TBCRM_WKPG_MD_MAST.nextval AS SEQ FROM DUAL");
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	public static boolean isEmail(String email) {
		Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find())
			return true;
		return false;
	}
	
	public Timestamp getBusiDay(Timestamp date) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT PABTH_UTIL.FC_getBusiDay(TO_DATE(:endDate, 'yyyyMMdd'), 'TWD', 5) AS TXN_DATE FROM DUAL");
		queryCondition.setObject("endDate", new SimpleDateFormat("yyyyMMdd").format(date));
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return (Timestamp) list.get(0).get("TXN_DATE");
	}
	
}