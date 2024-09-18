package com.systex.jbranch.fubon.jlb;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADSVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LE_REA_LOGPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LE_REA_LOGVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("cam998")
@Scope("prototype")
public class CAM998 extends BizLogic {
		
	private Logger logger = LoggerFactory.getLogger(CAM998.class);

	SimpleDateFormat SDFYYYYMMDD 	   = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat SDFYYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMdd HHmmss");

	public Map<String, Object> callCAM998 (DataAccessManager dam, 
										   String source, 
										   String campID, 
										   String channel,
										   Map<String, Object> campMap, 
										   Map<String, Object> tempMap, 
										   TBCRM_CUST_MASTVO custDtlMap) throws JBranchException {
		
		Map<String, Object> leadsMap = new HashMap<String, Object>();
		Map<String, Object> dispatchLead;
		
		try {
			leadsMap.put("CUST_ID", (String) tempMap.get("CUST_ID")); // 客戶ID
			leadsMap.put("CAMPAIGN_ID", (String) campMap.get("CAMPAIGN_ID")); // 活動代碼
			leadsMap.put("STEP_ID", (String) campMap.get("STEP_ID")); // 步驟代碼
			leadsMap.put("CHANNEL", channel); // 部隊
			leadsMap.put("AO_CODE", (String) tempMap.get("AO_CODE")); // 理專AOCODE
			leadsMap.put("BRANCH_ID",  (String) tempMap.get("BRANCH_ID")); // 分行
			leadsMap.put("EMP_ID", (String) tempMap.get("EMP_ID")); // 指定員編
			leadsMap.put("LEAD_TYPE", (String) tempMap.get("LEAD_TYPE")); // 名單類型
			leadsMap.put("START_DATE", (Timestamp) campMap.get("START_DATE")); // 活動起日
			leadsMap.put("END_DATE", (Timestamp) campMap.get("END_DATE")); // 活動迄日
			leadsMap.put("LEAD_ID", (String) tempMap.get("LEAD_ID")); // 來源系統的名單代碼

			logger.info("=====CAM998執行名單分派邏輯開始=====");
			logger.info("CUST_ID: " + tempMap.get("CUST_ID") + "/ " +
						"CAMPAIGN_ID: " + campMap.get("CAMPAIGN_ID") + "/ " +
						"STEP_ID: " + campMap.get("STEP_ID") + "/ " +
						"CHANNEL: " + channel + "/" +
						"AO_CODE: " + tempMap.get("AO_CODE") + "/ " +
						"BRANCH_ID: " + tempMap.get("BRANCH_ID") + "/ " +
						"EMP_ID: " + tempMap.get("EMP_ID") + "/ " +
						"LEAD_TYPE: " + campMap.get("LEAD_TYPE") + "/ " +
						"START_DATE: " + campMap.get("START_DATE") + "/ " +
						"END_DATE: " + campMap.get("END_DATE") + "/ " +
						"LEAD_ID: " + tempMap.get("LEAD_ID"));

			dispatchLead = dispatchLead(dam, leadsMap, source, custDtlMap);
			logger.info("=====CAM998執行名單分派邏輯結束=====");
		} finally {
			leadsMap = null;
		}
		
		logger.info("RETURN :" + dispatchLead);
		
		return dispatchLead;
	}
	
	public void insertLeads(DataAccessManager dam, 
							String campID, 
							String stepID, 
							String channel, 
							String channelType, 
							Map<String, Object> campMap, 
							Map<String, Object> tempMap, 
							Map<String, Object> outputVO, 
							String sfaLeadID) throws JBranchException, ParseException {
		
		TBCAM_SFA_LEADSVO vo;
		try {
			vo = new TBCAM_SFA_LEADSVO();
			vo.setSFA_LEAD_ID(sfaLeadID);
			vo.setCUST_ID((String) tempMap.get("CUST_ID"));
			vo.setAO_CODE(null != outputVO.get("AO_CODE") ? (String) outputVO.get("AO_CODE") : null);
			vo.setEMP_ID(null != outputVO.get("EMP_ID") ? (String) outputVO.get("EMP_ID") : null);
			vo.setLEAD_NAME((String) campMap.get("CAMPAIGN_NAME"));
			vo.setLEAD_STATUS("01");
			vo.setBRANCH_ID((String) outputVO.get("BRANCH_ID"));
			vo.setCAMPAIGN_ID(campID);
			vo.setSTEP_ID(stepID);
			vo.setSTART_DATE(new CAM999().getBusiDay(dam, (Timestamp) tempMap.get("START_DATE")));
			
			// modify by ocean 20181105 : 生日通知的到期日為原名單暫存檔之到期日，其他名單則需確認是否為工作日
			vo.setEND_DATE((StringUtils.equals(campID, "EVENT_BIRTH") ? (Timestamp) tempMap.get("END_DATE") : new CAM999().getBusiDay(dam, (Timestamp) tempMap.get("END_DATE"))));
			
//			vo.setEXPECTED_DATE((null != outputVO.get("EXPECTED_DATE")) ? new Timestamp(SDFYYYYMMDDHHMMSS.parse(((String) outputVO.get("EXPECTED_DATE")) + " 000000").getTime()) : null);
			vo.setLEAD_MEMO((String) tempMap.get("LEAD_MEMO"));
			vo.setDISP_FLAG("S");
			vo.setDISP_DATE(new Timestamp(System.currentTimeMillis()));
			vo.setLEAD_CHANNEL(channel);
			vo.setLEAD_TYPE("FIRST".equals(channelType) ? (String) campMap.get("LEAD_TYPE") : "04");

			dam.create(vo);
		} finally {
			vo = null;
		}
	}
	
	// update leads (主管分派、改派)
	public Map<String, Object> updLead(DataAccessManager dam, 
						String loginUser, 
						String source, 
						String campID,
						String channel,
						Map<String, Object> campMap, 
						Map<String, Object> tempMap) throws JBranchException, ParseException {
		
		Map<String, Object> outputVO = new HashMap<String, Object>();
		
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		TBCAM_SFA_LEADSVO vo = new TBCAM_SFA_LEADSVO();
		try {
			switch (source) {
				case "SYS": // 系統分派
					vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, (String) tempMap.get("SFA_LEAD_ID"));
					
					vo.setLEAD_STATUS((String) tempMap.get("LEAD_STATUS"));
					vo.setLastupdate(stamp);
					vo.setModifier(loginUser);
					
					dam.update(vo);
					
					break;
				case "10": // 10:作廢
					vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, (String) tempMap.get("SFA_LEAD_ID"));
					
					vo.setLEAD_STATUS("TR");
					vo.setLEAD_MEMO(vo.getLEAD_MEMO() + " / 作廢原因： " + tempMap.get("REASON"));
					vo.setModifier(loginUser);
					vo.setLastupdate(stamp);

					dam.update(vo);

					insReaLog(dam, loginUser, stamp, (String) tempMap.get("SFA_LEAD_ID"), source, (String) tempMap.get("REASON"));
					
					break;
				default: // 20:改派/30:手動分派
					outputVO = callCAM998(dam, source, campID, channel, campMap, tempMap, getCustDtlMap(dam, tempMap, "UPD_LEAD"));

					if ("Y".equals(outputVO.get("DISPATCH")) && null != outputVO.get("EMP_ID")) {
						vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, (String) tempMap.get("SFA_LEAD_ID"));
						
						vo.setAO_CODE((String) outputVO.get("AO_CODE"));
						vo.setEMP_ID((String) outputVO.get("EMP_ID"));
						vo.setBRANCH_ID((String) outputVO.get("BRANCH_ID"));
						vo.setDISP_FLAG("B"); //B：分行主管分派
						vo.setDISP_DATE(stamp);
						vo.setModifier(loginUser);
						vo.setLastupdate(stamp);

						dam.update(vo);

						insReaLog(dam, loginUser, stamp, (String) tempMap.get("SFA_LEAD_ID"), source, null);
					} else {
						logger.error("DISPATCH = " + outputVO.get("DISPATCH"));
						logger.error("AO_CODE = " + outputVO.get("AO_CODE") + ", EMP_ID = " + outputVO.get("EMP_ID"));
						logger.error("CAMPAIGN_ID = " + campMap.get("CAMPAIGN_ID") + ", STEP_ID = " + campMap.get("STEP_ID") + " - 員工(未登入/不存在)，暫不可分派");
					}
					
					break;
			}
		} finally {
			vo = null;
		}
		
		return outputVO;
	}
	
	// 建立名單異動記錄檔
	public void insReaLog(DataAccessManager dam, 
						  String loginUser, 
						  Timestamp stamp, 
						  String sfaLeadID, 
						  String source, 
						  String reason) throws JBranchException {
		
		logger.info("=====建立名單異動記錄檔 START=====");
		logger.info("loginUser: " + loginUser);
		logger.info("stamp: " + stamp);
		logger.info("sfaLeadID: " + sfaLeadID);
		logger.info("source: " + source);
		logger.info("reason: " + reason);
		
		TBCAM_SFA_LEADSVO vo = (TBCAM_SFA_LEADSVO) dam.findByPKey(TBCAM_SFA_LEADSVO.TABLE_UID, sfaLeadID);
		TBCAM_SFA_LE_REA_LOGVO logVO = new TBCAM_SFA_LE_REA_LOGVO();
		TBCAM_SFA_LE_REA_LOGPK logPK = new TBCAM_SFA_LE_REA_LOGPK();
		try {
			logger.info("vo: " + vo);

			logPK.setLE_REA_DTTM(stamp); // 名單異動時間
			logPK.setSFA_LEAD_ID(sfaLeadID); // 名單代碼
			logVO.setcomp_id(logPK);
			
			logVO.setLE_REA_TYPE(source); // 異動名單來源
			logVO.setLEAD_STATUS(vo.getLEAD_STATUS()); // 名單處理狀態
			logVO.setREV_LOGIN_ID(loginUser); // 主管員編代碼
			logVO.setEMP_ID(vo.getEMP_ID());
			logVO.setAO_CODE(vo.getAO_CODE());
			logVO.setMEMO(reason);

			dam.create(logVO);
		} finally {
			vo = null;
			logPK = null;
			logVO = null;
		}
		
		logger.info("=====建立名單異動記錄檔 END=====");
	}

	private Map<String, Object> dispatchLead(DataAccessManager dam, Map<String, Object> leadsMap, String source, TBCRM_CUST_MASTVO custDtlMap) throws JBranchException {
		
		Map<String, Object> outputVO = new HashMap<String, Object>();
		Map<String, Object> memDtlMap;

		try {
			// TODO : FC/FCH/AFC
			if (((String) leadsMap.get("CHANNEL")).indexOf("FC") > -1 && !StringUtils.equals("AFC", (String) leadsMap.get("CHANNEL"))) { //理專+FCH
				logger.info("***** CHANNEL IS 理專+FCH *****");
				switch ((String) leadsMap.get("LEAD_TYPE")) {
					case "09":
						outputVO = setBranchBoss(outputVO, custDtlMap, (String) leadsMap.get("BRANCH_ID"));
						break;	
					default:
						if (null == leadsMap.get("AO_CODE")) { // 無AO_CODE
							logger.info("***** LEADS NO HAVE AO_CODE *****");
							switch ((String) leadsMap.get("LEAD_TYPE")) {
								case "07":
								case "08":
									outputVO = setAoCode(dam, outputVO, leadsMap, custDtlMap, "design", source);
									
									break;
								default:
									if (StringUtils.isNotBlank(custDtlMap.getAO_CODE())) {
										// 客戶主檔中，有負責該客戶的AO_CODE
										logger.info("*****" + custDtlMap.getCUST_ID() + ": 客戶主檔中，有負責該客戶的AO_CODE，go setAoCode");
										outputVO = setAoCode(dam, outputVO, leadsMap, custDtlMap, "cust", source);
									} else {
										// add mark => 20200508 modify by ocean -> 君榮要求 若該客戶於客戶主檔無AO_CDOE，則分派給客戶主檔中，該客戶的歸屬行-分行主管 待分派
										outputVO = setBranchBoss(outputVO, custDtlMap, (String) leadsMap.get("BRANCH_ID"));
									}
									
									break;
							}
						} else {
							logger.info("***** LEADS HAVE AO_CODE *****");
							outputVO = setAoCode(dam, outputVO, leadsMap, custDtlMap, "design", source);
						}
						break;
				}
			} 
			// TODO : PS
			else if (((String) leadsMap.get("CHANNEL")).indexOf("PS") > -1) { //消金PS
				/*
				 * 房信貸線上留資名單分派角色改由個金AO
				 * 1.客戶有理專者，分派給所屬理專
				 * 2.客戶無理專者，個金AO 有登入系統者，分派給個金AO
				 *            個金AO 無登入系統者，提供給分行主管分派
				 */	
				switch ((String) leadsMap.get("LEAD_TYPE")) {
					case "05":
					case "06":
					case "H1":
					case "H2":
					case "UX":
						if (StringUtils.isNotBlank(custDtlMap.getAO_CODE())) {
							logger.info("*****" + custDtlMap.getCUST_ID() + ": 客戶主檔中，有負責該客戶的AO_CODE，go setAoCode");
							outputVO = setAoCode(dam, outputVO, leadsMap, custDtlMap, "cust", source);
						}
						
						break;
				}
				
				// 若『AO CODE有誤』或『無AO CODE』，依現行邏輯分派
				if (null == outputVO.get("EMP_ID")) {
					logger.info("*****" + custDtlMap.getCUST_ID() + ": 若『AO CODE有誤』或『無AO CODE』，依現行邏輯分派");
					switch ((String) leadsMap.get("LEAD_TYPE")) {
						case "05":
						case "06":
						case "H1":
						case "H2":
						case "UX":
							if (("SYS".equals(source))) {
								logger.info("*****" + custDtlMap.getCUST_ID() + ": 名單若為房信貸，則派予個金AO");
								leadsMap.put("CHANNEL", "PAO");
							}
							
							break;
					}
					
					memDtlMap = setMem(dam, leadsMap, custDtlMap, source);
					outputVO = setOutputVO(outputVO, leadsMap, memDtlMap, custDtlMap);
				}
			}
			// TODO : PAO
			else if (((String) leadsMap.get("CHANNEL")).indexOf("PAO") > -1) {
				/*
				 * 房信貸線上留資名單分派角色改由個金AO
				 * 1.客戶有理專者，分派給所屬理專
				 * 2.客戶無理專者，依管轄行分派，若無法分派，由主管待分派
				 */	
				switch ((String) leadsMap.get("LEAD_TYPE")) {
					case "05":
					case "06":
					case "H1":
					case "H2":
					case "UX":
						if (("SYS".equals(source))) {
							if (StringUtils.isNotBlank(custDtlMap.getAO_CODE())) {
								logger.info("*****" + custDtlMap.getCUST_ID() + ": 客戶有理專者，分派給所屬理專(setAoCode)");
								outputVO = setAoCode(dam, outputVO, leadsMap, custDtlMap, "cust", source);
							}
							
							// 若『AO CODE有誤』或『無AO CODE』，依現行邏輯分派
							if (null == outputVO.get("EMP_ID")) {
								logger.info("*****" + custDtlMap.getCUST_ID() + ": 若『AO CODE有誤』或『無AO CODE』，依現行邏輯分派(名單若為房信貸，則派予個金AO)。");
								memDtlMap = setMem(dam, leadsMap, custDtlMap, source);
								outputVO = setOutputVO(outputVO, leadsMap, memDtlMap, custDtlMap);
							}
						} else {
							logger.info("*****" + custDtlMap.getCUST_ID() + ": 若名單匯入未指定員編僅建議個金AO時，依管轄行分派，若無法分派，由主管待分派。");
							memDtlMap = setMem(dam, leadsMap, custDtlMap, source);
							outputVO = setOutputVO(outputVO, leadsMap, memDtlMap, custDtlMap);
						}
						
						break;
					default:
						logger.info("*****" + custDtlMap.getCUST_ID() + ": 若名單匯入未指定員編僅建議個金AO時，依管轄行分派，若無法分派，由主管待分派");
						memDtlMap = setMem(dam, leadsMap, custDtlMap, source);
						outputVO = setOutputVO(outputVO, leadsMap, memDtlMap, custDtlMap);
						
						break;
				}
			} 
			// TODO : 其他
			else { 
				// 皆依客戶歸屬行分派。
				// 同一分行有多位該種角色人員時(如OP)，隨機分派。
				memDtlMap = setMem(dam, leadsMap, custDtlMap, source);
				outputVO = setOutputVO(outputVO, leadsMap, memDtlMap, custDtlMap);
			}
			
			outputVO.put("AO_CODE", (null == outputVO.get("AO_CODE") ? outputVO.get("EMP_ID") : outputVO.get("AO_CODE")));
		} finally {
			custDtlMap = null;
			memDtlMap = null;
			if (null == outputVO) {
				outputVO = new HashMap<String, Object>();
			}
			outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");
		}
		
		return outputVO;
	}
	
	public Map<String, Object> setOutputVO (Map<String, Object> outputVO, 
											Map<String, Object> leadsMap, 
											Map<String, Object> memDtlMap, 
											TBCRM_CUST_MASTVO custDtlMap) throws JBranchException{

		if (null == memDtlMap) {
			outputVO = setBranchBoss(outputVO, custDtlMap, (String) leadsMap.get("BRANCH_ID"));
		} else {
			outputVO.put("AO_CODE", memDtlMap.get("EMP_ID"));
			outputVO.put("EMP_ID", memDtlMap.get("EMP_ID"));
			outputVO.put("BRANCH_ID", memDtlMap.get("BRANCH_ID"));
			outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");
		}
		
		return outputVO;
	}
	
	// 取得該客戶的詳細資料
	public TBCRM_CUST_MASTVO getCustDtlMap (DataAccessManager dam, Map<String, Object> leadsMap, String interTime) throws JBranchException {
		
		TBCRM_CUST_MASTVO custDtlMap = new TBCRM_CUST_MASTVO();
		
		custDtlMap = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, (String) leadsMap.get("CUST_ID"));
		
		if (null != custDtlMap && StringUtils.isNotBlank(custDtlMap.getUEMP_ID()) && StringUtils.equals(interTime, "FIRST")) {
			logger.info("*****客戶:" + (String) leadsMap.get("CUST_ID") + ": 有UHRM時");
		} else if (StringUtils.isBlank((String) leadsMap.get("BRANCH_ID")) || StringUtils.isBlank((String) leadsMap.get("AO_CODE")) ) {
			logger.info("*****客戶:" + (String) leadsMap.get("CUST_ID") + ": 名單未上傳客戶分行或名單未指定AO");
			switch ((String) leadsMap.get("LEAD_TYPE")) {
				case "05":
				case "06":
				case "07":
				case "08":
				case "09":
				case "H1":
				case "H2":
				case "I1":
				case "I2":
				case "F1":
				case "F2":
					logger.info("*****客戶:" + (String) leadsMap.get("CUST_ID") + ": 名單類別為05/06/08/H1/H2/I1/I2/F1/F2，客戶資料依名單明細為主");
					custDtlMap = null;
					
					break;
			}
		} else {
			logger.info("*****客戶:" + (String) leadsMap.get("CUST_ID") + ": 名單分行有值或有指定AO - " + (String) leadsMap.get("BRANCH_ID") + " / " + (String) leadsMap.get("AO_CODE"));
			custDtlMap = null;
		}
		
		if (null == custDtlMap) {
			custDtlMap = new TBCRM_CUST_MASTVO();
			custDtlMap.setCUST_ID((String) leadsMap.get("CUST_ID"));
			custDtlMap.setBRA_NBR((String) leadsMap.get("BRANCH_ID"));
			custDtlMap.setAO_CODE((String) leadsMap.get("AO_CODE"));
			custDtlMap.setUEMP_ID(null);
		}
		
		return custDtlMap;		
	}
	
	// 給分行主管待分派
	private Map<String, Object> setBranchBoss(Map<String, Object> outputVO, TBCRM_CUST_MASTVO custDtlMap, String branchID) throws JBranchException {

		outputVO.put("BRANCH_ID", (StringUtils.isBlank(branchID) ? custDtlMap.getBRA_NBR() : branchID));
		outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");
		
		return outputVO;
	}
	
	// 指定其他部隊的詳細資料
	private Map<String, Object> setMem(DataAccessManager dam, Map<String, Object> leadsMap, TBCRM_CUST_MASTVO custDtlMap, String source) throws JBranchException {
		
		// 同一分行有多位該種角色人員時(如OP)，隨機分派。
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT AO_CODE, EMP_ID, BRANCH_ID ");
		sb.append("FROM ( ");
		
		switch ((String) leadsMap.get("CHANNEL")) {
			case "PSAO":
				sb.append("SELECT EMP.BRANCH_NBR AS BRANCH_ID, EMP.EMP_ID, NULL AS AO_CODE ");
				sb.append("FROM TBORG_PSAO EMP ");
				sb.append("WHERE EMP.SERVICE_NBR = :branchID ");
				sb.append("AND EMP.ISONTHEJOB = 'Y' ");
				
				queryCondition.setObject("branchID", (StringUtils.isBlank((String) leadsMap.get("BRANCH_ID")) ? custDtlMap.getBRA_NBR() : (String) leadsMap.get("BRANCH_ID")));
				
				break;
			case "PAO":
				sb.append("SELECT EMP.BRANCH_NBR AS BRANCH_ID, EMP.EMP_ID, NULL AS AO_CODE ");
				sb.append("FROM TBORG_PAO EMP ");
				sb.append("WHERE EMP.BRANCH_NBR = :branchID ");
				switch ((String) leadsMap.get("LEAD_TYPE")) {
					// 留資房信貸 分派給個金AO時，需當天有登入的個金AO
					case "05":
					case "06":
					case "H1":
					case "H2":
					case "UX":
						if (StringUtils.equals("SYS", source)) {
							sb.append("AND EXISTS (SELECT TELLERID FROM TBSYSWSONLINESTATUS LOGIN WHERE TRUNC(LOGIN.CREATETIME) = TRUNC(SYSDATE) AND EMP.EMP_ID = LOGIN.TELLERID) ");
						}
					default:
						if (StringUtils.isNotBlank((String) leadsMap.get("AO_CODE")) && ((String) leadsMap.get("AO_CODE")).length() > 3) {
							logger.info("第一通路為個金AO且指定員編(BY AO_CODE)");
						} else if (StringUtils.isNotBlank((String) leadsMap.get("EMP_ID"))) {
							logger.info("第一通路為個金AO且指定員編(BY EMP_ID)");
						} else { // 部隊為個金AO且未指定員編，由主管待分派 改為 由客戶ID歸屬行所屬個金AO分派
							logger.info("第一通路為個金AO且未指定員編，由客戶ID歸屬行所屬個金AO分派");
						}
						
						break;
				}
				
				queryCondition.setObject("branchID", (StringUtils.isBlank((String) leadsMap.get("BRANCH_ID")) ? custDtlMap.getBRA_NBR() : (String) leadsMap.get("BRANCH_ID")));
				
				break;
			case "FA":
			case "IA":
				sb.append("SELECT EMP.SUPT_SALES_TEAM_ID, EMP.BRANCH_NBR AS BRANCH_ID, EMP.EMP_ID, NULL AS AO_CODE ");
				sb.append("FROM TBORG_FAIA EMP ");
				sb.append("WHERE EMP.SUPT_SALES_TEAM_ID = :channel ");
				sb.append("AND EMP.BRANCH_NBR = :branchID ");
				
				queryCondition.setObject("channel", (String) leadsMap.get("CHANNEL"));
				queryCondition.setObject("branchID", (StringUtils.isBlank((String) leadsMap.get("BRANCH_ID")) ? custDtlMap.getBRA_NBR() : (String) leadsMap.get("BRANCH_ID")));
				
				break;
			case "B01": //區督導
			case "P01": //區中心主管
				String roleID = "";
				switch ((String) leadsMap.get("CHANNEL")) {
					case "B01":
						roleID = "A146";
						break;
					case "P01":
						roleID = "A164";
						break;
				}
				
				sb.append("SELECT EMP.EMP_ID, EMP.AO_CODE, EMP.BRANCH_ID ");
				sb.append("FROM ( ");
	
				sb.append("  WITH DEPT AS ( ");
				sb.append("    SELECT REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME ");
				sb.append("    FROM VWORG_DEFN_INFO ");
				sb.append("    WHERE BRANCH_NBR = :branchID ");
				sb.append("  ) ");
						
				sb.append("  SELECT MEM.EMP_ID, NULL AS AO_CODE, (SELECT BRANCH_NBR FROM DEPT) AS BRANCH_ID ");
				sb.append("  FROM TBORG_MEMBER MEM, TBORG_MEMBER_ROLE MR, TBORG_ROLE R ");
				sb.append("  WHERE MEM.EMP_ID = MR.EMP_ID ");
				sb.append("  AND MR.ROLE_ID = R.ROLE_ID ");
				sb.append("  AND MR.IS_PRIMARY_ROLE = 'Y' ");
				sb.append("  AND 1 = ( ");
				sb.append("    CASE :roleID WHEN 'A146' THEN CASE MEM.DEPT_ID WHEN (SELECT BRANCH_AREA_ID FROM DEPT) THEN 1 ELSE 0 END ");
				sb.append("                 WHEN 'A164' THEN CASE MEM.DEPT_ID WHEN (SELECT REGION_CENTER_ID FROM DEPT) THEN 1 ELSE 0 END ");
				sb.append("    END ");
				sb.append("  ) ");
				
				sb.append("  UNION ");
				
				sb.append("  SELECT MEM.EMP_ID, NULL AS AO_CODE, (SELECT BRANCH_NBR FROM DEPT) AS BRANCH_ID ");
				sb.append("  FROM TBORG_MEMBER_PLURALISM MEM, TBORG_MEMBER_ROLE MR, TBORG_ROLE R ");
				sb.append("  WHERE MEM.EMP_ID = MR.EMP_ID ");
				sb.append("  AND MR.ROLE_ID = R.ROLE_ID ");
				sb.append("  AND MR.IS_PRIMARY_ROLE = 'N' ");
				
				// 20180913 add by ocean
				sb.append("  AND (TRUNC(MEM.TERDTE) >= TRUNC(SYSDATE) OR MEM.TERDTE IS NULL) ");
				sb.append("  AND MEM.ACTION <> 'D' ");
				
				sb.append("  AND 1 = ( ");
				sb.append("    CASE :roleID WHEN 'A146' THEN CASE MEM.DEPT_ID WHEN (SELECT BRANCH_AREA_ID FROM DEPT) THEN 1 ELSE 0 END ");
				sb.append("                 WHEN 'A164' THEN CASE MEM.DEPT_ID WHEN (SELECT REGION_CENTER_ID FROM DEPT) THEN 1 ELSE 0 END ");
				sb.append("    END ");
				sb.append("  ) ");
				sb.append(") EMP ");
				sb.append("WHERE 1 = 1 ");
				
				queryCondition.setObject("roleID", roleID);
				queryCondition.setObject("branchID", (StringUtils.isBlank((String) leadsMap.get("BRANCH_ID")) ? custDtlMap.getBRA_NBR() : (String) leadsMap.get("BRANCH_ID")));
				
				logger.info("CAM998.setMem(other channel):" + sb.toString());
				
				break;
			case "BM":
			case "SH":
			case "OPH":
			case "OP":
			case "PS":
			case "AFC":
			case "JRM":
				String tempChannel = "";
				switch ((String) leadsMap.get("CHANNEL")) {
					case "AFC":
						tempChannel = "RA";
						break;
					default:
						tempChannel = (String) leadsMap.get("CHANNEL");
						break;
				}
				
				logger.info("CAM998.setMem(sb start):" + sb.toString());
				sb.append(getMemDtl(null, tempChannel, source, null, (String) leadsMap.get("LEAD_TYPE")));
				logger.info("CAM998.setMem(sb start):" + sb.toString());
				
				switch ((String) leadsMap.get("CHANNEL")) {
					case "AFC":
					case "JRM":
						logger.info("### Channel(" + (String) leadsMap.get("CHANNEL") + "): " + (String) leadsMap.get("CHANNEL") + " / branchID: " + (StringUtils.isBlank((String) leadsMap.get("BRANCH_ID")) ? custDtlMap.getBRA_NBR() : (String) leadsMap.get("BRANCH_ID")) + "###");
						queryCondition.setObject("channel", tempChannel);
						break;
					default:
						logger.info("### Channel(" + (String) leadsMap.get("CHANNEL") + "): " + (String) leadsMap.get("CHANNEL") + " / branchID: " + (StringUtils.isBlank((String) leadsMap.get("BRANCH_ID")) ? custDtlMap.getBRA_NBR() : (String) leadsMap.get("BRANCH_ID")) + "###");
						break;
				}
				
				queryCondition.setObject("branchID", (StringUtils.isBlank((String) leadsMap.get("BRANCH_ID")) ? custDtlMap.getBRA_NBR() : (String) leadsMap.get("BRANCH_ID")));

				break;
		}

		if (StringUtils.isNotBlank((String) leadsMap.get("EMP_ID"))) { //有指定
			sb.append("AND EMP.EMP_ID = :empID");
			logger.info("###第一通路:" + (String) leadsMap.get("CHANNEL") + "-有指定 empID: " + (String) leadsMap.get("EMP_ID") + "###");

			sb.append(") ");
			queryCondition.setObject("empID", (String) leadsMap.get("EMP_ID"));
		} else if (StringUtils.isNotBlank((String) leadsMap.get("AO_CODE")) && ((String) leadsMap.get("AO_CODE")).length() > 3) {
			sb.append("AND EMP.EMP_ID = :empID");
			logger.info("###第一通路:" + (String) leadsMap.get("CHANNEL") + "-有指定 empID: " + (String) leadsMap.get("AO_CODE") + "###");

			sb.append(") ");
			queryCondition.setObject("empID", (String) leadsMap.get("AO_CODE"));
		} else {
			sb.append("ORDER BY DBMS_RANDOM.VALUE");
			sb.append(") "); //隨機分派
			sb.append("WHERE ROWNUM = 1 ");
		}
		
		queryCondition.setQueryString(sb.toString());
		logger.info("CAM998.setMem(final): 第一通路=" + (String) leadsMap.get("CHANNEL") + "/SQL=" + sb.toString());
		
		List<Map<String, Object>> memDtl = dam.exeQuery(queryCondition);
		
		sb.setLength(0);

		return (memDtl.size() > 0) ? memDtl.get(0) : null;
	}
	
	private Map<String, Object> setAoCode(DataAccessManager dam,  
										  Map<String, Object> outputVO, 
										  Map<String, Object> leadsMap, 
										  TBCRM_CUST_MASTVO custDtlMap, 
										  String aoSource, 
										  String source) throws JBranchException {
		
		//03必要通知名單 、02事件式行銷→有理專者，以每位理專的可分配名單上限做依據，未超過上限則派給所屬理專，超過上限則派給主管。活動期間內都可以分派
		//01:行銷活動名單→有理專者，以每位理專的可分配名單上限做依據，未超過上限則派給所屬理專，超過上限則不分派(不分派之名單須每日批次回送給Unica-此批次參考BTCAM028-(批次)回傳UNICA)。
		//04:整批客戶參考資訊→原則上全部收入理專系統
		
		// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
//		CAM997 cam997 = null;
		
		// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
//		List<Map<String, Object>> dateList = new ArrayList<Map<String,Object>>();
		Map<String, Object> aoDtlMap = new HashMap<String, Object>();
		
		// === 有指定AO或該客戶有AO時 START ===
		List<Map<String, Object>> aoDataDtl = new ArrayList<Map<String, Object>>(); 
		String aoCode = (StringUtils.equals("design", aoSource) ? (StringUtils.isNotBlank((String) leadsMap.get("AO_CODE")) ? (String) leadsMap.get("AO_CODE") : custDtlMap.getAO_CODE()) : custDtlMap.getAO_CODE());

		if (StringUtils.isNotBlank(aoCode)) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			queryCondition.setQueryString(getMemDtl(aoCode, null, source, null, (String) leadsMap.get("LEAD_TYPE")).toString());
			queryCondition.setObject("aoCode", aoCode);
			
			aoDataDtl = dam.exeQuery(queryCondition);
		}
		// === 有指定AO或該客戶有AO時 END ===
			
		switch ((String) leadsMap.get("LEAD_TYPE")) {
			case "04":
			case "05":
			case "06":
			case "H1":
			case "H2":
			case "UX":
				if (aoDataDtl.size() == 0) {
					logger.error("該AO_CODE有誤: " + aoCode);
				} else {
					aoDtlMap = aoDataDtl.get(0);
				}
				
				break;
			case "07":
			case "08":
			case "I1":
			case "I2":
			case "F1":
			case "F2":
				if (StringUtils.isBlank(aoCode)) { // // 無指定AO 且 為線上留資07、08時
					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					
					// 留資-保險(先派給FC2、FC3，若無，則派給FC4、FC5、若無，則派給FC1 → 2017-01-20 ADD
					List<String> roleList = null;
					for (int i = 0; i < 4; i++) {
						if (aoDataDtl.size() == 0 && i == 3) {
							logger.error("無登入的AO時，分派給分行主管");// 無登入的AO時，分派給分行主管
						} else if (aoDataDtl.size() == 0 && i < 3) {
							StringBuffer sb = new StringBuffer();

							sb.append("SELECT AO_CODE, EMP_ID, BRANCH_ID ");
							sb.append("FROM ( ");
							switch (i) {
								case 0:
									roleList = new ArrayList<String>();
									roleList.add("ABRH");
									roleList.add("A148");
									break;
								case 1:
									roleList = new ArrayList<String>();
									roleList.add("ABRE");
									roleList.add("A8A7");
									break;
								case 2:
									roleList = new ArrayList<String>();
									roleList.add("A145");
									break;
							}
							sb.append(getMemDtl(null, null, source, roleList, (String) leadsMap.get("LEAD_TYPE")));
							sb.append("ORDER BY DBMS_RANDOM.VALUE");
							sb.append(") "); //隨機分派
							sb.append("WHERE ROWNUM = 1 ");
							
							queryCondition.setQueryString(sb.toString());
							
							queryCondition.setObject("branchID", leadsMap.get("BRANCH_ID"));
							queryCondition.setObject("roleList", roleList);
							
							aoDataDtl = dam.exeQuery(queryCondition);
						}
					}
					
					if (aoDataDtl.size() > 0) { 
						aoDtlMap = aoDataDtl.get(0);
					}
				} else { // 有指定AO且為線上留資07、08時
					if (aoDataDtl.size() == 0) {
						logger.error("該AO_CODE有誤: " + aoCode);
					} else {
						aoDtlMap = aoDataDtl.get(0);
					}
				}
				break;
			default:
				switch ((String) leadsMap.get("CHANNEL")) {
					case "FCH":
						break;
					default:
						if (aoDataDtl.size() == 0) {
							logger.error("該AO_CODE有誤: " + aoCode);
						} else if (null == aoDataDtl.get(0).get("UPPER_LIMIT")) {
							logger.error("該員工非理專: EMP_ID(" + aoDataDtl.get(0).get("EMP_ID") + ") / AO_CODE(" + aoDataDtl.get(0).get("AO_CODE") + ") / ROLE_ID(" + aoDataDtl.get(0).get("ROLE_ID") + ") / ROLE_NAME(" + aoDataDtl.get(0).get("ROLE_NAME") + ")");
						} else if (aoDataDtl.size() > 0 && null != aoDataDtl.get(0).get("UPPER_LIMIT")) {
							// #0000114 : 20200131 modify by ocean : WMS-CR-20200117-01_名單優化調整需求變更申請單
//							cam997 = new CAM997();
//							try {
//								dateList = cam997.getDateList(dam,
//															  custDtlMap.getCUST_ID(),
//															  aoCode, 
//															  SDFYYYYMMDD.format((Timestamp) leadsMap.get("START_DATE")), 
//															  SDFYYYYMMDD.format((Timestamp) leadsMap.get("END_DATE")),
//															  (String) aoDataDtl.get(0).get("UPPER_LIMIT"));
//							}
//							finally{
//								cam997 = null;
//							}
						}
						
						break;
				}
				
				if (aoDataDtl.size() == 0) {
					logger.error("該AO_CODE有誤: " + aoCode);
				} else {
					aoDtlMap = aoDataDtl.get(0);
				}
				break;
				
		}

		logger.info("aoDtlMap: " + aoDtlMap + " / aoDtlMap.size() :" + aoDtlMap.size());
		logger.info("Cust BRA_NBR: " + custDtlMap.getBRA_NBR() + " / isNotBlank: " + StringUtils.isNotBlank(custDtlMap.getBRA_NBR()));
		
		if ((aoDtlMap == null || aoDtlMap.size() == 0 )&& StringUtils.isBlank(custDtlMap.getBRA_NBR())) {
			logger.error("*******************" + custDtlMap.getCUST_ID() + "無歸屬行。");
			outputVO.put("DISPATCH", "N");
		} else if ((aoDtlMap == null || aoDtlMap.size() == 0) && StringUtils.isNotBlank(custDtlMap.getBRA_NBR())) {
			logger.error("*******************無理專資訊但可壓客戶歸屬行。");
			outputVO.put("BRANCH_ID", (String) custDtlMap.getBRA_NBR());
			outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");
		} else {
			switch ((String) leadsMap.get("LEAD_TYPE")) {
				case "02": // 事件式行銷
				case "03": // 必要通知名單
				case "10": // 行銷名單
				case "99": // 內稽內控名單
					switch ((String) leadsMap.get("CHANNEL")) {
						case "FCH":
							logger.error("*******************【事件式行銷、必要通知名單、行銷名單、內稽內控名單】已指定，分派中");
							outputVO.put("AO_CODE", (String) aoDtlMap.get("AO_CODE"));
							outputVO.put("EMP_ID", (String) aoDtlMap.get("EMP_ID"));
							outputVO.put("BRANCH_ID", (String) aoDtlMap.get("BRANCH_ID"));
							outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");
							break;
						default:
							logger.error("*******************【事件式行銷、必要通知名單、行銷名單、內稽內控名單】分派中");
							outputVO.put("AO_CODE", (String) aoDtlMap.get("AO_CODE"));
							outputVO.put("EMP_ID", (String) aoDtlMap.get("EMP_ID"));
							outputVO.put("BRANCH_ID", (String) aoDtlMap.get("BRANCH_ID"));
							outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");

							break;
					}
					break;
				case "04": // 參考資訊
					logger.error("*******************【參考資訊】已指定，分派中");
					outputVO.put("AO_CODE", (String) aoDtlMap.get("AO_CODE"));
					outputVO.put("EMP_ID", (String) aoDtlMap.get("EMP_ID"));
					outputVO.put("BRANCH_ID", (String) aoDtlMap.get("BRANCH_ID"));
					outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");
					
					break;
				case "01": // 行銷名單(UNICA)
					logger.error("*******************【行銷活動名單】");
					outputVO.put("AO_CODE", (String) aoDtlMap.get("AO_CODE"));
					outputVO.put("EMP_ID", (String) aoDtlMap.get("EMP_ID"));
					outputVO.put("BRANCH_ID", (String) aoDtlMap.get("BRANCH_ID"));
					outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");
					
					break;
				case "05": // 留資名單_房貸
				case "06": // 留資名單_信貸
				case "07": // 留資名單_網銀登入後保險
				case "08": // 留資名單_網銀登入前保險
				case "H1": // 留資名單_客服自來房貸
				case "H2": // 留資名單_客服共銷房貸
				case "I1": // 留資名單_客服自來保險
				case "I2": // 留資名單_客服共銷保險
				case "F1": // 留資名單_客服自來基金
				case "F2": // 留資名單_客服共銷基金
				case "UX": // 留資名單_電銷匯入
					logger.error("*******************【05/06/07/08/H1/H2/I1/I2/F1/F2/UX】");
					if (aoDataDtl.size() == 0) { 
						outputVO = setBranchBoss(outputVO, custDtlMap, (String) leadsMap.get("BRANCH_ID"));
					} else {
						outputVO.put("AO_CODE", (String) aoDtlMap.get("AO_CODE"));
						outputVO.put("EMP_ID", (String) aoDtlMap.get("EMP_ID"));
						outputVO.put("BRANCH_ID", (String) aoDtlMap.get("BRANCH_ID"));
						outputVO.put("DISPATCH", null == outputVO.get("BRANCH_ID") ? "N" : "Y");
					}
					
					break;
				default :
					logger.error("*******************【WHAT??】" + leadsMap.get("LEAD_TYPE"));
					break;
			}
		}
		
		logger.info("=====Final output start=====");
		logger.info("outputVO: " + outputVO);
		logger.info("=====Final output end=====");
		return outputVO;
	}
	
	// 取得MEMBER基本資料
	private StringBuffer getMemDtl(String aoCode, String channel, String source, List<String> aoTempList, String leadType) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("WITH TBSYS AS ( ");
		sb.append("  SELECT PARAM_CODE AS ROLE_NAME, PARAM_NAME ");
		sb.append("  FROM TBSYSPARAMETER ");
		sb.append("  WHERE PARAM_TYPE = 'CAM.MAX_CONTACT' ");
		sb.append("), EMP AS ( ");
		sb.append("  SELECT MEM.EMP_ID, ");
		sb.append("         AO.AO_CODE, ");
		sb.append("         AO.TYPE AS AO_TYPE, ");
		sb.append("         MEM.EMP_NAME, ");
		sb.append("         MR.ROLE_ID, ");
		sb.append("         R.ROLE_NAME, ");
		sb.append("         CASE WHEN UB.EMP_ID IS NOT NULL THEN UB.BRANCH_NBR ELSE MEM.DEPT_ID END AS BRANCH_ID ");
		sb.append("  FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE R, TBORG_DEFN DEFN, TBORG_MEMBER MEM ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON MEM.EMP_ID = AO.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_UHRM_BRH UB ON MEM.EMP_ID = UB.EMP_ID ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND MEM.JOB_TITLE_NAME = R.JOB_TITLE_NAME ");
		sb.append("  AND MR.ROLE_ID = R.ROLE_ID ");
		sb.append("  AND MR.EMP_ID = MEM.EMP_ID ");
		sb.append("  AND MEM.DEPT_ID = DEFN.DEPT_ID ");
		
		if (StringUtils.isNotBlank(aoCode)) {
			sb.append("  AND R.IS_AO = 'Y' ");
		}
		
		sb.append("  AND MR.IS_PRIMARY_ROLE = 'Y' ");
		sb.append("  AND ( ");
		sb.append("    CASE WHEN UB.EMP_ID IS NOT NULL THEN 'Y' ");
		sb.append("         WHEN DEFN.ORG_TYPE = '50' THEN 'Y' ");
		sb.append("    ELSE 'N' END ");
		sb.append("  ) = 'Y' ");
		sb.append("  AND MEM.SERVICE_FLAG = 'A' ");
		sb.append("  AND MEM.CHANGE_FLAG IN ('A', 'M', 'P') ");
				  
		sb.append("  UNION ");
		  
		sb.append("  SELECT MEM.EMP_ID, ");
		sb.append("         AO.AO_CODE, ");
		sb.append("         AO.TYPE AS AO_TYPE, ");
		sb.append("         MEM.EMP_NAME, ");
		sb.append("         MR.ROLE_ID, ");
		sb.append("         R.ROLE_NAME, ");
		sb.append("         CASE WHEN UB.EMP_ID IS NOT NULL THEN UB.BRANCH_NBR ELSE PLUR.DEPT_ID END AS BRANCH_ID ");
		sb.append("  FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE R, TBORG_DEFN DEFN, TBORG_MEMBER MEM, TBORG_MEMBER_PLURALISM PLUR ");
		sb.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON PLUR.EMP_ID = AO.EMP_ID ");
		sb.append("  LEFT JOIN TBORG_UHRM_BRH UB ON PLUR.EMP_ID = UB.EMP_ID ");
		sb.append("  WHERE 1 = 1 ");
		sb.append("  AND PLUR.JOB_TITLE_NAME = R.JOB_TITLE_NAME ");
		sb.append("  AND MR.ROLE_ID = R.ROLE_ID ");
		sb.append("  AND MR.EMP_ID = PLUR.EMP_ID ");
		sb.append("  AND PLUR.DEPT_ID = DEFN.DEPT_ID ");
		sb.append("  AND MEM.EMP_ID = PLUR.EMP_ID ");
		
		if (StringUtils.isNotBlank(aoCode)) {
			sb.append("  AND R.IS_AO = 'Y' ");
		}
		
		sb.append("  AND MR.IS_PRIMARY_ROLE = 'N' ");
		sb.append("  AND ( ");
		sb.append("    CASE WHEN UB.EMP_ID IS NOT NULL THEN 'Y' ");
		sb.append("         WHEN DEFN.ORG_TYPE = '50' THEN 'Y' ");
		sb.append("    ELSE 'N' END ");
		sb.append("  ) = 'Y' ");
		sb.append("  AND (TRUNC(PLUR.TERDTE) >= TRUNC(SYSDATE) OR PLUR.TERDTE IS NULL) ");
		sb.append("  AND PLUR.ACTION <> 'D' ");
		sb.append(") ");
		
		sb.append("SELECT EMP.EMP_ID, EMP.AO_CODE, EMP.EMP_NAME, EMP.ROLE_ID, EMP.ROLE_NAME, EMP.BRANCH_ID, TBSYS.PARAM_NAME AS UPPER_LIMIT ");
		sb.append("FROM EMP ");
		sb.append("LEFT JOIN TBSYS ON TBSYS.ROLE_NAME = EMP.ROLE_NAME ");
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotBlank(aoCode)) {
			sb.append("AND EMP.AO_CODE = :aoCode ");
		} else if (null != aoTempList && aoTempList.size() > 0) {
			sb.append("AND EMP.BRANCH_ID = :branchID ");
			sb.append("AND EMP.ROLE_ID IN (:roleList) ");
			sb.append("AND EMP.AO_TYPE = '1' ");
		} else if (StringUtils.isNotBlank(channel)) {
			switch (channel) {
				case "BM":
					sb.append("AND EMP.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('011'))");
					break;
				case "SH":
					sb.append("AND EMP.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('009'))");
					break;
				case "OPH":
					sb.append("AND EMP.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('006'))");
					break;
				case "OP":
					sb.append("AND EMP.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('005', '007', '008'))");
					break;
				case "PS":
					sb.append("AND EMP.ROLE_ID IN (SELECT ROLEID FROM TBSYSSECUROLPRIASS WHERE PRIVILEGEID IN ('004'))");
					break;
				default:
					sb.append("AND EMP.ROLE_ID = :channel ");
					break;
			}
			
			sb.append("AND EMP.BRANCH_ID = :branchID ");
		}
		
		switch (leadType) {
			case "05":
			case "06":
			case "H1":
			case "H2":
				//分派給PS時，需當天有登入的PS
				if ((StringUtils.equals("PS", channel) || (null != aoTempList && aoTempList.size() > 0)) && StringUtils.equals("SYS", source)) {
					sb.append("AND EMP.EMP_ID IN (SELECT TELLERID FROM TBSYSWSONLINESTATUS WHERE CREATETIME BETWEEN TRUNC(SYSDATE) AND SYSDATE GROUP BY TELLERID) ");
				}
				break;
		}

		logger.info("CAM998.getMemDtl:" + sb.toString());
		
		return sb;
	}
	
	private String getSeqNum(DataAccessManager dam) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCAM_SFA_LEADS_RES.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
}