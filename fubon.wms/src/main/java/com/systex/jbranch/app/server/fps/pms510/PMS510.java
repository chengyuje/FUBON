package com.systex.jbranch.app.server.fps.pms510;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms510")
@Scope("request")
public class PMS510 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	private String getBaseSQL (String reportHierarchy) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("    SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("           TYPE_KINGDOM, TYPE_CLASS, TYPE_PHYLUM, VALUES_BY_CLASS ");
		sb.append("      FROM ( ");
		sb.append("        SELECT A.YYYYMM, A.REGION_CENTER_ID, A.REGION_CENTER_NAME, A.BRANCH_AREA_ID, A.BRANCH_AREA_NAME, A.BRANCH_NBR, A.BRANCH_NAME, ");
		sb.append("               A.TYPE_KINGDOM, A.TYPE_CLASS, ");
		sb.append("               A.ACTUAL, NVL(B.ESTIMATE, '-') AS ESTIMATE, A.MOM, A.BUDGET_NUM, A.BN_COMPARE, A.REACH_RATE, A.LY_ACTUAL, A.LY_COMPARE, A.YOY ");
		sb.append("        FROM ( ");
		sb.append("          SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("                 TYPE_KINGDOM, TYPE_CLASS, ");
		sb.append("                 ACTUAL, '-' AS ESTIMATE, '-' AS MOM, '-' AS BUDGET_NUM, '-' AS BN_COMPARE, '-' AS REACH_RATE, '-' AS LY_ACTUAL, '-' AS LY_COMPARE, '-' AS YOY ");
		sb.append("          FROM ( ");
		sb.append("            SELECT DISTINCT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("                   'AUM' AS TYPE_KINGDOM, RM_NUMS, R_CUST_NUMS, DEP_DF, DEP_DEMAND, DEP_FIXED, DEP_FIXED_EXP ");
		sb.append("            FROM TBPMS_AMT_COST_PROFIT ");
		sb.append("            WHERE 1 = 1 ");
		sb.append("            AND YYYYMM = :yyyymm ");
		
		switch (reportHierarchy) {
			case "BRH":
				sb.append("            AND BRANCH_NBR = :branchNbr ");
				break;
			case "AREA":
				sb.append("            AND INSTR(BRANCH_AREA_ID, '_TOTAL') > 0 ");
				sb.append("            AND REPLACE(BRANCH_AREA_ID, '_TOTAL', '') = :branchAreaID ");
				break;
			case "REGION":
				sb.append("            AND INSTR(REGION_CENTER_ID, '_TOTAL') > 0 ");
				sb.append("            AND REPLACE(REGION_CENTER_ID, '_TOTAL', '') = :regionCenterID ");
				break;
			case "ALL":
				sb.append("            AND INSTR(REGION_CENTER_ID, '_TOTAL') > 0 ");
				break;
		}

		sb.append("          ) ");
		sb.append("          UNPIVOT (ACTUAL FOR TYPE_CLASS IN (RM_NUMS, R_CUST_NUMS, DEP_DF, DEP_DEMAND, DEP_FIXED, DEP_FIXED_EXP)) ");
		sb.append("        ) A ");
		sb.append("        LEFT JOIN ( ");
		sb.append("          SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("                 TYPE_KINGDOM, REPLACE(TYPE_CLASS, '_EST', '') AS TYPE_CLASS, ");
		sb.append("                 '-' AS ACTUAL, ESTIMATE, '-' AS MOM, '-' AS BUDGET_NUM, '-' AS BN_COMPARE, '-' AS REACH_RATE, '-' AS LY_ACTUAL, '-' AS LY_COMPARE, '-' AS YOY ");
		sb.append("          FROM ( ");
		sb.append("            SELECT DISTINCT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("                   'AUM' AS TYPE_KINGDOM, DEP_DF_EST, DEP_DEMAND_EST, DEP_FIXED_EST, DEP_FIXED_EXP_EST ");
		sb.append("            FROM TBPMS_AMT_COST_PROFIT ");
		sb.append("            WHERE 1 = 1 ");
		sb.append("            AND YYYYMM = :yyyymm ");
		
		switch (reportHierarchy) {
			case "BRH":
				sb.append("            AND BRANCH_NBR = :branchNbr ");
				break;
			case "AREA":
				sb.append("            AND INSTR(BRANCH_AREA_ID, '_TOTAL') > 0 ");
				sb.append("            AND REPLACE(BRANCH_AREA_ID, '_TOTAL', '') = :branchAreaID ");
				break;
			case "REGION":
				sb.append("            AND INSTR(REGION_CENTER_ID, '_TOTAL') > 0 ");
				sb.append("            AND REPLACE(REGION_CENTER_ID, '_TOTAL', '') = :regionCenterID ");
				break;
			case "ALL":
				sb.append("            AND INSTR(REGION_CENTER_ID, '_TOTAL') > 0 ");
				break;
		}
		
		sb.append("          ) ");
		sb.append("          UNPIVOT (ESTIMATE FOR TYPE_CLASS IN (DEP_DF_EST, DEP_DEMAND_EST, DEP_FIXED_EST, DEP_FIXED_EXP_EST)) ");
		sb.append("        ) B ON A.BRANCH_NBR = B.BRANCH_NBR AND A.TYPE_KINGDOM = B.TYPE_KINGDOM AND A.TYPE_CLASS = B.TYPE_CLASS ");
		sb.append("      ) ");
		sb.append("      UNPIVOT (VALUES_BY_CLASS FOR TYPE_PHYLUM IN (ACTUAL, ESTIMATE, MOM, BUDGET_NUM, BN_COMPARE, REACH_RATE, LY_ACTUAL, LY_COMPARE, YOY)) ");
		sb.append("      UNION ");
		sb.append("      SELECT YYYYMM, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("             TYPE_KINGDOM, TYPE_CLASS, TYPE_PHYLUM, VALUES_BY_CLASS ");
		sb.append("      FROM TBPMS_AMT_COST_PROFIT ");
		sb.append("      WHERE 1 = 1 ");
		sb.append("      AND YYYYMM = :yyyymm ");
		
		switch (reportHierarchy) {
			case "BRH":
				sb.append("      AND BRANCH_NBR = :branchNbr ");
				break;
			case "AREA":
				sb.append("            AND INSTR(BRANCH_AREA_ID, '_TOTAL') > 0 ");
				sb.append("            AND REPLACE(BRANCH_AREA_ID, '_TOTAL', '') = :branchAreaID ");
				break;
			case "REGION":
				sb.append("            AND INSTR(REGION_CENTER_ID, '_TOTAL') > 0 ");
				sb.append("            AND REPLACE(REGION_CENTER_ID, '_TOTAL', '') = :regionCenterID ");
				break;
			case "ALL":
				sb.append("            AND INSTR(REGION_CENTER_ID, '_TOTAL') > 0 ");
				break;
		}
		
		return sb.toString();
	}
	
	// 查詢
	public void query (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS510InputVO inputVO = (PMS510InputVO) body;
		PMS510OutputVO outputVO = new PMS510OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		List<String> titleBrh = new ArrayList<String>();
		List<String> titleColName = new ArrayList<String>();
		List<String> titleCol = new ArrayList<String>();
		
		List<Map<String, Object>> colsList = new ArrayList<Map<String,Object>>();
		
		String deptStr = "";
		String str = "";
		
		switch (inputVO.getReportHierarchy()) {
			case "BRH":
				// === step 1
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT P_PHYLUM.PARAM_CODE AS PHYLUM_CODE, P_PHYLUM.PARAM_NAME AS PHYLUM_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME ");
				sb.append("FROM VWORG_DEFN_INFO INFO ");
				sb.append("LEFT JOIN TBSYSPARAMETER P_PHYLUM ON P_PHYLUM.PARAM_TYPE  = 'PMS.TYPE_PHYLUM' AND 1 = 1 ");
				sb.append("WHERE INFO.BRANCH_NBR = :branchNbr "); 
				sb.append("ORDER BY DECODE(REGION_CENTER_ID, '000', 999, 0), ");
				sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
				sb.append("         REGION_CENTER_NAME, ");
				sb.append("         DECODE(BRANCH_AREA_ID, NULL, 999, 0), ");
				sb.append("         BRANCH_AREA_NAME, ");
				sb.append("         DECODE(BRANCH_NBR, NULL, 999, 0), ");
				sb.append("         BRANCH_NAME, ");
				sb.append("         P_PHYLUM.PARAM_ORDER ");
				
				queryCondition.setObject("branchNbr", inputVO.getDeptID());
				
				queryCondition.setQueryString(sb.toString());
				
				colsList = dam.exeQuery(queryCondition);
				
				deptStr = "";
				str = "";
				for (int i = 0; i < colsList.size(); i++) {
					str = "'" + colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("BRANCH_NBR") + "' AS " + colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("BRANCH_NBR");
					deptStr = deptStr + str;
					if ((i + 1) != colsList.size()) {
						deptStr = deptStr + ", ";
					}
				}
				
				// === step 2
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT CASE WHEN TYPE_CLASS_CODE IN ('RM_NUMS', 'R_CUST_NUMS') THEN '' ELSE TYPE_KINGDOM_NAME END AS TYPE_KINGDOM_NAME, ");
				sb.append("       TYPE_CLASS_NAME, ");
				for (int i = 0; i < colsList.size(); i++) {
					sb.append(colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("BRANCH_NBR")).append(" ");
					if ((i + 1) != colsList.size()) {
						sb.append(", ");
					}
				}
				sb.append("FROM ( ");
				sb.append("  SELECT YYYYMM, ");
				sb.append("         P_KINGDOM.PARAM_CODE AS TYPE_KINGDOM_CODE, ");
				sb.append("         P_KINGDOM.PARAM_NAME AS TYPE_KINGDOM_NAME, ");
				sb.append("         P_KINGDOM.PARAM_ORDER AS TYPE_KINGDOM_ORDER, ");
				sb.append("         P_PHYLUM.PARAM_CODE || '_' || BRANCH_NBR AS TYPE_PHYLUM_CODE, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_CODE IS NULL THEN P_CLASS_E.PARAM_CODE ELSE P_CLASS.PARAM_CODE END AS TYPE_CLASS_CODE, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_NAME IS NULL THEN P_CLASS_E.PARAM_NAME ELSE P_CLASS.PARAM_NAME END AS TYPE_CLASS_NAME, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_CODE IS NULL THEN P_CLASS_E.PARAM_ORDER ELSE P_CLASS.PARAM_ORDER END AS TYPE_CLASS_ORDER, ");
				sb.append("         VALUES_BY_CLASS ");
				sb.append("  FROM ( ").append(getBaseSQL(inputVO.getReportHierarchy())).append(" ) BASE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_KINGDOM ON P_KINGDOM.PARAM_TYPE = 'PMS.TYPE_KINGDOM' AND BASE.TYPE_KINGDOM = P_KINGDOM.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_PHYLUM  ON P_PHYLUM.PARAM_TYPE  = 'PMS.TYPE_PHYLUM' AND BASE.TYPE_PHYLUM = P_PHYLUM.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_CLASS_E ON P_CLASS_E.PARAM_TYPE = 'PMS.TYPE_CLASS_EXCEPTION' AND BASE.TYPE_CLASS = P_CLASS_E.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_CLASS   ON P_CLASS.PARAM_TYPE   = 'PMS.TYPE_CLASS' AND BASE.TYPE_CLASS = P_CLASS.PARAM_CODE ");
				sb.append(") ");
				sb.append("PIVOT (MAX(VALUES_BY_CLASS) FOR TYPE_PHYLUM_CODE IN ( ").append(deptStr).append(")) ");
				sb.append("ORDER BY TYPE_KINGDOM_ORDER, TYPE_CLASS_ORDER ");
				
				queryCondition.setObject("yyyymm", inputVO.getsCreDate());
				queryCondition.setObject("branchNbr", inputVO.getDeptID());
				
				queryCondition.setQueryString(sb.toString());
				
				list = dam.exeQuery(queryCondition);
				
				// === step 3
				// ー,YYYYMM,組織名稱
				titleBrh.add(inputVO.getsCreDate());
				titleBrh.add("ー");
				for (int i = 0; i < colsList.size(); i++) {
					titleBrh.add(colsList.get(i).get("BRANCH_NAME") + "");
				}
				
				// 實際數,預估數,MOM,預算數,預算數+/-,達成率,去年同期實際數,YOY+/-,YOY
				titleColName.add("(單位：MN)");
				titleColName.add("ー");
				for (int i = 0; i < colsList.size(); i++) {
					titleColName.add(colsList.get(i).get("PHYLUM_NAME") + "");
				}
				
				// 上述欄位之欄位名稱
				titleCol.add("TYPE_KINGDOM_NAME");
				titleCol.add("TYPE_CLASS_NAME");
				for (int i = 0; i < colsList.size(); i++) {
					titleCol.add(colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("BRANCH_NBR"));
				}
				
				break;
			case "AREA":
				// === step 1
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT P_PHYLUM.PARAM_CODE AS PHYLUM_CODE, P_PHYLUM.PARAM_NAME AS PHYLUM_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME ");
				sb.append("FROM (SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME FROM VWORG_DEFN_INFO) INFO ");
				sb.append("LEFT JOIN TBSYSPARAMETER P_PHYLUM ON P_PHYLUM.PARAM_TYPE  = 'PMS.TYPE_PHYLUM' AND 1 = 1 ");
				sb.append("WHERE INFO.BRANCH_AREA_ID = :branchAreaID ");
				sb.append("ORDER BY DECODE(REGION_CENTER_ID, '000', 999, 0), ");
				sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
				sb.append("         REGION_CENTER_NAME, ");
				sb.append("         DECODE(BRANCH_AREA_ID, NULL, 999, 0), ");
				sb.append("         BRANCH_AREA_NAME, ");
				sb.append("         P_PHYLUM.PARAM_ORDER ");
				
				queryCondition.setObject("branchAreaID", inputVO.getDeptID());
				
				queryCondition.setQueryString(sb.toString());
				
				colsList = dam.exeQuery(queryCondition);
				
				deptStr = "";
				str = "";
				for (int i = 0; i < colsList.size(); i++) {
					str = "'" + colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("BRANCH_AREA_ID") + "' AS " + colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("BRANCH_AREA_ID");
					deptStr = deptStr + str;
					if ((i + 1) != colsList.size()) {
						deptStr = deptStr + ", ";
					}
				}
				
				// === step 2
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT CASE WHEN TYPE_CLASS_CODE IN ('RM_NUMS', 'R_CUST_NUMS') THEN '' ELSE TYPE_KINGDOM_NAME END AS TYPE_KINGDOM_NAME, ");
				sb.append("       TYPE_CLASS_NAME, ");
				for (int i = 0; i < colsList.size(); i++) {
					sb.append(colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("BRANCH_AREA_ID")).append(" ");
					if ((i + 1) != colsList.size()) {
						sb.append(", ");
					}
				}
				sb.append("FROM ( ");
				sb.append("  SELECT YYYYMM, ");
				sb.append("         P_KINGDOM.PARAM_CODE AS TYPE_KINGDOM_CODE, ");
				sb.append("         P_KINGDOM.PARAM_NAME AS TYPE_KINGDOM_NAME, ");
				sb.append("         P_KINGDOM.PARAM_ORDER AS TYPE_KINGDOM_ORDER, ");
				sb.append("         P_PHYLUM.PARAM_CODE || '_' || REPLACE(BRANCH_AREA_ID, '_TOTAL', '') AS TYPE_PHYLUM_CODE, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_CODE IS NULL THEN P_CLASS_E.PARAM_CODE ELSE P_CLASS.PARAM_CODE END AS TYPE_CLASS_CODE, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_NAME IS NULL THEN P_CLASS_E.PARAM_NAME ELSE P_CLASS.PARAM_NAME END AS TYPE_CLASS_NAME, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_CODE IS NULL THEN P_CLASS_E.PARAM_ORDER ELSE P_CLASS.PARAM_ORDER END AS TYPE_CLASS_ORDER, ");
				sb.append("         VALUES_BY_CLASS ");
				sb.append("  FROM ( ").append(getBaseSQL(inputVO.getReportHierarchy())).append(" ) BASE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_KINGDOM ON P_KINGDOM.PARAM_TYPE = 'PMS.TYPE_KINGDOM' AND BASE.TYPE_KINGDOM = P_KINGDOM.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_PHYLUM  ON P_PHYLUM.PARAM_TYPE  = 'PMS.TYPE_PHYLUM' AND BASE.TYPE_PHYLUM = P_PHYLUM.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_CLASS_E ON P_CLASS_E.PARAM_TYPE = 'PMS.TYPE_CLASS_EXCEPTION' AND BASE.TYPE_CLASS = P_CLASS_E.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_CLASS   ON P_CLASS.PARAM_TYPE   = 'PMS.TYPE_CLASS' AND BASE.TYPE_CLASS = P_CLASS.PARAM_CODE ");
				sb.append(") ");
				sb.append("PIVOT (MAX(VALUES_BY_CLASS) FOR TYPE_PHYLUM_CODE IN ( ").append(deptStr).append(")) ");
				sb.append("ORDER BY TYPE_KINGDOM_ORDER, TYPE_CLASS_ORDER ");
				
				queryCondition.setObject("yyyymm", inputVO.getsCreDate());
				queryCondition.setObject("branchAreaID", inputVO.getDeptID());
				
				queryCondition.setQueryString(sb.toString());
				
				list = dam.exeQuery(queryCondition);
				
				// === step 3
				// ー,YYYYMM,組織名稱
				titleBrh.add(inputVO.getsCreDate());
				titleBrh.add("ー");
				for (int i = 0; i < colsList.size(); i++) {
					titleBrh.add(colsList.get(i).get("BRANCH_AREA_NAME") + "");
				}
				
				// 實際數,預估數,MOM,預算數,預算數+/-,達成率,去年同期實際數,YOY+/-,YOY
				titleColName.add("(單位：MN)");
				titleColName.add("ー");
				for (int i = 0; i < colsList.size(); i++) {
					titleColName.add(colsList.get(i).get("PHYLUM_NAME") + "");
				}
				
				// 上述欄位之欄位名稱
				titleCol.add("TYPE_KINGDOM_NAME");
				titleCol.add("TYPE_CLASS_NAME");
				for (int i = 0; i < colsList.size(); i++) {
					titleCol.add(colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("BRANCH_AREA_ID"));
				}
				
				break;
			case "REGION":
				// === step 1
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT P_PHYLUM.PARAM_CODE AS PHYLUM_CODE, P_PHYLUM.PARAM_NAME AS PHYLUM_NAME, INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME ");
				sb.append("FROM (SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME FROM VWORG_DEFN_INFO) INFO ");
				sb.append("LEFT JOIN TBSYSPARAMETER P_PHYLUM ON P_PHYLUM.PARAM_TYPE  = 'PMS.TYPE_PHYLUM' AND 1 = 1 ");
				sb.append("WHERE INFO.REGION_CENTER_ID = :regionCenterID ");
				sb.append("ORDER BY DECODE(REGION_CENTER_ID, '000', 999, 0), ");
				sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
				sb.append("         REGION_CENTER_NAME, ");
				sb.append("         P_PHYLUM.PARAM_ORDER ");
				
				queryCondition.setObject("regionCenterID", inputVO.getDeptID());
				
				queryCondition.setQueryString(sb.toString());
				
				colsList = dam.exeQuery(queryCondition);
				
				deptStr = "";
				str = "";
				for (int i = 0; i < colsList.size(); i++) {
					str = "'" + colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("REGION_CENTER_ID") + "' AS " + colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("REGION_CENTER_ID");
					deptStr = deptStr + str;
					if ((i + 1) != colsList.size()) {
						deptStr = deptStr + ", ";
					}
				}
				
				// === step 2
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT CASE WHEN TYPE_CLASS_CODE IN ('RM_NUMS', 'R_CUST_NUMS') THEN '' ELSE TYPE_KINGDOM_NAME END AS TYPE_KINGDOM_NAME, ");
				sb.append("       TYPE_CLASS_NAME, ");
				for (int i = 0; i < colsList.size(); i++) {
					sb.append(colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("REGION_CENTER_ID")).append(" ");
					if ((i + 1) != colsList.size()) {
						sb.append(", ");
					}
				}
				sb.append("FROM ( ");
				sb.append("  SELECT YYYYMM, ");
				sb.append("         P_KINGDOM.PARAM_CODE AS TYPE_KINGDOM_CODE, ");
				sb.append("         P_KINGDOM.PARAM_NAME AS TYPE_KINGDOM_NAME, ");
				sb.append("         P_KINGDOM.PARAM_ORDER AS TYPE_KINGDOM_ORDER, ");
				sb.append("         P_PHYLUM.PARAM_CODE || '_' || REPLACE(REGION_CENTER_ID, '_TOTAL', '') AS TYPE_PHYLUM_CODE, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_CODE IS NULL THEN P_CLASS_E.PARAM_CODE ELSE P_CLASS.PARAM_CODE END AS TYPE_CLASS_CODE, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_NAME IS NULL THEN P_CLASS_E.PARAM_NAME ELSE P_CLASS.PARAM_NAME END AS TYPE_CLASS_NAME, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_CODE IS NULL THEN P_CLASS_E.PARAM_ORDER ELSE P_CLASS.PARAM_ORDER END AS TYPE_CLASS_ORDER, ");
				sb.append("         VALUES_BY_CLASS ");
				sb.append("  FROM ( ").append(getBaseSQL(inputVO.getReportHierarchy())).append(" ) BASE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_KINGDOM ON P_KINGDOM.PARAM_TYPE = 'PMS.TYPE_KINGDOM' AND BASE.TYPE_KINGDOM = P_KINGDOM.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_PHYLUM  ON P_PHYLUM.PARAM_TYPE  = 'PMS.TYPE_PHYLUM' AND BASE.TYPE_PHYLUM = P_PHYLUM.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_CLASS_E ON P_CLASS_E.PARAM_TYPE = 'PMS.TYPE_CLASS_EXCEPTION' AND BASE.TYPE_CLASS = P_CLASS_E.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_CLASS   ON P_CLASS.PARAM_TYPE   = 'PMS.TYPE_CLASS' AND BASE.TYPE_CLASS = P_CLASS.PARAM_CODE ");
				sb.append(") ");
				sb.append("PIVOT (MAX(VALUES_BY_CLASS) FOR TYPE_PHYLUM_CODE IN ( ").append(deptStr).append(")) ");
				sb.append("ORDER BY TYPE_KINGDOM_ORDER, TYPE_CLASS_ORDER ");
				
				queryCondition.setObject("yyyymm", inputVO.getsCreDate());
				queryCondition.setObject("regionCenterID", inputVO.getDeptID());
				
				queryCondition.setQueryString(sb.toString());
				
				list = dam.exeQuery(queryCondition);
				
				// === step 3
				// YYYYMM,-,組織名稱
				titleBrh.add(inputVO.getsCreDate());
				titleBrh.add("-");
				for (int i = 0; i < colsList.size(); i++) {
					titleBrh.add(colsList.get(i).get("REGION_CENTER_NAME") + "");
				}
				
				// (單位：MN),-,實際數,預估數,MOM,預算數,預算數+/-,達成率,去年同期實際數,YOY+/-,YOY
				titleColName.add("(單位：MN)");
				titleColName.add("-");
				for (int i = 0; i < colsList.size(); i++) {
					titleColName.add(colsList.get(i).get("PHYLUM_NAME") + "");
				}
				
				// 上述欄位之欄位名稱
				titleCol.add("TYPE_KINGDOM_NAME");
				titleCol.add("TYPE_CLASS_NAME");
				for (int i = 0; i < colsList.size(); i++) {
					titleCol.add(colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("REGION_CENTER_ID"));
				}
				
				break;
			case "ALL":
				// === step 1
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT P_PHYLUM.PARAM_CODE AS PHYLUM_CODE, P_PHYLUM.PARAM_NAME AS PHYLUM_NAME, INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME ");
				sb.append("FROM ( ");
				sb.append("  SELECT DISTINCT REGION_CENTER_ID, REGION_CENTER_NAME FROM VWORG_DEFN_INFO ");
				sb.append("  UNION ");
				sb.append("  SELECT 'ALL' AS REGION_CENTER_ID, '全行' AS REGION_CENTER_NAME FROM DUAL ");
				sb.append(") INFO ");
				sb.append("LEFT JOIN TBSYSPARAMETER P_PHYLUM ON P_PHYLUM.PARAM_TYPE  = 'PMS.TYPE_PHYLUM' AND 1 = 1 ");
				sb.append("WHERE 1 = 1 ");
				sb.append("ORDER BY DECODE(REGION_CENTER_ID, '000', 999, 0), ");
				sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
				sb.append("         REGION_CENTER_NAME, ");
				sb.append("         P_PHYLUM.PARAM_ORDER ");
				
				queryCondition.setQueryString(sb.toString());
				
				colsList = dam.exeQuery(queryCondition);
				
				deptStr = "";
				str = "";
				for (int i = 0; i < colsList.size(); i++) {
					str = "'" + colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("REGION_CENTER_ID") + "' AS " + colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("REGION_CENTER_ID");
					deptStr = deptStr + str;
					if ((i + 1) != colsList.size()) {
						deptStr = deptStr + ", ";
					}
				}
				
				// === step 2
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("SELECT CASE WHEN TYPE_CLASS_CODE IN ('RM_NUMS', 'R_CUST_NUMS') THEN '' ELSE TYPE_KINGDOM_NAME END AS TYPE_KINGDOM_NAME, ");
				sb.append("       TYPE_CLASS_NAME, ");
				for (int i = 0; i < colsList.size(); i++) {
					sb.append(colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("REGION_CENTER_ID")).append(" ");
					if ((i + 1) != colsList.size()) {
						sb.append(", ");
					}
				}
				sb.append("FROM ( ");
				sb.append("  SELECT YYYYMM, ");
				sb.append("         P_KINGDOM.PARAM_CODE AS TYPE_KINGDOM_CODE, ");
				sb.append("         P_KINGDOM.PARAM_NAME AS TYPE_KINGDOM_NAME, ");
				sb.append("         P_KINGDOM.PARAM_ORDER AS TYPE_KINGDOM_ORDER, ");
				sb.append("         P_PHYLUM.PARAM_CODE || '_' || REPLACE(REGION_CENTER_ID, '_TOTAL', '') AS TYPE_PHYLUM_CODE, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_CODE IS NULL THEN P_CLASS_E.PARAM_CODE ELSE P_CLASS.PARAM_CODE END AS TYPE_CLASS_CODE, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_NAME IS NULL THEN P_CLASS_E.PARAM_NAME ELSE P_CLASS.PARAM_NAME END AS TYPE_CLASS_NAME, ");
				sb.append("         CASE WHEN P_CLASS.PARAM_CODE IS NULL THEN P_CLASS_E.PARAM_ORDER ELSE P_CLASS.PARAM_ORDER END AS TYPE_CLASS_ORDER, ");
				sb.append("         VALUES_BY_CLASS ");
				sb.append("  FROM ( ").append(getBaseSQL(inputVO.getReportHierarchy())).append(" ) BASE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_KINGDOM ON P_KINGDOM.PARAM_TYPE = 'PMS.TYPE_KINGDOM' AND BASE.TYPE_KINGDOM = P_KINGDOM.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_PHYLUM  ON P_PHYLUM.PARAM_TYPE  = 'PMS.TYPE_PHYLUM' AND BASE.TYPE_PHYLUM = P_PHYLUM.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_CLASS_E ON P_CLASS_E.PARAM_TYPE = 'PMS.TYPE_CLASS_EXCEPTION' AND BASE.TYPE_CLASS = P_CLASS_E.PARAM_CODE ");
				sb.append("  LEFT JOIN TBSYSPARAMETER P_CLASS   ON P_CLASS.PARAM_TYPE   = 'PMS.TYPE_CLASS' AND BASE.TYPE_CLASS = P_CLASS.PARAM_CODE ");
				sb.append(") ");
				sb.append("PIVOT (MAX(VALUES_BY_CLASS) FOR TYPE_PHYLUM_CODE IN ( ").append(deptStr).append(")) ");
				sb.append("ORDER BY TYPE_KINGDOM_ORDER, TYPE_CLASS_ORDER ");
				
				queryCondition.setObject("yyyymm", inputVO.getsCreDate());
				
				queryCondition.setQueryString(sb.toString());

				list = dam.exeQuery(queryCondition);
				
				// === step 3
				// ー,YYYYMM,組織名稱
				titleBrh.add(inputVO.getsCreDate());
				titleBrh.add("ー");
				for (int i = 0; i < colsList.size(); i++) {
					titleBrh.add(colsList.get(i).get("REGION_CENTER_NAME") + "");
				}
				
				// 實際數,預估數,MOM,預算數,預算數+/-,達成率,去年同期實際數,YOY+/-,YOY
				titleColName.add("(單位：MN)");
				titleColName.add("ー");
				for (int i = 0; i < colsList.size(); i++) {
					titleColName.add(colsList.get(i).get("PHYLUM_NAME") + "");
				}
				
				// 上述欄位之欄位名稱
				titleCol.add("TYPE_KINGDOM_NAME");
				titleCol.add("TYPE_CLASS_NAME");
				for (int i = 0; i < colsList.size(); i++) {
					titleCol.add(colsList.get(i).get("PHYLUM_CODE") + "_" + colsList.get(i).get("REGION_CENTER_ID"));
				}
				
				break;
		}
		
		outputVO.setTitleBrh(titleBrh);
		outputVO.setTitleColName(titleColName);
		outputVO.setTitleCol(titleCol);
		outputVO.setResultList(list);
		
		sendRtnObject(outputVO);
	}
	
	public void getDeptList (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS510InputVO inputVO = (PMS510InputVO) body;
		PMS510OutputVO outputVO = new PMS510OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		switch (inputVO.getReportHierarchy()) {
			case "BRH":
				sb.append("SELECT DISTINCT BRANCH_NBR AS DATA, BRANCH_NAME AS LABEL ");
				sb.append("FROM VWORG_DEFN_INFO ");
				sb.append("WHERE BRANCH_NBR IN :branchList ");
				sb.append("ORDER BY BRANCH_NBR ");
				
				break;
			case "AREA":
				sb.append("SELECT DISTINCT BRANCH_AREA_ID AS DATA, BRANCH_AREA_NAME AS LABEL ");
				sb.append("FROM VWORG_DEFN_INFO ");
				sb.append("WHERE BRANCH_NBR IN :branchList ");
				sb.append("ORDER BY BRANCH_AREA_ID ");
				
				break;
			case "REGION":
				sb.append("SELECT DISTINCT REGION_CENTER_ID AS DATA, REGION_CENTER_NAME AS LABEL ");
				sb.append("FROM VWORG_DEFN_INFO ");
				sb.append("WHERE BRANCH_NBR IN :branchList ");
				sb.append("ORDER BY DECODE(REGION_CENTER_ID, '000', 999, 0), ");
				sb.append("      DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
				sb.append("      REGION_CENTER_NAME ");
				
				break;
		}
		queryCondition.setObject("branchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));

		queryCondition.setQueryString(sb.toString());

		outputVO.setDeptList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}
	
	// 匯出
	public void exportRPT(Object body, IPrimitiveMap header) throws Exception {
		
		PMS510InputVO inputVO = (PMS510InputVO) body;
		PMS510OutputVO outputVO = new PMS510OutputVO();
		
		String[] headerLine1 = inputVO.getTitleBrh().toArray(new String[0]);
		String[] headerLine2 = inputVO.getTitleColName().toArray(new String[0]);
		String[] mainLine    = inputVO.getTitleCol().toArray(new String[0]);
		
		String fileName = sdfYYYYMMDD.format(new Date()) + "量收本利預估報表.xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sdfYYYYMMDD.format(new Date()) + "量收本利預估報表");
		sheet.setDefaultColumnWidth(20);
		
		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		
		// === 標題 START ===
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
		}
		// === 標題 END ===
		
		// === 內容 START ===
		List<Map<String, Object>> list = inputVO.getExportList();

		index++;
		
		ArrayList<String> line1TempList = new ArrayList<String>();	// 比對用
		Integer line1StartFlag = 0;									// 比對用
		Integer line1EndFlag = 0;									// 比對用
		Integer contectStartIndex = index;
		DecimalFormat df = new DecimalFormat("#,##0.00");
		
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				
				String line1Name = (String) list.get(i).get("TYPE_KINGDOM_NAME");

				if (j == 0 && line1TempList.indexOf(line1Name) < 0) {
					line1TempList.add(line1Name);
					if (line1EndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(line1StartFlag + contectStartIndex, line1EndFlag + contectStartIndex, j, 0)); // firstRow, endRow, firstColumn, endColumn
					}
					
					line1StartFlag = i;
					line1EndFlag = 0;
				} else if (j == 0 && null != list.get(i).get("TYPE_KINGDOM_NAME")) {
					line1EndFlag = i;
				}
				
				// 最後一筆
				if (j == 0 && i == (list.size() - 1)) {
					if (line1EndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(line1StartFlag + contectStartIndex, line1EndFlag + contectStartIndex, j, 0)); // firstRow, endRow, firstColumn, endColumn
					}
				}
				
				if (null == list.get(i).get(mainLine[j])) {
					cell.setCellValue((String) list.get(i).get(mainLine[j]));
				} else if (StringUtils.equals("-", (String) list.get(i).get(mainLine[j]))) {
					cell.setCellValue((String) list.get(i).get(mainLine[j]));
				} else if (((String) list.get(i).get(mainLine[j])).matches("[+-]?\\d*(\\.\\d+)?")) {
					cell.setCellValue(df.format(Float.valueOf((String) list.get(i).get(mainLine[j]))));
				} else {
					cell.setCellValue((String) list.get(i).get(mainLine[j]));
				}
			}
			
			index++;
		}
		// === 內容 END ===
		
		workbook.write(new FileOutputStream(filePath));
		
 		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
				
		sendRtnObject(outputVO);
	}
	
	// 檢查Map取出欄位是否為Null
	private String checkIsNull(Map<String, Object> map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
