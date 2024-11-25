package com.systex.jbranch.app.server.fps.pms418;

import static org.apache.commons.lang.StringUtils.defaultString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
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
 
/**
 * @Description 理財戶同一IP交易警示報表
 */
@Component("pms418")
@Scope("prototype")
public class PMS418 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");	
	LinkedHashMap<String, String> EXCLUDE_IP = new LinkedHashMap<String, String>();

	public PMS418 () {
		EXCLUDE_IP.put("分行代碼", "BRANCH_NBR");
		EXCLUDE_IP.put("IP", "IP_ADDR");
		EXCLUDE_IP.put("排除起日", "START_DATE");
		EXCLUDE_IP.put("排除迄日", "END_DATE");
	}
	
	// 查詢-理財戶同一IP交易警示報表-for後端進入
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS418OutputVO outputVO = new PMS418OutputVO();
	    outputVO = this.queryData(body);
	        
	    sendRtnObject(outputVO);
	}
	
	// 查詢-理財戶同一IP交易警示報表-for前端進入
	public PMS418OutputVO queryData(Object body) throws JBranchException, ParseException {
		
		initUUID();
		
		PMS418InputVO inputVO = (PMS418InputVO) body;
		PMS418OutputVO outputVO = new PMS418OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		// #0731: WMS-CR-20210906-01_調整同一IP及分行人員資金往來報表 => 交易日為同一天+交易項目相同+同一IP+同一客戶 =簡化筆數 
		sb.append("SELECT CASE WHEN RPT.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("       RPT.SEQ, ");
		sb.append("       CASE WHEN TRUNC(RPT.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("       RPT.SNAP_DATE, ");
		sb.append("       RPT.CREATETIME, ");
		sb.append("       RPT.BRANCH_NBR, ");
		sb.append("       RPT.BRANCH_NAME, ");
		sb.append("       RPT.CUST_ID, ");	
		sb.append("       MAST.CUST_NAME, ");
		sb.append("       RPT.SUPERVISOR_FLAG, ");
		sb.append("       RPT.NOTE_TYPE, "); // #0879: WMS-CR-20220302-01_因應金檢查核及品保官會議，擬優化業管系統內控報表
		sb.append("       RPT.NOTE, ");
		sb.append("       RPT.CUST_BASE, ");
		sb.append("       RPT.NOTE2, ");
		sb.append("       RPT.NOTE3, ");
		sb.append("       RPT.RECORD_SEQ, ");
		sb.append("       RPT.AO_EMP_NAME, ");
		sb.append("       RPT.TXN_TIME, ");
		sb.append("       RPT.TASK_NM, ");
		sb.append("       RPT.IP_ADDR, ");
		sb.append("       RPT.MODIFIER, ");
		sb.append("       RPT.LASTUPDATE, ");
		sb.append("       RPT.FIRSTUPDATE, ");
		sb.append("       RPT.EMP_DLG, ");
		sb.append("       TO_CHAR(TXN_TIME, 'YYYYMMDD') AS TXN_DAY, ");
		sb.append("       RPT.CUST_AGE ");
		sb.append("FROM TBPMS_IP_EBALT_RPT RPT ");
		sb.append("LEFT JOIN VWORG_DEFN_INFO ORG ON RPT.BRANCH_NBR = ORG.BRANCH_NBR ");
		sb.append("LEFT JOIN TBCRM_CUST_MAST MAST ON RPT.CUST_ID = MAST.CUST_ID ");
		sb.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON RPT.AO_EMP_ID = MEM.EMP_ID AND RPT.BRANCH_NBR = MEM.DEPT_ID AND RPT.TXN_TIME BETWEEN MEM.START_TIME AND MEM.END_TIME ");
		sb.append("WHERE 1 = 1 ");
		
		// #0731: WMS-CR-20210906-01_調整同一IP及分行人員資金往來報表 => 交易日為同一天+交易項目相同+同一IP+同一客戶 =簡化筆數 
		sb.append("AND (RPT.IP_ADDR, RPT.TASK_NM, RPT.CUST_ID, RPT.TXN_TIME) IN ( ");
		sb.append("  SELECT IP_ADDR, TASK_NM, CUST_ID, MAX(TXN_TIME) AS MAX_TXN_TIME ");
		sb.append("  FROM TBPMS_IP_EBALT_RPT ");
		sb.append("  GROUP BY TO_CHAR(TXN_TIME, 'YYYYMMDD'), IP_ADDR, TASK_NM, CUST_ID ");
		sb.append(") ");
		
		sb.append("AND RPT.AO_CODE IS NOT NULL ");
		
		if (StringUtils.isNotEmpty(inputVO.getIpAddrSearch())) {
			sb.append("AND RPT.IP_ADDR = :ipAddrSearch ");
			queryCondition.setObject("ipAddrSearch", inputVO.getIpAddrSearch());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getCustIDSearch())) {
			sb.append("AND RPT.CUST_ID = :custIDSearch ");
			queryCondition.setObject("custIDSearch", inputVO.getCustIDSearch());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("AND RPT.FIRSTUPDATE IS NOT NULL ");
					break;
				case "02":
					sb.append("AND RPT.FIRSTUPDATE IS NULL ");
					break;
			}
		}
		
		if (null != inputVO.getsCreDate()) {
			sb.append("AND TRUNC(RPT.CREATETIME) >= TRUNC(:sDate) ");
			queryCondition.setObject("sDate", inputVO.getsCreDate());
		}
		
		if (null != inputVO.geteCreDate()) {
			sb.append("AND TRUNC(RPT.CREATETIME) <= TRUNC(:eDate) ");
			queryCondition.setObject("eDate", inputVO.geteCreDate());
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("AND RPT.BRANCH_NBR = :branch ");
				queryCondition.setObject("branch", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sb.append("AND ( ");
				sb.append("  (RPT.RM_FLAG = 'B' AND ORG.BRANCH_AREA_ID = :area) ");
				
				if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
					armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("  OR (RPT.RM_FLAG = 'U' AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE RPT.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :area )) ");
				}
			
				sb.append(") ");
				queryCondition.setObject("area", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("AND ORG.REGION_CENTER_ID = :region ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
			}

			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) && 
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("AND RPT.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("AND (");
				sb.append("     EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE RPT.AO_EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("  OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append(")");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("AND RPT.RM_FLAG = 'U' ");
		}

		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sb.append("AND FIRSTUPDATE IS NULL ");
		}
		
		sb.append("ORDER BY RPT.SNAP_DATE, RPT.IP_ADDR, RPT.BRANCH_NBR, RPT.TXN_TIME, RPT.CUST_ID ");
		
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		outputVO.setResultList(list);
		outputVO.setReportList(list);
		
		return outputVO;
	}
	
	// 更新-理財戶同一IP交易警示報表
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS418InputVO inputVO = (PMS418InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_IP_EBALT_RPT ");
			sb.append("SET EMP_DLG = :empFlag, ");
			sb.append("    NOTE_TYPE = :noteType, ");
			sb.append("    NOTE = :note, ");
			sb.append("    CUST_BASE = :custBase, ");
			sb.append("    NOTE2 = :note2, ");
			sb.append("    NOTE3 = :note3, ");
			sb.append("    RECORD_SEQ = :recordSEQ, ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = sysdate ");
			
			if (null == map.get("FIRSTUPDATE")) {
				sb.append("    , FIRSTUPDATE = sysdate ");
			}
			
			sb.append("WHERE TO_CHAR(TXN_TIME, 'YYYYMMDD') = :txnDay ");
			sb.append("AND TASK_NM = :taskNM ");
			sb.append("AND IP_ADDR = :ipAddr ");
			sb.append("AND CUST_ID = :custID ");
			
			// KEY
			queryCondition.setObject("txnDay", map.get("TXN_DAY"));
			queryCondition.setObject("taskNM", map.get("TASK_NM"));
			queryCondition.setObject("ipAddr", map.get("IP_ADDR"));
			queryCondition.setObject("custID", map.get("CUST_ID"));
			
			// CONTENT
			queryCondition.setObject("empFlag", map.get("EMP_DLG"));
			queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
			queryCondition.setObject("note", map.get("NOTE"));
			queryCondition.setObject("custBase", map.get("CUST_BASE"));
			queryCondition.setObject("note2", map.get("NOTE2"));
			queryCondition.setObject("note3", map.get("NOTE3"));
			queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
		
		sendRtnObject(null);
	}
	
	// 查詢-理財戶同一IP交易警示報表-明細
	public void queryDetail(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS418InputVO inputVO = (PMS418InputVO) body;
		PMS418OutputVO outputVO = new PMS418OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT TXN_TIME ");
		sb.append("FROM TBPMS_IP_EBALT_RPT ");
		sb.append("WHERE 1 = 1 ");
		
		sb.append("AND TO_CHAR(TXN_TIME, 'YYYYMMDD') = :txnDay ");
		sb.append("AND TASK_NM = :taskNM ");
		sb.append("AND IP_ADDR = :ipAddr ");
		sb.append("AND CUST_ID = :custID ");
		
		queryCondition.setObject("txnDay", inputVO.getTxnDay());
		queryCondition.setObject("taskNM", inputVO.getTaskNM());
		queryCondition.setObject("ipAddr", inputVO.getIpAddr());
		queryCondition.setObject("custID", inputVO.getCustID());
		
		sb.append("ORDER BY TXN_TIME ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setDetailList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}

	// 匯出-理財戶同一IP交易警示報表
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		List<Map<String, Object>> list = this.queryData(body).getReportList();
		
		String[] csvHeader = { 	"私銀註記", "報表日期", "分行代碼", "分行名稱", 
								"客戶ID", "客戶姓名", "高齡客戶", "客戶所屬理專", 
								"交易時間", "交易項目", "IP", 
								"查證方式", "電訪錄音編號", "客戶背景或關係", "具體說明", "初判異常轉法遵部調查", "首次建立時間", "最新異動人員", "最新異動日期" };
		String[] csvMain   = { 	"RM_FLAG", "CREATETIME", "BRANCH_NBR", "BRANCH_NAME", 
								"CUST_ID", "CUST_NAME", "CUST_AGE", "AO_EMP_NAME", 
								"TXN_TIME", "TASK_NM", "IP_ADDR", 
								"NOTE", "RECORD_SEQ", "NOTE2", "NOTE3", "EMP_DLG", "FIRSTUPDATE", "MODIFIER", "LASTUPDATE" };

		XmlInfo xmlInfo = new XmlInfo();

		List<Object[]> csvData = new ArrayList<Object[]>();
		if (CollectionUtils.isEmpty(list)) {
			String[] records = new String[2];
			records[0] = "查無資料";
			csvData.add(records);
		} else {
			SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

			for (Map<String, Object> map : list) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "CREATETIME":
						case "TXN_TIME":
						case "FIRSTUPDATE":
						case "LASTUPDATE":
							records[i] = formatDate(map.get(csvMain[i]), timeSdf); // 首次建立時間
							break;
						case "CUST_ID":
							records[i] = DataFormat.getCustIdMaskForHighRisk(StringUtils.defaultString((String) map.get(csvMain[i])));
							break;
						case "EMP_DLG":
//							records[i] = formatFlag(StringUtils.defaultString((String) map.get(csvMain[i])));
							switch (checkIsNull(map, csvMain[i]) ) {
								case "N":
									records[i] = "否-非行員代客戶操作";
									break;
								case "Y":
									records[i] = "是-通報有異常";
									break;
							}
							break;
						case "MODIFIER":
							records[i] = "=\"" + StringUtils.defaultString((String) map.get(csvMain[i])) + "\"";
							break;
						case "NOTE":
							String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");

							if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
								note = note + "：" + StringUtils.defaultString((String) map.get(csvMain[i]));
							}
							
							records[i] = (StringUtils.isNotEmpty(note) ? note : "");
							break;
						case "CUST_AGE":
							records[i] = StringUtils.equals("", checkIsNull(map, csvMain[i])) ? "" : (new BigDecimal(checkIsNull(map, csvMain[i])).compareTo(new BigDecimal("65")) >= 0 ? checkIsNull(map, csvMain[i]) : "");
							break;
						case "RECORD_SEQ":
							records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
							break;
						case "NOTE2":
							String note2 = (String) xmlInfo.getVariable("PMS.CUST_BASE_IP", (String) map.get("CUST_BASE"), "F3");

							if (null != map.get("CUST_BASE") && StringUtils.equals("O", (String) map.get("CUST_BASE"))) {
								note2 = note2 + "：" + StringUtils.defaultString((String) map.get(csvMain[i]));
							}
							
							records[i] = note2;
							break;
						default :
							if (null != map.get(csvMain[i])) {
								records[i] = defaultString(map.get(csvMain[i]).toString());
							} else {
								records[i] = "";
							}
							break;
					}
				}
				
				csvData.add(records);
			}
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);
		notifyClientToDownloadFile(csv.generateCSV(), "理財戶同一IP交易日報_" + dateSdf.format(new Date()) + ".csv");
	}
	
	// 查詢-排除IP
	public void queryExcludeIP(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS418OutputVO outputVO = new PMS418OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT IP_ADDR, ");
		sb.append("       BRANCH_NBR, ");
		sb.append("       START_DATE, ");
		sb.append("       END_DATE, ");
		sb.append("       TO_CHAR(CREATETIME, 'yyyy-MM-dd HH24:MI:SS') AS CREATETIME, ");
		sb.append("       CREATOR, ");
		sb.append("       TO_CHAR(LASTUPDATE, 'yyyy-MM-dd HH24:MI:SS') AS LASTUPDATE, ");
		sb.append("       MODIFIER ");
		sb.append("FROM TBPMS_IP_EBALT_EXCLUDE_LIST ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND START_DATE <= TO_CHAR(SYSDATE, 'YYYYMMDD') ");
		sb.append("AND END_DATE >= TO_CHAR(SYSDATE, 'YYYYMMDD') ");
		sb.append("ORDER BY START_DATE, END_DATE ");
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setExcludeList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	// 下載-排除IP範例
	public void getExample(Object body, IPrimitiveMap header) throws JBranchException {
		
		CSVUtil csv = new CSVUtil();

		String fileName = "排除IP清單檔(範例).csv";
		csv.setHeader(EXCLUDE_IP.keySet().toArray(new String[EXCLUDE_IP.keySet().size()]));
		
		// 設定表頭
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		
		sendRtnObject(null);
	}
	
	// 刪除-排除IP
	public void delExcludeIP(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS418InputVO inputVO = (PMS418InputVO) body;
		PMS418OutputVO outputVO = new PMS418OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("DELETE FROM TBPMS_IP_EBALT_EXCLUDE_LIST ");
		sb.append("WHERE BRANCH_NBR = :branchNbr ");
		sb.append("AND IP_ADDR = :ipAddr ");
		
		queryCondition.setObject("branchNbr", inputVO.getBranchNbrDel());
		queryCondition.setObject("ipAddr", inputVO.getIpAddrDel());
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
		
		sendRtnObject(outputVO);
	}
	
	// 上傳-排除IP
	public void updExcludeIP(Object body, IPrimitiveMap header) throws Exception {
		
		WorkStation ws = DataManager.getWorkStation(uuid);

		PMS418InputVO inputVO = (PMS418InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		if (!StringUtils.isBlank(inputVO.getFILE_NAME())) {
			// 1. 整檔清空
			queryCondition.setQueryString("TRUNCATE TABLE TBPMS_IP_EBALT_EXCLUDE_LIST ");
			dam.exeUpdate(queryCondition);
			
			// 2. 讀檔
			File csvFile = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFILE_NAME());
			
			FileInputStream fi = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fi, "BIG5"));
			
			// 將輸入資料轉型成List<Map<[資料庫欄位名稱], [欄位值]>> 方便下一步驟執行
			String[] head = br.readLine().split(",");
			String line = null;
			List<Map<String, Object>> inputLst = new ArrayList<Map<String, Object>>();
			
			while((line = br.readLine()) != null) {
				String[] data = line.split(",");
				HashMap<String, Object> dataMap = new HashMap<String, Object>();
				
				for (int i = 0; i < data.length; i++) {
					dataMap.put(EXCLUDE_IP.get(head[i]), data[i]);
				}
				
				inputLst.add(dataMap);
			}
			
			// 3. 寫入
			for (Map<String, Object> dataMap: inputLst) {
				sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
				sb.append("INSERT INTO TBPMS_IP_EBALT_EXCLUDE_LIST ( ");
				sb.append("  IP_ADDR, ");
				sb.append("  BRANCH_NBR, ");
				sb.append("  START_DATE, ");
				sb.append("  END_DATE, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				sb.append("VALUES ( ");
				sb.append("  :IP_ADDR, ");
				sb.append("  :BRANCH_NBR, ");
				sb.append("  :START_DATE, ");
				sb.append("  :END_DATE, ");
				sb.append("  :VERSION, SYSDATE, :CREATOR, :MODIFIER, SYSDATE ");
				sb.append(") ");
				
				queryCondition.setObject("IP_ADDR"    , dataMap.get("IP_ADDR"));
				queryCondition.setObject("BRANCH_NBR" , dataMap.get("BRANCH_NBR"));
				queryCondition.setObject("START_DATE" , dataMap.get("START_DATE"));
				queryCondition.setObject("END_DATE"   , dataMap.get("END_DATE"));
				queryCondition.setObject("VERSION"    , 0);
				queryCondition.setObject("CREATOR"    , ws.getUser().getUserID());
				queryCondition.setObject("MODIFIER"   , ws.getUser().getUserID());
				
				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);
			}
		}
		
		sendRtnObject(null);
	}
	
	// 匯出-排除IP
	public void expExcludeIP(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS418InputVO inputVO = (PMS418InputVO) body;
		
		List<Map<String, Object>> exportLst = inputVO.getExportList();
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		String fileName = "排除IP清單檔";
		String[] csvHeader = new String[] { "分行代碼", "IP", "排除起日", "排除迄日", "建立日期"};
		String[] csvMain = new String[] { "BRANCH_NBR", "IP_ADDR", "START_DATE", "END_DATE", "CREATETIME"};
		
		if (exportLst.size() > 0) {
			for (Map<String, Object> map : exportLst) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					records[i] = checkIsNull(map, csvMain[i]);
				}
		
				csvData.add(records);
			}
		
			CSVUtil csv = new CSVUtil();
		
			// 設定表頭
			csv.setHeader(csvHeader);
			// 添加明細的List
			csv.addRecordList(csvData);
			
			// 執行產生csv并收到該檔的url
			String url = csv.generateCSV();
		
			// 將url送回FlexClient存檔
			notifyClientToDownloadFile(url, fileName + "_" + sdfYYYYMMDD.format(new Date()) + ".csv");
		}
		
		sendRtnObject(null);
	}
	
	// 格式時間
	private String formatDate(Object date, SimpleDateFormat sdf) {
		
		if (date != null)
			return sdf.format(date);
		else
			return "";
	}

	// Flag Y="是"，N="否"
	private String formatFlag(String flag) {
		
		return flag.equals("Y") ? "是" : flag.equals("N") ? "否" : "";
	}

	// 檢查Map取出欄位是否為Null
	private String checkIsNull(Map<String, Object> map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}