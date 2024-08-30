package com.systex.jbranch.fubon.jlb;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMPAIGNVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_DOC_MAPPPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_CAMP_DOC_MAPPVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADSVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMPVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LE_IMP_TEMPVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("cam999")
@Scope("prototype")
public class CAM999 extends BizLogic {
	private Logger logger = LoggerFactory.getLogger(CAM999.class);
	SimpleDateFormat SDFYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	public Map<String, Object> importLead (DataAccessManager dam, Map<String, Object> campMap) throws JBranchException, ParseException {

		BigDecimal seqNo = (BigDecimal) campMap.get("SEQNO");
		Timestamp startDate = (Timestamp) campMap.get("START_DATE");
		Timestamp endDate = (Timestamp) campMap.get("END_DATE");
		String leadSourceID = (String) campMap.get("LEAD_SOURCE_ID");
		String campID = (String) campMap.get("CAMPAIGN_ID");
		String stepID = (String) campMap.get("STEP_ID");
		
		// 取得名單匯入暫存檔(客戶明細)
		List<Map<String, Object>> leadList = getImpTemp(dam, seqNo);
		
		// 取得行銷活動預設參數
		List<Map<String, Object>> paraMap = getParaCamp(dam, campID);
		boolean isPara = false; // 是否有預設參數
		if (paraMap.size() > 0 && StringUtils.isNotBlank(leadSourceID) && Integer.parseInt(leadSourceID) <= 3) {
			//名單來源是外部來源
			// 01 - Unica匯入 / 02 - 系統對接 / 03 - 其他系統對接
			isPara = true;
		} 

		// 產生行銷活動主檔
		TBCAM_SFA_CAMPAIGNVO campVO = new TBCAM_SFA_CAMPAIGNVO();
		TBCAM_SFA_CAMPAIGNPK campPK = new TBCAM_SFA_CAMPAIGNPK();
		campPK.setCAMPAIGN_ID((String) campMap.get("CAMPAIGN_ID"));
		campPK.setSTEP_ID((String) campMap.get("STEP_ID"));
		campVO.setcomp_id(campPK);
		campVO.setCAMPAIGN_DESC((String) campMap.get("CAMPAIGN_DESC"));
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_NAME ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_TYPE = 'CAM.LEAD_TYPE' ");
		sb.append("AND PARAM_CODE = :leadType ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("leadType", campMap.get("LEAD_TYPE"));
		List<Map<String, Object>> leadTypeName = dam.exeQuery(queryCondition);
		
		if ("05".equals(campMap.get("LEAD_TYPE")) || "06".equals(campMap.get("LEAD_TYPE")) || "07".equals(campMap.get("LEAD_TYPE")) || "08".equals(campMap.get("LEAD_TYPE")) || 
			"H1".equals(campMap.get("LEAD_TYPE")) || "H2".equals(campMap.get("LEAD_TYPE")) || 
			"I1".equals(campMap.get("LEAD_TYPE")) || "I2".equals(campMap.get("LEAD_TYPE")) || 
			"F2".equals(campMap.get("LEAD_TYPE")) || "F2".equals(campMap.get("LEAD_TYPE")) ||
			"09".equals(campMap.get("LEAD_TYPE"))) {
			campVO.setCAMPAIGN_NAME((String) leadTypeName.get(0).get("PARAM_NAME"));
		} else {
			campVO.setCAMPAIGN_NAME((String) campMap.get("CAMPAIGN_NAME"));
		}
		
		campVO.setSTEP_NAME((String) campMap.get("STEP_NAME"));
		campVO.setLEAD_SOURCE_ID((String) campMap.get("LEAD_SOURCE_ID"));
		campVO.setSTART_DATE((Timestamp) campMap.get("START_DATE"));
		campVO.setEND_DATE(getBusiDay(dam, (Timestamp) campMap.get("END_DATE")));
		campVO.setLEAD_USE_DP((String) campMap.get("LEAD_USE_DP"));
		campVO.setLEAD_DAYUSE(getDayUseCount(dam, startDate, endDate));
		
		//若para有值，優先使用para內的名稱,起迄日
		campVO.setCAMPAIGN_NAME((isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("CAMPAIGN_NAME"))) ? (String) paraMap.get(0).get("CAMPAIGN_NAME") : (StringUtils.isNotBlank((String) campMap.get("CAMPAIGN_NAME")) ? (String) campMap.get("CAMPAIGN_NAME") : campVO.getCAMPAIGN_NAME() ));
		campVO.setSTART_DATE((isPara && null != (Timestamp) paraMap.get(0).get("START_DT")) ? (Timestamp) paraMap.get(0).get("START_DT") : (null != (Timestamp) campMap.get("START_DT") ? (Timestamp) campMap.get("START_DT") : campVO.getSTART_DATE()));
		campVO.setEND_DATE((isPara && null != (Timestamp) paraMap.get(0).get("END_DT")) ? (Timestamp) paraMap.get(0).get("END_DT") : (null != (Timestamp) campMap.get("END_DT") ? (Timestamp) campMap.get("END_DT") : campVO.getEND_DATE()));		
		
		//FILE
		if (isPara) {
			saveParaToCampFile(dam, campID, stepID);
		}
		
		campVO.setLEAD_PARA1((isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("LEAD_PARA1"))) ? (String) paraMap.get(0).get("LEAD_PARA1") : (StringUtils.isNotBlank((String) campMap.get("LEAD_PARA1")) ? (String) campMap.get("LEAD_PARA1") : "N"));
		campVO.setLEAD_PARA2((isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("LEAD_PARA2"))) ? (String) paraMap.get(0).get("LEAD_PARA2") : (StringUtils.isNotBlank((String) campMap.get("LEAD_PARA2")) ? (String) campMap.get("LEAD_PARA2") : ""));
		campVO.setLEAD_TYPE((String) campMap.get("LEAD_TYPE"));
		
		String examID = "";
		if (isPara && null != paraMap.get(0).get("EXAM_ID") && !"0".equals((String) paraMap.get(0).get("EXAM_ID"))) {
			examID = (String) paraMap.get(0).get("EXAM_ID");
		} else if (campMap.get("EXAM_ID") != null && !"0".equals((String) campMap.get("EXAM_ID"))){
			examID = (String) campMap.get("EXAM_ID");
		}
		campVO.setEXAM_ID(examID);

		campVO.setSALES_PITCH((isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("SALES_PITCH"))) ? (String) paraMap.get(0).get("SALES_PITCH") : (StringUtils.isNotBlank((String) campMap.get("SALES_PITCH")) ? (String) campMap.get("SALES_PITCH") : ""));
		campVO.setREMOVE_FLAG("N");
		
		String firstChannel = (isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("FIRST_CHANNEL"))) ? (String) paraMap.get(0).get("FIRST_CHANNEL") : (StringUtils.isNotBlank((String) campMap.get("FIRST_CHANNEL")) ? (String) campMap.get("FIRST_CHANNEL") : ""); //若沒有主要通路，則不壓主要通路
		String secondChannel = (isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("SECOND_CHANNEL"))) ? (String) paraMap.get(0).get("SECOND_CHANNEL") : (StringUtils.isNotBlank((String) campMap.get("SECOND_CHANNEL")) ? (String) campMap.get("SECOND_CHANNEL") : "");
		campVO.setFIRST_CHANNEL(firstChannel);
		campVO.setSECOND_CHANNEL(secondChannel);
		
		campVO.setSTART_DT((isPara && null != (Timestamp) paraMap.get(0).get("START_DT")) ? (Timestamp) paraMap.get(0).get("START_DT") : (null != (Timestamp) campMap.get("START_DT") ? (Timestamp) campMap.get("START_DT") : null));
		campVO.setEND_DT((isPara && null != (Timestamp) paraMap.get(0).get("END_DT")) ? (Timestamp) paraMap.get(0).get("END_DT") : (null != (Timestamp) campMap.get("END_DT") ? (Timestamp) campMap.get("END_DT") : null));
		campVO.setGIFT_CAMPAIGN_ID((isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("GIF_CAMPAIGN_ID"))) ? (String) paraMap.get(0).get("GIF_CAMPAIGN_ID") : (StringUtils.isNotBlank((String) campMap.get("GIF_CAMPAIGN_ID")) ? (String) campMap.get("GIF_CAMPAIGN_ID") : ""));
		if ("01".equals(campMap.get("LEAD_SOURCE_ID")) || "02".equals(campMap.get("LEAD_SOURCE_ID"))) {
			campVO.setCreator((isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("CREATOR"))) ? (String) paraMap.get(0).get("CREATOR") : (StringUtils.isNotBlank((String) campMap.get("CREATOR")) ? (String) campMap.get("CREATOR") : "SCHEDULER"));
		}
		
		campVO.setLEAD_RESPONSE_CODE((isPara && StringUtils.isNotBlank((String) paraMap.get(0).get("LEAD_RESPONSE_CODE"))) ? (String) paraMap.get(0).get("LEAD_RESPONSE_CODE") : (StringUtils.isNotBlank((String) campMap.get("LEAD_RESPONSE_CODE")) ? (String) campMap.get("LEAD_RESPONSE_CODE") : "0000000000"));
		
		// 開始產生名單
		Integer returnCnt = getReturnCnt(dam).intValue(); //多少筆資料回寫一次資料庫
		Integer handleCnt = 0; //紀錄處理進度筆數
		Integer erOtherCnt = 0; //記錄其他錯誤筆數
		Integer imTotalCnt = 0; //分派筆數

		CAM998 cam998 = null;

		for (Map<String, Object> tempMap : leadList) {
			//callCAM998 return VO
			Map<String, Object> outputVO;
			
			try {
				// 取得多少筆回傳一次前端處理筆數
				handleCnt++;

				if (handleCnt % returnCnt == 0) {
					updateImpHandlecnt(dam, seqNo, handleCnt);
				}

				// 2018/6/22 cam140 use
				Boolean addBack = false;
				if("05".equals(campVO.getLEAD_SOURCE_ID()) && (StringUtils.isBlank(ObjectUtils.toString(tempMap.get("CUST_NAME"))) || StringUtils.isBlank(ObjectUtils.toString(tempMap.get("BRANCH_ID")))))
					addBack = true;
				//

				String custID = ((String) tempMap.get("CUST_ID")).trim();
				if (checkCust(dam, custID, tempMap, campVO)) {
					BigDecimal tempSDate = new BigDecimal(SDFYYYYMMDD.format((Timestamp) tempMap.get("START_DATE")));
					BigDecimal tempEDate = new BigDecimal(SDFYYYYMMDD.format((Timestamp) tempMap.get("END_DATE")));

					// 2018/6/22 cam140 use
					if(addBack) {
						TBCAM_SFA_LE_IMP_TEMPVO imp_vo = (TBCAM_SFA_LE_IMP_TEMPVO) dam.findByPKey(TBCAM_SFA_LE_IMP_TEMPVO.TABLE_UID, (BigDecimal) tempMap.get("SEQNO"));
						imp_vo.setCUST_NAME(ObjectUtils.toString(tempMap.get("CUST_NAME")));
						imp_vo.setBRANCH_ID(ObjectUtils.toString(tempMap.get("BRANCH_ID")));
						imp_vo.setAO_CODE(ObjectUtils.toString(tempMap.get("AO_CODE")));
						dam.update(imp_vo);
					}
					//若para有值，優先使用para內的起迄日
					if (isPara){
						tempSDate = new BigDecimal(SDFYYYYMMDD.format((Timestamp) campVO.getSTART_DATE()));
						tempEDate = new BigDecimal(SDFYYYYMMDD.format((Timestamp) campVO.getEND_DATE()));
					}								
					
					if (tempSDate.compareTo(tempEDate) == 1) {
						// 起始日期大於結束日期
						erOtherCnt++;
						updateImpLead(dam, (BigDecimal) tempMap.get("SEQNO"), "E1");
						logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 起始日期大於結束日期*****");
					} else if (checkIsCustDead(dam, custID)) {
						// 新增往生戶判斷
						erOtherCnt++;
						updateImpLead(dam, (BigDecimal) tempMap.get("SEQNO"), "E2");
						logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 往生戶*****");
					} else {
						tempMap.put("START_DATE", (isPara) ? campVO.getSTART_DATE() : tempMap.get("START_DATE"));
						tempMap.put("END_DATE", (isPara) ? campVO.getEND_DATE() : tempMap.get("END_DATE"));
						
						//使用CAM998執行名單分派邏輯
						cam998 = new CAM998();
						logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 分派中*****");
						
						tempMap.put("LEAD_TYPE", campMap.get("LEAD_TYPE"));
						TBCRM_CUST_MASTVO custDtlMap = cam998.getCustDtlMap(dam, tempMap, "FIRST");
						
						logger.info("*****客戶:" + custDtlMap.getCUST_ID() + " - 開始分派 - " + custDtlMap.getUEMP_ID() + "/" + firstChannel + ":" + !StringUtils.equals(firstChannel, "UHRM") + "*****");
						if (StringUtils.isNotBlank(custDtlMap.getUEMP_ID()) && 
							!StringUtils.equals(firstChannel, "UHRM") && 
							(firstChannel).indexOf("FC") > -1 && !StringUtils.equals("AFC", firstChannel)) {
							// 理專：若該客戶有UHRM服務，且第一部隊非UHRM時，強制改為UHRM名單，原部隊則需發佈參考資訊
							logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 理專：若該客戶有UHRM服務，且第一部隊非UHRM時，強制改為UHRM名單*****");
							if (disLead(dam, cam998, "UHRM", "FIRST", campMap, tempMap, custDtlMap, (String) tempMap.get("LEAD_ID"))) {
								imTotalCnt++;
							} else {
								erOtherCnt++;
							}
							
							// 若該客戶有UHRM服務，且第一部隊非UHRM時，強制改為UHRM名單後，原部隊則需發佈參考資訊
							logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 理專：若該客戶有UHRM服務，且第一部隊非UHRM時，強制改為UHRM名單，原部隊則需發佈參考資訊*****");
							tempMap.put("LEAD_TYPE", "04");
							logger.info("*****LEAD_TYPE:" + (String) tempMap.get("LEAD_TYPE") + "*****");
							disLead(dam, cam998, firstChannel, "FIRST_BK", campMap, tempMap, cam998.getCustDtlMap(dam, tempMap, "FIRST_BK"), ((String) tempMap.get("LEAD_ID")) + "_BK");
						} else if (StringUtils.isNotBlank(custDtlMap.getUEMP_ID()) && 
								   !StringUtils.equals(firstChannel, "UHRM")) {
							// 其他：若該客戶有UHRM服務，且第一部隊非UHRM時，原部隊正常分派，UHRM則需發佈參考資訊
							logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 其他：若該客戶有UHRM服務，且第一部隊非UHRM時，原部隊正常分派*****");
							if (disLead(dam, cam998, firstChannel, "FIRST", campMap, tempMap, custDtlMap, (String) tempMap.get("LEAD_ID"))) {
								imTotalCnt++;
							} else {
								erOtherCnt++;
							}
							
							// 其他：若該客戶有UHRM服務，且第一部隊非UHRM時，原部隊正常分派，UHRM則需發佈參考資訊
							logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 其他：若該客戶有UHRM服務，且第一部隊非UHRM時，原部隊正常分派，UHRM則需發佈參考資訊*****");
							tempMap.put("LEAD_TYPE", "04");
							logger.info("*****LEAD_TYPE:" + (String) tempMap.get("LEAD_TYPE") + "*****");
							disLead(dam, cam998, "UHRM", "FIRST_BK", campMap, tempMap, cam998.getCustDtlMap(dam, tempMap, "FIRST_BK"), ((String) tempMap.get("LEAD_ID")) + "_BK");
						} else {
							// 若該客戶無UHRM服務，正常分派
							if (disLead(dam, cam998, firstChannel, "FIRST", campMap, tempMap, custDtlMap, (String) tempMap.get("LEAD_ID")))
								imTotalCnt++;
							else 
								erOtherCnt++;
						}
					}
				} else {
					// 非本行客戶
					erOtherCnt++;
					updateImpLead(dam, (BigDecimal) tempMap.get("SEQNO"), "E3");
					logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 非本行客戶*****");
				}
			}finally{
				cam998 = null;
				outputVO = null;
				tempMap = null;
			}
		}
		
		dam.create(campVO);
		
		// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單 - 隱藏第二使用部隊之功能
//		// 第一部隊有分派成功名單才進行第二部隊名單分派
//		if (imTotalCnt > 0) {
//			// 開始處理第二部隊名單
//			logger.info("=====處理第二部隊名單("+ secondChannel + ") START=====");
//			if (StringUtils.isNotBlank(secondChannel)) {
//				String[] secondChannel_ary = secondChannel.split(",");
//
//				Integer row_count = 0; // 已處理筆數
//				for (String channelStr : secondChannel_ary) {
//					
//					// 取得多少筆回傳一次前端處理筆數
//					handleCnt++;
//					if (handleCnt % returnCnt == 0) {
//						updateImpHandlecnt(dam, seqNo, handleCnt);
//					}
//					
//					// 防止第一部隊與第二部隊同一個造成重覆
//					if (channelStr.equals(firstChannel)) {
//						logger.info("*****第一部隊與第二部隊同一個造成重覆*****");
//						continue;
//					}
//					
//					campMap.put("LEAD_TYPE", "04"); //2016-12-28 第二部隊皆為參考資訊
//					for (Map<String, Object> tempMap : leadList) {
//						try {
//							String custID = (String) tempMap.get("CUST_ID");
//							if (checkCust(dam, custID, tempMap, campVO)) {
//								Integer tempSDate = Integer.valueOf(SDFYYYYMMDD.format((Timestamp) tempMap.get("START_DATE")));
//								Integer tempEDate = Integer.valueOf(SDFYYYYMMDD.format((Timestamp) tempMap.get("END_DATE")));
//
//								if (tempSDate > tempEDate) {
//									// 起始日期大於結束日期
//									// 起始日期大於結束日期為E4排除名單
//									logger.info("*****起始日期大於結束日期*****");
//									continue;
//								} else {
//									//使用CAM998執行名單分派邏輯
//									cam998 = new CAM998();
//									
//									tempMap.put("LEAD_TYPE", campMap.get("LEAD_TYPE"));
//									TBCRM_CUST_MASTVO custDtlMap = cam998.getCustDtlMap(dam, tempMap, "SECOND_BK");
//									
//									disLead(dam, cam998, channelStr, "SECOND", campMap, tempMap, custDtlMap, ((String) tempMap.get("LEAD_ID")) + channelStr + "_S");
////									Map<String, Object> outputVO = cam998.callCAM998(dam, "SYS", campID, channelStr, campMap, tempMap);
////
////									if ("Y".equals(outputVO.get("DISPATCH"))) {
////										// 產生名單主檔
////										// 檢查名單是否已存在
////										String sfaLeadID = ((String) tempMap.get("LEAD_ID")) + channelStr + "_S";
////										if (checkLeadID(dam, sfaLeadID)) {
////											continue;
////										} else {
////											cam998.insertLeads(dam, campID, stepID, channelStr, "SECOND", campMap, tempMap, outputVO, sfaLeadID);
////										}
////									}
//								}
//							} else {
//								logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 非本行客戶*****");
//							}
//						} finally {
//							tempMap = null;
//							cam998 = null;
//						}
//					}
//				}
//			}
//			logger.info("=====處理第二部隊名單 END=====");
//		}
		
		Map<String, Object> returnResult = new HashMap<String, Object>();
		returnResult.put("imTotalCnt", imTotalCnt);
		returnResult.put("erOtherCnt", erOtherCnt);
		if (imTotalCnt != 0) {
			// ===變動欄位整批INSERT
			setChangeData(dam, seqNo);
			// === END
		}
		return returnResult;
	}

	public boolean disLead (DataAccessManager dam, 
							CAM998 cam998, 
							String channl, 
							String channelType, 
							Map<String, Object> campMap, 
							Map<String, Object> tempMap, 
							TBCRM_CUST_MASTVO custDtlMap, 
							String sfaLeadID) throws JBranchException, ParseException {
		
		Map<String, Object> outputVO = new HashMap<String, Object>();
		outputVO = cam998.callCAM998(dam, "SYS", (String) campMap.get("CAMPAIGN_ID"), channl, campMap, tempMap, custDtlMap);
		logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 分派中*****" + outputVO);

		if ("Y".equals(outputVO.get("DISPATCH")) && StringUtils.isNotBlank((String) outputVO.get("BRANCH_ID"))) {
//			imTotalCnt++;

			// 產生名單主檔
			// 檢查名單是否已存在
			if (checkLeadID(dam, sfaLeadID)) {
				//"FIRST".equals(channelType) ? (String) campMap.get("LEAD_TYPE") : "04"
				if (StringUtils.equals("FIRST", channelType)) {
					cam998.updLead(dam, "SCHDULER", "SYS", (String) campMap.get("CAMPAIGN_ID"), channl, campMap, tempMap);
				} else {
					return false;
				}
			} else {
				if (!StringUtils.equals("FIRST", channelType) && StringUtils.isNotBlank((String) outputVO.get("AO_CODE"))) { 	
					// 若非第一部隊，且有派至人員身上，則正常分派
					cam998.insertLeads(dam, (String) campMap.get("CAMPAIGN_ID"), (String) campMap.get("STEP_ID"), channl, channelType, campMap, tempMap, outputVO, sfaLeadID);
				} else if (StringUtils.equals("FIRST", channelType)) {	
					// 若為第一部隊，參照分派結果
					cam998.insertLeads(dam, (String) campMap.get("CAMPAIGN_ID"), (String) campMap.get("STEP_ID"), channl, channelType, campMap, tempMap, outputVO, sfaLeadID);
				} else {
					// 若非第一部隊，且無派至人員身上，則不產生名單
				}
			}
			
			if (StringUtils.equals("FIRST", channelType)) {
				updateImpLead(dam, (BigDecimal) tempMap.get("SEQNO"), "Y");
			}
			
			return true;
		} else { // 分派失敗
//			erOtherCnt++;

			logger.info("*****" + (BigDecimal) tempMap.get("SEQNO") + " - 分派失敗*****" + outputVO);
			updateImpLead(dam, (BigDecimal) tempMap.get("SEQNO"), "E4");
			
			return false;
		}
	}
		
	
	public void setChangeData (DataAccessManager dam, BigDecimal seqNo) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO TBCAM_SFA_LEADS_VAR (SFA_LEAD_ID, ");
		sb.append("VAR_FIELD_LABEL1, VAR_FIELD_VALUE1, VAR_FIELD_LABEL2, VAR_FIELD_VALUE2, ");
		sb.append("VAR_FIELD_LABEL3, VAR_FIELD_VALUE3, VAR_FIELD_LABEL4, VAR_FIELD_VALUE4, ");
		sb.append("VAR_FIELD_LABEL5, VAR_FIELD_VALUE5, VAR_FIELD_LABEL6, VAR_FIELD_VALUE6, ");
		sb.append("VAR_FIELD_LABEL7, VAR_FIELD_VALUE7, VAR_FIELD_LABEL8, VAR_FIELD_VALUE8, ");
		sb.append("VAR_FIELD_LABEL9, VAR_FIELD_VALUE9, VAR_FIELD_LABEL10, VAR_FIELD_VALUE10, ");
		sb.append("VAR_FIELD_LABEL11, VAR_FIELD_VALUE11, VAR_FIELD_LABEL12, VAR_FIELD_VALUE12, ");
		sb.append("VAR_FIELD_LABEL13, VAR_FIELD_VALUE13, VAR_FIELD_LABEL14, VAR_FIELD_VALUE14, ");
		sb.append("VAR_FIELD_LABEL15, VAR_FIELD_VALUE15, VAR_FIELD_LABEL16, VAR_FIELD_VALUE16, ");
		sb.append("VAR_FIELD_LABEL17, VAR_FIELD_VALUE17, VAR_FIELD_LABEL18, VAR_FIELD_VALUE18, ");
		sb.append("VAR_FIELD_LABEL19, VAR_FIELD_VALUE19, VAR_FIELD_LABEL20, VAR_FIELD_VALUE20) ");
		sb.append("SELECT LDS.SFA_LEAD_ID, ");
		sb.append("TMP.VAR_FIELD_LABEL1, TMP.VAR_FIELD_VALUE1, TMP.VAR_FIELD_LABEL2, TMP.VAR_FIELD_VALUE2, ");
		sb.append("TMP.VAR_FIELD_LABEL3, TMP.VAR_FIELD_VALUE3, TMP.VAR_FIELD_LABEL4, TMP.VAR_FIELD_VALUE4, ");
		sb.append("TMP.VAR_FIELD_LABEL5, TMP.VAR_FIELD_VALUE5, TMP.VAR_FIELD_LABEL6, TMP.VAR_FIELD_VALUE6,");
		sb.append("TMP.VAR_FIELD_LABEL7, TMP.VAR_FIELD_VALUE7, TMP.VAR_FIELD_LABEL8, TMP.VAR_FIELD_VALUE8, ");
		sb.append("TMP.VAR_FIELD_LABEL9, TMP.VAR_FIELD_VALUE9, TMP.VAR_FIELD_LABEL10, TMP.VAR_FIELD_VALUE10, ");
		sb.append("TMP.VAR_FIELD_LABEL11, TMP.VAR_FIELD_VALUE11, TMP.VAR_FIELD_LABEL12, TMP.VAR_FIELD_VALUE12, ");
		sb.append("TMP.VAR_FIELD_LABEL13, TMP.VAR_FIELD_VALUE13, TMP.VAR_FIELD_LABEL14, TMP.VAR_FIELD_VALUE14, ");
		sb.append("TMP.VAR_FIELD_LABEL15, TMP.VAR_FIELD_VALUE15, TMP.VAR_FIELD_LABEL16, TMP.VAR_FIELD_VALUE16,");
		sb.append("TMP.VAR_FIELD_LABEL17, TMP.VAR_FIELD_VALUE17, TMP.VAR_FIELD_LABEL18, TMP.VAR_FIELD_VALUE18, ");
		sb.append("TMP.VAR_FIELD_LABEL19, TMP.VAR_FIELD_VALUE19, TMP.VAR_FIELD_LABEL20, TMP.VAR_FIELD_VALUE20 ");
		sb.append("FROM TBCAM_SFA_LE_IMP_TEMP TMP ");
		sb.append("INNER JOIN TBCAM_SFA_LEADS LDS ON TMP.LEAD_ID = LDS.SFA_LEAD_ID ");
		sb.append("LEFT JOIN TBCAM_SFA_LEADS_VAR VAR ON LDS.SFA_LEAD_ID = VAR.SFA_LEAD_ID ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND (TMP.VAR_FIELD_LABEL1 IS NOT NULL OR ");
		sb.append("TMP.VAR_FIELD_LABEL2 IS NOT NULL OR ");
		sb.append("TMP.VAR_FIELD_LABEL3 IS NOT NULL OR ");
		sb.append("TMP.VAR_FIELD_LABEL4 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL5 IS NOT NULL OR ");
		sb.append("TMP.VAR_FIELD_LABEL6 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL7 IS NOT NULL OR ");
		sb.append("TMP.VAR_FIELD_LABEL8 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL9 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL10 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL11 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL12 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL13 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL14 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL15 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL16 IS NOT NULL OR ");
		sb.append("TMP.VAR_FIELD_LABEL17 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL18 IS NOT NULL OR "); 
		sb.append("TMP.VAR_FIELD_LABEL19 IS NOT NULL OR ");
		sb.append("TMP.VAR_FIELD_LABEL20 IS NOT NULL ");
		sb.append(") ");
		sb.append("AND IMP_SEQNO = :seqNo ");
		sb.append("AND IMP_FLAG IN ('Y', 'E0') ");
		sb.append("AND VAR.SFA_LEAD_ID IS NULL");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("seqNo", seqNo);
		
		dam.exeUpdate(queryCondition);
	}
	
	public boolean saveParaToCampFile(DataAccessManager dam, String campaignID, String stepID) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString(paraFileSQL().toString());
		queryCondition.setObject("campaignID", campaignID);
		queryCondition.setObject("stepID", stepID);
		
		List<Map<String, Object>> paraFileList = dam.exeQuery(queryCondition);
		
		if (paraFileList.size() > 0) {
			TBCAM_SFA_CAMP_DOC_MAPPPK dmpk = new TBCAM_SFA_CAMP_DOC_MAPPPK();
			dmpk.setCAMPAIGN_ID((String) paraFileList.get(0).get("CAMPAIGN_ID"));
			dmpk.setSTEP_ID((String) paraFileList.get(0).get("STEP_ID"));
			dmpk.setSFA_DOC_ID((String) paraFileList.get(0).get("SFA_DOC_ID"));
			
			TBCAM_SFA_CAMP_DOC_MAPPVO dmvo = new TBCAM_SFA_CAMP_DOC_MAPPVO();
			dmvo.setcomp_id(dmpk);
			dmvo = (TBCAM_SFA_CAMP_DOC_MAPPVO) dam.findByPKey(TBCAM_SFA_CAMP_DOC_MAPPVO.TABLE_UID, dmvo.getcomp_id());
			
			if (null != dmvo) {
				dam.delete(dmvo);
			}

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO TBCAM_SFA_CAMP_DOC_MAPP (CAMPAIGN_ID, STEP_ID, SFA_DOC_ID, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
			sb.append(paraFileSQL());

			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("campaignID", campaignID);
			queryCondition.setObject("stepID", stepID);
			
			dam.exeUpdate(queryCondition);
			return true;
		} else {
			return false;
		}
	}
	
	public StringBuffer paraFileSQL() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARA.CAMPAIGN_ID, :stepID AS STEP_ID, MAPP.SFA_DOC_ID, 0 AS VERSION, SYSDATE AS CREATETIME, 'SYSTEM' AS CREATOR, 'SYSTEM' AS MODIFIER, SYSDATE AS LASTUPDATE ");
		sb.append("FROM TBCAM_SFA_PARA_DOC_MAPP MAPP ");
		sb.append("LEFT JOIN TBCAM_SFA_PARAMETER PARA ON MAPP.SFA_PARA_ID = PARA.SFA_PARA_ID ");
		sb.append("WHERE PARA.CAMPAIGN_ID = :campaignID ");
		return sb;
	}
	
	// 提供前一日工作日
	public Timestamp getBusiDay(DataAccessManager dam, Timestamp endDate) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CASE WHEN (SELECT PABTH_UTIL.FC_IsHoliday(TO_DATE(:endDate, 'yyyyMMdd'), 'TWD') AS HOL_DAY FROM DUAL) = 'Y' THEN (SELECT PABTH_UTIL.FC_getBusiDay(TO_DATE(:endDate, 'yyyyMMdd'), 'TWD', 1) AS BUSI_DAY FROM DUAL) ");
		sb.append("ELSE TO_DATE(:endDate, 'yyyyMMdd') END AS BUSI_DAY ");
		sb.append("FROM DUAL ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("endDate", SDFYYYYMMDD.format(endDate));
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return (Timestamp) list.get(0).get("BUSI_DAY");
	}
	
	// 計算工作日
	public BigDecimal getDayUseCount(DataAccessManager dam, Timestamp startDate, Timestamp endDate) throws JBranchException {

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PABTH_UTIL.FC_getTwoDateDiff(TO_DATE(:sDate, 'yyyyMMdd'), TO_DATE(:eDate, 'yyyyMMdd')) AS COUNT_WORK_DAY ");
		sb.append("FROM DUAL ");
		queryCondition.setObject("sDate", SDFYYYYMMDD.format(startDate));
		queryCondition.setObject("eDate", SDFYYYYMMDD.format(endDate));
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return (BigDecimal) list.get(0).get("COUNT_WORK_DAY");
	}
	
	// 名單匯入主檔明細
	public List<Map<String, Object>> getImpCamp (DataAccessManager dam, String campID, String stepID) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQNO, CAMPAIGN_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, STEP_ID, STEP_NAME, LEAD_SOURCE_ID, START_DATE, END_DATE, ");
		sb.append("LEAD_USE_DP, LEAD_TYPE, LEAD_PARA1, LEAD_PARA2, LEAD_DAYUSE, FILE_SEQ, EXAM_ID, SALES_PITCH, FIRST_CHANNEL, SECOND_CHANNEL, IMP_STATUS, CHECK_STATUS, ");
		sb.append("LE_TOTAL_CNT, IM_TOTAL_CNT, IM_AO_CNT, IM_OTHER_CNT, ER_CNT, RV_LE_CNT, RV_REASON, START_DT, END_DT, GIFT_CAMPAIGN_ID, RE_STATUS, RE_LOG, HANDLE_CNT ");
		sb.append("FROM TBCAM_SFA_LEADS_IMP ");
		sb.append("WHERE CAMPAIGN_ID = :campID ");
		sb.append("AND STEP_ID = :stepID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("campID", campID);
		queryCondition.setObject("stepID", stepID);
		
		return dam.exeQuery(queryCondition);
	}
	
	// 回傳交易所需的名單匯入明細檔資料
	private List<Map<String, Object>> getImpTemp(DataAccessManager dam, BigDecimal seqNo) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQNO, IMP_SEQNO, LEAD_ID, CUST_ID, CUST_NAME, BRANCH_ID, EMP_ID, AO_CODE, ");
		sb.append("       IMP_FLAG, START_DATE, END_DATE, LEAD_TYPE, LEAD_STATUS, LEAD_MEMO ");
		sb.append("FROM TBCAM_SFA_LE_IMP_TEMP ");
		sb.append("WHERE IMP_SEQNO = :impSeqNo ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("impSeqNo", seqNo);
		
		return dam.exeQuery(queryCondition);
	}
	
	// 回傳交易所需的名單匯入參數檔資料
	private List<Map<String, Object>> getParaCamp(DataAccessManager dam, String campID) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SFA_PARA_ID, CAMPAIGN_ID, CAMPAIGN_NAME, CAMPAIGN_DESC, LEAD_SOURCE_ID, LEAD_TYPE, LEAD_PARA1, LEAD_PARA2, ");
		sb.append("EXAM_ID, SALES_PITCH, FIRST_CHANNEL, SECOND_CHANNEL, START_DT, END_DT, GIFT_CAMPAIGN_ID, LEAD_RESPONSE_CODE ");
		sb.append("FROM TBCAM_SFA_PARAMETER ");
		sb.append("WHERE CAMPAIGN_ID = :campID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("campID", campID);
		
		return dam.exeQuery(queryCondition);
	}
	
	// 取得多少筆回傳一次前端處理筆數
	private BigDecimal getReturnCnt(DataAccessManager dam) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_NAME ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_TYPE = 'CAM.LEADS_IMP' ");
		sb.append("AND PARAM_CODE = 'RETURN_CNT' ");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return new BigDecimal((String) list.get(0).get("PARAM_NAME"));
	}
	
	// 取得多少筆回傳一次前端處理筆數
	private void updateImpHandlecnt(DataAccessManager dam, BigDecimal seqNo, Integer handleCnt) throws JBranchException {
		TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
		vo = (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, seqNo);
		vo.setHANDLE_CNT(new BigDecimal(handleCnt));
		dam.update(vo);
	}
	
	// 是否為客戶
	private boolean checkCust(DataAccessManager dam, String custID, Map<String, Object> tempMap, TBCAM_SFA_CAMPAIGNVO campVO) throws JBranchException {
		
		String leadType = (String) tempMap.get("LEAD_TYPE");
		String leadSourceID = campVO.getLEAD_SOURCE_ID();
		
		TBCRM_CUST_MASTVO vo = new TBCRM_CUST_MASTVO();
		vo = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, custID);
		if (StringUtils.equals("05", leadType) || StringUtils.equals("06", leadType) || StringUtils.equals("07", leadType) || StringUtils.equals("08", leadType) || 
			StringUtils.equals("H1", leadType) || StringUtils.equals("H2", leadType) || StringUtils.equals("I1", leadType) || StringUtils.equals("I2", leadType) || 
			StringUtils.equals("F1", leadType) || StringUtils.equals("F2", leadType) ||
			StringUtils.equals("09", leadType)) {
			logger.info("*****05~09,H1~F2 可分派****");
			return true;
		} else if (StringUtils.equals("05", leadSourceID)) {
			if(null == vo) {
				// 2018/6/22 非本行branch不可為空
				if(StringUtils.isBlank(ObjectUtils.toString(tempMap.get("BRANCH_ID")))) {
					logger.info("非本行客戶分行不得為空");
					return false;
				}
			} else {
				// 2018/6/22 add branch back because cam140 remove vo
				tempMap.put("CUST_NAME", vo.getCUST_NAME());
				
				// 2020/2/26 modify by ocean
				if (null == tempMap.get("BRANCH_ID")) {
					tempMap.put("BRANCH_ID", vo.getBRA_NBR());
				}
				
				if(StringUtils.isBlank(ObjectUtils.toString(tempMap.get("AO_CODE"))))
					tempMap.put("AO_CODE", vo.getAO_CODE());
			}
			
			return true;
		} else if (null != vo) {
			logger.info("*****客戶存在 可分派****");
			return true;
		} else {
			logger.info("*****客戶不存在 不可分派****");
			return false;
		}
	}

	// 更新匯入結果
	private void updateImpLead(DataAccessManager dam, BigDecimal seqNo, String impFlag) throws JBranchException {
		
		TBCAM_SFA_LE_IMP_TEMPVO vo = new TBCAM_SFA_LE_IMP_TEMPVO();
		vo = (TBCAM_SFA_LE_IMP_TEMPVO) dam.findByPKey(TBCAM_SFA_LE_IMP_TEMPVO.TABLE_UID, seqNo);
		vo.setIMP_FLAG(impFlag);
		
		dam.update(vo);
	}
		
	// 往生戶判斷
	private boolean checkIsCustDead(DataAccessManager dam, String custID) throws JBranchException {
		
		TBCRM_CUST_NOTEVO vo = new TBCRM_CUST_NOTEVO();
		vo = (TBCRM_CUST_NOTEVO) dam.findByPKey(TBCRM_CUST_NOTEVO.TABLE_UID, custID);
		
		return (null != vo) ? "Y".equals(vo.getDEATH_YN()) ? true : false : false;
	}
	
	// 檢查名單是否已存在
	private boolean checkLeadID(DataAccessManager dam, String sfaLeadID) throws JBranchException {
		
		TBCAM_SFA_LEADSVO vo = new TBCAM_SFA_LEADSVO();
		vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, sfaLeadID);
		
		return (null != vo) ? true : false;
	}

	public void removeLead (DataAccessManager dam, String type, String campID, String stepID, String rvReason) throws JBranchException {
		List<Map<String, Object>> list = getImpDtl(dam, campID, stepID); // 修改活動移除/修改狀態為R1：名單移除(處理中)
		
		if (list.size() > 0) {
			updateImp(dam, list, 0, null, null, null);
			updateCamp(dam, list);
			
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE TBCAM_SFA_LEADS ");
			sb.append("SET LEAD_STATUS = 'TR' ");
			sb.append("WHERE 1=1 ");
			if ("M1".equals(type)) { //一般移除
				sb.append("AND LEAD_STATUS = '01' ");
			}
			sb.append("AND CAMPAIGN_ID = :campID ");
			sb.append("AND STEP_ID = :stepID ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("campID", campID);
			queryCondition.setObject("stepID", stepID);
			
			Integer cnt = dam.exeUpdate(queryCondition);
			
			updateImp(dam, list, 1, type, rvReason, cnt);
		}
	}
	
	public List<Map<String, Object>> getImpDtl (DataAccessManager dam, String campID, String stepID) throws JBranchException{
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PA.SFA_PARA_ID, IMP.SEQNO, ");
		sb.append("CAMP.STEP_ID, CAMP.CREATETIME, CAMP.CAMPAIGN_ID, CAMP.CAMPAIGN_NAME, CAMP.CAMPAIGN_DESC, ");
		sb.append("CAMP.START_DATE, CAMP.END_DATE, IMP.RV_LE_CNT ");
		sb.append("FROM TBCAM_SFA_CAMPAIGN CAMP ");
		sb.append("LEFT JOIN TBCAM_SFA_PARAMETER PA ON CAMP.CAMPAIGN_ID = PA.CAMPAIGN_ID ");
		sb.append("LEFT JOIN TBCAM_SFA_LEADS_IMP IMP ON IMP.CAMPAIGN_ID = CAMP.CAMPAIGN_ID AND IMP.STEP_ID = CAMP.STEP_ID ");
		sb.append("WHERE CAMP.CAMPAIGN_ID = :campID ");
		sb.append("AND CAMP.STEP_ID = :stepID ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("campID", campID);
		queryCondition.setObject("stepID", stepID);
		
		return dam.exeQuery(queryCondition);
	}
	
	public void updateImp (DataAccessManager dam, List<Map<String, Object>> campImp, Integer step, String type, String rvReason, Integer cnt) throws JBranchException {
		
		TBCAM_SFA_LEADS_IMPVO vo = new TBCAM_SFA_LEADS_IMPVO();
		vo = (TBCAM_SFA_LEADS_IMPVO) dam.findByPKey(TBCAM_SFA_LEADS_IMPVO.TABLE_UID, (BigDecimal) campImp.get(0).get("SEQNO"));
		if (null != vo) {
			switch (step) {
				case 0:
					vo.setRE_STATUS("R1");
					break;
				case 1:
					vo.setRE_STATUS("R2");
					vo.setRV_LE_CNT(new BigDecimal(cnt));
					vo.setRV_REASON(rvReason);
					vo.setRE_LOG(type);
					break;
			}
			
			dam.update(vo);
		}
	}
	
	public void updateCamp (DataAccessManager dam, List<Map<String, Object>> campImp) throws JBranchException {
		
		TBCAM_SFA_CAMPAIGNVO vo = new TBCAM_SFA_CAMPAIGNVO();
		TBCAM_SFA_CAMPAIGNPK pk = new TBCAM_SFA_CAMPAIGNPK();
		pk.setCAMPAIGN_ID((String) campImp.get(0).get("CAMPAIGN_ID"));
		pk.setSTEP_ID((String) campImp.get(0).get("STEP_ID"));
		vo.setcomp_id(pk);
		vo = (TBCAM_SFA_CAMPAIGNVO) dam.findByPKey(TBCAM_SFA_CAMPAIGNVO.TABLE_UID, vo.getcomp_id());
		if (null != vo) {
			vo.setREMOVE_FLAG("Y");
		
			dam.update(vo);
		}
	}
	
}
