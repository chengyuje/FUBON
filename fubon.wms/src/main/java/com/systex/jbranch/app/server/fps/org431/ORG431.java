package com.systex.jbranch.app.server.fps.org431;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org431")
@Scope("request")
public class ORG431 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	SimpleDateFormat sdfYYYY 	  = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdfYYYYMM    = new SimpleDateFormat("yyyy/MM");
	SimpleDateFormat sdfYYYYMMDD  = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfYYYYMMDD2 = new SimpleDateFormat("yyyy/MM/dd");
	
	public void query(Object body, IPrimitiveMap header) throws Exception {
		
		ORG431InputVO inputVO = (ORG431InputVO) body;
		ORG431OutputVO outputVO = new ORG431OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT MQUOTA.ROLE_TYPE, ");
		sb.append("       NVL(BASE.GOAL_COUNT, 0) AS GOAL_COUNTS, ");
		sb.append("       NVL(BASE.ROLE_COUNT, 0) AS ACTUAL_COUNTS, ");
		sb.append("       NVL(MQUOTA.ROLE_QUOTA_COUNT, 0) AS QUOTA_COUNTS, ");
		sb.append("       CASE WHEN (NVL(MQUOTA.ROLE_QUOTA_COUNT, 0) - NVL(BASE.ROLE_COUNT, 0)) < 0 THEN 0 ELSE (NVL(MQUOTA.ROLE_QUOTA_COUNT, 0) - NVL(BASE.ROLE_COUNT, 0)) END AS SHORT_COUNTS ");
		sb.append("FROM ( ");
		sb.append("  SELECT REPLACE(ROLE_TYPE, '_CNT') AS ROLE_TYPE, ");
		sb.append("         ROLE_QUOTA_COUNT ");
		sb.append("  FROM TBORG_UHRM_MBR_QUOTA ");
		sb.append("  UNPIVOT (ROLE_QUOTA_COUNT FOR ROLE_TYPE IN (RM1_CNT, RM2_CNT, SRM_CNT)) ");
		sb.append(") MQUOTA ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT ROLE_TYPE, COUNT(ROLE_TYPE) AS ROLE_COUNT, COUNT(PERF_EFF_DATE) AS GOAL_COUNT ");
		sb.append("  FROM ( ");
		sb.append("    SELECT DISTINCT UHRM.EMP_ID, MEM.JOB_TITLE_NAME, ");
		sb.append("           SUBSTR(MEM.JOB_TITLE_NAME, INSTR(MEM.JOB_TITLE_NAME, '(') + 1, LENGTH(MEM.JOB_TITLE_NAME) - INSTR(MEM.JOB_TITLE_NAME, '(') - 1) AS ROLE_TYPE, ");
		sb.append("           MEM.PERF_EFF_DATE ");
		sb.append("    FROM VWORG_EMP_UHRM_INFO UHRM ");
		sb.append("    LEFT JOIN TBORG_MEMBER MEM ON UHRM.EMP_ID = MEM.EMP_ID ");
		sb.append("    WHERE UHRM.PRIVILEGEID = 'UHRM002' ");
		sb.append("  ) ");
		sb.append("  GROUP BY ROLE_TYPE ");
		sb.append(") BASE ON MQUOTA.ROLE_TYPE = BASE.ROLE_TYPE ");
		sb.append("ORDER BY MQUOTA.ROLE_TYPE ");

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setAoCntLst(dam.exeQuery(queryCondition));
		outputVO.setReportLst(dam.exeQuery(queryCondition));

		Calendar calender = Calendar.getInstance();
		outputVO.setYear(sdfYYYY.format(new Date()));
		outputVO.setToDay(sdfYYYYMMDD2.format(new Date()));
		outputVO.setNowMonth(sdfYYYYMM.format(new Date()));
		calender.setTime(new Date());
		calender.add(Calendar.MONTH, 1);
		outputVO.setNextMonth(sdfYYYYMM.format(calender.getTime()));
		calender.add(Calendar.MONTH, 1);
		outputVO.setNext2Month(sdfYYYYMM.format(calender.getTime()));

		sendRtnObject(outputVO);
	}
	
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		ORG431InputVO inputVO = (ORG431InputVO) body;
		ORG431OutputVO outputVO = new ORG431OutputVO();
		
		String[] headerLine1 = {"職務", inputVO.getNowMonth() + "掛Goal人數", 
								inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額"};
		
		String[] headerLine2 = {"", "", 
							  	"截至" + inputVO.getToDay() + "實際數", "員額", "缺額"}; 
		
		String[] mainLine    = {"ROLE_TYPE", "GOAL_COUNTS",
								"ACTUAL_COUNTS", "QUOTA_COUNTS", "SHORT_COUNTS"};
		
		String fileName = "個金高端RM員額表_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("個金高端RM員額表_" + sdfYYYYMMDD.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		
		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		
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
			
			if ("".equals(headerLine2[i])) {
				sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
			}
		}
		
		index++;

		List<Map<String, String>> list = inputVO.getEXPORT_LST();
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			if (list.get(i) != null) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(style);
					cell.setCellValue(list.get(i).get(mainLine[j]));
				}
			}
			
			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		// download
 		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
		outputVO.setAoCntLst(inputVO.getEXPORT_LST());
		
		sendRtnObject(outputVO);
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkIsNull (Map map, String key) {
		
		if (StringUtils.isNotBlank((String) map.get(key))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}
