package com.systex.jbranch.app.server.fps.pms111;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms111")
@Scope("request")
public class PMS111 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	// 取得名單建立年月-起
	public void getSDateList (Object body, IPrimitiveMap header) throws Exception {
		
		PMS111OutputVO outputVO = new PMS111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TO_CHAR(ADD_MONTHS(TO_DATE('201701', 'yyyyMM'), ROWNUM - 1), 'yyyyMM') AS LABEL, ");
		sb.append("       TO_CHAR(ADD_MONTHS(TO_DATE('201701', 'yyyyMM'), ROWNUM - 1), 'yyyyMM') AS DATA ");
		sb.append("FROM DUAL ");
		sb.append("CONNECT BY ROWNUM <= (SELECT CEIL(MONTHS_BETWEEN(SYSDATE, TO_DATE('201701', 'yyyyMM'))) FROM DUAL) ");
		sb.append("ORDER BY DATA DESC ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setsDateList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	public void getEDateList (Object body, IPrimitiveMap header) throws JBranchException {
		
		this.sendRtnObject(this.getEDateList(body));
	}
	
	// 取得名單建立年月-迄
	public PMS111OutputVO getEDateList (Object body)  throws JBranchException {
		
		PMS111InputVO inputVO = (PMS111InputVO) body;
		PMS111OutputVO outputVO = new PMS111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		BigDecimal monInterval;
		
		if (inputVO.getMonInterval().compareTo(BigDecimal.ZERO) == 0) {
			monInterval = new BigDecimal(12);
		} else {
			monInterval = inputVO.getMonInterval();
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TO_CHAR(ADD_MONTHS(TO_DATE(:sDate, 'yyyyMM'), ROWNUM - 1), 'yyyyMM') AS LABEL, ");
		sb.append("       TO_CHAR(ADD_MONTHS(TO_DATE(:sDate, 'yyyyMM'), ROWNUM - 1), 'yyyyMM') AS DATA ");
		sb.append("FROM DUAL ");
		sb.append("CONNECT BY ROWNUM <= (");
		sb.append("  SELECT CASE WHEN CEIL(MONTHS_BETWEEN(SYSDATE, TO_DATE(:sDate, 'yyyyMM'))) > :monInterval THEN :monInterval ");
		sb.append("         ELSE CEIL(MONTHS_BETWEEN(SYSDATE, TO_DATE(:sDate, 'yyyyMM'))) ");
		sb.append("         END AS COUNTS ");
		sb.append("  FROM DUAL");
		sb.append(") ");
		sb.append("ORDER BY DATA DESC ");
		
		queryCondition.setObject("sDate", inputVO.getsDate());
		queryCondition.setObject("monInterval", monInterval);

		queryCondition.setQueryString(sb.toString());
		
		outputVO.seteDateList(dam.exeQuery(queryCondition));
		
		return(outputVO);
	}
	
	// 留資名單未聯繫明細表 - 查詢
	public void getNoContentDtl (Object body, IPrimitiveMap header) throws JBranchException {
	
		PMS111InputVO inputVO = (PMS111InputVO) body;
		PMS111OutputVO outputVO = new PMS111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 防呆
		if (StringUtils.isBlank(inputVO.geteDate())) {
			// 若前端沒有選擇起日，預設為系統日(年月)
			if (StringUtils.isBlank(inputVO.getsDate())) {
				inputVO.setsDate(new SimpleDateFormat("yyyyMM").format(new Date()));
			}
			
			// 以起日查LIST取得第一筆(最大值) - 起迄期間最長一年
			PMS111OutputVO outputVO_temp = getEDateList(inputVO);
			if (outputVO_temp.geteDateList().size() > 0) 
				inputVO.seteDate((String) outputVO_temp.geteDateList().get(0).get("DATA"));
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT CAMP.CAMPAIGN_NAME, ");
		sb.append("         TO_CHAR(LEADS.CREATETIME, 'yyyyMMdd') AS CREATETIME, ");
		sb.append("         CAMP.LEAD_TYPE, ");
		sb.append("         PARAM.PARAM_NAME AS LEAD_TYPE_NAME, ");
		sb.append("         DEFN.REGION_CENTER_ID, ");
		sb.append("         DEFN.REGION_CENTER_NAME, ");
		sb.append("         DEFN.BRANCH_AREA_ID, ");
		sb.append("         DEFN.BRANCH_AREA_NAME, ");
		sb.append("         DEFN.BRANCH_NBR, ");
		sb.append("         DEFN.BRANCH_NAME, ");
		sb.append("         LEADS.SFA_LEAD_ID, ");
		sb.append("         LEADS.CUST_ID, ");
		sb.append("         LEADS.EMP_ID, ");
		sb.append("         LEADS.LEAD_STATUS, ");
		sb.append("         CP.RESPONSE_NAME, ");
		sb.append("         CASE WHEN LEADS.EMP_ID IS NOT NULL THEN LEADS.EMP_ID ELSE LEADS.MODIFIER END AS EXEC_EMP_ID ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("  INNER JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON LEADS.BRANCH_ID = DEFN.BRANCH_NBR ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_TYPE = 'CAM.LEAD_TYPE' AND PARAM_CODE = CAMP.LEAD_TYPE ");
		sb.append("  LEFT JOIN TBCAM_SFA_CAMP_RESPONSE CP ON CAMP.LEAD_RESPONSE_CODE = CP.CAMPAIGN_ID AND LEADS.LEAD_STATUS = CP.LEAD_STATUS ");
		sb.append("  WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') "); 
		sb.append("  AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("  AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("  AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");
		sb.append("  AND LEADS.LEAD_STATUS = '01' ");
		sb.append(") ");
		sb.append(", CUST_DTL AS ( ");
		sb.append("  SELECT SFA_LEAD_ID, CUST_NAME, CUST_ID, NOTE ");
		sb.append("  FROM ( ");
		sb.append("    SELECT FL.SFA_LEAD_ID, ");
		sb.append("           CASE WHEN FL.RESULT LIKE '%客戶姓名%' OR FL.RESULT LIKE '%姓氏%' THEN 'CUST_NAME' ");
		sb.append("                WHEN FL.RESULT LIKE '%客戶ID%' THEN 'CUST_ID' ");
		sb.append("                WHEN FL.RESULT LIKE '%留言內容%' OR FL.RESULT LIKE '%備註%' THEN 'NOTE' ");
		sb.append("           ELSE NULL END AS VAR_FIELD_TYPE, FV.RESULT AS LABEL_VALUE ");
		sb.append("    FROM ( ");
		sb.append("      SELECT SFA_LEAD_ID, VAR_FIELD_LABEL AS VAR_FIELD_TYPE, RESULT ");
		sb.append("      FROM (SELECT * FROM TBCAM_SFA_LEADS_VAR LV WHERE EXISTS (SELECT 1 FROM BASE WHERE BASE.SFA_LEAD_ID = LV.SFA_LEAD_ID)) ");
		sb.append("      UNPIVOT (RESULT FOR VAR_FIELD_LABEL IN (VAR_FIELD_LABEL1, VAR_FIELD_LABEL2, VAR_FIELD_LABEL3, VAR_FIELD_LABEL4, VAR_FIELD_LABEL5, VAR_FIELD_LABEL6, VAR_FIELD_LABEL7, VAR_FIELD_LABEL8, VAR_FIELD_LABEL9, VAR_FIELD_LABEL10, VAR_FIELD_LABEL11, VAR_FIELD_LABEL12, VAR_FIELD_LABEL13, VAR_FIELD_LABEL14, VAR_FIELD_LABEL15, VAR_FIELD_LABEL16, VAR_FIELD_LABEL17, VAR_FIELD_LABEL18, VAR_FIELD_LABEL19, VAR_FIELD_LABEL20)) ");
		sb.append("    ) FL ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT SFA_LEAD_ID, REPLACE(VAR_FIELD_VALUE, 'VALUE', 'LABEL') AS VAR_FIELD_TYPE, RESULT ");
		sb.append("      FROM (SELECT * FROM TBCAM_SFA_LEADS_VAR LV WHERE EXISTS (SELECT 1 FROM BASE WHERE BASE.SFA_LEAD_ID = LV.SFA_LEAD_ID)) ");
		sb.append("      UNPIVOT (RESULT FOR VAR_FIELD_VALUE IN (VAR_FIELD_VALUE1, VAR_FIELD_VALUE2, VAR_FIELD_VALUE3, VAR_FIELD_VALUE4, VAR_FIELD_VALUE5, VAR_FIELD_VALUE6, VAR_FIELD_VALUE7, VAR_FIELD_VALUE8, VAR_FIELD_VALUE9, VAR_FIELD_VALUE10, VAR_FIELD_VALUE11, VAR_FIELD_VALUE12, VAR_FIELD_VALUE13, VAR_FIELD_VALUE14, VAR_FIELD_VALUE15, VAR_FIELD_VALUE16, VAR_FIELD_VALUE17, VAR_FIELD_VALUE18, VAR_FIELD_VALUE19, VAR_FIELD_VALUE20)) ");
		sb.append("    ) FV ON FL.SFA_LEAD_ID = FV.SFA_LEAD_ID AND FL.VAR_FIELD_TYPE = FV.VAR_FIELD_TYPE ");
		sb.append("    WHERE CASE WHEN FL.RESULT LIKE '%客戶姓名%' OR FL.RESULT LIKE '%姓氏%' THEN 'CUST_NAME' ");
		sb.append("               WHEN FL.RESULT LIKE '%客戶ID%' THEN 'CUST_ID' ");
		sb.append("               WHEN FL.RESULT LIKE '%留言內容%' OR FL.RESULT LIKE '%備註%' THEN 'NOTE' ");
		sb.append("          ELSE NULL END IS NOT NULL ");
		sb.append("  ) ");
		sb.append("  PIVOT (MAX(LABEL_VALUE) FOR VAR_FIELD_TYPE IN ('CUST_NAME' AS CUST_NAME, 'CUST_ID' AS CUST_ID, 'NOTE' AS NOTE)) ");
		sb.append(") ");
		sb.append("SELECT BASE.CAMPAIGN_NAME, ");
		sb.append("       BASE.CREATETIME, ");
		sb.append("       BASE.LEAD_TYPE, ");
		sb.append("       BASE.LEAD_TYPE_NAME, ");
		sb.append("       BASE.REGION_CENTER_ID, ");
		sb.append("       BASE.REGION_CENTER_NAME, ");
		sb.append("       BASE.BRANCH_AREA_ID, ");
		sb.append("       BASE.BRANCH_AREA_NAME, ");
		sb.append("       BASE.BRANCH_NBR, ");
		sb.append("       BASE.BRANCH_NAME, ");
		sb.append("       CASE WHEN BASE.LEAD_TYPE = 'H1' AND CD.CUST_ID IS NOT NULL THEN CD.CUST_ID ELSE BASE.CUST_ID END AS CUST_ID, ");
		sb.append("       CD.CUST_NAME, ");
		sb.append("       AO.EMP_ID, ");
		sb.append("       BASE.LEAD_STATUS, ");
		sb.append("       BASE.RESPONSE_NAME, ");
		sb.append("       BASE.EXEC_EMP_ID, ");
		sb.append("       EXEC_M.EMP_NAME AS EXEC_EMP_NAME ");
		sb.append("FROM BASE ");
		sb.append("LEFT JOIN CUST_DTL CD ON BASE.SFA_LEAD_ID = CD.SFA_LEAD_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER EXEC_M ON BASE.EXEC_EMP_ID = EXEC_M.EMP_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CM ON CASE WHEN BASE.LEAD_TYPE = 'H1' AND CD.CUST_ID IS NOT NULL THEN CD.CUST_ID ELSE BASE.CUST_ID END = CM.CUST_ID ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CM.AO_CODE = AO.AO_CODE ");
		sb.append("ORDER BY BASE.LEAD_TYPE, BASE.CREATETIME, BASE.REGION_CENTER_ID ASC, BASE.BRANCH_AREA_ID ASC, BASE.BRANCH_NBR ASC ");

		queryCondition.setObject("sDATE", inputVO.getsDate());
		queryCondition.setObject("eDATE", inputVO.geteDate());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setNoContentDtlList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	// 留資名單未聯繫明細表 - 匯出
	public void expNoContentDtl (Object body, IPrimitiveMap header) throws Exception {
		
		PMS111InputVO inputVO = (PMS111InputVO) body;
		PMS111OutputVO outputVO = new PMS111OutputVO();
		
		String[] headerLine1 = {"名單種類", "名單日期(名單建立日期)", "業務處", "營運區", "歸屬行", 
							    "客戶ID", "客戶姓名", "指定員編", "名單狀態", "名單維護者"};
		
		String[] mainLine    = {"LEAD_TYPE_NAME", "CREATETIME", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME",
								"CUST_ID", "CUST_NAME", "EMP_ID", "RESPONSE_NAME", "EXEC_EMP_NAME"}; 
		
		String fileName = "留資名單未聯繫明細表_" + inputVO.getsDate() + "-" + inputVO.geteDate() + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("留資名單未聯繫明細表");
		sheet.setDefaultColumnWidth(20);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);
		
		XSSFCellStyle qryHeadStyle = workbook.createCellStyle();
		qryHeadStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		qryHeadStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		qryHeadStyle.setFillForegroundColor(HSSFColor.YELLOW.index);// 填滿顏色
		qryHeadStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		qryHeadStyle.setBorderBottom((short) 1);
		qryHeadStyle.setBorderTop((short) 1);
		qryHeadStyle.setBorderLeft((short) 1);
		qryHeadStyle.setBorderRight((short) 1);
		
		Integer index = 0; // first row
		
		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine1.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(qryHeadStyle);
			cell.setCellValue(headerLine1[i]);
		}
		
		index++;
		
		List<Map<String, String>> list = inputVO.getRep_noContentDtlList();
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				
				if (StringUtils.equals(mainLine[j], "BRANCH_NAME")) {
					cell.setCellValue(list.get(i).get("BRANCH_NBR") + "-" + list.get(i).get(mainLine[j]));
				} else {
					cell.setCellValue(list.get(i).get(mainLine[j]));
				}
			}
			
			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		// download
 		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
 		sendRtnObject(outputVO);
	}
	
	// 留資名單執行統計表 - 查詢
	public void getExecStatistics (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS111InputVO inputVO = (PMS111InputVO) body;
		PMS111OutputVO outputVO = new PMS111OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 防呆
		if (StringUtils.isBlank(inputVO.geteDate())) {
			// 若前端沒有選擇起日，預設為系統日(年月)
			if (StringUtils.isBlank(inputVO.getsDate())) {
				inputVO.setsDate(new SimpleDateFormat("yyyyMM").format(new Date()));
			}
			
			// 以起日查LIST取得第一筆(最大值) - 起迄期間最長一年
			PMS111OutputVO outputVO_temp = getEDateList(inputVO);
			if (outputVO_temp.geteDateList().size() > 0) 
				inputVO.seteDate((String) outputVO_temp.geteDateList().get(0).get("DATA"));
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH DTL AS ( ");
		sb.append("  SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_AREA_GROUP, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP, ");
		sb.append("         L05_NO_CONTENT,  L05_CONTENT,  L05_ALL, ");
		sb.append("         L06_NO_CONTENT,  L06_CONTENT,  L06_ALL, ");
		sb.append("         LH1_NO_CONTENT,  LH1_CONTENT,  LH1_ALL, ");
		sb.append("         LUX_NO_CONTENT,  LUX_CONTENT,  LUX_ALL, ");
		sb.append("         LALL_NO_CONTENT, LALL_CONTENT, LALL_ALL ");
		sb.append("  FROM ( ");
		sb.append("    SELECT DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, DEFN.BRANCH_AREA_GROUP, DEFN.BRANCH_NBR, DEFN.BRANCH_NAME, DEFN.BRANCH_GROUP, ");
		sb.append("                    LT.LEAD_TYPE || LC.CODI_TYPE AS ORDER_TYPE, ");
		sb.append("           NVL(COD.COUNTS, 0) AS COUNTS ");
		sb.append("    FROM VWORG_DEFN_INFO DEFN ");
		sb.append("    LEFT JOIN (SELECT PARAM_CODE AS LEAD_TYPE, PARAM_NAME AS LEAD_TYPE_NAME, PARAM_ORDER AS LEAD_ORDER FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.PIPE_LEAD_TYPE') LT ON 1 = 1 ");
		sb.append("    LEFT JOIN (SELECT PARAM_CODE AS CODI_TYPE, PARAM_NAME AS CODI_NAME, PARAM_ORDER AS CODI_ORDER FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.PIPE_LEAD_CODI') LC ON 1 = 1 ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT CAMP.LEAD_TYPE, LEADS.BRANCH_ID, CASE WHEN LEADS.LEAD_STATUS = '01' THEN '=01' WHEN LEADS.LEAD_STATUS > '01' THEN '>01' ELSE '99' END AS CODI_TYPE, NVL(COUNT(1), 0) AS COUNTS ");
		sb.append("      FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("      LEFT JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("      WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') "); 
		sb.append("      AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");
		sb.append("      GROUP BY CAMP.LEAD_TYPE, LEADS.BRANCH_ID, CASE WHEN LEADS.LEAD_STATUS = '01' THEN '=01' WHEN LEADS.LEAD_STATUS > '01' THEN '>01' ELSE '99' END ");
		sb.append("      UNION ");
		sb.append("      SELECT CAMP.LEAD_TYPE, LEADS.BRANCH_ID, 'ALL' AS CODI_TYPE, COUNT(1)  ");
		sb.append("      FROM TBCAM_SFA_CAMPAIGN CAMP  ");
		sb.append("      LEFT JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("      WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') "); 
		sb.append("      AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");
		sb.append("      GROUP BY CAMP.LEAD_TYPE, LEADS.BRANCH_ID ");
		sb.append("      UNION ");
		sb.append("      SELECT 'ALL' AS LEAD_TYPE, LEADS.BRANCH_ID, CASE WHEN LEADS.LEAD_STATUS = '01' THEN '=01' WHEN LEADS.LEAD_STATUS > '01' THEN '>01' ELSE '99' END AS CODI_TYPE, NVL(COUNT(1), 0) AS COUNTS ");
		sb.append("      FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("      LEFT JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("      WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') "); 
		sb.append("      AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");
		sb.append("      GROUP BY 'ALL', LEADS.BRANCH_ID, CASE WHEN LEADS.LEAD_STATUS = '01' THEN '=01' WHEN LEADS.LEAD_STATUS > '01' THEN '>01' ELSE '99' END ");
		sb.append("      UNION ");
		sb.append("      SELECT 'ALL' AS LEAD_TYPE, LEADS.BRANCH_ID, 'ALL' AS CODI_TYPE, COUNT(1) ");
		sb.append("      FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("      LEFT JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("      WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') "); 
		sb.append("      AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("      AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");
		sb.append("      GROUP BY 'ALL', LEADS.BRANCH_ID ");
		sb.append("    ) COD ON DEFN.BRANCH_NBR = COD.BRANCH_ID AND LT.LEAD_TYPE = COD.LEAD_TYPE AND LC.CODI_TYPE = COD.CODI_TYPE ");
		sb.append("  ) ");
		sb.append("  PIVOT (SUM(COUNTS) FOR ORDER_TYPE IN ('05=01'  AS L05_NO_CONTENT,  '05>01'  AS L05_CONTENT,  '05ALL'  AS L05_ALL, ");
		sb.append("                                        '06=01'  AS L06_NO_CONTENT,  '06>01'  AS L06_CONTENT,  '06ALL'  AS L06_ALL, ");
		sb.append("                                        'H1=01'  AS LH1_NO_CONTENT,  'H1>01'  AS LH1_CONTENT,  'H1ALL'  AS LH1_ALL, ");
		sb.append("                                        'UX=01'  AS LUX_NO_CONTENT,  'UX>01'  AS LUX_CONTENT,  'UXALL'  AS LUX_ALL, ");
		sb.append("                                        'ALL=01' AS LALL_NO_CONTENT, 'ALL>01' AS LALL_CONTENT, 'ALLALL' AS LALL_ALL)) ");
		sb.append(") ");

		// 處 合計
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME || ' 合計' AS REGION_CENTER_NAME, '' AS BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS AREA_GROUP, ");
		sb.append("       SUM(L05_NO_CONTENT) AS L05_NO_CONTENT,  SUM(L05_CONTENT) AS L05_CONTENT,  SUM(L05_ALL) AS L05_ALL, ");
		sb.append("       SUM(L06_NO_CONTENT) AS L06_NO_CONTENT,  SUM(L06_CONTENT) AS L06_CONTENT,  SUM(L06_ALL) AS L06_ALL, ");
		sb.append("       SUM(LH1_NO_CONTENT) AS LH1_NO_CONTENT,  SUM(LH1_CONTENT) AS LH1_CONTENT,  SUM(LH1_ALL) AS LH1_ALL, ");
		sb.append("       SUM(LUX_NO_CONTENT) AS LUX_NO_CONTENT,  SUM(LUX_CONTENT) AS LUX_CONTENT,  SUM(LUX_ALL) AS LUX_ALL, ");
		sb.append("       SUM(LALL_NO_CONTENT) AS LALL_NO_CONTENT, SUM(LALL_CONTENT) AS LALL_CONTENT, SUM(LALL_ALL) AS LALL_ALL ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME || ' 合計' ");
		
		sb.append("UNION ");
		
		// 區 合計
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, BRANCH_AREA_GROUP AS AREA_GROUP, ");
		sb.append("       SUM(L05_NO_CONTENT) AS L05_NO_CONTENT,  SUM(L05_CONTENT) AS L05_CONTENT,  SUM(L05_ALL) AS L05_ALL, ");
		sb.append("       SUM(L06_NO_CONTENT) AS L06_NO_CONTENT,  SUM(L06_CONTENT) AS L06_CONTENT,  SUM(L06_ALL) AS L06_ALL, ");
		sb.append("       SUM(LH1_NO_CONTENT) AS LH1_NO_CONTENT,  SUM(LH1_CONTENT) AS LH1_CONTENT,  SUM(LH1_ALL) AS LH1_ALL, ");
		sb.append("       SUM(LUX_NO_CONTENT) AS LUX_NO_CONTENT,  SUM(LUX_CONTENT) AS LUX_CONTENT,  SUM(LUX_ALL) AS LUX_ALL, ");
		sb.append("       SUM(LALL_NO_CONTENT) AS LALL_NO_CONTENT, SUM(LALL_CONTENT) AS LALL_CONTENT, SUM(LALL_ALL) AS LALL_ALL ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME || ' 合計', BRANCH_AREA_GROUP ");
		
		sb.append("UNION ");
		
		// 分行
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP AS AREA_GROUP, ");
		sb.append("       SUM(L05_NO_CONTENT) AS L05_NO_CONTENT,  SUM(L05_CONTENT) AS L05_CONTENT,  SUM(L05_ALL) AS L05_ALL, ");
		sb.append("       SUM(L06_NO_CONTENT) AS L06_NO_CONTENT,  SUM(L06_CONTENT) AS L06_CONTENT,  SUM(L06_ALL) AS L06_ALL, ");
		sb.append("       SUM(LH1_NO_CONTENT) AS LH1_NO_CONTENT,  SUM(LH1_CONTENT) AS LH1_CONTENT,  SUM(LH1_ALL) AS LH1_ALL, ");
		sb.append("       SUM(LUX_NO_CONTENT) AS LUX_NO_CONTENT,  SUM(LUX_CONTENT) AS LUX_CONTENT,  SUM(LUX_ALL) AS LUX_ALL, ");
		sb.append("       SUM(LALL_NO_CONTENT) AS LALL_NO_CONTENT, SUM(LALL_CONTENT) AS LALL_CONTENT, SUM(LALL_ALL) AS LALL_ALL ");
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY  REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, BRANCH_GROUP ");
		
		sb.append("UNION ");
		
		// 全行 合計
		sb.append("SELECT '' AS REGION_CENTER_ID, '全行 合計' AS REGION_CENTER_NAME, '' AS  BRANCH_AREA_ID, '' AS BRANCH_AREA_NAME, '' AS BRANCH_NBR, '' AS BRANCH_NAME, '' AS AREA_GROUP,  ");
		sb.append("       SUM(L05_NO_CONTENT) AS L05_NO_CONTENT,  SUM(L05_CONTENT) AS L05_CONTENT,  SUM(L05_ALL) AS L05_ALL, ");
		sb.append("       SUM(L06_NO_CONTENT) AS L06_NO_CONTENT,  SUM(L06_CONTENT) AS L06_CONTENT,  SUM(L06_ALL) AS L06_ALL, ");
		sb.append("       SUM(LH1_NO_CONTENT) AS LH1_NO_CONTENT,  SUM(LH1_CONTENT) AS LH1_CONTENT,  SUM(LH1_ALL) AS LH1_ALL, ");
		sb.append("       SUM(LUX_NO_CONTENT) AS LUX_NO_CONTENT,  SUM(LUX_CONTENT) AS LUX_CONTENT,  SUM(LUX_ALL) AS LUX_ALL, ");
		sb.append("       SUM(LALL_NO_CONTENT) AS LALL_NO_CONTENT, SUM(LALL_CONTENT) AS LALL_CONTENT, SUM(LALL_ALL) AS LALL_ALL ");		
		sb.append("FROM DTL ");
		sb.append("WHERE 1 = 1 ");
		sb.append("GROUP BY '', '全行 合計' ");
		
		// 排序條件
		sb.append("ORDER BY REGION_CENTER_ID ASC, BRANCH_AREA_ID ASC, BRANCH_NBR ASC ");
				       
		queryCondition.setObject("sDATE", inputVO.getsDate());
		queryCondition.setObject("eDATE", inputVO.geteDate());
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);		
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
		for (Map<String, Object> centerMap : list) {
			String regionCenter = (String) centerMap.get("REGION_CENTER_NAME");
			if (centerTempList.indexOf(regionCenter) < 0) { //regionCenter
				centerTempList.add(regionCenter);
				
				Integer centerRowspan = 1;
				
				List<Map<String, Object>> branchAreaList = new ArrayList<Map<String,Object>>();
				ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
				for (Map<String, Object> branchAreaMap : list) {
					String branchArea = (String) branchAreaMap.get("BRANCH_AREA_NAME");
					
					Integer branchAreaRowspan = 1;
					
					//==== 營運區
					if (regionCenter.equals((String) branchAreaMap.get("REGION_CENTER_NAME"))) { 
						if (branchAreaTempList.indexOf(branchArea) < 0) { //branchArea
							branchAreaTempList.add(branchArea);
							
							//==== 分行
							List<Map<String, Object>> branchList = new ArrayList<Map<String,Object>>();
							ArrayList<String> branchTempList = new ArrayList<String>(); //比對用
							ArrayList<String> centerCountTempList = new ArrayList<String>(); //比對用
							for (Map<String, Object> branchMap : list) {
								String branch = (String) branchMap.get("BRANCH_NAME");
								
								if (branchArea != null && branchMap.get("BRANCH_AREA_NAME") != null) {
									if (branchArea.equals((String) branchMap.get("BRANCH_AREA_NAME"))) {
										if (branchTempList.indexOf(branch) < 0) { //branchArea
											branchTempList.add(branch);
											
											//==== 詳細資訊
											List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
											for (Map<String, Object> roleMap : list) {
												
												if (branch != null && roleMap.get("BRANCH_NAME") != null) {
													if (branch.equals((String) roleMap.get("BRANCH_NAME"))) {
														Map<String, Object> roleTempMap = new HashMap<String, Object>();
														
														roleTempMap.put("L05_NO_CONTENT", (BigDecimal) branchMap.get("L05_NO_CONTENT"));
														roleTempMap.put("L05_CONTENT", (BigDecimal) branchMap.get("L05_CONTENT"));
														roleTempMap.put("L05_ALL", (BigDecimal) branchMap.get("L05_ALL"));
														
														roleTempMap.put("L06_NO_CONTENT", (BigDecimal) branchMap.get("L06_NO_CONTENT"));
														roleTempMap.put("L06_CONTENT", (BigDecimal) branchMap.get("L06_CONTENT"));
														roleTempMap.put("L06_ALL", (BigDecimal) branchMap.get("L06_ALL"));
														
														roleTempMap.put("LH1_NO_CONTENT", (BigDecimal) branchMap.get("LH1_NO_CONTENT"));
														roleTempMap.put("LH1_CONTENT", (BigDecimal) branchMap.get("LH1_CONTENT"));
														roleTempMap.put("LH1_ALL", (BigDecimal) branchMap.get("LH1_ALL"));
														
														roleTempMap.put("LUX_NO_CONTENT", (BigDecimal) branchMap.get("LUX_NO_CONTENT"));
														roleTempMap.put("LUX_CONTENT", (BigDecimal) branchMap.get("LUX_CONTENT"));
														roleTempMap.put("LUX_ALL", (BigDecimal) branchMap.get("LUX_ALL"));
														
														roleTempMap.put("LALL_NO_CONTENT", (BigDecimal) branchMap.get("LALL_NO_CONTENT"));
														roleTempMap.put("LALL_CONTENT", (BigDecimal) branchMap.get("LALL_CONTENT"));
														roleTempMap.put("LALL_ALL", (BigDecimal) branchMap.get("LALL_ALL"));
														
														roleList.add(roleTempMap);
													}
												} else if (branchArea.equals(roleMap.get("BRANCH_AREA_NAME"))) {
													Map<String, Object> roleTempMap = new HashMap<String, Object>();
													
													roleTempMap.put("L05_NO_CONTENT", (BigDecimal) branchMap.get("L05_NO_CONTENT"));
													roleTempMap.put("L05_CONTENT", (BigDecimal) branchMap.get("L05_CONTENT"));
													roleTempMap.put("L05_ALL", (BigDecimal) branchMap.get("L05_ALL"));
													
													roleTempMap.put("L06_NO_CONTENT", (BigDecimal) branchMap.get("L06_NO_CONTENT"));
													roleTempMap.put("L06_CONTENT", (BigDecimal) branchMap.get("L06_CONTENT"));
													roleTempMap.put("L06_ALL", (BigDecimal) branchMap.get("L06_ALL"));
													
													roleTempMap.put("LH1_NO_CONTENT", (BigDecimal) branchMap.get("LH1_NO_CONTENT"));
													roleTempMap.put("LH1_CONTENT", (BigDecimal) branchMap.get("LH1_CONTENT"));
													roleTempMap.put("LH1_ALL", (BigDecimal) branchMap.get("LH1_ALL"));
													
													roleTempMap.put("LUX_NO_CONTENT", (BigDecimal) branchMap.get("LUX_NO_CONTENT"));
													roleTempMap.put("LUX_CONTENT", (BigDecimal) branchMap.get("LUX_CONTENT"));
													roleTempMap.put("LUX_ALL", (BigDecimal) branchMap.get("LUX_ALL"));
													
													roleTempMap.put("LALL_NO_CONTENT", (BigDecimal) branchMap.get("LALL_NO_CONTENT"));
													roleTempMap.put("LALL_CONTENT", (BigDecimal) branchMap.get("LALL_CONTENT"));
													roleTempMap.put("LALL_ALL", (BigDecimal) branchMap.get("LALL_ALL"));
													
													roleList.add(roleTempMap);
												}
											}
											
											Map<String, Object> branchTempMap = new HashMap<String, Object>();
											branchTempMap.put("AREA_GROUP", (String) branchMap.get("AREA_GROUP"));
											branchTempMap.put("BRANCH_NBR", (String) branchMap.get("BRANCH_NBR"));
											branchTempMap.put("BRANCH_NAME", branch);
											branchTempMap.put("ROLE", roleList);
											centerRowspan = centerRowspan + roleList.size();
											branchAreaRowspan = branchAreaRowspan + roleList.size();
											branchTempMap.put("ROWSPAN", roleList.size());
											
											branchList.add(branchTempMap);
										}
									}
								} else if (regionCenter.equals(branchMap.get("REGION_CENTER_NAME"))) {
									String centerCount = (String) branchMap.get("REGION_CENTER_NAME");
									if (centerCountTempList.indexOf(centerCount) < 0) { //regionCenter
										
										centerCountTempList.add(centerCount);
										
										//==== 詳細資訊
										List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
										for (Map<String, Object> roleMap : list) {
											
											if (regionCenter.equals(roleMap.get("REGION_CENTER_NAME"))) {
												Map<String, Object> roleTempMap = new HashMap<String, Object>();
											
												roleTempMap.put("L05_NO_CONTENT", (BigDecimal) branchMap.get("L05_NO_CONTENT"));
												roleTempMap.put("L05_CONTENT", (BigDecimal) branchMap.get("L05_CONTENT"));
												roleTempMap.put("L05_ALL", (BigDecimal) branchMap.get("L05_ALL"));
												
												roleTempMap.put("L06_NO_CONTENT", (BigDecimal) branchMap.get("L06_NO_CONTENT"));
												roleTempMap.put("L06_CONTENT", (BigDecimal) branchMap.get("L06_CONTENT"));
												roleTempMap.put("L06_ALL", (BigDecimal) branchMap.get("L06_ALL"));
												
												roleTempMap.put("LH1_NO_CONTENT", (BigDecimal) branchMap.get("LH1_NO_CONTENT"));
												roleTempMap.put("LH1_CONTENT", (BigDecimal) branchMap.get("LH1_CONTENT"));
												roleTempMap.put("LH1_ALL", (BigDecimal) branchMap.get("LH1_ALL"));
												
												roleTempMap.put("LUX_NO_CONTENT", (BigDecimal) branchMap.get("LUX_NO_CONTENT"));
												roleTempMap.put("LUX_CONTENT", (BigDecimal) branchMap.get("LUX_CONTENT"));
												roleTempMap.put("LUX_ALL", (BigDecimal) branchMap.get("LUX_ALL"));
												
												roleTempMap.put("LALL_NO_CONTENT", (BigDecimal) branchMap.get("LALL_NO_CONTENT"));
												roleTempMap.put("LALL_CONTENT", (BigDecimal) branchMap.get("LALL_CONTENT"));
												roleTempMap.put("LALL_ALL", (BigDecimal) branchMap.get("LALL_ALL"));
												
												roleList.add(roleTempMap);
												
											}
										}
										
										Map<String, Object> branchTempMap = new HashMap<String, Object>();
										branchTempMap.put("AREA_GROUP", (String) branchMap.get("AREA_GROUP"));
										branchTempMap.put("BRANCH_NBR", (String) branchMap.get("BRANCH_NBR"));
										branchTempMap.put("BRANCH_NAME", branch);
										branchTempMap.put("ROLE", roleList);
										centerRowspan = centerRowspan + roleList.size();
										branchAreaRowspan = branchAreaRowspan + roleList.size();
										branchTempMap.put("ROWSPAN", roleList.size());
										
										branchList.add(branchTempMap);
									}
								}
							}
							
							Map<String, Object> branchAreaTempMap = new HashMap<String, Object>();
							branchAreaTempMap.put("BRANCH_AREA_GROUP", (String) branchAreaMap.get("AREA_GROUP"));
							branchAreaTempMap.put("BRANCH_AREA_NAME", branchArea);
							branchAreaTempMap.put("BRANCH", branchList);
							centerRowspan = centerRowspan + branchList.size();
							branchAreaRowspan = branchAreaRowspan + branchList.size();
							branchAreaTempMap.put("ROWSPAN", branchAreaRowspan);
							branchAreaTempMap.put("COLSPAN", (branchList.size() == 1 && branchList.get(0).get("BRANCH_NAME") == null ? 3: 1));

							branchAreaList.add(branchAreaTempMap);
						}
					}
				}

				Map<String, Object> centerTempMap = new HashMap<String, Object>();
				centerTempMap.put("REGION_CENTER_NAME", regionCenter);
				centerTempMap.put("BRANCH_AREA", branchAreaList);
				centerRowspan = centerRowspan + branchAreaList.size();
				centerTempMap.put("ROWSPAN", centerRowspan);
				centerTempMap.put("COLSPAN", (branchAreaList.size() == 1 && branchAreaList.get(0).get("BRANCH_AREA_NAME") == null ? 4: 1));

				returnList.add(centerTempMap);
			}
		}
		
		outputVO.setExecStatisticsList(returnList);
		outputVO.setRep_execStatisticsList(list);
		
		sendRtnObject(outputVO);
	}

	// 留資名單執行統計表 - 匯出
	public void expExecStatistics (Object body, IPrimitiveMap header) throws Exception {
		
		PMS111InputVO inputVO = (PMS111InputVO) body;
		PMS111OutputVO outputVO = new PMS111OutputVO();
		
		String[] headerLine1 = {"業務處", "營運區", "分行別", "分別名稱", "組別", 
							    "官網線上留資房貸名單", "官網線上留資房貸名單", "官網線上留資房貸名單",
							    "官網線上留資信貸名單", "官網線上留資信貸名單", "官網線上留資信貸名單",
							    "官網轉介房貸名單", "官網轉介房貸名單", "官網轉介房貸名單",
							    "電銷轉介房貸名單", "電銷轉介房貸名單", "電銷轉介房貸名單",
							    "名單合計", "名單合計", "名單合計"};
		
		String[] headerLine2 = {"", "", "", "", "", 
								"未聯繫", "已聯繫", "名單數", 
								"未聯繫", "已聯繫", "名單數", 
								"未聯繫", "已聯繫", "名單數", 
								"未聯繫", "已聯繫", "名單數", 
								"未聯繫", "已聯繫", "名單數"};
		
		String[] mainLine    = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", "AREA_GROUP",
								"L05_NO_CONTENT", "L05_CONTENT", "L05_ALL", 
								"L06_NO_CONTENT", "L06_CONTENT", "L06_ALL", 
								"LH1_NO_CONTENT", "LH1_CONTENT", "LH1_ALL", 
								"LUX_NO_CONTENT", "LUX_CONTENT", "LUX_ALL", 
								"LALL_NO_CONTENT", "LALL_CONTENT", "LALL_ALL"}; 
						
		String fileName = "留資名單執行統計表_" + inputVO.getsDate() + "-" + inputVO.geteDate() + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("留資名單執行統計表");
		sheet.setDefaultColumnWidth(20);
		
		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		style.setBorderBottom((short) 1);
		style.setBorderTop((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);
		
		XSSFCellStyle qryHeadStyle = workbook.createCellStyle();
		qryHeadStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		qryHeadStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		qryHeadStyle.setFillForegroundColor(HSSFColor.YELLOW.index);// 填滿顏色
		qryHeadStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		qryHeadStyle.setBorderBottom((short) 1);
		qryHeadStyle.setBorderTop((short) 1);
		qryHeadStyle.setBorderLeft((short) 1);
		qryHeadStyle.setBorderRight((short) 1);
		
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
				cell.setCellStyle(qryHeadStyle);
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
			cell.setCellStyle(qryHeadStyle);
			cell.setCellValue(headerLine2[i]);
			
			if ("".equals(headerLine2[i])) {
				sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
			}
		}
		
		index++;
		
		ArrayList<String> centerTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchAreaTempList = new ArrayList<String>(); //比對用
		ArrayList<String> branchTempList = new ArrayList<String>(); //比對用
		Integer centerStartFlag = 0, branchAreaStartFlag = 0, branchStartFlag = 0;
		Integer centerEndFlag = 0, branchAreaEndFlag = 0, branchEndFlag = 0;
		
		Integer contectStartIndex = index;
		
		List<Map<String, String>> list = inputVO.getRep_execStatisticsList();
		
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(style);
				
				String centerName = list.get(i).get("REGION_CENTER_NAME");
				String branchAreaName = list.get(i).get("BRANCH_AREA_NAME");
				String branchName = list.get(i).get("BRANCH_NAME");
				
				if (j == 0 && centerTempList.indexOf(centerName) < 0) {
					centerTempList.add(centerName);
					if (centerEndFlag != 0) {
						if (null != centerName && centerName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(centerStartFlag + contectStartIndex, centerEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(i + contectStartIndex, i + contectStartIndex, j, j + 4)); // firstRow, endRow, firstColumn, endColumn
						}
					} else {
						if (StringUtils.equals("全行 合計", centerName)) {
							sheet.addMergedRegion(new CellRangeAddress(i + contectStartIndex, i + contectStartIndex, j, j + 4)); // firstRow, endRow, firstColumn, endColumn
						}
					}
					
					centerStartFlag = i;
					centerEndFlag = 0;
				} else if (j == 0 && null != list.get(i).get("REGION_CENTER_NAME")) {
					centerEndFlag = i;
				}
				
				if (j == 1 && branchAreaTempList.indexOf(branchAreaName) < 0) {
					branchAreaTempList.add(branchAreaName);
					
					if (branchAreaEndFlag != 0) {
						if (null != branchAreaName && branchAreaName.indexOf("合計") > 0) {
							sheet.addMergedRegion(new CellRangeAddress(branchAreaStartFlag + contectStartIndex, branchAreaEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
							sheet.addMergedRegion(new CellRangeAddress(i + contectStartIndex, i + contectStartIndex, j, j + 2)); // firstRow, endRow, firstColumn, endColumn
						} 
					} 
					
					branchAreaStartFlag = i;
					branchAreaEndFlag = 0;
				} else if (j == 1 && null != list.get(i).get("BRANCH_AREA_NAME")) {
					branchAreaEndFlag = i;
				}
				
				if (j == 2 && branchTempList.indexOf(branchName) < 0) {
					branchTempList.add(branchName);
					
					if (branchEndFlag != 0) {
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j, j)); // firstRow, endRow, firstColumn, endColumn
						sheet.addMergedRegion(new CellRangeAddress(branchStartFlag + contectStartIndex, branchEndFlag + contectStartIndex, j + 1, j + 1)); // firstRow, endRow, firstColumn, endColumn
					}
					
					branchStartFlag = i;
					branchEndFlag = 0;
				} else if (j == 2 && null != list.get(i).get("BRANCH_NAME")) {
					branchEndFlag = i; 
				}
				
				if (null != list.get(i).get(mainLine[0]) && null == list.get(i).get(mainLine[j])) {
				} else {
					cell.setCellValue(list.get(i).get(mainLine[j]));
				}
			}
			
			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		// download
 		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
 		sendRtnObject(outputVO);
	}
	
}
