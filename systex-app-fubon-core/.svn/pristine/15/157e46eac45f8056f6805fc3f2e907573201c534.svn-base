package com.systex.jbranch.platform.server.info.fubonuser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class WmsUser extends User{
	
	/**
	 * 登入時更新代理人資訊
	 * @param
	 * @return
	 * @throws JBranchException
	 */
	public void updateAgentInfo() throws JBranchException{
		
		DataAccessManager dam = new DataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		/* 代理狀態
		 	A：自動終止
			C：取消代理
			E：結束代理
			N：無代理人
			S：代理中
			U：預計代理
			W：待代理人確認
			X：自動取消
		 */
		// 若代理狀態為"S：代理中/U：預計代理/W：待代理人確認" => 代理人/被代理人無角色，停止代理並更新『代理狀態』欄位為"A：自動終止" / 『自動終止原因』欄位為"代理人/被代理人無角色"
		sb.setLength(0);
		sb.append("UPDATE TBORG_AGENT ");
		sb.append("SET AGENT_STATUS = 'A', ");
		sb.append("    AUTO_END_DATE = SYSDATE, ");
		sb.append("    AUTO_END_REASON = '代理人/被代理人無角色', ");
		sb.append("    MODIFIER = 'SYSTEM', LASTUPDATE = SYSDATE ");
		sb.append("WHERE (AGENT_ID, EMP_ID, AGENT_STATUS) IN ( ");
		sb.append("  SELECT AGE.AGENT_ID, AGE.EMP_ID, AGE.AGENT_STATUS ");
		sb.append("  FROM TBORG_AGENT AGE ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON AGE.AGENT_ID = MEM.EMP_ID ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND NOT EXISTS (SELECT 1 FROM TBORG_MEMBER_ROLE R WHERE AGE.EMP_ID = R.EMP_ID) ");
		sb.append("  AND AGENT_STATUS IN ('S', 'U', 'W') ");
		sb.append("  UNION ");
		sb.append("  SELECT AGE.AGENT_ID, AGE.EMP_ID, AGE.AGENT_STATUS ");
		sb.append("  FROM TBORG_AGENT AGE ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON AGE.AGENT_ID = MEM.EMP_ID ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND NOT EXISTS (SELECT 1 FROM TBORG_MEMBER_ROLE R WHERE AGE.AGENT_ID = R.EMP_ID) ");
		sb.append("  AND AGENT_STATUS IN ('S', 'U', 'W') ");
		sb.append(") ");
		
		cond.setQueryString(sb.toString());
		dam.exeUpdate(cond);
		
		// 若代理狀態為"S：代理中" => 已過代理時間，停止代理並更新『代理狀態』欄位為"A：自動終止" / 『自動終止原因』欄位為"代理期限已過期"
		sb.setLength(0);
		sb.append("UPDATE TBORG_AGENT ");
		sb.append("SET AGENT_STATUS = 'A', ");
		sb.append("    AUTO_END_DATE = SYSDATE, ");
		sb.append("    AUTO_END_REASON = '代理期限已過期', ");
		sb.append("    MODIFIER = 'SYSTEM', LASTUPDATE = SYSDATE ");
		sb.append("WHERE END_DATE <= SYSDATE AND AGENT_STATUS = 'S' ");
		
		cond.setQueryString(sb.toString());
		dam.exeUpdate(cond);
		
		// 若代理狀態為"U：預計代理" => 代理人或被代理人離職，停止代理並更新『代理狀態』欄位為"A：自動終止" / 『自動終止原因』欄位為"代理人或被代理人離職"
		// 代理人離職
		sb.setLength(0);
		sb.append("UPDATE ( ");
		sb.append("  SELECT AGE.AGENT_ID, AGE.EMP_ID, AGE.START_DATE, AGE.END_DATE, AGE.AGENT_STATUS, ");
		sb.append("         AGE.AUTO_END_DATE, AGE.AUTO_END_REASON, AGE.MODIFIER, AGE.LASTUPDATE ");
		sb.append("  FROM TBORG_AGENT AGE ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON AGE.AGENT_ID = MEM.EMP_ID ");
		sb.append("  WHERE MEM.JOB_RESIGN_DATE < SYSDATE ");
		sb.append(") ");
		sb.append("SET AGENT_STATUS = 'A', ");
		sb.append("    AUTO_END_DATE = SYSDATE, ");
		sb.append("    AUTO_END_REASON = '代理人或被代理人離職', ");
		sb.append("    MODIFIER = 'SYSTEM', LASTUPDATE = SYSDATE ");
		sb.append("WHERE AGENT_STATUS = 'U' ");
		
		cond.setQueryString(sb.toString());
		dam.exeUpdate(cond);
		
		// 被代理人離職
		sb.setLength(0);
		sb.append("UPDATE ( ");
		sb.append("  SELECT AGE.AGENT_ID, AGE.EMP_ID, AGE.START_DATE, AGE.END_DATE, AGE.AGENT_STATUS, ");
		sb.append("         AGE.AUTO_END_DATE, AGE.AUTO_END_REASON, AGE.MODIFIER, AGE.LASTUPDATE ");
		sb.append("  FROM TBORG_AGENT AGE ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON AGE.EMP_ID = MEM.EMP_ID ");
		sb.append("  WHERE MEM.JOB_RESIGN_DATE < SYSDATE ");
		sb.append(") ");
		sb.append("SET AGENT_STATUS = 'A', ");
		sb.append("    AUTO_END_DATE = SYSDATE, ");
		sb.append("    AUTO_END_REASON = '代理人或被代理人離職', ");
		sb.append("    MODIFIER = 'SYSTEM', LASTUPDATE = SYSDATE ");
		sb.append("WHERE AGENT_STATUS = 'U' ");;
		cond.setQueryString(sb.toString());
		dam.exeUpdate(cond);
		
		// 若代理狀態為"U：預計代理" => 當代理開始日期小於等於系統日 且 代理結束日期大於系統日時，『代理狀態』欄位為"U：預計代理"=>"S：代理中"
		sb.setLength(0);
		sb.append("UPDATE TBORG_AGENT  ");
		sb.append("SET AGENT_STATUS = 'S',  ");
		sb.append("    MODIFIER = 'SYSTEM',  ");
		sb.append("    LASTUPDATE = SYSDATE ");
		sb.append("WHERE START_DATE <= SYSDATE AND END_DATE > SYSDATE AND AGENT_STATUS = 'U' ");
		cond.setQueryString(sb.toString());
		dam.exeUpdate(cond);
		
		// 若代理狀態為"U：預計代理" => 當代理結束日期小於等於系統日時，『代理狀態』欄位為"U：預計代理"=>"A：自動終止" / 『自動終止原因』欄位為"代理期限已過期"
		sb.setLength(0);
		sb.append("UPDATE TBORG_AGENT  ");
		sb.append("SET AGENT_STATUS = 'A',  ");
		sb.append("    AUTO_END_DATE = SYSDATE, ");
		sb.append("    AUTO_END_REASON = '代理期限已過期', ");
		sb.append("    MODIFIER = 'SYSTEM',  ");
		sb.append("    LASTUPDATE = SYSDATE ");
		sb.append("WHERE END_DATE <= SYSDATE AND AGENT_STATUS = 'U' ");
		cond.setQueryString(sb.toString());
		dam.exeUpdate(cond);
		
		// 若代理狀態為"W：待代理人確認" => 當代理結束日期小於系統代理結束日時，『代理狀態』欄位為"W：待代理人確認"=>"X：自動取消" / 『自動終止原因』欄位為"代理人逾期未確認"
		sb.setLength(0);
		sb.append("UPDATE TBORG_AGENT ");
		sb.append("SET AGENT_STATUS = 'X', ");
		sb.append("    AUTO_END_DATE = SYSDATE, ");
		sb.append("    AUTO_END_REASON = '代理人逾期未確認', ");
		sb.append("    MODIFIER = 'SYSTEM', ");
		sb.append("    LASTUPDATE = SYSDATE ");
		sb.append("WHERE END_DATE <= SYSDATE AND AGENT_STATUS = 'W' ");
		cond.setQueryString(sb.toString());
		dam.exeUpdate(cond);
	}
	
	/**
	 * 登入時取得代理人
	 * @param userId
	 * @return
	 * @throws JBranchException
	 */
	public List<Map<String, String>> getAgentInfo(String userId) throws JBranchException{
		
		DataAccessManager dam = new DataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT ISPRI.EMP_ID, ISPRI.EMP_NAME, ISPRI.ROLE_ID, ISPRI.ROLE_NAME, 'Y' AS IS_PRIMARY_ROLE, ");
		sb.append("       MEM.DEPT_ID, DEFN.DEPT_NAME, ISPRI.REGION_CENTER_ID, ISPRI.REGION_CENTER_NAME, ISPRI.BRANCH_AREA_ID, ISPRI.BRANCH_AREA_NAME, ISPRI.BRANCH_NBR, ISPRI.BRANCH_NAME ");
		sb.append("FROM VWORG_BRANCH_EMP_DETAIL_INFO ISPRI ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON ISPRI.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN TBORG_DEFN DEFN ON MEM.DEPT_ID = DEFN.DEPT_ID ");
		sb.append("WHERE ISPRI.EMP_ID = :empID ");
		sb.append("AND ISPRI.ROLE_ID IS NOT NULL ");
		
		sb.append("UNION ALL ");
		
		sb.append("SELECT NOTPRI.EMP_ID, NOTPRI.EMP_NAME, NOTPRI.ROLE_ID, NOTPRI.ROLE_NAME, 'N' AS IS_PRIMARY_ROLE, ");
		sb.append("       CASE WHEN NOTPRI.REGION_CENTER_ID IS NOT NULL AND NOTPRI.BRANCH_AREA_ID IS NULL AND NOTPRI.BRANCH_NBR IS NULL THEN NOTPRI.REGION_CENTER_ID ");
		sb.append("            WHEN NOTPRI.REGION_CENTER_ID IS NOT NULL AND NOTPRI.BRANCH_AREA_ID IS NOT NULL AND NOTPRI.BRANCH_NBR IS NULL THEN NOTPRI.BRANCH_AREA_ID ");
		sb.append("            WHEN NOTPRI.REGION_CENTER_ID IS NOT NULL AND NOTPRI.BRANCH_AREA_ID IS NOT NULL AND NOTPRI.BRANCH_NBR IS NOT NULL THEN NOTPRI.BRANCH_NBR ");
		sb.append("       ELSE NULL END AS DEPT_ID, ");
		sb.append("       CASE WHEN NOTPRI.REGION_CENTER_ID IS NOT NULL AND NOTPRI.BRANCH_AREA_ID IS NULL AND NOTPRI.BRANCH_NBR IS NULL THEN NOTPRI.REGION_CENTER_NAME ");
		sb.append("            WHEN NOTPRI.REGION_CENTER_ID IS NOT NULL AND NOTPRI.BRANCH_AREA_ID IS NOT NULL AND NOTPRI.BRANCH_NBR IS NULL THEN NOTPRI.BRANCH_AREA_NAME ");
		sb.append("            WHEN NOTPRI.REGION_CENTER_ID IS NOT NULL AND NOTPRI.BRANCH_AREA_ID IS NOT NULL AND NOTPRI.BRANCH_NBR IS NOT NULL THEN NOTPRI.BRANCH_NAME ");
		sb.append("       ELSE NULL END AS DEPT_NAME, NOTPRI.REGION_CENTER_ID, NOTPRI.REGION_CENTER_NAME, NOTPRI.BRANCH_AREA_ID, NOTPRI.BRANCH_AREA_NAME, NOTPRI.BRANCH_NBR, NOTPRI.BRANCH_NAME ");
		sb.append("FROM VWORG_EMP_PLURALISM_INFO NOTPRI ");
		sb.append("WHERE NOTPRI.EMP_ID = :empID ");
		sb.append("AND NOTPRI.ROLE_ID IS NOT NULL ");
		
		sb.append("UNION ALL ");
		
		sb.append("SELECT AG.EMP_ID, AGENT_DTL.EMP_NAME, AGENT_DTL.ROLE_ID, AGENT_DTL.ROLE_NAME, 'A' AS IS_PRIMARY_ROLE, ");
		sb.append("       MEM.DEPT_ID, DEFN.DEPT_NAME, AGENT_DTL.REGION_CENTER_ID, AGENT_DTL.REGION_CENTER_NAME, AGENT_DTL.BRANCH_AREA_ID, AGENT_DTL.BRANCH_AREA_NAME, AGENT_DTL.BRANCH_NBR, AGENT_DTL.BRANCH_NAME ");
		sb.append("FROM TBORG_AGENT AG ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO AGENT_DTL ON AG.EMP_ID = AGENT_DTL.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON AGENT_DTL.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN TBORG_DEFN DEFN ON MEM.DEPT_ID = DEFN.DEPT_ID ");
		sb.append("WHERE AG.AGENT_ID = :empID ");
		sb.append("AND AG.AGENT_STATUS = 'S' ");
		sb.append("AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("AND AG.DEPT_ID = MEM.DEPT_ID ");
		
		sb.append("UNION ");
		
		sb.append("SELECT AG.EMP_ID, AGENT_DTL.EMP_NAME, AGENT_DTL.ROLE_ID, AGENT_DTL.ROLE_NAME || '(兼)', 'AN' AS IS_PRIMARY_ROLE, ");
		sb.append("       MEM.DEPT_ID, DEFN.DEPT_NAME, AGENT_DTL.REGION_CENTER_ID, AGENT_DTL.REGION_CENTER_NAME, AGENT_DTL.BRANCH_AREA_ID, AGENT_DTL.BRANCH_AREA_NAME, AGENT_DTL.BRANCH_NBR, AGENT_DTL.BRANCH_NAME ");
		sb.append("FROM TBORG_AGENT AG ");
		sb.append("LEFT JOIN VWORG_EMP_PLURALISM_INFO AGENT_DTL ON AG.EMP_ID = AGENT_DTL.EMP_ID AND AG.DEPT_ID = AGENT_DTL.DEPT_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER_PLURALISM MEM ON AGENT_DTL.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN TBORG_DEFN DEFN ON MEM.DEPT_ID = DEFN.DEPT_ID ");
		sb.append("WHERE AG.AGENT_ID = :empID ");
		sb.append("AND AG.AGENT_STATUS = 'S' ");
		sb.append("AND (TRUNC(MEM.TERDTE) >= TRUNC(SYSDATE) OR MEM.TERDTE IS NULL) ");
		sb.append("AND MEM.ACTION <> 'D' ");
		sb.append("AND AG.DEPT_ID = MEM.DEPT_ID ");
		
//		sb.append("SELECT DISTINCT AG.EMP_ID, AGENT_DTL.EMP_NAME, AGENT_DTL.ROLE_ID, AGENT_DTL.ROLE_NAME, 'A' AS IS_PRIMARY_ROLE, ");
//		sb.append("       MEM.DEPT_ID, DEFN.DEPT_NAME, AGENT_DTL.REGION_CENTER_ID, AGENT_DTL.REGION_CENTER_NAME, AGENT_DTL.BRANCH_AREA_ID, AGENT_DTL.BRANCH_AREA_NAME, AGENT_DTL.BRANCH_NBR, AGENT_DTL.BRANCH_NAME "); 
//		sb.append("FROM TBORG_AGENT AG ");
//		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO AGENT_DTL ON AG.EMP_ID = AGENT_DTL.EMP_ID ");
//		sb.append("LEFT JOIN TBORG_MEMBER MEM ON AGENT_DTL.EMP_ID = MEM.EMP_ID ");
//		sb.append("LEFT JOIN TBORG_DEFN DEFN ON MEM.DEPT_ID = DEFN.DEPT_ID ");
//		sb.append("WHERE AG.AGENT_ID = :empID ");
//		sb.append("AND AG.AGENT_STATUS = 'S' ");
//		sb.append("AND MEM.SERVICE_FLAG = 'A' ");
//		sb.append("AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("order by 5 desc ");
//		// 本人
//		sb.append("select m.EMP_ID, EMP_NAME, mr.ROLE_ID, ROLE_NAME, IS_PRIMARY_ROLE")
//				.append(" from (select EMP_ID, EMP_NAME from TBORG_MEMBER where EMP_ID = :empID) m")
//				.append(" left join (select EMP_ID, ROLE_ID, IS_PRIMARY_ROLE from TBORG_MEMBER_ROLE) mr on mr.EMP_ID = m.EMP_ID")
//				.append(" left join (select ROLE_ID, ROLE_NAME from TBORG_ROLE) r on r.ROLE_ID = mr.ROLE_ID")
//				.append(" union all")
//		// 被代理人
//				.append(" select a.EMP_ID, m.EMP_NAME, mr.ROLE_ID, '代理', 'A'")
//				.append(" from (select EMP_ID, AGENT_ID, AGENT_STATUS from TBORG_AGENT where AGENT_STATUS = 'S' and AGENT_ID = :empID) a")
//				.append(" left join (select EMP_ID, EMP_NAME from TBORG_MEMBER) m on m.EMP_ID = a.EMP_ID")
//				.append(" left join (select EMP_ID, ROLE_ID from TBORG_MEMBER_ROLE where IS_PRIMARY_ROLE = 'Y') mr on mr.EMP_ID = m.EMP_ID")
//				.append(" order by 5 desc");
		
		cond.setQueryString(sb.toString());
		cond.setObject("empID", userId);
		List<Map<String,String>> list = dam.exeQuery(cond);
		
		if (list.isEmpty()) {
			list = new ArrayList<Map<String, String>>();
		}
		
		return list;
	}
	
	/**
	 * 取得登入者AO CODE
	 * @param userId
	 * @return
	 * @throws JBranchException
	 */
	public List<String> getAoCode(String userId) throws JBranchException {
		DataAccessManager dam = new DataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append("select coalesce(AO_CODE, '') as AO_CODE ");
		sb.append("from TBORG_SALES_AOCODE ");
		sb.append("where EMP_ID = :loginID order by TYPE");
		
		cond.setObject("loginID", userId);
		cond.setQueryString(sb.toString());
		
		List<Map<String, Object>> result = dam.exeQuery(cond);
		List<String> aoList = new ArrayList<String>();
		
		for (Map<String, Object> map : result) {
			aoList.add((String)map.get("AO_CODE"));
		}
		
		return aoList;
	}
	
	
	/**
	 * 取得登入者相關註記
	 * @param userId
	 * @return
	 * @throws JBranchException
	 */
	public Map<String, Object> getUserInfo(String userId) throws JBranchException{
		Map<String, Object> mapUserInfo = new HashMap<String, Object>();
		
		DataAccessManager dam = new DataAccessManager();
		QueryConditionIF cond = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//StringBuilder sb = new StringBuilder(" select * from DBO.SPSFA_COM_getUserInfo(:loginID) ");
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select * from TBORG_MEMBER where EMP_ID = :loginID ");
		
		cond.setObject("loginID", userId);
		cond.setQueryString(sb.toString());
		
		List<Map<String, Object>> result = dam.exeQuery(cond);
		
		if(!result.isEmpty()){
			mapUserInfo = result.get(0);
		}
		
		return mapUserInfo;
	}
}
