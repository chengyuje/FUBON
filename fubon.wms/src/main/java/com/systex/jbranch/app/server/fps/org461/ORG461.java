package com.systex.jbranch.app.server.fps.org461;

import java.io.FileOutputStream;
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
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org461")
@Scope("request")
public class ORG461 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfYYYYMMDD_ = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfSlash = new SimpleDateFormat("yyyy/MM/dd");
	
	public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		ORG461InputVO inputVO = (ORG461InputVO) body;
		ORG461OutputVO outputVO = new ORG461OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH CERT_BASE AS ( ");
		sb.append("  SELECT INFO.EMP_ID, ");
		sb.append("         CERT_LIST.PARAM_CODE AS CERTIFICATE_CODE, ");
		sb.append("         CERT_LIST.PARAM_DESC AS CERTIFICATE_TYPE, ");
		sb.append("         CASE WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NOT NULL AND (MC.UNREG_DATE IS NOT NULL AND TO_CHAR(MC.REG_DATE, 'yyyyMMdd') < TO_CHAR(MC.UNREG_DATE, 'yyyyMMdd')) THEN NULL ");
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NOT NULL THEN TO_CHAR(MC.REG_DATE, 'yyyy/MM/dd') ");
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL AND (MC_INS.UNREG_DATE IS NOT NULL AND TO_CHAR(MC_INS.REG_DATE, 'yyyyMMdd') < TO_CHAR(MC_INS.UNREG_DATE, 'yyyyMMdd')) THEN NULL ");
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'ESSENTIAL' AND MC.REG_DATE IS NULL THEN TO_CHAR(MC_INS.REG_DATE, 'yyyy/MM/dd') ");
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NOT NULL THEN TO_CHAR(MC.REG_DATE, 'yyyy/MM/dd') ");
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NOT NULL THEN TO_CHAR(MC.CERTIFICATE_GET_DATE, 'yyyy/MM/dd') ");
		sb.append("              WHEN CERT_LIST.PARAM_DESC = 'CHOOSE_ONE' AND MC.REG_DATE IS NULL AND MC.CERTIFICATE_GET_DATE IS NULL AND MC.APPLY_DATE IS NOT NULL THEN TO_CHAR(MC.APPLY_DATE, 'yyyy/MM/dd') ");
		sb.append("         ELSE NULL END AS REG_DATE ");
		sb.append("  FROM ( ");
		sb.append("    SELECT DISTINCT EMP_ID, PRIVILEGEID ");
		sb.append("    FROM VWORG_EMP_UHRM_INFO ");
		sb.append("    WHERE PRIVILEGEID = 'UHRM002' "); 
		sb.append("  ) INFO ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_TYPE ON CERT_TYPE.PARAM_TYPE LIKE 'ORG.CERT_%' AND REPLACE(INFO.PRIVILEGEID, 'UHRM', '') IN CERT_TYPE.PARAM_CODE ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_LIST ON CERT_TYPE.PARAM_TYPE||'_LIST' = CERT_LIST.PARAM_TYPE ");
		sb.append("  LEFT JOIN TBORG_MEMBER_CERT MC ON INFO.EMP_ID = MC.EMP_ID AND CERT_LIST.PARAM_CODE = MC.CERTIFICATE_CODE ");
		sb.append("  LEFT JOIN TBSYSPARAMETER CERT_MAPP ON CERT_MAPP.PARAM_TYPE = 'ORG.CERT_MAPPING' AND CERT_LIST.PARAM_CODE = CERT_MAPP.PARAM_CODE ");
		sb.append("  LEFT JOIN TBORG_MEMBER_CERT MC_INS ON INFO.EMP_ID = MC_INS.EMP_ID AND CERT_MAPP.PARAM_DESC = MC_INS.CERTIFICATE_CODE ");
		sb.append("  WHERE 1 = 1 ");
		sb.append(") ");
		
		sb.append("SELECT INFO.EMP_ID, ");
		sb.append("       INFO.EMP_NAME, ");
		sb.append("       INFO.UHRM_CODE AS AO_CODE, ");
		sb.append("       (SELECT TO_CHAR(MAX(AO.CREATETIME), 'yyyy-MM-dd') FROM TBORG_SALES_AOCODE AO WHERE INFO.UHRM_CODE = AO.AO_CODE AND INFO.EMP_ID = AO.EMP_ID ) AS CREATETIME, ");
		sb.append("       SUBSTR(MEM.JOB_TITLE_NAME, INSTR(MEM.JOB_TITLE_NAME, '(') + 1, LENGTH(MEM.JOB_TITLE_NAME) - INSTR(MEM.JOB_TITLE_NAME, '(') - 1) AS AO_JOB_RANK, ");
		sb.append("       TO_CHAR(MEM.PERF_EFF_DATE, 'yyyy-MM-dd') AS PERF_EFF_DATE, ");
		sb.append("       PABTH_BTPMS902_EMP_N.FC_GET_SH_EMPID(INFO.EMP_ID) AS SALES_SUP_EMP_ID, ");
		sb.append("       (SELECT EMP_NAME FROM TBORG_MEMBER WHERE EMP_ID = PABTH_BTPMS902_EMP_N.FC_GET_SH_EMPID(INFO.EMP_ID)) AS SALES_SUP_EMP_NAME, ");
		sb.append("       MEM.GROUP_TYPE, ");
		sb.append("       TO_CHAR(MEM.JOB_GOAL_DATE, 'yyyy-MM-dd') AS JOB_GOAL_DATE, ");
		sb.append("       RANK.PARAM_NAME AS JOB_RANK, ");
		sb.append("       MEM.JOB_TITLE_NAME, ");
		sb.append("       TO_CHAR(MEM.JOB_ONBOARD_DATE, 'yyyy-MM-dd') AS JOB_ONBOARD_DATE, ");
		sb.append("       TO_CHAR(MEM.ONBOARD_DATE, 'yyyy-MM-dd') AS ONBOARD_DATE, ");
		sb.append("       MEM.JOB_POSITION, ");
		sb.append("       'TODO' AS MARK_SITE, ");
		sb.append("       CERT0086, ");
		sb.append("       CERT0011, ");
		sb.append("       CERT0014, ");
		sb.append("       CERT0013, ");
		sb.append("       CERT0399, ");
		sb.append("       GREATEST_CERT_DATE ");
		sb.append("FROM ( ");
		sb.append("  SELECT DISTINCT EMP_ID, PRIVILEGEID, EMP_NAME, UHRM_CODE ");
		sb.append("  FROM VWORG_EMP_UHRM_INFO ");
		sb.append("  WHERE PRIVILEGEID = 'UHRM002' ");
		sb.append("  AND CODE_TYPE = '1'");
		sb.append(") INFO ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN TBSYSPARAMETER RANK ON MEM.JOB_RANK = RANK.PARAM_CODE AND RANK.PARAM_TYPE = 'ORG.JOB_RANK' ");
		sb.append("LEFT JOIN (	");
		sb.append("  SELECT * ");
		sb.append("  FROM ( ");
		sb.append("    SELECT EMP_ID, CERTIFICATE_CODE, REG_DATE ");
		sb.append("    FROM CERT_BASE ");
		sb.append("    WHERE CERTIFICATE_TYPE = 'ESSENTIAL' ");
		sb.append("    UNION ");
		sb.append("    SELECT EMP_ID, 'GREATEST_CERT_DATE' AS CERTIFICATE_CODE, MAX(REG_DATE) AS REG_DATE ");
		sb.append("    FROM CERT_BASE ");
		sb.append("    WHERE CERTIFICATE_TYPE = 'CHOOSE_ONE' ");
		sb.append("    GROUP BY EMP_ID, CERTIFICATE_TYPE ");
		sb.append("  ) PIVOT (MAX(REG_DATE) FOR CERTIFICATE_CODE IN ('Cert0086' AS CERT0086, 'Cert0011' AS CERT0011, 'Cert0014' AS CERT0014, 'Cert0013' AS CERT0013, 'Cert0399' AS CERT0399, 'GREATEST_CERT_DATE' AS GREATEST_CERT_DATE)) ");
		sb.append(") CERT ON MEM.EMP_ID = CERT.EMP_ID ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getAO_JOB_RANK())) {
			sb.append("AND SUBSTR(MEM.JOB_TITLE_NAME, INSTR(MEM.JOB_TITLE_NAME, '(') + 1, LENGTH(MEM.JOB_TITLE_NAME) - INSTR(MEM.JOB_TITLE_NAME, '(') - 1) = :tId ");
			condition.setObject("tId", inputVO.getAO_JOB_RANK());
		}
		
		if (StringUtils.isNotBlank(inputVO.getPERF_EFF_DATE())) {
			sb.append("AND TO_CHAR(MEM.PERF_EFF_DATE, 'yyyyMM') <= :pDate ");
			condition.setObject("pDate", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getPERF_EFF_DATE()))));
		}
		
		sb.append("ORDER BY SUBSTR(MEM.JOB_TITLE_NAME, INSTR(MEM.JOB_TITLE_NAME, '(') + 1, LENGTH(MEM.JOB_TITLE_NAME) - INSTR(MEM.JOB_TITLE_NAME, '(') - 1), INFO.EMP_ID ");

		condition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		for (Map<String, Object> map : list) {
			StringBuffer str = new StringBuffer();
			
			for (int i = 2; i <= 3; i++) {
				condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				str = new StringBuffer();
				
				str.append("SELECT EMP_ID, AO_CODE, REPLACE(LTRIM(TO_CHAR(NVL(TO_CHAR(ROUND(TO_NUMBER(SYSDATE-ACTIVE_DATE) / 30, 1)), 0), '990.9')), '.0', '') AS ACTIVE_DATE ");
				str.append("FROM TBORG_SALES_AOCODE "); 
				str.append("WHERE TYPE = :type ");
				str.append("AND EMP_ID = :empID ");
				
				condition.setObject("type", i);
				condition.setObject("empID", map.get("EMP_ID"));
				
				condition.setQueryString(str.toString());
				
				List<Map<String, Object>> codeList = dam.exeQuery(condition);
				StringBuffer aoCodeList  = new StringBuffer();
				StringBuffer actDateList = new StringBuffer();
				for(Map<String,Object> map2 : codeList){
					aoCodeList.append((String)map2.get("AO_CODE")).append("\n");
					actDateList.append((String)map2.get("ACTIVE_DATE")).append("\n");
				}
				map.put("CODE_LIST_" + i, aoCodeList.toString());
				map.put("ACTDATE_LIST_" + i, actDateList.toString());
			}
		}
		
		outputVO.setAoGoalLst(list);
		
		this.sendRtnObject(outputVO);
	}
	
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		ORG461InputVO inputVO = (ORG461InputVO) body;
		ORG461OutputVO outputVO = new ORG461OutputVO();
		
		String fileName = "個人高端業務人員每月掛Goal名單報表_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		List<Map<String,String>> reportList = inputVO.getEXPORT_LST();
		String[] headerLine = {"員編", "員工姓名", "職級", "職等", "職務", "職稱", "入行日", 
				               "CODE", "AOCODE建立時間", "副CODE", "維護CODE", "副CODE月份", 
				               "任該職務掛GOAL日", "任業務職掛GOAL日", "掛GOAL日", 
				               "信託業務員", "人身保險業務員", "投資型業務員", "外幣收付非投資型保險商品", "結構型商品", "六擇一證照"};
		
		String[] mainLine  = {"EMP_ID", "EMP_NAME", "AO_JOB_RANK", "JOB_RANK", "JOB_POSITION","JOB_TITLE_NAME", "ONBOARD_DATE",  
							  "AO_CODE", "CREATETIME", "CODE_LIST_2", "CODE_LIST_3", "ACTDATE_LIST_2", 
							  "JOB_GOAL_DATE", "JOB_ONBOARD_DATE", "PERF_EFF_DATE", 
							  "CERT0086", "CERT0011", "CERT0014", "CERT0013", "CERT0399", "GREATEST_CERT_DATE"};

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("個人高端業務人員每月掛Goal名單報表_" + sdfYYYYMMDD.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
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
		
		Integer index = 0; // first row
		
		// Heading
		XSSFRow row = sheet.createRow(index);
		
		row = sheet.createRow(index);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
		}
		
		index++;
		
		// 資料 CELL型式
		XSSFCellStyle mainStyle = wb.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		mainStyle.setWrapText(true);
		
		for (Map<String, String> map : reportList) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				if (StringUtils.equals("PERF_EFF_DATE", mainLine[j]) || StringUtils.equals("JOB_GOAL_DATE", mainLine[j])) {
					cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, mainLine[j])) ? sdfYYYYMMDD_.format(sdfYYYYMMDD_.parse(checkIsNull(map, mainLine[j]))) : null);
				} else if (StringUtils.equals("CERT0086", mainLine[j]) || 
						   StringUtils.equals("CERT0011",  mainLine[j]) ||
						   StringUtils.equals("CERT0013",  mainLine[j]) ||
						   StringUtils.equals("CERT0014",  mainLine[j]) ||
						   StringUtils.equals("CERT0399",  mainLine[j]) ||
						   StringUtils.equals("GREATEST_CERT_DATE",  mainLine[j]) ){
					cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, mainLine[j])) ? sdfSlash.format(sdfSlash.parse(checkIsNull(map, mainLine[j]))) : null);
				} else {
					String codeList = checkIsNull(map, mainLine[j]).replaceAll("\n|\r", "/");
					codeList = (codeList.indexOf("/") > -1) ? (codeList.substring(0, codeList.length() - 1)).replaceAll("/", "\n") : codeList;
					cell.setCellValue(codeList.length() > 0 ? codeList.substring(0, codeList.length()) : "");
				}
			}
			
			index++;
		}
		
		wb.write(new FileOutputStream(filePath));
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
