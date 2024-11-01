package com.systex.jbranch.app.server.fps.pms998;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms998")
@Scope("request")
public class PMS998 extends FubonWmsBizLogic {

	public DataAccessManager dam = null;
	
	// 查詢轄上主管
	public boolean getItemFuncAss (String itemID, String functionID, String keyEmpID, String loginEmpID) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BRA_ROLE_LIST AS ( ");
		sb.append("  SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, PARAM_CODE AS ROLE_ID ");
		sb.append("  FROM VWORG_DEFN_INFO DEFN ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PAM ON PARAM_TYPE IN ('FUBONSYS.FCH_ROLE', 'FUBONSYS.FC_ROLE', 'FUBONSYS.PSOP_ROLE', 'FUBONSYS.BMMGR_ROLE', 'FUBONSYS.FAIA_ROLE') ");
		sb.append("  UNION ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, PARAM_CODE AS ROLE_ID ");
		sb.append("  FROM ( ");
		sb.append("    SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, NULL AS BRANCH_NBR, NULL AS BRANCH_NAME ");
		sb.append("    FROM VWORG_DEFN_INFO ");
		sb.append("  ) DEFN ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PAM ON PARAM_TYPE IN ('FUBONSYS.MBRMGR_ROLE') ");
		sb.append("  UNION ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, PARAM_CODE AS ROLE_ID ");
		sb.append("  FROM ( ");
		sb.append("  SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME, NULL AS BRANCH_AREA_ID, NULL AS BRANCH_AREA_NAME, NULL AS BRANCH_NBR, NULL AS BRANCH_NAME ");
		sb.append("    FROM VWORG_DEFN_INFO ");
		sb.append("  ) DEFN ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PAM ON PARAM_TYPE IN ('FUBONSYS.ARMGR_ROLE') ");
		sb.append(") ");
		sb.append(", Y_ROLE_EMP_LIST AS ( ");
		sb.append("  SELECT MR.CREATETIME AS CREATETIME, M.EMP_ID, M.CUST_ID, M.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, MR.ROLE_ID, MR.IS_PRIMARY_ROLE AS ROLE_TYPE, SYSDATE AS EFFDT ");
		sb.append("  FROM TBORG_MEMBER M ");
		sb.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("  LEFT JOIN ( ");
		sb.append("      SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("      FROM TBORG_DEFN ");
		sb.append("      START WITH DEPT_ID IS NOT NULL ");
		sb.append("      CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sb.append("  WHERE M.SERVICE_FLAG = 'A' ");
		sb.append("  AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append(") ");
		sb.append(", N_ROLE_EMP_LIST AS ( ");
		sb.append("  SELECT MR.CREATETIME, M.EMP_ID, MEM.CUST_ID, MEM.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, MR.ROLE_ID, MR.IS_PRIMARY_ROLE AS ROLE_TYPE, M.EFFDT ");
		sb.append("  FROM TBORG_MEMBER_PLURALISM M ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON M.EMP_ID = MEM.EMP_ID ");
		sb.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'N' ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("    FROM TBORG_DEFN ");
		sb.append("    START WITH DEPT_ID IS NOT NULL ");
		sb.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sb.append("  WHERE (TRUNC(M.TERDTE) >= TRUNC(SYSDATE) OR M.TERDTE IS NULL) ");
		sb.append("  AND M.ACTION <> 'D' ");
		sb.append("  UNION ");
		sb.append("  SELECT MR.CREATETIME AS CREATETIME, M.EMP_ID, M.CUST_ID, M.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, MR.ROLE_ID, MR.IS_PRIMARY_ROLE AS ROLE_TYPE, SYSDATE AS EFFDT ");
		sb.append("  FROM TBORG_MEMBER M ");
		sb.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'N' ");
		sb.append("  LEFT JOIN ( ");
		sb.append("      SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("      FROM TBORG_DEFN ");
		sb.append("      START WITH DEPT_ID IS NOT NULL ");
		sb.append("      CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sb.append("  WHERE M.SERVICE_FLAG = 'A' ");
		sb.append("  AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("  AND NOT EXISTS (SELECT R.ROLE_ID FROM TBORG_ROLE R WHERE R.JOB_TITLE_NAME IS NOT NULL AND MR.ROLE_ID = R.ROLE_ID) ");
		sb.append(") ");
		sb.append(", X_ROLE_EMP_LIST AS ( ");
		sb.append(" SELECT NULL AS CREATETIME, M.EMP_ID, M.CUST_ID, M.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, NULL, 'X' AS ROLE_TYPE, SYSDATE AS EFFDT ");
		sb.append(" FROM TBORG_MEMBER M ");
		sb.append(" LEFT JOIN ( ");
		sb.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("    FROM TBORG_DEFN ");
		sb.append("    START WITH DEPT_ID IS NOT NULL ");
		sb.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append(" ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sb.append(" WHERE M.SERVICE_FLAG = 'A' ");
		sb.append(" AND M.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append(" AND NOT EXISTS (SELECT Y.EMP_ID FROM Y_ROLE_EMP_LIST Y WHERE M.EMP_ID = Y.EMP_ID) ");
		sb.append(" AND NOT EXISTS (SELECT N.EMP_ID FROM N_ROLE_EMP_LIST N WHERE M.EMP_ID = N.EMP_ID) ");
		sb.append(") ");
		sb.append(", TREE_BY_EMP AS ( ");
		sb.append("  SELECT * FROM Y_ROLE_EMP_LIST WHERE EMP_ID = :keyEmpID ");
		sb.append("  UNION ");
		sb.append("  SELECT * FROM N_ROLE_EMP_LIST WHERE EMP_ID = :keyEmpID ");
		sb.append("  UNION ");
		sb.append("  SELECT * FROM X_ROLE_EMP_LIST WHERE EMP_ID = :keyEmpID ");
		sb.append(") ");

		sb.append("SELECT INFO.EMP_DEPT_ID AS DEPT_ID, INFO.EMP_ID, INFO.ROLE_ID, INFO.ROLE_NAME ");
		sb.append("FROM VWORG_EMP_INFO INFO ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND EXISTS ( ");
		sb.append("  SELECT BRA_ROLE.ROLE_ID, ");
		sb.append("         CASE WHEN BRA_ROLE.BRANCH_NBR IS NOT NULL THEN BRA_ROLE.BRANCH_NBR ");
		sb.append("              WHEN BRA_ROLE.BRANCH_AREA_ID IS NOT NULL THEN BRA_ROLE.BRANCH_AREA_ID ");
		sb.append("              WHEN BRA_ROLE.REGION_CENTER_ID IS NOT NULL THEN BRA_ROLE.REGION_CENTER_ID ");
		sb.append("         ELSE NULL END AS DEPT_ID ");
		sb.append("  FROM BRA_ROLE_LIST BRA_ROLE ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND EXISTS ( ");
		sb.append("    SELECT 1 ");
		sb.append("    FROM TBSYSSECUPRIFUNMAP PRIMAP ");
		sb.append("    LEFT JOIN TBSYSSECUROLPRIASS PRIASS ON PRIMAP.PRIVILEGEID = PRIASS.PRIVILEGEID ");
		sb.append("    WHERE ITEMID = :itemID ");
		sb.append("    AND FUNCTIONID = :functionID ");
		sb.append("    AND BRA_ROLE.ROLE_ID = PRIASS.ROLEID ");
		sb.append("  ) ");
		sb.append("  AND CASE WHEN BRANCH_NBR IS NOT NULL THEN BRANCH_NBR ");
		sb.append("           WHEN BRANCH_AREA_ID IS NOT NULL THEN BRANCH_AREA_ID ");
		sb.append("           WHEN REGION_CENTER_ID IS NOT NULL THEN REGION_CENTER_ID ");
		sb.append("      ELSE NULL END IN (SELECT DEPT_ID FROM TREE_BY_EMP) ");
		sb.append("  AND BRA_ROLE.ROLE_ID = INFO.ROLE_ID ");
		sb.append("  AND CASE WHEN BRA_ROLE.BRANCH_NBR IS NOT NULL THEN BRA_ROLE.BRANCH_NBR ");
		sb.append("           WHEN BRA_ROLE.BRANCH_AREA_ID IS NOT NULL THEN BRA_ROLE.BRANCH_AREA_ID ");
		sb.append("           WHEN BRA_ROLE.REGION_CENTER_ID IS NOT NULL THEN BRA_ROLE.REGION_CENTER_ID ");
		sb.append("      ELSE NULL END = INFO.EMP_DEPT_ID ");
		sb.append(")");
		
		switch (functionID) {
			case "confirm":
				sb.append("AND INFO.EMP_ID <> :keyEmpID ");
				sb.append("AND INFO.EMP_ID = :loginEmpID ");
				break;
			default:
				sb.append("AND INFO.EMP_ID = :loginEmpID ");
				break;
		}

		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("itemID", itemID);
		queryCondition.setObject("functionID", functionID);
		queryCondition.setObject("keyEmpID", keyEmpID);
		queryCondition.setObject("loginEmpID", loginEmpID);

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	// 查詢
	public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		initUUID();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		PMS998InputVO inputVO = (PMS998InputVO) body;
		PMS998OutputVO outputVO = new PMS998OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT CASE WHEN (SELECT COUNT(1) FROM TBORG_UHRM_BRH UB WHERE UB.EMP_ID = A.EMP_ID) > 0 THEN 'Y' ELSE 'N' END AS RM_FLAG,  ");
		sb.append("       A.SEQ, ");
		sb.append("       A.BRANCH_NBR, ");
		sb.append("       DEFN.BRANCH_NAME, ");
		sb.append("       A.EMP_ID, ");
		sb.append("       MEM.EMP_NAME, ");
		sb.append("       A.CUST_ID, ");
		sb.append("       A.CUST_NAME, ");
		sb.append("       A.C2E_RELATION, ");
		sb.append("       A.PROVE, ");
		sb.append("       A.NOTE, ");
		sb.append("       TO_CHAR(A.ALLOW_START_DATE, 'YYYY-MM-DD') AS ALLOW_START_DATE, ");
		sb.append("       TO_CHAR(A.ALLOW_END_DATE, 'YYYY-MM-DD') AS ALLOW_END_DATE, ");
		sb.append("       A.CREATE_BOSS, ");
		sb.append("       CB.EMP_NAME AS CREATE_BOSS_NAME, ");
		sb.append("       TO_CHAR(A.CREATE_DATE, 'YYYY-MM-DD') AS CREATE_DATE, ");
		sb.append("       A.STATUS, ");
		sb.append("       A.CREVIEW_BOSS, ");
		sb.append("       CR.EMP_NAME AS CREVIEW_BOSS_NAME ");
		sb.append("FROM TBPMS_ALLOW_LIST A ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON A.BRANCH_NBR = DEFN.BRANCH_NBR ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON A.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER CB ON A.CREATE_BOSS = CB.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER CR ON A.CREVIEW_BOSS = CR.EMP_ID ");
		sb.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N ERCN ON A.EMP_ID = ERCN.EMP_ID AND A.BRANCH_NBR = ERCN.DEPT_ID AND A.ALLOW_START_DATE BETWEEN ERCN.START_TIME AND ERCN.END_TIME ");

		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) { 
			if (StringUtils.isNumeric(inputVO.getBranch_nbr()) && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("AND DEFN.BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("AND DEFN.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("AND DEFN.REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("AND (DEFN.BRANCH_NBR IN (:branchIDList) OR DEFN.BRANCH_NBR IS NULL) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			
			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UB WHERE UB.EMP_ID = A.EMP_ID) ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("AND (");
				sb.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE A.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("  OR ERCN.E_DEPT_ID = :uhrmOP ");
				sb.append(")");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("AND CASE WHEN (SELECT COUNT(1) FROM VWORG_EMP_UHRM_INFO UB WHERE UB.EMP_ID = A.EMP_ID AND UB.DEPT_ID = :loginArea) > 0 THEN 'Y' ELSE 'N' END = 'Y' ");
			queryCondition.setObject("loginArea", (String) getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
		}
		
		
		if (StringUtils.isNotBlank(inputVO.getEMP_ID())) {
			sb.append("AND A.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEMP_ID());
		}
		
		sb.append("ORDER BY A.BRANCH_NBR, A.EMP_ID, TO_CHAR(A.ALLOW_START_DATE, 'YYYY-MM-DD') DESC ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		// confirm
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT PRIASS.ROLEID AS ROLE_ID ");
		sb.append("FROM TBSYSSECUPRIFUNMAP PRIMAP ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRIASS ON PRIMAP.PRIVILEGEID = PRIASS.PRIVILEGEID ");
		sb.append("WHERE PRIMAP.ITEMID = :itemID ");
		sb.append("AND PRIMAP.FUNCTIONID = :functionID ");
		sb.append("AND PRIASS.ROLEID = :roleID ");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("itemID", "PMS998");
		queryCondition.setObject("functionID", "confirm");
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		List<Map<String, Object>> confirmFlag = dam.exeQuery(queryCondition);
		
		// maintenance
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT PRIASS.ROLEID AS ROLE_ID ");
		sb.append("FROM TBSYSSECUPRIFUNMAP PRIMAP ");
		sb.append("LEFT JOIN TBSYSSECUROLPRIASS PRIASS ON PRIMAP.PRIVILEGEID = PRIASS.PRIVILEGEID ");
		sb.append("WHERE ITEMID = :itemID ");
		sb.append("AND FUNCTIONID = :functionID ");
		sb.append("AND PRIASS.ROLEID = :roleID ");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("itemID", "PMS998");
		queryCondition.setObject("functionID", "maintenance");
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		List<Map<String, Object>> maintenanceFlag = dam.exeQuery(queryCondition);
		
		// setFlag
		for (Map<String, Object> map : list) {
			switch((String) map.get("STATUS")) {
				case "W":
					if (confirmFlag.size() > 0 && !StringUtils.equals((String) getUserVariable(FubonSystemVariableConsts.LOGINID), (String) map.get("CREATE_BOSS"))) {
						map.put("CONFIRM_YN", "Y");
					} else {
						map.put("CONFIRM_YN", "N");
					}
					break;
				case "A":
					map.put("CONFIRM_YN", "N");
					break;
			}
			
			if (maintenanceFlag.size() > 0) {
				map.put("MAINTENANCE_YN", getItemFuncAss("PMS998", "maintenance", (String) map.get("CREATE_BOSS"), (String) getUserVariable(FubonSystemVariableConsts.LOGINID)) ? "Y" : "N");
			}
		}

		outputVO.setResultList(list);

		this.sendRtnObject(outputVO);
	}

	// 查詢客戶姓名/核可/退回/刪除/新增
	public void action(Object body, IPrimitiveMap header) throws JBranchException {

		PMS998InputVO inputVO = (PMS998InputVO) body;
		PMS998OutputVO outputVO = new PMS998OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		switch(inputVO.getActionType()) {
			case "A":	// 新增
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("SELECT BRANCH_NBR, EMP_ID, CUST_ID, STATUS ");
				sb.append("FROM TBPMS_ALLOW_LIST ");
				sb.append("WHERE BRANCH_NBR = :BRANCH_NBR ");
				sb.append("AND EMP_ID = :EMP_ID ");
				sb.append("AND CUST_ID = :CUST_ID ");
				sb.append("AND STATUS IN ('W', 'A') ");
				
				queryCondition.setObject("BRANCH_NBR", inputVO.getBranchNbr());
				queryCondition.setObject("EMP_ID", inputVO.getEmpID());
				queryCondition.setObject("CUST_ID", inputVO.getCustID());
				
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
				if (list.size() > 0) {
					throw new APException("該分行+員編+客戶已有設定白名單");
				} else {
					sb = new StringBuffer();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					
					sb.append("INSERT INTO TBPMS_ALLOW_LIST ( ");
					sb.append("  SEQ, ");
					sb.append("  BRANCH_NBR, ");
					sb.append("  EMP_ID, ");
					sb.append("  CUST_ID, ");
					sb.append("  CUST_NAME, ");
					sb.append("  C2E_RELATION, ");
					sb.append("  PROVE, ");
					sb.append("  NOTE, ");
					sb.append("  CREATE_BOSS, ");
					sb.append("  CREATE_DATE, ");
					sb.append("  STATUS, ");
					sb.append("  VERSION, ");
					sb.append("  CREATETIME, ");
					sb.append("  CREATOR ");
					sb.append(") ");
					sb.append("VALUES ( ");
					sb.append("	 TBPMS_ALLOW_LIST_SEQ.nextval, ");
					sb.append("	 :BRANCH_NBR, ");
					sb.append("	 :EMP_ID, ");
					sb.append("	 :CUST_ID, ");
					sb.append("	 :CUST_NAME, ");
					sb.append("	 :C2E_RELATION, ");
					sb.append("	 :PROVE, ");
					sb.append("	 :NOTE, ");
					sb.append("	 :CREATE_BOSS, ");
					sb.append("	 SYSDATE, ");
					sb.append("	 'W', ");
					sb.append("	 0, ");
					sb.append("  SYSDATE, ");
					sb.append("  :CREATE_BOSS ");
					sb.append(") ");
					
					queryCondition.setQueryString(sb.toString());
					
					queryCondition.setObject("BRANCH_NBR", inputVO.getBranchNbr());
					queryCondition.setObject("EMP_ID", inputVO.getEmpID());
					queryCondition.setObject("CUST_ID", inputVO.getCustID());
					queryCondition.setObject("CUST_NAME", inputVO.getCustName());
					queryCondition.setObject("C2E_RELATION", inputVO.getC2eRelation());
					queryCondition.setObject("PROVE", inputVO.getProve());
					queryCondition.setObject("NOTE", StringUtils.isNotEmpty(inputVO.getNote()) ? inputVO.getNote() : "");
					queryCondition.setObject("CREATE_BOSS", getUserVariable(FubonSystemVariableConsts.LOGINID));
					
					dam.exeUpdate(queryCondition);
					
					this.sendRtnObject(outputVO);
				}
				
				break;
			case "RT":	// 覆核-核可
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("UPDATE TBPMS_ALLOW_LIST ");
				sb.append("SET STATUS = 'A', ");
				sb.append("    ALLOW_START_DATE = TRUNC(SYSDATE + 1), ");
				sb.append("    ALLOW_END_DATE = TRUNC(SYSDATE + 181), ");
				sb.append("    CREVIEW_BOSS = :CREVIEW_BOSS, ");
				sb.append("    CREVIEW_DATE = SYSDATE, ");
				sb.append("    LASTUPDATE = SYSDATE, ");
				sb.append("    MODIFIER = :CREVIEW_BOSS ");
				sb.append("WHERE SEQ = :SEQ ");
				
				queryCondition.setQueryString(sb.toString());
				
				// KEY
				queryCondition.setObject("SEQ", inputVO.getSeq());
				
				// COLUMN
				queryCondition.setObject("CREVIEW_BOSS", getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				dam.exeUpdate(queryCondition);
				
				this.sendRtnObject(outputVO);
				
				break;
			case "RF":	// 覆核-退回
			case "D":	// 刪除
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("DELETE FROM TBPMS_ALLOW_LIST ");
				sb.append("WHERE SEQ = :SEQ ");

				queryCondition.setQueryString(sb.toString());
				
				queryCondition.setObject("SEQ", inputVO.getSeq());
				
				dam.exeUpdate(queryCondition);
				
				this.sendRtnObject(outputVO);
				
				break;
			case "SC":	// 查詢-客戶ID
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("SELECT CUST_ID, CUST_NAME ");
				sb.append("FROM TBCRM_CUST_MAST ");
				sb.append("WHERE CUST_ID = :CUST_ID ");
				
				queryCondition.setQueryString(sb.toString());
				
				queryCondition.setObject("CUST_ID", inputVO.getCustID());
				
				List<Map<String, Object>> custList = dam.exeQuery(queryCondition);
				
				if (custList.size() > 0) {
					outputVO.setCustName((String) custList.get(0).get("CUST_NAME"));
				} else {
					throw new APException("查無該客戶。");
				}
				
				this.sendRtnObject(outputVO);
				
				break;
		}
	}
	
	// 匯出檔案
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS998InputVO inputVO = (PMS998InputVO) body;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "內控報表白名單_" + sdf.format(new Date()) + ".csv";
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> statusMap = xmlInfo.doGetVariable("PMS.PMS998_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> c2eMap = xmlInfo.doGetVariable("PMS.C2E_RELATION", FormatHelper.FORMAT_3);
		Map<String, String> proveMap = xmlInfo.doGetVariable("PMS.PROVE", FormatHelper.FORMAT_3);

		String[] csvHeader = { 
				"狀態", "分行代碼", "分行名稱", 
				"行員員編", "行員姓名", "客戶姓名", 
				"客戶ID", "與行員關係", "確認佐證", "排除起日",
				"排除迄日", "建檔主管", "建檔日期", "放行主管" };

		String[] csvMain =  { 
				"STATUS", "BRANCH_NBR", "BRANCH_NAME", 
				"EMP_ID", "EMP_NAME", "CUST_NAME", "CUST_ID",
				"C2E_RELATION", "PROVE", "ALLOW_START_DATE", 
				"ALLOW_END_DATE", "CREATE_BOSS", "CREATE_BOSS_NAME",
				"CREATE_DATE", "CREVIEW_BOSS", "CREVIEW_BOSS_NAME" };
	

		List<Object[]> csvData = new ArrayList<>();

		for (Map<String, Object> map : inputVO.getList()) {

			String[] records = new String[csvHeader.length];

			for (int i = 0; i < csvMain.length; i++) {
				switch (csvMain[i]) {
				case "STATUS":
					records[i] = statusMap.get(String.valueOf(map.get(csvMain[i])));
					break;
				case "BRANCH_NBR":
					records[i] = "=\"" + String.valueOf(map.get(csvMain[i])) + "\"";
					break;
				case "BRANCH_NAME":
					records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "EMP_ID":
					records[i] = "=\"" + String.valueOf(map.get(csvMain[i])) + "\"";
					break;
				case "EMP_NAME":
					records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "CUST_NAME":
					records[i] = String.valueOf(map.get(csvMain[i]));
					break;
				case "CUST_ID":
					records[i] = DataFormat.getCustIdMaskForHighRisk(String.valueOf(map.get(csvMain[i])));
					break;
				case "C2E_RELATION":
					records[i] = c2eMap.get(String.valueOf(map.get(csvMain[i])));
					break;
				case "PROVE":
					if(!"03".equals(String.valueOf(map.get(csvMain[i])))) {
						records[i] = proveMap.get(String.valueOf(map.get(csvMain[i])));
					} else {
						records[i] = proveMap.get(String.valueOf(map.get(csvMain[i]))) + ":" + map.get("NOTE");
					}
					break;
				case "ALLOW_START_DATE":
					records[i] = checkIsNull(map, csvMain[i]);
					break;
				case "ALLOW_END_DATE":
					records[i] = checkIsNull(map, csvMain[i]);
					break;
				case "CREATE_BOSS":
				case "CREATE_BOSS_NAME":
					if ("CREATE_BOSS".equals(csvMain[i])) {
						records[i] = String.valueOf(map.get(csvMain[i]));
					} else {
						records[i - 1] = records[i - 1] + "-" + String.valueOf(map.get(csvMain[i]));
					}
					break;
				case "CREATE_DATE":
					records[i - 1] = String.valueOf(map.get(csvMain[i]));
					break;
				case "CREVIEW_BOSS":
				case "CREVIEW_BOSS_NAME":
					if ("CREVIEW_BOSS".equals(csvMain[i])) {
						if(map.get(csvMain[i]) != null)
							records[i - 1] = String.valueOf(map.get(csvMain[i]));
						else
							records[i - 1] = "";
					} else {
						if(map.get(csvMain[i]) != null)
							records[i - 2] = records[i - 2] + "-" + String.valueOf(map.get(csvMain[i]));
					}
					break;
				}
			}
			csvData.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);

		this.sendRtnObject(null);
	}
	
	private String checkIsNull(Map<String, Object> map, String key) {
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
