package com.systex.jbranch.app.server.fps.fps818;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils;
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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps818")
@Scope("request")
public class FPS818 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS818.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS818InputVO inputVO = (FPS818InputVO) body;
		FPS818OutputVO return_VO = new FPS818OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		if("INV".equals(inputVO.getPlan_type()))
			sql.append("SELECT * FROM TBFPS_DASHBOARD_DETAIL_INV WHERE 1=1 ");
		else {
			sql.append("SELECT * FROM TBFPS_DASHBOARD_DETAIL_SPP WHERE 1=1 ");
			if(inputVO.getChkPlan().size() > 0) {
				sql.append("AND SPP_TYPE IN :spp_type ");
				queryCondition.setObject("spp_type", inputVO.getChkPlan());
			}
		}
		// where
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append("AND REGION_CENTER_ID = :region ");
			queryCondition.setObject("region", inputVO.getRegion_center_id());
		} else {
			sql.append("AND (REGION_CENTER_ID IN (:rcIdList) OR REGION_CENTER_ID IS NULL) ");
			sql.append("AND (BRANCH_AREA_ID IN (:opIdList) OR BRANCH_AREA_ID IS NULL) ");
			sql.append("AND (BRANCH_NBR IN (:brNbrList) OR BRANCH_NBR IS NULL) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append("AND BRANCH_AREA_ID = :branch_area ");
			queryCondition.setObject("branch_area", inputVO.getBranch_area_id());
		}
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("AND BRANCH_NBR = :branch ");
			queryCondition.setObject("branch", inputVO.getBranch_nbr());
		}
		if (StringUtils.isNotBlank(inputVO.getVip_degree())) {
			sql.append("AND VIP_DEGREE = :vip_degree ");
			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
		}
		if (StringUtils.isNotBlank(inputVO.getAo_job_rank())) {
			sql.append("AND AO_JOB_RANK = :ao_job_rank ");
			queryCondition.setObject("ao_job_rank", inputVO.getAo_job_rank());
		}
		if (StringUtils.isNotBlank(inputVO.getPlan_category())) {
			sql.append("AND PLAN_CATEGORY = :plan_category ");
			queryCondition.setObject("plan_category", inputVO.getPlan_category());
		}
		if (StringUtils.isNotBlank(inputVO.getMemo())) {
			sql.append("AND MEMO = :memo ");
			queryCondition.setObject("memo", inputVO.getMemo());
		}
		if (StringUtils.isNotBlank(inputVO.getIs_valid())) {
			sql.append("AND IS_VALID = :is_valid ");
			queryCondition.setObject("is_valid", inputVO.getIs_valid());
		}
		if (StringUtils.isNotBlank(inputVO.getPlan_status())) {
			sql.append("AND PLAN_STATUS = :plan_status ");
			queryCondition.setObject("plan_status", inputVO.getPlan_status());
		}
		if(inputVO.getPlan_sDate() != null) {
			sql.append("and PLAN_CREATETIME >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getPlan_sDate());
		}
		if(inputVO.getPlan_eDate() != null) {
			sql.append("and PLAN_CREATETIME < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getPlan_eDate());
		}
		if(inputVO.getPlan_update_sDate() != null) {
			sql.append("and PLAN_LASTUPDATE >= TRUNC(:start_update) ");
			queryCondition.setObject("start_update", inputVO.getPlan_update_sDate());
		}
		if(inputVO.getPlan_update_eDate() != null) {
			sql.append("and PLAN_LASTUPDATE < TRUNC(:end_update)+1 ");
			queryCondition.setObject("end_update", inputVO.getPlan_update_eDate());
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	public void download(Object body, IPrimitiveMap header) throws Exception {
		FPS818InputVO inputVO = (FPS818InputVO) body;
		dam = this.getDataAccessManager();
		XmlInfo xmlInfo = new XmlInfo();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		String fileName = "理財規劃清單_"+ sdf.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		String filePath = Path + uuid;
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("理財規劃清單_"+ sdf.format(new Date()));
		sheet.setDefaultColumnWidth(25);
		sheet.setDefaultRowHeightInPoints(25);
		
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
		
		String[] headerLine1;
		String[] mainLine;
		if("INV".equals(inputVO.getPlan_type())) {
			headerLine1 = new String[]{"業務處", "營運區", "分行", "理專職級", "員工姓名", "客戶姓名", "客戶等級", "客戶風險屬性", "客戶年齡", "投組分類", "選擇投組"
					, "投組狀態", "是否失效", "投組成立日期", "註記", "已下單金額-TTL", "已下單金額-基金", "已下單金額-ETF", "已下單金額-海外債", "已下單金額-SN", "已下單金額-SI", "已下單金額-儲蓄型保險", "已下單金額-投資型保險", "未下單金額-TTL", "未下單金額-基金 ", "未下單金額-ETF", "未下單金額-海外債"
					, "未下單金額-SN", "未下單金額-SI", "未下單金額-儲蓄型保險", "未下單金額-投資型保險", "已贖回金額-TTL", "已贖回金額-基金 ", "已贖回金額-ETF", "已贖回金額-海外債", "已贖回金額-SN", "已贖回金額-SI", "已贖回金額-儲蓄型保險", "已贖回金額-投資型保險", "已下單手收-TTL", "已下單手收-基金", "已下單手收-ETF", "已下單手收-海外債"
					, "已下單手收-SN", "已下單手收-SI", "已下單手收-儲蓄型保險", "已下單手收-投資型保險", "投組損益", "已贖回商品損益-TTL", "已贖回商品損益-基金", "已贖回商品損益-ETF", "已贖回商品損益-海外債", "已贖回商品損益-SN", "已贖回商品損益-SI", "已贖回商品損益-儲蓄型保險", "已贖回商品損益-投資型保險", "累積報酬率", "達成率"};
			
			mainLine = new String[]{"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "AO_JOB_RANK", "EMP_ID", "CUST_ID", "VIP_DEGREE", "CUST_RISK_ATR", "AGE", "PLAN_TYPE", "PLAN_CATEGORY", "PLAN_STATUS", "IS_VALID", "PLAN_CREATETIME"
					, "MEMO", "PCH_TTL", "PCH_FUND", "PCH_ETF", "PCH_BOND", "PCH_SN", "PCH_SI", "PCH_INS_1", "PCH_INS_2", "N_PCH_TTL", "N_PCH_FUND", "N_PCH_ETF", "N_PCH_BOND", "N_PCH_SN", "N_PCH_SI", "N_PCH_INS_1", "N_PCH_INS_2"
					, "RDM_TTL", "RDM_FUND", "RDM_ETF", "RDM_BOND", "RDM_SN", "RDM_SI", "RDM_INS_1", "RDM_INS_2", "FEE_TTL", "FEE_FUND", "FEE_ETF", "FEE_BOND", "FEE_SN", "FEE_SI", "FEE_INS_1", "FEE_INS_2", "PLAN_INCOME"
					, "RDM_INCOME_TTL", "RDM_INCOME_FUND", "RDM_INCOME_ETF", "RDM_INCOME_BOND", "RDM_INCOME_SN", "RDM_INCOME_SI", "RDM_INCOME_INS_1", "RDM_INCOME_INS_2", "C_RETURN", "ACH_RATE"};
		}
		else {
			headerLine1 = new String[]{"業務處", "營運區", "分行", "理專職級", "員工姓名", "特定目的項目", "客戶姓名", "客戶等級", "客戶風險屬性", "客戶年齡", "投組分類", "選擇投組"
					, "投組狀態", "是否失效", "投組成立日期", "註記", "已下單金額-TTL", "已下單金額-基金", "已下單金額-ETF", "已下單金額-海外債", "已下單金額-SN", "已下單金額-SI", "已下單金額-儲蓄型保險", "已下單金額-投資型保險", "未下單金額-TTL", "未下單金額-基金 ", "未下單金額-ETF", "未下單金額-海外債"
					, "未下單金額-SN", "未下單金額-SI", "未下單金額-儲蓄型保險", "未下單金額-投資型保險", "已下單手收-TTL", "已下單手收-基金", "已下單手收-ETF", "已下單手收-海外債"
					, "已下單手收-SN", "已下單手收-SI", "已下單手收-儲蓄型保險", "已下單手收-投資型保險", "投組損益", "累積報酬率", "達成率"};
			
			mainLine = new String[]{"REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "AO_JOB_RANK", "EMP_ID", "CUST_ID", "SPP_TYPE", "VIP_DEGREE", "CUST_RISK_ATR", "AGE", "PLAN_TYPE", "PLAN_CATEGORY", "PLAN_STATUS", "IS_VALID", "PLAN_CREATETIME"
					, "MEMO", "PCH_TTL", "PCH_FUND", "PCH_ETF", "PCH_BOND", "PCH_SN", "PCH_SI", "PCH_INS_1", "PCH_INS_2", "N_PCH_TTL", "N_PCH_FUND", "N_PCH_ETF", "N_PCH_BOND", "N_PCH_SN", "N_PCH_SI", "N_PCH_INS_1", "N_PCH_INS_2"
					, "FEE_TTL", "FEE_FUND", "FEE_ETF", "FEE_BOND", "FEE_SN", "FEE_SI", "FEE_INS_1", "FEE_INS_2", "PLAN_INCOME", "C_RETURN", "ACH_RATE"};
		}
		
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
		
		Map<String, String> spp_type = xmlInfo.doGetVariable("FPS.PLANNING", FormatHelper.FORMAT_3);
		Map<String, String> vip_degree = xmlInfo.doGetVariable("CRM.VIP_DEGREE", FormatHelper.FORMAT_3);
//		Map<String, String> memo = xmlInfo.doGetVariable("FPS.MEMO", FormatHelper.FORMAT_3);
		Map<String, String> spp_plan_status = xmlInfo.doGetVariable("FPS.SPP_PLAN_STATUS", FormatHelper.FORMAT_3);
		Map<String, String> comm_yn = xmlInfo.doGetVariable("COMMON.YES_NO", FormatHelper.FORMAT_3);
		for (Map<String, Object> map : inputVO.getTotalList()) {
			row = sheet.createRow(index);
			if (map.size() > 0) {
				for (int j = 0; j < mainLine.length; j++) {
					XSSFCell cell = row.createCell(j);
					cell.setCellStyle(mainStyle);
					if("BRANCH_NBR".equals(mainLine[j]))
						cell.setCellValue(checkIsNull(map, "BRANCH_NBR") + "-" + checkIsNull(map, "BRANCH_NAME"));
					else if("EMP_ID".equals(mainLine[j]))
						cell.setCellValue(checkIsNull(map, "EMP_ID") + "-" + checkIsNull(map, "EMP_NAME"));
					else if("CUST_ID".equals(mainLine[j]))
						cell.setCellValue(checkIsNull(map, "CUST_ID") + "-" + checkIsNull(map, "CUST_NAME"));
					else if("SPP_TYPE".equals(mainLine[j]))
						cell.setCellValue(map.get("SPP_TYPE") != null ? spp_type.get(map.get("SPP_TYPE")) : "");
					else if("VIP_DEGREE".equals(mainLine[j]))
						cell.setCellValue(map.get("VIP_DEGREE") != null ? vip_degree.get(map.get("VIP_DEGREE")).substring(0, 2) : "");
					else if("PLAN_TYPE".equals(mainLine[j]))
						cell.setCellValue(map.get("PLAN_TYPE") == null ? "" : "SPP".equals(map.get("PLAN_TYPE")) ? "特定目的" : "非特定目的");
					else if("PLAN_STATUS".equals(mainLine[j]))
						cell.setCellValue(map.get("PLAN_STATUS") != null ? spp_plan_status.get(map.get("PLAN_STATUS")) : "");
					else if("IS_VALID".equals(mainLine[j]))
						cell.setCellValue(map.get("IS_VALID") != null ? comm_yn.get(map.get("IS_VALID")) : "");
					else
						cell.setCellValue(checkIsNull(map, mainLine[j]));
				}
				index++;
			}
		}
		
		workbook.write(new FileOutputStream(filePath));
		this.notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);
	}
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	
}