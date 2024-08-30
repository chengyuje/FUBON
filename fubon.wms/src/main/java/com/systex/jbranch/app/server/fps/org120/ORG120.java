package com.systex.jbranch.app.server.fps.org120;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_GEN_AO_CODEVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEPK;
import com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODEVO;
import com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODE_REVIEWVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * CMORG104
 * 
 * @author Lily
 * @date 2016/07/13
 * @spec 富邦WMS系統TDS-ORG120-AOCode查詢與設定.doc
 * 
 * 2016-11-04 modify by ocean 全部重寫
 * 2017-02-02 modify by ocean 組織連動
 * 
 */
@Component("org120")
@Scope("request")
public class ORG120 extends FubonWmsBizLogic{
	
	public DataAccessManager dam = null;
	
	public void getAoCode(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG120InputVO inputVO = (ORG120InputVO)body;
		ORG120OutputVO outputVO = new ORG120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ROLE_NAME ");
		sb.append("FROM TBSYSSECUROLPRIASS PRIASS, TBORG_ROLE ORG_ROLE ");
		sb.append("WHERE PRIASS.ROLEID = ORG_ROLE.ROLE_ID ");
		sb.append("AND PRIVILEGEID IN ('002', '003') ");
		sb.append("AND PRIASS.ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH INFO AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, AO_CODE ");
		sb.append("  FROM VWORG_BRANCH_EMP_DETAIL_INFO ");
		sb.append("  UNION ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, EMP_ID, EMP_NAME, AO_CODE ");
		sb.append("  FROM VWORG_EMP_PLURALISM_INFO ");
		sb.append(") ");
		sb.append("SELECT DISTINCT INFO.EMP_ID, INFO.EMP_NAME AS LABEL, AO.AO_CODE AS DATA ");
		sb.append("FROM TBORG_SALES_AOCODE AO ");
		sb.append("LEFT JOIN INFO ON AO.EMP_ID = INFO.EMP_ID ");
		sb.append("WHERE AO.AO_CODE IS NOT NULL ");
		sb.append("AND AO.TYPE <> '5' ");
		sb.append("AND INFO.EMP_ID IS NOT NULL ");
				
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND INFO.REGION_CENTER_ID = :regionCenterID "); 
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND INFO.REGION_CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND INFO.BRANCH_AREA_ID = :branchAreaID ");
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sb.append("AND INFO.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND INFO.BRANCH_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		} else {
			sb.append("AND INFO.BRANCH_NBR IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		if (list.size() > 0) {
			sb.append("AND INFO.EMP_ID = :empID ");
			queryCondition.setObject("empID", ws.getUser().getUserID());
		}

		sb.append("ORDER BY INFO.EMP_ID ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setAoLst(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/*
	 * 取得可執行編輯的群組
	 */
	public List<Map<String, Object>> getFuncList (DataAccessManager dam, String function) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'ORG120' AND FUNCTIONID = :function) "); 
		sb.append("AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setObject("function", function);
		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}
	
	/*
	 * 取得只能看見自己AOCODE的員工
	 */
	public List<Map<String, Object>> getLoginUserIsAOList (DataAccessManager dam) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIASS.PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS PRIASS ");
		sb.append("WHERE ROLEID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE IS_AO = 'Y') ");
		sb.append("AND ROLEID = :roleID ");
		sb.append("GROUP BY PRIASS.PRIVILEGEID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}

	public void getBranchMbrQuotaLst(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG120InputVO inputVO = (ORG120InputVO)body;
		ORG120OutputVO outputVO = new ORG120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		///
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT DISTINCT BRANCH_AREA_ID ");
		sb.append("FROM VWORG_DEFN_INFO ");
		sb.append("WHERE BRANCH_AREA_NAME LIKE '%私銀%' ");
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> uhrmOPList = dam.exeQuery(queryCondition);
		///
		
		List<Map<String, Object>> reviewList = getFuncList(dam, "confirm");
		List<Map<String, Object>> maintenanceList = getFuncList(dam, "maintenance");
		List<Map<String, Object>> loginUserIsAOList = getLoginUserIsAOList(dam);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("WITH EMP_BASE AS ( ");
		sb.append("  SELECT M.EMP_ID, M.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, 'Y' AS ROLE_TYPE, NULL AS EFFDT ");
		sb.append("  FROM TBORG_MEMBER M ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("    FROM TBORG_DEFN ");
		sb.append("    START WITH DEPT_ID IS NOT NULL ");
		sb.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sb.append("  UNION ");
		sb.append("  SELECT M.EMP_ID, MT.EMP_NAME, M.JOB_TITLE_NAME, M.DEPT_ID AS EMP_DEPT_ID, D.ORG_TYPE, D.DEPT_ID, D.DEPT_NAME, 'N' AS ROLE_TYPE, M.EFFDT ");
		sb.append("  FROM TBORG_MEMBER_PLURALISM M ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("    FROM TBORG_DEFN ");
		sb.append("    START WITH DEPT_ID IS NOT NULL ");
		sb.append("    CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append("  ) D ON M.DEPT_ID = D.CHILD_DEPT_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MT ON M.EMP_ID = MT.EMP_ID ");
		sb.append("  WHERE M.ACTION != 'D' ");
		sb.append(") ");
		
		sb.append(", ROLE_EMP_LIST AS ( ");
		sb.append("  SELECT EL.EMP_ID, EL.EMP_NAME, MR.ROLE_ID, PRI.PRIVILEGEID, EL.DEPT_ID, EL.ORG_TYPE, EL.DEPT_NAME, EL.EFFDT ");
		sb.append("  FROM EMP_BASE EL ");
		sb.append("  INNER JOIN TBORG_MEMBER_ROLE MR ON EL.ROLE_TYPE = MR.IS_PRIMARY_ROLE AND EL.EMP_ID = MR.EMP_ID ");
		sb.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
		sb.append(") ");
		
		sb.append(", ORG_BASE AS ( ");
		sb.append("  SELECT DISTINCT ORG_TYPE, CONNECT_BY_ROOT(DEPT_ID) AS CHILD_DEPT_ID, DEPT_ID, DEPT_NAME ");
		sb.append("  FROM TBORG_DEFN ");
		sb.append("  START WITH DEPT_ID IS NOT NULL ");
		sb.append("  CONNECT BY PRIOR PARENT_DEPT_ID = DEPT_ID ");
		sb.append(") ");
		
		sb.append(", DEPT_ORG AS ( ");
		sb.append("  SELECT ID.DEPT_ID, ID.DEPT_NAME, ID.ORG_TYPE, ");
		sb.append("         CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_30) THEN ID.DEPT_ID_30 ELSE NULL END AS REGION_CENTER_ID, ");
		sb.append("         CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_30) THEN NM.DEPT_NAME_30 ELSE NULL END AS REGION_CENTER_NAME, ");
		sb.append("         CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_40) THEN ID.DEPT_ID_40 ELSE NULL END AS BRANCH_AREA_ID, ");
		sb.append("         CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_40) THEN NM.DEPT_NAME_40 ELSE NULL END AS BRANCH_AREA_NAME, ");
		sb.append("         CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_50) THEN ID.DEPT_ID_50 ELSE NULL END AS BRANCH_NBR, ");
		sb.append("         CASE WHEN EXISTS (SELECT 1 FROM VWORG_DEPT_BR T WHERE T.DEPT_ID = ID.DEPT_ID_50) THEN NM.DEPT_NAME_50 ELSE NULL END AS BRANCH_NAME, ");
		sb.append("         ID.DEPT_ID_00, NM.DEPT_NAME_00, ");
		sb.append("         ID.DEPT_ID_05, NM.DEPT_NAME_05, ");
		sb.append("         ID.DEPT_ID_10, NM.DEPT_NAME_10, ");
		sb.append("         ID.DEPT_ID_20, NM.DEPT_NAME_20, ");
		sb.append("         ID.DEPT_ID_30, NM.DEPT_NAME_30, ");
		sb.append("         ID.DEPT_ID_40, NM.DEPT_NAME_40, ");
		sb.append("         ID.DEPT_ID_50, NM.DEPT_NAME_50 ");
		sb.append("  FROM ( ");
		sb.append("    SELECT * ");
		sb.append("    FROM ( ");
		sb.append("      SELECT A.DEPT_ID, A.ORG_TYPE, A.DEPT_NAME, ORG.ORG_TYPE AS ORG_TYPE_CLS, ORG.DEPT_ID AS LEV_DEPT_ID ");
		sb.append("      FROM TBORG_DEFN A ");
		sb.append("      LEFT JOIN ORG_BASE ORG ON A.DEPT_ID = ORG.CHILD_DEPT_ID ");
		sb.append("    ) ");
		sb.append("    PIVOT (MAX(LEV_DEPT_ID) FOR ORG_TYPE_CLS IN ('00' AS DEPT_ID_00, '05' AS DEPT_ID_05, '10' AS DEPT_ID_10, '20' AS DEPT_ID_20, '30' AS DEPT_ID_30, '40' AS DEPT_ID_40, '50' AS DEPT_ID_50)) ");
		sb.append("  ) ID ");
		sb.append("  INNER JOIN ( ");
		sb.append("    SELECT * ");
		sb.append("    FROM ( ");
		sb.append("      SELECT A.DEPT_ID, ORG.ORG_TYPE, ORG.DEPT_NAME ");
		sb.append("      FROM TBORG_DEFN A ");
		sb.append("      LEFT JOIN ORG_BASE ORG ON A.DEPT_ID = ORG.CHILD_DEPT_ID ");
		sb.append("    ) ");
		sb.append("    PIVOT (MAX(DEPT_NAME) FOR ORG_TYPE IN ('00' AS DEPT_NAME_00, '05' AS DEPT_NAME_05, '10' AS DEPT_NAME_10, '20' AS DEPT_NAME_20, '30' AS DEPT_NAME_30, '40' AS DEPT_NAME_40, '50' AS DEPT_NAME_50)) ");
		sb.append("  ) NM ON ID.DEPT_ID = NM.DEPT_ID ");
		sb.append(") ");
		
		sb.append(", EMP_LIST AS ( ");
		sb.append("  SELECT C.EMP_ID, C.EMP_NAME, C.ROLE_ID, C.PRIVILEGEID, ");
		sb.append("         C.REGION_CENTER_ID AS U_REGION_CENTER_ID, ");
		sb.append("         C.BRANCH_AREA_ID AS U_BRANCH_AREA_ID, ");
		sb.append("         C.BRANCH_NBR AS U_BRANCH_NBR, ");
		sb.append("         CASE WHEN U.REGION_CENTER_ID IS NOT NULL THEN U.REGION_CENTER_ID ELSE C.REGION_CENTER_ID END AS REGION_CENTER_ID, ");
		sb.append("         CASE WHEN U.REGION_CENTER_NAME IS NOT NULL THEN U.REGION_CENTER_NAME ELSE D.REGION_CENTER_NAME END AS REGION_CENTER_NAME, ");
		sb.append("         CASE WHEN U.BRANCH_AREA_ID IS NOT NULL THEN U.BRANCH_AREA_ID ELSE C.BRANCH_AREA_ID END AS BRANCH_AREA_ID, ");
		sb.append("         CASE WHEN U.BRANCH_AREA_NAME IS NOT NULL THEN U.BRANCH_AREA_NAME ELSE D.BRANCH_AREA_NAME END AS BRANCH_AREA_NAME, ");
		sb.append("         CASE WHEN U.BRANCH_NBR IS NOT NULL THEN U.BRANCH_NBR ELSE C.BRANCH_NBR END AS BRANCH_NBR, ");
		sb.append("         CASE WHEN U.BRANCH_NAME IS NOT NULL THEN U.BRANCH_NAME ELSE D.BRANCH_NAME END AS BRANCH_NAME ");
		sb.append("  FROM ( ");
		sb.append("    SELECT EMP_ID, EMP_NAME, ROLE_ID, PRIVILEGEID, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EFFDT ");
		sb.append("    FROM ( ");
		sb.append("      SELECT EMP_ID, EMP_NAME, ROLE_ID, PRIVILEGEID, ORG_TYPE, DEPT_ID, EFFDT ");
		sb.append("      FROM ROLE_EMP_LIST ");
		sb.append("    ) PIVOT (MAX(DEPT_ID) FOR ORG_TYPE IN ('30' AS REGION_CENTER_ID, '40' AS BRANCH_AREA_ID, '50' AS BRANCH_NBR)) ");
		sb.append("  ) C ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT EMP_ID, EMP_NAME, ROLE_ID, PRIVILEGEID, REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NAME, EFFDT ");
		sb.append("    FROM ( ");
		sb.append("      SELECT EMP_ID, EMP_NAME, ROLE_ID, PRIVILEGEID, ORG_TYPE, DEPT_NAME, EFFDT ");
		sb.append("      FROM ROLE_EMP_LIST ");
		sb.append("    ) PIVOT (MAX(DEPT_NAME) FOR ORG_TYPE IN ('30' AS REGION_CENTER_NAME, '40' AS BRANCH_AREA_NAME, '50' AS BRANCH_NAME)) ");
		sb.append("  ) D ON C.EMP_ID = D.EMP_ID AND NVL(C.ROLE_ID, ' ') = NVL(D.ROLE_ID, ' ') AND NVL(C.PRIVILEGEID, ' ') = NVL(D.PRIVILEGEID, ' ') AND NVL(C.EFFDT, SYSDATE) = NVL(D.EFFDT, SYSDATE) ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT BT.*, UB.EMP_ID ");
		sb.append("    FROM DEPT_ORG BT ");
		sb.append("    LEFT JOIN TBORG_UHRM_BRH UB ON BT.DEPT_ID = UB.BRANCH_NBR ");
		sb.append("  ) U ON C.EMP_ID = U.EMP_ID ");
		sb.append(") ");
		
		sb.append(", TAO AS ( ");
		sb.append("  SELECT BASE.EMP_ID, BASE.LASTUPDATE, CREATOR AS MODIFIER ");
		sb.append("  FROM (SELECT TEMP.EMP_ID, MAX(TEMP.CREATETIME) AS LASTUPDATE ");
		sb.append("        FROM TBORG_SALES_AOCODE TEMP ");
		sb.append("        GROUP BY TEMP.EMP_ID) BASE ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE BASE1 ON BASE.EMP_ID = BASE1.EMP_ID AND BASE.LASTUPDATE = BASE1.CREATETIME ");
		sb.append("  GROUP BY BASE.EMP_ID, BASE.LASTUPDATE, CREATOR ");
		sb.append(") ");
		
		sb.append("SELECT CASE WHEN (SELECT COUNT(1) FROM TBORG_SALES_AOCODE_REVIEW RE WHERE RE.EMP_ID = AO.EMP_ID AND REVIEW_STATUS = 'W') > 0 THEN 'W' ELSE 'Y' END AS REVIEW_STATUS, ");
		sb.append("       CASE WHEN (SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = INFO.ROLE_ID) = '002' THEN 'FC' ");
		sb.append("            WHEN (SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = INFO.ROLE_ID) = '003' THEN 'FCH' ");
		sb.append("            ELSE NULL ");
		sb.append("       END AS PRIVILEGEID, ");
		sb.append("       INFO.U_REGION_CENTER_ID, INFO.U_BRANCH_AREA_ID, INFO.U_BRANCH_NBR, ");

		sb.append("       INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
		sb.append("       INFO.EMP_ID, INFO.EMP_NAME, ");
		sb.append("       CASE WHEN REVIEW.ACT_TYPE = 'D' THEN AO.AO_CODE || '(刪)' ");
		sb.append("            WHEN REVIEW.ACT_TYPE = 'A' THEN AO.AO_CODE || '(增)' ");
		sb.append("            WHEN REVIEW.ACT_TYPE = 'M' THEN AO.AO_CODE || '(修)' ");
		sb.append("       ELSE AO.AO_CODE END AS AO_CODE, AO.TYPE, ");
		sb.append("       CASE WHEN REVIEW.PERF_EFF_DATE IS NOT NULL THEN REVIEW.PERF_EFF_DATE ELSE MEM.PERF_EFF_DATE END AS PERF_EFF_DATE, ");
		sb.append("       AO.CREATETIME, AO.CREATOR, ");
		sb.append("       TAO.MODIFIER, TAO.LASTUPDATE, ");
		sb.append("       (SELECT COUNT(1) FROM TBORG_SALES_AOCODE_REVIEW SA WHERE SA.EMP_ID = INFO.EMP_ID) AS COUNTS ");
		sb.append("FROM TBORG_SALES_AOCODE AO ");
		sb.append("LEFT JOIN EMP_LIST INFO ON AO.EMP_ID = INFO.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON AO.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE_REVIEW REVIEW ON AO.AO_CODE = REVIEW.AO_CODE AND REVIEW.REVIEW_STATUS = 'W' ");
		sb.append("LEFT JOIN TAO ON TAO.EMP_ID = AO.EMP_ID ");
		sb.append("WHERE AO.TYPE = '1' ");
		
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0) {
			sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO T WHERE T.EMP_ID = AO.EMP_ID) ");
			
			if (StringUtils.isNotBlank(inputVO.getUhrm_code())) {
				String[] uEmpDtl = inputVO.getUhrm_code().split(",");
				if (uEmpDtl.length >= 2) { //有輸入
					sb.append("AND AO.EMP_ID = :uEmpID ");
					queryCondition.setObject("uEmpID", uEmpDtl[0]);
				}
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
				sb.append("AND INFO.REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			} else {
				sb.append("AND INFO.REGION_CENTER_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			
			boolean uhrmOP = false;
			
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
				for (Map<String, Object> map : uhrmOPList) {
					uhrmOP = StringUtils.equals((String) map.get("BRANCH_AREA_ID"), inputVO.getBranch_area_id()) ? true : false;

					if (uhrmOP) {
						break;
					}
				}
			
				if (uhrmOP) {
					sb.append("AND INFO.U_BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else {
					sb.append("AND INFO.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				}
			} else {
				sb.append("AND INFO.BRANCH_AREA_ID IN (:branchAreaIDList) ");
				queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
				if (!uhrmOP) {
					sb.append("AND INFO.BRANCH_NBR = :branchID "); //分行代碼
					queryCondition.setObject("branchID", inputVO.getBranch_nbr());
				}
			} else {
				if (!uhrmOP) {
					sb.append("AND INFO.BRANCH_NBR IN (:branchIDList) ");
					queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
			}
		}
		
		if (!"".equals(inputVO.getAo_code()) && null != inputVO.getAo_code()) { 
			sb.append("AND AO.EMP_ID IN (SELECT TEMP.EMP_ID FROM TBORG_SALES_AOCODE TEMP WHERE TEMP.AO_CODE = :aoCode) ");
			queryCondition.setObject("aoCode", inputVO.getAo_code());
		}
		
		if (loginUserIsAOList.size() > 0) {
			sb.append("AND AO.EMP_ID = :empID ");
			queryCondition.setObject("empID", ws.getUser().getUserID());
		}
		
		sb.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 ELSE 1 END, INFO.REGION_CENTER_ID, INFO.BRANCH_AREA_ID, INFO.BRANCH_NBR, INFO.EMP_ID, AO.AO_CODE ");

		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> resList = dam.exeQuery(queryCondition);
		if (resList.size() > 0) {
			for (Map<String, Object> map : resList) {
				putAoCode(dam, map, "FCH_AO_CODE", "3", (String) map.get("EMP_ID"));
				putAoCode(dam, map, "FC_AO_CODE", "2", (String) map.get("EMP_ID"));

				if (reviewList.size() > 0) {
					map.put("ACTION_R", "review");
				} 
				
				if (maintenanceList.size() > 0) {
					map.put("ACTION_M", "modify");
				} 

				if (reviewList.size() == 0 && maintenanceList.size() == 0){
					map.put("ACTION", "readonly");
				}
			}
		}
		
		outputVO.setDataList(resList);
		
		sendRtnObject(outputVO);
	}
	
	private Map<String, Object> putAoCode (DataAccessManager dam, Map<String, Object> map, String key, String aoType, String empID) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT EMP_ID, ");
		sb.append("       CASE WHEN ACT_TYPE = 'D' THEN AO_CODE || '(刪)' ");
		sb.append("            WHEN ACT_TYPE = 'A' THEN AO_CODE || '(增)' ");
		sb.append("            WHEN ACT_TYPE = 'M' THEN AO_CODE || '(修)' ");
		sb.append("       ELSE AO_CODE END AS AO_CODE, ");
		sb.append("       MODIFIER, LASTUPDATE, ACT_TYPE ");
		sb.append("FROM TBORG_SALES_AOCODE_REVIEW ");
		sb.append("WHERE TYPE = :type ");
		sb.append("AND EMP_ID = :empID ");
		sb.append("AND REVIEW_STATUS = 'W' ");
		sb.append("AND AO_CODE IS NOT NULL ");
		
		sb.append("UNION ");
		
		sb.append("SELECT EMP_ID, AO_CODE, MODIFIER, LASTUPDATE, '' AS ACT_TYPE ");
		sb.append("FROM TBORG_SALES_AOCODE ");
		sb.append("WHERE TYPE = :type ");
		sb.append("AND EMP_ID = :empID ");
		sb.append("AND AO_CODE NOT IN (SELECT AO_CODE FROM TBORG_SALES_AOCODE_REVIEW WHERE REVIEW_STATUS = 'W' AND AO_CODE IS NOT NULL) ");
		
		queryCondition.setObject("type", aoType);
		queryCondition.setObject("empID", empID);
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> tempList = dam.exeQuery(queryCondition);
		
		if (StringUtils.equals("W", String.valueOf(map.get("REVIEW_STATUS")))) {
			if (tempList.size() > 0) {
				map.put("MODIFIER", String.valueOf(tempList.get(0).get("MODIFIER")));
				map.put("LASTUPDATE", (Date) tempList.get(0).get("LASTUPDATE"));
			}
		}

		String tmpStr = "";
		for (Map<String, Object> tMap : tempList) {
			tmpStr = tmpStr + tMap.get("AO_CODE") + " \n";
			map.put("MODIFIER", String.valueOf(tMap.get("MODIFIER")));
			map.put("LASTUPDATE", (Date) tMap.get("LASTUPDATE"));
		}

		map.put(key, tmpStr);
		
		return map;
	}
	
	public void showORG120MOD (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG120InputVO inputVO = (ORG120InputVO) body;
		ORG120OutputVO outputVO = new ORG120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CASE WHEN (SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = INFO.ROLE_ID) = '002' THEN 'FC' ");
		sb.append("            WHEN (SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = INFO.ROLE_ID) = '003' THEN 'FCH' ");
		sb.append("            WHEN (SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = INFO.ROLE_ID) = 'UHRM002' THEN 'UHRM' ");
		sb.append("       ELSE NULL END AS PRIVILEGEID, ");
		sb.append("       INFO.REGION_CENTER_ID, ");
		sb.append("       INFO.REGION_CENTER_NAME, ");
		sb.append("       INFO.BRANCH_AREA_ID, ");
		sb.append("       INFO.BRANCH_AREA_NAME, ");
		sb.append("       INFO.BRANCH_NBR, ");
		sb.append("       INFO.BRANCH_NAME, ");
		sb.append("       INFO.EMP_ID, ");
		sb.append("       INFO.EMP_NAME, ");
		sb.append("       INFO.AO_CODE, ");
		sb.append("       INFO.CODE_TYPE AS TYPE, ");
		sb.append("       MEM.PERF_EFF_DATE ");
		sb.append("FROM TBORG_SALES_AOCODE AO ");
		sb.append("LEFT JOIN VWORG_EMP_INFO INFO ON AO.AO_CODE = INFO.AO_CODE AND AO.EMP_ID = INFO.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
		sb.append("WHERE INFO.CODE_TYPE = '1' ");
		sb.append("AND INFO.EMP_ID = :empID ");
		queryCondition.setObject("empID", inputVO.getEmpID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				if (StringUtils.equals("FC", String.valueOf(list.get(0).get("PRIVILEGEID")))) {
					sb.append("SELECT AO.EMP_ID, AO.AO_CODE, AO.TYPE, AO.AO_CODE_ATCH_REASON, AO.ACTIVE_DATE, ");
				} else {
					sb.append("SELECT AO.EMP_ID, AO.AO_CODE, AO.TYPE, ");
				}
				sb.append("(SELECT COUNT(MAST.CUST_ID) FROM TBCRM_CUST_MAST MAST WHERE MAST.AO_CODE = AO.AO_CODE) AS COUNTS ");
				sb.append("FROM TBORG_SALES_AOCODE AO ");
				sb.append("WHERE AO.TYPE = :type ");
				sb.append("AND AO.EMP_ID = :empID ");
				queryCondition.setObject("type", (StringUtils.equals("FC", String.valueOf(list.get(0).get("PRIVILEGEID"))) ? "2" : "3"));
				queryCondition.setObject("empID", String.valueOf(map.get("EMP_ID")));
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> otherCodeList = dam.exeQuery(queryCondition);
				map.put("otherCodeList", otherCodeList);
			}
		}
		
		outputVO.setModList(list);
		
		sendRtnObject(outputVO);
	}
	
	public void review (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG120InputVO inputVO = (ORG120InputVO)body;
		ORG120OutputVO outputVO = new ORG120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQNO, EMP_ID, AO_CODE, TYPE, AO_CODE_ATCH_REASON, ACTIVE_DATE, PERF_EFF_DATE, ACT_TYPE, REVIEW_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBORG_SALES_AOCODE_REVIEW ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("AND REVIEW_STATUS = 'W' ");
		queryCondition.setObject("empID", inputVO.getEmpID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				TBORG_SALES_AOCODE_REVIEWVO reVO = new TBORG_SALES_AOCODE_REVIEWVO();
				reVO = (TBORG_SALES_AOCODE_REVIEWVO) dam.findByPKey(TBORG_SALES_AOCODE_REVIEWVO.TABLE_UID, (BigDecimal) map.get("SEQNO"));
				
				if (null != reVO) {
					if (StringUtils.equals("Y", inputVO.getREVIEW_STATUS())) { //核可
						TBORG_SALES_AOCODEPK mainPK = new TBORG_SALES_AOCODEPK();
						mainPK.setAO_CODE(String.valueOf(map.get("AO_CODE")));
						mainPK.setEMP_ID(String.valueOf(map.get("EMP_ID")));
						TBORG_SALES_AOCODEVO mainVO = new TBORG_SALES_AOCODEVO();
						mainVO.setcomp_id(mainPK);
						mainVO = (TBORG_SALES_AOCODEVO) dam.findByPKey(TBORG_SALES_AOCODEVO.TABLE_UID, mainVO.getcomp_id());
						if (StringUtils.equals("A", reVO.getACT_TYPE()) && StringUtils.equals("W", reVO.getREVIEW_STATUS())) {
							if (null == mainVO){
								mainVO = new TBORG_SALES_AOCODEVO();
								mainVO.setcomp_id(mainPK);
								mainVO.setTYPE(reVO.getTYPE());
								mainVO.setAO_CODE_ATCH_REASON(reVO.getAO_CODE_ATCH_REASON());
								mainVO.setACTIVE_DATE(reVO.getACTIVE_DATE());
								mainVO.setACT_TYPE(reVO.getACT_TYPE());
								mainVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
								
								dam.create(mainVO);
							}
						} else if (StringUtils.equals("M", reVO.getACT_TYPE()) && StringUtils.equals("W", reVO.getREVIEW_STATUS())) {
							if (null != mainVO){
								if (StringUtils.equals("1", mainVO.getTYPE())) {
									mainVO.setACT_TYPE(reVO.getACT_TYPE());
									mainVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
									dam.update(mainVO);
									
									TBORG_MEMBERVO memVO = new TBORG_MEMBERVO();
									memVO = (TBORG_MEMBERVO) dam.findByPKey(TBORG_MEMBERVO.TABLE_UID, reVO.getEMP_ID());
									memVO.setPERF_EFF_DATE(reVO.getPERF_EFF_DATE());
									dam.update(memVO);
								} else if (StringUtils.equals("2", mainVO.getTYPE())) { //2:副code
									mainVO.setAO_CODE_ATCH_REASON(reVO.getAO_CODE_ATCH_REASON());
									mainVO.setACTIVE_DATE(reVO.getACTIVE_DATE());
									mainVO.setACT_TYPE(reVO.getACT_TYPE());
									mainVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
									
									dam.update(mainVO);
								} else { // 3:維護code
									mainVO.setACT_TYPE(reVO.getACT_TYPE());
									mainVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
									dam.update(mainVO);
								}
							}
						} else if (StringUtils.equals("D", reVO.getACT_TYPE()) && StringUtils.equals("W", reVO.getREVIEW_STATUS())) {
							if (null != mainVO) {
								dam.delete(mainVO);
							}
						}
						
						reVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
						dam.update(reVO);
						
						TBORG_GEN_AO_CODEVO genVO = new TBORG_GEN_AO_CODEVO();
						genVO = (TBORG_GEN_AO_CODEVO) dam.findByPKey(TBORG_GEN_AO_CODEVO.TABLE_UID, String.valueOf(map.get("AO_CODE")));
						genVO.setUSE_FLAG("Y");
						genVO.setUSE_EMP_ID(String.valueOf(map.get("EMP_ID")));
						dam.update(genVO);
					} else { //退回
						reVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
						
						dam.update(reVO);
					}
				}
			}
		}
		
		sendRtnObject(outputVO);
	}
	
	public void getFreeAoCode (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG120OutputVO outputVO = new ORG120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT AO_CODE AS LABEL, AO_CODE AS DATA, USE_FLAG, IS_BLACK_LIST, USE_EMP_ID ");
		sql.append("FROM TBORG_GEN_AO_CODE TGAC ");
		sql.append("WHERE TGAC.USE_FLAG = 'N' ");
		sql.append("AND TGAC.IS_BLACK_LIST = 'N' ");
		sql.append("AND NOT EXISTS (SELECT AO_CODE FROM TBORG_SALES_AOCODE_REVIEW R WHERE R.REVIEW_STATUS = 'W' AND R.AO_CODE = TGAC.AO_CODE AND R.AO_CODE IS NOT NULL) ");
		sql.append("AND ROWNUM < 11 ");
		
		queryCondition.setQueryString(sql.toString());
		
		outputVO.setFreeAoCodeList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}	
	
	/**
	 * 刪除AO CODE
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	
	public void addAoCodeReviewByDel (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG120InputVO inputVO = (ORG120InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT AO_CODE, (SELECT COUNT(CUST_ID) FROM TBCRM_CUST_MAST WHERE AO_CODE = A.AO_CODE) AS COUNT_CUST ");
		sb.append("FROM TBORG_SALES_AOCODE A ");
		sb.append("WHERE EMP_ID = :empID ");
		
		queryCondition.setObject("empID", inputVO.getEmpID());
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> countCustList = dam.exeQuery(queryCondition);
		
		BigDecimal countCust = BigDecimal.ZERO;
		StringBuffer errorMsg = new StringBuffer();
		errorMsg.append("員編：").append(inputVO.getEmpID()).append("-");
		for (Map<String, Object> map : countCustList) {
			countCust = countCust.add((BigDecimal) map.get("COUNT_CUST"));
			if (((BigDecimal) map.get("COUNT_CUST")).compareTo(BigDecimal.ZERO) == 1) {
				errorMsg.append((String) map.get("AO_CODE")).append("轄下仍有").append((BigDecimal) map.get("COUNT_CUST")).append("位客戶；");
			}
		}
		
		
		if (countCust.compareTo(BigDecimal.ZERO) == 0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("SELECT EMP_ID, AO_CODE, TYPE, AO_CODE_ATCH_REASON ");
			sb.append("FROM TBORG_SALES_AOCODE ");
			sb.append("WHERE EMP_ID = :empID ");
			
			queryCondition.setObject("empID", inputVO.getEmpID());
			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			for (Map<String, Object> map : list) {
				TBORG_SALES_AOCODE_REVIEWVO vo = new TBORG_SALES_AOCODE_REVIEWVO();
				vo.setEMP_ID((String)  map.get("EMP_ID"));
				vo.setTYPE(map.get("TYPE").toString());
				vo.setACT_TYPE("D");
				vo.setREVIEW_STATUS("W");
				
				vo.setSEQNO(new BigDecimal(getSEQ()));
				vo.setAO_CODE((String)  map.get("AO_CODE"));
				
				dam.create(vo);
			}
		} else {
			throw new APException(errorMsg.toString());
		}
		
		sendRtnObject(null);
	}
	
	// === insert into review start ===
	public void addAoCodeSetting(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG120InputVO inputVO = (ORG120InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT EMP_ID, AO_CODE ");
		sb.append("FROM TBORG_SALES_AOCODE ");
		sb.append("WHERE TYPE = :type ");
		sb.append("AND EMP_ID = :empID ");
		queryCondition.setObject("type", inputVO.getTypeOne());
		queryCondition.setObject("empID", inputVO.getEmpID());
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> existAOList = dam.exeQuery(queryCondition);

		if (StringUtils.equals("2", inputVO.getTypeOne()) || StringUtils.equals("3", inputVO.getTypeOne())) { // 副code or 維護code
			// === 比對用
			HashMap newAOCODEMap = new HashMap();
			for (Integer i = 0; i < inputVO.getAoList().size(); i++) {
				newAOCODEMap.put(String.valueOf(i), inputVO.getAoList().get(i).get("AO_CODE"));
			}
			
			ArrayList<String> aoTempList = new ArrayList<String>(); // 已處理AOCODE
			// ===
			
			for (Map<String, Object> map : existAOList) {
				aoTempList.add(String.valueOf(map.get("AO_CODE"))); // 已處理AOCODE
				if (!newAOCODEMap.containsValue(String.valueOf(map.get("AO_CODE")))) { // 新的aoList中沒有舊的副code，標記為刪除
					TBORG_SALES_AOCODE_REVIEWVO vo = new TBORG_SALES_AOCODE_REVIEWVO();
					vo.setEMP_ID(inputVO.getEmpID());
					vo.setTYPE(inputVO.getTypeOne());
					vo.setACT_TYPE("D");
					vo.setREVIEW_STATUS("W");
					
					vo.setSEQNO(new BigDecimal(getSEQ()));
					vo.setAO_CODE(String.valueOf(map.get("AO_CODE")));
					
					dam.create(vo);
				}
			}
			
			for (Map<String, Object> map : inputVO.getAoList()) {
				if ((BigDecimal.valueOf((double) map.get("COUNTS"))).compareTo(new BigDecimal("0")) == 0) {
					if (aoTempList.indexOf(String.valueOf(map.get("AO_CODE"))) < 0) { //regionCenter
						TBORG_SALES_AOCODE_REVIEWVO vo = new TBORG_SALES_AOCODE_REVIEWVO();
						vo.setEMP_ID(inputVO.getEmpID());
						vo.setTYPE(inputVO.getTypeOne());
						vo.setACT_TYPE("A");
						vo.setREVIEW_STATUS("W");
						
						vo.setSEQNO(new BigDecimal(getSEQ()));
						vo.setAO_CODE((String) map.get("AO_CODE"));
						if (StringUtils.equals("2", inputVO.getTypeOne())) {
							vo.setAO_CODE_ATCH_REASON((String) map.get("AO_CODE_ATCH_REASON"));
							vo.setACTIVE_DATE(new Timestamp(new Date(((Double) map.get("ACTIVE_DATE")).longValue()).getTime()));
						}
						
						dam.create(vo);
					}
				}
			}
		} else if (StringUtils.equals("1", inputVO.getTypeOne())) { //主code
			TBORG_SALES_AOCODE_REVIEWVO vo = new TBORG_SALES_AOCODE_REVIEWVO();
			vo.setEMP_ID(inputVO.getEmpID());
			vo.setTYPE(inputVO.getTypeOne());
			vo.setACT_TYPE("M");
			vo.setREVIEW_STATUS("W");
			
			vo.setSEQNO(new BigDecimal(getSEQ()));
			vo.setAO_CODE(inputVO.getAo_code());
			vo.setPERF_EFF_DATE(new Timestamp(inputVO.getAoPerfEffDate().getTime()));

			dam.create(vo);
		}
		
		sendRtnObject(null);
	}
	//  === insert into review end ===
	
	public void getReviewList (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG120InputVO inputVO = (ORG120InputVO) body;
		ORG120OutputVO outputVO = new ORG120OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer(); 
		sb.append("SELECT SEQNO, EMP_ID, AO_CODE, TYPE, AO_CODE_ATCH_REASON, ACTIVE_DATE, PERF_EFF_DATE, ACT_TYPE, REVIEW_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBORG_SALES_AOCODE_REVIEW ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("ORDER BY CREATETIME DESC");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("empID", inputVO.getEmpID());
		
		outputVO.setReviewList(dam.exeQuery(queryCondition));
	
		sendRtnObject(outputVO);
	}
	
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBORG_SALES_AOCODE_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

}
