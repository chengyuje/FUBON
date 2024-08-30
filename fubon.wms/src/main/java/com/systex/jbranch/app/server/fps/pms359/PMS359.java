package com.systex.jbranch.app.server.fps.pms359;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms406.PMS406OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.reportdata.ConfigAdapterIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :通路業務周報<br>
 * Comments Name : PMS359java<br>
 * Author : Frank<br>
 * Date :2016/08/17 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms359")
@Scope("request")
public class PMS359 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS359.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	/*日期下拉選單查詢 0002259*/
	public void date_query (Object body, IPrimitiveMap header)
			throws JBranchException{
		PMS359OutputVO outputVO = new PMS359OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			StringBuilder sql=new StringBuilder();
			sql.append("SELECT distinct  DATA_DATE from TBPMS_ACCESS_BUSINESS");
			condition.setQueryString(sql.toString());
			List<Map<String,Object>> list=dam.exeQuery(condition);
			outputVO.setResultList(list);;
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		} 
	
	
	}
	
	
	
	
	
	/** 取得可視範圍 **/
	public void getOrgInfo(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS359InputVO inputVO = (PMS359InputVO) body;
		PMS359OutputVO outputVO = new PMS359OutputVO();
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT V_REGION_CENTER_ID, V_REGION_CENTER_NAME, ");
		sql.append("V_BRANCH_AREA_ID, V_BRANCH_AREA_NAME, ");
		sql.append("V_BRANCH_NBR, V_BRANCH_NAME, ");
		sql.append("V_AO_CODE, V_EMP_ID, V_EMP_NAME ");
		sql.append("FROM table( ");
		sql.append("FC_GET_VRR( ");
		sql.append(":purview_type, null, :e_dt, :emp_id, ");
		sql.append("null, null, null, null) ");
		sql.append(") ");

		condition.setQueryString(sql.toString());

		condition.setObject("purview_type", "OTHER"); // 非業績報表
		// condition.setObject("purview_type", "P_PERF"); //個人業績報表
		// condition.setObject("purview_type", "ORG_PERF"); //轄下人員業績

		if (inputVO.getsCreDate() != null) {
			condition.setObject("e_dt", inputVO.getsCreDate());
		} else
			condition.setObject("e_dt", stamp);
		condition.setObject("emp_id", loginID);
		// condition.setObject("org_id", null);
		// condition.setObject("v_ao_flag", null);
		// condition.setObject("v_emp_id", null);

		outputVO.setOrgList(dam.exeQuery(condition));
		sendRtnObject(outputVO);
	}

	/** 查詢資料 **/
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS359InputVO inputVO = (PMS359InputVO) body;
		PMS359OutputVO outputVO = new PMS359OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT  B.REGION_CENTER_ID,  B.REGION_CENTER_NAME,  B.BRANCH_AREA_ID,  B.BRANCH_AREA_NAME, ");
		sql.append(" B.BRANCH_NBR,  B.BRANCH_NAME,  R.EMP_NAME,  B.AO_CODE,  B.AO_JOB_RANK,  B.DEP_T_AUM,  B.DEP_F_AUM, ");
		sql.append(" B.INS_DEP_AUM,  B.INV_T_AUM,  B.INV_F_AUM,  B.INS_T_AUM,  B.INS_F_AUM,  B.CT_T_AUM,  B.OTHER_AUM, ");
		sql.append(" B.TOTAL_AUM,  B.VS_AUM,  B.F_AUM_RATE,  B.VS_AUM_RATE,  B.TOTAL_CUST_CNT,  B.VS_CUST_CNT, ");
		sql.append(" B.VS_CUST_RATE,  B.CUST_CNT_1,  B.CUST_CNT_2,  B.CUST_CNT_3,  B.DIFF_TOTAL_AUM,  B.DIFF_VS_AUM, ");
		sql.append(" B.DIFF_TOTAL_CUST_CNT,  B.DIFF_VS_CUST_CNT,  B.DIFF_CUST_CNT_1, ");
		sql.append(" B.DIFF_CUST_CNT_2, B.DIFF_CUST_CNT_3 , B.CREATETIME ");
		sql.append(" FROM TBPMS_ACCESS_BUSINESS B ");
		sql.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO R ON B.EMP_ID = R.EMP_ID ");
		sql.append(" where 1=1 ");
		// 資料日期
		if (!StringUtils.isNotBlank(inputVO.getReportDate()) )
			sql.append("and B.DATA_DATE = :dt ");
		// 業務處
		if (!StringUtils.isBlank(inputVO.getRegion_center_id()))
			sql.append("and B.REGION_CENTER_ID = :rcid ");
		// 營運區
		if (!StringUtils.isBlank(inputVO.getBranch_area_id()))
			sql.append("and B.BRANCH_AREA_ID = :opid ");
		// 分行
		if (!StringUtils.isBlank(inputVO.getBranch_nbr()))
			sql.append("and B.BRANCH_NBR = :brid ");
		// 理專AO CODE
		if (!StringUtils.isBlank(inputVO.getAo_code()))
			sql.append("and B.AO_CODE = :aocode ");
		// 理專員編
		if (!StringUtils.isBlank(inputVO.getEmp_id()))
			sql.append("and B.EMP_ID = :empid ");

		sql.append("ORDER BY B.REGION_CENTER_ID, B.BRANCH_AREA_ID, B.BRANCH_NBR ");

		condition.setQueryString(sql.toString());
		if (!StringUtils.isNotBlank(inputVO.getReportDate()))
	//		condition.setObject("dt", sdf.format(inputVO.getsCreDate()));
			condition.setObject("dt",inputVO.getReportDate());
		if (!StringUtils.isBlank(inputVO.getRegion_center_id()))
			condition.setObject("rcid", inputVO.getRegion_center_id());
		
		if (!StringUtils.isBlank(inputVO.getBranch_area_id()))
			condition.setObject("opid", inputVO.getBranch_area_id());
		
		if (!StringUtils.isBlank(inputVO.getBranch_nbr()))
			condition.setObject("brid", inputVO.getBranch_nbr());
		
		if (!StringUtils.isBlank(inputVO.getAo_code()))
			condition.setObject("aocode", inputVO.getAo_code());
		
		if (!StringUtils.isBlank(inputVO.getEmp_id()))
			condition.setObject("empid", inputVO.getEmp_id());

		// ResultIF list = dam.executePaging(condition, inputVO
		// .getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalList(dam.exeQuery(condition));
		sendRtnObject(outputVO);
		// if (list.size() > 0) {
		// outputVO.setTotalPage(list.getTotalPage());
		// outputVO.setResultList(list);
		// outputVO.setTotalRecord(list.getTotalRecord());
		// outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		// sendRtnObject(outputVO);
		// } else {
		// throw new APException("ehl_01_common_009");
		// }
	}

//	/** 匯出EXCEL檔 **/
//	public void export(Object body, IPrimitiveMap header)
//			throws JBranchException, Exception {
//		PMS359OutputVO outputVO = (PMS359OutputVO) body;
//		ConfigAdapterIF config = (ConfigAdapterIF) PlatformContext.getBean("configAdapter");
//		String fileName = "通路業務周報_" + sdf.format(new Date()) + ".xlsx";
//		String Path = (String) SysInfo
//				.getInfoValue(SystemVariableConsts.TEMP_PATH);
//		String filePath = Path + fileName;
//
//		List<Map<String, Object>> list = outputVO.getTotalList();
//
//		XSSFWorkbook wb = new XSSFWorkbook();
//		XSSFSheet sheet = wb.createSheet("通路業務周報");
//		sheet.setDefaultColumnWidth(20);
//		sheet.setDefaultRowHeightInPoints(20);
//
//		// 資料 CELL型式
//		XSSFCellStyle style = wb.createCellStyle();
//		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
//		style.setBorderBottom((short) 1);
//		style.setBorderTop((short) 1);
//		style.setBorderLeft((short) 1);
//		style.setBorderRight((short) 1);
//
//		// 表頭 CELL型式
//		XSSFCellStyle headingStyle = wb.createCellStyle();
//		headingStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//		headingStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
//		headingStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 填滿顏色
//		headingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//		headingStyle.setBorderBottom((short) 1);
//		headingStyle.setBorderTop((short) 1);
//		headingStyle.setBorderLeft((short) 1);
//		headingStyle.setBorderRight((short) 1);
//		headingStyle.setWrapText(true);
//
//		int index = 0; // 行數
//		String[] headerLine = { "業務處", "營運區", "分行代碼", "分行名稱", "員編", "AO CODE",
//				"理專職級", "台存(百萬)", "外存(百萬)", "類存(百萬)", "台投(百萬)", "外投(百萬)",
//				"台保(百萬)", "外保(百萬)", "台定(百萬)_不計入AUM", "黃金存褶(百萬)", "台外幣總AUM(百萬)",
//				"投保AUM(百萬)", "外幣佔比(%)", "投保佔比(%)", "總客戶數", "投保客戶數",
//				"投保商品滲透率(%)", "100萬到300萬客戶數", "300萬到1500萬客戶數", "1500萬以上客戶數",
//				"較上月底增減_總AUM(百萬)", "較上月底增減_投保AUM(百萬)", "較上月底增減_總客戶數",
//				"較上月底增減_投保客戶數", "較上月底增減_100萬到300萬客戶", "較上月底增減_300萬到1500萬客戶",
//				"較上月底增減_1500萬以上客戶" };
//
//		// Heading
//		XSSFRow row = sheet.createRow(index);
//		index++;
//		row = sheet.createRow(index);
//		row.setHeightInPoints(30);
//		for (int i = 0; i < headerLine.length; i++) {
//			XSSFCell cell = row.createCell(i);
//			cell.setCellStyle(headingStyle);
//			cell.setCellValue(headerLine[i]);
//		}
//
//		// Data row
//		String rcID = "";
//		String opID = "";
//		index++;
//		int startRC = index;
//		int endRC = 0;
//		int startOP = index;
//		int endOP = 0;
//		for (Map<String, Object> map : list) {
//			row = sheet.createRow(index);
//			int j = 0;
//
//			XSSFCell cell = row.createCell(j);
//			// 業務處
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "REGION_CENTER_NAME"));
//			// 營運區
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "BRANCH_AREA_NAME"));
//			// 分行代碼
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "BRANCH_NBR"));
//			// 分行名稱
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "BRANCH_NAME"));
//			// 員編
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "EMP_ID"));
//			// AO CODE
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "AO_CODE"));
//			// 理專職級
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "AO_JOB_RANK"));
//			// 台存(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "DEP_T_AUM"));
//			// 外存(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "DEP_F_AUM"));
//			// 類存(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "INS_DEP_AUM"));
//			// 台投(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "INV_T_AUM"));
//			// 外投(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "INV_F_AUM"));
//			// 台保(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "INS_T_AUM"));
//			// 外保(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "INS_F_AUM"));
//			// 台定(百萬)_不計入AUM
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "CT_T_AUM"));
//			// 黃金存褶(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "OTHER_AUM"));
//			// 台外幣總AUM(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "TOTAL_AUM"));
//			// 投保AUM(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "VS_AUM"));
//			// 外幣佔比(%)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "F_AUM_RATE") + "%");
//			// 投保佔比(%)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "VS_AUM_RATE") + "%");
//			// 總客戶數
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "TOTAL_CUST_CNT"));
//			// 投保客戶數
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "VS_CUST_CNT"));
//			// 投保商品滲透率(%)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(checkIsNull(map, "VS_CUST_RATE") + "%");
//			// 100萬到300萬客戶數
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "CUST_CNT_1"));
//			// 300萬到1500萬客戶數
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "CUST_CNT_2"));
//			// 1500萬以上客戶數
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "CUST_CNT_3"));
//			// 較上月底增減_總AUM(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "DIFF_TOTAL_AUM"));
//			// 較上月底增減_投保AUM(百萬)
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(currencyFormat(map, "DIFF_VS_AUM"));
//			// 較上月底增減_總客戶數
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "DIFF_TOTAL_CUST_CNT"));
//			// 較上月底增減_投保客戶數
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "DIFF_VS_CUST_CNT"));
//			// 較上月底增減_100萬到300萬客戶
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "DIFF_CUST_CNT_1"));
//			// 較上月底增減_300萬到1500萬客戶
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "DIFF_CUST_CNT_2"));
//			// 較上月底增減_1500萬以上客戶
//			cell = row.createCell(j++);
//			cell.setCellStyle(style);
//			cell.setCellValue(numType(map, "DIFF_CUST_CNT_3"));
//
//			if (!map.get("REGION_CENTER_ID").toString().equals(rcID)
//					|| index - 1 == list.size()) {
//				if (index - 1 == list.size())
//					sheet.addMergedRegion(new CellRangeAddress(startRC, startRC
//							+ endRC, 0, 0));
//				else if (endRC > 1)
//					sheet.addMergedRegion(new CellRangeAddress(startRC, startRC
//							+ endRC - 1, 0, 0));
//
//				startRC = index;
//				endRC = 0;
//			}
//			if (!map.get("BRANCH_AREA_ID").toString().equals(opID)
//					|| index - 1 == list.size()) {
//				if (index - 1 == list.size())
//					sheet.addMergedRegion(new CellRangeAddress(startOP, startOP
//							+ endOP, 1, 1));
//				else if (endOP > 1)
//					sheet.addMergedRegion(new CellRangeAddress(startOP, startOP
//							+ endOP - 1, 1, 1));
//
//				startOP = index;
//				endOP = 0;
//			}
//			endRC++;
//			endOP++;
//			rcID = map.get("REGION_CENTER_ID").toString();
//			opID = map.get("BRANCH_AREA_ID").toString();
//
//			index++;
//		}
//		//將檔名取為亂數uid
//		String tempName = UUID.randomUUID().toString();
//		//路徑結合
//		File f = new File(config.getServerHome(), config.getReportTemp() + tempName);
//		//絕對路徑建檔
//		wb.write(new FileOutputStream(f)); 
//		//相對路徑取檔
//		notifyClientToDownloadFile(config.getReportTemp().substring(1) + tempName, fileName); 
//		this.sendRtnObject(null);
////		wb.write(new FileOutputStream(filePath)); //舊寫法
//	}

	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS359OutputVO outputVO = (PMS359OutputVO) body;		
		
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "通路業務周報_" + sdf.format(new Date()) + ".csv"; 
		List listCSV =  new ArrayList();
		for(Map<String, Object> map : list){
			String[] records = new String[34];
			int i = 0;
			records[i]   = checkIsNull(map, "REGION_CENTER_NAME"); //業務處
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); //營運區
			records[++i] = checkIsNull(map, "BRANCH_NBR"); //分行代碼
			records[++i] = checkIsNull(map, "BRANCH_NAME"); //分行名稱
			records[++i] = checkIsNull(map, "EMP_ID"); //員編
			records[++i] = checkIsNull(map, "AO_CODE"); //AO CODE
			records[++i] = checkIsNull(map, "AO_JOB_RANK"); //理專職級
			records[++i] = currencyFormat(map, "DEP_T_AUM"); //台存(百萬)	
			records[++i] = currencyFormat(map, "DEP_F_AUM"); //外存(百萬)
			records[++i] = currencyFormat(map, "INS_DEP_AUM"); //類存(百萬)			
			records[++i] = currencyFormat(map, "INV_T_AUM");//台投(百萬)		
			records[++i] = currencyFormat(map, "INV_F_AUM");  //外投(百萬)
			records[++i] = currencyFormat(map, "INS_T_AUM");  //台保(百萬)
			records[++i] = currencyFormat(map, "INS_F_AUM");  //外保(百萬)
			records[++i] = currencyFormat(map, "CT_T_AUM"); //台定(百萬)_不計入AUM
			records[++i] = currencyFormat(map, "OTHER_AUM"); //黃金存褶(百萬)
			records[++i] = currencyFormat(map, "TOTAL_AUM"); //台外幣總AUM(百萬)			
			records[++i] = currencyFormat(map, "VS_AUM");  //投保AUM(百萬)
			records[++i] = checkIsNull(map, "F_AUM_RATE") + "%"; //外幣佔比(%)
			records[++i] = checkIsNull(map, "VS_AUM_RATE") + "%"; //投保佔比(%)
			records[++i] = checkIsNull(map, "TOTAL_CUST_CNT"); //總客戶數		
			records[++i] = checkIsNull(map, "VS_CUST_CNT");// 投保客戶數
			records[++i] = checkIsNull(map, "VS_CUST_RATE") + "%";// 投保商品滲透率(%)
			records[++i] = checkIsNull(map, "CUST_CNT_1");// 100萬到300萬客戶數
			records[++i] = checkIsNull(map, "CUST_CNT_2");// 300萬到1500萬客戶數
			records[++i] = checkIsNull(map, "CUST_CNT_3");// 1500萬以上客戶數
			records[++i] = currencyFormat(map, "DIFF_TOTAL_AUM");// 較上月底增減_總AUM(百萬)
			records[++i] = currencyFormat(map, "DIFF_VS_AUM");// 較上月底增減_投保AUM(百萬)
			records[++i] = checkIsNull(map, "DIFF_TOTAL_CUST_CNT");// 較上月底增減_總客戶數
			records[++i] = checkIsNull(map, "DIFF_VS_CUST_CNT");// 較上月底增減_投保客戶數
			records[++i] = checkIsNull(map, "DIFF_CUST_CNT_1");// 較上月底增減_100萬到300萬客戶
			records[++i] = checkIsNull(map, "DIFF_CUST_CNT_2");// 較上月底增減_300萬到1500萬客戶
			records[++i] = checkIsNull(map, "DIFF_CUST_CNT_3");// 較上月底增減_1500萬以上客戶
			listCSV.add(records);
		}
		//header
		String [] csvHeader = new String[34];
		int j = 0;
		csvHeader[j]   = "業務處";
		csvHeader[++j] = "營運區";
		csvHeader[++j] = "分行代碼";
		csvHeader[++j] = "分行名稱";
		csvHeader[++j] = "員編";
		csvHeader[++j] = "AO CODE";
		csvHeader[++j] = "理專職級";
		csvHeader[++j] = "台存(百萬)";			
		csvHeader[++j] = "外存(百萬)";
		csvHeader[++j] = "類存(百萬)";
		csvHeader[++j] = "台投(百萬)";
		csvHeader[++j] = "外投(百萬)";
		csvHeader[++j] = "台保(百萬)";
		csvHeader[++j] = "外保(百萬)";
		csvHeader[++j] = "台定(百萬)_不計入AUM";
		csvHeader[++j] = "黃金存褶(百萬)";
		csvHeader[++j] = "台外幣總AUM(百萬)";
		csvHeader[++j] = "投保AUM(百萬)";
		csvHeader[++j] = "外幣佔比(%)";
		csvHeader[++j] = "投保佔比(%)";
		csvHeader[++j] = "總客戶數";
		csvHeader[++j] = "投保客戶數";
		csvHeader[++j] = "投保商品滲透率(%)";
		csvHeader[++j] = "100萬到300萬客戶數";
		csvHeader[++j] = "300萬到1500萬客戶數";
		csvHeader[++j] = "1500萬以上客戶數";
		csvHeader[++j] = "較上月底增減_總AUM(百萬)";
		csvHeader[++j] = "較上月底增減_投保AUM(百萬)";
		csvHeader[++j] = "較上月底增減_總客戶數";
		csvHeader[++j] = "較上月底增減_投保客戶數";
		csvHeader[++j] = "較上月底增減_100萬到300萬客戶";
		csvHeader[++j] = "較上月底增減_300萬到1500萬客戶";
		csvHeader[++j] = "較上月底增減_1500萬以上客戶";
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}
	
	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			NumberFormat nf = NumberFormat.getInstance();
			return nf.format(map.get(key));
		} else
			return "0.00";
	}

	// 轉數字型態
	private int numType(Map map, String key) {
		return Math.round(Float.valueOf(map.get(key).toString()));
	}

	// 數字格式
	private String numFormat(int num) {
		NumberFormat nf = new DecimalFormat("#,###,###");
		return nf.format(num);
	}

}