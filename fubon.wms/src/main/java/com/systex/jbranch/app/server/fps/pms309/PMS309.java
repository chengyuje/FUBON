package com.systex.jbranch.app.server.fps.pms309;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
import com.systex.jbranch.app.server.fps.pms303.PMS303InputVO;
import com.systex.jbranch.app.server.fps.pms304.PMS304InputVO;
import com.systex.jbranch.app.server.fps.pms304.PMS304OutputVO;
import com.systex.jbranch.app.server.fps.pms308.PMS308InputVO;
import com.systex.jbranch.app.server.fps.pms308.PMS308OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information 
 * ******************************
 * Project          : fubon.wms
 * JDK version used : JDK 1.7
 * Description      : 各商品收益/各商品銷量 Controller
 * Comments Name    : PMS310.java
 * Author  			: Frank
 * Date    			: 2016年06月29日 
 * Version 			: 1.01 
 * Editor  			: Kevin
 * Editor Date 		: 2017年02月01日
 * ******************************
 */

@Component("pms309")
@Scope("request")
public class PMS309 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS309.class);
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	/** 取得可視範圍 **/
	public void getOrgInfo(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS309InputVO inputVO   = (PMS309InputVO) body;
		PMS309OutputVO outputVO = new PMS309OutputVO();
		String loginID  = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		// 日期查詢
		if ("1".equals(inputVO.getSrchDate())) {
			sql.append(" SELECT * FROM table( ");
			sql.append(" 	FN_getVISUAL_RANGE( ");
			sql.append(" 	:purview_type, null, :e_dt, :emp_id, ");
			sql.append(" 	:org_id, :v_ao_flag, :v_emp_id, NULL) ");
			sql.append("	) ");
			
			if (inputVO.getsCreDate() != null) {
				condition.setObject("e_dt", inputVO.getsCreDate());
			} else
				condition.setObject("e_dt", stamp);
		}
		// 月區間查詢
		if ("2".equals(inputVO.getSrchDate())) {
			sql.append(" SELECT * FROM table( ");
			sql.append(" 	FN_getVISUAL_RANGE( ");
			sql.append(" 		:purview_type, :s_dt, :e_dt, :emp_id, ");
			sql.append(" 		:org_id, :v_ao_flag, :v_emp_id, NULL) ");
			sql.append(" ) ");
			
			if (StringUtils.isNotBlank(inputVO.getDataMonth_S())) {
				Date fstDate = getMonthFstDate(inputVO.getDataMonth_S());
				condition.setObject("s_dt", fstDate);
			} else
				condition.setObject("s_dt", stamp);

			if (StringUtils.isNotBlank(inputVO.getDataMonth_E())) {
				Date lastDate = getMonthLastDate(inputVO.getDataMonth_E());
				condition.setObject("e_dt", lastDate);
			} else
				condition.setObject("e_dt", stamp);
		}

		condition.setQueryString(sql.toString());
		condition.setObject("purview_type", "OTHER"); // 非業績報表
		condition.setObject("emp_id", loginID);
		condition.setObject("org_id", null);
		condition.setObject("v_ao_flag", null);
		condition.setObject("v_emp_id", null);

		outputVO.setOrgList(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}

	// ----資料查詢------
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS309InputVO inputVO = (PMS309InputVO) body;
		PMS309OutputVO outputVO = new PMS309OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		try {

            //  === 依報表日期查詢 ===
            if (inputVO.getSrchDate().equals("1")) {
            	
            	condition = this.queryDataByDate(condition, inputVO);
            }
            //  === 依計績月份累計 ===
            else{
                condition = this.queryDataByPerformance(condition, inputVO);
            }

			ResultIF list1 = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			outputVO.setTotalList(dam.exeQuery(condition));
			
			int totalPage = list1.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list1);
			outputVO.setTotalRecord(list1.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	
	private QueryConditionIF queryDataByDate(QueryConditionIF condition,
			PMS309InputVO inputVO) throws JBranchException, ParseException {

		// === 依報表日期查詢 ===
		
		DataAccessManager dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sql.append(" WITH ORIGINAL_VIEW AS ( ");
		sql.append("     SELECT BRSAL.REGION_CENTER_ID, BRSAL.REGION_CENTER_NAME, BRSAL.BRANCH_AREA_ID, BRSAL.BRANCH_AREA_NAME ");
		sql.append("          , BRSAL.BRANCH_NBR, BRSAL.BRANCH_NAME, BRSAL.BRANCH_CLS ");

		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("      , BRSAL.ITEM_01_SALE, BRSAL.ITEM_02_SALE, BRSAL.ITEM_03_SALE, BRSAL.ITEM_04_SALE, BRSAL.ITEM_05_SALE ");
			sql.append("      , BRSAL.ITEM_06_SALE, BRSAL.ITEM_17_SALE, BRSAL.ITEM_10_SALE, BRSAL.ITEM_11_SALE, BRSAL.ITEM_12_SALE ");
			sql.append("      , BRSAL.ITEM_16_SALE, BRSAL.MTD_INV_SALE, BRSAL.MTD_INS_SALE, BRSAL.MTD_ALL_SALE ");
		} else {
			sql.append("      , BRSAL.ITEM_01_FEE, BRSAL.ITEM_02_FEE, BRSAL.ITEM_03_FEE, BRSAL.ITEM_04_FEE, BRSAL.ITEM_05_FEE ");
			sql.append("      , BRSAL.ITEM_06_FEE, BRSAL.ITEM_17_FEE, BRSAL.ITEM_10_FEE, BRSAL.ITEM_11_FEE, BRSAL.ITEM_12_FEE ");
			sql.append("      , BRSAL.ITEM_16_FEE, BRSAL.ITEM_09_FEE, BRSAL.ITEM_14_FEE, BRSAL.ITEM_15_FEE ");
			sql.append("	  , BRSAL.MTD_INV_FEE_PRD AS MTD_INV_FEE, BRSAL.MTD_INS_FEE, BRSAL.MTD_ALL_FEE ");
		}

		sql.append("     FROM TBPMS_BR_DAY_PROFIT_MYTD BRSAL ");
		sql.append("     WHERE BRSAL.DATA_DATE = :data_date  ");
		sql.append(" ), ");
		sql.append(" AREA AS ( ");
		sql.append("   SELECT OV.REGION_CENTER_ID, OV.REGION_CENTER_NAME, OV.BRANCH_AREA_ID, BRANCH_AREA_NAME,'AREA' as BRANCH_NBR ,'AREA' as BRANCH_NAME, 'AREA' as BRANCH_CLS ");
		
		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("    , SUM(ITEM_01_SALE) as ITEM_01_SALE, SUM(ITEM_02_SALE) as ITEM_02_SALE, SUM(ITEM_03_SALE) as ITEM_03_SALE ");
			sql.append("    , SUM(ITEM_04_SALE) as ITEM_04_SALE, SUM(ITEM_05_SALE) as ITEM_05_SALE, SUM(ITEM_06_SALE) as ITEM_06_SALE ");
			sql.append("    , SUM(ITEM_17_SALE) as ITEM_17_SALE, SUM(ITEM_10_SALE) as ITEM_10_SALE, SUM(ITEM_11_SALE) as ITEM_11_SALE ");
			sql.append("    , SUM(ITEM_12_SALE) as ITEM_12_SALE, SUM(ITEM_16_SALE) as ITEM_16_SALE, SUM(MTD_INV_SALE) as MTD_INV_SALE ");
			sql.append("    , SUM(MTD_INS_SALE) as MTD_INS_SALE, SUM(MTD_ALL_SALE) as MTD_ALL_SALE ");
		} else {
			sql.append("    , SUM(ITEM_01_FEE) as ITEM_01_FEE, SUM(ITEM_02_FEE) as ITEM_02_FEE, SUM(ITEM_03_FEE) as ITEM_03_FEE ");
			sql.append("    , SUM(ITEM_04_FEE) as ITEM_04_FEE, SUM(ITEM_05_FEE) as ITEM_05_FEE, SUM(ITEM_06_FEE) as ITEM_06_FEE ");
			sql.append("    , SUM(ITEM_17_FEE) as ITEM_17_FEE, SUM(ITEM_10_FEE) as ITEM_10_FEE, SUM(ITEM_11_FEE) as ITEM_11_FEE ");
			sql.append("    , SUM(ITEM_12_FEE) as ITEM_12_FEE, SUM(ITEM_16_FEE) as ITEM_16_FEE, SUM(ITEM_09_FEE) as ITEM_09_FEE ");
			sql.append("    , SUM(ITEM_14_FEE) as ITEM_14_FEE, SUM(ITEM_15_FEE) as ITEM_15_FEE ");
			sql.append("    , SUM(MTD_INV_FEE) as MTD_INV_FEE, SUM(MTD_INS_FEE) as MTD_INS_FEE, SUM(MTD_ALL_FEE) as MTD_ALL_FEE ");
		}
		sql.append("   FROM ORIGINAL_VIEW OV ");
		sql.append("   GROUP BY OV.REGION_CENTER_NAME,OV.BRANCH_AREA_NAME,OV.REGION_CENTER_ID,OV.BRANCH_AREA_ID ");
		sql.append(" ), ");
		sql.append(" REGION AS ( ");
		sql.append("   SELECT OV.REGION_CENTER_ID, OV.REGION_CENTER_NAME, 'REGION' as BRANCH_AREA_ID, 'REGION' as BRANCH_AREA_NAME,'REGION' as BRANCH_NBR ,'REGION' as BRANCH_NAME, 'REGION' as BRANCH_CLS ");
		
		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("    , SUM(ITEM_01_SALE) as ITEM_01_SALE, SUM(ITEM_02_SALE) as ITEM_02_SALE, SUM(ITEM_03_SALE) as ITEM_03_SALE ");
			sql.append("    , SUM(ITEM_04_SALE) as ITEM_04_SALE, SUM(ITEM_05_SALE) as ITEM_05_SALE, SUM(ITEM_06_SALE) as ITEM_06_SALE ");
			sql.append("    , SUM(ITEM_17_SALE) as ITEM_17_SALE, SUM(ITEM_10_SALE) as ITEM_10_SALE, SUM(ITEM_11_SALE) as ITEM_11_SALE ");
			sql.append("    , SUM(ITEM_12_SALE) as ITEM_12_SALE, SUM(ITEM_16_SALE) as ITEM_16_SALE, SUM(MTD_INV_SALE) as MTD_INV_SALE ");
			sql.append("    , SUM(MTD_INS_SALE) as MTD_INS_SALE, SUM(MTD_ALL_SALE) as MTD_ALL_SALE ");
		} else {
			sql.append("    , SUM(ITEM_01_FEE) as ITEM_01_FEE, SUM(ITEM_02_FEE) as ITEM_02_FEE, SUM(ITEM_03_FEE) as ITEM_03_FEE ");
			sql.append("    , SUM(ITEM_04_FEE) as ITEM_04_FEE, SUM(ITEM_05_FEE) as ITEM_05_FEE, SUM(ITEM_06_FEE) as ITEM_06_FEE ");
			sql.append("    , SUM(ITEM_17_FEE) as ITEM_17_FEE, SUM(ITEM_10_FEE) as ITEM_10_FEE, SUM(ITEM_11_FEE) as ITEM_11_FEE ");
			sql.append("    , SUM(ITEM_12_FEE) as ITEM_12_FEE, SUM(ITEM_16_FEE) as ITEM_16_FEE, SUM(ITEM_09_FEE) as ITEM_09_FEE ");
			sql.append("    , SUM(ITEM_14_FEE) as ITEM_14_FEE, SUM(ITEM_15_FEE) as ITEM_15_FEE ");
			sql.append("    , SUM(MTD_INV_FEE) as MTD_INV_FEE, SUM(MTD_INS_FEE) as MTD_INS_FEE, SUM(MTD_ALL_FEE) as MTD_ALL_FEE ");
		}
		sql.append("   FROM AREA OV ");
		sql.append("   GROUP BY OV.REGION_CENTER_NAME,OV.REGION_CENTER_ID ");
		sql.append(" ), ");
		sql.append(" TOTAL AS( ");
		sql.append("   SELECT 'TOTAL' as REGION_CENTER_ID, 'TOTAL' as REGION_CENTER_NAME, 'TOTAL' as BRANCH_AREA_ID, 'TOTAL' as BRANCH_AREA_NAME,'TOTAL' as BRANCH_NBR ,'TOTAL' as BRANCH_NAME, 'TOTAL' as BRANCH_CLS ");
		
		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("    , SUM(ITEM_01_SALE) as ITEM_01_SALE, SUM(ITEM_02_SALE) as ITEM_02_SALE, SUM(ITEM_03_SALE) as ITEM_03_SALE ");
			sql.append("    , SUM(ITEM_04_SALE) as ITEM_04_SALE, SUM(ITEM_05_SALE) as ITEM_05_SALE, SUM(ITEM_06_SALE) as ITEM_06_SALE ");
			sql.append("    , SUM(ITEM_17_SALE) as ITEM_17_SALE, SUM(ITEM_10_SALE) as ITEM_10_SALE, SUM(ITEM_11_SALE) as ITEM_11_SALE ");
			sql.append("    , SUM(ITEM_12_SALE) as ITEM_12_SALE, SUM(ITEM_16_SALE) as ITEM_16_SALE, SUM(MTD_INV_SALE) as MTD_INV_SALE ");
			sql.append("    , SUM(MTD_INS_SALE) as MTD_INS_SALE, SUM(MTD_ALL_SALE) as MTD_ALL_SALE ");
		} else {
			sql.append("    , SUM(ITEM_01_FEE) as ITEM_01_FEE, SUM(ITEM_02_FEE) as ITEM_02_FEE, SUM(ITEM_03_FEE) as ITEM_03_FEE ");
			sql.append("    , SUM(ITEM_04_FEE) as ITEM_04_FEE, SUM(ITEM_05_FEE) as ITEM_05_FEE, SUM(ITEM_06_FEE) as ITEM_06_FEE ");
			sql.append("    , SUM(ITEM_17_FEE) as ITEM_17_FEE, SUM(ITEM_10_FEE) as ITEM_10_FEE, SUM(ITEM_11_FEE) as ITEM_11_FEE ");
			sql.append("    , SUM(ITEM_12_FEE) as ITEM_12_FEE, SUM(ITEM_16_FEE) as ITEM_16_FEE, SUM(ITEM_09_FEE) as ITEM_09_FEE ");
			sql.append("    , SUM(ITEM_14_FEE) as ITEM_14_FEE, SUM(ITEM_15_FEE) as ITEM_15_FEE ");
			sql.append("    , SUM(MTD_INV_FEE) as MTD_INV_FEE, SUM(MTD_INS_FEE) as MTD_INS_FEE, SUM(MTD_ALL_FEE) as MTD_ALL_FEE ");
		}
		sql.append("   FROM REGION OV ");
		sql.append("   GROUP BY 'TOTAL' ");
		sql.append(" ) ");
		sql.append(" SELECT * FROM ( ");
		sql.append("   SELECT * FROM ORIGINAL_VIEW ");
		sql.append("   UNION ALL ");
		sql.append("   SELECT * FROM AREA ");
		sql.append("   UNION ALL ");
		sql.append("   SELECT * FROM REGION ");
		sql.append("   UNION ALL ");
		sql.append("   SELECT * FROM TOTAL) T ");
		sql.append(" WHERE 1 = 1 ");

		// 區域中心
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND T.REGION_CENTER_ID IN ( :region_center_id , 'TOTAL') ");
			condition.setObject("region_center_id",
					inputVO.getRegion_center_id());
		}

		// 營運區
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND T.BRANCH_AREA_ID IN ( :branch_area_id, 'TOTAL') ");
			condition.setObject("branch_area_id", inputVO.getBranch_area_id());
		}

		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND T.BRANCH_NBR IN ( :branch_nbr, 'TOTAL') ");
			condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		sql.append(" ORDER BY T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR, T.BRANCH_CLS ");

		condition.setObject("data_date", sdf.format(inputVO.getsCreDate()));
		condition.setQueryString(sql.toString());

		return condition;
	}
	
    private QueryConditionIF queryDataByPerformance(QueryConditionIF condition, PMS309InputVO inputVO) throws JBranchException, ParseException{

        //  === 依計績月份累計 ===
		DataAccessManager dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員

		// 取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);        
        
		sql.append(" WITH ORIGINAL_VIEW AS ( ");
		
		sql.append("     SELECT ORG.REGION_CENTER_ID, ORG.REGION_CENTER_NAME, ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME ");
		sql.append("          , RTN.BRANCH_NBR, ORG.BRANCH_NAME, ORG.BRANCH_CLS ");
		
		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("      , RTN.ITEM_01_SALE, RTN.ITEM_02_SALE, RTN.ITEM_03_SALE, RTN.ITEM_04_SALE ");
			sql.append("      , RTN.ITEM_05_SALE, RTN.ITEM_06_SALE, RTN.ITEM_17_SALE, RTN.ITEM_10_SALE ");
			sql.append("      , RTN.ITEM_11_SALE, RTN.ITEM_12_SALE, RTN.ITEM_16_SALE, RTN.MTD_INV_SALE ");
			sql.append("      , RTN.MTD_INS_SALE, RTN.MTD_ALL_SALE ");
		} else {
			sql.append("      , RTN.ITEM_01_FEE, RTN.ITEM_02_FEE, RTN.ITEM_03_FEE, RTN.ITEM_04_FEE ");
			sql.append("      , RTN.ITEM_05_FEE, RTN.ITEM_06_FEE, RTN.ITEM_17_FEE, RTN.ITEM_10_FEE ");
			sql.append("      , RTN.ITEM_11_FEE, RTN.ITEM_12_FEE, RTN.ITEM_16_FEE, RTN.ITEM_09_FEE ");
			sql.append("	  , RTN.ITEM_14_FEE, RTN.ITEM_15_FEE ");
			sql.append("	  , RTN.MTD_INV_FEE, RTN.MTD_INS_FEE, RTN.MTD_ALL_FEE ");
		}
		
		sql.append("     FROM ( ");
		sql.append("         SELECT BRANCH_NBR ");
		
		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("          , SUM(ITEM_01_SALE) AS ITEM_01_SALE, SUM(ITEM_02_SALE) AS ITEM_02_SALE ");
			sql.append("          , SUM(ITEM_03_SALE) AS ITEM_03_SALE, SUM(ITEM_04_SALE) AS ITEM_04_SALE ");
			sql.append("          , SUM(ITEM_05_SALE) AS ITEM_05_SALE, SUM(ITEM_06_SALE) AS ITEM_06_SALE ");
			sql.append("          , SUM(ITEM_17_SALE) AS ITEM_17_SALE, SUM(ITEM_10_SALE) AS ITEM_10_SALE ");
			sql.append("          , SUM(ITEM_11_SALE) AS ITEM_11_SALE, SUM(ITEM_12_SALE) AS ITEM_12_SALE ");
			sql.append("          , SUM(ITEM_16_SALE) AS ITEM_16_SALE, SUM(MTD_INV_SALE) AS MTD_INV_SALE ");
			sql.append("          , SUM(MTD_INS_SALE) AS MTD_INS_SALE, SUM(MTD_ALL_SALE) AS MTD_ALL_SALE ");
		} else {
			sql.append("          , SUM(ITEM_01_FEE) AS ITEM_01_FEE, SUM(ITEM_02_FEE) AS ITEM_02_FEE ");
			sql.append("          , SUM(ITEM_03_FEE) AS ITEM_03_FEE, SUM(ITEM_04_FEE) AS ITEM_04_FEE ");
			sql.append("          , SUM(ITEM_05_FEE) AS ITEM_05_FEE, SUM(ITEM_06_FEE) AS ITEM_06_FEE ");
			sql.append("          , SUM(ITEM_17_FEE) AS ITEM_17_FEE, SUM(ITEM_10_FEE) AS ITEM_10_FEE ");
			sql.append("          , SUM(ITEM_11_FEE) AS ITEM_11_FEE, SUM(ITEM_12_FEE) AS ITEM_12_FEE ");
			sql.append("          , SUM(ITEM_16_FEE) AS ITEM_16_FEE, SUM(ITEM_09_FEE) AS ITEM_09_FEE ");
			sql.append(" 		  , SUM(ITEM_14_FEE) AS ITEM_14_FEE, SUM(ITEM_15_FEE) AS ITEM_15_FEE, SUM(MTD_INV_FEE_PRD) AS MTD_INV_FEE ");
			sql.append("          , SUM(MTD_INS_FEE) AS MTD_INS_FEE, SUM(MTD_ALL_FEE) AS MTD_ALL_FEE ");
		}
		
		sql.append("         FROM TBPMS_BR_DAY_PROFIT_MYTD BRSAL ");
		sql.append("         WHERE BRSAL.DATA_DATE IN ( ");
		sql.append("                 SELECT MAX(DATA_DATE) ");
		sql.append("                 FROM TBPMS_BR_DAY_PROFIT_MYTD ");
		sql.append("                 WHERE SUBSTR(DATA_DATE,1,6) >= :yearmon_s ");
		sql.append("                   AND SUBSTR(DATA_DATE,1,6) <= :yearmon_e ");
		sql.append("                 GROUP BY SUBSTR(DATA_DATE,1,6) ) ");
		sql.append("         GROUP BY BRANCH_NBR ) RTN ");
		sql.append("     LEFT JOIN ( ");
		sql.append("         SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME ");
		sql.append("              , BRANCH_NBR, BRANCH_NAME, BRANCH_CLS ");
		sql.append("         FROM TBPMS_BR_DAY_PROFIT_MYTD ");
		sql.append("         WHERE DATA_DATE = ( ");
		sql.append("             SELECT MAX(DATA_DATE) ");
		sql.append("             FROM TBPMS_BR_DAY_PROFIT_MYTD ");
		sql.append("             WHERE SUBSTR(DATA_DATE,1,6) >= :yearmon_s ");
		sql.append("               AND SUBSTR(DATA_DATE,1,6) <= :yearmon_e ) ) ORG ON RTN.BRANCH_NBR = ORG.BRANCH_NBR ");
		sql.append(" ), ");
		sql.append(" AREA AS ( ");
		sql.append("   SELECT OV.REGION_CENTER_ID, OV.REGION_CENTER_NAME, OV.BRANCH_AREA_ID, BRANCH_AREA_NAME,'AREA' as BRANCH_NBR ,'AREA' as BRANCH_NAME, 'AREA' as BRANCH_CLS ");
		
		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("    , SUM(ITEM_01_SALE) as ITEM_01_SALE, SUM(ITEM_02_SALE) as ITEM_02_SALE, SUM(ITEM_03_SALE) as ITEM_03_SALE ");
			sql.append("    , SUM(ITEM_04_SALE) as ITEM_04_SALE, SUM(ITEM_05_SALE) as ITEM_05_SALE, SUM(ITEM_06_SALE) as ITEM_06_SALE ");
			sql.append("    , SUM(ITEM_17_SALE) as ITEM_17_SALE, SUM(ITEM_10_SALE) as ITEM_10_SALE, SUM(ITEM_11_SALE) as ITEM_11_SALE ");
			sql.append("    , SUM(ITEM_12_SALE) as ITEM_12_SALE, SUM(ITEM_16_SALE) as ITEM_16_SALE, SUM(MTD_INV_SALE) as MTD_INV_SALE ");
			sql.append("    , SUM(MTD_INS_SALE) as MTD_INS_SALE, SUM(MTD_ALL_SALE) as MTD_ALL_SALE ");
		} else {
			sql.append("    , SUM(ITEM_01_FEE) as ITEM_01_FEE, SUM(ITEM_02_FEE) as ITEM_02_FEE, SUM(ITEM_03_FEE) as ITEM_03_FEE ");
			sql.append("    , SUM(ITEM_04_FEE) as ITEM_04_FEE, SUM(ITEM_05_FEE) as ITEM_05_FEE, SUM(ITEM_06_FEE) as ITEM_06_FEE ");
			sql.append("    , SUM(ITEM_17_FEE) as ITEM_17_FEE, SUM(ITEM_10_FEE) as ITEM_10_FEE, SUM(ITEM_11_FEE) as ITEM_11_FEE ");
			sql.append("    , SUM(ITEM_12_FEE) as ITEM_12_FEE, SUM(ITEM_16_FEE) as ITEM_16_FEE, SUM(ITEM_09_FEE) as ITEM_09_FEE ");
			sql.append("    , SUM(ITEM_14_FEE) as ITEM_14_FEE, SUM(ITEM_15_FEE) as ITEM_15_FEE ");
			sql.append("    , SUM(MTD_INV_FEE) as MTD_INV_FEE, SUM(MTD_INS_FEE) as MTD_INS_FEE, SUM(MTD_ALL_FEE) as MTD_ALL_FEE ");
		}
		sql.append("   FROM ORIGINAL_VIEW OV ");
		sql.append("   GROUP BY OV.REGION_CENTER_NAME,OV.BRANCH_AREA_NAME,OV.REGION_CENTER_ID,OV.BRANCH_AREA_ID ");
		sql.append(" ), ");
		sql.append(" REGION AS ( ");
		sql.append("   SELECT OV.REGION_CENTER_ID, OV.REGION_CENTER_NAME, 'REGION' as BRANCH_AREA_ID, 'REGION' as BRANCH_AREA_NAME,'REGION' as BRANCH_NBR ,'REGION' as BRANCH_NAME, 'REGION' as BRANCH_CLS ");
		
		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("    , SUM(ITEM_01_SALE) as ITEM_01_SALE, SUM(ITEM_02_SALE) as ITEM_02_SALE, SUM(ITEM_03_SALE) as ITEM_03_SALE ");
			sql.append("    , SUM(ITEM_04_SALE) as ITEM_04_SALE, SUM(ITEM_05_SALE) as ITEM_05_SALE, SUM(ITEM_06_SALE) as ITEM_06_SALE ");
			sql.append("    , SUM(ITEM_17_SALE) as ITEM_17_SALE, SUM(ITEM_10_SALE) as ITEM_10_SALE, SUM(ITEM_11_SALE) as ITEM_11_SALE ");
			sql.append("    , SUM(ITEM_12_SALE) as ITEM_12_SALE, SUM(ITEM_16_SALE) as ITEM_16_SALE, SUM(MTD_INV_SALE) as MTD_INV_SALE ");
			sql.append("    , SUM(MTD_INS_SALE) as MTD_INS_SALE, SUM(MTD_ALL_SALE) as MTD_ALL_SALE ");
		} else {
			sql.append("    , SUM(ITEM_01_FEE) as ITEM_01_FEE, SUM(ITEM_02_FEE) as ITEM_02_FEE, SUM(ITEM_03_FEE) as ITEM_03_FEE ");
			sql.append("    , SUM(ITEM_04_FEE) as ITEM_04_FEE, SUM(ITEM_05_FEE) as ITEM_05_FEE, SUM(ITEM_06_FEE) as ITEM_06_FEE ");
			sql.append("    , SUM(ITEM_17_FEE) as ITEM_17_FEE, SUM(ITEM_10_FEE) as ITEM_10_FEE, SUM(ITEM_11_FEE) as ITEM_11_FEE ");
			sql.append("    , SUM(ITEM_12_FEE) as ITEM_12_FEE, SUM(ITEM_16_FEE) as ITEM_16_FEE, SUM(ITEM_09_FEE) as ITEM_09_FEE ");
			sql.append("    , SUM(ITEM_14_FEE) as ITEM_14_FEE, SUM(ITEM_15_FEE) as ITEM_15_FEE ");
			sql.append("    , SUM(MTD_INV_FEE) as MTD_INV_FEE, SUM(MTD_INS_FEE) as MTD_INS_FEE, SUM(MTD_ALL_FEE) as MTD_ALL_FEE ");
		}
		sql.append("   FROM AREA OV ");
		sql.append("   GROUP BY OV.REGION_CENTER_NAME,OV.REGION_CENTER_ID ");
		sql.append(" ), ");
		sql.append(" TOTAL AS( ");
		sql.append("   SELECT 'TOTAL' as REGION_CENTER_ID, 'TOTAL' as REGION_CENTER_NAME, 'TOTAL' as BRANCH_AREA_ID, 'TOTAL' as BRANCH_AREA_NAME,'TOTAL' as BRANCH_NBR ,'TOTAL' as BRANCH_NAME, 'TOTAL' as BRANCH_CLS ");
		
		if (inputVO.getSrchType().equals("SAL")) {
			sql.append("    , SUM(ITEM_01_SALE) as ITEM_01_SALE, SUM(ITEM_02_SALE) as ITEM_02_SALE, SUM(ITEM_03_SALE) as ITEM_03_SALE ");
			sql.append("    , SUM(ITEM_04_SALE) as ITEM_04_SALE, SUM(ITEM_05_SALE) as ITEM_05_SALE, SUM(ITEM_06_SALE) as ITEM_06_SALE ");
			sql.append("    , SUM(ITEM_17_SALE) as ITEM_17_SALE, SUM(ITEM_10_SALE) as ITEM_10_SALE, SUM(ITEM_11_SALE) as ITEM_11_SALE ");
			sql.append("    , SUM(ITEM_12_SALE) as ITEM_12_SALE, SUM(ITEM_16_SALE) as ITEM_16_SALE, SUM(MTD_INV_SALE) as MTD_INV_SALE ");
			sql.append("    , SUM(MTD_INS_SALE) as MTD_INS_SALE, SUM(MTD_ALL_SALE) as MTD_ALL_SALE ");
		} else {
			sql.append("    , SUM(ITEM_01_FEE) as ITEM_01_FEE, SUM(ITEM_02_FEE) as ITEM_02_FEE, SUM(ITEM_03_FEE) as ITEM_03_FEE ");
			sql.append("    , SUM(ITEM_04_FEE) as ITEM_04_FEE, SUM(ITEM_05_FEE) as ITEM_05_FEE, SUM(ITEM_06_FEE) as ITEM_06_FEE ");
			sql.append("    , SUM(ITEM_17_FEE) as ITEM_17_FEE, SUM(ITEM_10_FEE) as ITEM_10_FEE, SUM(ITEM_11_FEE) as ITEM_11_FEE ");
			sql.append("    , SUM(ITEM_12_FEE) as ITEM_12_FEE, SUM(ITEM_16_FEE) as ITEM_16_FEE, SUM(ITEM_09_FEE) as ITEM_09_FEE ");
			sql.append("    , SUM(ITEM_14_FEE) as ITEM_14_FEE, SUM(ITEM_15_FEE) as ITEM_15_FEE ");
			sql.append("    , SUM(MTD_INV_FEE) as MTD_INV_FEE, SUM(MTD_INS_FEE) as MTD_INS_FEE, SUM(MTD_ALL_FEE) as MTD_ALL_FEE ");
		}
		sql.append("   FROM REGION OV ");
		sql.append("   GROUP BY 'TOTAL' ");
		sql.append(" ) ");
		sql.append(" SELECT * FROM ( ");
		sql.append("   SELECT * FROM ORIGINAL_VIEW ");
		sql.append("   UNION ALL ");
		sql.append("   SELECT * FROM AREA ");
		sql.append("   UNION ALL ");
		sql.append("   SELECT * FROM REGION ");
		sql.append("   UNION ALL ");
		sql.append("   SELECT * FROM TOTAL) T ");
		sql.append(" WHERE 1 = 1 ");

		// 區域中心
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND T.REGION_CENTER_ID IN ( :region_center_id , 'TOTAL') ");
			condition.setObject("region_center_id",
					inputVO.getRegion_center_id());
		}
		
		// 營運區
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND T.BRANCH_AREA_ID IN ( :branch_area_id, 'TOTAL') ");
			condition.setObject("branch_area_id", inputVO.getBranch_area_id());
		}

		// 分行
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND T.BRANCH_NBR IN ( :branch_nbr, 'TOTAL') ");
			condition.setObject("branch_nbr", inputVO.getBranch_nbr());
		}

		condition.setObject("yearmon_s", inputVO.getDataMonth_S().substring(0, 6));
        condition.setObject("yearmon_e", inputVO.getDataMonth_E().substring(0, 6));
		sql.append(" ORDER BY T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR, T.BRANCH_CLS ");
		
        condition.setQueryString(sql.toString());

        return condition;
    }
	
	
	public void export(Object body, IPrimitiveMap header) throws JBranchException , ParseException , Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		PMS309OutputVO outputVO = (PMS309OutputVO) body;
		String type = outputVO.getSrchType();

		
		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");
		String fileName = "分行收益達成率_" + sdf.format(new Date()) + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";


		List<Map<String, Object>> list = outputVO.getTotalList();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("分行收益達成率");
		sheet.setDefaultColumnWidth(25);
		sheet.setDefaultRowHeightInPoints(25);

		// 表頭格式
        setXlsHeader(workbook, sheet, type);

		// 資料 CELL型式
		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);

        int index = 1; // 行數
        XSSFRow row;

		// Data row
		String rcID = "";
		String opID = "";
		String merge = "";
		index++;
		int startRC = index;
		int endRC = 0;
		int startOP = index;
		int endOP = 0;

		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);
			int j = 0;

			XSSFCell cell = row.createCell(j);

			// 業務處
			cell = row.createCell(j++);
			cell.setCellStyle(style);
			
			if(StringUtils.equals(checkIsNull(map, "REGION_CENTER_NAME"), "TOTAL")){
				cell.setCellValue("總計");
				merge = "T";
			}else{
				cell.setCellValue(checkIsNull(map, "REGION_CENTER_NAME"));
			}
			
			// 營運區
			cell = row.createCell(j++);
			cell.setCellStyle(style);
			cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME"));

			if(StringUtils.equals(checkIsNull(map, "BRANCH_AREA_NAME"), "TOTAL")){
				cell.setCellValue("總計");
				merge = "T";

			}else if(StringUtils.equals(checkIsNull(map, "BRANCH_AREA_NAME"), "REGION")){
				cell.setCellValue(checkIsNull(map, "REGION_CENTER_NAME") + "合計");
				merge = "C";

			}else{
				cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME"));
				merge = "A";
			}


			// 分行名稱
			cell = row.createCell(j++);
			cell.setCellStyle(style);
			
			//分行合計名稱更動
			if(StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "TOTAL")){
				cell.setCellValue("總計");
				// 分行組別
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue("總計");
			}else if(StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "AREA")){
				
				cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME") + "合計");
				// 分行組別
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BA_CLS"));
			}else if(StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "REGION")){
				cell.setCellValue(checkIsNull(map, "REGION_CENTER_NAME") + "合計");
				// 分行組別
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME") + "合計");
			}else{
				cell.setCellValue(checkIsNull(map, "BRANCH_NBR")+"-"+checkIsNull(map, "BRANCH_NAME"));
				// 分行組別
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_CLS"));
			}
			
			// 投資商品
			if(type.equals("INC")){
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_01_FEE"));

	            cell = row.createCell(j++);
	            cell.setCellStyle(style);
	            cell.setCellValue(currencyFormat(map, "ITEM_02_FEE"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_03_FEE"));            
	            
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_04_FEE"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_05_FEE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_17_FEE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_06_FEE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_09_FEE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_14_FEE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_15_FEE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"MTD_INV_FEE"));
				
			}else{
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_01_SALE"));

	            cell = row.createCell(j++);
	            cell.setCellStyle(style);
	            cell.setCellValue(currencyFormat(map, "ITEM_02_SALE"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_03_SALE"));            
	            
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_04_SALE"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_05_SALE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_17_SALE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"ITEM_06_SALE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"MTD_INV_SALE"));
			}

			// 保險商品
			if(type.equals("INC")){
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_10_FEE"));

	            cell = row.createCell(j++);
	            cell.setCellStyle(style);
	            cell.setCellValue(currencyFormat(map, "ITEM_16_FEE"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_11_FEE"));            
	            
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_12_FEE"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"MTD_INS_FEE"));
				
			}else{
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_10_SALE"));

	            cell = row.createCell(j++);
	            cell.setCellStyle(style);
	            cell.setCellValue(currencyFormat(map, "ITEM_16_SALE"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_11_SALE"));            
	            
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "ITEM_12_SALE"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map,"MTD_INS_SALE"));
			}

			// 合計
			if(type.equals("INC")){
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "MTD_ALL_FEE"));
			
			} else {
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "MTD_ALL_SALE"));
	
			}
			

			if(merge == "T"){
				sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 3));
			}else if(merge == "C"){
				sheet.addMergedRegion(new CellRangeAddress(index, index, 2, 3));
			}else if(merge == " "){
				sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 3));
			}

			if ((!map.get("REGION_CENTER_ID").toString().equals(rcID) && !rcID.equals("") && !StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "TOTAL"))
					|| index == list.size()) {
				if (index - 1 == list.size()){
					sheet.addMergedRegion(new CellRangeAddress(startRC, startRC + endRC, 0, 0));
				}
				else if (endRC > 1)
					sheet.addMergedRegion(new CellRangeAddress(startRC, startRC + endRC - 1, 0, 0));

				startRC = index;
				endRC = 0;
			}

			if ((!map.get("BRANCH_AREA_ID").toString().equals(opID) && !opID.equals("") && !StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "TOTAL")) 
					|| index == list.size()) {
				if (index - 2 == list.size()){
					sheet.addMergedRegion(new CellRangeAddress(startOP, startOP + endOP, 1, 1));
				}
				else if (endOP > 1)
					sheet.addMergedRegion(new CellRangeAddress(startOP, startOP + endOP - 1, 1, 1));

				startOP = index;
				endOP = 0;
			}

			endRC++;
			endOP++;
			rcID = map.get("REGION_CENTER_ID").toString();
			opID = map.get("BRANCH_AREA_ID").toString();

			index++;
		}


		//將檔名取為亂數uid
		String tempName = UUID.randomUUID().toString();
		//路徑結合
		File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
		//絕對路徑建檔
		workbook.write(new FileOutputStream(f));
		//相對路徑取檔
		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName); 
		this.sendRtnObject(null);
	}

    private void setXlsHeader(XSSFWorkbook workbook, XSSFSheet sheet, String type) {
        // 組出第一列表頭 MTD/QTD/YTD
        String SrchType = "1";
        if (type.equals("INC")){
        	SrchType = "收益";
        }else if (type.equals("SAL")){
        	SrchType = "銷量";
        }

	    // 組出表頭欄位所需參數 Start
	    Map<String, String> order = new LinkedHashMap<>();
	    order.put("REGION_CENTER_NAME", "業務處");
        order.put("BRANCH_AREA_NAME", "營運區");
        order.put("BRANCH_NAME", "分行");
        order.put("BRANCH_CLS", "組別");

        if (type.equals("INC")){
        	order.put("ITEM_01_FEE", "基金");
            order.put("ITEM_02_FEE", "SI");
            order.put("ITEM_03_FEE", "SN");
            order.put("ITEM_04_FEE", "奈米投");
            order.put("ITEM_05_FEE", "海外債");
            order.put("ITEM_17_FEE", "海外股票");
            order.put("ITEM_06_FEE", "海外ETF");
            order.put("ITEM_09_FEE", "匯兌損益");
            order.put("ITEM_14_FEE", "黃金存摺");
            order.put("ITEM_15_FEE", "信託轉介");
            order.put("MTD_INV_FEE", "小計");

            order.put("ITEM_10_FEE", "躉繳");
            order.put("ITEM_16_FEE", "投資型");
            order.put("ITEM_11_FEE", "短年期");
            order.put("ITEM_12_FEE", "長年期");
            order.put("MTD_INS_FEE", "小計");

            order.put("MTD_ALL_FEE", "合計");  
            
        }else if (type.equals("SAL")){
        	
        	order.put("ITEM_01_SALE", "基金");
            order.put("ITEM_02_SALE", "SI");
            order.put("ITEM_03_SALE", "SN");
            order.put("ITEM_04_SALE", "奈米投");
            order.put("ITEM_05_SALE", "海外債");
            order.put("ITEM_17_SALE", "海外股票");
            order.put("ITEM_06_SALE", "海外ETF");
            order.put("MTD_INV_SALE", "小計");

            order.put("ITEM_10_SALE", "躉繳");
            order.put("ITEM_16_SALE", "投資型");
            order.put("ITEM_11_SALE", "短年期");
            order.put("ITEM_12_SALE", "長年期");
            order.put("MTD_INS_SALE", "小計");

            order.put("MTD_ALL_SALE", "合計"); 
        }
        
        // 組出表頭欄位所需參數 End


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

        /*** 2018-10-01 by Allen 標題欄位調整 Start **********/
        Row rowHead = sheet.createRow(0);
        int cellHeadNum = 0;
        for (Map.Entry<String, String> strs : order.entrySet()) {
            if(cellHeadNum < 4){
                Cell cellHead = rowHead.createCell(cellHeadNum);
                String str = strs.getValue();
                cellHead.setCellStyle(headingStyle);
                cellHead.setCellValue(str);
                if(cellHeadNum == 3){
                    // 從第三欄開始新增 "投資"、"保險"、"合計" 合併欄位
                    // 固定欄位第一列第4欄為"投資"
                    if (type.equals("INC")){ 	//收益
                    	cellHead = rowHead.createCell(4);
                        cellHead.setCellStyle(headingStyle);
                        cellHead.setCellValue("投資");
                    	sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 14));
                    	
                        cellHead = rowHead.createCell(15);
                        cellHead.setCellStyle(headingStyle);
                        cellHead.setCellValue("保險");
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 15, 19));
                    
                        cellHead = rowHead.createCell(20);
                        cellHead.setCellStyle(headingStyle);
                        cellHead.setCellValue("合計");
                        
                    }else{ 						//銷量
                    	cellHead = rowHead.createCell(4);
                        cellHead.setCellStyle(headingStyle);
                        cellHead.setCellValue("投資");
                    	sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 11));
                    	
                    	cellHead = rowHead.createCell(12);
                        cellHead.setCellStyle(headingStyle);
                        cellHead.setCellValue("保險");
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 12, 16));
                    
                        cellHead = rowHead.createCell(17);
                        cellHead.setCellStyle(headingStyle);
                        cellHead.setCellValue("合計");
                    }

                    rowHead = sheet.createRow(1);

                }
            }

            cellHeadNum++;
        }

        // 多增加一列，將 "投資"、"保險"、"合計" 拆開，下面跑的是各項的值
        rowHead = sheet.createRow(1);
        cellHeadNum = 0;
        for (Entry<String, String> strs : order.entrySet()){
            if (cellHeadNum < 4){
                Cell cellHead = rowHead.createCell(cellHeadNum);
                cellHead.setCellStyle(headingStyle);
                // 合併處存格
                sheet.addMergedRegion(new CellRangeAddress(0, 1, cellHeadNum, cellHeadNum));

            }else {
                Cell cellHead = rowHead.createCell(cellHeadNum);
                String str = strs.getValue();
                cellHead.setCellStyle(headingStyle);
                cellHead.setCellValue(str);
            }
            cellHeadNum++;
        }

        // autoSizeColumn
        for (int i = 0; i < rowHead.getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

    }
	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
	
	// 處理貨幣格式(加總用)
	private String currencyFormat2(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("###0");
			return df.format(map.get(key));
		} else
			return "0";
	}
		
	// 處理貨幣格式(加總結果的用)
	private String currencyFormat3(int key) {
		if (StringUtils.isNotBlank(String.valueOf(key))
				&& key != 0) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(key);
		} else
			return "0.00";
	}

	/**
	 * 取月份第一天
	 * 
	 * @throws ParseException
	 **/
	private Date getMonthFstDate(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		Date rptDate = df.parse(date);
		cal.setTime(rptDate);
		cal.set(cal.DATE, cal.getActualMinimum(cal.DATE));
		
		return cal.getTime();
	}

	/**
	 * 取月份最後一天
	 * 
	 * @throws ParseException
	 **/
	private Date getMonthLastDate(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		Date rptDate = df.parse(date);
		cal.setTime(rptDate);
		cal.set(cal.DATE, cal.getActualMaximum(cal.DATE));
		
		return cal.getTime();
	}
	

	private int integerFormat(Map map, String key){
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			int intFormat = Integer.valueOf(((Double)map.get(key)).intValue());
			return intFormat;
		} else
			return 0;
	}
	

}