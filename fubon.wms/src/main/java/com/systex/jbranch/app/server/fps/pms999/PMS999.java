package com.systex.jbranch.app.server.fps.pms999;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms999")
@Scope("request")
public class PMS999 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD_D = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void query(Object body, IPrimitiveMap header) throws Exception {

		initUUID();

		PMS999InputVO inputVO = (PMS999InputVO) body;
		PMS999OutputVO outputVO = new PMS999OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT REPORT_DATE, ");
		sb.append("       REGION_CENTER_ID, ");
		sb.append("       REGION_CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, ");
		sb.append("       BRANCH_AREA_NAME, ");
		sb.append("       BRANCH_NBR, ");
		sb.append("       BRANCH_NAME, ");
		sb.append("       REPORT_TYPE, ");
		sb.append("       COUNTS AS NOT_RESPONDING ");
		sb.append("FROM VWPMS_INTERNAL_CONTROL_INFO RPT ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND RPT.STATUS = 'NOT_RESPONDING' ");

		// 資料統計日期-起
		if (inputVO.getsDate() != null) {
			sb.append("AND TRUNC(RPT.REPORT_DATE) >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}

		// 資料統計日期-迄
		if (inputVO.geteDate() != null) {
			sb.append("AND TRUNC(RPT.REPORT_DATE) <= TRUNC(:end) ");
			queryCondition.setObject("end", inputVO.geteDate());
		}

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

		// 報表名稱
		if (StringUtils.isNotBlank(inputVO.getReportToTable())) {
			sb.append("AND RPT.REPORT_TYPE = :reportToTable ");
			queryCondition.setObject("reportToTable", inputVO.getReportToTable());
		}

		sb.append("ORDER BY REPORT_DATE DESC, ");
		sb.append("         DECODE(REPLACE(REPLACE(REPLACE(REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
		sb.append("         REGION_CENTER_NAME, ");
		sb.append("         BRANCH_AREA_ID, ");
		sb.append("         BRANCH_AREA_NAME, ");
		sb.append("         BRANCH_NBR, ");
		sb.append("         BRANCH_NAME, ");
		sb.append("         REPORT_TYPE ");

		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}

	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {

		XmlInfo xmlInfo = new XmlInfo();

		PMS999InputVO inputVO = (PMS999InputVO) body;

		List<Map<String, Object>> list = inputVO.getExportList();

		String fileName = "內控報表尚未回覆統計表_" + sdfYYYYMMDD.format(inputVO.getsDate()) + "-" + sdfYYYYMMDD.format(inputVO.geteDate()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("內控報表尚未回覆統計表");
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

		String[] headerLine = { "資料日期", "業務處", "分行代號", "分行名稱", "報表名稱", "筆數" };
		String[] mainLine = { "REPORT_DATE", "REGION_CENTER_NAME", "BRANCH_NBR", "BRANCH_NAME", "REPORT_TYPE", "NOT_RESPONDING" };

		Integer index = 0;

		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
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
					case "REPORT_DATE":
						cell.setCellValue(sdfYYYYMMDD_D.format(sdfYYYYMMDD_D.parse((String) map.get(mainLine[i]))));
						break;
					case "REPORT_TYPE":
						cell.setCellValue(xmlInfo.getVariable("PMS.REPORT_TO_TABLE", checkIsNull(map, mainLine[i]), "F3"));
						break;
					case "NOT_RESPONDING":
						cell.setCellValue((getBigDecimal(map.get(mainLine[i]))).doubleValue());
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

	//轉Decimal
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
}