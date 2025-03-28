package com.systex.jbranch.app.server.fps.pms401;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms401")
@Scope("prototype")
public class PMS401 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	/* 取得可視範圍 */
	public void getOrgInfo(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS401InputVO inputVO = (PMS401InputVO) body;
		PMS401OutputVO outputVO = new PMS401OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT V_REGION_CENTER_ID, V_REGION_CENTER_NAME, ");
		sb.append("       V_BRANCH_AREA_ID, V_BRANCH_AREA_NAME, ");
		sb.append("       V_BRANCH_NBR, V_BRANCH_NAME, ");
		sb.append("       V_AO_CODE, V_EMP_ID, V_EMP_NAME ");
		sb.append("FROM table(FC_GET_VRR(:purview_type, null, :e_dt, :emp_id, null, null, null, null)) ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("purview_type", "OTHER"); // 非業績報表

		if (inputVO.geteCreDate() != null) {
			queryCondition.setObject("e_dt", inputVO.geteCreDate());
		} else {
			queryCondition.setObject("e_dt", new Timestamp(System.currentTimeMillis()));
		}

		queryCondition.setObject("emp_id", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));

		outputVO.setOrgList(dam.exeQuery(queryCondition));

		sendRtnObject(outputVO);
	}

	/* 查詢資料-前端 */
	public void queryData(Object body, IPrimitiveMap header) throws Exception {
		
		PMS401OutputVO outputVO = new PMS401OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}

	/* 查詢資料-後端 */
	public PMS401OutputVO queryData(Object body) throws Exception {

		initUUID();
		
		PMS401InputVO inputVO = (PMS401InputVO) body;
		PMS401OutputVO outputVO = new PMS401OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		XmlInfo xmlInfo = new XmlInfo();
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);
		boolean isARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);
		
		sb.append("SELECT ROWNUM, T.*, 'Y' AS RECORD_YN ");
		sb.append("FROM ( ");
		sb.append("  SELECT CASE WHEN DEP.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("         TO_CHAR(DEP.TX_DATE, 'YYYY/MM/DD') AS TX_DATE, ");
		sb.append("         DEFN.BRANCH_NBR, ");
		sb.append("         DEFN.BRANCH_NAME, ");
		sb.append("         DEP.EMP_ID, ");
		sb.append("         DEP.EMP_NAME, ");
		sb.append("         DEP.TX_AMT, ");
		sb.append("         DEP.NOTE_TYPE, ");
		sb.append("         DEP.NOTE, ");
		sb.append("         DEP.NOTE2, ");
		sb.append("         DEP.NOTE3, ");
		sb.append("         DEP.HR_ATTR, ");
		sb.append("         DEP.RECORD_SEQ, ");
		sb.append("         DEP.FIRSTUPDATE, ");
		sb.append("         TO_CHAR(DEP.CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("         DEP.MODIFIER, ");
		sb.append("         DEP.LASTUPDATE ");
		sb.append("  FROM TBPMS_EMP_DAILY_TXN_SUM_D DEP ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N EMPN ON DEP.EMP_ID = EMPN.EMP_ID AND DEP.TX_DATE BETWEEN EMPN.START_TIME AND EMPN.END_TIME ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO DEFN ON EMPN.DEPT_ID = DEFN.BRANCH_NBR ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N EMP ");
		sb.append("         ON DEP.EMP_ID = EMP.EMP_ID ");
		sb.append("        AND DEP.BRANCH_NBR = EMP.DEPT_ID ");
		sb.append("        AND DEP.TX_DATE BETWEEN EMP.START_TIME AND EMP.END_TIME ");
		sb.append("        AND (EMP.PS_FLAG = 'Y' OR EMP.AO_JOB_RANK LIKE ('%F%') OR EMP.ROLE_ID IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE ='BTPMS401.PERSON_ROLE')) ");
		sb.append("  LEFT JOIN TBSYSPARAMETER ROLE ON EMP.ROLE_ID = ROLE.PARAM_CODE AND PARAM_TYPE = 'BTPMS401.PERSON_ROLE' ");
		sb.append("  WHERE 1 = 1 ");
		
		sb.append("  AND DEFN.BRANCH_NBR IS NOT NULL ");

		// 資料統計日期
		if (null != inputVO.getsCreDate()) {
			sb.append("  AND TRUNC(DEP.CREATETIME) >= TRUNC(TO_DATE(:times, 'YYYY-MM-DD')) ");
			queryCondition.setObject("times", new java.text.SimpleDateFormat("yyyy-MM-dd").format(inputVO.getsCreDate()));
		}

		if (null != inputVO.geteCreDate()) {
			sb.append("  AND TRUNC(DEP.CREATETIME) <= TRUNC(TO_DATE(:timee, 'YYYY-MM-DD')) ");
			queryCondition.setObject("timee", new java.text.SimpleDateFormat("yyyy-MM-dd").format(inputVO.geteCreDate()));

		}
		
		if (StringUtils.isNotEmpty(inputVO.getEmpID())) {
			sb.append("  AND DEP.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpID());
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNumeric(inputVO.getBranch_nbr()) && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {				
				sb.append("  AND DEFN.BRANCH_NBR = :BRNCH_NBRR ");
				queryCondition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sb.append("  AND ( ");
				sb.append("    (DEP.RM_FLAG = 'B' AND DEFN.BRANCH_AREA_ID = :BRANCH_AREA_IDD) ");
				
				if (isHANDMGR || isARMGR) {
					sb.append("    OR (DEP.RM_FLAG = 'U' AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE DEP.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :BRANCH_AREA_IDD )) ");
				}
			
				sb.append("  ) ");
				queryCondition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("  AND DEFN.REGION_CENTER_ID = :REGION_CENTER_IDD ");
				queryCondition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}

			if (StringUtils.isNotBlank(inputVO.getPerson_role())) {
				sb.append("  AND ROLE.PARAM_NAME = :PERSON_ROLE ");
				queryCondition.setObject("PERSON_ROLE", inputVO.getPerson_role());
			}
			
			if (isHANDMGR && isARMGR) {
				sb.append("  AND DEP.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("  AND ( ");
				sb.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE DEP.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("    OR EMPN.E_DEPT_ID = :uhrmOP ");
				sb.append("  ) ");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("  AND DEP.RM_FLAG = 'U' ");
		}

		//由工作首頁CRM181過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sb.append("  AND DEP.FIRSTUPDATE IS NULL ");
		}
		
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("  AND DEP.FIRSTUPDATE IS NOT NULL ");
					break;
				case "02":
					sb.append("  AND DEP.FIRSTUPDATE IS NULL ");
					break;
			}
		}

		sb.append("  ORDER BY DEP.TX_DATE, DEP.BRANCH_NBR, DEP.EMP_ID ");
		sb.append(") T ");
		sb.append("WHERE 1 = 1 ");

		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setTotalList(list);
		outputVO.setResultList(list);
		
		return outputVO;
	}

	/* 查詢資料 */
	public void queryDetail(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS401InputdetailVO inputVO = (PMS401InputdetailVO) body;
		PMS401OutputVO outputVO = new PMS401OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT ROWNUM, t.* ");
		sb.append("FROM ( ");
		sb.append(" SELECT TO_CHAR(DEP.SNAP_DATE, 'YYYY/MM/DD') AS SNAP_DATE, ");
		sb.append("        TO_CHAR(DEP.TX_DATE, 'YYYY/MM/DD') AS TX_DATE, ");
		sb.append("        MEM.EMP_ID AS MEM_EMP_ID, ");
		sb.append("        DEP.EMP_ID, ");
		sb.append("        DEP.DRCR, ");
		sb.append("        DEP.ACCT_NBR_ORI, ");
		sb.append("        DEP.CUST_NAME, ");
		sb.append("        DEP.RCV_ACCT_NO,");
		sb.append("        DEP.TX_AMT, ");
		sb.append("        DEP.REMK, ");
		sb.append("        DEP.MEMO, ");
		sb.append("        DEP.JRNL_NO ");
		sb.append(" FROM TBPMS_EMP_DAILY_TXN_D DEP ");
		sb.append(" LEFT JOIN TBORG_MEMBER MEM ON DEP.EMP_ID = MEM.CUST_ID ");
		sb.append(" WHERE 1 = 1 ");

		if (StringUtils.isNotBlank(inputVO.getTxDate())) {
			sb.append("  AND TO_CHAR(DEP.TX_DATE, 'YYYY/MM/DD') = :txDate ");
			queryCondition.setObject("txDate", inputVO.getTxDate());
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmpID())) {
			sb.append("  AND MEM.EMP_ID = :empID ");
			queryCondition.setObject("empID", inputVO.getEmpID());
		}

		sb.append("  ORDER BY DEP.TX_DATE, DEP.EMP_ID ");
		sb.append(") t ");

		queryCondition.setQueryString(sb.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalList(dam.exeQuery(queryCondition));
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		sendRtnObject(outputVO);
	}

	/* 儲存 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {

		PMS401InputVO inputVO = (PMS401InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		for (Map<String, Object> map : inputVO.getList()) { // 資料修改後
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();

			sb.append("UPDATE TBPMS_EMP_DAILY_TXN_SUM_D ");
			sb.append("SET HR_ATTR = :hrAttr, ");
			sb.append("    NOTE_TYPE = :noteType, ");
			sb.append("    NOTE = :note, ");
			sb.append("    NOTE2 = :note2, ");
			sb.append("    NOTE3 = :note3, ");
			sb.append("    RECORD_SEQ = :recordSEQ, ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = sysdate ");
			
			if (null == map.get("FIRSTUPDATE")) {
				sb.append("    , FIRSTUPDATE = sysdate ");
			}

			sb.append("WHERE TO_CHAR(TX_DATE, 'YYYY/MM/DD') = :txDate ");
			sb.append("AND EMP_ID = :empID ");

			// KEY
			queryCondition.setObject("txDate", map.get("TX_DATE"));
			queryCondition.setObject("empID", map.get("EMP_ID"));
			
			// CONTENT
			queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
			queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
			queryCondition.setObject("note", map.get("NOTE"));
			queryCondition.setObject("note2", map.get("NOTE2"));
			queryCondition.setObject("note3", map.get("NOTE3"));
			queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
			
			queryCondition.setQueryString(sb.toString());

			dam.exeUpdate(queryCondition);
		}
		
		sendRtnObject(null);
	}

	/* 產出CSV */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {

		SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

		XmlInfo xmlInfo = new XmlInfo();
		
		PMS401OutputVO outputVO = (PMS401OutputVO) body;

		String reportName = "分行人員存款異動日報";
		String fileName = reportName + "_" + sdfYYYYMMDD.format(new Date()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(reportName);
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

		// 資料 CELL型式
		XSSFCellStyle mainStyle = workbook.createCellStyle();
		mainStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		mainStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		mainStyle.setBorderBottom((short) 1);
		mainStyle.setBorderTop((short) 1);
		mainStyle.setBorderLeft((short) 1);
		mainStyle.setBorderRight((short) 1);
		
		// 資料 CELL型式
		XSSFCellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		titleStyle.setBorderBottom((short) 1);
		titleStyle.setBorderTop((short) 1);
		titleStyle.setBorderLeft((short) 1);
		titleStyle.setBorderRight((short) 1);
		
		String[] headerLine = { "私銀註記", 
								"資料日期",
								"交易日期",
								"所屬分行",
								"員編",
								"行員姓名",
								"合計金額",
								"查證方式",
								"電訪錄音編號",
								"資金來源或帳戶關係",
								"具體原因或用途",
								"初判異常轉法遵部調查",
								"首次建立日期",
								"最新異動人員",
								"最新異動日期" };

		String[] mainLine 	= { "RM_FLAG", 
								"CREATETIME",
								"TX_DATE",
								"BRANCH_NBR",
								"EMP_ID",
								"EMP_NAME",
								"TX_AMT",
								"NOTE",
								"RECORD_SEQ",
								"NOTE2",
								"NOTE3",
								"HR_ATTR",
								"FIRSTUPDATE",
								"MODIFIER",
								"LASTUPDATE" };
		
		Integer index = 0;

		XSSFRow row = sheet.createRow(index);
		for (int i = 0; i < 1; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(titleStyle);
			cell.setCellValue(reportName);
		}
		
		index++;
		
		row = sheet.createRow(index);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerLine.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellStyle(headingStyle);
			cell.setCellValue(headerLine[i]);
		}

		index++;
		
		for (Map<String, Object> map : outputVO.getTotalList()) {
			row = sheet.createRow(index);
			
			for (int i = 0; i < mainLine.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(mainStyle);
				
				switch (mainLine[i]) {
					case "ROWNUM":
						cell.setCellValue(((int) Double.parseDouble(checkIsNull(map, mainLine[i]).toString())) + ""); // 序號 - 去小數點
						break;
					case "BRANCH_NBR":
						cell.setCellValue(StringUtils.defaultString((String) map.get(mainLine[i])) + "-" + StringUtils.defaultString((String) map.get("BRANCH_NAME")));
						break;
					case "NOTE":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");

						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(mainLine[i]));
						}
						
						cell.setCellValue((StringUtils.isNotEmpty(note) ? note : ""));
						break;
					case "TX_AMT":
						if (null != map.get(mainLine[i])) {
							cell.setCellValue(new DecimalFormat("#,##0.##").format(map.get(mainLine[i])));
						} else {
							cell.setCellValue("0");
						}

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

		this.sendRtnObject(null);
	}

	/* 產出CSV */
	public void exportDtl(Object body, IPrimitiveMap header) throws JBranchException {

		PMS401OutputVO outputVO = (PMS401OutputVO) body;

		String[] csvHeader = { 	"序號",
								"資料日期",
								"交易日期",
								"行員ID",
								"轉出/轉入",
								"行員帳號",
								"對方姓名",
								"對方帳號",
								"交易金額(台幣)",
								"備註",
								"摘要",
								"交易序號" };

		String[] csvMain = { 	"ROWNUM",
								"SNAP_DATE",
								"TX_DATE",
								"EMP_ID",
								"DRCR",
								"ACCT_NBR_ORI",
								"CUST_NAME",
								"RCV_ACCT_NO",
								"TX_AMT",
								"REMK",
								"MEMO",
								"JRNL_NO"};
		List<Object[]> csvData = new ArrayList<Object[]>();

		if ((outputVO.getTotalList()).size() == 0) {
			String[] records = new String[csvHeader.length];
			records[0] = "查無資料";
			csvData.add(records);
		} else {
			for (Map<String, Object> map : outputVO.getTotalList()) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "ROWNUM":
							records[i] = ((int) Double.parseDouble(checkIsNull(map, csvMain[i]).toString())) + ""; // 序號 - 去小數點
							break;
						case "ACCT_NBR_ORI":
						case "RCV_ACCT_NO":
						case "REMK":
						case "MEMO":
						case "JRNL_NO":
							records[i] = "=\"" + StringUtils.defaultString((String) map.get(csvMain[i])) + "\"";
							break;
						case "TX_AMT":
							if (null != map.get(csvMain[i])) {
								records[i] = new DecimalFormat("#,##0.00").format(map.get(csvMain[i]));
							} else {
								records[i] = "0.00";
							}

							break;
						default:
							records[i] = checkIsNull(map, csvMain[i]);
							break;
					}
				}
				csvData.add(records);
			}
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		notifyClientToDownloadFile(csv.generateCSV(), "分行人員存款異動日報_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + outputVO.getTotalList().get(0).get("MEM_EMP_ID") + "該員當日存款異動明細-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv");
	}
	/* 檢查Map取出欄位是否為Null */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/* 處理貨幣格式 */
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
	
	/* 格式時間 */
	private String formatDate(Object date, SimpleDateFormat sdf) {
		
		if (date != null)
			return sdf.format(date);
		else
			return "";
	}
}