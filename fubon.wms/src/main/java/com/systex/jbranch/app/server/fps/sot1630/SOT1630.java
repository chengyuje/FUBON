package com.systex.jbranch.app.server.fps.sot1630;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DYNAVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot110.SOT110OutputVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703Dyna;
import com.systex.jbranch.app.server.fps.sot703.SOT703InputVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703OutputVO;
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
 * sot1630
 * 動態鎖利贖回
 */
@Component("sot1630")
@Scope("request")
public class SOT1630 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT1630.class);
	
	/**
	 * 查詢購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		SOT1630InputVO inputVO = (SOT1630InputVO) body;
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		inputVO = (SOT1630InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1630OutputVO outputVO = new SOT1630OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT A.*, F.FUS07 ");
		sb.append(" FROM TBSOT_NF_REDEEM_DYNA A ");
		sb.append(" LEFT JOIN TBPRD_FUND F ON A.PROD_ID = F.PRD_ID ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> carList = dam.exeQuery(queryCondition);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> curMap = xmlInfo.doGetVariable("SOT.FUND_DECIMAL_POINT", FormatHelper.FORMAT_3);//幣別小數位控制

		for (Map<String, Object> dataMap : carList) {
			String trustCurr = dataMap.get("TRUST_CURR").toString();
			dataMap.put("CUR_NUM", curMap.get(trustCurr).toString());
			//2017-09-11 取基金-小數點位數
			String prodId = dataMap.get("PROD_ID").toString();
		}
		outputVO.setCarList(carList);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM TBSOT_TRADE_MAIN ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());
		outputVO.setMainList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}
	
	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {
		SOT1630InputVO inputVO = (SOT1630InputVO) body;
		SOT1630OutputVO outputVO = new SOT1630OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT f.*, i.FUS20, i.FUS40 ");
		sb.append("FROM TBPRD_FUND f ");
		sb.append("LEFT JOIN TBPRD_FUNDINFO i on (f.PRD_ID=i.PRD_ID) ");
		sb.append("WHERE f.PRD_ID = :prodID ");
		queryCondition.setObject("prodID", inputVO.getProdId());
		queryCondition.setQueryString(sb.toString());

		outputVO.setProdDTL(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}
		
	/**
	 * 鍵機頁面暫存
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		SOT1630InputVO inputVO = (SOT1630InputVO) body;
		inputVO = (SOT1630InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1630OutputVO outputVO = new SOT1630OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();

			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE(inputVO.getProdType()); //8：動態鎖利
			mainVO.setTRADE_TYPE("2");//2：贖回
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLV());
			if (inputVO.getKycDueDate() != null) mainVO.setKYC_DUE_DATE(new Timestamp(inputVO.getKycDueDate().getTime()));
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			if (inputVO.getPiDueDate() != null)	mainVO.setPI_DUE_DATE(new Timestamp(inputVO.getPiDueDate().getTime()));
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			mainVO.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
			if (inputVO.getBargainDueDate() != null) mainVO.setBARGAIN_DUE_DATE(new Timestamp(inputVO.getBargainDueDate().getTime()));
			mainVO.setTRADE_STATUS("1"); //1:暫存
			mainVO.setIS_BARGAIN_NEEDED("N");
			mainVO.setIS_REC_NEEDED("N"); //贖回不需要錄音
			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);
			mainVO.setPROF_INVESTOR_TYPE(inputVO.getCustProType()); //專業投資人類別1：大專投  2：小專投
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
			
			/*
			 * About CR list ↓
			 * 	WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5
			 * 	WMS-CR-20190710-05_個人高端客群處業管系統_行銷模組調整申請_P1
			 * 
			 * Version ↓
			 * 	2017-01-11 modify by mimi for SOT7XX
			 * 	2019-07-24 modify by ocean
			 *
			 */
			if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				
				sb.append("SELECT BRANCH_NBR ");
				sb.append("FROM TBORG_UHRM_BRH ");
				sb.append("WHERE EMP_ID = :loginID ");

				queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);
				
				if (loginBreach.size() > 0) {
					mainVO.setBRANCH_NBR((String) loginBreach.get(0).get("BRANCH_NBR"));
				} else {
					throw new APException("人員無有效分行"); //顯示錯誤訊息
				}
			} else {
				mainVO.setBRANCH_NBR(String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH)));
			}
			
			//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
			mainVO.setTRUST_TRADE_TYPE("S");  //M:金錢信託  S:特金交易			
			mainVO.setIS_WEB("N");//是否為快速申購(Y/N)\
			mainVO.setFLAG_NUMBER(inputVO.getFlagNumber());			//90天內是否有貸款紀錄 Y/N
			
			dam.create(mainVO);
		} else {
			vo.setTRADE_STATUS("1"); //暫存

			dam.update(vo);
		}

		//贖回明細資料
		TBSOT_NF_REDEEM_DYNAPK dPK = new TBSOT_NF_REDEEM_DYNAPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1
		TBSOT_NF_REDEEM_DYNAVO dVO = new TBSOT_NF_REDEEM_DYNAVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_REDEEM_DYNAVO) dam.findByPKey(TBSOT_NF_REDEEM_DYNAVO.TABLE_UID, dVO.getcomp_id());

		if (null != dVO) {
			dam.delete(dVO);
		}

		TBSOT_NF_REDEEM_DYNAPK dtlPK = new TBSOT_NF_REDEEM_DYNAPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1

		TBSOT_NF_REDEEM_DYNAVO dtlVO = new TBSOT_NF_REDEEM_DYNAVO();
		dtlVO.setcomp_id(dtlPK);

		dtlVO.setPROD_ID(inputVO.getProdId()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME(inputVO.getProdName()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_ID_C1(inputVO.getProdIdC1()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME_C1(inputVO.getProdNameC1()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_ID_C2(inputVO.getProdIdC2()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME_C2(inputVO.getProdNameC2()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_ID_C3(inputVO.getProdIdC3()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME_C3(inputVO.getProdNameC3()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_ID_C4(inputVO.getProdIdC4()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME_C4(inputVO.getProdNameC4()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_ID_C5(inputVO.getProdIdC5()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME_C5(inputVO.getProdNameC5()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_CURR(inputVO.getProdCurr()); //計價幣別  Char (3)
		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLv()); //產品風險等級  Varchar2 (3)
		dtlVO.setPROD_RISK_LV_C1(inputVO.getProdRiskLvC1()); //產品風險等級  Varchar2 (3)
		dtlVO.setPROD_RISK_LV_C2(inputVO.getProdRiskLvC2()); //產品風險等級  Varchar2 (3)
		dtlVO.setPROD_RISK_LV_C3(inputVO.getProdRiskLvC3()); //產品風險等級  Varchar2 (3)
		dtlVO.setPROD_RISK_LV_C4(inputVO.getProdRiskLvC4()); //產品風險等級  Varchar2 (3)
		dtlVO.setPROD_RISK_LV_C5(inputVO.getProdRiskLvC5()); //產品風險等級  Varchar2 (3)
		dtlVO.setTRUST_CURR_TYPE(inputVO.getTrustCurrType()); //信託幣別類別  Char (1)
		if (StringUtils.isBlank(inputVO.getTrustCurr())) {
			if (StringUtils.equals("N", inputVO.getTrustCurrType()) || StringUtils.equals("C", inputVO.getTrustCurrType())) {
				dtlVO.setTRUST_CURR("TWD"); //信託業務別非N.C則為台幣信託
			} else {
				dtlVO.setTRUST_CURR(inputVO.getProdCurr()); //信託業務別非N.C則為原幣信託
			}
		} else {
			dtlVO.setTRUST_CURR(inputVO.getTrustCurr()); //信託幣別  Char (3)
		}
		dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC1() != null) dtlVO.setPURCHASE_AMT_C1(inputVO.getPurchaseAmtC1()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC2() != null) dtlVO.setPURCHASE_AMT_C2(inputVO.getPurchaseAmtC2()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC3() != null) dtlVO.setPURCHASE_AMT_C3(inputVO.getPurchaseAmtC3()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC4() != null) dtlVO.setPURCHASE_AMT_C4(inputVO.getPurchaseAmtC4()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC5() != null) dtlVO.setPURCHASE_AMT_C5(inputVO.getPurchaseAmtC5()); //申購金額  Number (16, 2)
		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct()); //扣款帳號  Varchar2 (16)
		dtlVO.setTRUST_ACCT(inputVO.getTrustAcct()); //信託帳號  Varchar2 (16)
		dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct()); //收益入帳帳號  Varchar2 (16)
		dtlVO.setTRADE_DATE_TYPE(inputVO.getTradeDateType()); //交易日期類別  Char (1)
		Date tradeDate = null;
		if ("2".equals(inputVO.getTradeDateType()) && StringUtils.isNotBlank(inputVO.getReservationTradeDate())) {//2預約
			try {
				SimpleDateFormat SDFYYYY_MM_DDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				tradeDate = SDFYYYY_MM_DDHHMMSS.parse(inputVO.getReservationTradeDate());
			} catch (ParseException e) {
				throw new JBranchException(e);
			}
		} else {//即時
			tradeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate());
		}
		dtlVO.setTRADE_DATE(new Timestamp(tradeDate.getTime())); //交易日期  Date
		dtlVO.setCERTIFICATE_ID(inputVO.getCertificateID());
		dtlVO.setRDM_UNIT(inputVO.getRdmUnit());
		dtlVO.setUNIT_NUM(inputVO.getNumUnits());
		dtlVO.setREDEEM_TYPE(inputVO.getRedeemType());
		dtlVO.setPRESENT_VAL(inputVO.getPresentVal());
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編  Varchar2 (16)
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名  Varchar2 (100)
		
		dam.create(dtlVO);
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 鍵機頁面下一步
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void next(Object body, IPrimitiveMap header) throws Exception {
		SOT1630InputVO inputVO = (SOT1630InputVO) body;
		SOT1630OutputVO outputVO = new SOT1630OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null != vo) {
			vo.setTRADE_STATUS("2"); // 2:風控檢核中
			dam.update(vo);
		}

		//檢核電文
		inputVO.setConfirm("1"); 
		SOT703OutputVO outputVO703 = dynamicESBValidate(inputVO);
		outputVO.setErrorMsg(outputVO703.getErrorMsg());
		outputVO.setShortType(outputVO703.getShort_1());
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 風控頁面傳送OP交易並列印表單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT1630InputVO inputVO = (SOT1630InputVO) body;
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		inputVO = (SOT1630InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1630OutputVO outputVO = new SOT1630OutputVO();
		dam = this.getDataAccessManager();

		//確認電文
		inputVO.setConfirm("2"); 
		SOT703OutputVO outputVO703 = dynamicESBValidate(inputVO);
		String errorMsg = outputVO703.getErrorMsg();
		if (StringUtils.isNotBlank(errorMsg)) {
			outputVO.setErrorMsg(errorMsg);
		} else {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
			vo.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單 
			vo.setSEND_DATE(new Timestamp(System.currentTimeMillis())); //傳送OP後要設SEND_DATE
			dam.update(vo);
			// for CBS 測試用
			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
		}

		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 刪除購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void delProd(Object body, IPrimitiveMap header) throws JBranchException {
		SOT1630InputVO inputVO = (SOT1630InputVO) body;
		SOT1630OutputVO outputVO = new SOT1630OutputVO();
		dam = this.getDataAccessManager();

		//刪除明細檔資料
		TBSOT_NF_REDEEM_DYNAPK dPK = new TBSOT_NF_REDEEM_DYNAPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(new BigDecimal(1)); //固定為1
		TBSOT_NF_REDEEM_DYNAVO dVO = new TBSOT_NF_REDEEM_DYNAVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_REDEEM_DYNAVO) dam.findByPKey(TBSOT_NF_REDEEM_DYNAVO.TABLE_UID, dVO.getcomp_id());
		dam.delete(dVO);
		//刪除主檔資料
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		dam.delete(vo);
		
		this.sendRtnObject(null);
	}
		
	/***
	 * 下一步做檢核電文/傳送OP確認電文
	 * @param inputVO
	 * @return
	 * @throws Exception 
	 */
	public SOT703OutputVO dynamicESBValidate(SOT1630InputVO inputVO) throws Exception {
		SOT703InputVO inputVO703 = new SOT703InputVO();
		SOT703OutputVO outputVO703 = new SOT703OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		inputVO703.setTradeSeq(inputVO.getTradeSEQ());
		inputVO703.setConfirm(inputVO.getConfirm());

		SOT703Dyna sot703 = (SOT703Dyna) PlatformContext.getBean("sot703dyna");
		outputVO703 = sot703.verifyESBRedeemNFDYNA(inputVO703); //動態鎖利贖回交易驗證電文

		if (StringUtils.equals("1", inputVO.getConfirm()) && StringUtils.isNotBlank(outputVO703.getErrorMsg())) {
			//檢核電文失敗才刪除資料
			TBSOT_NF_REDEEM_DYNAPK errPK = new TBSOT_NF_REDEEM_DYNAPK();
			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			errPK.setSEQ_NO(inputVO.getSeqNo());
			TBSOT_NF_REDEEM_DYNAVO errVO = new TBSOT_NF_REDEEM_DYNAVO();
			errVO.setcomp_id(errPK);
			errVO = (TBSOT_NF_REDEEM_DYNAVO) dam.findByPKey(TBSOT_NF_REDEEM_DYNAVO.TABLE_UID, errVO.getcomp_id());

			//刪除明細
			if (null != errVO) {
				dam.delete(errVO);
			}

			//===若無明細，則將主檔也刪除
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBSOT_NF_REDEEM_DYNA WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

				dam.delete(mVO);
			}   
		}
		
		return outputVO703;
	}
	
}