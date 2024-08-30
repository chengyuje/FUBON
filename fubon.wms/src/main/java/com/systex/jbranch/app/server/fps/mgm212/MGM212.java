package com.systex.jbranch.app.server.fps.mgm212;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
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
import com.systex.jbranch.app.common.fps.table.TBMGM_MGMVO;
import com.systex.jbranch.app.common.fps.table.TBMGM_MGM_EVIDENCEVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO;
import com.systex.jbranch.app.server.fps.mgm110.MGM110OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MGM212
 * 
 * @author Carley
 * @date 2018/05/11
 */
@Component("mgm212")
@Scope("request")
public class MGM212 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//查詢該活動中所有已核點之案件(含已達門檻及未達門檻)
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		MGM212InputVO inputVO = (MGM212InputVO) body;
		MGM212OutputVO outputVO = new MGM212OutputVO();
		if (StringUtils.isNotBlank(inputVO.getAct_seq())){
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT CUST.CUST_NAME, CUST.BRA_NBR, DEFN.BRANCH_NAME, EVI.EVIDENCE_NAME, MGM.* FROM ( ");
			sb.append("SELECT * FROM TBMGM_MGM WHERE ACT_SEQ = :act_seq ");
			sb.append("AND APPR_DATE IS NOT NULL ) MGM ");
			sb.append("LEFT JOIN TBCRM_CUST_MAST CUST ON MGM.MGM_CUST_ID = CUST.CUST_ID ");
			sb.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON CUST.BRA_NBR = DEFN.BRANCH_NBR ");
			sb.append("LEFT JOIN TBMGM_MGM_EVIDENCE EVI ON MGM.SEQ = EVI.SEQ ");
			sb.append("WHERE 1 = 1 ");
			queryCondition.setObject("act_seq", inputVO.getAct_seq());
			
			//推薦人ID
			if (StringUtils.isNotBlank(inputVO.getMgm_cust_id())){
				sb.append("AND MGM.MGM_CUST_ID = :mgm_cust_id ");
				queryCondition.setObject("mgm_cust_id", inputVO.getMgm_cust_id());
			}
			
			//案件序號
			if (StringUtils.isNotBlank(inputVO.getSeq())){
				sb.append("AND MGM.SEQ = :seq ");
				queryCondition.setObject("seq", inputVO.getSeq());
			}
			
			//點數類型
			if (StringUtils.isNotBlank(inputVO.getPoints_type())){
				sb.append("AND MGM.POINTS_TYPE = :points_type ");
				queryCondition.setObject("points_type", inputVO.getPoints_type());
			}
			
			//狀態
			if (StringUtils.isNotBlank(inputVO.getRelease_status())){
				if("Y".equals(inputVO.getRelease_status())){			//客戶點數已放行
					sb.append("AND MGM.RELEASE_YN = 'Y' ");					
				} else if("N".equals(inputVO.getRelease_status())) {	//客戶點數未放行
					sb.append("AND (MGM.RELEASE_YN <> 'Y' OR MGM.RELEASE_YN IS NULL) ");
				}
			}
			
			//分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())){
				sb.append("AND CUST.BRA_NBR = :bra_nbr ");
				queryCondition.setObject("bra_nbr", inputVO.getBranch_nbr());
			}
			
			sb.append("ORDER BY MGM.MGM_CUST_ID, MGM.POINTS_TYPE ");
			queryCondition.setQueryString(sb.toString());		
			outputVO.setResultList(dam.exeQuery(queryCondition));
			
		} else {
			throw new APException("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
		}
		
		this.sendRtnObject(outputVO);
	}
	
	//查詢某案件點數
	public void checkSeq (Object body, IPrimitiveMap header) throws JBranchException {
		MGM212InputVO inputVO = (MGM212InputVO) body;
		MGM212OutputVO outputVO = new MGM212OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		if (StringUtils.isNotBlank(inputVO.getSeq())){
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT SEQ, ACT_SEQ, MGM_CUST_ID, APPR_POINTS AS BE_EDIT_POINTS, ");
			sb.append("POINTS_TYPE, RELEASE_YN, POINTS_MODIFY_DATE ");
			sb.append("FROM TBMGM_MGM WHERE SEQ = :seq ");
			queryCondition.setObject("seq", inputVO.getSeq());
			
			queryCondition.setQueryString(sb.toString());
			resultList = dam.exeQuery(queryCondition);
			if(resultList.size() > 0){
//				1. [客戶點數已放行]的案件不可修改。
//				2. [客戶點數尚未放行]但已修改過的案件亦不可修改。
//				3. [達人加碼點數]不可修改。
				if("2".equals(resultList.get(0).get("POINTS_TYPE").toString())){
					throw new APException("案件序號：" + inputVO.getSeq() + " 為[達人加碼點數]，故不可修改點數。");
				}
				if(resultList.get(0).get("RELEASE_YN") != null && "Y".equals(resultList.get(0).get("RELEASE_YN").toString())){
					throw new APException("案件序號：" + inputVO.getSeq() + " 客戶點數已放行，故不可修改點數。");
				}
				if(null != resultList.get(0).get("POINTS_MODIFY_DATE")){
					throw new APException("案件序號：" + inputVO.getSeq() + " 為已修改點數案件，故不可再次修改點數。");
				}
				String act_seq = resultList.get(0).get("ACT_SEQ").toString();
				if(!act_seq.equals(inputVO.getAct_seq())){
					throw new APException("案件序號：" + inputVO.getSeq() + " 不屬於活動：" + inputVO.getAct_seq() + " 之案件。");
				}
			}
			
		} else {
			throw new APException("請輸入案件序號後查詢。");
		}
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	//點數修改
	public void saveModify (Object body, IPrimitiveMap header) throws Exception {
		MGM212InputVO inputVO = (MGM212InputVO) body;
		String seq = inputVO.getSeq();
		String appr_points_s = inputVO.getAppr_points();
		String modify_reason = inputVO.getModify_reason();
		if (StringUtils.isNotBlank(seq) && StringUtils.isNotBlank(appr_points_s)){
			dam = this.getDataAccessManager();
			TBMGM_MGMVO vo = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, seq);
			//修改TBMGM_MGM
			if(null != vo){
				BigDecimal appr_points = new BigDecimal(appr_points_s);
				BigDecimal be_edit_points = vo.getAPPR_POINTS();
				String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
				
				vo.setAPPR_POINTS(appr_points);										//核點點數
				vo.setBE_EDIT_POINTS(be_edit_points);								//修改前點數
				vo.setMODIFY_REASON(modify_reason);									//修改原因
				vo.setPOINTS_MODIFIER(loginID);										//點數修改者
				vo.setPOINTS_MODIFY_DATE(new Timestamp(new Date().getTime()));		//點數修改日期
				
				dam.update(vo);
				
				//上傳MGM點數修改憑證
				if (StringUtils.isNotBlank(inputVO.getEvidence_name())) {
					TBMGM_MGM_EVIDENCEVO evidenceVO = (TBMGM_MGM_EVIDENCEVO) dam.findByPKey(TBMGM_MGM_EVIDENCEVO.TABLE_UID, seq);
					String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
					String joinedPath = new File(tempPath, inputVO.getEvidence_name()).toString();
					Path path = Paths.get(joinedPath);
					byte[] evidence = Files.readAllBytes(path);
					
					if(null == evidenceVO){
						evidenceVO = new TBMGM_MGM_EVIDENCEVO();
						evidenceVO.setSEQ(seq);
						evidenceVO.setEVIDENCE_NAME(inputVO.getReal_evidence_name());
						evidenceVO.setEVIDENCE(ObjectUtil.byteArrToBlob(evidence));
						
						dam.create(evidenceVO);
						
					} else {
						evidenceVO.setSEQ(seq);
						evidenceVO.setEVIDENCE_NAME(inputVO.getReal_evidence_name());
						evidenceVO.setEVIDENCE(ObjectUtil.byteArrToBlob(evidence));
						
						dam.update(evidenceVO);
					}
				}
				
			} else {
				throw new APException("系統發生錯誤請洽系統管理員。");
			}
		} else {
			throw new APException("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
		}
		
		this.sendRtnObject(null);
	}
	
	//給點
	public void release (Object body, IPrimitiveMap header) throws JBranchException {
		MGM212InputVO inputVO = (MGM212InputVO) body;
		String[] seq_list = inputVO.getSeq_list();
		if(seq_list.length > 0){
			dam = this.getDataAccessManager();
			for(String seq : seq_list){
				TBMGM_MGMVO vo = (TBMGM_MGMVO) dam.findByPKey(TBMGM_MGMVO.TABLE_UID, seq);
				if(null != vo){
					vo.setRELEASE_YN("Y");										//是否已放行
					vo.setRELEASE_DATE(new Timestamp(new Date().getTime()));	//點數放行日期
					dam.update(vo);
				} else {
					throw new APException("系統發生錯誤請洽系統管理員。");
				}
			}
		} else {
			throw new APException("請勾選案件。");
		}
		this.sendRtnObject(null);
	}
	
	//列印備查簿
	public void download (Object body, IPrimitiveMap<?> header) throws Exception {
		MGM212InputVO inputVO  = (MGM212InputVO) body;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfSlash = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		//撈取所需資料
		List<Map<String, Object>> resultList = inputVO.getResultList();
		
		//相關資訊設定
		String fileName = String.format("點數放行下載%s.xlsx", sdf.format(new Date()));
		String uuid = UUID.randomUUID().toString();
		
		//建置Excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("點數放行下載" + sdf.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
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
//		SubjectStyle.setBorderRight((short) 1);
		SubjectStyle.setWrapText(true);
		SubjectStyle.setFont(font);
		
		XSSFFont dateFont = workbook.createFont();
		dateFont.setColor(HSSFColor.BLACK.index);
		dateFont.setBoldweight((short)Font.NORMAL);
		dateFont.setFontHeight((short)250);
		
		XSSFCellStyle DateStyle = workbook.createCellStyle();
		DateStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
		DateStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		DateStyle.setBorderBottom((short) 1);
		DateStyle.setBorderTop((short) 1);
//		DateStyle.setBorderLeft((short) 1);
		DateStyle.setBorderRight((short) 1);
		DateStyle.setWrapText(true);
		DateStyle.setFont(dateFont);

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

		Integer index = 0; // first row
		
		//Subject
		XSSFRow row = sheet.createRow(index);
		row.setHeight((short)800);
		XSSFCell sCell = row.createCell(0);
		sCell.setCellStyle(SubjectStyle);
		String act_name = inputVO.getAct_name();
		sCell.setCellValue(act_name);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
		
		XSSFCell sCell2 = row.createCell(10);
		sCell2.setCellStyle(DateStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 10, 12));
		sCell2.setCellValue("日期：" + sdfSlash.format(new Date()));
		
		index++;
		
		//header
		String[] headerLine1 = {"案件序號", "推薦人ID", "被推薦人ID", 
								"點數類型", "點數", "投保銷量(元)", 
								"分行", "追蹤期間(起)", "追蹤期間(迄)", 
								"狀態", "修改前點數", "點數修改者", "點數修改原因"};
		
		String[] mainLine    = {"SEQ", "MGM_CUST_ID", "BE_MGM_CUST_ID", 
								"POINTS_TYPE", "APPR_POINTS", "INS_SELL_VOL",
								"BRA_NBR", "MGM_START_DATE","MGM_END_DATE", 
								"RELEASE_YN", "BE_EDIT_POINTS", "POINTS_MODIFIER", "MODIFY_REASON"};
		
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
			Map<String, String> mgm_points_type = xmlInfo.doGetVariable("MGM.POINTS_TYPE", FormatHelper.FORMAT_3); 	//MGM活動點數類型
//			System.out.println(mgm_points_type);
			
			for (Map<String, Object> map : resultList) {
				row = sheet.createRow(index);

				if (map.size() > 0) {
					for (int j = 0; j < mainLine.length; j++) {
						XSSFCell cell = row.createCell(j);
						cell.setCellStyle(mainStyle);
						
						if (mainLine[j].indexOf("MGM_CUST_ID") >= 0){
							//推薦人ID
							String cust_id = "";
							if(map.get("MGM_CUST_ID") != null){
								String mgm_cust_id = map.get("MGM_CUST_ID").toString();
								String sub_first = mgm_cust_id.substring(0, 3);
								String sub_end = mgm_cust_id.substring(8);
								cust_id = sub_first + "***" + sub_end;
							}
							cell.setCellValue(cust_id);
							
						} else if(mainLine[j].indexOf("BE_MGM_CUST_ID") >= 0){
							//被推薦人ID
							String cust_id = "";
							if(map.get("BE_MGM_CUST_ID") != null){
								String be_mgm_cust_id = map.get("BE_MGM_CUST_ID").toString();
								String sub_first = be_mgm_cust_id.substring(0, 3);
								String sub_end = be_mgm_cust_id.substring(8);
								cust_id = sub_first + "***" + sub_end;
							}
							cell.setCellValue(cust_id);
							
						} else if(mainLine[j].indexOf("APPR_POINTS") >= 0){
							//點數
							int appr_points = 0;
							if(map.get("APPR_POINTS") != null){
								BigDecimal points = new BigDecimal(map.get("APPR_POINTS").toString());
								appr_points = points.intValue();
							}
							cell.setCellValue(appr_points);
							
						} else if(mainLine[j].indexOf("POINTS_TYPE") >= 0){
							//點數類型
							String pointsType = "";
							if (map.containsKey("POINTS_TYPE") && (map.get("POINTS_TYPE") != null)) {
								if (map.get("POINTS_TYPE") != null) {
									pointsType = mgm_points_type.get(map.get("POINTS_TYPE"));
								}
							}
							cell.setCellValue(pointsType);
						
						} else if(mainLine[j].indexOf("INS_SELL_VOL") >= 0) {
							Object obj = null;
							Double ins_sell_vol = 0.0;
							//投保銷量
							if(map.get("INS_SELL_VOL") != null && "1".equals(map.get("POINTS_TYPE"))){
								obj = map.get("INS_SELL_VOL");
								ins_sell_vol = (Double) obj;							
							}
							if("2".equals(map.get("POINTS_TYPE"))){
								cell.setCellValue("");
								
							} else {
								cell.setCellValue(ins_sell_vol);								
							}
							
						} else if(mainLine[j].indexOf("BRA_NBR") >= 0) {
							//分行
							String braNbr = "";
							if (map.containsKey("BRA_NBR") && map.get("BRA_NBR") != null) {
								braNbr = map.get("BRA_NBR").toString() + "-" + map.get("BRANCH_NAME").toString();
							 }
							cell.setCellValue(braNbr);
							
						} else if(mainLine[j].indexOf("ALL_REVIEW_DATE") >= 0) {
							//主管覆核日期
							String reviewDate = "";
							if (map.containsKey("ALL_REVIEW_DATE") && map.get("ALL_REVIEW_DATE") != null) {
								Date all_review_date = formatter.parse(map.get("ALL_REVIEW_DATE").toString());
								reviewDate = sdfSlash.format(all_review_date).toString();
							 }
							cell.setCellValue(reviewDate);
							
						} else if(mainLine[j].indexOf("MGM_START_DATE") >= 0) {
							//追蹤期間(起)
							String startDate = "";
							if (map.containsKey("MGM_START_DATE") && map.get("MGM_START_DATE") != null) {
								Date mgm_start_date = formatter.parse(map.get("MGM_START_DATE").toString());
								startDate = sdfSlash.format(mgm_start_date).toString();
							 }
							cell.setCellValue(startDate);
							
						} else if(mainLine[j].indexOf("MGM_END_DATE") >= 0) {
							//追蹤期間(迄)
							String endDate = "";
							if (map.containsKey("MGM_END_DATE") && map.get("MGM_END_DATE") != null) {
								Date mgm_end_date = formatter.parse(map.get("MGM_END_DATE").toString());
								endDate = sdfSlash.format(mgm_end_date).toString();
							 }
							cell.setCellValue(endDate);
							
						} else if(mainLine[j].indexOf("RELEASE_YN") >= 0) {
							//狀態
							String releaseYN = "";
							if (map.containsKey("RELEASE_YN")) {
								if(map.get("RELEASE_YN") != null){
									if("Y".equals(map.get("RELEASE_YN"))){
										releaseYN = "客戶點數已放行";										
									} else {
										releaseYN = "客戶點數尚未放行";
									}
								} else {
									releaseYN = "客戶點數尚未放行";
								}
							 }
							cell.setCellValue(releaseYN);
						} else if(mainLine[j].indexOf("BE_EDIT_POINTS") >= 0) {
							//修改前點數
							int be_edit_points = 0;
							if(map.get("BE_EDIT_POINTS") != null){
								BigDecimal be_edit_points_b = new BigDecimal(map.get("BE_EDIT_POINTS").toString());
								be_edit_points = be_edit_points_b.intValue();
								cell.setCellValue(be_edit_points);
								
							} else {
								cell.setCellValue("");
							}
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
	
	public void getEviView(Object body, IPrimitiveMap header) throws Exception {
		MGM110InputVO inputVO = (MGM110InputVO) body;
		MGM110OutputVO outputVO = new MGM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT EVIDENCE_NAME, EVIDENCE FROM TBMGM_MGM_EVIDENCE WHERE SEQ = :seq ");
		queryCondition.setObject("seq", inputVO.getSeq());
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		String filePath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String fileName = (String) list.get(0).get("EVIDENCE_NAME");
		int index = fileName.lastIndexOf(".");
		String data_name = fileName.substring(index); 
		
		String uuid = UUID.randomUUID().toString() + data_name;
		Blob blob = (Blob) list.get(0).get("EVIDENCE");
		int blobLength = (int) blob.length();  
		byte[] blobAsBytes = blob.getBytes(1, blobLength);
		
		File targetFile = new File(filePath, uuid);
		FileOutputStream fos = new FileOutputStream(targetFile);
	    fos.write(blobAsBytes);
	    fos.close();
//	    this.notifyClientToDownloadFile("temp//"+uuid, fileName);
	    outputVO.setPdfUrl("temp//"+uuid);
	    this.sendRtnObject(outputVO);
	}
	
}