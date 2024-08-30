/**
 * 
 */
package com.systex.jbranch.app.server.fps.cam999bth;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMPVO;
import com.systex.jbranch.fubon.jlb.CAM999;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.scheduler.SchedulerHelper;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author 1600216
 * @date 
 */
@Repository("cam999bth")
@Scope("prototype")
public class CAM999BTH extends BizLogic {

	private Logger logger = LoggerFactory.getLogger(CAM999BTH.class);
//	private DataAccessManager dam = null;
	
	public void execute(Object body, IPrimitiveMap<?> header) throws JBranchException {

		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//取得傳入參數
		Map<String, Object> inputMap = (Map<String, Object>) body;
		Map<String, Object> jobParameter = (Map<String, Object>) inputMap.get(SchedulerHelper.JOB_PARAMETER_KEY);
		
		String leadType	= (String) jobParameter.get("leadType");
		List<String> leadTypeList = Arrays.asList(leadType.split(","));

		if (StringUtils.isNotBlank(leadType)) {
			// 2018/5/22 FETCH FIRST ROW ONLY
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			do {
				// old code
				StringBuffer sb = new StringBuffer();
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT SEQNO, CAMPAIGN_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, STEP_ID, STEP_NAME, LEAD_SOURCE_ID, START_DATE, END_DATE, LEAD_RESPONSE_CODE, ");
				sb.append("       LEAD_USE_DP, LEAD_TYPE, LEAD_PARA1, LEAD_PARA2, LEAD_DAYUSE, FILE_SEQ, EXAM_ID, SALES_PITCH, FIRST_CHANNEL, SECOND_CHANNEL, IMP_STATUS, CHECK_STATUS, ");
				sb.append("       LE_TOTAL_CNT, IM_TOTAL_CNT, IM_AO_CNT, IM_OTHER_CNT, ER_CNT, RV_LE_CNT, RV_REASON, START_DT, END_DT, GIFT_CAMPAIGN_ID, RE_STATUS, RE_LOG, HANDLE_CNT ");
				sb.append("FROM TBCAM_SFA_LEADS_IMP ");
				sb.append("WHERE IMP_STATUS = 'IN' ");
				sb.append("AND LEAD_TYPE IN (:leadType) ");
				sb.append("AND NVL(CHECK_STATUS, '00') IN ('00', '02') ");
				sb.append("ORDER BY CASE WHEN LEAD_TYPE = '03' OR LEAD_TYPE = '02' THEN '0' ");
				sb.append("WHEN LEAD_TYPE = '05' OR LEAD_TYPE = '06' OR LEAD_TYPE = '07' OR LEAD_TYPE = '08' OR LEAD_TYPE = '0' THEN '1' ");
				sb.append("WHEN LEAD_TYPE = 'H1' OR LEAD_TYPE = 'H2' OR LEAD_TYPE = 'I1' OR LEAD_TYPE = 'I2' OR LEAD_TYPE = 'F1' OR LEAD_TYPE = 'F2' THEN '1' ");
				sb.append("WHEN LEAD_TYPE = '01' THEN '2' ");
				sb.append("ELSE '3' END ");
				sb.append("FETCH FIRST ROW ONLY ");
				queryCondition.setQueryString(sb.toString());
				queryCondition.setObject("leadType", leadTypeList);
				
				list = dam.exeQuery(queryCondition);
				
				for (Map<String, Object> campMap : list) {
					logger.info("=====" + campMap.get("CAMPAIGN_ID") + " - " + campMap.get("STEP_ID") + " 名單匯入 START=====");
					dam.setAutoCommit(true);
					
					try {
						if (null != campMap.get("CAMPAIGN_ID") && !StringUtils.equals("03", (String) campMap.get("CHECK_STATUS"))) {
							if (("05".equals(campMap.get("LEAD_SOURCE_ID")) && "02".equals(campMap.get("CHECK_STATUS"))) || // 手動匯入 + 放行
								("05".equals(campMap.get("LEAD_SOURCE_ID")) && "00".equals(campMap.get("CHECK_STATUS")) && "UX".equals(campMap.get("LEAD_TYPE"))) || // 手動匯入 + 電銷匯入留資名單 + 無須放行
								(!"05".equals(campMap.get("LEAD_SOURCE_ID")) &&
								 ("02".equals(campMap.get("LEAD_TYPE")) || // 02 - 事件式行銷
								  "03".equals(campMap.get("LEAD_TYPE")) || // 03 - 必要通知
								  "99".equals(campMap.get("LEAD_TYPE")) || // 99 - 內稽內控名單
								  "04".equals(campMap.get("LEAD_TYPE")) || // 04 - 整批參考資訊
								  "01".equals(campMap.get("LEAD_TYPE")) || // 01 - 行銷活動名單
								  "05".equals(campMap.get("LEAD_TYPE")) || // 05 - 線上留資名單(限Unica帶入)_房貸
								  "06".equals(campMap.get("LEAD_TYPE")) || // 06 - 線上留資名單(限Unica帶入)_信貸 - 行銷活動名單 
								  "07".equals(campMap.get("LEAD_TYPE")) || // 07 - 線上留資名單(限Unica帶入)_本行客戶保險 
								  "08".equals(campMap.get("LEAD_TYPE")) || // 08 - 線上留資名單(限Unica帶入)_非本行客戶保險線上留資 
								  "H1".equals(campMap.get("LEAD_TYPE")) || // H1 - 線上留資名單(限Unica帶入)_客服自來房貸
								  "H2".equals(campMap.get("LEAD_TYPE")) || // H2 - 線上留資名單(限Unica帶入)_客服共銷房貸
								  "I1".equals(campMap.get("LEAD_TYPE")) || // I1 - 線上留資名單(限Unica帶入)_客服自來保險
								  "I2".equals(campMap.get("LEAD_TYPE")) || // I2 - 線上留資名單(限Unica帶入)_客服共銷保險
								  "F1".equals(campMap.get("LEAD_TYPE")) || // F1 - 線上留資名單(限Unica帶入)_客服自來基金
								  "F2".equals(campMap.get("LEAD_TYPE")) || // F2 - 線上留資名單(限Unica帶入)_客服共銷基金
								  "09".equals(campMap.get("LEAD_TYPE"))    // 09 - 線上留資名單_富樂退
								  ))) {
									
								if (checkDp(dam, (String) campMap.get("CAMPAIGN_ID"), (String) campMap.get("STEP_ID"))) {
									logger.info("=====此活動名單已匯入=====");
									updateImpStatus(dam, (BigDecimal) campMap.get("SEQNO"), "DP"); // 此活動名單已匯入
								} else {
									logger.info("=====此活動名單正在處理中=====");
									updateImpStatus(dam, (BigDecimal) campMap.get("SEQNO"), "PR"); // 此活動名單正在處理中
									// 取得名單匯入暫存檔(客戶明細)
									Integer leadList = getImpTemp(dam, (BigDecimal) campMap.get("SEQNO"));
									if ((new BigDecimal(leadList.toString())).compareTo(new BigDecimal("0")) == 0) {
										logger.info("=====此活動名單未匯入-無名單明細資料=====");
										updateImpStatus(dam, (BigDecimal) campMap.get("SEQNO"), "E2"); // 此活動名單未匯入-無名單明細資料
									}
									
									CAM999 cam999 = (CAM999) PlatformContext.getBean("cam999");
									Map<String, Object> returnResult = cam999.importLead(dam, campMap);
		
									logger.info("imTotalCnt: " + returnResult.get("imTotalCnt") + " / erOtherCnt: " + returnResult.get("erOtherCnt"));
									if ((new BigDecimal(((Integer) returnResult.get("imTotalCnt")).toString())).compareTo(new BigDecimal("0")) == 0) { // 分派筆數
										updateImpStatus(dam, (BigDecimal) campMap.get("SEQNO"), "E0"); // 此活動名單未匯入-所有名單分派失敗
										
										TBCAM_SFA_CAMPAIGNPK pk = new TBCAM_SFA_CAMPAIGNPK();
										pk.setCAMPAIGN_ID((String) campMap.get("CAMPAIGN_ID"));
										pk.setSTEP_ID((String) campMap.get("STEP_ID"));
										
										TBCAM_SFA_CAMPAIGNVO vo = new TBCAM_SFA_CAMPAIGNVO();
										vo.setcomp_id(pk);
										
										dam.delete(vo);
									} else {
										if ((new BigDecimal(((Integer) returnResult.get("erOtherCnt")).toString())).compareTo(new BigDecimal("0")) <= 0) {
											updateImpStatus(dam, (BigDecimal) campMap.get("SEQNO"), "IM"); // 此活動名單已匯入,無失敗名單
										} else if ((new BigDecimal(((Integer) returnResult.get("erOtherCnt")).toString())).compareTo(new BigDecimal("0")) > 0) {
											updateImpStatus(dam, (BigDecimal) campMap.get("SEQNO"), "E1"); //部份匯入-有失敗名單
										}
									}
								}
							} else {
								logger.error("CAMPAIGN_ID = " + campMap.get("CAMPAIGN_ID") + ", STEP_ID = " + campMap.get("STEP_ID") + " - 待放行，暫不可分派");
							}
						} else {
							logger.error("CAMPAIGN_ID = " + campMap.get("CAMPAIGN_ID") + ", STEP_ID = " + campMap.get("STEP_ID") + " - 已作廢活動不可分派");
						}
					} catch (Exception e) {
						logger.error(campMap.get("SEQNO") + " : " + campMap.get("CAMPAIGN_ID") + " / " + campMap.get("STEP_ID"));
						logger.error(ExceptionUtils.getStackTrace(e));
						
						TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
						vo =  (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, (BigDecimal) campMap.get("SEQNO"));
						vo.setIMP_STATUS("E4"); //E4-未匯入-其他例外狀況
						dam.update(vo);
						
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("DELETE FROM TBCAM_SFA_LEADS WHERE CAMPAIGN_ID = :campID AND STEP_ID = :stepID ");
						queryCondition.setObject("campID", campMap.get("CAMPAIGN_ID"));
						queryCondition.setObject("stepID", campMap.get("STEP_ID"));
						dam.exeUpdate(queryCondition);
						
						queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						queryCondition.setQueryString("DELETE FROM TBCAM_SFA_CAMPAIGN WHERE CAMPAIGN_ID = :campID AND STEP_ID = :stepID ");
						queryCondition.setObject("campID", campMap.get("CAMPAIGN_ID"));
						queryCondition.setObject("stepID", campMap.get("STEP_ID"));
						dam.exeUpdate(queryCondition);
					}
					dam.setAutoCommit(false);
					logger.info("=====" + campMap.get("CAMPAIGN_ID") + " - " + (String) campMap.get("STEP_ID") + " 名單匯入 END=====");
				}
			} while(list.size() > 0);
		}
	}
	
	// 檢查重覆匯入 PR-處理中 / E1-部份匯入-有失敗名單 / IM-已匯入,無失敗名單 / DP-重複
	public boolean checkDp(DataAccessManager dam, String campID, String stepID) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(SEQNO) AS COUNTS ");
		sb.append("FROM TBCAM_SFA_LEADS_IMP ");
		sb.append("WHERE IMP_STATUS IN ('PR', 'E1', 'IM', 'DP') ");
		sb.append("AND CAMPAIGN_ID = :campID ");
		sb.append("AND STEP_ID = :stepID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("campID", campID);
		queryCondition.setObject("stepID", stepID);
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return (((BigDecimal) list.get(0).get("COUNTS")).compareTo(new BigDecimal(0)) == 0) ? false : true;
	}
	
	// 更新TBCAM_SFA_LEADS_IMP的IMP_STATUS
	public void updateImpStatus(DataAccessManager dam, BigDecimal seqNo, String status) throws JBranchException {
		TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
		vo = (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, seqNo);

		if ("IM".equals(status) || "E1".equals(status)) {
			// E1-部份匯入-有失敗名單 / IM-已匯入,無失敗名單
			vo.setIMP_STATUS(status);
			
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT IMP.CAMPAIGN_ID, IMP.STEP_ID, ");
			sb.append("(SELECT COUNT(TEMP.SEQNO) FROM TBCAM_SFA_LE_IMP_TEMP TEMP WHERE TEMP.IMP_SEQNO = IMP.SEQNO) AS LE_TOTAL_CNT, ");
			sb.append("(SELECT COUNT(LEAD.SFA_LEAD_ID) FROM TBCAM_SFA_LEADS LEAD WHERE LEAD.CAMPAIGN_ID = IMP.CAMPAIGN_ID AND LEAD.STEP_ID = IMP.STEP_ID AND INSTR(LEAD.SFA_LEAD_ID, '_S', -1) = 0 AND INSTR(LEAD.SFA_LEAD_ID, '_BK', -1) = 0) AS IM_TOTAL_CNT, ");
			sb.append("(SELECT COUNT(LEAD.SFA_LEAD_ID) FROM TBCAM_SFA_LEADS LEAD WHERE LEAD.AO_CODE IS NOT NULL AND LEAD.CAMPAIGN_ID = IMP.CAMPAIGN_ID AND LEAD.STEP_ID = IMP.STEP_ID AND INSTR(LEAD.SFA_LEAD_ID, '_S', -1) = 0 AND INSTR(LEAD.SFA_LEAD_ID, '_BK', -1) = 0) AS IM_AO_CNT, ");
			sb.append("(SELECT COUNT(LEAD.SFA_LEAD_ID) FROM TBCAM_SFA_LEADS LEAD WHERE LEAD.AO_CODE IS NULL AND LEAD.CAMPAIGN_ID = IMP.CAMPAIGN_ID AND LEAD.STEP_ID = IMP.STEP_ID AND INSTR(LEAD.SFA_LEAD_ID, '_S', -1) = 0 AND INSTR(LEAD.SFA_LEAD_ID, '_BK', -1) = 0) AS IM_OTHER_CNT ");
			sb.append("FROM TBCAM_SFA_LEADS_IMP IMP ");
			sb.append("WHERE IMP.SEQNO = :seqNo ");
			
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("seqNo", seqNo);
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			
			if (list.size() > 0) {
				vo.setLE_TOTAL_CNT((BigDecimal) list.get(0).get("LE_TOTAL_CNT")); // 應匯入名單總數
				vo.setIM_TOTAL_CNT((BigDecimal) list.get(0).get("IM_TOTAL_CNT")); // 實際匯入總數
				vo.setIM_AO_CNT((BigDecimal) list.get(0).get("IM_AO_CNT")); // 匯入成功(分派至AO)
				vo.setIM_OTHER_CNT((BigDecimal) list.get(0).get("IM_OTHER_CNT")); // 匯入成功(分派至主管)
				vo.setER_CNT(((BigDecimal) list.get(0).get("LE_TOTAL_CNT")).subtract((BigDecimal) list.get(0).get("IM_TOTAL_CNT"))); // 匯入失敗
			} else {
				vo.setLE_TOTAL_CNT(new BigDecimal(0)); // 應匯入名單總數
				vo.setIM_TOTAL_CNT(new BigDecimal(0)); // 實際匯入總數
				vo.setIM_AO_CNT(new BigDecimal(0)); // 匯入成功(分派至AO)
				vo.setIM_OTHER_CNT(new BigDecimal(0)); // 匯入成功(分派至主管)
				vo.setER_CNT(new BigDecimal(0)); // 匯入失敗
			}
		} else {
			// IN-未處理 / PR-處理中 / DP-重複 / E0-未匯入-所有名單分派失敗 / E2-未匯入-無名單明細資料 / E3-未匯入-無行銷活動主檔
			vo.setIMP_STATUS(status);
			
			if ("E0".equals(status) || "E2".equals(status) || "E3".equals(status)) {
				TBCAM_SFA_CAMPAIGNPK cpk = new TBCAM_SFA_CAMPAIGNPK();
				cpk.setCAMPAIGN_ID(vo.getCAMPAIGN_ID());
				cpk.setSTEP_ID(vo.getSTEP_ID());
				TBCAM_SFA_CAMPAIGNVO cvo = new TBCAM_SFA_CAMPAIGNVO();
				cvo.setcomp_id(cpk);
				cvo = (TBCAM_SFA_CAMPAIGNVO) dam.findByPKey(TBCAM_SFA_CAMPAIGNVO.TABLE_UID, cvo.getcomp_id());
				if (null != cvo) {
					dam.delete(cvo);
				}
			}
		}

		vo.setRV_LE_CNT(new BigDecimal(0)); // 已移除名單數

		dam.update(vo);
	}
	
	// 回傳交易所需的名單匯入明細檔資料
	private Integer getImpTemp(DataAccessManager dam, BigDecimal seqNo) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(1) ");
		sb.append("FROM TBCAM_SFA_LE_IMP_TEMP ");
		sb.append("WHERE IMP_SEQNO = :impSeqNo ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("impSeqNo", seqNo);
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list.size();
	}
	
	
}