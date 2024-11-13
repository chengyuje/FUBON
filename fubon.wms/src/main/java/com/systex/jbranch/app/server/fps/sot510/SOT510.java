package com.systex.jbranch.app.server.fps.sot510;

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.common.fps.table.TBSOT_SN_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_SN_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.prd140.PRD140;
import com.systex.jbranch.app.server.fps.prd140.PRD140InputVO;
import com.systex.jbranch.app.server.fps.prd140.PRD140OutputVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot410.SOT410InputVO;
import com.systex.jbranch.app.server.fps.sot701.*;
import com.systex.jbranch.app.server.fps.sot707.SOT707;
import com.systex.jbranch.app.server.fps.sot707.SOT707InputVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707OutputVO;
import com.systex.jbranch.app.server.fps.sot711.SOT711;
import com.systex.jbranch.app.server.fps.sot711.SOT711InputVO;
import com.systex.jbranch.app.server.fps.sot711.SOT711OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sot210
 *
 * @author Kent
 * @date 2016/09/15
 * @spec SN申購
 */
@Component("sot510")
@Scope("request")
public class SOT510 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;

	//取得客戶資料
	public void getSOTCustInfo (Object body,IPrimitiveMap header) throws JBranchException, Exception {

//		try{
			SOT510InputVO inputVO =(SOT510InputVO) body;
			SOT510OutputVO outputVO = new SOT510OutputVO();
			SOT701InputVO input_701 = new SOT701InputVO();
			SOT701OutputVO output_701 = new SOT701OutputVO();

			input_701.setCustID(inputVO.getCustID());
			input_701.setProdType(inputVO.getProdType());
			input_701.setTradeType(inputVO.getTradeType());
			
			input_701.setNeedFlagNumber(true); 
			input_701.setTrustTS(inputVO.getTrustTS());

			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			output_701 = sot701.getSOTCustInfo(input_701);
			FP032675DataVO Fp032675 = output_701.getFp032675DataVO();
			CustKYCDataVO kyc = output_701.getCustKYCDataVO();
			CustAcctDataVO acc = output_701.getCustAcctDataVO();
			CustNoteDataVO note = output_701.getCustNoteDataVO();

			//FOR CBS測試日期修改
			outputVO.setKycDueDateUseful(output_701.getCustKYCDataVO().isKycDueDateUseful());
			outputVO.setPiDueDateUseful(output_701.getFp032675DataVO().isPiDueDateUseful());
			if(acc != null){
				List<Map<String,Object>> debitList = new ArrayList();
				for(AcctVO vo : acc.getDebitAcctList()){
					Map map = new HashMap();
					map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo())+"_"+vo.getCurrency());
					map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo())+"_"+vo.getCurrency());
					map.put("DEBIT_ACCT", vo.getAvbBalance());
					map.put("CURRENCY", vo.getCurrency());
					debitList.add(map);
				}
				List<Map<String,Object>> trustList = new ArrayList();
				for(AcctVO vo : acc.getTrustAcctList()){
					Map map = new HashMap();
					map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
					map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
					trustList.add(map);
				}
				List<Map<String,Object>> creditList = new ArrayList();
				for(AcctVO vo : acc.getCreditAcctList()){
					Map map = new HashMap();
					map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
					map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
					map.put("DEBIT_ACCT", vo.getAvbBalance());
					map.put("CURRENCY", vo.getCurrency());
					creditList.add(map);
				}
				outputVO.setDebitAcct(debitList);
				outputVO.setTrustAcct(trustList);
				outputVO.setCreditAcct(creditList);
			}

//			outputVO.setIsCustStackholder(sot701.isCustStakeholder(input_701));
//			outputVO.setCustQValue(sot701.getCustQValue(input_701));
			if(Fp032675 != null){
				outputVO.setPiRemark(Fp032675.getCustProRemark());
				outputVO.setCustProDate(Fp032675.getCustProDate());
				outputVO.setCustID(Fp032675.getCustID());
				outputVO.setNoSale(Fp032675.getNoSale());
				outputVO.setDeathFlag(Fp032675.getDeathFlag());
				outputVO.setRejectProdFlag(Fp032675.getRejectProdFlag());
				outputVO.setCustName(Fp032675.getCustName());
				outputVO.setCustRemarks(Fp032675.getCustRemarks());
				outputVO.setOutFlag(Fp032675.getObuFlag());
				outputVO.setProfInvestorYN(Fp032675.getCustProFlag());
				outputVO.setPiDueDate(Fp032675.getCustProDate());
				outputVO.setCustProType(Fp032675.getCustProType());
			}
			if(kyc != null){
				outputVO.setKycLevel(kyc.getKycLevel());
				outputVO.setKycDueDate(kyc.getKycDueDate());
			}
			if(note !=null){
				outputVO.setIsInterdict(note.getInterdict());//驗證用
			}
			
			//查詢客戶高資產註記資料
			SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
			CustHighNetWorthDataVO hNWCDataVO = sot714.getHNWCData(inputVO.getCustID());
			outputVO.setHnwcYN(StringUtils.isNotBlank(hNWCDataVO.getValidHnwcYN()) ? hNWCDataVO.getValidHnwcYN() : "N");
			outputVO.setHnwcServiceYN(hNWCDataVO.getHnwcService());
			
			outputVO.setFlagNumber(output_701.getFlagNumber());
			
			this.sendRtnObject(outputVO);
//		}catch(Exception e){
//			logger.debug(e.getMessage(),e);
////			throw new APException(ml_idGroup_s);
//		}
	}

	// 取得商品資訊
	public void getProdDTL (Object body, IPrimitiveMap header) throws JBranchException, Exception {
			SOT510InputVO inputVO = (SOT510InputVO) body;
			SOT510OutputVO outputVO = new SOT510OutputVO();
			SOT707OutputVO output707VO = new SOT707OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);


			boolean isFitness = StringUtils.equals("N", (String) new XmlInfo().getVariable("SOT.FITNESS_YN", "SN", "F3")) ? false : true;

			outputVO.setWarningMsg("");
			outputVO.setErrorMsg("");
			//適配
			boolean isFitnessOK = false;
			if (isFitness) {
				// 1.適配，由商品查詢取得，邏輯需一致
				PRD140OutputVO prdOutputVO = new PRD140OutputVO();
				PRD140InputVO prdInputVO =  new PRD140InputVO();
				prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
				prdInputVO.setSn_id(inputVO.getProdID());
				prdInputVO.setType("4");
				prdInputVO.setTrustTS(inputVO.getTrustTS());

				PRD140 prd140 = (PRD140) PlatformContext.getBean("prd140");
				prdOutputVO = prd140.inquire(prdInputVO);
				if(CollectionUtils.isNotEmpty(prdOutputVO.getResultList())) {
					outputVO.setBondVal((BigDecimal)((Map<String,Object>) prdOutputVO.getResultList().get(0)).get("BOND_VALUE"));//由prd140.inquire抓取票面價值
					outputVO.setHnwcBuy((String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("HNWC_BUY"));//商品限高資產客戶申購註記
					String warningMsg = (String) ((Map<String,Object>) prdOutputVO.getResultList().get(0)).get("warningMsg");
					String errId = (String) ((Map<String,Object>) prdOutputVO.getResultList().get(0)).get("errorID");

					if (StringUtils.isBlank(errId)) {
						isFitnessOK = true;
					}

					outputVO.setWarningMsg(warningMsg);
					outputVO.setErrorMsg(errId);
				}
			}else{
				isFitnessOK = true;
			}

			if (isFitnessOK) { // 適配條件符合
				SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
				SOT707InputVO inputVO_707 = new SOT707InputVO();
				inputVO_707.setCustId(inputVO.getCustID());
				inputVO_707.setProdId(inputVO.getProdID());
				output707VO = sot707.getProdRefVal(inputVO_707);
				outputVO.setCustAssetBondList(output707VO.getCustAssetBondList());
				outputVO.setProdRefVal(output707VO.getProdRefVal());
				this.checkRisk(inputVO,outputVO);
			}

			this.sendRtnObject(outputVO);
	}

	private void checkRisk (SOT510InputVO inputVO, SOT510OutputVO outputVO) throws JBranchException {

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//此單已申購金額
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sq_sumBDS = new StringBuffer();
		sq_sumBDS.append("SELECT sum(NVL(BDABP,0)) AS BDS ");
		sq_sumBDS.append("FROM TBPMS_BDS120_DAY ");
		sq_sumBDS.append("WHERE BDAB6 = 'B' AND BDABF = '1' ");
		if (!StringUtils.isBlank(inputVO.getCustID())) {
			sq_sumBDS.append(" AND BDAB5 = :custID ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		if (!StringUtils.isBlank(inputVO.getProdID())) {
			sq_sumBDS.append(" AND BDAB3 = :prdID ");
			queryCondition.setObject("prdID", inputVO.getProdID());
		}
		queryCondition.setQueryString(sq_sumBDS.toString());
		List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);

		if (list1 != null && list1.size() > 0 && list1.get(0).get("BDS") != null) {
			outputVO.setSumBDS(new BigDecimal(list1.get(0).get("BDS").toString()));
		} else {
		    logger.debug("not found 'BDS'");
		}


		//前一日投資AUM
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sq_AUM = new StringBuffer();
		sq_AUM.append("select NVL(AMT_20,0) AS NVLA from MVCRM_AST_AMT ");
		if (!StringUtils.isBlank(inputVO.getCustID())) {
			sq_AUM.append(" where CUST_ID = :custID ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		queryCondition.setQueryString(sq_AUM.toString());
		List<Map<String, Object>> list2_AUM = dam.exeQuery(queryCondition);
		if (list2_AUM != null && list2_AUM.size() > 0 && list2_AUM.get(0).get("NVLA") != null) {
		outputVO.setNvlAMT(new BigDecimal(list2_AUM.get(0).get("NVLA").toString()));
		}else{
			logger.debug("not found 'NVLA'");
		}

		//SI已申購金額+SI庫存金額+SN已申購金額+SN在途申購金額+SN庫存金額
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sq_sumITEM = new StringBuffer();
		sq_sumITEM.append("select sum(NVL(BAL,0)) AS  SUMITEM ");
		sq_sumITEM.append("FROM TBPMS_CUST_DAY_PROFIT ");
		if (!StringUtils.isBlank(inputVO.getCustID())) {
			sq_sumITEM.append(" WHERE CUST_ID = :custID  ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		sq_sumITEM.append("AND ITEM in ('02','03','07','08') ");
		sq_sumITEM.append("AND DATA_DATE = (SELECT max(DATA_DATE) ");
		sq_sumITEM.append("FROM TBPMS_CUST_DAY_PROFIT ");
		if (!StringUtils.isBlank(inputVO.getCustID())) {
			sq_sumITEM.append(" where CUST_ID = :custID ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		sq_sumITEM.append(")");
		queryCondition.setQueryString(sq_sumITEM.toString());
		List<Map<String, Object>> list_ITEM = dam.exeQuery(queryCondition);
		if (list_ITEM != null && list_ITEM.size() > 0 && list_ITEM.get(0).get("SUMITEM") != null) {
			outputVO.setSumITEM(new BigDecimal(list_ITEM.get(0).get("SUMITEM").toString()));
		}else{
			logger.debug("not found 'SUMITEM'");
		}
	}

	//op檢核
	public void verifyTradeBond (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT510InputVO inputVO = (SOT510InputVO)body;
		SOT510OutputVO outputVO = new SOT510OutputVO();

		String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
	    SOT707InputVO input707 = new SOT707InputVO();
	    input707.setProdType(inputVO.getProdType());
	    input707.setTradeSeq(inputVO.getTradeSEQ());
	    input707.setCheckType(inputVO.getCheckType());
	    input707.setPurchaseAmt(inputVO.getPurchaseAmt());
	    input707.setIsOBU(isOBU);
	    
		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			mainVo.setREC_SEQ(inputVO.getRecSEQ());//錄音序號
		}
		dam.update(mainVo);

		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
		SOT707OutputVO outputVO_707 = sot707.verifyESBPurchaseBN(input707);
		
//		SOT707OutputVO outputVO_707 = new SOT707OutputVO();
//		if (isOBU.equals("Y")) {
//			outputVO_707 = sot707.verifyESBPurchaseBN_OBU(input707);
//		} else {
//			outputVO_707 = sot707.verifyESBPurchaseBN(input707);
//		}
		
		String errorMsg = outputVO_707.getErrorMsg();
		List<String> warningList = outputVO_707.getWarningCode();
		if (!"".equals(errorMsg) && null != errorMsg) {
			outputVO.setErrorMsg(errorMsg);
		}else {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

			vo.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單
//			vo.setIS_REC_NEEDED(inputVO.getIsRecNeeded()=="true"?"Y":"N");
//			vo.setREC_SEQ(inputVO.getRecSEQ());
			vo.setSEND_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime()));
			dam.update(vo);

			// for CBS 測試用
			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
		}

		//若有警示訊息
		if(CollectionUtils.isNotEmpty(warningList)) {
			outputVO.setWarningMsg("");
			XMLInfo xmlInfo = new XMLInfo();

			for(String msg : warningList) {
				outputVO.setWarningMsg(xmlInfo.getErrorMsg(msg) + "\n" + outputVO.getWarningMsg());
			}
		}

		this.sendRtnObject(outputVO);
	}

	//取消
	public void delTrade (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT510InputVO inputVO = (SOT510InputVO) body;
		SOT510OutputVO outputVO = new SOT510OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		try{
			sb.append("DELETE FROM TBSOT_SN_TRADE_D WHERE TRADE_SEQ = :tradeSEQ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
			if(vo != null){
				dam.delete(vo);
			}

			this.sendRtnObject(null);
		}catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
	}

	// 下一步
	public void next (Object body, IPrimitiveMap header) throws JBranchException, Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		SOT510InputVO inputVO = (SOT510InputVO) body;
		SOT510OutputVO outputVO = new SOT510OutputVO();
		
		String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
		
		//#0564 金錢信託SN 發送A2電文
		SOT711 sot711 = (SOT711) PlatformContext.getBean("sot711");
		SOT711InputVO sot711InputVO = new SOT711InputVO();
		sot711InputVO.setBondNo(inputVO.getProdID());
		sot711InputVO.setCustId("99331241");
		sot711InputVO.setEntrustAmt(inputVO.getRefVal());
		sot711InputVO.setPriceType("2");
		sot711InputVO.setPurchaseAmt(inputVO.getTrustAmt());
		sot711InputVO.setTrustAcct(inputVO.getTrustAcct());
		sot711InputVO.setTxFeeType("1");
		
//		SOT711OutputVO sot711OutputVO = sot711.getDefaultFeeRateData(sot711InputVO);
		SOT711OutputVO sot711OutputVO = new SOT711OutputVO();
		if (isOBU.equals("Y")) {
			sot711InputVO.setCustId(inputVO.getCustID());
			sot711OutputVO = sot711.getDefaultFeeRateDataOBU(sot711InputVO);
		} else {
			sot711OutputVO = sot711.getDefaultFeeRateData(sot711InputVO);
		}
		
		BigDecimal A2DefaultFeeRate = sot711OutputVO.getDefaultFeeRate();
		BigDecimal A2BestFeeRate = sot711OutputVO.getBestFeeRate();
		
		System.out.println("1: " + sot711OutputVO.getDefaultFeeRate());
		System.out.println("2: " + sot711OutputVO.getBestFeeRate());
		//#0564 金錢信託SN 發送X1電文
		SOT711InputVO sot711InputVO2 = new SOT711InputVO();
		sot711InputVO2.setBondNo(inputVO.getProdID());
		sot711InputVO2.setCustId(inputVO.getCustID());
		sot711InputVO2.setEntrustAmt(inputVO.getRefVal());
		sot711InputVO2.setPriceType("2");
		sot711InputVO2.setPurchaseAmt(inputVO.getTrustAmt());
		sot711InputVO2.setTrustAcct("for the pass");
		sot711InputVO2.setTxFeeType("1");
		
//		SOT711OutputVO sot711OutputVO2 = sot711.getDefaultFeeRateDataByTrust(sot711InputVO2);
		SOT711OutputVO sot711OutputVO2 = new SOT711OutputVO();
		if (isOBU.equals("Y")) {
			sot711OutputVO2 = sot711.getDefaultFeeRateDataOBU(sot711InputVO);
		} else {
			sot711OutputVO2 = sot711.getDefaultFeeRateData(sot711InputVO);
		}
		
		BigDecimal X1DefaultFeeRate = sot711OutputVO2.getDefaultFeeRate();
		BigDecimal X1BestFeeRate = sot711OutputVO2.getBestFeeRate();
		System.out.println("1: " + sot711OutputVO2.getDefaultFeeRate());
		System.out.println("2: " + sot711OutputVO2.getBestFeeRate());
		
		inputVO.setDefaultFeeRate(A2DefaultFeeRate);
		inputVO.setBestFeeRate(A2BestFeeRate);
		inputVO.setFeeRate(X1BestFeeRate);
		inputVO.setEntrustAmt(inputVO.getRefVal());
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer errorMsg = new StringBuffer();
	    List<String> warningList = new ArrayList<String>();
	    
	    //買匯匯率
	    SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
	    BigDecimal rate = sot110.getBuyRate(inputVO.getProdCurr());	
	  				
	  	//高資產客戶投組風險權值檢核
	    //只限特金，客戶風險屬性非C4，有選擇越級商品
		if(!StringUtils.equals("M", inputVO.getTrustTS()) && !StringUtils.equals("C4", inputVO.getKycLV())
				&& (Integer.parseInt(inputVO.getProdRiskLV().substring(1))) > (Integer.parseInt(inputVO.getKycLV().substring(1)))
				&& StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN())) {
	    	//高資產客戶投組風險權值檢核
			if(!validHnwcRiskValue(inputVO, rate)) {
				throw new APException("客戶加計此筆投資之越級比率已超標，請選擇其他標的或調整投資面額");
			}
  		}
	    
		//高資產客戶購買高風險商品或不保本商品，檢核集中度
		//#2027: WMS-CR-20240509-01_高資產集中度交易取消不保本境內外結構型商品檢核
//		if(StringUtils.equals("Y", inputVO.getHnwcYN()) && (StringUtils.equals("Y", inputVO.getHnwcBuy()) || !isRateGuaranteed(inputVO.getProdID()))) {
//			inputVO.setOverCentRateYN(getOverCentRateYN(inputVO, rate));
//			if(StringUtils.equals("Y", inputVO.getOverCentRateYN())) {
//				warningList.add("客戶高風險商品集中度已超過控管上限，請取得客戶同意及處主管核准");
//			}
//		}
				
        add(dam, queryCondition, inputVO);

        // 檢核電文
        SOT707InputVO inputVO_707 = new SOT707InputVO();
        SOT707OutputVO outputVO_707_S = new SOT707OutputVO();
        SOT707OutputVO outputVO_707_M = new SOT707OutputVO();

        inputVO_707.setProdType("1");
        inputVO_707.setCheckType("1");
        inputVO_707.setTradeSeq(inputVO.getTradeSEQ());
        inputVO_707.setPurchaseAmt(inputVO.getPurchaseAmt());

        SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");

        // 若為金錢信託，先 call NJBRVX2
        if (StringUtils.equals("M", inputVO.getTrustTS())) {
            outputVO_707_M = sot707.verifyESBPurchaseBN_TRUST(inputVO_707);
        }
        
//      outputVO_707_S = sot707.verifyESBPurchaseBN(inputVO_707);
		if (isOBU.equals("Y")) {
			outputVO_707_S = sot707.verifyESBPurchaseBN_OBU(inputVO_707);
		} else {
			outputVO_707_S = sot707.verifyESBPurchaseBN(inputVO_707);
		}

        // 金錢信託申請電文錯誤訊息
        if (StringUtils.equals("M", inputVO.getTrustTS())) {
            if (StringUtils.isEmpty(errorMsg.toString()) && (!"".equals(outputVO_707_M.getErrorMsg()) && null != outputVO_707_M.getErrorMsg())) {
                errorMsg.append(outputVO_707_M.getErrorMsg());
                logger.error("next() sot707.verifyESBPurchaseBN_TRUST:" + outputVO_707_M.getErrorMsg());
            }

            if (warningList.size() == 0 && (!"".equals(outputVO_707_M.getWarningCode()) && null != outputVO_707_M.getWarningCode())) {
                warningList.addAll(outputVO_707_M.getWarningCode());
                logger.error("next() sot707.verifyESBPurchaseBN_TRUST:" + outputVO_707_M.getErrorMsg());
            }
        }

        // 特金申請電文錯誤訊息
        if (StringUtils.isEmpty(errorMsg.toString()) && (!"".equals(outputVO_707_S.getErrorMsg()) && null != outputVO_707_S.getErrorMsg())) {
            errorMsg.append(outputVO_707_S.getErrorMsg());
            logger.error("next() sot707.verifyESBPurchaseBN:" + outputVO_707_S.getErrorMsg());
        }

        if (warningList.size() == 0 && (!"".equals(outputVO_707_S.getWarningCode()) && null != outputVO_707_S.getWarningCode())) {
            warningList.addAll(outputVO_707_S.getWarningCode());
            logger.error("next() sot707.verifyESBPurchaseBN_TRUST:" + outputVO_707_M.getErrorMsg());
        }

        // 金錢+特金處理
        if (StringUtils.isNotEmpty(errorMsg.toString())) { // !"".equals(errorMsg) && null != errorMsg
            outputVO.setErrorMsg(errorMsg.toString());
        } else {
            TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
            vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
            if (null != vo) {
                vo.setTRADE_STATUS("2"); //2:風控檢核中
                vo.setModifier(ws.getUser().getUserID());
                vo.setLastupdate(new Timestamp(System.currentTimeMillis()));

                dam.update(vo);
            }
        }

        //若有警示訊息
        if(CollectionUtils.isNotEmpty(warningList)) {
            outputVO.setWarningMsg("");
            XMLInfo xmlInfo = new XMLInfo();

            for(String msg : warningList) {
                outputVO.setWarningMsg(xmlInfo.getErrorMsg(msg) + "\n" + outputVO.getWarningMsg());
            }
        }

        this.sendRtnObject(outputVO);
	}

	// 暫存
	public void save (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT510InputVO inputVO = (SOT510InputVO) body;
		SOT510OutputVO outputVO = new SOT510OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try{
			add(dam, queryCondition, inputVO);
			this.sendRtnObject(outputVO);
		}catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
	}

	//抓取弱勢客群flag
	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT510InputVO inputVO = (SOT510InputVO) body;
		SOT510OutputVO outputVO = new SOT510OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		Boolean isFirstTrade = false;

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);
		outputVO.setCustRemarks(fp032675DataVO.getCustRemarks()); 		 //弱勢
		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag());  //年齡小於70
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag());            //教育程度國中以上(不含國中)
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag());          //未領有全民健保重大傷病證明

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO  sot712InputVO = new SOT712InputVO();
        sot712InputVO.setCustID(inputVO.getCustID());
        outputVO.setCustAge(sot712.getCUST_AGE(sot712InputVO));          //查客戶年齡 (未成年警語)

		//首購
		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707 = new SOT707OutputVO();
		inputVO_707.setCustId(inputVO.getCustID());
		inputVO_707.setProdId(inputVO.getProdID());

		String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
		
		if (isOBU.equals("Y")) {
			isFirstTrade = sot707.getIsCustFirstTradeOBU(inputVO_707);
		} else {
			isFirstTrade = sot707.getIsCustFirstTrade(inputVO_707);	
		}
		outputVO.setIsFirstTrade((isFirstTrade) ? "Y" : "N");

		this.sendRtnObject(outputVO);
	}

	//存入TBSOT_TRADE主檔/明細，不判斷任何邏輯
	private void add(DataAccessManager dam, QueryConditionIF queryCondition, SOT510InputVO inputVO) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		try {
			// 1.寫入主檔/明細
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			//是否需要錄音
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO inputVO_712 = new SOT712InputVO();
			inputVO_712.setCustID(inputVO.getCustID());
			inputVO_712.setProdID(inputVO.getProdID());
			inputVO_712.setProdType("SN");
			inputVO_712.setProfInvestorYN(inputVO.getProfInvestorYN());
			inputVO_712.setCustRemarks(inputVO.getCustRemarks());
			inputVO_712.setCustProRemark(inputVO.getPiRemark());

			if (null == vo) {
				TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();

				/*
				 * About CR list ↓
				 * 	WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5
				 * 	WMS-CR-20190710-05_個人高端客群處業管系統_行銷模組調整申請_P1
				 *
				 * Version ↓
				 * 	create by kent
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
				mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
				mainVO.setPROD_TYPE("5"); //5：SN
				mainVO.setTRADE_TYPE("1"); //1:申購
				mainVO.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
				mainVO.setCUST_ID(inputVO.getCustID());
				mainVO.setCUST_NAME(inputVO.getCustName());
				mainVO.setKYC_LV(inputVO.getKycLV());
				mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(dateFormat.parse(inputVO.getKycDueDate().toString()).getTime()) : null);
				mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
				mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(dateFormat.parse(inputVO.getPiDueDate().toString()).getTime()) : null);
				mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
				mainVO.setIS_OBU(inputVO.getIsOBU());
				mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
				mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
				mainVO.setTRADE_STATUS("1"); //1:暫存
				mainVO.setIS_BARGAIN_NEEDED("N"); //是否需要議價:SN無議價功能
				mainVO.setBARGAIN_FEE_FLAG(null); //議價狀態
			
				mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); //是否需要錄音 (非常規交易)
//				mainVO.setIS_REC_NEEDED("N"); //是否需要錄音 (非常規交易)

				mainVO.setREC_SEQ(null);
				mainVO.setSEND_DATE(null);
				mainVO.setPROF_INVESTOR_TYPE(inputVO.getCustProType());//專業投資人類別

				mainVO.setTRUST_TRADE_TYPE(inputVO.getTrustTS());
				if(StringUtils.isBlank(inputVO.getGUARDIANSHIP_FLAG())){
					mainVO.setGUARDIANSHIP_FLAG(" ");
				}else{
					mainVO.setGUARDIANSHIP_FLAG(inputVO.getGUARDIANSHIP_FLAG());
				}

				mainVO.setHNWC_YN(inputVO.getHnwcYN());
				mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
				mainVO.setFLAG_NUMBER(inputVO.getFlagNumber());
				
				dam.create(mainVO);
			} else {
				vo.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
				vo.setCUST_ID(inputVO.getCustID());
				vo.setCUST_NAME(inputVO.getCustName());
				vo.setKYC_LV(inputVO.getKycLV());
				vo.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(dateFormat.parse(inputVO.getKycDueDate().toString()).getTime()) : null);
				vo.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
				vo.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(dateFormat.parse(inputVO.getPiDueDate().toString()).getTime()) : null);
				vo.setCUST_REMARKS(inputVO.getCustRemarks());
				vo.setIS_OBU(inputVO.getIsOBU());
				vo.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
				vo.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
				vo.setPROF_INVESTOR_TYPE(inputVO.getCustProType());//專業投資人類別
				vo.setHNWC_YN(inputVO.getHnwcYN());
				vo.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
				vo.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); //是否需要錄音 (非常規交易)
				
				dam.update(vo);
			}
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM TBSOT_SN_TRADE_D WHERE TRADE_SEQ = :tradeSEQ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());
			dam.exeUpdate(queryCondition);

			BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
			TBSOT_SN_TRADE_DPK dtlPK = new TBSOT_SN_TRADE_DPK();
			dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			dtlPK.setSEQ_NO(newTrade_SEQNO);
			//交易類型
			TBSOT_SN_TRADE_DVO dtlVO = new TBSOT_SN_TRADE_DVO();
			dtlVO.setcomp_id(dtlPK);
			dtlVO.setTRADE_SUB_TYPE("1");
			dtlVO.setTRUST_CURR_TYPE(inputVO.getTrustCurrType());
			dtlVO.setTRUST_CURR(inputVO.getTrustCurr());
			dtlVO.setMARKET_TYPE(inputVO.getMarketType());
			dtlVO.setBOND_VALUE(inputVO.getBondVal());																			//票面價值
			dtlVO.setPROD_ID(inputVO.getProdID());																				//商品代號
			dtlVO.setPROD_NAME(inputVO.getProdName());																			//商品名稱
			dtlVO.setPROD_CURR(inputVO.getProdCurr());
			dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLV());																		//產品風險等級
			dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt());																//最低申購面額
			dtlVO.setPROD_MIN_GRD_AMT(inputVO.getProdMinGrdAmt());
			dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt());
			dtlVO.setTRUST_AMT(inputVO.getTrustAmt());
			dtlVO.setREF_VAL(inputVO.getRefVal());																				//參考報價
			dtlVO.setREF_VAL_DATE(null != inputVO.getRefValDate() ? new Timestamp(inputVO.getRefValDate().getTime()) : null);	//參考報價日期
			dtlVO.setENTRUST_TYPE("2");
//			dtlVO.setENTRUST_AMT(inputVO.getEntrustAmt());
			dtlVO.setTOT_AMT(inputVO.getTotAmt());
			dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct().split("_")[0]);																		//扣款帳號
			dtlVO.setTRUST_ACCT(inputVO.getTrustAcct());																		//信託帳號
			dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct().split("_")[0]);																		//收益入帳帳號/贖回款入帳帳號
			dtlVO.setTRADE_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime()));															//交易日期
			dtlVO.setNARRATOR_ID(inputVO.getNarratorID());																		//解說專員員編
			dtlVO.setNARRATOR_NAME(inputVO.getNarratorName());
			dtlVO.setBOSS_ID(inputVO.getBossID());
			dtlVO.setAUTH_ID(inputVO.getAuthID());//授權交易人員員編
			dtlVO.setOVER_CENTRATE_YN(inputVO.getOverCentRateYN()); //集中度是否超過上限 Y:是
			
			BigDecimal rate = (null != inputVO.getFeeRate() ? inputVO.getFeeRate() : (null != inputVO.getBestFeeRate() ? inputVO.getBestFeeRate() : inputVO.getDefaultFeeRate()));
			dtlVO.setTOT_AMT(inputVO.getTotAmt());																				//總扣款金額/預估贖回入帳金額
																			//解說專員姓名
			dtlVO.setCONTRACT_ID(inputVO.getContractID());
			dtlVO.setTRUST_PEOP_NUM(StringUtils.isNotEmpty(inputVO.getTrustPeopNum()) ? inputVO.getTrustPeopNum() : "N");
			
			if(StringUtils.equals(inputVO.getTrustTS(),"M")){
				dtlVO.setBEST_FEE_RATE(inputVO.getBestFeeRate());
				dtlVO.setDEFAULT_FEE_RATE(inputVO.getDefaultFeeRate());
				dtlVO.setENTRUST_AMT(inputVO.getEntrustAmt());
				dtlVO.setFEE_RATE(inputVO.getFeeRate());
			}
			
			dam.create(dtlVO);
		}catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
	}

	private String getSeqNum (String TXN_ID, String format, Timestamp timeStamp, Integer minNum, Long maxNum,String status,Long nowNum) throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();

		String seqNum = "";
		try{
			seqNum = sn.getNextSerialNumber(TXN_ID);
		} catch(Exception e){
			sn.createNewSerial(TXN_ID, format, 1, "d", timeStamp, minNum, maxNum, status, nowNum, null);
			seqNum = sn.getNextSerialNumber(TXN_ID);
		}
		return seqNum;
	}

	public void query (Object body, IPrimitiveMap header) throws JBranchException {

		SOT510InputVO inputVO = (SOT510InputVO) body;
		SOT510OutputVO outputVO = new SOT510OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuilder sb = null;
		try{
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append("SELECT TRUST_PEOP_NUM, CONTRACT_ID, BOND_VALUE,MARKET_TYPE,TRUST_CURR_TYPE,PURCHASE_AMT,TRUST_AMT,TOT_AMT,PROD_ID,");
			sb.append("PROD_NAME,PROD_RISK_LV,PROD_CURR,PROD_MIN_BUY_AMT,PROD_MIN_GRD_AMT,DEBIT_ACCT,TRUST_ACCT,CREDIT_ACCT,NARRATOR_ID,NARRATOR_NAME,BOSS_ID, AUTH_ID, OVER_CENTRATE_YN ");
			sb.append("FROM TBSOT_SN_TRADE_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			outputVO.setCarList(dam.exeQuery(queryCondition));

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append("SELECT CUST_ID,CUST_NAME,KYC_LV,KYC_DUE_DATE,CUST_REMARKS,IS_OBU,PROF_INVESTOR_YN,PI_DUE_DATE,PROD_TYPE,TRADE_STATUS,PROF_INVESTOR_TYPE, ");
			sb.append(" IS_REC_NEEDED, PI_REMARK, TRUST_TRADE_TYPE, HNWC_YN, HNWC_SERVICE_YN, FLAG_NUMBER ");
			sb.append("FROM TBSOT_TRADE_MAIN ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			outputVO.setMainList(dam.exeQuery(queryCondition));

			//錄音序號
//			SOT712 sot712 =new SOT712();
//			SOT712InputVO input712 = new SOT712InputVO();
//			
//			input712.setCustID((String)outputVO.getMainList().get(0).get("CUST_ID"));
//			input712.setProdID((String)outputVO.getCarList().get(0).get("PROD_ID"));
//			input712.setProdType("SN");
//			input712.setProfInvestorYN(outputVO.getMainList().get(0).get("PROF_INVESTOR_YN")!=null?outputVO.getMainList().get(0).get("PROF_INVESTOR_YN").toString():null);
//			input712.setCustRemarks((String)outputVO.getMainList().get(0).get("CUST_REMARKS"));
//			input712.setCustProRemark((String)outputVO.getMainList().get(0).get("PI_REMARK"));
//			outputVO.setRecNeeded(new Boolean(sot712.getIsRecNeeded(input712)));

			inputVO.setCustID((String)outputVO.getMainList().get(0).get("CUST_ID"));
			inputVO.setProdID((String)outputVO.getCarList().get(0).get("PROD_ID"));
			this.checkRisk(inputVO,outputVO);




			this.sendRtnObject(outputVO);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}

	/***
	 * 集中度是否超過上限
	 * Y:超過 N:沒超過
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getOverCentRateYN(SOT510InputVO inputVO, BigDecimal rate) throws Exception {
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		//集中度加上新增商品金額
		inputVO714.setProdType("2");//SN
		inputVO714.setBuyAmt(inputVO.getTrustAmt().multiply(rate));
				
		//查詢客戶高資產集中度資料
		WMSHACRDataVO cRateData = sot714.getCentRateData(inputVO714);
		return StringUtils.equals("Y", cRateData.getVALIDATE_YN()) ? "N" : "Y"; //可交易=>沒超過上限; 不可交易=>超過上限
	}
	
	/***
	 * 商品是否保本
	 * true: 保本
	 * false: 不保本
	 * @param prodId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private boolean isRateGuaranteed(String prodId) throws DAOException, JBranchException {
		boolean returnVal = false;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		//取得保本率
		queryCondition.setQueryString("select NVL(RATE_GUARANTEEPAY, 0) AS RATE_GUARANTEEPAY from TBPRD_SN where PRD_ID = :prdId ");
		queryCondition.setObject("prdId", prodId);
		List<Map<String, Object>> pList = dam.exeQuery(queryCondition);
		if (CollectionUtils.isNotEmpty(pList)) {
			BigDecimal base = new BigDecimal(100);
			BigDecimal rate = (BigDecimal) pList.get(0).get("RATE_GUARANTEEPAY");
			returnVal = rate.compareTo(base) > -1;
		}
		
		return returnVal;
	}
	
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private boolean validHnwcRiskValue(SOT510InputVO inputVO, BigDecimal rate) throws Exception {
		//取得高資產客戶註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCustID());
		//高資產客戶投組風險權值檢核
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		inputVO714.setCUST_KYC(inputVO.getKycLV()); //客戶C值
		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(inputVO.getProdRiskLV()); //商品風險檢核值
		BigDecimal amtBuy = inputVO.getTrustAmt().multiply(rate);
		switch(inputVO.getProdRiskLV()) {
			case "P1":
				inputVO714.setAMT_BUY_1(amtBuy);
				break;
			case "P2":
				inputVO714.setAMT_BUY_2(amtBuy);
				break;
			case "P3":
				inputVO714.setAMT_BUY_3(amtBuy);
				break;
			case "P4":
				inputVO714.setAMT_BUY_4(amtBuy);
				break;
		}
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		return StringUtils.equals("Y", riskValData.getVALIDATE_YN()) ? true : false;
	}
	
}