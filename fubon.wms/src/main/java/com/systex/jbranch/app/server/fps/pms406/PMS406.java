package com.systex.jbranch.app.server.fps.pms406;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
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
 * Description :基金短線進出交易報表
 * Author : 2016/05/17 Frank
 * Editor : 2017/01/30 Kevin
 */

@Component("pms406")
@Scope("request")
public class PMS406 extends FubonWmsBizLogic {
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	DataAccessManager dam = null;
	
	/*
	 * 取得可執行編輯的群組
	 */
	public void chkMaintenance (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS406OutputVO outputVO = new PMS406OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'PMS406' AND FUNCTIONID = 'maintenance') "); 
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
		
		PMS406OutputVO outputVO = new PMS406OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}
	
	// 查詢
	public PMS406OutputVO queryData(Object body) throws JBranchException {
		
		initUUID();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		PMS406InputVO inputVO = (PMS406InputVO) body;
		PMS406OutputVO outputVO = new PMS406OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ROWNUM, t.* ");
		sb.append("FROM ( ");
		sb.append("  SELECT 'N' AS UPDATE_FLAG, ");
		sb.append("         CASE WHEN TRUNC(A.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("         A.DATA_DATE AS KEY_DATA_DATE, ");
		sb.append("         SUBSTR(A.DATA_DATE, 1, 4) || '/' || SUBSTR(A.DATA_DATE, 5, 2) || '/' || SUBSTR(A.DATA_DATE, 7, 2) AS DATA_DATE, ");
		sb.append("         A.REGION_CENTER_ID, ");
		sb.append("         A.REGION_CENTER_NAME,  ");
		sb.append("         A.BRANCH_AREA_ID, ");
		sb.append("         A.BRANCH_AREA_NAME, ");
		sb.append("         A.BRANCH_NBR, ");
		sb.append("         A.BRANCH_NAME, ");
		sb.append("         SUBSTR(A.PRCH_DATE, 1, 4) || '/' || SUBSTR(A.PRCH_DATE, 5, 2) || '/' || SUBSTR(A.PRCH_DATE, 7, 2) AS PRCH_DATE,  ");
		sb.append("         SUBSTR(A.RDMP_DATE, 1, 4) || '/' || SUBSTR(A.RDMP_DATE, 5, 2) || '/' || SUBSTR(A.RDMP_DATE, 7, 2) as RDMP_DATE, ");
		sb.append("         A.CERT_NBR, ");
		sb.append("         A.PRD_ID, ");
		sb.append("         A.PRD_NAME, ");
		sb.append("         A.TRUST_TYPE,  ");
		sb.append("         A.CRCY_TYPE, ");
		sb.append("         A.RDMP_AMT, ");
		sb.append("         A.ACT_AMT, ");
		sb.append("         A.INT_AMT, ");
		sb.append("         A.FEE, ");
		sb.append("         A.ROR, ");
		sb.append("         A.CUST_ID, ");
		sb.append("         A.CUST_NAME, ");
		sb.append("         A.CUST_ATTR, ");
		sb.append("         B.PARAM_NAME AS TRADE_SRC, ");
		sb.append("         A.EMP_ID, ");
		sb.append("         A.EMP_NAME, ");
		sb.append("         A.AO_CODE, ");
		sb.append("         A.CREATETIME, ");
		sb.append("         A.NOTE, ");
		sb.append("         A.NOTE2, ");
		sb.append("         A.HR_ATTR, ");
		sb.append("         A.FIRSTUPDATE, ");
		sb.append("         A.MODIFIER, ");
		sb.append("         A.LASTUPDATE, ");
		sb.append("         A.NOTE_TYPE, ");
		sb.append("         A.RECORD_SEQ ");
		sb.append("  FROM TBPMS_SHORT_TRAN A ");
		sb.append("  LEFT JOIN TBSYSPARAMETER B ON PARAM_TYPE = 'PMS.SHORT_TRADE_TRANS_SRC' AND B.PARAM_CODE = A.TRADE_SRC ");
		sb.append("  WHERE 1 = 1 ");

		if (inputVO.getsCreDate() != null) {
			sb.append("  AND TO_DATE(A.DATA_DATE, 'YYYY-MM-DD') >= :creDate ");
			queryCondition.setObject("creDate", inputVO.getsCreDate());
			
			sb.append("  AND TO_DATE(A.DATA_DATE, 'YYYY-MM-DD') <= :endDate ");
			queryCondition.setObject("endDate", inputVO.getEndDate());
		}
		
		//進出頻率
		if (inputVO.getFreqType().equals("1")) {
			sb.append("  AND A.TRANS_DAYS <= 7 ");
		}
		
		if (inputVO.getFreqType().equals("2")) {
			sb.append("  AND A.TRANS_DAYS >= 8 ");
			sb.append("  AND A.TRANS_DAYS <= 30 ");
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0 ||
			StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) { 
			// 非任何uhrm相關人員 或 為031之兼任FC，需查詢畫面中組織條件
			
			// by Willis 20171024 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) { 
				sb.append("  AND A.BRANCH_NBR = :branchNBR ");
				queryCondition.setObject("branchNBR", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("  AND A.BRANCH_NBR in ( select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :branchAreaID ) ");
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) { 
				sb.append("  AND A.BRANCH_NBR in ( select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :regionCenterID ) ");
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			}
			
			if (!headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) &&
				!StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
				// 非總人行員 或 非 為031之兼任FC，僅可視轄下
				sb.append("  AND A.RM_FLAG = 'B' ");
			}
		} else {
			sb.append("  AND A.RM_FLAG = 'U' ");
		}
		
		//理專(FOR可視範圍)
		if (!StringUtils.isBlank(inputVO.getEmp_id())) {
			sb.append("  AND A.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmp_id());
		}

		// 由工作首頁 CRM181 過來，只須查詢主管尚未確認資料 & 報酬率為負值
        if(StringUtils.equals("Y", inputVO.getFrom181())){
        	sb.append("  AND A.FIRSTUPDATE IS NULL ");
        	sb.append("  AND A.ROR < 0 ");
        }
        
		sb.append("  ORDER BY A.DATA_DATE, A.REGION_CENTER_ID, A.BRANCH_AREA_ID, A.BRANCH_NBR, A.CUST_ID, A.PRCH_DATE");
		sb.append(") t ");
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setTotalList(list);
		outputVO.setResultList(list);
		
		// 判斷是否為總行人員
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			outputVO.setIsHeadMgr("Y");
		} else {
			outputVO.setIsHeadMgr("N");
		}
				
		return outputVO;
	}

	// 更新資料，在前端篩選編輯過的資料。
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS406InputVO inputVO = (PMS406InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_SHORT_TRAN ");
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
			
			sb.append("WHERE DATA_DATE = :dataDate ");
			sb.append("AND BRANCH_NBR = :branchNBR ");
			sb.append("AND CERT_NBR = :certNBR ");
			
			// KEY
			queryCondition.setObject("dataDate", map.get("KEY_DATA_DATE"));
			queryCondition.setObject("branchNBR", map.get("BRANCH_NBR"));
			queryCondition.setObject("certNBR", map.get("CERT_NBR"));
			
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
		
		PMS406OutputVO outputVO = (PMS406OutputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "基金短線進出交易報表_" + sdf.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		
		String filePath = Path + uuid;
		
		List<Map<String, Object>> reportList = outputVO.getTotalList();
		
		String[] headerLine = { "序號", "資料日期", "業務處代號", "業務處名稱", "區別代號", "區別名稱", "分行代號", "分行名稱", 
				               	"申購日期", "贖回日期", "憑證號碼", "基金編號", "基金名稱", "信託業務別", "計價幣別", "參考贖回金額", "配息金額", "投資金額", "相關手續費", "報酬率", "客戶ID", "客戶姓名", "客戶屬性", "贖回交易來源", 
				               	"員工編號", "員工姓名", "AO Code", 
				               	"專員是否勸誘客戶短線進出", "查證方式", "電訪錄音編號", "具體原因", "首次建立時間", "最新異動人員", "最新異動日期"};
		String[] mainLine   = { "ROWNUM", "DATA_DATE", "REGION_CENTER_ID", "REGION_CENTER_NAME", "BRANCH_AREA_ID", "BRANCH_AREA_NAME", "BRANCH_NBR", "BRANCH_NAME", 
				               	"PRCH_DATE", "RDMP_DATE", "CERT_NBR", "PRD_ID", "PRD_NAME", "CRCY_TYPE_NAME", "CRCY_TYPE", "RDMP_AMT", "INT_AMT", "ACT_AMT", "FEE", "ROR", "CUST_ID", "CUST_NAME", "CUST_ATTR", "TRADE_SRC", 
				               	"EMP_ID", "EMP_NAME", "AO_CODE", 
				               	"HR_ATTR", "NOTE", "RECORD_SEQ", "NOTE2", "FIRSTUPDATE", "MODIFIER", "LASTUPDATE"};
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("基金短線進出交易報表_" + sdf.format(new Date()) + "_" + (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
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
		
		for (Map<String, Object> map : reportList) {
			row = sheet.createRow(index);

			for (int j = 0; j < mainLine.length; j++) {
				XSSFCell cell = row.createCell(j);
				cell.setCellStyle(mainStyle);
				
				switch (mainLine[j]) {
					case "ROWNUM" :
						cell.setCellValue(((int) Double.parseDouble(checkIsNull(map, mainLine[j]).toString())) + ""); //序號
						break;
					case "CRCY_TYPE_NAME":
						cell.setCellValue(("TWD".equals(checkIsNull(map, "CRCY_TYPE"))) ? "台幣信託" : "外幣信託");
						break;
					case "RDMP_AMT":
					case "INT_AMT":
					case "ACT_AMT":
					case "FEE":
						cell.setCellValue(currencyFormat(map, mainLine[j]));
						break;
					case "CUST_ATTR":
						cell.setCellValue(convertCodeToName(map, mainLine[j]));
						break;
					case "NOTE":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");
						
						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(mainLine[j]));
						}
						
						cell.setCellValue(note);
						break;
					default :
						cell.setCellValue(checkIsNull(map, mainLine[j]));
						break;
				}
				
			}

			index++;
		}
		
		wb.write(new FileOutputStream(filePath));
		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		this.sendRtnObject(null);
	}

	// 檢查Map取出欄位是否為Null
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}

	// 客戶屬性代碼轉中文名稱
	private String convertCodeToName(Map map, String key) throws JBranchException {
		
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		String sql = "SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.CUST_ATTR' AND PARAM_CODE = '" + map.get(key) + "'";
		condition.setQueryString(sql);
		List<Map<String, String>> list = dam.exeQuery(condition);
		if (list.size() > 0)
			return list.get(0).get("PARAM_NAME");
		else
			return "薪轉戶";
	}
}