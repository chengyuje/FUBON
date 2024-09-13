package com.systex.jbranch.app.server.fps.sot703;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AFBRNE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AI_BANK_SEND_PURCHASE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CMKTYPE_MK03;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CUST_ASSET_NF_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CUST_ASSET_NF_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CUST_ASSET_XF_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NFBRNE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_BANK_SEND_WEBPURCHASENF;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_BANK_VERIFY_PURCHASE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_CONFIRM_ESB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_ESB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_ESB_NF;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_PURCHASE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_VERIFY_REDEEM;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.OBU_NF_CONFIRM_ESB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.OBU_NF_VERIFY_ESB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.OBU_NF_VERIFY_ESB_NF;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.OBU_NF_VERIFY_PURCHASE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.OBU_NF_VERIFY_REDEEM;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.XF_VERIFY_PURCHASE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_BATCHVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_CHANGE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_PURCHASE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_REDEEM_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_NF_TRANSFER_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.app.server.fps.sot150.SOT150InputVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.afbrn9.AFBRN9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMN2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMN2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMNInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMNOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn1.NFBRN1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn1.NFBRN1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn2.NFBRN2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn2.NFBRN2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn3.NFBRN3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn3.NFBRN3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn5.NFBRN5InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn5.NFBRN5InputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn5.NFBRN5OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.CustAssetFundVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn9.NFBRN9OutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrne.NFBRNEInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrne.NFBRNEOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnf.NFBRNFOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrng.NFBRNGOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrnh.NFBRNHOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrni.NFBRNIOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrx9.NFBRX9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn067n.VN067NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn067n.VN067NOutputVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Created by SebastianWu on 2016/8/23.
 *
 * @version 2016/12/19
 * @author Sebastian NFBRN5電文欄位異動{@link NFBRN5InputVO}
 *
 * @version 2018/12/12
 * @author samtu 將電文上送帳號欄位多增加長度判斷
 */
@Component("sot703")
@Scope("request")
public class SOT703 extends EsbUtil {
	@Autowired
	CBSService cbsservice;
	@Autowired
	SOT701 sot701;
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private String thisClaz = this.getClass().getSimpleName() + ".";

	private DataAccessManager dam;
	protected SOT703InputVO sot703InputVO;
	protected SOT703OutputVO sot703OutputVO;
	private SOT712 sot712;

	// @Deprecated use this.effDate private Date effDate; 改為前端UI設定 EFF DATE

	/* const */
	protected String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;

	@PostConstruct
	public void SOT703() throws JBranchException {
		sot712 = (SOT712) PlatformContext.getBean("sot712");
		// @Deprecated use this.effDate effDate = getEffDate();
	}

	/**
	 * 取得生效日期
	 *
	 * @return
	 * @throws JBranchException
	 * @Deprecated 改為前端UI設定 EFF DATE private Date getEffDate() throws
	 *             JBranchException { SOT712InputVO sot712InputVO = new
	 *             SOT712InputVO(); sot712InputVO.setProdType("NF");
	 *             SOT712OutputVO sot712OutputVO =
	 *             sot712.getEffDate(sot712InputVO);
	 *
	 *             return sot712OutputVO.getTradeDate(); }
	 */

	/**
	 * 根據電文回傳錯誤訊息，放入SOT703OutputVO中 若最上層有錯誤訊息，則回傳最上層；否則回傳細項第一個錯誤訊息
	 *
	 * @param obj
	 * @return
	 * @throws JBranchException
	 */
	protected Boolean checkError(Object obj) throws JBranchException {
		String errCode = null;
		String errTxt = null;
		String txFlag = "Y";
		Boolean isErr = Boolean.FALSE;

		if (obj instanceof NFBRN1OutputVO) { // 申購
			NFBRN1OutputVO vo = (NFBRN1OutputVO) obj;

			errCode = StringUtils.isNotBlank(vo.getERR_COD()) ? vo.getERR_COD() : StringUtils.isNotBlank(vo.getERR_COD_1()) ? vo.getERR_COD_1() : StringUtils.isNotBlank(vo.getERR_COD_2()) ? vo.getERR_COD_2() : StringUtils.isNotBlank(vo.getERR_COD_3()) ? vo.getERR_COD_3() : null;

			errTxt = StringUtils.isNotBlank(vo.getERR_TXT()) ? vo.getERR_TXT() : StringUtils.isNotBlank(vo.getERR_TXT_1()) ? vo.getERR_TXT_1() : StringUtils.isNotBlank(vo.getERR_TXT_2()) ? vo.getERR_TXT_2() : StringUtils.isNotBlank(vo.getERR_TXT_3()) ? vo.getERR_TXT_3() : null;

			txFlag = vo.getTX_FLG();
		} else if (obj instanceof NFBRN2OutputVO) { // 轉換 //動態鎖利轉換
			NFBRN2OutputVO vo = (NFBRN2OutputVO) obj;

			errCode = StringUtils.isNotBlank(vo.getERR_COD()) ? vo.getERR_COD() : StringUtils.isNotBlank(vo.getERR_COD_1()) ? vo.getERR_COD_1() : StringUtils.isNotBlank(vo.getERR_COD_2()) ? vo.getERR_COD_2() : null;

			errTxt = StringUtils.isNotBlank(vo.getERR_TXT()) ? vo.getERR_TXT() : StringUtils.isNotBlank(vo.getERR_TXT_1()) ? vo.getERR_TXT_1() : StringUtils.isNotBlank(vo.getERR_TXT_2()) ? vo.getERR_TXT_2() : null;

			txFlag = vo.getTX_FLG();
		} else if (obj instanceof NFBRN3OutputVO) { // 贖回
			NFBRN3OutputVO vo = (NFBRN3OutputVO) obj;

			errCode = StringUtils.isNotBlank(vo.getErrorCode()) ? vo.getErrorCode() : StringUtils.isNotBlank(vo.getErrorCode_1()) ? vo.getErrorCode_1() : StringUtils.isNotBlank(vo.getErrorCode_2()) ? vo.getErrorCode_2() : StringUtils.isNotBlank(vo.getErrorCode_3()) ? vo.getErrorCode_3() : null;

			errTxt = StringUtils.isNotBlank(vo.getErrorMsg()) ? vo.getErrorMsg() : StringUtils.isNotBlank(vo.getErrorMsg_1()) ? vo.getErrorMsg_1() : StringUtils.isNotBlank(vo.getErrorMsg_2()) ? vo.getErrorMsg_2() : StringUtils.isNotBlank(vo.getErrorMsg_3()) ? vo.getErrorMsg_3() : null;
		} else if (obj instanceof NFBRN5OutputVO) { // 變更
			NFBRN5OutputVO vo = (NFBRN5OutputVO) obj;

			errCode = StringUtils.isNotBlank(vo.getErrorCode()) ? vo.getErrorCode() : null;
			errTxt = StringUtils.isNotBlank(vo.getErrorMsg()) ? vo.getErrorMsg() : null;
		} else if (obj instanceof NFBRN5InputVODetails) { // 變更
			NFBRN5InputVODetails vo = (NFBRN5InputVODetails) obj;

			errCode = StringUtils.isNotBlank(vo.getErrorCode1()) ? vo.getErrorCode1() : null;
			errTxt = StringUtils.isNotBlank(vo.getErrorMsg1()) ? vo.getErrorMsg1() : null;
		} else if (obj instanceof VN067NOutputVO) { // 網路快速下單 - 基金申購檢核
			VN067NOutputVO vo = (VN067NOutputVO) obj;

			// TODO 先避開ESB 電文BUG errCode =
			// StringUtils.isNotBlank(vo.getEMSGID()) ? vo.getEMSGID() : null;
			// TODO 先避開ESB 電文BUG errTxt =
			// StringUtils.isNotBlank(vo.getEMSGTXT()) ? vo.getEMSGTXT() : null;
		} else if (obj instanceof EBPMNOutputVO) { // 網路快速下單 –
													// 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息
			EBPMNOutputVO vo = (EBPMNOutputVO) obj;

			errCode = StringUtils.isNotBlank(vo.getErrorCode()) ? vo.getErrorCode() : null;
			errTxt = StringUtils.isNotBlank(vo.getErrorMsg()) ? vo.getErrorMsg() : null;
		} else if (obj instanceof EBPMN2OutputVO) { // AI BANK快速下單 – 發送電文至AI BANK, 行事曆表單上可以出現快速申購之訊息
			EBPMN2OutputVO vo = (EBPMN2OutputVO) obj;
			errCode = StringUtils.isNotBlank(vo.getErrorCode()) ? vo.getErrorCode() : null;
			errTxt = StringUtils.isNotBlank(vo.getErrorMsg()) ? vo.getErrorMsg() : null;
		}

		if (StringUtils.isNotBlank(errCode)) {
			sot703OutputVO.setErrorCode(errCode);
			sot703OutputVO.setErrorMsg(errTxt);
			isErr = Boolean.TRUE;
			if (sot703InputVO != null)
				logger.error(String.format("sot703Output:Error CustId %s ; TradeSeq %s ; ProdId %s:", sot703InputVO.getCustId(), sot703InputVO.getTradeSeq(), sot703InputVO.getProdId()));
			if (sot703OutputVO != null)
				logger.error(String.format("sot703Output:Error %s ; %s:", sot703OutputVO.getErrorCode(), sot703OutputVO.getErrorMsg()));
		} else if (!StringUtils.equals("Y", txFlag)) {
			// 若不可承作交易
			sot703OutputVO.setErrorCode("ehl_01_sot705_001");
			sot703OutputVO.setErrorMsg("此客戶不可承作此交易");
			isErr = Boolean.TRUE;
		} else if (obj instanceof NFBRNFOutputVO) { // 動態鎖利基金申購
			NFBRNFOutputVO vo = (NFBRNFOutputVO) obj;
			sot703OutputVO.setErrorCode(StringUtils.isNotBlank(vo.getERR_COD()) ? vo.getERR_COD() : null);
			sot703OutputVO.setErrorMsg(StringUtils.isNotBlank(vo.getERR_TXT()) ? vo.getERR_TXT() : null);
		} else if (obj instanceof NFBRNGOutputVO) { // 動態鎖利母基金加碼
			NFBRNGOutputVO vo = (NFBRNGOutputVO) obj;
			sot703OutputVO.setErrorCode(StringUtils.isNotBlank(vo.getERR_COD()) ? vo.getERR_COD() : null);
			sot703OutputVO.setErrorMsg(StringUtils.isNotBlank(vo.getERR_TXT()) ? vo.getERR_TXT() : null);
		} else if (obj instanceof NFBRNHOutputVO) { // 動態鎖利贖回
			NFBRNHOutputVO vo = (NFBRNHOutputVO) obj;
			sot703OutputVO.setErrorCode(StringUtils.isNotBlank(vo.getErrorCode()) ? vo.getErrorCode() : null);
			sot703OutputVO.setErrorMsg(StringUtils.isNotBlank(vo.getErrorMsg()) ? vo.getErrorMsg() : null);
		} else if (obj instanceof NFBRNIOutputVO) { // 動態鎖利事件變更
			NFBRNIOutputVO vo = (NFBRNIOutputVO) obj;
			sot703OutputVO.setErrorCode(StringUtils.isNotBlank(vo.getERR_COD()) ? vo.getERR_COD() : null);
			sot703OutputVO.setErrorMsg(StringUtils.isNotBlank(vo.getERR_TXT()) ? vo.getERR_TXT() : null);
		}

		return isErr;
	}

	/**
	 * 若有錯誤，Throw Exception
	 *
	 * @param obj
	 * @throws JBranchException
	 */
	public void throwError(Object obj) throws JBranchException {
		// 若有錯誤，Throw Exception
		if (checkError(obj)) {
			if (StringUtils.isNotBlank(sot703OutputVO.getErrorCode())) {
				String esbError = String.format("%s:%s", sot703OutputVO.getErrorCode(), sot703OutputVO.getErrorMsg());
				logger.error("ESB throwError " + esbError);
				throw new JBranchException(esbError);
			}
		}
	}

	/**
	 * 基金申購檢核 for js client
	 *
	 * 使用電文: NFBRN1
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void verifyESBPurchaseNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBPurchaseNF(body));
	}

	/**
	 * 基金申購檢核
	 *
	 * 使用電文: NFBRN1(特金)
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBPurchaseNF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號
		String seqNo = sot703InputVO.getSeqNo(); // 明細檔交易流水號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(seqNo)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 由交易序號取得交易資料
		MainInfoBean mainVO = selectVerifyPurchaseNF(tradeSeq, seqNo);

		Boolean isOBU = StringUtils.equals("Y", mainVO.getTradeMainVO().getIS_OBU()) ? true : false;
		// init util
		ESBUtilInputVO esbUtilInputVO;
		if (isOBU && !StringUtils.equals(mainVO.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
			esbUtilInputVO = getTxInstance(ESB_TYPE, OBU_NF_VERIFY_PURCHASE);
		} else {
			esbUtilInputVO = getTxInstance(ESB_TYPE, NF_VERIFY_PURCHASE);
		}

		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NFBRN1InputVO txBodyVO = new NFBRN1InputVO();
		esbUtilInputVO.setNfbrn1InputVO(txBodyVO);
		txBodyVO.setTX_FLG("Y");

		if (!StringUtils.equals(mainVO.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
			txBodyVO.setCUST_ID(mainVO.getTradeMainVO().getCUST_ID());
			txBodyVO.setDEBIT_ID(mainVO.getTradeMainVO().getCUST_ID());
		} else {
			//金錢信託固定帶台北富邦受託人ID
			txBodyVO.setCUST_ID("99331241");
			txBodyVO.setDEBIT_ID("99331241");
		}

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getTradeMainVO().getBRANCH_NBR();
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
		
		// 金錢信託合併已無下面邏輯 Eli。
		//		txBodyVO.setTRUST_CURR_TYPE(mainVO.getPurchaseDVO().getTRUST_CURR_TYPE());
		//		txBodyVO.setTRADE_SUB_TYPE("0" + mainVO.getPurchaseDVO().getTRADE_SUB_TYPE());
		//		txBodyVO.setTRADE_DATE(this.toChineseYearMMdd(mainVO.getPurchaseDVO().getCreatetime()));//交易日期
		//		txBodyVO.setEFF_DATE(this.toChineseYearMMdd(mainVO.getPurchaseDVO().getTRADE_DATE()));
		//		txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate()));  //FOR CBS測試日期修改
		//		txBodyVO.setEFF_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改

		txBodyVO.setTRUST_ACCT(mainVO.getPurchaseDVO().getTRUST_ACCT());
		txBodyVO.setCREDIT_ACCT(mainVO.getPurchaseDVO().getCREDIT_ACCT());
		//		txBodyVO.setDEBIT_ID(mainVO.getTradeMainVO().getCUST_ID());
		txBodyVO.setDEBIT_ACCT(mainVO.getPurchaseDVO().getDEBIT_ACCT());
		txBodyVO.setEMP_ID(mainVO.getPurchaseDVO().getModifier());
		txBodyVO.setNARRATOR_ID(mainVO.getPurchaseDVO().getNARRATOR_ID());

		txBodyVO.setTRUST_CURR_TYPE(mainVO.getPurchaseDVO().getTRUST_CURR_TYPE());
		txBodyVO.setTRADE_SUB_TYPE("0" + mainVO.getPurchaseDVO().getTRADE_SUB_TYPE());
		//		txBodyVO.setTRADE_DATE(this.toChineseYearMMdd(mainVO.getPurchaseDVO()
		//				.getCreatetime()));// 交易日期
		txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setEFF_DATE(this.toChineseYearMMdd(mainVO.getPurchaseDVO().getTRADE_DATE()));

		txBodyVO.setPAY_TYPE("1".equals(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE()) ? "3" : ""); // 單筆放"3",小額放空白
		txBodyVO.setCONFIRM("1");

		txBodyVO.setPROSPECTUS_TYPE(mainVO.getPurchaseDVO().getPROSPECTUS_TYPE()); // 公開說明書交付方式，待確認
		txBodyVO.setPROD_ID_1(mainVO.getPurchaseDVO().getPROD_ID());

		// 申購金額
		if (FundRule.isSingleOrMultiple(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
			// 單筆/定期定額
			txBodyVO.setPURCHASE_AMT_L_1(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT(), 2)));
		} else {
			// 定期不定額
			txBodyVO.setPURCHASE_AMT_L_1(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_L(), 2)));
			txBodyVO.setPURCHASE_AMT_M_1(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_M(), 2)));
			txBodyVO.setPURCHASE_AMT_H_1(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_H(), 2)));
		}

		// 手續費
		if (StringUtils.equals("E", mainVO.getPurchaseDVO().getFEE_TYPE())) {
			// 次數型團體優惠, 不需要放費率等欄位
			txBodyVO.setGROUP_IFA_1(mainVO.getPurchaseDVO().getGROUP_OFA());
		} else if (StringUtils.equals("1", mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
			// 單筆
			txBodyVO.setFEE_RATE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE(), 3)));
			txBodyVO.setFEE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE(), 2)));
		} else {
			// 定期定額/定期不定額
			if (mainVO.getPurchaseDVO().getFEE_TYPE().matches("A|D")) { // 申請議價、單次議價
				// 小額議價放團體代碼, 不需要放費率等欄位
				// 申請議價因為還未覆核過，所以先不放，否則電文會回錯誤
				txBodyVO.setGROUP_IFA_1(StringUtils.equals("D", mainVO.getPurchaseDVO().getFEE_TYPE()) ? mainVO.getPurchaseDVO().getGROUP_OFA() : "");
			} else {
				if (FundRule.isMultiple(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
					// 定期定額
					txBodyVO.setFEE_RATE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE(), 3)));
					txBodyVO.setFEE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE(), 2)));
				} else {
					// 定期不定額
					txBodyVO.setFEE_RATE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE_L(), 3)));
					txBodyVO.setFEE_RATE_M_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE_M(), 3)));
					txBodyVO.setFEE_RATE_H_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE_H(), 3)));
					txBodyVO.setFEE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_L(), 2)));
					txBodyVO.setFEE_M_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_M(), 2)));
					txBodyVO.setFEE_H_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_H(), 2)));
				}
			}
		}

		if (!StringUtils.equals("A", mainVO.getPurchaseDVO().getFEE_TYPE())) {
			// 申請議價時，檢核電文不須送議價編號，因為在主機中此議價編號還沒有鍵機；要等到下一步時才會鍵機
			txBodyVO.setBARGAIN_APPLY_SEQ_1(mainVO.getPurchaseDVO().getBARGAIN_APPLY_SEQ());
		}

		txBodyVO.setCHARGE_DATE_1_1(mainVO.getPurchaseDVO().getCHARGE_DATE_1());
		txBodyVO.setCHARGE_DATE_2_1(mainVO.getPurchaseDVO().getCHARGE_DATE_2());
		txBodyVO.setCHARGE_DATE_3_1(mainVO.getPurchaseDVO().getCHARGE_DATE_3());
		txBodyVO.setCHARGE_DATE_4_1(mainVO.getPurchaseDVO().getCHARGE_DATE_4());
		txBodyVO.setCHARGE_DATE_5_1(mainVO.getPurchaseDVO().getCHARGE_DATE_5());
		txBodyVO.setCHARGE_DATE_6_1(mainVO.getPurchaseDVO().getCHARGE_DATE_6());

		//特金交易才給值
		if (!StringUtils.equals(mainVO.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {

			txBodyVO.setBTH_COUPON_1((StringUtils.equals("B", mainVO.getPurchaseDVO().getFEE_TYPE())) ? "Y" : "");

			txBodyVO.setAUTO_CX_1(mainVO.getPurchaseDVO().getIS_AUTO_CX());

			BigDecimal takeProfitPerc = mainVO.getPurchaseDVO().getTAKE_PROFIT_PERC();
			BigDecimal stopLossPerc = mainVO.getPurchaseDVO().getSTOP_LOSS_PERC();

			txBodyVO.setTAKE_PROFIT_PERC_1(takeProfitPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) takeProfitPerc).abs(), 2)));
			txBodyVO.setSTOP_LOSS_PERC_1(stopLossPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) stopLossPerc).abs(), 2)));
			//0477 N改放空白
			txBodyVO.setIS_PL_1((takeProfitPerc != null || stopLossPerc != null) ? "Y" : " ");
			txBodyVO.setTAKE_PROFIT_SYM_1((takeProfitPerc != null && takeProfitPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-");
			txBodyVO.setSTOP_LOSS_SYM_1((stopLossPerc != null && stopLossPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-");

		}
		
		if(StringUtils.equals("1", mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())  
				&& !StringUtils.equals(mainVO.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {			
			//#1695 貸轉投
			txBodyVO.setFLAG_NUMBER(mainVO.getTradeMainVO().getFLAG_NUMBER());
		}
		
		

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN1OutputVO nfbrn1OutputVO;
			if (isOBU && !StringUtils.equals(mainVO.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
				nfbrn1OutputVO = esbUtilOutputVO.getAfbrn1OutputVO(); //OBU特金交易
			} else {
				nfbrn1OutputVO = esbUtilOutputVO.getNfbrn1OutputVO(); //特金交易
			}

			// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
			if (!checkError(nfbrn1OutputVO) && !StringUtils.equals(mainVO.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
				//2020-3-10 與建達確認金錢信託檢核電文的第二道舊電文不必儲存
				updateVerifyESBPurchaseNFDB(tradeSeq, new BigDecimal(seqNo), nfbrn1OutputVO, mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_L(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_M(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_H());
			}
		}

		return sot703OutputVO;
	}

	/**
	 * 基金申購檢核
	 *
	 * 使用電文: NFBRX1(金錢信託)
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBPurchaseXF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號
		String seqNo = sot703InputVO.getSeqNo(); // 明細檔交易流水號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(seqNo)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 由交易序號取得交易資料
		MainInfoBean mainVO = selectVerifyPurchaseNF(tradeSeq, seqNo);

		// init util
		ESBUtilInputVO esbUtilInputVO;
		//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
		esbUtilInputVO = getTxInstance(ESB_TYPE, XF_VERIFY_PURCHASE);

		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NFBRN1InputVO txBodyVO = new NFBRN1InputVO();
		esbUtilInputVO.setNfbrn1InputVO(txBodyVO);
		txBodyVO.setTX_FLG("Y");
		txBodyVO.setCUST_ID(mainVO.getTradeMainVO().getCUST_ID());

		//特金交易才給值
		if (!StringUtils.equals(mainVO.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNbr = mainVO.getTradeMainVO().getBRANCH_NBR();
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

			txBodyVO.setTRUST_ACCT(mainVO.getPurchaseDVO().getTRUST_ACCT());
			txBodyVO.setCREDIT_ACCT(mainVO.getPurchaseDVO().getCREDIT_ACCT());
			txBodyVO.setDEBIT_ID(mainVO.getTradeMainVO().getCUST_ID());
			txBodyVO.setDEBIT_ACCT(mainVO.getPurchaseDVO().getDEBIT_ACCT());
			txBodyVO.setEMP_ID(mainVO.getPurchaseDVO().getModifier());
			txBodyVO.setNARRATOR_ID(mainVO.getPurchaseDVO().getNARRATOR_ID());
		}

		txBodyVO.setTRUST_CURR_TYPE(mainVO.getPurchaseDVO().getTRUST_CURR_TYPE());
		txBodyVO.setTRADE_SUB_TYPE("0" + mainVO.getPurchaseDVO().getTRADE_SUB_TYPE());
		//		txBodyVO.setTRADE_DATE(this.toChineseYearMMdd(mainVO.getPurchaseDVO()
		//				.getCreatetime()));// 交易日期
		txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setEFF_DATE(this.toChineseYearMMdd(mainVO.getPurchaseDVO().getTRADE_DATE()));

		txBodyVO.setPAY_TYPE("1".equals(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE()) ? "3" : ""); // 單筆放"3",小額放空白
		txBodyVO.setCONFIRM("1");

		txBodyVO.setPROSPECTUS_TYPE(mainVO.getPurchaseDVO().getPROSPECTUS_TYPE()); // 公開說明書交付方式，待確認
		txBodyVO.setPROD_ID_1(mainVO.getPurchaseDVO().getPROD_ID());

		// 申購金額
		if (FundRule.isSingleOrMultiple(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
			// 單筆/定期定額
			txBodyVO.setPURCHASE_AMT_L_1(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT(), 2)));
		} else {
			// 定期不定額
			txBodyVO.setPURCHASE_AMT_L_1(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_L(), 2)));
			txBodyVO.setPURCHASE_AMT_M_1(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_M(), 2)));
			txBodyVO.setPURCHASE_AMT_H_1(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_H(), 2)));
		}

		// 手續費
		if (StringUtils.equals("E", mainVO.getPurchaseDVO().getFEE_TYPE())) {
			// 次數型團體優惠, 不需要放費率等欄位
			txBodyVO.setGROUP_IFA_1(mainVO.getPurchaseDVO().getGROUP_OFA());
		} else if (StringUtils.equals("1", mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
			// 單筆
			txBodyVO.setFEE_RATE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE(), 3)));
			txBodyVO.setFEE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE(), 2)));
		} else {
			// 定期定額/定期不定額
			if (mainVO.getPurchaseDVO().getFEE_TYPE().matches("A|D")) { // 申請議價、單次議價
				// 小額議價放團體代碼, 不需要放費率等欄位
				// 申請議價因為還未覆核過，所以先不放，否則電文會回錯誤
				//金錢信託小額的NFBRX1電文不送GROUP_IFA_1
				if (!(StringUtils.equals(mainVO.getTradeMainVO().getTRUST_TRADE_TYPE(), "M") && StringUtils.equals(mainVO.getTradeMainVO().getTRADE_TYPE(), "5"))) {

					txBodyVO.setGROUP_IFA_1(StringUtils.equals("D", mainVO.getPurchaseDVO().getFEE_TYPE()) ? mainVO.getPurchaseDVO().getGROUP_OFA() : "");
				}

			} else {
				if (FundRule.isMultiple(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
					// 定期定額
					txBodyVO.setFEE_RATE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE(), 3)));
					txBodyVO.setFEE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE(), 2)));
				} else {
					// 定期不定額
					txBodyVO.setFEE_RATE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE_L(), 3)));
					txBodyVO.setFEE_RATE_M_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE_M(), 3)));
					txBodyVO.setFEE_RATE_H_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_RATE_H(), 3)));
					txBodyVO.setFEE_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_L(), 2)));
					txBodyVO.setFEE_M_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_M(), 2)));
					txBodyVO.setFEE_H_1(String.valueOf(decimalPadding(mainVO.getPurchaseDVO().getFEE_H(), 2)));
				}
			}
		}

		if (!StringUtils.equals("A", mainVO.getPurchaseDVO().getFEE_TYPE())) {
			// 申請議價時，檢核電文不須送議價編號，因為在主機中此議價編號還沒有鍵機；要等到下一步時才會鍵機
			txBodyVO.setBARGAIN_APPLY_SEQ_1(mainVO.getPurchaseDVO().getBARGAIN_APPLY_SEQ());
		}

		txBodyVO.setCHARGE_DATE_1_1(mainVO.getPurchaseDVO().getCHARGE_DATE_1());
		txBodyVO.setCHARGE_DATE_2_1(mainVO.getPurchaseDVO().getCHARGE_DATE_2());
		txBodyVO.setCHARGE_DATE_3_1(mainVO.getPurchaseDVO().getCHARGE_DATE_3());
		txBodyVO.setCHARGE_DATE_4_1(mainVO.getPurchaseDVO().getCHARGE_DATE_4());
		txBodyVO.setCHARGE_DATE_5_1(mainVO.getPurchaseDVO().getCHARGE_DATE_5());
		txBodyVO.setCHARGE_DATE_6_1(mainVO.getPurchaseDVO().getCHARGE_DATE_6());

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {

			NFBRN1OutputVO nfbrn1OutputVO = esbUtilOutputVO.getNfbrx1OutputVO(); //金錢信託

			// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
			if (!checkError(nfbrn1OutputVO)) {
				updateVerifyESBPurchaseNFDB(tradeSeq, new BigDecimal(seqNo), nfbrn1OutputVO, mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_L(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_M(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_H());
			}
		}

		return sot703OutputVO;
	}

	/**
	 * 基金申購檢核回傳下行電文資料儲存DB
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @param nfbrn1OutputVO
	 * @param defaultFeeRate
	 * @throws JBranchException
	 */
	private void updateVerifyESBPurchaseNFDB(String tradeSeq, BigDecimal seqNo, NFBRN1OutputVO nfbrn1OutputVO, BigDecimal defaultFeeRate, BigDecimal defaultFeeRateL, BigDecimal defaultFeeRateM, BigDecimal defaultFeeRateH) throws JBranchException {
		dam = getDataAccessManager();

		BigDecimal zero = BigDecimal.ZERO;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		BigDecimal fee = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_1(), 2);
		BigDecimal feeRate = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_1(), 3);
		BigDecimal feeDiscount = zero;
		BigDecimal feeL = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_1(), 2);
		BigDecimal feeRateL = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_1(), 3);
		BigDecimal feeDiscountL = zero;
		BigDecimal feeM = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_M_1(), 2);
		BigDecimal feeRateM = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_M_1(), 3);
		BigDecimal feeDiscountM = zero;
		BigDecimal feeH = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_H_1(), 2);
		BigDecimal feeRateH = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_H_1(), 3);
		BigDecimal feeDiscountH = zero;
		// @Deprecated use this.effDate String tradeDateType =
		// (StringUtils.equals(sdf.format(this.effDate), sdf.format(new
		// Date()))) ? "1" : "2";
		// @Deprecated use this.effDate Date tradeDate = this.effDate;

		// 運算欄位
		if (defaultFeeRate != null) {
			feeDiscount = (feeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
			feeDiscountL = (feeRateL != null && defaultFeeRateL != null && defaultFeeRateL.compareTo(BigDecimal.ZERO) != 0) ? feeRateL.divide(defaultFeeRateL, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
			feeDiscountM = (feeRateM != null && defaultFeeRateM != null && defaultFeeRateM.compareTo(BigDecimal.ZERO) != 0) ? feeRateM.divide(defaultFeeRateM, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
			feeDiscountH = (feeRateH != null && defaultFeeRateH != null && defaultFeeRateH.compareTo(BigDecimal.ZERO) != 0) ? feeRateH.divide(defaultFeeRateH, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
		}

		// insert into DB
		TBSOT_NF_PURCHASE_DPK pk = new TBSOT_NF_PURCHASE_DPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(seqNo);

		TBSOT_NF_PURCHASE_DVO vo;

		// search data
		vo = (TBSOT_NF_PURCHASE_DVO) dam.findByPKey(TBSOT_NF_PURCHASE_DVO.TABLE_UID, pk);

		if (FundRule.isSingleOrMultiple(vo.getTRADE_SUB_TYPE())) {
			// 單筆/定期定額
			if (!StringUtils.equals("A", vo.getFEE_TYPE())) {
				// 若為申請議價則先不需要更新；因為主機尚未完成此筆議價授權，只會回最優手續費
				vo.setFEE(fee);
				vo.setFEE_RATE(feeRate);
				vo.setFEE_DISCOUNT(feeDiscount);
			}
		} else {
			// 定期不定額
			if (!StringUtils.equals("A", vo.getFEE_TYPE())) {
				// 若為申請議價則先不需要更新；因為主機尚未完成此筆議價授權，只會回最優手續費
				vo.setFEE_L(feeL);
				vo.setFEE_RATE_L(feeRateL);
				vo.setFEE_DISCOUNT_L(feeDiscountL);
				vo.setFEE_M(feeM);
				vo.setFEE_RATE_M(feeRateM);
				vo.setFEE_DISCOUNT_M(feeDiscountM);
				vo.setFEE_H(feeH);
				vo.setFEE_RATE_H(feeRateH);
				vo.setFEE_DISCOUNT_H(feeDiscountH);
			}
		}

		vo.setGROUP_OFA(vo.getGROUP_OFA());
		// @Deprecated use this.effDate vo.setTRADE_DATE_TYPE(tradeDateType);
		// @Deprecated use this.effDate vo.setTRADE_DATE(new
		// Timestamp(tradeDate.getTime()));

		// update data
		dam.update(vo);
	}

	/**
	 * 基金申購檢核交易資料
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @return
	 * @throws JBranchException
	 */
	private MainInfoBean selectVerifyPurchaseNF(String tradeSeq, String seqNo) throws JBranchException {
		String sql = null;

		sql = new StringBuffer("select M.CUST_ID, " + "       M.IS_OBU, " + "       M.BRANCH_NBR, " + "		  M.TRUST_TRADE_TYPE, " //WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
				+ "		  M.TRADE_TYPE, " //金錢信託小額NFBRX1不送group_ifa
				+ "		  M.FLAG_NUMBER, " //貸轉註記
				+ "       D.*  " + "  from TBSOT_TRADE_MAIN M " + "  inner join TBSOT_NF_PURCHASE_D D on D.TRADE_SEQ = M.TRADE_SEQ " + "  left outer join TBCRM_CUST_MAST C on C.CUST_ID = M.CUST_ID " + " where M.TRADE_SEQ = :TRADE_SEQ " + "   and D.SEQ_NO = :SEQ_NO ").toString();

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setObject("SEQ_NO", seqNo);
		condition.setQueryString(sql);

		List<Map> results = dam.exeQuery(condition);

		MainInfoBean main = new MainInfoBean();

		TBSOT_TRADE_MAINVO tradeMainVO = new TBSOT_TRADE_MAINVO();
		main.setTradeMainVO(tradeMainVO);

		TBSOT_NF_PURCHASE_DVO purchaseDVO = new TBSOT_NF_PURCHASE_DVO();
		main.setPurchaseDVO(purchaseDVO);

		TBORG_MEMBERVO memberVO = new TBORG_MEMBERVO();
		main.setMemberVO(memberVO);

		TBCRM_CUST_MASTVO custMastVO = new TBCRM_CUST_MASTVO();
		main.setCustMastVO(custMastVO);

		if (CollectionUtils.isNotEmpty(results)) {
			Map result = results.get(0);
			tradeMainVO.setCUST_ID((String) result.get("CUST_ID"));
			tradeMainVO.setIS_OBU(CharUtils.toString((Character) result.get("IS_OBU")));
			tradeMainVO.setBRANCH_NBR((String) result.get("BRANCH_NBR")); // 2017.01.05
			tradeMainVO.setTRUST_TRADE_TYPE((String) result.get("TRUST_TRADE_TYPE")); // 2020-2-10 金錢信託
			tradeMainVO.setFLAG_NUMBER((String) result.get("FLAG_NUMBER")); // 2020-2-10 金錢信託
			char tradeType = (char) result.get("TRADE_TYPE");
			tradeMainVO.setTRADE_TYPE(String.valueOf(tradeType));
			purchaseDVO.setTRUST_CURR_TYPE(CharUtils.toString((Character) result.get("TRUST_CURR_TYPE")));
			purchaseDVO.setTRADE_SUB_TYPE(CharUtils.toString((Character) result.get("TRADE_SUB_TYPE")));
			purchaseDVO.setTRUST_ACCT((String) result.get("TRUST_ACCT"));
			purchaseDVO.setCREDIT_ACCT((String) result.get("CREDIT_ACCT"));
			purchaseDVO.setDEBIT_ACCT((String) result.get("DEBIT_ACCT"));
			purchaseDVO.setModifier((String) result.get("MODIFIER"));
			purchaseDVO.setNARRATOR_ID((String) result.get("NARRATOR_ID"));
			purchaseDVO.setPROD_ID((String) result.get("PROD_ID"));
			purchaseDVO.setFEE_RATE((BigDecimal) result.get("FEE_RATE"));
			purchaseDVO.setFEE_RATE_L((BigDecimal) result.get("FEE_RATE_L"));
			purchaseDVO.setFEE_RATE_M((BigDecimal) result.get("FEE_RATE_M"));
			purchaseDVO.setFEE_RATE_H((BigDecimal) result.get("FEE_RATE_H"));
			purchaseDVO.setFEE((BigDecimal) result.get("FEE"));
			purchaseDVO.setFEE_L((BigDecimal) result.get("FEE_L"));
			purchaseDVO.setFEE_M((BigDecimal) result.get("FEE_M"));
			purchaseDVO.setFEE_H((BigDecimal) result.get("FEE_H"));
			purchaseDVO.setPURCHASE_AMT((BigDecimal) result.get("PURCHASE_AMT"));
			purchaseDVO.setPURCHASE_AMT_L((BigDecimal) result.get("PURCHASE_AMT_L"));
			purchaseDVO.setPURCHASE_AMT_M((BigDecimal) result.get("PURCHASE_AMT_M"));
			purchaseDVO.setPURCHASE_AMT_H((BigDecimal) result.get("PURCHASE_AMT_H"));
			purchaseDVO.setCHARGE_DATE_1((String) result.get("CHARGE_DATE_1"));
			purchaseDVO.setCHARGE_DATE_2((String) result.get("CHARGE_DATE_2"));
			purchaseDVO.setCHARGE_DATE_3((String) result.get("CHARGE_DATE_3"));
			purchaseDVO.setCHARGE_DATE_4((String) result.get("CHARGE_DATE_4"));
			purchaseDVO.setCHARGE_DATE_5((String) result.get("CHARGE_DATE_5"));
			purchaseDVO.setCHARGE_DATE_6((String) result.get("CHARGE_DATE_6"));
			purchaseDVO.setFEE_TYPE(CharUtils.toString((Character) result.get("FEE_TYPE")));
			purchaseDVO.setIS_AUTO_CX(CharUtils.toString((Character) result.get("IS_AUTO_CX")));
			purchaseDVO.setTAKE_PROFIT_PERC((BigDecimal) result.get("TAKE_PROFIT_PERC"));
			purchaseDVO.setSTOP_LOSS_PERC((BigDecimal) result.get("STOP_LOSS_PERC"));
			purchaseDVO.setBARGAIN_APPLY_SEQ((String) result.get("BARGAIN_APPLY_SEQ"));
			purchaseDVO.setDEFAULT_FEE_RATE((BigDecimal) result.get("DEFAULT_FEE_RATE"));
			purchaseDVO.setDEFAULT_FEE_RATE_L((BigDecimal) result.get("DEFAULT_FEE_RATE_L"));
			purchaseDVO.setDEFAULT_FEE_RATE_M((BigDecimal) result.get("DEFAULT_FEE_RATE_M"));
			purchaseDVO.setDEFAULT_FEE_RATE_H((BigDecimal) result.get("DEFAULT_FEE_RATE_H"));
			purchaseDVO.setCreatetime((Timestamp) result.get("CREATETIME"));
			purchaseDVO.setTRADE_DATE((Timestamp) result.get("TRADE_DATE"));
			purchaseDVO.setPROSPECTUS_TYPE(CharUtils.toString((Character) result.get("PROSPECTUS_TYPE")));// 公開說明書方式
			purchaseDVO.setGROUP_OFA((String) result.get("GROUP_OFA"));

			// Date birthDate = (Date) result.get("BIRTH_DATE");
			// custMastVO.setBIRTH_DATE(birthDate == null ? null: new
			// Timestamp(birthDate.getTime()));
		}

		return main;
	}

	/**
	 * 基金申購確認
	 *
	 * 使用電文: NFBRN1
	 *
	 * @param body
	 * @throws Exception
	 */
	public void confirmESBPurchaseNF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();
		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號未輸入");
		}

		// 取得主檔資料
		MainInfoBean mainInfo = selectConfirmESBPurchaseNF(tradeSeq);

		// 取得批號資料
		List<String> batchInfo = selectConfirmESBPurchaseNFBatches(tradeSeq);

		// 依批號分批發送電文
		List<DetailInfoBean> detailInfo = null;
		for (String batchSeq : batchInfo) {
			// 依照批號取得明細資料
			detailInfo = getConfirmESBPurchaseNFDetails(tradeSeq, batchSeq);

			// 使用欄位
			String custID = null; // 客戶ID
			String branchNbr = null; // 分行別
			String trustCurrType = null; // 信託業務別
			String tradeSubType = null; // 交易類別
			// Date tradeDate = null; //交易日期
			Date effDate = null; // 生效日期
			Date createTime = null;
			String trustAcct = null; // 信託帳號
			String creditAcct = null; // 收益入帳帳號
			String debitID = null; // 扣款ID
			String debitAcct = null; // 扣款帳號
			String payType = null; // 繳款方式
			String confirm = ""; // 確認碼
			String empID = null; // 建機人員
			String narratorID = null; // 解說人員
			BigDecimal feeRate1 = null; // 1手續費率
			BigDecimal fee1 = null; // 1手續費
			BigDecimal feeRateM1 = null; // 1手續費率中
			BigDecimal feeM1 = null; // 1手續費高
			BigDecimal feeRateH1 = null; // 1手續費率中
			BigDecimal feeH1 = null; // 1手續費高

			String prodID1 = null; // 1基金代號/基金套餐
			String groupOfa1 = null; // 1優惠團體代碼
			BigDecimal purchaseAmt1 = null; // 1扣款金額低
			BigDecimal purchaseAmtM1 = null; // 1扣款金額中
			BigDecimal purchaseAmtH1 = null; // 1扣款金額高
			String chargeDate1_1 = null; // 1約定扣款日1
			String chargeDate1_2 = null; // 1約定扣款日2
			String chargeDate1_3 = null; // 1約定扣款日3
			String chargeDate1_4 = null; // 1約定扣款日4
			String chargeDate1_5 = null; // 1約定扣款日5
			String chargeDate1_6 = null; // 1約定扣款日6
			String prodID2 = null; // 2基金代號/基金套餐
			String groupOfa2 = null; // 2優惠團體代碼
			BigDecimal feeRate2 = null; // 2手續費率
			BigDecimal fee2 = null; // 2手續費
			BigDecimal feeRateM2 = null; // 2手續費率中
			BigDecimal feeM2 = null; // 2手續費高
			BigDecimal feeRateH2 = null; // 2手續費率中
			BigDecimal feeH2 = null; // 2手續費高
			BigDecimal purchaseAmt2 = null; // 2扣款金額低
			BigDecimal purchaseAmtM2 = null; // 2扣款金額中
			BigDecimal purchaseAmtH2 = null; // 2扣款金額高
			String chargeDate2_1 = null; // 2約定扣款日1
			String chargeDate2_2 = null; // 2約定扣款日2
			String chargeDate2_3 = null; // 2約定扣款日3
			String chargeDate2_4 = null; // 2約定扣款日4
			String chargeDate2_5 = null; // 2約定扣款日5
			String chargeDate2_6 = null; // 2約定扣款日6
			String prodID3 = null; // 3基金代號/基金套餐
			String groupOfa3 = null; // 3優惠團體代碼
			BigDecimal feeRate3 = null; // 3手續費率
			BigDecimal fee3 = null; // 3手續費
			BigDecimal feeRateM3 = null; // 3手續費率中
			BigDecimal feeM3 = null; // 3手續費高
			BigDecimal feeRateH3 = null; // 3手續費率中
			BigDecimal feeH3 = null; // 3手續費高
			BigDecimal purchaseAmt3 = null; // 3扣款金額低
			BigDecimal purchaseAmtM3 = null; // 3扣款金額中
			BigDecimal purchaseAmtH3 = null; // 3扣款金額高
			String chargeDate3_1 = null; // 3約定扣款日1
			String chargeDate3_2 = null; // 3約定扣款日2
			String chargeDate3_3 = null; // 3約定扣款日3
			String chargeDate3_4 = null; // 3約定扣款日4
			String chargeDate3_5 = null; // 3約定扣款日5
			String chargeDate3_6 = null; // 3約定扣款日6
			String bthCoupon1 = null; // 生日券1
			String bthCoupon2 = null; // 生日券2
			String bthCoupon3 = null; // 生日券3
			String isAutoCx1 = null; // 自動換匯1
			String isAutoCx2 = null; // 自動換匯2
			String isAutoCx3 = null; // 自動換匯3
			String recSeq = mainInfo.getTradeMainVO().getREC_SEQ(); // 錄音序號
			String isPl1 = null; // 是否停損停利1
			String takeProfitPerc1 = null; // 停利1
			String takeProfitSym1 = null; // 停利符號1
			String stopLossPerc1 = null; // 停損1
			String stopLossSym1 = null; // 停損符號1
			String bargainApplySeq1 = null; // 是否停損停利2
			String isPl2 = null; // 停利2
			String takeProfitPerc2 = null; // 停利符號2
			String takeProfitSym2 = null; // 停損2
			String stopLossPerc2 = null; // 停損符號2
			String stopLossSym2 = null; // 是否停損停利3
			String bargainApplySeq2 = null; // 停利3
			String isPl3 = null; // 停利符號3
			String takeProfitPerc3 = null; // 停損3
			String takeProfitSym3 = null; // 停損符號3
			String stopLossPerc3 = null; // 議價編號1
			String stopLossSym3 = null; // 議價編號2
			String bargainApplySeq3 = null; // 議價編號3
			String prospectusType = null; // 公開說明書方式
			String flagNumber = mainInfo.getTradeMainVO().getFLAG_NUMBER(); // 貸款註記
			// 讀取電文批號資料 (至多三筆)
			int idx = 0;
			for (DetailInfoBean detail : detailInfo) {
				// duplicated column
				String prodID = detail.getPurchaseDVO().getPROD_ID();
				String gropuOfa = detail.getPurchaseDVO().getGROUP_OFA();
				BigDecimal purchaseAmt = new EsbUtil().decimalPadding(detail.getPurchaseDVO().getPURCHASE_AMT(), 2);
				BigDecimal purchaseAmtL = new EsbUtil().decimalPadding(detail.getPurchaseDVO().getPURCHASE_AMT_L(), 2);
				BigDecimal purchaseAmtM = new EsbUtil().decimalPadding(detail.getPurchaseDVO().getPURCHASE_AMT_M(), 2);
				BigDecimal purchaseAmtH = new EsbUtil().decimalPadding(detail.getPurchaseDVO().getPURCHASE_AMT_H(), 2);

				String chargeDate1 = detail.getPurchaseDVO().getCHARGE_DATE_1();
				String chargeDate2 = detail.getPurchaseDVO().getCHARGE_DATE_2();
				String chargeDate3 = detail.getPurchaseDVO().getCHARGE_DATE_3();
				String chargeDate4 = detail.getPurchaseDVO().getCHARGE_DATE_4();
				String chargeDate5 = detail.getPurchaseDVO().getCHARGE_DATE_5();
				String chargeDate6 = detail.getPurchaseDVO().getCHARGE_DATE_6();

				String feeType = detail.getPurchaseDVO().getFEE_TYPE();
				String bthCoupon = (StringUtils.isNotBlank(feeType) && StringUtils.equals("B", feeType)) ? "Y" : "";
				String isAutoCx = detail.getPurchaseDVO().getIS_AUTO_CX();

				BigDecimal takeProfitPerc = detail.getPurchaseDVO().getTAKE_PROFIT_PERC();
				BigDecimal stopLossPerc = detail.getPurchaseDVO().getSTOP_LOSS_PERC();

				String takeProfitPercStr = takeProfitPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) takeProfitPerc).abs(), 2));
				String takeProfitSym = (takeProfitPerc != null && takeProfitPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-";
				String stopLossPercStr = stopLossPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) stopLossPerc).abs(), 2));
				String stopLossSym = (stopLossPerc != null && stopLossPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-";
				//0477 N改放空白
				String isPl = (takeProfitPerc != null || stopLossPerc != null) ? "Y" : " ";

				String bargainApplySeq = detail.getPurchaseDVO().getBARGAIN_APPLY_SEQ();
				// BigDecimal feeRateL =
				// detail.getPurchaseDVO().getFEE_RATE_L();

				// 第一筆
				if (idx == 0) {
					custID = mainInfo.getTradeMainVO().getCUST_ID();

					// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
					// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
					branchNbr = mainInfo.getTradeMainVO().getBRANCH_NBR();
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

					createTime = detail.getPurchaseDVO().getCreatetime();
					effDate = detail.getPurchaseDVO().getTRADE_DATE();
					trustCurrType = detail.getPurchaseDVO().getTRUST_CURR_TYPE();
					tradeSubType = "0" + detail.getPurchaseDVO().getTRADE_SUB_TYPE();
					trustAcct = detail.getPurchaseDVO().getTRUST_ACCT();
					creditAcct = detail.getPurchaseDVO().getCREDIT_ACCT();
					debitID = mainInfo.getTradeMainVO().getCUST_ID();
					debitAcct = detail.getPurchaseDVO().getDEBIT_ACCT();
					payType = "1".equals(detail.getPurchaseDVO().getTRADE_SUB_TYPE()) ? "3" : ""; // 單筆放"3",小額放空白
					empID = detail.getPurchaseDVO().getModifier();
					narratorID = detail.getPurchaseDVO().getNARRATOR_ID();
					prodID1 = prodID;
					groupOfa1 = gropuOfa;
					// 申購金額
					if (FundRule.isSingleOrMultiple(detail.getPurchaseDVO().getTRADE_SUB_TYPE())) {
						// 單筆/定期定額
						purchaseAmt1 = purchaseAmt;
					} else {
						// 定期不定額
						purchaseAmt1 = purchaseAmtL;
						purchaseAmtM1 = purchaseAmtM;
						purchaseAmtH1 = purchaseAmtH;
					}
					// 手續費
					if (StringUtils.equals("1", detail.getPurchaseDVO().getTRADE_SUB_TYPE())) {
						// 單筆
						feeRate1 = detail.getPurchaseDVO().getFEE_RATE();
						fee1 = detail.getPurchaseDVO().getFEE();
					} else {
						if (FundRule.isMultiple(detail.getPurchaseDVO().getTRADE_SUB_TYPE())) {
							// 定期定額
							feeRate1 = detail.getPurchaseDVO().getFEE_RATE();
							fee1 = detail.getPurchaseDVO().getFEE();
						} else {
							// 定期不定額
							feeRate1 = detail.getPurchaseDVO().getFEE_RATE_L();
							fee1 = detail.getPurchaseDVO().getFEE_L();
							feeRateM1 = detail.getPurchaseDVO().getFEE_RATE_M();
							feeM1 = detail.getPurchaseDVO().getFEE_M();
							feeRateH1 = detail.getPurchaseDVO().getFEE_RATE_H();
							feeH1 = detail.getPurchaseDVO().getFEE_H();
						}
					}
					chargeDate1_1 = chargeDate1;
					chargeDate1_2 = chargeDate2;
					chargeDate1_3 = chargeDate3;
					chargeDate1_4 = chargeDate4;
					chargeDate1_5 = chargeDate5;
					chargeDate1_6 = chargeDate6;
					bthCoupon1 = bthCoupon;
					isAutoCx1 = isAutoCx;
					isPl1 = isPl;
					takeProfitPerc1 = takeProfitPercStr;
					takeProfitSym1 = takeProfitSym;
					stopLossPerc1 = stopLossPercStr;
					stopLossSym1 = stopLossSym;
					bargainApplySeq1 = bargainApplySeq;
					prospectusType = detail.getPurchaseDVO().getPROSPECTUS_TYPE();
				}
				// 第二筆
				else if (idx == 1) {
					prodID2 = prodID;
					groupOfa2 = gropuOfa;
					// 申購金額
					if (FundRule.isSingleOrMultiple(detail.getPurchaseDVO().getTRADE_SUB_TYPE())) {
						// 單筆/定期定額
						purchaseAmt2 = purchaseAmt;
					} else {
						// 定期不定額
						purchaseAmt2 = purchaseAmtL;
						purchaseAmtM2 = purchaseAmtM;
						purchaseAmtH2 = purchaseAmtH;
					}
					// 手續費
					if (StringUtils.equals("1", detail.getPurchaseDVO().getTRADE_SUB_TYPE())) {
						// 單筆
						feeRate2 = detail.getPurchaseDVO().getFEE_RATE();
						fee2 = detail.getPurchaseDVO().getFEE();
					} else {
						if (FundRule.isMultiple(detail.getPurchaseDVO().getTRADE_SUB_TYPE())) {
							// 定期定額
							feeRate2 = detail.getPurchaseDVO().getFEE_RATE();
							fee2 = detail.getPurchaseDVO().getFEE();
						} else {
							// 定期不定額
							feeRate2 = detail.getPurchaseDVO().getFEE_RATE_L();
							fee2 = detail.getPurchaseDVO().getFEE_L();
							feeRateM2 = detail.getPurchaseDVO().getFEE_RATE_M();
							feeM2 = detail.getPurchaseDVO().getFEE_M();
							feeRateH2 = detail.getPurchaseDVO().getFEE_RATE_H();
							feeH2 = detail.getPurchaseDVO().getFEE_H();
						}
					}

					chargeDate2_1 = chargeDate1;
					chargeDate2_2 = chargeDate2;
					chargeDate2_3 = chargeDate3;
					chargeDate2_4 = chargeDate4;
					chargeDate2_5 = chargeDate5;
					chargeDate2_6 = chargeDate6;
					bthCoupon2 = bthCoupon;
					isAutoCx2 = isAutoCx;
					isPl2 = isPl;
					takeProfitPerc2 = takeProfitPercStr;
					takeProfitSym2 = takeProfitSym;
					stopLossPerc2 = stopLossPercStr;
					stopLossSym2 = stopLossSym;
					bargainApplySeq2 = bargainApplySeq;
					prospectusType = detail.getPurchaseDVO().getPROSPECTUS_TYPE();
				}
				// 第三筆
				else if (idx == 2) {
					prodID3 = prodID;
					groupOfa3 = gropuOfa;
					// 申購金額
					if (FundRule.isSingleOrMultiple(detail.getPurchaseDVO().getTRADE_SUB_TYPE())) { // 單筆/定期定額
						purchaseAmt3 = purchaseAmt;
					} else { // 定期不定額
						purchaseAmt3 = purchaseAmtL;
						purchaseAmtM3 = purchaseAmtM;
						purchaseAmtH3 = purchaseAmtH;
					}
					// 手續費
					if (StringUtils.equals("1", detail.getPurchaseDVO().getTRADE_SUB_TYPE())) {
						// 單筆
						feeRate3 = detail.getPurchaseDVO().getFEE_RATE();
						fee3 = detail.getPurchaseDVO().getFEE();
					} else {
						if (FundRule.isMultiple(detail.getPurchaseDVO().getTRADE_SUB_TYPE())) {
							// 定期定額
							feeRate3 = detail.getPurchaseDVO().getFEE_RATE();
							fee3 = detail.getPurchaseDVO().getFEE();
						} else {
							// 定期不定額
							feeRate3 = detail.getPurchaseDVO().getFEE_RATE_L();
							fee3 = detail.getPurchaseDVO().getFEE_L();
							feeRateM3 = detail.getPurchaseDVO().getFEE_RATE_M();
							feeM3 = detail.getPurchaseDVO().getFEE_M();
							feeRateH3 = detail.getPurchaseDVO().getFEE_RATE_H();
							feeH3 = detail.getPurchaseDVO().getFEE_H();
						}
					}
					chargeDate3_1 = chargeDate1;
					chargeDate3_2 = chargeDate2;
					chargeDate3_3 = chargeDate3;
					chargeDate3_4 = chargeDate4;
					chargeDate3_5 = chargeDate5;
					chargeDate3_6 = chargeDate6;
					bthCoupon3 = bthCoupon;
					isAutoCx3 = isAutoCx;
					isPl3 = isPl;
					takeProfitPerc3 = takeProfitPercStr;
					takeProfitSym3 = takeProfitSym;
					stopLossPerc3 = stopLossPercStr;
					stopLossSym3 = stopLossSym;
					bargainApplySeq3 = bargainApplySeq;
					prospectusType = detail.getPurchaseDVO().getPROSPECTUS_TYPE();
				} else if (idx > 2) {
					break;
				}
				idx++;
			}

			// init util
			//金錢信託、特金交易確認電文都是打舊的
			Boolean isOBU = StringUtils.equals("Y", mainInfo.getTradeMainVO().getIS_OBU()) ? true : false;
			ESBUtilInputVO esbUtilInputVO;

			if (isOBU && !StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
				esbUtilInputVO = getTxInstance(ESB_TYPE, OBU_NF_VERIFY_PURCHASE);
			} else {
				esbUtilInputVO = getTxInstance(ESB_TYPE, NF_VERIFY_PURCHASE);
			}

			esbUtilInputVO.setModule(thisClaz + new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			NFBRN1InputVO txBodyVO = new NFBRN1InputVO();
			esbUtilInputVO.setNfbrn1InputVO(txBodyVO);
			txBodyVO.setTX_FLG("Y");

			//特金交易才給值
			if (!StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
				txBodyVO.setCUST_ID(custID);
				txBodyVO.setDEBIT_ID(debitID);

			} else {
				txBodyVO.setCUST_ID("99331241");
				txBodyVO.setDEBIT_ID("99331241");
			}
			txBodyVO.setBRANCH_NBR(branchNbr);
			txBodyVO.setTRUST_ACCT(trustAcct);
			txBodyVO.setCREDIT_ACCT(creditAcct);
			//			txBodyVO.setDEBIT_ID(debitID);
			txBodyVO.setDEBIT_ACCT(debitAcct);
			txBodyVO.setEMP_ID(empID);
			txBodyVO.setNARRATOR_ID(narratorID);
			txBodyVO.setBATCH_SEQ(batchSeq);
			txBodyVO.setTRUST_CURR_TYPE(trustCurrType);
			txBodyVO.setTRADE_SUB_TYPE(tradeSubType);
			txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate()));
			txBodyVO.setEFF_DATE(this.toChineseYearMMdd(effDate));
			txBodyVO.setPAY_TYPE(payType);
			txBodyVO.setCONFIRM(confirm);

			txBodyVO.setFEE_RATE_1(feeRate1 == null ? null : String.valueOf(this.decimalPadding(feeRate1, 3)));

			txBodyVO.setFEE_1(fee1 == null ? null : String.valueOf(this.decimalPadding(fee1, 2)));
			txBodyVO.setFEE_M_1(feeM1 == null ? null : String.valueOf(this.decimalPadding(feeM1, 2)));
			txBodyVO.setFEE_RATE_M_1(feeRateM1 == null ? null : String.valueOf(this.decimalPadding(feeRateM1, 3)));
			txBodyVO.setFEE_H_1(feeH1 == null ? null : String.valueOf(this.decimalPadding(feeH1, 2)));
			txBodyVO.setFEE_RATE_H_1(feeRateH1 == null ? null : String.valueOf(this.decimalPadding(feeRateH1, 3)));

			txBodyVO.setPROSPECTUS_TYPE(prospectusType); // 公開說明書交付方式
			txBodyVO.setPROD_ID_1(prodID1);

			txBodyVO.setPURCHASE_AMT_L_1(purchaseAmt1 == null ? "" : String.valueOf(purchaseAmt1));
			txBodyVO.setPURCHASE_AMT_M_1(purchaseAmtM1 == null ? "" : String.valueOf(purchaseAmtM1));
			txBodyVO.setPURCHASE_AMT_H_1(purchaseAmtH1 == null ? "" : String.valueOf(purchaseAmtH1));
			txBodyVO.setCHARGE_DATE_1_1(chargeDate1_1);
			txBodyVO.setCHARGE_DATE_2_1(chargeDate1_2);
			txBodyVO.setCHARGE_DATE_3_1(chargeDate1_3);
			txBodyVO.setCHARGE_DATE_4_1(chargeDate1_4);
			txBodyVO.setCHARGE_DATE_5_1(chargeDate1_5);
			txBodyVO.setCHARGE_DATE_6_1(chargeDate1_6);
			txBodyVO.setPROD_ID_2(prodID2);

			txBodyVO.setFEE_RATE_2(feeRate2 == null ? null : String.valueOf(this.decimalPadding(feeRate2, 3)));
			txBodyVO.setFEE_2(fee2 == null ? null : String.valueOf(this.decimalPadding(fee2, 2)));
			txBodyVO.setFEE_M_2(feeM2 == null ? null : String.valueOf(this.decimalPadding(feeM2, 2)));
			txBodyVO.setFEE_RATE_M_2(feeRateM2 == null ? null : String.valueOf(this.decimalPadding(feeRateM2, 3)));
			txBodyVO.setFEE_H_2(feeH2 == null ? null : String.valueOf(this.decimalPadding(feeH2, 2)));
			txBodyVO.setFEE_RATE_H_2(feeRateH2 == null ? null : String.valueOf(this.decimalPadding(feeRateH2, 3)));
			txBodyVO.setPURCHASE_AMT_L_2(purchaseAmt2 == null ? "" : String.valueOf(purchaseAmt2));
			txBodyVO.setPURCHASE_AMT_M_2(purchaseAmtM2 == null ? "" : String.valueOf(purchaseAmtM2));
			txBodyVO.setPURCHASE_AMT_H_2(purchaseAmtH2 == null ? "" : String.valueOf(purchaseAmtH2));
			txBodyVO.setCHARGE_DATE_1_2(chargeDate2_1);
			txBodyVO.setCHARGE_DATE_2_2(chargeDate2_2);
			txBodyVO.setCHARGE_DATE_3_2(chargeDate2_3);
			txBodyVO.setCHARGE_DATE_4_2(chargeDate2_4);
			txBodyVO.setCHARGE_DATE_5_2(chargeDate2_5);
			txBodyVO.setCHARGE_DATE_6_2(chargeDate2_6);
			txBodyVO.setPROD_ID_3(prodID3);

			txBodyVO.setFEE_RATE_3(feeRate3 == null ? null : String.valueOf(this.decimalPadding(feeRate3, 3)));
			txBodyVO.setFEE_3(fee3 == null ? null : String.valueOf(this.decimalPadding(fee3, 2)));
			txBodyVO.setFEE_M_3(feeM3 == null ? null : String.valueOf(this.decimalPadding(feeM3, 2)));
			txBodyVO.setFEE_RATE_M_3(feeRateM3 == null ? null : String.valueOf(this.decimalPadding(feeRateM3, 3)));
			txBodyVO.setFEE_H_3(feeH3 == null ? null : String.valueOf(this.decimalPadding(feeH3, 2)));
			txBodyVO.setFEE_RATE_H_3(feeRateH3 == null ? null : String.valueOf(this.decimalPadding(feeRateH3, 3)));
			txBodyVO.setPURCHASE_AMT_L_3(purchaseAmt3 == null ? "" : String.valueOf(purchaseAmt3));
			txBodyVO.setPURCHASE_AMT_M_3(purchaseAmtM3 == null ? "" : String.valueOf(purchaseAmtM3));
			txBodyVO.setPURCHASE_AMT_H_3(purchaseAmtH3 == null ? "" : String.valueOf(purchaseAmtH3));
			txBodyVO.setCHARGE_DATE_1_3(chargeDate3_1);
			txBodyVO.setCHARGE_DATE_2_3(chargeDate3_2);
			txBodyVO.setCHARGE_DATE_3_3(chargeDate3_3);
			txBodyVO.setCHARGE_DATE_4_3(chargeDate3_4);
			txBodyVO.setCHARGE_DATE_5_3(chargeDate3_5);
			txBodyVO.setCHARGE_DATE_6_3(chargeDate3_6);

			txBodyVO.setBTH_COUPON_1(bthCoupon1);
			txBodyVO.setBTH_COUPON_2(bthCoupon2);
			txBodyVO.setBTH_COUPON_3(bthCoupon3);
			//0000392: 業管之金錢信託中申購基金或海外債錄音序號不必上送給AS400
			if (StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "S")) {
				txBodyVO.setREC_SEQ(recSeq);
			}
			
			if("01".equals(tradeSubType)
					&& StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "S")) {			
				//#1695 貸轉投  特金單筆
				txBodyVO.setFLAG_NUMBER(mainInfo.getTradeMainVO().getFLAG_NUMBER());
			}

			txBodyVO.setIS_PL_1(isPl1);
			txBodyVO.setIS_PL_2(isPl2);
			txBodyVO.setIS_PL_3(isPl3);
			//金錢信託上送group_ifa邏輯
			if (StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
				if (checkInt(groupOfa1)) {
					txBodyVO.setGROUP_IFA_1(groupOfa1);
				}
				if (checkInt(groupOfa2)) {
					txBodyVO.setGROUP_IFA_2(groupOfa2);
				}
				if (checkInt(groupOfa3)) {
					txBodyVO.setGROUP_IFA_3(groupOfa3);
				}
			} else {
				txBodyVO.setGROUP_IFA_1(groupOfa1);
				txBodyVO.setGROUP_IFA_2(groupOfa2);
				txBodyVO.setGROUP_IFA_3(groupOfa3);
			}

			//金錢信託單筆不需送議價編號
			if (!(StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "M") && StringUtils.equals(tradeSubType, "01"))) { //小額交易要送假議價
				txBodyVO.setBARGAIN_APPLY_SEQ_1(bargainApplySeq1);
				txBodyVO.setBARGAIN_APPLY_SEQ_2(bargainApplySeq2);
				txBodyVO.setBARGAIN_APPLY_SEQ_3(bargainApplySeq3);
			}
			txBodyVO.setAUTO_CX_1(isAutoCx1);
			txBodyVO.setAUTO_CX_2(isAutoCx2);
			txBodyVO.setAUTO_CX_3(isAutoCx3);

			txBodyVO.setTAKE_PROFIT_PERC_1(takeProfitPerc1);
			txBodyVO.setTAKE_PROFIT_SYM_1(takeProfitSym1);
			txBodyVO.setSTOP_LOSS_PERC_1(stopLossPerc1);
			txBodyVO.setSTOP_LOSS_SYM_1(stopLossSym1);

			txBodyVO.setTAKE_PROFIT_PERC_2(takeProfitPerc2);
			txBodyVO.setTAKE_PROFIT_SYM_2(takeProfitSym2);
			txBodyVO.setSTOP_LOSS_PERC_2(stopLossPerc2);
			txBodyVO.setSTOP_LOSS_SYM_2(stopLossSym2);

			txBodyVO.setTAKE_PROFIT_PERC_3(takeProfitPerc3);
			txBodyVO.setTAKE_PROFIT_SYM_3(takeProfitSym3);
			txBodyVO.setSTOP_LOSS_PERC_3(stopLossPerc3);
			txBodyVO.setSTOP_LOSS_SYM_3(stopLossSym3);
			

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NFBRN1OutputVO nfbrn1OutputVO;
				if (isOBU && !StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
					nfbrn1OutputVO = esbUtilOutputVO.getAfbrn1OutputVO(); //OBU特金交易
				} else {
					nfbrn1OutputVO = esbUtilOutputVO.getNfbrn1OutputVO(); //特金交易
				}

				// 若電文有回錯誤，則Throw Exception
				throwError(nfbrn1OutputVO);

				// 將回傳下行電文資料儲存DB
				updateESBPurchaseNF(nfbrn1OutputVO, tradeSeq, detailInfo);
			}
		}

	}

	/**
	 * 將基金申購確認回傳下行電文資料儲存DB
	 *
	 * @param nfbrn1OutputVO
	 * @param tradeSeq
	 * @param details
	 */
	private void updateESBPurchaseNF(NFBRN1OutputVO nfbrn1OutputVO, String tradeSeq, List<DetailInfoBean> details) throws DAOException {
		dam = getDataAccessManager();

		int idx = 0;
		for (DetailInfoBean detail : details) {
			// 取得交易流水號
			BigDecimal seqNo = detail.getPurchaseDVO().getcomp_id().getSEQ_NO();

			// 查詢交易是否存在
			TBSOT_NF_PURCHASE_DPK pk = new TBSOT_NF_PURCHASE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(seqNo);

			TBSOT_NF_PURCHASE_DVO vo = (TBSOT_NF_PURCHASE_DVO) dam.findByPKey(TBSOT_NF_PURCHASE_DVO.TABLE_UID, pk);

			// 資料庫沒有該筆資料
			if (vo != null) {
				// 取得商品預設手續費
				BigDecimal defaultFeeRate = null;
				// BigDecimal defaultFeeRateL =
				// detail.getPurchaseDVO().getDEFAULT_FEE_RATE_L();
				if (FundRule.isSingleOrMultiple(vo.getTRADE_SUB_TYPE())) {
					defaultFeeRate = detail.getPurchaseDVO().getDEFAULT_FEE_RATE();
				} else {
					defaultFeeRate = detail.getPurchaseDVO().getDEFAULT_FEE_RATE_L();
				}
				BigDecimal defaultFeeRateM = detail.getPurchaseDVO().getDEFAULT_FEE_RATE_M();
				BigDecimal defaultFeeRateH = detail.getPurchaseDVO().getDEFAULT_FEE_RATE_H();

				// 交易日期類別
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				// 01.11 String TradeDateType =
				// (StringUtils.equals(sdf.format(this.effDate), sdf.format(new
				// Date()))) ? "1" : "2";

				BigDecimal fee = null;
				BigDecimal feeRate = null;
				BigDecimal feeDiscount = null;
				String groupOfa = null;
				BigDecimal feeM = null;
				BigDecimal feeRateM = null;
				BigDecimal feeDiscountM = null;
				BigDecimal feeH = null;
				BigDecimal feeRateH = null;
				BigDecimal feeDiscountH = null;
				boolean setIdx = false;
				// 第一筆資料
				if (idx == 0) {
					setIdx = true;
					fee = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_1(), 2);
					feeRate = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_1(), 3);
					feeDiscount = (defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
					groupOfa = nfbrn1OutputVO.getGROUP_OFA_1();
					feeM = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_M_1(), 2);
					feeRateM = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_M_1(), 3);
					feeDiscountM = (defaultFeeRateM != null && defaultFeeRateM.compareTo(BigDecimal.ZERO) != 0) ? feeRateM.divide(defaultFeeRateM, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
					feeH = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_H_1(), 2);
					feeRateH = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_H_1(), 3);
					feeDiscountH = (defaultFeeRateH != null && defaultFeeRateH.compareTo(BigDecimal.ZERO) != 0) ? feeRateH.divide(defaultFeeRateH, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
				}
				// 第二筆資料
				else if (idx == 1) {
					setIdx = true;
					fee = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_2(), 2);
					feeRate = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_2(), 3);
					feeDiscount = (defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
					groupOfa = nfbrn1OutputVO.getGROUP_OFA_1();
					feeM = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_M_2(), 2);
					feeRateM = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_M_2(), 3);
					feeDiscountM = (defaultFeeRateM != null && defaultFeeRateM.compareTo(BigDecimal.ZERO) != 0) ? feeRateM.divide(defaultFeeRateM, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
					feeH = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_H_2(), 2);
					feeRateH = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_H_2(), 3);
					feeDiscountH = (defaultFeeRateH != null && defaultFeeRateH.compareTo(BigDecimal.ZERO) != 0) ? feeRateH.divide(defaultFeeRateH, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;

				}
				// 第三筆資料
				else if (idx == 2) {
					setIdx = true;
					fee = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_3(), 2);
					feeRate = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_3(), 3);
					feeDiscount = (defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
					groupOfa = nfbrn1OutputVO.getGROUP_OFA_1();
					feeM = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_M_3(), 2);
					feeRateM = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_M_3(), 3);
					feeDiscountM = (defaultFeeRateM != null && defaultFeeRateM.compareTo(BigDecimal.ZERO) != 0) ? feeRateM.divide(defaultFeeRateM, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
					feeH = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_H_3(), 2);
					feeRateH = new EsbUtil().decimalPoint(nfbrn1OutputVO.getFEE_RATE_H_3(), 3);
					feeDiscountH = (defaultFeeRateH != null && defaultFeeRateH.compareTo(BigDecimal.ZERO) != 0) ? feeRateH.divide(defaultFeeRateH, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
				}
				if (setIdx) {
					if (FundRule.isSingleOrMultiple(vo.getTRADE_SUB_TYPE())) {
						// 單筆/定期定額
						vo.setFEE_RATE(feeRate);
						vo.setFEE(fee);
						vo.setFEE_DISCOUNT(feeDiscount);
					} else {
						vo.setFEE_L(fee);
						vo.setFEE_RATE_L(feeRate);
						vo.setFEE_DISCOUNT_L(feeDiscount);
						vo.setFEE_M(feeM);
						vo.setFEE_RATE_M(feeRateM);
						vo.setFEE_DISCOUNT_M(feeDiscountM);
						vo.setFEE_H(feeH);
						vo.setFEE_RATE_H(feeRateH);
						vo.setFEE_DISCOUNT_H(feeDiscountH);
					}

					vo.setGROUP_OFA(groupOfa);
					// 01.11 vo.setTRADE_DATE_TYPE(TradeDateType);
					// 01.11 vo.setTRADE_DATE(new
					// Timestamp(this.effDate.getTime()));
				}
				// 更新商品資訊
				dam.update(vo);
			}

			idx++;
		}
	}

	/**
	 * 依照批號取得明細資料
	 *
	 * @param batchSeq
	 * @return
	 * @throws JBranchException
	 */
	private List getConfirmESBPurchaseNFDetails(String tradeSeq, String batchSeq) throws JBranchException {
		String sql = new StringBuffer("select D.*  " + "  from TBSOT_NF_PURCHASE_D D " + " where D.TRADE_SEQ = :TRADE_SEQ  " + "   and D.BATCH_SEQ = :BATCH_SEQ " + " order by D.BATCH_NO ").toString();

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setObject("BATCH_SEQ", batchSeq);
		condition.setQueryString(sql);

		List<Map> list = dam.exeQuery(condition);

		List<DetailInfoBean> details = new ArrayList<DetailInfoBean>();
		if (CollectionUtils.isNotEmpty(list)) {
			DetailInfoBean bean = null;
			TBSOT_NF_PURCHASE_DVO purchaseDVO = null;
			TBSOT_TRADE_MAINVO tradeMainVO = null;
			TBORG_MEMBERVO memberVO = null;

			for (Map map : list) {
				// setting DetailInfoBean
				bean = new DetailInfoBean();
				purchaseDVO = new TBSOT_NF_PURCHASE_DVO();
				bean.setPurchaseDVO(purchaseDVO);
				memberVO = new TBORG_MEMBERVO();
				bean.setMemberVO(memberVO);

				purchaseDVO.setCreatetime((Timestamp) map.get("CREATETIME"));
				purchaseDVO.setTRADE_DATE((Timestamp) map.get("TRADE_DATE"));
				purchaseDVO.setTRADE_SUB_TYPE(CharUtils.toString((Character) map.get("TRADE_SUB_TYPE")));
				purchaseDVO.setTRUST_CURR_TYPE(CharUtils.toString((Character) map.get("TRUST_CURR_TYPE")));
				purchaseDVO.setTRUST_ACCT((String) map.get("TRUST_ACCT"));
				purchaseDVO.setCREDIT_ACCT((String) map.get("CREDIT_ACCT"));
				purchaseDVO.setDEBIT_ACCT((String) map.get("DEBIT_ACCT"));
				purchaseDVO.setBATCH_SEQ((String) map.get("BATCH_SEQ"));
				purchaseDVO.setModifier((String) map.get("MODIFIER"));
				purchaseDVO.setNARRATOR_ID((String) map.get("NARRATOR_ID"));
				purchaseDVO.setPROD_ID((String) map.get("PROD_ID"));
				purchaseDVO.setGROUP_OFA((String) map.get("GROUP_OFA"));
				purchaseDVO.setPURCHASE_AMT((BigDecimal) map.get("PURCHASE_AMT"));
				purchaseDVO.setPURCHASE_AMT_L((BigDecimal) map.get("PURCHASE_AMT_L"));
				purchaseDVO.setPURCHASE_AMT_M((BigDecimal) map.get("PURCHASE_AMT_M"));
				purchaseDVO.setPURCHASE_AMT_H((BigDecimal) map.get("PURCHASE_AMT_H"));
				purchaseDVO.setCHARGE_DATE_1((String) map.get("CHARGE_DATE_1"));
				purchaseDVO.setCHARGE_DATE_2((String) map.get("CHARGE_DATE_2"));
				purchaseDVO.setCHARGE_DATE_3((String) map.get("CHARGE_DATE_3"));
				purchaseDVO.setCHARGE_DATE_4((String) map.get("CHARGE_DATE_4"));
				purchaseDVO.setCHARGE_DATE_5((String) map.get("CHARGE_DATE_5"));
				purchaseDVO.setCHARGE_DATE_6((String) map.get("CHARGE_DATE_6"));
				purchaseDVO.setFEE_TYPE(CharUtils.toString((Character) map.get("FEE_TYPE")));
				purchaseDVO.setFEE((BigDecimal) map.get("FEE"));
				purchaseDVO.setFEE_L((BigDecimal) map.get("FEE_L"));
				purchaseDVO.setFEE_M((BigDecimal) map.get("FEE_M"));
				purchaseDVO.setFEE_H((BigDecimal) map.get("FEE_H"));
				purchaseDVO.setIS_AUTO_CX(CharUtils.toString((Character) map.get("IS_AUTO_CX")));
				purchaseDVO.setTAKE_PROFIT_PERC((BigDecimal) map.get("TAKE_PROFIT_PERC"));
				purchaseDVO.setSTOP_LOSS_PERC((BigDecimal) map.get("STOP_LOSS_PERC"));
				purchaseDVO.setBARGAIN_APPLY_SEQ((String) map.get("BARGAIN_APPLY_SEQ"));
				purchaseDVO.setcomp_id(new TBSOT_NF_PURCHASE_DPK((BigDecimal) map.get("SEQ_NO"), tradeSeq));
				purchaseDVO.setDEFAULT_FEE_RATE((BigDecimal) map.get("DEFAULT_FEE_RATE"));
				purchaseDVO.setDEFAULT_FEE_RATE_L((BigDecimal) map.get("DEFAULT_FEE_RATE_L"));
				purchaseDVO.setDEFAULT_FEE_RATE_M((BigDecimal) map.get("DEFAULT_FEE_RATE_M"));
				purchaseDVO.setDEFAULT_FEE_RATE_H((BigDecimal) map.get("DEFAULT_FEE_RATE_H"));
				purchaseDVO.setFEE_RATE((BigDecimal) map.get("FEE_RATE"));
				purchaseDVO.setFEE_RATE_L((BigDecimal) map.get("FEE_RATE_L"));
				purchaseDVO.setFEE_RATE_M((BigDecimal) map.get("FEE_RATE_M"));
				purchaseDVO.setFEE_RATE_H((BigDecimal) map.get("FEE_RATE_H"));
				purchaseDVO.setPROSPECTUS_TYPE(CharUtils.toString((Character) map.get("PROSPECTUS_TYPE"))); // 公開說明書交付方式
				purchaseDVO.setGROUP_OFA((String) map.get("GROUP_OFA"));
				details.add(bean);
			}
		}
		return details;
	}

	/**
	 * 基金申購確認取得批號資料
	 *
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	public List<String> selectConfirmESBPurchaseNFBatches(String tradeSeq) throws JBranchException {
		String sql = new StringBuffer(" select BATCH_SEQ " + "   from TBSOT_NF_PURCHASE_D " + "  where TRADE_SEQ = :TRADE_SEQ " + "  group by BATCH_SEQ ").toString();

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sql);

		List<Map> list = dam.exeQuery(condition);

		List<String> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map map : list) {
				result.add((String) map.get("BATCH_SEQ"));
			}
		}

		return result;
	}

	/**
	 * 基金申購確認取得主檔資料
	 *
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	private MainInfoBean selectConfirmESBPurchaseNF(String tradeSeq) throws JBranchException {
		String sql = null;

		sql = new StringBuffer("select * from TBSOT_TRADE_MAIN where TRADE_SEQ = :TRADE_SEQ ").toString();
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sql);

		List<Map> results = dam.exeQuery(condition);

		MainInfoBean main = new MainInfoBean();
		TBSOT_TRADE_MAINVO tradeMainVO = new TBSOT_TRADE_MAINVO();
		main.setTradeMainVO(tradeMainVO);

		if (CollectionUtils.isNotEmpty(results)) {
			Map result = results.get(0);
			tradeMainVO.setCUST_ID((String) result.get("CUST_ID"));
			tradeMainVO.setIS_OBU(CharUtils.toString((Character) result.get("IS_OBU")));
			tradeMainVO.setREC_SEQ((String) result.get("REC_SEQ"));
			tradeMainVO.setBRANCH_NBR((String) result.get("BRANCH_NBR"));
			tradeMainVO.setTRUST_TRADE_TYPE((String) result.get("TRUST_TRADE_TYPE"));
			tradeMainVO.setFLAG_NUMBER((String) result.get("FLAG_NUMBER"));
		}

		return main;
	}

	/**
	 * 網路快速下單 - 基金申購檢核 發送電文VN067N 需傳入參數：tradeSeq, seqNo
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void verifyESBWebPurchaseNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(verifyESBWebPurchaseNF(body));
	}

	/**
	 * 網路快速下單 - 基金申購檢核 發送電文VN067N
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBWebPurchaseNF(Object body) throws Exception {
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號
		// String seqNo = sot703InputVO.getSeqNo(); //明細檔交易流水號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("網路快速下單 下單交易序號未輸入");
		}

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'ESB.WEBID' ");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		String esbWebid = null; // txHead.HWSID 但是如果要模擬網銀快速下單 就必須送TBK_WEB001
		if (list.size() > 0) {
			esbWebid = list.get(0).get("PARAM_NAME").toString();
		}

		String sql = null;
		sql = new StringBuffer("select D.SEQ_NO from  TBSOT_NF_PURCHASE_D D where D.TRADE_SEQ = :TRADE_SEQ ").toString();

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sql);
		List<Map> results = dam.exeQuery(condition);

		List<String> seqNoList = new ArrayList<String>();
		if (results != null) {
			for (Map map : results) {
				seqNoList.add(map.get("SEQ_NO").toString());
			}
		}

		for (String seqNo : seqNoList) {
			// 由交易序號取得交易資料
			MainInfoBean mainVO = selectVerifyPurchaseNF(tradeSeq, seqNo);

			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NF_BANK_VERIFY_PURCHASE);
			esbUtilInputVO.setModule(thisClaz + new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			esbUtilInputVO.setTxHeadVO(txHead);
			txHead.setDefaultTxHead();
			txHead.setHWSID(esbWebid); // 但是如果要模擬網銀快速下單 就必須送TBK_WEB001

			SOT701InputVO sot701InputVO = new SOT701InputVO();
			sot701InputVO.setCustID(mainVO.getTradeMainVO().getCUST_ID());
			sot701InputVO.setTrustAcct(mainVO.getPurchaseDVO().getTRUST_ACCT());
			sot701InputVO.setDebitAcct(sot703InputVO.getDebitAcct());
			String curAcctName = sot701.getFC032659AcnoCode(sot701InputVO);// 查詢：網銀取得戶名電文
																			// 電文FC032659

			// body
			VN067NInputVO txBodyVO = new VN067NInputVO();
			esbUtilInputVO.setVn067nInputVO(txBodyVO);
			txBodyVO.setCurAcctName(curAcctName);// 戶名代號 "0001"
			txBodyVO.setAcctId16(mainVO.getPurchaseDVO().getDEBIT_ACCT()); // DEBIT_ACCT
																			// //
																			// 扣款帳號
			txBodyVO.setCustId(mainVO.getTradeMainVO().getCUST_ID()); // CUST_ID
																		// //
																		// 客戶ID
			txBodyVO.setTrustKind(mainVO.getPurchaseDVO().getTRUST_CURR_TYPE());// TRUST_CURR_TYPE
																				// //
																				// 信託業務別

			/*
			 * TRADE_SUB_TYPE== “1” ? “01” : “02” //
			 * 交易類別 單筆放 "01" // 小額放 "02"
			 * #397_特金基金相關模組_加入OBU邏輯
			 * OUB單筆申購放"24"
			 */
			if (StringUtils.equals("1", mainVO.getPurchaseDVO().getTRADE_SUB_TYPE()) && (StringUtils.equals("Y", mainVO.getTradeMainVO().getIS_OBU()))) {
				txBodyVO.setSubCode("24");
			} else if (StringUtils.equals("1", mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
				txBodyVO.setSubCode("01");
			} else {
				txBodyVO.setSubCode("02");
			}

			txBodyVO.setFundNO(mainVO.getPurchaseDVO().getPROD_ID());// PROD_ID
																		// //
																		// 基金代號
			txBodyVO.setConfirmCode("1"); // 確認碼
			txBodyVO.setPayDate01(mainVO.getPurchaseDVO().getCHARGE_DATE_1());// CHARGE_DATE_1
																				// //
																				// 扣款日期1
			txBodyVO.setPayDate02(mainVO.getPurchaseDVO().getCHARGE_DATE_2());// CHARGE_DATE_2
																				// //
																				// 扣款日期2
			txBodyVO.setPayDate03(mainVO.getPurchaseDVO().getCHARGE_DATE_3());// CHARGE_DATE_3
																				// //
																				// 扣款日期3
			txBodyVO.setPayDate04(mainVO.getPurchaseDVO().getCHARGE_DATE_4());// CHARGE_DATE_4
																				// //
																				// 扣款日期4
			txBodyVO.setPayDate05(mainVO.getPurchaseDVO().getCHARGE_DATE_5());// CHARGE_DATE_5
																				// //
																				// 扣款日期5
			txBodyVO.setPayDate06(mainVO.getPurchaseDVO().getCHARGE_DATE_6());// CHARGE_DATE_6
																				// //
																				// 扣款日期6
			txBodyVO.setChSource("WEB");// “WEB” // 通路來源

			// 申購金額
			if (FundRule.isSingleOrMultiple(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
				// 單筆/定期定額
				txBodyVO.setAmt13(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT(), 2)));
			} else {
				// 定期不定額
				txBodyVO.setAmt13(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_L(), 2)));
				txBodyVO.setPayAmt_M(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_M(), 2)));// PURCHASE_AMT_M //
																																	// 扣款金額中
				txBodyVO.setPayAmt_H(String.valueOf(new EsbUtil().decimalPadding(mainVO.getPurchaseDVO().getPURCHASE_AMT_H(), 2)));// PURCHASE_AMT_H //
																																	// 扣款金額高
			}

			if (FundRule.isMultipleN(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
				txBodyVO.setMark("Y");
			} else {
				txBodyVO.setMark("N");
			}

			txBodyVO.setContNum(mainVO.getPurchaseDVO().getBARGAIN_APPLY_SEQ());// TODO
																				// BARGAIN_APPLY_SEQ
																				// //
																				// 議價編號
																				// 小額可不可以議價待確認
			if (FundRule.isFundProject(mainVO.getPurchaseDVO().getTRADE_SUB_TYPE())) {
				txBodyVO.setTxType("A");/*
										 * If TRADE_SUB_TYPE = “4” or “5” //
										 * 交易類別 “Y” // End if
										 */
			}
			// TODO txBodyVO.set待確認(); // 團體優惠代碼

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				VN067NOutputVO vn067nOutputVO = esbUtilOutputVO.getVn067nOutputVO();
				// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
				if (!checkError(vn067nOutputVO) && !isBackEndProd(mainVO.getPurchaseDVO().getPROD_ID())) { // TODO 先避開ESB 電文BUG
					// 網路快速下單 - 基金申購檢核 發送電文VN067N 回傳下行電文FEE資料儲存DB
					// 後收型基金手續費固定為0，不需更新
					updateVerifyESBWebPurchaseNFDB(tradeSeq, new BigDecimal(seqNo), vn067nOutputVO, mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_L(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_M(), mainVO.getPurchaseDVO().getDEFAULT_FEE_RATE_H());
				}
			}
		}
		return sot703OutputVO;
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
	 * 網路快速下單 - 基金申購檢核 發送電文VN067N 回傳下行電文FEE資料儲存DB
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @param vn067nOutputVO
	 * @param defaultFeeRate
	 * @throws JBranchException
	 */
	private void updateVerifyESBWebPurchaseNFDB(String tradeSeq, BigDecimal seqNo, VN067NOutputVO vn067nOutputVO, BigDecimal defaultFeeRate, BigDecimal defaultFeeRateL, BigDecimal defaultFeeRateM, BigDecimal defaultFeeRateH) throws JBranchException {

		/*
		 * 將回傳下行電文資料儲存DB update TBSOT_NF_PURCHASE_D set FEE_RATE =
		 * rtnData.FeeRate FEE = rtnData.Fee FEE_DISCOUNT = (rtnData.FeeRate /
		 * detailRS.DEFAULT_FEE_RATE) x 10 GROUP_OFA = rtnData.BeneCode FEE_L =
		 * rtnData.Fee --小數二位 FEE_RATE_L = rtnData.Fee_Rate --小數五位
		 * FEE_DISCOUNT_L = (rtnData.FEE_RATE / detailRS.DEFAULT_FEE_RATE) x 10
		 * FEE_M = rtnData.Fee_M --小數二位 FEE_RATE_M = rtnData. FeeRate_M --小數五位
		 * FEE_DISCOUNT_M = (rtnData. FeeRate_M / detailRS.DEFAULT_FEE_RATE) x
		 * 10 FEE_H = rtnData.Fee_H --小數二位 FEE_RATE_H = rtnData. FeeRate_H
		 * --小數五位 FEE_DISCOUNT_H = (rtnData.FeeRate_H /
		 * detailRS.DEFAULT_FEE_RATE) x 10 where TRADE_SEQ = inputVO.tradeSeq
		 * and SEQ_NO = inputVO.seqNo
		 */

		dam = getDataAccessManager();

		// insert into DB
		TBSOT_NF_PURCHASE_DPK pk = new TBSOT_NF_PURCHASE_DPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(seqNo);

		// search data
		TBSOT_NF_PURCHASE_DVO vo = (TBSOT_NF_PURCHASE_DVO) dam.findByPKey(TBSOT_NF_PURCHASE_DVO.TABLE_UID, pk);

		BigDecimal zero = BigDecimal.ZERO;

		BigDecimal feeRate = null;
		BigDecimal fee = null;
		BigDecimal feeDiscount = zero;

		BigDecimal feeL = null;
		BigDecimal feeRateL = null;
		BigDecimal feeDiscountL = zero;
		BigDecimal feeM = null;
		BigDecimal feeRateM = null;
		BigDecimal feeDiscountM = zero;
		BigDecimal feeH = null;
		BigDecimal feeRateH = null;
		BigDecimal feeDiscountH = zero;

		if (FundRule.isSingleOrMultiple(vo.getTRADE_SUB_TYPE())) {
			// 單筆/定期定額
			feeRate = new EsbUtil().decimalPoint(vn067nOutputVO.getFeeRate(), 3);
			fee = new EsbUtil().decimalPoint(vn067nOutputVO.getFee(), 2);
			feeDiscount = zero;
		} else {
			feeL = new EsbUtil().decimalPoint(vn067nOutputVO.getFee(), 2);
			feeRateL = new EsbUtil().decimalPoint(vn067nOutputVO.getFeeRate(), 3);
			feeDiscountL = zero;
			feeM = new EsbUtil().decimalPoint(vn067nOutputVO.getFee_M(), 2);
			feeRateM = new EsbUtil().decimalPoint(vn067nOutputVO.getFeeRate_M(), 3);
			feeDiscountM = zero;
			feeH = new EsbUtil().decimalPoint(vn067nOutputVO.getFee_H(), 2);
			feeRateH = new EsbUtil().decimalPoint(vn067nOutputVO.getFeeRate_H(), 3);
			feeDiscountH = zero;
		}

		// @Deprecated use this.effDate String tradeDateType =
		// (StringUtils.equals(sdf.format(this.effDate), sdf.format(new
		// Date()))) ? "1" : "2";
		// @Deprecated use this.effDate Date tradeDate = this.effDate;

		BigDecimal number10 = BigDecimal.TEN;
		// 運算欄位
		if (defaultFeeRate != null) {
			feeDiscount = (feeRate != null && defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(number10) : null;
		}
		if (defaultFeeRateL != null) {
			feeDiscountL = (feeRateL != null && defaultFeeRateL != null && defaultFeeRateL.compareTo(BigDecimal.ZERO) != 0) ? feeRateL.divide(defaultFeeRateL, 3, BigDecimal.ROUND_HALF_UP).multiply(number10) : null;
			feeDiscountM = (feeRateM != null && defaultFeeRateM != null && defaultFeeRateM.compareTo(BigDecimal.ZERO) != 0) ? feeRateM.divide(defaultFeeRateM, 3, BigDecimal.ROUND_HALF_UP).multiply(number10) : null;
			feeDiscountH = (feeRateH != null && defaultFeeRateH != null && defaultFeeRateH.compareTo(BigDecimal.ZERO) != 0) ? feeRateH.divide(defaultFeeRateH, 3, BigDecimal.ROUND_HALF_UP).multiply(number10) : null;
		}

		/*
		 * feeRate 2.50 defaultFeeRate 3 2.50/3 *10 =8.333333333
		 */

		if (FundRule.isSingleOrMultiple(vo.getTRADE_SUB_TYPE())) {
			// 單筆/定期定額
			if (!StringUtils.equals("A", vo.getFEE_TYPE())) {
				// 若為申請議價則先不需要更新；因為主機尚未完成此筆議價授權，只會回最優手續費
				vo.setFEE(fee);
				vo.setFEE_RATE(feeRate);
				vo.setFEE_DISCOUNT(feeDiscount);
			}
		} else {
			vo.setFEE_L(feeL);
			vo.setFEE_RATE_L(feeRateL);
			vo.setFEE_DISCOUNT_L(feeDiscountL);
			vo.setFEE_M(feeM);
			vo.setFEE_RATE_M(feeRateM);
			vo.setFEE_DISCOUNT_M(feeDiscountM);
			vo.setFEE_H(feeH);
			vo.setFEE_RATE_H(feeRateH);
			vo.setFEE_DISCOUNT_H(feeDiscountH);
		}

		vo.setGROUP_OFA(vo.getGROUP_OFA());
		// @Deprecated use this.effDate vo.setTRADE_DATE_TYPE(tradeDateType);
		// @Deprecated use this.effDate vo.setTRADE_DATE(new
		// Timestamp(tradeDate.getTime()));

		// update data
		dam.update(vo);
	}

	/**
	 * 網路快速下單 – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息 發送電文EBPMN 需傳入參數：tradeSeq, seqNo
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void sendESBWebPurchaseNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(sendESBWebPurchaseNF(body));
	}

	/**
	 * 網路快速下單 – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息 發送電文EBPMN 需傳入參數：tradeSeq, seqNo
	 *
	 * @param body
	 * @throws Exception
	 */
	public SOT703OutputVO sendESBWebPurchaseNF(Object body) throws Exception {
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號
		String seqNo = sot703InputVO.getSeqNo(); // 明細檔交易流水號
		String notifyType = sot703InputVO.getNotifyType(); //通知類型 單筆:02 小額:03

		if (StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號未輸入");
		}

		StringBuffer sql = new StringBuffer("select distinct M.CUST_ID, D.BATCH_SEQ ");
		sql.append(" from TBSOT_TRADE_MAIN M ");
		sql.append(" inner join TBSOT_NF_PURCHASE_D D on D.TRADE_SEQ = M.TRADE_SEQ ");
		sql.append(" WHERE D.TRADE_SEQ = :TRADE_SEQ ");

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sql.toString());
		List<Map> results = dam.exeQuery(condition);

		for (Map detail : results) {

			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NF_BANK_SEND_WEBPURCHASENF);
			esbUtilInputVO.setModule(thisClaz + new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			EBPMNInputVO txBodyVO = new EBPMNInputVO();
			esbUtilInputVO.setEbpmnInputVO(txBodyVO);
			txBodyVO.setNotifyType(notifyType); // 通知類型 單筆:02 小額:03
			txBodyVO.setCustId(detail.get("CUST_ID").toString()); // 客戶ID
			txBodyVO.setApplySeq(detail.get("BATCH_SEQ").toString()); // 申購批號
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {

				EBPMNOutputVO ebpmnOutputVO = esbUtilOutputVO.getEbpmnOutputVO();

				// 回傳錯誤訊息
				Boolean isErr = checkError(ebpmnOutputVO);
			}
		}
		
		//#2060_AI BANK，電文EBPMN2
		for (Map detail : results) {

			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, AI_BANK_SEND_PURCHASE);
			esbUtilInputVO.setModule(thisClaz + new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			EBPMN2InputVO txBodyVO = new EBPMN2InputVO();
			esbUtilInputVO.setEbpmn2InputVO(txBodyVO);
			txBodyVO.setNotifyType(notifyType); // 通知類型 單筆:02 小額:03
			txBodyVO.setCustId(detail.get("CUST_ID").toString()); // 客戶ID
			txBodyVO.setApplySeq(detail.get("BATCH_SEQ").toString()); // 申購批號
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				EBPMN2OutputVO ebpmn2OutputVO = esbUtilOutputVO.getEbpmn2OutputVO();
				// 回傳錯誤訊息
				Boolean isErr = checkError(ebpmn2OutputVO);
			}
		}
		return sot703OutputVO;
	}

	/**
	 * 基金贖回檢核 for js client
	 *
	 * 使用電文: NFBRN3
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void verifyESBRedeemNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBRedeemNF(body));
	}

	/**
	 * 基金贖回檢核
	 *
	 * 使用電文: NFBRN3
	 *
	 * @param body
	 * @throws Exception
	 */
	public SOT703OutputVO verifyESBRedeemNF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號
		String seqNo = sot703InputVO.getSeqNo(); // 明細檔交易流水號

		if (StringUtils.isBlank(tradeSeq) && StringUtils.isBlank(seqNo)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 取得主檔資料
		MainInfoBean mainInfo = selectVerifyESBRedeemNF(tradeSeq, seqNo);

		// 電文需做邏輯處理欄位
		String isOBU = mainInfo.getTradeMainVO().getIS_OBU();
		String isRePurchase = mainInfo.getRedeemDVO().getIS_RE_PURCHASE();
		BigDecimal takeProfitPerc = mainInfo.getRedeemDVO().getTAKE_PROFIT_PERC();
		BigDecimal stopLossPerc = mainInfo.getRedeemDVO().getSTOP_LOSS_PERC();
		BigDecimal defaultFeeRate = mainInfo.getRedeemDVO().getDEFAULT_FEE_RATE();

		// init util
		Boolean checkOBU = StringUtils.equals("Y", isOBU) ? true : false;
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, checkOBU ? OBU_NF_VERIFY_REDEEM : NF_VERIFY_REDEEM);

		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		NFBRN3InputVO txBodyVO = new NFBRN3InputVO();
		esbUtilInputVO.setNfbrn3InputVO(txBodyVO);
		txBodyVO.setCheckCode("1"); // 確認碼
		//		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainInfo.getRedeemDVO()
		//				.getCreatetime())); // 申請日期
		txBodyVO.setApplyDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setEffDate(this.toChineseYearMMdd(mainInfo.getRedeemDVO().getTRADE_DATE())); // 生效日

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainInfo.getTradeMainVO().getBRANCH_NBR();
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

		txBodyVO.setKeyinId(mainInfo.getRedeemDVO().getModifier()); // 建機人員
		//#0105金錢信託需求
		if (StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
			txBodyVO.setTrustId("99331241"); // 金錢信託委託人統編固定為99331241
		} else {
			txBodyVO.setTrustId(mainInfo.getTradeMainVO().getCUST_ID()); // 委託人統編
		}

		txBodyVO.setType((StringUtils.equals("Y", isOBU)) ? "2" : "1"); // 類別
		txBodyVO.setEviNum_1(mainInfo.getRedeemDVO().getRDM_CERTIFICATE_ID().trim()); // 憑證編號1
		txBodyVO.setFundNo_1(mainInfo.getRedeemDVO().getRDM_PROD_ID()); // 基金代碼1
		txBodyVO.setBackType_1(mainInfo.getRedeemDVO().getREDEEM_TYPE()); // 贖回方式1
		// txBodyVO.setRcvAcctId_1(mainInfo.getRedeemDVO().getTRUST_ACCT().trim());
		// //入帳帳號1
		if (StringUtils.equals(mainInfo.getTradeMainVO().getTRUST_TRADE_TYPE(), "M")) {
			txBodyVO.setRcvAcctId_1(mainInfo.getRedeemDVO().getCREDIT_ACCT().trim()); // 金錢信託用信託帳號
		} else {
			txBodyVO.setRcvAcctId_1(mainInfo.getRedeemDVO().getCREDIT_ACCT().trim()); // 特金用入帳帳號1
		}

		txBodyVO.setUntNum_1(String.valueOf(new EsbUtil().decimalPadding(mainInfo.getRedeemDVO().getRDM_UNIT(), 4))); // 原單位數1
		txBodyVO.setBackUntNum_1(String.valueOf(new EsbUtil().decimalPadding(mainInfo.getRedeemDVO().getUNIT_NUM(), 4))); // 贖回單位數1
																															// //贖回單位數1//原單位數1
		// 是否贖扣1
		if (StringUtils.equals("Y", mainInfo.getRedeemDVO().getIS_END_CERTIFICATE())) {
			txBodyVO.setBackFlag_1("N");
		} else if (StringUtils.equals("N", mainInfo.getRedeemDVO().getIS_END_CERTIFICATE())) {
			txBodyVO.setBackFlag_1("Y");
		} else {
			txBodyVO.setBackFlag_1(null);
		}
		txBodyVO.setBuyFlag_1((StringUtils.equals("Y", isRePurchase)) ? "Y" : ""); // 贖回再申購1
		txBodyVO.setReFundNo_1(mainInfo.getRedeemDVO().getPCH_PROD_ID()); // 再申購基金1

		if (StringUtils.equals("Y", isRePurchase)) {
			txBodyVO.setCreditAcct_1(mainInfo.getRedeemDVO().getCREDIT_ACCT()); // 收益入帳帳號1
		}

		txBodyVO.setFeeRate_1(String.valueOf(new EsbUtil().decimalPadding(mainInfo.getRedeemDVO().getFEE_RATE(), 2))); // 手續費率1
		txBodyVO.setNarratorId_1(mainInfo.getRedeemDVO().getNARRATOR_ID()); // 推薦人1
		txBodyVO.setIsPL_1((takeProfitPerc != null || stopLossPerc != null) ? "Y" : "N"); // 是否停損停利1
		txBodyVO.setTakeProfitPerc_1(takeProfitPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) takeProfitPerc).abs(), 2))); // 停利1
		txBodyVO.setTakeProfitSym_1((takeProfitPerc != null && takeProfitPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-"); // 停利符號1
		txBodyVO.setStopLossPerc_1(stopLossPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) stopLossPerc).abs(), 2))); // 停損1
		txBodyVO.setStopLossSym_1((stopLossPerc != null && stopLossPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-"); // 停損符號1

		if (!StringUtils.equals("A", mainInfo.getRedeemDVO().getFEE_TYPE())) {
			// 申請議價時，檢核電文不須送議價編號，因為在主機中此筆議價編號還沒有鍵機；要等到下一步時才會鍵機
			txBodyVO.setBargainApplySeq_1(mainInfo.getRedeemDVO().getBARGAIN_APPLY_SEQ()); // 議價編號1
		}

		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN3OutputVO nfbrn3OutputVO = checkOBU ? esbUtilOutputVO.getAfbrn3OutputVO() : esbUtilOutputVO.getNfbrn3OutputVO();

			sot703OutputVO.setShort_1(nfbrn3OutputVO.getShort_1());
			sot703OutputVO.setShort_2(nfbrn3OutputVO.getShort_2());
			sot703OutputVO.setShort_3(nfbrn3OutputVO.getShort_3());

			// 回傳錯誤訊息
			Boolean isErr = checkError(nfbrn3OutputVO);

			// 將回傳下行電文資料儲存DB
			if (!isErr) {
				verifyESBRedeemNFUpdate(nfbrn3OutputVO, tradeSeq, seqNo, defaultFeeRate, mainInfo.getRedeemDVO().getIS_RE_PURCHASE(), mainInfo.getRedeemDVO().getFEE_TYPE());
			}
		}

		return sot703OutputVO;
	}

	/**
	 * 基金贖回檢核下行電文資料儲存DB
	 *
	 * @param nfbrn3OutputVO
	 * @param tradeSeq
	 * @param seqNo
	 * @param defaultFeeRate
	 * @throws DAOException
	 */
	private void verifyESBRedeemNFUpdate(NFBRN3OutputVO nfbrn3OutputVO, String tradeSeq, String seqNo, BigDecimal defaultFeeRate, String IS_RE_PURCHASE, String FEE_TYPE) throws DAOException {
		dam = getDataAccessManager();

		TBSOT_NF_REDEEM_DPK pk = new TBSOT_NF_REDEEM_DPK(new BigDecimal(seqNo), tradeSeq);
		TBSOT_NF_REDEEM_DVO vo = (TBSOT_NF_REDEEM_DVO) dam.findByPKey(TBSOT_NF_REDEEM_DVO.TABLE_UID, pk);

		BigDecimal feeRate = new EsbUtil().decimalPoint(nfbrn3OutputVO.getFeeRate_1(), 3);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		vo.setSHORT_TYPE(nfbrn3OutputVO.getShort_1());
		vo.setPRESENT_VAL(new EsbUtil().decimalPoint((nfbrn3OutputVO.getRedeemAmt_1()), 2));

		vo.setFEE_DISCOUNT(IS_RE_PURCHASE.equals("Y") ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null);
		if (StringUtils.equals("Y", IS_RE_PURCHASE) && !StringUtils.equals("A", FEE_TYPE)) { // 贖回再申購才會有RedeemAmt
			vo.setFEE(IS_RE_PURCHASE.equals("Y") ? feeRate.multiply(new EsbUtil().decimalPoint((nfbrn3OutputVO.getRedeemAmt_1()), 2)) : null);
			vo.setFEE_RATE(IS_RE_PURCHASE.equals("Y") ? feeRate : null);
		}
		vo.setGROUP_OFA(nfbrn3OutputVO.getGroupOfa_1());

		dam.update(vo);
	}

	/**
	 * 基金贖回檢核取得主檔資料
	 *
	 * @param tradeSeq
	 * @param seqNo
	 * @return
	 * @throws JBranchException
	 */
	private MainInfoBean selectVerifyESBRedeemNF(String tradeSeq, String seqNo) throws JBranchException {
		String sql = new String("select M.CUST_ID, " + "       M.IS_OBU, " + "       M.BRANCH_NBR, " + "       M.TRUST_TRADE_TYPE, " + "       D.* " + "  from TBSOT_TRADE_MAIN M " + "  inner join TBSOT_NF_REDEEM_D D on D.TRADE_SEQ = M.TRADE_SEQ " + " where M.TRADE_SEQ = :TRADE_SEQ " + "   and D.SEQ_NO = :SEQ_NO ");

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setObject("SEQ_NO", seqNo);
		condition.setQueryString(sql);

		List<Map> list = dam.exeQuery(condition);

		MainInfoBean mainVO = new MainInfoBean();

		if (CollectionUtils.isNotEmpty(list)) {
			TBORG_MEMBERVO memberVO = new TBORG_MEMBERVO();
			mainVO.setMemberVO(memberVO);

			TBSOT_NF_REDEEM_DVO redeemVO = new TBSOT_NF_REDEEM_DVO();
			mainVO.setRedeemDVO(redeemVO);

			TBSOT_TRADE_MAINVO tradeMainVO = new TBSOT_TRADE_MAINVO();
			mainVO.setTradeMainVO(tradeMainVO);

			redeemVO.setCreatetime((Timestamp) list.get(0).get("CREATETIME"));
			redeemVO.setModifier((String) list.get(0).get("MODIFIER"));
			tradeMainVO.setCUST_ID((String) list.get(0).get("CUST_ID"));
			tradeMainVO.setIS_OBU(CharUtils.toString((Character) list.get(0).get("IS_OBU")));
			tradeMainVO.setBRANCH_NBR((String) list.get(0).get("BRANCH_NBR"));
			tradeMainVO.setTRUST_TRADE_TYPE((String) list.get(0).get("TRUST_TRADE_TYPE"));
			redeemVO.setRDM_CERTIFICATE_ID((String) list.get(0).get("RDM_CERTIFICATE_ID"));
			redeemVO.setRDM_PROD_ID((String) list.get(0).get("RDM_PROD_ID"));
			redeemVO.setREDEEM_TYPE(CharUtils.toString((Character) list.get(0).get("REDEEM_TYPE")));
			redeemVO.setCREDIT_ACCT((String) list.get(0).get("CREDIT_ACCT"));
			redeemVO.setRDM_UNIT((BigDecimal) list.get(0).get("RDM_UNIT"));
			redeemVO.setUNIT_NUM((BigDecimal) list.get(0).get("UNIT_NUM"));
			redeemVO.setRDM_TRUST_CURR((String) list.get(0).get("RDM_TRUST_CURR"));
			redeemVO.setPRESENT_VAL((BigDecimal) list.get(0).get("PRESENT_VAL"));
			redeemVO.setIS_RE_PURCHASE(CharUtils.toString((Character) list.get(0).get("IS_RE_PURCHASE")));
			redeemVO.setPCH_PROD_ID((String) list.get(0).get("PCH_PROD_ID"));
			redeemVO.setFEE_TYPE(list.get(0).get("FEE_TYPE").toString());
			redeemVO.setFEE_RATE((BigDecimal) list.get(0).get("FEE_RATE"));
			redeemVO.setNARRATOR_ID((String) list.get(0).get("NARRATOR_ID"));
			redeemVO.setTAKE_PROFIT_PERC((BigDecimal) list.get(0).get("TAKE_PROFIT_PERC"));
			redeemVO.setSTOP_LOSS_PERC((BigDecimal) list.get(0).get("STOP_LOSS_PERC"));
			redeemVO.setBARGAIN_APPLY_SEQ((String) list.get(0).get("BARGAIN_APPLY_SEQ"));
			redeemVO.setDEFAULT_FEE_RATE((BigDecimal) list.get(0).get("DEFAULT_FEE_RATE"));
			redeemVO.setIS_END_CERTIFICATE(CharUtils.toString(((Character) list.get(0).get("IS_END_CERTIFICATE"))));
			redeemVO.setTRADE_DATE((Timestamp) list.get(0).get("TRADE_DATE"));
			redeemVO.setTRUST_ACCT((String) list.get(0).get("TRUST_ACCT"));
		}
		return mainVO;
	}

	/**
	 * Created by Moron on 2016/10/20.
	 */
	/**
	 * 基金贖回確認 for js client
	 *
	 * 使用電文: NFBRN3
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void confirmESBRedeemNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.confirmESBRedeemNF(body));
	}

	/**
	 * 基金贖回確認
	 *
	 * 使用電文: NFBRN3
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO confirmESBRedeemNF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號未輸入");
		}

		// 取得主檔資料
		MainInfoBean mainVO = selectConfirmESBRedeemNF(tradeSeq);

		// 取得批號資料
		List<String> batchInfo = selectConfirmESBRedeemNFBatches(tradeSeq);

		// 依批號分批發送電文
		List<DetailInfoBean> detailInfo = null;
		for (String batchSeq : batchInfo) {
			// 依照批號取得明細資料
			detailInfo = getConfirmESBRedeemNFDetails(tradeSeq, batchSeq);

			// 使用欄位
			String checkCode = "2";
			Date applyDate = null;
			Date effDate = null;
			String keyinNo = null;

			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNo = mainVO.getTradeMainVO().getBRANCH_NBR();
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
					branchNo = (String) loginBreach.get(0).get("BRANCH_NBR");
				} else {
					throw new APException("人員無有效分行"); //顯示錯誤訊息
				}
			} else if (StringUtils.equals(branchNo, branchChgMap.get("BS").toString())) {
				branchNo = branchChgMap.get("DEFAULT").toString();
			}
			
			String keyinId = null;
			//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
			String trustId = mainVO.getTradeMainVO().getTRUST_TRADE_TYPE() != null && mainVO.getTradeMainVO().getTRUST_TRADE_TYPE().equals("M") ? "99331241" : mainVO.getTradeMainVO().getCUST_ID();
			String type = StringUtils.equals(mainVO.getTradeMainVO().getIS_OBU(), "Y") ? "2" : "1";
			String eviNum_1 = null;
			String fundNo_1 = null;
			String backType_1 = null;
			String rcvAcctId_1 = null;
			BigDecimal untNum_1 = null;
			BigDecimal backUntNum_1 = null;
			String backFlag_1 = null;
			// Date ApplyDate_1
			String curCode_1 = null;
			BigDecimal backAmt_1 = null;
			String buyFlag_1 = null;
			String reFundNo_1 = null;
			String creditAcct_1 = null;
			BigDecimal feeRate_1 = null;
			String narratorId_1 = null;
			String eviNum_2 = null;
			String fundNo_2 = null;
			String backType_2 = null;
			String rcvAcctId_2 = null;
			BigDecimal untNum_2 = new BigDecimal("0");
			BigDecimal backUntNum_2 = new BigDecimal("0");
			String backFlag_2 = null;
			// Date ApplyDate_2
			String curCode_2 = null;
			BigDecimal backAmt_2 = null;
			String buyFlag_2 = null;
			String reFundNo_2 = null;
			String creditAcct_2 = null;
			BigDecimal feeRate_2 = new BigDecimal("0");
			String narratorId_2 = null;
			String eviNum_3 = null;
			String fundNo_3 = null;
			String backType_3 = null;
			String rcvAcctId_3 = null;
			BigDecimal untNum_3 = new BigDecimal("0");
			BigDecimal backUntNum_3 = new BigDecimal("0");
			String backFlag_3 = null;
			// Date ApplyDate_3
			String curCode_3 = null;
			BigDecimal backAmt_3 = null;
			String buyFlag_3 = null;
			String reFundNo_3 = null;
			String creditAcct_3 = null;
			BigDecimal feeRate_3 = new BigDecimal("0");
			String narratorId_3 = null;
			String recSeq = mainVO.getTradeMainVO().getREC_SEQ();
			String isPL_1 = null;
			String takeProfitPerc_1 = null;
			String takeProfitSym_1 = null;
			String stopLossPerc_1 = null;
			String stopLossSym_1 = null;
			String isPL_2 = null;
			String takeProfitPerc_2 = null;
			String takeProfitSym_2 = null;
			String stopLossPerc_2 = null;
			String stopLossSym_2 = null;
			String isPL_3 = null;
			String takeProfitPerc_3 = null;
			String takeProfitSym_3 = null;
			String stopLossPerc_3 = null;
			String stopLossSym_3 = null;
			String bargainApplySeq_1 = null;
			String bargainApplySeq_2 = null;
			String bargainApplySeq_3 = null;

			// 讀取電文批號資料 (至多三筆)
			DetailInfoBean d1stBatch = detailInfo.get(0);
			DetailInfoBean d2ndBatch = detailInfo.size() >= 2 ? detailInfo.get(1) : null;
			DetailInfoBean d3rdBatch = detailInfo.size() >= 3 ? detailInfo.get(2) : null;

			applyDate = d1stBatch.getRedeemDVO().getCreatetime();
			effDate = d1stBatch.getRedeemDVO().getTRADE_DATE();
			keyinNo = d1stBatch.getRedeemDVO().getBATCH_SEQ();
			branchNo = mainVO.getTradeMainVO().getBRANCH_NBR();
			keyinId = d1stBatch.getRedeemDVO().getModifier();

			eviNum_1 = d1stBatch.getRedeemDVO().getRDM_CERTIFICATE_ID();
			fundNo_1 = d1stBatch.getRedeemDVO().getRDM_PROD_ID();
			backType_1 = d1stBatch.getRedeemDVO().getREDEEM_TYPE();
			rcvAcctId_1 = d1stBatch.getRedeemDVO().getCREDIT_ACCT();
			untNum_1 = new EsbUtil().decimalPadding(d1stBatch.getRedeemDVO().getRDM_UNIT(), 4);
			backUntNum_1 = new EsbUtil().decimalPadding(d1stBatch.getRedeemDVO().getUNIT_NUM(), 4);
			if (StringUtils.equals(d1stBatch.getRedeemDVO().getIS_END_CERTIFICATE(), "Y")) {
				backFlag_1 = "N";
			} else if (StringUtils.equals(d1stBatch.getRedeemDVO().getIS_END_CERTIFICATE(), "N")) {
				backFlag_1 = "Y";
			}

			// ApplyDate_1
			curCode_1 = d1stBatch.getRedeemDVO().getRDM_TRUST_CURR();
			backAmt_1 = d1stBatch.getRedeemDVO().getPRESENT_VAL();
			buyFlag_1 = StringUtils.equals("Y", d1stBatch.getRedeemDVO().getIS_RE_PURCHASE()) ? "Y" : "";
			reFundNo_1 = d1stBatch.getRedeemDVO().getPCH_PROD_ID();
			creditAcct_1 = d1stBatch.getRedeemDVO().getCREDIT_ACCT();
			feeRate_1 = new EsbUtil().decimalPadding(d1stBatch.getRedeemDVO().getFEE_RATE(), 3);
			narratorId_1 = d1stBatch.getRedeemDVO().getNARRATOR_ID();

			BigDecimal takeProfitPerc = d1stBatch.getRedeemDVO().getTAKE_PROFIT_PERC();
			BigDecimal stopLossPerc = d1stBatch.getRedeemDVO().getSTOP_LOSS_PERC();

			isPL_1 = (takeProfitPerc != null || stopLossPerc != null) ? "Y" : "N";
			takeProfitPerc_1 = takeProfitPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) takeProfitPerc).abs(), 2));
			takeProfitSym_1 = (takeProfitPerc != null && takeProfitPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-";
			stopLossPerc_1 = stopLossPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) stopLossPerc).abs(), 2));
			stopLossSym_1 = (stopLossPerc != null && stopLossPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-";

			bargainApplySeq_1 = d1stBatch.getRedeemDVO().getBARGAIN_APPLY_SEQ();

			if (d2ndBatch != null) {
				eviNum_2 = d2ndBatch.getRedeemDVO().getRDM_CERTIFICATE_ID();
				fundNo_2 = d2ndBatch.getRedeemDVO().getRDM_PROD_ID();
				backType_2 = d2ndBatch.getRedeemDVO().getREDEEM_TYPE();
				rcvAcctId_2 = d2ndBatch.getRedeemDVO().getCREDIT_ACCT();
				untNum_2 = new EsbUtil().decimalPadding(d2ndBatch.getRedeemDVO().getRDM_UNIT(), 4);
				backUntNum_2 = new EsbUtil().decimalPadding(d2ndBatch.getRedeemDVO().getUNIT_NUM(), 4);
				if (StringUtils.equals(d2ndBatch.getRedeemDVO().getIS_END_CERTIFICATE(), "Y")) {
					backFlag_2 = "N";
				} else if (StringUtils.equals(d2ndBatch.getRedeemDVO().getIS_END_CERTIFICATE(), "N")) {
					backFlag_2 = "Y";
				}

				// ApplyDate_2
				curCode_2 = d2ndBatch.getRedeemDVO().getRDM_TRUST_CURR();
				backAmt_2 = d2ndBatch.getRedeemDVO().getPRESENT_VAL();
				buyFlag_2 = StringUtils.equals("Y", d2ndBatch.getRedeemDVO().getIS_RE_PURCHASE()) ? "Y" : "";
				reFundNo_2 = d2ndBatch.getRedeemDVO().getPCH_PROD_ID();
				creditAcct_2 = d2ndBatch.getRedeemDVO().getCREDIT_ACCT();
				feeRate_2 = new EsbUtil().decimalPadding(d2ndBatch.getRedeemDVO().getFEE_RATE(), 3);
				narratorId_2 = d2ndBatch.getRedeemDVO().getNARRATOR_ID();

				takeProfitPerc = d2ndBatch.getRedeemDVO().getTAKE_PROFIT_PERC();
				stopLossPerc = d2ndBatch.getRedeemDVO().getSTOP_LOSS_PERC();

				isPL_2 = (takeProfitPerc != null || stopLossPerc != null) ? "Y" : "N";
				takeProfitPerc_2 = takeProfitPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) takeProfitPerc).abs(), 2));
				takeProfitSym_2 = (takeProfitPerc != null && takeProfitPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-";
				stopLossPerc_2 = stopLossPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) stopLossPerc).abs(), 2));
				stopLossSym_2 = (stopLossPerc != null && stopLossPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-";

				bargainApplySeq_2 = d2ndBatch.getRedeemDVO().getBARGAIN_APPLY_SEQ();
			}
			if (d3rdBatch != null) {
				eviNum_3 = d3rdBatch.getRedeemDVO().getRDM_CERTIFICATE_ID();
				fundNo_3 = d3rdBatch.getRedeemDVO().getRDM_PROD_ID();
				backType_3 = d3rdBatch.getRedeemDVO().getREDEEM_TYPE();
				rcvAcctId_3 = d3rdBatch.getRedeemDVO().getCREDIT_ACCT();
				untNum_3 = new EsbUtil().decimalPadding(d3rdBatch.getRedeemDVO().getRDM_UNIT(), 4);
				backUntNum_3 = new EsbUtil().decimalPadding(d3rdBatch.getRedeemDVO().getUNIT_NUM(), 4);
				if (StringUtils.equals(d3rdBatch.getRedeemDVO().getIS_END_CERTIFICATE(), "Y")) {
					backFlag_3 = "N";
				} else if (StringUtils.equals(d3rdBatch.getRedeemDVO().getIS_END_CERTIFICATE(), "N")) {
					backFlag_3 = "Y";
				}

				// ApplyDate_3
				curCode_3 = d3rdBatch.getRedeemDVO().getRDM_TRUST_CURR();
				backAmt_3 = d3rdBatch.getRedeemDVO().getPRESENT_VAL();
				buyFlag_3 = StringUtils.equals("Y", d3rdBatch.getRedeemDVO().getIS_RE_PURCHASE()) ? "Y" : "";
				reFundNo_3 = d3rdBatch.getRedeemDVO().getPCH_PROD_ID();
				creditAcct_3 = d3rdBatch.getRedeemDVO().getCREDIT_ACCT();
				feeRate_3 = new EsbUtil().decimalPadding(d3rdBatch.getRedeemDVO().getFEE_RATE(), 3);
				narratorId_3 = d3rdBatch.getRedeemDVO().getNARRATOR_ID();

				takeProfitPerc = d3rdBatch.getRedeemDVO().getTAKE_PROFIT_PERC();
				stopLossPerc = d3rdBatch.getRedeemDVO().getSTOP_LOSS_PERC();

				isPL_3 = (takeProfitPerc != null || stopLossPerc != null) ? "Y" : "N";
				takeProfitPerc_3 = takeProfitPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) takeProfitPerc).abs(), 2));
				takeProfitSym_3 = (takeProfitPerc != null && takeProfitPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-";
				stopLossPerc_3 = stopLossPerc == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) stopLossPerc).abs(), 2));
				stopLossSym_3 = (stopLossPerc != null && stopLossPerc.compareTo(BigDecimal.ZERO) >= 0) ? "" : "-";

				bargainApplySeq_3 = d3rdBatch.getRedeemDVO().getBARGAIN_APPLY_SEQ();
			}

			// init util
			Boolean isOBU = StringUtils.equals("Y", mainVO.getTradeMainVO().getIS_OBU()) ? true : false;
			ESBUtilInputVO esbUtilInputVO;
			if (isOBU) {
				esbUtilInputVO = getTxInstance(ESB_TYPE, OBU_NF_CONFIRM_ESB);
			} else {
				esbUtilInputVO = getTxInstance(ESB_TYPE, NF_CONFIRM_ESB);
			}

			esbUtilInputVO.setModule(thisClaz + new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			NFBRN3InputVO txBodyVO = new NFBRN3InputVO();
			esbUtilInputVO.setNfbrn3InputVO(txBodyVO);
			txBodyVO.setCheckCode(checkCode);
			//			txBodyVO.setApplyDate(this.toChineseYearMMdd(applyDate));
			txBodyVO.setEffDate(this.toChineseYearMMdd(effDate));
			txBodyVO.setApplyDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
			//			txBodyVO.setEffDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
			txBodyVO.setKeyinNo(keyinNo);
			txBodyVO.setBranchNo(branchNo);
			txBodyVO.setKeyinId(keyinId);
			txBodyVO.setTrustId(trustId);
			txBodyVO.setType(type);
			txBodyVO.setEviNum_1(eviNum_1);
			txBodyVO.setFundNo_1(fundNo_1);
			txBodyVO.setBackType_1(backType_1);
			txBodyVO.setRcvAcctId_1(rcvAcctId_1);
			txBodyVO.setUntNum_1(String.valueOf(untNum_1));
			txBodyVO.setBackUntNum_1(String.valueOf(backUntNum_1));
			txBodyVO.setBackFlag_1(backFlag_1);
			// txBodyVO.setApplyDate_1(applyDate_1);
			// txBodyVO.setCurCode_1(curCode_1);
			// txBodyVO.setBackAmt_1(String.valueOf(backAmt_1));
			txBodyVO.setBuyFlag_1(buyFlag_1);
			txBodyVO.setReFundNo_1(reFundNo_1);

			if (StringUtils.equals("Y", buyFlag_1)) {
				txBodyVO.setCreditAcct_1(creditAcct_1);
			}

			txBodyVO.setFeeRate_1(String.valueOf(feeRate_1));
			txBodyVO.setNarratorId_1(narratorId_1);

			txBodyVO.setEviNum_2(eviNum_2);
			txBodyVO.setFundNo_2(fundNo_2);
			txBodyVO.setBackType_2(backType_2);
			txBodyVO.setRcvAcctId_2(rcvAcctId_2);
			txBodyVO.setUntNum_2(String.valueOf(untNum_2));
			txBodyVO.setBackUntNum_2(String.valueOf(backUntNum_2));
			txBodyVO.setBackFlag_2(backFlag_2);
			// txBodyVO.setApplyDate_2(applyDate_2);
			// txBodyVO.setCurCode_2(curCode_2);
			// txBodyVO.setBackAmt_2(String.valueOf(backAmt_2));
			txBodyVO.setBuyFlag_2(buyFlag_2);
			txBodyVO.setReFundNo_2(reFundNo_2);

			if (StringUtils.equals("Y", buyFlag_2)) {
				txBodyVO.setCreditAcct_2(creditAcct_2);
			}

			txBodyVO.setFeeRate_2(String.valueOf(feeRate_2));
			txBodyVO.setNarratorId_2(narratorId_2);

			txBodyVO.setEviNum_3(eviNum_3);
			txBodyVO.setFundNo_3(fundNo_3);
			txBodyVO.setBackType_3(backType_3);
			txBodyVO.setRcvAcctId_3(rcvAcctId_3);
			txBodyVO.setUntNum_3(String.valueOf(untNum_3));
			txBodyVO.setBackUntNum_3(String.valueOf(backUntNum_3));
			txBodyVO.setBackFlag_3(backFlag_3);
			// txBodyVO.setApplyDate_3(applyDate_3);
			// txBodyVO.setCurCode_3(curCode_3);
			// txBodyVO.setBackAmt_3(String.valueOf(backAmt_3));
			txBodyVO.setBuyFlag_3(buyFlag_3);
			txBodyVO.setReFundNo_3(reFundNo_3);
			txBodyVO.setCreditAcct_3(creditAcct_3);
			txBodyVO.setFeeRate_3(String.valueOf(feeRate_3));
			txBodyVO.setNarratorId_3(narratorId_3);

			txBodyVO.setRecSeq(recSeq);

			txBodyVO.setIsPL_1(isPL_1);
			txBodyVO.setTakeProfitPerc_1(takeProfitPerc_1);
			txBodyVO.setTakeProfitSym_1(takeProfitSym_1);
			txBodyVO.setStopLossPerc_1(stopLossPerc_1);
			txBodyVO.setStopLossSym_1(stopLossSym_1);

			txBodyVO.setIsPL_2(isPL_2);
			txBodyVO.setTakeProfitPerc_2(takeProfitPerc_2);
			txBodyVO.setTakeProfitSym_2(takeProfitSym_2);
			txBodyVO.setStopLossPerc_2(stopLossPerc_2);
			txBodyVO.setStopLossSym_2(stopLossSym_2);

			txBodyVO.setIsPL_3(isPL_3);
			txBodyVO.setTakeProfitPerc_3(takeProfitPerc_3);
			txBodyVO.setTakeProfitSym_3(takeProfitSym_3);
			txBodyVO.setStopLossPerc_3(stopLossPerc_3);
			txBodyVO.setStopLossSym_3(stopLossSym_3);

			txBodyVO.setBargainApplySeq_1(bargainApplySeq_1);
			txBodyVO.setBargainApplySeq_2(bargainApplySeq_2);
			txBodyVO.setBargainApplySeq_3(bargainApplySeq_3);

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NFBRN3OutputVO nfbrn3OutputVO;
				if (isOBU) {
					nfbrn3OutputVO = esbUtilOutputVO.getAfbrn3OutputVO();
				} else {
					nfbrn3OutputVO = esbUtilOutputVO.getNfbrn3OutputVO();
				}

				// 若電文有回錯誤，則Throw Exception
				throwError(nfbrn3OutputVO);

				updateConfirmESBRedeemNFDB(tradeSeq, d1stBatch, d2ndBatch, d3rdBatch, nfbrn3OutputVO);
			}
		}

		return sot703OutputVO;
	}

	/**
	 * 基金贖回確認取得主檔資料
	 *
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	private MainInfoBean selectConfirmESBRedeemNF(String tradeSeq) throws JBranchException {
		String sql = null;

		sql = new StringBuffer("select * from TBSOT_TRADE_MAIN where TRADE_SEQ = :TRADE_SEQ ").toString();
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sql);

		List<Map> results = dam.exeQuery(condition);

		MainInfoBean main = new MainInfoBean();
		TBSOT_TRADE_MAINVO tradeMainVO = new TBSOT_TRADE_MAINVO();
		main.setTradeMainVO(tradeMainVO);
		if (CollectionUtils.isNotEmpty(results)) {
			Map result = results.get(0);
			tradeMainVO.setCUST_ID((String) result.get("CUST_ID"));
			// tradeMainVO.setIS_OBU((String) result.get("IS_OBU"));
			tradeMainVO.setIS_OBU(CharUtils.toString((Character) result.get("IS_OBU")));
			tradeMainVO.setREC_SEQ((String) result.get("REC_SEQ"));
			tradeMainVO.setBRANCH_NBR((String) result.get("BRANCH_NBR"));
			tradeMainVO.setTRUST_TRADE_TYPE((String) result.get("TRUST_TRADE_TYPE"));
		}

		return main;
	}

	/**
	 * 基金贖回確認取得批號資料
	 *
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	public List<String> selectConfirmESBRedeemNFBatches(String tradeSeq) throws JBranchException {
		dam = getDataAccessManager();
		String sql = new StringBuffer("select BATCH_SEQ, count(1) as BATCH_COUNT from TBSOT_NF_REDEEM_D where TRADE_SEQ = :TRADE_SEQ group by BATCH_SEQ ").toString();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sql);

		List<Map> list = dam.exeQuery(condition);

		List<String> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map map : list) {
				result.add((String) map.get("BATCH_SEQ"));
			}
		}

		return result;
	}

	/**
	 * 依照批號取得明細資料
	 *
	 * @param batchSeq
	 * @return
	 * @throws JBranchException
	 * @throws ParseException
	 */
	private List getConfirmESBRedeemNFDetails(String tradeSeq, String batchSeq) throws JBranchException, ParseException {
		StringBuffer sql = new StringBuffer();
		sql.append("select D.* ");
		sql.append(" from TBSOT_NF_REDEEM_D D ");
		sql.append(" where D.TRADE_SEQ = :TRADE_SEQ  and D.BATCH_SEQ = :BATCH_SEQ ");
		sql.append(" order by D.BATCH_NO ");

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setObject("BATCH_SEQ", batchSeq);
		condition.setQueryString(sql.toString());

		List<Map> list = dam.exeQuery(condition);

		List<DetailInfoBean> details = new ArrayList<DetailInfoBean>();
		if (CollectionUtils.isNotEmpty(list)) {
			DetailInfoBean bean = null;
			TBSOT_NF_REDEEM_DVO redeemDVO = null;
			TBORG_MEMBERVO memberVO = null;

			// 每一批號最多只會有三筆資料，取得第一筆、第二筆與第三筆資料(若有第二、第三筆)
			for (Map map : list) {
				// setting DetailInfoBean
				bean = new DetailInfoBean();
				redeemDVO = new TBSOT_NF_REDEEM_DVO();
				bean.setRedeemDVO(redeemDVO);
				memberVO = new TBORG_MEMBERVO();
				bean.setMemberVO(memberVO);

				redeemDVO.setcomp_id(new TBSOT_NF_REDEEM_DPK((BigDecimal) map.get("SEQ_NO"), tradeSeq));
				redeemDVO.setBATCH_SEQ((String) map.get("BATCH_SEQ"));
				redeemDVO.setModifier((String) map.get("MODIFIER"));
				redeemDVO.setRDM_CERTIFICATE_ID((String) map.get("RDM_CERTIFICATE_ID"));
				redeemDVO.setRDM_PROD_ID((String) map.get("RDM_PROD_ID"));
				redeemDVO.setREDEEM_TYPE(map.get("REDEEM_TYPE").toString());
				redeemDVO.setCREDIT_ACCT((String) map.get("CREDIT_ACCT"));
				redeemDVO.setRDM_UNIT((BigDecimal) map.get("RDM_UNIT"));
				redeemDVO.setUNIT_NUM((BigDecimal) map.get("UNIT_NUM"));
				redeemDVO.setRDM_TRUST_CURR(map.get("RDM_TRUST_CURR").toString());
				redeemDVO.setPRESENT_VAL((BigDecimal) map.get("PRESENT_VAL"));
				redeemDVO.setIS_RE_PURCHASE(map.get("IS_RE_PURCHASE").toString());
				redeemDVO.setPCH_PROD_ID((String) map.get("PCH_PROD_ID"));
				redeemDVO.setFEE_RATE((BigDecimal) map.get("FEE_RATE"));
				redeemDVO.setNARRATOR_ID((String) map.get("NARRATOR_ID"));
				redeemDVO.setTAKE_PROFIT_PERC((BigDecimal) map.get("TAKE_PROFIT_PERC"));
				redeemDVO.setSTOP_LOSS_PERC((BigDecimal) map.get("STOP_LOSS_PERC"));
				redeemDVO.setBARGAIN_APPLY_SEQ((String) map.get("BARGAIN_APPLY_SEQ"));

				redeemDVO.setDEFAULT_FEE_RATE(map.get("DEFAULT_FEE_RATE") == null ? null : new BigDecimal(map.get("DEFAULT_FEE_RATE").toString()));
				if (map.get("IS_END_CERTIFICATE") != null) {
					redeemDVO.setIS_END_CERTIFICATE(map.get("IS_END_CERTIFICATE").toString());
				} else {
					redeemDVO.setIS_END_CERTIFICATE(null);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				redeemDVO.setCreatetime(new Timestamp(sdf.parse(map.get("CREATETIME").toString()).getTime()));
				redeemDVO.setTRADE_DATE(new Timestamp(sdf.parse(map.get("TRADE_DATE").toString()).getTime()));

				details.add(bean);
			}
		}
		return details;
	}

	private void updateConfirmESBRedeemNFDB(String tradeSeq, DetailInfoBean bean1, DetailInfoBean bean2, DetailInfoBean bean3, NFBRN3OutputVO nfbrn3OutputVO) throws JBranchException {
		dam = getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		TBSOT_NF_REDEEM_DVO vo = new TBSOT_NF_REDEEM_DVO();
		TBSOT_NF_REDEEM_DPK pk = new TBSOT_NF_REDEEM_DPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(bean1.getRedeemDVO().getcomp_id().getSEQ_NO());
		vo = (TBSOT_NF_REDEEM_DVO) dam.findByPKey(TBSOT_NF_REDEEM_DVO.TABLE_UID, pk);
		if (vo != null) {
			BigDecimal feeRate_1 = new EsbUtil().decimalPoint(nfbrn3OutputVO.getFeeRate_1(), 3);
			BigDecimal defaultFeeRate1 = bean1.getRedeemDVO().getDEFAULT_FEE_RATE();
			vo.setSHORT_TYPE(nfbrn3OutputVO.getShort_1());
			vo.setFEE_RATE(feeRate_1);
			vo.setFEE_DISCOUNT(defaultFeeRate1 == null || defaultFeeRate1.compareTo(BigDecimal.ZERO) == 0 ? null : feeRate_1.divide(defaultFeeRate1, 3, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(10)));

			vo.setGROUP_OFA(nfbrn3OutputVO.getGroupOfa_1());
			if (StringUtils.equals("Y", bean1.getRedeemDVO().getIS_RE_PURCHASE())) { // 贖回再申購才會有RedeemAmt
				vo.setFEE(StringUtils.isBlank(nfbrn3OutputVO.getRedeemAmt_1()) ? BigDecimal.ZERO : feeRate_1.multiply(new EsbUtil().decimalPoint((nfbrn3OutputVO.getRedeemAmt_1()), 2)));
				vo.setPRESENT_VAL(StringUtils.isBlank(nfbrn3OutputVO.getRedeemAmt_1()) ? BigDecimal.ZERO : new EsbUtil().decimalPoint((nfbrn3OutputVO.getRedeemAmt_1()), 2));
			}
			// vo.setTRADE_DATE_TYPE((StringUtils.equals(sdf.format(this.effDate),
			// sdf.format(new Date())) ? "1" : "2"));
			// vo.setTRADE_DATE(new Timestamp(this.effDate.getTime()));
			dam.update(vo);
		}
		if (bean2 != null) {
			TBSOT_NF_REDEEM_DVO vo2 = new TBSOT_NF_REDEEM_DVO();
			TBSOT_NF_REDEEM_DPK pk2 = new TBSOT_NF_REDEEM_DPK();
			pk2.setTRADE_SEQ(tradeSeq);
			pk2.setSEQ_NO(bean2.getRedeemDVO().getcomp_id().getSEQ_NO());
			vo2 = (TBSOT_NF_REDEEM_DVO) dam.findByPKey(TBSOT_NF_REDEEM_DVO.TABLE_UID, pk2);
			if (vo2 != null) {
				BigDecimal feeRate_2 = new EsbUtil().decimalPoint(nfbrn3OutputVO.getFeeRate_2(), 3);
				BigDecimal defaultFeeRate2 = bean2.getRedeemDVO().getDEFAULT_FEE_RATE();
				vo2.setSHORT_TYPE(nfbrn3OutputVO.getShort_2());
				vo2.setFEE_RATE(feeRate_2);
				vo2.setFEE_DISCOUNT(defaultFeeRate2 == null || defaultFeeRate2.compareTo(BigDecimal.ZERO) == 0 ? null : feeRate_2.divide(defaultFeeRate2, 3, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(10)));

				vo2.setGROUP_OFA(nfbrn3OutputVO.getGroupOfa_2());
				if (StringUtils.equals("Y", bean2.getRedeemDVO().getIS_RE_PURCHASE())) { // 贖回再申購才會有RedeemAmt
					vo2.setFEE(StringUtils.isBlank(nfbrn3OutputVO.getRedeemAmt_2()) ? BigDecimal.ZERO : feeRate_2.multiply(new BigDecimal(nfbrn3OutputVO.getRedeemAmt_2())));
					vo2.setPRESENT_VAL(StringUtils.isBlank(nfbrn3OutputVO.getRedeemAmt_2()) ? BigDecimal.ZERO : new BigDecimal(nfbrn3OutputVO.getRedeemAmt_2()));
				}
				// vo2.setTRADE_DATE_TYPE((StringUtils.equals(sdf.format(this.effDate),
				// sdf.format(new Date())) ? "1" : "2"));
				// vo2.setTRADE_DATE(new Timestamp(this.effDate.getTime()));
				dam.update(vo2);
			}
		}
		if (bean3 != null) {
			TBSOT_NF_REDEEM_DVO vo3 = new TBSOT_NF_REDEEM_DVO();
			TBSOT_NF_REDEEM_DPK pk3 = new TBSOT_NF_REDEEM_DPK();
			pk3.setTRADE_SEQ(tradeSeq);
			pk3.setSEQ_NO(bean3.getRedeemDVO().getcomp_id().getSEQ_NO());
			vo3 = (TBSOT_NF_REDEEM_DVO) dam.findByPKey(TBSOT_NF_REDEEM_DVO.TABLE_UID, pk3);
			if (vo3 != null) {
				BigDecimal feeRate_3 = new EsbUtil().decimalPoint(nfbrn3OutputVO.getFeeRate_3(), 3);
				BigDecimal defaultFeeRate3 = bean3.getRedeemDVO().getDEFAULT_FEE_RATE();
				vo3.setSHORT_TYPE(nfbrn3OutputVO.getShort_3());
				vo3.setFEE_RATE(feeRate_3);
				vo3.setFEE_DISCOUNT(defaultFeeRate3 == null || defaultFeeRate3.compareTo(BigDecimal.ZERO) == 0 ? null : feeRate_3.divide(defaultFeeRate3, 3, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(10)));

				vo3.setGROUP_OFA(nfbrn3OutputVO.getGroupOfa_3());
				if (StringUtils.equals("Y", bean3.getRedeemDVO().getIS_RE_PURCHASE())) { // 贖回再申購才會有RedeemAmt
					vo3.setFEE(StringUtils.isBlank(nfbrn3OutputVO.getRedeemAmt_3()) ? BigDecimal.ZERO : feeRate_3.multiply(new BigDecimal(nfbrn3OutputVO.getRedeemAmt_3())));
					vo3.setPRESENT_VAL(StringUtils.isBlank(nfbrn3OutputVO.getRedeemAmt_3()) ? BigDecimal.ZERO : new BigDecimal(nfbrn3OutputVO.getRedeemAmt_3()));
				}
				// vo3.setTRADE_DATE_TYPE((StringUtils.equals(sdf.format(this.effDate),
				// sdf.format(new Date())) ? "1" : "2"));
				// vo3.setTRADE_DATE(new Timestamp(this.effDate.getTime()));
				dam.update(vo3);
			}
		}
	}

	/**
	 * 基金轉換檢核
	 *
	 * 使用電文: NFBRN2
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void verifyESBTransferNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBTransferNF(body));
	}

	public SOT703OutputVO verifyESBTransferNF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號
		String seqNo = sot703InputVO.getSeqNo(); // 明細檔交易流水號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(seqNo)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 取得主檔資料
		MainInfoBean mainVO = selectVerifyESBTransferNF(tradeSeq, seqNo);

		// 電文需做邏輯處理欄位
		Date TRADE_DATE = mainVO.getTransferDVO().getCreatetime();
		Date EFF_DATE = mainVO.getTransferDVO().getTRADE_DATE();
		String CUST_ID = mainVO.getTradeMainVO().getCUST_ID();

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String BRANCH_NBR = mainVO.getTradeMainVO().getBRANCH_NBR();
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
				BRANCH_NBR = (String) loginBreach.get(0).get("BRANCH_NBR");
			} else {
				throw new APException("人員無有效分行"); //顯示錯誤訊息
			}
		} else if (StringUtils.equals(BRANCH_NBR, branchChgMap.get("BS").toString())) {
			BRANCH_NBR = branchChgMap.get("DEFAULT").toString();
		}

		String PAY_TYPE = "3";
		String FEE_DEBIT_ACCT = mainVO.getTransferDVO().getFEE_DEBIT_ACCT();
		String NARRATOR_ID = mainVO.getTransferDVO().getNARRATOR_ID();
		String EMP_ID = mainVO.getTransferDVO().getModifier();
		String CONFIRM = "1";
		String MINORITY_YN = "";
		MINORITY_YN = this.isCustAgeLessThan18(mainVO.getTradeMainVO().getCUST_ID(), "");
		String PROSPECTUS_TYPE = mainVO.getTransferDVO().getPROSPECTUS_TYPE();
		String CERTIFICATE_ID_1 = mainVO.getTransferDVO().getOUT_CERTIFICATE_ID();
		String TRUST_ACCT_1 = mainVO.getTransferDVO().getOUT_TRUST_ACCT();
		String OUT_PROD_ID_1 = mainVO.getTransferDVO().getOUT_PROD_ID();
		BigDecimal OUT_UNIT1 = mainVO.getTransferDVO().getOUT_UNIT();
		String TRANSFER_TYPE_1 = mainVO.getTransferDVO().getTRANSFER_TYPE();
		String IN_PROD_ID_1_1 = mainVO.getTransferDVO().getIN_PROD_ID_1();
		BigDecimal IN_UNIT_1_1 = mainVO.getTransferDVO().getIN_UNIT_1();

		String RISK_LV_1 = mainVO.getTransferDVO().getIN_PROD_RISK_LV_1();
		String IN_PROD_RISK_LV_1_1 = null;
		if (StringUtils.isNotBlank(RISK_LV_1) && RISK_LV_1.length() >= 2) {
			IN_PROD_RISK_LV_1_1 = RISK_LV_1.substring(1, 2);
		}

		String IN_PROD_ID_1_2 = mainVO.getTransferDVO().getIN_PROD_ID_2();
		BigDecimal IN_UNIT_1_2 = mainVO.getTransferDVO().getIN_UNIT_2();

		String RISK_LV_2 = mainVO.getTransferDVO().getIN_PROD_RISK_LV_2();
		String IN_PROD_RISK_LV_1_2 = null;
		if (StringUtils.isNotBlank(RISK_LV_2) && RISK_LV_2.length() >= 2) {
			IN_PROD_RISK_LV_1_2 = RISK_LV_2.substring(1, 2);
		}

		String IN_PROD_ID_1_3 = mainVO.getTransferDVO().getIN_PROD_ID_3();
		BigDecimal IN_UNIT_1_3 = mainVO.getTransferDVO().getIN_UNIT_3();

		String RISK_LV_3 = mainVO.getTransferDVO().getIN_PROD_RISK_LV_3();
		String IN_PROD_RISK_LV_1_3 = null;
		if (StringUtils.isNotBlank(RISK_LV_3) && RISK_LV_3.length() >= 2) {
			IN_PROD_RISK_LV_1_3 = RISK_LV_3.substring(1, 2);
		}
		Boolean isOBU = sot701.isObu(sot703InputVO.getCustId());
		//		Boolean isOBU = StringUtils.equals("Y", mainVO.getTradeMainVO().getIS_OBU()) ? true : false;
		ESBUtilInputVO esbUtilInputVO;
		if (isOBU) {
			esbUtilInputVO = getTxInstance(ESB_TYPE, OBU_NF_VERIFY_ESB);
		} else {
			esbUtilInputVO = getTxInstance(ESB_TYPE, NF_VERIFY_ESB);
		}

		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		NFBRN2InputVO txBodyVO = new NFBRN2InputVO();
		esbUtilInputVO.setNfbrn2InputVO(txBodyVO);
		//		txBodyVO.setTRADE_DATE(this.toChineseYearMMdd(TRADE_DATE));
		txBodyVO.setEFF_DATE(this.toChineseYearMMdd(EFF_DATE));
		txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		//		txBodyVO.setEFF_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		txBodyVO.setCUST_ID(CUST_ID);
		txBodyVO.setBRANCH_NBR(BRANCH_NBR);
		txBodyVO.setPAY_TYPE(PAY_TYPE);
		txBodyVO.setFEE_DEBIT_ACCT(FEE_DEBIT_ACCT);
		txBodyVO.setNARRATOR_ID(NARRATOR_ID);
		txBodyVO.setEMP_ID(EMP_ID);
		txBodyVO.setCONFIRM(CONFIRM);
		txBodyVO.setMINORITY_YN(MINORITY_YN);
		txBodyVO.setPROSPECTUS_TYPE(PROSPECTUS_TYPE);
		txBodyVO.setCERTIFICATE_ID_1(CERTIFICATE_ID_1);
		txBodyVO.setTRUST_ACCT_1(TRUST_ACCT_1);
		txBodyVO.setOUT_PROD_ID_1(OUT_PROD_ID_1);
		txBodyVO.setOUT_UNIT_1(String.valueOf(this.decimalPadding(OUT_UNIT1, 4)));
		txBodyVO.setTRANSFER_TYPE_1(TRANSFER_TYPE_1);
		txBodyVO.setIN_PROD_ID_1_1(IN_PROD_ID_1_1);
		txBodyVO.setIN_UNIT_1_1(String.valueOf(this.decimalPadding(IN_UNIT_1_1, 4)));
		txBodyVO.setIN_PROD_RISK_LV_1_1(IN_PROD_RISK_LV_1_1);
		txBodyVO.setIN_PROD_ID_1_2(IN_PROD_ID_1_2);
		txBodyVO.setIN_UNIT_1_2(String.valueOf(this.decimalPadding(IN_UNIT_1_2, 4)));
		txBodyVO.setIN_PROD_RISK_LV_1_2(IN_PROD_RISK_LV_1_2);
		txBodyVO.setIN_PROD_ID_1_3(IN_PROD_ID_1_3);
		txBodyVO.setIN_UNIT_1_3(String.valueOf(this.decimalPadding(IN_UNIT_1_3, 4)));
		txBodyVO.setIN_PROD_RISK_LV_1_3(IN_PROD_RISK_LV_1_3);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN2OutputVO nfbrn2OutputVO;
			if (isOBU) {
				nfbrn2OutputVO = esbUtilOutputVO.getAfbrn2OutputVO();
			} else {
				nfbrn2OutputVO = esbUtilOutputVO.getNfbrn2OutputVO();
			}

			sot703OutputVO.setSHORT_1(nfbrn2OutputVO.getSHORT_1());
			sot703OutputVO.setSHORT_2(nfbrn2OutputVO.getSHORT_2());

			// 確認是否回傳錯誤訊息,若發生錯誤，則Throw Exception; 若未發生錯誤則回傳下行電文資料儲存進DB
			throwError(nfbrn2OutputVO);

			// 確認是否回傳錯誤訊息,若未發生錯誤則將回傳下行電文資料儲存DB
			if (!checkError(nfbrn2OutputVO)) {
				// updateVerifyESBTransferNFDB(tradeSeq, new BigDecimal(seqNo));
				// //TODO remove
				updateVerifyESBTransferNFDB(nfbrn2OutputVO, tradeSeq, seqNo);
			}
		}
		return sot703OutputVO;
	}

	private void updateVerifyESBTransferNFDB(String tradeSeq, BigDecimal seqNo) throws JBranchException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		TBSOT_NF_TRANSFER_DVO vo = new TBSOT_NF_TRANSFER_DVO();
		TBSOT_NF_TRANSFER_DPK pk = new TBSOT_NF_TRANSFER_DPK();

		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(seqNo);

		vo = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, pk);

		if (vo != null) {
			// @Deprecated use this.effDate
			// vo.setTRADE_DATE_TYPE((StringUtils.equals(sdf.format(this.effDate),
			// sdf.format(new Date())) ? "1" : "2"));
			// @Deprecated use this.effDate vo.setTRADE_DATE(new
			// Timestamp(this.effDate.getTime()));
			dam.update(vo);
		}
	}

	private void updateVerifyESBTransferNFDB(NFBRN2OutputVO nfbrn2OutputVO, String tradeSeq, String seqNo) throws DAOException {
		dam = getDataAccessManager();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		TBSOT_NF_TRANSFER_DVO vo = new TBSOT_NF_TRANSFER_DVO();
		TBSOT_NF_TRANSFER_DPK pk = new TBSOT_NF_TRANSFER_DPK();

		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(new BigDecimal(seqNo));

		vo = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, pk);

		if (vo != null) {
			vo.setSHORT_TYPE(nfbrn2OutputVO.getSHORT_1());
			// @Deprecated use this.effDate
			// vo.setTRADE_DATE_TYPE((StringUtils.equals(sdf.format(this.effDate),
			// sdf.format(new Date())) ? "1" : "2"));
			// @Deprecated use this.effDate vo.setTRADE_DATE(new
			// Timestamp(this.effDate.getTime()));
			dam.update(vo);
		}
	}

	private MainInfoBean selectVerifyESBTransferNF(String tradeSeq, String seqNo) throws Exception {
		dam = getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select M.CUST_ID, M.BRANCH_NBR, D.* from TBSOT_TRADE_MAIN M ");
		sql.append("inner join TBSOT_NF_TRANSFER_D D on D.TRADE_SEQ = M.TRADE_SEQ ");
		sql.append("left outer join TBCRM_CUST_MAST C on C.CUST_ID = M.CUST_ID ");
		sql.append("where M.TRADE_SEQ = :TRADE_SEQ and D.SEQ_NO = :SEQ_NO ");
		queryCondition.setObject("TRADE_SEQ", tradeSeq);
		queryCondition.setObject("SEQ_NO", seqNo);
		queryCondition.setQueryString(sql.toString());

		List<Map> list = dam.exeQuery(queryCondition);

		MainInfoBean mainVO = new MainInfoBean();
		if (CollectionUtils.isNotEmpty(list)) {
			TBSOT_TRADE_MAINVO tradeMainVO = new TBSOT_TRADE_MAINVO();
			mainVO.setTradeMainVO(tradeMainVO);

			TBSOT_NF_TRANSFER_DVO transferDVO = new TBSOT_NF_TRANSFER_DVO();
			mainVO.setTransferDVO(transferDVO);

			TBORG_MEMBERVO memberVO = new TBORG_MEMBERVO();
			mainVO.setMemberVO(memberVO);

			TBCRM_CUST_MASTVO custMastVO = new TBCRM_CUST_MASTVO();
			mainVO.setCustMastVO(custMastVO);

			tradeMainVO.setCUST_ID((String) list.get(0).get("CUST_ID"));
			tradeMainVO.setBRANCH_NBR((String) list.get(0).get("BRANCH_NBR"));

			transferDVO.setcomp_id(new TBSOT_NF_TRANSFER_DPK(new BigDecimal(seqNo), tradeSeq));
			transferDVO.setFEE_DEBIT_ACCT((String) list.get(0).get("FEE_DEBIT_ACCT"));
			transferDVO.setNARRATOR_ID((String) list.get(0).get("NARRATOR_ID"));
			transferDVO.setModifier((String) list.get(0).get("MODIFIER"));
			transferDVO.setOUT_CERTIFICATE_ID((String) list.get(0).get("OUT_CERTIFICATE_ID"));
			transferDVO.setOUT_TRUST_ACCT((String) list.get(0).get("OUT_TRUST_ACCT"));
			transferDVO.setOUT_PROD_ID((String) list.get(0).get("OUT_PROD_ID"));
			if (list.get(0).get("OUT_UNIT") != null)
				transferDVO.setOUT_UNIT(new BigDecimal(ObjectUtils.toString(list.get(0).get("OUT_UNIT"))));
			transferDVO.setTRANSFER_TYPE(CharUtils.toString((Character) list.get(0).get("TRANSFER_TYPE")));
			transferDVO.setIN_PROD_ID_1((String) list.get(0).get("IN_PROD_ID_1"));
			if (list.get(0).get("IN_UNIT_1") != null)
				transferDVO.setIN_UNIT_1(new BigDecimal(ObjectUtils.toString(list.get(0).get("IN_UNIT_1"))));
			transferDVO.setIN_PROD_RISK_LV_1((String) list.get(0).get("IN_PROD_RISK_LV_1"));
			transferDVO.setIN_PROD_ID_2((String) list.get(0).get("IN_PROD_ID_2"));
			if (list.get(0).get("IN_UNIT_2") != null)
				transferDVO.setIN_UNIT_2(new BigDecimal(ObjectUtils.toString(list.get(0).get("IN_UNIT_2"))));
			transferDVO.setIN_PROD_RISK_LV_2((String) list.get(0).get("IN_PROD_RISK_LV_2"));
			transferDVO.setIN_PROD_ID_3((String) list.get(0).get("IN_PROD_ID_3"));
			if (list.get(0).get("IN_UNIT_3") != null)
				transferDVO.setIN_UNIT_3(new BigDecimal(ObjectUtils.toString(list.get(0).get("IN_UNIT_3"))));
			transferDVO.setIN_PROD_RISK_LV_3((String) list.get(0).get("IN_PROD_RISK_LV_3"));

			transferDVO.setCreatetime((Timestamp) list.get(0).get("CREATETIME"));
			transferDVO.setTRADE_DATE((Timestamp) list.get(0).get("TRADE_DATE"));
			transferDVO.setPROSPECTUS_TYPE(CharUtils.toString((Character) list.get(0).get("PROSPECTUS_TYPE")));
		}

		return mainVO;
	}

	/**
	 * 基金轉換確認
	 *
	 * 使用電文: NFBRN2
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void confirmESBTransferNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.confirmESBTransferNF(body));
	}

	public SOT703OutputVO confirmESBTransferNF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號未輸入");
		}

		// 取得主檔資料
		MainInfoBean mainVO = selectConfirmESBTransferNF(tradeSeq);

		// 取得批號資料
		List<String> batchInfo = selectConfirmESBTransferNFBatches(tradeSeq);

		// 依批號分批發送電文
		List<DetailInfoBean> detailInfo = null;
		for (String batchSeq : batchInfo) {
			// 依照批號取得明細資料
			detailInfo = getConfirmESBTransferNFDetails(tradeSeq, batchSeq);

			// 讀取電文批號資料 (至多二筆)
			DetailInfoBean d1stBatch = detailInfo.get(0);
			DetailInfoBean d2ndBatch = detailInfo.size() >= 2 ? detailInfo.get(1) : null;

			Date TRADE_DATE = d1stBatch.getTransferDVO().getCreatetime();
			Date EFF_DATE = d1stBatch.getTransferDVO().getTRADE_DATE();
			String BATCH_SEQ = batchSeq;
			String CUST_ID = mainVO.getTradeMainVO().getCUST_ID();

			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String BRANCH_NBR = mainVO.getTradeMainVO().getBRANCH_NBR();
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
					BRANCH_NBR = (String) loginBreach.get(0).get("BRANCH_NBR");
				} else {
					throw new APException("人員無有效分行"); //顯示錯誤訊息
				}
			} else if (StringUtils.equals(BRANCH_NBR, branchChgMap.get("BS").toString())) {
				BRANCH_NBR = branchChgMap.get("DEFAULT").toString();
			}
			
			String PAY_TYPE = "3";
			String FEE_DEBIT_ACCT = d1stBatch.getTransferDVO().getFEE_DEBIT_ACCT();
			String NARRATOR_ID = d1stBatch.getTransferDVO().getNARRATOR_ID();
			String EMP_ID = d1stBatch.getTransferDVO().getModifier();
			String CONFIRM = "2";
			String MINORITY_YN = "";

			MINORITY_YN = this.isCustAgeLessThan18(mainVO.getTradeMainVO().getCUST_ID(), "");
			String PROSPECTUS_TYPE = d1stBatch.getTransferDVO().getPROSPECTUS_TYPE();
			String CERTIFICATE_ID_1 = d1stBatch.getTransferDVO().getOUT_CERTIFICATE_ID();
			String TRUST_ACCT_1 = d1stBatch.getTransferDVO().getOUT_TRUST_ACCT();
			String OUT_PROD_ID_1 = d1stBatch.getTransferDVO().getOUT_PROD_ID();
			BigDecimal OUT_UNIT_1 = d1stBatch.getTransferDVO().getOUT_UNIT();
			String TRANSFER_TYPE_1 = d1stBatch.getTransferDVO().getTRANSFER_TYPE();
			String IN_PROD_ID_1_1 = d1stBatch.getTransferDVO().getIN_PROD_ID_1();
			BigDecimal IN_UNIT_1_1 = d1stBatch.getTransferDVO().getIN_UNIT_1();

			String RISK_LV_1 = d1stBatch.getTransferDVO().getIN_PROD_RISK_LV_1();
			String IN_PROD_RISK_LV_1_1 = null;
			if (StringUtils.isNotBlank(RISK_LV_1) && RISK_LV_1.length() >= 2) {
				IN_PROD_RISK_LV_1_1 = RISK_LV_1.substring(1, 2);
			}

			String IN_PROD_ID_1_2 = d1stBatch.getTransferDVO().getIN_PROD_ID_2();
			BigDecimal IN_UNIT_1_2 = d1stBatch.getTransferDVO().getIN_UNIT_2();

			String RISK_LV_2 = d1stBatch.getTransferDVO().getIN_PROD_RISK_LV_2();
			String IN_PROD_RISK_LV_1_2 = null;
			if (StringUtils.isNotBlank(RISK_LV_2) && RISK_LV_2.length() >= 2) {
				IN_PROD_RISK_LV_1_2 = RISK_LV_2.substring(1, 2);
			}

			String IN_PROD_ID_1_3 = d1stBatch.getTransferDVO().getIN_PROD_ID_3();
			BigDecimal IN_UNIT_1_3 = d1stBatch.getTransferDVO().getIN_UNIT_3();

			String RISK_LV_3 = d1stBatch.getTransferDVO().getIN_PROD_RISK_LV_3();
			String IN_PROD_RISK_LV_1_3 = null;
			if (StringUtils.isNotBlank(RISK_LV_3) && RISK_LV_3.length() >= 2) {
				IN_PROD_RISK_LV_1_3 = RISK_LV_3.substring(1, 2);
			}

			String CERTIFICATE_ID_2 = null;
			String TRUST_ACCT_2 = null;
			String OUT_PROD_ID_2 = null;
			BigDecimal OUT_UNIT_2 = null;
			String TRANSFER_TYPE_2 = null;
			String IN_PROD_ID_2_1 = null;
			BigDecimal IN_UNIT_2_1 = null;
			String IN_PROD_RISK_LV_2_1 = null;
			String IN_PROD_ID_2_2 = null;
			BigDecimal IN_UNIT_2_2 = null;
			String IN_PROD_RISK_LV_2_2 = null;
			String IN_PROD_ID_2_3 = null;
			BigDecimal IN_UNIT_2_3 = null;
			String IN_PROD_RISK_LV_2_3 = null;

			if (d2ndBatch != null) {
				CERTIFICATE_ID_2 = d2ndBatch.getTransferDVO().getOUT_CERTIFICATE_ID();
				TRUST_ACCT_2 = d2ndBatch.getTransferDVO().getOUT_TRUST_ACCT();
				OUT_PROD_ID_2 = d2ndBatch.getTransferDVO().getOUT_PROD_ID();
				OUT_UNIT_2 = d2ndBatch.getTransferDVO().getOUT_UNIT();
				TRANSFER_TYPE_2 = d2ndBatch.getTransferDVO().getTRANSFER_TYPE();
				IN_PROD_ID_2_1 = d2ndBatch.getTransferDVO().getIN_PROD_ID_1();
				IN_UNIT_2_1 = d2ndBatch.getTransferDVO().getIN_UNIT_1();

				String RISK_LV_2_1 = d2ndBatch.getTransferDVO().getIN_PROD_RISK_LV_1();
				if (StringUtils.isNotBlank(RISK_LV_2_1) && RISK_LV_2_1.length() >= 2) {
					IN_PROD_RISK_LV_2_1 = RISK_LV_2_1.substring(1, 2);
				}

				IN_PROD_ID_2_2 = d2ndBatch.getTransferDVO().getIN_PROD_ID_2();
				IN_UNIT_2_2 = d2ndBatch.getTransferDVO().getIN_UNIT_2();

				String RISK_LV_2_2 = d2ndBatch.getTransferDVO().getIN_PROD_RISK_LV_2();
				if (StringUtils.isNotBlank(RISK_LV_2_2) && RISK_LV_2_2.length() >= 2) {
					IN_PROD_RISK_LV_2_2 = RISK_LV_2_2.substring(1, 2);
				}

				IN_PROD_ID_2_3 = d2ndBatch.getTransferDVO().getIN_PROD_ID_3();
				IN_UNIT_2_3 = d2ndBatch.getTransferDVO().getIN_UNIT_3();

				String RISK_LV_2_3 = d2ndBatch.getTransferDVO().getIN_PROD_RISK_LV_3();
				if (StringUtils.isNotBlank(RISK_LV_2_3) && RISK_LV_2_3.length() >= 2) {
					IN_PROD_RISK_LV_2_3 = RISK_LV_2_3.substring(1, 2);
				}
			}

			String RECSEQ = mainVO.getTradeMainVO().getREC_SEQ();

			// init util
			Boolean isOBU = StringUtils.equals("Y", mainVO.getTradeMainVO().getIS_OBU()) ? true : false;
			ESBUtilInputVO esbUtilInputVO;
			if (isOBU) {
				esbUtilInputVO = getTxInstance(ESB_TYPE, OBU_NF_VERIFY_ESB);
			} else {
				esbUtilInputVO = getTxInstance(ESB_TYPE, NF_VERIFY_ESB);
			}
			esbUtilInputVO.setModule(thisClaz + new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			NFBRN2InputVO txBodyVO = new NFBRN2InputVO();
			esbUtilInputVO.setNfbrn2InputVO(txBodyVO);
			//			txBodyVO.setTRADE_DATE(this.toChineseYearMMdd(TRADE_DATE));
			txBodyVO.setEFF_DATE(this.toChineseYearMMdd(EFF_DATE));
			txBodyVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
			//			txBodyVO.setEFF_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
			txBodyVO.setBATCH_SEQ(BATCH_SEQ);
			txBodyVO.setCUST_ID(CUST_ID);
			txBodyVO.setBRANCH_NBR(BRANCH_NBR);
			txBodyVO.setPAY_TYPE(PAY_TYPE);
			txBodyVO.setFEE_DEBIT_ACCT(FEE_DEBIT_ACCT);
			txBodyVO.setNARRATOR_ID(NARRATOR_ID);
			txBodyVO.setEMP_ID(EMP_ID);
			txBodyVO.setCONFIRM(CONFIRM);
			txBodyVO.setMINORITY_YN(MINORITY_YN);
			txBodyVO.setPROSPECTUS_TYPE(PROSPECTUS_TYPE);
			txBodyVO.setCERTIFICATE_ID_1(CERTIFICATE_ID_1);
			txBodyVO.setTRUST_ACCT_1(TRUST_ACCT_1);
			txBodyVO.setOUT_PROD_ID_1(OUT_PROD_ID_1);
			txBodyVO.setOUT_UNIT_1(String.valueOf(this.decimalPadding(OUT_UNIT_1, 4)));
			txBodyVO.setTRANSFER_TYPE_1(TRANSFER_TYPE_1);
			txBodyVO.setIN_PROD_ID_1_1(IN_PROD_ID_1_1);
			txBodyVO.setIN_UNIT_1_1(String.valueOf(this.decimalPadding(IN_UNIT_1_1, 4)));
			txBodyVO.setIN_PROD_RISK_LV_1_1(IN_PROD_RISK_LV_1_1);
			txBodyVO.setIN_PROD_ID_1_2(IN_PROD_ID_1_2);
			txBodyVO.setIN_UNIT_1_2(String.valueOf(this.decimalPadding(IN_UNIT_1_2, 4)));
			txBodyVO.setIN_PROD_RISK_LV_1_2(IN_PROD_RISK_LV_1_2);
			txBodyVO.setIN_PROD_ID_1_3(IN_PROD_ID_1_3);
			txBodyVO.setIN_UNIT_1_3(String.valueOf(this.decimalPadding(IN_UNIT_1_3, 4)));
			txBodyVO.setIN_PROD_RISK_LV_1_3(IN_PROD_RISK_LV_1_3);
			if (d2ndBatch != null) {
				txBodyVO.setCERTIFICATE_ID_2(CERTIFICATE_ID_2);
				txBodyVO.setTRUST_ACCT_2(TRUST_ACCT_2);
				txBodyVO.setOUT_PROD_ID_2(OUT_PROD_ID_2);
				txBodyVO.setOUT_UNIT_2(String.valueOf(this.decimalPadding(OUT_UNIT_2, 4)));
				txBodyVO.setTRANSFER_TYPE_2(TRANSFER_TYPE_2);
				txBodyVO.setIN_PROD_ID_2_1(IN_PROD_ID_2_1);
				txBodyVO.setIN_UNIT_2_1(String.valueOf(this.decimalPadding(IN_UNIT_2_1, 4)));
				txBodyVO.setIN_PROD_RISK_LV_2_1(IN_PROD_RISK_LV_2_1);
				txBodyVO.setIN_PROD_ID_2_2(IN_PROD_ID_2_2);
				txBodyVO.setIN_UNIT_2_2(String.valueOf(this.decimalPadding(IN_UNIT_2_2, 4)));
				txBodyVO.setIN_PROD_RISK_LV_2_2(IN_PROD_RISK_LV_2_2);
				txBodyVO.setIN_PROD_ID_2_3(IN_PROD_ID_2_3);
				txBodyVO.setIN_UNIT_2_3(String.valueOf(this.decimalPadding(IN_UNIT_2_3, 4)));
				txBodyVO.setIN_PROD_RISK_LV_2_3(IN_PROD_RISK_LV_2_3);
			}
			// txBodyVO.setBRV451(BRV451); TODO

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NFBRN2OutputVO nfbrn2OutputVO;
				if (isOBU) {
					nfbrn2OutputVO = esbUtilOutputVO.getAfbrn2OutputVO();
				} else {
					nfbrn2OutputVO = esbUtilOutputVO.getNfbrn2OutputVO();
				}

				// 確認是否回傳錯誤訊息,若發生錯誤，則Throw Exception；若未發生錯誤則將回傳下行電文資料儲存DB
				throwError(nfbrn2OutputVO);

				// updateConfirmESBTransferNFDB(detailInfo); TODO remove
				updateConfirmESBTransferNFDB(tradeSeq, d1stBatch, d2ndBatch, nfbrn2OutputVO);
			}
		}

		return sot703OutputVO;
	}

	private void updateConfirmESBTransferNFDB(List<DetailInfoBean> details) throws JBranchException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		TBSOT_NF_TRANSFER_DVO vo = new TBSOT_NF_TRANSFER_DVO();
		TBSOT_NF_TRANSFER_DPK pk = new TBSOT_NF_TRANSFER_DPK();

		if (CollectionUtils.isNotEmpty(details)) {
			for (DetailInfoBean detailinfo : details) {
				pk.setTRADE_SEQ(detailinfo.getTransferDVO().getcomp_id().getTRADE_SEQ());
				pk.setSEQ_NO(detailinfo.getTransferDVO().getcomp_id().getSEQ_NO());
				vo = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, pk);

				if (vo != null) {
					// @Deprecated use this.effDate
					// vo.setTRADE_DATE_TYPE((StringUtils.equals(sdf.format(this.effDate),
					// sdf.format(new Date())) ? "1" : "2"));
					// @Deprecated use this.effDate vo.setTRADE_DATE(new
					// Timestamp(this.effDate.getTime()));
					dam.update(vo);
				}
			}
		}
	}

	private void updateConfirmESBTransferNFDB(String tradeSeq, DetailInfoBean bean1, DetailInfoBean bean2, NFBRN2OutputVO nfbrn2OutputVO) throws JBranchException {

		dam = getDataAccessManager();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		TBSOT_NF_TRANSFER_DVO vo = new TBSOT_NF_TRANSFER_DVO();
		TBSOT_NF_TRANSFER_DPK pk = new TBSOT_NF_TRANSFER_DPK();

		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(bean1.getTransferDVO().getcomp_id().getSEQ_NO());

		vo = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, pk);
		if (vo != null) {
			vo.setSHORT_TYPE(nfbrn2OutputVO.getSHORT_1());
			// @Deprecated use this.effDate
			// vo.setTRADE_DATE_TYPE((StringUtils.equals(sdf.format(this.effDate),
			// sdf.format(new Date())) ? "1" : "2"));
			// @Deprecated use this.effDate vo.setTRADE_DATE(new
			// Timestamp(this.effDate.getTime()));
			dam.update(vo);
		}
		if (bean2 != null) {
			TBSOT_NF_TRANSFER_DVO vo2 = new TBSOT_NF_TRANSFER_DVO();
			TBSOT_NF_TRANSFER_DPK pk2 = new TBSOT_NF_TRANSFER_DPK();

			pk2.setTRADE_SEQ(tradeSeq);
			pk2.setSEQ_NO(bean2.getTransferDVO().getcomp_id().getSEQ_NO());
			vo2 = (TBSOT_NF_TRANSFER_DVO) dam.findByPKey(TBSOT_NF_TRANSFER_DVO.TABLE_UID, pk);
			if (vo2 != null) {
				vo2.setSHORT_TYPE(nfbrn2OutputVO.getSHORT_2());
				// @Deprecated use this.effDate
				// vo2.setTRADE_DATE_TYPE((StringUtils.equals(sdf.format(this.effDate),
				// sdf.format(new Date())) ? "1" : "2"));
				// @Deprecated use this.effDate vo2.setTRADE_DATE(new
				// Timestamp(this.effDate.getTime()));
				dam.update(vo2);
			}
		}
	}

	private MainInfoBean selectConfirmESBTransferNF(String tradeSeq) throws Exception {
		dam = getDataAccessManager();
		//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		//		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		//		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.S");

		//		QueryConditionIF queryCondition = dam
		//				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//		StringBuffer sql = new StringBuffer();
		//		sql.append("select M.* from TBSOT_TRADE_MAIN M ");
		//		sql.append("where M.TRADE_SEQ = :TRADE_SEQ ");
		//		queryCondition.setObject("TRADE_SEQ", tradeSeq);
		//		queryCondition.setQueryString(sql.toString());
		//
		//		List<Map> list = dam.exeQuery(queryCondition);

		MainInfoBean mainVO = new MainInfoBean();
		TBSOT_TRADE_MAINVO tradeMainVO = (TBSOT_TRADE_MAINVO) dam.findByPKey(TBSOT_TRADE_MAINVO.TABLE_UID, tradeSeq);

		mainVO.setTradeMainVO(tradeMainVO);

		//		TBCRM_CUST_MASTVO custMastVO = new TBCRM_CUST_MASTVO();
		//		mainVO.setCustMastVO(custMastVO);
		//		if (CollectionUtils.isNotEmpty(list)) {
		//			TBSOT_TRADE_MAINVO tradeMainVO = new TBSOT_TRADE_MAINVO();
		//			mainVO.setTradeMainVO(tradeMainVO);
		//
		//			TBCRM_CUST_MASTVO custMastVO = new TBCRM_CUST_MASTVO();
		//			mainVO.setCustMastVO(custMastVO);
		//
		//			tradeMainVO.setCUST_ID((String) list.get(0).get("CUST_ID"));
		//			tradeMainVO.setREC_SEQ((String) list.get(0).get("REC_SEQ"));
		//			tradeMainVO.setBRANCH_NBR((String) list.get(0).get("BRANCH_NBR"));
		/*
		 * if (list.get(0).get("BIRTH_DATE") != null) { if
		 * (list.get(0).get("BIRTH_DATE") instanceof java.sql.Timestamp) {
		 * java.sql.Timestamp birthdate = (java.sql.Timestamp)
		 * list.get(0).get("BIRTH_DATE");
		 * custMastVO.setBIRTH_DATE(birthdate); } else { try {
		 * custMastVO.setBIRTH_DATE(new
		 * Timestamp(Long.valueOf(ObjectUtils.toString
		 * (list.get(0).get("BIRTH_DATE"))))); } catch(Exception e) { try {
		 * custMastVO.setBIRTH_DATE(new
		 * Timestamp(sdf.parse(ObjectUtils.toString
		 * (list.get(0).get("BIRTH_DATE"))).getTime())); } catch (Exception
		 * e2) { custMastVO.setBIRTH_DATE(new
		 * Timestamp(sdf3.parse(ObjectUtils
		 * .toString(list.get(0).get("BIRTH_DATE"))).getTime())); } } } }
		 */
		//		}

		return mainVO;
	}

	/**
	 * 基金轉換確認取得批號資料
	 *
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	public List<String> selectConfirmESBTransferNFBatches(String tradeSeq) throws JBranchException {
		dam = getDataAccessManager();
		String sql = new StringBuffer("select BATCH_SEQ,count(1) as BATCH_COUNT from TBSOT_NF_TRANSFER_D where TRADE_SEQ = :TRADE_SEQ group by BATCH_SEQ ").toString();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sql);

		List<Map> list = dam.exeQuery(condition);

		List<String> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map map : list) {
				result.add((String) map.get("BATCH_SEQ"));
			}
		}

		return result;
	}

	private List getConfirmESBTransferNFDetails(String tradeSeq, String batchSeq) throws JBranchException {
		StringBuffer sql = new StringBuffer();
		sql.append("select D.* from TBSOT_NF_TRANSFER_D D ");
		sql.append("where D.TRADE_SEQ = :TRADE_SEQ and D.BATCH_SEQ = :BATCH_SEQ ");
		sql.append("order by D.BATCH_NO ");

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setObject("BATCH_SEQ", batchSeq);
		condition.setQueryString(sql.toString());

		List<Map> list = dam.exeQuery(condition);

		List<DetailInfoBean> details = new ArrayList<DetailInfoBean>();
		if (CollectionUtils.isNotEmpty(list)) {
			DetailInfoBean bean = null;
			TBSOT_NF_TRANSFER_DVO transferDVO = null;
			TBSOT_TRADE_MAINVO tradeMainVO = null;
			TBORG_MEMBERVO memberVO = null;

			// 每一批號最多只會有兩筆資料，取得第一筆與第二筆資料(若有第二筆)
			for (Map map : list) {
				// setting DetailInfoBean
				bean = new DetailInfoBean();
				transferDVO = new TBSOT_NF_TRANSFER_DVO();
				bean.setTransferDVO(transferDVO);
				memberVO = new TBORG_MEMBERVO();
				bean.setMemberVO(memberVO);

				transferDVO.setcomp_id(new TBSOT_NF_TRANSFER_DPK((BigDecimal) map.get("SEQ_NO"), tradeSeq));
				transferDVO.setFEE_DEBIT_ACCT((String) map.get("FEE_DEBIT_ACCT"));
				transferDVO.setNARRATOR_ID((String) map.get("NARRATOR_ID"));
				transferDVO.setModifier((String) map.get("MODIFIER"));
				transferDVO.setOUT_CERTIFICATE_ID((String) map.get("OUT_CERTIFICATE_ID"));
				transferDVO.setOUT_TRUST_ACCT((String) map.get("OUT_TRUST_ACCT"));
				transferDVO.setOUT_PROD_ID((String) map.get("OUT_PROD_ID"));
				if (map.get("OUT_UNIT") != null)
					transferDVO.setOUT_UNIT(new BigDecimal(ObjectUtils.toString(map.get("OUT_UNIT"))));
				transferDVO.setTRANSFER_TYPE(map.get("TRANSFER_TYPE").toString());
				transferDVO.setIN_PROD_ID_1((String) map.get("IN_PROD_ID_1"));
				if (map.get("IN_UNIT_1") != null)
					transferDVO.setIN_UNIT_1(new BigDecimal(ObjectUtils.toString(map.get("IN_UNIT_1"))));
				transferDVO.setIN_PROD_RISK_LV_1((String) map.get("IN_PROD_RISK_LV_1"));
				transferDVO.setIN_PROD_ID_2((String) map.get("IN_PROD_ID_2"));
				if (map.get("IN_UNIT_2") != null)
					transferDVO.setIN_UNIT_2(new BigDecimal(ObjectUtils.toString(map.get("IN_UNIT_2"))));
				transferDVO.setIN_PROD_RISK_LV_2((String) map.get("IN_PROD_RISK_LV_2"));
				transferDVO.setIN_PROD_ID_3((String) map.get("IN_PROD_ID_3"));
				if (map.get("IN_UNIT_3") != null)
					transferDVO.setIN_UNIT_3(new BigDecimal(ObjectUtils.toString(map.get("IN_UNIT_3"))));
				transferDVO.setIN_PROD_RISK_LV_3((String) map.get("IN_PROD_RISK_LV_3"));
				transferDVO.setPROSPECTUS_TYPE(CharUtils.toString((Character) map.get("PROSPECTUS_TYPE")));

				transferDVO.setCreatetime((Timestamp) list.get(0).get("CREATETIME"));
				transferDVO.setTRADE_DATE((Timestamp) list.get(0).get("TRADE_DATE"));

				details.add(bean);
			}
		}
		return details;
	}

	/**
	 * 基金變更檢核
	 *
	 * 使用電文: NFBRN5
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void verifyESBChangeNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBChangeNF(body));
	}

	public SOT703OutputVO verifyESBChangeNF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號
		String seqNo = sot703InputVO.getSeqNo(); // 明細檔交易流水號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(seqNo)) {
			throw new JBranchException("下單交易序號或明細檔交易流水號未輸入");
		}

		// 取得主檔與明細資料
		List<MainInfoBean> mainInfo = selectVerifyESBChangeNF(tradeSeq, seqNo);

		// 根據變更項目分批發送
		for (MainInfoBean bean : mainInfo) {
			// init util
			Boolean isOBU = StringUtils.equals("Y", bean.getTradeMainVO().getIS_OBU()) ? true : false;
			ESBUtilInputVO esbUtilInputVO;
			if (isOBU) {
				esbUtilInputVO = getTxInstance(ESB_TYPE, OBU_NF_VERIFY_ESB_NF);
			} else {
				esbUtilInputVO = getTxInstance(ESB_TYPE, NF_VERIFY_ESB_NF);
			}

			esbUtilInputVO.setModule(thisClaz + new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			NFBRN5InputVO txBodyVO = new NFBRN5InputVO();
			esbUtilInputVO.setNfbrn5InputVO(txBodyVO);
			txBodyVO.setDetails(new ArrayList<NFBRN5InputVODetails>());

			txBodyVO.setCheckCode("1");
			//			txBodyVO.setApplyDate(this.toChineseYearMMdd(bean.getChangeDVO().getCreatetime()));
			txBodyVO.setEffDate(this.toChineseYearMMdd(bean.getChangeDVO().getTRADE_DATE()));
			txBodyVO.setApplyDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
			//			txBodyVO.setEffDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改

			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNbr = bean.getTradeMainVO().getBRANCH_NBR();
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
			txBodyVO.setBranchNo(branchNbr);

			txBodyVO.setKeyinId(bean.getChangeDVO().getModifier());
			txBodyVO.setCustId(bean.getTradeMainVO().getCUST_ID());
			txBodyVO.setChangeItem(bean.getItem_type()); // 變更方式
			txBodyVO.setDBUType(StringUtils.equals(bean.getTradeMainVO().getIS_OBU(), "Y") ? "2" : "1");

			String prospectusType = null; // 交付公開說明書 (變標的)
			String narratorId = null; // 解說專員(變標的)
			String recSeq = null; // 錄音續序號(變標的)
			NFBRN5InputVODetails list = new NFBRN5InputVODetails();
			// 變更扣款日期
			if (StringUtils.equals(bean.getItem_type(), "A9")) {
				list.setCertificateId(bean.getChangeDVO().getCERTIFICATE_ID());
				list.setChargeDate1(StringUtils.isNotBlank(bean.getChangeDVO().getF_CHARGE_DATE_1()) ? bean.getChangeDVO().getF_CHARGE_DATE_1() : "00");
				list.setChargeDate2(StringUtils.isNotBlank(bean.getChangeDVO().getF_CHARGE_DATE_2()) ? bean.getChangeDVO().getF_CHARGE_DATE_2() : "00");
				list.setChargeDate3(StringUtils.isNotBlank(bean.getChangeDVO().getF_CHARGE_DATE_3()) ? bean.getChangeDVO().getF_CHARGE_DATE_3() : "00");
				list.setChargeDate4(StringUtils.isNotBlank(bean.getChangeDVO().getF_CHARGE_DATE_4()) ? bean.getChangeDVO().getF_CHARGE_DATE_4() : "00");
				list.setChargeDate5(StringUtils.isNotBlank(bean.getChangeDVO().getF_CHARGE_DATE_5()) ? bean.getChangeDVO().getF_CHARGE_DATE_5() : "00");
				list.setChargeDate6(StringUtils.isNotBlank(bean.getChangeDVO().getF_CHARGE_DATE_6()) ? bean.getChangeDVO().getF_CHARGE_DATE_6() : "00");
			}
			// 變更扣款帳號
			else if (StringUtils.equals(bean.getItem_type(), "A8")) {
				list.setCertificateId(bean.getChangeDVO().getCERTIFICATE_ID());
				list.setDebitAcct(bean.getChangeDVO().getF_DEBIT_ACCT());
				list.setDebitAcctId(bean.getTradeMainVO().getCUST_ID());
			}
			// 變更標的及金額 AX:標的變更 A7:金額變更 X7:兩者
			else if (bean.getItem_type().matches("AX|A7|X7")) {

				if (bean.getItem_type().matches("AX|X7")) {
					list.setProdInfoYN("Y");
					prospectusType = bean.getChangeDVO().getPROSPECTUS_TYPE(); // 公開說明書交付方式
					narratorId = bean.getChangeDVO().getNARRATOR_ID(); // 解說專員
					// recSeq = bean.getTradeMainVO().getREC_SEQ(); //錄音序號
				}
				if (StringUtils.equals(bean.getItem_type(), "A7")) {
					list.setProdInfoYN("N");
				}
				txBodyVO.setChangeItem("X7"); // 不管標的變 或 只有金額 都用 X7發電文

				list.setCertificateId(bean.getChangeDVO().getCERTIFICATE_ID()); // 憑證編號
				list.setProdId(bean.getChangeDVO().getF_PROD_ID()); // 商品編號

				BigDecimal zero = BigDecimal.ZERO;
				if (bean.getChangeDVO().getF_PURCHASE_AMT_L() != null && !zero.equals(bean.getChangeDVO().getF_PURCHASE_AMT_L()))
					list.setPurchaseAmt1(String.valueOf(this.decimalPadding(bean.getChangeDVO().getF_PURCHASE_AMT_L(), 0)));
				if (bean.getChangeDVO().getF_PURCHASE_AMT_M() != null && !zero.equals(bean.getChangeDVO().getF_PURCHASE_AMT_M()))
					list.setPurchaseAmt2(String.valueOf(this.decimalPadding(bean.getChangeDVO().getF_PURCHASE_AMT_M(), 0)));
				if (bean.getChangeDVO().getF_PURCHASE_AMT_H() != null && !zero.equals(bean.getChangeDVO().getF_PURCHASE_AMT_H()))
					list.setPurchaseAmt3(String.valueOf(this.decimalPadding(bean.getChangeDVO().getF_PURCHASE_AMT_H(), 0)));

				// 同標的不需要傳ExchangeAmt
				if (!StringUtils.equals(bean.getChangeDVO().getB_PROD_ID(), bean.getChangeDVO().getF_PROD_ID())) {
					list.setExchangeCurr(bean.getChangeDVO().getB_EXCH_CURR()); // 換匯電文回傳
																				// 幣別
					if (bean.getChangeDVO().getB_PURCHASE_AMT_EXCH_L() != null)
						list.setExchangeAmt1(String.valueOf(this.decimalPadding(bean.getChangeDVO().getB_PURCHASE_AMT_EXCH_L(), 0))); // 換匯電文回傳
					if (bean.getChangeDVO().getB_PURCHASE_AMT_EXCH_M() != null)
						list.setExchangeAmt2(String.valueOf(this.decimalPadding(bean.getChangeDVO().getB_PURCHASE_AMT_EXCH_M(), 0))); // 換匯電文回傳
					if (bean.getChangeDVO().getB_PURCHASE_AMT_EXCH_H() != null)
						list.setExchangeAmt3(String.valueOf(this.decimalPadding(bean.getChangeDVO().getB_PURCHASE_AMT_EXCH_H(), 0))); // 換匯電文回傳
				}

			}
			// 暫停
			else if (StringUtils.equals(bean.getItem_type(), "B1")) {
				list.setCertificateId(bean.getChangeDVO().getCERTIFICATE_ID());
				list.setStartDate(this.toChineseYearMMdd(bean.getChangeDVO().getF_HOLD_START_DATE()));
				list.setEndDate(this.toChineseYearMMdd(bean.getChangeDVO().getF_HOLD_END_DATE()));
			}
			// 恢復
			else if (StringUtils.equals(bean.getItem_type(), "B2")) {
				list.setCertificateId(bean.getChangeDVO().getCERTIFICATE_ID());
				list.setResumeDate(this.toChineseYearMMdd(bean.getChangeDVO().getF_RESUME_DATE()));
			}
			// 終止
			else if (StringUtils.equals(bean.getItem_type(), "B8")) {
				list.setCertificateId(bean.getChangeDVO().getCERTIFICATE_ID());
			}
			// 變更收益入帳帳號
			else if (StringUtils.equals(bean.getItem_type(), "A0")) {
				list.setCertificateId(bean.getChangeDVO().getCERTIFICATE_ID());
				list.setCreditAcct(bean.getChangeDVO().getF_CREDIT_ACCT());
			}

			txBodyVO.setNarratorId(narratorId);// 解說專員(當變更標的)
			// txBodyVO.setRecSeq(recSeq); //錄音序號
			txBodyVO.setProspectusType(prospectusType);// 公開說明書交付方式 (當變更標的)
			txBodyVO.getDetails().add(list);

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NFBRN5OutputVO nfbrn5OutputVO;
				if (isOBU) {
					nfbrn5OutputVO = esbUtilOutputVO.getAfbrn5OutputVO();
				} else {
					nfbrn5OutputVO = esbUtilOutputVO.getNfbrn5OutputVO();
				}

				// 回傳錯誤訊息
				if (checkError(nfbrn5OutputVO))
					break;

				List<NFBRN5InputVODetails> outputDetails = nfbrn5OutputVO.getDetails();
				outputDetails = (CollectionUtils.isEmpty(outputDetails)) ? new ArrayList<NFBRN5InputVODetails>() : outputDetails;
				for (NFBRN5InputVODetails detail : outputDetails) {
					// 若有錯誤訊息，回傳錯誤訊息
					checkError(detail);
				}
			}
		}

		return sot703OutputVO;
	}

	private List<MainInfoBean> selectVerifyESBChangeNF(String tradeSeq, String seqNo) throws JBranchException {
		dam = getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		/*
		 * sql.append(
		 * "select M.CUST_ID,M.IS_OBU,M.BRANCH_NBR,M.PROSPECTUS_TYPE,D.*,B.CHANGE_TYPE as ITEM_TYPE from TBSOT_TRADE_MAIN M "
		 * );
		 * sql.append("inner join TBSOT_NF_CHANGE_D D on D.TRADE_SEQ = M.TRADE_SEQ "
		 * ); sql.append(
		 * "inner join (select distinct TRADE_SEQ,SEQ_NO, B_TRUST_ACCT,regexp_substr(CHANGE_TYPE,'[^,]+', 1, LEVEL) as CHANGE_TYPE "
		 * ); sql.append(
		 * "from TBSOT_NF_CHANGE_D connect by regexp_substr(CHANGE_TYPE,'[^,]+', 1, LEVEL) is not null) B on B.TRADE_SEQ = D.TRADE_SEQ and B.SEQ_NO = D.SEQ_NO "
		 * );
		 * sql.append("where M.TRADE_SEQ = :TRADE_SEQ and D.SEQ_NO = :SEQ_NO ");
		 */

		// 瑞國提供
		sql.append(" select M.CUST_ID,M.IS_OBU,M.BRANCH_NBR,D.*,BB.ITEM_TYPE from TBSOT_TRADE_MAIN M  ");
		sql.append("  inner join TBSOT_NF_CHANGE_D D on D.TRADE_SEQ = M.TRADE_SEQ  ");
		sql.append("  inner join ( ");
		sql.append(" 		  SELECT CUST_ID,IS_OBU,BRANCH_NBR,REGEXP_SUBSTR(ITEM_TYPE, '[^,]+' ,1,LEVEL) AS ITEM_TYPE ,SEQ_NO,TRADE_SEQ ");
		sql.append(" 		  FROM( ");
		sql.append(" 		  SELECT M.CUST_ID,M.IS_OBU,M.BRANCH_NBR,B.CHANGE_TYPE as ITEM_TYPE ,B.SEQ_NO,B.TRADE_SEQ ");
		sql.append(" 		  from TBSOT_TRADE_MAIN M INNER JOIN TBSOT_NF_CHANGE_D B on B.TRADE_SEQ = M.TRADE_SEQ  ");
		sql.append(" 	  where M.TRADE_SEQ =:TRADE_SEQ and B.SEQ_NO =:SEQ_NO AND CHANGE_TYPE is not null ");
		sql.append(" 		  )CONNECT BY LEVEL<=LENGTH(REGEXP_REPLACE(ITEM_TYPE,',',''))/2 ");
		sql.append(" 	)BB on BB.TRADE_SEQ = D.TRADE_SEQ and BB.SEQ_NO = D.SEQ_NO   ");

		queryCondition.setObject("TRADE_SEQ", tradeSeq);
		queryCondition.setObject("SEQ_NO", seqNo);
		queryCondition.setQueryString(sql.toString());

		List<Map> list = dam.exeQuery(queryCondition);

		List<MainInfoBean> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map map : list) {
				MainInfoBean mainVO = new MainInfoBean();

				TBSOT_TRADE_MAINVO tradeMainVO = new TBSOT_TRADE_MAINVO();
				mainVO.setTradeMainVO(tradeMainVO);

				TBSOT_NF_CHANGE_DVO changeDVO = new TBSOT_NF_CHANGE_DVO();
				mainVO.setChangeDVO(changeDVO);
				mainVO.setItem_type((String) map.get("ITEM_TYPE"));

				TBORG_MEMBERVO memberVO = new TBORG_MEMBERVO();
				mainVO.setMemberVO(memberVO);

				tradeMainVO.setCUST_ID((String) map.get("CUST_ID"));
				tradeMainVO.setIS_OBU(map.get("IS_OBU") == null ? null : map.get("IS_OBU").toString());
				tradeMainVO.setBRANCH_NBR((String) map.get("BRANCH_NBR"));
				changeDVO.setModifier((String) map.get("MODIFIER"));
				changeDVO.setTRADE_SUB_TYPE(CharUtils.toString((Character) map.get("TRADE_SUB_TYPE")));
				changeDVO.setCERTIFICATE_ID((String) map.get("CERTIFICATE_ID"));
				changeDVO.setF_CHARGE_DATE_1((String) map.get("F_CHARGE_DATE_1"));
				changeDVO.setF_CHARGE_DATE_2((String) map.get("F_CHARGE_DATE_2"));
				changeDVO.setF_CHARGE_DATE_3((String) map.get("F_CHARGE_DATE_3"));
				changeDVO.setF_CHARGE_DATE_4((String) map.get("F_CHARGE_DATE_4"));
				changeDVO.setF_CHARGE_DATE_5((String) map.get("F_CHARGE_DATE_5"));
				changeDVO.setF_CHARGE_DATE_6((String) map.get("F_CHARGE_DATE_6"));
				changeDVO.setF_DEBIT_ACCT((String) map.get("F_DEBIT_ACCT"));
				changeDVO.setF_PROD_ID((String) map.get("F_PROD_ID"));
				changeDVO.setB_PROD_ID((String) map.get("B_PROD_ID"));

				changeDVO.setB_TRUST_CURR((String) map.get("B_TRUST_CURR"));
				if (map.get("B_PURCHASE_AMT_L") != null)
					changeDVO.setB_PURCHASE_AMT_L(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_L"))));
				if (map.get("B_PURCHASE_AMT_M") != null)
					changeDVO.setB_PURCHASE_AMT_M(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_M"))));
				if (map.get("B_PURCHASE_AMT_H") != null)
					changeDVO.setB_PURCHASE_AMT_H(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_H"))));

				if (map.get("B_EXCH_CURR") != null)
					changeDVO.setB_EXCH_CURR(ObjectUtils.toString(map.get("B_EXCH_CURR")));// 變更前每月扣款金額CALL 換匯電文回傳的幣別

				if (map.get("B_PURCHASE_AMT_EXCH_L") != null)
					changeDVO.setB_PURCHASE_AMT_EXCH_L(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_EXCH_L"))));// 變更前每月扣款金額
																																// [換匯電文回傳]_低
				if (map.get("B_PURCHASE_AMT_EXCH_M") != null)
					changeDVO.setB_PURCHASE_AMT_EXCH_M(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_EXCH_M"))));
				if (map.get("B_PURCHASE_AMT_EXCH_H") != null)
					changeDVO.setB_PURCHASE_AMT_EXCH_H(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_EXCH_H"))));

				if (map.get("F_PURCHASE_AMT_L") != null)
					changeDVO.setF_PURCHASE_AMT_L(new BigDecimal(ObjectUtils.toString(map.get("F_PURCHASE_AMT_L"))));
				if (map.get("F_PURCHASE_AMT_M") != null)
					changeDVO.setF_PURCHASE_AMT_M(new BigDecimal(ObjectUtils.toString(map.get("F_PURCHASE_AMT_M"))));
				if (map.get("F_PURCHASE_AMT_H") != null)
					changeDVO.setF_PURCHASE_AMT_H(new BigDecimal(ObjectUtils.toString(map.get("F_PURCHASE_AMT_H"))));

				changeDVO.setF_TRUST_CURR((String) map.get("F_TRUST_CURR"));
				if (map.get("F_HOLD_START_DATE") != null)
					changeDVO.setF_HOLD_START_DATE(new Timestamp(((Date) map.get("F_HOLD_START_DATE")).getTime()));
				if (map.get("F_HOLD_END_DATE") != null)
					changeDVO.setF_HOLD_END_DATE(new Timestamp(((Date) map.get("F_HOLD_END_DATE")).getTime()));
				if (map.get("F_RESUME_DATE") != null)
					changeDVO.setF_RESUME_DATE(new Timestamp(((Date) map.get("F_RESUME_DATE")).getTime()));
				changeDVO.setF_CREDIT_ACCT((String) map.get("F_CREDIT_ACCT"));
				changeDVO.setCreatetime((Timestamp) list.get(0).get("CREATETIME"));
				changeDVO.setTRADE_DATE((Timestamp) list.get(0).get("TRADE_DATE"));

				changeDVO.setNARRATOR_ID((String) map.get("NARRATOR_ID"));// 解說專員
				changeDVO.setPROSPECTUS_TYPE(CharUtils.toString((Character) map.get("PROSPECTUS_TYPE")));// 公開說明書方式

				result.add(mainVO);
			}
		}
		return result;
	}

	/**
	 * 基金變更確認
	 *
	 * 使用電文: NFBRN5
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void confirmESBChangeNF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.confirmESBChangeNF(body));
	}

	public SOT703OutputVO confirmESBChangeNF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String tradeSeq = sot703InputVO.getTradeSeq(); // 下單交易序號

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號未輸入");
		}

		// 取得批號資料
		List<String> batchInfo = selectConfirmESBChangeNFBatches(tradeSeq);

		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		// 依批號分批發送電文
		List<DetailInfoBean> detailInfo = null;
		for (String batchSeq : batchInfo) {
			// 依照批號取得明細資料
			detailInfo = getConfirmESBChangeNFDetails(batchSeq);

			// 使用欄位
			String BRZ101 = "2";
			String BRZ104 = null;
			String BRZ105 = null;
			String BRZ106 = batchSeq;
			String BRZ107 = null; // BRANCH_NBR
			String BRZ108 = null;
			String BRZ109 = null;
			String BRZ110 = null;
			String BRZ111 = null;
			String narratorId = null;
			String prospectusType = null; // 公開說明書交付方式
			String recSeq = null;
			String minorityYN = null;
			String isOBU = null;

			List<NFBRN5InputVODetails> repeat = new ArrayList<NFBRN5InputVODetails>();

			for (DetailInfoBean detail : detailInfo) {
				BRZ104 = this.toChineseYearMMdd(detail.getChangeDVO().getCreatetime());
				BRZ105 = this.toChineseYearMMdd(detail.getChangeDVO().getTRADE_DATE());

				// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
				// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
				BRZ107 = detail.getTradeMainVO().getBRANCH_NBR();
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
						BRZ107 = (String) loginBreach.get(0).get("BRANCH_NBR");
					} else {
						throw new APException("人員無有效分行"); //顯示錯誤訊息
					}
				} else if (StringUtils.equals(BRZ107, branchChgMap.get("BS").toString())) {
					BRZ107 = branchChgMap.get("DEFAULT").toString();
				}
				
				BRZ108 = detail.getChangeDVO().getModifier();
				BRZ109 = detail.getTradeMainVO().getCUST_ID();
				BRZ110 = detail.getChangeBatchVO().getCHANGE_TYPE();
				BRZ111 = StringUtils.equals(detail.getTradeMainVO().getIS_OBU(), "Y") ? "2" : "1";

				minorityYN = this.isCustAgeLessThan18(detail.getTradeMainVO().getCUST_ID(), " ");

				isOBU = detail.getTradeMainVO().getIS_OBU();

				NFBRN5InputVODetails repDetails = new NFBRN5InputVODetails();
				// 變更扣款日期
				if (StringUtils.equals(BRZ110, "A9")) {
					repDetails.setCertificateId(detail.getChangeDVO().getCERTIFICATE_ID());
					repDetails.setChargeDate1(StringUtils.isNotBlank(detail.getChangeDVO().getF_CHARGE_DATE_1()) ? detail.getChangeDVO().getF_CHARGE_DATE_1() : "00");
					repDetails.setChargeDate2(StringUtils.isNotBlank(detail.getChangeDVO().getF_CHARGE_DATE_2()) ? detail.getChangeDVO().getF_CHARGE_DATE_2() : "00");
					repDetails.setChargeDate3(StringUtils.isNotBlank(detail.getChangeDVO().getF_CHARGE_DATE_3()) ? detail.getChangeDVO().getF_CHARGE_DATE_3() : "00");
					repDetails.setChargeDate4(StringUtils.isNotBlank(detail.getChangeDVO().getF_CHARGE_DATE_4()) ? detail.getChangeDVO().getF_CHARGE_DATE_4() : "00");
					repDetails.setChargeDate5(StringUtils.isNotBlank(detail.getChangeDVO().getF_CHARGE_DATE_5()) ? detail.getChangeDVO().getF_CHARGE_DATE_5() : "00");
					repDetails.setChargeDate6(StringUtils.isNotBlank(detail.getChangeDVO().getF_CHARGE_DATE_6()) ? detail.getChangeDVO().getF_CHARGE_DATE_6() : "00");
				}
				// 變更扣款帳號
				else if (StringUtils.equals(BRZ110, "A8")) {
					repDetails.setCertificateId(detail.getChangeDVO().getCERTIFICATE_ID());
					repDetails.setDebitAcct(detail.getChangeDVO().getF_DEBIT_ACCT());
					repDetails.setDebitAcctId(detail.getTradeMainVO().getCUST_ID());
				}
				// 變更標的及金額 AX:標的變更 A7:金額變更 X7:兩者
				else if (StringUtils.equals(BRZ110, "AX") || StringUtils.equals(BRZ110, "A7") || StringUtils.equals(BRZ110, "X7")) {
					if (BRZ110.matches("AX|X7")) {
						repDetails.setProdInfoYN("Y"); // Y=要商品資訊
						prospectusType = detail.getChangeDVO().getPROSPECTUS_TYPE(); // 公開說明書交付方式
						narratorId = detail.getChangeDVO().getNARRATOR_ID(); // 解說專員
						recSeq = detail.getTradeMainVO().getREC_SEQ(); // 錄音序號
					}
					if (StringUtils.equals(BRZ110, "A7")) {
						repDetails.setProdInfoYN("N"); // N=無
					}
					BRZ110 = "X7"; // 瑞國說 AX/A7 ，ChangeItem 都用X7
					repDetails.setCertificateId(detail.getChangeDVO().getCERTIFICATE_ID());
					repDetails.setProdId(detail.getChangeDVO().getF_PROD_ID());
					repDetails.setPurchaseAmt1(String.valueOf(this.decimalPadding(detail.getChangeDVO().getF_PURCHASE_AMT_L(), 0)));
					repDetails.setPurchaseAmt2(String.valueOf(this.decimalPadding(detail.getChangeDVO().getF_PURCHASE_AMT_M(), 0)));
					repDetails.setPurchaseAmt3(String.valueOf(this.decimalPadding(detail.getChangeDVO().getF_PURCHASE_AMT_H(), 0)));

					// 同標的不需要傳ExchangeAmt
					if (!StringUtils.equals(detail.getChangeDVO().getB_PROD_ID(), detail.getChangeDVO().getF_PROD_ID())) {
						repDetails.setExchangeCurr(detail.getChangeDVO().getB_EXCH_CURR()); // 變更前每月扣款金額CALL 換匯電文回傳的幣別
						if (detail.getChangeDVO().getB_PURCHASE_AMT_EXCH_L() != null)
							repDetails.setExchangeAmt1(String.valueOf(this.decimalPadding(detail.getChangeDVO().getB_PURCHASE_AMT_EXCH_L(), 0))); // 變更前每月扣款金額CALL
																																					// 換匯電文回傳的金額_低
						if (detail.getChangeDVO().getB_PURCHASE_AMT_EXCH_M() != null)
							repDetails.setExchangeAmt2(String.valueOf(this.decimalPadding(detail.getChangeDVO().getB_PURCHASE_AMT_EXCH_M(), 0))); // 變更前每月扣款金額CALL
																																					// 換匯電文回傳的金額_中
						if (detail.getChangeDVO().getB_PURCHASE_AMT_EXCH_H() != null)
							repDetails.setExchangeAmt3(String.valueOf(this.decimalPadding(detail.getChangeDVO().getB_PURCHASE_AMT_EXCH_H(), 0))); // 變更前每月扣款金額CALL
																																					// 換匯電文回傳的金額_高
					}

				}
				// 暫停
				else if (StringUtils.equals(BRZ110, "B1")) {
					repDetails.setCertificateId(detail.getChangeDVO().getCERTIFICATE_ID());
					repDetails.setStartDate(this.toChineseYearMMdd(detail.getChangeDVO().getF_HOLD_START_DATE()));
					repDetails.setEndDate(this.toChineseYearMMdd(detail.getChangeDVO().getF_HOLD_END_DATE()));
				}
				// 恢復
				else if (StringUtils.equals(BRZ110, "B2")) {
					repDetails.setCertificateId(detail.getChangeDVO().getCERTIFICATE_ID());
					repDetails.setResumeDate(this.toChineseYearMMdd(detail.getChangeDVO().getF_RESUME_DATE()));
				}
				// 終止
				else if (StringUtils.equals(BRZ110, "B8")) {
					repDetails.setCertificateId(detail.getChangeDVO().getCERTIFICATE_ID());
				}
				// 變更收益入帳帳號
				else if (StringUtils.equals(BRZ110, "A0")) {
					repDetails.setCertificateId(detail.getChangeDVO().getCERTIFICATE_ID());
					repDetails.setCreditAcct(detail.getChangeDVO().getF_CREDIT_ACCT());
				}
				repeat.add(repDetails);
			}

			// init util
			Boolean checkOBU = StringUtils.equals("Y", isOBU) ? true : false;
			ESBUtilInputVO esbUtilInputVO;
			esbUtilInputVO = getTxInstance(ESB_TYPE, checkOBU ? OBU_NF_VERIFY_ESB_NF : NF_VERIFY_ESB_NF);

			esbUtilInputVO.setModule(thisClaz + new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			// body
			NFBRN5InputVO txBodyVO = new NFBRN5InputVO();
			esbUtilInputVO.setNfbrn5InputVO(txBodyVO);

			txBodyVO.setCheckCode(BRZ101); // 確認碼
			//			txBodyVO.setApplyDate(BRZ104); //申請日期
			txBodyVO.setEffDate(BRZ105); // 生效日
			txBodyVO.setApplyDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
			//			txBodyVO.setEffDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
			txBodyVO.setKeyinNo(BRZ106); // 理專登錄編號(批號batchSeq)
			txBodyVO.setBranchNo(BRZ107); // 交易行
			txBodyVO.setKeyinId(BRZ108); // 建機人員
			txBodyVO.setCustId(BRZ109); // 委託人統編
			txBodyVO.setChangeItem(BRZ110); // 變更事項
			txBodyVO.setDBUType(BRZ111); // 類別

			txBodyVO.setNarratorId(narratorId); // 推薦人ID (改標的)
			txBodyVO.setProspectusType(prospectusType); // 交付說明書 (改標的)
			txBodyVO.setRecSeq(recSeq); // 錄音序號 (改標的)

			txBodyVO.setMinorityYN(minorityYN); // 未成年
			txBodyVO.setDetails(repeat);

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {

				NFBRN5OutputVO nfbrn5OutputVO = checkOBU ? esbUtilOutputVO.getAfbrn5OutputVO() : esbUtilOutputVO.getNfbrn5OutputVO();

				// 若有錯誤訊息，Throw Exception
				throwError(nfbrn5OutputVO);

				List<NFBRN5InputVODetails> outputDetails = nfbrn5OutputVO.getDetails();
				outputDetails = (CollectionUtils.isEmpty(outputDetails)) ? new ArrayList<NFBRN5InputVODetails>() : outputDetails;
				for (NFBRN5InputVODetails detail : outputDetails) {
					// 若有錯誤訊息，Throw Exception
					throwError(detail);
				}
			}
		}
		return sot703OutputVO;
	}

	public List<String> selectConfirmESBChangeNFBatches(String tradeSeq) throws JBranchException {
		dam = getDataAccessManager();
		String sql = new StringBuffer("select BATCH_SEQ,count(1) as BATCH_COUNT from TBSOT_NF_CHANGE_BATCH where TRADE_SEQ = :TRADE_SEQ group by BATCH_SEQ ").toString();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADE_SEQ", tradeSeq);
		condition.setQueryString(sql);

		List<Map> list = dam.exeQuery(condition);

		List<String> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map map : list) {
				result.add((String) map.get("BATCH_SEQ"));
			}
		}

		return result;
	}

	private List getConfirmESBChangeNFDetails(String batchSeq) throws JBranchException {
		StringBuffer sql = new StringBuffer();
		sql.append("select D.*,M.CUST_ID,M.IS_OBU,M.BRANCH_NBR,B.BATCH_SEQ,B.CHANGE_TYPE as ITEM_TYPE ");
		sql.append("from TBSOT_NF_CHANGE_BATCH B ");
		sql.append("inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = B.TRADE_SEQ ");
		sql.append("inner join TBSOT_NF_CHANGE_D D on D.TRADE_SEQ = B.TRADE_SEQ and D.SEQ_NO = B.SEQ_NO ");
		sql.append("where B.BATCH_SEQ = :BATCH_SEQ ");
		sql.append("order by B.BATCH_NO ");

		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("BATCH_SEQ", batchSeq);
		condition.setQueryString(sql.toString());

		List<Map> list = dam.exeQuery(condition);

		List<DetailInfoBean> details = new ArrayList<DetailInfoBean>();
		if (CollectionUtils.isNotEmpty(list)) {
			DetailInfoBean bean = null;
			TBSOT_NF_CHANGE_DVO changeDVO = null;
			TBSOT_NF_CHANGE_BATCHVO changeBatchVO = null;
			TBSOT_TRADE_MAINVO tradeMainVO = null;
			// TBORG_MEMBERVO memberVO = null;
			// TBCRM_CUST_MASTVO custVO = null;

			for (Map map : list) {
				// setting DetailInfoBean
				bean = new DetailInfoBean();
				changeDVO = new TBSOT_NF_CHANGE_DVO();
				bean.setChangeDVO(changeDVO);
				changeBatchVO = new TBSOT_NF_CHANGE_BATCHVO();
				bean.setChangeBatchVO(changeBatchVO);
				tradeMainVO = new TBSOT_TRADE_MAINVO();
				bean.setTradeMainVO(tradeMainVO);
				// memberVO = new TBORG_MEMBERVO();
				// bean.setMemberVO(memberVO);
				// custVO = new TBCRM_CUST_MASTVO();
				// bean.setCustMastVO(custVO);

				tradeMainVO.setCUST_ID((String) map.get("CUST_ID"));
				tradeMainVO.setBRANCH_NBR((String) map.get("BRANCH_NBR"));
				// tradeMainVO.setIS_OBU((String) map.get("IS_OBU"));
				tradeMainVO.setIS_OBU(CharUtils.toString((Character) map.get("IS_OBU")));
				changeBatchVO.setBATCH_SEQ((String) map.get("BATCH_SEQ"));
				changeBatchVO.setCHANGE_TYPE((String) map.get("ITEM_TYPE"));
				changeDVO.setModifier((String) map.get("MODIFIER"));
				changeDVO.setCERTIFICATE_ID((String) map.get("CERTIFICATE_ID"));
				changeDVO.setF_CHARGE_DATE_1((String) map.get("F_CHARGE_DATE_1"));
				changeDVO.setF_CHARGE_DATE_2((String) map.get("F_CHARGE_DATE_2"));
				changeDVO.setF_CHARGE_DATE_3((String) map.get("F_CHARGE_DATE_3"));
				changeDVO.setF_CHARGE_DATE_4((String) map.get("F_CHARGE_DATE_4"));
				changeDVO.setF_CHARGE_DATE_5((String) map.get("F_CHARGE_DATE_5"));
				changeDVO.setF_CHARGE_DATE_6((String) map.get("F_CHARGE_DATE_6"));
				changeDVO.setF_DEBIT_ACCT((String) map.get("F_DEBIT_ACCT"));
				changeDVO.setF_PROD_ID((String) map.get("F_PROD_ID"));
				changeDVO.setB_PROD_ID((String) map.get("B_PROD_ID"));
				changeDVO.setB_TRUST_CURR((String) map.get("B_TRUST_CURR"));

				if (map.get("B_EXCH_CURR") != null)
					changeDVO.setB_EXCH_CURR(ObjectUtils.toString(map.get("B_EXCH_CURR")));// 變更前每月扣款金額CALL 換匯電文回傳的幣別

				if (map.get("B_PURCHASE_AMT_EXCH_L") != null)
					changeDVO.setB_PURCHASE_AMT_EXCH_L(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_EXCH_L"))));// 變更前每月扣款金額換匯_低
				if (map.get("B_PURCHASE_AMT_EXCH_M") != null)
					changeDVO.setB_PURCHASE_AMT_EXCH_M(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_EXCH_M"))));
				if (map.get("B_PURCHASE_AMT_EXCH_H") != null)
					changeDVO.setB_PURCHASE_AMT_EXCH_H(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_EXCH_H"))));

				if (map.get("B_PURCHASE_AMT_L") != null)
					changeDVO.setB_PURCHASE_AMT_L(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_L"))));// 變更前每月扣款金額換匯_低
				if (map.get("B_PURCHASE_AMT_M") != null)
					changeDVO.setB_PURCHASE_AMT_M(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_M"))));
				if (map.get("B_PURCHASE_AMT_H") != null)
					changeDVO.setB_PURCHASE_AMT_H(new BigDecimal(ObjectUtils.toString(map.get("B_PURCHASE_AMT_H"))));

				if (map.get("F_PURCHASE_AMT_L") != null)
					changeDVO.setF_PURCHASE_AMT_L(new BigDecimal(ObjectUtils.toString(map.get("F_PURCHASE_AMT_L"))));
				if (map.get("F_PURCHASE_AMT_M") != null)
					changeDVO.setF_PURCHASE_AMT_M(new BigDecimal(ObjectUtils.toString(map.get("F_PURCHASE_AMT_M"))));
				if (map.get("F_PURCHASE_AMT_H") != null)
					changeDVO.setF_PURCHASE_AMT_H(new BigDecimal(ObjectUtils.toString(map.get("F_PURCHASE_AMT_H"))));
				changeDVO.setF_TRUST_CURR((String) map.get("F_TRUST_CURR"));
				if (map.get("F_HOLD_START_DATE") != null)
					changeDVO.setF_HOLD_START_DATE(new Timestamp(((Date) map.get("F_HOLD_START_DATE")).getTime()));
				if (map.get("F_HOLD_END_DATE") != null)
					changeDVO.setF_HOLD_END_DATE(new Timestamp(((Date) map.get("F_HOLD_END_DATE")).getTime()));
				if (map.get("F_RESUME_DATE") != null)
					changeDVO.setF_RESUME_DATE(new Timestamp(((Date) map.get("F_RESUME_DATE")).getTime()));
				changeDVO.setF_CREDIT_ACCT((String) map.get("F_CREDIT_ACCT"));
				// custVO.setBIRTH_DATE((Timestamp) map.get("BIRTH_DATE"));
				changeDVO.setPROSPECTUS_TYPE(CharUtils.toString((Character) map.get("PROSPECTUS_TYPE"))); // 公開說明書交付方式
				changeDVO.setNARRATOR_ID((String) map.get("NARRATOR_ID"));
				changeDVO.setCreatetime((Timestamp) list.get(0).get("CREATETIME"));
				changeDVO.setTRADE_DATE((Timestamp) list.get(0).get("TRADE_DATE"));

				details.add(bean);
			}
		}
		return details;
	}

	/**
	 * 基金事件標的變更，取得不同幣別的等值金額
	 *
	 * 使用電文: NFBRN5 2016版本
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void getAmtByProdCurr(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getAmtByProdCurr(body));
	}

	/**
	 * X7 標的和金額變更 要查原扣金額換匯 2017.02版本
	 *
	 * @param bean
	 * @throws Exception
	 */
	public SOT703OutputVO getAmtByProdCurr(Object body) throws Exception {
		// (MainInfoBean bean) throws Exception{

		SOT150InputVO sot150InputVO = (SOT150InputVO) body;
		sot703OutputVO = new SOT703OutputVO();
		logger.debug("getAmtByProdCurr(MainInfoBean)");
		// init util
		Boolean isOBU = StringUtils.equals("Y", sot150InputVO.getIsOBU()) ? true : false;
		ESBUtilInputVO esbUtilInputVO;
		if (isOBU) {
			esbUtilInputVO = getTxInstance(ESB_TYPE, OBU_NF_VERIFY_ESB_NF);
		} else {
			esbUtilInputVO = getTxInstance(ESB_TYPE, NF_VERIFY_ESB_NF);
		}
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		NFBRN5InputVO txBodyVO = new NFBRN5InputVO();
		esbUtilInputVO.setNfbrn5InputVO(txBodyVO);
		txBodyVO.setDetails(new ArrayList<NFBRN5InputVODetails>());

		txBodyVO.setCheckCode("1");
		//		txBodyVO.setApplyDate(this.toChineseYearMMdd(new Date()));
		txBodyVO.setApplyDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		Date tradeDate = new Date(); // TODO 一定要傳tradeDate，但選預約日查換匯是有問題的
		//		txBodyVO.setEffDate(this.toChineseYearMMdd(tradeDate));
		txBodyVO.setEffDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		//TODO       txBodyVO.setBranchNo(String.valueOf(getCommonVariable(SystemVariableConsts.LOGINBRH)));
		txBodyVO.setKeyinId(DataManager.getWorkStation(uuid).getUser().getUserID());
		txBodyVO.setCustId(sot150InputVO.getCustID());

		txBodyVO.setDBUType(StringUtils.equals(sot150InputVO.getIsOBU(), "Y") ? "2" : "1");
		txBodyVO.setChangeItem("X7"); // 不管標的變 或 只有金額 都用 X7發電文
		String narratorId = sot150InputVO.getNarratorID(); // 解說專員
		txBodyVO.setNarratorId(narratorId); // 解說專員(當變更標的)
		txBodyVO.setProspectusType(sot150InputVO.getProspectusType()); // 公開說明書交付方式
		NFBRN5InputVODetails list = new NFBRN5InputVODetails();
		list.setProdInfoYN("Y");// Y=要商品資訊 N=無

		list.setCertificateId(StringUtils.trim(sot150InputVO.getCertificateID()));
		list.setProdId(sot150InputVO.getfProdID());
		BigDecimal zero = BigDecimal.ZERO;

		// 換匯要用庫存金額查詢
		if (FundRule.isMultiple(sot150InputVO.getTradeSubType())) { // 定期定額
			if (sot150InputVO.getbPurchaseAmtL() != null && !zero.equals(sot150InputVO.getbPurchaseAmtL()))
				list.setPurchaseAmt1(String.valueOf(this.decimalPadding(sot150InputVO.getbPurchaseAmtL(), 0)));
		} else if (FundRule.isMultipleN(sot150InputVO.getTradeSubType())) { // 定期不定額
			if (sot150InputVO.getbPurchaseAmtL() != null && !zero.equals(sot150InputVO.getbPurchaseAmtL()))
				list.setPurchaseAmt1(String.valueOf(this.decimalPadding(sot150InputVO.getbPurchaseAmtL(), 0)));
			if (sot150InputVO.getbPurchaseAmtM() != null && !zero.equals(sot150InputVO.getbPurchaseAmtM()))
				list.setPurchaseAmt2(String.valueOf(this.decimalPadding(sot150InputVO.getbPurchaseAmtM(), 0)));
			if (sot150InputVO.getbPurchaseAmtH() != null && !zero.equals(sot150InputVO.getbPurchaseAmtH()))
				list.setPurchaseAmt3(String.valueOf(this.decimalPadding(sot150InputVO.getbPurchaseAmtH(), 0)));
		}
		txBodyVO.getDetails().add(list);

		String recSeq = null; // 錄音續序號(變標的)

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN5OutputVO nfbrn5OutputVO;
			if (isOBU) {
				nfbrn5OutputVO = esbUtilOutputVO.getAfbrn5OutputVO();
			} else {
				nfbrn5OutputVO = esbUtilOutputVO.getNfbrn5OutputVO();
			}

			// 回傳錯誤訊息
			if (checkError(nfbrn5OutputVO))
				break;

			List<NFBRN5InputVODetails> outputDetails = nfbrn5OutputVO.getDetails();
			outputDetails = (CollectionUtils.isEmpty(outputDetails)) ? new ArrayList<NFBRN5InputVODetails>() : outputDetails;
			for (NFBRN5InputVODetails detail : outputDetails) {
				sot703OutputVO.setExchangeCurr(detail.getExchangeCurr()); // 不同標的
																			// 同幣別
																			// 也要查換匯

				if (detail.getExchangeAmt1() != null)
					sot703OutputVO.setExchangeAmt1(this.decimalPoint(detail.getExchangeAmt1(), 0).toString());
				if (detail.getExchangeAmt2() != null)
					sot703OutputVO.setExchangeAmt2(this.decimalPoint(detail.getExchangeAmt2(), 0).toString());
				if (detail.getExchangeAmt3() != null)
					sot703OutputVO.setExchangeAmt3(this.decimalPoint(detail.getExchangeAmt3(), 0).toString());

				// 若有錯誤訊息，回傳錯誤訊息
				checkError(detail);
				return sot703OutputVO;
			}

		}

		return sot703OutputVO;
	}

	/**
	 * 基金庫存資料查詢
	 *
	 * 使用電文: NFBRN9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void getCustAssetNFData(Object body, IPrimitiveMap header) throws Exception {
		List<CustAssetFundVO> total = new ArrayList<CustAssetFundVO>();
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();
		String custId = sot703InputVO.getCustId();
		//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
		String trustTS = sot703InputVO.getTrustTS();
		String debitAcct = sot703InputVO.getDebitAcct();
		Boolean isOBU = sot701.isObu(sot703InputVO.getCustId());
		String returnNumZeroYN = sot703InputVO.getReturnNumZeroYN();
		
		// 欄位檢核
		if (StringUtils.isBlank(custId)) {
			throw new JBranchException("客戶ID未輸入");
		}
		if (StringUtils.isNotBlank(trustTS) && StringUtils.equals(trustTS, "M")) {
			//金錢信託查詢庫存, 扣款帳號必須輸入
			if (StringUtils.isBlank(debitAcct)) {
				throw new JBranchException("金錢信託交易,扣款帳號為必要條件");
			}
		}
		
		if(StringUtils.equals(trustTS, "M")){
			this.getCustAssetNFData(custId, debitAcct, total, "XF", returnNumZeroYN);
		}else {
			this.getCustAssetNFData(custId, debitAcct, total, "NF", returnNumZeroYN);
			if(isOBU){
				this.getCustAssetNFData(custId, debitAcct, total, "AF", returnNumZeroYN);
			}
		}
				
		sot703OutputVO.setCustAssetFundList(total);
		sendRtnObject(sot703OutputVO);
		
//		sendRtnObject(this.getCustAssetNFData(body));
	}
    /**
     * 2021.09.13 DBU身分只打NF電文 OBU身分則打NF+AF電文 
     * @param custId
     * @param debitAcct  XF的情況上行要帶此變數
     * @param total
     * @param mark 請輸入NF/AF/XF來指定發送哪道電文
     * @throws Exception
     * @author SamTu
     */
	private void getCustAssetNFData(String custId, String debitAcct, List<CustAssetFundVO> total, String mark, String returnNumZeroYN) throws Exception {
		Boolean isAF = StringUtils.equals(mark, "AF");
		Boolean isXF = StringUtils.equals(mark, "XF");
				
		// init util
		ESBUtilInputVO esbUtilInputVO;
		if (isAF) {
			esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_OBU);
		} else if (isXF) { //金錢信託
			esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_XF_DBU);
		} else {
			esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_DBU);
		}
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		//		txHead.setHFMTID("0001");
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		if (isAF) {
			AFBRN9InputVO txBodyVO = new AFBRN9InputVO();
			esbUtilInputVO.setAfbrn9InputVO(txBodyVO);
			txBodyVO.setCustId(custId);
		} else if (isXF) { //金錢信託
			NFBRX9InputVO txBodyVO = new NFBRX9InputVO();
			esbUtilInputVO.setNfbrx9InputVO(txBodyVO);
			txBodyVO.setCustId("99331241");
			txBodyVO.setDebitACCT(debitAcct);
		} else {
			NFBRN9InputVO txBodyVO = new NFBRN9InputVO();
			esbUtilInputVO.setNfbrn9InputVO(txBodyVO);
			txBodyVO.setCustId(custId);
		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();

			String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
			/**
			 * RN9的下行電文會有5個Format的格式 
			 * Header.‧HFMTID = 0001：單筆 
			 * Header.‧HFMTID = 0002：定期定額 
			 * Header.‧HFMTID = 0003：定期不定額 
			 * Header.‧HFMTID = 0004：定存轉基金
			 * Header.‧HFMTID = 0005：基金套餐
			 */
			if ("0001".equals(hfmtid)) { //單筆
				NFBRN9OutputVO outputVO;
				if (isAF) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (isXF) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 動態鎖利不需要判斷單位數>0，一律回傳前端
					if (StringUtils.isNotBlank(ObjectUtils.toString(devo.getDynamic())) || checkCustAssetFundVO(devo)) {	//判斷單位數 > 0
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid); // 信託種類
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setAcctId16(devo.getAcctId16());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setProjectCode(devo.getProjectCode());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAcctId(devo.getPayAcctId());
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記

						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));

						retVO.setDynamic(devo.getDynamic());
						retVO.setComboReturnDate(devo.getComboReturnDate());
						retVO.setComboReturnSign(devo.getComboReturnSign());
						retVO.setComboReturn(decimalPoint(devo.getComboReturn(), 2));
						retVO.setSatelliteBuyDate1(devo.getSatelliteBuyDate1());
						retVO.setSatelliteBuyDate2(devo.getSatelliteBuyDate2());
						retVO.setSatelliteBuyDate3(devo.getSatelliteBuyDate3());
						retVO.setSatelliteBuyDate4(devo.getSatelliteBuyDate4());
						retVO.setSatelliteBuyDate5(devo.getSatelliteBuyDate5());
						retVO.setSatelliteBuyDate6(devo.getSatelliteBuyDate6());
						retVO.setBenefitReturnRate1(decimalPoint(devo.getBenefitReturnRate1(), 2));
						retVO.setBenefitReturnRate2(decimalPoint(devo.getBenefitReturnRate2(), 2));
						retVO.setBenefitReturnRate3(decimalPoint(devo.getBenefitReturnRate3(), 2));
						retVO.setTRANSFERAmt(decimalPoint(devo.getTRANSFERAmt(), 0));
						retVO.setEviNumType(devo.getEviNumType());
						
						total.add(retVO);
					}
				}
			} else if ("0002".equals(hfmtid)) { // 定期定額
				NFBRN9OutputVO outputVO;
				if (isAF) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (isXF) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					// if(checkCustAssetFundVO(devo)){
					if (true) { // 測試用 TODO
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid);
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
						retVO.setTransferDate01(devo.getTransferDate01());
						retVO.setTransferDate02(devo.getTransferDate02());
						retVO.setTransferDate03(devo.getTransferDate03());
						retVO.setTransferDate04(devo.getTransferDate04());
						retVO.setTransferDate05(devo.getTransferDate05());
						retVO.setTransferDate06(devo.getTransferDate06());
						retVO.setTransferCount(devo.getTransferCount());
						retVO.setPayCount(devo.getPayCount());
						retVO.setStatus(devo.getStatus());
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setProjectCode(devo.getProjectCode());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAcctId(devo.getPayAcctId());
						retVO.setPayAccountNo(devo.getPayAccountNo());
						retVO.setTxType(devo.getTxType());
						retVO.setFrgnPurchaseFlag(devo.getFrgnPurchaseFlag());
						retVO.setSame(devo.getSame());
						retVO.setLongDiscount(decimalPoint(devo.getLongDiscount(), 2));
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
						total.add(retVO);
					}
				}
			} else if ("0003".equals(hfmtid)) {// 定期不定額
				NFBRN9OutputVO outputVO;
				if (isAF) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (isXF) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					// if(checkCustAssetFundVO(devo)){
					if (true) { // 測試用 TODO
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid);
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setTransferAmt_H(decimalPoint(devo.getTransferAmt_H(), 0));
						retVO.setTransferAmt_M(decimalPoint(devo.getTransferAmt_M(), 0));
						retVO.setTransferAmt_L(decimalPoint(devo.getTransferAmt_L(), 0));
						retVO.setTransferDate01(devo.getTransferDate01());
						retVO.setTransferDate02(devo.getTransferDate02());
						retVO.setTransferDate03(devo.getTransferDate03());
						retVO.setTransferDate04(devo.getTransferDate04());
						retVO.setTransferDate05(devo.getTransferDate05());
						retVO.setTransferDate06(devo.getTransferDate06());
						retVO.setTransferCount(devo.getTransferCount());
						retVO.setPayCount(devo.getPayCount());
						retVO.setStatus(devo.getStatus());
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setProjectCode(devo.getProjectCode());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAcctId(devo.getPayAcctId());
						retVO.setPayAccountNo(devo.getPayAccountNo());
						retVO.setTxType(devo.getTxType());
						retVO.setFrgnPurchaseFlag(devo.getFrgnPurchaseFlag());
						retVO.setSame(devo.getSame());
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
						total.add(retVO);
					}
				}
			} else if ("0004".equals(hfmtid)) {
				NFBRN9OutputVO outputVO;
				if (isAF) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (isXF) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					// if(checkCustAssetFundVO(devo)){
					if (true) { // 測試用 TODO
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid);
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
						retVO.setTransferDate01(devo.getTransferDate01());
						retVO.setTransferDate02(devo.getTransferDate02());
						retVO.setTransferDate03(devo.getTransferDate03());
						retVO.setTransferDate04(devo.getTransferDate04());
						retVO.setTransferDate05(devo.getTransferDate05());
						retVO.setTransferDate06(devo.getTransferDate06());
						retVO.setTransferCount(devo.getTransferCount());
						retVO.setPayCount(devo.getPayCount());
						retVO.setStatus(devo.getStatus());
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAccountNo(devo.getPayAccountNo());
						retVO.setAcctId(devo.getAcctId());
						retVO.setTimeDepositPrjCd(devo.getTimeDepositPrjCd());
						retVO.setTotal_Cnt(devo.getTotal_Cnt());
						retVO.setPay_Cnt(devo.getPay_Cnt());
						retVO.setEnd_Flg(devo.getEnd_Flg());
						retVO.setSame(devo.getSame());
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
						total.add(retVO);
					}
				}
			} else if ("0005".equals(hfmtid)) {
				NFBRN9OutputVO outputVO;
				if (isAF) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (isXF) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					if (true) { // 測試用 TODO
						// if(checkCustAssetFundVO(devo)){
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid);
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setAcctId16(devo.getAcctId16());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
						retVO.setTransferDate01(devo.getTransferDate01());
						retVO.setTransferDate02(devo.getTransferDate02());
						retVO.setTransferDate03(devo.getTransferDate03());
						retVO.setTransferDate04(devo.getTransferDate04());
						retVO.setTransferDate05(devo.getTransferDate05());
						retVO.setTransferDate06(devo.getTransferDate06());
						retVO.setTransferCount(devo.getTransferCount());
						retVO.setPayCount(devo.getPayCount());
						retVO.setStatus(devo.getStatus());
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setProjectCode(devo.getProjectCode());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAcctId(devo.getPayAcctId());
						retVO.setPayAccountNo(devo.getPayAccountNo());
						retVO.setTxType(devo.getTxType());
						retVO.setSame(devo.getSame());
						retVO.setFundPackageNo(devo.getFundPackageNo());
						retVO.setFundPackage(devo.getFundPackage());
						retVO.setEnd_Flg(devo.getEnd_Flg());
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
						total.add(retVO);
					}
				}
			}
		}

		Collections.sort(total, new Comparator<CustAssetFundVO>() {
			public int compare(CustAssetFundVO o1, CustAssetFundVO o2) {
				if (StringUtils.isEmpty(o1.getEviNum())) {
					return -1;
				}
				if (StringUtils.isEmpty(o2.getEviNum())) {
					return 1;
				}
				return (o1.getEviNum()).compareTo(o2.getEviNum());
			}
		});
	}

	public SOT703OutputVO getCustAssetNFData(Object body) throws Exception {
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();

		String custId = sot703InputVO.getCustId();
		//WMS-CR-20191009-01_金錢信託套表需求申請單 2020-2-10 add by Jacky
		String trustTS = sot703InputVO.getTrustTS();
		String debitAcct = sot703InputVO.getDebitAcct();

		// 欄位檢核
		if (StringUtils.isBlank(custId)) {
			throw new JBranchException("客戶ID未輸入");
		}
		if (StringUtils.isNotBlank(trustTS) && StringUtils.equals(trustTS, "M")) {
			//金錢信託查詢庫存, 扣款帳號必須輸入
			if (StringUtils.isBlank(debitAcct)) {
				throw new JBranchException("金錢信託交易,扣款帳號為必要條件");
			}
		}
		Boolean isOBU = sot701.isObu(sot703InputVO.getCustId());
		// init util
		ESBUtilInputVO esbUtilInputVO;
		if (isOBU) {
			esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_OBU);
		} else if (StringUtils.equals(trustTS, "M")) { //金錢信託
			esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_XF_DBU);
		} else {
			esbUtilInputVO = getTxInstance(ESB_TYPE, CUST_ASSET_NF_DBU);
		}
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		//		txHead.setHFMTID("0001");
		esbUtilInputVO.setTxHeadVO(txHead);

		// body
		if (isOBU) {
			AFBRN9InputVO txBodyVO = new AFBRN9InputVO();
			esbUtilInputVO.setAfbrn9InputVO(txBodyVO);
			txBodyVO.setCustId(custId);
		} else if (StringUtils.equals(trustTS, "M")) { //金錢信託
			NFBRX9InputVO txBodyVO = new NFBRX9InputVO();
			esbUtilInputVO.setNfbrx9InputVO(txBodyVO);
			txBodyVO.setCustId("99331241");
			txBodyVO.setDebitACCT(debitAcct);
		} else {
			NFBRN9InputVO txBodyVO = new NFBRN9InputVO();
			esbUtilInputVO.setNfbrn9InputVO(txBodyVO);
			txBodyVO.setCustId(custId);
		}

		List<CustAssetFundVO> total = new ArrayList<CustAssetFundVO>();

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			TxHeadVO headVO = esbUtilOutputVO.getTxHeadVO();

			String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();
			/**
			 * RN9的下行電文會有5個Format的格式 Header.‧HFMTID =0001：單筆 Header.‧HFMTID
			 * =0002：定期定額 Header.‧HFMTID =0003：定期不定額 Header.‧HFMTID =0004：定存轉基金
			 * Header.‧HFMTID =0005：基金套餐
			 */
			if ("0001".equals(hfmtid)) {
				NFBRN9OutputVO outputVO;
				if (isOBU) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (StringUtils.equals(trustTS, "M")) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					if (checkCustAssetFundVO(devo) || StringUtils.isNotBlank(devo.getDynamic())) {
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid); // 信託種類
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setAcctId16(devo.getAcctId16());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setProjectCode(devo.getProjectCode());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAcctId(devo.getPayAcctId());
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記

						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));

						retVO.setDynamic(devo.getDynamic());
						retVO.setComboReturnDate(devo.getComboReturnDate());
						retVO.setComboReturnSign(devo.getComboReturnSign());
						retVO.setComboReturn(decimalPoint(devo.getComboReturn(), 2));
						retVO.setSatelliteBuyDate1(devo.getSatelliteBuyDate1());
						retVO.setSatelliteBuyDate2(devo.getSatelliteBuyDate2());
						retVO.setSatelliteBuyDate3(devo.getSatelliteBuyDate3());
						retVO.setSatelliteBuyDate4(devo.getSatelliteBuyDate4());
						retVO.setSatelliteBuyDate5(devo.getSatelliteBuyDate5());
						retVO.setSatelliteBuyDate6(devo.getSatelliteBuyDate6());
						retVO.setBenefitReturnRate1(decimalPoint(devo.getBenefitReturnRate1(), 2));
						retVO.setBenefitReturnRate2(decimalPoint(devo.getBenefitReturnRate2(), 2));
						retVO.setBenefitReturnRate3(decimalPoint(devo.getBenefitReturnRate3(), 2));
						retVO.setTRANSFERAmt(decimalPoint(devo.getTRANSFERAmt(), 0));
						retVO.setEviNumType(devo.getEviNumType());
						
						total.add(retVO);
					}
				}
			} else if ("0002".equals(hfmtid)) { // 定期定額
				NFBRN9OutputVO outputVO;
				if (isOBU) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (StringUtils.equals(trustTS, "M")) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					// if(checkCustAssetFundVO(devo)){
					if (true) { // 測試用 TODO
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid);
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
						retVO.setTransferDate01(devo.getTransferDate01());
						retVO.setTransferDate02(devo.getTransferDate02());
						retVO.setTransferDate03(devo.getTransferDate03());
						retVO.setTransferDate04(devo.getTransferDate04());
						retVO.setTransferDate05(devo.getTransferDate05());
						retVO.setTransferDate06(devo.getTransferDate06());
						retVO.setTransferCount(devo.getTransferCount());
						retVO.setPayCount(devo.getPayCount());
						retVO.setStatus(devo.getStatus());
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setProjectCode(devo.getProjectCode());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAcctId(devo.getPayAcctId());
						retVO.setPayAccountNo(devo.getPayAccountNo());
						retVO.setTxType(devo.getTxType());
						retVO.setFrgnPurchaseFlag(devo.getFrgnPurchaseFlag());
						retVO.setSame(devo.getSame());
						retVO.setLongDiscount(decimalPoint(devo.getLongDiscount(), 2));
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
						total.add(retVO);
					}
				}
			} else if ("0003".equals(hfmtid)) {// 定期不定額
				NFBRN9OutputVO outputVO;
				if (isOBU) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (StringUtils.equals(trustTS, "M")) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					// if(checkCustAssetFundVO(devo)){
					if (true) { // 測試用 TODO
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid);
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setTransferAmt_H(decimalPoint(devo.getTransferAmt_H(), 0));
						retVO.setTransferAmt_M(decimalPoint(devo.getTransferAmt_M(), 0));
						retVO.setTransferAmt_L(decimalPoint(devo.getTransferAmt_L(), 0));
						retVO.setTransferDate01(devo.getTransferDate01());
						retVO.setTransferDate02(devo.getTransferDate02());
						retVO.setTransferDate03(devo.getTransferDate03());
						retVO.setTransferDate04(devo.getTransferDate04());
						retVO.setTransferDate05(devo.getTransferDate05());
						retVO.setTransferDate06(devo.getTransferDate06());
						retVO.setTransferCount(devo.getTransferCount());
						retVO.setPayCount(devo.getPayCount());
						retVO.setStatus(devo.getStatus());
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setProjectCode(devo.getProjectCode());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAcctId(devo.getPayAcctId());
						retVO.setPayAccountNo(devo.getPayAccountNo());
						retVO.setTxType(devo.getTxType());
						retVO.setFrgnPurchaseFlag(devo.getFrgnPurchaseFlag());
						retVO.setSame(devo.getSame());
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
						total.add(retVO);
					}
				}
			} else if ("0004".equals(hfmtid)) {
				NFBRN9OutputVO outputVO;
				if (isOBU) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (StringUtils.equals(trustTS, "M")) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					// if(checkCustAssetFundVO(devo)){
					if (true) { // 測試用 TODO
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid);
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
						retVO.setTransferDate01(devo.getTransferDate01());
						retVO.setTransferDate02(devo.getTransferDate02());
						retVO.setTransferDate03(devo.getTransferDate03());
						retVO.setTransferDate04(devo.getTransferDate04());
						retVO.setTransferDate05(devo.getTransferDate05());
						retVO.setTransferDate06(devo.getTransferDate06());
						retVO.setTransferCount(devo.getTransferCount());
						retVO.setPayCount(devo.getPayCount());
						retVO.setStatus(devo.getStatus());
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAccountNo(devo.getPayAccountNo());
						retVO.setAcctId(devo.getAcctId());
						retVO.setTimeDepositPrjCd(devo.getTimeDepositPrjCd());
						retVO.setTotal_Cnt(devo.getTotal_Cnt());
						retVO.setPay_Cnt(devo.getPay_Cnt());
						retVO.setEnd_Flg(devo.getEnd_Flg());
						retVO.setSame(devo.getSame());
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
						total.add(retVO);
					}
				}
			} else if ("0005".equals(hfmtid)) {
				NFBRN9OutputVO outputVO;
				if (isOBU) {
					outputVO = esbUtilOutputVO.getAfbrn9OutputVO();
				} else if (StringUtils.equals(trustTS, "M")) { //金錢信託
					outputVO = esbUtilOutputVO.getNfbrx9OutputVO();
				} else {
					outputVO = esbUtilOutputVO.getNfbrn9OutputVO();
				}

				for (NFBRN9OutputVODetailsVO devo : outputVO.getDetails()) {
					// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端
					if (true) { // 測試用 TODO
						// if(checkCustAssetFundVO(devo)){
						CustAssetFundVO retVO = new CustAssetFundVO();
						retVO.setAssetType(hfmtid);
						retVO.setSPRefId(devo.getSPRefId());
						retVO.setAcctId16(devo.getAcctId16());
						retVO.setOccur(devo.getOccur());
						retVO.setAcctId02(devo.getAcctId02());
						retVO.setEviNum(devo.getEviNum());
						retVO.setFundNO(devo.getFundNO());
						retVO.setFundName(devo.getFundName());
						retVO.setCurFund(devo.getCurFund());
						retVO.setCurCode(devo.getCurCode());
						retVO.setCurAmt(decimalPoint(devo.getCurAmt(), 2));
						retVO.setCurAmtNT(decimalPoint(devo.getCurAmtNT(), 2));
						retVO.setCurBal(decimalPoint(devo.getCurBal(), 2));
						retVO.setCurBalNT(decimalPoint(devo.getCurBalNT(), 2));
						retVO.setProfitAndLoss(decimalPoint(devo.getProfitAndLoss(), 2));
						retVO.setIncrease(decimalPoint(devo.getIncrease(), 2));
						retVO.setSignDigit(devo.getSignDigit());
						retVO.setReturn(decimalPoint(devo.getReturn(), 2));
						retVO.setRewRateDigit(devo.getRewRateDigit());
						retVO.setAccAllocateRewRate(decimalPoint(devo.getAccAllocateRewRate(), 2));
						retVO.setCurUntNum(decimalPoint(devo.getCurUntNum(), 4));
						retVO.setReferenceExchangeRate(decimalPoint(devo.getReferenceExchangeRate(), 4));
						retVO.setNetValueDate(devo.getNetValueDate());
						retVO.setNetValue(decimalPoint(devo.getNetValue(), 4));
						retVO.setTransferAmt(decimalPoint(devo.getTransferAmt(), 0));
						retVO.setTransferDate01(devo.getTransferDate01());
						retVO.setTransferDate02(devo.getTransferDate02());
						retVO.setTransferDate03(devo.getTransferDate03());
						retVO.setTransferDate04(devo.getTransferDate04());
						retVO.setTransferDate05(devo.getTransferDate05());
						retVO.setTransferDate06(devo.getTransferDate06());
						retVO.setTransferCount(devo.getTransferCount());
						retVO.setPayCount(devo.getPayCount());
						retVO.setStatus(devo.getStatus());
						retVO.setStoplossSign(devo.getStoplossSign());
						retVO.setStoploss(decimalPoint(devo.getStoploss(), 2));
						retVO.setSatisfiedSign(devo.getSatisfiedSign());
						retVO.setSatisfied(decimalPoint(devo.getSatisfied(), 2));
						retVO.setStrdate(devo.getStrdate());
						retVO.setFundType(devo.getFundType());
						retVO.setApproveFlag(devo.getApproveFlag());
						retVO.setProjectCode(devo.getProjectCode());
						retVO.setGroupCode(devo.getGroupCode());
						retVO.setPayAcctId(devo.getPayAcctId());
						retVO.setPayAccountNo(devo.getPayAccountNo());
						retVO.setTxType(devo.getTxType());
						retVO.setSame(devo.getSame());
						retVO.setFundPackageNo(devo.getFundPackageNo());
						retVO.setFundPackage(devo.getFundPackage());
						retVO.setEnd_Flg(devo.getEnd_Flg());
						retVO.setAccAllocateRew(decimalPoint(devo.getAccAllocateRew(), 2));
						retVO.setIsPledged(StringUtils.isEmpty(devo.getCmkType()) ? "N" : StringUtils.equals(devo.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); // 質借圈存註記
						if (retVO.getAcctId02() != null)
							retVO.setAcctId02(StringUtils.trim(retVO.getAcctId02()));
						if (retVO.getPayAcctId() != null)
							retVO.setPayAcctId(StringUtils.trim(retVO.getPayAcctId()));
						if (retVO.getPayAccountNo() != null)
							retVO.setPayAccountNo(StringUtils.trim(retVO.getPayAccountNo()));
						total.add(retVO);
					}
				}
			}
		}

		Collections.sort(total, new Comparator<CustAssetFundVO>() {
			public int compare(CustAssetFundVO o1, CustAssetFundVO o2) {
				if (StringUtils.isEmpty(o1.getEviNum())) {
					return -1;
				}
				if (StringUtils.isEmpty(o2.getEviNum())) {
					return 1;
				}
				return (o1.getEviNum()).compareTo(o2.getEviNum());
			}
		});

		sot703OutputVO.setCustAssetFundList(total);
		return sot703OutputVO;
	}

	/**
	 * 檢查可用庫存(憑證,判斷單位數 > 0 )
	 *
	 * @param devo
	 * @return
	 */
	private boolean checkCustAssetFundVO(NFBRN9OutputVODetailsVO devo) {

		// 2017-01-18 by Jacky 判斷單位數 > 0 才回傳前端 CurUntNum庫存單位數不能為0
		if (decimalPoint(devo.getCurUntNum(), 4).compareTo(BigDecimal.ZERO) == 1) {
			/*
			 * 瑞國 事件變更 可用憑證 if
			 * ("1110000097".equals(StringUtils.trim(devo.getEviNum())) ||
			 * "1110000098".equals(StringUtils.trim(devo.getEviNum()))) {
			 * logger.error("test SOT150 checkCustAssetFundVO "); return true; }
			 * if (devo.getFundNO().matches("5232|5108|5116|5119|5124")) { //
			 * TODO logger.error(
			 * "test SOT150 checkCustAssetFundVO 5232|5108|5116|5119|5124");
			 * return true; }
			 */
			return true;
		}
		return false;
	}

	/**
	 * 客戶年齡小於18 Y or ""
	 *
	 * @param custID
	 * @return
	 * @throws JBranchException
	 */
	protected String isCustAgeLessThan18(String custID, String falseStr) throws JBranchException {
		SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
		SOT712InputVO sot712InputVO = new SOT712InputVO();
		sot712InputVO.setCustID(custID);
		return (sot712.getCUST_AGE(sot712InputVO) < 18) ? "Y" : falseStr;
	}

	/**
	 * 查詢基金註記 : NO_E_PURCHASE 不可申購註記 NO_E_OUT 不可轉出註記 NO_E_IN 不可轉入註記 NO_E_BUYBACK
	 * 不可贖回註記
	 *
	 * @param body
	 * @param header
	 */
	public void qryFundMemo(Object body, IPrimitiveMap header) throws JBranchException {
		this.sendRtnObject(exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder().append("select decode(NO_E_PURCHASE, 'Y', '本基金停止申購', '') NO_E_PURCHASE, ").append("       decode(NO_E_OUT, 'Y', '本基金停止轉出', '') NO_E_OUT, ").append("       decode(NO_E_IN, 'Y', '本基金停止轉入', '') NO_E_IN, ").append("       decode(NO_E_BUYBACK, 'Y', '本基金停止贖回', '') NO_E_BUYBACK ").append("from TBPRD_FUNDINFO where PRD_ID = :prdId ").toString()).setObject("prdId", ((SOT703InputVO) body).getProdId())));
	}

	//上送group_ifa 因為ID用99331241
	//金錢信託單筆申購，若原客戶為薪轉戶，不可帶入優惠代號為TG
	//但金錢信託小額，都會走議價，這欄位會給折數(ex: 90)
	//用Interger.parse try catch決定送不送
	protected boolean checkInt(String str) {
		if (StringUtils.isBlank(str)) {
			return true;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/***
	 * 境外私募基金贖回調整單位數電文
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT703OutputVO adjESBRedeemNFOvsPri(Object body) throws Exception {
		sot703InputVO = (SOT703InputVO) body;
		sot703OutputVO = new SOT703OutputVO();
		String prdSeqNo = sot703InputVO.getSeqNo(); //境外私募基金商品檔序號 TBPRD_FUND_OVS_PRIVATE.SEQ_NO

		// 欄位檢核
		if (StringUtils.isBlank(prdSeqNo)) {
			throw new JBranchException("境外私募基金商品檔序號未輸入");
		}
		
		// 取得主檔資料
		dam = getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT A.*, C.IS_OBU ");
		sb.append(" FROM TBSOT_NF_REDEEM_D_OVS_PRI A ");
		sb.append(" LEFT JOIN TBSOT_NF_REDEEM_D B ON B.BATCH_SEQ = A.BATCH_SEQ AND B.RDM_CERTIFICATE_ID = A.CERTIFICATE_ID ");
		sb.append(" LEFT JOIN TBSOT_TRADE_MAIN C ON C.TRADE_SEQ = B.TRADE_SEQ ");
		sb.append(" WHERE A.PRD_SEQ_NO = :prdSeqNo ");
		queryCondition.setObject("prdSeqNo", new BigDecimal(prdSeqNo));
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		for(Map<String, Object> pvo: list) {
			//init util
	 		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, StringUtils.equals("Y", pvo.get("IS_OBU").toString()) ? AFBRNE : NFBRNE);
	 		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());
	 		//head
	 		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
	 		txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);
	 		//body
			NFBRNEInputVO nfbrneInputVO = new NFBRNEInputVO();
			nfbrneInputVO.setApplyDate(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
			nfbrneInputVO.setEffDate(this.toChineseYearMMdd((Timestamp)pvo.get("RDM_TRADE_DATE")));
			nfbrneInputVO.setKeyinNo((String)pvo.get("BATCH_SEQ"));
			nfbrneInputVO.setCustId((String)pvo.get("CUST_ID"));
			nfbrneInputVO.setEviNum((String)pvo.get("CERTIFICATE_ID"));
			nfbrneInputVO.setProdId((String)pvo.get("PRD_ID"));
			nfbrneInputVO.setUnitNum(String.valueOf(new EsbUtil().decimalPadding((BigDecimal)pvo.get("RDM_UNIT"), 4)));
			nfbrneInputVO.setRdmNum(String.valueOf(new EsbUtil().decimalPadding((BigDecimal)pvo.get("UNIT_NUM"), 4)));
			nfbrneInputVO.setAdjRdmNum(String.valueOf(new EsbUtil().decimalPadding((BigDecimal)pvo.get("ADJ_UNIT_NUM"), 4)));
	        esbUtilInputVO.setNfbrneInputVO(nfbrneInputVO);

	        //發送電文
	        try {
	        	NFBRNEOutputVO nfbrneOutputVO = new NFBRNEOutputVO();
	        	
	        	List<ESBUtilOutputVO> vos = send(esbUtilInputVO);	        
		        for (ESBUtilOutputVO esbUtilOutputVO : vos) {
		        	if(StringUtils.equals("Y", pvo.get("IS_OBU").toString())) {
		        		nfbrneOutputVO = esbUtilOutputVO.getAfbrneOutputVO();
		        	} else {
		        		nfbrneOutputVO = esbUtilOutputVO.getNfbrneOutputVO();
		        	}
		        	//有錯誤
		        	if(StringUtils.isNotBlank(nfbrneOutputVO.getErrorCode()) && !StringUtils.equals("0000", nfbrneOutputVO.getErrorCode())) {
		        		sot703OutputVO.setErrorCode(nfbrneOutputVO.getErrorCode());
		        		sot703OutputVO.setErrorMsg(nfbrneOutputVO.getErrorMsg());
		        	}
				}
	        } catch(Exception e) {
	        	sot703OutputVO.setErrorCode("ERR");
        		sot703OutputVO.setErrorMsg(e.getMessage());
	        }
		}

		return sot703OutputVO;
	}
}