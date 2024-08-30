package com.systex.jbranch.app.server.fps.pms354;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :投資IPO專案戰報<br>
 * Comments Name : PMS354java<br>
 * Author : Frank<br>
 * Date :2016/08/03 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms354")
@Scope("request")
public class PMS354 extends FubonWmsBizLogic {

	DataAccessManager dam = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	/** 查詢主檔頁面資料 **/
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {

		PMS354InputVO inputVO = (PMS354InputVO) body;
		PMS354OutputVO outputVO = new PMS354OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT A.PRJ_SEQ, A.PRJ_NAME, ");
		sql.append("       TO_CHAR(A.START_DT, 'YYYY.MM.DD') as START_DT, ");
		sql.append("       TO_CHAR(A.END_DT, 'YYYY.MM.DD') as END_DT, ");
		sql.append("       TO_CHAR(A.CREATETIME, 'YYYY.MM.DD') as CREATETIME, ");
		sql.append("       MEM.EMP_NAME ");
		sql.append(" FROM TBPMS_IPO_PARAM_MAST A ");
		sql.append(" LEFT JOIN TBORG_MEMBER MEM ON A.CREATOR = MEM.EMP_ID ");
		sql.append(" ORDER BY A.CREATETIME DESC");

		condition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		if (list.size() > 0) {
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setResultList(list); // 查詢結果資訊
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/** 取得動態欄位名稱 **/
	public void queryRPTCol (Object body, IPrimitiveMap header) throws JBranchException {

		PMS354InputVO inputVO = (PMS354InputVO) body;
		PMS354OutputVO outputVO = new PMS354OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT LISTAGG(CURRENCY_STD_ID, ',') WITHIN GROUP (ORDER BY CURRENCY_STD_ID) AS PRD_LIST ");
		sql.append("FROM (");
		sql.append("  SELECT DISTINCT A.PRJ_SEQ, C.CURRENCY_STD_ID ");
		sql.append("  FROM TBPMS_IPO_PARAM_MAST A ");
		sql.append("  INNER JOIN TBPMS_IPO_PARAM_PRD B ON A.PRJ_SEQ = B.PRJ_SEQ ");
		sql.append("  INNER JOIN TBPRD_FUND C ON B.PRD_ID = C.PRD_ID ");
		sql.append("  WHERE A.PRJ_SEQ = :seq ");
		sql.append("  UNION ");
		sql.append("  SELECT TO_NUMBER(:seq) AS PRJ_SEQ, 'TWD' AS CURRENCY_STD_ID ");
		sql.append("  FROM DUAL ");
		sql.append(") A ");
		sql.append("GROUP BY PRJ_SEQ ");

		condition.setQueryString(sql.toString());
		condition.setObject("seq", inputVO.getPrj_seq());

		List<Map<String, String>> map = dam.exeQuery(condition);

		if (map.size() > 0) {
			String[] str = map.get(0).get("PRD_LIST").split(",");
			List<String> cols = new ArrayList<String>();
			for (int i = 0; i < str.length; i++) {
				cols.add(str[i]);
			}

			outputVO.setColList(cols);

			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT TARGET ");
			sql.append("FROM TBPMS_IPO_PARAM_BR_NAME ");
			sql.append("WHERE PRJ_SEQ = :seq ");
			sql.append("AND COL_NO > 0 ");

			condition.setQueryString(sql.toString());
			condition.setObject("seq", inputVO.getPrj_seq());

			outputVO.setColList2(dam.exeQuery(condition));
		}

		sendRtnObject(outputVO);
	}

	/** 取得動態表格資料 **/
	public void queryDetail (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS354InputVO inputVO = (PMS354InputVO) body;
		PMS354OutputVO outputVO = new PMS354OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2); // 個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); // 營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); // 區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TO_CHAR(TO_DATE(BASE.DATA_DATE, 'yyyy/MM/dd'), 'yyyy/MM/dd') AS DATA_DATE, BASE.PRJ_SEQ, BASE.REGION_CENTER_ID, BASE.REGION_CENTER_NAME, BASE.BRANCH_AREA_ID, BASE.BRANCH_AREA_NAME, BASE.BRANCH_NBR, BASE.BRANCH_NAME, BASE.BRANCH_CLS, TAR_BASE.TARGET_LIST, ");
		sql.append("       LISTAGG(TO_CHAR(PRD_ID), ',') WITHIN GROUP(ORDER BY PRD_ID) AS PRD_ID_LIST, ");
		sql.append("       LISTAGG(CASE WHEN BAL = 0 THEN ' ' ELSE TO_CHAR(BAL) END, ',') WITHIN GROUP(ORDER BY PRD_ID) AS PRD_LIST, ");// -- 台幣金額
		sql.append("       LISTAGG(CASE WHEN ORG_BAL = 0 THEN ' ' ELSE TO_CHAR(ORG_BAL) END, ',') WITHIN GROUP(ORDER BY PRD_ID) AS ORG_PRD_LIST "); // -- 原幣金額
//		sql.append("       LISTAGG(TO_CHAR(BAL), ',') WITHIN GROUP(ORDER BY PRD_ID) AS PRD_LIST, "); // -- 台幣金額
//		sql.append("       LISTAGG(TO_CHAR(ORG_BAL), ',') WITHIN GROUP(ORDER BY PRD_ID) AS ORG_PRD_LIST "); // -- 原幣金額
		sql.append("FROM ( ");
		sql.append("  SELECT IPO_COUNT.DATA_DATE, IPO_COUNT.PRJ_SEQ, IPO_COUNT.REGION_CENTER_ID, IPO_COUNT.REGION_CENTER_NAME, IPO_COUNT.BRANCH_AREA_ID, IPO_COUNT.BRANCH_AREA_NAME, IPO_COUNT.BRANCH_NBR, IPO_COUNT.BRANCH_NAME, IPO_COUNT.BRANCH_CLS, ");
		sql.append("         IPO_COUNT.CURRENCY_STD_ID AS PRD_ID, NVL(IPO_COUNT.BAL, 0) AS BAL, NVL(IPO_COUNT.ORG_BAL, 0) AS ORG_BAL ");
		sql.append("  FROM ( ");
		sql.append("    SELECT B.DATA_DATE, B.PRJ_SEQ, ");
		sql.append("           B.REGION_CENTER_ID, B.REGION_CENTER_NAME, ");
		sql.append("           B.BRANCH_AREA_ID, B.BRANCH_AREA_NAME, ");
		sql.append("           B.BRANCH_NBR, B.BRANCH_NAME, B.BRANCH_CLS, ");
		sql.append("           B.CURRENCY_STD_ID, ");
		sql.append("           NVL(SUM(BAL), 0) AS BAL, NVL(SUM(ORG_BAL), 0) AS ORG_BAL ");
		sql.append("    FROM ( ");
		sql.append("      SELECT IPO_B.DATA_DATE, IPO_B.PRJ_SEQ, ");
		sql.append("             IPO_B.REGION_CENTER_ID, IPO_B.REGION_CENTER_NAME, ");
		sql.append("             IPO_B.BRANCH_AREA_ID, IPO_B.BRANCH_AREA_NAME, ");
		sql.append("             IPO_B.BRANCH_NBR, IPO_B.BRANCH_NAME, IPO_B.BRANCH_CLS, ");
		sql.append("             PRD.CURRENCY_STD_ID ");
		sql.append("      FROM ( ");
		sql.append("        SELECT DISTINCT ");
		sql.append("               (SELECT DISTINCT DATA_DATE FROM TBPMS_IPO_RPT WHERE PRJ_SEQ = MAST.PRJ_SEQ) AS DATA_DATE, ");
		sql.append("               PARAM_BR.PRJ_SEQ, ");
		sql.append("               REC_ORG.REGION_CENTER_ID, REC_ORG.REGION_CENTER_NAME, REC_ORG.BRANCH_AREA_ID, REC_ORG.BRANCH_AREA_NAME, REC_ORG.BRANCH_NBR, REC_ORG.BRANCH_NAME, REC_ORG.BRANCH_CLS ");
		sql.append("        FROM TBPMS_IPO_PARAM_MAST MAST ");
		sql.append("        INNER JOIN TBPMS_IPO_PARAM_BR PARAM_BR ON MAST.PRJ_SEQ = PARAM_BR.PRJ_SEQ ");
		sql.append("        LEFT JOIN TBPMS_ORG_REC_N REC_ORG ON TRUNC(MAST.END_DT) BETWEEN TRUNC(REC_ORG.START_TIME) AND TRUNC(REC_ORG.END_TIME) AND PARAM_BR.BRANCH_NBR = REC_ORG.BRANCH_NBR ");
		sql.append("        WHERE MAST.PRJ_SEQ = :seq ");
		sql.append("        AND PARAM_BR.BRANCH_NBR IS NOT NULL ");
		sql.append("        AND REC_ORG.BRANCH_NBR IS NOT NULL ");
		sql.append("        AND LENGTH(REC_ORG.BRANCH_NBR) = 3 ");
		sql.append("        AND TO_NUMBER(REC_ORG.BRANCH_NBR) >= 200 AND TO_NUMBER(REC_ORG.BRANCH_NBR) <= 900 ");
		sql.append("        AND TO_NUMBER(REC_ORG.BRANCH_NBR) <> 806 ");
		sql.append("        AND TO_NUMBER(REC_ORG.BRANCH_NBR) <> 810 ");
		sql.append("      ) IPO_B ");
		sql.append("      LEFT JOIN ( ");
		sql.append("        SELECT DISTINCT A.PRJ_SEQ, C.CURRENCY_STD_ID ");
		sql.append("        FROM TBPMS_IPO_PARAM_MAST A ");
		sql.append("        INNER JOIN TBPMS_IPO_PARAM_PRD B ON A.PRJ_SEQ = B.PRJ_SEQ ");
		sql.append("        INNER JOIN TBPRD_FUND C ON B.PRD_ID = C.PRD_ID ");
		sql.append("        WHERE A.PRJ_SEQ = :seq ");
		sql.append("        UNION ");
		sql.append("        SELECT TO_NUMBER(:seq) AS PRJ_SEQ, 'TWD' AS CURRENCY_STD_ID ");
		sql.append("        FROM DUAL ");
		sql.append("      ) PRD ON 1 = 1 ");
		sql.append("    ) B ");
		sql.append("    LEFT JOIN TBPMS_IPO_RPT IPO_RPT ON IPO_RPT.BRANCH_NBR = B.BRANCH_NBR AND IPO_RPT.TXN_CURR = B.CURRENCY_STD_ID AND B.PRJ_SEQ = IPO_RPT.PRJ_SEQ ");
		sql.append("    GROUP BY B.DATA_DATE, B.PRJ_SEQ, ");
		sql.append("             B.REGION_CENTER_ID, B.REGION_CENTER_NAME, ");
		sql.append("             B.BRANCH_AREA_ID, B.BRANCH_AREA_NAME, ");
		sql.append("             B.BRANCH_NBR, B.BRANCH_NAME, B.BRANCH_CLS, ");
		sql.append("             B.CURRENCY_STD_ID ");
		sql.append("  ) IPO_COUNT ");
		sql.append(") BASE ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT BR.PRJ_SEQ, BR.BRANCH_NBR, ");
		sql.append("         LISTAGG(NVL(TO_CHAR(BR.COL_NO), 0), ',') WITHIN GROUP(ORDER BY BR.COL_NO) AS COL_LIST, ");
		sql.append("         LISTAGG(NVL(TO_CHAR(BR.TARGET), 0), ',') WITHIN GROUP(ORDER BY BR.COL_NO) AS TARGET_LIST ");
		sql.append("  FROM TBPMS_IPO_PARAM_BR BR ");
		sql.append("  WHERE BR.PRJ_SEQ = :seq ");
		sql.append("  GROUP BY BR.PRJ_SEQ, BR.BRANCH_NBR ");
		sql.append(") TAR_BASE ON BASE.PRJ_SEQ = TAR_BASE.PRJ_SEQ AND BASE.BRANCH_NBR = TAR_BASE.BRANCH_NBR ");
		sql.append("WHERE BASE.BRANCH_NBR IS NOT NULL ");

		if (StringUtils.isNotBlank(inputVO.getRc_id())) {
			sql.append("AND BASE.REGION_CENTER_ID = :rcid ");
			condition.setObject("rcid", inputVO.getRc_id());
		} else {
			// 登入非總行人員強制加區域中心
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("AND BASE.REGION_CENTER_ID IN (:region_center_id) ");
				condition.setObject("region_center_id", pms000outputVO.getV_regionList());
			}
		}

		if (StringUtils.isNotBlank(inputVO.getOp_id())) {
			condition.setObject("opid", inputVO.getOp_id());
			sql.append("AND BASE.BRANCH_AREA_ID = :opid ");
		} else {
			// 登入非總行人員強制加營運區
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("AND BASE.BRANCH_AREA_ID IN (:branch_area_id) ");
				condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
			}
		}

		if (StringUtils.isNotBlank(inputVO.getBr_id())) {
			sql.append("AND BASE.BRANCH_NBR = :brid ");
			condition.setObject("brid", inputVO.getBr_id());
		} else {
			// 登入非總行人員強制加分行
			if (!headmgrMap.containsKey(roleID)) {
				sql.append("AND BASE.BRANCH_NBR IN (:branch_nbr) ");
				condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
			}
		}

		sql.append("GROUP BY TO_CHAR(TO_DATE(BASE.DATA_DATE, 'yyyy/MM/dd'), 'yyyy/MM/dd'), BASE.PRJ_SEQ, BASE.REGION_CENTER_ID, BASE.REGION_CENTER_NAME, BASE.BRANCH_AREA_ID, BASE.BRANCH_AREA_NAME, BASE.BRANCH_NBR, BASE.BRANCH_NAME, BASE.BRANCH_CLS, TAR_BASE.TARGET_LIST ");
		sql.append("ORDER BY BASE.REGION_CENTER_ID, BASE.BRANCH_AREA_ID, BASE.BRANCH_NBR, BASE.BRANCH_CLS ");

		condition.setQueryString(sql.toString());
		condition.setObject("seq", inputVO.getPrj_seq());
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalList(dam.exeQuery(condition));

		if (list.size() > 0) {
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/** 取得動態報表總計 **/
	public void queryDetail2 (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS354InputVO inputVO = (PMS354InputVO) body;
		PMS354OutputVO outputVO = new PMS354OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());

		StringBuffer sql_prd = new StringBuffer();
		sql_prd.append("SELECT BASE.PRJ_SEQ AS PRJ_SEQ, ");
		sql_prd.append("       BASE.PRD_ID AS CURRENCY_STD_ID, ");
		sql_prd.append("       SUM(BAL) AS TW_BAL, "); //-- 台幣金額
		sql_prd.append("       SUM(ORG_BAL) AS BAL "); //-- 原幣金額
		sql_prd.append("FROM ( ");
		sql_prd.append("  SELECT IPO_COUNT.DATA_DATE, BR_LIST.PRJ_SEQ, BR_LIST.REGION_CENTER_ID, BR_LIST.REGION_CENTER_NAME, BR_LIST.BRANCH_AREA_ID, BR_LIST.BRANCH_AREA_NAME, BR_LIST.BRANCH_NBR, BR_LIST.BRANCH_NAME, BR_LIST.BRANCH_CLS, ");
		sql_prd.append("         PRD_LIST.CURRENCY_STD_ID AS PRD_ID, NVL(IPO_COUNT.BAL, 0) AS BAL, NVL(IPO_COUNT.ORG_BAL, 0) AS ORG_BAL ");
		sql_prd.append("  FROM ( ");
		sql_prd.append("    SELECT DISTINCT ");
		sql_prd.append("           PARAM_BR.PRJ_SEQ, ");
		sql_prd.append("           REC_ORG.REGION_CENTER_ID, REC_ORG.REGION_CENTER_NAME, REC_ORG.BRANCH_AREA_ID, REC_ORG.BRANCH_AREA_NAME, REC_ORG.BRANCH_NBR, REC_ORG.BRANCH_NAME, REC_ORG.BRANCH_CLS ");
		sql_prd.append("    FROM TBPMS_IPO_PARAM_MAST MAST ");
		sql_prd.append("    INNER JOIN TBPMS_IPO_PARAM_BR PARAM_BR ON MAST.PRJ_SEQ = PARAM_BR.PRJ_SEQ ");
		sql_prd.append("    LEFT JOIN TBPMS_ORG_REC_N REC_ORG ON TRUNC(MAST.END_DT) BETWEEN TRUNC(REC_ORG.START_TIME) AND TRUNC(REC_ORG.END_TIME) AND PARAM_BR.BRANCH_NBR = REC_ORG.BRANCH_NBR ");
		sql_prd.append("    WHERE MAST.PRJ_SEQ = :seq ");
		sql_prd.append("    AND PARAM_BR.BRANCH_NBR IS NOT NULL ");
		sql_prd.append("    AND REC_ORG.BRANCH_NBR IS NOT NULL ");
		sql_prd.append("    AND LENGTH(REC_ORG.BRANCH_NBR) = 3 ");
		sql_prd.append("    AND TO_NUMBER(REC_ORG.BRANCH_NBR) >= 200 AND TO_NUMBER(REC_ORG.BRANCH_NBR) <= 900 ");
		sql_prd.append("  ) BR_LIST ");
		sql_prd.append("  LEFT JOIN ( ");
		sql_prd.append("    SELECT DISTINCT A.PRJ_SEQ, C.CURRENCY_STD_ID ");
		sql_prd.append("    FROM TBPMS_IPO_PARAM_MAST A ");
		sql_prd.append("    INNER JOIN TBPMS_IPO_PARAM_PRD B ON A.PRJ_SEQ = B.PRJ_SEQ ");
		sql_prd.append("    INNER JOIN TBPRD_FUND C ON B.PRD_ID = C.PRD_ID ");
		sql_prd.append("    WHERE A.PRJ_SEQ = :seq ");
		sql_prd.append("    UNION ");
		sql_prd.append("    SELECT TO_NUMBER(:seq) AS PRJ_SEQ, 'TWD' AS CURRENCY_STD_ID ");
		sql_prd.append("    FROM DUAL ");
		sql_prd.append("  ) PRD_LIST ON 1 = 1 ");
		sql_prd.append("  LEFT JOIN ( ");
		sql_prd.append("    SELECT IPO_RPT.DATA_DATE, IPO_RPT.PRJ_SEQ, IPO_RPT.BRANCH_NBR, IPO_RPT.TXN_CURR AS CURRENCY_STD_ID, SUM(BAL) AS BAL, SUM(ORG_BAL) AS ORG_BAL ");
		sql_prd.append("    FROM TBPMS_IPO_RPT IPO_RPT ");
		sql_prd.append("    WHERE IPO_RPT.PRJ_SEQ = :seq ");
		sql_prd.append("    GROUP BY IPO_RPT.DATA_DATE, IPO_RPT.PRJ_SEQ, IPO_RPT.BRANCH_NBR, IPO_RPT.TXN_CURR ");
		sql_prd.append("  ) IPO_COUNT ON IPO_COUNT.CURRENCY_STD_ID = PRD_LIST.CURRENCY_STD_ID AND IPO_COUNT.BRANCH_NBR = BR_LIST.BRANCH_NBR ");
		sql_prd.append(") BASE ");
		sql_prd.append("WHERE BASE.BRANCH_NBR IS NOT NULL ");
		sql_prd.append("GROUP BY BASE.PRJ_SEQ, BASE.PRD_ID ");
		sql_prd.append("ORDER BY BASE.PRD_ID ");
		
		condition.setQueryString(sql_prd.toString());
		condition.setObject("seq", inputVO.getPrj_seq());
		outputVO.setTotalprd(dam.exeQuery(condition));

		// 銷量總計
		StringBuffer sql_bal = new StringBuffer();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql_bal.append("WITH BASE AS ( ");
		sql_bal.append("  SELECT DISTINCT BR_LIST.BRANCH_NBR ");
		sql_bal.append("  FROM ( ");
		sql_bal.append("    SELECT DISTINCT ");
		sql_bal.append("           PARAM_BR.PRJ_SEQ, ");
		sql_bal.append("           REC_ORG.REGION_CENTER_ID, REC_ORG.REGION_CENTER_NAME, REC_ORG.BRANCH_AREA_ID, REC_ORG.BRANCH_AREA_NAME, REC_ORG.BRANCH_NBR, REC_ORG.BRANCH_NAME, REC_ORG.BRANCH_CLS ");
		sql_bal.append("    FROM TBPMS_IPO_PARAM_MAST MAST ");
		sql_bal.append("    INNER JOIN TBPMS_IPO_PARAM_BR PARAM_BR ON MAST.PRJ_SEQ = PARAM_BR.PRJ_SEQ ");
		sql_bal.append("    LEFT JOIN TBPMS_ORG_REC_N REC_ORG ON TRUNC(MAST.END_DT) BETWEEN TRUNC(REC_ORG.START_TIME) AND TRUNC(REC_ORG.END_TIME) AND PARAM_BR.BRANCH_NBR = REC_ORG.BRANCH_NBR ");
		sql_bal.append("    WHERE MAST.PRJ_SEQ = :seq ");
		sql_bal.append("    AND PARAM_BR.BRANCH_NBR IS NOT NULL ");
		sql_bal.append("    AND REC_ORG.BRANCH_NBR IS NOT NULL ");
		sql_bal.append("    AND LENGTH(REC_ORG.BRANCH_NBR) = 3 ");
		sql_bal.append("    AND TO_NUMBER(REC_ORG.BRANCH_NBR) >= 200 AND TO_NUMBER(REC_ORG.BRANCH_NBR) <= 900 ");
		sql_bal.append("  ) BR_LIST ");
		sql_bal.append("  LEFT JOIN ( ");
		sql_bal.append("    SELECT DISTINCT A.PRJ_SEQ, C.CURRENCY_STD_ID ");
		sql_bal.append("    FROM TBPMS_IPO_PARAM_MAST A ");
		sql_bal.append("    INNER JOIN TBPMS_IPO_PARAM_PRD B ON A.PRJ_SEQ = B.PRJ_SEQ ");
		sql_bal.append("    INNER JOIN TBPRD_FUND C ON B.PRD_ID = C.PRD_ID ");
		sql_bal.append("    WHERE A.PRJ_SEQ = :seq ");
		sql_bal.append("    UNION ");
		sql_bal.append("    SELECT TO_NUMBER(:seq) AS PRJ_SEQ, 'TWD' AS CURRENCY_STD_ID ");
		sql_bal.append("    FROM DUAL ");
		sql_bal.append("  ) PRD_LIST ON 1 = 1 ");
		sql_bal.append("  LEFT JOIN ( ");
		sql_bal.append("    SELECT IPO_RPT.DATA_DATE, IPO_RPT.PRJ_SEQ, IPO_RPT.BRANCH_NBR, IPO_RPT.TXN_CURR AS CURRENCY_STD_ID, SUM(BAL) AS BAL, SUM(ORG_BAL) AS ORG_BAL ");
		sql_bal.append("    FROM TBPMS_IPO_RPT IPO_RPT ");
		sql_bal.append("    WHERE IPO_RPT.PRJ_SEQ = :seq ");
		sql_bal.append("    GROUP BY IPO_RPT.DATA_DATE, IPO_RPT.PRJ_SEQ, IPO_RPT.BRANCH_NBR, IPO_RPT.TXN_CURR ");
		sql_bal.append("  ) IPO_COUNT ON IPO_COUNT.CURRENCY_STD_ID = PRD_LIST.CURRENCY_STD_ID AND IPO_COUNT.BRANCH_NBR = BR_LIST.BRANCH_NBR ");
		sql_bal.append(") ");

		sql_bal.append("SELECT BR.PRJ_SEQ, BR.COL_NO, SUM(BR.TARGET) AS TARGET ");
		sql_bal.append("FROM TBPMS_IPO_PARAM_BR BR ");
		sql_bal.append("WHERE BR.PRJ_SEQ = :seq ");
		sql_bal.append("AND EXISTS (SELECT 1 FROM BASE WHERE BASE.BRANCH_NBR = BR.BRANCH_NBR AND BASE.BRANCH_NBR IS NOT NULL) ");
		sql_bal.append("GROUP BY BR.PRJ_SEQ, BR.COL_NO ");
		sql_bal.append("ORDER BY BR.COL_NO ");
		
		condition.setQueryString(sql_bal.toString());
		condition.setObject("seq", inputVO.getPrj_seq());
		outputVO.setTotalbal(dam.exeQuery(condition));

		sendRtnObject(outputVO);

	}

	public void queryBRDetail (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS354InputVO inputVO = (PMS354InputVO) body;
		PMS354OutputVO outputVO = new PMS354OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append("WITH ORG_BASE AS ( ");
		sql.append("  SELECT DISTINCT ");
		sql.append("         PARAM_BR.PRJ_SEQ, MAST.END_DT, ");
		sql.append("         REC_ORG.REGION_CENTER_ID, REC_ORG.REGION_CENTER_NAME, REC_ORG.BRANCH_AREA_ID, REC_ORG.BRANCH_AREA_NAME, REC_ORG.BRANCH_NBR, REC_ORG.BRANCH_NAME, REC_ORG.BRANCH_CLS, ");
		sql.append("         REC_ORG.ORG_ID ");
		sql.append("  FROM TBPMS_IPO_PARAM_MAST MAST ");
		sql.append("  INNER JOIN TBPMS_IPO_PARAM_BR PARAM_BR ON MAST.PRJ_SEQ = PARAM_BR.PRJ_SEQ ");
		sql.append("  LEFT JOIN TBPMS_ORG_REC_N REC_ORG ON TRUNC(MAST.END_DT) BETWEEN TRUNC(REC_ORG.START_TIME) AND TRUNC(REC_ORG.END_TIME) AND PARAM_BR.BRANCH_NBR = REC_ORG.BRANCH_NBR ");
		sql.append("  WHERE MAST.PRJ_SEQ = :seq ");
		sql.append("  AND PARAM_BR.BRANCH_NBR IS NOT NULL ");
		sql.append("  AND REC_ORG.BRANCH_NBR IS NOT NULL ");
		sql.append("  AND LENGTH(REC_ORG.BRANCH_NBR) = 3 ");
		sql.append("  AND TO_NUMBER(REC_ORG.BRANCH_NBR) >= 200 AND TO_NUMBER(REC_ORG.BRANCH_NBR) <= 900 ");
		sql.append(") ");
		
		sql.append("SELECT TO_CHAR(TO_DATE(DATA_DATE, 'yyyy/MM/dd'), 'yyyy/MM/dd') AS DATA_DATE, REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_CLS, ");
		sql.append("       EMP_ID, EMP_NAME, CASE WHEN AO_CODE = '000' THEN NULL ELSE AO_CODE END AS AO_CODE, ");
		sql.append("       LISTAGG(TO_CHAR(PRD_ID), ',') WITHIN GROUP(ORDER BY PRD_ID) AS CURR_LIST, ");
		sql.append("       LISTAGG(TO_CHAR(BAL), ',') WITHIN GROUP(ORDER BY PRD_ID) AS PRD, "); // -- 台幣金額
		sql.append("       LISTAGG(TO_CHAR(ORG_BAL), ',') WITHIN GROUP(ORDER BY PRD_ID) AS ORG_PRD "); //  -- 原幣金額
		sql.append("FROM ( ");
		sql.append("  SELECT PRD_LIST.DATA_DATE, EMP_BASE.PRJ_SEQ, ");
		sql.append("         EMP_BASE.REGION_CENTER_ID, EMP_BASE.REGION_CENTER_NAME, EMP_BASE.BRANCH_AREA_ID, EMP_BASE.BRANCH_AREA_NAME, EMP_BASE.BRANCH_NBR, EMP_BASE.BRANCH_NAME, EMP_BASE.BRANCH_CLS, ");
		sql.append("         EMP_BASE.EMP_ID, EMP_BASE.EMP_NAME, EMP_BASE.AO_CODE, ");
		sql.append("         PRD_LIST.CURRENCY_STD_ID AS PRD_ID, NVL(IPO_BASE.BAL, 0) AS BAL, NVL(IPO_BASE.ORG_BAL, 0) AS ORG_BAL ");
		sql.append("  FROM ( ");
		sql.append("    SELECT ORG_BASE.PRJ_SEQ, ");
		sql.append("           ORG_BASE.REGION_CENTER_ID, ORG_BASE.REGION_CENTER_NAME, ORG_BASE.BRANCH_AREA_ID, ORG_BASE.BRANCH_AREA_NAME, ORG_BASE.BRANCH_NBR, ORG_BASE.BRANCH_NAME, ORG_BASE.BRANCH_CLS, ");
		sql.append("           REC_EMP.EMP_ID, REC_EMP.EMP_NAME, REC_AO.AO_CODE ");
		sql.append("    FROM ORG_BASE ");
		sql.append("    LEFT JOIN TBPMS_EMPLOYEE_REC_N REC_EMP ON TRUNC(ORG_BASE.END_DT) BETWEEN TRUNC(REC_EMP.START_TIME) AND TRUNC(REC_EMP.END_TIME) AND ORG_BASE.ORG_ID = REC_EMP.ORG_ID ");
		sql.append("    INNER JOIN TBPMS_SALES_AOCODE_REC REC_AO ON TRUNC(ORG_BASE.END_DT) BETWEEN TRUNC(REC_AO.START_TIME) AND TRUNC(REC_AO.END_TIME) AND REC_EMP.EMP_ID = REC_AO.EMP_ID ");
		sql.append("    WHERE ORG_BASE.PRJ_SEQ = :seq ");
		sql.append("    UNION ");
		sql.append("    SELECT ORG_BASE.PRJ_SEQ, ");
		sql.append("           ORG_BASE.REGION_CENTER_ID, ORG_BASE.REGION_CENTER_NAME, ORG_BASE.BRANCH_AREA_ID, ORG_BASE.BRANCH_AREA_NAME, ORG_BASE.BRANCH_NBR, ORG_BASE.BRANCH_NAME, ORG_BASE.BRANCH_CLS, ");
		sql.append("           '000000' AS EMP_ID, NULL AS EMP_NAME, '000' AS AO_CODE ");
		sql.append("    FROM ORG_BASE ");
		sql.append("    WHERE ORG_BASE.PRJ_SEQ = :seq ");
		sql.append("  ) EMP_BASE ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT DISTINCT (SELECT MAX(DATA_DATE) FROM TBPMS_IPO_RPT WHERE PRJ_SEQ = :seq) AS DATA_DATE, A.PRJ_SEQ, C.CURRENCY_STD_ID ");
		sql.append("    FROM TBPMS_IPO_PARAM_MAST A ");
		sql.append("    INNER JOIN TBPMS_IPO_PARAM_PRD B ON A.PRJ_SEQ = B.PRJ_SEQ ");
		sql.append("    INNER JOIN TBPRD_FUND C ON B.PRD_ID = C.PRD_ID ");
		sql.append("    WHERE A.PRJ_SEQ = :seq ");
		sql.append("    UNION ");
		sql.append("    SELECT (SELECT MAX(DATA_DATE) FROM TBPMS_IPO_RPT WHERE PRJ_SEQ = :seq) AS DATA_DATE, TO_NUMBER(:seq) AS PRJ_SEQ, 'TWD' AS CURRENCY_STD_ID ");
		sql.append("    FROM DUAL ");
		sql.append("  ) PRD_LIST ON 1 = 1 ");
		sql.append("  LEFT JOIN ( ");
		sql.append("    SELECT IPO_RPT.DATA_DATE, IPO_RPT.BRANCH_NBR, IPO_RPT.AO_CODE, IPO_RPT.EMP_ID, IPO_RPT.EMP_NAME, IPO_RPT.TXN_CURR AS CURRENCY_STD_ID, SUM(BAL) AS BAL, SUM(ORG_BAL) AS ORG_BAL ");
		sql.append("    FROM TBPMS_IPO_RPT IPO_RPT ");
		sql.append("    WHERE IPO_RPT.PRJ_SEQ = :seq ");
		sql.append("    GROUP BY IPO_RPT.DATA_DATE, IPO_RPT.BRANCH_NBR, IPO_RPT.AO_CODE, IPO_RPT.EMP_ID, IPO_RPT.EMP_NAME, IPO_RPT.TXN_CURR ");
		sql.append("  ) IPO_BASE ON EMP_BASE.BRANCH_NBR = IPO_BASE.BRANCH_NBR AND EMP_BASE.AO_CODE = IPO_BASE.AO_CODE AND EMP_BASE.EMP_ID = IPO_BASE.EMP_ID AND PRD_LIST.CURRENCY_STD_ID = IPO_BASE.CURRENCY_STD_ID ");
		sql.append("  WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("  AND EMP_BASE.BRANCH_NBR = :brid ");
			condition.setObject("brid", inputVO.getBranch_nbr());
		}
		
		sql.append(") ");
		sql.append("GROUP BY TO_CHAR(TO_DATE(DATA_DATE, 'yyyy/MM/dd'), 'yyyy/MM/dd'), REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_CLS, EMP_ID, EMP_NAME, AO_CODE ");
System.out.println(sql.toString());
System.out.println("seq:" + inputVO.getPrj_seq());
		condition.setQueryString(sql.toString());
		condition.setObject("seq", inputVO.getPrj_seq());
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalList(dam.exeQuery(condition));
		if (list.size() > 0) {
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/** 匯出EXCEL檔 **/
	public void export (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		PMS354OutputVO outputVO = (PMS354OutputVO) body;
		String fileName = "IPO專案戰報_" + sdf.format(new Date()) + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String filePath = Path + fileName;
		String uuid = UUID.randomUUID().toString();
		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");

		List<Map<String, Object>> list = outputVO.getTotalList();
		// 以下撈取全部資訊
		// 營運區 區域中心用途
		List<Map<String, Object>> list2 = outputVO.getEachSet();

		List<String> col1 = outputVO.getColList();

		List<Map<String, Object>> col2 = outputVO.getColList2();

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("IPO專案戰報");
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		// 資料 CELL型式
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);
		BigDecimal totalInvestMoney = new BigDecimal(0);

		// 資料 CELL型式
		XSSFCellStyle numberStyle = wb.createCellStyle();
		numberStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
		numberStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		numberStyle.setBorderBottom((short) 1);
		numberStyle.setBorderTop((short) 1);
		numberStyle.setBorderLeft((short) 1);
		numberStyle.setBorderRight((short) 1);

		// 表頭 CELL型式
		XSSFCellStyle headingStyle = wb.createCellStyle();
		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		headingStyle.setBorderBottom((short) 1);
		headingStyle.setBorderTop((short) 1);
		headingStyle.setBorderLeft((short) 1);
		headingStyle.setBorderRight((short) 1);
		headingStyle.setWrapText(true);

		Integer index = 0; // 行數
		List<String> headerLine = new ArrayList<String>();
		headerLine.add("業務處");
		headerLine.add("營運區");
		headerLine.add("分行代碼");
		headerLine.add("分行名稱");
		headerLine.add("組別");
		for (int i = 0; i < col1.size(); i++) {
			headerLine.add(col1.get(i) + " 銷量");
		}
		headerLine.add("折台幣銷量");

		// 理專為空不要匯幾億目標及達成率
		for (int i = 0; i < col2.size(); i++) {
			headerLine.add((String) col2.get(i).get("TARGET"));
			headerLine.add((String) col2.get(i).get("TARGET") + "達成率");
		}

		// Heading
		XSSFRow row = sheet.createRow(index);

		row = sheet.createRow(index);
		row.setHeightInPoints(25);
		for (int i = 0; i < headerLine.size(); i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine.get(i));
		}

		// Data row
		String[] mainLine = { "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "BRANCH_CLS" };

		index++;

		ArrayList<String> centerNameTempList = new ArrayList<String>(); // 比對用
		ArrayList<String> branchAreaNameTempList = new ArrayList<String>(); // 比對用
		ArrayList<String> branchTempList = new ArrayList<String>(); // 比對用
		Integer centerStartFlag = 0, branchAreaStartFlag = 0, branchStartFlag = 0; // rowspan用
		Integer centerEndFlag = 0, branchAreaEndFlag = 0, branchEndFlag = 0; // rowspan用
		Integer contectStartIndex = index;

		// 區域中心總合
		list = getSum(list, "REGION_CENTER_NAME");
		list = getSum(list, "BRANCH_AREA_NAME");
		BigDecimal prdSum = new BigDecimal(0);
		BigDecimal prdSum_reg = new BigDecimal(0);
		BigDecimal prdSum_are = new BigDecimal(0);
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);

			String centerName = (String) list.get(i).get("REGION_CENTER_NAME");
			String branchAreaName = (String) list.get(i).get("BRANCH_AREA_NAME");
			String branchName = (String) list.get(i).get("BRANCH_NAME");

			String[] prdList = {};
			if (null != list.get(i).get("ORG_PRD_LIST")) {
				prdList = list.get(i).get("ORG_PRD_LIST").toString().split(",");
			}

			String[] prdList2 = {};
			if (null != list.get(i).get("PRD_LIST")) {
				prdList2 = list.get(i).get("PRD_LIST").toString().split(",");
			}

			String[] tarList = {};
			if (null != list.get(i).get("TARGET_LIST")) {
				tarList = list.get(i).get("TARGET_LIST").toString().split(",");
			}

			int j = 0;
			// 銷量動態欄位組

			XSSFCell cell;
			for (j = 0; j < mainLine.length; j++) {
				cell = row.createCell(j);
				cell.setCellStyle(style);
				cell.setCellValue((String) list.get(i).get(mainLine[j]));

				if (j == 0 && centerNameTempList.indexOf(centerName) < 0) {
					centerNameTempList.add(centerName);

					if (centerEndFlag != 0) {
						if (StringUtils.isNotBlank(centerName) && centerName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j));
							// firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(i + 1, i + 1, j, j + 4));
						}
					}
					centerStartFlag = i;
					centerEndFlag = 0;
				} else if (j == 0 && StringUtils.isNotBlank(centerName)) {
					centerEndFlag = i;
				}

				if (j == 1 && StringUtils.isNotBlank(branchAreaName) && branchAreaNameTempList.indexOf(branchAreaName) < 0) {
					branchAreaNameTempList.add(branchAreaName);

					if (branchAreaName.indexOf("合計") > 0) {
						sheet.addMergedRegion(new CellRangeAddress(i + 1, i + 1, j, j + 3));

						if (branchAreaEndFlag != 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j));
						}
					}

					branchAreaStartFlag = i;
					branchAreaEndFlag = 0;
				} else if (j == 1 && StringUtils.isNotBlank(branchAreaName)) {
					branchAreaEndFlag = i;
				}

				if (j == 2 && StringUtils.isNotBlank(branchName) && branchTempList.indexOf(branchName) < 0) {
					branchTempList.add(branchName);

					if (branchEndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j));
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j + 1, j + 1));
					}
					branchStartFlag = i;
					branchEndFlag = 0;
				} else if (j == 2 && StringUtils.isNotBlank(branchName) && branchTempList.indexOf(branchName) > 0) {
					branchEndFlag = i;
				}

				if (j == 2 && i == (list.size() - 1)) { // 最後一筆
					if (branchEndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j));
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j + 1, j + 1));
					}
				} else if (j == 1 && i == (list.size() - 1)) {
					if (branchAreaEndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j + 2));
					}
				}
			}

			for (int x = 0; x < prdList.length; x++) {
				cell = row.createCell(j);
				cell.setCellStyle(numberStyle);
				// 所有產品欄位 usd twd 等資訊
				if (!branchName.equals("")) {
					if (StringUtils.equals(" ", prdList[x])) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(numFormat(new BigDecimal(prdList[x])));
					}
				} else {
					// 營運區 區域中心 CNY 等資訊 動態
					for (int tot = 0; tot < list2.size(); tot++) {
						if (list2.get(tot).get("DEPT_ID") == null) {
							continue;
						} else if ((list2.get(tot).get("DEPT_ID").toString() + " 合計").equals(branchAreaName) || (list2.get(tot).get("DEPT_ID").toString() + " 合計").equals(centerName)) {
							// cny twd動態計算區域等資訊
							cell.setCellValue(numFormat(new BigDecimal(list2.get(tot).get(col1.get(x)).toString())));
						}
					}
				}

				if (branchName.equals("")) {
					prdSum = prdSum.add(totalInvestMoney);
					totalInvestMoney = new BigDecimal(0);
				} else {
					prdSum = new BigDecimal(0);
					if (StringUtils.equals(" ", prdList2[x])) {
						totalInvestMoney = totalInvestMoney.add(BigDecimal.ZERO);
					} else {
						totalInvestMoney = totalInvestMoney.add(new BigDecimal(prdList2[x]));
					}
				}
				// 營運區最後一筆時候
				j++;
			}

			// 銷量合計
			cell = row.createCell(j);
			cell.setCellStyle(numberStyle);
			if (centerName.indexOf("合計") > 0) {
				// //折合台幣銷量 業務處用
				// cell.setCellValue(numFormat(new
				// BigDecimal(prdSum_reg.toString())));

				// 營運區 區域中心 CNY 等資訊 動態
				for (int tot = 0; tot < list2.size(); tot++) {
					if (list2.get(tot).get("DEPT_ID") == null) {
						continue;
					} else if ((list2.get(tot).get("DEPT_ID").toString() + " 合計").equals(centerName)) {
						// cny twd動態計算區域等資訊
						cell.setCellValue(numFormat(new BigDecimal(list2.get(tot).get("BAL").toString())));
						prdSum_reg = new BigDecimal(list2.get(tot).get("BAL").toString());
					}
				}
			} else {

				if (!branchName.equals("")) {
					BigDecimal prdSums = new BigDecimal(0);
					for (int x = 0; x < prdList.length; x++) {
						if (StringUtils.equals(" ", prdList2[x])) {
							prdSums = prdSums.add(BigDecimal.ZERO);
						} else {
							prdSums = prdSums.add(new BigDecimal(prdList2[x]));
						}
					}
					prdSum_reg = prdSum_reg.add(prdSums);

					prdSum_are = prdSum_are.add(prdSums);
					// 折合台幣銷量 分行
					cell.setCellValue(numFormat(new BigDecimal(prdSums.toString())));
				} else {
					// 折合台幣銷量 營運區
					// cell.setCellValue(numFormat(new
					// BigDecimal(prdSum.toString())));
					// 營運區 區域中心 CNY 等資訊 動態
					for (int tot = 0; tot < list2.size(); tot++) {
						if (list2.get(tot).get("DEPT_ID") == null) {
							continue;
						} else if ((list2.get(tot).get("DEPT_ID").toString() + " 合計").equals(branchAreaName)) {
							// cny twd動態計算區域等資訊
							cell.setCellValue(numFormat(new BigDecimal(list2.get(tot).get("BAL").toString())));
							prdSum_are = new BigDecimal(list2.get(tot).get("BAL").toString());
						}
					}

				}
			}
			j++;
			// 目標動態欄位
			if (tarList.length >= 1) {
				for (int y = 0; y < tarList.length; y++) {
					cell = row.createCell(j);
					cell.setCellStyle(numberStyle);
					BigDecimal tarVal = new BigDecimal(tarList[y]);
					if (branchName.equals("")) {
						// 營運區 區域中心
						for (int tot = 0; tot < list2.size(); tot++) {
							if (list2.get(tot).get("DEPT_ID") == null) {
								continue;
							}

							else if ((list2.get(tot).get("DEPT_ID").toString() + " 合計").equals(branchAreaName) || (list2.get(tot).get("DEPT_ID").toString() + " 合計").equals(centerName)) {
								// cny twd動態計算區域等資訊
								cell.setCellValue(numFormat(new BigDecimal(list2.get(tot).get(y + 1 + "").toString())));
							}
						}
					} else {
						cell.setCellValue(numFormat(new BigDecimal(tarList[y])));
					}

					j++;

					cell = row.createCell(j);
					cell.setCellStyle(numberStyle);
					cell.setCellValue("");
					if (centerName.indexOf("合計") > 0) {
						// 業務處 合計計算 百分比
						// 區域中心
						for (int tot = 0; tot < list2.size(); tot++) {
							if (list2.get(tot).get("DEPT_ID") == null) {
								continue;
							}

							else if ((list2.get(tot).get("DEPT_ID").toString() + " 合計").equals(centerName))
								cell.setCellValue((StringUtils.equals("0", numFormat(new BigDecimal(list2.get(tot).get(y + 1 + "").toString()))) ? "0" : prdSum_reg.divide(new BigDecimal(list2.get(tot).get(y + 1 + "").toString()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))) + "%");
						}
					} else {

						if (!branchName.equals("")) {
							BigDecimal prdSums = new BigDecimal(0);
							for (int x = 0; x < prdList.length; x++) {
								if (StringUtils.equals(" ", prdList2[x])) {
									prdSums = prdSums.add(BigDecimal.ZERO);
								} else {
									prdSums = prdSums.add(new BigDecimal(prdList2[x]));
								}
							}
							// 分行 合計計算 百分比
							cell.setCellValue((StringUtils.equals("0", numFormat(new BigDecimal(tarList[y]))) ? "0" : prdSums.divide(tarVal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))) + "%");

						} else {
							// 營運區 合計計算 百分比
							for (int tot = 0; tot < list2.size(); tot++) {
								if (list2.get(tot).get("DEPT_ID") == null) {
									continue;
								} else if ((list2.get(tot).get("DEPT_ID").toString() + " 合計").equals(branchAreaName)) {
									// cny twd動態計算區域等資訊
									cell.setCellValue((StringUtils.equals("0", numFormat(new BigDecimal(list2.get(tot).get(y + 1 + "").toString()))) ? "0" : prdSum_are.divide(new BigDecimal(list2.get(tot).get(y + 1 + "").toString()), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))) + "%");
								}

							}
						}
					}

					j++;
				}
				if (branchName.equals(""))
					// 營運區加總完歸0
					prdSum_are = new BigDecimal(0);
			}
			if (centerName.indexOf("合計") > 0) {
				// 區域中心加總完歸0
				prdSum_reg = new BigDecimal(0);
			}

			index++;
			// 以下全行總計
			if (list.size() - 1 == i) {
				// 總計用途
				List<Map<String, Object>> totalbal = outputVO.getTotalbal();
				List<Map<String, Object>> totalprd = outputVO.getTotalprd();
				row = sheet.createRow(index);

				for (j = 0; j < mainLine.length; j++) {
					cell = row.createCell(j); // 前四位區
					cell.setCellStyle(style);
					cell.setCellValue("全行總計");
				}
				sheet.addMergedRegion(new CellRangeAddress(i + 2, i + 2, 0, 4));
				// 折合台幣銷量
				BigDecimal cell_bal = new BigDecimal(0);
				for (int prd = 0; prd < totalprd.size(); prd++) {
					cell = row.createCell(j);
					cell.setCellStyle(numberStyle);
					BigDecimal cellva = new BigDecimal(totalprd.get(prd).get("BAL").toString());
					BigDecimal cellva_TWD = (totalprd.get(prd).get("TW_BAL")) != null ? new BigDecimal(totalprd.get(prd).get("TW_BAL").toString()) : new BigDecimal(0);
					cell_bal = cell_bal.add(cellva_TWD);
					cell.setCellValue(numFormat(cellva));
					j++;
				}
				cell = row.createCell(j);
				cell.setCellStyle(numberStyle);
				cell.setCellValue(numFormat(cell_bal));
				j++;

				for (int ba = 0; ba < totalbal.size(); ba++) {
					cell = row.createCell(j);
					cell.setCellStyle(numberStyle);
					BigDecimal cellva = new BigDecimal(totalbal.get(ba).get("TARGET").toString());
					cell.setCellValue(numFormat(cellva) + ((ba % 2) == 1 ? ".00%" : ""));

					j++;
				}
			}

		}

		String tempName = UUID.randomUUID().toString();
		// //路徑結合
		File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
		// //絕對路徑建檔
		wb.write(new FileOutputStream(f));

		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName);

		this.sendRtnObject(null);
	}

	private List<Map<String, Object>> getSum (List<Map<String, Object>> list, String mapKey) {

		for (int i = 0; i < list.size(); i++) {
			String centerName = (String) list.get(i).get("REGION_CENTER_NAME");
			String branchAreaName = (String) list.get(i).get("BRANCH_AREA_NAME");

			if ((((i + 1) < list.size() && !StringUtils.equals((StringUtils.equals(mapKey, "REGION_CENTER_NAME")) ? centerName : branchAreaName, (String) list.get(i + 1).get(mapKey))) || (i + 1) == list.size()) && StringUtils.isNotBlank((StringUtils.equals(mapKey, "REGION_CENTER_NAME")) ? centerName : branchAreaName) && ((StringUtils.equals(mapKey, "REGION_CENTER_NAME")) ? centerName : branchAreaName).indexOf("合計") < 0) {

				// ==== 銷量加總 ==== start
				Map<String, Integer> countPMap = new HashMap<String, Integer>();
				Map<String, Integer> countPMap2 = new HashMap<String, Integer>();
				for (int k = 0; k < list.size(); k++) {
					if (StringUtils.equals((StringUtils.equals(mapKey, "REGION_CENTER_NAME")) ? centerName : branchAreaName, (String) list.get(k).get(mapKey))) {
						String[] prdListByCenterArray = {};
						String[] prdListByCenterArray_ntd = {};
						if (null != list.get(k).get("ORG_PRD_LIST")) {
							prdListByCenterArray = list.get(k).get("ORG_PRD_LIST").toString().split(",");
							prdListByCenterArray_ntd = list.get(k).get("ORG_PRD_LIST").toString().split(",");
						}

						for (int countC = 0; countC < prdListByCenterArray_ntd.length; countC++) {
							if (null != countPMap.get(String.valueOf(countC)) && !StringUtils.equals(" ", prdListByCenterArray_ntd[countC])) {
								countPMap2.put(String.valueOf(countC), countPMap.get(String.valueOf(countC)) + Integer.valueOf(StringUtils.equals(" ", prdListByCenterArray_ntd[countC]) ? "0" :prdListByCenterArray_ntd[countC]));
							} else {
								countPMap2.put(String.valueOf(countC), Integer.valueOf(StringUtils.equals(" ", prdListByCenterArray_ntd[countC]) ? "0" :prdListByCenterArray_ntd[countC]));
							}
						}
						for (int countC = 0; countC < prdListByCenterArray.length; countC++) {
							if (null != countPMap.get(String.valueOf(countC))) {
								countPMap.put(String.valueOf(countC), countPMap.get(String.valueOf(countC)) + Integer.valueOf(StringUtils.equals(" ", prdListByCenterArray_ntd[countC]) ? "0" :prdListByCenterArray_ntd[countC]));
							} else {
								countPMap.put(String.valueOf(countC), Integer.valueOf(StringUtils.equals(" ", prdListByCenterArray[countC]) ? "0" :prdListByCenterArray[countC]));
							}
						}
					}
				}

				String prdCount = "";
				String prdCount2 = "";
				for (int k = 0; k < countPMap.size(); k++) {
					if ((k + 1) == countPMap.size()) {
						prdCount = prdCount + countPMap.get(String.valueOf(k));
					} else {
						prdCount = prdCount + countPMap.get(String.valueOf(k)) + ",";
					}
				}
				for (int k = 0; k < countPMap2.size(); k++) {
					if ((k + 1) == countPMap2.size()) {
						prdCount2 = prdCount2 + countPMap2.get(String.valueOf(k));
					} else {
						prdCount2 = prdCount2 + countPMap2.get(String.valueOf(k)) + ",";
					}
				}
				// ==== 銷量加總 ==== end

				// ==== 達成率加總 ==== start
				Map<String, Integer> countRMap = new HashMap<String, Integer>();
				for (int k = 0; k < list.size(); k++) {
					if (StringUtils.equals((StringUtils.equals(mapKey, "REGION_CENTER_NAME")) ? centerName : branchAreaName, (String) list.get(k).get(mapKey))) {
						String[] tarListByCenterArray = {};
						if (null != list.get(k).get("TARGET_LIST")) {
							tarListByCenterArray = list.get(k).get("TARGET_LIST").toString().split(",");
						}

						for (int countC = 0; countC < tarListByCenterArray.length; countC++) {
							if (null != countRMap.get(String.valueOf(countC))) {
								countRMap.put(String.valueOf(countC), countRMap.get(String.valueOf(countC)) + Integer.valueOf(tarListByCenterArray[countC]));
							} else {
								countRMap.put(String.valueOf(countC), Integer.valueOf(tarListByCenterArray[countC]));
							}
						}
					}
				}
				String tarCount = "";
				for (int k = 0; k < countRMap.size(); k++) {
					if ((k + 1) == countRMap.size()) {
						tarCount = tarCount + countRMap.get(String.valueOf(k));
					} else {
						tarCount = tarCount + countRMap.get(String.valueOf(k)) + ",";
					}
				}
				// ==== 達成率加總 ==== end

				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("REGION_CENTER_NAME", (StringUtils.equals(mapKey, "REGION_CENTER_NAME")) ? centerName + " 合計" : centerName);
				detail.put("BRANCH_AREA_NAME", (StringUtils.equals(mapKey, "REGION_CENTER_NAME")) ? "" : branchAreaName + " 合計");
				detail.put("BRANCH_ID", "");
				detail.put("BRANCH_NAME", "");
				detail.put("BRANCH_CLS", "");

				detail.put("ORG_PRD_LIST", prdCount);
				detail.put("PRD_LIST", prdCount2);
				detail.put("TARGET_LIST", tarCount);

				list.add(i + 1, detail);
			}
		}

		return list;
	}

	/** 檢查Map取出欄位是否為Null **/
	private String checkIsNull (Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/** 數字格式 **/
	private String numFormat (BigDecimal num) {

		return new DecimalFormat("#,###,###,###").format(num);
	}

	/** 取得動態表格資料 **/
	public void queryTotal (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS354InputVO inputVO = (PMS354InputVO) body;
		PMS354OutputVO outputVO = new PMS354OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String[] strPrd = inputVO.getSumCollect().get(0).get("BRANCH_AREA_NAME").toString().split(",");
		String[] strTar = inputVO.getSumCollect().get(1).get("BRANCH_AREA_NAME").toString().split(",");
		if (!strPrd[0].equals("") && !strTar[0].equals("")) {
			StringBuffer sql = new StringBuffer();
			
			sql.append("WITH BRANCH_BAL AS ( ");
			sql.append("  SELECT BR_LIST.PRJ_SEQ, BR_LIST.REGION_CENTER_ID, BR_LIST.REGION_CENTER_NAME, BR_LIST.BRANCH_AREA_ID, BR_LIST.BRANCH_AREA_NAME, BR_LIST.BRANCH_NBR, BR_LIST.BRANCH_NAME, BR_LIST.BRANCH_CLS, ");
			sql.append("         PRD_LIST.CURRENCY_STD_ID AS PRD_ID, NVL(IPO_COUNT.BAL, 0) AS BAL, NVL(IPO_COUNT.ORG_BAL, 0) AS ORG_BAL ");
			sql.append("  FROM ( ");
			sql.append("    SELECT DISTINCT ");
			sql.append("           PARAM_BR.PRJ_SEQ, ");
			sql.append("           REC_ORG.REGION_CENTER_ID, REC_ORG.REGION_CENTER_NAME, REC_ORG.BRANCH_AREA_ID, REC_ORG.BRANCH_AREA_NAME, REC_ORG.BRANCH_NBR, REC_ORG.BRANCH_NAME, REC_ORG.BRANCH_CLS ");
			sql.append("    FROM TBPMS_IPO_PARAM_MAST MAST ");
			sql.append("    INNER JOIN TBPMS_IPO_PARAM_BR PARAM_BR ON MAST.PRJ_SEQ = PARAM_BR.PRJ_SEQ ");
			sql.append("    LEFT JOIN TBPMS_ORG_REC_N REC_ORG ON TRUNC(MAST.END_DT) BETWEEN TRUNC(REC_ORG.START_TIME) AND TRUNC(REC_ORG.END_TIME) AND PARAM_BR.BRANCH_NBR = REC_ORG.BRANCH_NBR ");
			sql.append("    WHERE MAST.PRJ_SEQ = :seq ");
			sql.append("    AND PARAM_BR.BRANCH_NBR IS NOT NULL ");
			sql.append("    AND REC_ORG.BRANCH_NBR IS NOT NULL ");
			sql.append("    AND LENGTH(REC_ORG.BRANCH_NBR) = 3 ");
			sql.append("    AND TO_NUMBER(REC_ORG.BRANCH_NBR) >= 200 AND TO_NUMBER(REC_ORG.BRANCH_NBR) <= 900 ");
			sql.append("  ) BR_LIST ");
			sql.append("  LEFT JOIN ( ");
			sql.append("    SELECT DISTINCT A.PRJ_SEQ, C.CURRENCY_STD_ID ");
			sql.append("    FROM TBPMS_IPO_PARAM_MAST A ");
			sql.append("    INNER JOIN TBPMS_IPO_PARAM_PRD B ON A.PRJ_SEQ = B.PRJ_SEQ ");
			sql.append("    INNER JOIN TBPRD_FUND C ON B.PRD_ID = C.PRD_ID ");
			sql.append("    WHERE A.PRJ_SEQ = :seq ");
			sql.append("    UNION ");
			sql.append("    SELECT TO_NUMBER(:seq) AS PRJ_SEQ, 'TWD' AS CURRENCY_STD_ID ");
			sql.append("    FROM DUAL ");
			sql.append("  ) PRD_LIST ON 1 = 1 ");
			sql.append("  LEFT JOIN ( ");
			sql.append("    SELECT IPO_RPT.PRJ_SEQ, IPO_RPT.BRANCH_NBR, IPO_RPT.TXN_CURR AS CURRENCY_STD_ID, SUM(BAL) AS BAL, SUM(ORG_BAL) AS ORG_BAL ");
			sql.append("    FROM TBPMS_IPO_RPT IPO_RPT ");
			sql.append("    WHERE IPO_RPT.PRJ_SEQ = :seq ");
			sql.append("    GROUP BY IPO_RPT.PRJ_SEQ, IPO_RPT.BRANCH_NBR, IPO_RPT.TXN_CURR ");
			sql.append("  ) IPO_COUNT ON IPO_COUNT.CURRENCY_STD_ID = PRD_LIST.CURRENCY_STD_ID AND IPO_COUNT.BRANCH_NBR = BR_LIST.BRANCH_NBR ");
			sql.append(") ");
			sql.append(", TAR_BAL AS ( ");
			sql.append("  SELECT BR.PRJ_SEQ AS TR_P_SEQ, BR.BRANCH_NBR AS TR_BN, BR.COL_NO, BR.TARGET ");
			sql.append("  FROM TBPMS_IPO_PARAM_BR BR ");
			sql.append("  WHERE BR.PRJ_SEQ = :seq ");
			sql.append(") ");
			sql.append(", BAL_COUNT AS ( ");
			sql.append("  SELECT * ");
			sql.append("  FROM ( ");
			sql.append("    SELECT * ");
			sql.append("    FROM ( ");
			sql.append("      SELECT BRANCH_BAL.REGION_CENTER_ID, BRANCH_BAL.REGION_CENTER_NAME, BRANCH_BAL.BRANCH_AREA_ID, BRANCH_BAL.BRANCH_AREA_NAME, BRANCH_BAL.BRANCH_NBR, BRANCH_BAL.BRANCH_NAME, BRANCH_BAL.BRANCH_CLS, ");
			sql.append("             BRANCH_BAL.PRD_ID, ");
			sql.append("             NVL(BR_BAL.BAL, 0) AS BAL, ");
			sql.append("             NVL(BRANCH_BAL.ORG_BAL, 0) AS ORG_BAL ");
			sql.append("      FROM BRANCH_BAL ");
			sql.append("      LEFT JOIN ( ");
			sql.append("        SELECT BRANCH_NBR, SUM(BAL) AS BAL ");
			sql.append("        FROM BRANCH_BAL ");
			sql.append("        GROUP BY BRANCH_NBR ");
			sql.append("      ) BR_BAL ON BRANCH_BAL.BRANCH_NBR = BR_BAL.BRANCH_NBR ");
			sql.append("    ) ");
			sql.append("    PIVOT (SUM(ORG_BAL) FOR PRD_ID IN (");
			
			for (int i = 0; i < strPrd.length; i++) {
				if (strPrd.length - 1 != i)
					sql.append("'" + strPrd[i].toString() + "' AS " + " \"" + strPrd[i].toString() + "\", ");
				else
					sql.append("'" + strPrd[i].toString() + "' AS " + " \"" + strPrd[i].toString() + "\" ");
	
			}
			
			sql.append("                                      )) ");
			sql.append("  ) BAL ");
			sql.append("  LEFT JOIN ( ");
			sql.append("    SELECT * ");
			sql.append("    FROM TAR_BAL ");
			sql.append("    PIVOT (SUM(TARGET) FOR COL_NO IN (");
			
			for (int i = 0; i < strTar.length; i++) {
				if (strTar.length - 1 != i)
					sql.append("'" + strTar[i].toString() + "' AS " + " COL" + strTar[i].toString() + ", ");
				else
					sql.append("'" + strTar[i].toString() + "' AS " + " COL" + strTar[i].toString() + " ");
			}
		
			sql.append("                                     )) ");
			sql.append("  ) TAR ON BAL.BRANCH_NBR = TAR.TR_BN ");
			sql.append(") ");

			sql.append("SELECT BRANCH_AREA_NAME AS DEPT_ID, BRANCH_AREA_NAME AS DEPT_ID2, SUM(BAL) AS BAL, ");
			
			for (int i = 0; i < strPrd.length; i++) {
				sql.append("SUM(").append(strPrd[i].toString()).append(") AS ").append(strPrd[i].toString()).append(", ");
			}
			
			for (int i = 0; i < strTar.length; i++) {
				sql.append("SUM(COL").append(strTar[i].toString()).append(") AS \"").append(strTar[i].toString()).append("\", ");
			}
			
			sql.append("NULL AS FLAG ");
			
			sql.append("FROM BAL_COUNT ");
			sql.append("GROUP BY BRANCH_AREA_NAME ");
			
			sql.append("UNION ");
			
			sql.append("SELECT REGION_CENTER_NAME AS DEPT_ID, REGION_CENTER_NAME AS DEPT_ID2, SUM(BAL) AS BAL, ");
			
			for (int i = 0; i < strPrd.length; i++) {
				sql.append("SUM(").append(strPrd[i].toString()).append(") AS ").append(strPrd[i].toString()).append(", ");
			}
			
			for (int i = 0; i < strTar.length; i++) {
				sql.append("SUM(COL").append(strTar[i].toString()).append(") AS \"").append(strTar[i].toString()).append("\", ");
			}
			
			sql.append("NULL AS FLAG ");
			
			sql.append("FROM BAL_COUNT ");
			sql.append("GROUP BY REGION_CENTER_NAME ");
			condition.setQueryString(sql.toString());

			condition.setObject("seq", inputVO.getPrj_seq());

			outputVO.setEachSet(dam.exeQuery(condition));
		}
		
		this.sendRtnObject(outputVO);
	}

}