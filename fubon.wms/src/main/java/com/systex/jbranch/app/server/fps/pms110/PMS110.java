package com.systex.jbranch.app.server.fps.pms110;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
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
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Scope("request")
@Component("pms110")
public class PMS110 extends FubonWmsBizLogic {
	
	DataAccessManager dam = null;
	SimpleDateFormat SDFYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	private static BigDecimal amountUnit = new BigDecimal("1000000");
	
	String[] byPipelineColumn 	= {"PLAN_SEQ",				// 銷售計劃序號
								   "PLAN_YEARMON",			// 銷售計劃月份
								   "CENTER_ID",				// 業務處
								   "AREA_ID",				// 營運區
								   "BRA_NBR",				// 分行別
								   "CUST_ID",				// 客戶 ID
								   "CUST_NAME",				// 客戶姓名
								   "MEETING_DATE",			// 面談日期
								   "MEETING_RESULT",		// 面談結果=>PMS.PIPELINE_VIEW_RESULT
								   "EST_PRD",				// 預計承作商品大項=> PMS.PIPELINE_PRD
								   "EST_AMT",				// 預計承作金額
								   "EST_DRAW_DATE",			// 預計撥款日期
								   "CASE_SOURCE",			// 案件來源
								   "CASE_NUM",				// 案件編號(進件編號)
								   "MEMO",					// 備註
								   "EMP_ID",				// 業務人員員編
								   "CUST_SOURCE",			// 客戶來源
								   
								   "CHK_LOAN_DATE",			// 對保日期
								   "REFUSE_REASON",			// 已核不撥原因
								   
								   "LEAD_VAR_PHONE",		// 客戶連絡電話(留資名單使用)
								   "LEAD_VAR_C_TIME",		// 客戶方便聯絡時間(留資名單使用)
								   "LEAD_VAR_RTN_E_NAME",	// 客服或電銷轉介人員姓名(留資名單使用)
								   "LEAD_VAR_RTN_E_ID",		// 客服或電銷轉介人員員編(留資名單使用)

								   "LOAN_CUST_ID",			// 借款人身份證字號
								   "LOAN_CUST_NAME",		// 借款人姓名
								   "LOAN_AMT",				// 借款金額
								   
								   "VERSION",				// 版本
								   "CREATOR",				// 建立者
								   "CREATETIME",			// 開始建立日期
								   "MODIFIER",				// 最後更新者
								   "LASTUPDATE"				// 最後修改日期
								  };
	
	String[] byPipelineColumnID = {"planSEQ",				// 銷售計劃序號
								   "planYearmon",			// 銷售計劃月份
								   "centerId",				// 業務處
								   "areaId",				// 營運區
								   "braNbr",				// 分行別
								   "custId",				// 客戶 id
								   "custName",				// 客戶姓名
								   "meetingDate",			// 面談日期
								   "meetingResult",			// 面談結果=>pms.pipeline_view_result
								   "estPrd",				// 預計承作商品大項=> pms.pipeline_prd
								   "estAmt",				// 預計承作金額
								   "estDrawDate",			// 預計撥款日期
								   "caseSource",			// 案件來源
								   "caseNum",				// 案件編號(進件編號)
								   "memo",					// 備註
								   "empId",					// 業務人員員編
								   "custSource",			// 客戶來源
								   
								   "chkLoanDate",			// 對保日期
								   "refuseReason",			// 已核不撥原因
								   
								   "leadVarPhone",			// 客戶連絡電話(留資名單使用)
								   "leadVarCTime",			// 客戶方便聯絡時間(留資名單使用)
								   "leadVarRtnEName",		// 客服或電銷轉介人員姓名(留資名單使用)
								   "leadVarRtnEID",			// 客服或電銷轉介人員員編(留資名單使用)
					
								   "loanCustID",			// 借款人身份證字號
								   "loanCustName",			// 借款人姓名
								   "loanAmt",				// 借款金額
								   
								   "0",						// 版本
								   "loginID",				// 建立者
								   "SYSDATE",				// 開始建立日期
								   "loginID",				// 最後更新者
								   "SYSDATE"				// 最後修改日期
								  };

	String[] byPipelineDTLColumn= {"SNAP_DATE",				// ODS SNAP_DATE
								   "CASE_NO",				// 案件編號
								   "CASE_TYPE",				// 1:房貸/2:信貸/3:留貸
								   "CUST_ID",				// 客戶ID
								   "PIPELINE_PRD_ITEM",		// 承作商品類型11:循環型房貸-額度式/12:分期型房貸-購屋/13:分期型房貸-非購屋/21:一般信貸/22:職團信貸/23:卡友信貸/24:循環型信貸/99:無法分類
								   "PIPELINE_STATUS",		// 案件狀態01:進件/02:核准/03:已核准撤件/04:撥款/05:婉拒/06:自行撤件
								   "RECEIVE_DATE",			// 進件日期
								   "ALLOW_DATE",			// 核准日期
								   "ALLOW_AMT",				// 核准金額
								   "DECLINE_DATE",			// 婉拒日期
								   "DECLINE_AMT",			// 婉拒金額
								   "APPROPRIATION_DATE",	// 撥款日期
								   "APPROPRIATION_AMT",		// 撥款金額
								   "REF_UNIT",				// 轉介單位
								   "REF_EMP",				// 轉介人員
								   "REP_EMP_CID"			// 轉介人員身份證字號
	};

	/** 查詢 **/
	// EST_PRD_ITEM		預計承作商品細項 (討論後該表格不使用此欄位『存明細檔』)
	// CASE_STATUS		結案狀態/案件狀態 (討論後該表格不使用此欄位『存明細檔』)
	// PLAN_STATUS		銷售計劃狀態(暫無使用)
	// ACT_SEQ			活動序號(暫無使用)
	public void query (Object body, IPrimitiveMap header) throws JBranchException {

		PMS110InputVO inputVO = (PMS110InputVO) body;
		PMS110OutputVO outputVO = new PMS110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
			
		//=== 取得查詢資料
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		
		sb.append("SELECT ");
		
		for (int i = 0; i < byPipelineColumn.length; i++) {
			sb.append("       P.").append(byPipelineColumn[i]);
			if (i == byPipelineColumn.length - 1) {
				sb.append(" ");
			} else {
				sb.append(", ");
			}
		}

		sb.append(", ");
		
		for (int i = 0; i < byPipelineDTLColumn.length; i++) {
			switch (byPipelineDTLColumn[i]) {
				case "CASE_NO" :
					sb.append("       D.").append(byPipelineDTLColumn[i]).append(" AS D_CASE_NUM ");
					break;
				case "CUST_ID" :
					sb.append("       D.").append(byPipelineDTLColumn[i]).append(" AS D_CUST_ID ");
					break;
				default :
					sb.append("       D.").append(byPipelineDTLColumn[i]);
					break;
			}
			
			if (i == byPipelineDTLColumn.length - 1) {
				sb.append(" ");
			} else {
				sb.append(", ");
			}
		}
		
		sb.append("FROM TBPMS_PIPELINE P ");
		sb.append("LEFT JOIN TBPMS_PIPELINE_DTL D ON P.CASE_NUM = D.CASE_NO ");
		sb.append("WHERE 1 = 1 ");
		// 必填欄位：業務處/營運區/分行別/業務人員/銷售計劃月份
		sb.append("AND P.CENTER_ID = :centerID ");
		sb.append("AND P.AREA_ID = :areaID ");
		sb.append("AND P.BRA_NBR = :braID ");
		sb.append("AND P.EMP_ID = :empID ");
		sb.append("AND P.PLAN_YEARMON = :planYearmon ");
		queryCondition.setObject("centerID", inputVO.getRegion_center_id());
		queryCondition.setObject("areaID", inputVO.getBranch_area_id());
		queryCondition.setObject("braID", inputVO.getBranch_nbr());
		queryCondition.setObject("empID", inputVO.getEmp_id());
		queryCondition.setObject("planYearmon", inputVO.getPlanYearmon());

		// 承作商品
		if (StringUtils.isNotBlank(inputVO.getEstPrd())) {
			sb.append("AND P.EST_PRD = :estPrd ");
			queryCondition.setObject("estPrd", inputVO.getEstPrd());
		}
		
		// 客戶來源
		if (StringUtils.isNotBlank(inputVO.getCustSource())) {
			sb.append("AND P.CUST_SOURCE = :custSource ");
			queryCondition.setObject("custSource", inputVO.getCustSource());
		}

		// 案件狀態
		if (StringUtils.isNotBlank(inputVO.getPlanStatus())) {
			sb.append("AND D.PIPELINE_STATUS = :planStatus ");
			queryCondition.setObject("planStatus", inputVO.getPlanStatus());
		}
		
		// 撥款月份
		if (StringUtils.isNotBlank(inputVO.getDrawMonth())) {
			sb.append("AND TO_CHAR(D.APPROPRIATION_DATE, 'YYYYMM') = :drawMonth ");
			queryCondition.setObject("drawMonth", inputVO.getDrawMonth());
		}

		queryCondition.setQueryString(sb.toString());

		outputVO.setPipelineList(dam.exeQuery(queryCondition));

		//=== 取得總計
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		
		/***
			2021-08-18 : MODIFY BY OCEAN #0717: 修改消金業務計畫pipeline計畫 BY 雪慧
			預估撥款金額=>明細檔的核准金額
			實際撥款金額=>明細檔的撥款金額
	
			預估撥款金額計算規則，是依下列1+2 條件作為業績統計
			條件一：依新個金系統已核准待撥款且業管系統已登打預計本月撥款日期案件，採承作類型加總
			條件二：依新個金系統已進件且業管系統已登打預計本月撥款日期案件，採承作類型加總
			排除條件：新個金系統或業管系統「已核不撥原因」欄位非空白值
			
			實際撥款金額計算規則是依下列條件作為業績統計
			條件：依新個金系統本月已撥款案件，採承作類型加總
			PS:本月是以銷售計劃月份作為KEY值
		***/
		sb.append("SELECT BASE_TYPE.EST_PRD, ");
		sb.append("       BASE_TYPE.AMT_TYPE_NAME AS ITEM_NAME, ");
		sb.append("       NVL(BASE.ITEM11, 0) AS ITEM11, ");
		sb.append("       NVL(BASE.ITEM12, 0) AS ITEM12, ");
		sb.append("       NVL(BASE.ITEM13, 0) AS ITEM13, ");
		sb.append("       NVL(BASE.ITEM21, 0) AS ITEM21, ");
		sb.append("       NVL(BASE.ITEM22, 0) AS ITEM22, ");
		sb.append("       NVL(BASE.ITEM23, 0) AS ITEM23, ");
		sb.append("       NVL(BASE.ITEM24, 0) AS ITEM24, ");
		sb.append("       NVL(BASE.ITEM31, 0) AS ITEM31 ");
		sb.append("FROM ( ");
		sb.append("  SELECT EST_PRD, EST_PRD_NAME, AMT_TYPE, AMT_TYPE_NAME ");
		sb.append("  FROM ( ");
		sb.append("    SELECT PARAM_CODE AS EST_PRD, PARAM_NAME AS EST_PRD_NAME ");
		sb.append("    FROM TBSYSPARAMETER ");
		sb.append("    WHERE PARAM_TYPE = 'PMS.PIPELINE_PRD' ");
		sb.append("  ) PP ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT PARAM_CODE AS AMT_TYPE, PARAM_NAME AS AMT_TYPE_NAME ");
		sb.append("    FROM TBSYSPARAMETER ");
		sb.append("    WHERE PARAM_TYPE = 'PMS.PIPE_COUNT_COL' ");
		sb.append("  ) PPD ON 1 = 1 ");
		sb.append(") BASE_TYPE ");
		
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT EST_PRD, ");
		sb.append("         AMT_TYPE, ");
		sb.append("         NVL(ITEM11, 0) AS ITEM11, ");
		sb.append("         NVL(ITEM12, 0) AS ITEM12, ");
		sb.append("         NVL(ITEM13, 0) AS ITEM13, ");
		sb.append("         NVL(ITEM21, 0) AS ITEM21, ");
		sb.append("         NVL(ITEM22, 0) AS ITEM22, ");
		sb.append("         NVL(ITEM23, 0) AS ITEM23, ");
		sb.append("         NVL(ITEM24, 0) AS ITEM24, ");
		sb.append("         NVL(ITEM31, 0) AS ITEM31 ");
		sb.append("  FROM ( ");
		sb.append("    SELECT EST_PRD, PIPELINE_PRD_ITEM, AMT_TYPE, AMT ");
		sb.append("    FROM ( ");
		sb.append("      SELECT P.CASE_NUM, ");
		sb.append("             P.EST_PRD, ");
		sb.append("             D.PIPELINE_PRD_ITEM, ");
		sb.append("             CASE WHEN D.PIPELINE_STATUS IN ('01', '02') AND P.REFUSE_REASON IS NULL AND SUBSTR(P.EST_DRAW_DATE, 0, 6) = :planYearmon THEN NVL(D.ALLOW_AMT, 0) ELSE 0 END AS EST_AMT, ");
		sb.append("             CASE WHEN TO_CHAR(D.APPROPRIATION_DATE, 'YYYYMM') = :planYearmon THEN NVL(D.APPROPRIATION_AMT, 0) ELSE 0 END AS APPROPRIATION_AMT ");
		sb.append("      FROM TBPMS_PIPELINE P ");
		sb.append("      LEFT JOIN TBPMS_PIPELINE_DTL D ON P.CASE_NUM = D.CASE_NO ");
		sb.append("      WHERE P.CASE_NUM IS NOT NULL ");
		
		// 必填欄位：業務處/營運區/分行別/業務人員/銷售計劃月份
		sb.append("      AND P.CENTER_ID = :centerID ");
		sb.append("      AND P.AREA_ID = :areaID ");
		sb.append("      AND P.BRA_NBR = :braID ");
		sb.append("      AND P.EMP_ID = :empID ");
		queryCondition.setObject("centerID", inputVO.getRegion_center_id());
		queryCondition.setObject("areaID", inputVO.getBranch_area_id());
		queryCondition.setObject("braID", inputVO.getBranch_nbr());
		queryCondition.setObject("empID", inputVO.getEmp_id());
		queryCondition.setObject("planYearmon", inputVO.getPlanYearmon());
		
		sb.append("    ) ");
		sb.append("    UNPIVOT (AMT FOR AMT_TYPE IN (EST_AMT, APPROPRIATION_AMT)) ");
		sb.append("  ) ");
		sb.append("  PIVOT (SUM(AMT) FOR PIPELINE_PRD_ITEM IN ('11' AS ITEM11, '12' AS ITEM12, '13' AS ITEM13, '21' AS ITEM21, '22' AS ITEM22, '23' AS ITEM23, '24' AS ITEM24, '31' AS ITEM31)) ");
		sb.append(") BASE ON BASE_TYPE.EST_PRD = BASE.EST_PRD AND BASE_TYPE.AMT_TYPE = BASE.AMT_TYPE ");

		sb.append("ORDER BY BASE_TYPE.EST_PRD, BASE_TYPE.AMT_TYPE_NAME DESC ");

		queryCondition.setQueryString(sb.toString());

		outputVO.setPilelineSumList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/** 明細 **/
	public void getPipelineDTL (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS110InputVO inputVO = (PMS110InputVO) body;
		PMS110OutputVO outputVO = new PMS110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT ");
		for (int i = 0; i < byPipelineColumn.length; i++) {
			switch (byPipelineColumn[i]) {
				case "MEETING_DATE" :
				case "EST_DRAW_DATE" : 
				case "CHK_LOAN_DATE" : 
					sb.append("       TO_DATE(P.").append(byPipelineColumn[i]).append(", 'yyyyMMdd') AS ").append(byPipelineColumn[i]);
					break;
				case "LOAN_AMT" : 
					sb.append("       P.").append(byPipelineColumn[i]).append(" / ").append(amountUnit).append(" AS LOAN_AMT");
					break;
				default : 
					sb.append("       P.").append(byPipelineColumn[i]);
			}
			
			if (i == byPipelineColumn.length - 1) {
				sb.append(" ");
			} else {
				sb.append(", ");
			}
		}
		
		sb.append(", ");
		
		for (int i = 0; i < byPipelineDTLColumn.length; i++) {
			switch (byPipelineDTLColumn[i]) {
				case "CASE_NO" :
					sb.append("       D.").append(byPipelineDTLColumn[i]).append(" AS D_CASE_NUM ");
					break;
				case "CUST_ID" :
					sb.append("       D.").append(byPipelineDTLColumn[i]).append(" AS D_CUST_ID ");
					break;
				default :
					sb.append("       D.").append(byPipelineDTLColumn[i]);
					break;
			}
			
			if (i == byPipelineDTLColumn.length - 1) {
				sb.append(" ");
			} else {
				sb.append(", ");
			}
		}
		
		sb.append("FROM TBPMS_PIPELINE P ");
		sb.append("LEFT JOIN TBPMS_PIPELINE_DTL D ON P.CASE_NUM = D.CASE_NO ");
		sb.append("WHERE P.PLAN_SEQ = :planSEQ ");
		sb.append("AND P.PLAN_YEARMON = :planYearMon ");
		sb.append("AND P.CENTER_ID = :regionCenterID ");
		sb.append("AND P.AREA_ID = :branchAreaID ");

		// key
		queryCondition.setObject("planSEQ", inputVO.getPlanSeq());
		queryCondition.setObject("planYearMon", inputVO.getPlanYearmon());
		queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		
		// other
		// 進件編號
		if (StringUtils.isNotBlank(inputVO.getCase_num())) {
			sb.append("AND P.CASE_NUM = :caseNum ");
			queryCondition.setObject("caseNum", inputVO.getCase_num());
		}
		
		// TODO 實際承作商品類型(小項)
		if (StringUtils.isNotBlank(inputVO.getEstPrdItem())) {
			sb.append("AND D.PIPELINE_PRD_ITEM = :estPrdItem ");
			queryCondition.setObject("estPrdItem", inputVO.getEstPrdItem());
		}
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setPipelineDtl(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO); 
	}
	
	// 匯出
	public void export (Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException {
		
		XmlInfo xmlInfo = new XmlInfo();
		
		PMS110InputVO inputVO = (PMS110InputVO) body;
		PMS110OutputVO outputVO = new PMS110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		String fileName = "Pipeline計畫_" + inputVO.getPlanYearmon() + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Pipeline計畫");
		sheet.setDefaultColumnWidth(20);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		
		XSSFCellStyle sumHeadStyle = workbook.createCellStyle();
		sumHeadStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		sumHeadStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		sumHeadStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
		sumHeadStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		sumHeadStyle.setBorderBottom((short) 1);
		sumHeadStyle.setBorderTop((short) 1);
		sumHeadStyle.setBorderLeft((short) 1);
		sumHeadStyle.setBorderRight((short) 1);
		
		XSSFCellStyle qryHeadStyle = workbook.createCellStyle();
		qryHeadStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER); //水平置中
		qryHeadStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); //垂直置中
		qryHeadStyle.setFillForegroundColor(HSSFColor.YELLOW.index);// 填滿顏色
		qryHeadStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		qryHeadStyle.setBorderBottom((short) 1);
		qryHeadStyle.setBorderTop((short) 1);
		qryHeadStyle.setBorderLeft((short) 1);
		qryHeadStyle.setBorderRight((short) 1);

		//=== 取得統計header
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		
		sb.append("SELECT P.EST_PRD, P.EST_PRD_NAME, D.ITEM_TYPE AS ITEM_TYPE, D.ITEM_NAME ");
		sb.append("FROM (SELECT PARAM_CODE AS ITEM_TYPE, PARAM_NAME AS ITEM_NAME, PARAM_DESC AS EST_PRD, PARAM_ORDER AS ITEM_ORDER FROM TBSYSPARAMETER P WHERE PARAM_TYPE = 'PMS.PIPELINE_PRD_ITEM') D ");
		sb.append("INNER JOIN (SELECT PARAM_CODE AS EST_PRD, PARAM_NAME AS EST_PRD_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.PIPELINE_PRD') P ON D.EST_PRD = P.EST_PRD ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(inputVO.getEstPrd())) {
			sb.append("AND P.EST_PRD = :estPrd ");
			queryCondition.setObject("estPrd", inputVO.getEstPrd());
		}
			
		sb.append("ORDER BY D.ITEM_ORDER ");
		
		queryCondition.setQueryString(sb.toString());

		List<Map<String, String>> headerLineSumList = dam.exeQuery(queryCondition);
		//===
		
		Integer rowIndex = 0; // first row
		XSSFRow row = sheet.createRow(rowIndex);
		
		Integer cellIndex = 0;
		XSSFCell cell;
		
		ArrayList<String> estPrdTempList = new ArrayList<String>(); //比對用
		ArrayList<String> itemList = new ArrayList<String>(); //比對用
		for (int i = 0; i < headerLineSumList.size(); i++) {
			String estPrdName = (String) headerLineSumList.get(i).get("EST_PRD_NAME");

			// 當[非]相同商品時
			if (estPrdTempList.indexOf(estPrdName) < 0 ) {
				estPrdTempList.add(estPrdName);

				// 若明細LIST有商品時，先寫入預估撥款金額/實際撥款金額
				if (itemList.size() > 0) {
					cellIndex++;
					// 寫入總計
					cell = row.createCell(cellIndex);
					cell.setCellStyle(sumHeadStyle);
					cell.setCellValue("總計");

					cellIndex = 0;
					for (Map<String, String> sumMap : inputVO.getPilelineSumList()) {
						if (StringUtils.equals(sumMap.get("EST_PRD"), headerLineSumList.get(i - 1).get("EST_PRD"))) {
							row = sheet.createRow(rowIndex);
							
							Integer cellIndexT = 0;

							cell = row.createCell(cellIndexT);
							cell.setCellStyle(style);
							
							cell.setCellValue(sumMap.get("ITEM_NAME"));
							BigDecimal allAmt = BigDecimal.ZERO;
							for (int j = 0; j < itemList.size(); j++) {
								cellIndexT++;
								
								cell = row.createCell(cellIndexT);
								cell.setCellStyle(style);
								cell.setCellValue(sumMap.get(itemList.get(j)));
								allAmt = allAmt.add(new BigDecimal(sumMap.get(itemList.get(j))));
							}
							cellIndexT++;
							
							cell = row.createCell(cellIndexT);
							cell.setCellStyle(style);
							cell.setCellValue(String.valueOf(allAmt));
							
							rowIndex++;
						}
					}
					rowIndex++;
				}
				
				// 重新寫入明細LIST
				itemList = new ArrayList<String>();
				itemList.add("ITEM" + headerLineSumList.get(i).get("ITEM_TYPE"));

				// add row
				row = sheet.createRow(rowIndex);

				// 寫入承作商品(大)
				cell = row.createCell(cellIndex);
				cell.setCellStyle(sumHeadStyle);
				cell.setCellValue(estPrdName);

				cellIndex++;
				
				// 寫入承作商品(小)
				cell = row.createCell(cellIndex);
				cell.setCellStyle(sumHeadStyle);
				cell.setCellValue(headerLineSumList.get(i).get("ITEM_NAME"));
				
				rowIndex++;
			} else {
				itemList.add("ITEM" + headerLineSumList.get(i).get("ITEM_TYPE"));

				cellIndex++;
				
				// 寫入承作商品(小)
				cell = row.createCell(cellIndex);
				cell.setCellStyle(sumHeadStyle);
				cell.setCellValue(headerLineSumList.get(i).get("ITEM_NAME"));
			}
			
			// 最後一筆
			if (i == headerLineSumList.size() - 1) {
				// 若明細LIST有商品時，先寫入預估撥款金額/實際撥款金額
				if (itemList.size() > 0) {
					cellIndex++;
					// 寫入總計
					cell = row.createCell(cellIndex);
					cell.setCellStyle(sumHeadStyle);
					cell.setCellValue("總計");

					cellIndex = 0;
					for (Map<String, String> sumMap : inputVO.getPilelineSumList()) {
						if (StringUtils.equals(sumMap.get("EST_PRD"), headerLineSumList.get(i - 1).get("EST_PRD"))) {
							row = sheet.createRow(rowIndex);
							
							Integer cellIndexT = 0;

							cell = row.createCell(cellIndexT);
							cell.setCellStyle(style);
							
							cell.setCellValue(sumMap.get("ITEM_NAME"));
							BigDecimal allAmt = BigDecimal.ZERO;
							for (int j = 0; j < itemList.size(); j++) {
								cellIndexT++;
								
								cell = row.createCell(cellIndexT);
								cell.setCellStyle(style);
								cell.setCellValue(sumMap.get(itemList.get(j)));
								allAmt = allAmt.add(new BigDecimal(sumMap.get(itemList.get(j))));
							}
							cellIndexT++;
							
							cell = row.createCell(cellIndexT);
							cell.setCellStyle(style);
							cell.setCellValue(String.valueOf(allAmt));
							
							rowIndex++;
						}
					}
				}
				rowIndex++;
			}
		}
		
		//=== 查詢結果-表頭
		String[] headerLineList = {	"客戶來源", "案件狀態", "客戶ID", "客戶姓名", "面談日期", "預計承作商品", "預計承作金額", 
									"案件編號", "實際承作商品類型", "進件日期", "核准日期", "核准金額", "對保日期", "預計撥款日期", "撥款日期", "撥款金額", 
									"轉介單位", "轉介人員", "備註"};
		
		String[] mainLineList   = { "CUST_SOURCE", "PIPELINE_STATUS", "CUST_ID", "CUST_NAME", "MEETING_DATE", "EST_PRD", "EST_AMT",
									"CASE_NUM", "PRD_ITEM", "RECEIVE_DATE", "ALLOW_DATE", "ALLOW_AMT", "CHK_LOAN_DATE", "EST_DRAW_DATE", "APPROPRIATION_DATE", "APPROPRIATION_AMT", 
									"REF_UNIT", "REF_EMP", "MEMO"}; 
		
		row = sheet.createRow(rowIndex);
		for (int i = 0; i < headerLineList.length; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(qryHeadStyle);
			cell.setCellValue(headerLineList[i]);
		}
		
		rowIndex++;
		
		//=== 查詢結果-內容
		List<Map<String, String>> list = inputVO.getPipelineList();

		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(rowIndex);
			
			for (int j = 0; j < mainLineList.length; j++) {

				cell = row.createCell(j);
				cell.setCellStyle(style);
				switch (mainLineList[j]) {
					case "CUST_SOURCE":	// 客戶來源
						cell.setCellValue(xmlInfo.getVariable("PMS.CUST_SOURCE", list.get(i).get(mainLineList[j]), FormatHelper.FORMAT_3));
						break;
					case "PIPELINE_STATUS": // 案件狀態
						cell.setCellValue(xmlInfo.getVariable("PMS.PIPELINE_STATUS", list.get(i).get(mainLineList[j]), FormatHelper.FORMAT_3));
						break;
					case "EST_PRD": // 預計承作商品(大項)
						cell.setCellValue(xmlInfo.getVariable("PMS.PIPELINE_PRD", list.get(i).get(mainLineList[j]), FormatHelper.FORMAT_3));
						break;
					case "PRD_ITEM": // 實際承作商品(小項)
						cell.setCellValue(xmlInfo.getVariable("PMS.PIPELINE_PRD_ITEM", list.get(i).get(mainLineList[j]), FormatHelper.FORMAT_3));
						break;
					default:
						cell.setCellValue(list.get(i).get(mainLineList[j]));
				}
			}
			
			rowIndex++;
		}
		
		workbook.write(new FileOutputStream(filePath));
		
		// download
 		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
		
 		sendRtnObject(outputVO);
	}
	
	/** 新增銷售計劃-前端入口 **/
	//   1. PMS110
	//   2. CAM200 - 留資名單導入
	public void insert (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		this.sendRtnObject(this.insert(body));
	}
		
	/** 新增銷售計劃-後端入口 **/
	public PMS110OutputVO insert (Object body) throws JBranchException, ParseException {

		initUUID();
		
		PMS110InputVO inputVO = (PMS110InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		if (StringUtils.isNotBlank(inputVO.getCase_num()) && checkCaseNum(inputVO.getCase_num(), null, null, null, null)) {
			throw new APException("進件編號重覆，請重新確認！");
		}
		
		// 防呆
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		
		List<Map<String, Object>> defnList = new ArrayList<Map<String,Object>>();
		if (StringUtils.isBlank(inputVO.getRegion_center_id()) || StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sb.append("SELECT REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ");
			sb.append("FROM VWORG_DEFN_INFO ");
			sb.append("WHERE BRANCH_NBR = :branchNbr ");
			queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());

			queryCondition.setQueryString(sb.toString());

			defnList = dam.exeQuery(queryCondition);
		} else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("REGION_CENTER_ID", inputVO.getRegion_center_id());
			map.put("BRANCH_AREA_ID", inputVO.getBranch_area_id());
			map.put("BRANCH_NBR", inputVO.getBranch_nbr());
			
			defnList.add(map);
		}

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		if (defnList.size() > 0) {
			inputVO.setRegion_center_id((String) defnList.get(0).get("REGION_CENTER_ID"));
			inputVO.setBranch_area_id((String) defnList.get(0).get("BRANCH_AREA_ID"));
			
			sb.append("INSERT INTO TBPMS_PIPELINE ( ");
			
			for (int i = 0; i < byPipelineColumn.length; i++) {
				sb.append("       ").append(byPipelineColumn[i]);
				if (i == byPipelineColumn.length - 1) {
					sb.append(" ");
				} else {
					sb.append(", ");
				}
			}
			
			sb.append(") ");
			sb.append("VALUES ( ");
			
			for (int i = 0; i < byPipelineColumnID.length; i++) {
				switch (byPipelineColumn[i]) {
					case "VERSION" :
					case "CREATETIME":
					case "LASTUPDATE":
						sb.append("").append(byPipelineColumnID[i]);
						break;
					default :
						sb.append(":").append(byPipelineColumnID[i]);
						break;
				}
				
				if (i == byPipelineColumnID.length - 1) {
					sb.append(" ");
				} else {
					sb.append(", ");
				}
			}
			sb.append(") ");
	
			// KEY
			queryCondition.setObject("planSEQ", (StringUtils.isNotBlank(inputVO.getPlanSeq()) ? inputVO.getPlanSeq() : getSeqNum("TBPMS_PIPELINE_SEQ")));	
			queryCondition.setObject("planYearmon", inputVO.getPlanYearmon());
			queryCondition.setObject("centerId", inputVO.getRegion_center_id());
			queryCondition.setObject("areaId", inputVO.getBranch_area_id());
			
			// OTHER
			queryCondition.setObject("braNbr", inputVO.getBranch_nbr());
			queryCondition.setObject("custId", inputVO.getCustId());
			queryCondition.setObject("custName", inputVO.getCustName());
			queryCondition.setObject("meetingDate", (StringUtils.isNotBlank(inputVO.getMeetingDate()) ? SDFYYYYMMDD.format(new Date((long) Float.parseFloat(inputVO.getMeetingDate()))) : ""));
			queryCondition.setObject("meetingResult", (StringUtils.isNotBlank(inputVO.getMeetingResult()) ? inputVO.getMeetingResult() : ""));
			queryCondition.setObject("estPrd", inputVO.getEstPrd());
			queryCondition.setObject("estAmt", (StringUtils.isNotBlank(inputVO.getEstAmt()) ? new BigDecimal(inputVO.getEstAmt()) : BigDecimal.ZERO));
			queryCondition.setObject("estDrawDate", (StringUtils.isNotBlank(inputVO.getEstDrawDate()) ? SDFYYYYMMDD.format(new Date((long) Float.parseFloat(inputVO.getEstDrawDate()))) : ""));
			queryCondition.setObject("caseSource", (StringUtils.isNotBlank(inputVO.getCaseSource()) ? inputVO.getCaseSource() : ""));
			queryCondition.setObject("caseNum", (StringUtils.isNotBlank(inputVO.getCase_num()) ? inputVO.getCase_num().toUpperCase() : ""));
			queryCondition.setObject("memo", (StringUtils.isNotBlank(inputVO.getMemo()) ? inputVO.getMemo() : ""));
			queryCondition.setObject("empId", (StringUtils.isNotBlank(inputVO.getEmp_id()) ? inputVO.getEmp_id() : getUserVariable(FubonSystemVariableConsts.LOGINID)));
			queryCondition.setObject("custSource", (StringUtils.isNotBlank(inputVO.getCustSource()) ? inputVO.getCustSource() : "C"));

			queryCondition.setObject("chkLoanDate", (StringUtils.isNotBlank(inputVO.getChkLoanDate()) ? SDFYYYYMMDD.format(new Date((long) Float.parseFloat(inputVO.getChkLoanDate()))) : ""));
			queryCondition.setObject("refuseReason", (StringUtils.isNotBlank(inputVO.getRefuseReason()) ? inputVO.getRefuseReason() : ""));

			queryCondition.setObject("leadVarPhone", (StringUtils.isNotBlank(inputVO.getLeadVarPhone()) ? inputVO.getLeadVarPhone() : ""));
			queryCondition.setObject("leadVarCTime", (StringUtils.isNotBlank(inputVO.getLeadVarCTime()) ? inputVO.getLeadVarCTime() : ""));
			queryCondition.setObject("leadVarRtnEName", (StringUtils.isNotBlank(inputVO.getLeadVarRtnEName()) ? inputVO.getLeadVarRtnEName() : ""));
			queryCondition.setObject("leadVarRtnEID", (StringUtils.isNotBlank(inputVO.getLeadVarRtnEID()) ? inputVO.getLeadVarRtnEID() : ""));
			
			queryCondition.setObject("loanCustID", (StringUtils.isNotBlank(inputVO.getLoanCustID()) ? inputVO.getLoanCustID() : ""));
			queryCondition.setObject("loanCustName", (StringUtils.isNotBlank(inputVO.getLoanCustName()) ? inputVO.getLoanCustName() : ""));
			queryCondition.setObject("loanAmt", ((null != inputVO.getLoanAmt()) ? inputVO.getLoanAmt().multiply(amountUnit) : BigDecimal.ZERO));
	
			queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
	
			queryCondition.setQueryString(sb.toString());
	
			dam.exeUpdate(queryCondition);
		}
		
		return null;
	}
	
	/** 修改銷售計劃 **/
	public void update (Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS110InputVO inputVO = (PMS110InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		
		if (StringUtils.isNotBlank(inputVO.getCase_num()) && checkCaseNum(inputVO.getCase_num(), inputVO.getPlanSeq(), inputVO.getPlanYearmon(), inputVO.getRegion_center_id(), inputVO.getBranch_area_id())) {
			throw new APException("進件編號重覆，請重新確認！");
		}
		
		// 執行UPDATE
		sb.append("UPDATE TBPMS_PIPELINE ");
		sb.append("SET ");
		
		for (int i = 0; i < byPipelineColumn.length; i++) {
			switch (byPipelineColumn[i]) {
				// KEY 不更新
				case "PLAN_SEQ" :				// 銷售計劃序號
				case "PLAN_YEARMON" :			// 銷售計劃月份
				case "CENTER_ID" :				// 業務處
				case "AREA_ID" :				// 營運區
				// 非應寫入資訊不更新
				case "BRA_NBR" :				// 分行別
				case "CUST_SOURCE" :			// 客戶來源
				case "EMP_ID" : 				// 業務人員員編
				case "LEAD_VAR_PHONE" :			// 客戶連絡電話(留資名單使用)
				case "LEAD_VAR_C_TIME" :		// 客戶方便聯絡時間(留資名單使用)
				case "LEAD_VAR_RTN_E_NAME" :	// 客服或電銷轉介人員姓名(留資名單使用)
				case "LEAD_VAR_RTN_E_ID" :		// 客服或電銷轉介人員員編(留資名單使用)
				// 系統資訊部份不更新
				case "VERSION" :
				case "CREATOR":
				case "CREATETIME":
					break;
				// 最後更新日為系統日(使用oracle語法)
				case "LASTUPDATE":
					sb.append("").append(byPipelineColumn[i]).append(" = ").append(byPipelineColumnID[i]);
					
					if (i == byPipelineColumn.length - 1) {
						sb.append(" ");
					} else {
						sb.append(", ");
					}
					
					break;
				// 其他以畫面來源為主
				default :
					sb.append("").append(byPipelineColumn[i]).append(" = :").append(byPipelineColumnID[i]);
					
					if (i == byPipelineColumn.length - 1) {
						sb.append(" ");
					} else {
						sb.append(", ");
					}
					
					break;
			}
		}
		
		sb.append("WHERE PLAN_SEQ = :planSeq ");				// 銷售計劃序號
		sb.append("AND PLAN_YEARMON = :planYearmon ");			// 銷售計劃月份
		sb.append("AND CENTER_ID = :regionCenterID ");			// 業務處
		sb.append("AND AREA_ID = :branchAreaID ");				// 營運區

		// KEY
		queryCondition.setObject("planSeq", inputVO.getPlanSeq());
		queryCondition.setObject("planYearmon", inputVO.getPlanYearmon());
		queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());

		// OTHER
		queryCondition.setObject("custId", StringUtils.upperCase(inputVO.getCustId()));
		queryCondition.setObject("custName", inputVO.getCustName());
		queryCondition.setObject("meetingDate", (StringUtils.isNotBlank(inputVO.getMeetingDate()) ? SDFYYYYMMDD.format(new Date((long) Float.parseFloat(inputVO.getMeetingDate()))) : ""));
		queryCondition.setObject("meetingResult", (StringUtils.isNotBlank(inputVO.getMeetingResult()) ? inputVO.getMeetingResult() : ""));
		queryCondition.setObject("estPrd", inputVO.getEstPrd());
		queryCondition.setObject("estAmt", (StringUtils.isNotBlank(inputVO.getEstAmt()) ? new BigDecimal(inputVO.getEstAmt()) : BigDecimal.ZERO));
		queryCondition.setObject("estDrawDate", (StringUtils.isNotBlank(inputVO.getEstDrawDate()) ? SDFYYYYMMDD.format(new Date((long) Float.parseFloat(inputVO.getEstDrawDate()))) : ""));
		queryCondition.setObject("caseSource", (StringUtils.isNotBlank(inputVO.getCaseSource()) ? inputVO.getCaseSource() : ""));
		queryCondition.setObject("caseNum", (StringUtils.isNotBlank(inputVO.getCase_num()) ? inputVO.getCase_num().toUpperCase() : ""));
		queryCondition.setObject("memo", (StringUtils.isNotBlank(inputVO.getMemo()) ? inputVO.getMemo() : ""));
		queryCondition.setObject("chkLoanDate", (StringUtils.isNotBlank(inputVO.getChkLoanDate()) ? SDFYYYYMMDD.format(new Date((long) Float.parseFloat(inputVO.getChkLoanDate()))) : ""));
		queryCondition.setObject("refuseReason", (StringUtils.isNotBlank(inputVO.getRefuseReason()) ? inputVO.getRefuseReason() : ""));

		queryCondition.setObject("loanCustID", (StringUtils.isNotBlank(inputVO.getLoanCustID()) ? inputVO.getLoanCustID() : ""));
		queryCondition.setObject("loanCustName", inputVO.getLoanCustName());
		queryCondition.setObject("loanAmt", ((null != inputVO.getLoanAmt()) ? inputVO.getLoanAmt().multiply(amountUnit) : BigDecimal.ZERO));

		queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
		
		this.sendRtnObject(null);
	}

	/** 查詢案件編號是否已使用過 **/
	public Boolean checkCaseNum (String caseNum, 
								 String planSEQ,			// 銷售計劃序號
								 String planYearmon,		// 銷售計劃月份
								 String centerId,			// 業務處
								 String areaId				// 營運區
								) throws JBranchException, ParseException {

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT PLAN_SEQ, PLAN_YEARMON, CENTER_ID, AREA_ID ");
		sb.append("FROM TBPMS_PIPELINE ");
		sb.append("WHERE CASE_NUM = :caseNum ");
		
		queryCondition.setObject("caseNum", caseNum);
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			// 同一筆的話略過
			if (StringUtils.isNotBlank(planSEQ) && StringUtils.isNotBlank(planYearmon) && StringUtils.isNotBlank(centerId) && StringUtils.isNotBlank(areaId)) {
				if (StringUtils.equals(String.valueOf(list.get(0).get("PLAN_SEQ")), planSEQ) && 
					StringUtils.equals((String) list.get(0).get("PLAN_YEARMON"), planYearmon) &&
					StringUtils.equals((String) list.get(0).get("CENTER_ID"), centerId) &&
					StringUtils.equals((String) list.get(0).get("AREA_ID"), areaId)) {
					
					return false; 	// 更新狀態下：同一筆，不算重覆(無重覆)
				} else {
					return true; 	// 更新狀態下：非同一筆，即為重覆
				}
			} else {
				return true; 		// 新增狀態下：該進件編號已存在，即為重覆
			}
		} else {
			// 無資料，即無重覆
			return false;
		}
	}

	/** 取得seq**/
	private String getSeqNum(String TXN_ID) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch(TXN_ID) {
			case "TBPMS_PIPELINE_SEQ":
				sql.append("SELECT TBPMS_PIPELINE_SEQ.nextval AS SEQ FROM DUAL ");
				break;
		}
		
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
}
