package com.systex.jbranch.app.server.fps.pms414;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @Description 公用電腦查核日報
 * @Author 20161123 Frank
 * @Editor 20170126 Kevin
 * @Editor 20190801 Eli   : 匯出報表客戶 ID 欄位遮蔽處理
 * @Editor 20220512 Ocean : WMS-CR-20220302-01_因應金檢查核及品保官會議，擬優化業管系統內控報表
 * @Editor 20220512 Sam Tu: #1383 調整查詢條件
 */

@Component("pms414")
@Scope("prototype")
public class PMS414 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	/* 查詢-前端進入 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS414OutputVO outputVO = new PMS414OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}

	/* 查詢-後端進入 */
	public PMS414OutputVO queryData(Object body) throws JBranchException, ParseException {

		PMS414InputVO inputVO = (PMS414InputVO) body;
		PMS414OutputVO outputVO = new PMS414OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ROWNUM, T.*, R.REASON ");
		sql.append("FROM ( ");
		sql.append("  SELECT SEQ, ");
		sql.append("         REGION_CENTER_ID, ");
		sql.append("         REGION_CENTER_NAME, ");
		sql.append("         BRANCH_AREA_ID, ");
		sql.append("         BRANCH_AREA_NAME, ");
		sql.append("         DATA_DATE, ");
		sql.append("         CASE WHEN TRUNC(DATA_DATE) >= TO_DATE('20210326', 'YYYYMMDD') THEN 'Y' ELSE 'N' END AS CAN_UPD_FIRSTDATE_FLG, ");
		sql.append("         BRANCH_NBR, ");
		sql.append("         BRANCH_NAME, ");
		sql.append("         CUST_ID, ");
		sql.append("         CUST_NAME, ");
		sql.append("         AO_CODE, ");
		sql.append("         SPECIFIC_FLAG, ");
		sql.append("         TRADE_DATE, ");
		sql.append("         TRADE_ITEM, ");
		sql.append("         STAFF_THERE_FLAG, ");
		sql.append("         MEETING, ");
		sql.append("         MEETING_HOUR, ");
		sql.append("         MEETING_MIN,");
		sql.append("         OBO_CUST_FLAG, ");
		sql.append("         AVOID_FLAG, ");
		sql.append("         NEARBY_CUST_FLAG, ");
		sql.append("         EXPLANATION, ");
		sql.append("         VIOLATION_FLAG, ");
		sql.append("         MODIFIER, ");
		sql.append("         JOB_TITLE, ");
		sql.append("         LASTUPDATE, ");
		sql.append("         FIRSTUPDATE, ");
		sql.append("         CASE WHEN SYSDATE > (CREATETIME + 30) THEN 'Y' ELSE 'N' END AS Disable_FLAG, ");
		sql.append("         CREATETIME AS CDATE ");
		sql.append("  FROM TBPMS_DAILY_PUBCOM_CHECK ");
		sql.append("  WHERE 1 = 1 ");

		if (inputVO.getsCreDate() != null) {
			sql.append("  AND CREATETIME >= TO_DATE(:SDATE, 'YYYY-MM-DD') ");
			condition.setObject("SDATE", new java.text.SimpleDateFormat("yyyy/MM/dd").format(inputVO.getsCreDate()));
		}
		if (inputVO.geteCreDate() != null) {
			sql.append("  AND CREATETIME <= TO_DATE(:EDATE, 'YYYY-MM-DD HH24:MI:SS') ");
			Date eDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputVO.geteCreDate().toString().concat(" 23:59:59"));
			condition.setObject("EDATE", new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(eDate));
		}

		// by Willis 20171024 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("  AND BRANCH_NBR = :BRNCH_NBRR ");
			condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append("  AND BRANCH_NBR in (select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :BRANCH_AREA_IDD ) ");
			condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append("  AND BRANCH_NBR in (select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :REGION_CENTER_IDD ) ");
			condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
		}

		//由工作首頁CRM181過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sql.append("  AND FIRSTUPDATE IS NULL ");
		}

		//排序
//		sql.append("  order by DATA_DATE ASC,REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, TRADE_DATE, CUST_ID ");
		sql.append(") t ");
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT M_SEQNO, REASON ");
		sql.append("  FROM TBPMS_DAILY_PUBCOM_REASON ");
		sql.append("  WHERE (M_SEQNO, CREATETIME) IN (SELECT M_SEQNO, MAX(CREATETIME) FROM TBPMS_DAILY_PUBCOM_REASON GROUP BY M_SEQNO) ");
		sql.append(") R ON T.SEQ = R.M_SEQNO ");
		sql.append("ORDER BY T.CDATE ASC, T.DATA_DATE ASC, T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR, T.TRADE_DATE, T.CUST_ID ");
		
		condition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		outputVO.setTotalList(dam.exeQuery(condition));
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		return outputVO;
	}

	/* 更新資料 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		PMS414InputVO inputVO = (PMS414InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		
		for (Map<String, Object> afterMap : inputVO.getList()) { // 資料修改後
			for (Map<String, Object> beforeMap : inputVO.getList2()) { // 資料修改前
				if ((beforeMap.get("SEQ").equals(afterMap.get("SEQ")))) { // 判定是否為同一筆
					// 本次交易是否有行員在場
					String before_staffFlag = beforeMap.get("STAFF_THERE_FLAG") == null ? "" : beforeMap.get("STAFF_THERE_FLAG").toString();
					String after_staffFlag = afterMap.get("STAFF_THERE_FLAG") == null ? "" : afterMap.get("STAFF_THERE_FLAG").toString();

					// 若代「特定客戶」操作，照會是否為客戶本人意願(請註明照會時間)
					String before_meeting = beforeMap.get("MEETING") == null ? "" : beforeMap.get("MEETING").toString().replace("-", "").replace(" 00:00:00", "");
					String after_meeting = (afterMap.get("MEETING_SAVE") == null || StringUtils.equals((String) afterMap.get("MEETING_SAVE"), "NaN")) ? "" : sdf.format(new Date(Long.parseLong(afterMap.get("MEETING_SAVE").toString())));

					// 是否為行員代客登入網銀帳號密碼                               
					String before_oboFlag = beforeMap.get("OBO_CUST_FLAG") == null ? "" : beforeMap.get("OBO_CUST_FLAG").toString();
					String after_oboFlag = afterMap.get("OBO_CUST_FLAG") == null ? "" : afterMap.get("OBO_CUST_FLAG").toString();

					// 客戶登入網銀帳號密碼時，行員是否迴避
					String before_avoidFlag = beforeMap.get("AVOID_FLAG") == null ? "" : beforeMap.get("AVOID_FLAG").toString();
					String after_avoidFlag = afterMap.get("AVOID_FLAG") == null ? "" : afterMap.get("AVOID_FLAG").toString();

					// 行員協助操作時，客戶是否全程在旁
					String before_nearbyFlag = beforeMap.get("NEARBY_CUST_FLAG") == null ? "" : beforeMap.get("NEARBY_CUST_FLAG").toString();
					String after_nearbyFlag = afterMap.get("NEARBY_CUST_FLAG") == null ? "" : afterMap.get("NEARBY_CUST_FLAG").toString();

					// 其它說明                                      
					String before_explanation = beforeMap.get("EXPLANATION") == null ? "" : beforeMap.get("EXPLANATION").toString();
					String after_explanation = afterMap.get("EXPLANATION") == null ? "" : afterMap.get("EXPLANATION").toString();

					// 是否違規                                      
					String before_violFlag = beforeMap.get("VIOLATION_FLAG") == null ? "" : beforeMap.get("VIOLATION_FLAG").toString();
					String after_violFlag = afterMap.get("VIOLATION_FLAG") == null ? "" : afterMap.get("VIOLATION_FLAG").toString();

					// 照會時間-小時
					String before_meetingHour = beforeMap.get("MEETING_HOUR") == null ? "" : beforeMap.get("MEETING_HOUR").toString();
					String after_meetingHour = afterMap.get("MEETING_HOUR") == null ? "" : afterMap.get("MEETING_HOUR").toString();

					// 照會時間-分鐘
					String before_meetingMin = beforeMap.get("MEETING_MIN") == null ? "" : beforeMap.get("MEETING_MIN").toString();
					String after_meetingMin = afterMap.get("MEETING_MIN") == null ? "" : afterMap.get("MEETING_MIN").toString();

					// 且任一選項修改 (待新建立VO後調整)
					if (!before_staffFlag.equals(after_staffFlag) || 
						!before_meeting.equals(after_meeting) || 
						!before_avoidFlag.equals(after_avoidFlag) || 
						!before_nearbyFlag.equals(after_nearbyFlag) || 
						!before_explanation.equals(after_explanation) || 
						!before_violFlag.equals(after_violFlag) || 
						!before_oboFlag.equals(after_oboFlag) || 
						!before_meetingHour.equals(after_meetingHour) || 
						!before_meetingMin.equals(after_meetingMin)) {

						// 寫入檢核項目
						condition = dam.getQueryCondition();
						sql = new StringBuffer();
						
						sql.append("UPDATE TBPMS_DAILY_PUBCOM_CHECK ");
						sql.append("SET STAFF_THERE_FLAG = :staffFlag, ");
						
						if (before_meeting != null) {
							sql.append("MEETING = TRUNC(TO_DATE(:meeting, 'YYYY-MM-DD')), ");
							condition.setObject("meeting", after_meeting);
						}
						
						sql.append("MEETING_HOUR = :meetingHour, "); // 照會時間-小時
						sql.append("MEETING_MIN = :meetingMin, "); // 照會時間-分鐘
						sql.append("OBO_CUST_FLAG = :oboFlag, ");
						sql.append("AVOID_FLAG = :avoidFlag, ");
						sql.append("NEARBY_CUST_FLAG = :nearbyFlag, ");
						sql.append("EXPLANATION = :explanation, ");
						sql.append("VIOLATION_FLAG = :violFlag, ");
						sql.append("MODIFIER = :loginUser, "); //改用原來登入者，避免用代理角色存取。
						
						if (beforeMap.get("FIRSTUPDATE") == null && StringUtils.equals("Y", beforeMap.get("CAN_UPD_FIRSTDATE_FLG").toString())) {
							sql.append("FIRSTUPDATE = SYSDATE, ");
						}
						
						sql.append("LASTUPDATE = SYSDATE ");

						sql.append("WHERE SEQ = :seq ");

						condition.setObject("staffFlag", after_staffFlag);
						condition.setObject("meetingHour", after_meetingHour);
						condition.setObject("meetingMin", after_meetingMin);
						condition.setObject("oboFlag", after_oboFlag);
						condition.setObject("avoidFlag", after_avoidFlag);
						condition.setObject("nearbyFlag", after_nearbyFlag);
						condition.setObject("explanation", after_explanation);
						condition.setObject("violFlag", after_violFlag);
						condition.setObject("loginUser", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
						condition.setObject("seq", afterMap.get("SEQ"));

						condition.setQueryString(sql.toString());
						
						dam.exeUpdate(condition);
						
						// 是否違規欄位有異動，請說明異動原因
						if (!before_violFlag.equals(after_violFlag)) {
							condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sql = new StringBuffer();
							sql.append("INSERT INTO TBPMS_DAILY_PUBCOM_REASON (SEQNO, M_SEQNO, REASON, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE)");
							sql.append("VALUES (TBPMS_DAILY_PUBCOM_REASON_SEQ.nextval, :seq, :reason, 0, SYSDATE, :loginUser, :loginUser, SYSDATE) ");
							
							condition.setObject("seq", afterMap.get("SEQ"));
							condition.setObject("reason", inputVO.getReason());
							condition.setObject("loginUser", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
							
							condition.setQueryString(sql.toString());
							
							dam.exeUpdate(condition);
						}
					}
				}
			}
		}
		this.sendRtnObject(null);
	}

	/* 產出 CSV */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS414OutputVO outputVO = (PMS414OutputVO) body;

		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		String[] csvHeader = { 	"序號", "資料日期",
								"分行代碼", "分行名稱", "客戶ID", "客戶姓名", "AO Code",
								"特定客戶",
								"交易時間", "交易項目",
								"本次交易是否\r\n有行員在場",
								"1.若代「特定客戶」操作，照會是\r\n否為客戶本人意願(請註明照會時間)", "照會時間-小時", "照會時間-分鐘",
								"2.是否為行員代登\r\n入網銀帳號密碼",
								"3.客戶登入網銀帳號密碼\r\n時，行員是否迴避",
								"4.行員協助操作時，客\r\n戶是否全程在旁",
								"其他說明\r\n(限100字內)",
								"是否違規",
								"首次建立時間", "最新異動人員", "最新異動日期", "最新異動原因"};
		String[] csvMain   = {  "ROWNUM", "CDATE", 
								"BRANCH_NBR", "BRANCH_NAME", "CUST_ID", "CUST_NAME", "AO_CODE",
								"SPECIFIC_FLAG",
								"TRADE_DATE", "TRADE_ITEM",
								"STAFF_THERE_FLAG",
								"MEETING", "MEETING_HOUR", "MEETING_MIN",
								"OBO_CUST_FLAG",
								"AVOID_FLAG",
								"NEARBY_CUST_FLAG",
								"EXPLANATION",
								"VIOLATION_FLAG",
								"FIRSTUPDATE", "MODIFIER", "LASTUPDATE", "REASON"};

		
		String fileName = "公用電腦查核日報_" + sdf.format(new Date()) + ".csv";
		
		List<Object[]> csvData = new ArrayList<Object[]>();
		if (list.size() == 0) {
			String[] records = new String[2];
			records[0] = "查無資料";
			csvData.add(records);
		} else {
			for (Map<String, Object> map : list) {
				//20170929:問題單3791:本日無客戶使用公用電腦匯出不要出現
				if ((map.get("CUST_ID") == null || StringUtils.isBlank(String.valueOf(map.get("CUST_ID")))) && (map.get("AO_CODE") == null || StringUtils.isBlank(String.valueOf(map.get("AO_CODE"))))) {
					continue;
				}

				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "ROWNUM":
							records[i] = ((int) Double.parseDouble(checkIsNull(map, csvMain[i]).toString())) + ""; // 首次建立時間
							break;
						case "CUST_ID":
							records[i] = DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, csvMain[i]));
							break;
						case "SPECIFIC_FLAG":
						case "STAFF_THERE_FLAG":
						case "OBO_CUST_FLAG":
						case "AVOID_FLAG":
						case "NEARBY_CUST_FLAG":
						case "VIOLATION_FLAG":
							records[i] = flagFormat(map, csvMain[i]);
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
		
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		
		this.sendRtnObject(null);
	}

	/* 檢查Map取出欄位是否為Null */
	private String checkIsNull(Map map, String key) {
		
		if (String.valueOf(map.get(key)).equals("00")) {
			return "";
		} else if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	// 處理Y/N --> 是/否
	private String flagFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			if (map.get(key).equals("Y"))
				return "是";
			else if (map.get(key).equals("N"))
				return "否";
			else
				return "";
		} else
			return "";
	}

}