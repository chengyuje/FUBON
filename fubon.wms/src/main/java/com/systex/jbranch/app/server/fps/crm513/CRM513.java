package com.systex.jbranch.app.server.fps.crm513;

import java.io.FileOutputStream;
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

import com.google.gson.Gson;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.parse.JsonUtil;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.fubon.webservice.rs.SeniorCitizenClientRS;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2023/08/27
 * 
 */
@Component("crm513")
@Scope("request")
public class CRM513 extends FubonWmsBizLogic {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public DataAccessManager dam = null;
	
	private String apiParam = "SYS.SENIOR_CITIZEN_URL";
	
	public void query(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		CRM513InputVO inputVO = (CRM513InputVO) body;
		CRM513OutputVO outputVO = new CRM513OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		XmlInfo xmlinfo = new XmlInfo();
		Gson gson = JsonUtil.genDefaultGson();
		
		String apiName = "getOldCust_His_DTL";
		String url = xmlinfo.getVariable(apiParam, apiName, "F3");
		
		logger.info(apiName + " url:" + url);

		GenericMap inputGmap = new GenericMap();
		
		if (StringUtils.isNotEmpty(inputVO.getCustID())) {
			inputGmap.put("CUST_ID", inputVO.getCustID());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getBranchNbr())) {
			inputGmap.put("BRANCH_NBR", inputVO.getBranchNbr());
		}
		
		if (null != inputVO.getsDate()) {
			inputGmap.put("START_DATE", sdf.format(inputVO.getsDate()));
		}
		
		if (null != inputVO.geteDate()) {
			inputGmap.put("END_DATE", sdf.format(inputVO.geteDate()));
		}
		
		logger.info(apiName + " inputVO:" + gson.toJson(inputGmap.getParamMap()));

		List<Map<String, Object>> list = new SeniorCitizenClientRS().getList(url, inputGmap);
		
		if (StringUtils.equals((String) list.get(0).get("EMSGID"), "E005")) {
			throw new APException((String) list.get(0).get("EMSGTXT"));
		} else {
			sb = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("SELECT  BRANCH_NBR, BRANCH_NAME ");
			sb.append("FROM VWORG_DEFN_INFO ");
			sb.append("WHERE 1 = 1 ");
			
			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> branchList = dam.exeQuery(queryCondition);
			
			for (Map<String, Object> map : list) {
				logger.info(apiName + " return:" + map);

				for (Map<String, Object> braMap : branchList) {
					if (StringUtils.equals((String) map.get("CHG_DEPT_NAME"), (String) braMap.get("BRANCH_NBR"))) {
						map.put("CHG_DEPT_NAME", map.get("CHG_DEPT_NAME") + "-" + braMap.get("BRANCH_NAME"));
					}
				}
			}
			
			
			for (Map<String, Object> map : list) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT CUST_ID, CUST_NAME ");
				sb.append("FROM TBCRM_CUST_MAST ");
				sb.append("WHERE 1 = 1 ");
				sb.append("AND CUST_ID = :custID ");
				
				queryCondition.setQueryString(sb.toString());
				queryCondition.setObject("custID", map.get("CUST_ID"));
				
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> custList = dam.exeQuery(queryCondition);
				
				map.put("CUST_NAME", custList.size() > 0 ? custList.get(0).get("CUST_NAME") : "");
			}
			
			outputVO.setTradeList(list);
		}

		this.sendRtnObject(outputVO);
	}

	// 匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {

		CRM513InputVO inputVO = (CRM513InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "高齡客戶評估量表歷史記錄_" + sdf.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		List<Map<String, String>> reportList = inputVO.getTradeList();

		String[] headerLine = { "客戶姓名", "ID/統編", 
								"異動單位", "異動員編/姓名", "異動日期", 
				                "類別", "項目", "選項", "評估結果"};
		String[] mainLine  = { "CUST_NAME", "CUST_ID", 
								"CHG_DEPT_NAME", "CHG_CREATOR_NAME", "CHG_DATE", 
								"QUESTION_CLASS_NAME", "QUESTION_NAME_NAME", "OPTION", "RESULT"};

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("高齡客戶評估量表歷史記錄_" + sdf.format(new Date()));
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
		
		XSSFCellStyle answerStyle = wb.createCellStyle();
		answerStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		answerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		answerStyle.setBorderBottom((short) 1);
		answerStyle.setBorderTop((short) 1);
		answerStyle.setBorderLeft((short) 1);
		answerStyle.setBorderRight((short) 1);
		answerStyle.setWrapText(true);

		for (Map<String, String> map : reportList) {
			row = sheet.createRow(index);

			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				
				switch (mainLine[j]) {
					case "OPTION":
						cell.setCellStyle(answerStyle);
						String answerList = checkIsNull(map, "ANSWER_DESC").replaceAll(";", "/");
						answerList = (answerList.indexOf("/") > -1) ? (answerList.substring(0, answerList.length() - 1)).replaceAll("/", "\n") : answerList;
						
						cell.setCellValue(answerList.length() > 0 ? (StringUtils.isNotEmpty(checkIsNull(map, "QUESTION_DESCR")) ? checkIsNull(map, "QUESTION_DESCR") + "\n" : "") + answerList.substring(0, answerList.length()) : "");
						break;
					case "RESULT":
						cell.setCellStyle(answerStyle);
						String resultList = checkIsNull(map, "CUST_ANSWER_DESC").replaceAll(";", "/");
						resultList = (resultList.indexOf("/") > -1) ? (resultList.substring(0, resultList.length() - 1)).replaceAll("/", "\n") : resultList;
						
						cell.setCellValue(resultList.length() > 0 ? resultList.substring(0, resultList.length()) : "");
						break;
					default :
						cell.setCellStyle(mainStyle);
						cell.setCellValue(checkIsNull(map, mainLine[j]));
						break;
				}
				
			}

			index++;
		}

		wb.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		sendRtnObject(null);
	}

	// 檢查Map取出欄位是否為Null
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			if ("CUST_ID".equals(key)) {
				return DataFormat.getCustIdMaskForHighRisk(String.valueOf(map.get(key)));
			} else {
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}
}
