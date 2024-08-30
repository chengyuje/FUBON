package com.systex.jbranch.app.server.fps.pms303;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :分行保險速報統計Controller <br>
 * Comments Name : PMS303.java<br>
 * Author :Kevin<br>
 * Date :2016年08月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :分行保險速報明細Controller <br>
 * Comments Name : PMS303.java<br>
 * Author :Kevin<br>
 * Date :2016年07月21日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms303")
@Scope("request")
public class PMS303 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS303.class);
	SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 匯出確認NULL
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if(String.valueOf(map.get(key)) != "null")
				return String.valueOf(map.get(key));
			else
				return "0";
		} else {
			return "0";
		}
	}
	
	private String checkIsNegative(Map map, String key, Boolean negativeFlag) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if(String.valueOf(map.get(key)) != "null" && !"0".equals(map.get(key).toString())) {
				if (negativeFlag) {
					return "-" + String.valueOf(map.get(key));
				} else {
					return String.valueOf(map.get(key));									
				}
			} else {
				return "0";				
			}
		} else {
			return "0";
		}
	}
	
	private String checkIsNull2(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if(String.valueOf(map.get(key))!="null")
			return String.valueOf(map.get(key)).substring(0,13) + ":" + String.valueOf(map.get(key)).substring(14,18);
			else
		    return "";
		} else {
			return "";
		}
	}
	
	private String numberIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			if(String.valueOf(map.get(key))!="null")
			return String.valueOf(map.get(key));
			else
		    return "0";
		} else {
			return "";
		}
	}
	
	/**
	 * 匯出EXCEIL
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, FileNotFoundException, IOException {
		PMS303InputVO inputVO = (PMS303InputVO) body;
		PMS303OutputVO outputVO = new PMS303OutputVO();
        List<Map<String, Object>> list = inputVO.getList();
        
        Calendar busiday = Calendar.getInstance();  
        busiday.setTime(new Date());  
        int day = busiday.get(Calendar.DATE);  
        
        String rate = inputVO.getRate();
        BigDecimal rrate = new BigDecimal(rate);
        String re = rrate.multiply(new BigDecimal(100)).toString() + "%";
  
        List<String> item = new ArrayList<String>();
//        item.add("REGION_CENTER_NAME"); 		// 營運中心
//        item.add("BRANCH_AREA_NAME"); 		// 營運區
//        item.add("BRANCH_NBR"); 				// 分行代號
//        item.add("BRANCH_NAME"); 				// 營業單位
//        item.add("GROUP_ID"); 				// 組別
        					    
        item.add("OT_DAY_CNT"); 				//躉繳(當日)件數
        item.add("OT_DAY_PREM_FULL"); 			//躉繳(當日)保費
        item.add("OT_DAY_FEE_FULL");			//躉繳(當日)手收100%
        item.add("OT_DAY_FEE_DIS");				//躉繳(當日)手收85%
        					    
        item.add("IV_DAY_CNT"); 				//躉繳(當日)件數
        item.add("IV_DAY_PREM_FULL"); 			//躉繳(當日)保費
        item.add("IV_DAY_FEE_FULL");			//躉繳(當日)手收100%
        item.add("IV_DAY_FEE_DIS");				//躉繳(當日)手收85%
        					    
        item.add("SY_DAY_CNT");					//短年期繳(當日)件數
        item.add("SY_DAY_PREM_FULL");			//短年期繳(當日)保費
        item.add("SY_DAY_FEE_FULL");			//短年期繳(當日)手收100%
        item.add("SY_DAY_FEE_DIS");				//短年期繳(當日)手收85%
        					    
        item.add("LY_DAY_CNT");					//長年期繳(當日)件數
        item.add("LY_DAY_PREM_FULL");			//長年期繳(當日)保費
        item.add("LY_DAY_FEE_FULL");			//長年期繳(當日)手收100%
        item.add("LY_DAY_FEE_DIS");				//長年期繳(當日)手收85%
        					    
        item.add("SUM_DAY_CNT");				//小計(當日)件數
        item.add("SUM_DAY_PREM_FULL");			//小計(當日)保費100%
        item.add("SUM_DAY_PREM_DIS");			//小計(當日)保費85%
        item.add("SUM_DAY_FEE_FULL");			//小計(當日)手收100%
        item.add("SUM_DAY_FEE_DIS");			//小計(當日)手收85%
        					    
        item.add("OT_MON_CNT");					//躉繳(當月)-應達成率"+re+"件數
        item.add("OT_MON_PREM_FULL");			//躉繳(當月)-應達成率"+re+"保費
        item.add("OT_MON_FEE_FULL");			//躉繳(當月)-應達成率"+re+"手收100%
        item.add("OT_MON_FEE_DIS");				//躉繳(當月)-應達成率"+re+"手收85%
        item.add("OT_MON_PREM_FULL_ARATE"); 	//躉繳(當月)-應達成率"+re+"保費已達成率
        item.add("OT_MON_FEE_FULL_ARATE");		//躉繳(當月)-應達成率"+re+"手收已達成率
        					    
        item.add("IV_MON_CNT");					//投資型(當月)-應達成率"+re+"件數
        item.add("IV_MON_PREM_FULL");			//投資型(當月)-應達成率"+re+"保費
        item.add("IV_MON_FEE_FULL");			//投資型(當月)-應達成率"+re+"手收100%
        item.add("IV_MON_FEE_DIS");				//投資型(當月)-應達成率"+re+"手收85%
        item.add("IV_MON_PREM_FULL_ARATE"); 	//投資型(當月)-應達成率"+re+"保費已達成率
        item.add("IV_MON_FEE_FULL_ARATE");		//投資型(當月)-應達成率"+re+"手收已達成率
        					    
        item.add("SY_MON_CNT");					//短年期繳(當月)-應達成率"+re+"件數
        item.add("SY_MON_PREM_FULL");			//短年期繳(當月)-應達成率"+re+"保費
        item.add("SY_MON_FEE_FULL");			//短年期繳(當月)-應達成率"+re+"手收100%
        item.add("SY_MON_FEE_DIS");				//短年期繳(當月)-應達成率"+re+"手收85%
        item.add("SY_MON_PREM_FULL_ARATE"); 	//短年期繳(當月)-應達成率"+re+"保費已達成率
        item.add("SY_MON_FEE_FULL_ARATE");  	//短年期繳(當月)-應達成率"+re+"手收已達成率
        					    
        item.add("LY_MON_CNT");					//長年期繳(當月)-應達成率"+re+"件數
        item.add("LY_MON_PREM_FULL");			//長年期繳(當月)-應達成率"+re+"保費
        item.add("LY_MON_FEE_FULL");			//長年期繳(當月)-應達成率"+re+"手收100%
        item.add("LY_MON_FEE_DIS");				//長年期繳(當月)-應達成率"+re+"手收85%
        item.add("LY_MON_PREM_FULL_ARATE"); 	//長年期繳(當月)-應達成率"+re+"保費已達成率
        item.add("LY_MON_FEE_FULL_ARATE");  	//長年期繳(當月)-應達成率"+re+"手收已達成率
        					    
        item.add("SYLY_MON_CNT");				//分期繳(當月)-應達成率"+re+"件數
        item.add("SYLY_MON_PREM_FULL");			//分期繳(當月)-應達成率"+re+"保費
        item.add("SYLY_MON_FEE_FULL");			//分期繳(當月)-應達成率"+re+"手收100%
        item.add("SYLY_MON_FEE_DIS");			//分期繳(當月)-應達成率"+re+"手收85%
        item.add("SYLY_MON_PREM_FULL_ARATE");	//分期繳(當月)-應達成率"+re+"保費已達成率
        item.add("SYLY_MON_FEE_FULL_ARATE"); 	//分期繳(當月)-應達成率"+re+"手收已達成率
        					    
        item.add("SUM_MON_CNT");				//小計(當月)-應達成率"+re+"件數
        item.add("SUM_MON_PREM_FULL");			//小計(當月)-應達成率"+re+"保費100%
        item.add("SUM_MON_PREM_DIS");			//小計(當月)-應達成率"+re+"保費85%
        item.add("SUM_MON_PREM_TAR");			//小計(當月)-應達成率"+re+"保費目標
        item.add("SUM_MON_PREM_FULL_ARATE");	//小計(當月)-應達成率"+re+"保費已達成率
        item.add("SUM_MON_PREM_FULL_RRATE");	//小計(當月)-應達成率"+re+"保費達成率
        item.add("SUM_MON_FEE_FULL");			//小計(當月)-應達成率"+re+"實際手收100%
        item.add("SUM_MON_FEE_DIS");			//小計(當月)-應達成率"+re+"實際手收85%
        item.add("SUM_MON_FEE_TAR");			//小計(當月)-應達成率"+re+"手收目標
        item.add("SUM_MON_FEE_FULL_ARATE"); 	//小計(當月)-應達成率"+re+"手收已達成率
        item.add("SUM_MON_FEE_FULL_RRATE"); 	//小計(當月)-應達成率"+re+"手收達成率
	
		String[] headerLine1 = {  
				"業務處", "營運區", "分行代號", "營業單位", "組別",
				"躉繳(當日)",
				"躉繳(當日)", 
				"躉繳(當日)",
				"躉繳(當日)",
				"投資型(當日)",
				"投資型(當日)",
				"投資型(當日)",
				"投資型(當日)",
				"短年期繳(當日)", 
				"短年期繳(當日)", 
				"短年期繳(當日)",
				"短年期繳(當日)",
				"長年期繳(當日)", 
				"長年期繳(當日)", 
				"長年期繳(當日)", 
				"長年期繳(當日)",
				"小計(當日)", 
				"小計(當日)",
				"小計(當日)",
				"小計(當日)",
				"小計(當日)",
				"躉繳(當月)-應達成率" + re,
				"躉繳(當月)-應達成率" + re,
				"躉繳(當月)-應達成率" + re,
				"躉繳(當月)-應達成率" + re,
				"躉繳(當月)-應達成率" + re,
				"躉繳(當月)-應達成率" + re,
				"投資型(當月)-應達成率" + re,
				"投資型(當月)-應達成率" + re,
				"投資型(當月)-應達成率" + re,
				"投資型(當月)-應達成率" + re,
				"投資型(當月)-應達成率" + re,
				"投資型(當月)-應達成率" + re,
				"短年期繳(當月)-應達成率" + re,
				"短年期繳(當月)-應達成率" + re,
				"短年期繳(當月)-應達成率" + re,
				"短年期繳(當月)-應達成率" + re,
				"短年期繳(當月)-應達成率" + re,
				"短年期繳(當月)-應達成率" + re,
				"長年期繳(當月)-應達成率" + re,
				"長年期繳(當月)-應達成率" + re,
				"長年期繳(當月)-應達成率" + re,
				"長年期繳(當月)-應達成率" + re,
				"長年期繳(當月)-應達成率" + re,
				"長年期繳(當月)-應達成率" + re,
				"分期繳(當月)-應達成率" + re,
				"分期繳(當月)-應達成率" + re,
				"分期繳(當月)-應達成率" + re,
				"分期繳(當月)-應達成率" + re,
				"分期繳(當月)-應達成率" + re,
				"分期繳(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re,
				"小計(當月)-應達成率" + re 
		};
		
//		//TBSYSPARAMETER
//		XmlInfo xmlInfo = new XmlInfo();
//		Map<String, Object> headmgrMap = xmlInfo.doGetVariable("PMS.INS_DISCOUNT", FormatHelper.FORMAT_2); //總行
		
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PARAM_CODE, PARAM_NAME_EDIT from TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.INS_DISCOUNT' ");
		condition.setQueryString(sql.toString());       
		
		List<Map<String,Object>> disList = dam.exeQuery(condition);
		
		int insDisInt = 0;
		int feeDisInt = 0;
//		if (headmgrMap.get("1") != null)
//			insDisInt = new BigDecimal(headmgrMap.get("1").toString()).multiply(new BigDecimal(100)).intValue();
//		if (headmgrMap.get("2") != null)
//			feeDisInt = new BigDecimal(headmgrMap.get("2").toString()).multiply(new BigDecimal(100)).intValue();
		
		for (Map<String,Object> map : disList) {
			if (map.get("PARAM_CODE") != null && "1".equals(map.get("PARAM_CODE")) && map.get("PARAM_NAME_EDIT") != null) {
//				System.out.println("========111========");
//				System.out.println(map.get("PARAM_NAME_EDIT"));
				insDisInt = new BigDecimal(map.get("PARAM_NAME_EDIT").toString()).multiply(new BigDecimal(100)).intValue();				
			}
			if (map.get("PARAM_CODE") != null && "2".equals(map.get("PARAM_CODE")) && map.get("PARAM_NAME_EDIT") != null) {
//				System.out.println("=======222=========");
//				System.out.println(map.get("PARAM_NAME_EDIT"));
				feeDisInt = new BigDecimal(map.get("PARAM_NAME_EDIT").toString()).multiply(new BigDecimal(100)).intValue();				
			}
		}
		
		String insDis = Integer.toString(insDisInt) + "%";
		String feeDis = Integer.toString(feeDisInt) + "%"; 		
		
		String[] headerLine2 = { 
				"", "", "", "", "", 
				"件數", "保費", "手收 100%", "手收 " + feeDis,
				"件數", "保費", "手收 100%", "手收 " + feeDis,
				"件數", "保費", "手收 100%", "手收 " + feeDis,
				"件數", "保費", "手收 100%", "手收 " + feeDis,
				"件數", "保費 100%", "保費 " + insDis, "手收 100%", "手收 " + feeDis,
				"件數", "保費", "手收 100%", "手收 " + feeDis, "保費已達成率", "手收已達成率",
				"件數", "保費", "手收 100%", "手收 " + feeDis, "保費已達成率", "手收已達成率",
				"件數", "保費", "手收 100%", "手收 " + feeDis, "保費已達成率", "手收已達成率",
				"件數", "保費", "手收 100%", "手收 " + feeDis, "保費已達成率", "手收已達成率",
				"件數", "保費", "手收 100%", "手收 " + feeDis, "保費已達成率", "手收已達成率",
				"件數", "保費 100%", "保費 " + insDis, "保費目標", "保費已達成率 ", "保費達成率 ", 
				"實際手收 100%", "實際手收 " + feeDis, "手收目標", "手收已達成率", "手收達成率"
		};
		
		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");	
		String fileName = "分行保險戰報統計_" + sdfYYYYMMDD.format(new Date())+ "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".xlsx";

		
//		 try {
			//String filePath = ServerHome+PlatformTemp + fileName;
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("分行統計報表_"+ sdfYYYYMMDD.format(new Date()));
			sheet.setDefaultColumnWidth(20);
			
			XSSFCellStyle style = workbook.createCellStyle();
			XSSFCellStyle STYLE = workbook.createCellStyle();

			style.setAlignment(XSSFCellStyle.ALIGN_CENTER); 				// 水平置中
			style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); 		// 垂直置中

			STYLE.setAlignment(XSSFCellStyle.ALIGN_CENTER); 				// 水平置中
			STYLE.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER); 		// 垂直置中
			STYLE.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			STYLE.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			STYLE.setBorderBottom((short) 1);
			STYLE.setBorderTop((short) 1);
			STYLE.setBorderLeft((short) 1);
			STYLE.setBorderRight((short) 1);
			Integer index = 0; // first row
			Integer startFlag = 0;
			Integer endFlag = 0;
			ArrayList<String> tempList = new ArrayList<String>(); // 比對用

			XSSFRow row = sheet.createRow(index);
			for (int i = 0; i < headerLine1.length; i++) {
				String headerLine = headerLine1[i];
				if (tempList.indexOf(headerLine) < 0) {
					tempList.add(headerLine);
					XSSFCell cell = row.createCell(i);
					cell.setCellStyle(STYLE);
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
			if (endFlag != 0) { // 最後的CELL若需要合併儲存格，則在這裡做
				sheet.addMergedRegion(new CellRangeAddress(0, 0, startFlag, endFlag)); // firstRow, endRow, firstColumn, endColumn
			}

			index++; // next row
			row = sheet.createRow(index);
			for (int i = 0; i < headerLine2.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(STYLE);
				cell.setCellValue(headerLine2[i]);

				if ("".equals(headerLine2[i])) {
					sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // firstRow, endRow, firstColumn, endColumn
				}
			}

			index++;

			String centerName = "";
			String areaName = "";
			String centerID = "";
			String areaID = "";
			if (list.size() > 0) {
				centerName = list.get(0).get("REGION_CENTER_NAME").toString();
				areaName = list.get(0).get("BRANCH_AREA_NAME").toString();
				centerID = list.get(0).get("REGION_CENTER_ID").toString();
				areaID = list.get(0).get("BRANCH_AREA_ID").toString();
			}
			
			List<Map<String, Object>> totalList = inputVO.getTotalList();
			List<Map<String, Object>> centerList = inputVO.getCenterList();
			List<Map<String, Object>> areaList = inputVO.getAreaList();
			
			int listCnt = 0;
			for (Map<String, Object> map : list) {
				listCnt++;
				
				Boolean changeArea = false;
				if (map.get("BRANCH_AREA_NAME") != null && !areaName.equals(map.get("BRANCH_AREA_NAME").toString())) {
					changeArea = true;
				}
				
				Boolean changeCenter = false;
				if (map.get("REGION_CENTER_NAME") != null && !centerName.equals(map.get("REGION_CENTER_NAME").toString())) {
					changeCenter = true;
				}
				
				//營運區變換
				if (changeArea) {
					for (Map<String, Object> areaMap : areaList) {
						if (areaMap.get("BRANCH_AREA_ID").toString().equals(areaID)){
							countAreaTotal(row, sheet, style, index, centerName, areaName, areaMap, item);							
						}
					}
					areaName = map.get("BRANCH_AREA_NAME").toString();
					areaID = map.get("BRANCH_AREA_ID").toString();
					index++;
				}
				
				//業務處變換
				if (changeCenter) {
					for (Map<String, Object> centerMap : centerList) {
						if (centerMap.get("REGION_CENTER_ID").toString().equals(centerID)){
							countCenterTotal(row, sheet, style, index, centerName, centerMap, item);							
						}
					}
					centerName = map.get("REGION_CENTER_NAME").toString();
					centerID = map.get("REGION_CENTER_ID").toString();
					index++;
				}
				
				row = sheet.createRow(index);
				int j = 0;
				XSSFCell cell = row.createCell(j);
				
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "REGION_CENTER_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_NBR"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "BRANCH_NAME"));
				cell = row.createCell(j++);
				cell.setCellStyle(style);
				cell.setCellValue(checkIsNull(map, "GROUP_ID"));
				
				for(String key : item) {
					cell = row.createCell(j++);
					cell.setCellStyle(style);
					
					BigDecimal value = new BigDecimal(0);
					if (map.get(key) != null) {
						value = new BigDecimal(map.get(key).toString());
					}
					if ("RATE".equals(key.substring(key.length()-4, key.length()))) {
						cell.setCellValue(value.toPlainString() + "%");
					} else {				
						cell.setCellValue(value.toPlainString());		
					}
				}
				
				index++;
				//最後一筆
				if (listCnt == list.size()) {
					// 區合計
					for (Map<String, Object> areaMap : areaList) {
						if (areaMap.get("BRANCH_AREA_ID").toString().equals(areaID)){
							countAreaTotal(row, sheet, style, index, centerName, areaName, areaMap, item);							
						}
					}
					index++;
					
					//處合計
					for (Map<String, Object> centerMap : centerList) {
						if (centerMap.get("REGION_CENTER_ID").toString().equals(centerID)){
							countCenterTotal(row, sheet, style, index, centerName, centerMap, item);							
						}
					}
					index++;
					
					// 全行合計
					countCenterTotal(row, sheet, style, index, "全行", totalList.get(0), item);
				}
			}
			
			String tempName = UUID.randomUUID().toString();
			File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
			workbook.write(new FileOutputStream(f)); //絕對路徑建檔
			notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName); //相對路徑取檔
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s",
//					StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
	}

	// FOR 區合計
	private void countAreaTotal(XSSFRow row, XSSFSheet sheet, XSSFCellStyle style, int index, String centerName, String areaName, Map<String, Object> areaMap, List<String> item) {
		row = sheet.createRow(index);
		int i = 0;
		XSSFCell areaTalCell = row.createCell(i);
		areaTalCell = row.createCell(i++);
		areaTalCell.setCellStyle(style);
		areaTalCell.setCellValue(centerName);
		
		areaTalCell = row.createCell(i++);
		areaTalCell.setCellStyle(style);
		areaTalCell.setCellValue(areaName + " 合計");
		areaTalCell = row.createCell(i++);
		areaTalCell = row.createCell(i++);
		areaTalCell = row.createCell(i++);
		
		for (String key : item){
			areaTalCell = row.createCell(i++);
			areaTalCell.setCellStyle(style);
			
			BigDecimal value = new BigDecimal(0);
			if (areaMap.get(key) != null) {
				value = new BigDecimal(areaMap.get(key).toString());
			}
			if ("RATE".equals(key.substring(key.length()-4, key.length()))) {
				areaTalCell.setCellValue(value.toPlainString() + "%");
			} else {	
				areaTalCell.setCellValue(value.toPlainString());
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(index, index, 1, 4)); // firstRow, endRow, firstColumn, endColumn
	}
	
	// FOR 處&全行合計
	private void countCenterTotal(XSSFRow row, XSSFSheet sheet, XSSFCellStyle style, int index, String centerName, Map<String, Object> centerMap, List<String> item) {
		row = sheet.createRow(index);
		int i = 0;
		XSSFCell centerTalCell = row.createCell(i);
		centerTalCell = row.createCell(i++);
		centerTalCell.setCellStyle(style);
		centerTalCell.setCellValue(centerName + " 合計");
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);
		centerTalCell = row.createCell(i++);
		
		for (String key : item){
			centerTalCell = row.createCell(i++);
			centerTalCell.setCellStyle(style);
			
			BigDecimal value = new BigDecimal(0);
			if (centerMap.get(key) != null) {
				value = new BigDecimal(centerMap.get(key).toString());
			}
			if ("RATE".equals(key.substring(key.length()-4, key.length()))) {
				centerTalCell.setCellValue(value.toPlainString() + "%");
			} else {
				centerTalCell.setCellValue(value.toPlainString());			
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(index, index, 0, 4)); // firstRow, endRow, firstColumn, endColumn
	}
	
	/** ==下載明細== **/
	public void querydetail(Object body,IPrimitiveMap header )throws JBranchException{
		try {
			PMS303InputVO inputVO = (PMS303InputVO) body;
			PMS303OutputVO outputVO = new PMS303OutputVO();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			String roleType = "";
			String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
			
			//取得查詢資料可視範圍
			PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
			PMS000InputVO pms000InputVO = new PMS000InputVO();
			pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));
			PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
			
			dam=this.getDataAccessManager();
			QueryConditionIF condition=dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql=new StringBuffer();
			
			sql.append(" select * from (select  TO_CHAR(INS_PROFIT_S,'yyyymmdd') AS INS_PROFIT_S , TO_CHAR(INS_PROFIT_E,'yyyymmdd') AS INS_PROFIT_E");
			sql.append(" from TBPMS_INS_ACCEPT_TIME A where TO_CHAR(INS_PROFIT_S,'yyyymmdd')<=:nowdata and TO_CHAR(INS_PROFIT_E,'yyyymmdd')>=:nowdata) A ,");
			sql.append(" TBPMS_INS_TXN B ");
			sql.append(" LEFT JOIN (SELECT ORG.REGION_CENTER_ID,ORG.REGION_CENTER_NAME,ORG.BRANCH_AREA_ID, ");
			sql.append(" ORG.BRANCH_AREA_NAME,ORG.BRANCH_NBR AS BR ");
			sql.append(" FROM TBPMS_ORG_REC_N ORG WHERE TO_DATE(:nowdata,'YYYYMMDD') BETWEEN ORG.START_TIME AND ORG.END_TIME ");
			sql.append(" AND ORG.ORG_TYPE='50' ");
			sql.append(" AND ORG.DEPT_ID>='200' AND ORG.DEPT_ID<='900' ");
			sql.append(" AND LENGTH(ORG.DEPT_ID)=3 ");
			sql.append(" ) ORG ");
			sql.append(" ON B.BRANCH_NBR = BR ");
			sql.append(" where B.TX_DATE BETWEEN A.INS_PROFIT_S AND A.INS_PROFIT_E  ");
			sql.append(" AND B.TX_DATE =:nowdata ");
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD ");
				condition.setObject("REGION_CENTER_IDDD","%" + inputVO.getRegion_center_id() + "%");
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and REGION_CENTER_ID IN (:REGION_CENTER_IDDD) ");
					condition.setObject("REGION_CENTER_IDDD", pms000outputVO.getV_regionList());
				}
			}
			
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD ");
				condition.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("  and BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
					condition.setObject("OP_AREA_IDDD", pms000outputVO.getV_areaList());
				}
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR ");
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr() + "%");
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {		
					sql.append("  and BRANCH_NBR IN (:BRANCH_NBRR) ");
					condition.setObject("BRANCH_NBRR", pms000outputVO.getV_branchList());
				}
			}
			//員編
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and AO_CODE LIKE :EMP_IDEE");
				condition.setObject("EMP_IDEE", "%" + inputVO.getAo_code() + "%");
			}
			sql.append(" ORDER BY B.BRANCH_NBR,B.TX_DATE,A.INS_PROFIT_S  ");
			
			condition.setQueryString(sql.toString());
			String s = sdf.format(inputVO.getsCreDate());            
			condition.setObject("nowdata",s );
			
			List<Map<String,Object>> list = dam.exeQuery(condition);
			
			 try {
				if(list.size()>0){
					 String.format("%1$,20d", -3123);	 
					 SimpleDateFormat sdf2=new SimpleDateFormat("YYYYMM");
					 
					 String fileName = "分行保險戰報統計下載明細_"+ sdf2.format(inputVO.getsCreDate())
								+ "-"+getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					 
					List listcsv = new ArrayList(); 
					 
					for(Map<String,Object> map : list)
					 {
					  //44 colunm
					  int i=0;	 
						String[] records=new String[44]; 
						
						Boolean negativeFlag = false;
						if (map.get("TX_TYPE") != null && "A".equals(map.get("TX_TYPE").toString())) {
							negativeFlag = true;
						}
						
						records[i]=dateFormat(map,"KEYIN_DATE");//鍵機日
	
						 records[++i]=dateFormat (map,"APPLY_DATE");//要保書申請日
	
						 records[++i]=checkIsNull(map,"OP_BATCH_NO");//送件批號
	
						 records[++i]= "\'" + checkIsNull(map,"INS_ID"); 	//保險文件編號
	
						 records[++i]=checkIsNull(map,"BRANCH_NBR");//分行代碼
	
						 records[++i]=checkIsNull(map,"BRANCH_NAME");//分行名稱
	
						 records[++i]= "\'" + checkIsNull(map,"RECRUIT_ID");//招攬人員編
	
						 records[++i]=checkIsNull(map,"RECRUIT_IDNBR");//招攬人ID
	
						 records[++i]=checkIsNull(map,"RECRUIT_NAME");//招攬人姓名
	
						 records[++i]=checkIsNull(map,"AO_CODE");//AO Code
	
						 records[++i]=checkIsNull(map,"INSURED_NAME");//被保險人姓名
	
						 records[++i]= "\'" + checkIsNull(map,"INSURED_ID");//被保險人ID
	
						 records[++i]=checkIsNull(map,"PROPOSER_NAME");//要保人姓名
	
						 records[++i]= "\'" + checkIsNull(map,"CUST_ID");//要保人ID
	
						 records[++i]=checkIsNull(map,"PRD_ID");//險種代碼
	
						 records[++i]=checkIsNull(map,"PRD_NAME");//險種名稱
	
						 records[++i]=checkIsNull(map,"STATUS");//要保文件簽收狀態
	
						 records[++i]=checkIsNull(map,"PRD_TYPE");//保險產品類型
	
						 records[++i]=checkIsNull(map,"PRD_ANNUAL");//產品年期
	
						 records[++i]=checkIsNull(map,"PAY_TYPE");//躉繳/分期繳
	
						 records[++i]=checkIsNull(map,"MOP2");//分期繳別
	
						 records[++i]=checkIsNull(map,"SPECIAL_CONDITION");//特殊條件
	
						 records[++i]=checkIsNull(map,"CURR_CD");//幣別
	
						 records[++i]=checkIsNull(map,"EXCH_RATE");//參考匯率
	
//						 records[++i] = checkIsNegative(map, "REAL_PREMIUM", negativeFlag);//實收保費
						 records[++i] = checkIsNull(map, "REAL_PREMIUM");//實收保費
	
						 records[++i]=checkIsNull(map,"BASE_PREMIUM");//基本保費
	//					 System.out.println("適配日期 "+checkIsNull2(map,"MATCH_DATE"));
	
						 records[++i]=checkIsNull2(map,"MATCH_DATE");//適配日期
	
						 records[++i]=checkIsNull(map,"REF_CON_ID");//轉介編號
	
						 records[++i]=checkIsNull(map,"REF_EMP_ID");//轉介人員編
	
						 records[++i]=checkIsNull(map,"REF_EMP_NAME");//轉介人姓名
	
						 records[++i]=checkIsNull(map,"REG_TYPE");//要保書類型
	
						 records[++i]=checkIsNull(map,"WRITE_REASON");//使用手寫要保書原因
	
						 records[++i]=checkIsNull(map,"WRITE_REASON_OTH");//原因描述
	
						 records[++i]=checkIsNull(map,"QC_ANC_DOC");//要保文件內是否有檢附保險通報單
	//					 System.out.println("簽署人簽署時間 "+checkIsNull2(map,"SIGN_DATE"));
						 
						 records[++i]=checkIsNull2(map,"SIGN_DATE");//簽署人簽署時間
	//					 System.out.println("總行行政助理處理時間 "+checkIsNull2(map,"AFT_SIGN_DATE"));
	
						 records[++i]=checkIsNull2(map,"AFT_SIGN_DATE");//總行行政助理處理時間
	
						 records[++i]=checkIsNull(map,"INS_RCV_DATE");//人壽簽收時間
	
						 records[++i]=checkIsNull(map,"INS_RCV_OPRID");//簽收人
	
						 records[++i]=checkIsNull(map,"REMARK_BANK");//備註
	
//						 records[++i] = checkIsNegative(map, "ACT_FEE", negativeFlag);		//實際收益(台幣/非年化)
						 records[++i] = checkIsNull(map, "ACT_FEE");
						 
//						 records[++i] = checkIsNegative(map, "CNR_FEE", negativeFlag);		//CNR收益(台幣/非年化)
						 records[++i] = checkIsNull(map, "CNR_FEE");
					
						 records[++i]=checkIsNull(map,"TX_DATE");//速報日
	
//						 records[++i] = checkIsNegative(map, "ANNU_PREMIUM", negativeFlag);	//年化保費(台幣)
						 records[++i] = checkIsNull(map, "ANNU_PREMIUM");
	
//						 records[++i] = checkIsNegative(map, "ANNU_ACT_FEE", negativeFlag);	//年化收益(台幣)
						 records[++i] = checkIsNull(map, "ANNU_ACT_FEE");
	
						 listcsv.add(records);
						 
					 }
					// header
						String[] csvHeader = new String[44];
						int j = 0;
						csvHeader[j]="鍵機日";
						csvHeader[++j]="要保書申請日";
						csvHeader[++j]="送件批號";
						csvHeader[++j]="保險文件編號";
						csvHeader[++j]="分行代碼";
						csvHeader[++j]="分行名稱";
						csvHeader[++j]="招攬人員編";
						csvHeader[++j]="招攬人ID";
						csvHeader[++j]="招攬人姓名";
						csvHeader[++j]="AO Code";
						csvHeader[++j]="被保險人姓名";
						csvHeader[++j]="被保險人ID";
						csvHeader[++j]="要保人姓名";
						csvHeader[++j]="要保人ID";
						csvHeader[++j]="險種代碼";
						csvHeader[++j]="險種名稱";
						csvHeader[++j]="要保文件簽收狀態";
						csvHeader[++j]="保險產品類型	  (1:儲蓄、2:投資、3:保障)";
						csvHeader[++j]="產品年期";
						csvHeader[++j]="躉繳/分期繳  	(1:躉繳、2:短年繳、3:分期繳)";
						csvHeader[++j]="分期繳別	  (A:年繳、D:躉繳、M:月繳、Q:季繳、S:半年繳)";
						csvHeader[++j]="特殊條件";
						csvHeader[++j]="幣別";
						csvHeader[++j]="參考匯率";
						csvHeader[++j]="實收保費";
						csvHeader[++j]="基本保費";
						csvHeader[++j]="適配日期";
						csvHeader[++j]="轉介編號";
						csvHeader[++j]="轉介人員編";
						csvHeader[++j]="轉介人姓名";
						csvHeader[++j]="要保書類型";
						csvHeader[++j]="使用手寫要保書原因";
						csvHeader[++j]="原因描述";
						csvHeader[++j]="要保文件內是否有檢附保險通報單";
						csvHeader[++j]="簽署人簽署時間";
						csvHeader[++j]="總行行政助理處理時間";
						csvHeader[++j]="人壽簽收時間";
						csvHeader[++j]="簽收人";
						csvHeader[++j]="備註";
						csvHeader[++j]="實際收益(台幣/非年化)";
						csvHeader[++j]="CNR收益(台幣/非年化)";
						csvHeader[++j]="戰報日";
						csvHeader[++j]="年化保費(台幣)";
						csvHeader[++j]="年化收益(台幣)";
						
						CSVUtil csv = new CSVUtil();
				        csv.setHeader(csvHeader);
					    csv.addRecordList(listcsv); 
					    String url=csv.generateCSV();
					    notifyClientToDownloadFile(url, fileName);    
					     
				 }else{
					 outputVO.setResultList(list);
					 this.sendRtnObject(outputVO);
				 }
			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	//查RRATE
	private BigDecimal getRrate(String createDate) throws DAOException, JBranchException {
		BigDecimal rrate = new BigDecimal(0);
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();	

		sql.append(" SELECT ");
		sql.append(" ROUND(PABTH_UTIL.FC_getTwoDateDiff(t.INS_PROFIT_S, TO_DATE(:report_date,'YYYYMMDD')) ");
		sql.append(" /PABTH_UTIL.FC_getTwoDateDiff(t.INS_PROFIT_S,t.INS_PROFIT_E),2) AS RRATE ");
		sql.append(" FROM ");
		sql.append(" (select distinct INS_PROFIT_S,INS_PROFIT_E ");
		sql.append(" from TBPMS_INS_ACCEPT_TIME WHERE TO_DATE(:report_date,'YYYYMMDD') BETWEEN INS_PROFIT_S AND INS_PROFIT_E)T  ");
		
		// 進行轉換
		condition.setObject("report_date", createDate);
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		
		if (list.size() > 0 && list.get(0).get("RRATE") != null){
			Map<String,Object> map = list.get(0);
			rrate = new BigDecimal(map.get("RRATE").toString()); 
		}
		return rrate;
	}
	
	private List<Map<String, Object>> getTotal(DataAccessManager dam, String type, float RRATE, String DATA_DATEE, PMS303InputVO inputVO) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(DATA_DATEE);
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		QueryConditionIF condition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();	
		
		if ("total".equals(type)){
			sql2.append(" SELECT ");
		} else if ("center".equals(type)) {
			sql2.append(" SELECT   D.REGION_CENTER_ID, ");
		} else if ("area".equals(type)) {
			sql2.append(" SELECT   D.BRANCH_AREA_ID, ");
		}
		
		sql2.append("          NVL(SUM(OT_DAY_CNT), 0) AS OT_DAY_CNT,               NVL(SUM(OT_DAY_PREM_FULL), 0) AS OT_DAY_PREM_FULL, ");
		sql2.append("          NVL(SUM(OT_DAY_FEE_FULL), 0) AS OT_DAY_FEE_FULL,     NVL(SUM(OT_DAY_FEE_DIS), 0) AS OT_DAY_FEE_DIS, ");
		sql2.append("          NVL(SUM(IV_DAY_CNT), 0) AS IV_DAY_CNT,               NVL(SUM(IV_DAY_PREM_FULL), 0) AS IV_DAY_PREM_FULL,  ");
		sql2.append("          NVL(SUM(IV_DAY_FEE_FULL), 0) AS IV_DAY_FEE_FULL,     NVL(SUM(IV_DAY_FEE_DIS), 0) AS IV_DAY_FEE_DIS, ");
		sql2.append("          NVL(SUM(SY_DAY_CNT), 0) AS SY_DAY_CNT,               NVL(SUM(SY_DAY_PREM_FULL), 0) AS SY_DAY_PREM_FULL,  ");
		sql2.append("          NVL(SUM(SY_DAY_FEE_FULL), 0) AS SY_DAY_FEE_FULL,     NVL(SUM(SY_DAY_FEE_DIS), 0) AS SY_DAY_FEE_DIS, ");
		sql2.append("          NVL(SUM(LY_DAY_CNT), 0) AS LY_DAY_CNT,               NVL(SUM(LY_DAY_PREM_FULL), 0) AS LY_DAY_PREM_FULL, ");
		sql2.append("          NVL(SUM(LY_DAY_FEE_FULL), 0) AS LY_DAY_FEE_FULL,     NVL(SUM(LY_DAY_FEE_DIS), 0) AS LY_DAY_FEE_DIS, ");
		sql2.append("          NVL(SUM(SUM_DAY_CNT), 0) AS SUM_DAY_CNT,             NVL(SUM(SUM_DAY_PREM_FULL), 0) AS SUM_DAY_PREM_FULL,   SUM(NVL(SUM_DAY_PREM_DIS, 0)) AS SUM_DAY_PREM_DIS, ");
		sql2.append("          NVL(SUM(SUM_DAY_FEE_FULL), 0) AS SUM_DAY_FEE_FULL,   NVL(SUM(SUM_DAY_FEE_DIS), 0) AS SUM_DAY_FEE_DIS, ");
		sql2.append("          NVL(SUM(OT_MON_CNT), 0) AS OT_MON_CNT,               NVL(SUM(OT_MON_PREM_FULL), 0) AS OT_MON_PREM_FULL, ");
		sql2.append("          NVL(SUM(OT_MON_FEE_FULL), 0) AS OT_MON_FEE_FULL,     NVL(SUM(OT_MON_FEE_DIS), 0) AS OT_MON_FEE_DIS, ");
		sql2.append("          NVL(ROUND(( SUM(OT_MON_PREM_FULL)/SUM(OT_TAR_AMT))*100,2),0) AS OT_MON_PREM_FULL_ARATE, ");
		sql2.append("          NVL(ROUND(( SUM(OT_MON_FEE_FULL)/SUM(OT_TAR_FEE))*100,2),0) AS OT_MON_FEE_FULL_ARATE, ");
		sql2.append("          NVL(SUM(IV_MON_CNT), 0) AS IV_MON_CNT,               NVL(SUM(IV_MON_PREM_FULL), 0) AS IV_MON_PREM_FULL,     SUM(NVL(IV_MON_PREM_DIS, 0)) AS IV_MON_PREM_DIS, ");
		sql2.append("          NVL(SUM(IV_MON_FEE_FULL), 0) AS IV_MON_FEE_FULL,     NVL(SUM(IV_MON_FEE_DIS), 0) AS IV_MON_FEE_DIS, ");
		sql2.append("          NVL(ROUND(( SUM(IV_MON_PREM_DIS)/SUM(IV_TAR_AMT))*100,2),0) AS IV_MON_PREM_FULL_ARATE, ");
		sql2.append("          NVL(ROUND(( SUM(IV_MON_FEE_FULL)/SUM(IV_TAR_FEE))*100,2),0) AS IV_MON_FEE_FULL_ARATE, ");
		sql2.append("          NVL(SUM(SY_MON_CNT), 0) AS SY_MON_CNT,               NVL(SUM(SY_MON_PREM_FULL), 0) AS SY_MON_PREM_FULL,     SUM(NVL(SY_MON_PREM_DIS, 0)) AS SY_MON_PREM_DIS, ");
		sql2.append("          NVL(SUM(SY_MON_FEE_FULL), 0) AS SY_MON_FEE_FULL,     NVL(SUM(SY_MON_FEE_DIS), 0) AS SY_MON_FEE_DIS, ");
		sql2.append("          NVL(ROUND(( SUM(SY_MON_PREM_DIS)/SUM(SY_TAR_AMT))*100,2),0) AS SY_MON_PREM_FULL_ARATE, ");
		sql2.append("          NVL(ROUND(( SUM(SY_MON_FEE_FULL)/SUM(SY_TAR_FEE))*100,2),0) AS SY_MON_FEE_FULL_ARATE, ");
		sql2.append("          NVL(SUM(LY_MON_CNT), 0) AS LY_MON_CNT,               NVL(SUM(LY_MON_PREM_FULL), 0) AS LY_MON_PREM_FULL,     SUM(NVL(LY_MON_PREM_DIS, 0)) AS LY_MON_PREM_DIS, ");
		sql2.append("          NVL(SUM(LY_MON_FEE_FULL), 0) AS LY_MON_FEE_FULL,     NVL(SUM(LY_MON_FEE_DIS), 0) AS LY_MON_FEE_DIS, ");
		sql2.append("          NVL(ROUND(( SUM(LY_MON_PREM_DIS)/SUM(LY_TAR_AMT))*100,2),0) AS LY_MON_PREM_FULL_ARATE, ");
		sql2.append("          NVL(ROUND(( SUM(LY_MON_FEE_FULL)/SUM(LY_TAR_FEE))*100,2),0) AS LY_MON_FEE_FULL_ARATE, ");
		sql2.append("          NVL(SUM(SYLY_MON_CNT), 0) AS SYLY_MON_CNT,           NVL(SUM(SYLY_MON_PREM_FULL), 0) AS SYLY_MON_PREM_FULL, SUM(NVL(SYLY_MON_PREM_DIS, 0)) AS SYLY_MON_PREM_DIS, "); 
		sql2.append("          NVL(SUM(SYLY_MON_FEE_FULL), 0) AS SYLY_MON_FEE_FULL, NVL(SUM(SYLY_MON_FEE_DIS), 0) AS SYLY_MON_FEE_DIS, ");
		sql2.append("          NVL(ROUND(( SUM(SYLY_MON_PREM_DIS)/(SUM(SY_TAR_AMT)+SUM(LY_TAR_AMT)))*100,2),0) AS SYLY_MON_PREM_FULL_ARATE, ");
		sql2.append("          NVL(ROUND(( SUM(SYLY_MON_FEE_FULL)/(SUM(SY_TAR_FEE)+SUM(LY_TAR_FEE)))*100,2),0) AS SYLY_MON_FEE_FULL_ARATE, ");
		sql2.append("          NVL(SUM(SUM_MON_CNT), 0) AS SUM_MON_CNT,             NVL(SUM(SUM_MON_PREM_FULL), 0) AS SUM_MON_PREM_FULL,   SUM(NVL(SUM_MON_PREM_DIS, 0)) AS SUM_MON_PREM_DIS, ");
		sql2.append("          NVL(SUM(SUM_MON_FEE_FULL), 0) AS SUM_MON_FEE_FULL,   NVL(SUM(SUM_MON_FEE_DIS), 0) AS SUM_MON_FEE_DIS, ");
		sql2.append("          NVL(SUM(SUM_MON_FEE_TAR), 0) AS SUM_MON_FEE_TAR, NVL(SUM(SUM_MON_PREM_TAR), 0) AS SUM_MON_PREM_TAR, ");
		sql2.append("          NVL(ROUND((SUM(SUM_MON_PREM_DIS)/(SUM(IV_TAR_AMT)+SUM(OT_TAR_AMT)+SUM(SY_TAR_AMT)+SUM(LY_TAR_AMT)))*100,2), 0) AS SUM_MON_PREM_FULL_ARATE, ");
		sql2.append("          NVL(ROUND(100*((SUM(SUM_MON_PREM_DIS)/(SUM(IV_TAR_AMT)+SUM(OT_TAR_AMT)+SUM(SY_TAR_AMT)+SUM(LY_TAR_AMT)))/ :MTD_RRATE),2), 0) AS SUM_MON_PREM_FULL_RRATE, ");
		sql2.append("          NVL(ROUND((SUM(SUM_MON_FEE_FULL)/(SUM(IV_TAR_FEE)+SUM(OT_TAR_FEE)+SUM(SY_TAR_FEE)+SUM(LY_TAR_FEE)))*100,2), 0) AS SUM_MON_FEE_FULL_ARATE, ");
		sql2.append("          NVL(ROUND(100*((SUM(SUM_MON_FEE_FULL)/(SUM(IV_TAR_FEE)+SUM(OT_TAR_FEE)+SUM(SY_TAR_FEE)+SUM(LY_TAR_FEE)))/ :MTD_RRATE),2), 0) AS SUM_MON_FEE_FULL_RRATE ");
		sql2.append("     FROM TBPMS_BR_DAY_INS D ");
		sql2.append("     LEFT JOIN TBPMS_INS_TARGET_SET TA ");
		sql2.append("     ON TA.BRANCH_NBR = D.BRANCH_NBR AND TA.YEARMON = D.YEARMON ");
		sql2.append("     WHERE D.DATA_DATE = :DATA_DATEE ");
		
		condition2.setObject("MTD_RRATE", RRATE);
		condition2.setObject("DATA_DATEE", DATA_DATEE);
		
		if ("center".equals(type)) {
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql2.append(" 		and D.REGION_CENTER_ID = :REGION_CENTER_IDDD ");
				condition2.setObject("REGION_CENTER_IDDD", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql2.append("and D.REGION_CENTER_ID IN (:REGION_CENTER_IDDD) ");
					condition2.setObject("REGION_CENTER_IDDD", pms000outputVO.getV_regionList());
				}
			}
			sql2.append(" GROUP BY D.REGION_CENTER_ID ");	
			sql2.append(" ORDER BY 1 ASC ");
			
		} else if ("area".equals(type)) {
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql2.append(" 	 and D.BRANCH_AREA_ID = :OP_AREA_IDDD ");
				condition2.setObject("OP_AREA_IDDD", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql2.append("  and D.BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
					condition2.setObject("OP_AREA_IDDD", pms000outputVO.getV_areaList());
				}else if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
					sql2.append(" and D.BRANCH_AREA_ID LIKE :OP_AREA_IDDD ");
					condition2.setObject("OP_AREA_IDDD", "%" + inputVO.getRegion_center_id() + "%");
				}
			}
			sql2.append(" GROUP BY D.BRANCH_AREA_ID ");	
			sql2.append(" ORDER BY 1 ASC ");
		}
		
		condition2.setQueryString(sql2.toString());
		resultList = dam.exeQuery(condition2);
		
		return resultList;
	}

	/** ==主資料查詢== 
	 * @throws ParseException **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS303InputVO inputVO = (PMS303InputVO) body;
		PMS303OutputVO outputVO = new PMS303OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		String roleType = "";
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(sdf.format(inputVO.getsCreDate()));
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		StringBuffer sql = new StringBuffer();
		try {
			//==主查詢==
			sql.append("SELECT ROWNUM AS NUM,T.*, ");
			sql.append(" 	   IV_TAR_AMT, IV_TAR_FEE, OT_TAR_AMT, OT_TAR_FEE, ");
			sql.append("       SY_TAR_AMT, SY_TAR_FEE, LY_TAR_AMT, LY_TAR_FEE ");
			sql.append("FROM (select * from TBPMS_BR_DAY_INS WHERE 1=1 ");
			//==主查詢條件==
			//區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD ");
				condition.setObject("REGION_CENTER_IDDD", "%" + inputVO.getRegion_center_id() + "%");
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and REGION_CENTER_ID IN (:REGION_CENTER_IDDD) ");
					condition.setObject("REGION_CENTER_IDDD", pms000outputVO.getV_regionList());
				}
			}
			//營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD ");
				condition.setObject("OP_AREA_IDDD", "%" + inputVO.getBranch_area_id() + "%");
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("  and BRANCH_AREA_ID IN (:OP_AREA_IDDD) ");
					condition.setObject("OP_AREA_IDDD", pms000outputVO.getV_areaList());
				}
			}
			//分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and BRANCH_NBR LIKE :BRANCH_NBRR ");
				condition.setObject("BRANCH_NBRR", "%" + inputVO.getBranch_nbr() + "%");
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {		
					sql.append("  and BRANCH_NBR IN (:BRANCH_NBRR) ");
					condition.setObject("BRANCH_NBRR", pms000outputVO.getV_branchList());
				}
			}
			//日期
			if (inputVO.getsCreDate() != null) {
				sql.append(" and DATA_DATE = :DATA_DATEE ");
				condition.setObject("DATA_DATEE", sdf.format(inputVO.getsCreDate()));
			}
			//排序
			sql.append("   ORDER BY  DATA_DATE, REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR ) T ");
			sql.append("LEFT JOIN TBPMS_INS_TARGET_SET TA ");
			sql.append("       ON TA.BRANCH_NBR = T.BRANCH_NBR AND TA.YEARMON = T.YEARMON ");
			condition.setQueryString(sql.toString());
			
			//分頁查詢
			ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			
			List<Map<String, Object>> csvList = dam.exeQuery(condition);
			
			// 取RRATE
			String createDate = sdf.format(inputVO.getsCreDate());
			float RRATE= getRrate(createDate).floatValue();

			String dataDate = sdf.format(inputVO.getsCreDate());
			// 全行合計
			List<Map<String, Object>> totalList = getTotal(dam, "total", RRATE, dataDate, inputVO);
			
			// 業務處合計
			List<Map<String, Object>> regionCenterList = getTotal(dam, "center", RRATE, dataDate, inputVO);
						
			// 區域合計
			List<Map<String, Object>> branchAreaList = getTotal(dam, "area", RRATE, dataDate, inputVO);
			
			outputVO.setTotalList(totalList);                //全行
			outputVO.setBranchAreaList(branchAreaList);		 //區
			outputVO.setRegionCenterList(regionCenterList);	 //業務處
//			outputVO.setResultList(csvList); 	// data
			outputVO.setResultList(list); 	// data
			outputVO.setCsvList(csvList); 		// 匯出用
			outputVO.setRate(RRATE);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/** ==手收折數查詢== 
	 * @throws ParseException **/
	public void queryDiscount(Object body, IPrimitiveMap header) throws JBranchException, ParseException{
		PMS303InputVO inputVO = (PMS303InputVO) body;
		PMS303OutputVO outputVO = new PMS303OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT PARAM_NAME_EDIT ");
			sql.append(" FROM TBSYSPARAMETER ");
			sql.append(" WHERE PARAM_TYPE='PMS.INS_DISCOUNT' ");
			sql.append(" AND PARAM_CODE='2' ");
			condition.setQueryString(sql.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(condition);
			outputVO.setDiscountList(list);
			sendRtnObject(outputVO);
						
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));
		} else
			return "0.00";
	}

	// 達成率格式
	private String pcntFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return (int) (Float.parseFloat(map.get(key) + "") + 0.5) + "%";
		} else
			return "0.00";
	}

	// 轉數字型態
	private float numType(Map map, String key) {
		return Float.valueOf(map.get(key).toString());
	}

	// 數字格式
	private String numFormat(int num) {
		NumberFormat nf = new DecimalFormat("#,###,###");
		return nf.format(num);
	}
	
	//日期格式
	private String dateFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			SimpleDateFormat sdfd = new SimpleDateFormat("yyyyMMdd");
			return sdfd.format(map.get(key));
		} else
			return "";
	}
}