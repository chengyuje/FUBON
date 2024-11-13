package com.systex.jbranch.app.server.fps.cam130;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron
 * @date 2016/04/20
 * @spec null
 */
@Component("cam130")
@Scope("request")
public class CAM130 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM130.class);

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		
		CAM130InputVO inputVO = (CAM130InputVO) body;
		CAM130OutputVO return_VO = new CAM130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.SEQNO, a.CAMPAIGN_ID, a.CAMPAIGN_DESC, a.STEP_ID, a.LEAD_TYPE, a.CREATETIME, a.FIRST_CHANNEL, a.SECOND_CHANNEL, a.IMP_STATUS, a.CAMP_PURPOSE, ");
		sql.append("       NVL(a.LE_TOTAL_CNT, 0) AS LE_TOTAL_CNT, NVL(a.IM_TOTAL_CNT, 0) AS IM_TOTAL_CNT, NVL(a.IM_AO_CNT, 0) AS IM_AO_CNT, NVL(a.IM_OTHER_CNT, 0) AS IM_OTHER_CNT, NVL(a.ER_CNT, 0) AS ER_CNT, ");
		sql.append("       DECODE(b.CAMPAIGN_NAME, NULL, a.CAMPAIGN_NAME, b.CAMPAIGN_NAME) as CAMPAIGN_NAME, ");
		sql.append("       DECODE(b.START_DATE, NULL, a.START_DATE, b.START_DATE) as START_DATE, ");
		sql.append("       DECODE(b.END_DATE, NULL, a.END_DATE, b.END_DATE) as END_DATE, ");
		sql.append("       CASE WHEN c.EMP_NAME is not null THEN c.EMP_NAME ");
		sql.append("       ELSE CASE WHEN a.LEAD_SOURCE_ID = '01' THEN 'Unica匯入' ");
		sql.append("                 WHEN a.LEAD_SOURCE_ID = '02' THEN '系統對接' ");
		sql.append("                 WHEN a.LEAD_SOURCE_ID = '03' THEN '其他系統對接' ");
		sql.append("                 WHEN a.LEAD_SOURCE_ID = '04' THEN '個金分行業務系統批次執行' ");
		sql.append("                 WHEN a.LEAD_SOURCE_ID = '05' THEN '手動匯入' ");
		sql.append("            END ");
		sql.append("      END AS EMP_NAME ");
		sql.append("from TBCAM_SFA_LEADS_IMP a ");
		sql.append("left join tbcam_sfa_campaign b on a.campaign_id = b.campaign_id and a.step_id = b.step_id ");
		sql.append("left join VWORG_BRANCH_EMP_DETAIL_INFO c on a.CREATOR = c.EMP_ID ");
		sql.append("where 1=1 ");
		// where
		if (!StringUtils.isBlank(inputVO.getCam_id())) {
			sql.append("and a.CAMPAIGN_ID like :camid ");
			queryCondition.setObject("camid", inputVO.getCam_id() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getCam_name())) {
			sql.append("and case when b.CAMPAIGN_NAME is not null then b.campaign_name else a.CAMPAIGN_NAME end like :camname ");
			queryCondition.setObject("camname", "%" + inputVO.getCam_name() + "%");
		}
		if (inputVO.getsCreDate() != null) {
			sql.append("and TRUNC(a.CREATETIME) >= TRUNC(:crestart) ");
			queryCondition.setObject("crestart", new Timestamp(inputVO.getsCreDate().getTime()));
		}
		if (inputVO.geteCreDate() != null) {
			sql.append("and TRUNC(a.CREATETIME) <= TRUNC(:creend) ");
			queryCondition.setObject("creend", new Timestamp(inputVO.geteCreDate().getTime()));
		}
		if (!StringUtils.isBlank(inputVO.getSource_id())) {
			sql.append("and a.LEAD_SOURCE_ID = :souid ");
			queryCondition.setObject("souid", inputVO.getSource_id());
		}
		if (!StringUtils.isBlank(inputVO.getChannel())) {
			sql.append("and a.FIRST_CHANNEL like :channel ");
			queryCondition.setObject("channel", inputVO.getChannel());
		}
		if (inputVO.getsStaDate() != null) {
			sql.append("and case when b.START_DATE is not null then b.START_DATE else a.START_DATE end >= TRUNC(:sstart) ");
			queryCondition.setObject("sstart", new Timestamp(inputVO.getsStaDate().getTime()));
		}
		if (inputVO.geteStaDate() != null) {
			sql.append("and case when b.START_DATE is not null then b.START_DATE else a.START_DATE end <= TRUNC(:send) ");
			queryCondition.setObject("send", new Timestamp(inputVO.geteStaDate().getTime()));
		}
		if (!StringUtils.isBlank(inputVO.getType())) {
			sql.append("and a.LEAD_TYPE = :type ");
			queryCondition.setObject("type", inputVO.getType());
		}
		if (!StringUtils.isBlank(inputVO.getCamp_purpose())) {
			sql.append("and a.CAMP_PURPOSE = :camp_purpose ");
			queryCondition.setObject("camp_purpose", inputVO.getCamp_purpose());
		}
		if (!StringUtils.isBlank(inputVO.getStatus())) {
			sql.append("and a.IMP_STATUS = :status ");
			queryCondition.setObject("status", inputVO.getStatus());
		}
		if (inputVO.getsEndDate() != null) {
			sql.append("and case when b.END_DATE is not null then b.END_DATE else a.END_DATE end >= TRUNC(:estart) ");
			queryCondition.setObject("estart", new Timestamp(inputVO.getsEndDate().getTime()));
		}
		if (inputVO.geteEndDate() != null) {
			sql.append("and case when b.END_DATE is not null then b.END_DATE else a.END_DATE end <= TRUNC(:eend) ");
			queryCondition.setObject("eend", new Timestamp(inputVO.geteEndDate().getTime()));
		}
		// 若無，請依照行銷活動代碼,步驟代碼,匯入日期做排序
		sql.append("ORDER BY a.CAMPAIGN_ID, a.STEP_ID, a.CREATETIME DESC ");
		queryCondition.setQueryString(sql.toString());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}

	public void getFailData(Object body, IPrimitiveMap header) throws JBranchException {
		CAM130InputVO inputVO = (CAM130InputVO) body;
		CAM130OutputVO return_VO = new CAM130OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select a.CUST_ID, a.IMP_FLAG, b.PARAM_NAME,c.EMP_NAME,c.BRANCH_NBR,c.BRANCH_NAME from TBCAM_SFA_LE_IMP_TEMP a ");
		sql.append("left join TBSYSPARAMETER b on a.IMP_FLAG = b.PARAM_CODE ");
		sql.append("left join VWORG_BRANCH_EMP_DETAIL_INFO c on a.EMP_ID = c.EMP_ID ");
		sql.append("where a.IMP_SEQNO = :seq and a.IMP_FLAG != 'Y' and b.PARAM_TYPE = 'CAM.IMP_FLAG' ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("seq", inputVO.getSeq());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}

	/*
	 * 取得 匯入名單總數、實際匯入總數、匯入成功(分派至AO)、匯入成功(分派至主管)、匯入失敗的行銷活動名稱、分行、客戶ID、AO_CODE或員編。
	 * 2019/02/15 add by ocean
	 * 
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {

		CAM130InputVO inputVO = (CAM130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String fileName = "";

		switch (inputVO.getExpType()) {
		case "LE_TOTAL_CNT":
			fileName = "名單匯入明細檔(應匯入名單總數).csv";
			sb.append("SELECT CAMP.CAMPAIGN_NAME, LEAD.BRANCH_ID, LEAD.CUST_ID, LEAD.AO_CODE, LEAD.EMP_ID ");
			sb.append("FROM TBCAM_SFA_LE_IMP_TEMP LEAD ");
			sb.append("LEFT JOIN TBCAM_SFA_LEADS_IMP CAMP ON LEAD.IMP_SEQNO = CAMP.SEQNO ");
			sb.append("WHERE CAMP.SEQNO = :seqNO ");

			break;
		case "IM_TOTAL_CNT":
			fileName = "名單匯入明細檔(實際匯入總數).csv";
			sb.append("SELECT CAMP.CAMPAIGN_NAME, LEAD.BRANCH_ID, LEAD.CUST_ID, LEAD.AO_CODE, LEAD.EMP_ID ");
			sb.append("FROM TBCAM_SFA_LEADS LEAD ");
			sb.append("LEFT JOIN TBCAM_SFA_LEADS_IMP CAMP ON LEAD.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEAD.STEP_ID = CAMP.STEP_ID ");
			sb.append("WHERE INSTR(LEAD.SFA_LEAD_ID, '_S', -1) = 0 ");
			sb.append("AND INSTR(LEAD.SFA_LEAD_ID, '_BK', -1) = 0 ");
			sb.append("AND CAMP.SEQNO = :seqNO ");

			break;
		case "IM_AO_CNT":
			fileName = "名單匯入明細檔(匯入成功(分派至AO)).csv";
			sb.append("SELECT CAMP.CAMPAIGN_NAME, LEAD.BRANCH_ID, LEAD.CUST_ID, LEAD.AO_CODE, LEAD.EMP_ID ");
			sb.append("FROM TBCAM_SFA_LEADS LEAD ");
			sb.append("LEFT JOIN TBCAM_SFA_LEADS_IMP CAMP ON LEAD.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEAD.STEP_ID = CAMP.STEP_ID ");
			sb.append("WHERE INSTR(LEAD.SFA_LEAD_ID, '_S', -1) = 0 ");
			sb.append("AND INSTR(LEAD.SFA_LEAD_ID, '_BK', -1) = 0 ");
			sb.append("AND LEAD.AO_CODE IS NOT NULL ");
			sb.append("AND CAMP.SEQNO = :seqNO ");

			break;
		case "IM_OTHER_CNT":
			fileName = "名單匯入明細檔(匯入成功(分派至主管)).csv";
			sb.append("SELECT CAMP.CAMPAIGN_NAME, LEAD.BRANCH_ID, LEAD.CUST_ID, LEAD.AO_CODE, LEAD.EMP_ID ");
			sb.append("FROM TBCAM_SFA_LEADS LEAD ");
			sb.append("LEFT JOIN TBCAM_SFA_LEADS_IMP CAMP ON LEAD.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND LEAD.STEP_ID = CAMP.STEP_ID ");
			sb.append("WHERE INSTR(LEAD.SFA_LEAD_ID, '_S', -1) = 0 ");
			sb.append("AND INSTR(LEAD.SFA_LEAD_ID, '_BK', -1) = 0 ");
			sb.append("AND LEAD.AO_CODE IS NULL ");
			sb.append("AND CAMP.SEQNO = :seqNO ");

			break;
		case "ER_CNT":
			fileName = "名單匯入明細檔(匯入失敗).csv";
			sb.append("SELECT CAMP.CAMPAIGN_NAME, LEAD.BRANCH_ID, LEAD.CUST_ID, LEAD.AO_CODE, LEAD.EMP_ID ");
			sb.append("FROM TBCAM_SFA_LE_IMP_TEMP LEAD ");
			sb.append("LEFT JOIN TBCAM_SFA_LEADS_IMP CAMP ON LEAD.IMP_SEQNO = CAMP.SEQNO ");
			sb.append("WHERE CAMP.SEQNO = :seqNO ");
			sb.append("AND LEAD.IMP_FLAG LIKE 'E%' ");

			break;
		}

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("seqNO", inputVO.getSeq());

		List<Map<String, Object>> exportLst = dam.exeQuery(queryCondition);
		List<Object[]> csvData = new ArrayList<Object[]>();

		String[] csvHeader = new String[] { "行銷活動名稱", "分行", "客戶ID", "AO_CODE", "員編" };
		String[] csvMain = new String[] { "CAMPAIGN_NAME", "BRANCH_ID", "CUST_ID", "AO_CODE", "EMP_ID" };

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
			notifyClientToDownloadFile(url, fileName);
		}

		sendRtnObject(null);
	}
	
	public void exportRPT(Object body, IPrimitiveMap header) throws JBranchException {
		CAM130InputVO inputVO = (CAM130InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT A.CAMPAIGN_ID, A.LEAD_TYPE, A.FIRST_CHANNEL, A.IMP_STATUS, A.CAMP_PURPOSE, A.CHECK_STATUS, ");
		sb.append("        A.LEAD_SOURCE_ID, NVL(A.IM_TOTAL_CNT, 0) AS IM_TOTAL_CNT, ");
		sb.append("        DECODE(B.CAMPAIGN_NAME, NULL, A.CAMPAIGN_NAME, B.CAMPAIGN_NAME) AS CAMPAIGN_NAME, ");
		sb.append("        TO_CHAR(A.CREATETIME, 'YYYY/MM/DD') AS CREATETIME, ");
		sb.append("        TO_CHAR(DECODE(B.START_DATE, NULL, A.START_DATE, B.START_DATE), 'YYYY/MM/DD') AS START_DATE, ");
		sb.append("        TO_CHAR(DECODE(B.END_DATE, NULL, A.END_DATE, B.END_DATE), 'YYYY/MM/DD') AS END_DATE, ");
		sb.append("        CASE WHEN SUBSTR(A.CREATOR, 1, 5) = 'PABTH' THEN '批次建立' ELSE '自行建立' END AS CREATE_TYPE, ");
		sb.append("        CASE WHEN C.EMP_NAME IS NOT NULL THEN C.EMP_NAME ");
		sb.append("        ELSE CASE WHEN A.LEAD_SOURCE_ID = '01' THEN 'UNICA匯入' ");
		sb.append("                  WHEN A.LEAD_SOURCE_ID = '02' THEN '系統對接' ");
		sb.append("                  WHEN A.LEAD_SOURCE_ID = '03' THEN '其他系統對接' ");
		sb.append("                  WHEN A.LEAD_SOURCE_ID = '04' THEN '個金分行業務系統批次執行' ");
		sb.append("                  WHEN A.LEAD_SOURCE_ID = '05' THEN '手動匯入' ");
		sb.append("             END ");
		sb.append("       END AS EMP_NAME, C.EMP_ID, C.ROLE_ID, C.ROLE_NAME, C.JOB_TITLE_NAME, ");
		sb.append("       NVL(D.CAMPAIGN_END_CNT, 0) AS CAMPAIGN_END_CNT ");
		sb.append(" FROM TBCAM_SFA_LEADS_IMP A ");
		sb.append(" LEFT JOIN TBCAM_SFA_CAMPAIGN B ON A.CAMPAIGN_ID = B.CAMPAIGN_ID AND A.STEP_ID = B.STEP_ID ");
		sb.append(" LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO C ON A.CREATOR = C.EMP_ID ");
		sb.append(" LEFT JOIN ( ");
		sb.append(" SELECT CAMPAIGN_ID, STEP_ID, COUNT(*) AS CAMPAIGN_END_CNT FROM TBCAM_SFA_LEADS ");
		sb.append(" WHERE LEAD_STATUS > '03' AND LEAD_STATUS <> 'TR' ");
		sb.append(" GROUP BY CAMPAIGN_ID, STEP_ID) D ON A.CAMPAIGN_ID = D.CAMPAIGN_ID AND A.STEP_ID = D.STEP_ID ");
		sb.append(" WHERE 1 = 1 ");
		
		// where
		if (!StringUtils.isBlank(inputVO.getCam_id())) {
			sb.append("AND A.CAMPAIGN_ID LIKE :camid ");
			queryCondition.setObject("camid", inputVO.getCam_id() + "%");
		}
		if (!StringUtils.isBlank(inputVO.getCam_name())) {
			sb.append("AND CASE WHEN B.CAMPAIGN_NAME IS NOT NULL THEN B.CAMPAIGN_NAME ELSE A.CAMPAIGN_NAME END LIKE :camname ");
			queryCondition.setObject("camname", "%" + inputVO.getCam_name() + "%");
		}
		if (inputVO.getsCreDate() != null) {
			sb.append("AND TRUNC(A.CREATETIME) >= TRUNC(:crestart) ");
			queryCondition.setObject("crestart", new Timestamp(inputVO.getsCreDate().getTime()));
		}
		if (inputVO.geteCreDate() != null) {
			sb.append("AND TRUNC(A.CREATETIME) <= TRUNC(:creend) ");
			queryCondition.setObject("creend", new Timestamp(inputVO.geteCreDate().getTime()));
		}
		if (!StringUtils.isBlank(inputVO.getSource_id())) {
			sb.append("AND A.LEAD_SOURCE_ID = :souid ");
			queryCondition.setObject("souid", inputVO.getSource_id());
		}
		if (!StringUtils.isBlank(inputVO.getChannel())) {
			sb.append("AND A.FIRST_CHANNEL LIKE :channel ");
			queryCondition.setObject("channel", inputVO.getChannel());
		}
		if (inputVO.getsStaDate() != null) {
			sb.append("AND CASE WHEN B.START_DATE IS NOT NULL THEN B.START_DATE ELSE A.START_DATE END >= TRUNC(:sstart) ");
			queryCondition.setObject("sstart", new Timestamp(inputVO.getsStaDate().getTime()));
		}
		if (inputVO.geteStaDate() != null) {
			sb.append("AND CASE WHEN B.START_DATE IS NOT NULL THEN B.START_DATE ELSE A.START_DATE END <= TRUNC(:send) ");
			queryCondition.setObject("send", new Timestamp(inputVO.geteStaDate().getTime()));
		}
		if (!StringUtils.isBlank(inputVO.getType())) {
			sb.append("AND A.LEAD_TYPE = :type ");
			queryCondition.setObject("type", inputVO.getType());
		}
		if (!StringUtils.isBlank(inputVO.getCamp_purpose())) {
			sb.append("AND A.CAMP_PURPOSE = :camp_purpose ");
			queryCondition.setObject("camp_purpose", inputVO.getCamp_purpose());
		}
		if (!StringUtils.isBlank(inputVO.getStatus())) {
			sb.append("AND A.IMP_STATUS = :status ");
			queryCondition.setObject("status", inputVO.getStatus());
		}
		if (inputVO.getsEndDate() != null) {
			sb.append("AND CASE WHEN B.END_DATE IS NOT NULL THEN B.END_DATE ELSE A.END_DATE END >= TRUNC(:estart) ");
			queryCondition.setObject("estart", new Timestamp(inputVO.getsEndDate().getTime()));
		}
		if (inputVO.geteEndDate() != null) {
			sb.append("AND CASE WHEN B.END_DATE IS NOT NULL THEN B.END_DATE ELSE A.END_DATE END <= TRUNC(:eend) ");
			queryCondition.setObject("eend", new Timestamp(inputVO.geteEndDate().getTime()));
		}
		
		queryCondition.setQueryString(sb.toString());		
		List<Map<String, Object>> exportLst = dam.exeQuery(queryCondition);
		
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		String[] csvHeader = new String[] {"行銷活動代碼", "行銷活動名稱", "活動負責人", "EMP_NAME", "名單類型", "使用部隊", 
										   "名單狀態", "放行狀態", "建立日期", "活動起日", "活動迄日", "實際匯入總數", 
										   "結案總數", "名單來源", "建立方式", "建立人員", "建立人員角色", "建立人員職稱", "名單目的"};
		
		String[] csvMain = new String[] {"CAMPAIGN_ID", "CAMPAIGN_NAME", "EMP_ID", "EMP_NAME", "LEAD_TYPE", "FIRST_CHANNEL",
										 "IMP_STATUS", "CHECK_STATUS", "CREATETIME", "START_DATE", "END_DATE", "IM_TOTAL_CNT",
										 "CAMPAIGN_END_CNT", "LEAD_SOURCE_ID", "CREATE_TYPE", "EMP_ID", "ROLE_NAME", "JOB_TITLE_NAME", "CAMP_PURPOSE"};
		
		if (exportLst.size() > 0) {
			String fileName = "名單匯入查詢報表.csv";
			
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> leadTypeMap		= xmlInfo.doGetVariable("CAM.LEAD_TYPE"	   , FormatHelper.FORMAT_3);
			Map<String, String> channelCodeMap	= xmlInfo.doGetVariable("CAM.CHANNEL_CODE" , FormatHelper.FORMAT_3);
			Map<String, String> importStatusMap = xmlInfo.doGetVariable("CAM.IMPORT_STATUS", FormatHelper.FORMAT_3);
			Map<String, String> checkStatusMap	= xmlInfo.doGetVariable("CAM.CHECK_STATUS" , FormatHelper.FORMAT_3);
			Map<String, String> leadSourceMap	= xmlInfo.doGetVariable("CAM.LEAD_SOURCE"  , FormatHelper.FORMAT_3);
			Map<String, String> campPurposeMap	= xmlInfo.doGetVariable("CAM.CAMP_PURPOSE" , FormatHelper.FORMAT_3);
			
			for (Map<String, Object> map : exportLst) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvMain.length; i++) {
					switch (csvMain[i]) {
						case "LEAD_TYPE":
							records[i] = leadTypeMap.get(String.valueOf(map.get(csvMain[i])));
							break;
						case "FIRST_CHANNEL":
							records[i] = channelCodeMap.get(String.valueOf(map.get(csvMain[i])));
							break;
						case "IMP_STATUS":
							records[i] = importStatusMap.get(String.valueOf(map.get(csvMain[i])));
							break;
						case "CHECK_STATUS":
							records[i] = checkStatusMap.get(String.valueOf(map.get(csvMain[i])));
							break;
						case "LEAD_SOURCE_ID":
							records[i] = leadSourceMap.get(String.valueOf(map.get(csvMain[i])));
							break;
						case "CAMP_PURPOSE":
							records[i] = campPurposeMap.get(String.valueOf(map.get(csvMain[i])));
							break;
						default:
							records[i] = checkIsNull(map, csvMain[i]);
					}
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
			notifyClientToDownloadFile(url, fileName);
		}
		
		sendRtnObject(null);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map<String, Object> map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && null != map.get(key)) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
}