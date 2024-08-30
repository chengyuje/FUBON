package com.systex.jbranch.app.server.fps.sot140;

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.DecimalFormat;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DVO;
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
 * Created by Lily on 2016/10/21. copy FROM SOT150 基金轉換明細檔
 */
@Component("sot140")
@Scope("request")
public class SOT140 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private static String TBSOT_DETAIL = "TBSOT_NF_TRANSFER_D"; //基金轉換明細檔  
	private SimpleDateFormat SDFYYYY_MM_DDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
	private static String MAIN_TRADE_STATUS_4_WEBBANK ="4";
	public void getSOTCustInfo (Object body, IPrimitiveMap header) throws JBranchException, Exception {
	
		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		SOT701OutputVO outputVO_701 = new SOT701OutputVO();

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		outputVO_701 = sot701.getSOTCustInfo(inputVO_701);
		
		outputVO.setCustName(outputVO_701.getFp032675DataVO().getCustName());				//客戶姓名
		outputVO.setKycLevel(outputVO_701.getCustKYCDataVO().getKycLevel());				//Kyc等級
		outputVO.setKycDueDate(outputVO_701.getCustKYCDataVO().getKycDueDate());			//kyc日期
		outputVO.setIsAgreeProdAdv(outputVO_701.getFp032675DataVO().getCustTxFlag()); //交易中不需要再去做邏輯判斷是Y或N，可以直接回存DB
		outputVO.setPiRemark(outputVO_701.getFp032675DataVO().getCustProRemark());//這個欄位會直接回存電文DESC欄位資料
		outputVO.setPiDueDate(outputVO_701.getFp032675DataVO().getCustProDate());           //專業投資人效期
		outputVO.setCustRemarks(outputVO_701.getFp032675DataVO().getCustRemarks());			//客戶註記
		outputVO.setIsOBU(outputVO_701.getFp032675DataVO().getObuFlag());
		outputVO.setCustProType(outputVO_701.getFp032675DataVO().getCustProType());

		outputVO.setIsFirstTrade((outputVO_701.getIsFirstTrade()) ? "Y" : "N");//查詢客戶是否為首購 
		
		//FOR CBS測試日期修改
				outputVO.setKycDueDateUseful(outputVO_701.getCustKYCDataVO().isKycDueDateUseful());

		//適配
		outputVO.setProfInvestorYN(outputVO_701.getFp032675DataVO().getCustProFlag());
		outputVO.setAgeUnder70Flag(outputVO_701.getFp032675DataVO().getAgeUnder70Flag());

		List<AcctVO> debitAcct = outputVO_701.getCustAcctDataVO().getDebitAcctList();// TDS_SOT_701只有回CreditAcctList
		List<Map<String, Object>> debitAcctList = new ArrayList<Map<String, Object>>();
		
		for (AcctVO vo : debitAcct) {
			// 判斷是否有重複
			boolean noRepeat = true;
			for(Map temp : debitAcctList){
				if (StringUtils.equals(vo.getAcctNo(), temp.get("LABEL").toString())) {
					noRepeat = false;
				}
			}
			if (noRepeat){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
			map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
			debitAcctList.add(map);
			}
		}
		outputVO.setDebitAcct(debitAcctList);
		
		//查詢客戶高資產註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hNWCDataVO = sot714.getHNWCData(inputVO.getCustID());
		outputVO.setHnwcYN(StringUtils.isNotBlank(hNWCDataVO.getValidHnwcYN()) ? hNWCDataVO.getValidHnwcYN() : "N");
		outputVO.setHnwcServiceYN(hNWCDataVO.getHnwcService());
		
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

		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		SOT701InputVO inputVO_701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		Boolean isFirstTrade = false;

		inputVO_701.setCustID(inputVO.getCustID());
		inputVO_701.setProdType(inputVO.getProdType());
		inputVO_701.setTradeType(inputVO.getTradeType());

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		fp032675DataVO = sot701.getFP032675Data(inputVO_701);
		isFirstTrade = sot701.getIsCustFirstTrade(inputVO_701);

		outputVO.setIsFirstTrade((isFirstTrade) ? "Y" : "N");
		outputVO.setCustRemarks(fp032675DataVO.getCustRemarks()); 		 //弱勢
		outputVO.setAgeUnder70Flag(fp032675DataVO.getAgeUnder70Flag());
		outputVO.setEduJrFlag(fp032675DataVO.getEduJrFlag());
		outputVO.setHealthFlag(fp032675DataVO.getHealthFlag());

		this.sendRtnObject(outputVO);
	}

	/**
	 * 用客戶id適配檢核 再 查詢 轉入商品 ({'custID': ...,'inProdID1':..)
	 * 
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {

		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();

		String queryProdID = inputVO.getQueryProdID(); //查庫存風險值  或 轉入商品1~3

		boolean isFitness = StringUtils.equals("N", (String) new XmlInfo().getVariable("SOT.FITNESS_YN", "NF", "F3")) ? false : true;

		outputVO.setWarningMsg("");
		outputVO.setErrorMsg("");

		boolean isFitnessOK = false;
		//由庫存取的商品資料，不需做適配
		if (isFitness && !StringUtils.equals("Y", inputVO.getQueryOutProd())) {
			// 1.適配，由商品查詢取得，邏輯需一致
			PRD110OutputVO prdOutputVO = new PRD110OutputVO();
			PRD110InputVO prdInputVO = new PRD110InputVO();
			prdInputVO.setCust_id(inputVO.getCustID().toUpperCase());
			prdInputVO.setType("4");
			prdInputVO.setFund_id(queryProdID);
			prdInputVO.setSameSerialYN("Y"); //同系列商品
			prdInputVO.setSameSerialProdId(inputVO.getOutProdID());

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

		// 2.查詢基金主檔
		if (isFitnessOK) { // 
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			sb.append("SELECT FUND.PRD_ID, FUND.FUND_CNAME, FUND.CURRENCY_STD_ID, FUND.RISKCATE_ID ,FUND.CURRENCY_STD_ID as CURRENCY, PRICE.PRICE, i.FUS20, i.FUS40, FUND.OVS_PRIVATE_YN ");
			sb.append(" FROM TBPRD_FUND FUND LEFT JOIN TBPRD_FUNDPRICE PRICE ON FUND.PRD_ID=PRICE.PRD_ID");
			sb.append(" LEFT JOIN TBPRD_FUNDINFO i ON (FUND.PRD_ID=i.PRD_ID) ");
			sb.append(" WHERE FUND.PRD_ID = :prodID ");
			queryCondition.setObject("prodID", queryProdID);
			queryCondition.setQueryString(sb.toString());
			outputVO.setProdDTL(dam.exeQuery(queryCondition));

		}

		this.sendRtnObject(outputVO);
	}

	/**
	 * 檢查適配 (給選交易日期)
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	//	public void checkFitness(Object body, IPrimitiveMap header)
	//			throws JBranchException {
	//		/*
	//		From:伯瑞  Date: 2016-11-02 14:22   
	//		FitnessFund008	預約交易檢核
	//		這EFFECT_DATE 是  即時(今日)  或是 預約(預約日) => 預約日
	//		這樣 我切換  即時或是 預約 都要跑一次適配 =>是
	//		 */
	//	
	//		SOT140InputVO inputVO = (SOT140InputVO) body;
	//		SOT140OutputVO outputVO = new SOT140OutputVO();
	//		dam = this.getDataAccessManager();
	//		QueryConditionIF queryCondition = dam
	//				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	//
	//		boolean SOT702Status = false;
	//		if (isRunFitness()) {
	//			logger.debug("SOT.FITNESS_YN 進行適配 ");
	//			// 1.適配
	//			Map<String, Object> SOT701 = new HashMap<String, Object>();
	//			SOT701.put("CUST_ID", inputVO.getCustID().toUpperCase());
	//			SOT701.put("obuFlag", inputVO.getIsOBU());
	//		 
	//			SOT701.put("noSale", inputVO.getNoSale());
	//			SOT701.put("custProFlag", inputVO.getProfInvestorYN());
	//			SOT701.put("fatcaType", inputVO.getFatcaType());
	//			SOT701.put("ageUnder70Flag", inputVO.getAgeUnder70Flag()); // 這會跟預約交易日期有關吧
	//			SOT701.put("kycLV", inputVO.getKycLV());
	//
	//			SOT702 sot702 = new SOT702();
	//			Date tradeDate = null; 
	//			if ("2".equals(inputVO.getTradeDateType())) {//2預約
	//				try {
	//					tradeDate= SDFYYYY_MM_DDHHMMSS.parse(inputVO.getTradeDate());
	//				} catch (ParseException e) {
	//					throw new JBranchException(e);
	//				}	
	//			}else{//即時
	//				tradeDate = new Date();
	//			}
	//			
	//			List<String> inProdIdList = new ArrayList<String>();
	//			
	//			//全部檢查適配cart
	//			if (inputVO.getQueryProdIDindex() == null) {
	//				if (StringUtils.isNotBlank(inputVO.getInProdID1())) {
	//					inProdIdList.add(inputVO.getInProdID1());
	//				}
	//				if (StringUtils.isNotBlank(inputVO.getInProdID2())) {
	//					inProdIdList.add(inputVO.getInProdID2());
	//				}
	//				if (StringUtils.isNotBlank(inputVO.getInProdID3())) {
	//					inProdIdList.add(inputVO.getInProdID3());
	//				}
	//			} else {
	//				//單獨檢查適配
	//				if (inputVO.getQueryProdIDindex().intValue() ==1 && StringUtils.isNotBlank(inputVO.getInProdID1())) {
	//					inProdIdList.add(inputVO.getInProdID1());
	//				}else if (inputVO.getQueryProdIDindex().intValue() ==2 && StringUtils.isNotBlank(inputVO.getInProdID2())) {
	//					inProdIdList.add(inputVO.getInProdID2());
	//				}else if (inputVO.getQueryProdIDindex().intValue() ==3 && StringUtils.isNotBlank(inputVO.getInProdID3())) {
	//					inProdIdList.add(inputVO.getInProdID3());
	//				}   
	//			}
	//			if ("2".equals(inputVO.getTradeDateType())) {//2預約
	//				try {
	//					tradeDate= SDFYYYY_MM_DDHHMMSS.parse(inputVO.getTradeDate());
	//				} catch (ParseException e) {
	//					throw new JBranchException(e);
	//				}	
	//			} 
	//			for (String prodID : inProdIdList) {
	//				Map<String, Object> sot702Map = sot702.checkFitness(dam, inputVO.getTradeType(), tradeDate, prodID, SOT701);
	//				String fitnessMessage = sot702Map.get("Message").toString();// 以逗號分隔適配錯誤碼
	//				if (!StringUtils.equals("error", (String) sot702Map.get("MessageType"))) {
	//					SOT702Status = true;
	//				} else {
	//					logger.error(String.format("錯誤  客戶%s 下單產品%s 的適配檢核結果:%s", inputVO.getCustID(), prodID,
	//							fitnessMessage));
	//					SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
	//					String params = (sot702Map.get("Params")!=null)?sot702Map.get("Params").toString():null; 
	//					throw new JBranchException(sot712.inquireErrorCode(sot702Map.get("FundErrId").toString(), params));
	//				}
	//	
	//				logger.debug(String.format("客戶%s 下單產品%s 適配檢核結果:%s",
	//						inputVO.getCustID(), prodID, fitnessMessage));
	//				if (StringUtils.contains(fitnessMessage, "ehl_01_SOT702_016")) {
	//					outputVO.setFundInfoSelling("Y");
	//				}
	//				outputVO.setFitnessMessage(fitnessMessage);// 適配結果代碼以逗號分隔
	//			}
	//			
	//		} else {
	//			logger.debug("SOT.FITNESS_YN 不進行適配 ");
	//			SOT702Status = true;
	//		}
	//		this.sendRtnObject(outputVO);
	//	}

	/**
	 * 加入購物車
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

		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//高資產客戶投組風險權值檢核
		//客戶風險屬性非C4
		if(StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN()) && !StringUtils.equals("C4", inputVO.getKycLV())) {
			//轉入有選擇越級商品
			Integer prodRiskLV1 = StringUtils.isNotEmpty(inputVO.getInProdRiskLV1()) ? Integer.parseInt(inputVO.getInProdRiskLV1().substring(1)) : 1;
			Integer prodRiskLV2 = StringUtils.isNotEmpty(inputVO.getInProdRiskLV2()) ? Integer.parseInt(inputVO.getInProdRiskLV2().substring(1)) : 1;
			Integer prodRiskLV3 = StringUtils.isNotEmpty(inputVO.getInProdRiskLV3()) ? Integer.parseInt(inputVO.getInProdRiskLV3().substring(1)) : 1;
			Integer custRiskLV = Integer.parseInt(inputVO.getKycLV().substring(1));
			
			if(prodRiskLV1 > custRiskLV || prodRiskLV2 > custRiskLV || prodRiskLV3 > custRiskLV) {
				String errorMsg = getHnwcRiskValue(inputVO);
				if(StringUtils.isNotBlank(errorMsg)) {
					throw new APException(errorMsg);
				}	
			}
		}
		
		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE("1"); //1:基金
			mainVO.setTRADE_TYPE("3"); //3：轉換
			mainVO.setCUST_ID(inputVO.getCustID());
			mainVO.setCUST_NAME(inputVO.getCustName());
			mainVO.setKYC_LV(inputVO.getKycLV());
			mainVO.setKYC_DUE_DATE(null != inputVO.getKycDueDate() ? new Timestamp(inputVO.getKycDueDate().getTime()) : null);

			mainVO.setPI_DUE_DATE(null != inputVO.getPiDueDate() ? new Timestamp(inputVO.getPiDueDate().getTime()) : null);//專業投資人效期

			mainVO.setCUST_REMARKS(inputVO.getCustRemarks());
			mainVO.setIS_OBU(inputVO.getIsOBU());
			mainVO.setIS_AGREE_PROD_ADV(inputVO.getIsAgreeProdAdv());
			mainVO.setPI_REMARK(inputVO.getPiRemark()); //專業投資人註記
			mainVO.setBARGAIN_DUE_DATE(null != inputVO.getBargainDueDate() ? new Timestamp(inputVO.getBargainDueDate().getTime()) : null);
			mainVO.setTRADE_STATUS(MAIN_TRADE_STATUS_1_TEMP); //1:暫存
			mainVO.setIS_BARGAIN_NEEDED("N");

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
			inputVO_712.setProfInvestorYN(StringUtils.isBlank(inputVO.getProfInvestorYN()) ? "N" : inputVO.getProfInvestorYN()); //是否為專業投資人
			inputVO_712.setIsFirstTrade(inputVO.getIsFirstTrade()); //是否首購   
			inputVO_712.setCustRemarks(inputVO.getCustRemarks());
			inputVO_712.setCustProRemark(inputVO.getPiRemark());
			mainVO.setIS_REC_NEEDED(sot712.getIsRecNeeded(inputVO_712) ? "Y" : "N"); //IS_REC_NEEDED是否需要錄音 (非常規交易)

			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);
			mainVO.setPROF_INVESTOR_YN(StringUtils.isBlank(inputVO.getProfInvestorYN()) ? "N" : inputVO.getProfInvestorYN()); //PROF_INVESTOR_YN是否為專業投資人
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
			//如果主檔原本就有資料直接改狀態
			vo.setTRADE_STATUS(MAIN_TRADE_STATUS_1_TEMP); //1:暫存

			dam.update(vo);
		}

		TBSOT_NF_TRANSFER_DPK dPK = new TBSOT_NF_TRANSFER_DPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(inputVO.getCarSEQ());
		TBSOT_NF_TRANSFER_DVO carVO = new TBSOT_NF_TRANSFER_DVO();
		carVO.setcomp_id(dPK);
		carVO = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, carVO.getcomp_id());
		TBSOT_NF_TRANSFER_DVO dtlVO = null;
		BigDecimal newTrade_SEQNO = null;
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		TBSOT_NF_TRANSFER_DPK dtlPK = null;
		if (carVO != null) {
			dtlVO = carVO;
			newTrade_SEQNO = inputVO.getCarSEQ();
			dtlPK = dPK;
		} else {
			newTrade_SEQNO = new BigDecimal(sot712.getnewTrade_SEQNO());
			dtlPK = new TBSOT_NF_TRANSFER_DPK();
			dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			dtlPK.setSEQ_NO(newTrade_SEQNO);
			//BigDecimal
			dtlVO = new TBSOT_NF_TRANSFER_DVO();
			dtlVO.setcomp_id(dtlPK);
		}
		dtlVO.setBATCH_SEQ(null);
		dtlVO.setBATCH_NO(null);
		dtlVO.setOUT_PROD_ID(inputVO.getOutProdID()); //轉出基金代號
		dtlVO.setOUT_PROD_NAME(inputVO.getOutProdName()); //轉出基金名稱
		dtlVO.setOUT_PROD_CURR(inputVO.getOutProdCurr()); //轉出基金計價幣別
		dtlVO.setOUT_PROD_RISK_LV(inputVO.getOutProdRiskLV()); //轉出產品風險等級
		dtlVO.setOUT_TRUST_AMT(inputVO.getOutTrustAmt()); //轉出標的信託金額

		dtlVO.setOUT_TRADE_TYPE(StringUtils.right(inputVO.getOutTradeType(), 1)); // 轉出標的信託型態，參考 SOT.NEW_ASSET_TRADE_SUB_TYPE
		dtlVO.setOUT_TRADE_TYPE_D(inputVO.getOutTradeTypeD()); // 轉出標的詳細信託型態，參考 SOT.ASSET_TRADE_SUB_TYPE
		dtlVO.setOUT_CERTIFICATE_ID(inputVO.getOutCertificateID()); //轉出標的憑證編號
		dtlVO.setOUT_TRUST_CURR_TYPE(inputVO.getOutTrustCurrType()); //轉出標的信託幣別類別
		dtlVO.setOUT_TRUST_CURR(inputVO.getOutTrustCurr()); //轉出標的信託幣別
		dtlVO.setOUT_UNIT(inputVO.getOutUnit()); //轉出標的單位數
		dtlVO.setOUT_PRESENT_VAL(inputVO.getOutPresentVal()); //轉出標的參考現值
		dtlVO.setOUT_TRUST_ACCT(inputVO.getOutTrustAcct()); //轉出標的信託帳號
		dtlVO.setOUT_NOT_VERTIFY(inputVO.getOutNotVertify()); //轉出標的未核備(TBPRD_FUNDINFO.FUS40)

		dtlVO.setTRANSFER_TYPE(inputVO.getTransferType()); //轉出方式
		dtlVO.setIN_PROD_ID_1(inputVO.getInProdID1()); //轉入基金代碼 1
		dtlVO.setIN_PROD_NAME_1(inputVO.getInProdName1()); //轉入基金名稱 1
		dtlVO.setIN_PROD_CURR_1(inputVO.getInProdCurr1()); //轉入基金計價幣別 1
		dtlVO.setIN_PROD_RISK_LV_1(inputVO.getInProdRiskLV1()); //轉入基金產品風險等級 1
		dtlVO.setIN_UNIT_1(inputVO.getInUnit1()); //轉入基金單位數 1
		dtlVO.setIN_PRESENT_VAL_1(inputVO.getInPresentVal1()); //轉入基金參考現值 1
		dtlVO.setIN_PROD_ID_2(inputVO.getInProdID2()); //轉入基金代碼 2
		dtlVO.setIN_PROD_NAME_2(inputVO.getInProdName2()); //轉入基金名稱 2
		dtlVO.setIN_PROD_CURR_2(inputVO.getInProdCurr2()); //轉入基金計價幣別 2 
		dtlVO.setIN_PROD_RISK_LV_2(inputVO.getInProdRiskLV2()); //轉入基金產品風險等級 2
		dtlVO.setIN_UNIT_2(inputVO.getInUnit2()); //轉入基金單位數 2
		dtlVO.setIN_PRESENT_VAL_2(inputVO.getInPresentVal2()); //轉入基金參考現值 2
		dtlVO.setIN_PROD_ID_3(inputVO.getInProdID3()); //轉入基金代碼 3
		dtlVO.setIN_PROD_NAME_3(inputVO.getInProdName3()); //轉入基金名稱 3					
		dtlVO.setIN_PROD_CURR_3(inputVO.getInProdCurr3()); //轉入基金計價幣別 3
		dtlVO.setIN_PROD_RISK_LV_3(inputVO.getInProdRiskLV3()); //轉入基金產品風險等級 3
		dtlVO.setIN_UNIT_3(inputVO.getInUnit3()); //轉入基金單位數 3
		dtlVO.setIN_PRESENT_VAL_3(inputVO.getInPresentVal3()); //轉入基金參考現值 3
		dtlVO.setFEE_DEBIT_ACCT(inputVO.getFeeDebitAcct()); //手續費扣款帳號
		dtlVO.setTRADE_DATE_TYPE(inputVO.getTradeDateType()); //交易日期類別
		dtlVO.setPROSPECTUS_TYPE(inputVO.getProspectusType()); //公開說明書

		Date tradeDate = null;
		if ("2".equals(inputVO.getTradeDateType())) {//2預約
			try {
				tradeDate = SDFYYYY_MM_DDHHMMSS.parse(inputVO.getTradeDate());
			} catch (ParseException e) {
				throw new JBranchException(e);
			}
		} else {//即時
			tradeDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsservice.getCBSTestDate());
		}
		dtlVO.setTRADE_DATE(new Timestamp(tradeDate.getTime())); //交易日期  Date

		dtlVO.setFEE(null); //手續費金額
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名
		if (carVO != null) {
			dam.update(dtlVO);
		} else {
			dam.create(dtlVO);
		}
		this.logger.debug("save() tradeSEQ:" + dtlPK.getTRADE_SEQ() + " ,seqNo:" + dtlPK.getSEQ_NO());

		// 2.檢核電文
		SOT703InputVO inputVO_703 = new SOT703InputVO();
		SOT703OutputVO outputVO_703 = new SOT703OutputVO();
		inputVO_703.setCustId(inputVO.getCustID());
		inputVO_703.setTradeSeq(inputVO.getTradeSEQ());
		inputVO_703.setSeqNo(newTrade_SEQNO.toString());

		SOT703 sot703 = (SOT703) PlatformContext.getBean("sot703");
		try {
			//			sot703.verifyESBTransferNF(inputVO_703); //基金轉換檢核
			outputVO_703 = sot703.verifyESBTransferNF(inputVO_703); //基金轉換檢核
		} catch (Exception e) {
			String errorMsg = e.getMessage();
			sot712 = (SOT712) PlatformContext.getBean("sot712");
			errorMsg = sot712.inquireErrorCode(e.getMessage());
			logger.error(String.format("基金轉換檢核電文sot703.verifyESBTransferNF錯誤:%s", errorMsg), e);
			throw new JBranchException(String.format("基金轉換檢核電文錯誤:%s", errorMsg), e);
		}

		String errorMsg = outputVO_703.getErrorMsg();

		if (StringUtils.isNotBlank(errorMsg)) {
			logger.error(String.format("sot703.verifyESBTransferNF()  tradeSeq:%s , seqNo:%s  outputVO_703.ErrorMsg:%s", inputVO_703.getTradeSeq(), inputVO_703.getSeqNo(), outputVO_703.getErrorMsg()));

			/*改用dtlVO.getcomp_id()
			TBSOT_NF_TRANSFER_DPK errPK = new TBSOT_NF_TRANSFER_DPK();
			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			errPK.setSEQ_NO(inputVO.getCarSEQ());
			*/
			TBSOT_NF_TRANSFER_DPK errPK = dtlVO.getcomp_id();
			TBSOT_NF_TRANSFER_DVO errVO = new TBSOT_NF_TRANSFER_DVO();
			errVO = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, errPK);
			
			if (errVO != null)
				dam.delete(errVO);
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT TRADE_SEQ, SEQ_NO ");
			sb.append("FROM TBSOT_NF_TRANSFER_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", dtlVO.getcomp_id().getTRADE_SEQ());
			queryCondition.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, errPK.getTRADE_SEQ());
				if (mVO != null)
					dam.delete(mVO);
			}
			/*
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM TBSOT_NF_CHANGE_BATCH WHERE TRADE_SEQ = :tradeSEQ AND SEQ_NO = :seqNO ");
			queryCondition.setQueryString(sb.toString());
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setObject("seqNO", inputVO.getCarSEQ());
			dam.exeUpdate(queryCondition);
			
			//===若無明細，則將主檔也刪除
			sb = new StringBuffer();
			sb.append("SELECT TRADE_SEQ, SEQ_NO, BATCH_SEQ, BATCH_NO, TRADE_SUB_TYPE, CERTIFICATE_ID, B_PROD_ID, B_PROD_NAME, B_PROD_CURR, B_PROD_RISK_LV, B_TRUST_CURR, B_TRUST_CURR_TYPE, B_TRUST_AMT, B_PURCHASE_AMT_L, B_PURCHASE_AMT_M, B_PURCHASE_AMT_H, B_CHARGE_DATE_1, B_CHARGE_DATE_2, B_CHARGE_DATE_3, B_CHARGE_DATE_4, B_CHARGE_DATE_5, B_CHARGE_DATE_6, B_DEBIT_ACCT, B_CREDIT_ACCT, B_TRUST_ACCT, B_CERTIFICATE_STATUS, F_PROD_ID, F_PROD_NAME, F_PROD_CURR, F_PROD_RISK_LV, F_TRUST_CURR, F_PURCHASE_AMT_L, F_PURCHASE_AMT_M, F_PURCHASE_AMT_H, F_CHARGE_DATE_1, F_CHARGE_DATE_2, F_CHARGE_DATE_3, F_CHARGE_DATE_4, F_CHARGE_DATE_5, F_CHARGE_DATE_6, F_DEBIT_ACCT, F_CREDIT_ACCT, F_CERTIFICATE_STATUS, F_HOLD_START_DATE, F_HOLD_END_DATE, F_RESUME_DATE, F_FEE_L, F_FEE_M, F_FEE_H, NARRATOR_ID, NARRATOR_NAME, CHANGE_TYPE ");
			sb.append("FROM TBSOT_NF_CHANGE_D ");
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());
			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
				
				dam.delete(vo);
			}
			*/
			//===

			outputVO.setErrorMsg(errorMsg);
		}
		//取得判斷是否為短期交易或何種類型的值
		outputVO.setSHORT_1(outputVO_703.getSHORT_1());
		outputVO.setSHORT_2(outputVO_703.getSHORT_2());

		this.sendRtnObject(outputVO);
	}

	/**
	 * 產生 交易序號TRADE_SEQ；流水號 SEQ_NO
	 * 
	 * @param TXN_ID
	 * @param format
	 * @param timeStamp
	 * @param minNum
	 * @param maxNum
	 * @param status
	 * @param nowNum
	 * @return
	 * @throws JBranchException
	 */
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
	 * 風控 傳送OP交易並列印表單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void goOP(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		dam = this.getDataAccessManager();

		TBSOT_TRADE_MAINVO mainVo = new TBSOT_TRADE_MAINVO();
		mainVo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		//		mainVo.setPROSPECTUS_TYPE(this.getProspectusType(inputVO)); // MIMI 2/18 (公開說明書) 欄位都先寫死放1
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
 			outputVO_703 = sot703.confirmESBTransferNF(inputVO_703); //基金轉換確認
 		} catch (Exception e) {
			String errorMsg = e.getMessage();
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			errorMsg = sot712.inquireErrorCode(e.getMessage());
			logger.error(String.format("基金轉換確認電文sot703.confirmESBTransferNF錯誤:%s", errorMsg), e);
			throw new JBranchException(String.format("基金轉換確認電文錯誤:%s", errorMsg), e);
		}

		String errorMsg = outputVO_703.getErrorMsg();
		if (StringUtils.isNotBlank(errorMsg)) {
			logger.error(String.format("sot703.confirmESBTransferNF()  tradeSeq:%s  outputVO_703.ErrorMsg:%s", inputVO.getTradeSEQ(), outputVO_703.getErrorMsg()));

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
	 * 查詢購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append(String.format("FROM %s ", TBSOT_DETAIL));
		sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
		queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
		queryCondition.setQueryString(sb.toString());

		outputVO.setCarList(dam.exeQuery(queryCondition));

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

	/**
	 * 下單 [下一步]
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void next(Object body, IPrimitiveMap header) throws JBranchException {

		WorkStation ws = DataManager.getWorkStation(uuid);
		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
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

		this.sendRtnObject(null);
	}

	/**
	 * 刪除購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void delProd(Object body, IPrimitiveMap header) throws JBranchException {

		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBSOT_NF_TRANSFER_DPK dPK = new TBSOT_NF_TRANSFER_DPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(inputVO.getCarSEQ());
		TBSOT_NF_TRANSFER_DVO dVO = new TBSOT_NF_TRANSFER_DVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, dVO.getcomp_id());
		if (dVO != null) {
			dam.delete(dVO);

			//如果已無明細資料,同步移除主檔
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT TRADE_SEQ ");
			sb.append(String.format(" FROM %s ", TBSOT_DETAIL));
			sb.append("WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.isEmpty()) {
				TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
				vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
				if (vo != null)
					dam.delete(vo);
			}
		}

		this.sendRtnObject(null);
	}

	public void isBackend(Object body, IPrimitiveMap header) throws JBranchException {
		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT IS_BACKEND, OVS_PRIVATE_YN FROM TBPRD_FUND WHERE PRD_ID = :prd_id ");
		queryCondition.setObject("prd_id", inputVO.getOutProdID());
		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setProdDTL(list);
		this.sendRtnObject(outputVO);
	}

	/**
	 * 匯率 :部分轉換所顯示之金額為「現值」，單位數與金額擇一鍵入，可自動帶出另一個數字，使用之匯率等同對帳單使用之匯率。
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws Exception
	 */
	@Deprecated
	public void queryUnitAndPresentVal(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		//SOT140InputVO inputVO = (SOT140InputVO) body;
		//SOT140OutputVO outputVO = new SOT140OutputVO(); 

		// 直接JS換算即可
		//this.sendRtnObject(outputVO);
	}

	/**
	 *  MIMI 2/18 (公開說明書) 欄位都先寫死放1
	 * 
	 * @param inputVO
	 * @return
	 */
	//	private String getProspectusType(SOT140InputVO inputVO) {
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
	 * 比較後收型基金 轉入以及轉出標的的CDSC年期是否相符
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void compareCDSCMonth(Object body, IPrimitiveMap header)  throws Exception {
		SOT140InputVO inputVO = (SOT140InputVO) body;
		SOT140OutputVO outputVO = new SOT140OutputVO();
		outputVO.setSameCDSCMonth(this.getCDSCMonth(inputVO));
		this.sendRtnObject(outputVO);
	}
	public Boolean getCDSCMonth(Object body) throws Exception {
		SOT140InputVO inputVO = (SOT140InputVO) body;
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT CDSC_MONTH FROM TBPRD_FUNDINFO WHERE PRD_ID in(:inprd_id, :outprd_id) ");
		queryCondition.setObject("inprd_id", inputVO.getInProdID1());
		queryCondition.setObject("outprd_id", inputVO.getOutProdID());
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() == 1){
			return true;
		} else {
			return false;
		}
	}
	
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getHnwcRiskValue(SOT140InputVO inputVO) throws Exception {
		//取得購物車中各商品P值轉入金額合計
		Map<String, BigDecimal> cartBuy = getAmtBuyByPVal(inputVO.getTradeSEQ()); 
		//取得購物車中各商品P值轉出金額合計
		Map<String, BigDecimal> cartSell = getAmtSellByPVal(inputVO.getTradeSEQ()); 
		//取得這次轉入商品的信託幣別買匯
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		BigDecimal rate = sot110.getBuyRate(inputVO.getOutTrustCurr());
		//取得高資產客戶註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCustID());
		//準備電文資料
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		inputVO714.setCUST_KYC(inputVO.getKycLV()); //客戶C值
		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(getMaxPVal(inputVO)); //商品風險檢核值(取轉入商品最大風險值)
		BigDecimal amtBuy1 = BigDecimal.ZERO;//這次轉入金額1折台
		BigDecimal amtBuy2 = BigDecimal.ZERO;//這次轉入金額2折台
		BigDecimal amtBuy3 = BigDecimal.ZERO;//這次轉入金額3折台
		if(StringUtils.equals("1", inputVO.getTransferType())) {
			//全部轉出
			amtBuy1 = inputVO.getOutTrustAmt().multiply(rate); //信託金額*匯率
		} else {
			//部分轉出1
			if(StringUtils.isNotBlank(inputVO.getInProdID1()) &&  inputVO.getInUnit1() != null) {
				BigDecimal unitAmt = inputVO.getInUnit1().divide(inputVO.getOutUnit(), 9, RoundingMode.HALF_UP);
				amtBuy1 = inputVO.getOutTrustAmt().multiply(rate).multiply(unitAmt); //信託金額*匯率*(單位數/總單位數)
			}
			//部分轉出2
			if(StringUtils.isNotBlank(inputVO.getInProdID2()) &&  inputVO.getInUnit2() != null) {
				BigDecimal unitAmt = inputVO.getInUnit2().divide(inputVO.getOutUnit(), 9, RoundingMode.HALF_UP);
				amtBuy2 = inputVO.getOutTrustAmt().multiply(rate).multiply(unitAmt); //信託金額*匯率*(單位數/總單位數)
			}
			//部分轉出3
			if(StringUtils.isNotBlank(inputVO.getInProdID3()) &&  inputVO.getInUnit3() != null) {
				BigDecimal unitAmt = inputVO.getInUnit3().divide(inputVO.getOutUnit(), 9, RoundingMode.HALF_UP);
				amtBuy3 = inputVO.getOutTrustAmt().multiply(rate).multiply(unitAmt); //信託金額*匯率*(單位數/總單位數)
			}
		}
		
		for(int i = 1; i <= 4; i++) {
			String pVal = "P" + String.valueOf(i);
			//以這次申購商品風險檢核值判斷是否需要加上這次轉入金額
			BigDecimal amtBuy1Pval = StringUtils.equals(pVal, inputVO.getInProdRiskLV1()) ? amtBuy1 : BigDecimal.ZERO;
			BigDecimal amtBuy2Pval = StringUtils.equals(pVal, inputVO.getInProdRiskLV2()) ? amtBuy2 : BigDecimal.ZERO;
			BigDecimal amtBuy3Pval = StringUtils.equals(pVal, inputVO.getInProdRiskLV3()) ? amtBuy3 : BigDecimal.ZERO;
			//這個風險值的轉入金額合計
			BigDecimal amtBuyPval = amtBuy1Pval.add(amtBuy2Pval).add(amtBuy3Pval); 
			//以這次轉出商品風險檢核值判斷是否需要加上這次轉出金額
			BigDecimal amtSellPval = StringUtils.equals(pVal, inputVO.getOutProdRiskLV()) ? amtBuy1.add(amtBuy2).add(amtBuy3) : BigDecimal.ZERO;
			//這次轉入金額加上購物車中各商品P值轉入金額(申購)
			//這次轉出金額加上購物車中各商品P值轉出金額(贖回)
			switch(pVal) {
				case "P1":
					inputVO714.setAMT_BUY_1(amtBuyPval.add(cartBuy.get("P1") == null ? BigDecimal.ZERO : cartBuy.get("P1"))); 
					inputVO714.setAMT_SELL_1(amtSellPval.add(cartSell.get("P1") == null ? BigDecimal.ZERO : cartSell.get("P1")));
					break;
				case "P2":
					inputVO714.setAMT_BUY_2(amtBuyPval.add(cartBuy.get("P2") == null ? BigDecimal.ZERO : cartBuy.get("P2")));
					inputVO714.setAMT_SELL_2(amtSellPval.add(cartSell.get("P2") == null ? BigDecimal.ZERO : cartSell.get("P2")));
					break;
				case "P3":
					inputVO714.setAMT_BUY_3(amtBuyPval.add(cartBuy.get("P3") == null ? BigDecimal.ZERO : cartBuy.get("P3")));
					inputVO714.setAMT_SELL_3(amtSellPval.add(cartSell.get("P3") == null ? BigDecimal.ZERO : cartSell.get("P3")));
					break;
				case "P4":
					inputVO714.setAMT_BUY_4(amtBuyPval.add(cartBuy.get("P4") == null ? BigDecimal.ZERO : cartBuy.get("P4")));
					inputVO714.setAMT_SELL_4(amtSellPval.add(cartSell.get("P4") == null ? BigDecimal.ZERO : cartSell.get("P4")));
					break;
			}
		}
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		//若檢核不通過，產生錯誤訊息
		String errorMsg = "";
		if(!StringUtils.equals("Y", riskValData.getVALIDATE_YN())) {
			if(StringUtils.equals(inputVO.getInProdRiskLV1(), inputVO.getInProdRiskLV2()) &&
					StringUtils.equals(inputVO.getInProdRiskLV1(), inputVO.getInProdRiskLV3()) && 	//轉出風險屬性都相同
					((cartBuy.isEmpty() && cartSell.isEmpty()) || 									//購物車中沒有資料
					 (cartSell.size() == 1 && cartSell.containsKey(inputVO.getInProdRiskLV1())) ||	//購物車中轉出都相同
					 (cartBuy.size() == 1 && cartBuy.containsKey(inputVO.getInProdRiskLV1())))) {	//購物車中轉入都相同
				//若購物車中沒有資料，或轉入轉出所有資料的風險值都相同
				BigDecimal leftAmt = BigDecimal.ZERO;
  				switch(inputVO.getInProdRiskLV1()) {
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
  				errorMsg = "高資產客戶越級金額需低於" + inputVO.getOutTrustCurr() + df.format(leftAmtCurr);
			} else {
				errorMsg = "客戶投資組合風險權值已超標，可至「投組適配試算功能」重新試算";
			}
		}
		
		return errorMsg;
	}
	
	/***
	 * 取得購物車中各商品P值轉入(申購)金額折台合計
	 * @param tradeSeq
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, BigDecimal> getAmtBuyByPVal(String tradeSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//取得購物車中各商品P值轉入(申購)金額合計
		sb.append("SELECT PROD_RISK_LV, SUM(AMT_BUY) AS AMT_BUY ");
		sb.append(" FROM ( ");
		sb.append(" SELECT A.IN_PROD_RISK_LV_1 AS PROD_RISK_LV, SUM(NVL(A.OUT_TRUST_AMT, 0) * NVL(B.BUY_RATE, 1) * (NVL(A.IN_UNIT_1, 0) / A.OUT_UNIT)) AS AMT_BUY ");
		sb.append(" FROM TBSOT_NF_TRANSFER_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.OUT_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq AND A.IN_PROD_RISK_LV_1 IS NOT NULL ");
		sb.append(" GROUP BY A.IN_PROD_RISK_LV_1 ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT A.IN_PROD_RISK_LV_2 AS PROD_RISK_LV, SUM(NVL(A.OUT_TRUST_AMT, 0) * NVL(B.BUY_RATE, 1) * (NVL(A.IN_UNIT_2, 0) / A.OUT_UNIT)) AS AMT_BUY ");
		sb.append(" FROM TBSOT_NF_TRANSFER_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.OUT_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq AND A.IN_PROD_RISK_LV_2 IS NOT NULL ");
		sb.append(" GROUP BY A.IN_PROD_RISK_LV_2 ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT A.IN_PROD_RISK_LV_3 AS PROD_RISK_LV, SUM(NVL(A.OUT_TRUST_AMT, 0) * NVL(B.BUY_RATE, 1) * (NVL(A.IN_UNIT_3, 0) / A.OUT_UNIT)) AS AMT_BUY ");
		sb.append(" FROM TBSOT_NF_TRANSFER_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.OUT_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq AND A.IN_PROD_RISK_LV_3 IS NOT NULL ");
		sb.append(" GROUP BY A.IN_PROD_RISK_LV_3 ");
		sb.append(" ) GROUP BY PROD_RISK_LV ");
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
	 * 取得購物車中各商品P值轉出(贖回)金額折台合計
	 * @param tradeSeq
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, BigDecimal> getAmtSellByPVal(String tradeSeq) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		//取得購物車中各商品P值轉出(贖回)金額合計
		//全部轉換
		sb.append("SELECT PROD_RISK_LV, SUM(AMT_SELL) AS AMT_SELL FROM (");
		sb.append("SELECT A.OUT_PROD_RISK_LV AS PROD_RISK_LV, ");
		sb.append(" SUM(A.OUT_TRUST_AMT * NVL(B.BUY_RATE, 1)) AS AMT_SELL ");
		sb.append(" FROM TBSOT_NF_TRANSFER_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.OUT_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq AND A.TRANSFER_TYPE = '1' ");
		sb.append(" GROUP BY A.OUT_PROD_RISK_LV ");
		sb.append(" UNION ALL ");
		//部分轉換
		sb.append("SELECT A.OUT_PROD_RISK_LV AS PROD_RISK_LV, ");
		sb.append(" SUM(A.OUT_TRUST_AMT * NVL(B.BUY_RATE, 1) * ((NVL(A.IN_UNIT_1, 0) + NVL(A.IN_UNIT_2, 0) + NVL(A.IN_UNIT_3, 0)) / A.OUT_UNIT)) AS AMT_SELL ");
		sb.append(" FROM TBSOT_NF_TRANSFER_D A ");
		sb.append(" LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.OUT_TRUST_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append(" WHERE A.TRADE_SEQ = :tradeSeq AND A.TRANSFER_TYPE = '2' ");
		sb.append(" GROUP BY A.OUT_PROD_RISK_LV ");
		sb.append(") GROUP BY PROD_RISK_LV ");
		
		queryCondition.setObject("tradeSeq", tradeSeq);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		//購物車中各商品P值贖回金額合計
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		for(Map<String, Object> cart : list) {
			map.put((String)cart.get("PROD_RISK_LV"), (BigDecimal)cart.get("AMT_SELL"));
		}
				
		return map;
	}
	
	/***
	 * 取得轉入商品中最大的風險值
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getMaxPVal(SOT140InputVO inputVO) throws Exception {
		String maxPval = inputVO.getInProdRiskLV1();
		
		try {
			int PVal = Integer.parseInt(inputVO.getInProdRiskLV1().substring(1)); //轉入1
			
			if(StringUtils.isNotBlank(inputVO.getInProdRiskLV2())) { //轉入2
				if(Integer.parseInt(inputVO.getInProdRiskLV2().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getInProdRiskLV2().substring(1));
				}
			}
			
			if(StringUtils.isNotBlank(inputVO.getInProdRiskLV3())) { //轉入3
				if(Integer.parseInt(inputVO.getInProdRiskLV3().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getInProdRiskLV3().substring(1));
				}
			}
			
			maxPval = "P" + String.valueOf(PVal);
		} catch(Exception e) {}
		
		return maxPval;
	}
}
