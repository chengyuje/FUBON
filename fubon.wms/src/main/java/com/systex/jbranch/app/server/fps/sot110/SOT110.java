package com.systex.jbranch.app.server.fps.sot110;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.util.BigDecimalUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.common.fps.table.TBCRM_BRG_APPLY_SINGLEVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421;
import com.systex.jbranch.app.server.fps.crm421.CRM421InputVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421OutputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110;
import com.systex.jbranch.app.server.fps.prd110.PRD110InputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110OutputVO;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot701.SotUtils;
import com.systex.jbranch.app.server.fps.sot703.SOT703;
import com.systex.jbranch.app.server.fps.sot703.SOT703InputVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703OutputVO;
import com.systex.jbranch.app.server.fps.sot709.DefaultFeeRateVO;
import com.systex.jbranch.app.server.fps.sot709.MeteringFeeRateVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709;
import com.systex.jbranch.app.server.fps.sot709.SOT709InputVO;
import com.systex.jbranch.app.server.fps.sot709.SOT709OutputVO;
import com.systex.jbranch.app.server.fps.sot709.SingleFeeRateVO;
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
 * sot110
 * 
 * @author valentino
 * @date 2016/10/24
 * @spec null
 */
@Component("sot110")
@Scope("request")
public class SOT110 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT110.class);
	private static String TBSOT_DETAIL = "TBSOT_NF_PURCHASE_D"; //基金單筆、定期(不)定額申購明細檔 
	private SimpleDateFormat SDFYYYY_MM_DDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat SDFMM = new SimpleDateFormat("MM");
    
	/**
	 * 暫存
	 */
	private static String MAIN_TRADE_STATUS_1_TEMP = "1";
	/**
	 * 風控檢核中
	 */
	private static String MAIN_TRADE_STATUS_2_VERIFY = "2";
	/**
	 * 傳送OP交易並列印表單
	 */
	private static String MAIN_TRADE_STATUS_3_OP = "3";
	/**
	 * 網銀快速下單
	 */
	private static String MAIN_TRADE_STATUS_4_WEBBANK = "4";

	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
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
		outputVO.setCustRemarks(outputVO_701.getFp032675DataVO().getCustRemarks());
		outputVO.setIsOBU(outputVO_701.getFp032675DataVO().getObuFlag());
		outputVO.setIsAgreeProdAdv(outputVO_701.getFp032675DataVO().getCustTxFlag());		// 交易中不需要再去做邏輯判斷是Y或N，可以直接回存DB
		outputVO.setPiRemark(outputVO_701.getFp032675DataVO().getCustProRemark());			// 這個欄位會直接回存電文DESC欄位資料
		outputVO.setBargainDueDate(outputVO_701.getDueDate());								// 期間議價效期
		outputVO.setCustProType(outputVO_701.getFp032675DataVO().getCustProType()); 		// 專業投資人類型_1：大專投、2：小專投
		outputVO.setNoSale(outputVO_701.getFp032675DataVO().getNoSale()); 					// FC032675商品禁銷註記
		outputVO.setRejectProdFlag(outputVO_701.getFp032675DataVO().getRejectProdFlag());	// FC032675客戶拒銷註記
		if (outputVO_701.getCustPLDataVO() != null) {
			outputVO.setPlNotifyWays(outputVO_701.getCustPLDataVO().getPlMsg());
			outputVO.setTakeProfitPerc(outputVO_701.getCustPLDataVO().getTakeProfitPerc());
			outputVO.setStopLossPerc(outputVO_701.getCustPLDataVO().getStopLossPerc());
		} else {
			logger.error("Empty outputVO_701.getCustPLDataVO()");
		}
		
		// FOR 境外私募基金新增銷售客群
		String proCorpInv1 = outputVO_701.getFp032675DataVO().getProCorpInv() == null ? "" : outputVO_701.getFp032675DataVO().getProCorpInv();
		String proCorpInv2 = outputVO_701.getFp032675DataVO().getProCorpInv2() == null ? "" : outputVO_701.getFp032675DataVO().getProCorpInv2();
		String proCorpInv = (proCorpInv1.equals("Y") || proCorpInv2.equals("Y")) ? "Y" : "N";
		outputVO.setProCorpInv(proCorpInv);													// FC032675.專業機構投資人
		outputVO.setHighYieldCorp(outputVO_701.getFp032675DataVO().getHighYieldCorp());		// FC032675.高淨值法人

		outputVO.setIsFirstTrade((outputVO_701.getIsFirstTrade()) ? "Y" : "N");//查詢客戶是否為首購 

		// FOR CBS測試日期修改
		outputVO.setKycDueDateUseful(outputVO_701.getCustKYCDataVO().isKycDueDateUseful());

		//扣款帳號
		List<AcctVO> debitAcct = outputVO_701.getCustAcctDataVO().getDebitAcctList();
		List<Map<String, Object>> debitAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : debitAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", vo.getAcctNo() + "_" + vo.getCurrency());
			map.put("DATA", vo.getAcctNo() + "_" + vo.getCurrency());
			map.put("ACCT_BALACNE", vo.getAcctBalance());
			map.put("AVB_BALANCE", vo.getAvbBalance());
			map.put("CURRENCY", vo.getCurrency());
			debitAcctList.add(map);
		}
		outputVO.setDebitAcct(debitAcctList);

		//信託帳號
		List<AcctVO> trustAcct = outputVO_701.getCustAcctDataVO().getTrustAcctList();
		List<Map<String, Object>> trustAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : trustAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("ACCT_BALACNE", vo.getAcctBalance());
			trustAcctList.add(map);
		}
		outputVO.setTrustAcct(trustAcctList);

		//收益入帳帳號
		List<AcctVO> creditAcct = outputVO_701.getCustAcctDataVO().getCreditAcctList();
		List<Map<String, Object>> creditAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : creditAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("ACCT_BALACNE", vo.getAcctBalance());
			map.put("AVB_BALANCE", vo.getAvbBalance());
			map.put("CURRENCY", vo.getCurrency());
			creditAcctList.add(map);
		}
		outputVO.setCreditAcct(creditAcctList);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT 1 AS EXIST FROM DUAL ");
		sql.append(" WHERE EXISTS (SELECT 'x' FROM TBORG_MEMBER WHERE CUST_ID = :isBanker ");
		sql.append(" and CHANGE_FLAG IN ('A', 'M', 'P') and SERVICE_FLAG = 'A') ");

		queryCondition.setObject("isBanker", inputVO.getCustID());
		queryCondition.setQueryString(sql.toString());

		List listBanker = dam.exeQuery(queryCondition);
		String isBanker = CollectionUtils.isEmpty(listBanker) ? "N" : "Y";
		outputVO.setIsBanker(isBanker);
		outputVO.setDeathFlag(outputVO_701.getFp032675DataVO().getDeathFlag());
		outputVO.setIsInterdict((null != outputVO_701.getCustNoteDataVO() && outputVO_701.getCustNoteDataVO().getInterdict()) ? "Y" : "N");

		//查詢客戶高資產註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hNWCDataVO = sot714.getHNWCData(inputVO.getCustID());
		outputVO.setHnwcYN(StringUtils.isNotBlank(hNWCDataVO.getValidHnwcYN()) ? hNWCDataVO.getValidHnwcYN() : "N");
		outputVO.setHnwcServiceYN(hNWCDataVO.getHnwcService());
		
		outputVO.setFlagNumber(outputVO_701.getFlagNumber());
		
		this.sendRtnObject(outputVO);
	}

	/**
	 * 主檔沒有存非常規交易的條件，從整合交易畫面進風控頁的時候無法取得 只能重打電文 只打有關非常規交易的
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		Boolean isFirstTrade = false;

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);
		isFirstTrade = sot701.getIsCustFirstTrade(inputVO_701);

		outputVO.setIsFirstTrade((isFirstTrade) ? "Y" : "N"); //是否首購
		outputVO.setCustRemarks(fp032675DataVO.getCustRemarks()); // 弱勢
		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag()); // 年齡小於70
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag()); // 教育程度國中以上(不含國中)
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag()); // 未領有全民健保重大傷病證明

		this.sendRtnObject(outputVO);
	}

	/**
	 * 檢查是否需要錄音
	 * 
	 * @return Y/ N
	 * @throws JBranchException
	 */

	/**
	 * 查詢購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT A.*, F.OVS_PRIVATE_YN, P.MIN_PURCHASE_AMT AS MIN_BUYAMT_OVSPRI, P.TRADE_DATE AS TRADE_DATE_OVSPRI ");
		sb.append(" FROM TBSOT_NF_PURCHASE_D A ");
		sb.append(" LEFT JOIN TBPRD_FUND F ON F.PRD_ID = A.PROD_ID ");
		sb.append(" LEFT JOIN TBPRD_FUND_OVS_PRIVATE P ON P.PRD_ID = A.PROD_ID AND P.TRADE_TYPE = '1' AND TRUNC(SYSDATE) BETWEEN TRUNC(P.START_DATE) AND TRUNC(P.END_DATE) ");
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

		//此交易中是否有後收型基金
		StringBuffer sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("select 1 from TBSOT_NF_PURCHASE_D D INNER JOIN TBPRD_FUND F on F.PRD_ID = D.PROD_ID ");
		sql.append(" where D.TRADE_SEQ = :trade_seq and F.IS_BACKEND = 'Y' ");
		queryCondition.setObject("trade_seq", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> b_list = dam.exeQuery(queryCondition);

		if (CollectionUtils.isNotEmpty(b_list))
			outputVO.setIsBackendInCart("Y"); //交易中其中一筆為後收型基金		
		else
			outputVO.setIsBackendInCart("N");

		this.sendRtnObject(outputVO);
	}

	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {

		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
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
			sb.append("SELECT f.*,i.FUS20,i.FUS40, P.MIN_PURCHASE_AMT AS MIN_BUYAMT_OVSPRI, P.TRADE_DATE AS TRADE_DATE_OVSPRI ");
			sb.append("FROM TBPRD_FUND f ");
			sb.append("LEFT JOIN TBPRD_FUNDINFO i on (f.PRD_ID=i.PRD_ID) ");
			sb.append("LEFT JOIN TBPRD_FUND_OVS_PRIVATE P ON P.PRD_ID = f.PRD_ID AND P.TRADE_TYPE = '1' AND TRUNC(SYSDATE) BETWEEN TRUNC(P.START_DATE) AND TRUNC(P.END_DATE) ");
			sb.append("WHERE f.PRD_ID = :prodID ");
			queryCondition.setObject("prodID", inputVO.getProdId());
			queryCondition.setQueryString(sb.toString());

			outputVO.setProdDTL(dam.exeQuery(queryCondition));
			
			//若為境外私募基金，檢核申購人數上限
			if(CollectionUtils.isNotEmpty(outputVO.getProdDTL()) && StringUtils.equals("Y", (String)outputVO.getProdDTL().get(0).get("OVS_PRIVATE_YN"))) {
				if(isOverOvspriFundLimit(inputVO)) {
					outputVO.setErrorMsg("境外私募基金商品超過申購人數上限");
				}
			}
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 下單 加入購物車
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//高資產客戶投組風險權值檢核
		//只限特金，客戶風險屬性非C4，有選擇越級商品
		if(!StringUtils.equals("M", inputVO.getTrustTS()) && !StringUtils.equals("C4", inputVO.getKycLV())
				&& (Integer.parseInt(inputVO.getProdRiskLv().substring(1))) > (Integer.parseInt(inputVO.getKycLV().substring(1)))
				&& StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN())) {
			String errorMsg = getHnwcRiskValue(inputVO);
			if(StringUtils.isNotBlank(errorMsg)) {
				throw new APException(errorMsg);
			}			
		}
				
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
			mainVO.setPROD_TYPE("1"); //1：基金
			mainVO.setTRADE_TYPE("1");//1：單筆申購
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLV());
			if (inputVO.getKycDueDate() != null)
				mainVO.setKYC_DUE_DATE(new Timestamp(inputVO.getKycDueDate().getTime()));
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			if (inputVO.getPiDueDate() != null)
				mainVO.setPI_DUE_DATE(new Timestamp(inputVO.getPiDueDate().getTime()));
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			mainVO.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
			if (inputVO.getBargainDueDate() != null)
				mainVO.setBARGAIN_DUE_DATE(new Timestamp(inputVO.getBargainDueDate().getTime()));
			mainVO.setTRADE_STATUS(MAIN_TRADE_STATUS_1_TEMP); //1:暫存
			mainVO.setIS_BARGAIN_NEEDED(StringUtils.equals("A", inputVO.getFeeType()) ? "Y" : "N");
			mainVO.setBARGAIN_FEE_FLAG(StringUtils.equals("A", inputVO.getFeeType()) ? "1" : null);

			//是否需要錄音  需要首購判斷
			/*Boolean isFirstTrade = false;
			SOT701InputVO inputVO_701 = new SOT701InputVO();
			inputVO_701.setCustID(inputVO.getCustID());
			inputVO_701.setProdType(inputVO.getProdType());
			inputVO_701.setTradeType(inputVO.getTradeType());
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			isFirstTrade = sot701.getIsCustFirstTrade(inputVO_701);*/
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
				SotUtils utils = (SotUtils) PlatformContext.getBean("sotUtils");
				mainVO.setBRANCH_NBR(utils.getBranchNbr(inputVO.getTrustTS(), String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH))));
			}
			
			//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
			mainVO.setTRUST_TRADE_TYPE(inputVO.getTrustTS());  //M:金錢信託  S:特金交易
			
			//0000275: 金錢信託受監護受輔助宣告交易控管調整
			if(StringUtils.isBlank(inputVO.getGUARDIANSHIP_FLAG())){
				mainVO.setGUARDIANSHIP_FLAG(" ");
			}else{
				mainVO.setGUARDIANSHIP_FLAG(inputVO.getGUARDIANSHIP_FLAG());
			}
			
			mainVO.setIS_WEB(inputVO.getIsWeb());//是否為快速申購(Y/N)\
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
			vo.setTRADE_STATUS(MAIN_TRADE_STATUS_1_TEMP); //暫存
			vo.setHNWC_YN(inputVO.getHnwcYN());
			vo.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());

			dam.update(vo);
		}

		TBSOT_NF_PURCHASE_DPK dPK = new TBSOT_NF_PURCHASE_DPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(inputVO.getSeqNo());
		TBSOT_NF_PURCHASE_DVO dVO = new TBSOT_NF_PURCHASE_DVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_PURCHASE_DVO) dam.findByPKey(TBSOT_NF_PURCHASE_DVO.TABLE_UID, dVO.getcomp_id());

		if (null != dVO) {
			dam.delete(dVO);
		}

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
		TBSOT_NF_PURCHASE_DPK dtlPK = new TBSOT_NF_PURCHASE_DPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(newTrade_SEQNO);

		TBSOT_NF_PURCHASE_DVO dtlVO = new TBSOT_NF_PURCHASE_DVO();
		dtlVO.setcomp_id(dtlPK);

		//Map<String, Object> map = inputVO.getCarList().get(0);
		//dtlVO.setBATCH_SEQ(ObjectUtils.toString(map.get("BATCH_SEQ")));     //下單批號  Varchar2 (30)
		//dtlVO.setBATCH_NO(ObjectUtils.toString(map.get("BATCH_NO")));     //下單批號流水號  Number (6)
		dtlVO.setTRADE_SUB_TYPE(inputVO.getTradeSubType()); //信託型態  Char (1)
		dtlVO.setPROD_ID(inputVO.getProdId()); //基金代號  Varchar2 (16)
		dtlVO.setPROD_NAME(inputVO.getProdName()); //基金名稱  Varchar2 (80)
		dtlVO.setPROD_CURR(inputVO.getProdCurr()); //計價幣別  Char (3)
		dtlVO.setPROD_RISK_LV(inputVO.getProdRiskLv()); //產品風險等級  Varchar2 (3)

		if (inputVO.getProdMinBuyAmt() != null)
			dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt()); //最低申購金額  Number (16, 2)

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

		if (inputVO.getTradeSubType().matches("1")) {
			dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt()); //申購金額  Number (16, 2)
		}

		//單筆申購 沒有自動換匯  dtlVO.setIS_AUTO_CX(inputVO.getIsAutoCx());     //是否自動換匯  Char (1)

		/*
		11/23 Cathy 耀婷: 手續費優惠方式為"請選擇"，手續費率要放最優手續費，Fee
		  Fee
		 FeeType = "C"
		 手續費優惠方式為"申請議價"才會有議價狀態
		 手續費方式選擇事先議價的單次議價，才會有議價編號
		 */
		dtlVO.setFEE_TYPE(StringUtils.isNotBlank(inputVO.getFeeType()) ? inputVO.getFeeType() : "C"); //手續費優惠方式
		dtlVO.setBARGAIN_STATUS(StringUtils.equals("A", inputVO.getFeeType()) ? "1" : null); //手續費方式為申請議價時，才放這個欄位資料
		dtlVO.setBARGAIN_APPLY_SEQ(inputVO.getBargainApplySEQ()); //手續費方式為事先單次議價或申請議價，才會有議價編號欄位

		if (inputVO.getTradeSubType().matches("1")) {
			dtlVO.setDEFAULT_FEE_RATE(inputVO.getDefaultFeeRate()); //表定手續費率  Number (6, 3)
			dtlVO.setFEE_RATE(inputVO.getFeeRate()); //手續費率  Number (6, 3)
			dtlVO.setFEE(inputVO.getFee()); //手續費金額  Number (13, 2)
			dtlVO.setFEE_DISCOUNT(inputVO.getFeeDiscount()); //手續費折數  Number (6, 3)
		}

		dtlVO.setDEBIT_ACCT(inputVO.getDebitAcct()); //扣款帳號  Varchar2 (16)
		dtlVO.setTRUST_ACCT(inputVO.getTrustAcct()); //信託帳號  Varchar2 (16)
		dtlVO.setCREDIT_ACCT(inputVO.getCreditAcct()); //收益入帳帳號  Varchar2 (16)
		dtlVO.setPROSPECTUS_TYPE(inputVO.getProspectusType()); //公開說明書選項

		dtlVO.setTRADE_DATE_TYPE(inputVO.getTradeDateType()); //交易日期類別  Char (1)

		Date tradeDate = null;
		if ("2".equals(inputVO.getTradeDateType()) && StringUtils.isNotBlank(inputVO.getReservationTradeDate())) {//2預約
			try {
				tradeDate = SDFYYYY_MM_DDHHMMSS.parse(inputVO.getReservationTradeDate());
			} catch (ParseException e) {
				throw new JBranchException(e);
			}
		} else {//即時
			tradeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate());
		}
		dtlVO.setTRADE_DATE(new Timestamp(tradeDate.getTime())); //交易日期  Date

		dtlVO.setSTOP_LOSS_PERC(inputVO.getStopLossPerc()); //停損點(%)  Number (6, 3)
		dtlVO.setTAKE_PROFIT_PERC(inputVO.getTakeProfitPerc()); //停利點(%)  Number (6, 3)
		dtlVO.setPL_NOTIFY_WAYS(inputVO.getPlNotifyWays()); //停損停利通知方式  NVarchar2 (100)
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編  Varchar2 (16)
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名  Varchar2 (100)
		dtlVO.setPLAN_ID(inputVO.getPlanID()); //投組編號 

		if (inputVO.getFeeType().matches("C|E")) { // C：最優手續費/E:次數型團體優惠代碼 要記錄團體優惠
			dtlVO.setGROUP_OFA(inputVO.getGroupOfa());// 團體優惠代碼(DefaultFeeRateVO)
		}

		dtlVO.setNOT_VERTIFY(inputVO.getNotVertify());//未核備欄位(TBPRD_FUNDINFO.FUS40)
		
		//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
		dtlVO.setCONTRACT_ID(inputVO.getContractID()); //契約編號
		dtlVO.setTRUST_PEOP_NUM(StringUtils.isNotEmpty(inputVO.getTrustPeopNum()) ? inputVO.getTrustPeopNum() : "N");
		
		dam.create(dtlVO);
		this.logger.debug("save() tradeSEQ:" + dtlPK.getTRADE_SEQ() + " ,seqNo:" + dtlPK.getSEQ_NO());

		// 2.檢核電文
		SOT703InputVO inputVO_703 = new SOT703InputVO();
		SOT703OutputVO outputVO_703 = new SOT703OutputVO();
		SOT703OutputVO outputVO_703M = new SOT703OutputVO(); //金錢信託錯誤訊息

		inputVO_703.setTradeSeq(inputVO.getTradeSEQ());
		inputVO_703.setSeqNo(newTrade_SEQNO.toString());

		SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
		try {
			if(StringUtils.equals(inputVO.getTrustTS(), "M")){ //金錢信託驗證電文
				outputVO_703M = sot703.verifyESBPurchaseXF(inputVO_703); //第一道新電文
				outputVO_703 = sot703.verifyESBPurchaseNF(inputVO_703); //第二道舊電文
			}else{
				outputVO_703 = sot703.verifyESBPurchaseNF(inputVO_703); //特金交易驗證電文
			}			
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			sot712 = (SOT712) PlatformContext.getBean("sot712");
			errorMsg = sot712.inquireErrorCode(e.getMessage());
			logger.error(String.format("基金申購驗證電文sot703.verifyESBPurchaseNF錯誤:%s", errorMsg), e);
			throw new JBranchException(String.format("基金申購驗證電文錯誤:%s", errorMsg), e);
		}

		String errorMsg = outputVO_703.getErrorMsg();
		String errorMsgM = outputVO_703M.getErrorMsg();
		if (StringUtils.isNotBlank(errorMsg) || (StringUtils.isNotBlank(errorMsgM) && inputVO.getTrustTS().equals("M"))) {
			logger.error(String.format("sot703.verifyESBPurchaseNF()  tradeSeq:%s , seqNo:%s  outputVO_703.ErrorMsg:%s", inputVO_703.getTradeSeq(), inputVO_703.getSeqNo(), outputVO_703.getErrorMsg()));
            
			TBSOT_NF_PURCHASE_DPK errPK = new TBSOT_NF_PURCHASE_DPK();
			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			errPK.setSEQ_NO(newTrade_SEQNO);
			TBSOT_NF_PURCHASE_DVO errVO = new TBSOT_NF_PURCHASE_DVO();
			errVO.setcomp_id(errPK);
			errVO = (TBSOT_NF_PURCHASE_DVO) dam.findByPKey(TBSOT_NF_PURCHASE_DVO.TABLE_UID, errVO.getcomp_id());

			//刪除明細
			if (null != errVO) {
				dam.delete(errVO);
			}

			//===若無明細，則將主檔也刪除
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * ");
			sb.append(String.format("FROM %s ", TBSOT_DETAIL));
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

				dam.delete(mVO);
			}
			//===
            if(inputVO.getTrustTS().equals("M")){
            	outputVO.setErrorMsg((StringUtils.isNotBlank(errorMsgM) ? "NFBRX1電文錯誤訊息: " + errorMsgM : "") + (StringUtils.isNotBlank(errorMsg) ? "NFBRN1電文錯誤訊息: " + errorMsg : "")  );
            }else{
            	outputVO.setErrorMsg(errorMsg);
            }
			
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 相當於議價的加入清單; 存檔並送單次議價檢核電文
	 * 
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private CRM421OutputVO applyBargain(SOT110InputVO inputVO) throws Exception {
		CRM421OutputVO crm421OutputVO = new CRM421OutputVO();
		CRM421InputVO crm421InputVO = new CRM421InputVO();

		//initial data
		crm421InputVO.setApply_type("1"); //基金單筆申購
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
	 * 下單 下一步
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void next(Object body, IPrimitiveMap header) throws Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		SOT110InputVO inputVO = (SOT110InputVO) body;
		SOT110OutputVO outputVO = new SOT110OutputVO();
		List<String> warningList = new ArrayList<String>();
		
		//高資產客戶購買高風險商品，檢核集中度
		if(StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getOvsPrivateYN())) {
			inputVO.setOverCentRateYN(getOverCentRateYN(inputVO));
			if(StringUtils.equals("Y", inputVO.getOverCentRateYN())) {
				warningList.add("ehl_01_SOT310_001");
			}
		}
				
		//若有申請議價，則送出覆核
		callCRM421ApplySingle(inputVO.getTradeSEQ());

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null != vo) {
			vo.setTRADE_STATUS(MAIN_TRADE_STATUS_2_VERIFY); // 2:風控檢核中
			vo.setModifier(ws.getUser().getUserID());
			vo.setLastupdate(new Timestamp(System.currentTimeMillis()));

			dam.update(vo);
		}

		//若有警示訊息
		outputVO.setWarningMsg("");
		if (CollectionUtils.isNotEmpty(warningList)) {
			XMLInfo xmlInfo = new XMLInfo();

			for (String msg : warningList) {
				outputVO.setWarningMsg(xmlInfo.getErrorMsg(msg) + "\n" + outputVO.getWarningMsg());
			}
		}
				
		this.sendRtnObject(outputVO);
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
		sb.append("FROM TBSOT_NF_PURCHASE_D WHERE TRADE_SEQ = :tradeSEQ and FEE_TYPE = 'A' ");
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

	/**
	 * 刪除購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void delProd(Object body, IPrimitiveMap header) throws JBranchException {

		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append(String.format(" FROM %s ", TBSOT_DETAIL));
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		TBSOT_NF_PURCHASE_DPK dPK = new TBSOT_NF_PURCHASE_DPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(inputVO.getSeqNo());
		TBSOT_NF_PURCHASE_DVO dVO = new TBSOT_NF_PURCHASE_DVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_PURCHASE_DVO) dam.findByPKey(TBSOT_NF_PURCHASE_DVO.TABLE_UID, dVO.getcomp_id());
		dam.delete(dVO);
		if (list.size() == 1) {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
			dam.delete(vo);
		}

		this.sendRtnObject(null);
	}

	/**
	 * 風控 傳送OP交易並列印表單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		dam = this.getDataAccessManager();

		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		//mainVo.setPROSPECTUS_TYPE(this.getProspectusType(inputVO)); //MIMI 2/18 (公開說明書) 欄位都先寫死放1
		if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			mainVo.setREC_SEQ(inputVO.getRecSEQ());//錄音序號
		}
		dam.update(mainVo);

		// 檢核電文
		SOT703InputVO inputVO_703 = new SOT703InputVO();
		SOT703OutputVO outputVO_703 = new SOT703OutputVO();

		inputVO_703.setTradeSeq(inputVO.getTradeSEQ());

		SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
		try {
			sot703.confirmESBPurchaseNF(inputVO_703); //確認電文
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			errorMsg = sot712.inquireErrorCode(e.getMessage());
			logger.error(String.format("基金申購確認電文sot703.confirmESBPurchaseNF錯誤:%s", errorMsg), e);
			throw new JBranchException(String.format("基金申購確認電文錯誤:%s", errorMsg), e);
		}
		/*Method說明	基金申購確認
		發送電文NFBRN1
		需傳入參數：tradeSeq
		Method名稱	confirmESBPurchaseNF(SOT703InputVO inputVO) 
		RETURN void
		
		TODO沒有回傳值outputVO_703?
		*/

		String errorMsg = outputVO_703.getErrorMsg();
		if (!"".equals(errorMsg) && null != errorMsg) {
			logger.error(String.format("sot703.confirmESBPurchaseNF()  tradeSeq:%s , seqNo:%s  outputVO_703.ErrorMsg:%s", inputVO_703.getTradeSeq(), inputVO_703.getSeqNo(), outputVO_703.getErrorMsg()));

			outputVO.setErrorMsg(errorMsg);
		} else {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

			vo.setTRADE_STATUS(MAIN_TRADE_STATUS_3_OP); //3:傳送OP交易並列印表單 
			vo.setSEND_DATE(new Timestamp(System.currentTimeMillis())); //傳送OP後要設SEND_DATE
			dam.update(vo);

			// for CBS 測試用
			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
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

		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		dam = this.getDataAccessManager();

		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		//mainVo.setPROSPECTUS_TYPE(this.getProspectusType(inputVO)); //MIMI 2/18 (公開說明書) 欄位都先寫死放1
		if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			mainVo.setREC_SEQ(inputVO.getRecSEQ());//錄音序號
		}
		dam.update(mainVo);

		// 檢核電文
		SOT703InputVO inputVO_703 = new SOT703InputVO();
		SOT703OutputVO outputVO_703 = new SOT703OutputVO();

		inputVO_703.setTradeSeq(inputVO.getTradeSEQ());
		inputVO_703.setNotifyType("02");//單筆通知類型為02

		SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
		outputVO_703 = sot703.sendESBWebPurchaseNF(inputVO_703); //網路快速下單EBPMN – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息
		
		if (StringUtils.isNotBlank(outputVO_703.getErrorMsg()) && !StringUtils.equals("0000", outputVO_703.getErrorCode())) {
			//有錯誤代碼，且不為"0000"
			outputVO.setErrorMsg(outputVO_703.getErrorMsg());
		} else {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

			vo.setTRADE_STATUS(MAIN_TRADE_STATUS_4_WEBBANK); //4:網銀快速下單
			vo.setSEND_DATE(new Timestamp(System.currentTimeMillis()));
			dam.update(vo);

			// for CBS 測試用
			cbsservice.setTBSOT_TRADE_MAIN_LASTUPDATE_FOR_TEST(vo.getTRADE_SEQ());
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 要[保留原申請議價]
	 * 
	 * @param inputVO
	 * @return Map
	 * @throws JBranchException
	 * @throws DAOException
	 */
	private Map getFeeApplyMap(SOT110InputVO inputVO) throws JBranchException {
		Map feeAapply = new HashMap();

		//基金單筆申購：後收型基金不提供議價功能
		if(!isBackEndProd(inputVO.getProdId())) {
			feeAapply.put("LABEL", "申請議價");
			feeAapply.put("feeType", "A"); //A：申請議價
			feeAapply.put("feeTypeIndex", "idxAPPLY");
			if (StringUtils.equals("A", inputVO.getFeeType())) { //若UI選申請議價
				// cathy 要[保留原申請議價]
				feeAapply.put("FeeRate", inputVO.getFeeRate());
				feeAapply.put("Fee", inputVO.getFee());
				feeAapply.put("FeeDiscount", inputVO.getFeeDiscount());
			}
		}
		return feeAapply;
	}

	//是否為後收型基金
	private boolean isBackEndProd(String prodId) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT IS_BACKEND FROM TBPRD_FUND WHERE PRD_ID = :prd_id ");
		queryCondition.setObject("prd_id", prodId);
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return "Y".equals(ObjectUtils.toString(list.get(0).get("IS_BACKEND"))) ? true : false;
	}
	
	/**
	 * 基金表定手續費以及最優手續費查詢
	 * 
	 * @param SOT110InputVO
	 * @return
	 * @throws Exception
	 */
	private Map getDefaultFeeRate(SOT110InputVO inputVO) throws Exception {
		Map feeCdefault = new HashMap();
		if (StringUtils.isNotBlank(inputVO.getTradeSubType())) {
			SOT709OutputVO sot709OutputVO = null;
			SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
			SOT709InputVO sot709InputVO = new SOT709InputVO();
			String tradeSubType = inputVO.getTradeSubType();
			sot709InputVO.setCustId(inputVO.getCustID());
			sot709InputVO.setBranchNbr(cbsservice.getAcctBra(inputVO.getCustID(), inputVO.getTrustAcct()));  //FOR CBS改帳號分行抓取範圍
			sot709InputVO.setTrustCurrType(inputVO.getTrustCurrType());
			sot709InputVO.setTradeSubType(tradeSubType); // 參考 SOT.CHANGE_TRADE_SUB_TYPE
			sot709InputVO.setProdId(inputVO.getProdId());
			if (tradeSubType != null && tradeSubType.matches("1")) {
				sot709InputVO.setPurchaseAmtL(inputVO.getPurchaseAmt());
			}

			//因SOT709.getDefaultFeeRate 會用到TradeDate做判斷effDate值為何
			Date tradeDate = null;
			if ("1".equals(inputVO.getTradeDateType())) {//即時
				tradeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate());
			}
			sot709InputVO.setTradeDate(tradeDate);
			
			//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
			sot709InputVO.setTrustTS(inputVO.getTrustTS());
			
			//單筆沒有  自動換匯 sot709InputVO.setAutoCx(inputVO.getIsAutoCx()); // 自動換匯
			// 用不到 sot709InputVO.setBargainApplySeq(bargainApplySeq);//議價編號
			// 用不到 sot709InputVO.setBthCoupon 生日卷
			// 用不到sot709InputVO.setGroupIfa(); 優惠團體代碼
			try {
				sot709OutputVO = sot709.getDefaultFeeRate(sot709InputVO);
			} catch (Exception e) {
				String errorMsg = e.getMessage();
				SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
				errorMsg = sot712.inquireErrorCode(e.getMessage());
				logger.error(String.format("基金表定手續費電文sot709.getDefaultFeeRate錯誤:%s", errorMsg), e);
				throw new JBranchException(String.format("基金表定手續費查詢錯誤:%s", errorMsg), e);
			}
			if (sot709OutputVO != null && StringUtils.isNotBlank(sot709OutputVO.getErrorCode())) {
				throw new JBranchException("基金表定手續費:" + sot709OutputVO.getErrorMsg());
			}

			DefaultFeeRateVO defaultFeeRates = sot709OutputVO.getDefaultFeeRates();
			
			if (defaultFeeRates != null) {
				feeCdefault.put("LABEL", "請 選 擇"); // 請選擇;最優手續費
				feeCdefault.put("feeType", "C"); //C：最優手續費
				//後收型基金規則:OBU/DBU申購後收型基金，最優手續費均應為0%
				feeCdefault.put("FeeRateL", isBackEndProd(inputVO.getProdId()) ? 0 : defaultFeeRates.getFeeRateL()); // 最優手續費率(低);手續費率 
				feeCdefault.put("FeeL", isBackEndProd(inputVO.getProdId()) ? 0 : defaultFeeRates.getFeeL()); // 最優手續費(低);手續費 
				feeCdefault.put("DefaultFeeRateL", defaultFeeRates.getDefaultFeeRateL()); // 表定手續費(低);表定手續費 
				feeCdefault.put("TrustCurr", defaultFeeRates.getTrustCurr()); // 投資幣別
				feeCdefault.put("GroupOfa", defaultFeeRates.getGroupOfa()); // 團體優惠代碼   若選'最優'要[團體優惠代碼]寫入資料庫
				feeCdefault.put("feeTypeIndex", "idx" + "99"); //99請選擇專用
			}

		}
		return feeCdefault;
	}

	/**
	 * 查詢基金表定、最優手續費 (含最優手續費[請選擇])
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void getDefaultFeeData(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		List<Map<String, Object>> feeTypeList = new ArrayList<Map<String, Object>>(); //手續費優惠方式
		Map feeCdefault = getDefaultFeeRate(inputVO);//基金表定手續費以及最優手續費查詢 
		feeTypeList.add(feeCdefault); //設定重算 [最優手續費]   tota1[0].body.feeTypeList[0]
		feeTypeList.add(getFeeApplyMap(inputVO));//目前[保留原申請議價]    tota1[0].body.feeTypeList[1]
		SOT110OutputVO outputVO = new SOT110OutputVO();
		outputVO.setFeeTypeIndex(inputVO.getFeeTypeIndex());//保留上次選的手續費優惠方式  EX:申請議價
		outputVO.setFeeTypeList(feeTypeList);
		this.sendRtnObject(outputVO);
	}

	/**
	 * 查詢手續費優惠方式 (含最優手續費[請選擇])
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void getFeeTypeListData(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		outputVO.setFeeTypeIndex(inputVO.getFeeTypeIndex());//保留上次選的手續費優惠方式
		List<Map<String, Object>> feeTypeList = new ArrayList<Map<String, Object>>(); //手續費優惠方式

		Map defaultFeeRateMap = getDefaultFeeRate(inputVO);
		feeTypeList.add(defaultFeeRateMap);//C預設請選擇,基金表定手續費以及最優手續費查詢
		feeTypeList.add(getFeeApplyMap(inputVO)); //A[保留原申請議價]

		BigDecimal defaultFeeRate = (defaultFeeRateMap.get("DefaultFeeRateL") == null ? null : (BigDecimal) defaultFeeRateMap.get("DefaultFeeRateL"));

		if(!StringUtils.equals(inputVO.getTrustTS(), "M") && !StringUtils.equals(inputVO.getIsOBU(),"Y")){ //金錢信託或OBU 不適用生日卷優惠
			StringBuffer getCustDtlSQL = new StringBuffer();
			getCustDtlSQL.append("SELECT BIRTH_DATE FROM TBCRM_CUST_MAST WHERE CUST_ID = :custID");
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString(getCustDtlSQL.toString());
			queryCondition.setObject("custID", inputVO.getCustID());
			List<Map<String, Object>> custList = dam.exeQuery(queryCondition);
			Date birthDate;
			if (!custList.isEmpty() && (birthDate = (Date) custList.get(0).get("BIRTH_DATE")) != null) {
				Date sysDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate());// 交易日

				//生日券: 自生日當月起三個月內想基金單筆申購手續費優惠乙次
				int sysDateMM = Integer.valueOf(SDFMM.format(sysDate));//交易日
				int birthDateMM = Integer.valueOf(SDFMM.format(birthDate));//生日當月

				if ((sysDateMM - birthDateMM >= 0 && sysDateMM - birthDateMM <= 2) || sysDateMM - birthDateMM <= -10) { //生日3個月內 和 交易日 相同月份
					Map feeBbirth = new HashMap();
					feeBbirth.put("LABEL", "生日券使用");
					feeBbirth.put("feeType", "B"); //B：生日券使用
					feeBbirth.put("feeTypeIndex", "idxBTH");
					feeTypeList.add(feeBbirth); //生日卷  
				}
			}
		}
		int feeTypeIndex = 0;
		//單次議價資料(已申請議價清單)
		SOT709 sot709 = (SOT709) PlatformContext.getBean("sot709");
		SOT709InputVO sot709InputVO = new SOT709InputVO();
		sot709InputVO.setCustId(inputVO.getCustID());
		sot709InputVO.setStartDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate()));
		List<SingleFeeRateVO> singleFeeRateList = null;
		if (!StringUtils.equals(inputVO.getTrustTS(), "M")) {
			//cathy 基金單次單筆議價查詢(下單用) 發送電文VN085N(不限制DBU; 限制OBU!=Y)
			//#397_特金基金相關模組_加入OBU邏輯
			SOT709OutputVO sot709OutputVO = null;
			try {
				sot709OutputVO = sot709.getSingleFeeRate(sot709InputVO);
			} catch (Exception e) {
				String errorMsg = e.getMessage();
				SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
				errorMsg = sot712.inquireErrorCode(e.getMessage());
				logger.error(String.format("基金單次單筆議價查詢電文sot709.getSingleFeeRate錯誤:%s", errorMsg), e);
				throw new JBranchException(String.format("基金單次單筆議價查詢錯誤:%s", errorMsg), e);
			}
			singleFeeRateList = sot709OutputVO.getSingleFeeRateList();

			if (sot709OutputVO != null && StringUtils.isNotBlank(sot709OutputVO.getErrorCode())) {
				throw new JBranchException("基金單次單筆議價查詢:" + sot709OutputVO.getErrorCode() + sot709OutputVO.getErrorMsg());
			}

			/**
			 * 2016/12/22 目前基金下單手續費 和議價CRM421/SOT709 有待釐清 1.議價與下單介接
			 * 2.SOT110/ SOT120 還是SOT703 CALL AFBRN8(SOT709) ? 變更 事先議價申購金額
			 */
			for (SingleFeeRateVO rateVO : singleFeeRateList) {
				if (inputVO.getProdId().equals(rateVO.getFundNo())) { //過濾相同的基金代號
					Map sRate = new HashMap();
					// 單次議價列表會列出〈代號+短名稱+折數〉。	
					BigDecimal discount = defaultFeeRate != null ? (BigDecimalUtils.divide(rateVO.getFeeRate(), defaultFeeRate).multiply(new BigDecimal(10))).setScale(3, BigDecimal.ROUND_UP) : null;

					sRate.put("LABEL", discount != null ? inputVO.getProdName() + "_" + discount + "折" : "事先議價" + discount + "折");
					sRate.put("feeType", "D"); //D：單次議價
					sRate.put("FeeRate", rateVO.getFeeRate());
					sRate.put("EviNum", rateVO.getEviNum());
					sRate.put("BeneCode", rateVO.getBeneCode()); //議價編號beneCode
					sRate.put("InvestAmt", rateVO.getInvestAmt());//事先議價的申購金額，將來下單要可以事後 變更事先議價的申購金額
					sRate.put("feeTypeIndex", "idx" + feeTypeIndex++);
					feeTypeList.add(sRate);
				}
			}
		}

		//查詢次數型優惠   是否要擋OBU
		if(!StringUtils.equals(inputVO.getTrustTS(), "M")){ //金錢信託不用查詢次數型優惠
			SOT709OutputVO sot709OutputVO = new SOT709OutputVO();
			sot709OutputVO = sot709.getMeteringFeeRate(sot709InputVO);
			List<MeteringFeeRateVO> meteringRateList = sot709OutputVO.getMeteringFeeRateList();
			if (CollectionUtils.isNotEmpty(meteringRateList)) {
				for (MeteringFeeRateVO mvo : meteringRateList) {
					if (StringUtils.isNotBlank(mvo.getCntSingle())) { //空白表示沒有優惠
						Map mRate = new HashMap();
						mRate.put("LABEL", mvo.getGroupName()); //團體名稱
						mRate.put("feeType", "E"); //E：次數型團體優惠
						mRate.put("groupCode", mvo.getCntSingle()); //臨櫃單筆
						mRate.put("totalCount", mvo.getTotalCount());
						mRate.put("UsedCount", mvo.getUsedCount());
						mRate.put("feeTypeIndex", "idx" + feeTypeIndex++);
						feeTypeList.add(mRate);
					}
				}
			}
		}
		
		outputVO.setFeeTypeList(feeTypeList);
		this.sendRtnObject(outputVO);
	}

	/*
	 
	 CATHY根據剛才討論的基金/ETF議價流程如下
	
	1.      下單交易，若選擇〝申請議價〞
	A.          加入購物車：呼叫CRM421.addToList (傳入參數CRM421InputVO.商品代碼、信託幣別、申購金額、申請折扣類別、折扣費率、折扣數、手續費金額，請參照CRM421 TDS)
	B.          下一步：呼叫CRM421.applySingle (傳入參數CRM421InputVO.APPLY_SEQ，請參照CRM421 TDS)
	2.      議價交易
	A.          點選〝進行下單〞(只有單次議價有此功能)
	                    i.                議價類別為〝基金單筆申購〞：呼叫SOT110，傳入參數bargainApplySeq
	                  ii.                議價類別為〝基金定期(不)定額申購〞：呼叫SOT120，傳入參數bargainApplySeq
	                iii.                議價類別為〝海外ETF/股票申購〞：呼叫SOT210，傳入參數bargainApplySeq
	                 iv.                下單交易，根據傳入的議價編號參數取得TBCRM_BRG_APPLY_SINGLE中的客戶ID、商品代號、商品名稱、信託幣別、申購金額、委託數量、委託價格、表定手續費率、手續費率、手續費金額、手續費折數資料，顯示於畫面中；再呼叫相關電文取得客戶、商品及其他資料
	B.          於覆核完成之後需要更新下單議價狀態，呼叫SOT712.updateBargainStatus (傳入參數SOT712InputVO.applySeq) (SOT712我會加上這個method)
	 * 
	 * **/
	/**
	 * 查詢事先議價設定值 CRM421
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getCrmBrgApplySingle(Object body, IPrimitiveMap header) throws JBranchException {
		SOT110InputVO inputVO = (SOT110InputVO) body;
		inputVO = this.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		Map<String, Object> brgApplySingle = new HashMap<String, Object>();
		dam = this.getDataAccessManager();
		TBCRM_BRG_APPLY_SINGLEVO vo = new TBCRM_BRG_APPLY_SINGLEVO();
		if (StringUtils.isNotBlank(inputVO.getBargainApplySEQ())) {
			vo = (TBCRM_BRG_APPLY_SINGLEVO) dam.findByPKey(TBCRM_BRG_APPLY_SINGLEVO.TABLE_UID, inputVO.getBargainApplySEQ());
		}
		if (vo != null) {
			brgApplySingle.put("APPLY_SEQ", vo.getAPPLY_SEQ());// 議價編號
			brgApplySingle.put("CUST_ID", vo.getCUST_ID()); //客戶編號 
			brgApplySingle.put("PROD_ID", vo.getPROD_ID()); // 商品代號
			brgApplySingle.put("PURCHASE_AMT", vo.getPURCHASE_AMT());// 申購金額 
			brgApplySingle.put("APPLY_TYPE", vo.getAPPLY_TYPE()); //申請類別 1：基金單筆申購 2：基金定期(不)定額申購 3：基金贖回再申購 4：海外ETF/股票申購 5：海外ETF股票贖回
			brgApplySingle.put("TRUST_CURR", vo.getTRUST_CURR());// 信託幣別
			brgApplySingle.put("DISCOUNT_TYPE", vo.getDISCOUNT_TYPE()); // 折扣類型 1:手續費率 2:手續費折數
			brgApplySingle.put("AUTH_STATUS", vo.getAUTH_STATUS());
			brgApplySingle.put("tradeSubType", "1");//信託型態:1.單筆申購(預設)
			//單筆沒有 brgApplySingle.put("isAutoCx", "Y");//自動換匯(預設)
			if ("C".equals(vo.getTRUST_CURR_TYPE())) {
				brgApplySingle.put("trustCurrType", vo.getTRUST_CURR_TYPE());
			} else {
				brgApplySingle.put("trustCurrType", "TWD".equals(vo.getTRUST_CURR()) ? "N" : "Y");
			}

			//brgApplySingle手續費應該跟電文一樣    :  DBU單次小額議價編編號查詢 NFEE086   (OBU客戶就不查'事先議價')
			brgApplySingle.put("DEFAULT_FEE_RATE", vo.getDEFAULT_FEE_RATE());// 表定手續費率 
			brgApplySingle.put("FEE_RATE", vo.getFEE_RATE());// 手續費率
			brgApplySingle.put("FEE", vo.getFEE());// 手續費金額
			brgApplySingle.put("FEE_DISCOUNT", vo.getFEE_DISCOUNT());// 手續費折數

			outputVO.setBrgApplySingle(brgApplySingle);
		} else {
			brgApplySingle.put("tradeSubType", "1");//信託型態:1.單筆申購(預設)
			outputVO.setBrgApplySingle(brgApplySingle);
		}
		logger.debug(String.format("getCrmBrgApplySingle:SEQ %s , brgApplySingle:%s", inputVO.getBargainApplySEQ(), brgApplySingle));
		this.sendRtnObject(outputVO);
	}

	public void getReserveDateTimestamp(Object body, IPrimitiveMap header) throws JBranchException {
		SOT110OutputVO outputVO = new SOT110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.RESERVE_DATE_TIMESTAMP' AND PARAM_CODE = '1' ");
		queryCondition.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(outputVO);
	}
	
	// 控管國內貨幣型基金交易時間，超過10:30 轉為預約交易狀態
	// #1203 貨幣型基金預約交易時間BUG修復 2022.07.19  SamTu
	public void checkReserve(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		SOT110InputVO inputVO = (SOT110InputVO) body;
		SOT110OutputVO outputVO = new SOT110OutputVO();
		
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate());
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		SimpleDateFormat sdf2 = new SimpleDateFormat("mm");
		Integer SYSHour;
		Integer SYSmin;
		SYSHour = Integer.valueOf(sdf.format(date));
		SYSmin = Integer.valueOf(sdf2.format(date));

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUBSTR(PARAM_NAME,1,2) AS START_H, ");
		sb.append("       SUBSTR(PARAM_NAME,3,2) AS START_M, ");
		sb.append("       SUBSTR(PARAM_NAME,6,2) AS END_H, ");
		sb.append("       SUBSTR(PARAM_NAME,8,2) AS END_M ");
		sb.append("FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.RESERVE_DATE_TIMESTAMP_FUND' AND PARAM_CODE = '1' ");
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		int s_h = Integer.valueOf(list.get(0).get("START_H").toString());
		int s_m = Integer.valueOf(list.get(0).get("START_M").toString());
		int e_h = Integer.valueOf(list.get(0).get("END_H").toString());
		int e_m = Integer.valueOf(list.get(0).get("END_M").toString());

        //參數預設為09:00~10:30不用預約，其餘時間段看TBPMS_NFFUSMP0邏輯
		if (SYSHour == s_h && SYSmin >= s_m ) {
			outputVO.setReserve(true);
			this.sendRtnObject(outputVO);
		} else if (SYSHour > s_h && SYSHour < e_h ) {                              
			outputVO.setReserve(true);
			this.sendRtnObject(outputVO);
			
		} else if (SYSHour == e_h  && SYSmin < e_m  ) {
			outputVO.setReserve(true);
			this.sendRtnObject(outputVO);
	
		}else{
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT CASE WHEN SUBSTR(FUSM98, 7, 1) = 'Y' ");
			sb.append("THEN 'Y' ELSE 'N' END AS YN ");
			sb.append("FROM TBPMS_NFFUSMP0 ");
			sb.append("WHERE FUS01 || FUS02 = :prdID ");
			
			queryCondition.setObject("prdID", inputVO.getProdId());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> count = dam.exeQuery(queryCondition);
			String cnt = count.get(0).get("YN").toString();
			if("Y".equals(cnt)){
				outputVO.setReserve(false);
			}else{
				outputVO.setReserve(true);
			}
			this.sendRtnObject(outputVO);

		}
		
	}

	/**
	 * 清除 帳號的幣別
	 * 
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 */
	private SOT110InputVO cleanAcctCcy(SOT110InputVO inputVO) throws JBranchException {
		if (StringUtils.isNotBlank(inputVO.getDebitAcct()) && StringUtils.indexOf(inputVO.getDebitAcct(), "_") > 0)
			inputVO.setDebitAcct(StringUtils.split(inputVO.getDebitAcct(), "_")[0]);
		if (StringUtils.isNotBlank(inputVO.getCreditAcct()) && StringUtils.indexOf(inputVO.getCreditAcct(), "_") > 0)
			inputVO.setCreditAcct(StringUtils.split(inputVO.getCreditAcct(), "_")[0]);

		return inputVO;
	}

	/**
	 * MIMI 2/18 (公開說明書) 欄位都先寫死放1
	 * 
	 * @param inputVO
	 * @return
	 */
	private String getProspectusType(SOT110InputVO inputVO) {
		logger.error("TODO 2/18 (公開說明書) 欄位都先寫死放1");
		/*MIMI 2/18
		每一支申購交易
		TBSOT_TRADE_MAIN.PROSPECTUS_TYPE (公開說明書) 欄位都先寫死放1
		1.已取得公開說明書
		2.已由經理公司交付或貴行取得
		
		*/
		return "1";
	}
	
	/***
	 * 集中度是否超過上限
	 * @param inputVO
	 * @return Y:超過 N:沒超過
	 * @throws Exception
	 */
	private String getOverCentRateYN(SOT110InputVO inputVO) throws Exception {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//取得買匯
		sb.append("select NVL(BUY_RATE, 1) AS BUY_RATE ");
		sb.append("  from TBPMS_IQ053 ");
		sb.append(" where CUR_COD = :currCode AND MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		queryCondition.setObject("currCode", inputVO.getProdCurr());
		
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);
		BigDecimal rate = new BigDecimal(1);
		if (CollectionUtils.isNotEmpty(cList)) {
			rate = (BigDecimal) cList.get(0).get("BUY_RATE");
		}
		
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		//集中度加上新增商品金額
		inputVO714.setProdType("6");//境外私募基金
		inputVO714.setBuyAmt(inputVO.getPurchaseAmt().multiply(rate));
				
		//查詢客戶高資產集中度資料
		WMSHACRDataVO cRateData = sot714.getCentRateData(inputVO714);
		return StringUtils.equals("Y", cRateData.getVALIDATE_YN()) ? "N" : "Y"; //可交易=>沒超過上限; 不可交易=>超過上限
	}
	
	/***
	 * 是否已超過境外私募基金商品可申購客戶數上限
	 * 歸戶計算
	 * @param inputVO
	 * @return true:已超過	false:未超過
	 * @throws JBranchException 
	 */
	private boolean isOverOvspriFundLimit(SOT110InputVO inputVO) throws JBranchException {
		boolean isOver = false;
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		//境外私募基金商品可申購客戶數上限(參數預設99)
		BigDecimal custCount = new BigDecimal(new XmlInfo().getVariable("SOT.OVSPRI_FUND_CUST_LIMIT", "1", "F3"));
		
		//此客戶ID是否有申購過此商品(主機)
		queryCondition.setQueryString("SELECT 1 FROM TBSOT_FUND_OVSPRI_CNT WHERE PRD_ID = :prodID AND CUST_ID = :custId ");
		queryCondition.setObject("prodID", inputVO.getProdId());
		queryCondition.setObject("custId", inputVO.getCustID());
		List<Map<String, Object>> list1 = dam.exeQuery(queryCondition);
		
		//此客戶ID是否有申購過此商品(當日業管已傳送OP資料)
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT 1 FROM TBSOT_TRADE_MAIN M, TBSOT_FCI_TRADE_D D ");
		sb.append(" WHERE M.CUST_ID = :custId AND M.TRADE_SEQ = D.TRADE_SEQ AND D.PROD_ID = :prodID AND TRUNC(D.TRADE_DATE) = TRUNC(SYSDATE) AND M.TRADE_STATUS = '3' ");
		queryCondition.setQueryString(sb.toString());//客戶是否有申購過此商品
		queryCondition.setObject("prodID", inputVO.getProdId());
		queryCondition.setObject("custId", inputVO.getCustID());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		
		//都未申購過才繼續檢核，已申購過則已歸戶算過不用再檢核
		if(CollectionUtils.isEmpty(list1) && CollectionUtils.isEmpty(list2)) {
			//申購過此商品客戶數(主機)
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			queryCondition.setQueryString("SELECT COUNT(1) AS CNT FROM TBSOT_FUND_OVSPRI_CNT WHERE PRD_ID = :prodID ");
			queryCondition.setObject("prodID", inputVO.getProdId());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			BigDecimal count400 = CollectionUtils.isNotEmpty(list3) ? (BigDecimal)list3.get(0).get("CNT") : BigDecimal.ZERO;
			
			//申購過此商品客戶數(當日業管已傳送OP資料)
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT COUNT(1) AS CNT FROM TBSOT_TRADE_MAIN M, TBSOT_FCI_TRADE_D D ");
			sb.append(" WHERE D.PROD_ID = :prodID AND M.TRADE_SEQ = D.TRADE_SEQ AND TRUNC(D.TRADE_DATE) = TRUNC(SYSDATE) AND M.TRADE_STATUS = '3' ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("prodID", inputVO.getProdId());
			List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
			BigDecimal countwms = CollectionUtils.isNotEmpty(list4) ? (BigDecimal)list4.get(0).get("CNT") : BigDecimal.ZERO;
			
			//總申購數超過參數設定(99人)=>超過
			if(custCount.compareTo(count400.add(countwms)) <= 0) {
				isOver = true;
			}
		}
		
		return isOver;
	}
	
	/***
	 * 取得商品買匯匯率
	 * @param currency
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public BigDecimal getBuyRate(String currency) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//取得買匯
		sb.append("select NVL(BUY_RATE, 1) AS BUY_RATE ");
		sb.append("  from TBPMS_IQ053 ");
		sb.append(" where CUR_COD = :currCode AND MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		queryCondition.setObject("currCode", currency);
		
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);
		BigDecimal rate = new BigDecimal(1);
		if (CollectionUtils.isNotEmpty(cList)) {
			rate = (BigDecimal) cList.get(0).get("BUY_RATE");
		}
		
		return rate;
	}
	
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getHnwcRiskValue(SOT110InputVO inputVO) throws Exception {
		//取得購物車中各商品P值申購金額合計
		Map<String, BigDecimal> cartAmt = getAmtBuyByPVal(inputVO.getTradeSEQ()); 
		//取得這次申購商品信託幣別買匯
		BigDecimal rate = getBuyRate(inputVO.getTrustCurr());
				
		//取得高資產客戶註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCustID());
		
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		inputVO714.setCUST_KYC(inputVO.getKycLV()); //客戶C值
		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(inputVO.getProdRiskLv()); //商品風險檢核值
		BigDecimal amtBuy = inputVO.getPurchaseAmt().multiply(rate);//這次申購金額折台
		
		for(int i = 1; i <= 4; i++) {
			String pVal = "P" + String.valueOf(i);
			//以這次申購商品風險檢核值判斷是否需要加上這次申購金額
			BigDecimal amt = StringUtils.equals(pVal, inputVO.getProdRiskLv()) ? amtBuy : BigDecimal.ZERO;
			//這次申購金額加上購物車中各商品P值申購金額
			switch(pVal) {
				case "P1":
					inputVO714.setAMT_BUY_1(amt.add(cartAmt.get("P1") == null ? BigDecimal.ZERO : cartAmt.get("P1"))); 
					break;
				case "P2":
					inputVO714.setAMT_BUY_2(amt.add(cartAmt.get("P2") == null ? BigDecimal.ZERO : cartAmt.get("P2")));
					break;
				case "P3":
					inputVO714.setAMT_BUY_3(amt.add(cartAmt.get("P3") == null ? BigDecimal.ZERO : cartAmt.get("P3")));
					break;
				case "P4":
					inputVO714.setAMT_BUY_4(amt.add(cartAmt.get("P4") == null ? BigDecimal.ZERO : cartAmt.get("P4")));
					break;
			}
		}
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		//若檢核不通過，產生錯誤訊息
		String errorMsg = "";
		if(!StringUtils.equals("Y", riskValData.getVALIDATE_YN())) {
			if(cartAmt.isEmpty() || (cartAmt.size() == 1 && cartAmt.containsKey(inputVO.getProdRiskLv()))) {
				//若購物車中沒有資料，或多檔都是同一P值(購物車合計中只有一筆且與本次申購商品P值相同)
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
	 * 取得購物車中各商品P值申購金額折台合計(以信託幣別來看)
	 * @param tradeSeq
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, BigDecimal> getAmtBuyByPVal(String tradeSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//取得購物車中各商品P值申購金額合計
		sb.append("SELECT A.PROD_RISK_LV, SUM(A.PURCHASE_AMT * NVL(B.BUY_RATE, 1)) AS AMT_BUY ");
		sb.append(" FROM TBSOT_NF_PURCHASE_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq ");
		sb.append(" GROUP BY A.PROD_RISK_LV ");
		queryCondition.setObject("tradeSeq", tradeSeq);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		//購物車中各商品P值申購金額合計
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		for(Map<String, Object> cart : list) {
			map.put((String)cart.get("PROD_RISK_LV"), (BigDecimal)cart.get("AMT_BUY"));
		}
		
		return map;
	}
	
}