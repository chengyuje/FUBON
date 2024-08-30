package com.systex.jbranch.app.server.fps.sot420;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustNoteDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot708.SOT708;
import com.systex.jbranch.app.server.fps.sot708.SOT708InputVO;
import com.systex.jbranch.app.server.fps.sot708.SOT708OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712OutputVO;
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
 * sot420
 * 
 * @author valentino
 * @date 2016/10/11
 * @spec SI委託贖回
 */
@Component("sot420")
@Scope("request")
public class SOT420 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private static String TBSOT_DETAIL = "TBSOT_SI_TRADE_D"; //SI交易明細檔 

	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT420InputVO inputVO = (SOT420InputVO) body;
		SOT420OutputVO outputVO = new SOT420OutputVO();
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
		outputVO.setPiRemark(outputVO_701.getFp032675DataVO().getCustProRemark());//這個欄位會直接回存電文DESC欄位資料
		outputVO.setPiDueDate(outputVO_701.getFp032675DataVO().getCustProDate());
		outputVO.setCustRemarks(outputVO_701.getFp032675DataVO().getCustRemarks());
		outputVO.setIsOBU(outputVO_701.getFp032675DataVO().getObuFlag());
		outputVO.setIsAgreeProdAdv(("Y".equals(outputVO_701.getFp032675DataVO().getCustProFlag()) ? "Y" : (null != outputVO_701.getFp032675DataVO().getCustTxFlag() && "Y".equals(outputVO_701.getFp032675DataVO().getCustTxFlag().substring(0, 1)) ? "Y" : "N")));
		outputVO.setBargainDueDate(outputVO_701.getDueDate());
		outputVO.setPlNotifyWays(outputVO_701.getCustPLDataVO().getPlMsg());
		outputVO.setTakeProfitPerc(outputVO_701.getCustPLDataVO().getTakeProfitPerc());
		outputVO.setStopLossPerc(outputVO_701.getCustPLDataVO().getStopLossPerc());

		//組合式商品帳號
		List<AcctVO> prodAcct = outputVO_701.getCustAcctDataVO().getProdAcctList();
		List<Map<String, Object>> prodAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : prodAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));

			prodAcctList.add(map);
		}
		outputVO.setProdAcct(prodAcctList);

		//贖回款入帳帳號
		List<AcctVO> debitAcct = outputVO_701.getCustAcctDataVO().getDebitAcctList();
		List<Map<String, Object>> debitAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : debitAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DEBIT_ACCT", vo.getAvbBalance()); //贖回款入帳餘額
			debitAcctList.add(map);
		}
		outputVO.setDebitAcct(debitAcctList);

		outputVO.setNoSale(outputVO_701.getFp032675DataVO().getNoSale());
		outputVO.setDeathFlag(outputVO_701.getFp032675DataVO().getDeathFlag());

		CustNoteDataVO custNoteDataVO = outputVO_701.getCustNoteDataVO();
		if (custNoteDataVO == null) {
			custNoteDataVO = sot701.getCustNoteData(inputVO_701);
			outputVO.setIsInterdict((custNoteDataVO.getInterdict()) ? "Y" : "N");
		}
		outputVO.setRejectProdFlag(outputVO_701.getFp032675DataVO().getRejectProdFlag());

		this.sendRtnObject(outputVO);
	}

	//非常規交易
	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT420InputVO inputVO = (SOT420InputVO) body;
		SOT420OutputVO outputVO = new SOT420OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		Boolean isFirstTrade = false;
		
		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());
		
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);

		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag());  //年齡小於70
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag());            //教育程度國中以上(不含國中)
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag());          //未領有全民健保重大傷病證明
		
		
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO sot712InputVO = new SOT712InputVO();
		sot712InputVO.setCustID(inputVO.getCustID());
		outputVO.setCustAge(sot712.getCUST_AGE(sot712InputVO)); // 查客戶年齡 (未成年警語)

		this.sendRtnObject(outputVO);
	}

	// 取得商品資訊   贖回頁面需要取商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws JBranchException {

		SOT420InputVO inputVO = (SOT420InputVO) body;
		SOT420OutputVO outputVO = new SOT420OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// TODO: 1.適配邏輯SOT704→9/20小V  贖回需要適配? 
		boolean SOT706Status = true;

		// 2.查詢商品主檔
		if (SOT706Status) { // 適配條件符合
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT S.PRD_ID, S.SI_CNAME, S.RISKCATE_ID, S.CURRENCY_STD_ID, ");
			sb.append("SI.BASE_AMT_OF_PURCHASE, SI.UNIT_AMT_OF_PURCHASE, ");
			sb.append("SP.SDAMT1, SP.SDDTE ");
			sb.append("FROM TBPRD_SI S, TBPRD_SIINFO SI, TBPRD_SIPRICE SP ");
			sb.append("WHERE  S.PRD_ID = SI.PRD_ID ");
			sb.append("AND SI.PRD_ID = SP.SDPRD ");
			sb.append("AND S.PRD_ID = :prodID ");
			queryCondition.setObject("prodID", inputVO.getProdID());
			queryCondition.setQueryString(sb.toString());
			outputVO.setProdDTL(dam.exeQuery(queryCondition));
		}

		this.sendRtnObject(outputVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT420InputVO inputVO = (SOT420InputVO) body;
		SOT420OutputVO outputVO = new SOT420OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM " + TBSOT_DETAIL + " ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setCarList(dam.exeQuery(queryCondition));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT TRADE_SEQ, PROD_TYPE, TRADE_TYPE, CUST_ID, CUST_NAME, AGENT_ID, AGENT_NAME, KYC_LV, KYC_DUE_DATE, PROF_INVESTOR_YN, PI_DUE_DATE, CUST_REMARKS, IS_OBU, IS_AGREE_PROD_ADV, BARGAIN_DUE_DATE, TRADE_STATUS, IS_BARGAIN_NEEDED, BARGAIN_FEE_FLAG, IS_REC_NEEDED, REC_SEQ, SEND_DATE ");
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

		SOT420InputVO inputVO = (SOT420InputVO) body;
		SOT420OutputVO outputVO = new SOT420OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE("4"); //4：SI
			mainVO.setTRADE_TYPE("2"); //2:贖回
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLV());
			mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			mainVO.setPI_REMARK(inputVO.getPiRemark());//專業投資人註記
			mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
			mainVO.setTRADE_STATUS("1"); //1:暫存 
			mainVO.setIS_BARGAIN_NEEDED("N"); //TODO 待提供
			mainVO.setBARGAIN_FEE_FLAG(null); //TODO 待提供
			mainVO.setIS_REC_NEEDED("N"); //TODO 待提供
			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);

			/*
			 * About CR list ↓
			 * 	WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5
			 * 	WMS-CR-20190710-05_個人高端客群處業管系統_行銷模組調整申請_P1
			 * 
			 * Version ↓
			 * 	IvBrh from 庫存
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
			
			/*
			 * WMS-CR-20191009-01_金錢信託套表需求申請單
			 * 
			 * 2019-12-20 add by ocean
			 */
			mainVO.setTRUST_TRADE_TYPE("S");
			
			dam.create(mainVO);
		}

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
		TBSOT_SI_TRADE_DPK dtlPK = new TBSOT_SI_TRADE_DPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(newTrade_SEQNO);

		TBSOT_SI_TRADE_DVO dtlVO = new TBSOT_SI_TRADE_DVO();
		dtlVO.setcomp_id(dtlPK);
		dtlVO.setBATCH_SEQ(null); //下單批號
		dtlVO.setTRADE_SUB_TYPE("2"); //2：贖回	 
		dtlVO.setRECEIVED_NO(inputVO.getReceivedNo()); //收件編號
		dtlVO.setCERTIFICATE_ID(inputVO.getCertificateID()); //憑證編號	 
		dtlVO.setPROD_ID(inputVO.getProdID()); //商品代號 
		dtlVO.setPROD_NAME(inputVO.getProdName()); //商品名稱				 
		dtlVO.setPROD_CURR(inputVO.getProdCurr()); //計價幣別			 
		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLV()); //產品風險等級			 
		dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt()); //最低申購面額			 
		dtlVO.setPROD_MIN_GRD_AMT(inputVO.getProdMinGrdAmt()); //累計申購面額				 
		dtlVO.setREF_VAL(inputVO.getRefVal()); //參考報價				 
		dtlVO.setREF_VAL_DATE(null != inputVO.getRefValDate() ? new Timestamp(inputVO.getRefValDate().getTime()) : null); //參考報價日期
		dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt()); //申購金額/庫存面額
		dtlVO.setENTRUST_TYPE(inputVO.getEntrustType()); //贖回方式			 
		dtlVO.setENTRUST_AMT(inputVO.getEntrustAmt()); //贖回限價				 
		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct()); //贖回款入帳帳號		 
		dtlVO.setPROD_ACCT(inputVO.getProdAcct()); //組合式商品帳號
		dtlVO.setTRADE_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime())); //交易日期		 
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名

		dam.create(dtlVO);

		// 更新批號
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		SOT712OutputVO outputVO_712 = new SOT712OutputVO();

		inputVO_712.setProdType("SI");
		inputVO_712.setTradeSeq(inputVO.getTradeSEQ());
		sot712.updateBatchSeq(inputVO_712);
		//

		// 確認電文
		SOT708InputVO inputVO_708 = new SOT708InputVO();
		SOT708OutputVO outputVO_708 = new SOT708OutputVO();

		inputVO_708.setCheckType("2"); //2:確認
		inputVO_708.setTradeSeq(inputVO.getTradeSEQ());

		SOT708 sot708 = (SOT708) PlatformContext.getBean("sot708");
		String errorMsg = null;
		try {
			sot708.verifyESBRedeemSI(inputVO_708);
		} catch (Exception e) {
			String errMsg = e.getMessage();
			SOT712 sot712a = (SOT712) PlatformContext.getBean("sot712");
			errMsg = sot712a.inquireErrorCode(e.getMessage());
			logger.error(String.format("SI贖回確認電文sot708.verifyESBRedeemSI錯誤:%s", errMsg), e);
			throw new JBranchException(String.format("SI贖回確認電文錯誤:%s", errMsg), e);
		}
		if (!"".equals(errorMsg) && null != errorMsg) {
			outputVO.setErrorMsg(errorMsg);
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