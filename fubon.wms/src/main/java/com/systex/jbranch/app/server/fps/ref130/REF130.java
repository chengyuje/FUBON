package com.systex.jbranch.app.server.fps.ref130;

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
@Component("ref130")
@Scope("request")
public class REF130 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void query (Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		REF130InputVO inputVO = (REF130InputVO) body;
		REF130OutputVO outputVO = new REF130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("WITH SALEREC AS ( ");
//		sb.append("  SELECT TO_CHAR(TXN_DATE, 'yyyyMM') AS TXN_DATE, SALES_PERSON, SALES_NAME, SALES_ROLE, REF_PROD, SUM(COUNTS) AS COUNTS ");
//		sb.append("  FROM VWCAM_REF_SALEREC ");
//		sb.append("  WHERE 1 = 1 ");
//		sb.append("  AND TO_CHAR(TXN_DATE, 'yyyy') = SUBSTR(:txnDate, 0, 4) "); 
//		sb.append("  GROUP BY TO_CHAR(TXN_DATE, 'yyyyMM'), SALES_PERSON, SALES_NAME, SALES_ROLE, REF_PROD ");

		sb.append("  SELECT TO_CHAR(TXN_DATE, 'yyyyMM') AS TXN_DATE, SALES_PERSON, SALES_NAME, SALES_ROLE, REF_PROD ");
		sb.append("  FROM TBCAM_LOAN_SALEREC ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND TO_CHAR(TXN_DATE, 'yyyy') = SUBSTR(:txnDate, 0, 4) "); 
		sb.append("  AND TRIM(SALES_PERSON) IS NOT NULL ");
		sb.append("  GROUP BY TO_CHAR(TXN_DATE, 'yyyyMM'), SALES_PERSON, SALES_NAME, SALES_ROLE, REF_PROD ");
		sb.append(") ");
		sb.append(", TEMP AS ( ");
		sb.append("  SELECT SALEREC.TXN_DATE, TEMP.SALES_PERSON, TEMP.CONT_RSLT, TEMP.REF_PROD, TEMP.NON_GRANT_REASON ");
		sb.append("  FROM TBCAM_LOAN_SALEREC TEMP ");
		sb.append("  INNER JOIN SALEREC ON TEMP.SALES_PERSON = SALEREC.SALES_PERSON ");
		sb.append("                     AND TO_CHAR(TEMP.TXN_DATE, 'yyyyMMdd') >= TO_CHAR(TO_DATE(SALEREC.TXN_DATE || '01', 'yyyyMMdd'), 'yyyyMMdd') ");
		sb.append("                     AND TO_CHAR(TEMP.TXN_DATE, 'yyyyMMdd') <= TO_CHAR(LAST_DAY(TO_DATE(SALEREC.TXN_DATE || '01', 'yyyyMMdd')), 'yyyyMMdd') ");
		sb.append("                     AND TEMP.REF_PROD = SALEREC.REF_PROD ");
		sb.append(") ");
		
		
		sb.append("SELECT SALEREC.TXN_DATE AS YYYYMM, "); //--轉介年月
		sb.append("       INFO.REGION_CENTER_ID, ");
		sb.append("       INFO.REGION_CENTER_NAME, ");
		sb.append("       INFO.BRANCH_AREA_ID, ");
		sb.append("       INFO.BRANCH_AREA_NAME, ");
		sb.append("       INFO.BRANCH_NBR, ");
		sb.append("       INFO.BRANCH_NAME AS REF_SRC, "); //--分行名稱
		sb.append("       SALEREC.SALES_ROLE, "); //--轉介人身份
		sb.append("       INFO.EMP_NAME, "); //--轉介人姓名
		sb.append("       SALEREC.SALES_PERSON,  "); //--轉介人員編
		sb.append("       SALEREC.REF_PROD, "); //--轉介商品
		sb.append("       NVL(ROL.MON_TARGET_CNT, 0) AS REF_MONTH_AIMS_NUMBER, "); //--每月轉介目標參考件數
		sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD) AS MTD_TOT, "); //--MTD轉介件數
		sb.append("       CASE WHEN NVL(ROL.MON_TARGET_CNT, 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD) / NVL(ROL.MON_TARGET_CNT, 0) * 100, 2) END AS REF_MTD_NUMBER_RATE, "); //--MTD轉介件數達成率
	
		sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B08', 'B09', 'B10', 'B11')) AS REF_MTD_CLOSE_NUMBER, "); //--MTD已結案轉介件數
		sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND (TEMP.CONT_RSLT IS NULL OR TRIM(TEMP.CONT_RSLT) = '')) AS REF_MTD_N_CLOSE_NUMBER, "); //--MTD未結案轉介件數
		sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT = 'A01') AS REF_MTD_PROCESSING_NUMBER, "); //--MTD處理中轉介件數
		
		sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('C01', 'B05', 'B06', 'B07', 'B12')) AS REF_MTD_INTO_NUMBER, "); //--MTD進件數
				sb.append("       CASE WHEN NVL((SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD), 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('C01', 'B05', 'B06', 'B07', 'B12')) / (SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD) * 100, 2) END AS REF_MTD_INTO_RATE, "); //--MTD進件率
	       

		sb.append("       NVL(ROL.MON_SUC_TARGET_CNT, 0) AS REF_MONTH_SUCCESS_NUMBER, "); //--每月轉介成功目標件數
		sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT = 'B08') AS REF_MTD_SUCCESS_NUMBER, "); //--MTD轉介成功件數
		sb.append("       CASE WHEN NVL(ROL.MON_TARGET_CNT, 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND TEMP.TXN_DATE = :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT = 'B08') / NVL(ROL.MON_SUC_TARGET_CNT, 0) * 100, 2) END AS REF_MTD_SUCCESS_RATE, "); //--MTD轉介成功件數達成率
		sb.append("       NVL(ROL.YEAR_SUC_TARGET_CNT, 0) AS REF_YEAR_SUCCESS_NUMBER, "); //--年度轉介成功目標件數

		sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND SUBSTR(TEMP.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(TEMP.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND ((TEMP.REF_PROD = '5' AND TEMP.CONT_RSLT = 'B08') OR (TEMP.REF_PROD <> '5' AND TEMP.CONT_RSLT = 'B05'))) AS REF_YTD_SUCCESS_NUMBER, "); //--YTD轉介成功件數
		sb.append("       CASE WHEN NVL(ROL.YEAR_SUC_TARGET_CNT, 0) = 0 THEN 0 ELSE TRUNC((SELECT COUNT(1) FROM TEMP WHERE TEMP.SALES_PERSON = SALEREC.SALES_PERSON AND SUBSTR(TEMP.TXN_DATE, 0, 4) = SUBSTR(:txnDate, 0, 4) AND SUBSTR(TEMP.TXN_DATE, 0, 6) <= :txnDate AND SALEREC.REF_PROD = TEMP.REF_PROD AND ((SALEREC.REF_PROD = '5' AND TEMP.CONT_RSLT = 'B08') OR (SALEREC.REF_PROD <> '5' AND TEMP.CONT_RSLT = 'B05'))) / NVL(ROL.YEAR_SUC_TARGET_CNT, 0) * 100, 2) END AS REF_YTD_SUCCESS_RATE "); //--YTD轉介成功件數達成率		

		sb.append("FROM SALEREC ");
		sb.append("LEFT JOIN TBCAM_REF_TARG_ROL ROL ON ROL.YYYYMM = SALEREC.TXN_DATE AND ROL.SALES_ROLE = SALEREC.SALES_ROLE AND ROL.REF_PROD = SALEREC.REF_PROD ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON SALEREC.SALES_PERSON = INFO.EMP_ID ");
		sb.append("WHERE 1 = 1 ");
		System.out.println(sb.toString());
//		sb.append("AND SALEREC.COUNTS > 0 ");

		if (StringUtils.isNotBlank(inputVO.getTxnDate())) { //
			sb.append("AND SALEREC.TXN_DATE = :txnDate "); 
		}
		queryCondition.setObject("txnDate", inputVO.getTxnDate());
		
		if (StringUtils.isNotBlank(inputVO.getSalesPerson())) { //轉介人員編
			sb.append("AND SALEREC.SALES_PERSON = :salesPerson ");
			queryCondition.setObject("salesPerson", inputVO.getSalesPerson());
		}
		
		if (StringUtils.isNotBlank(inputVO.getRefProd())) { //轉介商品
			sb.append("AND SALEREC.REF_PROD ").append(((StringUtils.equals("5", inputVO.getRefProd())) ? "=" : "<")).append(" 5 ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getRegionID()) && !"null".equals(inputVO.getRegionID())) {
			sb.append("AND INFO.REGION_CENTER_ID = :regionCenterID "); //區域代碼
			queryCondition.setObject("regionCenterID", inputVO.getRegionID());
		} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sb.append("AND INFO.REGION_CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranchAreaID()) && !"null".equals(inputVO.getBranchAreaID())) {
			sb.append("AND INFO.BRANCH_AREA_ID = :branchAreaID "); //營運區代碼
			queryCondition.setObject("branchAreaID", inputVO.getBranchAreaID());
		} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sb.append("AND INFO.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranchID()) && Integer.valueOf(inputVO.getBranchID()) > 0) {
			sb.append("AND INFO.BRANCH_NBR = :branchID "); //分行代碼
			queryCondition.setObject("branchID", inputVO.getBranchID());
		} else if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sb.append("AND INFO.BRANCH_NBR IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		sb.append("ORDER BY SALEREC.TXN_DATE "); 
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		REF130InputVO inputVO = (REF130InputVO) body;
		REF130OutputVO outputVO = new REF130OutputVO();
		
		List<Map<String, String>> list = inputVO.getEXPORT_LST();
		
		String fileName = "轉介人進度追蹤查詢_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("轉介人進度追蹤查詢_" + sdfYYYYMMDD.format(new Date()));
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
			String[] header1 = {"轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", 
								"轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", 
								"轉介成效", "轉介成效", "轉介成效", "轉介成效", "轉介成效", "轉介成效", "轉介成效", "轉介成效"};
			
			String[] header2 = {"轉介年月", "分行名稱", "轉介人身份", "轉介人姓名", "員編", "轉介商品", 
								"每月轉介目標參考件數", "MTD轉介件數", "MTD轉介件數達成率", "MTD已結案轉介件數", "MTD未結案轉介件數", "MTD處理中轉介件數", 
								"MTD進件數", "MTD進件率", "每月轉介成功目標件數", "MTD轉介成功件數", "MTD轉介成功件數達成率", "年度轉介成功目標件數", "YTD轉介成功件數", "YTD轉介成功件數達成率"};
			
			String[] main    = {"YYYYMM", "REF_SRC", "SALES_ROLE", "EMP_NAME", "SALES_PERSON", "REF_PROD", 
								"REF_MTD_INTO_NUMBER", "MTD_TOT", "REF_MTD_NUMBER_RATE", "REF_MTD_CLOSE_NUMBER", "REF_MTD_N_CLOSE_NUMBER", "REF_MTD_PROCESSING_NUMBER", 
								"REF_MTD_INTO_NUMBER", "REF_MTD_INTO_RATE", "REF_MONTH_SUCCESS_NUMBER", "REF_MTD_SUCCESS_NUMBER", "REF_MTD_SUCCESS_RATE", "REF_YEAR_SUCCESS_NUMBER", "REF_YTD_SUCCESS_NUMBER", "REF_YTD_SUCCESS_RATE"};
	
			headerLine1 = header1;
			headerLine2 = header2;
			mainLine = main;
		} else {
			String[] header1 = {"轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", 
								"轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", 
								"轉介成效", "轉介成效", "轉介成效", "轉介成效", "轉介成效", "轉介成效"};
			
			String[] header2 = {"轉介年月", "分行名稱", "轉介人身份", "轉介人姓名", "員編", "轉介商品", 
								"每月轉介目標參考件數", "MTD轉介件數", "MTD轉介件數達成率", "MTD已結案轉介件數", "MTD未結案轉介件數", "MTD處理中轉介件數", 
								"每月轉介成功目標件數", "MTD轉介成功件數", "MTD轉介成功件數達成率", "年度轉介成功目標件數", "YTD轉介成功件數", "YTD轉介成功件數達成率"};
			
			String[] main    = {"YYYYMM", "REF_SRC", "SALES_ROLE", "EMP_NAME", "SALES_PERSON", "REF_PROD", 
								"REF_MTD_INTO_NUMBER", "MTD_TOT", "REF_MTD_NUMBER_RATE", "REF_MTD_CLOSE_NUMBER", "REF_MTD_N_CLOSE_NUMBER", "REF_MTD_PROCESSING_NUMBER", 
								"REF_MONTH_SUCCESS_NUMBER", "REF_MTD_SUCCESS_NUMBER", "REF_MTD_SUCCESS_RATE", "REF_YEAR_SUCCESS_NUMBER", "REF_YTD_SUCCESS_NUMBER", "REF_YTD_SUCCESS_RATE"};

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

		Map<String, String> salesRoMap = new XmlInfo().doGetVariable("CAM.REF_SALES_ROLE", FormatHelper.FORMAT_3);
		Map<String, String> refProdMap = new XmlInfo().doGetVariable("CAM.REF_PROD", FormatHelper.FORMAT_3);

		for (Map<String, String> map : list) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				if (StringUtils.equals("SALES_ROLE", mainLine[j])) {
					cell.setCellValue(salesRoMap.get(checkIsNull(map, mainLine[j])));
				} else if (StringUtils.equals("REF_PROD", mainLine[j])) {
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
