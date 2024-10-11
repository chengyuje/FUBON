package com.systex.jbranch.app.server.fps.org220;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_AGENTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;



/**
 * MENU
 * 
 * @author Stella
 * @date 2016/07/04
 * @spec null
 * 
 * modify by ocean 2016/11/08 add getEmpLst()
 * 
 * 
 */
@Component("org220")
@Scope("request")

public class ORG220 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(ORG220.class);
	SimpleDateFormat SDFYYYYMMDDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//角色登入
	public void loginrole(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG220InputVO inputVO = (ORG220InputVO) body;
		ORG220OutputVO return_VO = new ORG220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		// 0-其他人 ，1-主管('011','012','013','006','009')   ，
		sql.append("SELECT PRIASS.PRIVILEGEID, ");
		sql.append("       PRIASS.ROLEID, ");
		sql.append("       CASE WHEN PRIASS.PRIVILEGEID IN ('011', '012', '013', '006', '009') OR (PRIASS.PRIVILEGEID <> 'UHRM001' AND ROL.JOB_TITLE_NAME IS NULL) THEN 1 ");
		sql.append("       ELSE 0 END AS COUNTS ");
		sql.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sql.append("LEFT JOIN TBORG_ROLE ROL ON PRIASS.ROLEID = ROL.ROLE_ID AND ROL.JOB_TITLE_NAME IS NOT NULL ");
		sql.append("WHERE PRIASS.ROLEID = :roleID ");
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List result = dam.exeQuery(queryCondition);
		return_VO.setResultList5(result);
		sendRtnObject(return_VO);
	}
	
	//寄信，取得被代理人主管 
	public void message(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG220InputVO inputVO = (ORG220InputVO) body;
		ORG220OutputVO return_VO = new ORG220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("WITH BASE AS ( ");
		sql.append("  SELECT INFO.REGION_CENTER_ID, INFO.BRANCH_AREA_ID, INFO.BRANCH_NBR, INFO.ROLE_ID, INFO.ROLE_NAME, PRIASS.PRIVILEGEID ");
		sql.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO, TBSYSSECUROLPRIASS PRIASS  ");
		sql.append("  WHERE INFO.ROLE_ID = PRIASS.ROLEID ");
		sql.append("  AND INFO.EMP_ID = :emp_id ");
		sql.append(") ");

		sql.append("SELECT INFO.EMP_ID, INFO.EMP_NAME ");
		sql.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO, BASE ");
		sql.append("WHERE ( ");
		sql.append("  CASE WHEN INFO.BRANCH_NBR IS NOT NULL AND INFO.BRANCH_NBR = BASE.BRANCH_NBR THEN 1 ");
		sql.append("       WHEN INFO.BRANCH_AREA_ID IS NOT NULL AND INFO.BRANCH_NBR IS NULL AND INFO.BRANCH_AREA_ID = BASE.BRANCH_AREA_ID THEN 1 ");
		sql.append("       WHEN INFO.REGION_CENTER_ID IS NOT NULL AND INFO.BRANCH_AREA_ID IS NULL AND INFO.BRANCH_NBR IS NULL AND INFO.REGION_CENTER_ID = BASE.REGION_CENTER_ID THEN 1 ");
		sql.append("  ELSE 0 END  ");
		sql.append(") = 1 ");
		sql.append("AND INFO.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID = (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_CODE = BASE.PRIVILEGEID AND PARAM_TYPE = 'ORG.AGENT_EMAIL_BOSS')) ");
 
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("emp_id", inputVO.getEmp_ao_id());
		return_VO.setMailLst(dam.exeQuery(queryCondition));

		return_VO.setMailContent(getMailContent(inputVO.getSeq_no()));
		
		sendRtnObject(return_VO);
	}
	
	public String getMailContent (BigDecimal seqNO) throws JBranchException {
		
		dam = this.getDataAccessManager();
		StringBuffer sb = new StringBuffer();
		
		TBORG_AGENTVO vo = new TBORG_AGENTVO();
		vo = (TBORG_AGENTVO) dam.findByPKey(TBORG_AGENTVO.TABLE_UID, seqNO);
		if (null != vo) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT AGENT_P.EMP_ID, MEM.EMP_NAME, AGENT_P.START_DATE, AGENT_P.END_DATE, AGENT_P.AGENT_STATUS ");
			sql.append("FROM TBORG_AGENT AGENT_P ");
			sql.append("LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = AGENT_P.EMP_ID ");
			sql.append("WHERE AGENT_P.AGENT_ID = :agentID ");
			sql.append("AND AGENT_P.AGENT_STATUS IN ('S', 'U') ");
			sql.append("AND ( ");
			sql.append("  (AGENT_P.START_DATE BETWEEN :startDate AND :endDate) ");
			sql.append("   OR (AGENT_P.END_DATE BETWEEN :startDate AND :endDate) ");
			sql.append("   OR (AGENT_P.START_DATE <= :startDate AND AGENT_P.END_DATE >= :endDate) ");
			sql.append("   OR (AGENT_P.START_DATE >= :startDate AND AGENT_P.END_DATE <= :endDate) ");
			sql.append(") ");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("agentID", vo.getEMP_ID());
			queryCondition.setObject("startDate", vo.getSTART_DATE());
			queryCondition.setObject("endDate", vo.getEND_DATE());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			if (list.size() > 0) {
				sb.append("<br />");
				sb.append("<br />");
				sb.append("被代理人(").append(vo.getEMP_ID()).append(")").append("於");
				sb.append("<table>");
				for (Map<String, Object> map : list) {
					sb.append("<tr>");
					sb.append("<td>");
					sb.append(SDFYYYYMMDDHHMMSS.format((Timestamp) map.get("START_DATE"))).append("~").append(SDFYYYYMMDDHHMMSS.format((Timestamp) map.get("END_DATE")));
					sb.append("&nbsp;代理&nbsp;").append((String) map.get("EMP_ID")).append("-").append((String) map.get("EMP_NAME"));
					sb.append("</td>");
					sb.append("</tr>");
				}
				sb.append("</table>");
			}
		}
		
		return sb.toString();
	}
	
	/** 被代理人資料: 區域中心、營運區、分行、員工編碼  **/
	//所有員工姓名
	public void allPerson(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG220InputVO inputVO = (ORG220InputVO) body;
		ORG220OutputVO return_VO = new ORG220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//== 取得權限群組
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("SELECT PRIASS.PRIVILEGEID, PRIASS.ROLEID ");
		sb.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sb.append("LEFT JOIN TBORG_ROLE ROL ON PRIASS.ROLEID = ROL.ROLE_ID AND ROL.JOB_TITLE_NAME IS NOT NULL ");
		sb.append("WHERE PRIASS.ROLEID = :roleID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		//== 取得所有員工姓名
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("WITH BASE AS (  ");
		sb.append("  SELECT DEFN.DEPT_ID, DEFN.ORG_TYPE, DEFN.PARENT_DEPT_ID ");
		sb.append("  FROM TBORG_MEMBER MEM ");
		sb.append("  LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = MEM.DEPT_ID ");
		sb.append("  WHERE MEM.EMP_ID = :emp_id ");
		sb.append(") ");
		
		sb.append("SELECT EMP_ID, EMP_NAME, EMP_ANAME, DEPT, JOB_TITLE_NAME, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
		sb.append("       BRANCH_NBR, BRANCH_NAME ");
		sb.append("FROM ( ");
		sb.append("  SELECT INFO.EMP_ID, INFO.EMP_NAME, INFO.EMP_ID||'-'||INFO.EMP_NAME AS EMP_ANAME, MEM.DEPT_ID AS DEPT, INFO.JOB_TITLE_NAME, ");
		sb.append("         INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME ");
		sb.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
		sb.append("  WHERE 1 = 1 ");
		
		// 2022-10-04 modify by ocean
		// #0001276_WMS-CR-20220908-01_人員休假角色權限控管 : 調整分行績效管理科經辦(045)新增代理人範圍，於新增代理人時，可協助全行人員進行代理人設定。
		if (!StringUtils.equals("045", (String) list.get(0).get("PRIVILEGEID"))) {
			sb.append("  AND DEPT_ID IN (SELECT DEPT_ID FROM BASE) ");
		} 
		
		switch (inputVO.getMroleid()) {
			case "1":
				switch ((String) list.get(0).get("PRIVILEGEID")) {
					case "UHRM001":
						break;
					default:
						sb.append("  UNION ");
						sb.append("  SELECT INFO.EMP_ID, INFO.EMP_NAME, INFO.EMP_ID||'-'||INFO.EMP_NAME AS EMP_ANAME, MEM.DEPT_ID AS DEPT, INFO.JOB_TITLE_NAME, ");
						sb.append("         INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME ");
						sb.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
						sb.append("  LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
						sb.append("  WHERE DEPT_ID IN (SELECT DEPT_ID FROM TBORG_DEFN WHERE PARENT_DEPT_ID IN (SELECT DEPT_ID FROM BASE)) ");
						break;
				}
				break;
			case "0":
				break;
		}
		
		sb.append(" ) ");
		sb.append("WHERE EMP_ID IN (SELECT DISTINCT EMP_ID FROM TBORG_MEMBER_ROLE) ");
		sb.append("ORDER BY EMP_ID ");

		queryCondition.setObject("emp_id",ws.getUser().getUserID());
	
		queryCondition.setQueryString(sb.toString());

		return_VO.setAllPerson(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
	
	/** 查詢  **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		ORG220InputVO inputVO = (ORG220InputVO) body;
		ORG220OutputVO return_VO1 = new ORG220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRIASS.PRIVILEGEID, ROL.JOB_TITLE_NAME ");
		sql.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sql.append("LEFT JOIN TBORG_ROLE ROL ON PRIASS.ROLEID = ROL.ROLE_ID AND ROL.JOB_TITLE_NAME IS NOT NULL ");
		sql.append("WHERE PRIASS.ROLEID = :roleID "); 
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List<Map<String, Object>> privilege = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT AGT.SEQ_NO, ");
		sql.append("       (AGT.EMP_ID || '-' || EDN.EMP_NAME) AS EMP_NAME, ");
		sql.append("       (AGT.AGENT_ID || '-' || EDI.EMP_NAME) AS AGENT_NAME, ");
		sql.append("       (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.AGENT_STATUS' AND PARAM_CODE = AGT.AGENT_STATUS) AS AGENT_STATUS_NAME, ");
		sql.append("       AGT.AGENT_STATUS, ");
		sql.append("       AGT.START_DATE, ");
		sql.append("       AGT.END_DATE, ");
		sql.append("       (SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = AGT.DEPT_ID) AS DEPT_NAME, ");
		sql.append("       (SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = AGT.AGENT_DEPT_ID) AS AGENT_DEPT_NAME, ");
		sql.append("       AGT.DEPT_ID, ");
		sql.append("       AGT.AGENT_DEPT_ID, ");
		sql.append("       AGT.AGENT_DESC, ");
		sql.append("       TO_CHAR(AGT.START_DATE, 'hh24') AS STIME, ");
		sql.append("       TO_CHAR(AGT.END_DATE, 'hh24') AS ETIME, ");
		sql.append("       EDI.REGION_CENTER_ID, ");
		sql.append("       EDI.BRANCH_AREA_ID, ");
		sql.append("       EDI.BRANCH_NBR, ");
		sql.append("       AGT.AGENT_ID AS AGENT_ID, ");
		sql.append("       AGT.EMP_ID AS EMP_ID, ");
		sql.append("       AGT.MODIFIER, ");
		sql.append("       AGT.LASTUPDATE, ");
		sql.append("       EDN.JOB_TITLE_NAME, ");
		sql.append("       AGT.AUTO_END_REASON ");
		sql.append("FROM TBORG_AGENT AGT ");   
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO EDI ON AGT.AGENT_ID = EDI.EMP_ID ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO EDN ON AGT.EMP_ID = EDN.EMP_ID ");
		
		sql.append("WHERE 1=1 ");
		
		// 為高端組織下，但非uhrm人員
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
			!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
			sql.append("AND (EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE AGT.AGENT_ID = U.EMP_ID) OR EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE AGT.EMP_ID = U.EMP_ID)) ");
		}
		//
		
		if (StringUtils.isBlank(inputVO.getEmp_id())) {
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") < 0 ||
				StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
				
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getBranch_area_id())) {
					sql.append("AND EDI.REGION_CENTER_ID = :rc_id ");
					queryCondition.setObject("rc_id", inputVO.getRegion_center_id());
				} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
					sql.append("AND (EDI.REGION_CENTER_ID IN (:rcIdList) OR EDI.REGION_CENTER_ID IS NULL) ");
					queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
				
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getRegion_center_id())) {
					sql.append("AND EDI.BRANCH_AREA_ID = :op_id ");
					queryCondition.setObject("op_id", inputVO.getBranch_area_id());
				} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
					sql.append("AND (EDI.BRANCH_AREA_ID IN (:opIdList) OR EDI.BRANCH_AREA_ID IS NULL) ");
					queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
				
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
					sql.append("AND EDI.BRANCH_NBR = :br_id ");
					queryCondition.setObject("br_id", inputVO.getBranch_nbr());
				} else if (privilege.size() > 0 && StringUtils.isBlank((String) privilege.get(0).get("JOB_TITLE_NAME"))) {
					sql.append("AND (EDI.BRANCH_NBR IN (:brIdList) OR EDI.BRANCH_NBR IS NULL) ");
					queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
			}
		} 
		
		if (StringUtils.isNotBlank(inputVO.getuEmpID())) {
			sql.append("AND (AGT.EMP_ID = :uEmpID OR AGT.AGENT_ID = :uEmpID) ");
			queryCondition.setObject("uEmpID", inputVO.getuEmpID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sql.append("AND (AGT.EMP_ID = :aocode OR AGT.AGENT_ID = :aocode) ");
			queryCondition.setObject("aocode", inputVO.getEmp_id());
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id_txt())) {
			if(inputVO.getMroleid() == null){
				sql.append("AND EDI.EMP_ID = :emp_id AND DEPT_ID = (SELECT DEPT_ID FROM TBORG_MEMBER WHERE EMP_ID = :emp_id) ");
			} else {
				sql.append("AND EDI.EMP_ID = :emp_id ");
			}
			queryCondition.setObject("emp_id", inputVO.getEmp_id_txt());
		}
	
		if(inputVO.getsCreDate() != null && inputVO.getsTime().length() == 0){
			sql.append("AND TO_CHAR(AGT.START_DATE, 'yyyyMMdd') >= TO_CHAR(TO_DATE(:startDate, 'yyyyMMdd'), 'yyyyMMdd') ");
			queryCondition.setObject("startDate", sdf.format(inputVO.getsCreDate()));
		}
		if(inputVO.geteCreDate() != null && inputVO.geteTime().length() == 0 ){
			sql.append("AND TO_CHAR(AGT.END_DATE, 'yyyyMMdd') <= TO_CHAR(TO_DATE(:endDate, 'yyyyMMdd'), 'yyyyMMdd') ");
			queryCondition.setObject("endDate", sdf.format(inputVO.geteCreDate()));
		}
		
		if (inputVO.getsCreDate() != null && inputVO.getsTime().length()!=0) {
			sql.append("AND AGT.START_DATE >= TO_DATE(:start||:startTime,'YYYYMMDDhh24') ");
			queryCondition.setObject("start", sdf.format(inputVO.getsCreDate()));
			queryCondition.setObject("startTime", inputVO.getsTime());
		}
		if (inputVO.geteCreDate() != null && inputVO.geteTime().length()!=0) {
			sql.append("AND AGT.END_DATE <= TO_DATE(:end||:endTime,'YYYYMMDDhh24') ");
			queryCondition.setObject("end", sdf.format(inputVO.geteCreDate()));
			queryCondition.setObject("endTime", inputVO.geteTime());
		}
		sql.append("ORDER BY CASE  AGT.AGENT_STATUS WHEN 'W' THEN 0 WHEN 'U' THEN 1 WHEN 'S' THEN 2  ELSE 9 END, AGT.START_DATE DESC  ");
		queryCondition.setQueryString(sql.toString());

		return_VO1.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO1);
	}
	
	/** 限定代理人 **/
	public void limagent(Object body, IPrimitiveMap header) throws JBranchException {
			
		ORG220InputVO inputVO = (ORG220InputVO) body;
		ORG220OutputVO outputVO = new ORG220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT BASE.AGENT_ID, (INFO.EMP_ID||'-'|| INFO.EMP_NAME) AS EMP_NAME, INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME ");
		sql.append("FROM ( ");
		sql.append("SELECT EMP_ID, REPLACE(ORDER_AGENT, 'AGENT_ID_') AS ORDER_AGENT, AGENT_ID ");
		sql.append("FROM TBORG_AGENT_CONSTRAIN ");
		sql.append("UNPIVOT (AGENT_ID FOR ORDER_AGENT IN (AGENT_ID_1, AGENT_ID_2, AGENT_ID_3)) ");
		sql.append("WHERE EMP_ID = :emp_id ");
		sql.append("ORDER BY ORDER_AGENT) BASE, VWORG_BRANCH_EMP_DETAIL_INFO INFO ");
		sql.append("WHERE BASE.AGENT_ID = INFO.EMP_ID ");
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("emp_id", inputVO.getEmp_ao_id());
		List<Map<String, Object>> result = dam.exeQuery(queryCondition);
		outputVO.setResultList4(result);
			
		//無限定代理人	
		if(result.size() == 0){
			// 依系統角色決定下拉選單可視範圍
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
			Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);
			Map<String, String> mbrmgrMap  = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ROL.ROLE_ID, REPLACE(ROL.SYS_ROLE, '_ROLE', '') AS LOGIN_ROLE ");
			sb.append("FROM TBORG_ROLE ROL ");
			sb.append("WHERE ROL.ROLE_ID IN (SELECT C.ROLE_ID FROM TBORG_ROLE C WHERE INSTR((SELECT AGENT_ROLE FROM TBORG_ROLE WHERE ROLE_ID = (SELECT ROLE_ID FROM TBORG_MEMBER_ROLE WHERE EMP_ID = :empID AND ROLE_ID = :roleID GROUP BY ROLE_ID)), C.ROLE_ID) > 0) ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("empID", inputVO.getEmp_ao_id());
			queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));

			List<Map<String, Object>> availKeyList = dam.exeQuery(queryCondition);
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT (A.EMP_ID || '-' || A.EMP_NAME) AS EMP_NAME, A.EMP_ID AS AGENT_ID, A.REGION_CENTER_ID, A.REGION_CENTER_NAME, A.BRANCH_AREA_ID, A.BRANCH_AREA_NAME, A.BRANCH_NBR, A.BRANCH_NAME ");
			sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO A ");
			sb.append("LEFT JOIN TBORG_ROLE B ON A.ROLE_ID = B.ROLE_ID ");
			sb.append("WHERE 1 = 1 "); 
			sb.append("AND B.ROLE_ID IN (SELECT C.ROLE_ID FROM TBORG_ROLE C WHERE INSTR((SELECT AGENT_ROLE FROM TBORG_ROLE WHERE ROLE_ID = (SELECT ROLE_ID FROM TBORG_MEMBER_ROLE WHERE EMP_ID = :emp_id AND ROLE_ID = :roleID GROUP BY ROLE_ID)), C.ROLE_ID) > 0) ");
			sb.append("AND A.EMP_ID <> :emp_id ");

			for (int i = 0; i < availKeyList.size(); i++) {
				if (i == 0) {
					sb.append("AND ( ");
				} else {
					sb.append("OR ");
				}
				
				if (headmgrMap.containsKey((String) availKeyList.get(i).get("ROLE_ID"))) {
					sb.append("    (A.REGION_CENTER_ID IN (:rcIdList").append(i).append(") OR A.REGION_CENTER_ID IS NULL) ");
					sb.append("AND (A.BRANCH_AREA_ID IN (:opIdList").append(i).append(") OR A.BRANCH_AREA_ID IS NULL) ");
					sb.append("AND (A.BRANCH_NBR IN (:brIdList").append(i).append(") OR A.BRANCH_NBR IS NULL) ");
					sb.append("AND A.ROLE_ID = :agentRoleID").append(i).append(" ");
					queryCondition.setObject("rcIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("opIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
					queryCondition.setObject("brIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
					queryCondition.setObject("agentRoleID" + i, (String) availKeyList.get(i).get("ROLE_ID"));
				} else if (armgrMap.containsKey((String) availKeyList.get(i).get("ROLE_ID"))) {
					sb.append("    A.REGION_CENTER_ID IN (:rcIdList").append(i).append(") ");
					sb.append("AND A.ROLE_ID = :agentRoleID").append(i).append(" ");
					queryCondition.setObject("rcIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("agentRoleID" + i, (String) availKeyList.get(i).get("ROLE_ID"));
				} else if (mbrmgrMap.containsKey((String) availKeyList.get(i).get("ROLE_ID"))) {
					sb.append("    A.REGION_CENTER_ID IN (:rcIdList").append(i).append(") ");
					sb.append("AND A.BRANCH_AREA_ID IN (:opIdList").append(i).append(") ");
					sb.append("AND A.ROLE_ID = :agentRoleID").append(i).append(" ");
					queryCondition.setObject("rcIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("opIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
					queryCondition.setObject("agentRoleID" + i, (String) availKeyList.get(i).get("ROLE_ID"));
				} else {
					sb.append("    A.REGION_CENTER_ID IN (:rcIdList").append(i).append(")  ");
					sb.append("AND A.BRANCH_AREA_ID IN (:opIdList").append(i).append(") ");
					sb.append("AND A.BRANCH_NBR IN (:brIdList").append(i).append(") ");
					sb.append("AND A.ROLE_ID = :agentRoleID").append(i).append(" ");
					queryCondition.setObject("rcIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("opIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
					queryCondition.setObject("brIdList" + i, getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
					queryCondition.setObject("agentRoleID" + i, (String) availKeyList.get(i).get("ROLE_ID"));
				}

				if ((i + 1) == availKeyList.size()) {
					sb.append(") ");
				}
			}
			sb.append("ORDER BY A.EMP_ID ");
			
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("emp_id", inputVO.getEmp_ao_id());
			queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
			
			outputVO.setResultList4(dam.exeQuery(queryCondition));
		}
		
		sendRtnObject(outputVO);
	}

	/**新增代理人
	 * @throws ParseException **/
	public void edit(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG220InputVO inputVO3 = (ORG220InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//date+time
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		long aa = Long.parseLong(inputVO3.getsTime());
		long bb = Long.parseLong(inputVO3.geteTime());
		
		Date date1 = new Timestamp(inputVO3.getsCreDate().getTime() + aa);
		Date date2 = new Timestamp(inputVO3.geteCreDate().getTime() + bb);
		
		sql.append("SELECT EMP_ID, START_DATE, END_DATE, AGENT_ID ");
		sql.append("FROM TBORG_AGENT ");
		sql.append("WHERE EMP_ID = :empID ");
		sql.append("AND START_DATE = :date1 ");
		sql.append("AND END_DATE = :date2 ");
		sql.append("AND AGENT_ID = :agentID ");
		sql.append("AND AGENT_STATUS IN ('S', 'U', 'W') ");
		queryCondition.setObject("empID", inputVO3.getEmp_ao_id());
		queryCondition.setObject("date1", (Timestamp) date1);
		queryCondition.setObject("date2", (Timestamp) date2);
		queryCondition.setObject("agentID", inputVO3.getEmp_id());
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String,Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() == 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT A.DEPT_ID, A.DEPT_NAME, B.AGENT_DEPT_ID, B.AGENT_DEPT_NAME ");
			sql.append("FROM (");
			sql.append("  SELECT MEM.DEPT_ID AS DEPT_ID, DEFN.DEPT_NAME AS DEPT_NAME ");
			sql.append("  FROM TBORG_MEMBER MEM ");
			sql.append("  LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = MEM.DEPT_ID ");
			sql.append("  WHERE MEM.EMP_ID = '").append(inputVO3.getEmp_ao_id()).append("' ");
			sql.append("  UNION ");
			sql.append("  SELECT MEM.DEPT_ID AS DEPT_ID, DEFN.DEPT_NAME AS DEPT_NAME ");
			sql.append("  FROM TBORG_MEMBER_PLURALISM MEM ");
			sql.append("  LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = MEM.DEPT_ID ");
			sql.append("  WHERE (TRUNC(MEM.TERDTE) >= TRUNC(SYSDATE) OR MEM.TERDTE IS NULL) ");
			sql.append("  AND MEM.ACTION <> 'D' ");
			sql.append("  AND MEM.EMP_ID = '").append(inputVO3.getEmp_ao_id()).append("' ");
			sql.append(") A, (");
			sql.append("  SELECT MEM.DEPT_ID AS AGENT_DEPT_ID, DEFN.DEPT_NAME AS AGENT_DEPT_NAME  ");
			sql.append("  FROM TBORG_MEMBER MEM ");
			sql.append("  LEFT JOIN TBORG_DEFN DEFN ON DEFN.DEPT_ID = MEM.DEPT_ID ");
			sql.append("  WHERE MEM.EMP_ID = '").append(inputVO3.getEmp_id()).append("' ");
			sql.append(") B ");
			
			queryCondition.setQueryString(sql.toString());
			List<Map<String,Object>> member = dam.exeQuery(queryCondition);
			
			for (Map<String, Object> map : member) {
				TBORG_AGENTVO vo = new TBORG_AGENTVO();
				BigDecimal seqno = new BigDecimal(getSEQ()); //產生序號
				inputVO3.setSeq_no(seqno);
				
				// 新增欄位
				vo.setSEQ_NO(inputVO3.getSeq_no());
				vo.setEMP_ID(inputVO3.getEmp_ao_id());
				vo.setDEPT_ID((String) map.get("DEPT_ID"));
				vo.setSTART_DATE((Timestamp) date1);
				vo.setEND_DATE((Timestamp) date2);
				vo.setAGENT_ID(inputVO3.getEmp_id());
				vo.setAGENT_DEPT_ID((String) map.get("AGENT_DEPT_ID"));
				vo.setAGENT_STATUS("W");
				vo.setAGENT_DESC(inputVO3.getAgent_desc());
				vo.setCreator(inputVO3.getEmp_ao_id());
				vo.setCreatetime(currentTM);
				vo.setModifier(inputVO3.getEmp_ao_id());
				vo.setLastupdate(currentTM);
				
				dam.create(vo);
			}
		} else {
			throw new APException("已重覆申請(同代理人/同時段)");
		}

		this.sendRtnObject(null);
	}
	
	public void mailMsg(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG220InputVO inputVO = (ORG220InputVO) body;
		ORG220OutputVO return_VO = new ORG220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT AGT.SEQ_NO, (AGT.EMP_ID||'-'||EDN.EMP_NAME) AS EMP_NAME , (AGT.AGENT_ID||'-'||EDI.EMP_NAME) AS AGENT_NAME, ");
		sql.append("(SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.AGENT_STATUS' AND PARAM_CODE = AGT.AGENT_STATUS) AS AGENT_STATUS_NAME, ");
		sql.append(" AGT.AGENT_STATUS, AGT.START_DATE, AGT.END_DATE,  ");
		sql.append("(SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = AGT.DEPT_ID) AS DEPT_NAME, ");
		sql.append("(SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = AGT.AGENT_DEPT_ID) AS AGENT_DEPT_NAME, ");
		sql.append("AGT.DEPT_ID , AGT.AGENT_DEPT_ID, ");
		sql.append("AGT.AGENT_DESC, TO_CHAR(AGT.START_DATE,'hh24') AS STIME, TO_CHAR( AGT.END_DATE,'hh24') AS ETIME,");
		sql.append("EDI.REGION_CENTER_ID, EDI.BRANCH_AREA_ID, ");
		sql.append("EDI.BRANCH_NBR, AGT.AGENT_ID AS AGENT_ID, AGT.EMP_ID AS EMP_ID, AGT.MODIFIER, AGT.LASTUPDATE,EDN.JOB_TITLE_NAME ");
		sql.append("FROM TBORG_AGENT AGT ");   
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO EDI ON AGT.AGENT_ID = EDI.EMP_ID ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO EDN ON AGT.EMP_ID = EDN.EMP_ID ");
		sql.append("WHERE 1=1 ");
		sql.append("AND AGT.EMP_ID = '").append(inputVO.getEmp_ao_id()).append("' ");
		sql.append("AND AGT.AGENT_ID = '").append(inputVO.getEmp_id()).append("' ");
		sql.append("AND AGT.AGENT_STATUS = 'W' ");
		sql.append("AND TO_CHAR(AGT.CREATETIME,'YYYYMMDD') = TO_CHAR(SYSDATE,'YYYYMMDD') ");

		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setMailMsg(list);
		if (list.size() > 0) {
			return_VO.setMailContent(getMailContent((BigDecimal) list.get(0).get("SEQ_NO")));
		}
		
		sendRtnObject(return_VO);
	}
	
	/** 預計代理修改狀態為修改代理 **/
	public void update (Object body, IPrimitiveMap header) throws JBranchException, ParseException { 
		
		dam = this.getDataAccessManager();
		ORG220InputVO inputVO = (ORG220InputVO) body;

		TBORG_AGENTVO vo = new TBORG_AGENTVO();
		vo = (TBORG_AGENTVO) dam.findByPKey(TBORG_AGENTVO.TABLE_UID, inputVO.getSeq_no());
		
		System.out.println(vo);
		if (null != vo) {	
			if ("ORG".equals(inputVO.getRPT_TYPE())) {
				vo.setAGENT_ID(inputVO.getEmp_id());
				vo.setAGENT_DEPT_ID(inputVO.getDept_id());
			}
			
			if ("EMP".equals(inputVO.getRPT_TYPE())) {
				vo.setAGENT_ID(inputVO.getEmp_id_txt());
				vo.setAGENT_DEPT_ID(inputVO.getAgent_dept_id());
			}
			
			vo.setSTART_DATE(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd").format(inputVO.getsCreDate()) + " " + inputVO.getsTime() + ":00:00").getTime()));
			vo.setEND_DATE(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd").format(inputVO.geteCreDate()) + " " + inputVO.geteTime() + ":00:00").getTime()));
			vo.setAGENT_STATUS("W");
			vo.setAGENT_DESC(inputVO.getAgent_desc());
			vo.setModifier(inputVO.getEmp_ao_id());
			vo.setLastupdate(new Timestamp(System.currentTimeMillis()));
			
			dam.update(vo);
		}
		
		this.sendRtnObject(null);
	}
	
	/** 預計代理、代理中修改狀態為取消代理 **/
	public void updateAct (Object body, IPrimitiveMap header) throws JBranchException { 
		ORG220InputVO inputVO = (ORG220InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_AGENTVO vo = new TBORG_AGENTVO();
		vo =(TBORG_AGENTVO) dam.findByPKey(TBORG_AGENTVO.TABLE_UID, inputVO.getSeq_no());
		if(vo != null){	
			/***若代理人確認同意，則為預計代理人***/
			
			vo.setAGENT_STATUS((StringUtils.equals("Y", inputVO.getAct_type()) ? "U" : (StringUtils.equals("N", inputVO.getAct_type()) ? "C" : inputVO.getAct_type())));

			dam.update(vo);
		}
	
		sendRtnObject(null);
	}
	
	//檢核日期為工作日
	public void rewdate(Object body, IPrimitiveMap header) throws JBranchException {
		ORG220InputVO inputVO3 = (ORG220InputVO) body;
		ORG220OutputVO vo = new ORG220OutputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		List<Map<String, String>> inputLst = new ArrayList<Map<String,String>>();
		HashMap<String, String> dataMap = new HashMap<String, String>();
		if(inputVO3.getsCreDate()!=null && !inputVO3.getsCreDate().equals("")){
			String screDate = sdf.format(inputVO3.getsCreDate());
			dataMap.put("SCREDATE", validateHoliday(screDate));
			inputLst.add(dataMap);
			vo.setReviewdate(inputLst); 
		}
		if(inputVO3.geteCreDate()!=null && !inputVO3.geteCreDate().equals("")){
			String ecreDate = sdf.format(inputVO3.geteCreDate());
			dataMap.put("ECREDATE", validateHoliday(ecreDate));
			inputLst.add(dataMap);
			vo.setReviewdate(inputLst); 
		}
		
		
		this.sendRtnObject(vo);
	}

	/** 產生seq No */
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBORG_AGENT.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	
	/** 工作日 **/
	private String validateHoliday(String date) throws JBranchException{
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT PABTH_UTIL.FC_IsHoliday(to_date(:endDate,'YYYYMMDD'), 'TWD')AS REWDATE FROM DUAL ");
		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("endDate", date);
		List<Map> list =null;
		list = dam.exeQuery(queryCondition);
		date = (String)list.get(0).get("REWDATE").toString();
		
		return  date;
	}
	
	public void getEmpList (Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);

		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG220InputVO inputVO = (ORG220InputVO)body;
		ORG220OutputVO outputVO = new ORG220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIASS.PRIVILEGEID, PRIASS.ROLEID, ");
		sb.append("       CASE WHEN PRIASS.PRIVILEGEID IN (SELECT DISTINCT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE IN ('FUBONSYS.BMMGR_ROLE', 'FUBONSYS.MBRMGR_ROLE', 'FUBONSYS.ARMGR_ROLE'))) THEN 'BOSS' ");
		sb.append("            WHEN ROL.JOB_TITLE_NAME IS NULL THEN 'ALL' ");
		sb.append("       ELSE 'MEMBER' END AS MEM_TYPE ");
		sb.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sb.append("LEFT JOIN TBORG_ROLE ROL ON PRIASS.ROLEID = ROL.ROLE_ID AND ROL.JOB_TITLE_NAME IS NOT NULL ");
		sb.append("WHERE PRIASS.ROLEID = :roleID ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		if (uhrmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			uhrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sb.append("WITH BASE AS ( ");
			sb.append("  SELECT DISTINCT UHRM_C.UEMP_ID AS EMP_ID, ");
			sb.append("         M.EMP_NAME AS LABEL, ");
			sb.append("         UHRM_C.UEMP_ID AS DATA, ");
			sb.append("         AO.AO_CODE, ");
			sb.append("         MR.ROLE_ID, ");
			sb.append("         CM.BRA_NBR AS CUST_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME ");
			sb.append("  FROM TBORG_CUST_UHRM_PLIST UHRM_C ");
			sb.append("  LEFT JOIN TBORG_MEMBER M ON UHRM_C.UEMP_ID = M.EMP_ID ");
			sb.append("  LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID ");
			sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON UHRM_C.UEMP_ID = AO.EMP_ID AND TYPE = '5' ");
			sb.append("  LEFT JOIN TBCRM_CUST_MAST CM ON UHRM_C.CUST_ID = CM.CUST_ID ");
			sb.append("  LEFT JOIN ( ");
			sb.append("      SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
			sb.append("      FROM TBORG_DEFN ");
			sb.append("      START WITH DEPT_ID IS NOT NULL ");
			sb.append("      CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
			sb.append("  ) D ON CM.BRA_NBR = D.CHILD_DEPT_ID ");
			sb.append("  WHERE CM.BRA_NBR IS NOT NULL ");
			sb.append("  AND EXISTS (SELECT T.ROLEID FROM TBSYSSECUROLPRIASS T WHERE T.PRIVILEGEID IN ('UHRM002') AND MR.ROLE_ID = T.ROLEID) ");
			sb.append(") ");
			sb.append("SELECT DISTINCT C.EMP_ID, ");
			sb.append("       C.LABEL, ");
			sb.append("       C.DATA, ");
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
				sb.append("       C.DEPT_ID_30 AS RECENT_CENTER_ID, ");
				sb.append("       C.DEPT_ID_40 AS BRANCH_AREA_ID, ");
				sb.append("       C.DEPT_ID_50 AS BRANCH_NBR, ");
			} 

			sb.append("       C.AO_CODE, ");
			sb.append("       C.ROLE_ID ");
			sb.append("FROM ( ");
			sb.append("  SELECT EMP_ID, LABEL, DATA, AO_CODE, ROLE_ID, DEPT_ID_00, DEPT_ID_05, DEPT_ID_10, DEPT_ID_20, DEPT_ID_30, DEPT_ID_40, DEPT_ID_50, CUST_DEPT_ID ");
			sb.append("  FROM ( ");
			sb.append("    SELECT EMP_ID, LABEL, DATA, AO_CODE, ROLE_ID, ORG_TYPE, DEPT_ID, CUST_DEPT_ID ");
			sb.append("    FROM BASE ");
			sb.append("  ) PIVOT (MAX(DEPT_ID) FOR ORG_TYPE IN ('00' AS DEPT_ID_00, '05' AS DEPT_ID_05, '10' AS DEPT_ID_10, '20' AS DEPT_ID_20, '30' AS DEPT_ID_30, '40' AS DEPT_ID_40, '50' AS DEPT_ID_50)) ");
			sb.append(") C ");
			sb.append("LEFT JOIN ( ");
			sb.append("  SELECT EMP_ID, LABEL, DATA, AO_CODE, ROLE_ID, DEPT_NAME_00, DEPT_NAME_05, DEPT_NAME_10, DEPT_NAME_20, DEPT_NAME_30, DEPT_NAME_40, DEPT_NAME_50, CUST_DEPT_ID ");
			sb.append("  FROM ( ");
			sb.append("    SELECT EMP_ID, LABEL, DATA, AO_CODE, ROLE_ID, ORG_TYPE, DEPT_NAME, CUST_DEPT_ID ");
			sb.append("    FROM BASE ");
			sb.append("  ) PIVOT (MAX(DEPT_NAME) FOR ORG_TYPE IN ('00' AS DEPT_NAME_00, '05' AS DEPT_NAME_05, '10' AS DEPT_NAME_10, '20' AS DEPT_NAME_20, '30' AS DEPT_NAME_30, '40' AS DEPT_NAME_40, '50' AS DEPT_NAME_50)) "); 
			sb.append(") D ON C.EMP_ID = D.EMP_ID AND NVL(C.CUST_DEPT_ID, ' ') = NVL(D.CUST_DEPT_ID, ' ') AND NVL(C.AO_CODE, ' ') = NVL(D.AO_CODE, ' ') AND C.ROLE_ID = D.ROLE_ID ");
		
			sb.append("WHERE 1 = 1");
			
			if (uhrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("AND C.EMP_ID = :empID ");
	
				queryCondition.setObject("empID", ws.getUser().getUserID());
			}
			
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
				sb.append(" AND C.DEPT_ID_50 = :brNbr ");
				queryCondition.setObject("brNbr", inputVO.getBranch_nbr());
			} 
		} else {
			// 20210224 ADD BY OCEAN : #0524: WMS-CR-20210208-01_新增銀證督導主管角色功能
			sb.append("SELECT EMP_ID, ");
			sb.append("       EMP_NAME AS LABEL, ");
			sb.append("       EMP_ID   AS DATA, ");
			sb.append("       REGION_CENTER_ID, ");
			sb.append("       BRANCH_AREA_ID, ");
			sb.append("       BRANCH_NBR, ");
			sb.append("       ROLE_ID, ");
			
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("bs") >= 0) {
				sb.append("       BS_CODE ");
				sb.append("FROM VWORG_EMP_BS_INFO INFO1 ");
			} else {
				sb.append("       AO_CODE ");
				sb.append("FROM VWORG_EMP_INFO INFO1 ");
			}
			
			sb.append("WHERE 1 = 1 ");

			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
				sb.append(" AND INFO1.REGION_CENTER_ID = :rcId ");
				queryCondition.setObject("rcId", inputVO.getRegion_center_id());
			} else if (list.size() > 0 && StringUtils.equals("ALL", (String) list.get(0).get("MEM_TYPE"))) {
				sb.append(" AND (INFO1.REGION_CENTER_ID IN (:rcIdList) OR INFO1.REGION_CENTER_ID IS NULL) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
				sb.append(" AND INFO1.BRANCH_AREA_ID = :opId ");
				queryCondition.setObject("opId", inputVO.getBranch_area_id());
			} else if (list.size() > 0 && StringUtils.equals("ALL", (String) list.get(0).get("MEM_TYPE"))) {
				sb.append(" AND (INFO1.BRANCH_AREA_ID IN (:opIdList) OR INFO1.BRANCH_AREA_ID IS NULL) ");
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && !"null".equals(inputVO.getBranch_nbr())) {
				sb.append(" AND INFO1.BRANCH_NBR = :brNbr ");
				queryCondition.setObject("brNbr", inputVO.getBranch_nbr());
			} else if (list.size() > 0 && StringUtils.equals("ALL", (String) list.get(0).get("MEM_TYPE"))) {
				sb.append(" AND (INFO1.BRANCH_NBR IN (:brNbrList) OR INFO1.BRANCH_NBR IS NULL) ");
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			
			if (list.size() > 0 && StringUtils.equals("MEMBER", (String) list.get(0).get("MEM_TYPE"))) {
				sb.append("AND INFO1.EMP_ID = :empID ");
				queryCondition.setObject("empID", ws.getUser().getUserID());
			}
			
			sb.append("AND (INFO1.CODE_TYPE IS NULL OR INFO1.CODE_TYPE = '1') ");
		}

		sb.append("ORDER BY EMP_ID ");

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setEmpList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
}
