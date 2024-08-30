package com.systex.jbranch.app.server.fps.pms355;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.app.server.fps.pms227.PMS227InputVO;
import com.systex.jbranch.app.server.fps.pms227.PMS227OutputVO;
import com.systex.jbranch.app.server.fps.pms335.PMS335OutputVO;
import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms355")
@Scope("request")
public class PMS355 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private static String yeaMon = new SimpleDateFormat("yyyyMM").format(new Date()); //預設這個月分
	private static String finalstring;
	
	public void query(Object body, IPrimitiveMap header) throws Exception {
		
		PMS355OutputVO outputVO = new PMS355OutputVO();
		outputVO = this.query(body);

		sendRtnObject(outputVO);
	}
	
	// 查詢
	public PMS355OutputVO query(Object body) throws Exception {

		initUUID();
		XmlInfo xmlInfo = new XmlInfo();
		boolean isHeadMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		PMS355InputVO inputVO = (PMS355InputVO) body;
		PMS355OutputVO outputVO = new PMS355OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT  RPT.YYYYMM, ");
		sb.append("        RPT.REGION_CENTER_ID, ");
		sb.append("        RPT.REGION_CENTER_NAME, ");
		sb.append("        RPT.BRANCH_AREA_ID, ");
		sb.append("        RPT.BRANCH_AREA_NAME, ");
		sb.append("        RPT.BRANCH_NBR, ");
		sb.append("        RPT.BRANCH_NAME, ");
		sb.append("        RPT.AO_CODE, ");
		sb.append("        RPT.AO_EMP_ID, ");
		sb.append("        RPT.AO_EMP_NAME, ");
		sb.append("        RPT.AO_CODE_TYPE, ");
		sb.append("        RPT.AO_CODE_TYPE_NAME, ");
		sb.append("        RPT.CUST_AO, ");
		sb.append("        RPT.TOTAL_CUST, ");
		sb.append("        RPT.TOTAL_PRD, ");
		sb.append("        RPT.TOTAL_PRD_TARGET, ");
		sb.append("        RPT.FOLD, ");
		sb.append("        RPT.ACTITITY_RATIO * 100 AS ACTITITY_RATIO, ");
		sb.append("        RPT.ACT_PRD1, ");
		sb.append("        RPT.ACT_PRD2, ");
		sb.append("        RPT.ACT_PRD3, ");
		sb.append("        RPT.ACT_PRD4, ");
		sb.append("        RPT.ACT_PRD5, ");
		sb.append("        RPT.ACT_PRD6, ");
		sb.append("        RPT.ACT_PRD7, ");
		sb.append("        RPT.ACT_PRD8, ");
		sb.append("        RPT.ACT_PRD9, ");
		sb.append("        RPT.USER_OLD, ");
		sb.append("        RPT.USER_YOUNG, ");
		sb.append("        RPT.USER_COMPANY, ");
		sb.append("        RPT.USER_OBU, ");
		sb.append("        RPT.USER_SPEC, ");
		sb.append("        RPT.USER_N_SPEC ");
		sb.append("FROM TBPMS_PRD_ACTIVITY_AO RPT ");

		sb.append("WHERE 1 = 1 ");
		
		//資料月份
		if (StringUtils.isNotBlank(inputVO.getDataMon())) {
			sb.append("AND RPT.YYYYMM = :dataMon ");
			queryCondition.setObject("dataMon", inputVO.getDataMon());
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0 ||
			StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) { 
			// 非任何uhrm相關人員 或 為031之兼任FC，需查詢畫面中組織條件
			
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("AND RPT.REGION_CENTER_ID = :regionCenterID ");
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			}

			// 營運區	
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("AND RPT.BRANCH_AREA_ID = :branchAreaID ");
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			}

			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("AND RPT.BRANCH_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			
			if (!isHeadMGR &&
				!StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
				// 非總人行員 或 非 為031之兼任FC，僅可視轄下
				sb.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = RPT.AO_EMP_ID) ");
			}
			
			if (isFC) {
				sb.append("AND RPT.AO_EMP_ID = :empID ");
				queryCondition.setObject("empID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			} 
		} else {
			sb.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = RPT.AO_EMP_ID) ");
		}
		
		sb.append("ORDER BY RPT.YYYYMM DESC, ");
		sb.append("         DECODE(REPLACE(REPLACE(REPLACE(RPT.REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
		sb.append("         RPT.REGION_CENTER_NAME, ");
		sb.append("         RPT.BRANCH_AREA_ID, ");
		sb.append("         RPT.BRANCH_AREA_NAME, ");
		sb.append("         RPT.BRANCH_NBR, ");
		sb.append("         RPT.BRANCH_NAME, ");
		sb.append("         RPT.AO_EMP_ID ");
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		return outputVO;
	}

	// 匯出
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {

		PMS355InputVO inputVO = (PMS355InputVO) body;

		List<Map<String, Object>> list = inputVO.getExportList();

		String fileName = inputVO.getDataMon() + "_客戶活躍度週報.xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("客戶活躍度週報");
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

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

		String[] headerLineTop = {"資料年月", "分行", "專員員編", "AO Code", "總客戶數", "總目標數", "活躍度", 
								  "各商品持有客戶數", "各商品持有客戶數", "各商品持有客戶數", "各商品持有客戶數", "各商品持有客戶數", "各商品持有客戶數", "各商品持有客戶數", "各商品持有客戶數", "各商品持有客戶數", 
								  "客戶類型", "客戶類型", "客戶類型", "客戶類型", "客戶類型"};
		
		String[] headerLineSec = {"", "", "", "", "", "", "", 
								  "(1) 存款", "(2) 保險", "(3) 基金", "(4) ETF/海外股票", "(5) DCI/SI/SN/海外債/金市債", "(6) 奈米投", "(7) 金錢信託(含有價證券信託)", "(8) 信用卡", "(9) 房貸/信貸", 
								  "age>=70", "特定客戶", "age<20", "法人戶/OBU", "一般戶"};
		
		String[] mainLine  = {"YYYYMM", "BRANCH_NBR", "AO_EMP_ID", "CUST_AO", "TOTAL_CUST", "TOTAL_PRD_TARGET", "ACTITITY_RATIO", 
							  "ACT_PRD1", "ACT_PRD2", "ACT_PRD3", "ACT_PRD4", "ACT_PRD5", "ACT_PRD6", "ACT_PRD7", "ACT_PRD8", "ACT_PRD9", 
							  "USER_OLD", "USER_SPEC", "USER_YOUNG", "USER_CO", "USER_N_SPEC"};
		
		Integer index = 0; // first row
		Integer startFlag = 0;
		Integer endFlag = 0;
		ArrayList<String> tempList = new ArrayList<String>(); //比對用
		
		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLineTop.length; i++) {
			String headerLine = headerLineTop[i];
			if (tempList.indexOf(headerLine) < 0) {
				tempList.add(headerLine);
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(headingStyle);
				cell.setCellValue(headerLineTop[i]);

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
		for (int i = 0; i < headerLineSec.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLineSec[i]);
			
			if ("".equals(headerLineSec[i])) {
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

		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);

			for (int i = 0; i < mainLine.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(mainStyle);

				switch (mainLine[i]) {
					case "BRANCH_NBR":
						cell.setCellValue((String) map.get(mainLine[i]) + "-" + map.get("BRANCH_NAME"));
						break;
					case "USER_CO":
						cell.setCellValue((new BigDecimal(map.get("USER_COMPANY").toString()).add(new BigDecimal(map.get("USER_OBU").toString()))).toString());
						break;
					case "ACTITITY_RATIO":
						cell.setCellValue(currencyFormat(map, mainLine[i]) + "%");
						break;
					case "TOTAL_PRD_TARGET":
						cell.setCellValue(currencyFormat(map, mainLine[i]));
						break;
					case "TOTAL_CUST":
					case "ACT_PRD1": 
					case "ACT_PRD2":
					case "ACT_PRD3":
					case "ACT_PRD4":
					case "ACT_PRD5":
					case "ACT_PRD6":
					case "ACT_PRD7":
					case "ACT_PRD8":
					case "ACT_PRD9": 
					case "USER_OLD":
					case "USER_SPEC":
					case "USER_YOUNG":
					case "USER_N_SPEC":
						cell.setCellValue(currencyPeople(map, mainLine[i]));
						break;
					default:
						cell.setCellValue(checkIsNull(map, mainLine[i]));
						break;
				}
			}

			index++;
		}

		workbook.write(new FileOutputStream(filePath));

		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		sendRtnObject(null);
	}

	// 空值檢查
	private String checkIsNull(Map map, String key) {

		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	// FORMAT
	private String currencyFormat(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
	
	// FORMAT
	private String currencyPeople(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0");
			return df.format(map.get(key));
		} else
			return "0";
	}
	
	//  找字串"\""功能 
	public String liesCheck(String lines) throws JBranchException {

		if (lines.indexOf("\"") != -1) {
			//抓出第一個"
			int acc = lines.indexOf("\"");
			
			//原本所有字串
			StringBuffer lines1 = new StringBuffer(lines.toString());
			
			//切掉前面的字串
			String oriline = lines.substring(0, acc).toString();
			String fist = lines1.substring(acc);
			fist = lines1.substring(acc).substring(1, fist.indexOf("\"", 1));
			String[] firstsub = fist.split(",");
			for (int i = 0; i < firstsub.length; i++) {
				oriline += firstsub[i].toString();
			}
			
			String oriline2 = lines.substring(acc).replaceFirst("\"" + fist + "\"", "");
			lines = oriline + oriline2;

		}
		if (lines.indexOf("\"") != -1) {
			liesCheck(lines);
		} else {
			finalstring = lines;
		}
		return lines;
	}
	
	// 下載範例
	public void downLoad(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PMS//PMS227_CUST_EXAMPLE.csv", "保險活躍客戶名單_上傳範例.csv");
		
		this.sendRtnObject(null);
	}
	
	// 計算保險活躍度客戶筆數
	public void countsInsCust(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS355InputVO inputVO = (PMS355InputVO) body;
		PMS355OutputVO outputVO = new PMS355OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT COUNT(*) AS COU, YYYYMM,　MAX(CREATETIME) AS CREATETIME ");
		sb.append("FROM TBPMS_ACTIVITY_INS_SG ");
		sb.append("WHERE YYYYMM = :YM   ");
		sb.append("GROUP BY YYYYMM HAVING COUNT(*) > 0");
		
		queryCondition.setObject("YM", inputVO.getsTime());

		queryCondition.setQueryString(sb.toString());

		outputVO.setCustList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);

	}

	// 從excel表格中新增數據 
	public void importCustData(Object body, IPrimitiveMap header) throws Exception {

		PMS355InputVO inputVO = (PMS355InputVO) body;
		PMS355OutputVO outputVO = new PMS355OutputVO();

		//JDBC
		GenFileTools gft = new GenFileTools();
		Connection conn = gft.getConnection();
		conn.setAutoCommit(false);
		
		PreparedStatement pstmtTruncate = conn.prepareStatement("TRUNCATE TABLE TBPMS_ACTIVITY_INS_SG");
		pstmtTruncate.execute();
		conn.commit();

		StringBuffer sb = new StringBuffer();

		sb.append("INSERT INTO TBPMS_ACTIVITY_INS_SG ( ");
		sb.append("  YYYYMM, ");
		sb.append("  CUST_ID, ");
		sb.append("  VERSION, ");
		sb.append("  CREATETIME, ");
		sb.append("  CREATOR, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE ");
		sb.append(") ");
		
		sb.append("VALUES ( ");
		sb.append("  ?, ");
		sb.append("  ?, ");
		sb.append("  0, ");
		sb.append("  SYSDATE, ");
		sb.append("  ?, ");
		sb.append("  ?, ");
		sb.append("  SYSDATE ");
		sb.append(") ");

		PreparedStatement pstmt = conn.prepareStatement(sb.toString());

		int flag = 0;

		Path path = Paths.get(new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString());
		List<String> lines = FileUtils.readLines(new File(path.toString()), "big5");

		String errMsg = "";

		try {
			for (int i = 1; i < lines.size(); i++) {
				liesCheck(lines.get(i).toString());
				String str_line = finalstring;
				String[] str = str_line.split(",");
				if (str.length < 2)
					throw new APException("第" + (i + 1) + "筆,欄位數小於兩筆!");

				flag++;

				if (StringUtils.equals("", errMsg)) {
					pstmt.setString(1, (str[0].equals("")) ? yeaMon : str[0]);
					pstmt.setString(2, (str[1].equals("") ? "0" : str[1]));
					pstmt.setString(3, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					pstmt.setString(4, (String) getUserVariable(FubonSystemVariableConsts.LOGINID));

					pstmt.addBatch();
					
					if (flag % 1000 == 0) {
						pstmt.executeBatch();
						conn.commit();
					}
				}
			}

			pstmt.executeBatch();
			conn.commit();

			outputVO.setFlag(flag);
			
		} catch (JBranchException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (Exception e2) {
				}
			
			e.printStackTrace();
			throw new APException("第" + (flag + 1) + "筆檔案的資料有誤!");
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
				}
		}
		
		if (!errMsg.equals(""))
			throw new APException(errMsg);
		else
			this.sendRtnObject(null);

	}
}