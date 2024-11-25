package com.systex.jbranch.app.server.fps.pms361;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @Description 分行人員使用行銀進行非本人帳號交易日報
 * @Author Eli
 * @date 20190801 匯出報表客戶 ID 欄位遮蔽處理
 *
 */
@Component("pms361")
@Scope("prototype")
public class PMS361 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void inquire(Object body, IPrimitiveMap header) throws Exception { 
		
		PMS361OutputVO outputVO = new PMS361OutputVO();
		outputVO = this.inquire(body);

		sendRtnObject(outputVO);
	}

	public PMS361OutputVO inquire(Object body) throws Exception {
		
		initUUID();
		PMS361InputVO inputVO = (PMS361InputVO) body;
		PMS361OutputVO return_VO = new PMS361OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		// get privilegeid
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE ROLEID = :roleID ");
		
		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		queryCondition.setObject("roleID", loginRoleID);
		
		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> priList = dam.exeQuery(queryCondition);
		
		//
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT ROWNUM, A.* ");
		sb.append("FROM ( ");
		sb.append("  SELECT DISTINCT CASE WHEN note.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("         rpt.ACCESS_TIME, ");
		sb.append("         CASE WHEN TRUNC(rpt.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("         org.BRANCH_NBR, ");
		sb.append("         org.BRANCH_NAME, ");
		sb.append("         rpt.EMP_ID, ");
		sb.append("         rpt.EMP_NAME, ");
		sb.append("         rpt.AO_CODE, ");
		sb.append("         rpt.TXN_TYP, ");
		sb.append("         rpt.CUST_ID, ");
		sb.append("         rpt.CUST_NAME, ");
		sb.append("         rpt.CUST_AO_CODE, ");
		sb.append("         rpt.DEVICE_ID, ");
		sb.append("         TO_CHAR(note.CREATETIME, 'YYYY/MM/DD') AS NOTE_CREATETIME, ");
		sb.append("         note.SUPERVISOR_FLAG, ");
		sb.append("         note.NOTE_TYPE, "); // #0001258_WMS-CR-20220829-01_因應高齡客戶交易加強關懷擬優化內控報表欄位
		sb.append("         note.NOTE_TIME, ");
		sb.append("         note.CUST_BASE,  ");
		sb.append("         note.NOTE,  ");
		sb.append("         note.NOTE2, ");
		sb.append("         note.NOTE3, ");
		sb.append("         note.RECORD_SEQ, ");
		sb.append("         note.WARNING_YN, ");
		sb.append("         note.RELATION, ");
		sb.append("         note.MODIFIER, ");
		sb.append("         note.LASTUPDATE, ");
		sb.append("         note.FIRSTUPDATE, ");
		sb.append("         rpt.CUST_AGE ");
		sb.append("  FROM TBPMS_BNKALT_RPT rpt ");
		sb.append("  INNER JOIN TBPMS_BNKALT_RPT_NOTE note ON rpt.ACCESS_TIME = note.ACCESS_TIME and rpt.EMP_ID = note.EMP_ID and rpt.DEVICE_ID = note.DEVICE_ID and rpt.BRANCH_NBR = note.BRANCH_NBR ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON rpt.EMP_ID = MEM.EMP_ID AND rpt.BRANCH_NBR = MEM.DEPT_ID AND rpt.ACCESS_TIME BETWEEN MEM.START_TIME AND MEM.END_TIME ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO org ON org.BRANCH_NBR = rpt.BRANCH_NBR ");
		sb.append("  WHERE 1 = 1 ");
		
		// 20231121 add by ocean : 丁怡文=>依角色區分可視範圍041、042
		switch((String) priList.get(0).get("PRIVILEGEID")) {
			case "041":
			case "042":
				break;
			default:
				sb.append("  AND NVL(note.NOTE, ' ') <> '本日無資料' ");
				break;
		}
		
		// where
		if (inputVO.getsDate() != null) {
			sb.append("  AND TRUNC(rpt.CREATETIME) >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}
		
		if (inputVO.geteDate() != null) {
			sb.append("  AND TRUNC(rpt.CREATETIME) < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.geteDate());
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNumeric(inputVO.getBranch_nbr()) && StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("  AND org.BRANCH_NBR = :bra_nbr ");
				queryCondition.setObject("bra_nbr", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	
				sb.append("  AND ( ");
				sb.append("    (note.RM_FLAG = 'B' AND org.BRANCH_AREA_ID = :area) ");
				
				if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) ||
					armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sb.append("    OR (note.RM_FLAG = 'U' AND EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE rpt.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :area )) ");
				}
			
				sb.append("  ) ");
				queryCondition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("  AND org.REGION_CENTER_ID = :region ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
			}
			
			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) && 
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("  AND note.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("  AND (");
				sb.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE rpt.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("    OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append("  )");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("  AND note.RM_FLAG = 'U' ");
		}

		//由工作首頁CRM181過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sb.append("  AND note.FIRSTUPDATE IS NULL ");
		}

		//匯出時需要顯示"本日無異動"，畫面及待辦事項不需要顯示
		if (StringUtils.isBlank(inputVO.getShowNoData())) { //查詢及匯出時，showNoData=Y; 首頁待辦是像為空值，不須顯示"本日無異動"
			sb.append("  AND rpt.CUST_ID is not null ");
		}
		//
		
		// #0001258_WMS-CR-20220829-01_因應高齡客戶交易加強關懷擬優化內控報表欄位 : 新增「行員員編ID」，以利查詢行員區間內產出相關資訊。
		if (StringUtils.isNotBlank(inputVO.getEmpIDSearch())) { //查詢及匯出時，showNoData=Y; 首頁待辦是像為空值，不須顯示"本日無異動"
			sb.append("  AND rpt.EMP_ID = :empIDSearch ");
			queryCondition.setObject("empIDSearch", inputVO.getEmpIDSearch());
		}
		
		// #0001258_WMS-CR-20220829-01_因應高齡客戶交易加強關懷擬優化內控報表欄位 : 依「首次建立時間」是否為空值判斷，以利填寫檢核說明時可篩選。
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("  AND note.FIRSTUPDATE IS NOT NULL ");
					break;
				case "02":
					sb.append("  AND ( note.NOTE_TYPE <> 'O' or note.NOTE_TYPE is null) ");
					sb.append("  AND note.FIRSTUPDATE IS NULL ");
					break;
			}
		}
		
		sb.append("  ORDER BY rpt.ACCESS_TIME DESC ");
		sb.append(") A ");
		
		queryCondition.setQueryString(sb.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));

		return return_VO;
	}

	/** 匯出報表 **/
	public void export(Object body, IPrimitiveMap header) throws Exception {
		
		PMS361InputVO inputVO = (PMS361InputVO) body;
		
		String[] csvHeader = { 	"私銀註記", "資料日期", "分行代碼", "分行名稱", "行員員編", "行員姓名", "AO CODE", 
								"交易時間", "交易項目", "客戶ID", "客戶姓名", "高齡客戶", "客戶所屬理專", 
								"查證方式", "電訪錄音編號", "客戶背景或關係", "具體說明", "初判異常轉法遵部調查", "首次建立時間", 
								"最新異動人員", "最新異動日期", "DEVICE ID" };
		String[] csvMain   = { 	"RM_FLAG", "NOTE_CREATETIME", "BRANCH_NBR", "BRANCH_NAME", "EMP_ID", "EMP_NAME", "AO_CODE", 
								"ACCESS_TIME", "TXN_TYP", "CUST_ID", "CUST_NAME", "CUST_AGE", "CUST_AO_CODE", 
								"NOTE", "RECORD_SEQ", "NOTE2", "NOTE3", "WARNING_YN", "FIRSTUPDATE", 
								"MODIFIER", "LASTUPDATE", "DEVICE_ID" };
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		XmlInfo xmlInfo = new XmlInfo();

		for (Map<String, Object> map : inputVO.getTotalList()) {
			String[] records = new String[csvHeader.length];
			for (int i = 0; i < csvHeader.length; i++) {
				switch (csvMain[i]) {
					case "NOTE_CREATETIME":
					case "ACCESS_TIME":
					case "MODIFIER":
					case "LASTUPDATE":
						records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
						break;
					case "CUST_ID":
						records[i] = DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, csvMain[i]));
						break;
					case "CUST_AGE":
						records[i] = StringUtils.equals("", checkIsNull(map, csvMain[i])) ? "" : (new BigDecimal(checkIsNull(map, csvMain[i])).compareTo(new BigDecimal("65")) >= 0 ? checkIsNull(map, csvMain[i]) : "");
						break;
					case "NOTE":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");

						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(csvMain[i]));
						}
						
						records[i] = note;
						break;
					case "NOTE2":
						String note2 = (String) xmlInfo.getVariable("PMS.CUST_BASE", (String) map.get("CUST_BASE"), "F3");

						if (null != map.get("CUST_BASE") && StringUtils.equals("O", (String) map.get("CUST_BASE"))) {
							note2 = note2 + "：" + StringUtils.defaultString((String) map.get(csvMain[i]));
						}
						
						records[i] = note2;
						break;
					case "RECORD_SEQ":
						records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
						break;
					default :
						records[i] = checkIsNull(map, csvMain[i]);
						break;
				}
			}
			
			csvData.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);
		notifyClientToDownloadFile(csv.generateCSV(), "行銀交易查核日報_" + sdf.format(new Date()) + ".csv");
	}

	private String checkIsNull(Map map, String key) {
		
		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	public void update(Object body, IPrimitiveMap header) throws Exception {
		
		PMS361InputVO inputVO = (PMS361InputVO) body;
		dam = this.getDataAccessManager();
		String loginID = DataManager.getWorkStation(uuid).getUser().getCurrentUserId();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Map<String, Object> map : inputVO.getTotalList()) {
			Manager.manage(this.getDataAccessManager())
			.append("update TBPMS_BNKALT_RPT_NOTE ")
			.append("SET NOTE = :note, ")
			.append("    NOTE2 = :note2, ")
			.append("    NOTE3 = :note3, ")
			.append("    RECORD_SEQ = :recordSEQ, ")
			.append("    NOTE_TYPE = :noteType, ")
			.append("    CUST_BASE = :custBase, ")
			.append("    WARNING_YN = :warning_yn, ")
			.append("    MODIFIER = :modifier, ")
			.append("    LASTUPDATE = sysdate ")
			
			/** 第一次使用者儲存編輯的資料，更新其 FIRSTUPDATE（首次建立時間） 欄位 **/
			.condition(map.get("FIRSTUPDATE") == null, ", FIRSTUPDATE = sysdate ")

			.append("where ACCESS_TIME = :accessTime ")
			.append("and BRANCH_NBR = :branch ")
			.append("and EMP_ID = :empId ")
			.append("and DEVICE_ID = :deviceId ")
			
			//.put("superFlag", map.get("SUPERVISOR_FLAG"))
			.put("note", map.get("NOTE"))
			.put("note2", map.get("NOTE2"))
			.put("note3", map.get("NOTE3"))
			.put("recordSEQ", map.get("RECORD_SEQ"))
			.put("noteType", map.get("NOTE_TYPE"))
			.put("custBase", map.get("CUST_BASE"))
			.put("warning_yn", map.get("WARNING_YN"))
			.put("modifier", loginID)
			.put("accessTime", new Timestamp(sdf.parse(map.get("ACCESS_TIME").toString()).getTime()))
			.put("branch", map.get("BRANCH_NBR"))
			.put("empId", map.get("EMP_ID"))
			.put("deviceId", map.get("DEVICE_ID")).update();
		}

		this.sendRtnObject(null);
	}

}