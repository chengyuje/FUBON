package com.systex.jbranch.app.server.fps.ref150;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2016/06/30
 * 
 */
@Component("ref150")
@Scope("request")
public class REF150 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void query (Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		
		REF150InputVO inputVO = (REF150InputVO) body;
		REF150OutputVO outputVO = new REF150OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("WITH SALEREC AS ( ");
		sb.append("  SELECT TO_CHAR(TXN_DATE, 'yyyyMM') AS TXN_DATE, REF_ORG_ID AS BRANCH_ID, REF_PROD, TRIM(CONT_RSLT) AS CONT_RSLT ");
		sb.append("  FROM TBCAM_LOAN_SALEREC "); 
		sb.append("  WHERE TRIM(CUST_ID) IS NOT NULL ");
		sb.append("  AND TO_CHAR(TXN_DATE, 'yyyy') = SUBSTR(:txnDate, 0, 4) "); 
		sb.append(") ");
		
		switch (Integer.valueOf(inputVO.getRefProd())) {
			case 0:
				sb.append(", DTL AS ( ");
				sb.append("  SELECT DISTINCT RTB.YYYYMM AS TXN_DATE, DEFN.REGION_CENTER_ID AS CENTER_ID, DEFN.REGION_CENTER_NAME AS CENTER_NAME, DEFN.BRANCH_AREA_ID AS AREA_ID, DEFN.BRANCH_AREA_NAME, RTB.BRANCH_NBR AS BRANCH_ID, DEFN.BRANCH_NAME, ");
				sb.append("         PAR.REF_PROD, ");
				sb.append("         NVL(RTB2.MON_TARGET_CNT, 0) AS REF_M_LOAN_AIMS_NUMBER, ");
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD) AS MTD_LOAN_TOT, "); //--MTD轉介件數
				sb.append("         CASE WHEN NVL(RTB2.MON_TARGET_CNT, 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD) / NVL(RTB2.MON_TARGET_CNT, 0) * 100, 2) END AS REF_M_LOAN_RATE1, "); // --MTD轉介件數達成率 : MTD轉介件數/每月轉介目標參考件數
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND SALEREC.CONT_RSLT IN ('C01', 'B05', 'B06', 'B07', 'B12')) AS MTD_LOAN_INTO_PIECES_NUMBER, "); // --MTD進件數 : 轉介案件進度回報當月選6~10
				sb.append("         NVL(RTB2.MON_SUC_TARGET_CNT, 0) AS REF_M_LOAN_SUCCESS_NUMBER, "); 
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND SALEREC.CONT_RSLT IN ('B05')) AS MTD_LOAN_SUCCESS_NUMBER, "); // --MTD轉介成功件數 : 轉介案件進度回報當月選7
				sb.append("         CASE WHEN NVL(RTB2.MON_SUC_TARGET_CNT, 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND SALEREC.CONT_RSLT IN ('B05')) / NVL(RTB2.MON_SUC_TARGET_CNT, 0) * 100, 2) END AS REF_M_LOAN_RATE2, "); // --MTD轉介成功件數達成率 : MTD轉介成功件數/每月轉介成功目標件數
				sb.append("         NVL(RTB2.YEAR_TARGET_CNT, 0) AS REF_Y_LOAN_AIMS_NUMBER, ");
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD) AS YTD_LOAN_TOT, "); // --YTD轉介件數 : 自7月起該轉介人總轉介件數
				sb.append("         CASE WHEN NVL(RTB2.YEAR_TARGET_CNT, 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD) / NVL(RTB2.YEAR_TARGET_CNT, 0) * 100, 2) END AS REF_Y_LOAN_RATE1, "); // --YTD轉介件數達成率 : YTD轉介件數/年度轉介目標參考件數
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND SALEREC.CONT_RSLT IN ('C01', 'B05', 'B06', 'B07', 'B12')) AS YTD_LOAN_INTO_TOT, "); // --YTD進件數 : 自7月起轉介案件進度回報選6~10
				sb.append("         NVL(RTB2.YEAR_SUC_TARGET_CNT, 0) AS REF_Y_LOAN_SUCCESS_NUMBER, "); 
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND SALEREC.CONT_RSLT IN ('B05')) AS REF_YTD_LOAN_SUCCESS_NUMBER, "); // --YTD轉介成功件數 : 自7月起轉介案件進度回報選7 
				sb.append("         CASE WHEN NVL(RTB2.YEAR_SUC_TARGET_CNT, 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND SALEREC.CONT_RSLT IN ('B05')) / NVL(RTB2.YEAR_SUC_TARGET_CNT, 0) * 100, 2) END AS REF_Y_LOAN_RATE2 "); // --YTD轉介成功件數達成率 : YTD轉介成功件數/年度轉介成功目標件數
				sb.append("  FROM TBCAM_REF_TARG_BRH RTB "); 
				sb.append("  LEFT JOIN (SELECT PARAM_CODE AS REF_PROD FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.REF_PROD') PAR ON 1 = 1 "); 
				sb.append("  LEFT JOIN TBCAM_REF_TARG_BRH RTB2 ON RTB.YYYYMM = RTB2.YYYYMM AND RTB.BRANCH_NBR = RTB2.BRANCH_NBR AND PAR.REF_PROD = RTB2.REF_PROD "); 
				sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON RTB.BRANCH_NBR = DEFN.BRANCH_NBR "); 
				sb.append("  WHERE 1 = 1 "); 

				if (StringUtils.isNotBlank(inputVO.getRefProd())) //轉介商品
					sb.append("AND PAR.REF_PROD <> :refProd ");
				
				if (StringUtils.isNotBlank(inputVO.getTxnDate())) //轉介商品
					sb.append("AND RTB.YYYYMM = :txnDate "); 
				
				sb.append("  AND REGION_CENTER_ID IS NOT NULL "); 
				sb.append(") "); 

				sb.append("SELECT * ");
				sb.append("FROM ( ");
				sb.append("SELECT TXN_DATE, CENTER_ID, CENTER_NAME, '' AS AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_ID, '' AS BRANCH_NAME, '' AS REF_PROD, "); 
				sb.append("       SUM(REF_M_LOAN_AIMS_NUMBER) AS REF_M_LOAN_AIMS_NUMBER, "); 
				sb.append("       SUM(MTD_LOAN_TOT) AS MTD_LOAN_TOT, "); 
				sb.append("       TRUNC(AVG(REF_M_LOAN_RATE1), 2) AS REF_M_LOAN_RATE1, "); 
				sb.append("       SUM(MTD_LOAN_INTO_PIECES_NUMBER) AS MTD_LOAN_INTO_PIECES_NUMBER, ");
				sb.append("       SUM(REF_M_LOAN_SUCCESS_NUMBER) AS REF_M_LOAN_SUCCESS_NUMBER, ");
				sb.append("       SUM(MTD_LOAN_SUCCESS_NUMBER) AS MTD_LOAN_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_LOAN_RATE2), 2) AS REF_M_LOAN_RATE2, ");
				sb.append("       SUM(REF_Y_LOAN_AIMS_NUMBER) AS REF_Y_LOAN_AIMS_NUMBER, ");
				sb.append("       SUM(YTD_LOAN_TOT) AS YTD_LOAN_TOT, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE1), 2) AS REF_Y_LOAN_RATE1, ");
				sb.append("       SUM(YTD_LOAN_INTO_TOT) AS YTD_LOAN_INTO_TOT, ");
				sb.append("       SUM(REF_Y_LOAN_SUCCESS_NUMBER) AS REF_Y_LOAN_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_YTD_LOAN_SUCCESS_NUMBER) AS REF_YTD_LOAN_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE2), 2) AS REF_Y_LOAN_RATE2 ");
				sb.append("FROM DTL ");
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
					sb.append("AND CENTER_ID = :regionCenterID "); //區域代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND CENTER_ID IN (:regionCenterIDList) ");
				}
				sb.append("GROUP BY TXN_DATE, CENTER_ID, CENTER_NAME ");
				sb.append("UNION ");
				sb.append("SELECT TXN_DATE, CENTER_ID, CENTER_NAME, AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_ID, '' AS BRANCH_NAME, '' AS REF_PROD, ");
				sb.append("       SUM(REF_M_LOAN_AIMS_NUMBER) AS REF_M_LOAN_AIMS_NUMBER, "); 
				sb.append("       SUM(MTD_LOAN_TOT) AS MTD_LOAN_TOT, "); 
				sb.append("       TRUNC(AVG(REF_M_LOAN_RATE1), 2) AS REF_M_LOAN_RATE1, "); 
				sb.append("       SUM(MTD_LOAN_INTO_PIECES_NUMBER) AS MTD_LOAN_INTO_PIECES_NUMBER, ");
				sb.append("       SUM(REF_M_LOAN_SUCCESS_NUMBER) AS REF_M_LOAN_SUCCESS_NUMBER, ");
				sb.append("       SUM(MTD_LOAN_SUCCESS_NUMBER) AS MTD_LOAN_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_LOAN_RATE2), 2) AS REF_M_LOAN_RATE2, ");
				sb.append("       SUM(REF_Y_LOAN_AIMS_NUMBER) AS REF_Y_LOAN_AIMS_NUMBER, ");
				sb.append("       SUM(YTD_LOAN_TOT) AS YTD_LOAN_TOT, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE1), 2) AS REF_Y_LOAN_RATE1, ");
				sb.append("       SUM(YTD_LOAN_INTO_TOT) AS YTD_LOAN_INTO_TOT, ");
				sb.append("       SUM(REF_Y_LOAN_SUCCESS_NUMBER) AS REF_Y_LOAN_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_YTD_LOAN_SUCCESS_NUMBER) AS REF_YTD_LOAN_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE2), 2) AS REF_Y_LOAN_RATE2 ");
				sb.append("FROM DTL ");
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
					sb.append("AND CENTER_ID = :regionCenterID "); //區域代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND CENTER_ID IN (:regionCenterIDList) ");
				}
				if (StringUtils.isNotBlank(inputVO.getBranchAreaID()) && !"null".equals(inputVO.getBranchAreaID())) {
					sb.append("AND AREA_ID = :branchAreaID "); //營運區代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND AREA_ID IN (:branchAreaIDList) ");
				}
				sb.append("GROUP BY TXN_DATE, CENTER_ID, CENTER_NAME, AREA_ID, BRANCH_AREA_NAME ");
				sb.append("UNION ");
				sb.append("SELECT TXN_DATE, CENTER_ID, CENTER_NAME, AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, REF_PROD, ");
				sb.append("       SUM(REF_M_LOAN_AIMS_NUMBER) AS REF_M_LOAN_AIMS_NUMBER, "); 
				sb.append("       SUM(MTD_LOAN_TOT) AS MTD_LOAN_TOT, "); 
				sb.append("       TRUNC(AVG(REF_M_LOAN_RATE1), 2) AS REF_M_LOAN_RATE1, "); 
				sb.append("       SUM(MTD_LOAN_INTO_PIECES_NUMBER) AS MTD_LOAN_INTO_PIECES_NUMBER, ");
				sb.append("       SUM(REF_M_LOAN_SUCCESS_NUMBER) AS REF_M_LOAN_SUCCESS_NUMBER, ");
				sb.append("       SUM(MTD_LOAN_SUCCESS_NUMBER) AS MTD_LOAN_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_LOAN_RATE2), 2) AS REF_M_LOAN_RATE2, ");
				sb.append("       SUM(REF_Y_LOAN_AIMS_NUMBER) AS REF_Y_LOAN_AIMS_NUMBER, ");
				sb.append("       SUM(YTD_LOAN_TOT) AS YTD_LOAN_TOT, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE1), 2) AS REF_Y_LOAN_RATE1, ");
				sb.append("       SUM(YTD_LOAN_INTO_TOT) AS YTD_LOAN_INTO_TOT, ");
				sb.append("       SUM(REF_Y_LOAN_SUCCESS_NUMBER) AS REF_Y_LOAN_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_YTD_LOAN_SUCCESS_NUMBER) AS REF_YTD_LOAN_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE2), 2) AS REF_Y_LOAN_RATE2 ");
				sb.append("FROM DTL ");
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
					sb.append("AND CENTER_ID = :regionCenterID "); //區域代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND CENTER_ID IN (:regionCenterIDList) ");
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranchAreaID()) && !"null".equals(inputVO.getBranchAreaID())) {
					sb.append("AND AREA_ID = :branchAreaID "); //營運區代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND AREA_ID IN (:branchAreaIDList) ");
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranchID()) && Integer.valueOf(inputVO.getBranchID()) > 0) {
					sb.append("AND BRANCH_ID = :branchID "); //分行代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND BRANCH_ID IN (:branchIDList) ");
				}
				sb.append("GROUP BY TXN_DATE, CENTER_ID, CENTER_NAME, AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, REF_PROD ");
				sb.append(") ");

				sb.append("ORDER BY CENTER_ID ASC, AREA_ID DESC, BRANCH_ID DESC, ");
				sb.append("         (CASE WHEN CENTER_ID IS NOT NULL AND AREA_ID IS NULL AND BRANCH_ID IS NULL THEN 1 ");
				sb.append("               WHEN CENTER_ID IS NOT NULL AND AREA_ID IS NOT NULL AND BRANCH_ID IS NULL THEN 2 ");
				sb.append("               WHEN CENTER_ID IS NOT NULL AND AREA_ID IS NULL AND BRANCH_ID IS NOT NULL THEN 3 ");
				sb.append("          ELSE 99 END), REF_PROD ");

				break;
			case 5:
				sb.append(", DTL AS ( ");
				sb.append("  SELECT DISTINCT RTB.YYYYMM AS TXN_DATE, DEFN.REGION_CENTER_ID AS CENTER_ID, DEFN.REGION_CENTER_NAME AS CENTER_NAME, DEFN.BRANCH_AREA_ID AS AREA_ID, DEFN.BRANCH_AREA_NAME, RTB.BRANCH_NBR AS BRANCH_ID, DEFN.BRANCH_NAME, ");
				sb.append("         PAR.REF_PROD, ");
				sb.append("         NVL(RTB2.MON_TARGET_CNT, 0) AS REF_M_INV_AIMS_NUMBER, "); //--每月轉介目標參考件數
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD) AS MTD_INV_TOT, "); //--MTD轉介件數
				sb.append("         CASE WHEN NVL(RTB2.MON_TARGET_CNT, 0) = 0 THEN 0 ELSE (SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD) / NVL(RTB2.MON_TARGET_CNT, 0) * 100 END AS REF_M_INV_RATE1, "); //--MTD轉介件數達成率 : MTD轉介件數/每月轉介目標參考件數
				sb.append("         NVL(RTB2.MON_SUC_TARGET_CNT, 0) AS REF_M_INV_SUCCESS_NUMBER, "); //--每月轉介成功件數
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND CONT_RSLT = 'B08') AS REF_MTD_INV_SUCCESS_NUMBER, "); //--MTD轉介成功件數 : 轉介案件進度回報當月選2
				sb.append("         CASE WHEN NVL(RTB2.MON_SUC_TARGET_CNT, 0) = 0 THEN 0 ELSE (SELECT COUNT(1) FROM SALEREC WHERE SALEREC.TXN_DATE = :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND CONT_RSLT = 'B08') / NVL(RTB2.MON_SUC_TARGET_CNT, 0) * 100 END AS REF_M_INV_RATE2, "); //--MTD轉介成功件數達成率 : MTD轉介成功件數/每月轉介成功目標件數
				sb.append("         NVL(RTB2.YEAR_TARGET_CNT, 0) AS REF_Y_INV_AIMS_NUMBER, ");
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD) AS YTD_INV_TOT, "); //--YTD轉介件數 : 自7月起該轉介人總轉介件數
				sb.append("         CASE WHEN NVL(RTB2.YEAR_TARGET_CNT, 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD) / NVL(RTB2.YEAR_TARGET_CNT, 0) * 100, 2) END AS REF_Y_LOAN_RATE1, "); // --YTD轉介件數達成率 : YTD轉介件數/年度轉介目標參考件數
				sb.append("         NVL(RTB2.YEAR_SUC_TARGET_CNT, 0) AS REF_Y_INV_SUCCESS_NUMBER, "); //--年度轉介成功目標件數
				sb.append("         (SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND CONT_RSLT = 'B08') AS REF_YTD_INV_SUCCESS_NUMBER, "); //--YTD轉介成功件數 : 自7月起轉介案件進度回報選2
				sb.append("         CASE WHEN NVL(RTB2.YEAR_SUC_TARGET_CNT, 0) = 0 THEN 0 ELSE (SELECT COUNT(1) FROM SALEREC WHERE SUBSTR(SALEREC.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(SALEREC.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.BRANCH_ID = RTB.BRANCH_NBR AND SALEREC.REF_PROD = PAR.REF_PROD AND CONT_RSLT = 'B08') / NVL(RTB2.YEAR_SUC_TARGET_CNT, 0) * 100 END AS REF_M_INV_RATE3 "); //--YTD轉介成功件數達成率 : YTD轉介成功件數/年度轉介成功目標件數
				sb.append("  FROM TBCAM_REF_TARG_BRH RTB ");
				sb.append("  LEFT JOIN (SELECT PARAM_CODE AS REF_PROD FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CAM.REF_PROD') PAR ON 1 = 1 ");
				sb.append("  LEFT JOIN TBCAM_REF_TARG_BRH RTB2 ON RTB.YYYYMM = RTB2.YYYYMM AND RTB.BRANCH_NBR = RTB2.BRANCH_NBR AND PAR.REF_PROD = RTB2.REF_PROD ");
				sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON RTB.BRANCH_NBR = DEFN.BRANCH_NBR ");
				sb.append("  WHERE 1 = 1 ");

				if (StringUtils.isNotBlank(inputVO.getRefProd())) //轉介商品
					sb.append("AND PAR.REF_PROD = :refProd ");

				if (StringUtils.isNotBlank(inputVO.getTxnDate())) //轉介商品
					sb.append("AND RTB.YYYYMM = :txnDate "); 
				
				sb.append("  AND REGION_CENTER_ID IS NOT NULL ");
				sb.append(") ");

				sb.append("SELECT * ");
				sb.append("FROM ( ");
				sb.append("SELECT TXN_DATE, CENTER_ID, CENTER_NAME, '' AS AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_ID, '' AS BRANCH_NAME, '' AS REF_PROD, ");
				sb.append("       SUM(REF_M_INV_AIMS_NUMBER) AS REF_M_INV_AIMS_NUMBER, ");
				sb.append("       SUM(MTD_INV_TOT) AS MTD_INV_TOT, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE1), 2) AS REF_M_INV_RATE1, ");
				sb.append("       SUM(REF_M_INV_SUCCESS_NUMBER) AS REF_M_INV_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_MTD_INV_SUCCESS_NUMBER) AS REF_MTD_INV_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE2), 2) AS REF_M_INV_RATE2, ");
				sb.append("       SUM(REF_Y_INV_AIMS_NUMBER) AS REF_Y_INV_AIMS_NUMBER, ");
				sb.append("       SUM(YTD_INV_TOT) AS YTD_INV_TOT, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE1), 2) AS REF_Y_LOAN_RATE1, ");
				sb.append("       SUM(REF_Y_INV_SUCCESS_NUMBER) AS REF_Y_INV_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_YTD_INV_SUCCESS_NUMBER) AS REF_YTD_INV_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE3), 2) AS REF_Y_INV_RATE2 ");
				sb.append("FROM DTL ");
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
					sb.append("AND CENTER_ID = :regionCenterID "); //區域代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND CENTER_ID IN (:regionCenterIDList) ");
				}
				sb.append("GROUP BY TXN_DATE, CENTER_ID, CENTER_NAME ");
				sb.append("UNION ");
				sb.append("SELECT TXN_DATE, CENTER_ID, CENTER_NAME, AREA_ID, BRANCH_AREA_NAME, '' AS BRANCH_ID, '' AS BRANCH_NAME, '' AS REF_PROD, ");
				sb.append("       SUM(REF_M_INV_AIMS_NUMBER) AS REF_M_INV_AIMS_NUMBER, ");
				sb.append("       SUM(MTD_INV_TOT) AS MTD_INV_TOT, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE1), 2) AS REF_M_INV_RATE1, ");
				sb.append("       SUM(REF_M_INV_SUCCESS_NUMBER) AS REF_M_INV_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_MTD_INV_SUCCESS_NUMBER) AS REF_MTD_INV_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE2), 2) AS REF_M_INV_RATE2, ");
				sb.append("       SUM(REF_Y_INV_AIMS_NUMBER) AS REF_Y_INV_AIMS_NUMBER, ");
				sb.append("       SUM(YTD_INV_TOT) AS YTD_INV_TOT, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE1), 2) AS REF_Y_LOAN_RATE1, ");
				sb.append("       SUM(REF_Y_INV_SUCCESS_NUMBER) AS REF_Y_INV_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_YTD_INV_SUCCESS_NUMBER) AS REF_YTD_INV_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE3), 2) AS REF_Y_INV_RATE2 ");
				sb.append("FROM DTL ");
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
					sb.append("AND CENTER_ID = :regionCenterID "); //區域代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND CENTER_ID IN (:regionCenterIDList) ");
				}
				if (StringUtils.isNotBlank(inputVO.getBranchAreaID()) && !"null".equals(inputVO.getBranchAreaID())) {
					sb.append("AND AREA_ID = :branchAreaID "); //營運區代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND AREA_ID IN (:branchAreaIDList) ");
				}
				sb.append("GROUP BY TXN_DATE, CENTER_ID, CENTER_NAME, AREA_ID, BRANCH_AREA_NAME ");
				sb.append("UNION ");
				sb.append("SELECT TXN_DATE, CENTER_ID, CENTER_NAME, AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, REF_PROD, ");
				sb.append("       SUM(REF_M_INV_AIMS_NUMBER) AS REF_M_INV_AIMS_NUMBER, ");
				sb.append("       SUM(MTD_INV_TOT) AS MTD_INV_TOT, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE1), 2) AS REF_M_INV_RATE1, ");
				sb.append("       SUM(REF_M_INV_SUCCESS_NUMBER) AS REF_M_INV_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_MTD_INV_SUCCESS_NUMBER) AS REF_MTD_INV_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE2), 2) AS REF_M_INV_RATE2, ");
				sb.append("       SUM(REF_Y_INV_AIMS_NUMBER) AS REF_Y_INV_AIMS_NUMBER, ");
				sb.append("       SUM(YTD_INV_TOT) AS YTD_INV_TOT, ");
				sb.append("       TRUNC(AVG(REF_Y_LOAN_RATE1), 2) AS REF_Y_LOAN_RATE1, ");
				sb.append("       SUM(REF_Y_INV_SUCCESS_NUMBER) AS REF_Y_INV_SUCCESS_NUMBER, ");
				sb.append("       SUM(REF_YTD_INV_SUCCESS_NUMBER) AS REF_YTD_INV_SUCCESS_NUMBER, ");
				sb.append("       TRUNC(AVG(REF_M_INV_RATE3), 2) AS REF_Y_INV_RATE2 ");
				sb.append("FROM DTL ");
				sb.append("WHERE 1 = 1 ");
				if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
					sb.append("AND CENTER_ID = :regionCenterID "); //區域代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND CENTER_ID IN (:regionCenterIDList) ");
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranchAreaID()) && !"null".equals(inputVO.getBranchAreaID())) {
					sb.append("AND AREA_ID = :branchAreaID "); //營運區代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND AREA_ID IN (:branchAreaIDList) ");
				}
			
				if (StringUtils.isNotBlank(inputVO.getBranchID()) && Integer.valueOf(inputVO.getBranchID()) > 0) {
					sb.append("AND BRANCH_ID = :branchID "); //分行代碼
				} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND BRANCH_ID IN (:branchIDList) ");
				}
				sb.append("GROUP BY TXN_DATE, CENTER_ID, CENTER_NAME, AREA_ID, BRANCH_AREA_NAME, BRANCH_ID, BRANCH_NAME, REF_PROD ");
				sb.append(") ");
				
				sb.append("ORDER BY CENTER_ID ASC, AREA_ID DESC, BRANCH_ID DESC, ");
				sb.append("         (CASE WHEN CENTER_ID IS NOT NULL AND AREA_ID IS NULL AND BRANCH_ID IS NULL THEN 1 ");
				sb.append("               WHEN CENTER_ID IS NOT NULL AND AREA_ID IS NOT NULL AND BRANCH_ID IS NULL THEN 2 ");
				sb.append("               WHEN CENTER_ID IS NOT NULL AND AREA_ID IS NULL AND BRANCH_ID IS NOT NULL THEN 3 ");
				sb.append("          ELSE 99 END) ");

				break;
		}
		
		if (StringUtils.isNotBlank(inputVO.getRefProd())) //轉介商品
			queryCondition.setObject("refProd", ("5".equals(inputVO.getRefProd()) ? "5" : "5"));
		if (StringUtils.isNotBlank(inputVO.getTxnDate()))
			queryCondition.setObject("txnDate", inputVO.getTxnDate());
		
		if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
			queryCondition.setObject("regionCenterID", inputVO.getRegionID());
		} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranchAreaID()) && !"null".equals(inputVO.getBranchAreaID())) {
			queryCondition.setObject("branchAreaID", inputVO.getBranchAreaID());
		} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranchID()) && Integer.valueOf(inputVO.getBranchID()) > 0) {
			queryCondition.setObject("branchID", inputVO.getBranchID());
		} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		this.sendRtnObject(outputVO);
	}
	
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		REF150InputVO inputVO = (REF150InputVO) body;
		REF150OutputVO outputVO = new REF150OutputVO();
		
		List<Map<String, String>> list = inputVO.getEXPORT_LST();
		
		String fileName = "轉介成效全行彙總表_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("轉介成效全行彙總表_" + sdfYYYYMMDD.format(new Date()));
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
		
		String[] headerLine1 = new String[]{};
		String[] headerLine2 = new String[]{};
		String[] mainLine = new String[]{};
		if (StringUtils.equals("0", inputVO.getRefProd())) {
			String[] header1 = {"轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", 
								"貸款類商品", "貸款類商品", "貸款類商品", "貸款類商品", 
								"貸款類商品", "貸款類商品", "貸款類商品", "貸款類商品", 
								"貸款類商品", "貸款類商品", "貸款類商品", "貸款類商品", "貸款類商品", "貸款類商品",
								"貸款類商品"};
			
			String[] header2 = {"轉介年月", "業務處", "營運區", "分行名稱", 
								"轉介商品", "每月轉介目標參考件數", "MTD轉介件數", "MTD轉介件數達成率", 
								"MTD進件數", "每月轉介成功目標件數", "MTD轉介成功件數", "MTD轉介成功件數達成率", 
								"年度轉介目標參考件數", "YTD轉介件數", "YTD轉介件數達成率", "YTD進件數", "年度轉介成功目標件數", "YTD轉介成功件數", 
								"YTD轉介成功件數達成率"};
			
			String[] main    = {"TXN_DATE", "CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", 
								"REF_PROD", "REF_M_LOAN_AIMS_NUMBER", "MTD_LOAN_TOT", "REF_M_LOAN_RATE1", 
								"MTD_LOAN_INTO_PIECES_NUMBER", "REF_M_LOAN_SUCCESS_NUMBER", "MTD_LOAN_SUCCESS_NUMBER", "REF_M_LOAN_RATE2", 
								"REF_Y_LOAN_AIMS_NUMBER", "YTD_LOAN_TOT", "REF_Y_LOAN_RATE1", "YTD_LOAN_INTO_TOT", "REF_Y_LOAN_SUCCESS_NUMBER", "REF_YTD_LOAN_SUCCESS_NUMBER", 
								"REF_Y_LOAN_RATE2"};
	
			headerLine1 = header1;
			headerLine2 = header2;
			mainLine = main;
		} else {
			String[] header1 = {"轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", 
								"投保商品", "投保商品", "投保商品", "投保商品", 
								"投保商品", "投保商品", "投保商品", "投保商品", "投保商品", "投保商品",
								"投保商品", "投保商品"};
			
			String[] header2 = {"轉介年月", "業務處", "營運區", "分行名稱",  
								"每月轉介目標參考件數", "MTD轉介件數", "MTD轉介件數達成率", "每月轉介成功目標件數", 
								"MTD轉介成功件數", "MTD轉介成功件數達成率", "年度轉介目標參考件數", "YTD轉介件數", "YTD轉介件數達成率", "年度轉介成功目標件數", 
								"YTD轉介成功件數", "YTD轉介成功件數達成率"};
			
			String[] main    = {"TXN_DATE", "CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", 
								"REF_M_INV_AIMS_NUMBER", "MTD_INV_TOT", "REF_M_INV_RATE1", "REF_M_INV_SUCCESS_NUMBER", 
								"REF_MTD_INV_SUCCESS_NUMBER", "REF_M_INV_RATE2", "REF_Y_INV_AIMS_NUMBER", "YTD_INV_TOT", "REF_Y_LOAN_RATE1", "REF_Y_INV_SUCCESS_NUMBER", 
								"REF_YTD_INV_SUCCESS_NUMBER", "REF_Y_INV_RATE2"};

			headerLine1 = header1;
			headerLine2 = header2;
			mainLine = main;
		}
		
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
		
		index++; //next row
		row = sheet.createRow(index);
		for (int i = 0; i < headerLine2.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine2[i]);
			
			if ("".equals(headerLine2[i])) {
				sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
			}
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
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				if (StringUtils.equals("REF_PROD", mainLine[j])) {
					cell.setCellValue(refProdMap.get(checkIsNull(map, mainLine[j])));
				} else if (mainLine[j].indexOf("_RATE") >= 0){
					cell.setCellValue(checkIsNull(map, mainLine[j]) + "%");
				} else {
					cell.setCellValue(checkIsNull(map, mainLine[j]));
				}
			}

			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
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