package com.systex.jbranch.app.server.fps.pms997;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms997")
@Scope("prototype")
public class PMS997 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	SimpleDateFormat sdfYYYYMMDD_DASH = new SimpleDateFormat("yyyy-MM-dd");
//	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");

	LinkedHashMap<String, String> svipCaseMColumnMap = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> svipCaseTColumnMap = new LinkedHashMap<String, String>();

	public PMS997() {
		svipCaseMColumnMap.put("資料月份(YYYYMM)", "SNAP_YYYYMM");
		svipCaseMColumnMap.put("客戶ID", "CUST_ID");
		svipCaseMColumnMap.put("關懷類別", "CARE_TYPE");
		
		svipCaseTColumnMap.put("關懷類別", "CARE_TYPE");
		svipCaseTColumnMap.put("說明內容", "CARE_DRCR");
	}
	
	public void query(Object body, IPrimitiveMap header) throws Exception {
		
		PMS997OutputVO outputVO = new PMS997OutputVO();
		outputVO = this.query(body);

		sendRtnObject(outputVO);
	}
	
	// 查詢
	public PMS997OutputVO query(Object body) throws Exception {

		initUUID();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		PMS997InputVO inputVO = (PMS997InputVO) body;
		PMS997OutputVO outputVO = new PMS997OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		sb.append("SELECT CASE WHEN DEP.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("       DEP.YYYYMM, ");
		sb.append("       DEP.EMP_ID, ");
		sb.append("       DEP.EMP_NAME, ");
		sb.append("       DEP.BRANCH_NBR, ");
		sb.append("       ORG.BRANCH_NAME, ");
		sb.append("       DEP.CUST_ID, ");
		sb.append("       DEP.CUST_NAME, ");
		sb.append("       TO_CHAR(TRUNC(DEP.AGE, 0)) AS AGE, ");
		sb.append("       DEP.CARE_TYPE, ");
		sb.append("       CT.CARE_DRCR, ");
		sb.append("       DEP.SOURCE_TYPE, ");
		sb.append("       DEP.NOTE_TYPE, ");
		sb.append("       DEP.NOTE, ");
		sb.append("       DEP.NOTE2, ");
		sb.append("       DEP.NOTE3, ");
		sb.append("       DEP.HR_ATTR, ");
		sb.append("       DEP.FIRSTUPDATE, ");
		sb.append("       DEP.RECORD_SEQ, ");

		sb.append("       DEP.VERSION, ");
		sb.append("       DEP.CREATETIME, ");
		sb.append("       DEP.CREATOR, ");
		sb.append("       DEP.MODIFIER, ");
		sb.append("       DEP.LASTUPDATE ");
		
		sb.append("FROM TBPMS_SVIP_CASE DEP ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO ORG ON DEP.BRANCH_NBR = ORG.BRANCH_NBR ");
		sb.append("LEFT JOIN TBPMS_SVIP_CARE_TYPE CT ON DEP.CARE_TYPE = CT.CARE_TYPE ");
		sb.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON DEP.EMP_ID = MEM.EMP_ID AND DEP.BRANCH_NBR = MEM.DEPT_ID AND TO_DATE(DEP.YYYYMM || '01', 'YYYYMMDD') BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sb.append("WHERE 1 = 1 ");
		
		//資料月份
		if (StringUtils.isNotBlank(inputVO.getImportSDate())) {
			sb.append("AND DEP.YYYYMM >= :yearMonS ");
			queryCondition.setObject("yearMonS", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportSDate()))));
		}
		
		if (StringUtils.isNotBlank(inputVO.getImportEDate())) {
			sb.append("AND DEP.YYYYMM <= :yearMonE ");
			queryCondition.setObject("yearMonE", sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportEDate()))));
		}
		
		if (StringUtils.isNotEmpty(inputVO.getCustID())) {
			sb.append("AND DEP.CUST_ID LIKE :CUST_ID ");
			queryCondition.setObject("CUST_ID", "%" + inputVO.getCustID().toUpperCase() + "%");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getEmpID())) {
			sb.append("AND DEP.EMP_ID LIKE :EMP_ID ");
			queryCondition.setObject("EMP_ID", "%" + inputVO.getEmpID() + "%");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getCareType())) {
			sb.append("AND DEP.CARE_TYPE LIKE :CARE_TYPE ");
			queryCondition.setObject("CARE_TYPE", "%" + inputVO.getCareType() + "%");
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) { 
			if (StringUtils.isNumeric(inputVO.getBranch_nbr()) && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("AND ORG.BRANCH_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sb.append("AND ( ");
				sb.append("  (DEP.RM_FLAG = 'B' AND ORG.BRANCH_AREA_ID = :branchAreaID) ");
				
				if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
					armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("  OR (DEP.RM_FLAG = 'U' AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE DEP.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :branchAreaID )) ");
				}
			
				sb.append(") ");
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("AND ORG.REGION_CENTER_ID = :regionCenterID ");
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			}
			
			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) &&
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("AND DEP.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("AND (");
				sb.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE DEP.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("  OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append(")");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("AND DEP.RM_FLAG = 'U' ");
		}
		
		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
		if(StringUtils.equals("Y", inputVO.getFrom181())){
			sb.append("AND DEP.FIRSTUPDATE IS NULL ");
		}
        
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
 			switch (inputVO.getNoteStatus()) {
 				case "01":
 					sb.append("AND DEP.FIRSTUPDATE IS NOT NULL ");
 					break;
 				case "02":
 					sb.append("AND DEP.FIRSTUPDATE IS NULL ");
 					break;
 			}
 		}
		
		sb.append("ORDER BY DEP.YYYYMM DESC, ");
		sb.append("         DECODE(REPLACE(REPLACE(REPLACE(ORG.REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
		sb.append("         ORG.REGION_CENTER_NAME, ");
		sb.append("         ORG.BRANCH_AREA_ID, ");
		sb.append("         ORG.BRANCH_AREA_NAME, ");
		sb.append("         DEP.BRANCH_NBR, ");
		sb.append("         ORG.BRANCH_NAME ");

		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		return outputVO;
	}

	public void queryDTL(Object body, IPrimitiveMap header) throws Exception {
		
		PMS997InputVO inputVO = (PMS997InputVO) body;
		PMS997OutputVO outputVO = new PMS997OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT CARE_DRCR ");
		sb.append("FROM TBPMS_SVIP_CARE_TYPE DEP ");
		sb.append("WHERE CARE_TYPE = :CARE_TYPE ");
		
		queryCondition.setObject("CARE_TYPE", inputVO.getCareType());
		
		queryCondition.setQueryString(sb.toString());

		outputVO.setDtlList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	// 總行上傳名單
	public void updCareCase(Object body, IPrimitiveMap header) throws JBranchException {

		PMS997InputVO inputVO = (PMS997InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFILE_NAME());
		if(!dataCsv.isEmpty()) {
			// START ==== 取得第一筆之資料年月，進行delete
			queryCondition.setQueryString("DELETE FROM TBPMS_SVIP_CASE_M_SG WHERE SNAP_YYYYMM = :dataDate ");
			queryCondition.setObject("dataDate", dataCsv.get(1)[0]);
			
			dam.exeUpdate(queryCondition);
			// END ====
			
			for(int i = 1; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				
				BigDecimal age = BigDecimal.ZERO;
				for (int j = 0; j < str.length; j++) {
					switch (j) {
						case 1:
							queryCondition.setObject("custID", str[j]);

							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuffer();
							
							sb.append("SELECT ROUND(MONTHS_BETWEEN(LAST_DAY(ADD_MONTHS(SYSDATE, -1)), BIRTH_DATE) / 12, 2) AS AGE ");
							sb.append("FROM TBCRM_CUST_MAST ");
							sb.append("WHERE CUST_ID = :custID ");
							
							queryCondition.setQueryString(sb.toString());
							
							queryCondition.setObject("custID", str[j]);
							
							List<Map<String, Object>> ageList = dam.exeQuery(queryCondition);
							
							age = ageList.size() > 0 ? (BigDecimal) ageList.get(0).get("AGE") : BigDecimal.ZERO;

							break;
					}
				}
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("INSERT INTO TBPMS_SVIP_CASE_M_SG ( ");
				sb.append("  SNAP_YYYYMM, CUST_ID, AGE, CARE_TYPE, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				sb.append("VALUES (  ");
				sb.append("  :snapYYYYMM, :custID, :age, :careType, ");
				sb.append("  0, SYSDATE, :loginID, :loginID, SYSDATE ");
				sb.append(") ");
				
				queryCondition.setQueryString(sb.toString());

				for (int j = 0; j < str.length; j++) {
					switch (j) {
						case 0:
							queryCondition.setObject("snapYYYYMM", str[j]);

							break;
						case 1:
							queryCondition.setObject("custID", str[j]);
							break;
						case 2:
							queryCondition.setObject("careType", str[j]);

							break;
					}
				}
				
				queryCondition.setObject("age", age);
				queryCondition.setObject("loginID", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());
				
				dam.exeUpdate(queryCondition);
			}
		}
		
		sendRtnObject(null);
	}
	
	// 總行上傳內容說明
	public void updCareType(Object body, IPrimitiveMap header) throws JBranchException {

		PMS997InputVO inputVO = (PMS997InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
		List<String[]> dataCsv = CSVUtil.getBig5CSVFile(tempPath, inputVO.getFILE_NAME());
		if(!dataCsv.isEmpty()) {
			for(int i = 1; i < dataCsv.size(); i++) {
				String[] str = dataCsv.get(i);
				
				List<Map<String, Object>> cntList = new ArrayList<Map<String, Object>>();
				
				for (int j = 0; j < str.length; j++) {
					switch (j) {
						case 0:
							queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuffer();
							sb.append("SELECT COUNT(1) AS CNT ");
							sb.append("FROM TBPMS_SVIP_CARE_TYPE ");
							sb.append("WHERE CARE_TYPE = :CARE_TYPE ");
							
							queryCondition.setObject("CARE_TYPE", str[j]);
							
							queryCondition.setQueryString(sb.toString());
							
							cntList = dam.exeQuery(queryCondition);
					}
				}
					
					
				if (cntList.size() > 0) {
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					
					if (((BigDecimal) cntList.get(0).get("CNT")).compareTo(BigDecimal.ZERO) == 1) {
						sb.append("UPDATE TBPMS_SVIP_CARE_TYPE ");
						sb.append("SET CARE_DRCR = :careDrcr, ");
						sb.append("    MODIFIER = :loginID, ");
						sb.append("    LASTUPDATE = SYSDATE ");
						sb.append("WHERE CARE_TYPE = :careType ");
						
					} else {
						sb.append("INSERT INTO TBPMS_SVIP_CARE_TYPE ( ");
						sb.append("  CARE_TYPE, CARE_DRCR, ");
						sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
						sb.append(") ");
						sb.append("VALUES (  ");
						sb.append("  :careType, :careDrcr, ");
						sb.append("  0, SYSDATE, :loginID, :loginID, SYSDATE ");
						sb.append(") ");
						
					}
					
					for (int j = 0; j < str.length; j++) {
						switch (j) {
							case 0:
								queryCondition.setObject("careType", str[j]);
								break;
							case 1:
								queryCondition.setObject("careDrcr", str[j]);
								break;
						}
					}
					
					queryCondition.setObject("loginID", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());

					queryCondition.setQueryString(sb.toString());
					
					dam.exeUpdate(queryCondition);
				}
			}
		}
		
		sendRtnObject(null);
	}
	
	// 取得總行上傳名單範例
	public void getCareCaseExp(Object body, IPrimitiveMap header) throws JBranchException {

		CSVUtil csv = new CSVUtil();

		// 設定表頭
		csv.setHeader(svipCaseMColumnMap.keySet().toArray(new String[svipCaseMColumnMap.keySet().size()]));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "總行上傳名單範例.csv");

		sendRtnObject(null);
	}
	
	// 取得總行上傳內容說明範例
	public void getCareTypeExp(Object body, IPrimitiveMap header) throws JBranchException {

		CSVUtil csv = new CSVUtil();

		// 設定表頭
		csv.setHeader(svipCaseTColumnMap.keySet().toArray(new String[svipCaseTColumnMap.keySet().size()]));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, "總行上傳內容說明範例.csv");

		sendRtnObject(null);
	}
	
	// 更新資料，在前端篩選編輯過的資料。
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS997InputVO inputVO = (PMS997InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_SVIP_CASE ");
			sb.append("SET ");
			sb.append("    NOTE_TYPE = :noteType, ");
			sb.append("    NOTE = :note, ");
			sb.append("    NOTE2 = :note2, ");
			sb.append("    HR_ATTR = :hrAttr, ");
			sb.append("    RECORD_SEQ = :recordSEQ, ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = sysdate ");
			
			if (null == map.get("FIRSTUPDATE")) {
				sb.append("    , FIRSTUPDATE = sysdate ");
			}
			
			sb.append("WHERE YYYYMM = :YYYYMM ");
			sb.append("AND CUST_ID = :CUST_ID ");
			sb.append("AND SOURCE_TYPE = :SOURCE_TYPE ");
			
			// KEY
			queryCondition.setObject("YYYYMM", map.get("YYYYMM"));
			queryCondition.setObject("CUST_ID", map.get("CUST_ID"));
			queryCondition.setObject("SOURCE_TYPE", map.get("SOURCE_TYPE"));
			
			// CONTENT
			queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
			queryCondition.setObject("note", map.get("NOTE"));
			queryCondition.setObject("note2", map.get("NOTE2"));
			queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
			queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
		
		sendRtnObject(null);
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {

		PMS997InputVO inputVO = (PMS997InputVO) body;

		List<Map<String, Object>> list = inputVO.getExportList();

		String fileName = "內部強化關懷名單_" + sdfYYYYMM.format(new Date(Long.parseLong(inputVO.getImportSDate()))) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("內部強化關懷名單");
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(20);

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

		String[] headerLine = { "私銀註記", 
								"資料月份", 
								"行員歸屬行", 
								"員編", 
								"行員姓名", 
								"客戶姓名", 
								"客戶ID", 
								"高齡客戶", 
								"關懷類別", 
								"說明內容", 
								"查詢方式",
								"電訪錄音編號",
								"關懷結果",
								"初判異常",
								"首次建立時間",
								"最新異動人員",
								"最新異動日期 "};
		String[] mainLine	= { "RM_FLAG", 
								"YYYYMM", 
								"BRANCH_NBR", 
								"EMP_ID", 
								"EMP_NAME", 
								"CUST_NAME", 
								"CUST_ID", 
								"AGE", 
								"CARE_TYPE", 
								"CARE_DRCR", 
								"NOTE",
								"RECORD_SEQ",
								"NOTE2",
								"HR_ATTR",
								"FIRSTUPDATE",
								"MODIFIER",
								"LASTUPDATE"};

		Integer index = 0;

		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
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

		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);

			for (int i = 0; i < mainLine.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(mainStyle);

				switch (mainLine[i]) {
					case "FIRSTUPDATE":
					case "LASTUPDATE":
						if (StringUtils.isNotEmpty(checkIsNull(map, mainLine[i]))) {
							cell.setCellValue(sdfYYYYMMDDHHMMSS.format(sdfYYYYMMDDHHMMSS.parse((String) map.get(mainLine[i]))));
						} else {
							cell.setCellValue("");
						}
						break;
					case "BRANCH_NBR":
						cell.setCellValue((String) map.get(mainLine[i]) + "-" + map.get("BRANCH_NAME"));
						break;
					case "NOTE":
						XmlInfo xmlInfo = new XmlInfo();
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");

						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(mainLine[i]));
						}
						
						cell.setCellValue((StringUtils.isNotEmpty(note) ? note : ""));
						break;
					case "RECORD_SEQ":
						cell.setCellValue(checkIsNull(map, mainLine[i]) + "");
						break;
					default:
						cell.setCellValue(checkIsNull(map, mainLine[i]));
						break;
				}
			}

			index++;
		}

		workbook.write(new FileOutputStream(filePath));

		notifyClientToDownloadFile(DataManager.getSystem().getPath().get("temp").toString() + uuid, fileName);

		sendRtnObject(null);
	}

	// 空值檢查
	private String checkIsNull(Map map, String key) {

		if (null != map && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}