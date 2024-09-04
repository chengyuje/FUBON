package com.systex.jbranch.app.server.fps.sot1610;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DYNAVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421;
import com.systex.jbranch.app.server.fps.crm421.CRM421InputVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421OutputVO;
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
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sot1610
 * 動態鎖利單筆申購
 */
@Component("sot1610")
@Scope("request")
public class SOT1610 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT1610.class);
	
	/**
	 * 查詢購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		SOT1610InputVO inputVO = (SOT1610InputVO) body;
		SOT1610OutputVO outputVO = new SOT1610OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT A.* ");
		sb.append(" FROM TBSOT_NF_PURCHASE_DYNA A ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> carList = dam.exeQuery(queryCondition);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> curMap = xmlInfo.doGetVariable("SOT.FUND_DECIMAL_POINT", FormatHelper.FORMAT_3);//幣別小數位控制

		for (Map<String, Object> dataMap : carList) {
			String trustCurr = "";
			if (StringUtils.equals(dataMap.get("TRUST_CURR_TYPE").toString(), "Y")) {
				trustCurr = dataMap.get("PROD_CURR").toString(); //外幣信託
			} else {
				trustCurr = "TWD"; //台幣信託
			}
			dataMap.put("CUR_NUM", curMap.get(trustCurr).toString());
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

		outputVO.setIsBackendInCart("N"); //交易中沒有後收型基金		

		this.sendRtnObject(outputVO);
	}
	
	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {
		SOT1610InputVO inputVO = (SOT1610InputVO) body;
		SOT1610OutputVO outputVO = new SOT1610OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		boolean isFitness = StringUtils.equals("N", (String) new XmlInfo().getVariable("SOT.FITNESS_YN", "NF", "F3")) ? false : true;

		outputVO.setWarningMsg("");
		outputVO.setErrorMsg("");

		boolean isFitnessOK = false;
		if (isFitness) {
			// 1.適配，由商品查詢取得，邏輯需一致
			PRD110OutputVO prdOutputVO = new PRD110OutputVO();
			PRD110InputVO prdInputVO = new PRD110InputVO();
			prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
			prdInputVO.setType("4");
			prdInputVO.setFund_id(inputVO.getProdId());
			prdInputVO.setTrustTS(inputVO.getTrustTS());
			//動態鎖利
			prdInputVO.setFromSOTProdYN("Y");
			prdInputVO.setDynamicType(inputVO.getDynamicType());
			if(StringUtils.equals("C", inputVO.getDynamicType())) {
				//子基金須為母基金同系列商品
				prdInputVO.setSameSerialYN("Y"); 
				prdInputVO.setSameSerialProdId(inputVO.getProdIdM());
				prdInputVO.setDynamicProdCurrM(inputVO.getDynamicProdCurrM());
			}

			PRD110 prd110 = (PRD110) PlatformContext.getBean("prd110");
			prdOutputVO = prd110.inquire(prdInputVO);

			if (CollectionUtils.isNotEmpty(prdOutputVO.getResultList())) {
				String warningMsg = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("warningMsg");
				String errId = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("errorID");

				if (StringUtils.isBlank(errId)) {
					isFitnessOK = true;
				}

				outputVO.setWarningMsg(warningMsg);
				outputVO.setErrorMsg(errId);
			}
		} else {
			logger.debug("SOT.FITNESS_YN 不進行適配 ");
			isFitnessOK = true;
		}

		// 2.查詢商品主檔
		if (isFitnessOK) { // 適配條件符合
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT f.*, i.FUS20, i.FUS40 ");
			sb.append("FROM TBPRD_FUND f ");
			sb.append("LEFT JOIN TBPRD_FUNDINFO i on (f.PRD_ID=i.PRD_ID) ");
			sb.append("WHERE f.PRD_ID = :prodID ");
			queryCondition.setObject("prodID", inputVO.getProdId());
			queryCondition.setQueryString(sb.toString());

			outputVO.setProdDTL(dam.exeQuery(queryCondition));
		}

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
		SOT1610InputVO inputVO = (SOT1610InputVO) body;
		inputVO = (SOT1610InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1610OutputVO outputVO = new SOT1610OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				
		//高資產客戶投組風險權值檢核
		//動態鎖利下單不可越級適配
//		String errorMsg = getHnwcRiskValue((SOT1610InputVO)inputVO);
//		if(StringUtils.isNotBlank(errorMsg)) {
//			throw new APException(errorMsg);
//		}
				
		//若為申請議價，需先呼叫CRM421存檔並檢核
		if (StringUtils.equals("A", inputVO.getFeeType())) {
			CRM421OutputVO crm421OutputVO = applyBargain(inputVO);
			if (StringUtils.isNotBlank(crm421OutputVO.getErrorMsg())) {
				throw new APException("議價申請檢核錯誤：" + crm421OutputVO.getErrorMsg()); //顯示錯誤訊息
			} else {
				inputVO.setBargainApplySEQ(crm421OutputVO.getApplySeqList().get(0).toString());
			}
		}

		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();

			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE(inputVO.getProdType()); //8：動態鎖利
			mainVO.setTRADE_TYPE("1");//1：單筆申購
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLV());
			if (inputVO.getKycDueDate() != null) mainVO.setKYC_DUE_DATE(new Timestamp(inputVO.getKycDueDate().getTime()));
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			if (inputVO.getPiDueDate() != null)
				mainVO.setPI_DUE_DATE(new Timestamp(inputVO.getPiDueDate().getTime()));
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			mainVO.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
			if (inputVO.getBargainDueDate() != null) mainVO.setBARGAIN_DUE_DATE(new Timestamp(inputVO.getBargainDueDate().getTime()));
			mainVO.setTRADE_STATUS("1"); //1:暫存
			mainVO.setIS_BARGAIN_NEEDED(StringUtils.equals("A", inputVO.getFeeType()) ? "Y" : "N");
			mainVO.setBARGAIN_FEE_FLAG(StringUtils.equals("A", inputVO.getFeeType()) ? "1" : null);
			
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO inputVO_712 = new SOT712InputVO();
			inputVO_712.setCustID(inputVO.getCustID());
			inputVO_712.setProdID(null);
			inputVO_712.setProdType("NF");
			inputVO_712.setProfInvestorYN(inputVO.getProfInvestorYN()); //是否為專業投資人
			inputVO_712.setIsFirstTrade(inputVO.getIsFirstTrade()); //是否首購  
			inputVO_712.setCustRemarks(inputVO.getCustRemarks());
			inputVO_712.setCustProRemark(inputVO.getPiRemark());
			inputVO_712.setOvsPrivateYN(inputVO.getOvsPrivateYN());
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); //IS_REC_NEEDED是否需要錄音 (非常規交易)
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
			//購物車中的申購方式需一樣(臨櫃/快速申購)
			if(!StringUtils.equals(inputVO.getIsWeb(), vo.getIS_WEB())) {
				throw new APException("購物車中的申購方式需一致"); //顯示錯誤訊息
			}
			
			//若購物車中多筆中的任一筆需要議價，主檔都須修改議價相關欄位
			if (StringUtils.equals("A", inputVO.getFeeType())) {
				vo.setIS_BARGAIN_NEEDED("Y");
				vo.setBARGAIN_FEE_FLAG("1");
			}
			vo.setTRADE_STATUS("1"); //暫存
			vo.setHNWC_YN(inputVO.getHnwcYN());
			vo.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());

			dam.update(vo);
		}

		//單筆申購明細資料
		TBSOT_NF_PURCHASE_DYNAPK dPK = new TBSOT_NF_PURCHASE_DYNAPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1
		TBSOT_NF_PURCHASE_DYNAVO dVO = new TBSOT_NF_PURCHASE_DYNAVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_PURCHASE_DYNAVO) dam.findByPKey(TBSOT_NF_PURCHASE_DYNAVO.TABLE_UID, dVO.getcomp_id());

		if (null != dVO) {
			dam.delete(dVO);
		}

		TBSOT_NF_PURCHASE_DYNAPK dtlPK = new TBSOT_NF_PURCHASE_DYNAPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1

		TBSOT_NF_PURCHASE_DYNAVO dtlVO = new TBSOT_NF_PURCHASE_DYNAVO();
		dtlVO.setcomp_id(dtlPK);

		dtlVO.setTRADE_SUB_TYPE(inputVO.getTradeSubType()); //信託型態  Char (1)
		dtlVO.setPROD_ID(inputVO.getProdId()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME(inputVO.getProdName()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_ID_C1(inputVO.getProdIdC1()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME_C1(inputVO.getProdNameC1()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_ID_C2(inputVO.getProdIdC2()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME_C2(inputVO.getProdNameC2()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_ID_C3(inputVO.getProdIdC3()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME_C3(inputVO.getProdNameC3()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_CURR(inputVO.getProdCurr()); //計價幣別  Char (3)
		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLv()); //產品風險等級  Varchar2 (3)
		dtlVO.setPROD_RISK_LV_C1(inputVO.getProdRiskLvC1()); //產品風險等級  Varchar2 (3)
		dtlVO.setPROD_RISK_LV_C2(inputVO.getProdRiskLvC2()); //產品風險等級  Varchar2 (3)
		dtlVO.setPROD_RISK_LV_C3(inputVO.getProdRiskLvC3()); //產品風險等級  Varchar2 (3)

		if (inputVO.getProdMinBuyAmt() != null) dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt()); //最低申購金額  Number (16, 2)
		if (inputVO.getProdMinBuyAmtC1() != null) dtlVO.setPROD_MIN_BUY_AMT_C1(inputVO.getProdMinBuyAmtC1()); //最低申購金額  Number (16, 2)
		if (inputVO.getProdMinBuyAmtC2() != null) dtlVO.setPROD_MIN_BUY_AMT_C2(inputVO.getProdMinBuyAmtC2()); //最低申購金額  Number (16, 2)
		if (inputVO.getProdMinBuyAmtC3() != null) dtlVO.setPROD_MIN_BUY_AMT_C3(inputVO.getProdMinBuyAmtC3()); //最低申購金額  Number (16, 2)

		if (inputVO.getProdMinGrdAmt() != null) dtlVO.setPROD_MIN_GRD_AMT(inputVO.getProdMinGrdAmt()); //最低累進金額  Number (16, 2)
		if (inputVO.getProdMinGrdAmtC1() != null) dtlVO.setPROD_MIN_GRD_AMT_C1(inputVO.getProdMinGrdAmtC1()); //最低累進金額  Number (16, 2)
		if (inputVO.getProdMinGrdAmtC2() != null) dtlVO.setPROD_MIN_GRD_AMT_C2(inputVO.getProdMinGrdAmtC2()); //最低累進金額  Number (16, 2)
		if (inputVO.getProdMinGrdAmtC3() != null) dtlVO.setPROD_MIN_GRD_AMT_C3(inputVO.getProdMinGrdAmtC3()); //最低累進金額  Number (16, 2)

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
		dtlVO.setFEE_TYPE(StringUtils.isNotBlank(inputVO.getFeeType()) ? inputVO.getFeeType() : "C"); //手續費優惠方式
		dtlVO.setBARGAIN_STATUS(StringUtils.equals("A", inputVO.getFeeType()) ? "1" : null); //手續費方式為申請議價時，才放這個欄位資料
		dtlVO.setBARGAIN_APPLY_SEQ(inputVO.getBargainApplySEQ()); //手續費方式為事先單次議價或申請議價，才會有議價編號欄位
		dtlVO.setDEFAULT_FEE_RATE(inputVO.getDefaultFeeRate()); //表定手續費率  Number (6, 3)
		dtlVO.setFEE_RATE(inputVO.getFeeRate()); //手續費率  Number (6, 3)
		dtlVO.setFEE(inputVO.getFee()); //手續費金額  Number (13, 2)
		dtlVO.setFEE_DISCOUNT(inputVO.getFeeDiscount()); //手續費折數  Number (6, 3)
		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct()); //扣款帳號  Varchar2 (16)
		dtlVO.setTRUST_ACCT(inputVO.getTrustAcct()); //信託帳號  Varchar2 (16)
		dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct()); //收益入帳帳號  Varchar2 (16)
		dtlVO.setPROSPECTUS_TYPE(inputVO.getProspectusType()); //公開說明書選項
		dtlVO.setTRADE_DATE_TYPE(inputVO.getTradeDateType()); //交易日期類別  Char (1)
		dtlVO.setTRANSFER_DATE_1(StringUtils.isBlank(inputVO.getTransDate1()) ? "N" : inputVO.getTransDate1());
		dtlVO.setTRANSFER_DATE_2(StringUtils.isBlank(inputVO.getTransDate2()) ? "N" : inputVO.getTransDate2());
		dtlVO.setTRANSFER_DATE_3(StringUtils.isBlank(inputVO.getTransDate3()) ? "N" : inputVO.getTransDate3());
		dtlVO.setTRANSFER_DATE_4(StringUtils.isBlank(inputVO.getTransDate4()) ? "N" : inputVO.getTransDate4());
		dtlVO.setTRANSFER_DATE_5(StringUtils.isBlank(inputVO.getTransDate5()) ? "N" : inputVO.getTransDate5());
		dtlVO.setTRANSFER_DATE_6(StringUtils.isBlank(inputVO.getTransDate6()) ? "N" : inputVO.getTransDate6());

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
		if (inputVO.getEngagedROI() != null) dtlVO.setENGAGED_ROI(inputVO.getEngagedROI());
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編  Varchar2 (16)
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名  Varchar2 (100)

		if (inputVO.getFeeType().matches("C|E")) { // C：最優手續費/E:次數型團體優惠代碼 要記錄團體優惠
			dtlVO.setGROUP_OFA(inputVO.getGroupOfa());// 團體優惠代碼(DefaultFeeRateVO)
		}	
		
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
		SOT1610InputVO inputVO = (SOT1610InputVO) body;
		SOT1610OutputVO outputVO = new SOT1610OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//若有申請議價，則送出覆核
		callCRM421ApplySingle(inputVO.getTradeSEQ());
		
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null != vo) {
			vo.setTRADE_STATUS("2"); // 2:風控檢核中
			dam.update(vo);
		}

		//檢核電文
		inputVO.setConfirm("1"); 
		String errMsg = dynamicESBValidate(inputVO);
		outputVO.setErrorMsg(errMsg);
		
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
		SOT1610InputVO inputVO = (SOT1610InputVO) body;
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		inputVO = (SOT1610InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1610OutputVO outputVO = new SOT1610OutputVO();
		dam = this.getDataAccessManager();

		//更新錄音序號
		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			mainVo.setREC_SEQ(inputVO.getRecSEQ());//錄音序號
		}
		dam.update(mainVo);

		//確認電文
		inputVO.setConfirm(""); 
		String errMsg = dynamicESBValidate(inputVO);
		if (StringUtils.isNotBlank(errMsg)) {
			outputVO.setErrorMsg(errMsg);
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
		SOT1610InputVO inputVO = (SOT1610InputVO) body;
		SOT1610OutputVO outputVO = new SOT1610OutputVO();
		dam = this.getDataAccessManager();

		//刪除明細檔資料
		TBSOT_NF_PURCHASE_DYNAPK dPK = new TBSOT_NF_PURCHASE_DYNAPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(new BigDecimal(1)); //固定為1
		TBSOT_NF_PURCHASE_DYNAVO dVO = new TBSOT_NF_PURCHASE_DYNAVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_PURCHASE_DYNAVO) dam.findByPKey(TBSOT_NF_PURCHASE_DYNAVO.TABLE_UID, dVO.getcomp_id());
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
	public String dynamicESBValidate(SOT1610InputVO inputVO) throws Exception {
		SOT703InputVO inputVO703 = new SOT703InputVO();
		SOT703OutputVO outputVO703 = new SOT703OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		inputVO703.setTradeSeq(inputVO.getTradeSEQ());
		inputVO703.setConfirm(inputVO.getConfirm());

		SOT703Dyna sot703 = (SOT703Dyna) PlatformContext.getBean("sot703dyna");
		outputVO703 = sot703.verifyESBPurchaseNFDYNA(inputVO703); //動態鎖利特金交易驗證電文

		String errorMsg = outputVO703.getErrorMsg();
		if (StringUtils.equals("1", inputVO.getConfirm()) && StringUtils.isNotBlank(errorMsg)) {
			//檢核電文失敗才刪除資料
			TBSOT_NF_PURCHASE_DYNAPK errPK = new TBSOT_NF_PURCHASE_DYNAPK();
			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			errPK.setSEQ_NO(inputVO.getSeqNo());
			TBSOT_NF_PURCHASE_DYNAVO errVO = new TBSOT_NF_PURCHASE_DYNAVO();
			errVO.setcomp_id(errPK);
			errVO = (TBSOT_NF_PURCHASE_DYNAVO) dam.findByPKey(TBSOT_NF_PURCHASE_DYNAVO.TABLE_UID, errVO.getcomp_id());

			//刪除明細
			if (null != errVO) {
				dam.delete(errVO);
			}

			//===若無明細，則將主檔也刪除
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBSOT_NF_PURCHASE_DYNA WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

				dam.delete(mVO);
			}   
		}
		
		return StringUtils.isNotBlank(errorMsg) ? outputVO703.getErrorCode() + ":" + errorMsg : "";  
	}
	
	/**
	 * 相當於議價的加入清單; 存檔並送單次議價檢核電文
	 * 
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private CRM421OutputVO applyBargain(SOT1610InputVO inputVO) throws Exception {
		CRM421OutputVO crm421OutputVO = new CRM421OutputVO();
		CRM421InputVO crm421InputVO = new CRM421InputVO();

		//initial data
		crm421InputVO.setApply_type("6"); //基金動態鎖利
		crm421InputVO.setProd_type("1"); //基金
		crm421InputVO.setCust_id(inputVO.getCustID());
		crm421InputVO.setProd_id(inputVO.getProdId());
		crm421InputVO.setProd_name(inputVO.getProdName());
		crm421InputVO.setTrustCurrType(inputVO.getTrustCurrType());
		crm421InputVO.setTrust_curr(inputVO.getTrustCurr());
		crm421InputVO.setPurchase_amt(inputVO.getPurchaseAmt().toString());
		crm421InputVO.setDefaultFeeRate(inputVO.getDefaultFeeRate());
		crm421InputVO.setFee(inputVO.getFee().toString());
		crm421InputVO.setFee_discount(inputVO.getFeeDiscount());
		crm421InputVO.setFee_rate(inputVO.getFeeRate());
		crm421InputVO.setDiscount_type("1"); //by費率			
		crm421InputVO.setBrg_reason(inputVO.getBrgReason()); //議價原因	
		crm421InputVO.setCon_degree(getCustConDegree(inputVO.getCustID()));
		crm421InputVO.setTrustTS(inputVO.getTrustTS());
		//議價時，先取得最高授權等級之前要先設定最低折數  (Highest_auth_lv 變數共用)
		crm421InputVO.setHighest_auth_lv(inputVO.getFeeDiscount().toString());

		CRM421 crm421 = (CRM421) PlatformContext.getBean("crm421");
		//再取得最高層級授權主管 (Highest_auth_lv 變數共用) 
		CRM421OutputVO crmoutput = crm421.getHighest_auth_lv(crm421InputVO);
		List<Map<String, Object>> mapList = crmoutput.getHighest_lvList();
		if (CollectionUtils.isNotEmpty(mapList)) {
			crm421InputVO.setHighest_auth_lv(mapList.get(0).get("HIGHEST_LV").toString());
		} else {
			crm421InputVO.setHighest_auth_lv("");
		}

		//相當於議價的加入清單
		crm421OutputVO = crm421.addToList(crm421InputVO);

		return crm421OutputVO;
	}
	
	/**
	 * 取得客戶貢獻度等級
	 * 
	 * @param custId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getCustConDegree(String custId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CON_DEGREE from TBCRM_CUST_MAST ");
		sb.append("WHERE CUST_ID = :custID ");
		queryCondition.setObject("custID", custId);
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> conlist = dam.exeQuery(queryCondition);
		String conDegree = "";
		if (CollectionUtils.isNotEmpty(conlist)) {
			conDegree = (String) conlist.get(0).get("CON_DEGREE");
		}

		return conDegree;
	}
	
	/**
	 * 議價鍵機，送出覆核
	 * 
	 * @param tradeSeq
	 * @throws Exception
	 */
	private void callCRM421ApplySingle(String tradeSeq) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT '1' as APPLY_TYPE, PROD_NAME, BARGAIN_APPLY_SEQ as APPLY_SEQ ");
		sb.append("FROM TBSOT_NF_PURCHASE_DYNA WHERE TRADE_SEQ = :tradeSEQ and FEE_TYPE = 'A' ");
		queryCondition.setObject("tradeSEQ", tradeSeq);
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (CollectionUtils.isNotEmpty(list)) {
			CRM421InputVO crm421InputVO = new CRM421InputVO();
			crm421InputVO.setList(list);

			CRM421 crm421 = (CRM421) PlatformContext.getBean("crm421");
			crm421.applySingle(crm421InputVO);
		}
	}
	
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getHnwcRiskValue(SOT1610InputVO inputVO) throws Exception {
		//若檢核不通過，產生錯誤訊息
		String errorMsg = "";
		//高資產客戶且客戶風險屬性非C4，有選擇越級商品，才做投組越級檢核	
		if(!(!StringUtils.equals("C4", inputVO.getKycLV())
				&& StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN())
				&& ((Integer.parseInt(inputVO.getProdRiskLv().substring(1))) > (Integer.parseInt(inputVO.getKycLV().substring(1)))
						|| (StringUtils.isNotBlank(inputVO.getProdCurrC1()) && (Integer.parseInt(inputVO.getProdRiskLvC1().substring(1))) > (Integer.parseInt(inputVO.getKycLV().substring(1))))
						|| (StringUtils.isNotBlank(inputVO.getProdCurrC2()) && (Integer.parseInt(inputVO.getProdRiskLvC2().substring(1))) > (Integer.parseInt(inputVO.getKycLV().substring(1))))
						|| (StringUtils.isNotBlank(inputVO.getProdCurrC3()) && (Integer.parseInt(inputVO.getProdRiskLvC3().substring(1))) > (Integer.parseInt(inputVO.getKycLV().substring(1)))))
				)) {
			return errorMsg; //非高資產客戶或客戶風險屬性為C4，或沒有選擇越級商品，不做投組越級檢核，回傳空值	
		}
		
		//取得這次申購商品信託幣別買匯
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		BigDecimal rate = sot110.getBuyRate(inputVO.getTrustCurr());
		//取得高資產客戶註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCustID());
		
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		inputVO714.setCUST_KYC(inputVO.getKycLV()); //客戶C值
		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(getMaxPVal(inputVO)); //商品風險檢核值
		BigDecimal amtBuy = inputVO.getPurchaseAmt().multiply(rate);//母基金這次申購金額折台
		BigDecimal amtBuyC1 = inputVO.getPurchaseAmtC1() == null ? BigDecimal.ZERO : inputVO.getPurchaseAmtC1().multiply(rate);//子基金1這次申購金額折台
		BigDecimal amtBuyC2 = inputVO.getPurchaseAmtC2() == null ? BigDecimal.ZERO : inputVO.getPurchaseAmtC2().multiply(rate);//子基金2這次申購金額折台
		BigDecimal amtBuyC3 = inputVO.getPurchaseAmtC3() == null ? BigDecimal.ZERO : inputVO.getPurchaseAmtC3().multiply(rate);//子基金3這次申購金額折台
		
		for(int i = 1; i <= 4; i++) {
			String pVal = "P" + String.valueOf(i);
			//以這次申購商品風險檢核值判斷是否需要加上這次申購金額
			BigDecimal amt = StringUtils.equals(pVal, inputVO.getProdRiskLv()) ? amtBuy : BigDecimal.ZERO;
			BigDecimal amt1 = StringUtils.equals(pVal, inputVO.getProdRiskLvC1()) ? amtBuyC1 : BigDecimal.ZERO;
			BigDecimal amt2 = StringUtils.equals(pVal, inputVO.getProdRiskLvC2()) ? amtBuyC2 : BigDecimal.ZERO;
			BigDecimal amt3 = StringUtils.equals(pVal, inputVO.getProdRiskLvC3()) ? amtBuyC3 : BigDecimal.ZERO;
			//這次申購金額加上購物車中各商品P值申購金額
			switch(pVal) {
				case "P1":
					inputVO714.setAMT_BUY_1(amt.add(amt1).add(amt2).add(amt3)); 
					break;
				case "P2":
					inputVO714.setAMT_BUY_2(amt.add(amt1).add(amt2).add(amt3));
					break;
				case "P3":
					inputVO714.setAMT_BUY_3(amt.add(amt1).add(amt2).add(amt3));
					break;
				case "P4":
					inputVO714.setAMT_BUY_4(amt.add(amt1).add(amt2).add(amt3));
					break;
			}
		}
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		if(StringUtils.equals("N", riskValData.getVALIDATE_YN())) {
			if(StringUtils.equals(inputVO.getProdRiskLv(), inputVO.getProdRiskLvC1()) &&
					(StringUtils.isNotBlank(inputVO.getProdRiskLvC2()) && StringUtils.equals(inputVO.getProdRiskLvC1(), inputVO.getProdRiskLvC2())) &&
					(StringUtils.isNotBlank(inputVO.getProdRiskLvC3()) && StringUtils.equals(inputVO.getProdRiskLvC2(), inputVO.getProdRiskLvC3()))) {
				//若母子基金本次申購商品P值都相同)
				BigDecimal leftAmt = BigDecimal.ZERO;
  				switch(inputVO.getProdRiskLv()) {
  					case "P1":
  						break;
  					case "P2":
  						leftAmt = riskValData.getAMT_LEFT_2();
  						break;
  					case "P3":
  						leftAmt = riskValData.getAMT_LEFT_3();
  						break;
  					case "P4":
  						leftAmt = riskValData.getAMT_LEFT_4();
  						break;
  				}
  				BigDecimal leftAmtCurr = leftAmt.divide(rate, 2, RoundingMode.HALF_UP); //原幣(信託幣別)金額
  				DecimalFormat df = new DecimalFormat("#,###.00");
  				errorMsg = "高資產客戶越級金額需低於" + inputVO.getTrustCurr() + df.format(leftAmtCurr);
			} else {
				errorMsg = "客戶投資組合風險權值已超標，可至「投組適配試算功能」重新試算";
			}
		}
		
		return errorMsg;
	}
	
	/***
	 * 取得母子基金商品中最大的風險值
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getMaxPVal(SOT1610InputVO inputVO) throws Exception {
		String maxPval = inputVO.getProdRiskLv();
		
		try {
			//母基金
			int PVal = Integer.parseInt(inputVO.getProdRiskLv().substring(1)); 
			//子基金1
			if(StringUtils.isNotBlank(inputVO.getProdRiskLvC1())) { 
				if(Integer.parseInt(inputVO.getProdRiskLvC1().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getProdRiskLvC1().substring(1));
				}
			}
			//子基金2
			if(StringUtils.isNotBlank(inputVO.getProdRiskLvC2())) { 
				if(Integer.parseInt(inputVO.getProdRiskLvC2().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getProdRiskLvC2().substring(1));
				}
			}
			//子基金3
			if(StringUtils.isNotBlank(inputVO.getProdRiskLvC3())) {
				if(Integer.parseInt(inputVO.getProdRiskLvC3().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getProdRiskLvC3().substring(1));
				}
			}
			
			maxPval = "P" + String.valueOf(PVal);
		} catch(Exception e) {}
		
		return maxPval;
	}
}