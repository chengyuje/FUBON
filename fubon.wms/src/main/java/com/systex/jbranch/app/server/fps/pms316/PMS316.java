package com.systex.jbranch.app.server.fps.pms316;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
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
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 *
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :分行銷量達成率 Controller <br>
 * Comments Name : PMS316.java<br>
 * Author :Frank<br>
 * Date :2016年05月17日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms316")
@Scope("request")
public class PMS316 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS316.class);

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		PMS316InputVO inputVO = (PMS316InputVO) body;
		PMS316OutputVO outputVO = new PMS316OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		try {

            //  === 依報表日期查詢 ===
            if (inputVO.getSrchDate().equals("1")) {

            	condition = this.queryDataByDate(condition, inputVO);
            	outputVO.setIncomeStr(inputVO.getIncome());
            }
            //  === 依計績月份累計 ===
            else{
                condition = this.queryDataByPerformance(condition, inputVO);
                outputVO.setIncomeStr(inputVO.getIncome());
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

    public QueryConditionIF queryDataByDate(QueryConditionIF condition, PMS316InputVO inputVO) throws JBranchException, ParseException{

        //  === 依報表日期查詢 ===

        DataAccessManager dam = this.getDataAccessManager();
        StringBuffer sql = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        
        sql.append(" WITH ORIGINAL_VIEW AS ( ");
        sql.append("     SELECT BRSAL.REGION_CENTER_ID, BRSAL.REGION_CENTER_NAME, BRSAL.BRANCH_AREA_ID, BRSAL.BRANCH_AREA_NAME ");
        sql.append("          , BRSAL.BRANCH_NBR, BRSAL.BRANCH_NAME, BRSAL.BRANCH_CLS ");
        sql.append("          , BRSAL.MTD_INV_SALE, BRTAR.MTD_INV_SALE_GOAL, BRSAL.MTD_INV_SALE_RATE_MM ");
        sql.append("          , DECODE(BRTAR.MTD_INV_SALE_GOAL,0,0,ROUND( BRSAL.MTD_INV_SALE/BRTAR.MTD_INV_SALE_GOAL *100,2)) AS MTD_INV_SALE_RATE ");
        sql.append("          , DECODE(BRTAR.MTD_INV_SALE_GOAL,0,0,ROUND((BRSAL.MTD_INV_SALE/BRTAR.MTD_INV_SALE_GOAL)*100/BRSAL.MTD_INV_SALE_RATE_MM*100,2)) AS MTD_INV_SALE_RATE_NOW ");
        sql.append("          , BRSAL.MTD_INS_SALE, BRTAR.MTD_INS_SALE_GOAL, BRSAL.MTD_INS_SALE_RATE_MM ");
        sql.append("          , DECODE(BRTAR.MTD_INS_SALE_GOAL,0,0,ROUND( BRSAL.MTD_INS_SALE/BRTAR.MTD_INS_SALE_GOAL *100,2)) AS MTD_INS_SALE_RATE ");
        sql.append("          , DECODE(BRTAR.MTD_INS_SALE_GOAL,0,0,ROUND((BRSAL.MTD_INS_SALE/BRTAR.MTD_INS_SALE_GOAL)*100/BRSAL.MTD_INS_SALE_RATE_MM*100,2)) AS MTD_INS_SALE_RATE_NOW ");
        sql.append("          , BRSAL.MTD_ALL_SALE, BRTAR.MTD_ALL_SALE_GOAL, BRSAL.MTD_ALL_SALE_RATE_MM ");
        sql.append("          , DECODE(BRTAR.MTD_ALL_SALE_GOAL,0,0,ROUND( BRSAL.MTD_ALL_SALE/BRTAR.MTD_ALL_SALE_GOAL *100,2)) AS MTD_ALL_SALE_RATE ");
        sql.append("          , DECODE(BRTAR.MTD_ALL_SALE_GOAL,0,0,ROUND((BRSAL.MTD_ALL_SALE/BRTAR.MTD_ALL_SALE_GOAL)*100/BRSAL.MTD_ALL_SALE_RATE_MM*100,2)) AS MTD_ALL_SALE_RATE_NOW ");
        sql.append("          , BRSAL.YTD_INV_SALE, YRTAR.YTD_INV_SALE_GOAL, BRSAL.YTD_INV_SALE_RATE_MM ");
        sql.append("          , DECODE(YRTAR.YTD_INV_SALE_GOAL,0,0,ROUND( BRSAL.YTD_INV_SALE/YRTAR.YTD_INV_SALE_GOAL *100,2)) AS YTD_INV_SALE_RATE ");
        sql.append("          , DECODE(YRTAR.YTD_INV_SALE_GOAL,0,0,ROUND((BRSAL.YTD_INV_SALE/YRTAR.YTD_INV_SALE_GOAL)*100/BRSAL.YTD_INV_SALE_RATE_MM*100,2)) AS YTD_INV_SALE_RATE_NOW ");
        sql.append("          , BRSAL.YTD_INS_SALE, YRTAR.YTD_INS_SALE_GOAL, BRSAL.YTD_INS_SALE_RATE_MM ");
        sql.append("          , DECODE(YRTAR.YTD_INS_SALE_GOAL,0,0,ROUND( BRSAL.YTD_INS_SALE/YRTAR.YTD_INS_SALE_GOAL *100,2)) AS YTD_INS_SALE_RATE ");
        sql.append("          , DECODE(YRTAR.YTD_INS_SALE_GOAL,0,0,ROUND((BRSAL.YTD_INS_SALE/YRTAR.YTD_INS_SALE_GOAL)*100/BRSAL.YTD_INS_SALE_RATE_MM*100,2)) AS YTD_INS_SALE_RATE_NOW ");
        sql.append("          , BRSAL.YTD_ALL_SALE, YRTAR.YTD_ALL_SALE_GOAL, BRSAL.YTD_ALL_SALE_RATE_MM ");
        sql.append("          , DECODE(YRTAR.YTD_ALL_SALE_GOAL,0,0,ROUND( BRSAL.YTD_ALL_SALE/YRTAR.YTD_ALL_SALE_GOAL *100,2)) AS YTD_ALL_SALE_RATE ");
        sql.append("          , DECODE(YRTAR.YTD_ALL_SALE_GOAL,0,0,ROUND((BRSAL.YTD_ALL_SALE/YRTAR.YTD_ALL_SALE_GOAL)*100/BRSAL.YTD_ALL_SALE_RATE_MM*100,2)) AS YTD_ALL_SALE_RATE_NOW ");
        sql.append("          , RANK() OVER(PARTITION BY BRSAL.BRANCH_CLS ORDER BY DECODE(BRTAR.MTD_ALL_SALE_GOAL,0,0,BRSAL.MTD_ALL_SALE/BRTAR.MTD_ALL_SALE_GOAL) DESC ) AS RANK_MTD_ALL_BY_CLS ");
        sql.append("          , RANK() OVER(PARTITION BY BRSAL.BRANCH_CLS ORDER BY DECODE(YRTAR.YTD_ALL_SALE_GOAL,0,0,BRSAL.YTD_ALL_SALE/YRTAR.YTD_ALL_SALE_GOAL) DESC ) AS RANK_YTD_ALL_BY_CLS ");
        sql.append("     FROM TBPMS_BR_DAY_PROFIT_MYTD BRSAL ");
        sql.append("     LEFT JOIN ( ");
        sql.append("          SELECT BRANCH_NBR ");
        sql.append("               , NVL(INV_TAR_AMOUNT ,0) AS MTD_INV_SALE_GOAL ");
        sql.append("               , NVL(INS_TAR_AMOUNT ,0) AS MTD_INS_SALE_GOAL ");
        sql.append("               , NVL(TOT_TAR_AMOUNT ,0) AS MTD_ALL_SALE_GOAL ");
        sql.append("          FROM TBPMS_BR_SALE_TAR_M ");
        sql.append("          WHERE DATA_YEARMON = SUBSTR(:data_date,1,6) ) BRTAR ");
        sql.append("       ON  BRSAL.BRANCH_NBR = BRTAR.BRANCH_NBR ");
        sql.append("     LEFT JOIN ( ");
        sql.append("          SELECT BRANCH_NBR ");
        sql.append("               , SUM(NVL(INV_TAR_AMOUNT ,0)) AS YTD_INV_SALE_GOAL ");
        sql.append("               , SUM(NVL(INS_TAR_AMOUNT ,0)) AS YTD_INS_SALE_GOAL ");
        sql.append("               , SUM(NVL(TOT_TAR_AMOUNT ,0)) AS YTD_ALL_SALE_GOAL ");
        sql.append("          FROM TBPMS_BR_SALE_TAR_M ");
        sql.append("          WHERE SUBSTR(DATA_YEARMON,1,4) = SUBSTR(:data_date,1,4) ");
        sql.append("            AND DATA_YEARMON <= SUBSTR(:data_date,1,6) ");
        sql.append("          GROUP BY BRANCH_NBR ) YRTAR ");
        sql.append("       ON BRSAL.BRANCH_NBR = YRTAR.BRANCH_NBR ");
        sql.append("     WHERE BRSAL.DATA_DATE = :data_date ) , ");
        sql.append(" RATE AS ( ");
        sql.append("     SELECT DISTINCT REGION_CENTER_ID,BRANCH_AREA_ID ");
        sql.append("          , MTD_INV_SALE_RATE_MM, MTD_INS_SALE_RATE_MM, MTD_ALL_SALE_RATE_MM ");
        sql.append("          , YTD_INV_SALE_RATE_MM, YTD_INS_SALE_RATE_MM, YTD_ALL_SALE_RATE_MM ");
        sql.append("     FROM ORIGINAL_VIEW ) , ");
        sql.append(" AREA AS ( ");
        sql.append("   SELECT OV.REGION_CENTER_ID, OV.REGION_CENTER_NAME, OV.BRANCH_AREA_ID, BRANCH_AREA_NAME,'AREA' AS BRANCH_NBR ,'AREA' AS BRANCH_NAME, 'AREA' AS BRANCH_CLS ");
        sql.append("        , SUM(MTD_INV_SALE) AS MTD_INV_SALE, SUM(MTD_INV_SALE_GOAL) AS MTD_INV_SALE_GOAL ");
        sql.append("        , R.MTD_INV_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_INV_SALE_GOAL),0,0,ROUND(SUM(MTD_INV_SALE)*100/SUM(MTD_INV_SALE_GOAL),3)) AS MTD_INV_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_INV_SALE_GOAL),0,0,ROUND(SUM(MTD_INV_SALE)*10000/SUM(MTD_INV_SALE_GOAL)/R.MTD_INV_SALE_RATE_MM,3)) AS MTD_INV_SALE_RATE_NOW ");
        sql.append("        , SUM(MTD_INS_SALE) AS MTD_INS_SALE, SUM(MTD_INS_SALE_GOAL) AS MTD_INS_SALE_GOAL ");
        sql.append("        , R.MTD_INS_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_INS_SALE_GOAL),0,0,ROUND(SUM(MTD_INS_SALE)*100/SUM(MTD_INS_SALE_GOAL),3)) AS MTD_INS_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_INS_SALE_GOAL),0,0,ROUND(SUM(MTD_INS_SALE)*10000/SUM(MTD_INS_SALE_GOAL)/R.MTD_INS_SALE_RATE_MM,3)) AS MTD_INS_SALE_RATE_NOW ");
        sql.append("        , SUM(MTD_ALL_SALE) AS MTD_ALL_SALE, SUM(MTD_ALL_SALE_GOAL) AS MTD_ALL_SALE_GOAL ");
        sql.append("        , R.MTD_ALL_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(MTD_ALL_SALE)*100/SUM(MTD_ALL_SALE_GOAL),3)) AS MTD_ALL_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(MTD_ALL_SALE)*10000/SUM(MTD_ALL_SALE_GOAL)/R.MTD_ALL_SALE_RATE_MM,3)) AS MTD_ALL_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_INV_SALE) AS YTD_INV_SALE, SUM(YTD_INV_SALE_GOAL) AS YTD_INV_SALE_GOAL ");
        sql.append("        , R.YTD_INV_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_INV_SALE_GOAL),0,0,ROUND(SUM(YTD_INV_SALE)*100/SUM(YTD_INV_SALE_GOAL),3)) AS YTD_INV_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_INV_SALE_GOAL),0,0,ROUND(SUM(YTD_INV_SALE)*10000/SUM(YTD_INV_SALE_GOAL)/R.YTD_INV_SALE_RATE_MM,3)) AS YTD_INV_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_INS_SALE) AS YTD_INS_SALE, SUM(YTD_INS_SALE_GOAL) AS YTD_INS_SALE_GOAL ");
        sql.append("        , R.YTD_INS_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_INS_SALE_GOAL),0,0,ROUND(SUM(YTD_INS_SALE)*100/SUM(YTD_INS_SALE_GOAL),3)) AS YTD_INS_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_INS_SALE_GOAL),0,0,ROUND(SUM(YTD_INS_SALE)*10000/SUM(YTD_INS_SALE_GOAL)/R.YTD_INS_SALE_RATE_MM,3)) AS YTD_INS_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_ALL_SALE) AS YTD_ALL_SALE, SUM(YTD_ALL_SALE_GOAL) AS YTD_ALL_SALE_GOAL ");
        sql.append("        , R.YTD_ALL_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_ALL_SALE_GOAL),0,0,ROUND(SUM(YTD_ALL_SALE)*100/SUM(YTD_ALL_SALE_GOAL),3)) AS YTD_ALL_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_ALL_SALE_GOAL),0,0,ROUND(SUM(YTD_ALL_SALE)*10000/SUM(YTD_ALL_SALE_GOAL)/R.YTD_ALL_SALE_RATE_MM,3)) AS YTD_ALL_SALE_RATE_NOW ");
        sql.append("        , RANK() OVER(PARTITION BY ORG.DEPT_GROUP ORDER BY DECODE(SUM(MTD_ALL_SALE_GOAL),0,0,SUM(MTD_ALL_SALE)/SUM(MTD_ALL_SALE_GOAL)) DESC ) AS RANK_MTD_ALL_BY_CLS ");
        sql.append("        , RANK() OVER(PARTITION BY ORG.DEPT_GROUP ORDER BY DECODE(SUM(YTD_ALL_SALE_GOAL),0,0,SUM(YTD_ALL_SALE)/SUM(YTD_ALL_SALE_GOAL)) DESC ) AS RANK_YTD_ALL_BY_CLS ");
        sql.append("   FROM ORIGINAL_VIEW OV ");
        sql.append("   LEFT JOIN RATE R ON OV.BRANCH_AREA_ID = R.BRANCH_AREA_ID ");
        sql.append("   LEFT JOIN TBORG_DEFN ORG ON OV.BRANCH_AREA_ID = ORG.DEPT_ID AND ORG.ORG_TYPE='40' and ORG.PARENT_DEPT_ID IN ('171','172','174') ");
        sql.append("   GROUP BY OV.REGION_CENTER_NAME,OV.BRANCH_AREA_NAME,OV.REGION_CENTER_ID,OV.BRANCH_AREA_ID,ORG.DEPT_GROUP, ");
        sql.append("            R.MTD_INV_SALE_RATE_MM, R.MTD_INS_SALE_RATE_MM, R.MTD_ALL_SALE_RATE_MM, ");
        sql.append("            R.YTD_INV_SALE_RATE_MM, R.YTD_INS_SALE_RATE_MM, R.YTD_ALL_SALE_RATE_MM ) , ");
        sql.append(" REGION AS ( ");
        sql.append("   SELECT OV.REGION_CENTER_ID, OV.REGION_CENTER_NAME, 'REGION' AS BRANCH_AREA_ID, 'REGION' AS BRANCH_AREA_NAME,'REGION' AS BRANCH_NBR ,'REGION' AS BRANCH_NAME, 'REGION' AS BRANCH_CLS ");
        sql.append("        , SUM(MTD_INV_SALE) AS MTD_INV_SALE, SUM(MTD_INV_SALE_GOAL) AS MTD_INV_SALE_GOAL ");
        sql.append("        , R.MTD_INV_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_INV_SALE_GOAL),0,0,ROUND(SUM(MTD_INV_SALE)*100/SUM(MTD_INV_SALE_GOAL),3)) AS MTD_INV_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_INV_SALE_GOAL),0,0,ROUND(SUM(MTD_INV_SALE)*10000/SUM(MTD_INV_SALE_GOAL)/R.MTD_INV_SALE_RATE_MM,3)) AS MTD_INV_SALE_RATE_NOW ");
        sql.append("        , SUM(MTD_INS_SALE) AS MTD_INS_SALE, SUM(MTD_INS_SALE_GOAL) AS MTD_INS_SALE_GOAL ");
        sql.append("        , R.MTD_INS_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_INS_SALE_GOAL),0,0,ROUND(SUM(MTD_INS_SALE)*100/SUM(MTD_INS_SALE_GOAL),3)) AS MTD_INS_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_INS_SALE_GOAL),0,0,ROUND(SUM(MTD_INS_SALE)*10000/SUM(MTD_INS_SALE_GOAL)/R.MTD_INS_SALE_RATE_MM,3)) AS MTD_INS_SALE_RATE_NOW ");
        sql.append("        , SUM(MTD_ALL_SALE) AS MTD_ALL_SALE, SUM(MTD_ALL_SALE_GOAL) AS MTD_ALL_SALE_GOAL ");
        sql.append("        , R.MTD_ALL_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(MTD_ALL_SALE)*100/SUM(MTD_ALL_SALE_GOAL),3)) AS MTD_ALL_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(MTD_ALL_SALE)*10000/SUM(MTD_ALL_SALE_GOAL)/R.MTD_ALL_SALE_RATE_MM,3)) AS MTD_ALL_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_INV_SALE) AS YTD_INV_SALE, SUM(YTD_INV_SALE_GOAL) AS YTD_INV_SALE_GOAL ");
        sql.append("        , R.YTD_INV_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_INV_SALE_GOAL),0,0,ROUND(SUM(YTD_INV_SALE)*100/SUM(YTD_INV_SALE_GOAL),3)) AS YTD_INV_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_INV_SALE_GOAL),0,0,ROUND(SUM(YTD_INV_SALE)*10000/SUM(YTD_INV_SALE_GOAL)/R.YTD_INV_SALE_RATE_MM,3)) AS YTD_INV_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_INS_SALE) AS YTD_INS_SALE, SUM(YTD_INS_SALE_GOAL) AS YTD_INS_SALE_GOAL ");
        sql.append("        , R.YTD_INS_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_INS_SALE_GOAL),0,0,ROUND(SUM(YTD_INS_SALE)*100/SUM(YTD_INS_SALE_GOAL),3)) AS YTD_INS_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_INS_SALE_GOAL),0,0,ROUND(SUM(YTD_INS_SALE)*10000/SUM(YTD_INS_SALE_GOAL)/R.YTD_INS_SALE_RATE_MM,3)) AS YTD_INS_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_ALL_SALE) AS YTD_ALL_SALE, SUM(YTD_ALL_SALE_GOAL) AS YTD_ALL_SALE_GOAL ");
        sql.append("        , R.YTD_ALL_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_ALL_SALE_GOAL),0,0,ROUND(SUM(YTD_ALL_SALE)*100/SUM(YTD_ALL_SALE_GOAL),3)) AS YTD_ALL_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_ALL_SALE_GOAL),0,0,ROUND(SUM(YTD_ALL_SALE)*10000/SUM(YTD_ALL_SALE_GOAL)/R.YTD_ALL_SALE_RATE_MM,3)) AS YTD_ALL_SALE_RATE_NOW ");
        sql.append("        , RANK() OVER(ORDER BY DECODE(SUM(MTD_ALL_SALE_GOAL),0,0,SUM(MTD_ALL_SALE)/SUM(MTD_ALL_SALE_GOAL)) DESC ) AS RANK_MTD_ALL_BY_CLS ");
        sql.append("        , RANK() OVER(ORDER BY DECODE(SUM(YTD_ALL_SALE_GOAL),0,0,SUM(YTD_ALL_SALE)/SUM(YTD_ALL_SALE_GOAL)) DESC ) AS RANK_YTD_ALL_BY_CLS ");
        sql.append("   FROM AREA OV ");
        sql.append("   LEFT JOIN (SELECT DISTINCT REGION_CENTER_ID, MTD_INV_SALE_RATE_MM, MTD_INS_SALE_RATE_MM, MTD_ALL_SALE_RATE_MM ");
        sql.append("                   , YTD_INV_SALE_RATE_MM, YTD_INS_SALE_RATE_MM, YTD_ALL_SALE_RATE_MM ");
        sql.append("              FROM RATE ");
        sql.append("              GROUP BY REGION_CENTER_ID, MTD_INV_SALE_RATE_MM, MTD_INS_SALE_RATE_MM, MTD_ALL_SALE_RATE_MM, YTD_INV_SALE_RATE_MM, YTD_INS_SALE_RATE_MM, YTD_ALL_SALE_RATE_MM) R ");
        sql.append("   ON OV.REGION_CENTER_ID = R.REGION_CENTER_ID ");
        sql.append("   GROUP BY OV.REGION_CENTER_NAME,OV.REGION_CENTER_ID, R.MTD_INV_SALE_RATE_MM, R.MTD_INS_SALE_RATE_MM, R.MTD_ALL_SALE_RATE_MM,R.YTD_INV_SALE_RATE_MM, R.YTD_INS_SALE_RATE_MM, R.YTD_ALL_SALE_RATE_MM ), ");
        sql.append(" TOTAL AS( ");
        sql.append("   SELECT 'TOTAL' AS REGION_CENTER_ID, 'TOTAL' AS REGION_CENTER_NAME, 'TOTAL' AS BRANCH_AREA_ID, 'TOTAL' AS BRANCH_AREA_NAME,'TOTAL' AS BRANCH_NBR ,'TOTAL' AS BRANCH_NAME, 'TOTAL' AS BRANCH_CLS ");
        sql.append("        , SUM(MTD_INV_SALE) AS MTD_INV_SALE, SUM(MTD_INV_SALE_GOAL) AS MTD_INV_SALE_GOAL ");
        sql.append("        , R.MTD_INV_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_INV_SALE_GOAL),0,0,ROUND(SUM(MTD_INV_SALE)*100/SUM(MTD_INV_SALE_GOAL),3)) AS MTD_INV_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_INV_SALE_GOAL),0,0,ROUND(SUM(MTD_INV_SALE)*10000/SUM(MTD_INV_SALE_GOAL)/R.MTD_INV_SALE_RATE_MM,3)) AS MTD_INV_SALE_RATE_NOW ");
        sql.append("        , SUM(MTD_INS_SALE) AS MTD_INS_SALE, SUM(MTD_INS_SALE_GOAL) AS MTD_INS_SALE_GOAL ");
        sql.append("        , R.MTD_INS_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_INS_SALE_GOAL),0,0,ROUND(SUM(MTD_INS_SALE)*100/SUM(MTD_INS_SALE_GOAL),3)) AS MTD_INS_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_INS_SALE_GOAL),0,0,ROUND(SUM(MTD_INS_SALE)*10000/SUM(MTD_INS_SALE_GOAL)/R.MTD_INS_SALE_RATE_MM,3)) AS MTD_INS_SALE_RATE_NOW ");
        sql.append("        , SUM(MTD_ALL_SALE) AS MTD_ALL_SALE, SUM(MTD_ALL_SALE_GOAL) AS MTD_ALL_SALE_GOAL ");
        sql.append("        , R.MTD_ALL_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(MTD_ALL_SALE)*100/SUM(MTD_ALL_SALE_GOAL),3)) AS MTD_ALL_SALE_RATE ");
        sql.append("        , DECODE(SUM(MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(MTD_ALL_SALE)*10000/SUM(MTD_ALL_SALE_GOAL)/R.MTD_ALL_SALE_RATE_MM,3)) AS MTD_ALL_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_INV_SALE) AS YTD_INV_SALE, SUM(YTD_INV_SALE_GOAL) AS YTD_INV_SALE_GOAL ");
        sql.append("        , R.YTD_INV_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_INV_SALE_GOAL),0,0,ROUND(SUM(YTD_INV_SALE)*100/SUM(YTD_INV_SALE_GOAL),3)) AS YTD_INV_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_INV_SALE_GOAL),0,0,ROUND(SUM(YTD_INV_SALE)*10000/SUM(YTD_INV_SALE_GOAL)/R.YTD_INV_SALE_RATE_MM,3)) AS YTD_INV_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_INS_SALE) AS YTD_INS_SALE, SUM(YTD_INS_SALE_GOAL) AS YTD_INS_SALE_GOAL ");
        sql.append("        , R.YTD_INS_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_INS_SALE_GOAL),0,0,ROUND(SUM(YTD_INS_SALE)*100/SUM(YTD_INS_SALE_GOAL),3)) AS YTD_INS_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_INS_SALE_GOAL),0,0,ROUND(SUM(YTD_INS_SALE)*10000/SUM(YTD_INS_SALE_GOAL)/R.YTD_INS_SALE_RATE_MM,3)) AS YTD_INS_SALE_RATE_NOW ");
        sql.append("        , SUM(YTD_ALL_SALE) AS YTD_ALL_SALE, SUM(YTD_ALL_SALE_GOAL) AS YTD_ALL_SALE_GOAL ");
        sql.append("        , R.YTD_ALL_SALE_RATE_MM ");
        sql.append("        , DECODE(SUM(YTD_ALL_SALE_GOAL),0,0,ROUND(SUM(YTD_ALL_SALE)*100/SUM(YTD_ALL_SALE_GOAL),3)) AS YTD_ALL_SALE_RATE ");
        sql.append("        , DECODE(SUM(YTD_ALL_SALE_GOAL),0,0,ROUND(SUM(YTD_ALL_SALE)*10000/SUM(YTD_ALL_SALE_GOAL)/R.YTD_ALL_SALE_RATE_MM,3)) AS YTD_ALL_SALE_RATE_NOW ");
        sql.append("        , 1 AS RANK_MTD_ALL_BY_CLS ");
        sql.append("        , 1 AS RANK_YTD_ALL_BY_CLS ");
        sql.append("   FROM REGION OV ");
        sql.append("   LEFT JOIN ( ");
        sql.append("       SELECT DISTINCT MTD_INV_SALE_RATE_MM, 'TOTAL' AS REGION_CENTER_ID, MTD_INS_SALE_RATE_MM, MTD_ALL_SALE_RATE_MM ");
        sql.append("            , YTD_INV_SALE_RATE_MM, YTD_INS_SALE_RATE_MM, YTD_ALL_SALE_RATE_MM ");
        sql.append("       FROM RATE ) R ");
        sql.append("   ON 'TOTAL' = R.REGION_CENTER_ID ");
        sql.append("   GROUP BY 'TOTAL', R.MTD_INV_SALE_RATE_MM, R.MTD_INS_SALE_RATE_MM, R.MTD_ALL_SALE_RATE_MM,R.YTD_INV_SALE_RATE_MM, R.YTD_INS_SALE_RATE_MM, R.YTD_ALL_SALE_RATE_MM ) ");
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
            condition.setObject("region_center_id", inputVO.getRegion_center_id());
        }
        
        // 營運區
        if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
            sql.append(" AND T.BRANCH_AREA_ID IN ( :branch_area_id, 'REGION', 'TOTAL') ");
            condition.setObject("branch_area_id", inputVO.getBranch_area_id());
        }
        
        // 分行
        if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
            sql.append(" AND T.BRANCH_NBR IN ( :branch_nbr, 'AREA', 'REGION', 'TOTAL') ");
            condition.setObject("branch_nbr", inputVO.getBranch_nbr());
        }
        
        condition.setObject("data_date", sdf.format(inputVO.getsCreDate()));
        sql.append(" ORDER BY T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR, T.BRANCH_CLS ");

        condition.setQueryString(sql.toString());

        return condition;
    }
    
    
    public QueryConditionIF queryDataByPerformance(QueryConditionIF condition, PMS316InputVO inputVO) throws JBranchException, ParseException{

        //  === 依計績月份累計 ===
        StringBuffer sql = new StringBuffer();

        sql.append(" WITH DTL AS ( ");
        sql.append("   SELECT DT.DATA_YEARMON, DT.DATADATE, DT.INS_PROFIT_S, DT.INS_PROFIT_E, DT.INV_YEAR_S, DT.INV_YEAR_E ");
        sql.append("        , CASE WHEN DT.DATADATE > DT.INV_YEAR_E THEN PABTH_UTIL.FC_getTwoDateDiff( DT.INV_YEAR_S , DT.INV_YEAR_E) ");
        sql.append("               ELSE PABTH_UTIL.FC_getTwoDateDiff( DT.INV_YEAR_S , DT.DATADATE) END AS INV_YY_NOW_CNT ");
        sql.append("        , PABTH_UTIL.FC_getTwoDateDiff( DT.INV_YEAR_S , DT.INV_YEAR_E)     AS INV_YY_ALL_CNT ");
        sql.append("        , CASE WHEN DT.DATADATE > DT.INS_PROFIT_E THEN PABTH_UTIL.FC_getTwoDateDiff( DT.INS_PROFIT_S , DT.INS_PROFIT_E) ");
        sql.append("               ELSE PABTH_UTIL.FC_getTwoDateDiff( DT.INS_PROFIT_S , DT.DATADATE) END AS INS_YY_NOW_CNT ");
        sql.append("        , PABTH_UTIL.FC_getTwoDateDiff( DT.INS_PROFIT_S , DT.INS_PROFIT_E) AS INS_YY_ALL_CNT ");
        sql.append("        , TAR.INV_TAR_GOAL, TAR.INS_TAR_GOAL, TAR.ALL_TAR_GOAL ");
        sql.append("   FROM ( ");
        sql.append("     SELECT B.DATADATE ");
        sql.append("          , A.YEARMON                       AS DATA_YEARMON ");
        sql.append("          , TRUNC(A.INS_PROFIT_S)           AS INS_PROFIT_S ");
        sql.append("          , TRUNC(A.INS_PROFIT_E)           AS INS_PROFIT_E ");
        sql.append("          , TRUNC(A.INS_PROFIT_E, 'MM')     AS INV_YEAR_S ");
        sql.append("          , LAST_DAY(TRUNC(A.INS_PROFIT_E))  AS INV_YEAR_E ");
        sql.append("     FROM TBPMS_INS_ACCEPT_TIME A ");
        sql.append("     LEFT JOIN ( ");
        sql.append("         SELECT SUBSTR(MAX(DATA_DATE), 1, 6) AS DATA_YEARMON ");
        sql.append("              , TO_DATE(MAX(DATA_DATE), 'YYYYMMDD') AS DATADATE ");
        sql.append("         FROM TBPMS_BR_DAY_PROFIT_MYTD ");
        sql.append("         WHERE SUBSTR(DATA_DATE,1,6) >= :yearmon_s AND SUBSTR(DATA_DATE,1,6) <= :yearmon_e ");
        sql.append("         GROUP BY SUBSTR(DATA_DATE,1,6) ) B ");
        sql.append("       ON A.YEARMON = B.DATA_YEARMON ");
        sql.append("     WHERE A.YEARMON >= :yearmon_s AND A.YEARMON <= :yearmon_e ) DT ");
        sql.append("   LEFT JOIN ( ");
        sql.append("     SELECT DATA_YEARMON ");
        sql.append("          , INV_TAR_AMOUNT AS INV_TAR_GOAL ");
        sql.append("          , INS_TAR_AMOUNT AS INS_TAR_GOAL ");
        sql.append("          , TOT_TAR_AMOUNT AS ALL_TAR_GOAL ");
        sql.append("     FROM TBPMS_BR_SALE_TAR_M ");
        sql.append("     WHERE SUBSTR(DATA_YEARMON,1,6) >= :yearmon_s AND SUBSTR(DATA_YEARMON,1,6) <= :yearmon_e ");
        sql.append("       AND BRANCH_NBR=(SELECT MIN(BRANCH_NBR) FROM TBPMS_BR_PRD_TAR_M WHERE DATA_YEARMON = :yearmon_e) ) TAR ");
        sql.append("     ON DT.DATA_YEARMON = TAR.DATA_YEARMON ");
        sql.append("   ORDER BY DT.DATA_YEARMON ) ");
        sql.append(" , MMTOMM AS ( ");
        sql.append("      SELECT SUM(INV_YY_NOW_CNT)*100/SUM(INV_YY_ALL_CNT) AS INV_RATE_MTM ");
        sql.append("           , SUM(INS_YY_NOW_CNT)*100/SUM(INS_YY_ALL_CNT) AS INS_RATE_MTM ");
        sql.append("           , SUM(INV_YY_NOW_CNT)*100/SUM(INV_YY_ALL_CNT)*SUM(INV_TAR_GOAL)/SUM(ALL_TAR_GOAL) + ");
        sql.append("             SUM(INS_YY_NOW_CNT)*100/SUM(INS_YY_ALL_CNT)*SUM(INS_TAR_GOAL)/SUM(ALL_TAR_GOAL) AS ALL_RATE_MTM ");
        sql.append("      FROM DTL  ) ");
        sql.append(" , ALL_BRANCH AS ( ");
        sql.append("     SELECT ORG.REGION_CENTER_ID, ORG.REGION_CENTER_NAME, ORG.BRANCH_AREA_ID, ORG.BRANCH_AREA_NAME ");
        sql.append("          , RTN.BRANCH_NBR, ORG.BRANCH_NAME, ORG.BRANCH_CLS ");
        sql.append("          , RTN.MTD_INV_SALE, TAR.MTD_INV_SALE_GOAL ");
        sql.append("          , RTN.MTD_INS_SALE, TAR.MTD_INS_SALE_GOAL ");
        sql.append("          , RTN.MTD_ALL_SALE, TAR.MTD_ALL_SALE_GOAL ");
        sql.append("          , DECODE(TAR.MTD_INV_SALE_GOAL,0,0,ROUND(RTN.MTD_INV_SALE*100/TAR.MTD_INV_SALE_GOAL, 2)) AS MTD_INV_SALE_RATE ");
        sql.append("          , DECODE(TAR.MTD_INS_SALE_GOAL,0,0,ROUND(RTN.MTD_INS_SALE*100/TAR.MTD_INS_SALE_GOAL, 2)) AS MTD_INS_SALE_RATE ");
        sql.append("          , DECODE(TAR.MTD_ALL_SALE_GOAL,0,0,ROUND(RTN.MTD_ALL_SALE*100/TAR.MTD_ALL_SALE_GOAL, 2)) AS MTD_ALL_SALE_RATE ");
        sql.append("          , ROUND(MYTD_RATE.INV_RATE_MTM, 2) AS MTD_INV_SALE_RATE_MM ");
        sql.append("          , ROUND(MYTD_RATE.INS_RATE_MTM, 2) AS MTD_INS_SALE_RATE_MM ");
        sql.append("          , ROUND(MYTD_RATE.ALL_RATE_MTM, 2) AS MTD_ALL_SALE_RATE_MM ");
        sql.append("          , DECODE(TAR.MTD_INV_SALE_GOAL,0,0,ROUND((RTN.MTD_INV_SALE/TAR.MTD_INV_SALE_GOAL)*10000/MYTD_RATE.INV_RATE_MTM, 2)) AS MTD_INV_SALE_RATE_NOW ");
        sql.append("          , DECODE(TAR.MTD_INS_SALE_GOAL,0,0,ROUND((RTN.MTD_INS_SALE/TAR.MTD_INS_SALE_GOAL)*10000/MYTD_RATE.INS_RATE_MTM, 2)) AS MTD_INS_SALE_RATE_NOW ");
        sql.append("          , DECODE(TAR.MTD_ALL_SALE_GOAL,0,0,ROUND((RTN.MTD_ALL_SALE/TAR.MTD_ALL_SALE_GOAL)*10000/MYTD_RATE.ALL_RATE_MTM, 2)) AS MTD_ALL_SALE_RATE_NOW ");
        sql.append("          , RANK() OVER(PARTITION BY ORG.BRANCH_CLS ORDER BY DECODE(TAR.MTD_ALL_SALE_GOAL,0,0,RTN.MTD_ALL_SALE/TAR.MTD_ALL_SALE_GOAL) DESC ) AS RANK_MTD_ALL_BY_CLS ");
        sql.append("     FROM ( ");
        sql.append("         SELECT BRSAL.BRANCH_NBR ");
        sql.append("              , SUM(BRSAL.MTD_INV_SALE) AS MTD_INV_SALE ");
        sql.append("              , SUM(BRSAL.MTD_INS_SALE) AS MTD_INS_SALE ");
        sql.append("              , SUM(BRSAL.MTD_ALL_SALE) AS MTD_ALL_SALE ");
        sql.append("         FROM TBPMS_BR_DAY_PROFIT_MYTD BRSAL ");
        sql.append("         WHERE BRSAL.DATA_DATE IN ( ");
        sql.append("                 SELECT MAX(DATA_DATE) ");
        sql.append("                 FROM TBPMS_BR_DAY_PROFIT_MYTD ");
        sql.append("                 WHERE SUBSTR(DATA_DATE,1,6) >= :yearmon_s ");
        sql.append("                   AND SUBSTR(DATA_DATE,1,6) <= :yearmon_e ");
        sql.append("                 GROUP BY SUBSTR(DATA_DATE,1,6) ) ");
        sql.append("         GROUP BY BRSAL.BRANCH_NBR ) RTN ");
        sql.append("     LEFT JOIN ( ");
        sql.append("       SELECT BRANCH_NBR ");
        sql.append("            , SUM(INV_TAR_AMOUNT)  AS MTD_INV_SALE_GOAL ");
        sql.append("            , SUM(INS_TAR_AMOUNT)  AS MTD_INS_SALE_GOAL ");
        sql.append("            , SUM(TOT_TAR_AMOUNT)  AS MTD_ALL_SALE_GOAL ");
        sql.append("       FROM TBPMS_BR_SALE_TAR_M ");
        sql.append("       WHERE SUBSTR(DATA_YEARMON,1,6) >= :yearmon_s AND SUBSTR(DATA_YEARMON,1,6) <= :yearmon_e ");
        sql.append("       GROUP BY BRANCH_NBR ) TAR ON RTN.BRANCH_NBR = TAR.BRANCH_NBR ");
        sql.append("     LEFT JOIN ( ");
        sql.append("         SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME ");
        sql.append("              , BRANCH_NBR, BRANCH_NAME, BRANCH_CLS ");
        sql.append("         FROM TBPMS_BR_DAY_PROFIT_MYTD ");
        sql.append("         WHERE DATA_DATE = ( ");
        sql.append("             SELECT MAX(DATA_DATE) ");
        sql.append("             FROM TBPMS_BR_DAY_PROFIT_MYTD ");
        sql.append("             WHERE SUBSTR(DATA_DATE,1,6) >= :yearmon_s ");
        sql.append("               AND SUBSTR(DATA_DATE,1,6) <= :yearmon_e ) ) ORG ON RTN.BRANCH_NBR = ORG.BRANCH_NBR ");
        sql.append("     LEFT JOIN MMTOMM MYTD_RATE ON 1 = 1 ) ");
        sql.append(" , AREA AS ( ");
        sql.append("     SELECT AB.REGION_CENTER_ID, AB.REGION_CENTER_NAME, AB.BRANCH_AREA_ID, AB.BRANCH_AREA_NAME ");
        sql.append("          , 'AREA' AS BRANCH_NBR, 'AREA' AS BRANCH_NAME, 'AREA' AS BRANCH_CLS ");
        sql.append("          , SUM(AB.MTD_INV_SALE) AS MTD_INV_SALE, SUM(AB.MTD_INV_SALE_GOAL) AS MTD_INV_SALE_GOAL ");
        sql.append("          , SUM(AB.MTD_INS_SALE) AS MTD_INS_SALE, SUM(AB.MTD_INS_SALE_GOAL) AS MTD_INS_SALE_GOAL ");
        sql.append("          , SUM(AB.MTD_ALL_SALE) AS MTD_ALL_SALE, SUM(AB.MTD_ALL_SALE_GOAL) AS MTD_ALL_SALE_GOAL ");
        sql.append("          , DECODE(SUM(AB.MTD_INV_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INV_SALE)*100/SUM(AB.MTD_INV_SALE_GOAL), 2)) AS MTD_INV_SALE_RATE ");
        sql.append("          , DECODE(SUM(AB.MTD_INS_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INS_SALE)*100/SUM(AB.MTD_INS_SALE_GOAL), 2)) AS MTD_INS_SALE_RATE ");
        sql.append("          , DECODE(SUM(AB.MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_ALL_SALE)*100/SUM(AB.MTD_ALL_SALE_GOAL), 2)) AS MTD_ALL_SALE_RATE ");
        sql.append("          , ROUND(MYTD_RATE.INV_RATE_MTM, 2) AS MTD_INV_SALE_RATE_MM ");
        sql.append("          , ROUND(MYTD_RATE.INS_RATE_MTM, 2) AS MTD_INS_SALE_RATE_MM ");
        sql.append("          , ROUND(MYTD_RATE.ALL_RATE_MTM, 2) AS MTD_ALL_SALE_RATE_MM ");
        sql.append("          , DECODE(SUM(AB.MTD_INV_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INV_SALE)*10000/SUM(AB.MTD_INV_SALE_GOAL)/MYTD_RATE.INV_RATE_MTM, 2)) AS MTD_INV_SALE_RATE_NOW ");
        sql.append("          , DECODE(SUM(AB.MTD_INS_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INS_SALE)*10000/SUM(AB.MTD_INS_SALE_GOAL)/MYTD_RATE.INS_RATE_MTM, 2)) AS MTD_INS_SALE_RATE_NOW ");
        sql.append("          , DECODE(SUM(AB.MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_ALL_SALE)*10000/SUM(AB.MTD_ALL_SALE_GOAL)/MYTD_RATE.ALL_RATE_MTM, 2)) AS MTD_ALL_SALE_RATE_NOW ");
        sql.append("          , RANK() OVER(PARTITION BY ORG.DEPT_GROUP ORDER BY DECODE(SUM(AB.MTD_ALL_SALE_GOAL),0,0,(ROUND(SUM(AB.MTD_ALL_SALE)/SUM(AB.MTD_ALL_SALE_GOAL), 2))) DESC ) AS RANK_MTD_ALL_BY_CLS ");
        sql.append("     FROM ALL_BRANCH AB ");
        sql.append("     LEFT JOIN MMTOMM MYTD_RATE ON 1 = 1 ");
        sql.append("     LEFT JOIN TBORG_DEFN ORG ON AB.BRANCH_AREA_ID = ORG.DEPT_ID AND ORG.ORG_TYPE='40' and ORG.PARENT_DEPT_ID IN ('171','172','174') ");
        sql.append("     GROUP BY AB.REGION_CENTER_ID, AB.REGION_CENTER_NAME, AB.BRANCH_AREA_ID, AB.BRANCH_AREA_NAME, ORG.DEPT_GROUP ");
        sql.append("            , MYTD_RATE.INV_RATE_MTM, MYTD_RATE.INS_RATE_MTM, MYTD_RATE.ALL_RATE_MTM ) ");
        sql.append(" , REGION AS ( ");
        sql.append("     SELECT AB.REGION_CENTER_ID, AB.REGION_CENTER_NAME, 'REGION' AS BRANCH_AREA_ID, 'REGION' AS BRANCH_AREA_NAME ");
        sql.append("          , 'REGION' AS BRANCH_NBR, 'REGION' AS BRANCH_NAME, 'REGION' AS BRANCH_CLS ");
        sql.append("          , SUM(AB.MTD_INV_SALE) AS MTD_INV_SALE, SUM(AB.MTD_INV_SALE_GOAL) AS MTD_INV_SALE_GOAL ");
        sql.append("          , SUM(AB.MTD_INS_SALE) AS MTD_INS_SALE, SUM(AB.MTD_INS_SALE_GOAL) AS MTD_INS_SALE_GOAL ");
        sql.append("          , SUM(AB.MTD_ALL_SALE) AS MTD_ALL_SALE, SUM(AB.MTD_ALL_SALE_GOAL) AS MTD_ALL_SALE_GOAL ");
        sql.append("          , DECODE(SUM(AB.MTD_INV_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INV_SALE)*100/SUM(AB.MTD_INV_SALE_GOAL), 2)) AS MTD_INV_SALE_RATE ");
        sql.append("          , DECODE(SUM(AB.MTD_INS_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INS_SALE)*100/SUM(AB.MTD_INS_SALE_GOAL), 2)) AS MTD_INS_SALE_RATE ");
        sql.append("          , DECODE(SUM(AB.MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_ALL_SALE)*100/SUM(AB.MTD_ALL_SALE_GOAL), 2)) AS MTD_ALL_SALE_RATE ");
        sql.append("          , ROUND(MYTD_RATE.INV_RATE_MTM, 2) AS MTD_INV_SALE_RATE_MM ");
        sql.append("          , ROUND(MYTD_RATE.INS_RATE_MTM, 2) AS MTD_INS_SALE_RATE_MM ");
        sql.append("          , ROUND(MYTD_RATE.ALL_RATE_MTM, 2) AS MTD_ALL_SALE_RATE_MM ");
        sql.append("          , DECODE(SUM(AB.MTD_INV_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INV_SALE)*10000/SUM(AB.MTD_INV_SALE_GOAL)/MYTD_RATE.INV_RATE_MTM, 2)) AS MTD_INV_SALE_RATE_NOW ");
        sql.append("          , DECODE(SUM(AB.MTD_INS_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INS_SALE)*10000/SUM(AB.MTD_INS_SALE_GOAL)/MYTD_RATE.INS_RATE_MTM, 2)) AS MTD_INS_SALE_RATE_NOW ");
        sql.append("          , DECODE(SUM(AB.MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_ALL_SALE)*10000/SUM(AB.MTD_ALL_SALE_GOAL)/MYTD_RATE.ALL_RATE_MTM, 2)) AS MTD_ALL_SALE_RATE_NOW ");
        sql.append("          , RANK() OVER(ORDER BY DECODE(SUM(AB.MTD_ALL_SALE_GOAL),0,0,(ROUND(SUM(AB.MTD_ALL_SALE)/SUM(AB.MTD_ALL_SALE_GOAL), 2))) DESC ) AS RANK_MTD_ALL_BY_CLS ");
        sql.append("     FROM AREA AB ");
        sql.append("     LEFT JOIN MMTOMM MYTD_RATE ON 1 = 1 ");
        sql.append("     GROUP BY AB.REGION_CENTER_ID, AB.REGION_CENTER_NAME ");
        sql.append("            , MYTD_RATE.INV_RATE_MTM, MYTD_RATE.INS_RATE_MTM, MYTD_RATE.ALL_RATE_MTM ) ");
        sql.append(" , TOTAL AS ( ");
        sql.append("     SELECT 'TOTAL' AS REGION_CENTER_ID, 'TOTAL' AS REGION_CENTER_NAME, 'TOTAL' AS BRANCH_AREA_ID, 'TOTAL' AS BRANCH_AREA_NAME ");
        sql.append("          , 'TOTAL' AS BRANCH_NBR, 'TOTAL' AS BRANCH_NAME, 'TOTAL' AS BRANCH_CLS ");
        sql.append("          , SUM(AB.MTD_INV_SALE) AS MTD_INV_SALE, SUM(AB.MTD_INV_SALE_GOAL) AS MTD_INV_SALE_GOAL ");
        sql.append("          , SUM(AB.MTD_INS_SALE) AS MTD_INS_SALE, SUM(AB.MTD_INS_SALE_GOAL) AS MTD_INS_SALE_GOAL ");
        sql.append("          , SUM(AB.MTD_ALL_SALE) AS MTD_ALL_SALE, SUM(AB.MTD_ALL_SALE_GOAL) AS MTD_ALL_SALE_GOAL ");
        sql.append("          , DECODE(SUM(AB.MTD_INV_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INV_SALE)*100/SUM(AB.MTD_INV_SALE_GOAL), 3)) AS MTD_INV_SALE_RATE ");
        sql.append("          , DECODE(SUM(AB.MTD_INS_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INS_SALE)*100/SUM(AB.MTD_INS_SALE_GOAL), 3)) AS MTD_INS_SALE_RATE ");
        sql.append("          , DECODE(SUM(AB.MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_ALL_SALE)*100/SUM(AB.MTD_ALL_SALE_GOAL), 3)) AS MTD_ALL_SALE_RATE ");
        sql.append("          , ROUND(MYTD_RATE.INV_RATE_MTM, 3) AS MTD_INV_SALE_RATE_MM ");
        sql.append("          , ROUND(MYTD_RATE.INS_RATE_MTM, 3) AS MTD_INS_SALE_RATE_MM ");
        sql.append("          , ROUND(MYTD_RATE.ALL_RATE_MTM, 3) AS MTD_ALL_SALE_RATE_MM ");
        sql.append("          , DECODE(SUM(AB.MTD_INV_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INV_SALE)*10000/SUM(AB.MTD_INV_SALE_GOAL)/MYTD_RATE.INV_RATE_MTM, 2)) AS MTD_INV_SALE_RATE_NOW ");
        sql.append("          , DECODE(SUM(AB.MTD_INS_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_INS_SALE)*10000/SUM(AB.MTD_INS_SALE_GOAL)/MYTD_RATE.INS_RATE_MTM, 2)) AS MTD_INS_SALE_RATE_NOW ");
        sql.append("          , DECODE(SUM(AB.MTD_ALL_SALE_GOAL),0,0,ROUND(SUM(AB.MTD_ALL_SALE)*10000/SUM(AB.MTD_ALL_SALE_GOAL)/MYTD_RATE.ALL_RATE_MTM, 2)) AS MTD_ALL_SALE_RATE_NOW ");
        sql.append("          , 1 AS RANK_MTD_ALL_BY_CLS ");
        sql.append("     FROM REGION AB ");
        sql.append("     LEFT JOIN MMTOMM MYTD_RATE ON 1 = 1 ");
        sql.append("     GROUP BY MYTD_RATE.INV_RATE_MTM, MYTD_RATE.INS_RATE_MTM, MYTD_RATE.ALL_RATE_MTM ) ");
        sql.append(" SELECT * FROM ( ");
        sql.append("   SELECT * FROM ALL_BRANCH ");
        sql.append("   UNION ALL ");
        sql.append("   SELECT * FROM AREA ");
        sql.append("   UNION ALL ");
        sql.append("   SELECT * FROM REGION ");
        sql.append("   UNION ALL ");
        sql.append("   SELECT * FROM TOTAL ) T ");
        sql.append(" WHERE 1 = 1 ");
        
        // 區域中心
        if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
            sql.append(" AND T.REGION_CENTER_ID IN ( :region_center_id , 'TOTAL') ");
            condition.setObject("region_center_id", inputVO.getRegion_center_id());
        }
        
        // 營運區
        if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
            sql.append(" AND T.BRANCH_AREA_ID IN ( :branch_area_id, 'REGION', 'TOTAL') ");
            condition.setObject("branch_area_id", inputVO.getBranch_area_id());
        }
        
        // 分行
        if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
            sql.append(" AND T.BRANCH_NBR IN ( :branch_nbr, 'AREA', 'REGION', 'TOTAL') ");
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
		PMS316OutputVO outputVO = (PMS316OutputVO) body;
		String income = outputVO.getIncomeStr();


		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");
		String fileName = "分行銷量達成率_" + sdf.format(new Date()) + "_" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";

		List<Map<String, Object>> list = outputVO.getTotalList();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("分行收益達成率");
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

		// 表頭格式
        setXlsHeader(workbook, sheet, income);

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
				merge = "";
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
				// 分行組別
				cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME") + "合計");
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

			
			if(income.equals("3")){
				// 投資商品
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "YTD_INV_SALE"));

	            cell = row.createCell(j++);
	            cell.setCellStyle(style);
	            cell.setCellValue(currencyFormat(map, "YTD_INV_SALE_GOAL"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "YTD_INV_SALE_RATE"));            
	            
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "YTD_INV_SALE_RATE_MM"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map,"YTD_INV_SALE_RATE_NOW"));
				
				// 保險商品
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "YTD_INS_SALE"));

	            cell = row.createCell(j++);
	            cell.setCellStyle(style);
	            cell.setCellValue(currencyFormat(map, "YTD_INS_SALE_GOAL"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "YTD_INS_SALE_RATE"));            
	            
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "YTD_INS_SALE_RATE_MM"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map,"YTD_INS_SALE_RATE_NOW"));
				
				// 合計
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "YTD_ALL_SALE"));
	
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "YTD_ALL_SALE_GOAL"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "YTD_ALL_SALE_RATE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "YTD_ALL_SALE_RATE_MM"));
	
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map,"YTD_ALL_SALE_RATE_NOW"));
	
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				if(StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "TOTAL")){
					cell.setCellValue("");	
				}else{
					cell.setCellValue(checkIsNull(map, "RANK_YTD_ALL_BY_CLS"));	
				}
				
			}else{
				
				// 投資商品
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "MTD_INV_SALE"));

	            cell = row.createCell(j++);
	            cell.setCellStyle(style);
	            cell.setCellValue(currencyFormat(map, "MTD_INV_SALE_GOAL"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "MTD_INV_SALE_RATE"));            
	            
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "MTD_INV_SALE_RATE_MM"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map,"MTD_INV_SALE_RATE_NOW"));
				
				// 保險商品
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "MTD_INS_SALE"));

	            cell = row.createCell(j++);
	            cell.setCellStyle(style);
	            cell.setCellValue(currencyFormat(map, "MTD_INS_SALE_GOAL"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "MTD_INS_SALE_RATE"));            
	            
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "MTD_INS_SALE_RATE_MM"));

				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map,"MTD_INS_SALE_RATE_NOW"));
				
				// 合計
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "MTD_ALL_SALE"));
	
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(currencyFormat(map, "MTD_ALL_SALE_GOAL"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "MTD_ALL_SALE_RATE"));
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map, "MTD_ALL_SALE_RATE_MM"));
	
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(pcntFormat(map,"MTD_ALL_SALE_RATE_NOW"));
	
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				if(StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "TOTAL")){
					cell.setCellValue("");	
				}else{
					cell.setCellValue(checkIsNull(map, "RANK_MTD_ALL_BY_CLS"));			
				}
			}


			if(merge == "T"){
				sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 3));
			}else if(merge == "C"){
				sheet.addMergedRegion(new CellRangeAddress(index, index, 2, 3));
			}else if(merge == "A"){
				sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 3));
			}

			if ((!map.get("REGION_CENTER_ID").toString().equals(rcID) && !rcID.equals("")&& !StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "TOTAL"))
					|| index == list.size()) {
				if (index - 1 == list.size()){
					sheet.addMergedRegion(new CellRangeAddress(startRC, startRC + endRC, 0, 0));
				}
				else if (endRC > 1)
					sheet.addMergedRegion(new CellRangeAddress(startRC, startRC + endRC - 1, 0, 0));

				startRC = index;
				endRC = 0;
			}

			if ((!map.get("BRANCH_AREA_ID").toString().equals(opID) && !opID.equals("")&& !StringUtils.equals(checkIsNull(map, "BRANCH_NAME"), "TOTAL"))
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

    private void setXlsHeader(XSSFWorkbook workbook, XSSFSheet sheet, String income) {
        // 組出第一列表頭 MTD/QTD/YTD
        String incomeHeader = "";
        if (income.equals("1")){
            incomeHeader = "MTD";
        }else if (income.equals("2")){
            incomeHeader = "QTD";
        }else if (income.equals("3")){
            incomeHeader = "YTD";
        }


	    // 組出表頭欄位所需參數 Start
	    Map<String, String> order = new LinkedHashMap<>();
	    order.put("REGION_CENTER_NAME", "業務處");
        order.put("BRANCH_AREA_NAME", "營運區");
        order.put("BRANCH_NAME", "分行");
        order.put("BRANCH_CLS", "組別");

        if (income.equals("3")){
            order.put("YTD_INV_SALE", "投資商品戰報實績");
            order.put("YTD_INV_SALE_GOAL", "投資目標");
            order.put("YTD_INV_SALE_RATE", "已達成率");
            order.put("YTD_INV_SALE_RATE_MM", "應達成率");
            order.put("YTD_INV_SALE_RATE_NOW", "達成率");

            order.put("YTD_INS_SALE", "保險商品戰報實績");
            order.put("YTD_INS_SALE_GOAL", "保險目標");
            order.put("YTD_INS_SALE_RATE", "已達成率");
            order.put("YTD_INS_SALE_RATE_MM", "應達成率");
            order.put("YTD_INS_SALE_RATE_NOW", "達成率");

            order.put("YTD_ALL_SALE", "戰報實績");
            order.put("YTD_ALL_SALE_GOAL", "分行目標");
            order.put("YTD_ALL_SALE_RATE", "已達成率");
            order.put("YTD_ALL_SALE_RATE_MM", "應達成率");
            order.put("YTD_ALL_SALE_RATE_NOW", "達成率");
            order.put("RANK_YTD_ALL_BY_CLS", "分組排名");
        } else {
        	order.put("MTD_INV_SALE", "投資商品戰報實績");
            order.put("MTD_INV_SALE_GOAL", "投資目標");
            order.put("MTD_INV_SALE_RATE", "已達成率");
            order.put("MTD_INV_SALE_RATE_MM", "應達成率");
            order.put("MTD_INV_SALE_RATE_NOW", "達成率");

            order.put("MTD_INS_SALE", "保險商品戰報實績");
            order.put("MTD_INS_SALE_GOAL", "保險目標");
            order.put("MTD_INS_SALE_RATE", "已達成率");
            order.put("MTD_INS_SALE_RATE_MM", "應達成率");
            order.put("MTD_INS_SALE_RATE_NOW", "達成率");

            order.put("MTD_ALL_SALE", "戰報實績");
            order.put("MTD_ALL_SALE_GOAL", "分行目標");
            order.put("MTD_ALL_SALE_RATE", "已達成率");
            order.put("MTD_ALL_SALE_RATE_MM", "應達成率");
            order.put("MTD_ALL_SALE_RATE_NOW", "達成率");
            order.put("RANK_MTD_ALL_BY_CLS", "分組排名");
        	
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
        for (Entry<String, String> strs : order.entrySet()) {
            if(cellHeadNum < 4){
                Cell cellHead = rowHead.createCell(cellHeadNum);
                String str = strs.getValue();
                cellHead.setCellStyle(headingStyle);
                cellHead.setCellValue(str);
                if(cellHeadNum == 3){
                    // 從第三欄開始新增 "XXX投資"、"XXX保險"、"XXX合計" 合併欄位
                    // 固定欄位第一列第4欄為"XXX投資"，將合併第一列第4欄到第8欄
                    cellHead = rowHead.createCell(4);
                    cellHead.setCellStyle(headingStyle);
                    cellHead.setCellValue(incomeHeader + "投資");
                    // 第0列，第0行，第4格 ~ 第8格 合併
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 8));


                    // 由上述從第一列第14欄開始，將合併第一列第14欄 ~ 第18欄-"XXX保險"
                    cellHead = rowHead.createCell(9);
                    cellHead.setCellStyle(headingStyle);
                    cellHead.setCellValue(incomeHeader + "保險");
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 9, 13));


                    // 由上述從第一列第19欄開始，將合併第一列第19欄 ~ 第24欄-"XXX合計"
                    cellHead = rowHead.createCell(14);
                    cellHead.setCellStyle(headingStyle);
                    cellHead.setCellValue(incomeHeader + "合計");
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 14, 19));

                    rowHead = sheet.createRow(1);

                }
            }

            cellHeadNum++;

        }

        // 多增加一列，將 "XXX投資"、"XXX保險"、"XXX合計" 拆開，下面跑的是各項的值
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

        /*** 2018-10-01 by Allen 標題欄位調整  End  **********/

    }

    /**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}

	// 達成率格式
	private String pcntFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return Float.parseFloat(map.get(key) + "") + "%";
		} else
			return "0%";
	}
	private String pcntFormat(float value) {
		return (float) (value*100) + "%";
	}

	private String percentInsInv(Map map, String key){
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return map.get(key) + "";
		} else
			return "0.00";
	}
}