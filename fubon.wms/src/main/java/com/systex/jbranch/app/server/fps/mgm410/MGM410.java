package com.systex.jbranch.app.server.fps.mgm410;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Font;
import com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILPK;
import com.systex.jbranch.app.common.fps.table.TBMGM_APPLY_DETAILVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2018/05/15
 * 
 */
@Component("mgm410")
@Scope("request")
public class MGM410 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//禮贈品結算查詢
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM410InputVO inputVO = (MGM410InputVO) body;
		MGM410OutputVO outputVO = new MGM410OutputVO();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DET.*, MAI.ACT_SEQ, MAI.CUST_ID, CUST.CUST_NAME, ");
		sb.append("NVL(INFO.BRANCH_NBR, CUST.BRA_NBR) AS BRANCH_NBR, ");
		sb.append("NVL(INFO.BRANCH_NAME, DEFN.BRANCH_NAME) AS BRANCH_NAME, ");
		sb.append("INFO.EMP_NAME, GIFT.GIFT_KIND, GIFT.GIFT_NAME FROM TBMGM_APPLY_DETAIL DET ");
		sb.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MAI.CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON CUST.BRA_NBR = DEFN.BRANCH_NBR ");
		sb.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO ON DET.CREATOR = INFO.EMP_ID ");
		sb.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON DET.GIFT_SEQ = GIFT.GIFT_SEQ ");
		sb.append("WHERE 1 = 1 ");
		
		//活動代碼
		if(StringUtils.isNotBlank(inputVO.getAct_seq())){
			sb.append("AND MAI.ACT_SEQ = :act_seq ");
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
		}
		
		//客戶ID
		if(StringUtils.isNotBlank(inputVO.getCust_id())){
			sb.append("AND MAI.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
		//申請書序號
		if(StringUtils.isNotBlank(inputVO.getApply_seq())){
			sb.append("AND DET.APPLY_SEQ = :apply_seq ");
			queryCondition.setObject("apply_seq", inputVO.getApply_seq());
		}
		
		//申請狀態
		if(StringUtils.isNotBlank(inputVO.getDelivery_status())){
			sb.append("AND DET.DELIVERY_STATUS = :delivery_status ");
			queryCondition.setObject("delivery_status", inputVO.getDelivery_status());
		}
		
		//申請日期(起)
		if(inputVO.getS_createtime() != null){
			sb.append("AND TRUNC(DET.CREATETIME) >= TRUNC( :s_createtime ) ");
			queryCondition.setObject("s_createtime", inputVO.getS_createtime());
		}
		
		//申請日期(迄)
		if(inputVO.getE_createtime() != null){
			sb.append("AND TRUNC(DET.CREATETIME) <= TRUNC( :e_createtime ) ");
			queryCondition.setObject("e_createtime", inputVO.getE_createtime());
		}
		
		//贈品性質
		if(StringUtils.isNotBlank(inputVO.getGift_kind())){
			sb.append("AND GIFT.GIFT_KIND = :gift_kind ");
			queryCondition.setObject("gift_kind", inputVO.getGift_kind());
		}
		
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	//禮贈品結算查詢
	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		MGM410InputVO inputVO = (MGM410InputVO) body;
		List<Map<String,Object>> editList = inputVO.getEditList();
		String delivery_status = inputVO.getDelivery_status();
		if(editList.size() > 0){
			if(StringUtils.isNotBlank(delivery_status)){
				dam = this.getDataAccessManager();
				for(Map<String,Object> map : editList){
					TBMGM_APPLY_DETAILPK pk = new TBMGM_APPLY_DETAILPK();
					pk.setAPPLY_SEQ(map.get("APPLY_SEQ").toString());
					pk.setGIFT_SEQ(map.get("GIFT_SEQ").toString());
					
					TBMGM_APPLY_DETAILVO vo = (TBMGM_APPLY_DETAILVO) dam.findByPKey(TBMGM_APPLY_DETAILVO.TABLE_UID, pk);
					if(null != vo){
						vo.setDELIVERY_STATUS(delivery_status);		//贈品出貨狀態
						
						//贈品出貨狀態：　1.分行已兌換待總行執行　2.廠商作業中　3.已出貨/已入帳
						if("2".equals(delivery_status)) {
							vo.setORDER_DATE(new Timestamp(System.currentTimeMillis()));		//贈品下單日期
							
						} else if ("3".equals(delivery_status)){
							vo.setDELIVERY_DATE(new Timestamp(System.currentTimeMillis()));		//贈品出貨日期
						}
						dam.update(vo);
						
					} else {
						throw new APException("系統發生錯誤請洽系統管理員。");
					}
				}
			} else {
				throw new APException("請選擇申請狀態。");
			}
		} else {
			throw new APException("請勾選。");
		}
		this.sendRtnObject(null);
	}
	
	//完整記錄匯出
	public void completeExport (Object body, IPrimitiveMap header) throws Exception {
		MGM410InputVO inputVO = (MGM410InputVO) body;
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT DET.*, TO_CHAR(DET.CREATETIME, 'YYYY/MM/DD') AS APPLY_DATE, ");
		sb.append("TO_CHAR(DET.DELIVERY_DATE, 'YYYY/MM/DD') AS DEL_DATE, ");
		sb.append("MAI.ACT_SEQ, MAI.CUST_ID, CUST.CUST_NAME, ");
		sb.append("CUST.AO_CODE, AO.EMP_ID, ORG.EMP_NAME, ");
		sb.append("DEFN.REGION_CENTER_ID, DEFN.REGION_CENTER_NAME, ");
		sb.append("DEFN.BRANCH_AREA_ID, DEFN.BRANCH_AREA_NAME, ");
		sb.append("CUST.BRA_NBR, DEFN.BRANCH_NAME, ");
		sb.append("GIFT.GIFT_KIND, GIFT.GIFT_NAME, DET.APPLY_NUMBER * GIFT.GIFT_COSTS AS COST, ");
		sb.append("AGIFT.GIFT_POINTS, ACT.ACT_NAME ");
		sb.append("FROM TBMGM_APPLY_DETAIL DET ");
		sb.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MAI.CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE AO ON CUST.AO_CODE = AO.AO_CODE ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG ON AO.EMP_ID = ORG.EMP_ID ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON CUST.BRA_NBR = DEFN.BRANCH_NBR ");
		sb.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON DET.GIFT_SEQ = GIFT.GIFT_SEQ ");
		sb.append("LEFT JOIN TBMGM_ACTIVITY_GIFT AGIFT ");
		sb.append("ON MAI.ACT_SEQ = AGIFT.ACT_SEQ AND DET.GIFT_SEQ = AGIFT.GIFT_SEQ ");
		sb.append("LEFT JOIN TBMGM_ACTIVITY_MAIN ACT ON MAI.ACT_SEQ = ACT.ACT_SEQ ");
		sb.append("WHERE 1 = 1 ");
		
		//活動代碼
		if(StringUtils.isNotBlank(inputVO.getAct_seq())){
			sb.append("AND MAI.ACT_SEQ = :act_seq ");
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
		}
		
		//客戶ID
		if(StringUtils.isNotBlank(inputVO.getCust_id())){
			sb.append("AND MAI.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
		//申請書序號
		if(StringUtils.isNotBlank(inputVO.getApply_seq())){
			sb.append("AND DET.APPLY_SEQ = :apply_seq ");
			queryCondition.setObject("apply_seq", inputVO.getApply_seq());
		}
		
		//申請狀態
		if(StringUtils.isNotBlank(inputVO.getDelivery_status())){
			sb.append("AND DET.DELIVERY_STATUS = :delivery_status ");
			queryCondition.setObject("delivery_status", inputVO.getDelivery_status());
		}
		
		//申請日期(起)
		if(inputVO.getS_createtime() != null){
			sb.append("AND TRUNC(DET.CREATETIME) >= TRUNC( :s_createtime ) ");
			queryCondition.setObject("s_createtime", inputVO.getS_createtime());
		}
		
		//申請日期(迄)
		if(inputVO.getE_createtime() != null){
			sb.append("AND TRUNC(DET.CREATETIME) <= TRUNC( :e_createtime ) ");
			queryCondition.setObject("e_createtime", inputVO.getE_createtime());
		}
		
		//贈品性質
		if(StringUtils.isNotBlank(inputVO.getGift_kind())){
			sb.append("AND GIFT.GIFT_KIND = :gift_kind ");
			queryCondition.setObject("gift_kind", inputVO.getGift_kind());
		}
		
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		if(resultList.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdfSlash = new SimpleDateFormat("yyyy/MM/dd");
			
			//相關資訊設定
			String fileName = String.format("完整記錄匯出%s.xlsx", sdf.format(new Date()));
			String uuid = UUID.randomUUID().toString();
			
			//建置Excel
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("完整記錄匯出" + sdf.format(new Date()));
			sheet.setDefaultColumnWidth(20);
			sheet.setDefaultRowHeightInPoints(20);
			
			//header
			String[] headerLine1 = {"現在區域名稱","現在營運區名稱","現在分行別","現在分行名稱","現在理專姓名",
									"客戶ID","客戶姓名","申請(兌換)日期","贈品性質","贈品名稱","數量","金額",
									"禮品點數","兌換點數總計","申請書序號","贈品出貨狀態","出貨日期","備註"};
			
			String[] mainLine    = {"REGION_CENTER_NAME","BRANCH_AREA_NAME","BRA_NBR","BRANCH_NAME","EMP_NAME",
									"CUST_ID","CUST_NAME","APPLY_DATE","GIFT_KIND","GIFT_NAME","APPLY_NUMBER","COST",
									"GIFT_POINTS","APPLY_POINTS","APPLY_SEQ","DELIVERY_STATUS","DEL_DATE","ACT_NAME"};
			
			//Subject font型式
			XSSFFont font = workbook.createFont();
			font.setColor(HSSFColor.BLACK.index);
			font.setBoldweight((short)Font.NORMAL);
			font.setFontHeight((short)500);

			// Subject cell型式
			XSSFCellStyle SubjectStyle = workbook.createCellStyle();
			SubjectStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			SubjectStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			SubjectStyle.setBorderBottom((short) 1);
			SubjectStyle.setBorderTop((short) 1);
			SubjectStyle.setBorderLeft((short) 1);
			SubjectStyle.setBorderRight((short) 1);
			SubjectStyle.setWrapText(true);
			SubjectStyle.setFont(font);

			// 表頭 CELL型式
			XSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			headingStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);	//填滿顏色
			headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headingStyle.setBorderBottom((short) 1);
			headingStyle.setBorderTop((short) 1);
			headingStyle.setBorderLeft((short) 1);
			headingStyle.setBorderRight((short) 1);
			headingStyle.setWrapText(true);

			Integer index = 0; // first row
			
			//計算合併儲存格欄位數
			int colNbr = headerLine1.length - 1;
			int dateColNbr = colNbr - 3;
			int titleColNbr = dateColNbr - 1;
			
			//Subject
			XSSFRow row = sheet.createRow(index);
			row.setHeight((short)800);
			XSSFCell sCell = row.createCell(0);
			sCell.setCellStyle(SubjectStyle);
			sCell.setCellValue("完　整　紀　錄　匯　出　");
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleColNbr));
			
			XSSFCell sCell2 = row.createCell(dateColNbr);
			sCell2.setCellStyle(SubjectStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, dateColNbr, colNbr));
			sCell2.setCellValue("日期：" + sdfSlash.format(new Date()));
			
			index++;
			
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); //比對用
			
		    row = sheet.createRow(index);
		    
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
			
			index++;
			
			// 資料 CELL型式
			XSSFCellStyle mainStyle = workbook.createCellStyle();
			mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			mainStyle.setBorderBottom((short) 1);
			mainStyle.setBorderTop((short) 1);
			mainStyle.setBorderLeft((short) 1);
			mainStyle.setBorderRight((short) 1);
			
			if (resultList.size() > 0) {
				
				XmlInfo xmlInfo = new XmlInfo();
				//MGM贈品品項檔_贈品性質
				Map<String, String> mgm_gift_kind = xmlInfo.doGetVariable("MGM.GIFT_KIND", FormatHelper.FORMAT_3);
				//贈品出貨狀態
				Map<String, String> mgm_delivery_status = xmlInfo.doGetVariable("MGM.DELIVERY_STATUS", FormatHelper.FORMAT_3);
				
				for (Map<String, Object> map : resultList) {
					row = sheet.createRow(index);

					if (map.size() > 0) {
						for (int j = 0; j < mainLine.length; j++) {
							XSSFCell cell = row.createCell(j);
							cell.setCellStyle(mainStyle);
							if (mainLine[j].indexOf("GIFT_KIND") >= 0){
								//贈品性質
								String gift_kind = "";
								if (map.containsKey("GIFT_KIND") && (map.get("GIFT_KIND") != null)) {
									gift_kind = mgm_gift_kind.get(map.get("GIFT_KIND"));
								}
								cell.setCellValue(gift_kind);
							} else if (mainLine[j].indexOf("DELIVERY_STATUS") >= 0) {
								//贈品出貨狀態
								String delivery_status = "";
								if (map.containsKey("DELIVERY_STATUS") && (map.get("DELIVERY_STATUS") != null)) {
									delivery_status = mgm_delivery_status.get(map.get("DELIVERY_STATUS"));
								}
								cell.setCellValue(delivery_status);
							} else {
								//其餘欄位
								cell.setCellValue(checkMap(map, mainLine[j]));
							}
						}

						index++;
					} 
				}
			} else { 
				row = sheet.createRow(index);
				XSSFCell cell = row.createCell(0);
				cell.setCellStyle(mainStyle);
				cell.setCellValue("查無資料！");
			}
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			
			workbook.write(fos);
			workbook.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName);
		}
	}
	
	//產製所得人建檔文件
	public void getIncomeData (Object body, IPrimitiveMap header) throws Exception {
		MGM410InputVO inputVO = (MGM410InputVO) body;
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT MAI.ACT_SEQ, DET.APPLY_SEQ, DET.DELIVERY_STATUS, DET.CREATETIME, ");
		sb.append("GIFT.GIFT_KIND, MAI.CUST_ID, CUST.CUST_NAME, ");
		sb.append("CON.CEN_ZIP_CODE, CON.CEN_ADDRESS, CON.COM_ZIP_CODE, CON.COM_ADDRESS, ");
		sb.append("NVL(CON.MOBILE_NO, TEL_NO || '#' || EXT_NO) AS PHONE, ");
		sb.append("DET.GIFT_SEQ, GIFT.GIFT_NAME, DET.APPLY_NUMBER, ");
		sb.append("DET.APPLY_NUMBER * GIFT.GIFT_COSTS AS PRICE ");
		sb.append("FROM TBMGM_APPLY_DETAIL DET ");
		sb.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
		sb.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON DET.GIFT_SEQ = GIFT.GIFT_SEQ ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MAI.CUST_ID = CUST.CUST_ID ");
		sb.append("LEFT JOIN TBCRM_CUST_CONTACT CON ON MAI.CUST_ID = CON.CUST_ID ");
		sb.append("WHERE 1 = 1 ");
		
		//活動代碼
		if(StringUtils.isNotBlank(inputVO.getAct_seq())){
			sb.append("AND MAI.ACT_SEQ = :act_seq ");
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
		}
		
		//客戶ID
		if(StringUtils.isNotBlank(inputVO.getCust_id())){
			sb.append("AND MAI.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
		//申請書序號
		if(StringUtils.isNotBlank(inputVO.getApply_seq())){
			sb.append("AND DET.APPLY_SEQ = :apply_seq ");
			queryCondition.setObject("apply_seq", inputVO.getApply_seq());
		}
		
		//申請狀態
		if(StringUtils.isNotBlank(inputVO.getDelivery_status())){
			sb.append("AND DET.DELIVERY_STATUS = :delivery_status ");
			queryCondition.setObject("delivery_status", inputVO.getDelivery_status());
		}
		
		//申請日期(起)
		if(inputVO.getS_createtime() != null){
			sb.append("AND TRUNC(DET.CREATETIME) >= TRUNC( :s_createtime ) ");
			queryCondition.setObject("s_createtime", inputVO.getS_createtime());
		}
		
		//申請日期(迄)
		if(inputVO.getE_createtime() != null){
			sb.append("AND TRUNC(DET.CREATETIME) <= TRUNC( :e_createtime ) ");
			queryCondition.setObject("e_createtime", inputVO.getE_createtime());
		}
		
		//贈品性質
		if(StringUtils.isNotBlank(inputVO.getGift_kind())){
			sb.append("AND GIFT.GIFT_KIND = :gift_kind ");
			queryCondition.setObject("gift_kind", inputVO.getGift_kind());
		}
		
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		if(resultList.size() > 0){
			int sum_apply_number = 0;
			int sum_price = 0;
			for(int i = 0; i < resultList.size(); i++){
				BigDecimal apply_number_b = (BigDecimal) resultList.get(i).get("APPLY_NUMBER");
				int apply_number = apply_number_b.intValueExact();
				BigDecimal price_b = (BigDecimal) resultList.get(i).get("PRICE");
				int price = price_b.intValueExact();
				
				sum_apply_number += apply_number;
				sum_price += price;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdfSlash = new SimpleDateFormat("yyyy/MM/dd");
			
			//相關資訊設定
			String fileName = String.format("產製所得人建檔文件%s.xlsx", sdf.format(new Date()));
			String uuid = UUID.randomUUID().toString();
			
			//建置Excel
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("產製所得人建檔文件" + sdf.format(new Date()));
			sheet.setDefaultColumnWidth(20);
			sheet.setDefaultRowHeightInPoints(20);
			
			//Subject font型式
			XSSFFont font = workbook.createFont();
			font.setColor(HSSFColor.BLACK.index);
			font.setBoldweight((short)Font.NORMAL);
			font.setFontHeight((short)300);

			// Subject cell型式
			XSSFCellStyle SubjectStyle = workbook.createCellStyle();
			SubjectStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			SubjectStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			SubjectStyle.setBorderBottom((short) 1);
//			SubjectStyle.setBorderTop((short) 1);
//			SubjectStyle.setBorderLeft((short) 1);
//			SubjectStyle.setBorderRight((short) 1);
			SubjectStyle.setWrapText(true);
			SubjectStyle.setFont(font);
			
			// 表頭  font型式
			XSSFFont headingFont = workbook.createFont();
			headingFont.setColor(HSSFColor.WHITE.index);
			headingFont.setBoldweight((short)Font.NORMAL);
			
			// 表頭 cell型式
			XSSFCellStyle headingStyle = workbook.createCellStyle();
			headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			headingStyle.setFillForegroundColor(HSSFColor.INDIGO.index);// 填滿顏色
			headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headingStyle.setBorderBottom((short) 1);
			headingStyle.setBorderTop((short) 1);
			headingStyle.setBorderLeft((short) 1);
			headingStyle.setBorderRight((short) 1);
			headingStyle.setWrapText(true);
			headingStyle.setFont(headingFont);

			Integer index = 0; // first row
			
			//Subject
			XSSFRow row = sheet.createRow(index);
			row.setHeight((short)500);
			XSSFCell sCell = row.createCell(0);
			sCell.setCellStyle(SubjectStyle);
			sCell.setCellValue("MGM活動總管系統建檔_銷帳用");
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
			
//			XSSFCell sCell2 = row.createCell(6);
//			sCell2.setCellStyle(SubjectStyle);
//			sheet.addMergedRegion(new CellRangeAddress(0,0,6,10));
//			sCell2.setCellValue("日期：" + sdfSlash.format(new Date()));
			
			index++;
			
			//header
			String[] headerLine1 = {"廠商編號/統編","廠商名稱","戶籍郵遞區號","營業/戶籍地址","通訊郵遞區號","通訊地址",
									"聯絡電話1","品項","數量","價格"};
			
			String[] mainLine    = {"CUST_ID","CUST_NAME","CEN_ZIP_CODE","CEN_ADDRESS","COM_ZIP_CODE","COM_ADDRESS",
									"PHONE","GIFT_NAME","APPLY_NUMBER","PRICE"};
			
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); //比對用
			
		    row = sheet.createRow(index);
		    
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
			
			index++;
			
			// 資料 CELL型式
			XSSFCellStyle mainStyle = workbook.createCellStyle();
			mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			mainStyle.setBorderBottom((short) 1);
			mainStyle.setBorderTop((short) 1);
			mainStyle.setBorderLeft((short) 1);
			mainStyle.setBorderRight((short) 1);
			
			if (resultList.size() > 0) {
				for (Map<String, Object> map : resultList) {
					row = sheet.createRow(index);

					if (map.size() > 0) {
						for (int j = 0; j < mainLine.length; j++) {
							XSSFCell cell = row.createCell(j);
							cell.setCellStyle(mainStyle);
							cell.setCellValue(checkMap(map, mainLine[j]));
						}
						index++;
					} 
				}
				
				//表尾加總
				row = sheet.createRow(index);
				XSSFCell endCell = row.createCell(8);
				endCell.setCellStyle(mainStyle);
				endCell.setCellValue(sum_apply_number);
				endCell = row.createCell(9);
				endCell.setCellStyle(mainStyle);
				endCell.setCellValue(sum_price);
				index++;
				
				//表尾簽核欄位、注意事項
				row = sheet.createRow(index);
				XSSFCell  memoCell = row.createCell(0);
				memoCell.setCellValue("處長：");
				
				XSSFCell  memoCell2 = row.createCell(2);
				memoCell2.setCellValue("科主管：");
				
				XSSFCell  memoCell3 = row.createCell(4);
				memoCell3.setCellValue("1. 此為予總管系統-MGM活動銷帳與提報所得人之前置作業。");
				sheet.addMergedRegion(new CellRangeAddress(index, index, 4, 9));
				
				index++;
				row = sheet.createRow(index);
				memoCell3 = row.createCell(4);
				memoCell3.setCellValue("2. 兌換筆數：共計" + resultList.size() + "筆、待銷帳金額：" + sum_price + "元。");
				sheet.addMergedRegion(new CellRangeAddress(index, index, 4, 9));
				
				index++;
				row = sheet.createRow(index);
				memoCell3 = row.createCell(4);
				memoCell3.setCellValue("3. 總管系統建立所得人資訊流程：");
				sheet.addMergedRegion(new CellRangeAddress(index, index, 4, 9));
				
				index++;
				row = sheet.createRow(index);
				memoCell3 = row.createCell(4);
				memoCell3.setCellValue("(1) 業務單位提供主管簽核之紙本與電子檔的所得人資訊給會計部。");
				sheet.addMergedRegion(new CellRangeAddress(index, index, 4, 9));
				
				index++;
				row = sheet.createRow(index);
				memoCell3 = row.createCell(4);
				memoCell3.setCellValue("(2) 會計部再依提供名單至總管系統建立所得人資訊。");
				sheet.addMergedRegion(new CellRangeAddress(index, index, 4, 9));
				
				index++;
				row = sheet.createRow(index);
				memoCell3 = row.createCell(4);
				memoCell3.setCellValue("註： 會計部將所得人資料建立完成後，將可至總管系統核銷費用與提報所得人。");
				sheet.addMergedRegion(new CellRangeAddress(index, index, 4, 9));
				
				sheet.addMergedRegion(new CellRangeAddress(index-5, index, 0, 1));
				sheet.addMergedRegion(new CellRangeAddress(index-5, index, 2, 3));
				
			} else { 
				row = sheet.createRow(index);
				XSSFCell cell = row.createCell(0);
				cell.setCellStyle(mainStyle);
				cell.setCellValue("查無資料！");
			}
			String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
			File targetFile = new File(filePath, uuid);
			FileOutputStream fos = new FileOutputStream(targetFile);
			
			workbook.write(fos);
			workbook.close();
			notifyClientToDownloadFile("temp//" + uuid, fileName);
		}
	}
	
	//報表下載
	public void reportDownload (Object body, IPrimitiveMap header) throws Exception {
		MGM410InputVO inputVO = (MGM410InputVO) body;
		
		if(StringUtils.isNotBlank(inputVO.getAct_seq())){
			//取得活動名稱及活動年份
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ACT_NAME, TO_CHAR(EFF_DATE, 'YYYY') AS YEAR FROM TBMGM_ACTIVITY_MAIN ");
			sb.append("WHERE ACT_SEQ = :act_seq ");
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
			queryCondition.setQueryString(sb.toString());
			resultList = dam.exeQuery(queryCondition);
			
			if(resultList.size() > 0){
				String act_name = resultList.get(0).get("ACT_NAME").toString();
				String act_year = resultList.get(0).get("YEAR").toString();
				int year = Integer.parseInt(act_year) + 1;
				String act_next_year = String.valueOf(year); 
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat sdfSlash = new SimpleDateFormat("yyyy/MM/dd");
				
				//相關資訊設定
				String fileName = String.format("商品兌換統計月報表%s.xlsx", sdf.format(new Date()));
				String uuid = UUID.randomUUID().toString();
				
				//建置Excel
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet("商品兌換統計月報表" + sdf.format(new Date()));
				sheet.setDefaultColumnWidth(20);
				sheet.setDefaultRowHeightInPoints(20);
				
				//標題_字型樣式
				XSSFFont font = workbook.createFont();
				font.setColor(HSSFColor.BLACK.index);
				font.setBoldweight((short)Font.NORMAL);
				font.setFontHeight((short)300);

				//標題_欄位型式
				XSSFCellStyle SubjectStyle = workbook.createCellStyle();
				SubjectStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
				SubjectStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
				SubjectStyle.setBorderBottom((short) 1);
				SubjectStyle.setBorderTop((short) 1);
				SubjectStyle.setBorderLeft((short) 1);
				SubjectStyle.setBorderRight((short) 1);
				SubjectStyle.setWrapText(true);
				SubjectStyle.setFont(font);

				//表頭_字型樣式
				XSSFFont headingFont = workbook.createFont();
				headingFont.setBoldweight((short)Font.NORMAL);
				
				//表頭_欄位型式
				XSSFCellStyle headingStyle = workbook.createCellStyle();
				headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
				headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
				headingStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);	//填滿顏色
				headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				headingStyle.setBorderBottom((short) 1);
				headingStyle.setBorderTop((short) 1);
				headingStyle.setBorderLeft((short) 1);
				headingStyle.setBorderRight((short) 1);
				headingStyle.setWrapText(true);
				headingStyle.setFont(headingFont);

				Integer index = 0; // first row
				
				//Subject
				XSSFRow row = sheet.createRow(index);
				row.setHeight((short)500);
				XSSFCell sCell = row.createCell(0);
				sCell.setCellStyle(SubjectStyle);
				sCell.setCellValue("【" + act_name + "】商品兌換統計月報表");
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 19));
				
				index++;
				
				//header
				String[] headerLine1 = {"商品編號","兌換點數","兌換贈品","成本", 
										act_year + ".05", act_year + ".06", act_year + ".07", act_year + ".08", 
										act_year + ".09", act_year + ".10", act_year + ".11", act_year + ".12", 
										act_next_year + ".01", act_next_year + ".02", act_next_year + ".03",
										act_next_year + ".04", act_next_year + ".05", act_next_year + ".06",
										act_next_year + ".06 II", "合計"};
				
//				String[] mainLine    = {"REGION_CENTER_NAME","BRANCH_AREA_NAME","BRA_NBR","BRANCH_NAME","EMP_NAME",
//										"CUST_ID","CUST_NAME","GIFT_KIND","GIFT_NAME","APPLY_NUMBER","COST","GIFT_POINTS",
//										"APPLY_POINTS","APPLY_SEQ","ACT_NAME"};
				
				Integer startFlag = 0;
				Integer endFlag = 0;
				ArrayList<String> tempList = new ArrayList<String>(); //比對用
				
			    row = sheet.createRow(index);
			    
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
				
				index++;
				
				//取得活動適用贈品相關資訊
				List<Map<String,Object>> giftList = new ArrayList<Map<String,Object>>();
				dam = this.getDataAccessManager();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("SELECT AG.GIFT_SEQ, AG.GIFT_POINTS, GI.GIFT_NAME, GI.GIFT_COSTS FROM ( ");
				sb.append("SELECT GIFT_SEQ, GIFT_POINTS FROM TBMGM_ACTIVITY_GIFT WHERE ACT_SEQ = :act_seq ) AG ");
				sb.append("LEFT JOIN TBMGM_GIFT_INFO GI ON AG.GIFT_SEQ = GI.GIFT_SEQ ORDER BY GIFT_SEQ ");
				queryCondition.setObject("act_seq", inputVO.getAct_seq());
				queryCondition.setQueryString(sb.toString());
				giftList = dam.exeQuery(queryCondition);
				
				// 資料 CELL型式
				XSSFCellStyle mainStyle = workbook.createCellStyle();
				mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
				mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
				mainStyle.setBorderBottom((short) 1);
				mainStyle.setBorderTop((short) 1);
				mainStyle.setBorderLeft((short) 1);
				mainStyle.setBorderRight((short) 1);
				
				if(giftList.size() > 0){
					for(Map<String, Object> map : giftList){
						row = sheet.createRow(index);
						
						if (map.size() > 0) {
							//商品編號
							XSSFCell cell = row.createCell(0);
							cell.setCellStyle(mainStyle);
							cell.setCellValue(map.get("GIFT_SEQ").toString());
							
							//兌換點數
							cell = row.createCell(1);
							cell.setCellStyle(mainStyle);
							cell.setCellValue(map.get("GIFT_POINTS").toString());
							
							//兌換贈品
							cell = row.createCell(2);
							cell.setCellStyle(mainStyle);
							cell.setCellValue(map.get("GIFT_NAME").toString());
							
							//成本
							cell = row.createCell(3);
							cell.setCellStyle(mainStyle);
							cell.setCellValue(map.get("GIFT_COSTS").toString());
							
							//取得活動當年份5月～隔年5月份商品兌換份數
							List<Map<String,Object>> appNbrList = new ArrayList<Map<String,Object>>();
							dam = this.getDataAccessManager();
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuffer();
							sb.append("SELECT TO_CHAR(ORDER_DATE, 'YYYY') AS YEAR, ");
							sb.append("TO_CHAR(ORDER_DATE, 'MM') AS MONTH, ");
							sb.append("GIFT_SEQ, SUM(APPLY_NUMBER) AS APPLY_NUMBER ");
							sb.append("FROM ( SELECT DET.* FROM TBMGM_APPLY_DETAIL DET ");
							sb.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
							sb.append("WHERE MAI.ACT_SEQ = :act_seq AND ORDER_DATE IS NOT NULL ) ");
							sb.append("GROUP BY TO_CHAR(ORDER_DATE, 'YYYY'), TO_CHAR(ORDER_DATE, 'MM'), GIFT_SEQ ");
							
							queryCondition.setObject("act_seq", inputVO.getAct_seq());
							queryCondition.setQueryString(sb.toString());
							appNbrList = dam.exeQuery(queryCondition);
							
							//活動當年5月~12月
							for(int i = 4; i < 12; i++){
								cell = row.createCell(i);
								cell.setCellStyle(mainStyle);
								
								for(Map<String,Object> appMap : appNbrList){
									if(act_year.equals(appMap.get("YEAR").toString()) && map.get("GIFT_SEQ").toString().equals(appMap.get("GIFT_SEQ").toString())){
										int month = Integer.parseInt(appMap.get("MONTH").toString());
										if(month == i + 1){
											cell.setCellValue(appMap.get("APPLY_NUMBER").toString());
										}
									}
								}
							}
							
							//活動隔年1月~5月份商品兌換份數
							for(int i = 12; i < 17; i++){
								cell = row.createCell(i);
								cell.setCellStyle(mainStyle);
								
								for(Map<String,Object> appMap : appNbrList){
									if(act_next_year.equals(appMap.get("YEAR").toString()) && map.get("GIFT_SEQ").toString().equals(appMap.get("GIFT_SEQ").toString())){
										int month = Integer.parseInt(appMap.get("MONTH").toString());
										for(int mon = 1; mon < 6; mon++){
											if(month == mon){
												cell.setCellValue(appMap.get("APPLY_NUMBER").toString());
											}
										}
									}
								}
							}
							
							//活動隔年6月份商品兌換份數
							List<Map<String,Object>> totalAppList = new ArrayList<Map<String,Object>>();
							List<Map<String,Object>> junAppList = new ArrayList<Map<String,Object>>();
							dam = this.getDataAccessManager();
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuffer();
							sb.append("SELECT TO_CHAR(ORDER_DATE, 'YYYY') AS YEAR, TO_CHAR(ORDER_DATE, 'MM') AS MONTH, ");
							sb.append("TO_CHAR(ORDER_DATE, 'DD') AS DAY, GIFT_SEQ, SUM(APPLY_NUMBER) AS APPLY_NUMBER ");
							sb.append("FROM ( SELECT DET.* FROM TBMGM_APPLY_DETAIL DET ");
							sb.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
							sb.append("WHERE MAI.ACT_SEQ = :act_seq AND ORDER_DATE IS NOT NULL ) ");
							sb.append("GROUP BY TO_CHAR(ORDER_DATE, 'YYYY'), TO_CHAR(ORDER_DATE, 'MM'), ");
							sb.append("TO_CHAR(ORDER_DATE, 'DD'), GIFT_SEQ ORDER BY TO_CHAR(ORDER_DATE, 'YYYY'), ");
							sb.append("TO_CHAR(ORDER_DATE, 'MM'), TO_CHAR(ORDER_DATE, 'DD') ");
							queryCondition.setObject("act_seq", inputVO.getAct_seq());
							queryCondition.setQueryString(sb.toString());
							totalAppList = dam.exeQuery(queryCondition);
							
							for(int i = 0; i < totalAppList.size(); i++){
								if(act_next_year.equals(totalAppList.get(i).get("YEAR").toString()) && 
										map.get("GIFT_SEQ").toString().equals(totalAppList.get(i).get("GIFT_SEQ").toString())){
									int month = Integer.parseInt(totalAppList.get(i).get("MONTH").toString());
									if(month == 6){
										junAppList.add(totalAppList.get(i));
									}
								}
							}
							
							if(junAppList.size() > 1){
								cell = row.createCell(17);
								cell.setCellStyle(mainStyle);
								cell.setCellValue(junAppList.get(0).get("APPLY_NUMBER").toString());
								
								cell = row.createCell(18);
								cell.setCellStyle(mainStyle);
								cell.setCellValue(junAppList.get(1).get("APPLY_NUMBER").toString());
								
							} else if (junAppList.size() == 1){
								cell = row.createCell(17);
								cell.setCellStyle(mainStyle);
								cell.setCellValue(junAppList.get(0).get("APPLY_NUMBER").toString());
								cell = row.createCell(18);
								cell.setCellStyle(mainStyle);
							} else {
								cell = row.createCell(17);
								cell.setCellStyle(mainStyle);
								cell = row.createCell(18);
								cell.setCellStyle(mainStyle);
							}
							
							//商品兌換份數合計
							cell = row.createCell(19);
							cell.setCellStyle(mainStyle);
							
							List<Map<String,Object>> giftAppList = new ArrayList<Map<String,Object>>();
							dam = this.getDataAccessManager();
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuffer();
							sb.append("SELECT GIFT_SEQ, SUM(APPLY_NUMBER) AS SUM_APPLY_NUMBER FROM ( ");
							sb.append("SELECT * FROM TBMGM_APPLY_DETAIL DET ");
							sb.append("LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
							sb.append("WHERE MAI.ACT_SEQ = :act_seq AND ORDER_DATE IS NOT NULL ) ");
							sb.append("GROUP BY GIFT_SEQ ");
							
							queryCondition.setObject("act_seq", inputVO.getAct_seq());
							queryCondition.setQueryString(sb.toString());
							giftAppList = dam.exeQuery(queryCondition);
							
							for(Map<String, Object> giftAppMap : giftAppList){
								if(map.get("GIFT_SEQ").toString().equals(giftAppMap.get("GIFT_SEQ").toString())){
									cell.setCellValue(giftAppMap.get("SUM_APPLY_NUMBER").toString());
								}
							}
						}
						index++;
					}
				} else { 
					row = sheet.createRow(index);
					XSSFCell cell = row.createCell(0);
					cell.setCellStyle(mainStyle);
					cell.setCellValue("查無資料！");
				}
				
				//總計(筆數、點數、份數、金額、總核發點數、已兌換點數、未兌換點數)
				int startIndex = index;
				for(int i = 0; i < 7; i++){
					XSSFRow totalRow = sheet.createRow(index);
					for(int col = 0; col < 3; col++){
						XSSFCell totalCell = totalRow.createCell(col);
						totalCell.setCellStyle(mainStyle);
						totalCell.setCellValue("總計");
					}
					
					XSSFCell totalCell = totalRow.createCell(3);
					totalCell.setCellStyle(mainStyle);
					if(i == 0){
						totalCell.setCellValue("筆數");
					} else if (i == 1){
						totalCell.setCellValue("點數");
					} else if (i == 2){
						totalCell.setCellValue("份數");
					} else if (i == 3){
						totalCell.setCellValue("金額");
					} else if (i == 4){
						totalCell.setCellValue("總核發點數");
					} else if (i == 5){
						totalCell.setCellValue("已兌換點數");
					} else if (i == 6){
						totalCell.setCellValue("未兌換點數");
					}
					
					//查詢每月筆數、點數、份數、金額
					List<Map<String,Object>> totalCountList = new ArrayList<Map<String,Object>>();
					dam = this.getDataAccessManager();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append("SELECT TO_CHAR(DET.ORDER_DATE, 'YYYY') AS YEAR, TO_CHAR(DET.ORDER_DATE, 'MM') AS MONTH, ");
					sb.append("COUNT(DISTINCT DET.APPLY_SEQ) AS APPLY_COUNT, SUM(DET.APPLY_POINTS) AS MON_TOL_POINTS, ");
					sb.append("SUM(DET.APPLY_NUMBER) AS MON_TOL_NBR, SUM(DET.APPLY_NUMBER * GIFT.GIFT_COSTS) AS MON_TOL_COSTS ");
					sb.append("FROM TBMGM_APPLY_DETAIL DET LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
					sb.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON DET.GIFT_SEQ = GIFT.GIFT_SEQ ");
					sb.append("WHERE MAI.ACT_SEQ = :act_seq AND DET.ORDER_DATE IS NOT NULL ");
					sb.append("GROUP BY TO_CHAR(DET.ORDER_DATE, 'YYYY'), TO_CHAR(DET.ORDER_DATE, 'MM') ");
					
					queryCondition.setObject("act_seq", inputVO.getAct_seq());
					queryCondition.setQueryString(sb.toString());
					totalCountList = dam.exeQuery(queryCondition);
					
					//查詢每月核發點數
					List<Map<String,Object>> accPointsList = new ArrayList<Map<String,Object>>();
					dam = this.getDataAccessManager();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append("SELECT TO_CHAR(RELEASE_DATE, 'YYYY') AS YEAR, ");
					sb.append("TO_CHAR(RELEASE_DATE, 'MM') AS MONTH, SUM(APPR_POINTS) AS MONTHLY_APPR_POINTS ");
					sb.append("FROM TBMGM_MGM WHERE ACT_SEQ = :act_seq AND RELEASE_DATE IS NOT NULL ");
					sb.append("GROUP BY TO_CHAR(RELEASE_DATE, 'YYYY'), TO_CHAR(RELEASE_DATE, 'MM') ");
					sb.append("ORDER BY TO_CHAR(RELEASE_DATE, 'YYYY'), TO_CHAR(RELEASE_DATE, 'MM') ");
					
					queryCondition.setObject("act_seq", inputVO.getAct_seq());
					queryCondition.setQueryString(sb.toString());
					accPointsList = dam.exeQuery(queryCondition);
					
					//查詢每月兌換點數
					List<Map<String,Object>> exchPointsList = new ArrayList<Map<String,Object>>();
					dam = this.getDataAccessManager();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append("SELECT TO_CHAR(CREATETIME, 'YYYY') AS YEAR, ");
					sb.append("TO_CHAR(CREATETIME, 'MM') AS MONTH, SUM(EXCHANGE_POINTS) AS MONTHLY_EXCH_POINTS ");
					sb.append("FROM TBMGM_APPLY_MAIN WHERE ACT_SEQ = :act_seq ");
					sb.append("GROUP BY TO_CHAR(CREATETIME, 'YYYY'), TO_CHAR(CREATETIME, 'MM') ");
					sb.append("ORDER BY TO_CHAR(CREATETIME, 'YYYY'), TO_CHAR(CREATETIME, 'MM') ");
					
					queryCondition.setObject("act_seq", inputVO.getAct_seq());
					queryCondition.setQueryString(sb.toString());
					exchPointsList = dam.exeQuery(queryCondition);
					
					int accPoints = 0;
					int exchPoints = 0;
					//活動當年5月~12月
					for(int tolCol = 4; tolCol < 12; tolCol++){
						XSSFCell tolColCell = totalRow.createCell(tolCol);
						tolColCell.setCellStyle(mainStyle);
						
						for(Map<String,Object> totalCountMap : totalCountList){
							int mon = Integer.parseInt(totalCountMap.get("MONTH").toString());
							
							if(act_year.equals(totalCountMap.get("YEAR").toString()) && mon == tolCol + 1){
								if(startIndex == index){
									//筆數
									tolColCell.setCellValue(totalCountMap.get("APPLY_COUNT").toString());
									
								} else if(startIndex + 1 == index){
									//點數
									tolColCell.setCellValue(totalCountMap.get("MON_TOL_POINTS").toString());
									
								} else if(startIndex + 2 == index){
									//份數
									tolColCell.setCellValue(totalCountMap.get("MON_TOL_NBR").toString());
									
								} else if(startIndex + 3 == index){
									//金額
									tolColCell.setCellValue(totalCountMap.get("MON_TOL_COSTS").toString());
								}
							}
						}
						
						if(startIndex + 4 == index){
							//總核發點數
							for(Map<String,Object> accPointsMap : accPointsList){
								int accMon = Integer.parseInt(accPointsMap.get("MONTH").toString());
								if(act_year.equals(accPointsMap.get("YEAR").toString()) && accMon == tolCol + 1){
									int monthly_appr_points = Integer.parseInt(accPointsMap.get("MONTHLY_APPR_POINTS").toString());
									accPoints += monthly_appr_points;
								}
							}
							if(accPoints > 0){
								tolColCell.setCellValue(accPoints);												
							}
						} else if(startIndex + 5 == index){
							//已兌換點數
							for(Map<String,Object> exchPointsMap : exchPointsList){
								int exchMon = Integer.parseInt(exchPointsMap.get("MONTH").toString());
								if(act_year.equals(exchPointsMap.get("YEAR").toString()) && exchMon == tolCol + 1){
									int monthly_exch_points = Integer.parseInt(exchPointsMap.get("MONTHLY_EXCH_POINTS").toString());
									exchPoints += monthly_exch_points;
								}
							}
							if(exchPoints > 0){
								tolColCell.setCellValue(exchPoints);												
							}
							
						} else if(startIndex + 6 == index){
							//未兌換點數
							if(tolCol == 4){
								tolColCell.setCellFormula("E11-E12");								
							} else if (tolCol == 5){
								tolColCell.setCellFormula("F11-F12");
							} else if (tolCol == 6){
								tolColCell.setCellFormula("G11-G12");
							} else if (tolCol == 7){
								tolColCell.setCellFormula("H11-H12");
							} else if (tolCol == 8){
								tolColCell.setCellFormula("I11-I12");
							} else if (tolCol == 9){
								tolColCell.setCellFormula("J11-J12");
							} else if (tolCol == 10){
								tolColCell.setCellFormula("K11-K12");
							} else if (tolCol == 11){
								tolColCell.setCellFormula("L11-L12");
							}
						}
					}
					
					//活動隔年1月~5月
					for(int tolCol = 12; tolCol < 17; tolCol++){
						XSSFCell tolColCell = totalRow.createCell(tolCol);
						tolColCell.setCellStyle(mainStyle);
						
						for(Map<String,Object> totalCountMap : totalCountList){
							int mon = Integer.parseInt(totalCountMap.get("MONTH").toString());
							if(act_next_year.equals(totalCountMap.get("YEAR").toString()) && mon == tolCol - 11){
								if(startIndex == index){
									//筆數
									tolColCell.setCellValue(totalCountMap.get("APPLY_COUNT").toString());
									
								} else if(startIndex + 1 == index){
									//點數
									tolColCell.setCellValue(totalCountMap.get("MON_TOL_POINTS").toString());
									
								} else if(startIndex + 2 == index){
									//份數
									tolColCell.setCellValue(totalCountMap.get("MON_TOL_NBR").toString());
									
								} else if(startIndex + 3 == index){
									//金額
									tolColCell.setCellValue(totalCountMap.get("MON_TOL_COSTS").toString());
								}
							}
						}
						
						if(startIndex + 4 == index){
							//累計核發點數
							for(Map<String,Object> accPointsMap : accPointsList){
								int accMon = Integer.parseInt(accPointsMap.get("MONTH").toString());
								if(act_next_year.equals(accPointsMap.get("YEAR").toString()) && accMon == tolCol - 11){
									int monthly_appr_points = Integer.parseInt(accPointsMap.get("MONTHLY_APPR_POINTS").toString());
									accPoints += monthly_appr_points;
								}
							}
							if(accPoints > 0){
								tolColCell.setCellValue(accPoints);												
							}
							
						} else if(startIndex + 5 == index){
							//已兌換點數
							for(Map<String,Object> exchPointsMap : exchPointsList){
								int exchMon = Integer.parseInt(exchPointsMap.get("MONTH").toString());
								if(act_next_year.equals(exchPointsMap.get("YEAR").toString()) && exchMon == tolCol - 11){
									int monthly_exch_points = Integer.parseInt(exchPointsMap.get("MONTHLY_EXCH_POINTS").toString());
									exchPoints += monthly_exch_points;
								}
							}
							if(exchPoints > 0){
								tolColCell.setCellValue(exchPoints);												
							}
							
						} else if(startIndex + 6 == index){
							//未兌換點數
							if(tolCol == 12){
								tolColCell.setCellFormula("M11-M12");								
							} else if (tolCol == 13){
								tolColCell.setCellFormula("N11-N12");
							} else if (tolCol == 14){
								tolColCell.setCellFormula("O11-O12");
							} else if (tolCol == 15){
								tolColCell.setCellFormula("P11-P12");
							} else if (tolCol == 16){
								tolColCell.setCellFormula("Q11-Q12");
							} 
						}
					}
					
					//活動隔年6月
					List<Map<String,Object>> junCountList = new ArrayList<Map<String,Object>>();
					dam = this.getDataAccessManager();
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append("SELECT TO_CHAR(DET.ORDER_DATE, 'YYYY') AS YEAR, TO_CHAR(DET.ORDER_DATE, 'MM') AS MONTH, ");
					sb.append("TO_CHAR(DET.ORDER_DATE, 'DD') AS DAY, ");
					sb.append("COUNT(DISTINCT DET.APPLY_SEQ) AS APPLY_COUNT, SUM(DET.APPLY_POINTS) AS MON_TOL_POINTS, ");
					sb.append("SUM(DET.APPLY_NUMBER) AS MON_TOL_NBR, SUM(DET.APPLY_NUMBER * GIFT.GIFT_COSTS) AS MON_TOL_COSTS ");
					sb.append("FROM TBMGM_APPLY_DETAIL DET LEFT JOIN TBMGM_APPLY_MAIN MAI ON DET.APPLY_SEQ = MAI.APPLY_SEQ ");
					sb.append("LEFT JOIN TBMGM_GIFT_INFO GIFT ON DET.GIFT_SEQ = GIFT.GIFT_SEQ ");
					sb.append("WHERE MAI.ACT_SEQ = :act_seq AND DET.ORDER_DATE IS NOT NULL ");
					sb.append("AND TO_CHAR(DET.ORDER_DATE, 'YYYY') = :act_next_year AND TO_CHAR(DET.ORDER_DATE, 'MM') = '06' ");
					sb.append("GROUP BY TO_CHAR(DET.ORDER_DATE, 'YYYY'), TO_CHAR(DET.ORDER_DATE, 'MM'), ");
					sb.append("TO_CHAR(DET.ORDER_DATE, 'DD') ORDER BY TO_CHAR(DET.ORDER_DATE, 'DD') ");
					
					queryCondition.setObject("act_seq", inputVO.getAct_seq());
					queryCondition.setObject("act_next_year", act_next_year.toString());
					queryCondition.setQueryString(sb.toString());
					junCountList = dam.exeQuery(queryCondition);
					
					XSSFCell junColCell1 = totalRow.createCell(17);
					junColCell1.setCellStyle(mainStyle);
					XSSFCell junColCell2 = totalRow.createCell(18);
					junColCell2.setCellStyle(mainStyle);
//					XSSFCell endColCell = totalRow.createCell(19);
//					endColCell.setCellStyle(mainStyle);
					
					if(junCountList.size() > 1){
						int mon_tol_points1 = 0;
						int mon_tol_points2 = 0;
						
						if(startIndex == index){
							//筆數
							junColCell1.setCellValue(junCountList.get(0).get("APPLY_COUNT").toString());
							junColCell2.setCellValue(junCountList.get(1).get("APPLY_COUNT").toString());
							
						} else if(startIndex + 1 == index){
							//點數
							junColCell1.setCellValue(junCountList.get(0).get("MON_TOL_POINTS").toString());
							junColCell2.setCellValue(junCountList.get(1).get("MON_TOL_POINTS").toString());
							
						} else if(startIndex + 2 == index){
							//份數
							junColCell1.setCellValue(junCountList.get(0).get("MON_TOL_NBR").toString());
							junColCell2.setCellValue(junCountList.get(1).get("MON_TOL_NBR").toString());
							
						} else if(startIndex + 3 == index){
							//金額
							junColCell1.setCellValue(junCountList.get(0).get("MON_TOL_COSTS").toString());
							junColCell2.setCellValue(junCountList.get(1).get("MON_TOL_COSTS").toString());
							
						} else if(startIndex + 4 == index){
							//累計核發點數
							for(Map<String,Object> accPointsMap : accPointsList){
								int accMon = Integer.parseInt(accPointsMap.get("MONTH").toString());
								if(act_next_year.equals(accPointsMap.get("YEAR").toString()) && accMon == 6){
									int monthly_appr_points = Integer.parseInt(accPointsMap.get("MONTHLY_APPR_POINTS").toString());
									accPoints += monthly_appr_points;
								}
							}
							if(accPoints > 0){
								junColCell1.setCellValue(accPoints);
								junColCell2.setCellValue(accPoints);												
							}
								
						} else if(startIndex + 5 == index){
							//已兌換點數
							for(Map<String,Object> exchPointsMap : exchPointsList){
								int exchMon = Integer.parseInt(exchPointsMap.get("MONTH").toString());
								if(act_next_year.equals(exchPointsMap.get("YEAR").toString()) && exchMon == 6){
									int monthly_exch_points = Integer.parseInt(exchPointsMap.get("MONTHLY_EXCH_POINTS").toString());
									exchPoints += monthly_exch_points;
								}
							}
							if(exchPoints > 0){
								junColCell1.setCellValue(exchPoints);
								junColCell2.setCellValue(exchPoints);										
							}
							
						} else if(startIndex + 6 == index){
							//未兌換點數
							junColCell1.setCellFormula("R11-R12");
							junColCell2.setCellFormula("S11-S12");
						}
					} else if (junCountList.size() == 1){
						
						if(startIndex == index){
							//筆數
							junColCell1.setCellValue(junCountList.get(0).get("APPLY_COUNT").toString());
							
						} else if(startIndex + 1 == index){
							//點數
							junColCell1.setCellValue(junCountList.get(0).get("MON_TOL_POINTS").toString());
							
						} else if(startIndex + 2 == index){
							//份數
							junColCell1.setCellValue(junCountList.get(0).get("MON_TOL_NBR").toString());
							
						} else if(startIndex + 3 == index){
							//金額
							junColCell1.setCellValue(junCountList.get(0).get("MON_TOL_COSTS").toString());
							
						} else if(startIndex + 4 == index){
							//累計核發點數
							for(Map<String,Object> accPointsMap : accPointsList){
								int accMon = Integer.parseInt(accPointsMap.get("MONTH").toString());
								if(act_next_year.equals(accPointsMap.get("YEAR").toString()) && accMon == 6){
									int monthly_appr_points = Integer.parseInt(accPointsMap.get("MONTHLY_APPR_POINTS").toString());
									accPoints += monthly_appr_points;
								}
							}
							if(accPoints > 0){
								junColCell1.setCellValue(accPoints);
								junColCell2.setCellValue(accPoints);												
							}
						} else if(startIndex + 5 == index){
							//已兌換點數
							for(Map<String,Object> exchPointsMap : exchPointsList){
								int exchMon = Integer.parseInt(exchPointsMap.get("MONTH").toString());
								if(act_next_year.equals(exchPointsMap.get("YEAR").toString()) && exchMon == 6){
									int monthly_exch_points = Integer.parseInt(exchPointsMap.get("MONTHLY_EXCH_POINTS").toString());
									exchPoints += monthly_exch_points;
								}
							}
							if(exchPoints > 0){
								junColCell1.setCellValue(exchPoints);
								junColCell2.setCellValue(exchPoints);										
							}
							
						} else if(startIndex + 6 == index){
							//未兌換點數
							junColCell1.setCellFormula("R11-R12");
							junColCell2.setCellFormula("S11-S12");
						}
					}
					
					index++;
				}
				sheet.addMergedRegion(new CellRangeAddress(startIndex, index - 1 , 0, 2));
				
				index++;
				index++;
				
				row = sheet.createRow(index);
				XSSFCell cell = row.createCell(0);
				cell.setCellValue("主管：");
				cell = row.createCell(8);
				cell.setCellValue("經辦：");
				
				String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
				File targetFile = new File(filePath, uuid);
				FileOutputStream fos = new FileOutputStream(targetFile);
				
				workbook.write(fos);
				workbook.close();
				notifyClientToDownloadFile("temp//" + uuid, fileName);

			} else {
				throw new APException("系統發生錯誤請洽系統管理員。");
			}
		} else {
			throw new APException("請選擇活動代碼。");
		}
	}
	
	/**
	* 檢查Map取出欄位是否為Null
	*/
	private String checkMap (Map map, String key) {
		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}