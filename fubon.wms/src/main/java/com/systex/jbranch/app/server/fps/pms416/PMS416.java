package com.systex.jbranch.app.server.fps.pms416;

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

/**
 * Description :海外債短線進出交易月報
 * Author : 2018/10/05 Sam
 */

@Component("pms416")
@Scope("request")
public class PMS416 extends FubonWmsBizLogic {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	DataAccessManager dam = null;
	/*
	 * 取得可執行編輯的群組
	 */
	public void chkMaintenance (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS416OutputVO outputVO = new PMS416OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PMS416' AND FUNCTIONID = 'maintenance') "); 
		sb.append("AND ROLEID = :roleID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("roleID", (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		List<Map<String, Object>> priList = dam.exeQuery(queryCondition);
		if (priList.size() > 0) {
			outputVO.setIsMaintenancePRI("Y");
		} else {
			outputVO.setIsMaintenancePRI("N");
		}
		
		sendRtnObject(outputVO);
	}
	
	public void queryData(Object body, IPrimitiveMap header) throws Exception {
		
		PMS416OutputVO outputVO = new PMS416OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}
	
	// 查詢
	public PMS416OutputVO queryData(Object body) throws JBranchException {
		
		initUUID();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2); // 理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2); // OP
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		PMS416InputVO inputVO = (PMS416InputVO) body;
		PMS416OutputVO outputVO = new PMS416OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//申購日期
		sb.append("SELECT A.YEARMON, ");
		sb.append("       CASE WHEN TRUNC(A.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("       A.BRANCH_NBR, ");
		sb.append("       BRH.BRANCH_NAME, ");
		sb.append("       BRH.BRANCH_AREA_NAME, ");
		sb.append("       BRH.REGION_CENTER_NAME, ");
		sb.append("       A.INV_DATE, ");
		sb.append("       A.REF_DATE, ");
		sb.append("       TO_CHAR(A.REF_DATE, 'YYYYMMDD') AS KEY_REF_DATE, ");
		sb.append("       A.CERT_NBR, ");
		sb.append("       A.BOND_NBR, ");
		sb.append("       A.CUR_COD, ");
		sb.append("       A.REF_AMT, ");
		sb.append("       A.INV_AMT, ");
		sb.append("       A.MANAGE_FEE, ");
		sb.append("       A.INV_FEE, ");
		sb.append("       A.CHANNEL_FEE, ");
		sb.append("       (A.DIV_AMT - A.INV_FRONT_FEE + A.REF_FRONT_FEE) AS DIV_AMT, ");
		sb.append("       CASE WHEN A.INV_AMT <> 0 THEN (ROUND((A.REF_AMT + A.DIV_AMT - A.INV_FRONT_FEE + A.REF_FRONT_FEE - A.INV_AMT - A.MANAGE_FEE - A.INV_FEE) / A.INV_AMT * 100, 2)) ELSE 0 END AS PROFIT_RATE, ");
		sb.append("       CASE WHEN A.TRAN_SRC = 'WEB' THEN '網銀' WHEN A.TRAN_SRC = 'MOB' THEN '行銀' ELSE '臨櫃' END AS TRAN_SRC, ");
		sb.append("       A.CUST_ID, ");
		sb.append("       PAR.PARAM_NAME AS CUST_ATT, ");
		sb.append("       CUS.CUST_NAME, ");
		sb.append("       A.EMP_ID, ");
		sb.append("       MEM.EMP_NAME, ");
		sb.append("       A.AO_CODE, ");
		sb.append("       A.CHANNEL_FEE_TWD, ");
		sb.append("       A.INV_FEE_TWD, ");
		sb.append("       A.NOTE, ");
		sb.append("       A.NOTE2, ");
		sb.append("       A.HR_ATTR, ");
		sb.append("       A.FIRSTUPDATE, ");
		sb.append("       A.MODIFIER, ");
		sb.append("       A.LASTUPDATE, ");
		sb.append("       CASE WHEN A.BUY_TRAN_SRC = 'WEB' THEN '網銀' WHEN A.BUY_TRAN_SRC = 'MOB' THEN '行銀' ELSE '臨櫃' END AS BUY_TRAN_SRC, ");
		sb.append("       A.NOTE_TYPE, ");
		sb.append("       A.RECORD_SEQ ");
		sb.append("FROM TBPMS_SHORT_BOND A ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO BRH ON A.BRANCH_NBR = BRH.BRANCH_NBR ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST CUS ON A.CUST_ID = CUS.CUST_ID ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON A.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN TBSYSPARAMETER PAR ON	CUS.VIP_DEGREE = PAR.PARAM_CODE	AND	PAR.PARAM_TYPE = 'CRM.VIP_DEGREE' ");
		sb.append("WHERE 1 = 1 ");

		//資料月份
		if (StringUtils.isNotBlank(inputVO.getDataMon())) {
			sb.append("AND A.YEARMON = :dataMon ");
			queryCondition.setObject("dataMon", inputVO.getDataMon());
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0 || 
			StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
			
			if (!headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) &&
				!StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
				// 非總人行員 且 非 為031之兼任FC，僅可視轄下
				sb.append("AND A.RM_FLAG = 'B' ");
				
				// 登入為銷售人員強制加AO_CODE
				if (fcMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
					psopMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("AND CUS.AO_CODE IN :aoCodeList ");
					queryCondition.setObject("aoCodeList", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
				}
			} else if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
				sb.append("AND CUS.AO_CODE IN :aoCodeList ");
				queryCondition.setObject("aoCodeList", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			}
			
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("AND A.BRANCH_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
			} 
			// 營運區	
			else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("AND BRH.BRANCH_AREA_ID = :branchAreaID ");
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());				
			}
			// 區域中心
			else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("AND BRH.REGION_CENTER_ID = :regionCenterID ");
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			}
		} else {
			sb.append("AND A.RM_FLAG = 'U' ");
		}

		// 理專
		if (inputVO.getAoCode() != null && !"".equals(inputVO.getAoCode())) {
			sb.append("AND CUS.AO_CODE = :aoCode ");
			queryCondition.setObject("aoCode", inputVO.getAoCode());
		}
		
		// 由工作首頁 CRM181 過來，只須查詢主管尚未確認資料 & 報酬率為負值
        if(StringUtils.equals("Y", inputVO.getFrom181())){
        	sb.append("AND A.FIRSTUPDATE IS NULL ");
        	sb.append("AND CASE WHEN A.INV_AMT <> 0 THEN (ROUND((A.REF_AMT + A.DIV_AMT - A.INV_FRONT_FEE + A.REF_FRONT_FEE - A.INV_AMT - A.MANAGE_FEE - A.INV_FEE) / A.INV_AMT * 100, 2)) ELSE 0 END < 0 ");
        }

		//進出頻率
		if (StringUtils.isNotBlank(inputVO.getFreqType())) {
			if ("1".equals(inputVO.getFreqType())) {
				sb.append("AND A.INV_DATE >= ADD_MONTHS(A.REF_DATE, -3) ");
			} else {
				sb.append("AND A.INV_DATE >= ADD_MONTHS(A.REF_DATE, -6) AND A.INV_DATE < ADD_MONTHS(A.REF_DATE, -3) ");
			}
		} else {
			sb.append("AND A.INV_DATE >= ADD_MONTHS(A.REF_DATE, -6) ");
		}
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setTotalList(list);
		outputVO.setResultList(list);
		
		return outputVO;
	}

	// 更新資料，在前端篩選編輯過的資料。
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS416InputVO inputVO = (PMS416InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_SHORT_BOND ");
			sb.append("SET HR_ATTR = :hrAttr, ");
			sb.append("    NOTE_TYPE = :noteType, ");
			sb.append("    NOTE = :note, ");
			sb.append("    NOTE2 = :note2, ");
			sb.append("    RECORD_SEQ = :recordSEQ, ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = sysdate ");
			
			if (null == map.get("FIRSTUPDATE")) {
				sb.append("    , FIRSTUPDATE = sysdate ");
			}
			
			sb.append("WHERE CERT_NBR = :certNBR ");
			sb.append("AND CUST_ID = :custID ");
			sb.append("AND TO_CHAR(REF_DATE, 'YYYYMMDD') = :refDate ");
			sb.append("AND YEARMON = :yearMon ");

			// KEY
			queryCondition.setObject("certNBR", map.get("CERT_NBR"));
			queryCondition.setObject("custID", map.get("CUST_ID"));
			queryCondition.setObject("refDate", map.get("KEY_REF_DATE"));
			queryCondition.setObject("yearMon", map.get("YEARMON"));
			
			// CONTENT
			queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
			queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
			queryCondition.setObject("note", map.get("NOTE"));
			queryCondition.setObject("note2", map.get("NOTE2"));
			queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
		
		sendRtnObject(null);
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {

		XmlInfo xmlInfo = new XmlInfo();
		
		PMS416InputVO inputVO = (PMS416InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "海外債短線進出交易月報_" + sdf.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		List<Map<String, String>> reportList = inputVO.getExportList();

		String[] headerLine = { "資料月份", "業務處", "區別", "分行代號", "分行名稱", 
				                "申購日期", "贖回日期", "憑證號碼", "債券代號", "計價幣別", "贖回金額", "相關配息", "投資金額", "申購手續費", "申購手續費折台", "通路服務費", "通路服務費折台", "信管費", "報酬率", "客戶ID", "客戶姓名", "客戶屬性", "贖回交易來源", "申購交易來源", 
				                "員工編號", "員工姓名", "AO CODE", 
	                            "專員是否勸誘客戶短線進出", "查證方式", "電訪錄音編號", "具體原因", "首次建立時間", "最新異動人員", "最新異動日期"};
		String[] mainLine   = { "YEARMON", "REGION_CENTER_NAME", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", 
				                "INV_DATE", "REF_DATE", "CERT_NBR", "BOND_NBR", "CUR_COD", "REF_AMT", "DIV_AMT", "INV_AMT", "INV_FEE", "INV_FEE_TWD", "CHANNEL_FEE", "CHANNEL_FEE_TWD", "MANAGE_FEE", "PROFIT_RATE", "CUST_ID", "CUST_NAME", "CUST_ATT", "TRAN_SRC", "BUY_TRAN_SRC", 
				                "EMP_ID", "EMP_NAME", "AO_CODE", 
	                            "HR_ATTR", "NOTE", "RECORD_SEQ", "NOTE2", "FIRSTUPDATE", "MODIFIER", "LASTUPDATE"};

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("海外債短線進出交易月報_" + sdf.format(new Date()));
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
				
				switch (mainLine[j]) {
					case "NOTE":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");
						
						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(mainLine[j]));
						}
						
						cell.setCellValue(note);
						break;
					default :
						cell.setCellValue(codeList.length() > 0 ? codeList.substring(0, codeList.length()) : "");
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
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

}