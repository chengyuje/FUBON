package com.systex.jbranch.app.server.fps.pms114;

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
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms111.PMS111;
import com.systex.jbranch.app.server.fps.pms111.PMS111InputVO;
import com.systex.jbranch.app.server.fps.pms111.PMS111OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms114")
@Scope("request")
public class PMS114 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	
	// 查詢
	public void getList (Object body, IPrimitiveMap header) throws Exception {
		
		PMS114InputVO inputVO = (PMS114InputVO) body;
		PMS114OutputVO outputVO = new PMS114OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		// 防呆
		if (StringUtils.isBlank(inputVO.geteDate())) {
			// 若前端沒有選擇起日，預設為系統日(年月)
			if (StringUtils.isBlank(inputVO.getsDate())) {
				inputVO.setsDate(new SimpleDateFormat("yyyyMM").format(new Date()));
			}
			
			// 以起日查LIST取得第一筆(最大值) - 若無迄日=當月(1)
			PMS111InputVO inputVO_111 = new PMS111InputVO();
			inputVO_111.setsDate(inputVO.getsDate());
			inputVO_111.setMonInterval(BigDecimal.ONE);
			PMS111 pms111 = (PMS111) PlatformContext.getBean("pms111");
			PMS111OutputVO outputVO_temp = pms111.getEDateList(inputVO_111);
			if (outputVO_temp.geteDateList().size() > 0) 
				inputVO.seteDate((String) outputVO_temp.geteDateList().get(0).get("DATA"));
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT CAMP.CAMPAIGN_NAME, ");
		sb.append("         TO_CHAR(LEADS.CREATETIME, 'yyyy/MM/dd') AS CREATETIME, ");
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
		sb.append("         CASE WHEN LEADS.EMP_ID IS NOT NULL THEN LEADS.EMP_ID ELSE NULL END AS EXEC_EMP_ID, ");
		sb.append("         LEADS.PLAN_EMP_ID, ");
		sb.append("         LEADS.CUST_MEMO_SEQ, ");
		sb.append("         LEADS.PLAN_SEQ, ");
		sb.append("         LEADS.PLAN_YEARMON, ");
		sb.append("         LEADS.PLAN_CENTER_ID, ");
		sb.append("         LEADS.PLAN_AREA_ID ");
		sb.append("  FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("  INNER JOIN TBCAM_SFA_LEADS LEADS ON CAMP.CAMPAIGN_ID = LEADS.CAMPAIGN_ID AND CAMP.STEP_ID = LEADS.STEP_ID ");
		sb.append("  INNER JOIN VWORG_DEFN_INFO DEFN ON LEADS.BRANCH_ID = DEFN.BRANCH_NBR ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_TYPE = 'CAM.LEAD_TYPE' AND PARAM_CODE = CAMP.LEAD_TYPE ");
		sb.append("  LEFT JOIN TBCAM_SFA_CAMP_RESPONSE CP ON CAMP.LEAD_RESPONSE_CODE = CP.CAMPAIGN_ID AND LEADS.LEAD_STATUS = CP.LEAD_STATUS ");
		sb.append("  WHERE CAMP.LEAD_TYPE IN ('05', '06', 'H1', 'UX') ");
		sb.append("  AND CAMP.LEAD_RESPONSE_CODE = '0000000009' ");
		sb.append("  AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') >= :sDATE ");
		sb.append("  AND TO_CHAR(LEADS.CREATETIME, 'yyyyMM') <= :eDATE ");
		
		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			sb.append("      AND CAMP.LEAD_TYPE = :leadType ");
		
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
		sb.append("SELECT BASE.SFA_LEAD_ID, ");
		sb.append("       BASE.CREATETIME, ");
		sb.append("       BASE.LEAD_TYPE_NAME, ");
		sb.append("       CASE WHEN BASE.LEAD_TYPE = 'H1' AND CD.CUST_ID IS NOT NULL THEN CD.CUST_ID ELSE BASE.CUST_ID END AS CUST_ID, ");
		sb.append("       CD.CUST_NAME, ");
		sb.append("       BASE.BRANCH_NBR, ");
		sb.append("       BASE.BRANCH_NAME, ");
		sb.append("       CD.NOTE, ");
		sb.append("       NULL AS REF_EMP, "); // TODO -- 待確認
		sb.append("       NULL AS REP_EMP_CID, "); // TODO -- 待確認 
		sb.append("       BASE.EXEC_EMP_ID, ");
		sb.append("       LEAD_M.EMP_NAME AS EXEC_EMP_NAME, ");
		sb.append("       REC.VISIT_CREPLY, ");
		sb.append("       BASE.RESPONSE_NAME, ");
		sb.append("       REC.VISIT_DT, ");
		sb.append("       REC.VISIT_MEMO, ");
		sb.append("       BASE.PLAN_EMP_ID, ");
		sb.append("       PLAN_M.EMP_NAME AS PLAN_EMP_NAME, ");
		sb.append("       PL.MEETING_RESULT, ");
		sb.append("       PL.LOAN_CUST_ID, ");
		sb.append("       PL.CASE_NUM, ");
		sb.append("       DL.PIPELINE_STATUS, ");
		sb.append("       DL.APPROPRIATION_DATE, ");
		sb.append("       DL.APPROPRIATION_AMT ");
		sb.append("FROM BASE ");
		sb.append("LEFT JOIN TBCRM_CUST_VISIT_RECORD REC ON BASE.CUST_MEMO_SEQ = REC.VISIT_SEQ AND BASE.CUST_ID = REC.CUST_ID ");
		sb.append("LEFT JOIN TBPMS_PIPELINE PL ON BASE.PLAN_SEQ = PL.PLAN_SEQ AND BASE.PLAN_YEARMON = PL.PLAN_YEARMON AND BASE.PLAN_CENTER_ID = PL.CENTER_ID AND BASE.PLAN_AREA_ID = PL.AREA_ID ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT CASE_NO, PIPELINE_STATUS, MAX(APPROPRIATION_DATE) AS APPROPRIATION_DATE, SUM(APPROPRIATION_AMT) AS APPROPRIATION_AMT ");
		sb.append("  FROM TBPMS_PIPELINE_DTL ");
		sb.append("  GROUP BY CASE_NO, PIPELINE_STATUS ");
		sb.append(") DL ON PL.CASE_NUM = DL.CASE_NO ");
		sb.append("LEFT JOIN CUST_DTL CD ON BASE.SFA_LEAD_ID = CD.SFA_LEAD_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER LEAD_M ON BASE.EXEC_EMP_ID = LEAD_M.EMP_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER PLAN_M ON BASE.PLAN_EMP_ID = PLAN_M.EMP_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CM ON CASE WHEN BASE.LEAD_TYPE = 'H1' AND CD.CUST_ID IS NOT NULL THEN CD.CUST_ID ELSE BASE.CUST_ID END = CM.CUST_ID ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CM.AO_CODE = AO.AO_CODE ");
		sb.append("ORDER BY BASE.LEAD_TYPE, BASE.CREATETIME, BASE.REGION_CENTER_ID ASC, BASE.BRANCH_AREA_ID ASC, BASE.BRANCH_NBR ASC ");
				
		queryCondition.setObject("sDATE", inputVO.getsDate());
		queryCondition.setObject("eDATE", inputVO.geteDate());
		
		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			queryCondition.setObject("leadType", inputVO.getLeadType());
		
		queryCondition.setQueryString(sb.toString());
		outputVO.setQryList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	// 匯出
	public void export (Object body, IPrimitiveMap header) throws Exception {
		
		PMS114InputVO inputVO = (PMS114InputVO) body;
		PMS114OutputVO outputVO = new PMS114OutputVO();
		
		String[] headerLine1 = {"名單建立日期", "名單名稱(種類)", "客戶ID", "客戶名稱", "方便往來分行", "備註",
							    "轉介人員", "轉介人員",
							    "分行業務處名單處理狀態", "分行業務處名單處理狀態", "分行業務處名單處理狀態", "分行業務處名單處理狀態", "分行業務處名單處理狀態", "分行業務處名單處理狀態", 
							    "Pipleline銷售計劃", "Pipleline銷售計劃", "Pipleline銷售計劃", "Pipleline銷售計劃", "Pipleline銷售計劃", "Pipleline銷售計劃"};
		
		String[] headerLine2 = {"", "", "", "", "", "", 
								"姓名", "員工編號", 
								"業務員編", "業務姓名", "客戶回覆內容", "名單回應選項", "聯繫客戶時間", "通知客戶內容",
								"面談結果", "借款人身分證字號", "案件進件編號", "新個金徵審系統進度", "撥款日期", "撥款金額_百萬元"};
		
		String[] mainLine    = {"CREATETIME", "LEAD_TYPE_NAME", "CUST_ID", "CUST_NAME", "BRANCH_NBR", "NOTE",
								"REF_EMP", "REP_EMP_CID",
								"EXEC_EMP_ID", "EXEC_EMP_NAME", "VISIT_CREPLY", "RESPONSE_NAME", "VISIT_DT", "VISIT_MEMO", 
								"MEETING_RESULT", "LOAN_CUST_ID", "CASE_NUM", "PIPELINE_STATUS", "APPROPRIATION_DATE", "APPROPRIATION_AMT"}; 
				
		StringBuffer fileName = new StringBuffer();
		fileName.append("留資名單逐案追蹤聯繫狀態明細表_");
		
		if (StringUtils.isNotBlank(inputVO.getLeadType())) {
			XmlInfo xmlInfo = new XmlInfo();
			String leadTypeName = (String) xmlInfo.getVariable("PMS.PIPE_CONTENT_LEAD_TYPE", inputVO.getLeadType(), "F3");
			
			fileName.append(leadTypeName).append("_");
		} else {
			fileName.append("留資房信貸總名單_");
		}
		
		fileName.append(inputVO.getsDate());
		if (StringUtils.isNotBlank(inputVO.geteDate()) && !StringUtils.equals(inputVO.getsDate(), inputVO.geteDate())) {
			fileName.append("-" + inputVO.geteDate());
		}
		
		fileName.append(".xlsx");

		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("留資名單逐案追蹤聯繫狀態明細表");
		sheet.setDefaultColumnWidth(20);
		
		XSSFCellStyle centerStyle = workbook.createCellStyle();
		centerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		centerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		
		XSSFCellStyle leftStyle = workbook.createCellStyle();
		leftStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT); //水平置中
		leftStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		
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
				cell.setCellStyle(centerStyle);
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
			cell.setCellStyle(centerStyle);
			cell.setCellValue(headerLine2[i]);
			
			if ("".equals(headerLine2[i])) {
				sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
			}
		}
		
		index++;
		
		List<Map<String, String>> list = inputVO.getRep_qryList();
		
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				switch (mainLine[j]) {
					case "BRANCH_NBR" :
						cell.setCellStyle(centerStyle);
						cell.setCellValue(list.get(i).get(mainLine[j]) + "-" + list.get(i).get("BRANCH_NAME"));
						break;
					case "NOTE" :
						cell.setCellStyle(leftStyle);
						cell.setCellValue(list.get(i).get(mainLine[j]));
						break;
					default :
						cell.setCellStyle(centerStyle);
						cell.setCellValue(list.get(i).get(mainLine[j]));
						break;
				}
			}
			
			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		// download
 		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName.toString());
		
 		sendRtnObject(outputVO);
	}
}
