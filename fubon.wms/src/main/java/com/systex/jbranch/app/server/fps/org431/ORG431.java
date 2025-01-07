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
		sb.append("WITH DTL AS ( ");
		sb.append("  SELECT CENTER_ID, ");
		sb.append("         CENTER_NAME, ");
		sb.append("         BRANCH_AREA_ID, ");
		sb.append("         BRANCH_AREA_NAME, ");
		sb.append("         ROLE_NAME, ");
		sb.append("         SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("         SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("         SRM_COUNT, ");
		sb.append("         SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("         NVL(SRM_COUNT, 0) - NVL(SUM(SERVING_COUNT), 0) AS ESTIMATED ");
		sb.append("  FROM ( ");
		sb.append("    SELECT CENTER_ID, ");
		sb.append("           CENTER_NAME, ");
		sb.append("           BRANCH_AREA_ID, ");
		sb.append("           BRANCH_AREA_NAME, ");
		sb.append("           ROLEID, ");
		sb.append("           ROLE_NAME, ");
		sb.append("           (SELECT COUNT(MEM.EMP_ID) ");
		sb.append("            FROM TBORG_MEMBER MEM ");
		sb.append("            LEFT JOIN TBORG_MEMBER_ROLE MROLE ON MEM.EMP_ID = MROLE.EMP_ID AND MROLE.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("             WHERE MROLE.ROLE_ID = BASE.ROLEID ");
		sb.append("            AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("            AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("            AND MEM.DEPT_ID = BASE.BRANCH_AREA_ID ");
		sb.append("            AND TO_CHAR(MEM.PERF_EFF_DATE, 'yyyyMM') <= TO_CHAR(SYSDATE, 'yyyyMM')) AS GOAL_COUNT, ");
		sb.append("           NVL(NOW_STATUS.SERVING, 0) AS SERVING_COUNT, ");
		sb.append("           NVL(MQUOTA.SRM_COUNT, 0) AS SRM_COUNT, "); 
		sb.append("           NVL(NOW_STATUS.LEAVING, 0) AS LEAVING_COUNT ");
		sb.append("    FROM ( ");
		sb.append("      SELECT REGION_CENTER_ID AS CENTER_ID, ");
		sb.append("             REGION_CENTER_NAME AS CENTER_NAME, ");
		sb.append("             BRANCH_AREA_ID, ");
		sb.append("             BRANCH_AREA_NAME, ");
		sb.append("             SYS_ROL.ROLEID, ");
		sb.append("             REPLACE(REPLACE(REPLACE(REPLACE(FUBON_ROL.JOB_TITLE_NAME, '私人銀行客戶經理', ''), '(', ''), ')', ''), '私銀', '') AS ROLE_NAME ");
		sb.append("      FROM TBSYSSECUROLPRIASS SYS_ROL ");
		sb.append("      LEFT JOIN TBORG_ROLE FUBON_ROL ON SYS_ROL.ROLEID = FUBON_ROL.ROLE_ID ");
		sb.append("      LEFT JOIN VWORG_DEFN_INFO DEPT_DTL ON DEPT_DTL.BRANCH_AREA_NAME LIKE '私銀%' ");
		sb.append("      WHERE PRIVILEGEID IN ('UHRM002') ");
		sb.append("    ) BASE ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT DEPT_ID, ");
		sb.append("             REPLACE(ROLE_TYPE, '_CNT') AS ROLE_TYPE, ");
		sb.append("             SRM_COUNT ");
		sb.append("      FROM TBORG_UHRM_MBR_QUOTA ");
		sb.append("      UNPIVOT (SRM_COUNT FOR ROLE_TYPE IN (SRM1_CNT, SRM2_CNT, SRM3_CNT)) ");
		sb.append("    ) MQUOTA ON BASE.ROLE_NAME = MQUOTA.ROLE_TYPE AND MQUOTA.DEPT_ID = BASE.BRANCH_AREA_ID ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT DEPT_ID, ROLE_ID, SUM(SERVING) AS SERVING, SUM(LEAVING) AS LEAVING ");
		sb.append("      FROM ( ");
		sb.append("        SELECT MEM.DEPT_ID, ");
		sb.append("               MEM.EMP_ID, ");
		sb.append("               MROLE.ROLE_ID, ");
		sb.append("               R.ROLE_NAME, ");
		sb.append("               CASE WHEN (MEM.JOB_RESIGN_DATE IS NULL OR TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') > TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS SERVING, ");
		sb.append("               CASE WHEN (TO_CHAR(MEM.JOB_RESIGN_DATE, 'yyyyMMdd') >= TO_CHAR(SYSDATE, 'yyyyMMdd')) THEN 1 ELSE 0 END AS LEAVING ");
		sb.append("        FROM TBORG_MEMBER MEM, TBORG_MEMBER_ROLE MROLE ");
		sb.append("        LEFT JOIN TBORG_ROLE R ON MROLE.ROLE_ID = R.ROLE_ID ");
		sb.append("        WHERE MEM.DEPT_ID IS NOT NULL ");
		sb.append("        AND MEM.EMP_ID = MROLE.EMP_ID ");
		sb.append("        AND MROLE.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("        AND MROLE.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('UHRM002')) ");
		sb.append("        AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("        AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
		sb.append("      ) ARRANGE ");
		sb.append("      GROUP BY DEPT_ID, ROLE_ID ");
		sb.append("    ) NOW_STATUS ON BASE.BRANCH_AREA_ID = NOW_STATUS.DEPT_ID AND BASE.ROLEID = NOW_STATUS.ROLE_ID ");
		sb.append("  ) ");
		sb.append("  GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, ROLE_NAME, SRM_COUNT ");
		sb.append(") ");
		
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME || ' 合計' AS CENTER_NAME, ");
		sb.append("       '' AS BRANCH_AREA_ID, ");
		sb.append("       '' AS BRANCH_AREA_NAME, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(SRM_COUNT) AS SRM_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(inputVO.getUhrmRC())) {
			sb.append("AND DTL.CENTER_ID = :CENTER_ID "); 
			queryCondition.setObject("CENTER_ID", inputVO.getUhrmRC());
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME || ' 合計', ROLE_NAME ");
		
		sb.append("UNION ");
		
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME || ' 合計' AS CENTER_NAME, ");
		sb.append("       '' AS BRANCH_AREA_ID, ");
		sb.append("       '' AS BRANCH_AREA_NAME, ");
		sb.append("       '' AS ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(SRM_COUNT) AS SRM_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(inputVO.getUhrmRC())) {
			sb.append("AND DTL.CENTER_ID = :CENTER_ID "); 
			queryCondition.setObject("CENTER_ID", inputVO.getUhrmRC());
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME || ' 合計' ");
		
		sb.append("UNION ");
		
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, ");
		sb.append("       BRANCH_AREA_NAME, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(SRM_COUNT) AS SRM_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(inputVO.getUhrmOP())) {
			sb.append("AND DTL.BRANCH_AREA_ID = :BRANCH_AREA_ID "); 
			queryCondition.setObject("BRANCH_AREA_ID", inputVO.getUhrmOP());
		} else if (StringUtils.isNotEmpty(inputVO.getUhrmRC())) {
			sb.append("AND DTL.CENTER_ID = :CENTER_ID "); 
			queryCondition.setObject("CENTER_ID", inputVO.getUhrmRC());
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, ROLE_NAME ");
		
		sb.append("UNION ");
		
		sb.append("SELECT CENTER_ID, ");
		sb.append("       CENTER_NAME, ");
		sb.append("       BRANCH_AREA_ID, ");
		sb.append("       BRANCH_AREA_NAME, ");
		sb.append("       '' AS ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(SRM_COUNT) AS SRM_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(inputVO.getUhrmOP())) {
			sb.append("AND DTL.BRANCH_AREA_ID = :BRANCH_AREA_ID "); 
			queryCondition.setObject("BRANCH_AREA_ID", inputVO.getUhrmOP());
		} else if (StringUtils.isNotEmpty(inputVO.getUhrmRC())) {
			sb.append("AND DTL.CENTER_ID = :CENTER_ID "); 
			queryCondition.setObject("CENTER_ID", inputVO.getUhrmRC());
		}
		
		sb.append("GROUP BY CENTER_ID, CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME ");
		
		sb.append("UNION ");
		
		sb.append("SELECT '' AS CENTER_ID, ");
		sb.append("       '全行 合計' AS CENTER_NAME, ");
		sb.append("       '' AS BRANCH_AREA_ID, ");
		sb.append("       '' AS BRANCH_AREA_NAME, ");
		sb.append("       ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(SRM_COUNT) AS SRM_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("GROUP BY '', '全行 合計', ROLE_NAME ");
		
		sb.append("UNION ");
		
		sb.append("SELECT '' AS CENTER_ID, ");
		sb.append("       '全行 合計' AS CENTER_NAME, ");
		sb.append("       '' AS BRANCH_AREA_ID, ");
		sb.append("       '' AS BRANCH_AREA_NAME, ");
		sb.append("       '' AS ROLE_NAME, ");
		sb.append("       SUM(GOAL_COUNT) AS GOAL_COUNT, ");
		sb.append("       SUM(SERVING_COUNT) AS SERVING_COUNT, ");
		sb.append("       SUM(SRM_COUNT) AS SRM_COUNT, ");
		sb.append("       SUM(LEAVING_COUNT) AS LEAVING_COUNT, ");
		sb.append("       SUM(ESTIMATED) AS ESTIMATED ");
		sb.append("FROM DTL ");
		sb.append("GROUP BY '', '全行 合計' ");
		
		sb.append("ORDER BY CENTER_ID ASC, BRANCH_AREA_ID ASC, ROLE_NAME ASC ");

		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);		
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
		for (Map<String, Object> centerMap : list) {
			String regionCenter = (String) centerMap.get("CENTER_NAME");
			if (centerTempList.indexOf(regionCenter) < 0) { //regionCenter
				centerTempList.add(regionCenter);
				
				Integer centerRowspan = 1;
				
				List<Map<String, Object>> branchAreaList = new ArrayList<Map<String,Object>>();
				ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用

				for (Map<String, Object> branchAreaMap : list) {
					String branchArea = (String) branchAreaMap.get("BRANCH_AREA_NAME");
					
					Integer branchAreaRowspan = 1;
					
					if (regionCenter.equals((String) branchAreaMap.get("CENTER_NAME"))) { 
						if (branchAreaTempList.indexOf(branchArea) < 0) { //branchArea
							branchAreaTempList.add(branchArea);
							
							List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
							ArrayList<String> roleTempList = new ArrayList<String>(); //比對用
								
							for (Map<String, Object> roleMap : list) {
								String role = (String) roleMap.get("ROLE_NAME");
								
								if (null != branchArea && roleMap.get("BRANCH_AREA_NAME") != null) {
									if (branchArea.equals((String) roleMap.get("BRANCH_AREA_NAME"))) {
										if (roleTempList.indexOf(role) < 0) { //branchArea
											roleTempList.add(role);
											
											Map<String, Object> roleTempMap = new HashMap<String, Object>();
											roleTempMap.put("ROLE_NAME", role);
											roleTempMap.put("GOAL_COUNT", (BigDecimal) roleMap.get("GOAL_COUNT"));
											roleTempMap.put("SERVING_COUNT", (BigDecimal) roleMap.get("SERVING_COUNT"));
											roleTempMap.put("SRM_COUNT", (BigDecimal) roleMap.get("SRM_COUNT"));
											roleTempMap.put("LEAVING_COUNT", (BigDecimal) roleMap.get("LEAVING_COUNT"));
											roleTempMap.put("ESTIMATED", (BigDecimal) roleMap.get("ESTIMATED"));
											
											roleList.add(roleTempMap);
										}
									}
								} else if (regionCenter.equals(roleMap.get("CENTER_NAME"))) {
									Map<String, Object> roleTempMap = new HashMap<String, Object>();
									roleTempMap.put("ROLE_NAME", role);
									roleTempMap.put("GOAL_COUNT", (BigDecimal) roleMap.get("GOAL_COUNT"));
									roleTempMap.put("SERVING_COUNT", (BigDecimal) roleMap.get("SERVING_COUNT"));
									roleTempMap.put("SRM_COUNT", (BigDecimal) roleMap.get("SRM_COUNT"));
									roleTempMap.put("LEAVING_COUNT", (BigDecimal) roleMap.get("LEAVING_COUNT"));
									roleTempMap.put("ESTIMATED", (BigDecimal) roleMap.get("ESTIMATED"));
									
									roleList.add(roleTempMap);
								}
							}
							
							Map<String, Object> branchAreaTempMap = new HashMap<String, Object>();
							branchAreaTempMap.put("BRANCH_AREA_NAME", branchArea);
							branchAreaTempMap.put("ROLE", roleList);
							centerRowspan = centerRowspan + roleList.size();
							branchAreaRowspan = branchAreaRowspan + roleList.size();
							branchAreaTempMap.put("ROWSPAN", branchAreaRowspan);
							branchAreaTempMap.put("COLSPAN", (roleList.size() == 1 ? 2 : 1));

							branchAreaList.add(branchAreaTempMap);
						} 
					}
				}
				
				Map<String, Object> centerTempMap = new HashMap<String, Object>();
				centerTempMap.put("CENTER_NAME", regionCenter);
				centerTempMap.put("BRANCH_AREA", branchAreaList);
				centerRowspan = centerRowspan + branchAreaList.size();
				centerTempMap.put("ROWSPAN", centerRowspan);
				centerTempMap.put("COLSPAN", (branchAreaList.size() == 1 && branchAreaList.get(0).get("BRANCH_AREA_NAME") == null ? 2 : 1));

				returnList.add(centerTempMap);
			}
		}
		
		outputVO.setAoCntLst(returnList);
		outputVO.setReportLst(list);

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
		
		String[] headerLine1 = {"業務處", "營運區", "職務", inputVO.getNowMonth() + "掛Goal人數", 
								inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額", inputVO.getYear() + "應有員額"};
		
		String[] headerLine2 = {"", "", "", "", 
							  	"截至" + inputVO.getToDay() + "實際數", "員額", "缺額"}; 
		
		String[] mainLine    = {"CENTER_NAME", "BRANCH_AREA_NAME", "ROLE_NAME", "GOAL_COUNT",
								"SERVING_COUNT", "SRM_COUNT", "ESTIMATED"};
		
		String fileName = "私銀理專員額表_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("私銀理專員額表_" + sdfYYYYMMDD.format(new Date()));
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

		ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
		Integer centerStartFlag = 0, branchAreaStartFlag = 0;
		Integer centerEndFlag = 0, branchAreaEndFlag = 0;
		
		Integer contectStartIndex = index;
		
		List<Map<String, String>> list = inputVO.getEXPORT_LST();
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				if (null != list.get(i)) {
					String centerName = list.get(i).get("CENTER_NAME");
					String branchAreaName = list.get(i).get("BRANCH_AREA_NAME");
					
					if (j == 0 && centerTempList.indexOf(centerName) < 0) {
						centerTempList.add(centerName);
						if (centerEndFlag != 0) {
							if (null != centerName && centerName.indexOf("合計") > 0) {
								if (StringUtils.equals("全行 合計", centerName)) {
									sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j + 1)); // firstRow, endRow, firstColumn, endColumn
								} else {
									sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
								}
							} else {
								sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j + 1)); // firstRow, endRow, firstColumn, endColumn
							}
						}
						
						centerStartFlag = i;
						centerEndFlag = 0;
					} else if (j == 0 && null != list.get(i).get("CENTER_NAME")) {
						centerEndFlag = i;
					}
					
					if (j == 1 && branchAreaTempList.indexOf(branchAreaName) < 0) {
						branchAreaTempList.add(branchAreaName);
						
						if (branchAreaEndFlag != 0) {
							if (null != branchAreaName && branchAreaName.indexOf("合計") > 0) {
								sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j + 1)); // firstRow, endRow, firstColumn, endColumn
							} else {
								sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							}
						}
						branchAreaStartFlag = i;
						branchAreaEndFlag = 0;
					} else if (j == 1 && null != list.get(i).get("BRANCH_AREA_NAME")) {
						branchAreaEndFlag = i;
					}
					
					if (j == (mainLine.length - 1) && i == (list.size() - 3) && null != centerName && StringUtils.equals("全行 合計", centerName)) {
						if (StringUtils.isEmpty(inputVO.getUhrmRC())) {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, 1, 1)); // firstRow, endRow, firstColumn, endColumn
						}
						sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex + 1, 0, 1)); // firstRow, endRow, firstColumn, endColumn
					} 
					
					if (null != list.get(i).get(mainLine[0]) && "ROLE_NAME".equals(mainLine[j]) && null == list.get(i).get(mainLine[j])) {
						cell.setCellValue("小計");
					} else {
						cell.setCellValue(list.get(i).get(mainLine[j]));
					}
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
