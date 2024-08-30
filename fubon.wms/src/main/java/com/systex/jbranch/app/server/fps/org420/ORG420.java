package com.systex.jbranch.app.server.fps.org420;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org420")
@Scope("request")
public class ORG420 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public void query(Object body, IPrimitiveMap header) throws Exception {
		
		ORG420InputVO inputVO = (ORG420InputVO) body;
		ORG420OutputVO outputVO = new ORG420OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH DTL AS ( ");
		sb.append("  SELECT DEPT.REGION_CENTER_ID, DEPT.REGION_CENTER_NAME, DEPT.BRANCH_AREA_ID, DEPT.BRANCH_AREA_NAME, DEPT.BRANCH_NBR, DEPT.BRANCH_NAME, ");
		sb.append("         CASE WHEN SYS_ROL.PRIVILEGEID IN ('001') THEN '001' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('002') THEN '002' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('003') THEN '003' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('004') THEN '004' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('005', '007', '008') THEN '005' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('009') THEN '009' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('006') THEN '006' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('010', '011') THEN '011' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('012') THEN '012' ");
		sb.append("         ELSE SYS_ROL.ROLEID END AS ROLEID, ");
		sb.append("         CASE WHEN FUBON_ROL.ROLE_NAME LIKE 'FCH%' THEN 'FCH'  ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('005', '007', '008') THEN '作業人員' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('009') THEN '業務主管' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('006') THEN '作業主管' ");
		sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('010', '011') THEN '分行個金主管' ");
		sb.append("         ELSE FUBON_ROL.ROLE_NAME END AS ROLE_NAME, ");
		sb.append("         JAN_CNT, FEB_CNT, MAR_CNT, APR_CNT, MAY_CNT, JUN_CNT, JUL_CNT, AUG_CNT, SEP_CNT, OCT_CNT, NOV_CNT, DEC_CNT, ");
		sb.append("         NVL(NOW_STATUS.MN_LEAVING, 0) AS MN_LEAVING, NVL(NOW_STATUS.YR_LEAVING, 0) AS YR_LEAVING, NVL(NOW_STATUS.SERVING, 0) AS SERVING, NVL(NOW_STATUS.LEAVING, 0) AS LEAVING, ");
		sb.append("         NVL((JAN_CNT + FEB_CNT + MAR_CNT + APR_CNT + MAY_CNT + JUN_CNT + JUL_CNT + AUG_CNT + SEP_CNT + OCT_CNT + NOV_CNT + DEC_CNT), 0) AS TOTAL_LEAVING ");
		sb.append("  FROM TBSYSSECUROLPRIASS SYS_ROL ");
		sb.append("  LEFT JOIN TBORG_ROLE FUBON_ROL ON SYS_ROL.ROLEID = FUBON_ROL.ROLE_ID ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEPT ON 1 = 1 ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT BRANCH_ID, ROLEID, JAN_CNT, FEB_CNT, MAR_CNT, APR_CNT, MAY_CNT, JUN_CNT, JUL_CNT, AUG_CNT, SEP_CNT, OCT_CNT, NOV_CNT, DEC_CNT ");
		sb.append("    FROM ( ");
		sb.append("      SELECT DEPT_ID AS BRANCH_ID, DEPT_NAME AS BRANCH_NAME, SYS_ROL.ROLEID, FUBON_ROL.ROLE_NAME, ");
		sb.append("             SUBSTR(YEAR_LIST.YR, 5, 2) AS YR, ");
		sb.append("             (SELECT COUNT(MEM_T.EMP_ID) ");
		sb.append("              FROM TBORG_MEMBER MEM_T ");
		sb.append("              LEFT JOIN TBORG_ROLE ROL_T ON MEM_T.JOB_TITLE_NAME = ROL_T.JOB_TITLE_NAME AND ROL_T.JOB_TITLE_NAME IS NOT NULL ");
		sb.append("              WHERE TO_CHAR(MEM_T.JOB_RESIGN_DATE, 'yyyyMM') = YEAR_LIST.YR ");
		sb.append("              AND ROL_T.ROLE_ID = SYS_ROL.ROLEID ");
		sb.append("              AND MEM_T.DEPT_ID = DEPT_DTL.DEPT_ID) AS LEVING_COUNT ");
		sb.append("      FROM TBSYSSECUROLPRIASS SYS_ROL ");
		sb.append("      LEFT JOIN TBORG_ROLE FUBON_ROL ON SYS_ROL.ROLEID = FUBON_ROL.ROLE_ID ");
		sb.append("      LEFT JOIN TBORG_DEFN DEPT_DTL ON 1=1 ");
		sb.append("      LEFT JOIN (SELECT TO_CHAR(LAST_DAY(ADD_MONTHS(ADD_MONTHS(TRUNC(SYSDATE,'yyyy'),13)-1/24/60/60,-LEVEL)), 'yyyyMM') AS YR FROM DUAL CONNECT BY LEVEL<=12) YEAR_LIST ON 1 = 1 ");
		sb.append("      WHERE SYS_ROL.PRIVILEGEID IN ('001', '002', '003', '004', '005', '006', '007', '008', '009', '010', '011') ");
		sb.append("      AND DEPT_DTL.ORG_TYPE = '50' ");
		sb.append("      AND DEPT_ID >= '200' ");
		sb.append("      AND DEPT_ID <= '900' ");
		sb.append("      AND LENGTH(DEPT_ID) = 3 ");
		sb.append("    ) PIVOT (SUM(LEVING_COUNT) FOR YR IN ('01' AS JAN_CNT, '02' AS FEB_CNT, '03' AS MAR_CNT, '04' AS APR_CNT, '05' AS MAY_CNT, '06' AS JUN_CNT, '07' AS JUL_CNT, '08' AS AUG_CNT, '09' AS SEP_CNT, '10' AS OCT_CNT, '11' AS NOV_CNT, '12' AS DEC_CNT)) ");
		sb.append("  ) MON_COUNT ON DEPT.BRANCH_NBR = MON_COUNT.BRANCH_ID AND SYS_ROL.ROLEID = MON_COUNT.ROLEID ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT DEPT_ID, ROLE_ID, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(MN_LEAVING) AS MN_LEAVING ");
		sb.append("    FROM (SELECT MEM.DEPT_ID, MEM.EMP_ID, MROLE.ROLE_ID, ");
		sb.append("                 CASE WHEN (MEM.JOB_RESIGN_DATE IS NULL OR TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') > TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS SERVING, "); // --至系統日止仍在職(不含當日離職)
		sb.append("                 CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyy') = TO_CHAR(SYSDATE, 'yyyy') AND TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS LEAVING, "); // --至系統日止預計離職(含當日離職)
		sb.append("                 CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyy') = TO_CHAR(SYSDATE, 'yyyy') AND TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') <= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS YR_LEAVING, "); // --當年度至系統日止離職(含當日離職)小計
		sb.append("                 CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMM') = TO_CHAR(SYSDATE, 'yyyyMM') AND TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') <= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS MN_LEAVING "); // --當月至系統日止離職(含當日離職)數
		sb.append("          FROM TBORG_MEMBER MEM, TBORG_ROLE MROLE ");
		sb.append("          WHERE MEM.DEPT_ID IS NOT NULL ");
		sb.append("          AND MEM.JOB_TITLE_NAME = MROLE.JOB_TITLE_NAME ");
		sb.append("          AND MROLE.JOB_TITLE_NAME IS NOT NULL ");
		sb.append("          AND MROLE.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('001', '002', '003', '004', '005', '006', '007', '008', '009', '010', '011'))) ARRANGE ");
		sb.append("    GROUP BY DEPT_ID, ROLE_ID ");
		sb.append("  ) NOW_STATUS ON DEPT.BRANCH_NBR = NOW_STATUS.DEPT_ID AND SYS_ROL.ROLEID = NOW_STATUS.ROLE_ID ");
		sb.append("  WHERE SYS_ROL.PRIVILEGEID IN ('001', '002', '003', '004', '005', '006', '007', '008', '009', '010', '011') ");

		if (StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sb.append("  UNION ");
			  
			sb.append("  SELECT DEPT.REGION_CENTER_ID, DEPT.REGION_CENTER_NAME, DEPT.BRANCH_AREA_ID, DEPT.BRANCH_AREA_NAME, DEPT.BRANCH_NBR, DEPT.BRANCH_NAME, ");
			sb.append("         CASE WHEN SYS_ROL.PRIVILEGEID IN ('001') THEN '001' ");
			sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('002') THEN '002' ");
			sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('003') THEN '003' ");
			sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('004') THEN '004' ");
			sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('005', '007', '008') THEN '005' ");
			sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('009') THEN '009' ");
			sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('006') THEN '006' ");
			sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('010', '011') THEN '011' ");
			sb.append("				 WHEN SYS_ROL.PRIVILEGEID IN ('012') THEN '012' ");
			sb.append("         ELSE SYS_ROL.ROLEID END AS ROLEID, ");
			sb.append("         FUBON_ROL.ROLE_NAME, ");
			sb.append("         JAN_CNT, FEB_CNT, MAR_CNT, APR_CNT, MAY_CNT, JUN_CNT, JUL_CNT, AUG_CNT, SEP_CNT, OCT_CNT, NOV_CNT, DEC_CNT, ");
			sb.append("         NVL(NOW_STATUS.MN_LEAVING, 0) AS MN_LEAVING, NVL(NOW_STATUS.YR_LEAVING, 0) AS YR_LEAVING, NVL(NOW_STATUS.SERVING, 0) AS SERVING, NVL(NOW_STATUS.LEAVING, 0) AS LEAVING, ");
			sb.append("         NVL((JAN_CNT + FEB_CNT + MAR_CNT + APR_CNT + MAY_CNT + JUN_CNT + JUL_CNT + AUG_CNT + SEP_CNT + OCT_CNT + NOV_CNT + DEC_CNT), 0) AS TOTAL_LEAVING ");
			sb.append("  FROM TBSYSSECUROLPRIASS SYS_ROL ");
			sb.append("  LEFT JOIN TBORG_ROLE FUBON_ROL ON SYS_ROL.ROLEID = FUBON_ROL.ROLE_ID ");
			sb.append("  LEFT JOIN (SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME FROM VWORG_DEFN_INFO GROUP BY  REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME) DEPT ON 1 = 1 ");
			sb.append("  LEFT JOIN ( ");
			sb.append("    SELECT BRANCH_AREA_ID, ROLEID, JAN_CNT, FEB_CNT, MAR_CNT, APR_CNT, MAY_CNT, JUN_CNT, JUL_CNT, AUG_CNT, SEP_CNT, OCT_CNT, NOV_CNT, DEC_CNT ");
			sb.append("    FROM ( ");
			sb.append("      SELECT DEPT_ID AS BRANCH_AREA_ID, DEPT_NAME AS BRANCH_AREA_NAME, SYS_ROL.ROLEID, FUBON_ROL.ROLE_NAME, ");
			sb.append("             SUBSTR(YEAR_LIST.YR, 5, 2) AS YR, ");
			sb.append("             (SELECT COUNT(MEM_T.EMP_ID) ");
			sb.append("              FROM TBORG_MEMBER MEM_T ");
			sb.append("              LEFT JOIN TBORG_ROLE ROL_T ON MEM_T.JOB_TITLE_NAME = ROL_T.JOB_TITLE_NAME AND ROL_T.JOB_TITLE_NAME IS NOT NULL ");
			sb.append("              WHERE TO_CHAR(MEM_T.JOB_RESIGN_DATE, 'yyyyMM') = YEAR_LIST.YR ");
			sb.append("              AND ROL_T.ROLE_ID = SYS_ROL.ROLEID ");
			sb.append("              AND MEM_T.DEPT_ID = DEPT_DTL.DEPT_ID) AS LEVING_COUNT ");
			sb.append("      FROM TBSYSSECUROLPRIASS SYS_ROL ");
			sb.append("      LEFT JOIN TBORG_ROLE FUBON_ROL ON SYS_ROL.ROLEID = FUBON_ROL.ROLE_ID ");
			sb.append("      LEFT JOIN TBORG_DEFN DEPT_DTL ON 1=1 ");
			sb.append("      LEFT JOIN (SELECT TO_CHAR(LAST_DAY(ADD_MONTHS(ADD_MONTHS(TRUNC(SYSDATE,'yyyy'),13)-1/24/60/60,-LEVEL)), 'yyyyMM') AS YR FROM DUAL CONNECT BY LEVEL<=12) YEAR_LIST ON 1 = 1 ");
			sb.append("      WHERE SYS_ROL.PRIVILEGEID IN ('012') ");
			sb.append("      AND DEPT_DTL.ORG_TYPE = '40' ");
			sb.append("    ) PIVOT (SUM(LEVING_COUNT) FOR YR IN ('01' AS JAN_CNT, '02' AS FEB_CNT, '03' AS MAR_CNT, '04' AS APR_CNT, '05' AS MAY_CNT, '06' AS JUN_CNT, '07' AS JUL_CNT, '08' AS AUG_CNT, '09' AS SEP_CNT, '10' AS OCT_CNT, '11' AS NOV_CNT, '12' AS DEC_CNT)) ");
			sb.append("  ) MON_COUNT ON DEPT.BRANCH_AREA_ID = MON_COUNT.BRANCH_AREA_ID AND SYS_ROL.ROLEID = MON_COUNT.ROLEID ");
			sb.append("  LEFT JOIN ( ");
			sb.append("    SELECT DEPT_ID, ROLE_ID, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(MN_LEAVING) AS MN_LEAVING ");
			sb.append("    FROM (SELECT MEM.DEPT_ID, MEM.EMP_ID, MROLE.ROLE_ID, ");
			sb.append("                 CASE WHEN (MEM.JOB_RESIGN_DATE IS NULL OR TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') > TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS SERVING, "); // --至系統日止仍在職(不含當日離職)
			sb.append("                 CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyy') = TO_CHAR(SYSDATE, 'yyyy') AND TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS LEAVING, "); // --至系統日止預計離職(含當日離職)
			sb.append("                 CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyy') = TO_CHAR(SYSDATE, 'yyyy') AND TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') <= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS YR_LEAVING, "); // --當年度至系統日止離職(含當日離職)小計
			sb.append("                 CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMM') = TO_CHAR(SYSDATE, 'yyyyMM') AND TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') <= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS MN_LEAVING "); // --當月至系統日止離職(含當日離職)數
			sb.append("          FROM TBORG_MEMBER MEM, TBORG_MEMBER_ROLE MROLE ");
			sb.append("          WHERE MEM.DEPT_ID IS NOT NULL ");
			sb.append("          AND MEM.EMP_ID = MROLE.EMP_ID ");
			sb.append("          AND MROLE.IS_PRIMARY_ROLE = 'Y' ");
			sb.append("          AND MROLE.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('012'))) ARRANGE ");
			sb.append("    GROUP BY DEPT_ID, ROLE_ID ");
			sb.append("  ) NOW_STATUS ON DEPT.BRANCH_AREA_ID = NOW_STATUS.DEPT_ID AND SYS_ROL.ROLEID = NOW_STATUS.ROLE_ID ");
			sb.append("  WHERE SYS_ROL.PRIVILEGEID IN ('012') ");
		}
		
		sb.append(") ");
		
		if ("JOB".equals(inputVO.getRPT_TYPE())) {
			sb.append("SELECT * ");
			sb.append("FROM ( ");
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
				sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ROLEID, ROLE_NAME, ");
				sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
				sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
				sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
				sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
				sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
				sb.append("FROM DTL ");
				
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
					sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
					queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
				} else {
					sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
					queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
					sb.append("AND BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else {
					sb.append("AND BRANCH_AREA_ID IN (:branchAreaIDList) ");
					queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
					sb.append("AND BRANCH_NBR = :branchID "); //分行代碼
					queryCondition.setObject("branchID", inputVO.getBranch_nbr());
				} else {
					sb.append("AND BRANCH_NBR IN (:branchIDList) ");
					queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				
				sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ROLEID, ROLE_NAME ");

				sb.append("UNION ");

				sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, '' AS ROLEID, '' AS ROLE_NAME, ");
				sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
				sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
				sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
				sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
				sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
				sb.append("FROM DTL ");
				
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
					sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
					queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
				} else {
					sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
					queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
					sb.append("AND BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else {
					sb.append("AND BRANCH_AREA_ID IN (:branchAreaIDList) ");
					queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
					sb.append("AND BRANCH_NBR = :branchID "); //分行代碼
					queryCondition.setObject("branchID", inputVO.getBranch_nbr());
				} else {
					sb.append("AND BRANCH_NBR IN (:branchIDList) ");
					queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				
				sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME ");
				
			} else if ((StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) && StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ROLEID, ROLE_NAME, ");
				sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(SEP_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
				sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
				sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
				sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
				sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
				sb.append("FROM DTL ");
				
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
					sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
					queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
				} else {
					sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
					queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
					sb.append("AND BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else {
					sb.append("AND BRANCH_AREA_ID IN (:branchAreaIDList) ");
					queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
					sb.append("AND BRANCH_NBR = :branchID "); //分行代碼
					queryCondition.setObject("branchID", inputVO.getBranch_nbr());
				} else {
					sb.append("AND BRANCH_NBR IN (:branchIDList) ");
					queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				
				sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, ROLEID, ROLE_NAME ");
				
				sb.append("UNION ");

				sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS ROLE_ID, '' AS ROLE_NAME, ");
				sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
				sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
				sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
				sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
				sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
				sb.append("FROM DTL ");
				
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
					sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
					queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
				} else {
					sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
					queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
					sb.append("AND BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else {
					sb.append("AND BRANCH_AREA_ID IN (:branchAreaIDList) ");
					queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
					sb.append("AND BRANCH_NBR = :branchID "); //分行代碼
					queryCondition.setObject("branchID", inputVO.getBranch_nbr());
				} else {
					sb.append("AND BRANCH_NBR IN (:branchIDList) ");
					queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				
				sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME ");
				
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ROLEID, ROLE_NAME, ");
				sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
				sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
				sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
				sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
				sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
				sb.append("FROM DTL ");
				
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
					sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
					queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
				} else {
					sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
					queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
					sb.append("AND BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else {
					sb.append("AND BRANCH_AREA_ID IN (:branchAreaIDList) ");
					queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
					sb.append("AND BRANCH_NBR = :branchID "); //分行代碼
					queryCondition.setObject("branchID", inputVO.getBranch_nbr());
				} else {
					sb.append("AND BRANCH_NBR IN (:branchIDList) ");
					queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				
				sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, ROLEID, ROLE_NAME ");
				
				sb.append("UNION ");

				sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS ROLEID, '' AS ROLE_NAME, ");
				sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
				sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
				sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
				sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
				sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
				sb.append("FROM DTL ");
				
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
					sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
					queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
				} else {
					sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
					queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
					sb.append("AND BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
					queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
				} else {
					sb.append("AND BRANCH_AREA_ID IN (:branchAreaIDList) ");
					queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
					sb.append("AND BRANCH_NBR = :branchID "); //分行代碼
					queryCondition.setObject("branchID", inputVO.getBranch_nbr());
				} else {
					sb.append("AND BRANCH_NBR IN (:branchIDList) ");
					queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
				
				sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME ");
			} else {
				sb.append("SELECT '' AS REGION_CENTER_ID, '' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ROLEID, ROLE_NAME, ");
				sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
				sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
				sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
				sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
				sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
				sb.append("FROM DTL ");
				sb.append("GROUP BY ROLEID, ROLE_NAME ");
				
				sb.append("UNION ");

				sb.append("SELECT '' AS REGION_CENTER_ID, '' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS ROLEID, '' AS ROLE_NAME, ");
				sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
				sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
				sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
				sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
				sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
				sb.append("FROM DTL ");
			}
			sb.append(") ");
			sb.append("ORDER BY DECODE(ROLEID, '003', 1, '001', 2, '002', 3, '004', 4, '009', 5, '011', 6, '012', 7, '005', 8, '006', 9, 99), ROLE_NAME ");
		
			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			outputVO.setEmpLeftJobLst(list);
			outputVO.setReportLst(list);
		} else {
			//業務處合計
			sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME || '合計' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS ROLE_NAME, ");
			sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
			sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
			sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
			sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
			sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
			sb.append("FROM DTL ");
			sb.append("WHERE 1 = 1 ");
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
				sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			} else {
				sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
			sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME || '合計' ");
			
			sb.append("UNION ");
			
			//營運區合計
			sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || '合計' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS ROLE_NAME, ");
			sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
			sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
			sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
			sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
			sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
			sb.append("FROM DTL ");
			sb.append("WHERE 1 = 1 ");
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
				sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			} else {
				sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
				sb.append("AND BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else {
				sb.append("AND BRANCH_AREA_ID IN (:branchAreaIDList) ");
				queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
			sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || '合計' ");
			
			sb.append("UNION ");
			
			//分行合計
			sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, '' AS ROLE_NAME, ");
			sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
			sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
			sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
			sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
			sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
			sb.append("FROM DTL ");
			sb.append("WHERE 1 = 1 ");
			sb.append("AND BRANCH_NBR IS NOT NULL ");
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
				sb.append("AND REGION_CENTER_ID = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			} else {
				sb.append("AND REGION_CENTER_ID IN (:regionCenterIDList) ");
				queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
				sb.append("AND BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else {
				sb.append("AND BRANCH_AREA_ID IN (:branchAreaIDList) ");
				queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			}
		
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
				sb.append("AND BRANCH_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch_nbr());
			} else {
				sb.append("AND BRANCH_NBR IN (:branchIDList) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME ");
			
			sb.append("UNION ");
			
			//全行合計
			sb.append("SELECT '' AS REGION_CENTER_ID, '全行 合計' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS ROLE_NAME, ");
			sb.append("       SUM(JAN_CNT) AS JAN_CNT, SUM(FEB_CNT) AS FEB_CNT, SUM(MAR_CNT) AS MAR_CNT, SUM(APR_CNT) AS APR_CNT, SUM(MAY_CNT) AS MAY_CNT, SUM(JUN_CNT) AS JUN_CNT, ");
			sb.append("       SUM(JUL_CNT) AS JUL_CNT, SUM(AUG_CNT) AS AUG_CNT, SUM(SEP_CNT) AS SEP_CNT, SUM(OCT_CNT) AS OCT_CNT, SUM(NOV_CNT) AS NOV_CNT, SUM(DEC_CNT) AS DEC_CNT, ");
			sb.append("       SUM(MN_LEAVING) AS MN_LEAVING, SUM(YR_LEAVING) AS YR_LEAVING, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING, ");
			sb.append("       TRUNC(DECODE((NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)), 0, 0, NVL(SUM(YR_LEAVING), 0) / (NVL(SUM(SERVING), 0) + NVL(SUM(YR_LEAVING), 0)) * 100), 2) AS RATE_LEAVING, ");
			sb.append("       SUM(JAN_CNT) + SUM(FEB_CNT) + SUM(MAR_CNT) + SUM(APR_CNT) + SUM(MAY_CNT) + SUM(JUN_CNT) + SUM(JUL_CNT) + SUM(AUG_CNT) + SUM(SEP_CNT) + SUM(OCT_CNT) + SUM(NOV_CNT) + SUM(DEC_CNT) AS TOTAL_LEAVING ");
			sb.append("FROM DTL ");
			sb.append("GROUP BY '', '全行 合計' ");
			
			sb.append("ORDER BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME ");
			
			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);		
			List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
			
			ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
			for (Map<String, Object> centerMap : list) {
				String regionCenter = (String) centerMap.get("REGION_CENTER_NAME");
				if (centerTempList.indexOf(regionCenter) < 0) { //regionCenter
					centerTempList.add(regionCenter);
					
					Integer centerRowspan = 1;
					
					List<Map<String, Object>> branchAreaList = new ArrayList<Map<String,Object>>();
					ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
					for (Map<String, Object> branchAreaMap : list) {
						String branchArea = (String) branchAreaMap.get("BRANCH_AREA_NAME");
						
						Integer branchAreaRowspan = 1;
						
						//==== 營運區
						if (regionCenter.equals((String) branchAreaMap.get("REGION_CENTER_NAME"))) { 
							if (branchAreaTempList.indexOf(branchArea) < 0) { //branchArea
								branchAreaTempList.add(branchArea);
								
								//==== 分行
								List<Map<String, Object>> branchList = new ArrayList<Map<String,Object>>();
								ArrayList<String> branchTempList = new ArrayList<String>(); //比對用
								ArrayList<String> centerCountTempList = new ArrayList<String>(); //比對用
								for (Map<String, Object> branchMap : list) {
									String branch = (String) branchMap.get("BRANCH_NAME");
									
									if (branchArea != null && branchMap.get("BRANCH_AREA_NAME") != null) {
										if (branchArea.equals((String) branchMap.get("BRANCH_AREA_NAME"))) {
											if (branchTempList.indexOf(branch) < 0) { //branchArea
												branchTempList.add(branch);
												
												//==== 詳細資訊
												List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
												ArrayList<String> roleTempList = new ArrayList<String>(); //比對用
												for (Map<String, Object> roleMap : list) {
													if (branch != null && roleMap.get("BRANCH_NAME") != null) {
														if (branch.equals((String) roleMap.get("BRANCH_NAME"))) {
															Map<String, Object> roleTempMap = new HashMap<String, Object>();
															
															roleTempMap.put("JAN_CNT", (BigDecimal) roleMap.get("JAN_CNT"));
															roleTempMap.put("FEB_CNT", (BigDecimal) roleMap.get("FEB_CNT"));
															roleTempMap.put("MAR_CNT", (BigDecimal) roleMap.get("MAR_CNT"));
															roleTempMap.put("APR_CNT", (BigDecimal) roleMap.get("APR_CNT"));
															roleTempMap.put("MAY_CNT", (BigDecimal) roleMap.get("MAY_CNT"));
															roleTempMap.put("JUN_CNT", (BigDecimal) roleMap.get("JUN_CNT"));
															roleTempMap.put("JUL_CNT", (BigDecimal) roleMap.get("JUL_CNT"));
															roleTempMap.put("AUG_CNT", (BigDecimal) roleMap.get("AUG_CNT"));
															roleTempMap.put("SEP_CNT", (BigDecimal) roleMap.get("SEP_CNT"));
															roleTempMap.put("OCT_CNT", (BigDecimal) roleMap.get("OCT_CNT"));
															roleTempMap.put("NOV_CNT", (BigDecimal) roleMap.get("NOV_CNT"));
															roleTempMap.put("DEC_CNT", (BigDecimal) roleMap.get("DEC_CNT"));
															roleTempMap.put("MN_LEAVING", (BigDecimal) roleMap.get("MN_LEAVING"));
															roleTempMap.put("YR_LEAVING", (BigDecimal) roleMap.get("YR_LEAVING"));
															roleTempMap.put("SERVING", (BigDecimal) roleMap.get("SERVING"));
															roleTempMap.put("RATE_LEAVING", (BigDecimal) roleMap.get("RATE_LEAVING"));
															roleTempMap.put("LEAVING", (BigDecimal) roleMap.get("LEAVING"));
															roleTempMap.put("TOTAL_LEAVING", (BigDecimal) roleMap.get("TOTAL_LEAVING"));

															roleList.add(roleTempMap);
														}
														
													} else if (branchArea.equals(roleMap.get("BRANCH_AREA_NAME"))) {
														Map<String, Object> roleTempMap = new HashMap<String, Object>();

														roleTempMap.put("JAN_CNT", (BigDecimal) roleMap.get("JAN_CNT"));
														roleTempMap.put("FEB_CNT", (BigDecimal) roleMap.get("FEB_CNT"));
														roleTempMap.put("MAR_CNT", (BigDecimal) roleMap.get("MAR_CNT"));
														roleTempMap.put("APR_CNT", (BigDecimal) roleMap.get("APR_CNT"));
														roleTempMap.put("MAY_CNT", (BigDecimal) roleMap.get("MAY_CNT"));
														roleTempMap.put("JUN_CNT", (BigDecimal) roleMap.get("JUN_CNT"));
														roleTempMap.put("JUL_CNT", (BigDecimal) roleMap.get("JUL_CNT"));
														roleTempMap.put("AUG_CNT", (BigDecimal) roleMap.get("AUG_CNT"));
														roleTempMap.put("SEP_CNT", (BigDecimal) roleMap.get("SEP_CNT"));
														roleTempMap.put("OCT_CNT", (BigDecimal) roleMap.get("OCT_CNT"));
														roleTempMap.put("NOV_CNT", (BigDecimal) roleMap.get("NOV_CNT"));
														roleTempMap.put("DEC_CNT", (BigDecimal) roleMap.get("DEC_CNT"));
														roleTempMap.put("MN_LEAVING", (BigDecimal) roleMap.get("MN_LEAVING"));
														roleTempMap.put("YR_LEAVING", (BigDecimal) roleMap.get("YR_LEAVING"));
														roleTempMap.put("SERVING", (BigDecimal) roleMap.get("SERVING"));
														roleTempMap.put("RATE_LEAVING", (BigDecimal) roleMap.get("RATE_LEAVING"));
														roleTempMap.put("LEAVING", (BigDecimal) roleMap.get("LEAVING"));
														roleTempMap.put("TOTAL_LEAVING", (BigDecimal) roleMap.get("TOTAL_LEAVING"));
														
														roleList.add(roleTempMap);
													}
												}

												Map<String, Object> branchTempMap = new HashMap<String, Object>();
												branchTempMap.put("BRANCH_NBR", (String) branchMap.get("BRANCH_NBR"));
												branchTempMap.put("BRANCH_NAME", branch);
												branchTempMap.put("ROLE", roleList);
												centerRowspan = centerRowspan + roleList.size();
												branchAreaRowspan = branchAreaRowspan + roleList.size();
												branchTempMap.put("ROWSPAN", roleList.size());
												
												branchList.add(branchTempMap);
											}
										}
									} else if (regionCenter.equals(branchMap.get("REGION_CENTER_NAME"))) {
										String centerCount = (String) branchMap.get("REGION_CENTER_NAME");
										if (centerCountTempList.indexOf(centerCount) < 0) { //regionCenter
											
											centerCountTempList.add(centerCount);
											
											//==== 詳細資訊
											List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
											ArrayList<String> roleTempList = new ArrayList<String>(); //比對用
											for (Map<String, Object> roleMap : list) {
												if (regionCenter.equals(roleMap.get("REGION_CENTER_NAME"))) {
													Map<String, Object> roleTempMap = new HashMap<String, Object>();

													roleTempMap.put("JAN_CNT", (BigDecimal) roleMap.get("JAN_CNT"));
													roleTempMap.put("FEB_CNT", (BigDecimal) roleMap.get("FEB_CNT"));
													roleTempMap.put("MAR_CNT", (BigDecimal) roleMap.get("MAR_CNT"));
													roleTempMap.put("APR_CNT", (BigDecimal) roleMap.get("APR_CNT"));
													roleTempMap.put("MAY_CNT", (BigDecimal) roleMap.get("MAY_CNT"));
													roleTempMap.put("JUN_CNT", (BigDecimal) roleMap.get("JUN_CNT"));
													roleTempMap.put("JUL_CNT", (BigDecimal) roleMap.get("JUL_CNT"));
													roleTempMap.put("AUG_CNT", (BigDecimal) roleMap.get("AUG_CNT"));
													roleTempMap.put("SEP_CNT", (BigDecimal) roleMap.get("SEP_CNT"));
													roleTempMap.put("OCT_CNT", (BigDecimal) roleMap.get("OCT_CNT"));
													roleTempMap.put("NOV_CNT", (BigDecimal) roleMap.get("NOV_CNT"));
													roleTempMap.put("DEC_CNT", (BigDecimal) roleMap.get("DEC_CNT"));
													roleTempMap.put("MN_LEAVING", (BigDecimal) roleMap.get("MN_LEAVING"));
													roleTempMap.put("YR_LEAVING", (BigDecimal) roleMap.get("YR_LEAVING"));
													roleTempMap.put("SERVING", (BigDecimal) roleMap.get("SERVING"));
													roleTempMap.put("RATE_LEAVING", (BigDecimal) roleMap.get("RATE_LEAVING"));
													roleTempMap.put("LEAVING", (BigDecimal) roleMap.get("LEAVING"));
													roleTempMap.put("TOTAL_LEAVING", (BigDecimal) roleMap.get("TOTAL_LEAVING"));
													
													roleList.add(roleTempMap);
												}
											}
											
											Map<String, Object> branchTempMap = new HashMap<String, Object>();
											branchTempMap.put("BRANCH_NBR", (String) branchMap.get("BRANCH_NBR"));
											branchTempMap.put("BRANCH_NAME", branch);
											branchTempMap.put("ROLE", roleList);
											centerRowspan = centerRowspan + roleList.size();
											branchAreaRowspan = branchAreaRowspan + roleList.size();
											branchTempMap.put("ROWSPAN", roleList.size());
											
											branchList.add(branchTempMap);
										}
									}
								}
								
								Map<String, Object> branchAreaTempMap = new HashMap<String, Object>();
								branchAreaTempMap.put("BRANCH_AREA_NAME", branchArea);
								branchAreaTempMap.put("BRANCH", branchList);
								centerRowspan = centerRowspan + branchList.size();
								branchAreaRowspan = branchAreaRowspan + branchList.size();
								branchAreaTempMap.put("ROWSPAN", branchAreaRowspan);
								branchAreaTempMap.put("COLSPAN", (branchList.size() == 1 && branchList.get(0).get("BRANCH_NAME") == null ? 3: 1));

								branchAreaList.add(branchAreaTempMap);
							}
						}
					}
					
					Map<String, Object> centerTempMap = new HashMap<String, Object>();
					centerTempMap.put("REGION_CENTER_NAME", regionCenter);
					centerTempMap.put("BRANCH_AREA", branchAreaList);
					centerRowspan = centerRowspan + branchAreaList.size();
					centerTempMap.put("ROWSPAN", centerRowspan);
					centerTempMap.put("COLSPAN", (branchAreaList.size() == 1 && branchAreaList.get(0).get("BRANCH_AREA_NAME") == null ? 4: 1));

					returnList.add(centerTempMap);
				}
			}
			
			outputVO.setEmpLeftJobLst(returnList);
			outputVO.setReportLst(list);
		}

		Calendar calender = Calendar.getInstance();
		outputVO.setToDay(sdfYYYYMMDD.format(new Date()));
		
		sendRtnObject(outputVO);
	}
	
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		ORG420InputVO inputVO = (ORG420InputVO) body;
		
		Calendar calender = Calendar.getInstance();
		//"JOB".equals(inputVO.getRPT_TYPE())
		if (StringUtils.equals("JOB", inputVO.getRPT_TYPE())) {
			List<Map<String, String>> list = inputVO.getEXPORT_LST();
			
			String fileName = "分行離職率統計表(各職務統計表)_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
			String uuid = UUID.randomUUID().toString();
			String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			
			String filePath = Path + uuid;
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("分行離職率統計表(各職務統計表)_" + sdfYYYYMMDD.format(new Date()));
			sheet.setDefaultColumnWidth(20);
			sheet.setDefaultRowHeightInPoints(20);
			
			// 表頭 CELL型式
			XSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
			headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headingStyle.setBorderBottom((short) 1);
			headingStyle.setBorderTop((short) 1);
			headingStyle.setBorderLeft((short) 1);
			headingStyle.setBorderRight((short) 1);
			headingStyle.setWrapText(true);
			
			String[] headerLine1 = {"職務",  
									"1月份", "2月份", "3月份", "4月份", "5月份", "6月份", "7月份", "8月份", "9月份", "10月份", "11月份", "12月份", 
									"MTD截至" + sdfYYYYMMDD.format(new Date()) + "離職人數", 
									"YTD截至" + sdfYYYYMMDD.format(new Date()) + "離職人數", 
									"截至" + sdfYYYYMMDD.format(new Date()) + "實際人數", 
									"YTD離職率", 
									"預計離職人數", 
									"總計"};
			String[] mainLine    = {"ROLE_NAME", 
									"JAN_CNT", "FEB_CNT", "MAR_CNT", "APR_CNT", "MAY_CNT", "JUN_CNT", "JUL_CNT", "AUG_CNT", "SEP_CNT", "OCT_CNT", "NOV_CNT", "DEC_CNT", 
									"MN_LEAVING", 
									"YR_LEAVING", 
									"SERVING",
									"RATE_LEAVING",
									"LEAVING", "TOTAL_LEAVING"};
		
			Integer index = 0; // first row
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); //比對用
			
			XSSFRow row = sheet.createRow(index);
			for (int i = 0; i < headerLine1.length; i++) {
				String headerLine = headerLine1[i];
				if (tempList.indexOf(headerLine) < 0) {
					tempList.add(headerLine);
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(headerLine1[i]);

					if (endFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
					}
					startFlag = i;
					endFlag = 0;
				} else {
					endFlag = i;
				}
			}
			if (endFlag != 0) { //最後的CELL若需要合併儲存格，則在這裡做
				sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
			}
			
			index++;
			
			// 資料 CELL型式
			XSSFCellStyle mainStyle = workbook.createCellStyle();
			mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			mainStyle.setBorderBottom((short) 1);
			mainStyle.setBorderTop((short) 1);
			mainStyle.setBorderLeft((short) 1);
			mainStyle.setBorderRight((short) 1);

			Map<String, String> refProdMap = new XmlInfo().doGetVariable("CAM.REF_PROD", FormatHelper.FORMAT_3);

			for (Map<String, String> map : list) {
				row = sheet.createRow(index);

				if (map.size() > 0) {
					for (int j = 0; j < mainLine.length; j++) {
						XSSFCell cell = row.createCell(j);
						cell.setCellStyle(mainStyle);
						if (StringUtils.equals("ROLE_NAME", mainLine[j]) && StringUtils.isBlank(checkIsNull(map, mainLine[j]))) {
							cell.setCellValue("小計");
						} else {
							cell.setCellValue(checkIsNull(map, mainLine[j]));
						}
					}

					index++;
				}
			}
			
			workbook.write(new FileOutputStream(filePath));
			notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		} else {
			String[] headerLine1 = {"業務處", "營運區", "分行別", "分行名稱", 
									"1月份", "2月份", "3月份", "4月份", "5月份", "6月份", "7月份", "8月份", "9月份", "10月份", "11月份", "12月份", 
									"MTD截至" + sdfYYYYMMDD.format(new Date()) + "離職人數", 
									"YTD截至" + sdfYYYYMMDD.format(new Date()) + "離職人數", 
									"截至" + sdfYYYYMMDD.format(new Date()) + "實際人數", 
									"YTD離職率", 
									"預計離職人數", 
									"總計"};
			String[] mainLine    = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", 
									"JAN_CNT", "FEB_CNT", "MAR_CNT", "APR_CNT", "MAY_CNT", "JUN_CNT", "JUL_CNT", "AUG_CNT", "SEP_CNT", "OCT_CNT", "NOV_CNT", "DEC_CNT", 
									"MN_LEAVING", 
									"YR_LEAVING", 
									"SERVING",
									"RATE_LEAVING",
									"LEAVING", "TOTAL_LEAVING"};
					
			String fileName = "分行離職率統計表(全區域統計表)_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
			String uuid = UUID.randomUUID().toString();
			String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			
			String filePath = Path + uuid;
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("分行離職率統計表(全區域統計表)_" + sdfYYYYMMDD.format(new Date()));
			sheet.setDefaultColumnWidth(20);
			sheet.setDefaultRowHeightInPoints(20);
			
			// 表頭 CELL型式
			XSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
			headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headingStyle.setBorderBottom((short) 1);
			headingStyle.setBorderTop((short) 1);
			headingStyle.setBorderLeft((short) 1);
			headingStyle.setBorderRight((short) 1);
			headingStyle.setWrapText(true);
			
			XSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
			style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
			
			Integer index = 0; // first row
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); //比對用
			
			XSSFRow row = sheet.createRow(index);
			for (int i = 0; i < headerLine1.length; i++) {
				String headerLine = headerLine1[i];
				if (tempList.indexOf(headerLine) < 0) {
					tempList.add(headerLine);
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(headingStyle);
					cell.setCellValue(headerLine1[i]);
			
					if (endFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
					}
					startFlag = i;
					endFlag = 0;
				} else {
					endFlag = i;
				}
			}
			if (endFlag != 0) { //最後的CELL若需要合併儲存格，則在這裡做
				sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
			}
			
			index++;
			ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
			ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
			ArrayList<String> branchTempList = new ArrayList<String>(); //比對用
			Integer centerStartFlag = 0, branchAreaStartFlag = 0, branchStartFlag = 0;
			Integer centerEndFlag = 0, branchAreaEndFlag = 0, branchEndFlag = 0;
			
			Integer contectStartIndex = index;
			
			List<Map<String, String>> list = inputVO.getEXPORT_LST();
			for (int i = 0; i < list.size(); i++) {
				row = sheet.createRow(index);
			
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(style);
			
					String centerName = list.get(i).get("REGION_CENTER_NAME");
					String branchAreaName = list.get(i).get("BRANCH_AREA_NAME");
					String branchName = list.get(i).get("BRANCH_NAME");
			
					if (j == 0 && centerTempList.indexOf(centerName) < 0) {
						centerTempList.add(centerName);
	
						if (centerEndFlag != 0) {
							if (null != centerName && centerName.indexOf("合計") > 0) {
								sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
								sheet.addMergedRegion(new CellRangeAddress(i + 1, i + 1, j, j + 3)); // firstRow, endRow, firstColumn, endColumn
							}
						}
						
						if (StringUtils.isBlank(centerName)) {
							sheet.addMergedRegion(new CellRangeAddress(i, i, j, j + 3)); // firstRow, endRow, firstColumn, endColumn
						}
						centerStartFlag = i;
						centerEndFlag = 0;
					} else if (j == 0 && null != list.get(i).get("REGION_CENTER_NAME")) {
						centerEndFlag = i;
					}
			
					if (j == 1 && StringUtils.isNotBlank(branchAreaName) && branchAreaTempList.indexOf(branchAreaName) < 0) {
						branchAreaTempList.add(branchAreaName);
	
						if (branchAreaName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(i + 1, i + 1, j, j + 3)); // firstRow, endRow, firstColumn, endColumn
							
							if (branchAreaEndFlag != 0) {
								sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							}
						}
						branchAreaStartFlag = i;
						branchAreaEndFlag = 0;
					} else if (j == 1 && StringUtils.isNotBlank(branchAreaName)) {
						branchAreaEndFlag = i;
					}
			
					if (j == 2 && branchTempList.indexOf(branchName) < 0) {
						branchTempList.add(branchName);
				
						if (branchEndFlag != 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j + 1, j + 1)); // firstRow, endRow, firstColumn, endColumn
						}
						branchStartFlag = i;
						branchEndFlag = 0;
					} else if (j == 2 && null != list.get(i).get("BRANCH_NAME")) {
						branchEndFlag = i; 
					}
					
					if (j == 2 && i == (list.size() - 1)) { //最後一筆
						if (branchEndFlag != 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j + 1, j + 1)); // firstRow, endRow, firstColumn, endColumn
						}
					} else if (j == 1 && i == (list.size() - 1)) {
						if (branchAreaEndFlag != 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j + 2)); // firstRow, endRow, firstColumn, endColumn
						}
					}
			
					if (null != list.get(i).get(mainLine[0]) && "ROLE_NAME".equals(mainLine[j]) && null == list.get(i).get(mainLine[j])) {
						cell.setCellValue("小計");
					} else {
						cell.setCellValue(list.get(i).get(mainLine[j]));
					}
				}
				
				index++;
			}
			
			workbook.write(new FileOutputStream(filePath));
			
			// download
			notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		}
		
		sendRtnObject(null);
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull (Map map, String key) {
		
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
