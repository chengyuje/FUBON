package com.systex.jbranch.app.server.fps.sot703;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_CHANGE_DYNA_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_CHANGE_DYNA_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_ESB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_PURCHASE_DYNA_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_PURCHASE_DYNA_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_RAISE_AMT_DYNA_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_RAISE_AMT_DYNA_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_REDEEM_DYNA_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_REDEEM_DYNA_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.OBU_NF_VERIFY_ESB;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DYNAVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_RAISE_AMT_DYNAVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DYNAPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DYNAVO;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn2.NFBRN2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn2.NFBRN2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnf.NFBRNFInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnf.NFBRNFOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrng.NFBRNGInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrng.NFBRNGOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnh.NFBRNHInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnh.NFBRNHOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrni.NFBRNIInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrni.NFBRNIOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 動態鎖利下單交易電文
 */
@Component("sot703dyna")
@Scope("request")
public class SOT703Dyna extends SOT703 {
	private String thisClaz = this.getClass().getSimpleName() + ".";
	private DataAccessManager dam;
	
	/**
	 * 動態鎖利基金申購檢核/確認電文
	 * 使用電文: NFBRNF(DBU)/AFBRNF(OBU)
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void verifyESBPurchaseNFDYNA(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBPurchaseNFDYNA(body));
	}

	/**
	 * 動態鎖利基金申購檢核
	 * 使用電文: NFBRNF(DBU)/AFBRNF(OBU)
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBPurchaseNFDYNA(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		String sendESBYN = ObjectUtils.toString(xmlInfo.getVariable("SOT.SEND_DYNA_ESB_YN", "1", "F3"));

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if(StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 由交易序號取得交易資料
		List<Map<String, Object>> list = getPurchaseNFDynaData(tradeSeq);
		if(CollectionUtils.isEmpty(list)) {
			throw new JBranchException("查無申購資料");
		}
		Map<String, Object> dynaData = list.get(0);

		Boolean isOBU = StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("IS_OBU"))) ? true : false;
		// init util
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, isOBU ? NF_VERIFY_PURCHASE_DYNA_OBU : NF_VERIFY_PURCHASE_DYNA_DBU);
		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NFBRNFInputVO txBodyVO = new NFBRNFInputVO();
		esbUtilInputVO.setNfbrnfInputVO(txBodyVO);
		txBodyVO.setTX_FLG("Y");
		txBodyVO.setCUST_ID(ObjectUtils.toString(dynaData.get("CUST_ID")));
		
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = ObjectUtils.toString(dynaData.get("BRANCH_NBR"));
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
				branchNbr = (String) loginBreach.get(0).get("BRANCH_NBR");
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else if (StringUtils.equals(branchNbr, branchChgMap.get("BS").toString())) {
			branchNbr = branchChgMap.get("DEFAULT").toString();
		}
		txBodyVO.setBRANCH_NBR(branchNbr); // 2017.01.05 BRANCH_NBR
		
		txBodyVO.setTRUST_CURR_TYPE(ObjectUtils.toString(dynaData.get("TRUST_CURR_TYPE")));
		txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setEFF_DATE(this.toChineseYearMMdd((Timestamp) dynaData.get("TRADE_DATE")));
		txBodyVO.setTRUST_ACCT(ObjectUtils.toString(dynaData.get("TRUST_ACCT")));
		txBodyVO.setCREDIT_ACCT(ObjectUtils.toString(dynaData.get("CREDIT_ACCT")));
		txBodyVO.setDEBIT_ACCT(ObjectUtils.toString(dynaData.get("DEBIT_ACCT")));
		txBodyVO.setPAY_TYPE("3"); //固定放3
		txBodyVO.setCONFIRM(sot703InputVO.getConfirm()); //確認碼 1.檢核 , 空白：確認
		txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ")));
		txBodyVO.setEMP_ID(ObjectUtils.toString(dynaData.get("MODIFIER")));
		txBodyVO.setNARRATOR_ID(ObjectUtils.toString(dynaData.get("NARRATOR_ID")));
		txBodyVO.setPROSPECTUS_TYPE(ObjectUtils.toString(dynaData.get("PROSPECTUS_TYPE"))); // 公開說明書交付方式，待確認
		txBodyVO.setTRUST_CURR(ObjectUtils.toString(dynaData.get("TRUST_CURR")));
		// 手續費
		txBodyVO.setFEE_RATE(String.valueOf(decimalPadding((BigDecimal) dynaData.get("FEE_RATE"), 3)));
		txBodyVO.setFEE(String.valueOf(decimalPadding((BigDecimal) dynaData.get("FEE"), 2)));
		txBodyVO.setPROD_ID(ObjectUtils.toString(dynaData.get("PROD_ID")));
		txBodyVO.setPURCHASE_AMT(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("PURCHASE_AMT"), 2)));
		txBodyVO.setPROD_ID_C1(ObjectUtils.toString(dynaData.get("PROD_ID_C1")));
		txBodyVO.setPURCHASE_AMT_C1(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("PURCHASE_AMT_C1"), 2)));
		txBodyVO.setPROD_ID_C2(ObjectUtils.toString(dynaData.get("PROD_ID_C2")));
		txBodyVO.setPURCHASE_AMT_C2(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("PURCHASE_AMT_C2"), 2)));
		txBodyVO.setPROD_ID_C3(ObjectUtils.toString(dynaData.get("PROD_ID_C3")));
		txBodyVO.setPURCHASE_AMT_C3(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("PURCHASE_AMT_C3"), 2)));
		txBodyVO.setTRANSFER_DATE_1(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("TRANSFER_DATE_1"))) ? "V" : ""); //約定扣款日1
		txBodyVO.setTRANSFER_DATE_2(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("TRANSFER_DATE_2"))) ? "V" : ""); //約定扣款日11
		txBodyVO.setTRANSFER_DATE_3(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("TRANSFER_DATE_3"))) ? "V" : ""); //約定扣款日21
		txBodyVO.setTRANSFER_DATE_4(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("TRANSFER_DATE_4"))) ? "V" : ""); //約定扣款日6
		txBodyVO.setTRANSFER_DATE_5(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("TRANSFER_DATE_5"))) ? "V" : ""); //約定扣款日16
		txBodyVO.setTRANSFER_DATE_6(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("TRANSFER_DATE_6"))) ? "V" : ""); //約定扣款日26
		txBodyVO.setENGAGED_ROI(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("ENGAGED_ROI"), 0)));
		txBodyVO.setREC_SEQ(ObjectUtils.toString(dynaData.get("REC_SEQ")));
		if (!StringUtils.equals("A", ObjectUtils.toString(dynaData.get("FEE_TYPE"))) || StringUtils.isBlank(sot703InputVO.getConfirm())) {
			// 檢核且申請議價時，檢核電文不須送議價編號，因為在主機中此議價編號還沒有鍵機，要等到下一步時才會鍵機；確認電文時才需要送
			txBodyVO.setBARGAIN_APPLY_SEQ(ObjectUtils.toString(dynaData.get("BARGAIN_APPLY_SEQ")));
		}
		txBodyVO.setFLAG_NUMBER(ObjectUtils.toString(dynaData.get("FLAG_NUMBER")));

		// 發送電文
		if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NFBRNFOutputVO nfbrnfOutputVO;
				if (isOBU) {
					nfbrnfOutputVO = esbUtilOutputVO.getAfbrnfOutputVO(); //OBU交易
				} else {
					nfbrnfOutputVO = esbUtilOutputVO.getNfbrnfOutputVO(); //DBU交易
				}

				// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
				if (!checkError(nfbrnfOutputVO)) {
					updateESBPurchaseNFDynaDB(tradeSeq, nfbrnfOutputVO, (BigDecimal) dynaData.get("DEFAULT_FEE_RATE"));
				}
			}
		}

		return sot703OutputVO;
	}
	
	/**
	 * 動態鎖利基金申購檢核回傳下行電文資料儲存DB
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @param nfbrn1OutputVO
	 * @param defaultFeeRate
	 * @throws JBranchException
	 */
	private void updateESBPurchaseNFDynaDB(String tradeSeq, NFBRNFOutputVO nfbrnfOutputVO, BigDecimal defaultFeeRate) throws JBranchException {
		dam = getDataAccessManager();
		
		BigDecimal fee = new EsbUtil().decimalPoint(nfbrnfOutputVO.getFEE(), 2);
		BigDecimal feeRate = new EsbUtil().decimalPoint(nfbrnfOutputVO.getFEE_RATE(), 3);
		BigDecimal feeDiscount = BigDecimal.ZERO;
		// 運算欄位
		if (defaultFeeRate != null) {
			feeDiscount = (feeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
		}

		// insert into DB
		TBSOT_NF_PURCHASE_DYNAPK pk = new TBSOT_NF_PURCHASE_DYNAPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(new BigDecimal(1));

		TBSOT_NF_PURCHASE_DYNAVO vo = (TBSOT_NF_PURCHASE_DYNAVO) dam.findByPKey(TBSOT_NF_PURCHASE_DYNAVO.TABLE_UID, pk);
		if (!StringUtils.equals("A", vo.getFEE_TYPE())) {
			// 若為申請議價則先不需要更新；因為主機尚未完成此筆議價授權，只會回最優手續費
			vo.setFEE(fee);
			vo.setFEE_RATE(feeRate);
			vo.setFEE_DISCOUNT(feeDiscount);
		}
		// update data
		dam.update(vo);
	}

	/**
	 * 動態鎖利基金申購檢核交易資料
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getPurchaseNFDynaData(String tradeSeq) throws JBranchException {
		StringBuffer sb = new StringBuffer();
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("select M.CUST_ID, M.IS_OBU, M.BRANCH_NBR, M.TRUST_TRADE_TYPE, M.TRADE_TYPE, M.FLAG_NUMBER, M.REC_SEQ, D.* ");
		sb.append(" from TBSOT_TRADE_MAIN M ");
		sb.append(" inner join TBSOT_NF_PURCHASE_DYNA D on D.TRADE_SEQ = M.TRADE_SEQ ");
		sb.append(" where M.TRADE_SEQ = :TRADE_SEQ ");
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sb.toString());

		List<Map<String, Object>> results = dam.exeQuery(condition);

		return results;
	}
	
	/**
	 * 動態鎖利母基金加碼檢核/確認電文
	 * 使用電文: NFBRNG(DBU)/AFBRNG(OBU)
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void verifyESBRaiseAmtNFDYNA(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBRaiseAmtNFDYNA(body));
	}

	/**
	 * 動態鎖利母基金加碼檢核
	 * 使用電文: NFBRNF(DBU)/AFBRNF(OBU)
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBRaiseAmtNFDYNA(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		String sendESBYN = ObjectUtils.toString(xmlInfo.getVariable("SOT.SEND_DYNA_ESB_YN", "1", "F3"));

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if(StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 由交易序號取得交易資料
		List<Map<String, Object>> list = getRaiseAmtNFDynaData(tradeSeq);
		if(CollectionUtils.isEmpty(list)) {
			throw new JBranchException("查無母基金加碼資料");
		}
		Map<String, Object> dynaData = list.get(0);

		Boolean isOBU = StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("IS_OBU"))) ? true : false;
		// init util
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, isOBU ? NF_VERIFY_RAISE_AMT_DYNA_OBU : NF_VERIFY_RAISE_AMT_DYNA_DBU);
		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NFBRNGInputVO txBodyVO = new NFBRNGInputVO();
		esbUtilInputVO.setNfbrngInputVO(txBodyVO);
		txBodyVO.setTX_FLG("Y");
		txBodyVO.setCUST_ID(ObjectUtils.toString(dynaData.get("CUST_ID")));
		
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = ObjectUtils.toString(dynaData.get("BRANCH_NBR"));
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
				branchNbr = (String) loginBreach.get(0).get("BRANCH_NBR");
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else if (StringUtils.equals(branchNbr, branchChgMap.get("BS").toString())) {
			branchNbr = branchChgMap.get("DEFAULT").toString();
		}
		txBodyVO.setBRANCH_NBR(branchNbr); // 2017.01.05 BRANCH_NBR
		
		txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setEFF_DATE(this.toChineseYearMMdd((Timestamp) dynaData.get("TRADE_DATE")));
		txBodyVO.setTRUST_ACCT(ObjectUtils.toString(dynaData.get("TRUST_ACCT")));
		txBodyVO.setCERTIFICATE_ID(ObjectUtils.toString(dynaData.get("CERTIFICATE_ID")));
		txBodyVO.setDEBIT_ACCT(ObjectUtils.toString(dynaData.get("DEBIT_ACCT")));
		txBodyVO.setPAY_TYPE("3"); //固定放3
		txBodyVO.setCONFIRM(sot703InputVO.getConfirm()); //確認碼 1.檢核 , 空白：確認
		txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ")));
		txBodyVO.setEMP_ID(ObjectUtils.toString(dynaData.get("MODIFIER")));
		txBodyVO.setNARRATOR_ID(ObjectUtils.toString(dynaData.get("NARRATOR_ID")));
		txBodyVO.setPROSPECTUS_TYPE(ObjectUtils.toString(dynaData.get("PROSPECTUS_TYPE"))); // 公開說明書交付方式，待確認
		txBodyVO.setTRUST_CURR(ObjectUtils.toString(dynaData.get("TRUST_CURR")));
		// 手續費
		txBodyVO.setFEE_RATE(String.valueOf(decimalPadding((BigDecimal) dynaData.get("FEE_RATE"), 3)));
		txBodyVO.setFEE(String.valueOf(decimalPadding((BigDecimal) dynaData.get("FEE"), 2)));
		txBodyVO.setPROD_ID(ObjectUtils.toString(dynaData.get("PROD_ID")));
		txBodyVO.setPURCHASE_AMT(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("RAISE_AMT"), 2)));
		txBodyVO.setREC_SEQ(ObjectUtils.toString(dynaData.get("REC_SEQ")));
		if (!StringUtils.equals("A", ObjectUtils.toString(dynaData.get("FEE_TYPE"))) || StringUtils.isBlank(sot703InputVO.getConfirm())) {
			// 檢核且申請議價時，檢核電文不須送議價編號，因為在主機中此議價編號還沒有鍵機，要等到下一步時才會鍵機；確認電文時才需要送
			txBodyVO.setBARGAIN_APPLY_SEQ(ObjectUtils.toString(dynaData.get("BARGAIN_APPLY_SEQ")));
		}
		txBodyVO.setFLAG_NUMBER(ObjectUtils.toString(dynaData.get("FLAG_NUMBER")));

		// 發送電文
		if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NFBRNGOutputVO nfbrngOutputVO;
				if (isOBU) {
					nfbrngOutputVO = esbUtilOutputVO.getAfbrngOutputVO(); //OBU交易
				} else {
					nfbrngOutputVO = esbUtilOutputVO.getNfbrngOutputVO(); //DBU交易
				}

				// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
				if (!checkError(nfbrngOutputVO)) {
					updateESBRaiseAmtNFDynaDB(tradeSeq, nfbrngOutputVO, (BigDecimal) dynaData.get("DEFAULT_FEE_RATE"));
				}
			}
		}

		return sot703OutputVO;
	}
	
	/**
	 * 動態鎖利母基金加碼檢核回傳下行電文資料儲存DB
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @param nfbrn1OutputVO
	 * @param defaultFeeRate
	 * @throws JBranchException
	 */
	private void updateESBRaiseAmtNFDynaDB(String tradeSeq, NFBRNGOutputVO nfbrngOutputVO, BigDecimal defaultFeeRate) throws JBranchException {
		dam = getDataAccessManager();
		
		BigDecimal fee = new EsbUtil().decimalPoint(nfbrngOutputVO.getFEE(), 2);
		BigDecimal feeRate = new EsbUtil().decimalPoint(nfbrngOutputVO.getFEE_RATE(), 3);
		BigDecimal feeDiscount = BigDecimal.ZERO;
		// 運算欄位
		if (defaultFeeRate != null) {
			feeDiscount = (feeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
		}

		// insert into DB
		TBSOT_NF_RAISE_AMT_DYNAPK pk = new TBSOT_NF_RAISE_AMT_DYNAPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(new BigDecimal(1));

		TBSOT_NF_RAISE_AMT_DYNAVO vo = (TBSOT_NF_RAISE_AMT_DYNAVO) dam.findByPKey(TBSOT_NF_RAISE_AMT_DYNAVO.TABLE_UID, pk);
		if (!StringUtils.equals("A", vo.getFEE_TYPE())) {
			// 若為申請議價則先不需要更新；因為主機尚未完成此筆議價授權，只會回最優手續費
			vo.setFEE(fee);
			vo.setFEE_RATE(feeRate);
			vo.setFEE_DISCOUNT(feeDiscount);
		}
		// update data
		dam.update(vo);
	}

	/**
	 * 動態鎖利母基金加碼檢核交易資料
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getRaiseAmtNFDynaData(String tradeSeq) throws JBranchException {
		StringBuffer sb = new StringBuffer();
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("select M.CUST_ID, M.IS_OBU, M.BRANCH_NBR, M.TRUST_TRADE_TYPE, M.TRADE_TYPE, M.FLAG_NUMBER, M.REC_SEQ, D.* ");
		sb.append(" from TBSOT_TRADE_MAIN M ");
		sb.append(" inner join TBSOT_NF_RAISE_AMT_DYNA D on D.TRADE_SEQ = M.TRADE_SEQ ");
		sb.append(" where M.TRADE_SEQ = :TRADE_SEQ ");
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sb.toString());

		List<Map<String, Object>> results = dam.exeQuery(condition);

		return results;
	}
	
	/**
	 * 動態鎖利贖回檢核
	 * 使用電文: NFBRNH(DBU)/AFBRNH(OBU)
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void verifyESBRedeemNFDYNA(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBRedeemNFDYNA(body));
	}

	/**
	 * 動態鎖利母贖回檢核
	 * 使用電文: NFBRNH(DBU)/AFBRNH(OBU)
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBRedeemNFDYNA(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		String sendESBYN = ObjectUtils.toString(xmlInfo.getVariable("SOT.SEND_DYNA_ESB_YN", "1", "F3"));

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if(StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 由交易序號取得交易資料
		List<Map<String, Object>> list = getRedeemNFDynaData(tradeSeq);
		if(CollectionUtils.isEmpty(list)) {
			throw new JBranchException("查無贖回資料");
		}
		Map<String, Object> dynaData = list.get(0);

		Boolean isOBU = StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("IS_OBU"))) ? true : false;
		// init util
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, isOBU ? NF_VERIFY_REDEEM_DYNA_OBU : NF_VERIFY_REDEEM_DYNA_DBU);
		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NFBRNHInputVO txBodyVO = new NFBRNHInputVO();
		esbUtilInputVO.setNfbrnhInputVO(txBodyVO);
		txBodyVO.setCheckCode(sot703InputVO.getConfirm()); // 確認碼 1 檢核 2 確認
		txBodyVO.setApplyDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setEffDate(this.toChineseYearMMdd((Timestamp) dynaData.get("TRADE_DATE"))); // 生效日
		
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = ObjectUtils.toString(dynaData.get("BRANCH_NBR"));
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
				branchNbr = (String) loginBreach.get(0).get("BRANCH_NBR");
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else if (StringUtils.equals(branchNbr, branchChgMap.get("BS").toString())) {
			branchNbr = branchChgMap.get("DEFAULT").toString();
		}
		txBodyVO.setBranchNo(branchNbr); // 交易行
		
		txBodyVO.setKeyinId(ObjectUtils.toString(dynaData.get("MODIFIER"))); // 建機人員
		txBodyVO.setKeyinNo(ObjectUtils.toString(dynaData.get("BATCH_SEQ"))); //批號
		txBodyVO.setTrustId(ObjectUtils.toString(dynaData.get("CUST_ID"))); //委託人統編
		txBodyVO.setEviNum(ObjectUtils.toString(dynaData.get("CERTIFICATE_ID"))); //憑證編號
		txBodyVO.setRcvAcctId(ObjectUtils.toString(dynaData.get("CREDIT_ACCT"))); //入帳帳號
		txBodyVO.setBackUntNum(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("UNIT_NUM"), 4))); // 贖回單位數
		txBodyVO.setBackType(ObjectUtils.toString(dynaData.get("REDEEM_TYPE"))); //贖回類別 1全部贖回2部分贖回

		// 發送電文
		if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NFBRNHOutputVO nfbrnhOutputVO;
				if (isOBU) {
					nfbrnhOutputVO = esbUtilOutputVO.getAfbrnhOutputVO(); //OBU交易
				} else {
					nfbrnhOutputVO = esbUtilOutputVO.getNfbrnhOutputVO(); //DBU交易
				}
				
				sot703OutputVO.setShort_1(nfbrnhOutputVO.getShort()); //短線交易類別
				// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
				if (!checkError(nfbrnhOutputVO)) {
					updateESBRedeemNFDynaDB(tradeSeq, nfbrnhOutputVO);
				}
			}
		}

		return sot703OutputVO;
	}
	
	/***
	 * 動態鎖利贖回回傳下行電文資料儲存DB
	 * @param tradeSeq
	 * @param nfbrnhOutputVO
	 * @throws JBranchException
	 */
	private void updateESBRedeemNFDynaDB(String tradeSeq, NFBRNHOutputVO nfbrnhOutputVO) throws JBranchException {
		dam = getDataAccessManager();
		// insert into DB
		TBSOT_NF_REDEEM_DYNAPK pk = new TBSOT_NF_REDEEM_DYNAPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(new BigDecimal(1));

		TBSOT_NF_REDEEM_DYNAVO vo = (TBSOT_NF_REDEEM_DYNAVO) dam.findByPKey(TBSOT_NF_REDEEM_DYNAVO.TABLE_UID, pk);
		vo.setSHORT_TYPE(nfbrnhOutputVO.getShort());
		// update data
		dam.update(vo);
	}
	
	/**
	 * 動態鎖利贖回檢核交易資料
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getRedeemNFDynaData(String tradeSeq) throws JBranchException {
		StringBuffer sb = new StringBuffer();
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("select M.CUST_ID, M.IS_OBU, M.BRANCH_NBR, M.TRUST_TRADE_TYPE, M.TRADE_TYPE, M.FLAG_NUMBER, D.* ");
		sb.append(" from TBSOT_TRADE_MAIN M ");
		sb.append(" inner join TBSOT_NF_REDEEM_DYNA D on D.TRADE_SEQ = M.TRADE_SEQ ");
		sb.append(" where M.TRADE_SEQ = :TRADE_SEQ ");
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sb.toString());

		List<Map<String, Object>> results = dam.exeQuery(condition);

		return results;
	}
	
	/**
	 * 動態鎖利轉換檢核
	 * 使用電文: NFBRN2(DBU)/AFBRN2(OBU)
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void verifyESBTransferNFDYNA(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBTransferNFDYNA(body));
	}

	/**
	 * 動態鎖利轉換檢核
	 * 使用電文: NFBRN2(DBU)/AFBRN2(OBU)
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBTransferNFDYNA(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		String sendESBYN = ObjectUtils.toString(xmlInfo.getVariable("SOT.SEND_DYNA_ESB_YN", "1", "F3"));

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if(StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 由交易序號取得交易資料
		List<Map<String, Object>> list = getTransferNFDynaData(tradeSeq);
		if(CollectionUtils.isEmpty(list)) {
			throw new JBranchException("查無轉換資料");
		}
		Map<String, Object> dynaData = list.get(0);

		Boolean isOBU = StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("IS_OBU"))) ? true : false;
		// init util
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, isOBU ? OBU_NF_VERIFY_ESB : NF_VERIFY_ESB);
		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NFBRN2InputVO txBodyVO = new NFBRN2InputVO();
		esbUtilInputVO.setNfbrn2InputVO(txBodyVO);
		txBodyVO.setEFF_DATE(this.toChineseYearMMdd((Timestamp) dynaData.get("TRADE_DATE")));
		txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setCUST_ID(ObjectUtils.toString(dynaData.get("CUST_ID")));
	
		String branchNbr = ObjectUtils.toString(dynaData.get("BRANCH_NBR"));
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
				branchNbr = (String) loginBreach.get(0).get("BRANCH_NBR");
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else if (StringUtils.equals(branchNbr, branchChgMap.get("BS").toString())) {
			branchNbr = branchChgMap.get("DEFAULT").toString();
		}
		txBodyVO.setBRANCH_NBR(branchNbr);
		
		txBodyVO.setPAY_TYPE("3");
//		txBodyVO.setFEE_DEBIT_ACCT(FEE_DEBIT_ACCT);
		txBodyVO.setNARRATOR_ID(ObjectUtils.toString(dynaData.get("NARRATOR_ID")));
		txBodyVO.setEMP_ID(ObjectUtils.toString(dynaData.get("MODIFIER")));
		txBodyVO.setCONFIRM(sot703InputVO.getConfirm());
		txBodyVO.setMINORITY_YN(this.isCustAgeLessThan18(ObjectUtils.toString(dynaData.get("CUST_ID")), ""));
		txBodyVO.setPROSPECTUS_TYPE(ObjectUtils.toString(dynaData.get("PROSPECTUS_TYPE")));
		txBodyVO.setCERTIFICATE_ID_1(ObjectUtils.toString(dynaData.get("CERTIFICATE_ID")));
		txBodyVO.setTRUST_ACCT_1(ObjectUtils.toString(dynaData.get("TRUST_ACCT")));
		txBodyVO.setRECSEQ(ObjectUtils.toString(dynaData.get("REC_SEQ")));
		txBodyVO.setTRANSFER_TYPE_1("1"); //動態鎖利轉換都是全部轉換
		if(StringUtils.equals("1", ObjectUtils.toString(dynaData.get("TRANSFER_TYPE")))) {
			//母基金轉換
			txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ"))); //批號
			txBodyVO.setOUT_PROD_ID_1(ObjectUtils.toString(dynaData.get("PROD_ID")));
			txBodyVO.setIN_PROD_ID_1_1(ObjectUtils.toString(dynaData.get("IN_PROD_ID")));
			//全部轉換，轉出單位數=轉入單位數
			txBodyVO.setOUT_UNIT_1(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("UNIT_NUM"), 4)));
			txBodyVO.setIN_UNIT_1_1(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("UNIT_NUM"), 4)));
			txBodyVO.setIN_PROD_RISK_LV_1_1(ObjectUtils.toString(dynaData.get("IN_PROD_RISK_LV")));
			
			// 發送電文
			if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
				sendTransferTo400(esbUtilInputVO, isOBU);
			}
		} else {
			//子基金轉回母基金
			//每一檔子基金要轉回母基金都要發一次電文
			for(int i = 1; i <= 5; i++) {
				if(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("IN_PROD_C" + String.valueOf(i) + "_YN")))) {
					txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ_C" + String.valueOf(i)))); //批號
					txBodyVO.setOUT_PROD_ID_1(ObjectUtils.toString(dynaData.get("PROD_ID_C" + String.valueOf(i)))); //轉出子基金
					txBodyVO.setIN_PROD_ID_1_1(ObjectUtils.toString(dynaData.get("PROD_ID"))); //轉入母基金
					//全部轉換，轉出單位數=轉入單位數=轉出子基金單位數
					txBodyVO.setOUT_UNIT_1(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("UNIT_NUM_C" + String.valueOf(i)), 4)));
					txBodyVO.setIN_UNIT_1_1(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("UNIT_NUM_C" + String.valueOf(i)), 4)));
					txBodyVO.setIN_PROD_RISK_LV_1_1(ObjectUtils.toString(dynaData.get("PROD_RISK_LV")));
					
					// 發送電文
					if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
						sendTransferTo400(esbUtilInputVO, isOBU);
					}
				}
			}
		}

		return sot703OutputVO;
	}
	
	//發送轉換電文至400主機
	private void sendTransferTo400(ESBUtilInputVO esbUtilInputVO, boolean isOBU) throws Exception {
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN2OutputVO nfbrn2OutputVO;
			if (isOBU) {
				nfbrn2OutputVO = esbUtilOutputVO.getAfbrn2OutputVO(); //OBU交易
			} else {
				nfbrn2OutputVO = esbUtilOutputVO.getNfbrn2OutputVO(); //DBU交易
			}

			// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
			if (!checkError(nfbrn2OutputVO)) {
				
			}
		}
	}
	
	/**
	 * 動態鎖利轉換檢核交易資料
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getTransferNFDynaData(String tradeSeq) throws JBranchException {
		StringBuffer sb = new StringBuffer();
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("select M.CUST_ID, M.IS_OBU, M.BRANCH_NBR, M.TRUST_TRADE_TYPE, M.TRADE_TYPE, M.FLAG_NUMBER, M.REC_SEQ, D.* ");
		sb.append(" from TBSOT_TRADE_MAIN M ");
		sb.append(" inner join TBSOT_NF_TRANSFER_DYNA D on D.TRADE_SEQ = M.TRADE_SEQ ");
		sb.append(" where M.TRADE_SEQ = :TRADE_SEQ ");
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sb.toString());

		List<Map<String, Object>> results = dam.exeQuery(condition);

		return results;
	}
	
	/**
	 * 動態鎖利事件變更檢核
	 * 使用電文: NFBRNI(DBU)/AFBRNI(OBU)
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void verifyESBChangeNFDYNA(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBTransferNFDYNA(body));
	}

	/**
	 * 動態鎖利事件變更檢核
	 * 使用電文: NFBRNI(DBU)/AFBRNI(OBU)
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBChangeNFDYNA(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		String sendESBYN = ObjectUtils.toString(xmlInfo.getVariable("SOT.SEND_DYNA_ESB_YN", "1", "F3"));

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if(StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 由交易序號取得交易資料
		List<Map<String, Object>> list = getChangeNFDynaData(tradeSeq);
		if(CollectionUtils.isEmpty(list)) {
			throw new JBranchException("查無事件變更資料");
		}
		Map<String, Object> dynaData = list.get(0);

		Boolean isOBU = StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("IS_OBU"))) ? true : false;
		// init util
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, isOBU ? NF_VERIFY_CHANGE_DYNA_OBU : NF_VERIFY_CHANGE_DYNA_DBU);
		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NFBRNIInputVO txBodyVO = new NFBRNIInputVO();
		esbUtilInputVO.setNfbrniInputVO(txBodyVO);
		txBodyVO.setCUST_ID(ObjectUtils.toString(dynaData.get("CUST_ID")));
		
		String branchNbr = ObjectUtils.toString(dynaData.get("BRANCH_NBR"));
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
				branchNbr = (String) loginBreach.get(0).get("BRANCH_NBR");
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else if (StringUtils.equals(branchNbr, branchChgMap.get("BS").toString())) {
			branchNbr = branchChgMap.get("DEFAULT").toString();
		}
		txBodyVO.setBRANCH_NBR(branchNbr);
		
		txBodyVO.setEFF_DATE(this.toChineseYearMMdd((Timestamp) dynaData.get("TRADE_DATE")));
		txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setTRUST_ACCT(ObjectUtils.toString(dynaData.get("TRUST_ACCT")));
		txBodyVO.setCONFIRM(sot703InputVO.getConfirm());		
		txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ")));
		txBodyVO.setEMP_ID(ObjectUtils.toString(dynaData.get("MODIFIER")));
		txBodyVO.setNARRATOR_ID(ObjectUtils.toString(dynaData.get("NARRATOR_ID")));
		txBodyVO.setREC_SEQ(ObjectUtils.toString(dynaData.get("REC_SEQ")));
		txBodyVO.setCERTIFICATE_ID(ObjectUtils.toString(dynaData.get("CERTIFICATE_ID")));
		
		//不同變更事項為不同批號，須個別作檢核
		//變更轉換日期
		if(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("CHG_TRANSDATE_YN")))) {
			txBodyVO.setCHANGE_TYPE("E3");
			txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ_TRANSDATE")));
			
			txBodyVO.setE3_TRANSFER_DATE1(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_TRANSFER_DATE_1"))) ? "V" : ""); //約定扣款日1
			txBodyVO.setE3_TRANSFER_DATE11(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_TRANSFER_DATE_2"))) ? "V" : ""); //約定扣款日11
			txBodyVO.setE3_TRANSFER_DATE21(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_TRANSFER_DATE_3"))) ? "V" : ""); //約定扣款日21
			txBodyVO.setE3_TRANSFER_DATE6(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_TRANSFER_DATE_4"))) ? "V" : ""); //約定扣款日6
			txBodyVO.setE3_TRANSFER_DATE16(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_TRANSFER_DATE_5"))) ? "V" : ""); //約定扣款日16
			txBodyVO.setE3_TRANSFER_DATE26(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_TRANSFER_DATE_6"))) ? "V" : ""); //約定扣款日26
			
			// 發送電文
			if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
				sendChangeTo400(esbUtilInputVO, isOBU);
			}
		}
		//變更子基金轉換金額
		if(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("CHG_AMOUNT_YN")))) {
			txBodyVO.setCHANGE_TYPE("E4");
			txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ_AMOUNT")));
			
			if(dynaData.get("F_PURCHASE_AMT_C1") != null) {
				txBodyVO.setE4_PROD_ID_C1(ObjectUtils.toString(dynaData.get("PROD_ID_C1")));
				txBodyVO.setE4_PURCHASE_AMT_C1(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_PURCHASE_AMT_C1"), 2)));
			}
			if(dynaData.get("F_PURCHASE_AMT_C2") != null) {
				txBodyVO.setE4_PROD_ID_C2(ObjectUtils.toString(dynaData.get("PROD_ID_C2")));
				txBodyVO.setE4_PURCHASE_AMT_C2(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_PURCHASE_AMT_C2"), 2)));
			}
			if(dynaData.get("F_PURCHASE_AMT_C3") != null) {
				txBodyVO.setE4_PROD_ID_C3(ObjectUtils.toString(dynaData.get("PROD_ID_C3")));
				txBodyVO.setE4_PURCHASE_AMT_C3(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_PURCHASE_AMT_C3"), 2)));
			}
			if(dynaData.get("F_PURCHASE_AMT_C4") != null) {
				txBodyVO.setE4_PROD_ID_C4(ObjectUtils.toString(dynaData.get("PROD_ID_C4")));
				txBodyVO.setE4_PURCHASE_AMT_C4(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_PURCHASE_AMT_C4"), 2)));
			}
			if(dynaData.get("F_PURCHASE_AMT_C5") != null) {
				txBodyVO.setE4_PROD_ID_C5(ObjectUtils.toString(dynaData.get("PROD_ID_C5")));
				txBodyVO.setE4_PURCHASE_AMT_C5(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_PURCHASE_AMT_C5"), 2)));
			}
			
			// 發送電文
			if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
				sendChangeTo400(esbUtilInputVO, isOBU);
			}
		}
		//變更子基金扣款狀態
		if(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("CHG_STATUS_YN")))) {
			txBodyVO.setCHANGE_TYPE("E5");
			txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ_STATUS")));
			
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C1")))) {
				txBodyVO.setE5_PROD_ID_C1(ObjectUtils.toString(dynaData.get("PROD_ID_C1")));
				txBodyVO.setE5_SUSPEND_C1(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C1"))) ? "Y" : "");
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C2")))) {
				txBodyVO.setE5_PROD_ID_C2(ObjectUtils.toString(dynaData.get("PROD_ID_C2")));
				txBodyVO.setE5_SUSPEND_C2(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C2"))) ? "Y" : "");
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C3")))) {
				txBodyVO.setE5_PROD_ID_C3(ObjectUtils.toString(dynaData.get("PROD_ID_C3")));
				txBodyVO.setE5_SUSPEND_C3(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C3"))) ? "Y" : "");
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C4")))) {
				txBodyVO.setE5_PROD_ID_C4(ObjectUtils.toString(dynaData.get("PROD_ID_C4")));
				txBodyVO.setE5_SUSPEND_C4(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C4"))) ? "Y" : "");
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C5")))) {
				txBodyVO.setE5_PROD_ID_C5(ObjectUtils.toString(dynaData.get("PROD_ID_C5")));
				txBodyVO.setE5_SUSPEND_C5(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("F_PROD_STATUS_C5"))) ? "Y" : "");
			}
			
			// 發送電文
			if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
				sendChangeTo400(esbUtilInputVO, isOBU);
			}
		}
		//變更事項_新增子基金
		if(StringUtils.equals("Y", ObjectUtils.toString(dynaData.get("CHG_ADDPROD_YN")))) {
			txBodyVO.setCHANGE_TYPE("E6");
			txBodyVO.setBATCH_SEQ(ObjectUtils.toString(dynaData.get("BATCH_SEQ_ADDPROD")));
			
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_ID_C1")))) {
				txBodyVO.setE6_PROD_ID_C2(ObjectUtils.toString(dynaData.get("F_PROD_ID_C1")));
				txBodyVO.setE6_PURCHASE_AMT_C2(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_ADDPROD_AMT_C1"), 2)));
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_ID_C2")))) {
				txBodyVO.setE6_PROD_ID_C3(ObjectUtils.toString(dynaData.get("F_PROD_ID_C2")));
				txBodyVO.setE6_PURCHASE_AMT_C3(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_ADDPROD_AMT_C2"), 2)));
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_ID_C3")))) {
				txBodyVO.setE6_PROD_ID_C4(ObjectUtils.toString(dynaData.get("F_PROD_ID_C3")));
				txBodyVO.setE6_PURCHASE_AMT_C4(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_ADDPROD_AMT_C3"), 2)));
			}
			if(StringUtils.isNotBlank(ObjectUtils.toString(dynaData.get("F_PROD_ID_C4")))) {
				txBodyVO.setE6_PROD_ID_C5(ObjectUtils.toString(dynaData.get("F_PROD_ID_C4")));
				txBodyVO.setE6_PURCHASE_AMT_C5(String.valueOf(new EsbUtil().decimalPadding((BigDecimal) dynaData.get("F_ADDPROD_AMT_C4"), 2)));
			}
			
			// 發送電文
			if(StringUtils.equals("Y", sendESBYN)) { //是否打電文，電文還沒好先用參數控
				sendChangeTo400(esbUtilInputVO, isOBU);
			}
		}
		return sot703OutputVO;
	}
	
	/**
	 * 動態鎖利事件變更檢核交易資料
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @return
	 * @throws JBranchException
	 */
	private List<Map<String, Object>> getChangeNFDynaData(String tradeSeq) throws JBranchException {
		StringBuffer sb = new StringBuffer();
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("select M.CUST_ID, M.IS_OBU, M.BRANCH_NBR, M.TRUST_TRADE_TYPE, M.TRADE_TYPE, M.FLAG_NUMBER, M.REC_SEQ, D.* ");
		sb.append(" from TBSOT_TRADE_MAIN M ");
		sb.append(" inner join TBSOT_NF_CHANGE_DYNA D on D.TRADE_SEQ = M.TRADE_SEQ ");
		sb.append(" where M.TRADE_SEQ = :TRADE_SEQ ");
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sb.toString());

		List<Map<String, Object>> results = dam.exeQuery(condition);

		return results;
	}
	
	//發送事件變更電文至400主機
	private void sendChangeTo400(ESBUtilInputVO esbUtilInputVO, boolean isOBU) throws Exception {
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRNIOutputVO nfbrniOutputVO;
			if (isOBU) {
				nfbrniOutputVO = esbUtilOutputVO.getAfbrniOutputVO(); //OBU交易
			} else {
				nfbrniOutputVO = esbUtilOutputVO.getNfbrniOutputVO(); //DBU交易
			}

			// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
			if (!checkError(nfbrniOutputVO)) {
				
			}
		}
	}
}