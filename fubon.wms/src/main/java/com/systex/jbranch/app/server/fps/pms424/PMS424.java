package com.systex.jbranch.app.server.fps.pms424;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
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

@Component("pms424")
@Scope("prototype")
public class PMS424 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void query (Object body, IPrimitiveMap header) throws Exception {
		
		PMS424OutputVO outputVO = new PMS424OutputVO();
		outputVO = this.query(body);

		sendRtnObject(outputVO);
	}
	
	public PMS424OutputVO query (Object body) throws JBranchException {
		
		initUUID();
		PMS424InputVO inputVO = (PMS424InputVO) body;
		PMS424OutputVO outputVO = new PMS424OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		
		XmlInfo xmlInfo = new XmlInfo();
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);
		boolean isARMGR = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(loginRoleID);

		sb.append("SELECT ROWNUM, A.* ");
		sb.append("FROM ( ");
		sb.append("  SELECT CASE WHEN RPT.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("         RPT.TXN_DATE, ");
		sb.append("         CASE WHEN TRUNC(RPT.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("         RPT.EMP_ID, ");
		sb.append("         MBR.EMP_NAME, ");
		sb.append("         ORG.REGION_CENTER_ID, ");
		sb.append("         ORG.REGION_CENTER_NAME, ");
		sb.append("         ORG.BRANCH_AREA_ID, ");
		sb.append("         ORG.BRANCH_AREA_NAME, ");
		sb.append("         RPT.BRANCH_NBR || '-' || ORG.BRANCH_NAME AS BRANCH_NBR, ");
		sb.append("         RPT.ACCT_OUT_BANK, ");
		sb.append("         RPT.ACCT_OUT, ");
		sb.append("         RPT.ACCT_OUT_ID, ");
		sb.append("         O.CUST_NAME AS ACCT_OUT_NAME, ");
		sb.append("         RPT.ACCT_IN_BANK, ");
		sb.append("         RPT.ACCT_IN, ");
		sb.append("         RPT.ACCT_IN_ID, ");
		sb.append("         RPT.TXNSEQ, ");
		sb.append("         I.CUST_NAME AS ACCT_IN_NAME, ");
		sb.append("         RPT.TXN_AMT, ");
		sb.append("         RPT.NOTE_TYPE, ");
		sb.append("         RPT.NOTE1, ");
		sb.append("         RPT.NOTE2, ");
		sb.append("         RPT.CUST_BASE, ");
		sb.append("         RPT.NOTE3, ");
		sb.append("         RPT.RECORD_SEQ, ");
		sb.append("         RPT.WARNING_YN, ");
		sb.append("         RPT.FIRST_CREATIME, ");
		sb.append("         RPT.MODIFIER, ");
		sb.append("         RPT.LASTUPDATE, ");
		sb.append("         TO_CHAR(RPT.TXN_DATE, 'YYYY/MM/DD') AS TXN_DATE_S, ");
		sb.append("         TO_CHAR(RPT.SNAP_DATE, 'YYYY/MM/DD') AS SNAP_DATE_S, ");
		sb.append("         TO_CHAR(RPT.CREATETIME, 'YYYY/MM/DD') AS CREATETIME_S, ");
		sb.append("         PAR.PARAM_NAME AS SOURCE_OF_DEMAND_N ");
		sb.append("  FROM TBPMS_INDIRECT_ACCT_MONITOR RPT ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO ORG ON RPT.BRANCH_NBR = ORG.BRANCH_NBR ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST O ON RPT.ACCT_OUT_ID = O.CUST_ID ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST I ON RPT.ACCT_IN_ID = I.CUST_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MBR ON RPT.EMP_ID = MBR.EMP_ID ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'PMS.PMS424_SOURCE_OF_DEMAND' AND RPT.SOURCE_OF_DEMAND = PAR.PARAM_CODE ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON RPT.EMP_ID = MEM.EMP_ID AND RPT.BRANCH_NBR = MEM.DEPT_ID AND RPT.TXN_DATE BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sb.append("  WHERE 1 = 1 ");
					
		if (null != inputVO.getSourceOfDemand()) {
			sb.append("  AND RPT.SOURCE_OF_DEMAND = :sourceOfDemand ");
			queryCondition.setObject("sourceOfDemand", inputVO.getSourceOfDemand());
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("  AND RPT.BRANCH_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sb.append("  AND ( ");
				sb.append("    (DEP.RM_FLAG = 'B' AND ORG.BRANCH_AREA_ID = :BRANCH_AREA_IDD) ");
				
				if (isHANDMGR || isARMGR) {
					sb.append("    OR (DEP.RM_FLAG = 'U' AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE RPT.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :BRANCH_AREA_IDD )) ");
				}
			
				sb.append("  ) ");
				queryCondition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("  AND ORG.REGION_CENTER_ID = :REGION_CENTER_IDD ");
				queryCondition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}

			if (!isHANDMGR && !isARMGR) {
				sb.append("  AND RPT.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("  AND (");
				sb.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE RPT.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("    OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append("  )");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("  AND RPT.RM_FLAG = 'U' ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sb.append("  AND RPT.EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", inputVO.getEmp_id());
		}
		
		if (inputVO.getsDate() != null) {
			sb.append("  AND TRUNC(RPT.CREATETIME) >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}
		
		if (inputVO.geteDate() != null) {
			sb.append("  AND TRUNC(RPT.CREATETIME) <= TRUNC(:end) ");
			queryCondition.setObject("end", inputVO.geteDate());
		}
		
		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
        if(StringUtils.equals("Y", inputVO.getFrom181())){
        	sb.append("  AND NVL(RPT.SUPERVISOR_FLAG, 'N') <> 'Y' ");
        }
        
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("  AND RPT.FIRST_CREATIME IS NOT NULL ");
					break;
				case "02":
					sb.append("  AND RPT.FIRST_CREATIME IS NULL ");
					break;
			}
		}
        
        sb.append("  ORDER BY RPT.BRANCH_NBR, RPT.ACCT_IN_ID ");
        sb.append(") A ");
        
		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		return outputVO;
	}
	
	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS424InputVO inputVO = (PMS424InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) { // 資料修改後
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_INDIRECT_ACCT_MONITOR ");
			sb.append("SET ");
			sb.append("    NOTE_TYPE = :noteType, ");
			sb.append("    NOTE1 = :note1, ");
			sb.append("    CUST_BASE = :custBase, ");
			sb.append("    NOTE2 = :note2, ");
			sb.append("    NOTE3 = :note3, ");
			sb.append("    RECORD_SEQ = :recordSEQ, ");
			sb.append("    WARNING_YN = :warning_yn, ");
			sb.append("    SUPERVISOR_FLAG = 'Y', ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = SYSDATE ");
			
			if (map.get("FIRST_CREATIME") == null) {
				sb.append(", FIRST_CREATIME = sysdate ");
			}
			
			sb.append("WHERE TO_CHAR(TXN_DATE, 'YYYY/MM/DD') = :txn_date_s ");
			sb.append("AND TXNSEQ = :txnseq ");

			// KEY
			queryCondition.setObject("txn_date_s", map.get("TXN_DATE_S") == null ? "" : map.get("TXN_DATE_S").toString());
			queryCondition.setObject("txnseq", map.get("TXNSEQ") == null ? "" : map.get("TXNSEQ").toString());
			
			// CONTECT
			queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
			queryCondition.setObject("note1", map.get("NOTE1") == null ? "" : map.get("NOTE1").toString());
			queryCondition.setObject("custBase", map.get("CUST_BASE"));
			queryCondition.setObject("note2", map.get("NOTE2") == null ? "" : map.get("NOTE2").toString());
			queryCondition.setObject("note3", map.get("NOTE3") == null ? "" : map.get("NOTE3").toString());
			queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			queryCondition.setObject("warning_yn", map.get("WARNING_YN") == null ? "" : map.get("WARNING_YN").toString());
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
		
		sendRtnObject(null);
	}
	
	public void export (Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {
		
		SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

		XmlInfo xmlInfo = new XmlInfo();

		PMS424InputVO inputVO = (PMS424InputVO) body;
		
		List<Map<String, Object>> list = inputVO.getList();

		String reportName = "關聯戶交易日報";
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
		
		String[] headerLine = { "序號", 
								"私銀註記", 
								"資料日期", 
								"產出來源",
								"交易日期",
								"理專歸屬行", 
								"理專姓名",
								"轉出姓名", 
								"轉出身分證字號", 
								"轉出帳號",
								"轉入姓名", 
								"轉入身分證字號", 
								"轉入帳號",
								"交易金額",
								"查證方式", 
								"電訪錄音編號", 
								"資金來源或帳戶關係", 
								"具體原因或用途", 
								"初判異常轉法遵部調查", 
								"首次建立時間", 
								"最後異動人員", 
								"最後異動日期"};
		
		String[] mainLine   = {	"ROWNUM", 
								"RM_FLAG", 
								"CREATETIME_S", 
								"SOURCE_OF_DEMAND_N",
								"TXN_DATE_S", 
								"BRANCH_NBR", 
								"EMP_NAME",
								"ACCT_OUT_NAME", 
								"ACCT_OUT_ID", 
								"ACCT_OUT",
								"ACCT_IN_NAME", 
								"ACCT_IN_ID", 
								"ACCT_IN",
								"TXN_AMT",
								"NOTE1", 
								"RECORD_SEQ", 
								"NOTE2", 
								"NOTE3", 
								"WARNING_YN", 
								"FIRST_CREATIME",
								"MODIFIER", 
								"LASTUPDATE"};
		
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
		
		for (Map<String, Object> map : list) {
			row = sheet.createRow(index);
			
			for (int i = 0; i < mainLine.length; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellStyle(mainStyle);
				
				switch (mainLine[i]) {
					case "ROWNUM" :
						cell.setCellValue(((int) Double.parseDouble(checkIsNull(map, mainLine[i]).toString())) + ""); 	// 序號 - 去小數點;
						break;
					case "TXN_AMT":
						cell.setCellValue(currencyFormat(map, mainLine[i]));
						break;
					case "NOTE1":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");
						
						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(mainLine[i]));
						}
						
						cell.setCellValue((StringUtils.isNotEmpty(note) ? note : ""));
						break;
					case "RECORD_SEQ":
						cell.setCellValue(checkIsNull(map, mainLine[i]) + "");
						break;
					case "NOTE2":
						String note2 = (String) xmlInfo.getVariable("PMS.CUST_BASE_IP", (String) map.get("CUST_BASE"), "F3");

						if (null != map.get("CUST_BASE") && StringUtils.equals("O", (String) map.get("CUST_BASE"))) {
							note2 = note2 + "：" + StringUtils.defaultString((String) map.get(mainLine[i]));
						}
						
						cell.setCellValue((StringUtils.isNotEmpty(note2) ? note2 : ""));
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
	
	public void getExample (Object body, IPrimitiveMap header) throws Exception {
		
		notifyClientToDownloadFile("doc//PMS//PMS424_getExample.xlsx", "2020.1_11關聯戶交易日報.xlsx");
	    this.sendRtnObject(null);
	}
	
	
	private String checkIsNull(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			if ("ACCT_OUT_ID".equals(key) || "ACCT_IN_ID".equals(key)) {
				return DataFormat.getCustIdMaskForHighRisk(String.valueOf(map.get(key)));
			} else {
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}
	
	
	// 處理貨幣格式
	private String currencyFormat (Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.##");
			return df.format(map.get(key));
		} else
			return "0";
	}
}