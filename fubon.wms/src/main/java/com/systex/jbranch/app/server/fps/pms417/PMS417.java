package com.systex.jbranch.app.server.fps.pms417;

import java.io.FileOutputStream; 
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
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

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :基金頻繁交易月報<br>
 * Comments Name : PMS417.java<br>
 * Author : Sam<br>
 * Date :2018/10/25 <br>
 * Version : 1.0 <br>
 */

@Component("pms417")
@Scope("request")
public class PMS417 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		
		initUUID();
		SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		PMS417InputVO inputVO = (PMS417InputVO) body;
		PMS417OutputVO outputVO = new PMS417OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		//申購日期
		sql.append("SELECT CASE WHEN A.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sql.append("       A.YEARMON, ");
		sql.append("       A.BRANCH_NBR, ");
		sql.append("       BRH.BRANCH_NAME, ");
		sql.append("       BRH.BRANCH_AREA_NAME, ");
		sql.append("       BRH.REGION_CENTER_NAME, ");
		sql.append("       A.INV_DATE, ");
		sql.append("       A.REF_DATE, ");
		sql.append("       A.CERT_NBR, ");
		sql.append("       A.FUND_CODE, ");
		sql.append("       A.FUND_NAME, ");
		sql.append("       A.CUR_COD, ");
		sql.append("       A.REF_AMT, ");
		sql.append("       PAR.PARAM_NAME AS TRUST_TYPE, ");
		sql.append("       A.INV_AMT, ");
		sql.append("       A.TOT_FEE, ");
		sql.append("       A.DIV_AMT, ");
		sql.append("       CASE WHEN NVL(INV_AMT,0) = 0 THEN 0 ELSE (REF_AMT + DIV_AMT - INV_AMT - TOT_FEE ) / INV_AMT * 100 END AS PROFIT_RATE, ");
		sql.append("       A.TRAN_SRC, ");
		sql.append("       A.CUST_ID, ");
		sql.append("       VIP.PARAM_NAME AS CUST_ATT, ");
		sql.append("       CUS.CUST_NAME, ");
		sql.append("       MEM.EMP_ID, ");
		sql.append("       MEM.EMP_NAME, ");
		sql.append("       CUS.AO_CODE, ");
		sql.append("       CASE WHEN A.CUST_AGE >= 65 THEN '65' ELSE '' END AS CUST_AGE ");
		sql.append("FROM TBPMS_FUND_FREQ A ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO BRH ON A.BRANCH_NBR = BRH.BRANCH_NBR ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST CUS ON A.CUST_ID = CUS.CUST_ID ");
		sql.append("LEFT JOIN TBORG_SALES_AOCODE AO ON A.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN TBORG_MEMBER MEM ON AO.EMP_ID = MEM.EMP_ID ");
		sql.append("LEFT JOIN TBSYSPARAMETER PAR ON A.TRUST_TYPE = PAR.PARAM_CODE AND PAR.PARAM_TYPE = 'SOT.TRUST_CURR_TYPE' ");
		sql.append("LEFT JOIN TBSYSPARAMETER VIP ON CUS.VIP_DEGREE = VIP.PARAM_CODE AND VIP.PARAM_TYPE = 'CRM.VIP_DEGREE' ");
		sql.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N EMPN ON EMPN.EMP_ID = MEM.EMP_ID AND A.BRANCH_NBR = EMPN.DEPT_ID AND TO_DATE(A.YEARMON || '01', 'YYYYMMDD') BETWEEN EMPN.START_TIME AND EMPN.END_TIME ");

		sql.append("WHERE 1 = 1 ");
		
		//資料月份
		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
			sql.append("AND A.YEARMON >= :yearMonS ");
			condition.setObject("yearMonS", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportSDate()))));
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
			sql.append("AND A.YEARMON <= :yearMonE ");
			condition.setObject("yearMonE", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportEDate()))));
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNumeric(inputVO.getBranch_nbr()) && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {				// 分行
				sql.append("AND	BRH.BRANCH_NBR = :branchNbr ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	// 營運區	
				sql.append("AND	BRH.BRANCH_AREA_ID = :branchAreaID ");
				condition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {	// 區域中心
				sql.append("AND BRH.REGION_CENTER_ID = :regionCenterID ");
				condition.setObject("regionCenterID", inputVO.getRegion_center_id());
			}
			
			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("AND A.RM_FLAG = 'B' ");
			}
		} else if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
			sql.append("AND MEM.EMP_ID = :empID ");
			condition.setObject("empID", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
			
			sql.append("AND A.RM_FLAG = 'U' ");
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sql.append("AND (");
				sql.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE EMPN.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sql.append("  OR EMPN.E_DEPT_ID = :uhrmOP ");
				sql.append(")");
				condition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sql.append("AND A.RM_FLAG = 'U' ");
		}
	
		// 理專
		if (inputVO.getAoCode() != null && !"".equals(inputVO.getAoCode())) {
			sql.append("AND CUS.AO_CODE	= :aoCode ");
			condition.setObject("aoCode", inputVO.getAoCode());
		}

		condition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);

		boolean validData = true;
		int refTimes = 0;
		
		//WMS-CR-20180829-02_擬新增基金頻繁交易月報 10筆以上，至少5筆贖回
		if (list.size() < 10)
			validData = false;

		for (Map<String, Object> map : list) {
			if (map.get("REF_DATE") != null)
				refTimes += 1;
		}
		
		if (refTimes < 5)
			validData = false;

		if (!validData) {
			list = new ArrayList<Map<String, Object>>();
		}

		outputVO.setTotalList(list);
		outputVO.setResultList(list);
		
		sendRtnObject(outputVO);
	}

	public void export(Object body, IPrimitiveMap header) throws Exception {

		PMS417InputVO inputVO = (PMS417InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "基金頻繁交易月報_" + sdf.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		List<Map<String, String>> reportList = inputVO.getExportList();

		String[] headerLine = { "私銀註記", "資料月份", "業務處", "區別", "分行代號", "分行名稱", 
								"申購日期", "贖回日期", "憑證號碼", "基金編號", "基金名稱", 
								"信託業務別", "計價幣別", "贖回金額", "相關配息", "投資金額", "相關手續費", "報酬率", 
								"客戶ID", "客戶姓名", "高齡客戶", "客戶屬性", 
								"交易來源", 
								"員工編號", "員工姓名", "AO CODE" };
		String[] mainLine = { 	"RM_FLAG", "YEARMON", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", 
								"INV_DATE", "REF_DATE", "CERT_NBR", "FUND_CODE", "FUND_NAME", 
								"TRUST_TYPE", "CUR_COD", "REF_AMT", "DIV_AMT", "INV_AMT", "TOT_FEE", "PROFIT_RATE", 
								"CUST_ID", "CUST_NAME", "CUST_AGE", "CUST_ATT", 
								"TRAN_SRC", 
								"EMP_ID", "EMP_NAME", "AO_CODE" };

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("基金頻繁交易月報_" + sdf.format(new Date()));
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

		for (Map<String, String> map : reportList) {
			row = sheet.createRow(index);

			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				String codeList = checkIsNull(map, mainLine[j]).replaceAll("\n|\r", "/");
				codeList = (codeList.indexOf("/") > -1) ? (codeList.substring(0, codeList.length() - 1)).replaceAll("/", "\n") : codeList;
				cell.setCellValue(codeList.length() > 0 ? codeList.substring(0, codeList.length()) : "");
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

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));
		} else
			return "$0.00";
	}

}