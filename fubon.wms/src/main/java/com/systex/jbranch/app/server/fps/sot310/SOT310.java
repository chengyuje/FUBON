package com.systex.jbranch.app.server.fps.sot310;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.prd130.PRD130;
import com.systex.jbranch.app.server.fps.prd130.PRD130InputVO;
import com.systex.jbranch.app.server.fps.prd130.PRD130OutputVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot701.SotUtils;
import com.systex.jbranch.app.server.fps.sot707.ProdRefValVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707;
import com.systex.jbranch.app.server.fps.sot707.SOT707InputVO;
import com.systex.jbranch.app.server.fps.sot707.SOT707OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.njweea70.NJWEEA70;
import com.systex.jbranch.fubon.commons.esb.vo.njweea70.NJWEEA70OutputDetailVO;
import com.systex.jbranch.fubon.commons.esb.vo.njweea70.NJWEEA70OutputVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
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
 * sot310
 * 
 * @author ocean
 * @date 2016/10/03
 * @spec 海外債申購資料輸入及適配
 */
@Component("sot310")
@Scope("request")
public class SOT310 extends EsbUtil {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT310.class);
	
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	private String thisClaz = this.getClass().getSimpleName() + ".";

	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		SOT701OutputVO outputVO_701 = new SOT701OutputVO();

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());
		
		inputVO_701.setNeedFlagNumber(true);
		inputVO_701.setTrustTS(inputVO.getTrustTS());

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

		//FOR CBS測試日期修改
		outputVO.setKycDueDateUseful(outputVO_701.getCustKYCDataVO().isKycDueDateUseful());
		outputVO.setPiDueDateUseful(outputVO_701.getFp032675DataVO().isPiDueDateUseful());
		// add by ocean 2016-10-26 因應適配
		//		outputVO.setIsCustStackholder(sot701.isCustStakeholder(inputVO_701));
		//		outputVO.setCustQValue(sot701.getCustQValue(inputVO_701));

		List<AcctVO> debitAcct = outputVO_701.getCustAcctDataVO().getDebitAcctList();
		List<Map<String, Object>> debitAcctList = new ArrayList<Map<String, Object>>();

		/*目前扣款帳號、收益入帳帳號SOT701會回傳所有幣別帳號
			1. 若選擇外幣信託：扣款帳號及收益入帳帳號，只顯示該商品幣別的帳號
			2. 若選擇台幣信託、國內基金：扣款帳號及收益入帳帳號，只顯示該台幣帳號
			3. 扣款帳號幣別餘額需顯示於帳號旁邊
		 */
		for (AcctVO vo : debitAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()) + "_" + vo.getCurrency());
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()) + "_" + vo.getCurrency());
			map.put("AVL_BALANCE", vo.getAvbBalance());
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

		//查詢客戶高資產註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hNWCDataVO = sot714.getHNWCData(inputVO.getCustID());
		outputVO.setHnwcYN(StringUtils.isNotBlank(hNWCDataVO.getValidHnwcYN()) ? hNWCDataVO.getValidHnwcYN() : "N");
		outputVO.setHnwcServiceYN(hNWCDataVO.getHnwcService());
		outputVO.setFlagNumber(outputVO_701.getFlagNumber());
		
		this.sendRtnObject(outputVO);
	}

	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		
		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);

		outputVO.setCustRemarks(fp032675DataVO.getCustRemarks()); 		 //弱勢
		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag());  //年齡小於70
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag());            //教育程度國中以上(不含國中)
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag());          //未領有全民健保重大傷病證明
		
		//首購
//		String prodType = "BN";
//		switch (inputVO.getProdType()) {
//			case "1" : 
//				prodType = "NF";
//				break;
//			case "2" : 
//				prodType = "ETF";
//				break;
//			case "3" : 
//				prodType = "BN";
//				break;
//		}
		Boolean isFirstTrade = false;
//		String isMisFirstTrade = sot701.getMisFirst(dam, inputVO.getProdID(), prodType, inputVO.getCustRemarks(), inputVO.getCustID());
		
		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707 = new SOT707OutputVO();

		inputVO_707.setCustId(inputVO.getCustID());
		inputVO_707.setProdId(inputVO.getProdID());

		String isOBU = StringUtils.isBlank(fp032675DataVO.getObuFlag()) ? "" : fp032675DataVO.getObuFlag();
		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
//		isFirstTrade = sot707.getIsCustFirstTrade(inputVO_707);
		if (isOBU.equals("Y")) {
			isFirstTrade = sot707.getIsCustFirstTradeOBU(inputVO_707);
		} else {
			isFirstTrade = sot707.getIsCustFirstTrade(inputVO_707);	
		}
		outputVO.setIsFirstTrade((isFirstTrade) ? "Y" : "N");

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO sot712InputVO = new SOT712InputVO();
		sot712InputVO.setCustID(inputVO.getCustID());
		outputVO.setCustAge(sot712.getCUST_AGE(sot712InputVO)); //查客戶年齡 (未成年警語)

		this.sendRtnObject(outputVO);
	}

	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		boolean isFitness = StringUtils.equals("N", (String) new XmlInfo().getVariable("SOT.FITNESS_YN", "BN", "F3")) ? false : true;

		outputVO.setWarningMsg("");
		outputVO.setErrorMsg("");

		boolean isFitnessOK = false;
		if (isFitness) {
			// 1.適配，由商品查詢取得，邏輯需一致
			PRD130OutputVO prdOutputVO = new PRD130OutputVO();
			PRD130InputVO prdInputVO = new PRD130InputVO();
			prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
			prdInputVO.setPrd_id(inputVO.getProdID());
			prdInputVO.setType("4");
			prdInputVO.setTrustTS(inputVO.getTrustTS());

			PRD130 prd130 = (PRD130) PlatformContext.getBean("prd130");
			prdOutputVO = prd130.inquire(prdInputVO);

			if (CollectionUtils.isNotEmpty(prdOutputVO.getResultList())) {
				outputVO.setBondVal((BigDecimal) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("BOND_VALUE"));//由prd140.inquire抓取票面價值
				outputVO.setIsWebSale((String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("IS_WEB_SALE"));//商品是否可網銀快速申購
				outputVO.setHnwcBuy((String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("HNWC_BUY"));//商品限高資產客戶申購註記
				String warningMsg = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("warningMsg");
				String errId = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("errorID");

				if (StringUtils.isBlank(errId)) {
					isFitnessOK = true;
					
					//取得商品計價幣別最新買匯匯率
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuffer();
					sb.append("SELECT NVL(BUY_RATE, 1) AS BUY_RATE FROM TBPMS_IQ053 WHERE CUR_COD = :curr ");
					sb.append(" AND MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053 WHERE CUR_COD = :curr ) ");
					queryCondition.setObject("curr", (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("CURRENCY_STD_ID"));
					queryCondition.setQueryString(sb.toString());
					List<Map<String, Object>> cList = dam.exeQuery(queryCondition);
					if(CollectionUtils.isEmpty(cList)) {
						outputVO.setBuyRate(new BigDecimal(1));
					} else {
						outputVO.setBuyRate((BigDecimal) ((Map<String, Object>) cList.get(0)).get("BUY_RATE"));
					}
					
				}

				outputVO.setWarningMsg(warningMsg);
				outputVO.setErrorMsg(errId);
			}
		} else {
			isFitnessOK = true;
		}

		// 2.查詢商品參考報價電文
		if (isFitnessOK) {
			SOT707InputVO inputVO_707 = new SOT707InputVO();
			SOT707OutputVO outputVO_707 = new SOT707OutputVO();

			inputVO_707.setCustId(inputVO.getCustID());
			inputVO_707.setProdId(inputVO.getProdID());

			SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
			outputVO_707 = sot707.getProdRefVal(inputVO_707);

			List<ProdRefValVO> refValList = outputVO_707.getProdRefVal();
			refValList = (CollectionUtils.isEmpty(refValList)) ? new ArrayList<ProdRefValVO>() : refValList;

			List<Map<String, Object>> prodList = new ArrayList<Map<String, Object>>();
			for (ProdRefValVO refVal : refValList) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("PRD_ID", refVal.getBondNo());
				map.put("BOND_CNAME", refVal.getBondName());
				map.put("RISKCATE_ID", refVal.getProdRiskLv());
				map.put("CURRENCY_STD_ID", refVal.getProdCurr());
				map.put("BASE_AMT_OF_PURCHASE", refVal.getProdMinBuyAmt());
				map.put("UNIT_OF_VALUE", refVal.getProdMinGrdAmt());
				map.put("BUY_PRICE", refVal.getRefVal());
				map.put("BARGAIN_DATE", refVal.getRefValDate());

				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("select LIMITED_PRICE ");
				sb.append("  from TBPRD_LIMITED_PRICE ");
				sb.append(" where PRD_ID = :prodID and CUST_ID = :custID and REVIEW_STATUS = 'Y' ");
				queryCondition.setObject("prodID", inputVO.getProdID());
				queryCondition.setObject("custID", inputVO.getCustID());
				queryCondition.setQueryString(sb.toString());

				List<Map<String, Object>> pList = dam.exeQuery(queryCondition);
				BigDecimal limitPrice = null;
				if (pList.size() > 0) {
					limitPrice = (BigDecimal) pList.get(0).get("LIMITED_PRICE");
				}
				map.put("LIMITED_PRICE", limitPrice);

				prodList.add(map);
			}

			outputVO.setProdDTL(prodList);

			//NvlAMT: 前一日投資AUM
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
			} else {
				logger.debug("not found 'NVLA'");
			}

			//SumITEM: AUM在途申購金額+庫存金額
			/* select sum(NVL(BAL,0))
			from TBPMS_CUST_DAY_PROFIT
			where CUST_ID = ?? and ITEM in ('05','13')
			and TX_DATE = (select max(TX_DATE) from TBPMS_CUST_DAY_PROFIT where CUST_ID = ??)
			*/
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sq_sumITEM = new StringBuffer();
			sq_sumITEM.append("select sum(NVL(BAL,0)) AS  SUMITEM ");
			sq_sumITEM.append("FROM TBPMS_CUST_DAY_PROFIT ");
			if (!StringUtils.isBlank(inputVO.getCustID())) {
				sq_sumITEM.append(" WHERE CUST_ID = :custID  ");
				queryCondition.setObject("custID", inputVO.getCustID());
			}
			sq_sumITEM.append("AND ITEM in ('05','13') ");
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
				//AUM在途申購金額+庫存金額
				outputVO.setSumITEM(new BigDecimal(list_ITEM.get(0).get("SUMITEM").toString()));
			} else {
				outputVO.setSumITEM(BigDecimal.ZERO);
				logger.debug("not found 'SUMITEM'");
			}

		}

		this.sendRtnObject(outputVO);
	}

	// 暫存
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT310InputVO inputVO = (SOT310InputVO) body;

		SOT310OutputVO outputVO = new SOT310OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		add(dam, queryCondition, inputVO);

		this.sendRtnObject(outputVO);
	}

	public void add(DataAccessManager dam, QueryConditionIF queryCondition, SOT310InputVO inputVO) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		logger.debug("海外債 TradeSEQ:" + inputVO.getTradeSEQ());
		logger.debug("海外債 CustID:" + inputVO.getCustID());

		if (StringUtils.indexOf(inputVO.getDebitAcct(), "_") > 0)
			inputVO.setDebitAcct(StringUtils.split(inputVO.getDebitAcct(), "_")[0]);
		if (StringUtils.indexOf(inputVO.getCreditAcct(), "_") > 0)
			inputVO.setCreditAcct(StringUtils.split(inputVO.getCreditAcct(), "_")[0]);

		//是否需要錄音  需要首購判斷 新增、修改共用 START
		Boolean isFirstTrade = false;
		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707 = new SOT707OutputVO();
		inputVO_707.setCustId(inputVO.getCustID());
		inputVO_707.setProdId(inputVO.getProdID());
		String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
		if (isOBU.equals("Y")) {
			isFirstTrade = sot707.getIsCustFirstTradeOBU(inputVO_707); //  海外債/SN產品首購資料OBU查詢 使用電文: AJBRVA3
		} else {
			isFirstTrade = sot707.getIsCustFirstTrade(inputVO_707); //  海外債/SN產品首購資料查詢 使用電文: NJBRVA3			
		}

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		inputVO_712.setCustID(inputVO.getCustID());
		inputVO_712.setProdID(inputVO.getProdID());
		inputVO_712.setProdType("BN");
		inputVO_712.setProfInvestorYN(inputVO.getProfInvestorYN());
		inputVO_712.setIsFirstTrade((isFirstTrade) ? "Y" : "N");
		inputVO_712.setCustRemarks(inputVO.getCustRemarks());
		inputVO_712.setCustProRemark(inputVO.getPiRemark());
		//是否需要錄音  需要首購判斷 新增、修改共用 END

		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE("3"); //3：海外債
			mainVO.setTRADE_TYPE("1"); //1:申購
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
			mainVO.setIS_BARGAIN_NEEDED("N"); //不提供議價，所以設N
			mainVO.setBARGAIN_FEE_FLAG(null);
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());

			//是否需要錄音  需要首購判斷
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N");
			logger.debug(String.format("是否需要錄音: main.IS_REC_NEEDED:%s", mainVO.getIS_REC_NEEDED()));
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
			
			mainVO.setIS_WEB(inputVO.getIsWeb());//是否為快速申購
			mainVO.setFLAG_NUMBER(inputVO.getFlagNumber());
			
			dam.create(mainVO);
		} else {
			vo.setCUST_ID(inputVO.getCustID());
			vo.setCUST_NAME(inputVO.getCustName());
			vo.setKYC_LV(inputVO.getKycLV());
			vo.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);
			vo.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			vo.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);
			vo.setPI_REMARK(inputVO.getPiRemark());
			vo.setCUST_REMARKS(inputVO.getCustRemarks());
			vo.setIS_OBU(inputVO.getIsOBU());
			vo.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			vo.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
			vo.setHNWC_YN(inputVO.getHnwcYN());
			vo.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
			
			//是否需要錄音  需要首購判斷
			vo.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N");
			//			logger.debug(String.format("是否需要錄音: main.IS_REC_NEEDED:%s", mainVO.getIS_REC_NEEDED()));
			vo.setPROF_INVESTOR_TYPE(inputVO.getCustProType());
			
			vo.setIS_WEB(inputVO.getIsWeb());//是否為快速申購
			
			dam.update(vo);
		}

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM TBSOT_BN_TRADE_D WHERE TRADE_SEQ = :tradeSEQ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);

		BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
		TBSOT_BN_TRADE_DPK dtlPK = new TBSOT_BN_TRADE_DPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(newTrade_SEQNO);

		TBSOT_BN_TRADE_DVO dtlVO = new TBSOT_BN_TRADE_DVO();
		dtlVO.setcomp_id(dtlPK);
		dtlVO.setBATCH_SEQ(null); //下單批號
		dtlVO.setTRADE_SUB_TYPE("1"); //交易類型
		dtlVO.setCERTIFICATE_ID(null); //憑證編號
		dtlVO.setBOND_VALUE(inputVO.getBondVal());
		dtlVO.setPROD_ID(inputVO.getProdID()); //商品代號
		dtlVO.setPROD_NAME(inputVO.getProdName()); //商品名稱
		dtlVO.setPROD_CURR(inputVO.getProdCurr()); //計價幣別
		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLV()); //產品風險等級
		dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt()); //最低申購面額
		dtlVO.setPROD_MIN_GRD_AMT(inputVO.getProdMinGrdAmt()); //累計申購面額
		dtlVO.setTRUST_CURR_TYPE(inputVO.getTrustCurrType()); //信託幣別類型
		dtlVO.setTRUST_CURR(inputVO.getTrustCurr()); //信託幣別
		dtlVO.setTRUST_UNIT(inputVO.getTrustUnit()); //庫存張數
		dtlVO.setMARKET_TYPE(inputVO.getMarketType()); //債券市場種類
		dtlVO.setREF_VAL(inputVO.getRefVal()); //參考報價
		dtlVO.setREF_VAL_DATE(null != inputVO.getRefValDate() ? new Timestamp(inputVO.getRefValDate().getTime()) : null); //參考報價日期
		dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt()); //申購金額/庫存面額
		dtlVO.setENTRUST_TYPE(inputVO.getEntrustType()); //委託價格類型/贖回方式
		dtlVO.setENTRUST_AMT(inputVO.getEntrustAmt()); //委託價格/贖回價格
		dtlVO.setTRUST_AMT(inputVO.getTrustAmt()); //信託本金
		dtlVO.setDEFAULT_FEE_RATE(inputVO.getDefaultFeeRate()); //表定手續費率
		dtlVO.setADV_FEE_RATE(null); //事先申請手續費率
		dtlVO.setBEST_FEE_RATE(inputVO.getBestFeeRate()); //事先申請手續費率
		dtlVO.setBARGAIN_APPLY_SEQ(null); //議價編號

		//BigDecimal rate = (null != inputVO.getFeeRate() ? inputVO.getFeeRate() : (null != inputVO.getBestFeeRate() ? inputVO.getBestFeeRate() : inputVO.getDefaultFeeRate()));
		//dtlVO.setFEE_RATE(rate);																							//手續費率/信託管理費率
		//若沒有議價，就不用放手續費率資料
		dtlVO.setFEE_RATE(inputVO.getFeeRate());

		dtlVO.setFEE(inputVO.getFee()); //手續費金額/預估信託管理費

		dtlVO.setFEE_DISCOUNT(null != inputVO.getFeeDiscount() ? inputVO.getFeeDiscount() : (null == inputVO.getFeeRate() ? null : inputVO.getFeeRate().divide(inputVO.getDefaultFeeRate(), 3, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(10)))); //手續費折數

		double feeDiscount10 = 10.0;//10折不寫入資料庫;//10折不寫入資料庫
		if (inputVO.getFeeDiscount() != null && feeDiscount10 == inputVO.getFeeDiscount().doubleValue()) {
			dtlVO.setFEE_DISCOUNT(null);
		}

		dtlVO.setPAYABLE_FEE(inputVO.getPayableFee()); //應付前手息/應收前手息
		dtlVO.setTOT_AMT(inputVO.getTotAmt()); //總扣款金額/預估贖回入帳金額
		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct()); //扣款帳號
		dtlVO.setTRUST_ACCT(inputVO.getTrustAcct()); //信託帳號
		dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct()); //收益入帳帳號/贖回款入帳帳號
		dtlVO.setTRADE_DATE(new Timestamp(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()).getTime())); //交易日期
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名

		dtlVO.setGTC_YN(inputVO.getGtcYN()); //長效單Y 當日單N	 預約單P
		dtlVO.setGTC_START_DATE(null != inputVO.getGtcStartDate() ? new Timestamp(inputVO.getGtcStartDate().getTime()) : null); //長效單起日
		dtlVO.setGTC_END_DATE(null != inputVO.getGtcEndDate() ? new Timestamp(inputVO.getGtcEndDate().getTime()) : null); 		//長效單迄日

		//mantis #000104: WMS-CR-20191009-01_金錢信託套表需求申請單 2019-12-20 add by ocean
		dtlVO.setCONTRACT_ID(inputVO.getContractID());
		dtlVO.setTRUST_PEOP_NUM(StringUtils.isNotEmpty(inputVO.getTrustPeopNum()) ? inputVO.getTrustPeopNum() : "N");
		dtlVO.setOVER_CENTRATE_YN(inputVO.getOverCentRateYN()); //集中度檢核結果
		
		dam.create(dtlVO);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT TRUST_PEOP_NUM, CONTRACT_ID, "); //mantis #000104: WMS-CR-20191009-01_金錢信託套表需求申請單 2019-12-20 add by ocean
		sb.append("       BOND_VALUE,TRADE_SEQ, SEQ_NO, BATCH_SEQ, TRADE_SUB_TYPE, CERTIFICATE_ID, PROD_ID, PROD_NAME, PROD_CURR, PROD_RISK_LV, ");
		sb.append("       PROD_MIN_BUY_AMT, PROD_MIN_GRD_AMT, TRUST_CURR_TYPE, TRUST_CURR, TRUST_UNIT, MARKET_TYPE, REF_VAL, REF_VAL_DATE, PURCHASE_AMT, ENTRUST_TYPE, ");
		sb.append("       ENTRUST_AMT, TRUST_AMT, DEFAULT_FEE_RATE, ADV_FEE_RATE, BEST_FEE_RATE, BARGAIN_APPLY_SEQ, FEE_RATE, FEE, FEE_DISCOUNT, PAYABLE_FEE, ");
		sb.append("       TOT_AMT, DEBIT_ACCT, TRUST_ACCT, CREDIT_ACCT, TRADE_DATE, NARRATOR_ID, NARRATOR_NAME, GTC_YN, GTC_START_DATE, GTC_END_DATE, OVER_CENTRATE_YN ");
		sb.append("FROM TBSOT_BN_TRADE_D ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setCarList(dam.exeQuery(queryCondition));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT TRADE_SEQ, PROD_TYPE, TRADE_TYPE, CUST_ID, CUST_NAME, AGENT_ID, AGENT_NAME, KYC_LV, KYC_DUE_DATE, PROF_INVESTOR_YN, PI_DUE_DATE, PI_REMARK, CUST_REMARKS, ");
		sb.append("       IS_OBU, IS_AGREE_PROD_ADV, BARGAIN_DUE_DATE, TRADE_STATUS, IS_BARGAIN_NEEDED, BARGAIN_FEE_FLAG, ");
		sb.append("       IS_REC_NEEDED, REC_SEQ, SEND_DATE, NVL(IS_WEB, 'N') AS IS_WEB, ");
		sb.append("       TRUST_TRADE_TYPE, "); //mantis #000104: WMS-CR-20191009-01_金錢信託套表需求申請單 2019-12-20 add by ocean
		sb.append("       FLAG_NUMBER ");
		sb.append("FROM TBSOT_TRADE_MAIN ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setMainList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	// 下一步
	public void next(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer errorMsg = new StringBuffer();
		List<String> warningList = new ArrayList<String>();
		
		//高資產客戶投組風險權值檢核
		//只限特金，客戶風險屬性非C4，有選擇越級商品
		if(!StringUtils.equals("M", inputVO.getTrustTS()) && !StringUtils.equals("C4", inputVO.getKycLV())
				&& (Integer.parseInt(inputVO.getProdRiskLV().substring(1))) > (Integer.parseInt(inputVO.getKycLV().substring(1)))
				&& StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN())) {
			//買匯匯率
			SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
			BigDecimal rate = sot110.getBuyRate(inputVO.getProdCurr());
			
			//高資產客戶投組風險權值檢核
			if(!validHnwcRiskValue(inputVO, rate)) {
				throw new APException("客戶加計此筆投資之越級比率已超標，請選擇其他標的或調整投資面額");
			}
		}
		
		//高資產客戶購買高風險商品，檢核集中度
		String overCentRateResult = "";
		if(StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcBuy())) {
			//買匯匯率
			SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
			BigDecimal rate = sot110.getBuyRate(inputVO.getProdCurr());
			
			overCentRateResult = getOverCentRateYN(inputVO, rate); //Y:可交易 W:超過通知門檻 N:不可交易
			inputVO.setOverCentRateYN(overCentRateResult);
			if(StringUtils.equals("W", inputVO.getOverCentRateYN())) {
				warningList.add("ehl_01_SOT310_001");
			}
		}
		outputVO.setOverCentRateResult(overCentRateResult); //集中度檢核結果
				
		add(dam, queryCondition, inputVO);

		// 檢核電文
		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707_S = new SOT707OutputVO();
		SOT707OutputVO outputVO_707_M = new SOT707OutputVO();

		inputVO_707.setProdType("2");
		inputVO_707.setCheckType("1");
		inputVO_707.setPurchaseAmt(inputVO.getPurchaseAmt());//2017/11/06增加，之前有漏
		inputVO_707.setTradeSeq(inputVO.getTradeSEQ());

		String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
		if ("Y".equals(inputVO.getGtcYN()) || "P".equals(inputVO.getGtcYN())) {
			if (isOBU.equals("Y")) {
				outputVO_707_S = sot707.verifyESBPurchaseBN_GTC_OBU(inputVO_707);	// 長效單、預約單
			} else {
				outputVO_707_S = sot707.verifyESBPurchaseBN_GTC(inputVO_707); 		// 長效單、預約單				
			}
		} else {
			// 若為金錢信託，先 call NJBRVX2
			if (StringUtils.equals("M", inputVO.getTrustTS())) {
				// 債券申購電文 : 原客戶ID + 信託/扣款/收益入帳 = NULL
				outputVO_707_M = sot707.verifyESBPurchaseBN_TRUST(inputVO_707); //當日單
				
				// 債券申購電文 : 客戶ID=99331241 + 信託/扣款/收益入帳 = 原畫面值
			}
			if (isOBU.equals("Y")) {
				outputVO_707_S = sot707.verifyESBPurchaseBN_OBU(inputVO_707); //當日單
			} else {
				outputVO_707_S = sot707.verifyESBPurchaseBN(inputVO_707); //當日單				
			}
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
		if (CollectionUtils.isNotEmpty(warningList)) {
			outputVO.setWarningMsg("");
			XMLInfo xmlInfo = new XMLInfo();

			for (String msg : warningList) {
				outputVO.setWarningMsg(xmlInfo.getErrorMsg(msg) + "\n" + outputVO.getWarningMsg());
			}
		}

		this.sendRtnObject(outputVO);
	}

	//傳送OP交易並列印表單
	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		dam = this.getDataAccessManager();
		//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			mainVo.setREC_SEQ(inputVO.getRecSEQ());//錄音序號
		}
		dam.update(mainVo);

		// 檢核電文
		String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707 = new SOT707OutputVO();
		inputVO_707.setProdType("2");//產品類別  1：SN 2：海外債
		inputVO_707.setCheckType("2");//電文確認碼 1:檢核  2:確認
		inputVO_707.setPurchaseAmt(inputVO.getPurchaseAmt());
		inputVO_707.setTradeSeq(inputVO.getTradeSEQ());
		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
		
		if (isOBU.equals("Y")) {
			if ("Y".equals(inputVO.getGtcYN()) || "P".equals(inputVO.getGtcYN()))
				outputVO_707 = sot707.verifyESBPurchaseBN_GTC_OBU(inputVO_707);	// 長效單
			else
				outputVO_707 = sot707.verifyESBPurchaseBN_OBU(inputVO_707);		// 當日單
		} else {
			if ("Y".equals(inputVO.getGtcYN()) || "P".equals(inputVO.getGtcYN()))
				outputVO_707 = sot707.verifyESBPurchaseBN_GTC(inputVO_707);		// 長效單
			else
				outputVO_707 = sot707.verifyESBPurchaseBN(inputVO_707);			// 當日單			
		}

		String errorMsg = outputVO_707.getErrorMsg();
		List<String> warningList = outputVO_707.getWarningCode();
		if (!"".equals(errorMsg) && null != errorMsg) {
			logger.error("goOP() sot707.verifyESBPurchaseBN:" + errorMsg);
			outputVO.setErrorMsg(errorMsg);
		} else {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

			vo.setTRADE_STATUS("3"); //3:傳送OP交易並列印表單
			//			if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			//				vo.setREC_SEQ(inputVO.getRecSEQ());
			//			}

			vo.setSEND_DATE(new Timestamp(System.currentTimeMillis())); //傳送OP後要設SEND_DATE
			dam.update(vo);

			// for CBS 測試用
			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
		}

		//若有警示訊息
		if (CollectionUtils.isNotEmpty(warningList)) {
			outputVO.setWarningMsg("");
			XMLInfo xmlInfo = new XMLInfo();

			for (String msg : warningList) {
				outputVO.setWarningMsg(xmlInfo.getErrorMsg(msg) + "\n" + outputVO.getWarningMsg());
			}
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 網銀/行銀快速下單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void goBANK(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		dam = this.getDataAccessManager();

		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		//mainVo.setPROSPECTUS_TYPE(this.getProspectusType(inputVO)); // MIMI 2/18 (公開說明書) 欄位都先寫死放1
		if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			mainVo.setREC_SEQ(inputVO.getRecSEQ());//錄音序號
		}
		dam.update(mainVo);

		// 海外債檢核電文NJBRVA9
		String isOBU = StringUtils.isBlank(inputVO.getIsOBU()) ? "" : inputVO.getIsOBU();
		SOT707InputVO inputVO_707 = new SOT707InputVO();
		SOT707OutputVO outputVO_707 = new SOT707OutputVO();

		inputVO_707.setProdType("2");//產品類別  1：SN 2：海外債
		inputVO_707.setCheckType("1");//電文確認碼 1:檢核  2:確認
		inputVO_707.setPurchaseAmt(inputVO.getPurchaseAmt());
		inputVO_707.setTradeSeq(inputVO.getTradeSEQ());

		SOT707 sot707 = (SOT707) PlatformContext.getBean("sot707");
//		outputVO_707 = sot707.verifyESBPurchaseBN(inputVO_707);
		if (isOBU.equals("Y")) {
			outputVO_707 = sot707.verifyESBPurchaseBN_OBU(inputVO_707);
		} else {
			outputVO_707 = sot707.verifyESBPurchaseBN(inputVO_707);
		}
		
		if (StringUtils.isNotBlank(outputVO_707.getErrorMsg())) {
			outputVO.setErrorMsg(outputVO_707.getErrorMsg());
		} else {
			//網路快速下單電文EBPMN
			outputVO_707 = sot707.sendESBWebPurchaseBN(inputVO_707); 
			
			if (StringUtils.isNotBlank(outputVO_707.getErrorMsg()) && !StringUtils.equals("0000", outputVO_707.getErrorCode())) {
				//有錯誤代碼，且不為"0000"
				outputVO.setErrorMsg(outputVO_707.getErrorMsg());
			} else {
				TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
				vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

				vo.setTRADE_STATUS("4"); //4:網銀快速下單
				vo.setSEND_DATE(new Timestamp(System.currentTimeMillis()));
				dam.update(vo);
			}
		}

		this.sendRtnObject(outputVO);
	}
	
	//取消
	public void delTrade(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM TBSOT_BN_TRADE_D WHERE TRADE_SEQ = :tradeSEQ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());
		dam.exeUpdate(queryCondition);

		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		dam.delete(vo);

		this.sendRtnObject(null);
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

	//查詢限價維護時間是否為下單日期
	public void limitPrice(Object body, IPrimitiveMap header) throws JBranchException {
		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TRUNC(CREATETIME) AS CREATETIME, ACT_TYPE, REVIEW_STATUS ");
		sb.append("FROM TBPRD_LIMITED_PRICE_REVIEW ");
		sb.append("WHERE CREATETIME = (SELECT MAX(CREATETIME) FROM TBPRD_LIMITED_PRICE_REVIEW ");
		sb.append("WHERE PRD_ID = :prd_id AND CUST_ID = :cust_id ) ");
		sb.append("AND TRUNC(CREATETIME) = TRUNC(SYSDATE) ");
		sb.append("AND PRD_ID = :prd_id AND CUST_ID = :cust_id ");
		sb.append("AND ACT_TYPE <> 'D' ");
		sb.append("AND REVIEW_STATUS = 'Y' ");

		queryCondition.setObject("prd_id", inputVO.getProdID());
		queryCondition.setObject("cust_id", inputVO.getCustID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0 && list.get(0).get("CREATETIME") != null) {
			outputVO.setLimitPrice(true);
		} else {
			outputVO.setLimitPrice(false);
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 取得長效單最大委託迄日 (預設20個營業日)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getMaxGtcEndDate(Object body, IPrimitiveMap header) throws Exception {
//		SOT310OutputVO outputVO = new SOT310OutputVO();
//		String maxdays = "20"; //預設20個營業日
//		dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
//
//		StringBuilder sb = new StringBuilder();
//		sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER ");
//		sb.append("WHERE PARAM_CODE = '1' ");
//		sb.append("AND PARAM_TYPE = 'SOT.BOND_MAX_GTC_DATE' ");
//		queryCondition.setQueryString(sb.toString());
//
//		List<Map<String, Object>> days = dam.exeQuery(queryCondition);
//		if (CollectionUtils.isNotEmpty(days))
//			maxdays = days.get(0).get("PARAM_NAME").toString();
//
//		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sb = new StringBuilder();
//		sb.append("SELECT PABTH_UTIL.FC_getBusiDay(SYSDATE, 'TWD', :days) AS BUSI_DAY FROM DUAL ");
//		queryCondition.setObject("days", maxdays);
//		queryCondition.setQueryString(sb.toString());
//
//		List<Map<String, Object>> tradeDateList = dam.exeQuery(queryCondition);
//
//		outputVO.setMaxGtcEndDate((Timestamp) tradeDateList.get(0).get("BUSI_DAY"));
//
//		this.sendRtnObject(outputVO);
		
		// 指定交易日限定為次營業日起的20個營業日內，AS400仍要檢核是否有遇到臺灣、美國或香港假日。
		SOT310OutputVO outputVO = new SOT310OutputVO();
		String today = this.toChineseYearMMdd(new Date());	// 將系統日轉為民國年
		Date startMinDate = null;
		Date startMaxDate = null;
		Date endMinDate = null;
		Date endMaxDate = null;
		List<NJWEEA70> njweea70List = this.getNJWEEA70();
		int i = 0;
		for (NJWEEA70 njweea70 : njweea70List) {
			if (njweea70.getTxtCode().equals("Y")) {
				if (startMinDate == null) {
					String txtDt = njweea70.getTxtDt();
					if (Integer.parseInt(txtDt) > Integer.parseInt(today)) {
						startMinDate = this.toAdYearMMdd(njweea70.getTxtDt());
						i++;
					}
				} else {
					i++;					
				}
				if (i == 2) {
					endMinDate = this.toAdYearMMdd(njweea70.getTxtDt());
				}
				if (i == 20) {
					// 民國年轉換成西元年
					startMaxDate = this.toAdYearMMdd(njweea70.getTxtDt());
				}
				if (i == 24) {
					// 民國年轉換成西元年
					endMaxDate = this.toAdYearMMdd(njweea70.getTxtDt());
					break;
				}
			}
		}
		outputVO.setMinGtcStartDate(startMinDate);
		outputVO.setMaxGtcStartDate(startMaxDate);
		outputVO.setMinGtcEndDate(endMinDate);
		outputVO.setMaxGtcEndDate(endMaxDate);
		this.sendRtnObject(outputVO);

	}
		
	/***
	 * 集中度檢核結果
	 * @param inputVO
	 * @return Y:可交易 W:超過通知門檻 N:不可交易
	 * @throws Exception
	 */
	private String getOverCentRateYN(SOT310InputVO inputVO, BigDecimal rate) throws Exception {
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		//集中度加上新增商品金額
		inputVO714.setProdType("4");//海外債
		inputVO714.setBuyAmt(inputVO.getTrustAmt().multiply(rate));
				
		//查詢客戶高資產集中度資料
		WMSHACRDataVO cRateData = sot714.getCentRateData(inputVO714);
		return cRateData.getVALIDATE_YN(); //Y:可交易 W:超過通知門檻 N:不可交易
	}
	
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private boolean validHnwcRiskValue(SOT310InputVO inputVO, BigDecimal rate) throws Exception {
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
	
	/**
	 * 檢查：
	 * 1. 長效單、預約單：委託日期不可為 臺灣、香港、美國之假日(電文_NJWEEA70)
	 * 2. 長效單：最短2個營業日、最長5個營業日（需排除美國、香港、台灣休假日）
	 * **/
	public void checkGtcDate(Object body, IPrimitiveMap header) throws Exception {
		SOT310InputVO inputVO = (SOT310InputVO) body;
		SOT310OutputVO outputVO = new SOT310OutputVO();
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		List<NJWEEA70> list = this.getNJWEEA70();
		
		Date gtcStartDate = null;
		Date gtcEndDate = null;
		if ("Y".equals(inputVO.getGtcYN())) {
			// 長效單需檢查起、迄日
			gtcStartDate = inputVO.getGtcStartDate();
			gtcEndDate = inputVO.getGtcEndDate();
		} else if ("P".equals(inputVO.getGtcYN())) {
			// 預約單需檢查起日
			gtcStartDate = inputVO.getGtcStartDate();
		}
		String sDateFlag = "N";
		String eDateFlag = "N";
		String sDate = "";
		String eDate = "";
		int dates = 0;
		if (gtcStartDate != null) {
			sDate = this.toChineseYearMMdd(gtcStartDate); // 西元轉民國 ex.01130720
			sDateFlag = this.gtcDateYN(list, sDate);
		}
		if (gtcEndDate != null) {
			eDate = this.toChineseYearMMdd(gtcEndDate);	 // 西元轉民國 ex.01130720
			eDateFlag = this.gtcDateYN(list, eDate);
		}
		if ("Y".equals(inputVO.getGtcYN())) {
			dates = this.countDates(list, sDate, eDate);
		}
		
		Map<String, Object> map = new HashMap<>();
		map.put("START_DATE_YN", sDateFlag);
		map.put("END_DATE_YN", eDateFlag);
		map.put("COUNT_DATES", dates);
		resultList.add(map);
		outputVO.setResultList(resultList);
		this.sendRtnObject(outputVO);
	}
	
	private int countDates(List<NJWEEA70> list, String sDate, String eDate) throws Exception {
		int i = 0;
		if (StringUtils.isNotBlank(sDate) && StringUtils.isNotBlank(eDate)) {
			boolean startCount = false;
			for (NJWEEA70 vo : list) {
				if (sDate.equals(vo.getTxtDt())) {
					startCount = true;
				}
				if (startCount && "Y".equals(vo.getTxtCode())) {
					i++;
				}
				if (eDate.equals(vo.getTxtDt())) {
					break;
				}
			}			
		}
		return i;
	}
	
	private String gtcDateYN(List<NJWEEA70> list, String gtcDate) throws Exception {
		String flag = "N";
		for (NJWEEA70 vo : list) {
			if (gtcDate.equals(vo.getTxtDt())) {
				String txtCode = vo.getTxtCode();
				flag = txtCode.equals("Y") ? "Y" : "N";
				break;
			}
		}
		return flag;
	}
	
	/**
	 * NJWEEA70_債券預約單/長效單營業日註記
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public List<NJWEEA70> getNJWEEA70() throws Exception {
		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, EsbSotCons.NJWEEA70);
		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
//		NJWEEA70InputVO njweea70inputvo = new NJWEEA70InputVO();
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		NJWEEA70OutputVO njweea70OutputVO = new NJWEEA70OutputVO();
		List<NJWEEA70> results = new ArrayList<NJWEEA70>();
		NJWEEA70 njweea70 = new NJWEEA70();
		
		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();
			
			String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
			
			/**
			 * 下行電文會有2個Format的格式
			 * Header.‧HFMTID =0001：債券預約單/長效單營業日註記
			 * Header.‧HFMTID =E001：錯誤訊息使用
			 */
			if("0001".equals(hfmtid)) {
				njweea70OutputVO = esbUtilOutputVO.getNjweea70OutputVO();
				for(NJWEEA70OutputDetailVO detail : njweea70OutputVO.getDetails()) {
					njweea70.setTxtDt(detail.getTxtDt());		// 日期(民國年) ex.01130607
					njweea70.setTxtCode(detail.getTxtCode());	// 營業日註記_空白：非營業日、Y：營業日

					results.add(njweea70);
					njweea70 = new NJWEEA70();
				}
			} else if("E001".equals(hfmtid)) {
				if(StringUtils.isNotBlank(njweea70OutputVO.getMSGID())) {
					throw new JBranchException(String.format("%s:%s", njweea70OutputVO.getMSGID(), njweea70OutputVO.getMSGTXT()));
				} 
			}
		}
		return results;
	}
	
	public void checkTime(Object body, IPrimitiveMap header) throws Exception {
		SOT310OutputVO outputVO = new SOT310OutputVO();
		String sotYN = "Y";
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.RESERVE_DATE_TIMESTAMP_BND' ");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(qc);
		Map<String, Object> map = list.get(0);
		String limitTime = map.get("PARAM_CODE") != null ? map.get("PARAM_CODE").toString() : "1500";
		String now = sdf.format(new Date());
		if (Integer.parseInt(now) > Integer.parseInt(limitTime)) {
			sotYN = "N";
		}
		outputVO.setSotYN(sotYN);
		this.sendRtnObject(outputVO);
	}
}