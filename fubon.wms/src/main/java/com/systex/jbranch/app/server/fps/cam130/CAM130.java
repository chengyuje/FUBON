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
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
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
		sql.append("SELECT a.SEQNO, a.CAMPAIGN_ID, a.CAMPAIGN_DESC, a.STEP_ID, a.LEAD_TYPE, a.CREATETIME, a.FIRST_CHANNEL, a.SECOND_CHANNEL, a.IMP_STATUS, ");
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