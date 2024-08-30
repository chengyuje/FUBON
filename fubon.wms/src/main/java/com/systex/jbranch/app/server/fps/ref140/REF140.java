package com.systex.jbranch.app.server.fps.ref140;

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
@Component("ref140")
@Scope("request")
public class REF140 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void query (Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		REF140InputVO inputVO = (REF140InputVO) body;
		REF140OutputVO outputVO = new REF140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH SALEREC AS ( ");
		sb.append("  SELECT TO_CHAR(TEMP.TXN_DATE, 'yyyyMM') AS TXN_DATE, TEMP.USERID, TEMP.USERNAME, TEMP.USERROLE, TEMP.REF_PROD ");
		sb.append("  FROM TBCAM_LOAN_SALEREC TEMP ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND TO_CHAR(TEMP.TXN_DATE, 'yyyy') = SUBSTR(:txnDate, 0, 4) "); 
		sb.append("  AND TRIM(USERID) IS NOT NULL ");
		sb.append("  GROUP BY TO_CHAR(TXN_DATE, 'yyyyMM'), USERID, USERNAME, USERROLE, REF_PROD ");
		sb.append(") ");
		sb.append(", TEMP AS ( ");
		sb.append("  SELECT DISTINCT TEMP.SEQ, SALEREC.TXN_DATE, TEMP.USERID, TEMP.CONT_RSLT, TEMP.REF_PROD, TEMP.NON_GRANT_REASON ");
		sb.append("  FROM TBCAM_LOAN_SALEREC TEMP ");
		sb.append("  INNER JOIN SALEREC ON TEMP.USERID = SALEREC.USERID ");
		sb.append("                     AND TO_CHAR(TEMP.TXN_DATE, 'yyyyMMdd') >= TO_CHAR(TO_DATE(SALEREC.TXN_DATE || '01', 'yyyyMMdd'), 'yyyyMMdd') ");
		sb.append("                     AND TO_CHAR(TEMP.TXN_DATE, 'yyyyMMdd') <= TO_CHAR(LAST_DAY(TO_DATE(SALEREC.TXN_DATE || '01', 'yyyyMMdd')), 'yyyyMMdd') ");
		sb.append(") ");
		
		sb.append("SELECT SALEREC.TXN_DATE AS YYYYMM, "); //--轉介年月	
		sb.append("       INFO.REGION_CENTER_ID, "); 
		sb.append("       INFO.REGION_CENTER_NAME, "); 
		sb.append("       INFO.BRANCH_AREA_ID, "); 
		sb.append("       INFO.BRANCH_AREA_NAME, "); 
		sb.append("       INFO.BRANCH_NBR, "); 
		sb.append("       INFO.BRANCH_NAME AS REF_SRC, "); //--分行名稱
		sb.append("       SALEREC.USERROLE, "); //--轉介人身份	
		sb.append("       SALEREC.REF_PROD, "); //--轉介商品
		sb.append("       INFO.EMP_NAME, "); //--受轉介人姓名
		sb.append("       SALEREC.USERID AS EMP_ID, "); //--員編
		switch (Integer.valueOf(inputVO.getRefProd())) {
			case 0:
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD) AS MTD_TOT, "); //  --MTD轉介件數
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B01', 'B03', 'B02', 'B04', 'C01', 'B05', 'B06', 'B07', 'B12')) AS REF_MTD_CLOSE_NUMBER, "); //--MTD已結案轉介件數
				sb.append("       TRUNC((SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B01', 'B03', 'B02', 'B04', 'C01', 'B05', 'B06', 'B07', 'B12')) / (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD) * 100, 2) AS REF_MTD_PROCESSING_RATE, "); //--MTD處理率
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND (TEMP.CONT_RSLT IS NULL OR TRIM(TEMP.CONT_RSLT) = '')) AS REF_MTD_N_CLOSE_NUMBER, "); //--MTD未結案轉介件數
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.REF_PROD = SALEREC.REF_PROD AND TEMP.CONT_RSLT = 'A01') AS REF_MTD_PROCESSING_NUMBER, "); //--MTD處理中轉介件數
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B01', 'B03', 'B02', 'B04')) AS REF_MTD_NO_INTO, "); //--MTD未進件件數
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B01')) AS REF_MTD_NO_INTO_R1, "); //--MTD未進件原因-僅詢問
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B03')) AS REF_MTD_NO_INTO_R2, "); //--MTD未進件原因-利率無競爭力
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B02')) AS REF_MTD_NO_INTO_R3, "); //--MTD未進件原因-不符客戶需求
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B04')) AS REF_MTD_NO_INTO_R4, "); //--MTD未進件原因-其他
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B06', 'B07', 'B12')) AS REF_MTD_INTO_PIECES_NUMBER, "); //--MTD已進件未撥款件數
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B06')) AS REF_MTD_INTO_PIECES_R1, "); //--MTD已進件-徵審婉拒
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B07')) AS REF_MTD_INTO_PIECES_R2, "); //--MTD已進件-自行撤件
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B12')) AS REF_MTD_INTO_PIECES_R3, "); // --MTD已進件-已核不撥
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B12') AND TEMP.NON_GRANT_REASON = '01') AS REF_MTD_NOT_DIALED_R1, "); //--MTD已核不撥原因-額度不符需求
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B12') AND TEMP.NON_GRANT_REASON = '02') AS REF_MTD_NOT_DIALED_R2, "); //--MTD已核不撥原因-利率不符需求
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B12') AND TEMP.NON_GRANT_REASON = '03') AS REF_MTD_NOT_DIALED_R3, "); //--MTD已核不撥原因-額度及利率不符需求
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B12') AND TEMP.NON_GRANT_REASON = '04') AS REF_MTD_NOT_DIALED_R4, "); //--MTD已核不撥原因-已無資金需求
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B12') AND TEMP.NON_GRANT_REASON = '05') AS REF_MTD_NOT_DIALED_R5, "); //--MTD已核不撥原因-他行已核貸
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B12') AND TEMP.NON_GRANT_REASON = '06') AS REF_MTD_NOT_DIALED_R6 "); //--MTD已核不撥原因-其他

				break;
			case 5:
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD) AS MTD_TOT, "); //   --MTD轉介件數
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B08', 'B09', 'B10', 'B11')) AS REF_MTD_CLOSE_NUMBER, "); //  --MTD已結案轉介件數
				sb.append("       TRUNC((SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT IN ('B08', 'B09', 'B10', 'B11')) / (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD) * 100, 2) AS REF_MTD_PROCESSING_RATE, "); //  --MTD處理率
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND (TEMP.CONT_RSLT IS NULL OR TRIM(TEMP.CONT_RSLT) = '')) AS REF_MTD_N_CLOSE_NUMBER, "); //  --MTD未結案轉介件數
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT = 'A01') AS REF_MTD_PROCESSING_NUMBER, "); //  --MTD處理中轉介件數
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT = 'B09') AS REF_MTD_INS_UNSOLE_R1, "); //  --MTD未成交原因-商品無競爭力
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT = 'B10') AS REF_MTD_INS_UNSOLE_R2, "); //  --MTD未成交原因-客戶無需求
				sb.append("       (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD AND TEMP.CONT_RSLT = 'B11') AS REF_MTD_INS_UNSOLE_R3 "); //  --MTD未成交原因-其他

				break;
		}
		sb.append("FROM SALEREC "); 
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON SALEREC.USERID = INFO.EMP_ID "); 
		sb.append("WHERE 1 = 1 "); 
		sb.append("AND (SELECT COUNT(1) FROM TEMP WHERE TEMP.USERID = SALEREC.USERID AND TEMP.TXN_DATE = SALEREC.TXN_DATE AND SALEREC.REF_PROD = TEMP.REF_PROD) > 0 "); //

		if (StringUtils.isNotBlank(inputVO.getUserID())) { //受轉介人員編
			sb.append("AND SALEREC.USERID LIKE :userID ");
			queryCondition.setObject("userID", "%" + inputVO.getUserID() + "%");
		}
			
		if (StringUtils.isNotBlank(inputVO.getRefProd())) { //轉介商品
			sb.append("AND SALEREC.REF_PROD ").append(((StringUtils.equals("5", inputVO.getRefProd())) ? "=" : "<")).append(" '5' ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getTxnDate())) {
			sb.append("AND SALEREC.TXN_DATE = :txnDate "); 
			queryCondition.setObject("txnDate", inputVO.getTxnDate());
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
		
		REF140InputVO inputVO = (REF140InputVO) body;
		REF140OutputVO outputVO = new REF140OutputVO();
		
		List<Map<String, String>> list = inputVO.getEXPORT_LST();
		
		String fileName = "受轉介人進度追蹤查詢_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("受轉介人進度追蹤查詢_" + sdfYYYYMMDD.format(new Date()));
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
								"轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況",
								"未進件原因", "未進件原因", "未進件原因", "未進件原因", "未進件原因", 
								"已進件未撥款原因", "已進件未撥款原因", "已進件未撥款原因", "已進件未撥款原因", 
								"已核不撥原因", "已核不撥原因", "已核不撥原因", "已核不撥原因", "已核不撥原因", "已核不撥原因"};
			
			String[] header2 = {"轉介年月", "分行名稱", "受轉介人身份", "受轉介人姓名", "員編", "轉介商品", 
								"MTD轉介件數", "MTD已結案轉介件數", "MTD處理率", "MTD未結案轉介件數", "MTD處理中轉介件數", 
								"MTD未進件數", "MTD未進件原因-僅詢問", "MTD未進件原因-利率無競爭力", "MTD未進件原因-不符合客戶需求", "MTD未進件原因-其他", 
								"MTD已進件未撥款件數", "MTD已進件-徵審婉拒", "MTD已進件-自行撤件", "MTD已進件-已核不撥", 
								"MTD已核不撥原因-額度不符需求", "MTD已核不撥原因-利率不符需求", "MTD已核不撥原因-額度及利率不符需求", "MTD已核不撥原因-已無資金需求", "MTD已核不撥原因-他行已核貸", "MTD已核不撥原因-其他"};
			
			String[] main    = {"YYYYMM", "REF_SRC", "USERROLE", "EMP_NAME", "EMP_ID", "REF_PROD", 
								"MTD_TOT", "REF_MTD_CLOSE_NUMBER", "REF_MTD_PROCESSING_RATE", "REF_MTD_N_CLOSE_NUMBER", "REF_MTD_PROCESSING_NUMBER", 
								"REF_MTD_NO_INTO", "REF_MTD_NO_INTO_R1", "REF_MTD_NO_INTO_R2", "REF_MTD_NO_INTO_R3", "REF_MTD_NO_INTO_R4", 
								"REF_MTD_INTO_PIECES_NUMBER", "REF_MTD_INTO_PIECES_R1", "REF_MTD_INTO_PIECES_R2", "REF_MTD_INTO_PIECES_R3", 
								"REF_MTD_NOT_DIALED_R1", "REF_MTD_NOT_DIALED_R2", "REF_MTD_NOT_DIALED_R3", "REF_MTD_NOT_DIALED_R4", "REF_MTD_NOT_DIALED_R5", "REF_MTD_NOT_DIALED_R6"};
	
			headerLine1 = header1;
			headerLine2 = header2;
			mainLine = main;
		} else {
			String[] header1 = {"轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", "轉介基本資料", 
								"轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況", "轉介案件處理狀況",
								"未成交原因", "未成交原因", "未成交原因"};
			
			String[] header2 = {"轉介年月", "分行名稱", "受轉介人身份", "受轉介人姓名", "員編", "轉介商品", 
								"MTD轉介件數", "MTD已結案轉介件數", "MTD處理率", "MTD未結案轉介件數", "MTD處理中轉介件數", 
								"MTD未成交原因-商品無競爭力", "MTD未成交原因-客戶無需求", "MTD未成交原因-其他"};
			
			String[] main    = {"YYYYMM", "REF_SRC", "USERROLE", "EMP_NAME", "EMP_ID", "REF_PROD", 
								"MTD_TOT", "REF_MTD_CLOSE_NUMBER", "REF_MTD_PROCESSING_RATE", "REF_MTD_N_CLOSE_NUMBER", "REF_MTD_PROCESSING_NUMBER", 
								"REF_MTD_INS_UNSOLE_R1", "REF_MTD_INS_UNSOLE_R2", "REF_MTD_INS_UNSOLE_R3"};

			
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

		Map<String, String> refUserMap = new XmlInfo().doGetVariable("CAM.REF_USER_ROLE", FormatHelper.FORMAT_3);
		Map<String, String> refProdMap = new XmlInfo().doGetVariable("CAM.REF_PROD", FormatHelper.FORMAT_3);

		for (Map<String, String> map : list) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				if (StringUtils.equals("USERROLE", mainLine[j])) {
					cell.setCellValue(refUserMap.get(checkIsNull(map, mainLine[j])));
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
