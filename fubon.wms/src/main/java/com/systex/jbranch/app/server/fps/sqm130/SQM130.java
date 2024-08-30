package com.systex.jbranch.app.server.fps.sqm130;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Description : 滿意度問卷統計
 * Author : Willis
 * Date : 2018/05/07
 * Modifier : 2021/03/04 BY OCEAN => #0535: WMS-CR-20210115-01_擬新增即時滿意度淨推薦值問項
 */

@Component("sqm130")
@Scope("request")
public class SQM130 extends FubonWmsBizLogic {

	private DataAccessManager dam;

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		SQM130InputVO inputVO = (SQM130InputVO) body;
		SQM130OutputVO outputVO = new SQM130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("WITH RPT_CNT AS ( ");
		sql.append("    SELECT  ORG.REGION_CENTER_ID, ");
		sql.append("            ORG.BRANCH_AREA_ID, ");
		sql.append("            ORG.BRANCH_NBR, ");
		sql.append("            (CASE WHEN CNT.REPORT_TYPE = '3' THEN EMP.EMP_ID || '-' || EMP.EMP_NAME ELSE CNT.STATISTICS_TYPE END ) STATISTICS_TYPE, ");
		sql.append("            CNT.REPORT_TYPE, ");
		sql.append("            SUM(CNT.VS_CNT) VS_CNT, ");
		sql.append("            SUM(CNT.S_CNT) S_CNT, ");
		sql.append("            SUM(CNT.OS_CNT) OS_CNT, ");
		sql.append("            SUM(CNT.NS_CNT) NS_CNT, ");
		sql.append("            SUM(CNT.VD_CNT) VD_CNT, ");
		sql.append("            SUM(CNT.NC_CNT) NC_CNT, ");
		sql.append("            SUM(CNT.TOT_CNT) TOT_CNT, ");
		sql.append("            ORG.REGION_CENTER_NAME, ");
		sql.append("            ORG.BRANCH_AREA_NAME, ");
		sql.append("            ORG.BRANCH_NAME ");
		sql.append("    FROM TBSQM_CSM_STATISTICS_RPT CNT ");
		sql.append("    LEFT JOIN TBPMS_ORG_REC_N ORG on ORG.dept_id = CNT.branch_nbr and to_date(CNT.YEARMON, 'yyyymm') between ORG.START_TIME and ORG.END_TIME ");
		sql.append("    LEFT JOIN TBORG_BRH_CONTACT B ON CNT.BRANCH_NBR = B.BRH_COD ");
		sql.append("    LEFT JOIN TBORG_MEMBER EMP ON CNT.STATISTICS_TYPE = EMP.EMP_ID ");
		sql.append("    WHERE YEARMON >= :yearMonS ");
		
		if (StringUtils.isNotBlank(inputVO.getTradeDateYE() + inputVO.getTradeDateME())) {
			sql.append("    AND YEARMON <= :yearMonE ");
		}

		if (StringUtils.isNotBlank(inputVO.getRegionCenterId())) {
			sql.append("    AND CNT.REGION_CENTER_ID = :regionId ");
			condition.setObject("regionId", inputVO.getRegionCenterId());
		}

		if (StringUtils.isNotBlank(inputVO.getBranchAreaId())) {
			sql.append("    AND CNT.BRANCH_AREA_ID = :branchAreaId ");
			condition.setObject("branchAreaId", inputVO.getBranchAreaId());
		}

		if (StringUtils.isNotBlank(inputVO.getBranchNbr())) {
			sql.append("    AND CNT.BRANCH_NBR =:branchNbr ");
			condition.setObject("branchNbr", inputVO.getBranchNbr());
		}

		if (StringUtils.isNotBlank(inputVO.getReportType())) {
			sql.append("    AND CNT.REPORT_TYPE = :reportType ");
			condition.setObject("reportType", inputVO.getReportType());
		}

		// 報表類型為員編時
		if (StringUtils.isNotBlank(inputVO.getEmpId()) && inputVO.getReportType().equals("3")) {
			sql.append("    AND CNT.STATISTICS_TYPE =:empId ");
			condition.setObject("empId", inputVO.getEmpId());
		}

		sql.append("    GROUP BY ORG.REGION_CENTER_ID, ORG.BRANCH_AREA_ID, ORG.BRANCH_NBR, CNT.STATISTICS_TYPE, CNT.REPORT_TYPE, ORG.REGION_CENTER_NAME, ORG.BRANCH_AREA_NAME, ORG.BRANCH_NAME, EMP.EMP_NAME, EMP.EMP_ID ");
		sql.append(") ");

		sql.append("SELECT R_CNT.*, ");
		sql.append("     BRANCH_CNT.TOT_CNT as BRANCH_CNT_TOT, ");
		sql.append("     BRANCH_AREA_CNT.VS_CNT  AS BRANCH_AREA_VS_CNT, ");
		sql.append("     BRANCH_AREA_CNT.S_CNT   AS BRANCH_AREA_S_CNT, ");
		sql.append("     BRANCH_AREA_CNT.OS_CNT  AS BRANCH_AREA_OS_CNT, ");
		sql.append("     BRANCH_AREA_CNT.NS_CNT  AS BRANCH_AREA_NS_CNT, ");
		sql.append("     BRANCH_AREA_CNT.VD_CNT  AS BRANCH_AREA_VD_CNT, ");
		sql.append("     BRANCH_AREA_CNT.NC_CNT  AS BRANCH_AREA_NC_CNT, ");
		sql.append("     BRANCH_AREA_CNT.TOT_CNT AS BRANCH_AREA_TOT_CNT, ");
		sql.append("     REGION.VS_CNT   AS REGION_VS_CNT, ");
		sql.append("     REGION.S_CNT    AS REGION_S_CNT,  ");
		sql.append("     REGION.OS_CNT   AS REGION_OS_CNT, ");
		sql.append("     REGION.NS_CNT   AS REGION_NS_CNT, ");
		sql.append("     REGION.VD_CNT   AS REGION_VD_CNT, ");
		sql.append("     REGION.NC_CNT   AS REGION_NC_CNT, ");
		sql.append("     REGION.TOT_CNT  AS REGION_TOT_CNT, ");
		sql.append("     TOTAL.VS_CNT   AS TOTAL_VS_CNT, ");
		sql.append("     TOTAL.S_CNT    AS TOTAL_S_CNT,  ");
		sql.append("     TOTAL.OS_CNT   AS TOTAL_OS_CNT, ");
		sql.append("     TOTAL.NS_CNT   AS TOTAL_NS_CNT, ");
		sql.append("     TOTAL.VD_CNT   AS TOTAL_VD_CNT, ");
		sql.append("     TOTAL.NC_CNT   AS TOTAL_NC_CNT, ");
		sql.append("     TOTAL.TOT_CNT  AS TOTAL_TOT_CNT, ");
		sql.append("     CASE WHEN NVL(BRANCH_CNT.TOT_CNT, 0) = 0 THEN 0 ELSE round(R_CNT.VS_CNT/BRANCH_CNT.TOT_CNT*100, 2) END VS_PC, ");
		sql.append("     CASE WHEN NVL(BRANCH_CNT.TOT_CNT, 0) = 0 THEN 0 ELSE round(R_CNT.S_CNT/BRANCH_CNT.TOT_CNT*100, 2)  END S_PC,  ");
		sql.append("     CASE WHEN NVL(BRANCH_CNT.TOT_CNT, 0) = 0 THEN 0 ELSE round(R_CNT.OS_CNT/BRANCH_CNT.TOT_CNT*100, 2) END OS_PC, ");
		sql.append("     CASE WHEN NVL(BRANCH_CNT.TOT_CNT, 0) = 0 THEN 0 ELSE round(R_CNT.NS_CNT/BRANCH_CNT.TOT_CNT*100, 2) END NS_PC, ");
		sql.append("     CASE WHEN NVL(BRANCH_CNT.TOT_CNT, 0) = 0 THEN 0 ELSE round(R_CNT.VD_CNT/BRANCH_CNT.TOT_CNT*100, 2) END VD_PC, ");
		sql.append("     CASE WHEN NVL(BRANCH_CNT.TOT_CNT, 0) = 0 THEN 0 ELSE round(R_CNT.NC_CNT/BRANCH_CNT.TOT_CNT*100, 2) END NC_PC, ");
		sql.append("     CASE WHEN NVL(BRANCH_CNT.TOT_CNT, 0) = 0 THEN 0 ELSE round(R_CNT.TOT_CNT/BRANCH_CNT.TOT_CNT*100, 2) END TOT_PC ");
		sql.append("FROM RPT_CNT R_CNT ");
		  
		// #5577 計算分行合計
		sql.append("LEFT JOIN ( ");
		sql.append("  select BRANCH_NBR, sum(TOT_CNT) AS TOT_CNT ");
		sql.append("  from RPT_CNT ");
		sql.append("  group by BRANCH_NBR ");
		sql.append(") BRANCH_CNT ON R_CNT.BRANCH_NBR = BRANCH_CNT.BRANCH_NBR ");

		// #5577 計算區合計
		sql.append("LEFT JOIN ( ");
		sql.append("  select ORG.BRANCH_AREA_ID, ");
		sql.append("         SUM(VS_CNT)  AS VS_CNT, ");
		sql.append("         SUM(S_CNT)   AS S_CNT,  ");
		sql.append("         SUM(OS_CNT)  AS OS_CNT, ");
		sql.append("         SUM(NS_CNT)  AS NS_CNT, ");
		sql.append("         SUM(VD_CNT)  AS VD_CNT, ");
		sql.append("         SUM(NC_CNT)  AS NC_CNT, ");
		sql.append("         SUM(TOT_CNT) AS TOT_CNT ");
		sql.append("  from TBSQM_CSM_STATISTICS_RPT B ");
		sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORG on ORG.dept_id = B.branch_nbr and to_date(B.YEARMON, 'yyyymm') between ORG.START_TIME and ORG.END_TIME ");
		sql.append("  WHERE B.YEARMON >= :yearMonS ");

		if (StringUtils.isNotBlank(inputVO.getTradeDateYE() + inputVO.getTradeDateME())) {
			sql.append("  AND B.YEARMON <= :yearMonE ");
		}

		if (StringUtils.isNotBlank(inputVO.getReportType())) {
			sql.append("  AND B.REPORT_TYPE = :reportType ");
		}

		sql.append("  group by ORG.BRANCH_AREA_ID ");
		sql.append(") BRANCH_AREA_CNT ON R_CNT.BRANCH_AREA_ID = BRANCH_AREA_CNT.BRANCH_AREA_ID ");

		// #5577 計算處合計
		sql.append("LEFT JOIN ( ");
		sql.append("  select ORG.REGION_CENTER_ID, ");
		sql.append("         SUM(VS_CNT)  AS VS_CNT, ");
		sql.append("         SUM(S_CNT)   AS S_CNT,  ");
		sql.append("         SUM(OS_CNT)  AS OS_CNT, ");
		sql.append("         SUM(NS_CNT)  AS NS_CNT, ");
		sql.append("         SUM(VD_CNT)  AS VD_CNT, ");
		sql.append("         SUM(NC_CNT)  AS NC_CNT, ");
		sql.append("         SUM(TOT_CNT) AS TOT_CNT ");
		sql.append("  from TBSQM_CSM_STATISTICS_RPT C ");
		sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORG on ORG.dept_id = C.branch_nbr and to_date(C.YEARMON, 'yyyymm') between ORG.START_TIME and ORG.END_TIME ");
		sql.append("  WHERE C.YEARMON >= :yearMonS ");

		if (StringUtils.isNotBlank(inputVO.getTradeDateYE() + inputVO.getTradeDateME())) {
			sql.append("  AND C.YEARMON <= :yearMonE ");
		}

		if (StringUtils.isNotBlank(inputVO.getReportType())) {
			sql.append("  AND C.REPORT_TYPE = :reportType ");
		}

		sql.append("  group by ORG.REGION_CENTER_ID ");
		sql.append(") REGION ON R_CNT.REGION_CENTER_ID = REGION.REGION_CENTER_ID ");

		// #5577 計算全行合計
		sql.append("LEFT JOIN ( ");
		sql.append("  select SUM(VS_CNT)  AS VS_CNT, ");
		sql.append("         SUM(S_CNT)   AS S_CNT,  ");
		sql.append("         SUM(OS_CNT)  AS OS_CNT, ");
		sql.append("         SUM(NS_CNT)  AS NS_CNT, ");
		sql.append("         SUM(VD_CNT)  AS VD_CNT, ");
		sql.append("         SUM(NC_CNT)  AS NC_CNT, ");
		sql.append("         SUM(TOT_CNT) AS TOT_CNT ");
		sql.append("  from TBSQM_CSM_STATISTICS_RPT D ");
		sql.append("  WHERE D.YEARMON >= :yearMonS ");
		
		if (StringUtils.isNotBlank(inputVO.getTradeDateYE() + inputVO.getTradeDateME())) {
			sql.append("  AND D.YEARMON <= :yearMonE ");
		}

		if (StringUtils.isNotBlank(inputVO.getReportType())) {
			sql.append("  AND D.REPORT_TYPE =:reportType ");
		}
		sql.append(") TOTAL ON 1=1 ");

		sql.append("ORDER BY R_CNT.REGION_CENTER_ID, R_CNT.BRANCH_AREA_ID, R_CNT.BRANCH_NBR, R_CNT.STATISTICS_TYPE ");


		condition.setObject("yearMonS", inputVO.getTradeDateYS() + inputVO.getTradeDateMS());
		
		if (StringUtils.isNotBlank(inputVO.getTradeDateYE() + inputVO.getTradeDateME())) {
			condition.setObject("yearMonE", inputVO.getTradeDateYE() + inputVO.getTradeDateME());
		}
		
		if (StringUtils.isNotBlank(inputVO.getReportType())) {
			condition.setObject("reportType", inputVO.getReportType());
		}
		
		condition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		int totalPage = list.getTotalPage();

		outputVO.setReportType(inputVO.getReportType());
		outputVO.setTotalPage(totalPage);
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		sendRtnObject(outputVO);
	}

	public void queryData(QueryConditionIF condition, SQM130InputVO inputVO) throws JBranchException, ParseException {
		
		StringBuffer sql = new StringBuffer();
		sql.append("WITH RPT_CNT AS ( ");
		sql.append("  SELECT  ORG.REGION_CENTER_ID, ");
		sql.append("          ORG.BRANCH_AREA_ID, ");
		sql.append("          ORG.BRANCH_NBR, ");
		sql.append("          (CASE WHEN CNT.REPORT_TYPE = '3' THEN EMP.EMP_ID || '-' || EMP.EMP_NAME ELSE CNT.STATISTICS_TYPE END ) STATISTICS_TYPE, ");
		sql.append("          CNT.REPORT_TYPE, ");
		sql.append("          SUM(CNT.VS_CNT) VS_CNT, ");
		sql.append("          SUM(CNT.S_CNT) S_CNT, ");
		sql.append("          SUM(CNT.OS_CNT) OS_CNT, ");
		sql.append("          SUM(CNT.NS_CNT) NS_CNT, ");
		sql.append("          SUM(CNT.VD_CNT) VD_CNT, ");
		sql.append("          SUM(CNT.NC_CNT) NC_CNT, ");
		sql.append("          SUM(CNT.TOT_CNT) TOT_CNT, ");
		sql.append("          ORG.REGION_CENTER_NAME, ");
		sql.append("          ORG.BRANCH_AREA_NAME, ");
		sql.append("          ORG.BRANCH_NAME ");
		sql.append("  FROM TBSQM_CSM_STATISTICS_RPT CNT ");
		sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON ORG.DEPT_ID = CNT.BRANCH_NBR AND TO_DATE(CNT.YEARMON, 'yyyymm') >=  ORG.START_TIME AND TO_DATE(CNT.YEARMON, 'yyyymm') <= ORG.END_TIME ");
		sql.append("  LEFT JOIN TBORG_BRH_CONTACT B ON CNT.BRANCH_NBR = B.BRH_COD ");
		sql.append("  LEFT JOIN TBORG_MEMBER EMP ON CNT.STATISTICS_TYPE = EMP.EMP_ID ");
		sql.append("  WHERE YEARMON >= :yearMonS ");
		if (StringUtils.isNotBlank(inputVO.getTradeDateYE() + inputVO.getTradeDateME())) {
			sql.append("  AND YEARMON <= :yearMonE ");
		}

		if (StringUtils.isNotBlank(inputVO.getRegionCenterId())) {
			sql.append("  AND CNT.REGION_CENTER_ID = :regionId ");
			condition.setObject("regionId", inputVO.getRegionCenterId());
		}

		if (StringUtils.isNotBlank(inputVO.getBranchAreaId())) {
			sql.append("  AND CNT.BRANCH_AREA_ID = :branchAreaId ");
			condition.setObject("branchAreaId", inputVO.getBranchAreaId());
		}

		if (StringUtils.isNotBlank(inputVO.getBranchNbr())) {
			sql.append("  AND CNT.BRANCH_NBR =:branchNbr ");
			condition.setObject("branchNbr", inputVO.getBranchNbr());
		}

		if (StringUtils.isNotBlank(inputVO.getReportType())) {
			sql.append("  AND CNT.REPORT_TYPE = :reportType ");
			condition.setObject("reportType", inputVO.getReportType());
		}
		
		sql.append("  GROUP BY ORG.REGION_CENTER_ID, ORG.BRANCH_AREA_ID, ORG.BRANCH_NBR, CNT.STATISTICS_TYPE, CNT.REPORT_TYPE, ORG.REGION_CENTER_NAME, ORG.BRANCH_AREA_NAME, ORG.BRANCH_NAME, EMP.EMP_NAME, EMP.EMP_ID ");
		sql.append(") ");
		sql.append(", BASE AS ( ");
		sql.append("  SELECT BRA.REGION_CENTER_ID, BRA.REGION_CENTER_NAME, BRA.BRANCH_AREA_ID, BRA.BRANCH_AREA_NAME, BRA.BRANCH_NBR, BRA.BRANCH_NAME, ");
		sql.append("         BRA.STATISTICS_TYPE, BRA.REPORT_TYPE, ");
		sql.append("         CENTER_C.CENTER_COUNT, ");
		sql.append("         AREA_C.AREA_COUNT, ");
		sql.append("         BRA_C.BRA_COUNT, ");
		sql.append("         ALL_C.ALL_COUNT, ");
		sql.append("         NVL(SUM(BRA.VS_CNT), 0) AS VS_CNT, ");
		sql.append("         NVL(SUM(BRA.S_CNT), 0) AS S_CNT, ");
		sql.append("         NVL(SUM(BRA.OS_CNT), 0) AS OS_CNT, ");
		sql.append("         NVL(SUM(BRA.NS_CNT), 0) AS NS_CNT, ");
		sql.append("         NVL(SUM(BRA.VD_CNT), 0) AS VD_CNT, ");
		sql.append("         NVL(SUM(BRA.NC_CNT), 0) AS NC_CNT, ");
		sql.append("         NVL(SUM(BRA.TOT_CNT), 0) AS TOT_CNT ");
		sql.append("  FROM RPT_CNT BRA ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT BRANCH_NBR, NVL(SUM(TOT_CNT), 0) AS BRA_COUNT ");
		sql.append("    FROM RPT_CNT ");
		sql.append("    GROUP BY BRANCH_NBR ");
		sql.append("  ) BRA_C ON BRA.BRANCH_NBR = BRA_C.BRANCH_NBR ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT BRANCH_AREA_ID, NVL(SUM(TOT_CNT), 0) AS AREA_COUNT ");
		sql.append("    FROM RPT_CNT ");
		sql.append("    GROUP BY BRANCH_AREA_ID ");
		sql.append("  ) AREA_C ON BRA.BRANCH_AREA_ID = AREA_C.BRANCH_AREA_ID ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT REGION_CENTER_ID, NVL(SUM(TOT_CNT), 0) AS CENTER_COUNT ");
		sql.append("    FROM RPT_CNT ");
		sql.append("    GROUP BY REGION_CENTER_ID ");
		sql.append("  ) CENTER_C ON BRA.REGION_CENTER_ID = CENTER_C.REGION_CENTER_ID ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT NVL(SUM(TOT_CNT), 0) AS ALL_COUNT ");
		sql.append("    FROM RPT_CNT ");
		sql.append("  ) ALL_C ON 1 = 1 ");
		sql.append("  GROUP BY BRA.REGION_CENTER_NAME, ");
		sql.append("           BRA.BRANCH_AREA_NAME, ");
		sql.append("           BRA.BRANCH_NAME, ");
		sql.append("           BRA.REGION_CENTER_ID, ");
		sql.append("           BRA.BRANCH_AREA_ID, ");
		sql.append("           BRA.BRANCH_NBR, ");
		sql.append("           BRA.STATISTICS_TYPE, ");
		sql.append("           BRA.REPORT_TYPE, ");
		sql.append("           CENTER_C.CENTER_COUNT, ");
		sql.append("           AREA_C.AREA_COUNT, ");
		sql.append("           BRA_C.BRA_COUNT, ");
		sql.append("           ALL_C.ALL_COUNT ");
		sql.append(") ");

		sql.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sql.append("       STATISTICS_TYPE, REPORT_TYPE, ");
		sql.append("       CASE WHEN NVL(SUM(VS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VS_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS VS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(S_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(S_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS S_PC, ");
		sql.append("       CASE WHEN NVL(SUM(OS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(OS_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS OS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NS_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS NS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(VD_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VD_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS VD_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NC_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NC_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS NC_PC, ");
		sql.append("       CASE WHEN NVL(SUM(TOT_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(TOT_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS TOT_PC, ");
		sql.append("       NVL(SUM(VS_CNT), 0) AS VS_CNT, ");
		sql.append("       NVL(SUM(S_CNT), 0) AS S_CNT, ");
		sql.append("       NVL(SUM(OS_CNT), 0) AS OS_CNT, ");
		sql.append("       NVL(SUM(NS_CNT), 0) AS NS_CNT, ");
		sql.append("       NVL(SUM(VD_CNT), 0) AS VD_CNT, ");
		sql.append("       NVL(SUM(NC_CNT), 0) AS NC_CNT, ");
		sql.append("       NVL(SUM(TOT_CNT), 0) AS TOT_CNT ");
		sql.append("FROM BASE ");
		sql.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sql.append("         STATISTICS_TYPE, REPORT_TYPE, ");
		sql.append("         BRA_COUNT ");
		sql.append("UNION ");
		sql.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME || ' 合計' AS BRANCH_NAME, ");
		sql.append("       '' AS STATISTICS_TYPE, REPORT_TYPE, ");
		sql.append("       CASE WHEN NVL(SUM(VS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VS_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS VS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(S_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(S_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS S_PC, ");
		sql.append("       CASE WHEN NVL(SUM(OS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(OS_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS OS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NS_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS NS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(VD_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VD_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS VD_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NC_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NC_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS NC_PC, ");
		sql.append("       CASE WHEN NVL(SUM(TOT_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(TOT_CNT), 0) / BRA_COUNT * 100, 2) || '%' END AS TOT_PC, ");
		sql.append("       NVL(SUM(VS_CNT), 0) AS VS_CNT, ");
		sql.append("       NVL(SUM(S_CNT), 0) AS S_CNT, ");
		sql.append("       NVL(SUM(OS_CNT), 0) AS OS_CNT, ");
		sql.append("       NVL(SUM(NS_CNT), 0) AS NS_CNT, ");
		sql.append("       NVL(SUM(VD_CNT), 0) AS VD_CNT, ");
		sql.append("       NVL(SUM(NC_CNT), 0) AS NC_CNT, ");
		sql.append("       NVL(SUM(TOT_CNT), 0) AS TOT_CNT ");
		sql.append("FROM BASE ");
		sql.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME || ' 合計', ");
		sql.append("         '', REPORT_TYPE, ");
		sql.append("         BRA_COUNT ");
		sql.append("UNION ");
		sql.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		sql.append("       '' AS STATISTICS_TYPE, REPORT_TYPE, ");
		sql.append("       CASE WHEN NVL(SUM(VS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VS_CNT), 0) / AREA_COUNT * 100, 2) || '%' END AS VS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(S_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(S_CNT), 0) / AREA_COUNT * 100, 2) || '%' END AS S_PC, ");
		sql.append("       CASE WHEN NVL(SUM(OS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(OS_CNT), 0) / AREA_COUNT * 100, 2) || '%' END AS OS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NS_CNT), 0) / AREA_COUNT * 100, 2) || '%' END AS NS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(VD_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VD_CNT), 0) / AREA_COUNT * 100, 2) || '%' END AS VD_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NC_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NC_CNT), 0) / AREA_COUNT * 100, 2) || '%' END AS NC_PC, ");
		sql.append("       CASE WHEN NVL(SUM(TOT_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(TOT_CNT), 0) / AREA_COUNT * 100, 2) || '%' END AS TOT_PC, ");
		sql.append("       NVL(SUM(VS_CNT), 0) AS VS_CNT, ");
		sql.append("       NVL(SUM(S_CNT), 0) AS S_CNT, ");
		sql.append("       NVL(SUM(OS_CNT), 0) AS OS_CNT, ");
		sql.append("       NVL(SUM(NS_CNT), 0) AS NS_CNT, ");
		sql.append("       NVL(SUM(VD_CNT), 0) AS VD_CNT, ");
		sql.append("       NVL(SUM(NC_CNT), 0) AS NC_CNT, ");
		sql.append("       NVL(SUM(TOT_CNT), 0) AS TOT_CNT ");
		sql.append("FROM BASE ");
		sql.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計', '', '', ");
		sql.append("         '', REPORT_TYPE, ");
		sql.append("         AREA_COUNT ");
		sql.append("UNION ");
		sql.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME || ' 合計' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, ''AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		sql.append("       '' AS STATISTICS_TYPE, REPORT_TYPE, ");
		sql.append("       CASE WHEN NVL(SUM(VS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VS_CNT), 0) / CENTER_COUNT * 100, 2) || '%' END AS VS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(S_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(S_CNT), 0) / CENTER_COUNT * 100, 2) || '%' END AS S_PC, ");
		sql.append("       CASE WHEN NVL(SUM(OS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(OS_CNT), 0) / CENTER_COUNT * 100, 2) || '%' END AS OS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NS_CNT), 0) / CENTER_COUNT * 100, 2) || '%' END AS NS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(VD_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VD_CNT), 0) / CENTER_COUNT * 100, 2) || '%' END AS VD_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NC_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NC_CNT), 0) / CENTER_COUNT * 100, 2) || '%' END AS NC_PC, ");
		sql.append("       CASE WHEN NVL(SUM(TOT_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(TOT_CNT), 0) / CENTER_COUNT * 100, 2) || '%' END AS TOT_PC, ");
		sql.append("       NVL(SUM(VS_CNT), 0) AS VS_CNT, ");
		sql.append("       NVL(SUM(S_CNT), 0) AS S_CNT, ");
		sql.append("       NVL(SUM(OS_CNT), 0) AS OS_CNT, ");
		sql.append("       NVL(SUM(NS_CNT), 0) AS NS_CNT, ");
		sql.append("       NVL(SUM(VD_CNT), 0) AS VD_CNT, ");
		sql.append("       NVL(SUM(NC_CNT), 0) AS NC_CNT, ");
		sql.append("       NVL(SUM(TOT_CNT), 0) AS TOT_CNT ");
		sql.append("FROM BASE ");
		sql.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME || ' 合計', '', '', '', '', ");
		sql.append("         '', REPORT_TYPE, ");
		sql.append("         CENTER_COUNT ");
		sql.append("UNION ");
		sql.append("SELECT '全行合計' AS REGION_CENTER_ID, '全行合計' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, ''AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, ");
		sql.append("       '' AS STATISTICS_TYPE, REPORT_TYPE, ");
		sql.append("       CASE WHEN NVL(SUM(VS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VS_CNT), 0) / ALL_COUNT * 100, 2) || '%' END AS VS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(S_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(S_CNT), 0) / ALL_COUNT * 100, 2) || '%' END AS S_PC, ");
		sql.append("       CASE WHEN NVL(SUM(OS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(OS_CNT), 0) / ALL_COUNT * 100, 2) || '%' END AS OS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NS_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NS_CNT), 0) / ALL_COUNT * 100, 2) || '%' END AS NS_PC, ");
		sql.append("       CASE WHEN NVL(SUM(VD_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(VD_CNT), 0) / ALL_COUNT * 100, 2) || '%' END AS VD_PC, ");
		sql.append("       CASE WHEN NVL(SUM(NC_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(NC_CNT), 0) / ALL_COUNT * 100, 2) || '%' END AS NC_PC, ");
		sql.append("       CASE WHEN NVL(SUM(TOT_CNT), 0) = 0 THEN '–' ELSE ROUND(NVL(SUM(TOT_CNT), 0) / ALL_COUNT * 100, 2) || '%' END AS TOT_PC, ");
		sql.append("       NVL(SUM(VS_CNT), 0) AS VS_CNT, ");
		sql.append("       NVL(SUM(S_CNT), 0) AS S_CNT, ");
		sql.append("       NVL(SUM(OS_CNT), 0) AS OS_CNT, ");
		sql.append("       NVL(SUM(NS_CNT), 0) AS NS_CNT, ");
		sql.append("       NVL(SUM(VD_CNT), 0) AS VD_CNT, ");
		sql.append("       NVL(SUM(NC_CNT), 0) AS NC_CNT, ");
		sql.append("       NVL(SUM(TOT_CNT), 0) AS TOT_CNT ");
		sql.append("FROM BASE ");
		sql.append("GROUP BY '全行合計', '全行合計', '', '', '', '', ");
		sql.append("         '', REPORT_TYPE, ");
		sql.append("         ALL_COUNT ");
		sql.append("ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, STATISTICS_TYPE ");
		
		condition.setObject("yearMonS", inputVO.getTradeDateYS() + inputVO.getTradeDateMS());
		
		if (StringUtils.isNotBlank(inputVO.getTradeDateYE() + inputVO.getTradeDateME())) {
			condition.setObject("yearMonE", inputVO.getTradeDateYE() + inputVO.getTradeDateME());
		}
		
		if (StringUtils.isNotBlank(inputVO.getReportType())) {
			condition.setObject("reportType", inputVO.getReportType());
		}
		
		condition.setQueryString(sql.toString());
	}

	/* === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, IOException {
		
		SQM130InputVO inputVO = (SQM130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		this.queryData(condition, inputVO);

		List<Map<String, Object>> list = dam.exeQuery(condition);

		Map<String, String> typeList = new HashMap<String, String>();
		typeList.put("1", "貢獻度");
		typeList.put("2", "客群");
		typeList.put("3", "員編");
		typeList.put("4", "服務別");
		typeList.put("5", "問卷別");

		Map<String, String> order = new LinkedHashMap<String, String>();
		order.put("REGION_CENTER_NAME", "業務處");
		order.put("BRANCH_AREA_NAME", "營運區");
		order.put("BRANCH_NAME", "分行別");
		order.put("STATISTICS_TYPE", "類型-" + ObjectUtils.toString(typeList.get(inputVO.getReportType())));
		order.put("VS_PC", "非常滿意");
		order.put("S_PC", "滿意");
		order.put("OS_PC", "普通");
		order.put("NS_PC", "不滿意");
		order.put("VD_PC", "非常不滿意");
		order.put("NC_PC", "未聯繫");
		order.put("TOT_PC", "合計");
		order.put("VS_CNT", "非常滿意");
		order.put("S_CNT", "滿意");
		order.put("OS_CNT", "普通");
		order.put("NS_CNT", "不滿意");
		order.put("VD_CNT", "非常不滿意");
		order.put("NC_CNT", "未聯繫");
		order.put("TOT_CNT", "合計");

		String fileName = "滿意度問卷統計";

		Map<String, String> file = new HashMap<String, String>();
		file = this.exportxlsx_cname_2(fileName, list, order);
		
		this.sendRtnObject("downloadFile", file);
	}

	// 匯出EXCEL BY 排序 AND 欄位名稱設定
	public Map<String, String> exportxlsx_cname(String name, List<Map<String, Object>> list, Map<String, String> order) throws JBranchException, IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sheet");
		CellStyle cellStyleFont = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontName("微軟正黑體");
		cellStyleFont.setFont(font);

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

		// sheet = doMergeCell(sheet, list ,
		// "REGION_CENTER_NAME","BRANCH_AREA_NAME","BRANCH_NAME");
		CellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd h:mm:ss"));

		XmlInfo xmlinfo = new XmlInfo();
		Map<String, String> vipDegree = xmlinfo.doGetVariable("CRM.VIP_DEGREE", FormatHelper.FORMAT_3);
		// $scope.QTN_LIST = [{'LABEL':'投資保險', 'DATA': '1'},{'LABEL':'理專',
		// 'DATA': '2'},{'LABEL':'開戶', 'DATA': '3'},{'LABEL':'櫃檯', 'DATA':
		// '4'},{'LABEL':'簡訊', 'DATA': '5'}];

		Map<String, String> qtnList = new HashMap<String, String>();
		qtnList.put("WMS01", "投資/保險");
		qtnList.put("WMS02", "理專");
		qtnList.put("WMS03", "開戶");
		qtnList.put("WMS04", "櫃檯");
		qtnList.put("WMS05", "簡訊");

		/*** 2018-06-04 by willis 標題欄位調整 Start **********/
		Row row_head = sheet.createRow(0);
		int cell_head_num = 0;
		for (Entry<String, String> strs : order.entrySet()) {
			if (cell_head_num < 4) {
				Cell cell_head = row_head.createCell(cell_head_num);
				String str = strs.getValue();
				cell_head.setCellStyle(headingStyle);
				cell_head.setCellValue(str);
				if (cell_head_num == 3) {// 從第三欄開始新增加比例、件數合併欄位
					// 固定欄位第一列第4欄為比例，將合併第一列第4欄到第9欄
					cell_head = row_head.createCell(4);
					cell_head.setCellStyle(headingStyle);
					cell_head.setCellValue("比例");
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 10)); // 第0列, 第0行, 第4格~第10格合併
					// 由上述從第一列第10欄開始，將合併第一列第11欄到第17欄-件數
					cell_head = row_head.createCell(11);
					cell_head.setCellStyle(headingStyle);
					cell_head.setCellValue("件數");
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 17));
					row_head = sheet.createRow(1);
				}
			}
			cell_head_num++;
		}
		// 多增加一列，將比例件數滿意度拆開，下面跑的是各項滿意度
		row_head = sheet.createRow(1);
		cell_head_num = 0;
		for (Entry<String, String> strs : order.entrySet()) {
			if (cell_head_num < 4) {// 前四欄需合併且已給值，只需給Style
				Cell cell_head = row_head.createCell(cell_head_num);
				cell_head.setCellStyle(headingStyle);
				sheet.addMergedRegion(new CellRangeAddress(0, 1, cell_head_num, cell_head_num)); // 合併處存格
			} else {
				Cell cell_head = row_head.createCell(cell_head_num);
				String str = strs.getValue();
				cell_head.setCellStyle(headingStyle);
				cell_head.setCellValue(str);
			}
			cell_head_num++;
		}
		/**** 2018-06-04 by willis 標題欄位調整 end *********************************/

		List<String> orderkey = new ArrayList<String>(); // order : 表頭
		for (String key : order.keySet()) {
			orderkey.add(key);
		}

		int rownum = sheet.getLastRowNum(); // 最后一行行标，比行数小1

		for (Map<String, Object> objs : list) { // list : queryData資料
			Row row = sheet.createRow(++rownum);

			for (Entry<String, Object> en : objs.entrySet()) {
				Object obj = en.getValue();

				if (objs.get("REPORT_TYPE").equals("2") && en.getKey().equals("STATISTICS_TYPE"))
					obj = vipDegree.get(obj);

				if (objs.get("REPORT_TYPE").equals("5") && en.getKey().equals("STATISTICS_TYPE"))
					obj = qtnList.get(obj);

				int idx = orderkey.indexOf(en.getKey());

				if (idx >= 0) {
					Cell cell = row.createCell(idx);
					cell.setCellStyle(cellStyleFont);

					// sql String
					if (obj instanceof String) {
						if (en.getKey().equals("BRANCH_NAME")) {
							cell.setCellValue(ObjectUtils.toString(objs.get("BRANCH_NBR")) + "-" + (String) obj);
						} else {
							cell.setCellValue((String) obj);
						}
						// sql Date
					} else if (obj instanceof Date) {
						cell.setCellStyle(cellStyleDate);
						cell.setCellValue((Date) obj);
					}
					// sql Number
					else if (obj instanceof Number) {
						if (en.getKey().indexOf("_PC") > 0 && getBigDecimal(obj).compareTo(new BigDecimal(0)) > 0) {
							if (getBigDecimal(obj).compareTo(new BigDecimal(100)) >= 0) {
								cell.setCellValue(obj + "%");
							} else {
								cell.setCellValue(getBigDecimal(obj).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "%");
							}

						} else {
							cell.setCellValue((getBigDecimal(obj)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						}
					}
					// sql Double
					else if (obj instanceof Double) {
						cell.setCellValue((Double) obj);
					}
					// sql Boolean
					else if (obj instanceof Boolean) {
						cell.setCellValue((Boolean) obj);
					} else if (obj instanceof Blob) {
						cell.setCellValue("檔案");
					}
					// sql Null
					else if (obj == null) {
						cell.setCellValue("");
					}
					// sql undefined
					else {
						cell.setCellValue("ERROR");
					}
				}
			}
		}

		// autoSizeColumn
		for (int i = 0; i < row_head.getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}

		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		String fileName = name + ".xlsx";
		String filePath = Path + uuid;

		workbook.write(new FileOutputStream(filePath));

		Map<String, String> params = new HashMap<String, String>();
		params.put("fileUrl", DataManager.getSystem().getPath().get("temp").toString() + uuid);
		params.put("defaultFileName", fileName);

		// workbook.close();
		return (params);
	}

	// 轉Decimal
	public BigDecimal getBigDecimal(Object value) {
		
		BigDecimal ret = null;
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
			}
		}
		return ret;
	}

	// 匯出EXCEL BY 排序 AND 欄位名稱設定
	public Map<String, String> exportxlsx_cname_2(String name, List<Map<String, Object>> list, Map<String, String> order) throws JBranchException, IOException {

		/******* 設定參數 -- Strat **********/
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sheet");
		CellStyle cellStyleFont = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontName("微軟正黑體");
		cellStyleFont.setFont(font);

		CellStyle cellStyleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd h:mm:ss"));
		/******* 設定參數 -- End **********/

		setXlsHeader(workbook, sheet, order);

		List<String> orderkey = new ArrayList<String>(); // order : 表頭
		for (String key : order.keySet()) {
			orderkey.add(key);
		}

		int rownum = sheet.getLastRowNum(); // 最后一行行标，比行数小1

		printXlsRow(list, sheet, rownum, cellStyleFont);

		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String uuid = UUID.randomUUID().toString();
		String fileName = name + ".xlsx";
		String filePath = Path + uuid;

		workbook.write(new FileOutputStream(filePath));

		Map<String, String> params = new HashMap<String, String>();
		params.put("fileUrl", DataManager.getSystem().getPath().get("temp").toString() + uuid);
		params.put("defaultFileName", fileName);

		// workbook.close();
		return (params);
	}

	public void setXlsHeader(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> order) {
		
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

		/*** 2018-06-04 by willis 標題欄位調整 Start **********/
		Row row_head = sheet.createRow(0);
		int cell_head_num = 0;
		for (Entry<String, String> strs : order.entrySet()) {
			if (cell_head_num < 4) {
				Cell cell_head = row_head.createCell(cell_head_num);
				String str = strs.getValue();
				cell_head.setCellStyle(headingStyle);
				cell_head.setCellValue(str);
				if (cell_head_num == 3) {// 從第三欄開始新增加比例、件數合併欄位
					// 固定欄位第一列第4欄為比例，將合併第一列第4欄到第9欄
					cell_head = row_head.createCell(4);
					cell_head.setCellStyle(headingStyle);
					cell_head.setCellValue("比例");
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 10)); // 第0列,第0行,第4格~第10格合併
					// 由上述從第一列第10欄開始，將合併第一列第11欄到第17欄-件數
					cell_head = row_head.createCell(11);
					cell_head.setCellStyle(headingStyle);
					cell_head.setCellValue("件數");
					sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 17));
					row_head = sheet.createRow(1);
				}
			}
			cell_head_num++;
		}
		// 多增加一列，將比例件數滿意度拆開，下面跑的是各項滿意度
		row_head = sheet.createRow(1);
		cell_head_num = 0;
		for (Entry<String, String> strs : order.entrySet()) {
			if (cell_head_num < 4) {// 前四欄需合併且已給值，只需給Style
				Cell cell_head = row_head.createCell(cell_head_num);
				cell_head.setCellStyle(headingStyle);
				sheet.addMergedRegion(new CellRangeAddress(0, 1, cell_head_num, cell_head_num)); // 合併處存格
			} else {
				Cell cell_head = row_head.createCell(cell_head_num);
				String str = strs.getValue();
				cell_head.setCellStyle(headingStyle);
				cell_head.setCellValue(str);
			}
			cell_head_num++;
		}

		// autoSizeColumn
		for (int i = 0; i < row_head.getPhysicalNumberOfCells(); i++) {
			sheet.autoSizeColumn(i);
		}
		/**** 2018-06-04 by willis 標題欄位調整 end *********************************/
	}

	public void setXlsCellType(Object obj, Cell cell, boolean pc_type) {
		
		if (obj instanceof String) {
			cell.setCellValue((String) obj);
		} else if (obj instanceof Number) {
			if (pc_type && getBigDecimal(obj).compareTo(new BigDecimal(0)) > 0) {
				if (getBigDecimal(obj).compareTo(new BigDecimal(100)) >= 0) {
					cell.setCellValue(obj + "%");
				} else {
					cell.setCellValue(getBigDecimal(obj).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "%");
				}

			} else {
				cell.setCellValue((getBigDecimal(obj)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
		} else if (obj instanceof Double) {
			cell.setCellValue((Double) obj);
		} else if (obj instanceof Boolean) {
			cell.setCellValue((Boolean) obj);
		} else if (obj instanceof Blob) {
			cell.setCellValue("檔案");
		} else if (obj == null) {
			cell.setCellValue("");
		} else {
			cell.setCellValue("ERROR");
		}
	}

	public void printXlsRow(List<Map<String, Object>> list, XSSFSheet sheet, int rownum, CellStyle cellStyleFont) throws JBranchException {

		String[] mainLine = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "STATISTICS_TYPE", "VS_PC", "S_PC", "OS_PC", "NS_PC", "VD_PC", "NC_PC", "TOT_PC", "VS_CNT", "S_CNT", "OS_CNT", "NS_CNT", "VD_CNT", "NC_CNT", "TOT_CNT"};

		XmlInfo xmlinfo = new XmlInfo();
		Map<String, String> vipDegrees = xmlinfo.doGetVariable("CRM.VIP_DEGREE", FormatHelper.FORMAT_3);
		Map<String, String> qtnType = xmlinfo.doGetVariable("SQM.QTN_TYPE", FormatHelper.FORMAT_3);

		for (int i = 0; i < list.size(); i++) {
			rownum++;
			XSSFRow row = sheet.createRow(rownum);
			row = sheet.createRow(rownum);

			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(cellStyleFont);
				
				switch (mainLine[j]) {
					case "STATISTICS_TYPE":
						switch ((String) list.get(i).get("REPORT_TYPE")) {
							case "2":
								// #5577 一般 ----> 調整為「非理財會員」
								if (StringUtils.equals((String) list.get(i).get(mainLine[j]), "") || StringUtils.equals((String) list.get(i).get(mainLine[j]), "M")) {
									setXlsCellType("非理財會員", cell, false);
								} else {
									if (null != list.get(i).get(mainLine[j])) {
										setXlsCellType(vipDegrees.get((String) list.get(i).get(mainLine[j])), cell, false);
									}
								}
								
								break;
							case "5":
								if (null != list.get(i).get(mainLine[j])) {
									setXlsCellType(qtnType.get((String) list.get(i).get(mainLine[j])), cell, false);
								} else {
									setXlsCellType("", cell, false);
								}

								break;
							default:
								setXlsCellType((String) list.get(i).get(mainLine[j]), cell, false);
								
								break;
						}
						
						break;
					case "REGION_CENTER_NAME":
						if (null != list.get(i).get("BRANCH_NAME") && ((String) list.get(i).get("BRANCH_NAME")).indexOf("合計") >= 0) {
							setXlsCellType("", cell, false);
						} else if (null != list.get(i).get("BRANCH_AREA_NAME") && ((String) list.get(i).get("BRANCH_AREA_NAME")).indexOf("合計") >= 0) {
							setXlsCellType("", cell, false);
						} else {
							setXlsCellType((String) list.get(i).get(mainLine[j]), cell, false);
						}
						
						break;
					case "BRANCH_AREA_NAME":
						if (null != list.get(i).get("BRANCH_NAME") && ((String) list.get(i).get("BRANCH_NAME")).indexOf("合計") >= 0) {
							setXlsCellType("", cell, false);
						} else {
							setXlsCellType((String) list.get(i).get(mainLine[j]), cell, false);
						}
						
						break;
					case "BRANCH_NAME":
						if (null !=  list.get(i).get("BRANCH_AREA_NAME") && ((String) list.get(i).get("BRANCH_AREA_NAME")).indexOf("合計") == -1) {
							setXlsCellType((String) list.get(i).get("BRANCH_NBR") + "-" + (String) list.get(i).get(mainLine[j]), cell, false);
						}
						
						break;
					default:
						setXlsCellType(list.get(i).get(mainLine[j]), cell, false);

						break;
				}
			}
		}
	}
}