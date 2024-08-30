package com.systex.jbranch.app.server.fps.sot150;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_BATCHVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110;
import com.systex.jbranch.app.server.fps.prd110.PRD110InputVO;
import com.systex.jbranch.app.server.fps.prd110.PRD110OutputVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701OutputVO;
import com.systex.jbranch.app.server.fps.sot703.FundRule;
import com.systex.jbranch.app.server.fps.sot703.SOT703;
import com.systex.jbranch.app.server.fps.sot703.SOT703InputVO;
import com.systex.jbranch.app.server.fps.sot703.SOT703OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.app.server.fps.sot714.WMSHAIADataVO;
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

/**
 * sot150
 * 
 * @author ocean
 * @date 2016/10/26
 * @spec 基金變更
 */
@Component("sot150")
@Scope("request")
public class SOT150 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private SimpleDateFormat SDFYYYY_MM_DDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Logger logger = LoggerFactory.getLogger(SOT150.class);

	public void getSOTCustInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT150InputVO inputVO = (SOT150InputVO) body;
		SOT150OutputVO outputVO = new SOT150OutputVO();
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
		outputVO.setCustRemarks(outputVO_701.getFp032675DataVO().getCustRemarks());
		outputVO.setIsOBU(outputVO_701.getFp032675DataVO().getObuFlag());
		outputVO.setIsAgreeProdAdv(("Y".equals(outputVO_701.getFp032675DataVO().getCustProFlag()) ? "Y" : (StringUtil.isBlank(outputVO_701.getFp032675DataVO().getCustTxFlag()) ? "" : ("Y".equals(outputVO_701.getFp032675DataVO().getCustTxFlag().substring(0, 1)) ? "Y" : "N"))));
		outputVO.setBargainDueDate(outputVO_701.getDueDate());
		outputVO.setPlNotifyWays(outputVO_701.getCustPLDataVO().getPlMsg());
		outputVO.setTakeProfitPerc(outputVO_701.getCustPLDataVO().getTakeProfitPerc());
		outputVO.setStopLossPerc(outputVO_701.getCustPLDataVO().getStopLossPerc());
		outputVO.setCustProType(outputVO_701.getFp032675DataVO().getCustProType());
		outputVO.setNoSale(outputVO_701.getFp032675DataVO().getNoSale());
		outputVO.setPiRemark(outputVO_701.getFp032675DataVO().getCustProRemark());
		
//		outputVO.setIsFirstTrade(sot701.getIsCustFirstTrade(inputVO_701) ? "Y" : "N");
		outputVO.setAgeUnder70Flag(outputVO_701.getFp032675DataVO().getAgeUnder70Flag()); //AgeUnder70Flag現在內容為65歲以下
//		outputVO.setEduJrFlag(outputVO_701.getFp032675DataVO().getEduJrFlag());
//		outputVO.setHealthFlag(outputVO_701.getFp032675DataVO().getHealthFlag());
		
		//扣款帳號
		List<AcctVO> debitAcct = outputVO_701.getCustAcctDataVO().getDebitAcctList();
		List<Map<String, Object>> debitAcctList = new ArrayList<Map<String, Object>>();
		for (AcctVO vo : debitAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()) + "_"+ vo.getCurrency());
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()) + "_"+ vo.getCurrency());
			map.put("ACCT_BALACNE", vo.getAcctBalance());
			map.put("AVB_BALANCE", vo.getAvbBalance());
			map.put("CURRENCY", vo.getCurrency());
			debitAcctList.add(map);
		}
		outputVO.setDebitAcct(debitAcctList);

		/*//信託帳號
		List<AcctVO> trustAcct = outputVO_701.getCustAcctDataVO().getTrustAcctList();
		List<Map<String, Object>> trustAcctList = new ArrayList<Map<String,Object>>();
		for (AcctVO vo : trustAcct) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			
			trustAcctList.add(map);
		}
		outputVO.setTrustAcct(trustAcctList);*/

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

		//查詢客戶高資產註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hNWCDataVO = sot714.getHNWCData(inputVO.getCustID());
		outputVO.setHnwcYN(StringUtils.isNotBlank(hNWCDataVO.getValidHnwcYN()) ? hNWCDataVO.getValidHnwcYN() : "N");
		outputVO.setHnwcServiceYN(hNWCDataVO.getHnwcService());
		
		this.sendRtnObject(outputVO);
	}

	public void getSOTCustInfoCT(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT150InputVO inputVO = (SOT150InputVO) body;
		SOT150OutputVO outputVO = new SOT150OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		Boolean isFirstTrade = false;

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);
		isFirstTrade = sot701.getIsCustFirstTrade(inputVO_701);
		
		outputVO.setIsFirstTrade((isFirstTrade) ? "Y" : "N");            //是否首購
		outputVO.setCustRemarks(fp032675DataVO.getCustRemarks()); 		 //弱勢
		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag());  //年齡小於70
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag());            //教育程度國中以上(不含國中)
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag());          //未領有全民健保重大傷病證明

		this.sendRtnObject(outputVO);
	}

	/**
	 * 是否首購
	 * 
	 * @param SOT150InputVO
	 * @return
	 * @throws Exception
	 */
	private String isFirstTrade(SOT150InputVO inputVO) throws Exception {
		Boolean isFirstTrade = false;
		if (StringUtils.isNotBlank(inputVO.getfProdID())) { //變更商品才要判斷首購
			SOT701InputVO inputVO_701 = new SOT701InputVO();
			inputVO_701.setCustID(inputVO.getCustID());
			inputVO_701.setProdType(inputVO.getProdType());
			inputVO_701.setTradeType(inputVO.getTradeType());
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			isFirstTrade = sot701.getIsCustFirstTrade(inputVO_701);
		}
		return (isFirstTrade == true) ? "Y" : "N";
	}

	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {

		SOT150InputVO inputVO = (SOT150InputVO) body;
		SOT150OutputVO outputVO = new SOT150OutputVO();

		String queryProdID = null;
		if (StringUtils.isNotBlank(inputVO.getfProdID())) {
			queryProdID = inputVO.getfProdID(); //變更後標的
		} else if (StringUtils.isNotBlank(inputVO.getbProdID())) {
			queryProdID = inputVO.getbProdID(); //變更前庫存標的
		}
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PTYPE, PRD_ID, PNAME ");
		sb.append("FROM VWPRD_MASTER ");
		sb.append("WHERE PRD_ID = :prodID ");
		queryCondition.setObject("prodID", queryProdID);
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> pTypeList = dam.exeQuery(queryCondition);
		if (pTypeList.size() > 0) {
			String pType = (String) pTypeList.get(0).get("PTYPE");

			boolean SOT702Status = false;
			if (isRunFitness() && StringUtils.isNotBlank(inputVO.getfProdID()) && !(inputVO.getfProdID().equals(inputVO.getbProdID()))) { //變更後標的 或 變更後標的與變更前標的不同才要適配 
				logger.debug("SOT.FITNESS_YN 進行適配 ");
				// 1.適配
				PRD110OutputVO prdOutputVO = new PRD110OutputVO();
				PRD110InputVO prdInputVO = new PRD110InputVO();
				prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
				prdInputVO.setType("4");
				prdInputVO.setFund_id(queryProdID);

				PRD110 prd110 = (PRD110) PlatformContext.getBean("prd110");
				prdOutputVO = prd110.inquire(prdInputVO);

				if (CollectionUtils.isNotEmpty(prdOutputVO.getResultList())) {
					String warningMsg = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("warningMsg");
					String errId = (String) ((Map<String, Object>) prdOutputVO.getResultList().get(0)).get("errorID");

					if (StringUtils.isBlank(errId)) {
						SOT702Status = true;
					}

					outputVO.setWarningMsg(warningMsg);
					outputVO.setErrorMsg(errId);
				}

			} else {
				logger.debug("SOT.FITNESS_YN 不進行適配 ");
				SOT702Status = true;
			}
			// 2.查詢商品主檔
			if (SOT702Status) { // 
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("SELECT FUND.PRD_ID, FUND.FUND_CNAME, FUND.CURRENCY_STD_ID, FUND.RISKCATE_ID,i.FUS20,i.FUS40  ");
				sb.append("FROM TBPRD_FUND FUND ");
				sb.append("LEFT JOIN TBPRD_FUNDINFO i ON (FUND.PRD_ID=i.PRD_ID) ");
				sb.append("WHERE FUND.PRD_ID = :prodID ");
				queryCondition.setObject("prodID", queryProdID);
				queryCondition.setQueryString(sb.toString());

				outputVO.setProdDTL(dam.exeQuery(queryCondition));
			}
		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 當A基金 換成B基金 其幣別不同 ，要透過查詢換成等值幣別 原扣款USD10，查詢後取得TWD310，藉此判斷是否有變更金額(IF
	 * 用戶改成TWD330 即為變更金額) 基金事件標的變更，取得不同幣別的等值金額 發送電文NFBRN5 需傳入參數：custId, isOBU,
	 * certificateId, prodId, amt1, amt2, amt3, currency, tradeDate
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 * 
	 *             public void getAmtByProdCurr(SOT150InputVO inputVO, Object
	 *             body, IPrimitiveMap header) throws JBranchException { try {
	 *             SOT703InputVO inputVO_703 = new SOT703InputVO();
	 *             SOT703OutputVO outputVO_703 = new SOT703OutputVO();
	 * 
	 *             inputVO_703.setCustId(inputVO.getCustID());
	 *             inputVO_703.setIsOBU(inputVO.getIsOBU());
	 *             inputVO_703.setCertificateId(inputVO.getCertificateID());
	 *             inputVO_703.setProdId(inputVO.getfProdID());
	 *             inputVO_703.setAmt1(inputVO.getfPurchaseAmtL() != null ?
	 *             inputVO .getfPurchaseAmtL().toString() : null);
	 *             inputVO_703.setAmt2(inputVO.getfPurchaseAmtM() != null ?
	 *             inputVO .getfPurchaseAmtM().toString() : null);
	 *             inputVO_703.setAmt3(inputVO.getfPurchaseAmtH() != null ?
	 *             inputVO .getfPurchaseAmtH().toString() : null);
	 *             inputVO_703.setCurrency(inputVO.getfProdCurr());
	 * 
	 *             Date tradeDate = null; if
	 *             ("2".equals(inputVO.getTradeDateType())) {// 2預約 //  畫面沒有
	 *             即時/預約(交易日期) 1：即時 2：預約 String errMsg =
	 *             "事件變更，因為超過即時交易時間，沒有提供預約功能"; logger.error(errMsg); //  先不要
	 *             throw new JBranchException(errMsg); try { tradeDate =
	 *             SDFYYYY_MM_DDHHMMSS.parse(inputVO .getTradeDate()); } catch
	 *             (ParseException e) { throw new JBranchException(e); } } else
	 *             {// 即時 tradeDate = new Date(); //  畫面沒有 交易日期 但電文要 }
	 *             inputVO_703.setTradeDate(tradeDate);
	 * 
	 * 
	 *             SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
	 * 
	 *             outputVO_703 NFBRN5OutputVO =
	 *             sot703.getAmtByProdCurr(inputVO);
	 * 
	 *             boolean isEqualAmt = true; if (inputVO_703.getAmt1() != null
	 *             && !inputVO_703.getAmt1().equals(outputVO_703.getAmt1())) {
	 *             isEqualAmt = false; } if (inputVO_703.getAmt2() != null &&
	 *             !inputVO_703.getAmt2().equals(outputVO_703.getAmt3())) {
	 *             isEqualAmt = false; } if (inputVO_703.getAmt3() != null &&
	 *             !inputVO_703.getAmt3().equals(outputVO_703.getAmt3())) {
	 *             isEqualAmt = false; }
	 * 
	 *             logger.debug("getAmtByProdCurr isEqualAmt:" + isEqualAmt); }
	 *             catch (Exception e) {
	 * 
	 *             String errorMsg = e.getMessage(); SOT712 sot712 = (SOT712)
	 *             PlatformContext.getBean("sot712"); errorMsg =
	 *             sot712.inquireErrorCode(e.getMessage());
	 *             logger.error(String.format("基金查詢換匯電文錯誤:%s", errorMsg), e);
	 *             throw new JBranchException(String.format("基金查詢換匯電文錯誤:%s",
	 *             errorMsg), e);
	 * 
	 *             } }
	 */

	//日期格式整合
	private String DateIntegration(String DateData) throws JBranchException {
		try {
			String[] DatesplitData = {};
			DatesplitData = StringUtils.split(DateData, "、");
			String DayList = "";
			for (int i = 0; i < DatesplitData.length; i++) {
				if (i == 0) {
					if (DatesplitData[i].length() <= 1) {
						DayList = "0" + DatesplitData[i];
					} else {
						DayList = DatesplitData[i];
					}
				} else {
					if (DatesplitData[i].length() <= 1) {
						DayList = DayList + "、0" + DatesplitData[i];
					} else {
						DayList = DayList + "、" + DatesplitData[i];
					}

				}
			}
			return DayList;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new APException("日期格式整合錯誤");
		}

	}

	// 加入購物車
	public void save(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM

		SOT150InputVO inputVO = (SOT150InputVO) body;
		inputVO = cleanAcctCcy(inputVO);
		SOT150OutputVO outputVO = new SOT150OutputVO();

		if (StringUtils.isNotBlank(inputVO.getfDebitAcct()))
			inputVO.setfDebitAcct(StringUtils.trim(inputVO.getfDebitAcct()));
		if (StringUtils.isNotBlank(inputVO.getbDebitAcct()))
			inputVO.setbDebitAcct(StringUtils.trim(inputVO.getbDebitAcct()));
		if (StringUtils.isNotBlank(inputVO.getfCreditAcct()))
			inputVO.setfCreditAcct(StringUtils.trim(inputVO.getfCreditAcct()));
		if (StringUtils.isNotBlank(inputVO.getbCreditAcct()))
			inputVO.setbCreditAcct(StringUtils.trim(inputVO.getbCreditAcct()));

		//高資產客戶投組風險權值檢核
		//需要做越級適配檢核：有變更標的、有恢復扣款、有變更金額且增加扣款金額、有變更扣款日期且有增加次數
		if(StringUtils.equals("Y", inputVO.getNeedHnwcRiskValueYN())) {
			String errorMsg = getHnwcRiskValue(inputVO);
			if(StringUtils.isNotBlank(errorMsg)) {
				throw new APException(errorMsg);
			}			
		}
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();

		if (null == vo) {

			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE("1"); //1:基金
			mainVO.setTRADE_TYPE("4"); //4:變更
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLV());
			mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);
			mainVO.setPROF_INVESTOR_YN(inputVO.getProfInvestorYN());
			mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);
			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setPI_REMARK(inputVO.getPiRemark());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
			mainVO.setTRADE_STATUS("1"); //1:暫存
			mainVO.setIS_BARGAIN_NEEDED("N");
			mainVO.setBARGAIN_FEE_FLAG(null);

			mainVO.setIS_REC_NEEDED("N");
			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);
			mainVO.setPROF_INVESTOR_TYPE(inputVO.getCustProType());
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
			
			dam.create(mainVO);
		} else {
			vo.setTRADE_STATUS("1"); //暫存

			dam.update(vo);
		}

		if (StringUtils.isNotBlank(inputVO.getbProdID()) && StringUtils.isNotBlank(inputVO.getfProdID()) && !StringUtils.equals(inputVO.getbProdID(), inputVO.getfProdID())) { //變更商品才要錄音序號
			QueryConditionIF updateCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			SOT712InputVO inputVO_712 = new SOT712InputVO();
			inputVO_712.setCustID(inputVO.getCustID());
			inputVO_712.setProdID(inputVO.getfProdID());
			inputVO_712.setProdType("NF");
			inputVO_712.setProfInvestorYN(inputVO.getProfInvestorYN());
			inputVO_712.setIsFirstTrade(this.isFirstTrade(inputVO)); //是否首購
			inputVO_712.setCustRemarks(inputVO.getCustRemarks());
			inputVO_712.setCustProRemark(inputVO.getPiRemark());
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N");
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE TBSOT_TRADE_MAIN SET IS_REC_NEEDED = :IS_REC_NEEDED WHERE TRADE_SEQ = :tradeSEQ");
			
			updateCondition.setObject("IS_REC_NEEDED", mainVO.getIS_REC_NEEDED());
			updateCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			updateCondition.setQueryString(sb.toString());
			dam.exeUpdate(updateCondition);
		}

		TBSOT_NF_CHANGE_DPK dPK = new TBSOT_NF_CHANGE_DPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(inputVO.getCarSEQ());
		TBSOT_NF_CHANGE_DVO dVO = new TBSOT_NF_CHANGE_DVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_CHANGE_DVO) dam.findByPKey(TBSOT_NF_CHANGE_DVO.TABLE_UID, dVO.getcomp_id());

		if (null != dVO) {
			dam.delete(dVO);

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM TBSOT_NF_CHANGE_BATCH WHERE TRADE_SEQ = :tradeSEQ AND SEQ_NO = :seqNO ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setObject("seqNO", inputVO.getCarSEQ());

			dam.exeUpdate(queryCondition);
		}

		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		BigDecimal newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
		TBSOT_NF_CHANGE_DPK dtlPK = new TBSOT_NF_CHANGE_DPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(newTrade_SEQNO);

		TBSOT_NF_CHANGE_DVO dtlVO = new TBSOT_NF_CHANGE_DVO();

		dtlVO.setTRADE_DATE_TYPE(inputVO.getTradeDateType());
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

		dtlVO.setcomp_id(dtlPK);

		dtlVO.setTRADE_SUB_TYPE(inputVO.getTradeSubType());
		dtlVO.setTRADE_SUB_TYPE_D(inputVO.getTradeSubTypeD());
		dtlVO.setCERTIFICATE_ID(StringUtils.trim(inputVO.getCertificateID()));

		//=== 變更前
		dtlVO.setB_PROD_ID(inputVO.getbProdID());
		dtlVO.setB_PROD_NAME(inputVO.getbProdName());
		dtlVO.setB_PROD_CURR(inputVO.getbProdCurr());
		dtlVO.setB_PROD_RISK_LV(inputVO.getbProdRiskLV());
		dtlVO.setB_TRUST_CURR(inputVO.getbTrustCurr());
		dtlVO.setB_TRUST_CURR_TYPE(inputVO.getbTrustCurrType());
		dtlVO.setB_TRUST_AMT(inputVO.getbTrustAmt());
		dtlVO.setB_PURCHASE_AMT_L(inputVO.getbPurchaseAmtL());
		dtlVO.setB_PURCHASE_AMT_M(inputVO.getbPurchaseAmtM());
		dtlVO.setB_PURCHASE_AMT_H(inputVO.getbPurchaseAmtH());
		dtlVO.setB_PURCHASE_AMT_EXCH_L(inputVO.getbPurchaseAmtExchL());//變更前每月扣款金額換匯_低
		dtlVO.setB_PURCHASE_AMT_EXCH_M(inputVO.getbPurchaseAmtExchM());
		dtlVO.setB_PURCHASE_AMT_EXCH_H(inputVO.getbPurchaseAmtExchH());
		dtlVO.setB_EXCH_CURR(inputVO.getbExchCurr()); //變更前 庫存每月扣款金額換匯電文回傳  幣別

		dtlVO.setB_NOT_VERTIFY(inputVO.getbNotVertify()); //轉出標的未核備(TBPRD_FUNDINFO.FUS40)

		String[] bChargeDate = {};
		if (StringUtils.isNotBlank(inputVO.getbChargeDateList())) {
			bChargeDate = StringUtils.split(inputVO.getbChargeDateList(), "、");
			if (bChargeDate.length > 0)
				dtlVO.setB_CHARGE_DATE_1(this.getChargeDateAddZero(StringUtils.trim(bChargeDate[0])));
			if (bChargeDate.length > 1)
				dtlVO.setB_CHARGE_DATE_2(this.getChargeDateAddZero(StringUtils.trim(bChargeDate[1])));
			if (bChargeDate.length > 2)
				dtlVO.setB_CHARGE_DATE_3(this.getChargeDateAddZero(StringUtils.trim(bChargeDate[2])));
			if (bChargeDate.length > 3)
				dtlVO.setB_CHARGE_DATE_4(this.getChargeDateAddZero(StringUtils.trim(bChargeDate[3])));
			if (bChargeDate.length > 4)
				dtlVO.setB_CHARGE_DATE_5(this.getChargeDateAddZero(StringUtils.trim(bChargeDate[4])));
			if (bChargeDate.length > 5)
				dtlVO.setB_CHARGE_DATE_6(this.getChargeDateAddZero(StringUtils.trim(bChargeDate[5])));
		}

		dtlVO.setB_DEBIT_ACCT(inputVO.getbDebitAcct());
		dtlVO.setB_CREDIT_ACCT(inputVO.getbCreditAcct());
		dtlVO.setB_TRUST_ACCT(inputVO.getbTrustAcct());
		dtlVO.setB_CERTIFICATE_STATUS(inputVO.getbCertificateStatus());

		//=== 變更後
		/*
		 * A9：扣款日期變更
		 * A8：扣款帳號變更
		 * AX：標的變更
		 * A7：金額變更
		 * X7：標的與金額變更
		 * B2：正常扣款變更
		 * B1：暫停扣款變更
		 * B8：終止憑證變更
		 * A0：新收益入帳帳號變更
		 */
		boolean a9Status = false;
		boolean axStatus = false;
		boolean a7Status = false;//金額變更
		String changeType = "";
		if (StringUtils.isNotBlank(inputVO.getfProdID()) && !StringUtils.equals(inputVO.getfProdID(), inputVO.getbProdID())) {
			axStatus = true; //標的變更
			dtlVO.setF_PROD_ID(StringUtils.isNotBlank(inputVO.getfProdID()) ? inputVO.getfProdID() : null);
			dtlVO.setF_PROD_NAME(StringUtils.isNotBlank(inputVO.getfProdName()) ? inputVO.getfProdName() : null);
			dtlVO.setF_PROD_CURR(StringUtils.isNotBlank(inputVO.getfProdCurr()) ? inputVO.getfProdCurr() : null);
			dtlVO.setF_PROD_RISK_LV(StringUtils.isNotBlank(inputVO.getfProdRiskLV()) ? inputVO.getfProdRiskLV() : null);
		}
		dtlVO.setF_TRUST_CURR(StringUtils.isNotBlank(inputVO.getfTrustCurr()) ? inputVO.getfTrustCurr() : null); //變更後信託幣別

		if (null != inputVO.getfPurchaseAmtL() && null != inputVO.getbEquivalentAmtL() && inputVO.getbEquivalentAmtL().compareTo(inputVO.getfPurchaseAmtL()) != 0) {
			a7Status = true;//金額變更
			dtlVO.setF_PROD_ID(StringUtils.isNotBlank(inputVO.getfProdID()) ? inputVO.getfProdID() : null);
			dtlVO.setF_PROD_NAME(StringUtils.isNotBlank(inputVO.getfProdName()) ? inputVO.getfProdName() : null); //金額變更報表 需要商品名稱
			dtlVO.setF_PROD_CURR(StringUtils.isNotBlank(inputVO.getfProdCurr()) ? inputVO.getfProdCurr() : null);
			dtlVO.setF_PROD_RISK_LV(StringUtils.isNotBlank(inputVO.getfProdRiskLV()) ? inputVO.getfProdRiskLV() : null);
		}
		dtlVO.setF_PROD_ID(StringUtils.isNotBlank(inputVO.getfProdID()) ? inputVO.getfProdID() : null); //只改金額也要傳 fProdID 給 X7電文(都call X7)
		dtlVO.setF_PURCHASE_AMT_L(inputVO.getfPurchaseAmtL());


		// 參考 SOT.ASSET_TRADE_SUB_TYPE
		if (FundRule.isMultipleN(inputVO.getTradeSubType())) {
			if (null != inputVO.getfPurchaseAmtM() && inputVO.getbEquivalentAmtM().compareTo(inputVO.getfPurchaseAmtM()) != 0) {
				a7Status = true;
			} else if (a7Status) {
			}
			dtlVO.setF_PURCHASE_AMT_M(inputVO.getfPurchaseAmtM());
			if (null != inputVO.getfPurchaseAmtH() && inputVO.getbEquivalentAmtH().compareTo(inputVO.getfPurchaseAmtH()) != 0) {
				a7Status = true;
			} else if (a7Status) {
			}
			dtlVO.setF_PURCHASE_AMT_H(inputVO.getfPurchaseAmtH());
		}

		String[] chargeDate = {};

		if (StringUtils.isNotBlank(inputVO.getbChargeDateList()) && StringUtils.isNotBlank(inputVO.getChargeDateList()) && !StringUtils.equals(DateIntegration(inputVO.getbChargeDateList()), DateIntegration(inputVO.getChargeDateList()))) {
			chargeDate = StringUtils.split(inputVO.getChargeDateList(), "、");
			a9Status = true;

			if (chargeDate.length > 0)
				dtlVO.setF_CHARGE_DATE_1(this.getChargeDateAddZero(StringUtils.trim(chargeDate[0])));

			if (chargeDate.length > 1)
				dtlVO.setF_CHARGE_DATE_2(this.getChargeDateAddZero(StringUtils.trim(chargeDate[1])));

			if (chargeDate.length > 2)
				dtlVO.setF_CHARGE_DATE_3(this.getChargeDateAddZero(StringUtils.trim(chargeDate[2])));

			if (chargeDate.length > 3)
				dtlVO.setF_CHARGE_DATE_4(this.getChargeDateAddZero(StringUtils.trim(chargeDate[3])));

			if (chargeDate.length > 4)
				dtlVO.setF_CHARGE_DATE_5(this.getChargeDateAddZero(StringUtils.trim(chargeDate[4])));

			if (chargeDate.length > 5)
				dtlVO.setF_CHARGE_DATE_6(this.getChargeDateAddZero(StringUtils.trim(chargeDate[5])));
		}

		/*
		dtlVO.setF_DEBIT_ACCT(StringUtils.equals(inputVO.getfDebitAcct(), inputVO.getbDebitAcct()) ? null : inputVO.getfDebitAcct());     //扣款帳號
		dtlVO.setF_CREDIT_ACCT(StringUtils.equals(inputVO.getfCreditAcct(), inputVO.getbCreditAcct()) ? null : inputVO.getfCreditAcct()); //新收益入帳帳號
		*/
		//為了編輯購物車，所以要存帳號，即使沒變更
		dtlVO.setF_DEBIT_ACCT(inputVO.getfDebitAcct()); //扣款帳號
		dtlVO.setF_CREDIT_ACCT(inputVO.getfCreditAcct()); //新收益入帳帳號

		//NFBRN9 (客戶庫存查詢) 定期定額C30狀態	Status  0:正常 1:暫停 2:非主標的 3:終止
		dtlVO.setF_CERTIFICATE_STATUS(StringUtils.equals(inputVO.getfCertificateStatus(), inputVO.getbCertificateStatus()) ? null : inputVO.getfCertificateStatus());
		dtlVO.setF_HOLD_START_DATE(!StringUtils.equals(inputVO.getfCertificateStatus(), inputVO.getbCertificateStatus()) && StringUtils.equals("1", inputVO.getfCertificateStatus()) ? new Timestamp(inputVO.getfHoldStartDate().getTime()) : null);
		dtlVO.setF_HOLD_END_DATE(!StringUtils.equals(inputVO.getfCertificateStatus(), inputVO.getbCertificateStatus()) && StringUtils.equals("1", inputVO.getfCertificateStatus()) ? new Timestamp(inputVO.getfHoldEndDate().getTime()) : null);

		if (!"1".equals(inputVO.getTradeSubType())) { //1.單筆以外
			dtlVO.setF_RESUME_DATE(!StringUtils.equals(inputVO.getfCertificateStatus(), inputVO.getbCertificateStatus()) && StringUtils.equals("0", inputVO.getfCertificateStatus()) ? new Timestamp(inputVO.getfResumeDate().getTime()) : null); //變更後恢復正常扣款起日
		}
		dtlVO.setF_FEE_L(null);
		dtlVO.setF_FEE_M(null);
		dtlVO.setF_FEE_H(null);
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID());
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName());

		if (axStatus && a7Status) {
			dtlVO.setPROSPECTUS_TYPE(inputVO.getProspectusType()); //公開說明書交付方式
			changeType = changeType + "X7,";
			addBatch(dam, inputVO, newTrade_SEQNO, "X7"); //X7：標的與金額變更 
		} else if (axStatus) {
			dtlVO.setPROSPECTUS_TYPE(inputVO.getProspectusType()); //公開說明書交付方式
			changeType = changeType + "AX,";
			addBatch(dam, inputVO, newTrade_SEQNO, "AX"); //AX：標的變更
		} else if (a7Status) {
			dtlVO.setPROSPECTUS_TYPE(inputVO.getProspectusType()); //公開說明書交付方式
			changeType = changeType + "A7,";
			addBatch(dam, inputVO, newTrade_SEQNO, "A7"); //A7：金額變更
		}

		if (a9Status) {
			changeType = changeType + "A9,";
			addBatch(dam, inputVO, newTrade_SEQNO, "A9");
		}

		//A8：扣款帳號變更
		if (StringUtils.isNotBlank(inputVO.getfDebitAcct()) && !StringUtils.equals(inputVO.getfDebitAcct(), inputVO.getbDebitAcct())) {
			changeType = changeType + "A8,";
			addBatch(dam, inputVO, newTrade_SEQNO, "A8");
		}

		//A0：新收益入帳帳號變更
		if (StringUtils.isNotBlank(inputVO.getfCreditAcct()) && !StringUtils.equals(inputVO.getfCreditAcct(), inputVO.getbCreditAcct())) {
			changeType = changeType + "A0,";
			addBatch(dam, inputVO, newTrade_SEQNO, "A0");
		}

		/**
		 * DB的 fCertificateStatus/bCertificateStatus 應該跟庫文電文 的狀態一致: NFBRN9
		 * (客戶庫存查詢) 定期定額C30狀態 Status 0:正常 1:暫停 2:非主標的 3:終止
		 */
		logger.debug(String.format("afterStatus:%s  beforeStatus:%s", inputVO.getfCertificateStatus(), inputVO.getbCertificateStatus()));
		if (StringUtils.isNotBlank(inputVO.getfCertificateStatus()) && !StringUtils.equals(inputVO.getfCertificateStatus(), inputVO.getbCertificateStatus())) {
			Integer afterCertificateStatus = Integer.valueOf(inputVO.getfCertificateStatus());//變更後狀態
			switch (afterCertificateStatus) {
			case 0: //0:正常
				changeType = changeType + "B2,"; //B2恢復
				addBatch(dam, inputVO, newTrade_SEQNO, "B2");
				break;
			case 1: //1:暫停
				changeType = changeType + "B1,"; //B1暫停
				addBatch(dam, inputVO, newTrade_SEQNO, "B1");
				break;
			case 3: //3終止(非電文status)
				changeType = changeType + "B8,"; //B8終止      
				addBatch(dam, inputVO, newTrade_SEQNO, "B8");
				break;
			}
		}

		dtlVO.setCHANGE_TYPE(changeType.length() > 0 ? changeType.substring(0, changeType.length() - 1) : null);

		if (changeType.length() > 0) {
			dam.create(dtlVO);
		}

		// 2.檢核電文
		SOT703InputVO inputVO_703 = new SOT703InputVO();
		SOT703OutputVO outputVO_703 = new SOT703OutputVO();
		inputVO_703.setCustId(inputVO.getCustID());
		inputVO_703.setTradeSeq(inputVO.getTradeSEQ());
		inputVO_703.setSeqNo(newTrade_SEQNO.toString());

		SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
		try {
			outputVO_703 = sot703.verifyESBChangeNF(inputVO_703);
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			sot712 = (SOT712) PlatformContext.getBean("sot712");
			errorMsg = sot712.inquireErrorCode(e.getMessage());
			logger.error(String.format("基金變更檢核電文sot703.verifyESBChangeNF錯誤:%s", errorMsg), e);
			throw new JBranchException(String.format("基金變更檢核電文錯誤:%s", errorMsg), e);
		}
		String errorMsg = outputVO_703.getErrorMsg();
		String errorCode = outputVO_703.getErrorCode();
		if (StringUtils.isNotBlank(errorCode) || StringUtils.isNotBlank(errorMsg)) {
			logger.error(String.format("sot703.verifyESBChangeNF()  tradeSeq:%s , seqNo:%s  outputVO_703.Error:%s %s", inputVO_703.getTradeSeq(), inputVO_703.getSeqNo(), outputVO_703.getErrorCode(), outputVO_703.getErrorMsg()));

			TBSOT_NF_CHANGE_DPK errPK = dtlVO.getcomp_id();
			TBSOT_NF_CHANGE_DVO errVO = new TBSOT_NF_CHANGE_DVO();
			errVO.setcomp_id(errPK);
			errVO = (TBSOT_NF_CHANGE_DVO) dam.findByPKey(TBSOT_NF_CHANGE_DVO.TABLE_UID, errVO.getcomp_id());
			if (errVO != null)
				dam.delete(errVO);

			StringBuffer sb = new StringBuffer();

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("DELETE FROM TBSOT_NF_CHANGE_BATCH WHERE TRADE_SEQ = :tradeSEQ AND SEQ_NO = :seqNO ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("tradeSEQ", errPK.getTRADE_SEQ());
			queryCondition.setObject("seqNO", errPK.getSEQ_NO());
			dam.exeUpdate(queryCondition);

			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			//===若無明細，則將主檔也刪除
			sb = new StringBuffer();
			sb.append("SELECT TRADE_SEQ, SEQ_NO, TRADE_SUB_TYPE, CERTIFICATE_ID, B_PROD_ID, B_PROD_NAME, B_PROD_CURR, B_PROD_RISK_LV, B_TRUST_CURR, B_TRUST_CURR_TYPE, B_TRUST_AMT, B_PURCHASE_AMT_L, B_PURCHASE_AMT_M, B_PURCHASE_AMT_H, B_CHARGE_DATE_1, B_CHARGE_DATE_2, B_CHARGE_DATE_3, B_CHARGE_DATE_4, B_CHARGE_DATE_5, B_CHARGE_DATE_6, B_DEBIT_ACCT, B_CREDIT_ACCT, B_TRUST_ACCT, B_CERTIFICATE_STATUS, F_PROD_ID, F_PROD_NAME, F_PROD_CURR, F_PROD_RISK_LV, F_TRUST_CURR, F_PURCHASE_AMT_L, F_PURCHASE_AMT_M, F_PURCHASE_AMT_H, F_CHARGE_DATE_1, F_CHARGE_DATE_2, F_CHARGE_DATE_3, F_CHARGE_DATE_4, F_CHARGE_DATE_5, F_CHARGE_DATE_6, F_DEBIT_ACCT, F_CREDIT_ACCT, F_CERTIFICATE_STATUS, F_HOLD_START_DATE, F_HOLD_END_DATE, F_RESUME_DATE, F_FEE_L, F_FEE_M, F_FEE_H, NARRATOR_ID, NARRATOR_NAME, CHANGE_TYPE ");
			sb.append("FROM TBSOT_NF_CHANGE_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", errPK.getTRADE_SEQ());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, errPK.getTRADE_SEQ());

				dam.delete(mVO);
			}
			//===
            if(errorCode.equals("E999")){
            	errorMsg = "無法受理未持有居留證之外國人申請本交易或檢視居留證效期";
            }
			outputVO.setErrorMsg(errorCode + " " + errorMsg);
		}

		this.sendRtnObject(outputVO);
	}

	private void addBatch(DataAccessManager dam, SOT150InputVO inputVO, BigDecimal newTrade_SEQNO, String changeType) throws JBranchException {

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SQ_TBSOT_NF_CHANGE_BATCH.nextval AS SEQNO FROM DUAL");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> seq = dam.exeQuery(queryCondition);

		TBSOT_NF_CHANGE_BATCHVO vo = new TBSOT_NF_CHANGE_BATCHVO();
		vo.setSERIAL_NO((BigDecimal) seq.get(0).get("SEQNO"));
		vo.setTRADE_SEQ(inputVO.getTradeSEQ());
		vo.setSEQ_NO(newTrade_SEQNO);
		//vo.setBATCH_NO(null);
		//vo.setBATCH_SEQ(null);
		vo.setCHANGE_TYPE(changeType);

		dam.create(vo);
	}

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT150InputVO inputVO = (SOT150InputVO) body;
		SOT150OutputVO outputVO = new SOT150OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT MI.TRADE_SEQ, ");
		sb.append("MI.SEQ_NO, ");
		sb.append("DI.B_PROD_ID || ' ' || DI.B_PROD_NAME AS PROD_INFO, ");
		sb.append("MI.CHANGE_TYPE, ");
		sb.append("DI.PROSPECTUS_TYPE, ");
		sb.append("DI.B_CERTIFICATE_STATUS, ");
		sb.append("DI.F_CERTIFICATE_STATUS, ");
		sb.append("DI.B_PURCHASE_AMT_EXCH_L, DI.B_PURCHASE_AMT_EXCH_M, DI.B_PURCHASE_AMT_EXCH_H, ");
		sb.append("CASE WHEN MI.CHANGE_TYPE = 'A9' THEN NVL2(DI.B_CHARGE_DATE_1, DI.B_CHARGE_DATE_1, '') || NVL2(DI.B_CHARGE_DATE_2, ',' || DI.B_CHARGE_DATE_2, '') || NVL2(DI.B_CHARGE_DATE_3, ',' || DI.B_CHARGE_DATE_3, '') || NVL2(DI.B_CHARGE_DATE_4, ',' || DI.B_CHARGE_DATE_4, '') || NVL2(DI.B_CHARGE_DATE_5, ',' || DI.B_CHARGE_DATE_5, '') || NVL2(DI.B_CHARGE_DATE_6, ',' || DI.B_CHARGE_DATE_6, '') ");
		sb.append("WHEN MI.CHANGE_TYPE = 'A8' THEN DI.B_DEBIT_ACCT ");
		sb.append("WHEN MI.CHANGE_TYPE = 'AX' THEN DI.B_PROD_ID || ' ' || DI.B_PROD_NAME ");
		sb.append("WHEN MI.CHANGE_TYPE = 'A7' THEN NVL2(DI.B_PURCHASE_AMT_L, to_char(DI.B_PURCHASE_AMT_L, 'FM999,999,999,999,999.00'), '') || NVL2(DI.B_PURCHASE_AMT_M, ' / ' || to_char(DI.B_PURCHASE_AMT_M, 'FM999,999,999,999,999.00'), '') || NVL2(DI.B_PURCHASE_AMT_H, ' / ' || to_char(DI.B_PURCHASE_AMT_H, 'FM999,999,999,999,999.00'), '') ");
		sb.append("WHEN MI.CHANGE_TYPE = 'X7' THEN DI.B_PROD_ID || ' ' || DI.B_PROD_NAME || '\n' || NVL2(DI.B_PURCHASE_AMT_L, to_char(DI.B_PURCHASE_AMT_L, 'FM999,999,999,999,999.00'), '') || NVL2(DI.B_PURCHASE_AMT_M, ' / ' || to_char(DI.B_PURCHASE_AMT_M, 'FM999,999,999,999,999.00'), '') || NVL2(DI.B_PURCHASE_AMT_H, ' / ' || to_char(DI.B_PURCHASE_AMT_H, 'FM999,999,999,999,999.00'), '')  ");
		sb.append("WHEN MI.CHANGE_TYPE = 'B2' THEN '' ");
		sb.append("WHEN MI.CHANGE_TYPE = 'B1' THEN '' ");
		sb.append("WHEN MI.CHANGE_TYPE = 'B8' THEN '' ");
		sb.append("WHEN MI.CHANGE_TYPE = 'A0' THEN DI.B_CREDIT_ACCT ");
		sb.append("ELSE '' END AS BEFORE_DATA, ");
		sb.append("CASE WHEN MI.CHANGE_TYPE = 'A9' THEN NVL2(DI.F_CHARGE_DATE_1, DI.F_CHARGE_DATE_1, '') || NVL2(DI.F_CHARGE_DATE_2, ',' || DI.F_CHARGE_DATE_2, '') || NVL2(DI.F_CHARGE_DATE_3, ',' || DI.F_CHARGE_DATE_3, '') || NVL2(DI.F_CHARGE_DATE_4, ',' || DI.F_CHARGE_DATE_4, '') || NVL2(DI.F_CHARGE_DATE_5, ',' || DI.F_CHARGE_DATE_5, '') || NVL2(DI.F_CHARGE_DATE_6, ',' || DI.F_CHARGE_DATE_6, '') ");
		sb.append("WHEN MI.CHANGE_TYPE = 'A8' THEN DI.F_DEBIT_ACCT ");
		sb.append("WHEN MI.CHANGE_TYPE = 'AX' THEN DI.F_PROD_ID || ' ' || DI.F_PROD_NAME ");
		sb.append("WHEN MI.CHANGE_TYPE = 'A7' THEN NVL2(DI.F_PURCHASE_AMT_L, to_char(DI.F_PURCHASE_AMT_L, 'FM999,999,999,999,999.00'), '') || NVL2(DI.F_PURCHASE_AMT_M, ' / ' || to_char(DI.F_PURCHASE_AMT_M, 'FM999,999,999,999,999.00'), '') || NVL2(DI.F_PURCHASE_AMT_H, ' / ' || to_char(DI.F_PURCHASE_AMT_H, 'FM999,999,999,999,999.00'), '') ");
		sb.append("WHEN MI.CHANGE_TYPE = 'X7' THEN DI.F_PROD_ID || ' ' || DI.F_PROD_NAME || '\n' || NVL2(DI.F_PURCHASE_AMT_L, to_char(DI.F_PURCHASE_AMT_L, 'FM999,999,999,999,999.00'), '') || NVL2(DI.F_PURCHASE_AMT_M, ' / ' || to_char(DI.F_PURCHASE_AMT_M, 'FM999,999,999,999,999.00'), '') || NVL2(DI.F_PURCHASE_AMT_H, ' / ' || to_char(DI.F_PURCHASE_AMT_H, 'FM999,999,999,999,999.00'), '')  ");
		sb.append("WHEN MI.CHANGE_TYPE = 'B2' THEN TO_CHAR(DI.F_RESUME_DATE, 'yyyy-MM-dd') ");
		sb.append("WHEN MI.CHANGE_TYPE = 'B1' THEN TO_CHAR(DI.F_HOLD_START_DATE, 'yyyy-MM-dd') || '~' || TO_CHAR(DI.F_HOLD_END_DATE, 'yyyy-MM-dd') ");
		sb.append("WHEN MI.CHANGE_TYPE = 'B8' THEN '' ");
		sb.append("WHEN MI.CHANGE_TYPE = 'A0' THEN DI.F_CREDIT_ACCT ");
		sb.append("ELSE '' END AS AFTER_DATA, ");
		sb.append("DI.TRADE_SUB_TYPE, DI.TRADE_SUB_TYPE_D, DI.CERTIFICATE_ID, ");
		sb.append("DI.B_PROD_ID, DI.B_PROD_NAME, DI.B_PROD_CURR, DI.B_PROD_RISK_LV, DI.B_TRUST_CURR, DI.B_TRUST_CURR_TYPE, DI.B_TRUST_AMT, DI.B_PURCHASE_AMT_L, DI.B_PURCHASE_AMT_M, DI.B_PURCHASE_AMT_H, DI.B_CHARGE_DATE_1, DI.B_CHARGE_DATE_2, DI.B_CHARGE_DATE_3, DI.B_CHARGE_DATE_4, DI.B_CHARGE_DATE_5, DI.B_CHARGE_DATE_6, DI.B_DEBIT_ACCT, DI.B_CREDIT_ACCT, DI.B_TRUST_ACCT, ");
		sb.append("DI.F_PROD_ID, DI.F_PROD_NAME, DI.F_PROD_CURR, DI.F_PROD_RISK_LV, DI.F_TRUST_CURR, DI.F_PURCHASE_AMT_L, DI.F_PURCHASE_AMT_M, DI.F_PURCHASE_AMT_H, DI.F_CHARGE_DATE_1, DI.F_CHARGE_DATE_2, DI.F_CHARGE_DATE_3, DI.F_CHARGE_DATE_4, DI.F_CHARGE_DATE_5, DI.F_CHARGE_DATE_6, DI.F_DEBIT_ACCT, DI.F_CREDIT_ACCT, DI.F_HOLD_START_DATE, DI.F_HOLD_END_DATE, DI.F_RESUME_DATE, DI.F_FEE_L, DI.F_FEE_M, DI.F_FEE_H, DI.NARRATOR_ID, DI.NARRATOR_NAME ");
		sb.append(", DI.TRADE_DATE_TYPE, DI.TRADE_DATE ");
		sb.append("FROM TBSOT_NF_CHANGE_BATCH MI ");
		sb.append("LEFT JOIN TBSOT_NF_CHANGE_D DI ON MI.TRADE_SEQ = DI.TRADE_SEQ AND MI.SEQ_NO = DI.SEQ_NO ");
		sb.append("WHERE MI.TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setCarList(dam.exeQuery(queryCondition));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT TRADE_SEQ, PROD_TYPE, TRADE_TYPE, CUST_ID, CUST_NAME, AGENT_ID, AGENT_NAME, KYC_LV, KYC_DUE_DATE, PROF_INVESTOR_YN, PI_DUE_DATE, CUST_REMARKS, IS_OBU, IS_AGREE_PROD_ADV, BARGAIN_DUE_DATE, TRADE_STATUS, IS_BARGAIN_NEEDED, BARGAIN_FEE_FLAG, IS_REC_NEEDED, REC_SEQ, SEND_DATE, PROF_INVESTOR_TYPE ");
		sb.append("FROM TBSOT_TRADE_MAIN ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setMainList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	public void next(Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		SOT150InputVO inputVO = (SOT150InputVO) body;
		SOT150OutputVO outputVO = new SOT150OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null != vo) {
			vo.setTRADE_STATUS("2"); //2:風控檢核中
			vo.setModifier(ws.getUser().getUserID());
			vo.setLastupdate(new Timestamp(System.currentTimeMillis()));

			dam.update(vo);
		}

		this.sendRtnObject(null);
	}

	public void delProd(Object body, IPrimitiveMap header) throws JBranchException {

		SOT150InputVO inputVO = (SOT150InputVO) body;
		SOT150OutputVO outputVO = new SOT150OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TRADE_SEQ, SEQ_NO, TRADE_SUB_TYPE, CERTIFICATE_ID, B_PROD_ID, B_PROD_NAME, B_PROD_CURR, B_PROD_RISK_LV, B_TRUST_CURR, B_TRUST_CURR_TYPE, B_TRUST_AMT, B_PURCHASE_AMT_L, B_PURCHASE_AMT_M, B_PURCHASE_AMT_H, B_CHARGE_DATE_1, B_CHARGE_DATE_2, B_CHARGE_DATE_3, B_CHARGE_DATE_4, B_CHARGE_DATE_5, B_CHARGE_DATE_6, B_DEBIT_ACCT, B_CREDIT_ACCT, B_TRUST_ACCT, B_CERTIFICATE_STATUS, F_PROD_ID, F_PROD_NAME, F_PROD_CURR, F_PROD_RISK_LV, F_TRUST_CURR, F_PURCHASE_AMT_L, F_PURCHASE_AMT_M, F_PURCHASE_AMT_H, F_CHARGE_DATE_1, F_CHARGE_DATE_2, F_CHARGE_DATE_3, F_CHARGE_DATE_4, F_CHARGE_DATE_5, F_CHARGE_DATE_6, F_DEBIT_ACCT, F_CREDIT_ACCT, F_CERTIFICATE_STATUS, F_HOLD_START_DATE, F_HOLD_END_DATE, F_RESUME_DATE, F_FEE_L, F_FEE_M, F_FEE_H, NARRATOR_ID, NARRATOR_NAME, CHANGE_TYPE ");
		sb.append("FROM TBSOT_NF_CHANGE_D ");
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		if (list.size() == 1) {
			TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
			vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
			if (vo != null) {
				dam.delete(vo);
			}
		}

		TBSOT_NF_CHANGE_DPK dPK = new TBSOT_NF_CHANGE_DPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(inputVO.getCarSEQ());
		TBSOT_NF_CHANGE_DVO dVO = new TBSOT_NF_CHANGE_DVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_CHANGE_DVO) dam.findByPKey(TBSOT_NF_CHANGE_DVO.TABLE_UID, dVO.getcomp_id());
		if (dVO != null) {
			dam.delete(dVO);
		}
		sb = new StringBuffer();
		QueryConditionIF queryConditionB = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("DELETE FROM TBSOT_NF_CHANGE_BATCH WHERE TRADE_SEQ = :tradeSEQ AND SEQ_NO = :seqNO ");
		queryConditionB.setQueryString(sb.toString());
		queryConditionB.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryConditionB.setObject("seqNO", inputVO.getCarSEQ());
		dam.exeUpdate(queryConditionB);

		this.sendRtnObject(null);
	}

	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT150InputVO inputVO = (SOT150InputVO) body;
		SOT150OutputVO outputVO = new SOT150OutputVO();
		dam = this.getDataAccessManager();

		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		//mainVo.setPROSPECTUS_TYPE(inputVO.getProspectusType());  改為子表 存公開說明書
		if (StringUtils.isNotBlank(inputVO.getRecSEQ())) {
			mainVo.setREC_SEQ(inputVO.getRecSEQ());//錄音序號
		}
		dam.update(mainVo);

		SOT703InputVO inputVO_703 = new SOT703InputVO();
		SOT703OutputVO outputVO_703 = new SOT703OutputVO();
		inputVO_703.setCustId(inputVO.getCustID());
		inputVO_703.setTradeSeq(inputVO.getTradeSEQ());

		SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
		try {
			outputVO_703 = sot703.confirmESBChangeNF(inputVO_703);
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			errorMsg = sot712.inquireErrorCode(e.getMessage());
			logger.error(String.format("基金變更確認電文sot703.confirmESBChangeNF錯誤:%s", errorMsg), e);
			throw new JBranchException(String.format("基金變更確認電文錯誤:%s", errorMsg), e);
		}

		System.out.println("123");
		String errorMsg = outputVO_703.getErrorMsg();
		if (StringUtils.isNotBlank(outputVO_703.getErrorCode()) || StringUtils.isNotBlank(errorMsg)) {
			logger.error(String.format("sot703.confirmESBChangeNF()  tradeSeq:%s , seqNo:%s  outputVO_703.Error:%s %s", inputVO_703.getTradeSeq(), inputVO_703.getSeqNo(), outputVO_703.getErrorCode(), outputVO_703.getErrorMsg()));

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

	/**
	 * 是否進行適配
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private boolean isRunFitness() throws JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.FITNESS_YN' AND PARAM_CODE = 'NF' ");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0) {
			String runFitness = list.get(0).get("PARAM_NAME").toString();
			return ("Y".equals(runFitness) ? true : false);
		}
		return true;
	}

	/**
	 * 清除 帳號的幣別
	 * 
	 * @param inputVO
	 * @return
	 * @throws JBranchException
	 */
	private SOT150InputVO cleanAcctCcy(SOT150InputVO inputVO) throws JBranchException {
		if (StringUtils.isNotBlank(inputVO.getfDebitAcct()) && StringUtils.indexOf(inputVO.getfDebitAcct(), "_") > 0)
			inputVO.setfDebitAcct(StringUtils.split(inputVO.getfDebitAcct(), "_")[0]);
		else if (StringUtils.isBlank(inputVO.getfDebitAcct()) || StringUtils.indexOf(inputVO.getfDebitAcct(), "_") == 0 || StringUtils.endsWithIgnoreCase("null", inputVO.getfDebitAcct())) {
			inputVO.setfDebitAcct(null);// 無對應帳號 只有幣別
		}

		return inputVO;
	}

	/**
	 *  MIMI 2/18 (公開說明書) 欄位都先寫死放1
	 * 
	 * @param inputVO
	 * @return
	 */
	//	private String getProspectusType(SOT150InputVO inputVO) {
	//		logger.error(" 2/18 (公開說明書) 欄位都先寫死放1");
	//		/*MIMI 2/18
	//		每一支申購交易
	//		TBSOT_TRADE_MAIN.PROSPECTUS_TYPE (公開說明書) 欄位都先寫死放1
	//		1.已取得公開說明書
	//		2.已由經理公司交付或貴行取得
	//		
	//		*/
	//		return "1";
	//	}

	/**
	 * 日期要補0
	 * 
	 * @param date
	 * @return
	 */
	private String getChargeDateAddZero(String date) {
		if (date.length() < 2) {
			return "0" + date;
		} else
			return date;
	}

	/***
	 * 是否為新興市場之非投資等級債券型基金
	 * 若是回傳Y 否則N
	 * @param body
	 * @param header
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public void getNFS100YN(Object body, IPrimitiveMap header) throws DAOException, JBranchException {
		SOT150InputVO inputVO = (SOT150InputVO) body;
		SOT150OutputVO outputVO = new SOT150OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT 1 FROM TBPRD_NFS100 WHERE PRD_ID = :prdId "); //此TABLE中商品為新興市場之非投資等級債券型基金
		queryCondition.setObject("prdId", inputVO.getbProdID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setNfs100YN(CollectionUtils.isNotEmpty(list) ? "Y" : "N");
		
		this.sendRtnObject(outputVO);
	}
		
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getHnwcRiskValue(SOT150InputVO inputVO) throws Exception {
		//取得購物車中各商品P值申購金額合計
		Map<String, BigDecimal> cartAmt = getAmtBuyByPVal(inputVO.getTradeSEQ()); 
		//取得購物車中各商品P值贖回金額合計(有變更標的，將變更前金額當作贖回金額)
		Map<String, BigDecimal> cartSell = getAmtSellByPVal(inputVO.getTradeSEQ()); 
		//取得這次申購商品幣別買匯
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		BigDecimal buyRate = sot110.getBuyRate(StringUtils.isBlank(inputVO.getfTrustCurr()) ? inputVO.getbTrustCurr() : inputVO.getfTrustCurr());
		//取得這次贖回商品幣別買匯
		BigDecimal sellRate = sot110.getBuyRate(inputVO.getbTrustCurr());
		
		//有變更投資標的，以變更後計算；否則以變更前計算
		String prodRiskLv = StringUtils.isBlank(inputVO.getfProdRiskLV()) ? inputVO.getbProdRiskLV() : inputVO.getfProdRiskLV();
		//取得高資產客戶註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCustID());
		
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		inputVO714.setCUST_KYC(inputVO.getKycLV()); //客戶C值
		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(prodRiskLv); //商品風險檢核值
		BigDecimal amtBuy = BigDecimal.ZERO;
		BigDecimal amtSell = BigDecimal.ZERO;
		if(inputVO.getTradeSubType().matches("3|5|9")) {
			//定期不定額
			amtBuy = (inputVO.getfPurchaseAmtM() == null ? inputVO.getbPurchaseAmtM() : inputVO.getfPurchaseAmtM()).multiply(buyRate);//這次申購金額折台
			amtSell = (inputVO.getfPurchaseAmtM() == null ? BigDecimal.ZERO : inputVO.getbPurchaseAmtM()).multiply(sellRate);//這次贖回金額折台(有變更標的)
		} else {
			//定期定額
			amtBuy = (inputVO.getfPurchaseAmtL() == null ? inputVO.getbPurchaseAmtL() : inputVO.getfPurchaseAmtL()).multiply(buyRate);//這次申購金額折台
			amtSell = (inputVO.getfPurchaseAmtL() == null ? BigDecimal.ZERO : inputVO.getbPurchaseAmtL()).multiply(sellRate);//這次贖回金額折台(有變更標的)
		}
		
		for(int i = 1; i <= 4; i++) {
			String pVal = "P" + String.valueOf(i);
			//以這次申購商品風險檢核值判斷是否需要加上這次申購金額
			BigDecimal amt = StringUtils.equals(pVal, prodRiskLv) ? amtBuy : BigDecimal.ZERO;
			//以這次贖回商品風險檢核值判斷是否需要加上這次贖回金額
//			BigDecimal amtSellPval = (StringUtils.isNotBlank(inputVO.getfProdRiskLV()) && StringUtils.equals(pVal, inputVO.getbProdRiskLV())) ? amtSell : BigDecimal.ZERO;
			//這次申購金額加上購物車中各商品P值申購金額
			//有變更標的：這次變更前金額加上購物車中各商品P值贖回金額
			//變更不送sell，送新的buy就好
			switch(pVal) {
				case "P1":
					inputVO714.setAMT_BUY_1(amt.add(cartAmt.get("P1") == null ? BigDecimal.ZERO : cartAmt.get("P1"))); 
//					inputVO714.setAMT_SELL_1(amtSellPval.add(cartSell.get("P1") == null ? BigDecimal.ZERO : cartSell.get("P1")));
					inputVO714.setAMT_SELL_1(BigDecimal.ZERO);
					break;
				case "P2":
					inputVO714.setAMT_BUY_2(amt.add(cartAmt.get("P2") == null ? BigDecimal.ZERO : cartAmt.get("P2")));
//					inputVO714.setAMT_SELL_2(amtSellPval.add(cartSell.get("P2") == null ? BigDecimal.ZERO : cartSell.get("P2")));
					inputVO714.setAMT_SELL_2(BigDecimal.ZERO);
					break;
				case "P3":
					inputVO714.setAMT_BUY_3(amt.add(cartAmt.get("P3") == null ? BigDecimal.ZERO : cartAmt.get("P3")));
//					inputVO714.setAMT_SELL_3(amtSellPval.add(cartSell.get("P3") == null ? BigDecimal.ZERO : cartSell.get("P3")));
					inputVO714.setAMT_SELL_3(BigDecimal.ZERO);
					break;
				case "P4":
					inputVO714.setAMT_BUY_4(amt.add(cartAmt.get("P4") == null ? BigDecimal.ZERO : cartAmt.get("P4")));
//					inputVO714.setAMT_SELL_4(amtSellPval.add(cartSell.get("P4") == null ? BigDecimal.ZERO : cartSell.get("P4")));
					inputVO714.setAMT_SELL_4(BigDecimal.ZERO);
					break;
			}
		}
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		//若檢核不通過，產生錯誤訊息
		String errorMsg = "";
		if(!StringUtils.equals("Y", riskValData.getVALIDATE_YN())) {
			if(cartAmt.isEmpty() || (cartAmt.size() == 1 && cartAmt.containsKey(prodRiskLv))) {
				//若購物車中沒有資料，或多檔都是同一P值(購物車合計中只有一筆且與本次申購商品P值相同)
				BigDecimal leftAmt = BigDecimal.ZERO;
  				switch(prodRiskLv) {
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
  				DecimalFormat df = new DecimalFormat("#,###.00");
//  				errorMsg = "高資產客戶越級金額需低於" + df.format(leftAmt);
  				errorMsg = "客戶投資組合風險權值已超標，可至「投組適配試算功能」重新試算";
			} else {
				errorMsg = "客戶投資組合風險權值已超標，可至「投組適配試算功能」重新試算";
			}
		}
		
		return errorMsg;
	}
	
	/***
	 * 取得購物車中各商品P值申購金額折台合計
	 * 有變更投資標的，以變更後計算；沒有變更投資標的，以變更前計算，再加總做為申購金額
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
		sb.append("SELECT PROD_RISK_LV, SUM(AMT_BUY) AS AMT_BUY FROM ( ");
		//有變更投資標的，以變更後計算
		sb.append(" SELECT A.F_PROD_RISK_LV AS PROD_RISK_LV, "); 
		sb.append(" SUM((CASE WHEN A.TRADE_SUB_TYPE IN ('3','5','9') THEN A.F_PURCHASE_AMT_M ELSE A.F_PURCHASE_AMT_L END) * NVL(B.BUY_RATE, 1)) AS AMT_BUY ");
		sb.append(" FROM TBSOT_NF_CHANGE_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.F_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq AND A.F_PROD_RISK_LV IS NOT NULL ");
		sb.append(" GROUP BY A.F_PROD_RISK_LV ");
		sb.append(" UNION ALL ");
		//沒有變更投資標的，以變更前計算
		sb.append(" SELECT A.B_PROD_RISK_LV AS PROD_RISK_LV, "); 
		sb.append(" SUM((CASE WHEN A.TRADE_SUB_TYPE IN ('3','5','9') THEN A.B_PURCHASE_AMT_M ELSE A.B_PURCHASE_AMT_L END) * NVL(B.BUY_RATE, 1)) AS AMT_BUY ");
		sb.append(" FROM TBSOT_NF_CHANGE_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.B_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq AND A.F_PROD_RISK_LV IS NULL ");
		sb.append(" GROUP BY A.B_PROD_RISK_LV ) ");
		sb.append(" GROUP BY PROD_RISK_LV ");
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
	
	/***
	 * 取得購物車中各商品P值贖回金額折台合計
	 * 有變更投資標的，以變更前金額計算，當作贖回金額
	 * @param tradeSeq
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, BigDecimal> getAmtSellByPVal(String tradeSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//有變更投資標的，以變更前金額計算
		sb.append(" SELECT A.B_PROD_RISK_LV AS PROD_RISK_LV, "); 
		sb.append(" SUM((CASE WHEN A.TRADE_SUB_TYPE IN ('3','5','9') THEN A.B_PURCHASE_AMT_M ELSE A.B_PURCHASE_AMT_L END) * NVL(B.BUY_RATE, 1)) AS AMT_BUY ");
		sb.append(" FROM TBSOT_NF_CHANGE_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.B_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq AND A.F_PROD_ID IS NOT NULL ");
		sb.append(" GROUP BY A.B_PROD_RISK_LV ");
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