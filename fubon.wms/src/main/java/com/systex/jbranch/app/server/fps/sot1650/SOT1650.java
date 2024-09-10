package com.systex.jbranch.app.server.fps.sot1650;

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

import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DYNAVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.sot110.SOT110;
import com.systex.jbranch.app.server.fps.sot110.SOT110OutputVO;
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
 * sot1650
 * 動態鎖利事件變更
 */
@Component("sot1650")
@Scope("request")
public class SOT1650 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT1650.class);
	
	/**
	 * 查詢購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		SOT1650InputVO inputVO = (SOT1650InputVO) body;
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		inputVO = (SOT1650InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1650OutputVO outputVO = new SOT1650OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT A.*, F.FUS07 ");
		sb.append(" FROM TBSOT_NF_CHANGE_DYNA A ");
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
		SOT1650InputVO inputVO = (SOT1650InputVO) body;
		SOT1650OutputVO outputVO = new SOT1650OutputVO();
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
		SOT1650InputVO inputVO = (SOT1650InputVO) body;
		inputVO = (SOT1650InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1650OutputVO outputVO = new SOT1650OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//高資產客戶投組風險權值檢核
		//高資產客戶且客戶風險屬性非C4
		//需要做越級適配檢核：有變更標的、有恢復扣款、有變更金額且增加扣款金額、有變更扣款日期且有增加次數
		//動態鎖利下單不可越級適配
//		if(!StringUtils.equals("C4", inputVO.getKycLV()) //客戶風險屬性非C4
//				&& StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN())	//高資產客戶
//				&& StringUtils.equals("Y", inputVO.getNeedHnwcRiskValueYN())) {
//			String errorMsg = getHnwcRiskValue(inputVO);
//			if(StringUtils.isNotBlank(errorMsg)) {
//				throw new APException(errorMsg);
//			}			
//		}
				
		// 1.寫入主檔/明細
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

		if (null == vo) {
			TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();

			mainVO.setTRADE_SEQ(inputVO.getTradeSEQ());
			mainVO.setPROD_TYPE(inputVO.getProdType()); //8：動態鎖利
			mainVO.setTRADE_TYPE("4");//4：事件變更
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
			
			//有新增子基金需要判斷是否需要錄音
			if(StringUtils.equals("Y", inputVO.getChgAddProdYN())) {
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
			} else {
				mainVO.setIS_REC_NEEDED("N");
			}
			mainVO.setREC_SEQ(null);
			mainVO.setSEND_DATE(null);
			mainVO.setPROF_INVESTOR_TYPE(inputVO.getCustProType()); //專業投資人類別1：大專投  2：小專投
			mainVO.setHNWC_YN(inputVO.getHnwcYN());
			mainVO.setHNWC_SERVICE_YN(inputVO.getHnwcServiceYN());
			
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

		//事件變更明細資料
		TBSOT_NF_CHANGE_DYNAPK dPK = new TBSOT_NF_CHANGE_DYNAPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1
		TBSOT_NF_CHANGE_DYNAVO dVO = new TBSOT_NF_CHANGE_DYNAVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_CHANGE_DYNAVO) dam.findByPKey(TBSOT_NF_CHANGE_DYNAVO.TABLE_UID, dVO.getcomp_id());

		if (null != dVO) {
			dam.delete(dVO);
		}

		TBSOT_NF_CHANGE_DYNAPK dtlPK = new TBSOT_NF_CHANGE_DYNAPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1

		TBSOT_NF_CHANGE_DYNAVO dtlVO = new TBSOT_NF_CHANGE_DYNAVO();
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
		dtlVO.setTRUST_CURR(inputVO.getTrustCurr()); //信託幣別
		dtlVO.setPURCHASE_AMT(inputVO.getPurchaseAmt()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC1() != null) dtlVO.setPURCHASE_AMT_C1(inputVO.getPurchaseAmtC1()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC2() != null) dtlVO.setPURCHASE_AMT_C2(inputVO.getPurchaseAmtC2()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC3() != null) dtlVO.setPURCHASE_AMT_C3(inputVO.getPurchaseAmtC3()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC4() != null) dtlVO.setPURCHASE_AMT_C4(inputVO.getPurchaseAmtC4()); //申購金額  Number (16, 2)
		if (inputVO.getPurchaseAmtC5() != null) dtlVO.setPURCHASE_AMT_C5(inputVO.getPurchaseAmtC5()); //申購金額  Number (16, 2)
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
		dtlVO.setNARRATOR_ID(inputVO.getNarratorID()); //解說專員員編  Varchar2 (16)
		dtlVO.setNARRATOR_NAME(inputVO.getNarratorName()); //解說專員姓名  Varchar2 (100)
		
		dtlVO.setPROD_STATUS_C1(inputVO.getEviNumTypeC1());
		dtlVO.setPROD_STATUS_C2(inputVO.getEviNumTypeC2());
		dtlVO.setPROD_STATUS_C3(inputVO.getEviNumTypeC3());
		dtlVO.setPROD_STATUS_C4(inputVO.getEviNumTypeC4());
		dtlVO.setPROD_STATUS_C5(inputVO.getEviNumTypeC5());
		dtlVO.setTRANSFER_DATE(inputVO.getTransDate());	
		dtlVO.setCHG_STATUS_YN(inputVO.getChgStatusYN());	
		dtlVO.setCHG_AMOUNT_YN(inputVO.getChgAmountYN());	
		dtlVO.setCHG_TRANSDATE_YN(inputVO.getChgTransDateYN());	
		dtlVO.setCHG_ADDPROD_YN(inputVO.getChgAddProdYN());	
		dtlVO.setF_PROD_STATUS_C1(inputVO.getfProdStatusC1());	
		dtlVO.setF_PROD_STATUS_C2(inputVO.getfProdStatusC2());		
		dtlVO.setF_PROD_STATUS_C3(inputVO.getfProdStatusC3());		
		dtlVO.setF_PROD_STATUS_C4(inputVO.getfProdStatusC4());		
		dtlVO.setF_PROD_STATUS_C5(inputVO.getfProdStatusC5());		
		dtlVO.setF_PURCHASE_AMT_C1(inputVO.getfPurchaseAmtC1());
		dtlVO.setF_PURCHASE_AMT_C2(inputVO.getfPurchaseAmtC2());
		dtlVO.setF_PURCHASE_AMT_C3(inputVO.getfPurchaseAmtC3());
		dtlVO.setF_PURCHASE_AMT_C4(inputVO.getfPurchaseAmtC4());	
		dtlVO.setF_PURCHASE_AMT_C5(inputVO.getfPurchaseAmtC5());	
		dtlVO.setF_TRANSFER_DATE_1(inputVO.getfTransDate1());	
		dtlVO.setF_TRANSFER_DATE_2(inputVO.getfTransDate2());	
		dtlVO.setF_TRANSFER_DATE_3(inputVO.getfTransDate3());	
		dtlVO.setF_TRANSFER_DATE_4(inputVO.getfTransDate4());	
		dtlVO.setF_TRANSFER_DATE_5(inputVO.getfTransDate5());	
		dtlVO.setF_TRANSFER_DATE_6(inputVO.getfTransDate6());	
		dtlVO.setF_PROD_ID_C1(inputVO.getfProdIdC1());
		dtlVO.setF_PROD_NAME_C1(inputVO.getfProdNameC1());	
		dtlVO.setF_PROD_ID_C2(inputVO.getfProdIdC2());
		dtlVO.setF_PROD_NAME_C2(inputVO.getfProdNameC2());		
		dtlVO.setF_PROD_ID_C3(inputVO.getfProdIdC3());
		dtlVO.setF_PROD_NAME_C3(inputVO.getfProdNameC3());	
		dtlVO.setF_PROD_ID_C4(inputVO.getfProdIdC4());
		dtlVO.setF_PROD_NAME_C4(inputVO.getfProdNameC4());	
		dtlVO.setF_PROD_ID_C5(inputVO.getfProdIdC5());
		dtlVO.setF_PROD_NAME_C5(inputVO.getfProdNameC5());	
		dtlVO.setF_ADDPROD_AMT_C1(inputVO.getfAddprodAmtC1());
		dtlVO.setF_ADDPROD_AMT_C2(inputVO.getfAddprodAmtC2());
		dtlVO.setF_ADDPROD_AMT_C3(inputVO.getfAddprodAmtC3());
		dtlVO.setF_ADDPROD_AMT_C4(inputVO.getfAddprodAmtC4());
		dtlVO.setF_ADDPROD_AMT_C5(inputVO.getfAddprodAmtC5());
		dtlVO.setF_PROD_RISK_LV_C1(inputVO.getfProdRiskLvC1());
		dtlVO.setF_PROD_RISK_LV_C2(inputVO.getfProdRiskLvC2());
		dtlVO.setF_PROD_RISK_LV_C3(inputVO.getfProdRiskLvC3());
		dtlVO.setF_PROD_RISK_LV_C4(inputVO.getfProdRiskLvC4());
		dtlVO.setF_PROD_RISK_LV_C5(inputVO.getfProdRiskLvC5());
		dtlVO.setF_PROD_MIN_BUY_AMT_C1(inputVO.getfProdMinBuyAmtC1());
		dtlVO.setF_PROD_MIN_BUY_AMT_C2(inputVO.getfProdMinBuyAmtC2());
		dtlVO.setF_PROD_MIN_BUY_AMT_C3(inputVO.getfProdMinBuyAmtC3());
		dtlVO.setF_PROD_MIN_BUY_AMT_C4(inputVO.getfProdMinBuyAmtC4());
		dtlVO.setF_PROD_MIN_BUY_AMT_C5(inputVO.getfProdMinBuyAmtC5());
		dtlVO.setF_PROD_MIN_GRD_AMT_C1(inputVO.getfProdMinGrdAmtC1());
		dtlVO.setF_PROD_MIN_GRD_AMT_C2(inputVO.getfProdMinGrdAmtC2());
		dtlVO.setF_PROD_MIN_GRD_AMT_C3(inputVO.getfProdMinGrdAmtC3());
		dtlVO.setF_PROD_MIN_GRD_AMT_C4(inputVO.getfProdMinGrdAmtC4());
		dtlVO.setF_PROD_MIN_GRD_AMT_C5(inputVO.getfProdMinGrdAmtC5());
		
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
		SOT1650InputVO inputVO = (SOT1650InputVO) body;
		SOT1650OutputVO outputVO = new SOT1650OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		TBSOT_TRADE_MAINVO vo = new TBSOT_TRADE_MAINVO();
		vo = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
		if (null != vo) {
			vo.setTRADE_STATUS("2"); // 2:風控檢核中
			dam.update(vo);
		}

		//檢核電文
		String errMsg = "";
		try {
			inputVO.setConfirm("1"); 
			errMsg = dynamicESBValidate(inputVO);
		} catch(Exception e) {
			errMsg = e.toString();
		}
		outputVO.setErrorMsg(errMsg);
		
		//有錯誤，刪除資料
		if (StringUtils.isNotBlank(errMsg)) {
			TBSOT_NF_CHANGE_DYNAPK errPK = new TBSOT_NF_CHANGE_DYNAPK();
			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			errPK.setSEQ_NO(new BigDecimal(1));
			TBSOT_NF_CHANGE_DYNAVO errVO = new TBSOT_NF_CHANGE_DYNAVO();
			errVO.setcomp_id(errPK);
			errVO = (TBSOT_NF_CHANGE_DYNAVO) dam.findByPKey(TBSOT_NF_CHANGE_DYNAVO.TABLE_UID, errVO.getcomp_id());

			//刪除明細
			if (null != errVO) {
				dam.delete(errVO);
			}

			//===主檔也刪除
			TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
			mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());
			dam.delete(mVO);
		}
				
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
		SOT1650InputVO inputVO = (SOT1650InputVO) body;
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		inputVO = (SOT1650InputVO) sot110.cleanAcctCcy(inputVO);
		SOT110OutputVO outputVO = new SOT110OutputVO();
		dam = this.getDataAccessManager();

		//確認電文
		inputVO.setConfirm("2"); 
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
		SOT1650InputVO inputVO = (SOT1650InputVO) body;
		dam = this.getDataAccessManager();

		//刪除明細檔資料
		TBSOT_NF_CHANGE_DYNAPK dPK = new TBSOT_NF_CHANGE_DYNAPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(new BigDecimal(1)); //固定為1
		TBSOT_NF_CHANGE_DYNAVO dVO = new TBSOT_NF_CHANGE_DYNAVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_CHANGE_DYNAVO) dam.findByPKey(TBSOT_NF_CHANGE_DYNAVO.TABLE_UID, dVO.getcomp_id());
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
	public String dynamicESBValidate(SOT1650InputVO inputVO) throws Exception {
		SOT703InputVO inputVO703 = new SOT703InputVO();
		SOT703OutputVO outputVO703 = new SOT703OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		inputVO703.setTradeSeq(inputVO.getTradeSEQ());
		inputVO703.setConfirm(inputVO.getConfirm());

		SOT703Dyna sot703 = (SOT703Dyna) PlatformContext.getBean("sot703dyna");
		outputVO703 = sot703.verifyESBChangeNFDYNA(inputVO703); //動態鎖利事件變更交易驗證電文

		String errorMsg = outputVO703.getErrorMsg();
		if (StringUtils.equals("1", inputVO.getConfirm()) && StringUtils.isNotBlank(errorMsg)) {
			//檢核電文失敗才刪除資料
			TBSOT_NF_CHANGE_DYNAPK errPK = new TBSOT_NF_CHANGE_DYNAPK();
			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			errPK.setSEQ_NO(inputVO.getSeqNo());
			TBSOT_NF_CHANGE_DYNAVO errVO = new TBSOT_NF_CHANGE_DYNAVO();
			errVO.setcomp_id(errPK);
			errVO = (TBSOT_NF_CHANGE_DYNAVO) dam.findByPKey(TBSOT_NF_CHANGE_DYNAVO.TABLE_UID, errVO.getcomp_id());

			//刪除明細
			if (null != errVO) {
				dam.delete(errVO);
			}

			//===若無明細，則將主檔也刪除
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBSOT_NF_CHANGE_DYNA WHERE TRADE_SEQ = :tradeSEQ ");
			queryCondition.setObject("tradeSEQ", inputVO.getTradeSEQ());
			queryCondition.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBSOT_TRADE_MAINVO mVO = new TBSOT_TRADE_MAINVO();
				mVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, inputVO.getTradeSEQ());

				dam.delete(mVO);
			}   
		}
		
		return StringUtils.isNotBlank(errorMsg) ? errorMsg : "";  
	}
	
	/***
	 * 高資產客戶投組風險權值檢核
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getHnwcRiskValue(SOT1650InputVO inputVO) throws Exception {
		//取得這次申購商品幣別買匯
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		BigDecimal buyRate = sot110.getBuyRate(inputVO.getTrustCurr());
		//取得母子基金商品中最大的風險值
		String prodRiskLv = getMaxPVal(inputVO);
		
		//取得高資產客戶註記資料
		SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
		CustHighNetWorthDataVO hnwcData = sot714.getHNWCData(inputVO.getCustID());
		
		SOT714InputVO inputVO714 = new SOT714InputVO();
		inputVO714.setCustID(inputVO.getCustID());
		inputVO714.setCUST_KYC(inputVO.getKycLV()); //客戶C值
		inputVO714.setSP_YN(hnwcData.getSpFlag()); //是否為特定客戶
		inputVO714.setPROD_RISK(prodRiskLv); //商品風險檢核值
		//變更不送sell，送新的buy就好
		BigDecimal amtBuyC1 = inputVO.getfPurchaseAmtC1() == null ? BigDecimal.ZERO : inputVO.getfPurchaseAmtC1().multiply(buyRate);//子基金1這次變更申購金額折台
		BigDecimal amtBuyC2 = inputVO.getfPurchaseAmtC2() == null ? BigDecimal.ZERO : inputVO.getfPurchaseAmtC2().multiply(buyRate);//子基金2這次變更申購金額折台
		BigDecimal amtBuyC3 = inputVO.getfPurchaseAmtC3() == null ? BigDecimal.ZERO : inputVO.getfPurchaseAmtC3().multiply(buyRate);//子基金3這次變更申購金額折台
		BigDecimal amtBuyC4 = inputVO.getfPurchaseAmtC4() == null ? BigDecimal.ZERO : inputVO.getfPurchaseAmtC4().multiply(buyRate);//子基金4這次變更申購金額折台
		BigDecimal amtBuyC5 = inputVO.getfPurchaseAmtC5() == null ? BigDecimal.ZERO : inputVO.getfPurchaseAmtC5().multiply(buyRate);//子基金5這次變更申購金額折台
		BigDecimal addAmtBuyC1 = inputVO.getfAddprodAmtC1() == null ? BigDecimal.ZERO : inputVO.getfAddprodAmtC1().multiply(buyRate);//新增子基金1這次申購金額折台
		BigDecimal addAmtBuyC2 = inputVO.getfAddprodAmtC2() == null ? BigDecimal.ZERO : inputVO.getfAddprodAmtC2().multiply(buyRate);//新增子基金2這次申購金額折台
		BigDecimal addAmtBuyC3 = inputVO.getfAddprodAmtC3() == null ? BigDecimal.ZERO : inputVO.getfAddprodAmtC3().multiply(buyRate);//新增子基金3這次申購金額折台
		BigDecimal addAmtBuyC4 = inputVO.getfAddprodAmtC4() == null ? BigDecimal.ZERO : inputVO.getfAddprodAmtC4().multiply(buyRate);//新增子基金4這次申購金額折台
		BigDecimal addAmtBuyC5 = inputVO.getfAddprodAmtC5() == null ? BigDecimal.ZERO : inputVO.getfAddprodAmtC5().multiply(buyRate);//新增子基金5這次申購金額折台
		
		for(int i = 1; i <= 4; i++) {
			String pVal = "P" + String.valueOf(i);
			//以這次申購商品風險檢核值判斷是否需要加上這次申購金額//子基金變更申購金額//新增子基金申購金額
			BigDecimal amt1 = StringUtils.equals(pVal, inputVO.getProdRiskLvC1()) ? amtBuyC1 : BigDecimal.ZERO;
			BigDecimal amt2 = StringUtils.equals(pVal, inputVO.getProdRiskLvC2()) ? amtBuyC2 : BigDecimal.ZERO;
			BigDecimal amt3 = StringUtils.equals(pVal, inputVO.getProdRiskLvC3()) ? amtBuyC3 : BigDecimal.ZERO;
			BigDecimal amt4 = StringUtils.equals(pVal, inputVO.getProdRiskLvC4()) ? amtBuyC4 : BigDecimal.ZERO;
			BigDecimal amt5 = StringUtils.equals(pVal, inputVO.getProdRiskLvC5()) ? amtBuyC5 : BigDecimal.ZERO;
			BigDecimal addAmt1 = StringUtils.equals(pVal, inputVO.getfProdRiskLvC1()) ? addAmtBuyC1 : BigDecimal.ZERO;
			BigDecimal addAmt2 = StringUtils.equals(pVal, inputVO.getfProdRiskLvC2()) ? addAmtBuyC2 : BigDecimal.ZERO;
			BigDecimal addAmt3 = StringUtils.equals(pVal, inputVO.getfProdRiskLvC3()) ? addAmtBuyC3 : BigDecimal.ZERO;
			BigDecimal addAmt4 = StringUtils.equals(pVal, inputVO.getfProdRiskLvC4()) ? addAmtBuyC4 : BigDecimal.ZERO;
			BigDecimal addAmt5 = StringUtils.equals(pVal, inputVO.getfProdRiskLvC5()) ? addAmtBuyC5 : BigDecimal.ZERO;
			//變更不送sell，送新的buy就好
			switch(pVal) {
				case "P1":
					inputVO714.setAMT_BUY_1(amt1.add(amt2).add(amt3).add(amt4).add(amt5).add(addAmt1).add(addAmt2).add(addAmt3).add(addAmt4).add(addAmt5)); 
					break;
				case "P2":
					inputVO714.setAMT_BUY_2(amt1.add(amt2).add(amt3).add(amt4).add(amt5).add(addAmt1).add(addAmt2).add(addAmt3).add(addAmt4).add(addAmt5)); 
					break;
				case "P3":
					inputVO714.setAMT_BUY_3(amt1.add(amt2).add(amt3).add(amt4).add(amt5).add(addAmt1).add(addAmt2).add(addAmt3).add(addAmt4).add(addAmt5)); 
					break;
				case "P4":
					inputVO714.setAMT_BUY_4(amt1.add(amt2).add(amt3).add(amt4).add(amt5).add(addAmt1).add(addAmt2).add(addAmt3).add(addAmt4).add(addAmt5)); 
					break;
			}
		}
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		//若檢核不通過，產生錯誤訊息
		String errorMsg = "";
		if(!StringUtils.equals("Y", riskValData.getVALIDATE_YN())) {
			errorMsg = "客戶投資組合風險權值已超標，可至「投組適配試算功能」重新試算";
		}
		
		return errorMsg;
	}
	
	/***
	 * 取得母子基金商品中最大的風險值
	 * @param inputVO
	 * @return
	 * @throws Exception
	 */
	private String getMaxPVal(SOT1650InputVO inputVO) throws Exception {
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
			//子基金4
			if(StringUtils.isNotBlank(inputVO.getProdRiskLvC4())) {
				if(Integer.parseInt(inputVO.getProdRiskLvC4().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getProdRiskLvC4().substring(1));
				}
			}
			//子基金5
			if(StringUtils.isNotBlank(inputVO.getProdRiskLvC5())) {
				if(Integer.parseInt(inputVO.getProdRiskLvC5().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getProdRiskLvC5().substring(1));
				}
			}
			//若有新增子基金也一起檢核
			//新增子基金1
			if(StringUtils.isNotBlank(inputVO.getfProdRiskLvC1())) { 
				if(Integer.parseInt(inputVO.getfProdRiskLvC1().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getfProdRiskLvC1().substring(1));
				}
			}
			//新增子基金2
			if(StringUtils.isNotBlank(inputVO.getfProdRiskLvC2())) { 
				if(Integer.parseInt(inputVO.getfProdRiskLvC2().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getfProdRiskLvC2().substring(1));
				}
			}
			//新增子基金3
			if(StringUtils.isNotBlank(inputVO.getfProdRiskLvC3())) {
				if(Integer.parseInt(inputVO.getfProdRiskLvC3().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getfProdRiskLvC3().substring(1));
				}
			}
			//新增子基金4
			if(StringUtils.isNotBlank(inputVO.getfProdRiskLvC4())) {
				if(Integer.parseInt(inputVO.getfProdRiskLvC4().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getfProdRiskLvC4().substring(1));
				}
			}
			//新增子基金5
			if(StringUtils.isNotBlank(inputVO.getfProdRiskLvC5())) {
				if(Integer.parseInt(inputVO.getfProdRiskLvC5().substring(1)) > PVal) {
					PVal = Integer.parseInt(inputVO.getfProdRiskLvC5().substring(1));
				}
			}
			
			maxPval = "P" + String.valueOf(PVal);
		} catch(Exception e) {}
		
		return maxPval;
	}
	
}