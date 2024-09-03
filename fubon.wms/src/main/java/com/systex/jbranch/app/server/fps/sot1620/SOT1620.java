package com.systex.jbranch.app.server.fps.sot1620;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNAVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421;
import com.systex.jbranch.app.server.fps.crm421.CRM421InputVO;
import com.systex.jbranch.app.server.fps.crm421.CRM421OutputVO;
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
 * sot1620
 * 動態鎖利母基金加碼
 */
@Component("sot1620")
@Scope("request")
public class SOT1620 extends FubonWmsBizLogic {
	
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;

	
	/**
	 * 查詢購物車明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		SOT1620InputVO inputVO = (SOT1620InputVO) body;
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		inputVO = (SOT1620InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1620OutputVO outputVO = new SOT1620OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuffer sb = null;

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append("SELECT A.* ");
		sb.append(" FROM TBSOT_NF_RAISE_AMT_DYNA A ");
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
		outputVO.setIsBackendInCart("N");

		this.sendRtnObject(outputVO);
	}
	
	// 取得商品資訊
	public void getProdDTL(Object body, IPrimitiveMap header) throws Exception {
		SOT1620InputVO inputVO = (SOT1620InputVO) body;
		SOT1620OutputVO outputVO = new SOT1620OutputVO();
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
		SOT1620InputVO inputVO = (SOT1620InputVO) body;
		inputVO = (SOT1620InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1620OutputVO outputVO = new SOT1620OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//高資產客戶投組風險權值檢核
		//動態鎖利下單不可越級適配
//		String errorMsg = getHnwcRiskValue((SOT1620InputVO)inputVO);
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
			mainVO.setTRADE_TYPE("5");//5：母基金加碼
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

		//母基金加碼明細資料
		TBSOT_NF_RAISE_AMT_DYNAPK dPK = new TBSOT_NF_RAISE_AMT_DYNAPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1
		TBSOT_NF_RAISE_AMT_DYNAVO dVO = new TBSOT_NF_RAISE_AMT_DYNAVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_RAISE_AMT_DYNAVO) dam.findByPKey(TBSOT_NF_RAISE_AMT_DYNAVO.TABLE_UID, dVO.getcomp_id());

		if (null != dVO) {
			dam.delete(dVO);
		}

		TBSOT_NF_RAISE_AMT_DYNAPK dtlPK = new TBSOT_NF_RAISE_AMT_DYNAPK();
		dtlPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dtlPK.setSEQ_NO(new BigDecimal(1)); //動態鎖利只有一筆固定為1

		TBSOT_NF_RAISE_AMT_DYNAVO dtlVO = new TBSOT_NF_RAISE_AMT_DYNAVO();
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
		if (inputVO.getProdMinBuyAmt() != null) dtlVO.setPROD_MIN_BUY_AMT(inputVO.getProdMinBuyAmt()); //最低申購金額  Number (16, 2)
		if (inputVO.getProdMinGrdAmt() != null) dtlVO.setPROD_MIN_GRD_AMT(inputVO.getProdMinGrdAmt()); //最低累進金額  Number (16, 2)
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
		dtlVO.setRAISE_AMT(inputVO.getRaiseAmt()); //加碼金額  Number (16, 2)
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
		dtlVO.setTRANSFER_DATE(inputVO.getTransDate());
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
		if (inputVO.getEngagedROI1() != null) dtlVO.setENGAGED_ROI1(inputVO.getEngagedROI1()); //約定報酬率
		if (inputVO.getEngagedROI2() != null) dtlVO.setENGAGED_ROI2(inputVO.getEngagedROI2()); //約定報酬率
		if (inputVO.getEngagedROI3() != null) dtlVO.setENGAGED_ROI3(inputVO.getEngagedROI3()); //約定報酬率
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
		SOT1620InputVO inputVO = (SOT1620InputVO) body;
		SOT1620OutputVO outputVO = new SOT1620OutputVO();
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
		SOT1620InputVO inputVO = (SOT1620InputVO) body;
		SOT110 sot110 = (SOT110) PlatformContext.getBean("sot110");
		inputVO = (SOT1620InputVO) sot110.cleanAcctCcy(inputVO);
		SOT1620OutputVO outputVO = new SOT1620OutputVO();
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
		SOT1620InputVO inputVO = (SOT1620InputVO) body;
		SOT1620OutputVO outputVO = new SOT1620OutputVO();
		dam = this.getDataAccessManager();

		//刪除明細檔資料
		TBSOT_NF_RAISE_AMT_DYNAPK dPK = new TBSOT_NF_RAISE_AMT_DYNAPK();
		dPK.setTRADE_SEQ(inputVO.getTradeSEQ());
		dPK.setSEQ_NO(new BigDecimal(1)); //固定為1
		TBSOT_NF_RAISE_AMT_DYNAVO dVO = new TBSOT_NF_RAISE_AMT_DYNAVO();
		dVO.setcomp_id(dPK);
		dVO = (TBSOT_NF_RAISE_AMT_DYNAVO) dam.findByPKey(TBSOT_NF_RAISE_AMT_DYNAVO.TABLE_UID, dVO.getcomp_id());
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
	public String dynamicESBValidate(SOT1620InputVO inputVO) throws Exception {
		SOT703InputVO inputVO703 = new SOT703InputVO();
		SOT703OutputVO outputVO703 = new SOT703OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		inputVO703.setTradeSeq(inputVO.getTradeSEQ());
		inputVO703.setConfirm(inputVO.getConfirm());

		SOT703Dyna sot703 = (SOT703Dyna) PlatformContext.getBean("sot703dyna");
		outputVO703 = sot703.verifyESBRaiseAmtNFDYNA(inputVO703); //動態鎖利母基金加碼交易驗證電文

		String errorMsg = outputVO703.getErrorMsg();
		if (StringUtils.equals("1", inputVO.getConfirm()) && StringUtils.isNotBlank(errorMsg)) {
			//檢核電文失敗才刪除資料
			TBSOT_NF_RAISE_AMT_DYNAPK errPK = new TBSOT_NF_RAISE_AMT_DYNAPK();
			errPK.setTRADE_SEQ(inputVO.getTradeSEQ());
			errPK.setSEQ_NO(inputVO.getSeqNo());
			TBSOT_NF_RAISE_AMT_DYNAVO errVO = new TBSOT_NF_RAISE_AMT_DYNAVO();
			errVO.setcomp_id(errPK);
			errVO = (TBSOT_NF_RAISE_AMT_DYNAVO) dam.findByPKey(TBSOT_NF_RAISE_AMT_DYNAVO.TABLE_UID, errVO.getcomp_id());

			//刪除明細
			if (null != errVO) {
				dam.delete(errVO);
			}

			//===若無明細，則將主檔也刪除
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM TBSOT_NF_RAISE_AMT_DYNA WHERE TRADE_SEQ = :tradeSEQ ");
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
	private CRM421OutputVO applyBargain(SOT1620InputVO inputVO) throws Exception {
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
		sb.append("FROM TBSOT_NF_RAISE_AMT_DYNA WHERE TRADE_SEQ = :tradeSEQ and FEE_TYPE = 'A' ");
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
	private String getHnwcRiskValue(SOT1620InputVO inputVO) throws Exception {
		//若檢核不通過，產生錯誤訊息
		String errorMsg = "";
		//高資產客戶且客戶風險屬性非C4，有選擇越級商品，才做投組越級檢核	
		if(!(!StringUtils.equals("C4", inputVO.getKycLV())
				&& StringUtils.equals("Y", inputVO.getHnwcYN()) && StringUtils.equals("Y", inputVO.getHnwcServiceYN())
				&& (Integer.parseInt(inputVO.getProdRiskLv().substring(1))) > (Integer.parseInt(inputVO.getKycLV().substring(1)))
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
		inputVO714.setPROD_RISK(inputVO.getProdRiskLv()); //商品風險檢核值
		BigDecimal amtBuy = inputVO.getPurchaseAmt().multiply(rate);//母基金這次申購金額折台
		
		for(int i = 1; i <= 4; i++) {
			String pVal = "P" + String.valueOf(i);
			//以這次申購商品風險檢核值判斷是否需要加上這次申購金額
			BigDecimal amt = StringUtils.equals(pVal, inputVO.getProdRiskLv()) ? amtBuy : BigDecimal.ZERO;
			//這次申購金額加上購物車中各商品P值申購金額
			switch(pVal) {
				case "P1":
					inputVO714.setAMT_BUY_1(amt); 
					break;
				case "P2":
					inputVO714.setAMT_BUY_2(amt);
					break;
				case "P3":
					inputVO714.setAMT_BUY_3(amt);
					break;
				case "P4":
					inputVO714.setAMT_BUY_4(amt);
					break;
			}
		}
		
		//查詢客戶風險檢核值資料
		WMSHAIADataVO riskValData = sot714.getByPassRiskData(inputVO714);
		
		if(StringUtils.equals("N", riskValData.getVALIDATE_YN())) {
			errorMsg = "客戶投資組合風險權值已超標，可至「投組適配試算功能」重新試算";
		}
		
		return errorMsg;
	}
	
}