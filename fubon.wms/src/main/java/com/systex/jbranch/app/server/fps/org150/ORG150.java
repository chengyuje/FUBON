package com.systex.jbranch.app.server.fps.org150;

import java.io.FileOutputStream;
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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_RESIGN_MEMOVO;
import com.systex.jbranch.app.server.fps.ref130.REF130InputVO;
import com.systex.jbranch.app.server.fps.ref130.REF130OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org150")
@Scope("request")
public class ORG150 extends FubonWmsBizLogic {
	
	public DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void getResignMemberLst(Object body, IPrimitiveMap header) throws Exception {

		ORG150InputVO inputVO = (ORG150InputVO) body;
		ORG150OutputVO outputVO = new ORG150OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM VWORG_DEPT_BR ");
		sb.append("), ");
		sb.append("FINAL_EMP AS ( ");
		sb.append("  SELECT E.EMP_ID, E.ROLE_ID, E.START_TIME, E.END_TIME ");
		sb.append("  FROM TBPMS_EMPLOYEE_REC_N E ");
		sb.append("  INNER JOIN (SELECT EMP_ID,MAX(START_TIME) START_TIME FROM TBPMS_EMPLOYEE_REC_N GROUP BY EMP_ID) E1 ON E.EMP_ID = E1.EMP_ID AND E.START_TIME = E1.START_TIME ");
		sb.append(") ");
		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
		sb.append("       EMP_ID, EMP_NAME, ROLE_ID, TO_CHAR(JOB_RESIGN_DATE, 'yyyy-MM-dd') AS JOB_RESIGN_DATE, RESIGN_REASON, RESIGN_DESTINATION, DESTINATION_BANK_ID ");
		sb.append("FROM ( ");
		sb.append("  SELECT RC.DEPT_ID AS REGION_CENTER_ID, RC.DEPT_NAME AS REGION_CENTER_NAME, OP.DEPT_ID AS BRANCH_AREA_ID, OP.DEPT_NAME AS BRANCH_AREA_NAME, BR.DEPT_ID AS BRANCH_NBR, BR.DEPT_NAME AS BRANCH_NAME, ");
		sb.append("         MEM.EMP_ID, MEM.EMP_NAME, FE.ROLE_ID, MEMO.RESIGN_DATE AS JOB_RESIGN_DATE, MEMO.RESIGN_REASON, MEMO.RESIGN_DESTINATION, MEMO.DESTINATION_BANK_ID ");
		sb.append("  FROM TBORG_RESIGN_MEMO MEMO ");
		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON MEMO.EMP_ID = MEM.EMP_ID ");
		sb.append("  LEFT JOIN FINAL_EMP FE ON MEM.EMP_ID = FE.EMP_ID ");
		sb.append("  LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '50') BR ON MEM.DEPT_ID = BR.DEPT_ID ");
		sb.append("  LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '40') OP ON BR.PARENT_DEPT_ID = OP.DEPT_ID OR MEM.DEPT_ID = OP.DEPT_ID ");
		sb.append("  LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '30') RC ON OP.PARENT_DEPT_ID = RC.DEPT_ID OR MEM.DEPT_ID = RC.DEPT_ID ");
		sb.append(") ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND ROLE_ID IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ORG.RESGGN_ROLE') ");
//		sb.append("WITH BASE AS ( "); 
//		sb.append("  SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM VWORG_DEPT_BR "); 
//		sb.append(") "); 
//		sb.append("SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, "); 
//		sb.append("       EMP_ID, EMP_NAME, TO_CHAR(JOB_RESIGN_DATE, 'yyyy-MM-dd') AS JOB_RESIGN_DATE, RESIGN_REASON, RESIGN_DESTINATION, DESTINATION_BANK_ID "); 
//		sb.append("FROM ( "); 
//		sb.append("  SELECT RC.DEPT_ID AS REGION_CENTER_ID, RC.DEPT_NAME AS REGION_CENTER_NAME, OP.DEPT_ID AS BRANCH_AREA_ID, OP.DEPT_NAME AS BRANCH_AREA_NAME, BR.DEPT_ID AS BRANCH_NBR, BR.DEPT_NAME AS BRANCH_NAME, "); 
//		sb.append("         MEM.EMP_ID, MEM.EMP_NAME, MEM.JOB_RESIGN_DATE, MEMO.RESIGN_REASON, MEMO.RESIGN_DESTINATION, MEMO.DESTINATION_BANK_ID "); 
//		sb.append("  FROM TBORG_RESIGN_MEMO MEMO "); 
//		sb.append("  LEFT JOIN TBORG_MEMBER MEM ON MEMO.EMP_ID = MEM.EMP_ID "); 
//		sb.append("  LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '50') BR ON MEM.DEPT_ID = BR.DEPT_ID "); 
//		sb.append("  LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '40') OP ON BR.PARENT_DEPT_ID = OP.DEPT_ID OR MEM.DEPT_ID = OP.DEPT_ID "); 
//		sb.append("  LEFT JOIN (SELECT DEPT_ID, DEPT_NAME, PARENT_DEPT_ID, ORG_TYPE FROM BASE WHERE ORG_TYPE = '30') RC ON OP.PARENT_DEPT_ID = RC.DEPT_ID OR MEM.DEPT_ID = RC.DEPT_ID "); 
////		sb.append("  WHERE MEMO.RESIGN_HANDOVER = '01' ");
//		sb.append(") "); 
//		sb.append("WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND REGION_CENTER_ID = :regID "); //區域代碼
			queryCondition.setObject("regID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND REGION_CENTER_ID IN (:rcIdList) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
		
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND BRANCH_AREA_ID = :branID "); //營運區代碼
			queryCondition.setObject("branID", inputVO.getBranch_area_id());
		} else {
			sb.append("AND BRANCH_AREA_ID IN (:opIdList) ");
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND BRANCH_NBR = :branNbr "); //分行代碼
			queryCondition.setObject("branNbr", inputVO.getBranch_nbr());
		} else {
			sb.append("AND BRANCH_NBR IN (:brNbrList) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		if (inputVO.getEMP_ID().trim().length() > 0) {
			sb.append(" AND EMP_ID LIKE :empId ");
			queryCondition.setObject("empId", "%" + inputVO.getEMP_ID() + "%");
		}
		
		if (inputVO.getEMP_NAME().trim().length() > 0) {
			sb.append(" AND EMP_NAME LIKE :empName ");
			queryCondition.setObject("empName", "%" + inputVO.getEMP_NAME() + "%");
		}
		
		if (inputVO.getRESIGN_REASON().trim().length() > 0) {
			sb.append(" AND RESIGN_REASON = :rsReason ");
			queryCondition.setObject("rsReason", inputVO.getRESIGN_REASON());
		}
		
		if (inputVO.getRESIGN_DESTINATION().trim().length() > 0) {
			sb.append(" AND RESIGN_DESTINATION = :rsDes ");
			queryCondition.setObject("rsDes", inputVO.getRESIGN_DESTINATION());
		}
		
		if (inputVO.getDESTINATION_BANK_ID().trim().length() > 0) {
			sb.append(" AND DESTINATION_BANK_ID = :desId ");
			queryCondition.setObject("desId", inputVO.getDESTINATION_BANK_ID());
		}
		//日期區間
		if (inputVO.getDateS() != null){
			sb.append("AND TRUNC(JOB_RESIGN_DATE) >= TO_DATE( :dateS , 'YYYY-MM-DD') ");
			queryCondition.setObject("dateS", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.getDateS()));
		}
		if (inputVO.getDateE() != null){
			sb.append("AND TRUNC(JOB_RESIGN_DATE) <= TO_DATE( :dateE , 'YYYY-MM-DD') ");
			queryCondition.setObject("dateE", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.getDateE()));
		}
		
		
		queryCondition.setQueryString(sb.toString());
//		ResultIF resignMemberLst = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
//		int totalPage_i = resignMemberLst.getTotalPage(); // 分頁用
//		int totalRecord_i = resignMemberLst.getTotalRecord(); // 分頁用
//		outputVO.setResignMemberLst(resignMemberLst);// data
//		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
//		outputVO.setTotalPage(totalPage_i);// 總頁次
//		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		outputVO.setResignMemberLst(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	public void updateResignMemo(Object body, IPrimitiveMap header) throws Exception {
		
		ORG150InputVO inputVO = (ORG150InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_RESIGN_MEMOVO rsVO = (TBORG_RESIGN_MEMOVO) dam.findByPKey(TBORG_RESIGN_MEMOVO.TABLE_UID, inputVO.getEMP_ID());
		rsVO.setRESIGN_REASON(inputVO.getRESIGN_REASON());
		rsVO.setRESIGN_DESTINATION(inputVO.getRESIGN_DESTINATION());
		rsVO.setDESTINATION_BANK_ID(inputVO.getDESTINATION_BANK_ID());
		dam.update(rsVO);
		
		sendRtnObject(null);
	}
	
	public void export (Object body, IPrimitiveMap header) throws Exception {
		
		ORG150InputVO inputVO = (ORG150InputVO) body;
		ORG150OutputVO outputVO = new ORG150OutputVO();
		
		List<Map<String, String>> list = inputVO.getEXPORT_LST();
		
		String fileName = "理專離職_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("理專離職_" + sdfYYYYMMDD.format(new Date()));
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);
		
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
		
		String[] headerLine1 = {"業務處", "營運區", "分行", "員工編號", "姓名", "離職原因", "離職動向", "離職至同業", "最後上班日"};
		String[] mainLine    = {"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NAME", "EMP_ID", "EMP_NAME", "RESIGN_REASON", "RESIGN_DESTINATION", "DESTINATION_BANK_ID", "JOB_RESIGN_DATE"};

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

		Map<String, String> resignReasonMap = new XmlInfo().doGetVariable("ORG.RESIGN_REASON", FormatHelper.FORMAT_3);
		Map<String, String> resignDestinationMap = new XmlInfo().doGetVariable("ORG.RESIGN_DESTINATION", FormatHelper.FORMAT_3);
		Map<String, String> destinationBankIDMap = new XmlInfo().doGetVariable("ORG.DESTINATION_BANK_ID", FormatHelper.FORMAT_3);

		for (Map<String, String> map : list) {
			row = sheet.createRow(index);
			
			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				if (StringUtils.equals("RESIGN_REASON", mainLine[j])) {
					cell.setCellValue(resignReasonMap.get(checkIsNull(map, mainLine[j])));
				} else if (StringUtils.equals("RESIGN_DESTINATION", mainLine[j])) {
					cell.setCellValue(resignDestinationMap.get(checkIsNull(map, mainLine[j])));
				} else if (StringUtils.equals("DESTINATION_BANK_ID", mainLine[j])) {
					cell.setCellValue(destinationBankIDMap.get(checkIsNull(map, mainLine[j])));
				} else {
					cell.setCellValue(checkIsNull(map, mainLine[j]));
				}
			}

			index++;
		}
		
		workbook.write(new FileOutputStream(filePath));
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
