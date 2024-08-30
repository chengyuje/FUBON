package com.systex.jbranch.app.server.fps.sot320;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot701.SotUtils;
import com.systex.jbranch.app.server.fps.sot707.SOT707;
import com.systex.jbranch.app.server.fps.sot707.SOT707InputVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sot320
 * 
 * @author ocean
 * @date 2016/10/07
 * @spec 海外債債券委託贖回
 */
@Component("sot320")
@Scope("request")
public class SOT320 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;

	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT320InputVO inputVO = (SOT320InputVO) body;
		SOT320OutputVO outputVO = new SOT320OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		SOT701OutputVO outputVO_701 = new SOT701OutputVO();

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		outputVO_701 = sot701.getSOTCustInfo(inputVO_701);

		outputVO.setCustName(outputVO_701.getFp032675DataVO().getCustName());
		outputVO.setKycLV(outputVO_701.getCustKYCDataVO().getKycLevel());
		outputVO.setKycDueDate(outputVO_701.getCustKYCDataVO().getKycDueDate());
		outputVO.setProfInvestorYN(outputVO_701.getFp032675DataVO().getCustProFlag());
		outputVO.setPiDueDate(outputVO_701.getFp032675DataVO().getCustProDate());
		outputVO.setPiRemark(outputVO_701.getFp032675DataVO().getCustProRemark());
		outputVO.setCustRemarks(outputVO_701.getFp032675DataVO().getCustRemarks());
		outputVO.setIsOBU(outputVO_701.getFp032675DataVO().getObuFlag());
		outputVO.setIsAgreeProdAdv(outputVO_701.getFp032675DataVO().getCustTxFlag());
		outputVO.setBargainDueDate(outputVO_701.getDueDate());
		outputVO.setPlNotifyWays(outputVO_701.getCustPLDataVO().getPlMsg());
		outputVO.setTakeProfitPerc(outputVO_701.getCustPLDataVO().getTakeProfitPerc());
		outputVO.setStopLossPerc(outputVO_701.getCustPLDataVO().getStopLossPerc());
		outputVO.setCustProType(outputVO_701.getFp032675DataVO().getCustProType());

		List<AcctVO> debitAcct = outputVO_701.getCustAcctDataVO().getDebitAcctList();
		List<Map<String, Object>> debitAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : debitAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DEBIT_ACCT", vo.getAvbBalance());
			map.put("CURRENCY", vo.getCurrency());
			debitAcctList.add(map);
		}
		outputVO.setDebitAcct(debitAcctList);

		List<AcctVO> trustAcct = outputVO_701.getCustAcctDataVO().getTrustAcctList();
		List<Map<String, Object>> trustAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : trustAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));

			trustAcctList.add(map);
		}
		outputVO.setTrustAcct(trustAcctList);

		List<AcctVO> creditAcct = outputVO_701.getCustAcctDataVO().getCreditAcctList();
		List<Map<String, Object>> creditAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : creditAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			//map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()) + "_" + vo.getCurrency());
			//map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()) + "_" + vo.getCurrency());
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("CURRENCY", vo.getCurrency());
			creditAcctList.add(map);
		}
		outputVO.setCreditAcct(creditAcctList);

		outputVO.setNoSale(outputVO_701.getFp032675DataVO().getNoSale());
		outputVO.setDeathFlag(outputVO_701.getFp032675DataVO().getDeathFlag());
		outputVO.setIsInterdict((null != outputVO_701.getCustNoteDataVO() && outputVO_701.getCustNoteDataVO().getInterdict()) ? "Y" : "N");
		outputVO.setRejectProdFlag(outputVO_701.getFp032675DataVO().getRejectProdFlag());

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO sot712InputVO = new SOT712InputVO();
		sot712InputVO.setCustID(inputVO.getCustID());
		outputVO.setCustAge(sot712.getCUST_AGE(sot712InputVO)); //查客戶年齡 (未成年警語)

		this.sendRtnObject(outputVO);
	}

	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws JBranchException {

		SOT320InputVO inputVO = (SOT320InputVO) body;
		SOT320OutputVO outputVO = new SOT320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// TODO: 1.適配邏輯SOT704→9/20小V
		boolean SOT704Status = true;
		// 2.查詢商品主檔
		if (SOT704Status) { // 適配條件符合
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT B.PRD_ID, B.BOND_CNAME, B.RISKCATE_ID, B.CURRENCY_STD_ID, ");
			sb.append("	BI.BASE_AMT_OF_PURCHASE, BI.UNIT_OF_VALUE, ");
			sb.append("	BP.BUY_PRICE, BP.BARGAIN_DATE, LIMIT_P.LIMITED_PRICE ");
			sb.append("	FROM TBPRD_BOND B ");
			sb.append(" 	left outer join TBPRD_BONDINFO BI on BI.PRD_ID = B.PRD_ID ");
			sb.append(" 	left outer join TBPRD_BONDPRICE BP on BP.PRD_ID = B.PRD_ID ");
			sb.append(" 	left outer join TBPRD_LIMITED_PRICE LIMIT_P on LIMIT_P.PRD_ID = B.PRD_ID AND LIMIT_P.REVIEW_STATUS = 'Y' AND LIMIT_P.CUST_ID = :custID ");
			sb.append("	WHERE B.PRD_ID = :prodID ");
			queryCondition.setObject("prodID", inputVO.getProdID());
			queryCondition.setObject("custID", inputVO.getCustID());
			queryCondition.setQueryString(sb.toString());

			outputVO.setProdDTL(dam.exeQuery(queryCondition));
		}

		this.sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT320InputVO inputVO = (SOT320InputVO) body;
		SOT320OutputVO outputVO = new SOT320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT TRADE_SEQ, SEQ_NO, BATCH_SEQ, TRADE_SUB_TYPE, CERTIFICATE_ID, PROD_ID, PROD_NAME, PROD_CURR, PROD_RISK_LV, ");
		sb.append("		PROD_MIN_BUY_AMT, PROD_MIN_GRD_AMT, TRUST_CURR_TYPE, TRUST_CURR, TRUST_UNIT, MARKET_TYPE, REF_VAL, REF_VAL_DATE, ");
		sb.append("		PURCHASE_AMT, ENTRUST_TYPE, ENTRUST_AMT, TRUST_AMT, DEFAULT_FEE_RATE, ADV_FEE_RATE, BARGAIN_APPLY_SEQ, FEE_RATE, ");
		sb.append("		FEE, FEE_DISCOUNT, PAYABLE_FEE, TOT_AMT, DEBIT_ACCT, TRUST_ACCT, CREDIT_ACCT, TRADE_DATE, NARRATOR_ID, NARRATOR_NAME, ");
		sb.append("		GTC_YN, ");
		// TODO 上[2080]拆[2056]
//		sb.append("		GTC_START_DATE, ");
		sb.append("		GTC_END_DATE ");
		sb.append("FROM TBSOT_BN_TRADE_D ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setCarList(dam.exeQuery(queryCondition));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT TRADE_SEQ, PROD_TYPE, TRADE_TYPE, CUST_ID, CUST_NAME, AGENT_ID, AGENT_NAME, KYC_LV, KYC_DUE_DATE, PROF_INVESTOR_YN, PI_DUE_DATE, PI_REMARK, CUST_REMARKS, IS_OBU, IS_AGREE_PROD_ADV, BARGAIN_DUE_DATE, TRADE_STATUS, IS_BARGAIN_NEEDED, BARGAIN_FEE_FLAG, IS_REC_NEEDED, REC_SEQ, SEND_DATE ");
		sb.append("FROM TBSOT_TRADE_MAIN ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setMainList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	//傳送OP交易並列印表單
	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		SOT320InputVO inputVO = (SOT320InputVO) body;
		SOT320OutputVO outputVO = new SOT320OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

		if (StringUtils.indexOf(inputVO.getCreditAcct(), "_") > 0)
			inputVO.setCreditAcct(StringUtils.split(inputVO.getCreditAcct(), "_")[0]);//去除帳號幣別
		
		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE("3"); //3：海外債
			mainVO.setTRADE_TYPE("2"); //2:贖回
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLV());
			mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);
			mainVO.setPI_REMARK(inputVO.getPiRemark());
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
			mainVO.setTRADE_STATUS("1"); //1:暫存
			mainVO.setIS_BARGAIN_NEEDED("N");
			mainVO.setBARGAIN_FEE_FLAG(null);

			/*贖回不須錄音序號
			 * SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO inputVO_712 = new SOT712InputVO();
			inputVO_712.setCustID(inputVO.getCustID());
			inputVO_712.setProdID(null);
			inputVO_712.setProdType("BN");
			inputVO_712.setProfInvestorYN(inputVO.getProfInvestorYN());
			inputVO_712.setIsFirstTrade(TODO);
			inputVO_712.setCustRemarks(inputVO.getCustRemarks());
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N");
			*/
			mainVO.setIS_REC_NEEDED("N");//IS_REC_NEEDED 不能為NULL
			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);

			mainVO.setPROF_INVESTOR_TYPE(inputVO.getCustProType());

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
				SotUtils utils = (SotUtils) PlatformContext.getBean("sotUtils");
				mainVO.setBRANCH_NBR(utils.getBranchNbr(inputVO.getTrustTS(), String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH))));
			}

			//mantis #000104: WMS-CR-20191009-01_金錢信託套表需求申請單 2019-12-20 add by ocean
			mainVO.setTRUST_TRADE_TYPE(inputVO.getTrustTS());
			
			//0000275: 金錢信託受監護受輔助宣告交易控管調整
			if(StringUtils.isBlank(inputVO.getGUARDIANSHIP_FLAG())){
				mainVO.setGUARDIANSHIP_FLAG(" ");
			}else{
				mainVO.setGUARDIANSHIP_FLAG(inputVO.getGUARDIANSHIP_FLAG());
			}
			
			dam.create(mainVO);
		}

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
		TBSOT_BN_TRADE_DPK dtlPK = new TBSOT_BN_TRADE_DPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(newTrade_SEQNO);

		TBSOT_BN_TRADE_DVO dtlVO = new TBSOT_BN_TRADE_DVO();
		dtlVO.setcomp_id(dtlPK);
		dtlVO.setBATCH_SEQ(sot712.getSeq()); //下單批號
		dtlVO.setTRADE_SUB_TYPE("2"); //交易類型
		dtlVO.setCERTIFICATE_ID(inputVO.getCertificateID()); //憑證編號
		dtlVO.setPROD_ID(inputVO.getProdID()); //商品代號
		dtlVO.setPROD_NAME(inputVO.getProdName()); //商品名稱
		dtlVO.setPROD_CURR(inputVO.getProdCurr()); //計價幣別
		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLV()); //產品風險等級
		dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt()); //最低申購面額
		dtlVO.setPROD_MIN_GRD_AMT(inputVO.getProdMinGrdAmt()); //累計申購面額
		dtlVO.setTRUST_CURR_TYPE(inputVO.getTrustCurrType()); //信託幣別類型
		dtlVO.setTRUST_CURR(inputVO.getTrustCurr()); //信託幣別
		dtlVO.setTRUST_UNIT(inputVO.getTrustUnit()); //庫存張數
		dtlVO.setMARKET_TYPE("2"); //次級																						//債券市場種類
		dtlVO.setREF_VAL(inputVO.getRefVal()); //參考報價
		dtlVO.setREF_VAL_DATE(null != inputVO.getRefValDate() ? new Timestamp(inputVO.getRefValDate().getTime()) : null); //參考報價日期
		dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt()); //申購金額/庫存面額
		dtlVO.setENTRUST_TYPE(inputVO.getEntrustType()); //委託價格類型/贖回方式
		dtlVO.setENTRUST_AMT(StringUtils.equals("Y", inputVO.getGtcYN()) ? inputVO.getGtcRefVal() : inputVO.getEntrustAmt()); //長效單委託價格/當日單委託價格
		dtlVO.setTRUST_AMT(inputVO.getTrustAmt()); //信託本金
		dtlVO.setDEFAULT_FEE_RATE(inputVO.getDefaultFeeRate()); //表定手續費率
		dtlVO.setADV_FEE_RATE(null); //事先申請手續費率
		dtlVO.setBARGAIN_APPLY_SEQ(null);
		dtlVO.setFEE_RATE(inputVO.getFeeRate()); //手續費率/信託管理費率
		dtlVO.setFEE(inputVO.getFee()); //手續費金額/預估信託管理費
		dtlVO.setFEE_DISCOUNT(inputVO.getFeeDiscount()); //手續費折數
		dtlVO.setPAYABLE_FEE(inputVO.getPayableFee()); //應付前手息/應收前手息
		dtlVO.setTOT_AMT(inputVO.getTotAmt()); //總扣款金額/預估贖回入帳金額
		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct()); //扣款帳號
		dtlVO.setTRUST_ACCT(inputVO.getTrustAcct()); //信託帳號
		dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct()); //收益入帳帳號/贖回款入帳帳號
		dtlVO.setTRADE_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime())); //交易日期
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名
		dtlVO.setBOND_VALUE(inputVO.getBondVal());
		dtlVO.setGTC_YN(inputVO.getGtcYN()); //長效單Y 當日單N
		//TODO 上[2080]拆[2056]
//		dtlVO.setGTC_START_DATE(null != inputVO.getGtcStartDate() ? new Timestamp(inputVO.getGtcStartDate().getTime()) : null); //長效單起日
		dtlVO.setGTC_END_DATE(null != inputVO.getGtcEndDate() ? new Timestamp(inputVO.getGtcEndDate().getTime()) : null); 		//長效單迄日
		
		//mantis #000104: WMS-CR-20191009-01_金錢信託套表需求申請單 2019-12-20 add by ocean
		dtlVO.setCONTRACT_ID(inputVO.getContractID());
		dtlVO.setTRUST_PEOP_NUM(StringUtils.isNotEmpty(inputVO.getTrustPeopNum()) ? inputVO.getTrustPeopNum() : "N");
				
		dam.create(dtlVO);

		// 更新批號
		//		SOT712InputVO inputVO_712 = new SOT712InputVO();
		//		SOT712OutputVO outputVO_712 = new SOT712OutputVO();
		//		
		//		inputVO_712.setProdType("BN");
		//		inputVO_712.setTradeSeq(inputVO.getTradeSEQ());
		//		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		//		sot712.updateBatchSeq(inputVO_712);
		//

		// 確認電文
		// TODO 上[2080]拆[2090]
//		String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707 = new SOT707OutputVO();
		inputVO_707.setProdType("2");
		inputVO_707.setCheckType("2");
		inputVO_707.setTradeSeq(inputVO.getTradeSEQ());
		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
		
		// TODO 上[2080]拆[2090]
//		if (isOBU.equals("Y")) {
//			if ("Y".equals(inputVO.getGtcYN()))
//				outputVO_707 = sot707.verifyESBRedeemBN_GTC_OBU(inputVO_707);	// 長效單
//			else
//				outputVO_707 = sot707.verifyESBRedeemBN_OBU(inputVO_707); 		// 當日單
//		} else {
			if ("Y".equals(inputVO.getGtcYN()))
				outputVO_707 = sot707.verifyESBRedeemBN_GTC(inputVO_707);		// 長效單
			else
				outputVO_707 = sot707.verifyESBRedeemBN(inputVO_707);			// 當日單			
//		}

		String errorMsg = outputVO_707.getErrorMsg();
		outputVO.setWarningCode(outputVO_707.getWarningCode());
		if (!"".equals(errorMsg) && null != errorMsg) {
			outputVO.setErrorMsg(errorMsg);
			dam.delete(dtlVO);// 要刪除該筆 ， 不然SOT703會撈到重覆
		} else {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
			mainVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

			mainVO.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單
			mainVO.setSEND_DATE(new Timestamp(System.currentTimeMillis())); //傳送OP後要設SEND_DATE
			dam.update(mainVO);

			// for CBS 測試用
			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(mainVO.getTRADE_SEQ());
		}

		this.sendRtnObject(outputVO);
	}

	private String getSeqNum(String TXN_ID, String format, Timestamp timeStamp, Integer minNum, Long maxNum, String status, Long nowNum) throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();

		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber(TXN_ID);
		} catch (Exception e) {
			sn.createNewSerial(TXN_ID, format, 1, "d", timeStamp, minNum, maxNum, status, nowNum, null);
			seqNum = sn.getNextSerialNumber(TXN_ID);
		}
		return seqNum;
	}

}