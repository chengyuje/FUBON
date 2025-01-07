package com.systex.jbranch.app.server.fps.org430;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.CellRangeAddress;
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
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org430")
@Scope("request")
public class ORG430 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	SimpleDateFormat sdfYYYY 	  = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdfYYYYMM    = new SimpleDateFormat("yyyy/MM");
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfYYYYMMDD2 = new SimpleDateFormat("yyyy/MM/dd");
	
	public void query(Object body, IPrimitiveMap header) throws Exception {
		
		ORG430InputVO inputVO = (ORG430InputVO) body;
		ORG430OutputVO outputVO = new ORG430OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//===取得理專員額表
		StringBuffer sb = new StringBuffer();
		sb.append("WITH DTL AS ( ");
		sb.append("  SELECT CENTER_ID, ");
		sb.append("         CENTER_NAME, ");
		sb.append("         BRANCH_AREA_ID, ");
		sb.append("         BRANCH_AREA_NAME, ");
		sb.append("         BRANCH_ID, ");
		sb.append("         BRANCH_NAME, ");
		sb.append("         ROLE_NAME, ");
		sb.append("         SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("         SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("         FC_COUNT, SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("         (NVL(FC_COUNT, 0) - NVL(SUM(SERVING_COUNT), 0)) AS SHORTFALL, ");
		sb.append("         SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("         SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("         SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("         SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("         SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("         SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("         SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("         (NVL(FC_COUNT, 0) - NVL(SUM(SERVING_COUNT), 0)) - (NVL(SUM(MON1_WAIT_IN), 0) + NVL(SUM(MON2_WAIT_IN), 0) + NVL(SUM(MON3_WAIT_IN), 0)) AS ESTIMATED ");
		sb.append("  FROM ( ");
		sb.append("    SELECT CENTER_ID, ");
		sb.append("           CENTER_NAME, ");
		sb.append("           BRANCH_AREA_ID, ");
		sb.append("           BRANCH_AREA_NAME, ");
		sb.append("           BRANCH_ID, ");
		sb.append("           BRANCH_NAME, ");
		sb.append("           ROLEID, ");
		sb.append("           ROLE_NAME, ");
		sb.append("           CASE WHEN ROLEID = 'FCH' THEN (SELECT COUNT(DISTINCT MEM.EMP_ID) ");
		sb.append("                                          FROM TBORG_MEMBER MEM ");
		sb.append("                                          LEFT JOIN TBORG_MEMBER_ROLE MROLE ON MEM.EMP_ID = MROLE.EMP_ID AND MROLE.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("                                          INNER JOIN TBSYSSECUROLPRIASS PRI ON MROLE.ROLE_ID = PRI.ROLEID AND PRI.PRIVILEGEID = '003' ");
		sb.append("                                          WHERE 1 = 1 ");
		sb.append("                                          AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("                                          AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("                                          AND MEM.DEPT_ID = BASE.BRANCH_ID ");
		sb.append("                                          AND TO_CHAR(MEM.PERF_EFF_DATE, 'yyyyMM') <= TO_CHAR(SYSDATE, 'yyyyMM')) ");
		sb.append("           ELSE (SELECT COUNT(MEM.EMP_ID) ");
		sb.append("                 FROM TBORG_MEMBER MEM ");
		sb.append("                 LEFT JOIN TBORG_MEMBER_ROLE MROLE ON MEM.EMP_ID = MROLE.EMP_ID AND MROLE.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("                 WHERE MROLE.ROLE_ID = BASE.ROLEID ");
		sb.append("                 AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("                 AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("                 AND MEM.DEPT_ID = BASE.BRANCH_ID ");
		sb.append("                 AND TO_CHAR(MEM.PERF_EFF_DATE, 'yyyyMM') <= TO_CHAR(SYSDATE, 'yyyyMM')) END AS GOAL_COUNT, ");
		sb.append("           NVL(NOW_STATUS.SERVING, 0) AS SERVING_COUNT, ");
		sb.append("           NVL(MQUOTA.FC_COUNT, 0) AS FC_COUNT, ");
		sb.append("           NVL(NOW_STATUS.LEAVING, 0) AS LEAVING_COUNT, ");
		sb.append("           NVL(ING_STATUS.WAIT_IN, 0) AS WAIT_IN, ");
		sb.append("           NVL(ING_STATUS.MON1_WAIT_IN, 0) AS MON1_WAIT_IN, ");
		sb.append("           NVL(ING_STATUS.MON2_WAIT_IN, 0) AS MON2_WAIT_IN, ");
		sb.append("           NVL(ING_STATUS.MON3_WAIT_IN, 0) AS MON3_WAIT_IN, ");
		sb.append("           NVL(ING_STATUS.CHK_PRICE_ING, 0) AS CHK_PRICE_ING, ");
		sb.append("           NVL(ING_STATUS.CHK_WAIT_MEET, 0) AS CHK_WAIT_MEET, ");
		sb.append("           NVL(ING_STATUS.CHK_PIP_REPORT, 0) AS CHK_PIP_REPORT ");
		sb.append("    FROM ( ");
		sb.append("      SELECT REGION_CENTER_ID AS CENTER_ID, ");
		sb.append("             REGION_CENTER_NAME AS CENTER_NAME, ");
		sb.append("             BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
		sb.append("             BRANCH_NBR AS BRANCH_ID, ");
		sb.append("             BRANCH_NAME, ");
		sb.append("             SYS_ROL.ROLEID, ");
		sb.append("             FUBON_ROL.ROLE_NAME ");
		sb.append("      FROM TBSYSSECUROLPRIASS SYS_ROL ");
		sb.append("      LEFT JOIN TBORG_ROLE FUBON_ROL ON SYS_ROL.ROLEID = FUBON_ROL.ROLE_ID ");
		sb.append("      LEFT JOIN VWORG_DEFN_INFO DEPT_DTL ON 1 = 1 ");
		sb.append("      WHERE PRIVILEGEID IN ('002', 'JRM') ");
		sb.append("    ) BASE ");
		sb.append("    LEFT JOIN (");
		sb.append("      SELECT DEPT_ID, ");
		sb.append("             CASE WHEN REPLACE(ROLE_TYPE, '_CNT') = 'OP' THEN '作業人員' ");
		sb.append("                  WHEN REPLACE(ROLE_TYPE, '_CNT') = 'OPH' THEN '作業主管' ");
		sb.append("                  WHEN REPLACE(ROLE_TYPE, '_CNT') = 'AO' THEN '新興AO' ");
		sb.append("                  WHEN REPLACE(ROLE_TYPE, '_CNT') = 'CAO' THEN '消金AO' ");//需新增角色後才可統計
		sb.append("             ELSE REPLACE(ROLE_TYPE, '_CNT') END AS ROLE_TYPE, ");
		sb.append("             FC_COUNT ");
		sb.append("      FROM TBORG_BRH_MBR_QUOTA ");
		sb.append("      UNPIVOT (FC_COUNT FOR ROLE_TYPE IN (FC1_CNT, FC2_CNT, FC3_CNT, FC4_CNT, FC5_CNT, FCH_CNT, OP_CNT, OPH_CNT, PS_CNT, AO_CNT, CAO_CNT, JRM_CNT)) ");
		sb.append("    ) MQUOTA ON BASE.ROLE_NAME = MQUOTA.ROLE_TYPE AND MQUOTA.DEPT_ID = BASE.BRANCH_ID ");
		sb.append("    LEFT JOIN (");
		sb.append("      SELECT DEPT_ID, CASE WHEN ROLE_NAME LIKE 'FCH%' THEN 'FCH' ELSE ROLE_ID END AS ROLE_ID, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING ");
		sb.append("      FROM (SELECT MEM.DEPT_ID, ");
		sb.append("                   MEM.EMP_ID, ");
		sb.append("                   CASE WHEN R.ROLE_NAME = 'AFC' THEN 'A145' ELSE MROLE.ROLE_ID END AS ROLE_ID, ");
		sb.append("                   CASE WHEN R.ROLE_NAME = 'AFC' THEN 'FC1' ELSE R.ROLE_NAME END AS ROLE_NAME, ");
		sb.append("                   CASE WHEN (MEM.JOB_RESIGN_DATE IS NULL OR TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') > TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS SERVING, ");
		sb.append("                   CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS LEAVING ");
		sb.append("            FROM TBORG_MEMBER MEM, TBORG_MEMBER_ROLE MROLE ");
		sb.append("            LEFT JOIN TBORG_ROLE R ON MROLE.ROLE_ID = R.ROLE_ID ");
		sb.append("            WHERE MEM.DEPT_ID IS NOT NULL ");
		sb.append("            AND MEM.EMP_ID = MROLE.EMP_ID ");
		sb.append("            AND MROLE.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("            AND MROLE.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('001', '002', '003', 'JRM')) ");
		sb.append("            AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("            AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("      ) ARRANGE ");
		sb.append("      GROUP BY DEPT_ID, CASE WHEN ROLE_NAME LIKE 'FCH%' THEN 'FCH' ELSE ROLE_ID END ");
		sb.append("    ) NOW_STATUS ON BASE.BRANCH_ID = NOW_STATUS.DEPT_ID AND BASE.ROLEID = NOW_STATUS.ROLE_ID ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT BRANCH_NBR, ");
		sb.append("             AO_JOB_RANK, ");
		sb.append("             SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("             SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("             SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("             SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("             SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("             SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("             SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT ");
		sb.append("      FROM ( ");
		sb.append("        SELECT CUST_ID, BRANCH_NBR, AO_JOB_RANK, ");
		sb.append("               CASE WHEN STATUS = '07' THEN 1 ELSE 0 END AS WAIT_IN, ");
		sb.append("               CASE WHEN (STATUS = '07' AND TO_CHAR(BOOKED_ONBOARD_DATE, 'yyyyMM') = TO_CHAR(SYSDATE, 'yyyyMM') AND TO_CHAR(BOOKED_ONBOARD_DATE, 'yyyyMMdd') > TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS MON1_WAIT_IN, ");
		sb.append("               CASE WHEN (STATUS = '07' AND TO_CHAR(BOOKED_ONBOARD_DATE, 'yyyyMM') = TO_CHAR(ADD_MONTHS(SYSDATE, 1), 'yyyyMM')) THEN 1 ELSE 0 END AS MON2_WAIT_IN, ");
		sb.append("               CASE WHEN (STATUS = '07' AND TO_CHAR(BOOKED_ONBOARD_DATE, 'yyyyMM') = TO_CHAR(ADD_MONTHS(SYSDATE, 2), 'yyyyMM')) THEN 1 ELSE 0 END AS MON3_WAIT_IN, ");
		sb.append("               CASE WHEN STATUS IN ('04', '05', '06') THEN 1 ELSE 0 END AS CHK_PRICE_ING, ");
		sb.append("               CASE WHEN STATUS IN ('01', '02', '03', '11') THEN 1 ELSE 0 END AS CHK_WAIT_MEET, ");
		sb.append("               CASE WHEN STATUS IN ('01', '02', '03', '11') THEN 1 ELSE 0 END AS CHK_PIP_REPORT ");
		sb.append("        FROM TBORG_EMP_HIRE_INFO ");
		sb.append("        WHERE AO_JOB_RANK IN (SELECT ROLE_NAME FROM TBSYSSECUROLPRIASS PRIASS, TBORG_ROLE ORG_ROLE WHERE PRIASS.ROLEID = ORG_ROLE.ROLE_ID AND PRIVILEGEID IN ('002', '003', 'JRM')) ");
		sb.append("      ) HIRE_INFO ");
		sb.append("      GROUP BY BRANCH_NBR, AO_JOB_RANK ");
		sb.append("    ) ING_STATUS ON BASE.BRANCH_ID = ING_STATUS.BRANCH_NBR AND BASE.ROLE_NAME = ING_STATUS.AO_JOB_RANK ");
		sb.append("  ) ");
		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, ROLE_NAME, FC_COUNT ");
		sb.append(") ");

		//業務處小計
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME || ' 合計' AS CENTER_NAME, ");
		sb.append("       '' AS BRANCH_AREA_ID, ");
		sb.append("       '' AS BRANCH_AREA_NAME, ");
		sb.append("       '' AS BRANCH_ID, ");
		sb.append("       '' AS BRANCH_NAME, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(FC_COUNT) AS FC_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("       SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("       SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("       SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("       SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME || ' 合計', ROLE_NAME ");
		
		sb.append("UNION ");
		
		// 業務處合計
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME || ' 合計' AS CENTER_NAME, ");
		sb.append("       '' AS BRANCH_AREA_ID, ");
		sb.append("       '' AS BRANCH_AREA_NAME, ");
		sb.append("       '' AS BRANCH_ID, ");
		sb.append("       '' AS BRANCH_NAME, ");
		sb.append("       '' AS ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(FC_COUNT) AS FC_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(SHORTFALL) AS SHORTFALL, "); 
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("       SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("       SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("       SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("       SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME || ' 合計' ");
		
		sb.append("UNION ");
		
		//營運中心小計
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, ");
		sb.append("       BRANCH_AREA_NAME || ' 合計' AS BRANCH_AREA_NAME, ");
		sb.append("       '' AS BRANCH_ID, ");
		sb.append("       '' AS BRANCH_NAME, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(FC_COUNT) AS FC_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("       SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("       SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("       SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("       SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND DTL.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
		 
		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計', ROLE_NAME ");
		
		sb.append("UNION ");
		
		//營運區合計
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, ");
		sb.append("       BRANCH_AREA_NAME || ' 合計' AS BRANCH_AREA_NAME, ");
		sb.append("       '' AS BRANCH_ID, ");
		sb.append("       '' AS BRANCH_NAME, ");
		sb.append("       '' AS ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(FC_COUNT) AS FC_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("       SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("       SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("       SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("       SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND DTL.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計' ");
		
		sb.append("UNION ");
		
		//分行小計
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, ");
		sb.append("       BRANCH_AREA_NAME, ");
		sb.append("       BRANCH_ID, ");
		sb.append("       BRANCH_NAME, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(FC_COUNT) AS FC_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("       SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("       SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("       SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("       SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND DTL.BRANCH_ID = :branchID "); //分行代碼
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND DTL.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.BRANCH_ID IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, ROLE_NAME ");
		
		sb.append("UNION ");
		
		//分行合計
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, ");
		sb.append("       BRANCH_AREA_NAME, ");
		sb.append("       BRANCH_ID, BRANCH_NAME, ");
		sb.append("       '' AS ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(FC_COUNT) AS FC_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("       SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("       SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("       SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("       SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND DTL.BRANCH_ID = :branchID "); //分行代碼
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND DTL.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND DTL.CENTER_ID = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND DTL.BRANCH_ID IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME ");
		
		sb.append("UNION ");
		
		//全行小計
		sb.append("SELECT '' AS CENTER_ID, ");
		sb.append("       '全行 合計' AS CENTER_NAME, ");
		sb.append("       '' AS BRANCH_AREA_ID, ");
		sb.append("       '' AS BRANCH_AREA_NAME, ");
		sb.append("       '' AS BRANCH_ID, ");
		sb.append("       '' AS BRANCH_NAME, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(FC_COUNT) AS FC_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("       SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("       SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("       SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("       SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("GROUP BY '', '全行 合計', ROLE_NAME ");
		
		sb.append("UNION ");
		
		//全行合計
		sb.append("SELECT '' AS CENTER_ID, ");
		sb.append("       '全行 合計' AS CENTER_NAME, ");
		sb.append("       '' AS BRANCH_AREA_ID, ");
		sb.append("       '' AS BRANCH_AREA_NAME, ");
		sb.append("       '' AS BRANCH_ID, ");
		sb.append("       '' AS BRANCH_NAME, ");
		sb.append("       '' AS ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(FC_COUNT) AS FC_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(SHORTFALL) AS SHORTFALL, ");
		sb.append("       SUM(WAIT_IN) AS WAIT_IN, ");
		sb.append("       SUM(MON1_WAIT_IN) AS MON1_WAIT_IN, ");
		sb.append("       SUM(MON2_WAIT_IN) AS MON2_WAIT_IN, ");
		sb.append("       SUM(MON3_WAIT_IN) AS MON3_WAIT_IN, ");
		sb.append("       SUM(CHK_PRICE_ING) AS CHK_PRICE_ING, ");
		sb.append("       SUM(CHK_WAIT_MEET) AS CHK_WAIT_MEET, ");
		sb.append("       SUM(CHK_PIP_REPORT) AS CHK_PIP_REPORT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("GROUP BY '', '全行 合計' ");
		
		sb.append("ORDER BY CENTER_ID ASC, BRANCH_AREA_ID ASC, BRANCH_ID ASC, ROLE_NAME ASC ");
				       
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);		
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
		for (Map<String, Object> centerMap : list) {
			String regionCenter = (String) centerMap.get("CENTER_NAME");
			if (centerTempList.indexOf(regionCenter) < 0) { //regionCenter
				centerTempList.add(regionCenter);
				
				Integer centerRowspan = 1;
				
				List<Map<String, Object>> branchAreaList = new ArrayList<Map<String,Object>>();
				ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
				for (Map<String, Object> branchAreaMap : list) {
					String branchArea = (String) branchAreaMap.get("BRANCH_AREA_NAME");
					
					Integer branchAreaRowspan = 1;
					
					//==== 營運區
					if (regionCenter.equals((String) branchAreaMap.get("CENTER_NAME"))) { 
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
												String role = (String) roleMap.get("ROLE_NAME");
												
												if (branch != null && roleMap.get("BRANCH_NAME") != null) {
													if (branch.equals((String) roleMap.get("BRANCH_NAME"))) {
														if (roleTempList.indexOf(role) < 0) { //branchArea
															roleTempList.add(role);
															
															Map<String, Object> roleTempMap = new HashMap<String, Object>();
															roleTempMap.put("ROLE_NAME", role);
															roleTempMap.put("GOAL_COUNT", (BigDecimal) roleMap.get("GOAL_COUNT"));
															roleTempMap.put("SERVING_COUNT", (BigDecimal) roleMap.get("SERVING_COUNT"));
															roleTempMap.put("FC_COUNT", (BigDecimal) roleMap.get("FC_COUNT"));
															roleTempMap.put("LEAVING_COUNT", (BigDecimal) roleMap.get("LEAVING_COUNT"));
															roleTempMap.put("SHORTFALL", (BigDecimal) roleMap.get("SHORTFALL"));
															roleTempMap.put("WAIT_IN", (BigDecimal) roleMap.get("WAIT_IN"));
															roleTempMap.put("MON1_WAIT_IN", (BigDecimal) roleMap.get("MON1_WAIT_IN"));
															roleTempMap.put("MON2_WAIT_IN", (BigDecimal) roleMap.get("MON2_WAIT_IN"));
															roleTempMap.put("MON3_WAIT_IN", (BigDecimal) roleMap.get("MON3_WAIT_IN"));
															roleTempMap.put("CHK_PRICE_ING", (BigDecimal) roleMap.get("CHK_PRICE_ING"));
															roleTempMap.put("CHK_WAIT_MEET", (BigDecimal) roleMap.get("CHK_WAIT_MEET"));
															roleTempMap.put("CHK_PIP_REPORT", (BigDecimal) roleMap.get("CHK_PIP_REPORT"));
															roleTempMap.put("ESTIMATED", (BigDecimal) roleMap.get("ESTIMATED"));
															
															roleList.add(roleTempMap);
														}
													}
												} else if (branchArea.equals(roleMap.get("BRANCH_AREA_NAME"))) {
													Map<String, Object> roleTempMap = new HashMap<String, Object>();
													roleTempMap.put("ROLE_NAME", role);
													roleTempMap.put("GOAL_COUNT", (BigDecimal) roleMap.get("GOAL_COUNT"));
													roleTempMap.put("SERVING_COUNT", (BigDecimal) roleMap.get("SERVING_COUNT"));
													roleTempMap.put("FC_COUNT", (BigDecimal) roleMap.get("FC_COUNT"));
													roleTempMap.put("LEAVING_COUNT", (BigDecimal) roleMap.get("LEAVING_COUNT"));
													roleTempMap.put("SHORTFALL", (BigDecimal) roleMap.get("SHORTFALL"));
													roleTempMap.put("WAIT_IN", (BigDecimal) roleMap.get("WAIT_IN"));
													roleTempMap.put("MON1_WAIT_IN", (BigDecimal) roleMap.get("MON1_WAIT_IN"));
													roleTempMap.put("MON2_WAIT_IN", (BigDecimal) roleMap.get("MON2_WAIT_IN"));
													roleTempMap.put("MON3_WAIT_IN", (BigDecimal) roleMap.get("MON3_WAIT_IN"));
													roleTempMap.put("CHK_PRICE_ING", (BigDecimal) roleMap.get("CHK_PRICE_ING"));
													roleTempMap.put("CHK_WAIT_MEET", (BigDecimal) roleMap.get("CHK_WAIT_MEET"));
													roleTempMap.put("CHK_PIP_REPORT", (BigDecimal) roleMap.get("CHK_PIP_REPORT"));
													roleTempMap.put("ESTIMATED", (BigDecimal) roleMap.get("ESTIMATED"));
													
													roleList.add(roleTempMap);
												}
											}
											
											Map<String, Object> branchTempMap = new HashMap<String, Object>();
											branchTempMap.put("BRANCH_ID", (String) branchMap.get("BRANCH_ID"));
											branchTempMap.put("BRANCH_NAME", branch);
											branchTempMap.put("ROLE", roleList);
											centerRowspan = centerRowspan + roleList.size();
											branchAreaRowspan = branchAreaRowspan + roleList.size();
											branchTempMap.put("ROWSPAN", roleList.size());
											
											branchList.add(branchTempMap);
										}
									}
								} else if (regionCenter.equals(branchMap.get("CENTER_NAME"))) {
									String centerCount = (String) branchMap.get("CENTER_NAME");
									if (centerCountTempList.indexOf(centerCount) < 0) { //regionCenter
										
										centerCountTempList.add(centerCount);
										
										//==== 詳細資訊
										List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
										ArrayList<String> roleTempList = new ArrayList<String>(); //比對用
										for (Map<String, Object> roleMap : list) {
											String role = (String) roleMap.get("ROLE_NAME");
											
											if (regionCenter.equals(roleMap.get("CENTER_NAME"))) {
												if (roleTempList.indexOf(role) < 0) { //branchArea
													roleTempList.add(role);
													
													Map<String, Object> roleTempMap = new HashMap<String, Object>();
													roleTempMap.put("ROLE_NAME", role);
													roleTempMap.put("GOAL_COUNT", (BigDecimal) roleMap.get("GOAL_COUNT"));
													roleTempMap.put("SERVING_COUNT", (BigDecimal) roleMap.get("SERVING_COUNT"));
													roleTempMap.put("FC_COUNT", (BigDecimal) roleMap.get("FC_COUNT"));
													roleTempMap.put("LEAVING_COUNT", (BigDecimal) roleMap.get("LEAVING_COUNT"));
													roleTempMap.put("SHORTFALL", (BigDecimal) roleMap.get("SHORTFALL"));
													roleTempMap.put("WAIT_IN", (BigDecimal) roleMap.get("WAIT_IN"));
													roleTempMap.put("MON1_WAIT_IN", (BigDecimal) roleMap.get("MON1_WAIT_IN"));
													roleTempMap.put("MON2_WAIT_IN", (BigDecimal) roleMap.get("MON2_WAIT_IN"));
													roleTempMap.put("MON3_WAIT_IN", (BigDecimal) roleMap.get("MON3_WAIT_IN"));
													roleTempMap.put("CHK_PRICE_ING", (BigDecimal) roleMap.get("CHK_PRICE_ING"));
													roleTempMap.put("CHK_WAIT_MEET", (BigDecimal) roleMap.get("CHK_WAIT_MEET"));
													roleTempMap.put("CHK_PIP_REPORT", (BigDecimal) roleMap.get("CHK_PIP_REPORT"));
													roleTempMap.put("ESTIMATED", (BigDecimal) roleMap.get("ESTIMATED"));
													
													roleList.add(roleTempMap);
												}
											}
										}
										
										Map<String, Object> branchTempMap = new HashMap<String, Object>();
										branchTempMap.put("BRANCH_ID", (String) branchMap.get("BRANCH_ID"));
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
				centerTempMap.put("CENTER_NAME", regionCenter);
				centerTempMap.put("BRANCH_AREA", branchAreaList);
				centerRowspan = centerRowspan + branchAreaList.size();
				centerTempMap.put("ROWSPAN", centerRowspan);
				centerTempMap.put("COLSPAN", (branchAreaList.size() == 1 && branchAreaList.get(0).get("BRANCH_AREA_NAME") == null ? 4: 1));

				returnList.add(centerTempMap);
			}
		}
		
		outputVO.setAoCntLst(returnList);
		outputVO.setReportLst(list);

		Calendar calender = Calendar.getInstance();
		outputVO.setYear(sdfYYYY.format(new Date()));
		outputVO.setToDay(sdfYYYYMMDD2.format(new Date()));
		outputVO.setNowMonth(sdfYYYYMM.format(new Date()));
		calender.setTime(new Date());
		calender.add(Calendar.MONTH, 1);
		outputVO.setNextMonth(sdfYYYYMM.format(calender.getTime()));
		calender.add(Calendar.MONTH, 1);
		outputVO.setNext2Month(sdfYYYYMM.format(calender.getTime()));

		sendRtnObject(outputVO);
	}
	
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		ORG430InputVO inputVO = (ORG430InputVO) body;
		ORG430OutputVO outputVO = new ORG430OutputVO();
		
		String[] headerLine1 = {"業務處", "營運區", "分行別", "分別名稱", "職務", inputVO.getToDay() + "掛Goal人數", 
								inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額", 
								"在途", "在途", "在途", "在途", 
								 "預估實際缺額數(加入" + inputVO.getNowMonth() + "/" + inputVO.getNextMonth() + "/" + inputVO.getNext2Month() + "月待報到人數)"};
		
		String[] headerLine2 = {"", "", "", "", "", "", 
							  	"截至" + inputVO.getToDay() + "實際數", "員額", "預計離職人數", "缺額", 
							  	"待報到總數", "核薪中", "待面試", "人員進用pipline report", 
							 	""}; // "待報到_" + inputVO.getNowMonth(), "待報到_" + inputVO.getNextMonth(), "待報到_" + inputVO.getNext2Month(), 
		String[] mainLine    = {"CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_ID", "BRANCH_NAME", "ROLE_NAME", "GOAL_COUNT",
								"SERVING_COUNT", "FC_COUNT", "LEAVING_COUNT", "SHORTFALL", 
								"WAIT_IN", "CHK_PRICE_ING", "CHK_WAIT_MEET", "CHK_PIP_REPORT", 
								"ESTIMATED"}; //"MON1_WAIT_IN", "MON2_WAIT_IN", "MON3_WAIT_IN", 
		
		String fileName = "理專員額表_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("理專員額表_" + sdfYYYYMMDD.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		
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
				cell.setCellStyle(style);
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
		
		index++; //next row
		row = sheet.createRow(index);
		for (int i = 0; i < headerLine2.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(headerLine2[i]);
			
			if ("".equals(headerLine2[i])) {
				sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
			}
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
				
				String centerName = list.get(i).get("CENTER_NAME");
				String branchAreaName = list.get(i).get("BRANCH_AREA_NAME");
				String branchName = list.get(i).get("BRANCH_NAME");
				
				if (j == 0 && centerTempList.indexOf(centerName) < 0) {
					centerTempList.add(centerName);
					if (centerEndFlag != 0) {
						if (null != centerName && centerName.indexOf("合計") > 0) {
							if (StringUtils.equals("全行 合計", centerName)) {
								sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j + 3)); // firstRow, endRow, firstColumn, endColumn
							} else {
								sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							}
						} else {
							sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j + 3)); // firstRow, endRow, firstColumn, endColumn
						}
					}
					
					centerStartFlag = i;
					centerEndFlag = 0;
				} else if (j == 0 && null != list.get(i).get("CENTER_NAME")) {
					centerEndFlag = i;
				}
				
				if (j == 1 && branchAreaTempList.indexOf(branchAreaName) < 0) {
					branchAreaTempList.add(branchAreaName);
					
					if (branchAreaEndFlag != 0) {
						if (null != branchAreaName && branchAreaName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
						} else {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j + 2)); // firstRow, endRow, firstColumn, endColumn
						}
					}
					branchAreaStartFlag = i;
					branchAreaEndFlag = 0;
				} else if (j == 1 && null != list.get(i).get("BRANCH_AREA_NAME")) {
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
				} else if (j == 0 && i == (list.size() - 1)) {
					if (centerEndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j + 3)); // firstRow, endRow, firstColumn, endColumn
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
		
		outputVO.setAoCntLst(inputVO.getEXPORT_LST());
		
		sendRtnObject(outputVO);
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull (Map map, String key) {
		
		if (StringUtils.isNotBlank((String) map.get(key))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
