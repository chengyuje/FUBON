package com.systex.jbranch.app.server.fps.cam191;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cam190.CAM190;
import com.systex.jbranch.app.server.fps.cam190.CAM190InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.CAM999;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2021/06/30
 * 
 */
@Component("cam191")
@Scope("request")
public class CAM191 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM999.class);
		
	// By 活動(已過期)_前端入口
	public void getCampExpiredList (Object body, IPrimitiveMap header) throws JBranchException {
		
		sendRtnObject(this.getCampExpiredList(body));
	}
	
	// By 活動(已過期)_後端入口
	public CAM191OutputVO getCampExpiredList (Object body) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM191OutputVO outputVO = new CAM191OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(new CAM190().getByCampListSQL("getCampExpiredList", "CAM191").toString());
		queryCondition.setObject("empID", ws.getUser().getUserID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setCampExpiredList(list);
		
		return outputVO;
	}
	
	// By 活動(即期)_前端入口
	public void getCampNExpiredList (Object body, IPrimitiveMap header) throws JBranchException {
		
		sendRtnObject(this.getCampNExpiredList(body));
	}
	
	// By 活動(即期)_後端入口
	public CAM191OutputVO getCampNExpiredList (Object body) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM191OutputVO outputVO = new CAM191OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(new CAM190().getByCampListSQL("getCampNExpiredList", "CAM191").toString());
		queryCondition.setObject("empID", ws.getUser().getUserID());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setCampNExpiredList(list);
		
		return outputVO;
	}
	
	// 自訂查詢_前端入口
	public void getList (Object body, IPrimitiveMap header) throws JBranchException {
		
		sendRtnObject(this.getList(body));
	}
	
	// 自訂查詢_後端入口
	public CAM191OutputVO getList (Object body) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CAM191InputVO inputVO = (CAM191InputVO) body;
		CAM191OutputVO outputVO = new CAM191OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String type = "";
		if ("tab97".equals(inputVO.getCustomTabType())) {
			type = "getBeContactList";
		} else if ("tab98".equals(inputVO.getCustomTabType())) {
			type = "getExpiredList";
		} else if ("tab99".equals(inputVO.getCustomTabType())) {
			type = "getClosedList";
		}
		
		CAM190InputVO cam190InputVO = new CAM190InputVO();
		cam190InputVO.setRegionID(inputVO.getRegionID());
		cam190InputVO.setOpID(inputVO.getOpID());
		cam190InputVO.setBranchID(inputVO.getBranchID());
		cam190InputVO.setCustID(inputVO.getCustID());
		cam190InputVO.setCustName(inputVO.getCustName());
		cam190InputVO.setAoCode(inputVO.getAoCode());
		cam190InputVO.setCampName(inputVO.getCampName());
		cam190InputVO.setLeadStatus(inputVO.getLeadStatus());
		cam190InputVO.setVipDegree(inputVO.getVipDegree());
		cam190InputVO.setLeadDateRange(inputVO.getLeadDateRange());
		cam190InputVO.setLeadType(inputVO.getLeadType());
		cam190InputVO.setConDegree(inputVO.getConDegree());
		
		queryCondition.setQueryString(new CAM190().customSQL(type, cam190InputVO, "CAM191").toString());
		
		logger.info("=====CAM191 debug start=====");
		logger.info(new CAM190().customSQL(type, cam190InputVO, "CAM191").toString());
		logger.info("regionID:" + inputVO.getRegionID());
		logger.info("opID:" + inputVO.getOpID());
		logger.info("branchID:" + inputVO.getBranchID());
		logger.info("custID:" + "%" + inputVO.getCustID() + "%");
		logger.info("custName:" + "%" + inputVO.getCustName() + "%");
		logger.info("aoCode:" + inputVO.getAoCode());
		logger.info("empID:" + ws.getUser().getUserID());
		logger.info("campName:" + "%" + inputVO.getCampName() + "%");
		logger.info("leadStatus:" + inputVO.getLeadStatus() + "%");
		logger.info("vipDegree:" + inputVO.getVipDegree());
		logger.info("leadType:" + inputVO.getLeadType());
		logger.info("conDegree:" + inputVO.getConDegree());
		logger.info("=====CAM191 debug end=====");
		
		if (StringUtils.isNotBlank(inputVO.getRegionID()))
			queryCondition.setObject("regionID", inputVO.getRegionID());
		if (StringUtils.isNotBlank(inputVO.getOpID()))
			queryCondition.setObject("opID", inputVO.getOpID());
		if (StringUtils.isNotBlank(inputVO.getBranchID()))
			queryCondition.setObject("branchID", inputVO.getBranchID());
		if (StringUtils.isNotBlank(inputVO.getCustID()))
			queryCondition.setObject("custID", "%" + inputVO.getCustID() + "%");
		if (StringUtils.isNotBlank(inputVO.getCustName()))
			queryCondition.setObject("custName", "%" + inputVO.getCustName() + "%");
		
		if (StringUtils.isNotBlank(inputVO.getAoCode())) {
			switch (inputVO.getAoCode()) {
				case "Diamond Team":
					queryCondition.setObject("empID", ws.getUser().getUserID());
					break;
				default:
					queryCondition.setObject("aoCode", inputVO.getAoCode());
					break;
			}
		} else {
			queryCondition.setObject("empID", ws.getUser().getUserID());
		}
		
		if (StringUtils.isNotBlank(inputVO.getCampName()))
			queryCondition.setObject("campName", "%" + inputVO.getCampName() + "%");
		if (StringUtils.isNotBlank(inputVO.getLeadStatus()))
			queryCondition.setObject("leadStatus", inputVO.getLeadStatus() + "%");
		if (StringUtils.isNotBlank(inputVO.getVipDegree()))
			queryCondition.setObject("vipDegree", inputVO.getVipDegree());
		if (StringUtils.isNotBlank(inputVO.getLeadType()))
			queryCondition.setObject("leadType", inputVO.getLeadType());
		if (StringUtils.isNotBlank(inputVO.getConDegree()))
			queryCondition.setObject("conDegree", inputVO.getConDegree());
		
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage(); // 分頁用
		int totalRecord_i = list.getTotalRecord(); // 分頁用
		outputVO.setCustomList(list); // data
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i);// 總頁次
		outputVO.setTotalRecord(totalRecord_i);// 總筆數
		
		return outputVO;
	}
	
	// 匯出
	public void exportCust(Object body, IPrimitiveMap header) throws Exception {
		
		CAM191InputVO inputVO = (CAM191InputVO) body;
		dam = this.getDataAccessManager();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		// copy cam210 excel
		String fileName = "";
		if("tab97".equals(inputVO.getCustomTabType()))
			fileName += "待聯繫名單_" + sdf.format(new Date());
		else if("tab98".equals(inputVO.getCustomTabType()))
			fileName += "未結案已過期名單_" + sdf.format(new Date());
		else if("tab99".equals(inputVO.getCustomTabType()))
			fileName += "已結案名單_" + sdf.format(new Date());
		
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(fileName);
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
		
		String[] headerLine1 = {"分行", "AO CODE", "理專姓名", "客戶身份證字號", "客戶姓名", "名單/待辦工作名稱", "名單到期日", "貢獻度等級"};
		String[] mainLine    = {"BRANCH_NAME", "AO_CODE", "EMP_NAME", "CUST_ID", 
								"CUST_NAME", "CAMPAIGN_NAME", "END_DATE", "CON_DEGREE"};
		
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
		
		Map<String, String> conMap = new XmlInfo().doGetVariable("CRM.CON_DEGREE", FormatHelper.FORMAT_3);
		
		for (Map<String, Object> map : inputVO.getCustom_list()) {
			row = sheet.createRow(index);
			
			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);
					
					if(StringUtils.equals("CON_DEGREE", mainLine[j])) {
						cell.setCellValue(conMap.get(checkIsNull(map, mainLine[j])));
					}
					else if(StringUtils.equals("END_DATE", mainLine[j])) {
						cell.setCellValue(StringUtils.isNotBlank(checkIsNull(map, mainLine[j])) ? sdf2.format(sdf2.parse(checkIsNull(map, mainLine[j]))) : null);
					}
					else {
						cell.setCellValue(checkIsNull(map, mainLine[j]));
					}
				}
				index++;
			}
		}
		
		workbook.write(new FileOutputStream(filePath));
		this.notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName + ".xlsx");
	}
	
	private String checkIsNull (Map map, String key) {
		if (null != map && null != map.get(key))
			return String.valueOf(map.get(key));
		else
			return "";
	}
	
}