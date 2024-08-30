package com.systex.jbranch.app.server.fps.sot1640;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DYNAVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110;
import com.systex.jbranch.app.server.fps.prd110.PRD110InputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110OutputVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703Dyna;
import com.systex.jbranch.app.server.fps.sot703.SOT703InputVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sot1640
 * 動態鎖利轉換
 */
@Component("sot1640")
@Scope("request")
public class SOT1640 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT1640.class);
	
	/**
	 * 查詢購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
//	public void query(Object body, IPrimitiveMap header) throws JBranchException {
//		SOT1640InputVO inputVO = (SOT1640InputVO) body;
//		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
//		inputVO = (SOT1640InputVO) sot110.cleanAcctCcy(inputVO);
//		SOT1640OutputVO outputVO = new SOT1640OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = null;
//		StringBuffer sb = null;
//
//		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sb = new StringBuffer();
//		sb.append("SELECT A.*, F.FUS07 ");
//		sb.append(" FROM TBSOT_NF_TRANSFER_DYNA A ");
//		sb.append(" LEFT JOIN TBPRD_FUND F ON A.PROD_ID = F.PRD_ID ");
//		sb.append(" WHERE A.TRADE_SEQ = :tradeSEQ ");
//		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
//		queryCondition.setQueryString(sb.toString());
//
//		List<Map<String, Object>> carList = dam.exeQuery(queryCondition);
//		XmlInfo xmlInfo = new XmlInfo();
//		Map<String, String> curMap = xmlInfo.doGetVariable("SOT.FUND_DECIMAL_POINT", FormatHelper.FORMAT_3);//幣別小數位控制
//
//		for (Map<String, Object> dataMap : carList) {
//			String trustCurr = dataMap.get("TRUST_CURR").toString();
//			dataMap.put("CUR_NUM", curMap.get(trustCurr).toString());
//			//2017-09-11 取基金-小數點位數
//			String prodId = dataMap.get("PROD_ID").toString();
//		}
//		outputVO.setCarList(carList);
//
//		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sb = new StringBuffer();
//		sb.append("SELECT * ");
//		sb.append("FROM TBSOT_TRADE_MAIN ");
//		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
//		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
//		queryCondition.setQueryString(sb.toString());
//		outputVO.setMainList(dam.exeQuery(queryCondition));
//
//		this.sendRtnObject(outputVO);
//	}
//	
//	// 取得可轉換母基金商品資訊
//	public void getInProdDTL(Object body, IPrimitiveMap header) throws Exception {
//		SOT1640InputVO inputVO = (SOT1640InputVO) body;
//		SOT1640OutputVO outputVO = new SOT1640OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//
//		boolean isFitness = StringUtils.equals("N", (String) new XmlInfo().getVariable("SOT.FITNESS_YN", "NF", "F3")) ? false : true;
//
//		outputVO.setWarningMsg("");
//		outputVO.setErrorMsg("");
//
//		boolean isFitnessOK = false;
//		if (isFitness) {
//			// 1.適配，由商品查詢取得，邏輯需一致
//			PRD110OutputVO prdOutputVO = new PRD110OutputVO();
//			PRD110InputVO prdInputVO = new PRD110InputVO();
//			prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
//			prdInputVO.setType("4");
//			prdInputVO.setFund_id(inputVO.getInProdId());
//			prdInputVO.setInProdIdM(inputVO.getInProdId());
//			prdInputVO.setTrustTS("S");
//			//動態鎖利
//			prdInputVO.setFromSOTProdYN("Y");
//			prdInputVO.setDynamicType("M");
//			//可轉換母基金須為原庫存母基金同系列商品
//			prdInputVO.setSameSerialYN("Y"); 
//			prdInputVO.setSameSerialProdId(inputVO.getProdId());
//			prdInputVO.setDynamicProdCurrM(inputVO.getDynamicProdCurrM());
//			PRD110 prd110 = (PRD110) PlatformContext.getBean("prd110");
//			prdOutputVO = prd110.inquire(prdInputVO);
//
//			if (CollectionUtils.isNotEmpty(prdOutputVO.getResultList())) {
//				String warningMsg = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("warningMsg");
//				String errId = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("errorID");
//
//				if (StringUtils.isBlank(errId)) {
//					isFitnessOK = true;
//				}
//
//				outputVO.setWarningMsg(warningMsg);
//				outputVO.setErrorMsg(errId);
//			}
//		} else {
//			logger.debug("SOT.FITNESS_YN 不進行適配 ");
//			isFitnessOK = true;
//		}
//
//		// 2.查詢可轉換母基金商品主檔
//		if (isFitnessOK) { // 適配條件符合
//			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//			StringBuffer sb = new StringBuffer();
//			sb.append("SELECT f.*, i.FUS20, i.FUS40 ");
//			sb.append("FROM TBPRD_FUND f ");
//			sb.append("LEFT JOIN TBPRD_FUNDINFO i on (f.PRD_ID=i.PRD_ID) ");
//			sb.append("WHERE f.PRD_ID = :prodID ");
//			queryCondition.setObject("prodID", inputVO.getInProdId());
//			queryCondition.setQueryString(sb.toString());
//	
//			outputVO.setProdDTL(dam.exeQuery(queryCondition));
//		}
//
//		this.sendRtnObject(outputVO);
//	}
//	
//	// 取得商品資訊
//	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {
//		SOT1640InputVO inputVO = (SOT1640InputVO) body;
//		SOT1640OutputVO outputVO = new SOT1640OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//
//		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT f.*, i.FUS20, i.FUS40 ");
//		sb.append("FROM TBPRD_FUND f ");
//		sb.append("LEFT JOIN TBPRD_FUNDINFO i on (f.PRD_ID=i.PRD_ID) ");
//		sb.append("WHERE f.PRD_ID = :prodID ");
//		queryCondition.setObject("prodID", inputVO.getProdId());
//		queryCondition.setQueryString(sb.toString());
//
//		outputVO.setProdDTL(dam.exeQuery(queryCondition));
//
//		this.sendRtnObject(outputVO);
//	}
//		
//	/**
//	 * 鍵機頁面暫存
//	 * @param body
//	 * @param header
//	 * @throws JBranchException
//	 * @throws Exception
//	 */
//	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {
//
//		initUUID();
//		
//		XmlInfo xmlInfo = new XmlInfo();
//		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
//
//		SOT1640InputVO inputVO = (SOT1640InputVO) body;
//		SOT1640OutputVO outputVO = new SOT1640OutputVO();
//		
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//
//		//高資產客戶投組風險權值檢核
//		//動態鎖利下單不可越級適配
////		String errorMsg = getHnwcRiskValue(inputVO);
////		if(StringUtils.isNotBlank(errorMsg)) {
////			throw new APException(errorMsg);
////		}
//				
//		// 1.寫入主檔/明細
//		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
//		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
//
//		if (null == vo) {
//			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
//
//			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
//			mainVO.setPROD_TYPE(inputVO.getProdType()); //8：動態鎖利
//			mainVO.setTRADE_TYPE("3");//3：轉換
//			mainVO.setCUST_ID(inputVO.getCustID());
//			mainVO.setCUST_NAME(inputVO.getCustName());
//			mainVO.setKYC_LV(inputVO.getKycLV());
//			if (inputVO.getKycDueDate() != null) mainVO.setKYC_DUE_DATE(new Timestamp(inputVO.getKycDueDate().getTime()));
//			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
//			if (inputVO.getPiDueDate() != null)	mainVO.setPI_DUE_DATE(new Timestamp(inputVO.getPiDueDate().getTime()));
//			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
//			mainVO.setIS_OBU(inputVO.getIsOBU());
//			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
//			mainVO.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
//			if (inputVO.getBargainDueDate() != null) mainVO.setBARGAIN_DUE_DATE(new Timestamp(inputVO.getBargainDueDate().getTime()));
//			mainVO.setTRADE_STATUS("1"); //1:暫存
//			mainVO.setIS_BARGAIN_NEEDED("N");
//			
//			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
//			SOT712InputVO inputVO_712 = new SOT712InputVO();
//			inputVO_712.setCustID(inputVO.getCustID());
//			inputVO_712.setProdID(null);
//			inputVO_712.setProdType("NF");
//			inputVO_712.setProfInvestorYN(StringUtils.isBlank(inputVO.getProfInvestorYN()) ? "N" : inputVO.getProfInvestorYN()); //是否為專業投資人
//			inputVO_712.setIsFirstTrade(inputVO.getIsFirstTrade()); //是否首購   
//			inputVO_712.setCustRemarks(inputVO.getCustRemarks());
//			inputVO_712.setCustProRemark(inputVO.getPiRemark());
//			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); //IS_REC_NEEDED是否需要錄音 (非常規交易)
//			
//			mainVO.setREC_SEQ(null);
//			mainVO.setSEND_DATE(null);
//			mainVO.setPROF_INVESTOR_TYPE(inputVO.getCustProType()); //專業投資人類別1：大專投  2：小專投
//			mainVO.setHNWC_YN(inputVO.getHnwcYN());
//			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
//			
//			/*
//			 * About CR list ↓
//			 * 	WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5
//			 * 	WMS-CR-20190710-05_個人高端客群處業管系統_行銷模組調整申請_P1
//			 * 
//			 * Version ↓
//			 * 	2017-01-11 modify by mimi for SOT7XX
//			 * 	2019-07-24 modify by ocean
//			 *
//			 */
//			if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
//				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//				StringBuffer sb = new StringBuffer();
//				
//				sb.append("SELECT BRANCH_NBR ");
//				sb.append("FROM TBORG_UHRM_BRH ");
//				sb.append("WHERE EMP_ID = :loginID ");
//
//				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
//				queryCondition.setQueryString(sb.toString());
//
//				List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);
//				
//				if (loginBreach.size() > 0) {
//					mainVO.setBRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
//				} else {
//					throw new APException("人員無有效分行"); //顯示錯誤訊息
//				}
//			} else {
//				mainVO.setBRANCH_NBR(String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH)));
//			}
//			
//			//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
//			mainVO.setTRUST_TRADE_TYPE("S");  //M:金錢信託  S:特金交易			
//			mainVO.setIS_WEB("N");//是否為快速申購(Y/N)\
//			mainVO.setFLAG_NUMBER(inputVO.getFlagNumber());			//90天內是否有貸款紀錄 Y/N
//			
//			dam.create(mainVO);
//		} else {
//			vo.setTRADE_STATUS("1"); //暫存
//
//			dam.update(vo);
//		}
//
//		//贖回明細資料
//		TBSOT_NF_TRANSFER_DYNAPK dPK = new TBSOT_NF_TRANSFER_DYNAPK();
//		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
//		dPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1
//		TBSOT_NF_TRANSFER_DYNAVO dVO = new TBSOT_NF_TRANSFER_DYNAVO();
//		dVO.setcomp_id(dPK);
//		dVO = (TBSOT_NF_TRANSFER_DYNAVO) dam.findByPKey(TBSOT_NF_TRANSFER_DYNAVO.TABLE_UID, dVO.getcomp_id());
//
//		if (null != dVO) {
//			dam.delete(dVO);
//		}
//
//		TBSOT_NF_TRANSFER_DYNAPK dtlPK = new TBSOT_NF_TRANSFER_DYNAPK();
//		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
//		dtlPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1
//
//		TBSOT_NF_TRANSFER_DYNAVO dtlVO = new TBSOT_NF_TRANSFER_DYNAVO();
//		dtlVO.setcomp_id(dtlPK);
//
//		dtlVO.setPROD_ID(inputVO.getProdId()); //基金代號  Varchar2 (16)
//		dtlVO.setPROD_NAME(inputVO.getProdName()); //基金名稱  Varchar2 (80)
//		dtlVO.setPROD_ID_C1(inputVO.getProdIdC1()); //基金代號  Varchar2 (16)
//		dtlVO.setPROD_NAME_C1(inputVO.getProdNameC1()); //基金名稱  Varchar2 (80)
//		dtlVO.setPROD_ID_C2(inputVO.getProdIdC2()); //基金代號  Varchar2 (16)
//		dtlVO.setPROD_NAME_C2(inputVO.getProdNameC2()); //基金名稱  Varchar2 (80)
//		dtlVO.setPROD_ID_C3(inputVO.getProdIdC3()); //基金代號  Varchar2 (16)
//		dtlVO.setPROD_NAME_C3(inputVO.getProdNameC3()); //基金名稱  Varchar2 (80)
//		dtlVO.setPROD_ID_C4(inputVO.getProdIdC4()); //基金代號  Varchar2 (16)
//		dtlVO.setPROD_NAME_C4(inputVO.getProdNameC4()); //基金名稱  Varchar2 (80)
//		dtlVO.setPROD_ID_C5(inputVO.getProdIdC5()); //基金代號  Varchar2 (16)
//		dtlVO.setPROD_NAME_C5(inputVO.getProdNameC5()); //基金名稱  Varchar2 (80)
//		dtlVO.setPROD_CURR(inputVO.getProdCurr()); //計價幣別  Char (3)
//		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLv()); //產品風險等級  Varchar2 (3)
//		dtlVO.setPROD_RISK_LV_C1(inputVO.getProdRiskLvC1()); //產品風險等級  Varchar2 (3)
//		dtlVO.setPROD_RISK_LV_C2(inputVO.getProdRiskLvC2()); //產品風險等級  Varchar2 (3)
//		dtlVO.setPROD_RISK_LV_C3(inputVO.getProdRiskLvC3()); //產品風險等級  Varchar2 (3)
//		dtlVO.setPROD_RISK_LV_C4(inputVO.getProdRiskLvC4()); //產品風險等級  Varchar2 (3)
//		dtlVO.setPROD_RISK_LV_C5(inputVO.getProdRiskLvC5()); //產品風險等級  Varchar2 (3)
//		dtlVO.setTRUST_CURR_TYPE(inputVO.getTrustCurrType()); //信託幣別類別  Char (1)
//		if (StringUtils.isBlank(inputVO.getTrustCurr())) {
//			if (StringUtils.equals("N", inputVO.getTrustCurrType()) || StringUtils.equals("C", inputVO.getTrustCurrType())) {
//				dtlVO.setTRUST_CURR("TWD"); //信託業務別非N.C則為台幣信託
//			} else {
//				dtlVO.setTRUST_CURR(inputVO.getProdCurr()); //信託業務別非N.C則為原幣信託
//			}
//		} else {
//			dtlVO.setTRUST_CURR(inputVO.getTrustCurr()); //信託幣別  Char (3)
//		}
//		dtlVO.setTRUST_ACCT(inputVO.getTrustAcct());
//		dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct());
//		dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt()); //申購金額  Number (16, 2)
//		if (inputVO.getPurchaseAmtC1() != null) dtlVO.setPURCHASE_AMT_C1(inputVO.getPurchaseAmtC1()); //申購金額  Number (16, 2)
//		if (inputVO.getPurchaseAmtC2() != null) dtlVO.setPURCHASE_AMT_C2(inputVO.getPurchaseAmtC2()); //申購金額  Number (16, 2)
//		if (inputVO.getPurchaseAmtC3() != null) dtlVO.setPURCHASE_AMT_C3(inputVO.getPurchaseAmtC3()); //申購金額  Number (16, 2)
//		if (inputVO.getPurchaseAmtC4() != null) dtlVO.setPURCHASE_AMT_C4(inputVO.getPurchaseAmtC4()); //申購金額  Number (16, 2)
//		if (inputVO.getPurchaseAmtC5() != null) dtlVO.setPURCHASE_AMT_C5(inputVO.getPurchaseAmtC5()); //申購金額  Number (16, 2)
//		dtlVO.setIN_PROD_ID(inputVO.getInProdId());
//		dtlVO.setIN_PROD_NAME(inputVO.getInProdName());
//		dtlVO.setIN_PROD_RISK_LV(inputVO.getInProdRiskLv());
//		dtlVO.setIN_PROD_CURR(inputVO.getInProdCurr());
//		dtlVO.setIN_PROD_C1_YN(inputVO.getInProdC1YN());
//		dtlVO.setIN_PROD_C2_YN(inputVO.getInProdC2YN());
//		dtlVO.setIN_PROD_C3_YN(inputVO.getInProdC3YN());
//		dtlVO.setIN_PROD_C4_YN(inputVO.getInProdC4YN());
//		dtlVO.setIN_PROD_C5_YN(inputVO.getInProdC5YN());
//		dtlVO.setTRADE_DATE_TYPE(inputVO.getTradeDateType()); //交易日期類別  Char (1)
//		Date tradeDate = null;
//		if ("2".equals(inputVO.getTradeDateType()) && StringUtils.isNotBlank(inputVO.getReservationTradeDate())) {//2預約
//			try {
//				SimpleDateFormat SDFYYYY_MM_DDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				tradeDate = SDFYYYY_MM_DDHHMMSS.parse(inputVO.getReservationTradeDate());
//			} catch (ParseException e) {
//				throw new JBranchException(e);
//			}
//		} else {//即時
//			tradeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate());
//		}
//		dtlVO.setTRADE_DATE(new Timestamp(tradeDate.getTime())); //交易日期  Date
//		dtlVO.setPROSPECTUS_TYPE(inputVO.getProspectusType()); //公開說明書選項
//		dtlVO.setTRANSFER_TYPE(inputVO.getTransferType());
//		dtlVO.setCERTIFICATE_ID(inputVO.getCertificateID());
//		dtlVO.setUNIT_NUM(inputVO.getNumUnits());
//		dtlVO.setUNIT_NUM_C1(inputVO.getNumUnitsC1());
//		dtlVO.setUNIT_NUM_C2(inputVO.getNumUnitsC2());
//		dtlVO.setUNIT_NUM_C3(inputVO.getNumUnitsC3());
//		dtlVO.setUNIT_NUM_C4(inputVO.getNumUnitsC4());
//		dtlVO.setUNIT_NUM_C5(inputVO.getNumUnitsC5());
//		dtlVO.setPRESENT_VAL(inputVO.getPresentVal());
//		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編  Varchar2 (16)
//		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名  Varchar2 (100)
//		
//		dam.create(dtlVO);
//		
//		this.sendRtnObject(outputVO);
//	}
//	
//	/**
//	 * 鍵機頁面下一步
//	 * 
//	 * @param body
//	 * @param header
//	 * @throws Exception
//	 */
//	public void next(Object body, IPrimitiveMap header) throws Exception {
//		SOT1640InputVO inputVO = (SOT1640InputVO) body;
//		SOT1640OutputVO outputVO = new SOT1640OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		
//		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
//		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
//		if (null != vo) {
//			vo.setTRADE_STATUS("2"); // 2:風控檢核中
//			dam.update(vo);
//		}
//
//		//檢核電文
//		inputVO.setConfirm("1"); 
//		String errMsg = dynamicESBValidate(inputVO);
//		outputVO.setErrorMsg(errMsg);
//		
//		this.sendRtnObject(outputVO);
//	}
//	
//	/**
//	 * 風控頁面傳送OP交易並列印表單
//	 * 
//	 * @param body
//	 * @param header
//	 * @throws JBranchException
//	 * @throws Exception
//	 */
//	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {
//		SOT1640InputVO inputVO = (SOT1640InputVO) body;
//		SOT1640OutputVO outputVO = new SOT1640OutputVO();
//		dam = this.getDataAccessManager();
//
//		//確認電文
//		inputVO.setConfirm("2"); 
//		String errMsg = dynamicESBValidate(inputVO);
//		if (StringUtils.isNotBlank(errMsg)) {
//			outputVO.setErrorMsg(errMsg);
//		} else {
//			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
//			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
//			vo.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單 
//			vo.setSEND_DATE(new Timestamp(System.currentTimeMillis())); //傳送OP後要設SEND_DATE
//			dam.update(vo);
//			// for CBS 測試用
//			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
//		}
//
//		this.sendRtnObject(outputVO);
//	}
//	
//	/**
//	 * 刪除購物車明細
//	 * 
//	 * @param body
//	 * @param header
//	 * @throws JBranchException
//	 */
//	public void delProd(Object body, IPrimitiveMap header) throws JBranchException {
//		SOT1640InputVO inputVO = (SOT1640InputVO) body;
//		SOT1640OutputVO outputVO = new SOT1640OutputVO();
//		dam = this.getDataAccessManager();
//
//		//刪除明細檔資料
//		TBSOT_NF_TRANSFER_DYNAPK dPK = new TBSOT_NF_TRANSFER_DYNAPK();
//		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
//		dPK.setSEQ_NO(new BigDecimal(1)); //固定為1
//		TBSOT_NF_TRANSFER_DYNAVO dVO = new TBSOT_NF_TRANSFER_DYNAVO();
//		dVO.setcomp_id(dPK);
//		dVO = (TBSOT_NF_TRANSFER_DYNAVO) dam.findByPKey(TBSOT_NF_TRANSFER_DYNAVO.TABLE_UID, dVO.getcomp_id());
//		dam.delete(dVO);
//		//刪除主檔資料
//		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
//		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
//		dam.delete(vo);
//		
//		this.sendRtnObject(null);
//	}
//		
//	/***
//	 * 下一步做檢核電文/傳送OP確認電文
//	 * @param inputVO
//	 * @return
//	 * @throws Exception 
//	 */
//	public String dynamicESBValidate(SOT1640InputVO inputVO) throws Exception {
//		SOT703InputVO inputVO703 = new SOT703InputVO();
//		SOT703OutputVO outputVO703 = new SOT703OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		
//		inputVO703.setTradeSeq(inputVO.getTradeSEQ());
//		inputVO703.setConfirm(inputVO.getConfirm());
//
//		SOT703Dyna sot703 = (SOT703Dyna) PlatformContext.getBean("sot703dyna");
//		outputVO703 = sot703.verifyESBTransferNFDYNA(inputVO703); //動態鎖利轉換交易驗證電文
//
//		String errorMsg = outputVO703.getErrorMsg();
//		if (StringUtils.equals("1", inputVO.getConfirm()) && StringUtils.isNotBlank(errorMsg)) {
//			//檢核電文失敗才刪除資料
//			TBSOT_NF_TRANSFER_DYNAPK errPK = new TBSOT_NF_TRANSFER_DYNAPK();
//			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
//			errPK.setSEQ_NO(inputVO.getSeqNo());
//			TBSOT_NF_TRANSFER_DYNAVO errVO = new TBSOT_NF_TRANSFER_DYNAVO();
//			errVO.setcomp_id(errPK);
//			errVO = (TBSOT_NF_TRANSFER_DYNAVO) dam.findByPKey(TBSOT_NF_TRANSFER_DYNAVO.TABLE_UID, errVO.getcomp_id());
//
//			//刪除明細
//			if (null != errVO) {
//				dam.delete(errVO);
//			}
//
//			//===若無明細，則將主檔也刪除
//			StringBuffer sb = new StringBuffer();
//			sb.append("SELECT * FROM TBSOT_NF_TRANSFER_DYNA WHERE TRADE_SEQ = :tradeSEQ ");
//			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
//			queryCondition.setQueryString(sb.toString());
//
//			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
//
//			if (list.size() == 0) {
//				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
//				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
//
//				dam.delete(mVO);
//			}   
//		}
//		
//		return StringUtils.isNotBlank(errorMsg) ? outputVO703.getErrorCode() + ":" + errorMsg : "";  
//	}
//	
//	/***
//	 * 高資產客戶投組風險權值檢核
//	 * @param inputVO
//	 * @return
//	 * @throws Exception
//	 */
//	private String getHnwcRiskValue(SOT1640InputVO inputVO) throws Exception {
//		//若檢核不通過，產生錯誤訊息
//		String errorMsg = "";
//		//高資產客戶且客戶風險屬性非C4，轉入有選擇越級商品，才做投組越級檢核	
//		//轉換方式 1：母基金轉換 => 轉入母基金有越級	
//		//轉換方式 2：子基金轉回母基金 => 轉回原母基金有越級
//		Integer prodRiskLV = StringUtils.isNotEmpty(inputVO.getProdRiskLv()) ? Integer.parseInt(inputVO.getProdRiskLv().substring(1)) : 1;
//		Integer prodRiskLVM = StringUtils.isNotEmpty(inputVO.getInProdRiskLv()) ? Integer.parseInt(inputVO.getInProdRiskLv().substring(1)) : 1;
//		Integer custRiskLV = Integer.parseInt(inputVO.getKycLV().substring(1));
//		if(!(!StringUtils.equals("C4", inputVO.getKycLV()) //客戶風險屬性非C4
//				&& StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN())	//高資產客戶
//				&& (prodRiskLVM > custRiskLV || (StringUtils.equals("2", inputVO.getTransferType())) && prodRiskLV > custRiskLV))) { //轉入母基金有越級
//			return errorMsg; //非高資產客戶或客戶風險屬性為C4，或轉入沒有選擇越級商品，不做投組越級檢核，回傳空值	
//		}
//		
//		//取得這次申購商品信託幣別買匯
//		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
//		BigDecimal rate = sot110.getBuyRate(inputVO.getTrustCurr());
//		//取得高資產客戶註記資料
//		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
//		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCustID());
//		
//		SOT714InputVO inputVO714 = new SOT714InputVO();
//		inputVO714.setCustID(inputVO.getCustID());
//		inputVO714.setCUST_KYC(inputVO.getKycLV()); //客戶C值
//		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
//		inputVO714.setPROD_RISK(getMaxPVal(inputVO)); //商品風險檢核值
//		BigDecimal amtBuy = BigDecimal.ZERO;//原母基金金額折台
//		BigDecimal amtBuyM = BigDecimal.ZERO;//這次轉入母基金金額折台
//		BigDecimal amtSell1 = BigDecimal.ZERO;//這次轉出子基金金額1折台
//		BigDecimal amtSell2 = BigDecimal.ZERO;//這次轉出子基金金額2折台
//		BigDecimal amtSell3 = BigDecimal.ZERO;//這次轉出子基金金額3折台
//		BigDecimal amtSell4 = BigDecimal.ZERO;//這次轉出子基金金額4折台
//		BigDecimal amtSell5 = BigDecimal.ZERO;//這次轉出子基金金額5折台
//		if(StringUtils.equals("1", inputVO.getTransferType())) { 
//			//轉換方式 1：母基金轉換
//			amtBuyM = inputVO.getPurchaseAmt().multiply(rate); //信託金額*匯率
//		} else { 
//			//轉換方式 2：子基金轉回母基金
//			//轉入母基金(原母基金)
//			amtBuy = inputVO.getPurchaseAmt().multiply(rate); //原母基金信託金額*匯率
//			//轉出子基金1
//			if(StringUtils.equals("Y", inputVO.getInProdC1YN())) {
//				amtSell1 = inputVO.getPurchaseAmtC1().multiply(rate); //信託金額*匯率
//			}
//			//轉出轉出2
//			if(StringUtils.equals("Y", inputVO.getInProdC2YN())) {
//				amtSell2 = inputVO.getPurchaseAmtC2().multiply(rate); //信託金額*匯率
//			}
//			//轉出轉出3
//			if(StringUtils.equals("Y", inputVO.getInProdC3YN())) {
//				amtSell3 = inputVO.getPurchaseAmtC3().multiply(rate); //信託金額*匯率
//			}
//			//轉出轉出4
//			if(StringUtils.equals("Y", inputVO.getInProdC4YN())) {
//				amtSell4 = inputVO.getPurchaseAmtC4().multiply(rate); //信託金額*匯率
//			}
//			//轉出轉出5
//			if(StringUtils.equals("Y", inputVO.getInProdC5YN())) {
//				amtSell5 = inputVO.getPurchaseAmtC5().multiply(rate); //信託金額*匯率
//			}
//		}
//		
//		for(int i = 1; i <= 4; i++) {
//			String pVal = "P" + String.valueOf(i);
//			if(StringUtils.equals("1", inputVO.getTransferType())) { 
//				//轉換方式 1：母基金轉換
//				BigDecimal amtBuyPval = StringUtils.equals(pVal, inputVO.getInProdRiskLv()) ? amtBuyM : BigDecimal.ZERO; //轉入母基金
//				BigDecimal amtSellPval = StringUtils.equals(pVal, inputVO.getProdRiskLv()) ? amtBuy : BigDecimal.ZERO; //轉出母基金(原母基金)
//				switch(pVal) {
//					case "P1":
//						inputVO714.setAMT_BUY_1(amtBuyPval); 
//						inputVO714.setAMT_SELL_1(amtSellPval);
//						break;
//					case "P2":
//						inputVO714.setAMT_BUY_2(amtBuyPval); 
//						inputVO714.setAMT_SELL_2(amtSellPval);
//						break;
//					case "P3":
//						inputVO714.setAMT_BUY_3(amtBuyPval); 
//						inputVO714.setAMT_SELL_3(amtSellPval);
//						break;
//					case "P4":
//						inputVO714.setAMT_BUY_4(amtBuyPval); 
//						inputVO714.setAMT_SELL_4(amtSellPval);
//						break;
//				}
//			} else {
//				//轉換方式 2：子基金轉回母基金
//				BigDecimal amtBuyPval = StringUtils.equals(pVal, inputVO.getProdRiskLv()) ? amtBuy : BigDecimal.ZERO; //轉入母基金(原母基金)
//				BigDecimal amtSellPvalC1 = (StringUtils.equals("Y", inputVO.getInProdC1YN()) && StringUtils.equals(pVal, inputVO.getProdRiskLvC1())) ? amtSell1 : BigDecimal.ZERO; //轉出子基金
//				BigDecimal amtSellPvalC2 = (StringUtils.equals("Y", inputVO.getInProdC2YN()) && StringUtils.equals(pVal, inputVO.getProdRiskLvC2())) ? amtSell2 : BigDecimal.ZERO; //轉出子基金
//				BigDecimal amtSellPvalC3 = (StringUtils.equals("Y", inputVO.getInProdC3YN()) && StringUtils.equals(pVal, inputVO.getProdRiskLvC3())) ? amtSell3 : BigDecimal.ZERO; //轉出子基金
//				BigDecimal amtSellPvalC4 = (StringUtils.equals("Y", inputVO.getInProdC4YN()) && StringUtils.equals(pVal, inputVO.getProdRiskLvC4())) ? amtSell4 : BigDecimal.ZERO; //轉出子基金
//				BigDecimal amtSellPvalC5 = (StringUtils.equals("Y", inputVO.getInProdC5YN()) && StringUtils.equals(pVal, inputVO.getProdRiskLvC5())) ? amtSell5 : BigDecimal.ZERO; //轉出子基金
//				switch(pVal) {
//					case "P1":
//						inputVO714.setAMT_BUY_1(amtBuyPval); 
//						inputVO714.setAMT_SELL_1(amtSellPvalC1.add(amtSellPvalC2).add(amtSellPvalC3).add(amtSellPvalC4).add(amtSellPvalC5));
//						break;
//					case "P2":
//						inputVO714.setAMT_BUY_2(amtBuyPval); 
//						inputVO714.setAMT_SELL_2(amtSellPvalC1.add(amtSellPvalC2).add(amtSellPvalC3).add(amtSellPvalC4).add(amtSellPvalC5));
//						break;
//					case "P3":
//						inputVO714.setAMT_BUY_3(amtBuyPval); 
//						inputVO714.setAMT_SELL_3(amtSellPvalC1.add(amtSellPvalC2).add(amtSellPvalC3).add(amtSellPvalC4).add(amtSellPvalC5));
//						break;
//					case "P4":
//						inputVO714.setAMT_BUY_4(amtBuyPval); 
//						inputVO714.setAMT_SELL_4(amtSellPvalC1.add(amtSellPvalC2).add(amtSellPvalC3).add(amtSellPvalC4).add(amtSellPvalC5));
//						break;
//				}	
//			}
//		}
//		//查詢客戶風險檢核值資料
//		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
//		
//		if(StringUtils.equals("N", riskValData.getVALIDATE_YN())) {
//			errorMsg = "客戶投資組合風險權值已超標，可至「投組適配試算功能」重新試算";
//		}
//		
//		return errorMsg;
//	}
//	
//	/***
//	 * 取得母子基金商品中最大的風險值
//	 * @param inputVO
//	 * @return
//	 * @throws Exception
//	 */
//	private String getMaxPVal(SOT1640InputVO inputVO) throws Exception {
//		String maxPval = inputVO.getProdRiskLv(); //預設原母基金C值
//		
//		try {
//			int PVal = Integer.parseInt(inputVO.getProdRiskLv().substring(1)); //預設原母基金C值;
//			if(StringUtils.equals("1", inputVO.getTransferType())) { 
//				//轉換方式 1：母基金轉換
//				//轉入母基金
//				PVal = Integer.parseInt(inputVO.getInProdRiskLv().substring(1)); 
//			} else { 
//				//轉換方式 2：子基金轉回母基金
//				//子基金1
//				if(StringUtils.equals("Y", inputVO.getInProdC1YN())) { 
//					if(Integer.parseInt(inputVO.getProdRiskLvC1().substring(1)) > PVal) {
//						PVal = Integer.parseInt(inputVO.getProdRiskLvC1().substring(1));
//					}
//				}
//				//子基金2
//				if(StringUtils.equals("Y", inputVO.getInProdC2YN())) { 
//					if(Integer.parseInt(inputVO.getProdRiskLvC2().substring(1)) > PVal) {
//						PVal = Integer.parseInt(inputVO.getProdRiskLvC2().substring(1));
//					}
//				}
//				//子基金3
//				if(StringUtils.equals("Y", inputVO.getInProdC3YN())) {
//					if(Integer.parseInt(inputVO.getProdRiskLvC3().substring(1)) > PVal) {
//						PVal = Integer.parseInt(inputVO.getProdRiskLvC3().substring(1));
//					}
//				}
//				//子基金4
//				if(StringUtils.equals("Y", inputVO.getInProdC4YN())) {
//					if(Integer.parseInt(inputVO.getProdRiskLvC4().substring(1)) > PVal) {
//						PVal = Integer.parseInt(inputVO.getProdRiskLvC4().substring(1));
//					}
//				}
//				//子基金5
//				if(StringUtils.equals("Y", inputVO.getInProdC5YN())) {
//					if(Integer.parseInt(inputVO.getProdRiskLvC5().substring(1)) > PVal) {
//						PVal = Integer.parseInt(inputVO.getProdRiskLvC5().substring(1));
//					}
//				}
//			}
//			
//			maxPval = "P" + String.valueOf(PVal);
//		} catch(Exception e) {}
//		
//		return maxPval;
//	}
	
}