package com.systex.jbranch.app.server.fps.kycoperation;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_COOLING_PERIODVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_MVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_M_HISVO;
import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("kyccoolingrelease")
@Scope("prototype")
public class KycCoolingRelease extends FubonWmsBizLogic {

	private static Logger logger = LoggerFactory.getLogger(KycCoolingRelease.class);
	private DataAccessManager dam = null;

	/**
	 * 解除冷靜期，更新C值資料
	 * 
	 * @param seq
	 *            : 客戶風險評估問卷主鍵
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public void coolingReleaseUpdate(String seq) throws JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		// 先取得欲解除冷靜期資料
		queryCondition.setQueryString("SELECT * FROM TBKYC_COOLING_PERIOD WHERE SEQ = :seq ");
		queryCondition.setObject("seq", seq);
		List<Map<String, Object>> listCooling = dam.exeQuery(queryCondition);

		// 更新資料
		for (Map<String, Object> map : listCooling) {
			String riskBef = ObjectUtils.toString(map.get("CUST_RISK_BEF"));
			String riskAfr = ObjectUtils.toString(map.get("CUST_RISK_AFR"));
			String custId = ObjectUtils.toString(map.get("CUST_ID"));

			// 更新KYC主檔
			TBKYC_INVESTOREXAM_MVO timVO = (TBKYC_INVESTOREXAM_MVO) dam.findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID, seq);
			if (timVO != null) {
				timVO.setCUST_RISK_BEF(riskBef);
				timVO.setCUST_RISK_AFR(riskAfr);
				dam.update(timVO);
			}

			// 更新KYC主檔歷史資料
			TBKYC_INVESTOREXAM_M_HISVO timHisVO = (TBKYC_INVESTOREXAM_M_HISVO) dam.findByPKey(TBKYC_INVESTOREXAM_M_HISVO.TABLE_UID, seq);
			if (timHisVO != null) {
				timHisVO.setCUST_RISK_BEF(riskBef);
				timHisVO.setCUST_RISK_AFR(riskAfr);
				dam.update(timHisVO);
			}

			// 更新KYC當下最新風險屬性資料
			// 原KYC311沒有用TABLEVO，UPDATE用VO會出現錯誤
			dam.exeUpdate(genDefaultQueryConditionIF().setQueryString("UPDATE TBKYC_INVESTOREXAM_NOW SET CUST_RISK_ATR = :riskAfr, MODIFIER = 'BTKYC006', LASTUPDATE = SYSDATE WHERE CUST_ID = :custId ").setObject("riskAfr", riskAfr).setObject("custId", custId));
			// TBKYC_INVESTOREXAM_NOWVO nowVO = (TBKYC_INVESTOREXAM_NOWVO)
			// dam.findByPKey(TBKYC_INVESTOREXAM_NOWVO.TABLE_UID, custId);
			// if(nowVO != null) {
			// nowVO.setCUST_RISK_ATR(riskAfr);
			// dam.update(nowVO);
			// }

			// 更新客戶主檔資料
			TBCRM_CUST_MASTVO custVO = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, custId);
			if (custVO != null) {
				custVO.setCUST_RISK_ATR(riskAfr);
				dam.update(custVO);
			}

			// 將此筆資料標示為"冷靜期已解除"
			TBKYC_COOLING_PERIODVO tcpVO = (TBKYC_COOLING_PERIODVO) dam.findByPKey(TBKYC_COOLING_PERIODVO.TABLE_UID, seq);
			if (tcpVO != null) {
				tcpVO.setSTATUS("R"); // Released
				dam.update(tcpVO);
			}
		}
	}

	/**
	 * 解除冷靜期，更新C值資料給主機
	 * 
	 * @param seq
	 *            : 客戶風險評估問卷主鍵
	 * @throws Exception
	 */
	public void coolingReleaseSend390(String seq) throws Exception {
		dam = getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//先取得欲解除冷靜期並上送主機資料
		queryCondition.setQueryString("SELECT C.CUST_ID, C.CUST_RISK_AFR, TO_CHAR(C.CREATE_DATE, 'DDMMYYYY') AS CREATE_DATE, "
				+ " TO_CHAR(H.EXPIRY_DATE, 'DDMMYYYY') AS EXPIRY_DATE, H.INVEST_BRANCH_NBR "
				+ " FROM TBKYC_COOLING_PERIOD C "
				+ " INNER JOIN TBKYC_INVESTOREXAM_M_HIS H ON H.SEQ = C.SEQ "
				+ " WHERE C.SEQ = :seq ");
		queryCondition.setObject("seq", seq);		
		List<Map<String, Object>> listCooling = dam.exeQuery(queryCondition);
			
		//逐筆更新資料
		for(Map<String, Object> map : listCooling) {
			String custId = ObjectUtils.toString(map.get("CUST_ID"));
			String riskAfr = ObjectUtils.toString(map.get("CUST_RISK_AFR"));
			String testDate = ObjectUtils.toString(map.get("CREATE_DATE"));
			String expiryDate = ObjectUtils.toString(map.get("EXPIRY_DATE"));
			String branch = ObjectUtils.toString(map.get("INVEST_BRANCH_NBR"));
			
			//(11)電文上送(上送C值電文修改資料)
			KYC311InputVO kyc311InputVO = new KYC311InputVO();

			kyc311InputVO.setCUST_ID(custId);
			kyc311InputVO.setCUST_RISK_AFR(riskAfr);
			kyc311InputVO.setKYC_TEST_DATE(testDate);
			kyc311InputVO.setEXPIRY_DATE(expiryDate);
			kyc311InputVO.setBRANCH(branch);  
				
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
//			logger.info(kyc311InputVO.getCUST_ID() + "冷靜期解除:[" + seq + "]上送390CUST_DATA：" + kyc311InputVO.toString());
			
			try{
				//執行TP032675更新		
				sot701.kycUpdateCValue(kyc311InputVO);
				// 更新主管已覆核
				sot701.kycUpdateSupervisorCheck(kyc311InputVO);
				// 更新基本資料先註解掉
//				sot701.kycUpdateBasic(kyc311InputVO);
//				logger.info(kyc311InputVO.getCUST_ID() + "冷靜期解除:[" + seq + "]上送390CUST_DATA：回傳");
						
				//將此筆資料上送主機狀態更新
				TBKYC_COOLING_PERIODVO tcpVO = (TBKYC_COOLING_PERIODVO) dam.findByPKey(TBKYC_COOLING_PERIODVO.TABLE_UID, seq);
				if(tcpVO != null) {
					tcpVO.setSENT_390_STATUS("Y");	//資料已傳送390主機
					dam.update(tcpVO);
				}
			} catch (Exception e) {
				logger.info("冷靜期解除:[" + seq + "]上送主機電文TP032675有誤");
				throw new Exception("冷靜期解除:上送主機電文TP032675有誤。");
			}
		}
	}

	private static void logInfo(String firstTitle, String title, Object msg) {
		logger.info("[" + firstTitle + "][" + title + "]" + ObjectUtils.toString(msg));
	}
}