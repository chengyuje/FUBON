package com.systex.jbranch.app.server.fps.pms428;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.ibm.icu.math.BigDecimal;
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

@Component("pms428")
@Scope("prototype")
public class PMS428 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdfYYYYMMDD_DASH = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	public void query(Object body, IPrimitiveMap header) throws Exception {
		PMS428OutputVO outputVO = new PMS428OutputVO();
		outputVO = this.query(body);

		sendRtnObject(outputVO);
	}
	
	// 查詢
	public PMS428OutputVO query(Object body) throws Exception {

		initUUID();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		PMS428InputVO inputVO = (PMS428InputVO) body;
		PMS428OutputVO outputVO = new PMS428OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		sb.append("SELECT CASE WHEN CD.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("       ORG.REGION_CENTER_ID, ");
		sb.append("       ORG.REGION_CENTER_NAME, ");
		sb.append("       ORG.BRANCH_AREA_ID, ");
		sb.append("       ORG.BRANCH_AREA_NAME, ");
		sb.append("       CD.BRANCH_NBR, ");
		sb.append("       ORG.BRANCH_NAME, ");
		
		sb.append("       CASE WHEN TRUNC(CD.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("       CD.SEQNO, ");
		sb.append("       CD.DATA_DATE, ");
		sb.append("       CD.DATA_SOURCE, ");
		sb.append("       CD.EMP_CUST_ID, ");  
		sb.append("       CD.EMP_ID, ");
		sb.append("       CD.EMP_NAME, ");   
		sb.append("       CD.TXN_DATE, ");
		sb.append("       CD.ACCT_OUT_NAME, ");
		sb.append("       CD.ACCT_OUT, ");
		sb.append("       CD.ACCT_IN_NAME, ");
		sb.append("       CD.ACCT_IN, ");
		sb.append("       CD.TX_AMT, ");
		sb.append("       CD.ACC_AMT, ");

		sb.append("       CD.NOTE_TYPE, "); // #0001258_WMS-CR-20220829-01_因應高齡客戶交易加強關懷擬優化內控報表欄位
		sb.append("       CD.NOTE, ");
		sb.append("       CD.NOTE2, ");
		sb.append("       CD.NOTE3, ");
		sb.append("       CD.RECORD_SEQ, ");
		sb.append("       CD.HR_ATTR, ");
		sb.append("       CD.FIRSTUPDATE, ");

		sb.append("       CD.VERSION, ");
		sb.append("       CD.CREATETIME, ");
		sb.append("       CD.CREATOR, ");
		sb.append("       CD.MODIFIER, ");
		sb.append("       CD.LASTUPDATE, ");
		
		sb.append("       CD.CUST_AGE ");
		sb.append("FROM TBPMS_TXN_LARGE_WITHDRAWAL CD ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO ORG ON CD.BRANCH_NBR = ORG.BRANCH_NBR ");
		sb.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON CD.EMP_ID = MEM.EMP_ID AND CD.BRANCH_NBR = MEM.DEPT_ID AND CD.DATA_DATE BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sb.append("WHERE 1 = 1 ");
		
		// 資料統計日期-起
		if (inputVO.getsDate() != null) {
			sb.append("AND TRUNC(CD.CREATETIME) >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}

		// 資料統計日期-迄
		if (inputVO.geteDate() != null) {
			sb.append("AND TRUNC(CD.CREATETIME) <= TRUNC(:end) ");
			queryCondition.setObject("end", inputVO.geteDate());
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) { 
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("AND ORG.BRANCH_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("AND ORG.BRANCH_AREA_ID = :branchAreaID ");
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("AND ORG.REGION_CENTER_ID = :regionCenterID ");
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			} 
			
			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("AND CD.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("AND (");
				sb.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE CD.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("  OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append(")");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("AND CD.RM_FLAG = 'U' ");
		}
		
		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
        if(StringUtils.equals("Y", inputVO.getFrom181())){
        	sb.append("AND FIRSTUPDATE IS NULL ");
        }
        
        // #0001258_WMS-CR-20220829-01_因應高齡客戶交易加強關懷擬優化內控報表欄位 : 新增「行員員編ID」，以利查詢行員區間內產出相關資訊。
 		if (StringUtils.isNotBlank(inputVO.getAccOutNameSearch())) { //查詢及匯出時，showNoData=Y; 首頁待辦是像為空值，不須顯示"本日無異動"
 			sb.append("AND CD.ACCT_OUT_NAME LIKE :accOutNameSearch ");
 			queryCondition.setObject("accOutNameSearch", "%" + inputVO.getAccOutNameSearch() + "%");
 		}
 		
 		// #0001258_WMS-CR-20220829-01_因應高齡客戶交易加強關懷擬優化內控報表欄位 : 依「首次建立時間」是否為空值判斷，以利填寫檢核說明時可篩選。
 		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
 			switch (inputVO.getNoteStatus()) {
 				case "01":
 					sb.append("AND CD.FIRSTUPDATE IS NOT NULL ");
 					break;
 				case "02":
 					sb.append("AND CD.FIRSTUPDATE IS NULL ");
 					break;
 			}
 		}
		
		sb.append("ORDER BY CD.DATA_DATE DESC, ");
		sb.append("         DECODE(REPLACE(REPLACE(REPLACE(ORG.REGION_CENTER_NAME, '分行業務', ''), '處', ''), ' 合計', ''), '一', 1, '二', 2, '三', 3, '四', 4, '五', 5, '六', 6, '七', 7, '八', 8, '九', 9, '十', 10, 99), ");
		sb.append("         ORG.REGION_CENTER_NAME, ");
		sb.append("         ORG.BRANCH_AREA_ID, ");
		sb.append("         ORG.BRANCH_AREA_NAME, ");
		sb.append("         CD.BRANCH_NBR, ");
		sb.append("         ORG.BRANCH_NAME ");

		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		return outputVO;
	}

	// 更新資料，在前端篩選編輯過的資料。
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS428InputVO inputVO = (PMS428InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_TXN_LARGE_WITHDRAWAL ");
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
			
			sb.append("WHERE SEQNO = :seqNO ");
			
			// KEY
			queryCondition.setObject("seqNO", map.get("SEQNO"));
			
			// CONTENT
			queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
			queryCondition.setObject("hrAttr", map.get("HR_ATTR"));
			queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			queryCondition.setObject("note", map.get("NOTE"));
			queryCondition.setObject("note2", map.get("NOTE2"));
			queryCondition.setObject("note3", map.get("NOTE3"));
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
		
		sendRtnObject(null);
	}
	
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException, FileNotFoundException, IOException {

		XmlInfo xmlInfo = new XmlInfo();

		PMS428InputVO inputVO = (PMS428InputVO) body;

		List<Map<String, Object>> list = inputVO.getExportList();

		String fileName = "理財戶大額轉出報表_" + sdfYYYYMMDD.format(inputVO.getsDate()) + "-" + sdfYYYYMMDD.format(inputVO.geteDate()) + ".xlsx";
		String uuid = UUID.randomUUID().toString();
		String Path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);

		String filePath = Path + uuid;

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("理財戶大額轉出報表");
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
								"資料日期",
								"資料來源",
								"理專歸屬行",
								"理專姓名",
								"交易日期",
								"轉出姓名",
								"高齡客戶", 
								"轉出帳號",
								"轉入姓名",
								"轉入帳號",
								"交易金額",
								"累積金額",
								"查證方式",
								"電訪錄音編號",
								"資金來源或帳戶關係",
								"具體原因或用途",
								"初判異常轉法遵部調查",
								"首次建立時間",
								"最新異動人員",
								"最新異動日期 "};
		String[] mainLine	= { "RM_FLAG", 
								"CREATETIME",
								"DATA_SOURCE",
								"BRANCH_NBR",
								"EMP_NAME",
								"TXN_DATE",
								"ACCT_OUT_NAME",
								"CUST_AGE", 
								"ACCT_OUT",
								"ACCT_IN_NAME",
								"ACCT_IN",
								"TX_AMT",
								"ACC_AMT",
								"NOTE",
								"RECORD_SEQ", 
								"NOTE2",
								"NOTE3",
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
					case "CREATETIME":
					case "TXN_DATE":
						if (StringUtils.isNotEmpty(checkIsNull(map, mainLine[i]))) {
							cell.setCellValue(sdfYYYYMMDD_DASH.format(sdfYYYYMMDD_DASH.parse((String) map.get(mainLine[i]))));
						} else {
							cell.setCellValue("");
						}
						break;
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
					case "TX_AMT":
					case "ACC_AMT":
						cell.setCellValue(currencyFormat(map, mainLine[i]));
						break;
					case "CUST_AGE":
						cell.setCellValue(StringUtils.equals("", checkIsNull(map, mainLine[i])) ? "" : (new BigDecimal(checkIsNull(map, mainLine[i])).compareTo(new BigDecimal("65")) >= 0 ? checkIsNull(map, mainLine[i]) : ""));
						break;
					case "NOTE":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");

						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(mainLine[i]));
						}
						
						cell.setCellValue(note);
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
	
	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}

}