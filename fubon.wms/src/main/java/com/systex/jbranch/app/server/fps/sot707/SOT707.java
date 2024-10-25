package com.systex.jbranch.app.server.fps.sot707;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AI_BANK_SEND_PURCHASE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AJBRVA3;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AJBRVA9;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AJBRVB9;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AJBRVC9;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AJBRVD9;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.BOND_GTC_DATA_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.BOND_GTC_DATA_DETAIL_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.BOND_GTC_DATA_DETAIL_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.BOND_GTC_DATA_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.BOND_GTC_PURCHASE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.BOND_GTC_REDEEM;
//import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CMKTYPE_MK03;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ASSETS;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SN_ASSETS_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SN_ASSETS_OBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SN_FIRST_TRADE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SN_PURCHASE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SN_QUG;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SN_REDEEM;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_BANK_SEND_WEBPURCHASENF;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NJBRVX2;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NJBRVX3;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_BN_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_SN_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_SN_TRADE_DVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva3.AJBRVA3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva3.AJBRVA3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva3.AJBRVA3OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva9.AJBRVA9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva9.AJBRVA9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvb1.AJBRVB1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvb9.AJBRVB9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvb9.AJBRVB9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvc1.AJBRVC1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvc2.AJBRVC2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvc9.AJBRVC9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvc9.AJBRVC9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvd9.AJBRVD9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrvd9.AJBRVD9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMN2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMN2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMNInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ebpmn.EBPMNOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva1.NJBRVA1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva1.NJBRVA1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva1.NJBRVA1OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva3.NJBRVA3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva3.NJBRVA3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva3.NJBRVA3OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva9.NJBRVA9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva9.NJBRVA9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1OutputVODetials;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb9.NJBRVB9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb9.NJBRVB9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc1.NJBRVC1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc1.NJBRVC1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc1.NJBRVC1OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc2.NJBRVC2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc2.NJBRVC2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc2.NJBRVC2OutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc9.NJBRVC9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvc9.NJBRVC9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvd9.NJBRVD9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvd9.NJBRVD9OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx2.NJBRVX2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx2.NJBRVX2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx3.NJBRVX3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx3.NJBRVX3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx3.NJBRVX3OutputVODetails;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Created by SebastianWu on 2016/9/26.
 * 
 * revised by Cathy Tang on 2016/10/24 上行電文日期與數字格式修正 日期格式為民國年月日：上行電文欄位格式 9(08)
 * 要用民國年,如今日: 2016/10/18 => 01051018 電文欄位格式 9(07) 要用民國年,如今日: 2016/10/18 =>
 * 1051018 上行數字格式呼叫ESBUtil.decimalPadding：去除小數點,並用0向右補足小數位長度
 * 
 * revised by Cathy Tang on 2016/11/07
 * 下行數字格式呼叫ESBUtil.decimalPoint：因esb主機傳送整數數值，將整數格式轉換成小數點
 * 
 */
@Component("sot707")
@Scope("request")
public class SOT707 extends EsbUtil {
	@Autowired
	private CBSService cbsservice;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private DataAccessManager dam = null;
	private SOT707InputVO sot707InputVO;
	private SOT707OutputVO sot707OutputVO;
	private String thisClaz = this.getClass().getSimpleName() + ".";

	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;

	private Boolean checkError(Object obj) throws JBranchException {
		String errCode = null;
		String errTxt = null;

		if (obj instanceof NJBRVA9OutputVO) {
			errCode = ((NJBRVA9OutputVO) obj).getErrorCode();
			errTxt = ((NJBRVA9OutputVO) obj).getErrorMsg();
		} else if (obj instanceof NJBRVB9OutputVO) {
			errCode = ((NJBRVB9OutputVO) obj).getErrorCode();
			errTxt = ((NJBRVB9OutputVO) obj).getErrorMsg();
		} else if (obj instanceof NJBRVC9OutputVO) {
			errCode = ((NJBRVC9OutputVO) obj).getErrorCode();
			errTxt = ((NJBRVC9OutputVO) obj).getErrorMsg();
		} else if (obj instanceof NJBRVD9OutputVO) {
			errCode = ((NJBRVD9OutputVO) obj).getErrorCode();
			errTxt = ((NJBRVD9OutputVO) obj).getErrorMsg();
			
		} else if (obj instanceof AJBRVA9OutputVO) {
			errCode = ((AJBRVA9OutputVO) obj).getErrorCode();
			errTxt = ((AJBRVA9OutputVO) obj).getErrorMsg();
		} else if (obj instanceof AJBRVB9OutputVO) {
			errCode = ((AJBRVB9OutputVO) obj).getErrorCode();
			errTxt = ((AJBRVB9OutputVO) obj).getErrorMsg();
		} else if (obj instanceof AJBRVC9OutputVO) {
			errCode = ((AJBRVC9OutputVO) obj).getErrorCode();
			errTxt = ((AJBRVC9OutputVO) obj).getErrorMsg();
		} else if (obj instanceof AJBRVD9OutputVO) {
			errCode = ((AJBRVD9OutputVO) obj).getErrorCode();
			errTxt = ((AJBRVD9OutputVO) obj).getErrorMsg();
			
		} else if (obj instanceof NJBRVX2OutputVO) {
			errCode = ((NJBRVX2OutputVO) obj).getErrorCode();
			errTxt = ((NJBRVX2OutputVO) obj).getErrorMsg();
		} else if (obj instanceof EBPMNOutputVO) { // 網路快速下單 – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息
			errCode = ((EBPMNOutputVO) obj).getErrorCode();
			errTxt = ((EBPMNOutputVO) obj).getErrorMsg();
		} else if (obj instanceof EBPMN2OutputVO) { // AI BANK快速下單 – 發送電文至AI BANK, 行事曆表單上可以出現快速申購之訊息
			EBPMN2OutputVO vo = (EBPMN2OutputVO) obj;
			errCode = StringUtils.isNotBlank(vo.getErrorCode()) ? vo.getErrorCode() : null;
			errTxt = StringUtils.isNotBlank(vo.getErrorMsg()) ? vo.getErrorMsg() : null;
		}

		if (StringUtils.isNotBlank(errCode)) {
			sot707OutputVO.setErrorCode(errCode);
			sot707OutputVO.setErrorMsg(errTxt);

			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * 由交易序號以及產品類別取得交易資料
	 *
	 * @param prodType
	 * @return
	 */
	private MainInfoBean mainInfo(String prodType, String tradeSeq) throws JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		//SN
		if (StringUtils.equals("1", prodType)) {
			sb.append("select M.CUST_ID, ");
			sb.append("       M.CUST_NAME, ");
			sb.append("       M.REC_SEQ, ");
			sb.append("       M.BRANCH_NBR, ");
			sb.append("       D.SEQ_NO, ");
			sb.append("       D.BATCH_SEQ, ");
			sb.append("       D.BOND_VALUE, ");
			sb.append("       D.TRADE_DATE, ");
			sb.append("       D.MARKET_TYPE, ");
			sb.append("       D.TRUST_CURR_TYPE, ");
			sb.append("       D.TRUST_ACCT, ");
			sb.append("       D.TRUST_AMT, ");
			sb.append("       D.TRUST_UNIT, ");
			sb.append("       D.PROD_ID, ");
			sb.append("       D.CERTIFICATE_ID, ");
			sb.append("       D.DEBIT_ACCT, ");
			sb.append("       D.CREDIT_ACCT, ");
			sb.append("       D.MODIFIER, ");
			sb.append("       D.PROD_CURR, ");
			sb.append("       D.PURCHASE_AMT, ");
			sb.append("       D.REDEEM_AMT, ");
			sb.append("       D.ENTRUST_TYPE, ");
			sb.append("       D.ENTRUST_AMT, ");
			sb.append("       D.REF_VAL, ");
			sb.append("       D.REF_VAL_DATE, ");
			sb.append("       D.NARRATOR_ID, ");
			sb.append("       D.BOSS_ID, ");
			sb.append("       D.AUTH_ID, ");
			sb.append("       CASE WHEN (COALESCE(D.FEE_RATE, 0) = COALESCE(D.DEFAULT_FEE_RATE, 0) OR COALESCE(D.FEE_RATE, 0) = COALESCE(D.BEST_FEE_RATE, 0)) THEN '1' ELSE '2' END AS FEE_TYPE, ");
			sb.append("       CASE WHEN (COALESCE(D.FEE_RATE, 0) = COALESCE(D.DEFAULT_FEE_RATE, 0) OR COALESCE(D.FEE_RATE, 0) = COALESCE(D.BEST_FEE_RATE, 0)) THEN NULL ELSE D.FEE_RATE END AS FEE_RATE, ");
			sb.append("	      M.TRUST_TRADE_TYPE, ");
			sb.append("	      M.FLAG_NUMBER, ");
			sb.append("		  S.HNWC_BUY ");
			sb.append("FROM TBSOT_TRADE_MAIN M ");
			sb.append("INNER JOIN TBSOT_SN_TRADE_D D ON D.TRADE_SEQ = M.TRADE_SEQ ");
			sb.append("LEFT JOIN TBPRD_SN S ON S.PRD_ID = D.PROD_ID ");
			sb.append("WHERE M.TRADE_SEQ = :TRADESEQ ");		
		}
		//海外債
		else if (StringUtils.equals("2", prodType)) {
			sb.append("SELECT M.CUST_ID, ");
			sb.append("       M.CUST_NAME, ");
			sb.append("       M.REC_SEQ, ");
			sb.append("       M.BRANCH_NBR, ");
			sb.append("       D.SEQ_NO, ");
			sb.append("       D.BATCH_SEQ, ");
			sb.append("       D.BOND_VALUE, ");
			sb.append("       D.TRADE_DATE, ");
			sb.append("       D.MARKET_TYPE, ");
			sb.append("       D.TRUST_CURR_TYPE, ");
			sb.append("       D.TRUST_ACCT, ");
			sb.append("       D.PROD_ID, ");
			sb.append("       D.CERTIFICATE_ID, ");
			sb.append("       D.TRUST_UNIT, ");
			sb.append("       D.TRUST_AMT, ");
			sb.append("       D.DEBIT_ACCT, ");
			sb.append("       D.CREDIT_ACCT, ");
			sb.append("       D.MODIFIER, ");
			sb.append("       D.PROD_CURR, ");
			sb.append("       D.PURCHASE_AMT, ");
			sb.append("       D.NARRATOR_ID, ");
			sb.append("       D.ENTRUST_TYPE, ");
			sb.append("       D.ENTRUST_AMT, ");
			sb.append("       D.REF_VAL, ");
			sb.append("       D.REF_VAL_DATE, ");
			sb.append("       D.DEFAULT_FEE_RATE, ");
			sb.append("       D.FEE, ");
			sb.append("       CASE WHEN (COALESCE(D.FEE_RATE, 0) = COALESCE(D.DEFAULT_FEE_RATE, 0) OR COALESCE(D.FEE_RATE, 0) = COALESCE(D.BEST_FEE_RATE, 0)) THEN '1' ELSE '2' END AS FEE_TYPE, ");
			sb.append("       CASE WHEN (COALESCE(D.FEE_RATE, 0) = COALESCE(D.DEFAULT_FEE_RATE, 0) OR COALESCE(D.FEE_RATE, 0) = COALESCE(D.BEST_FEE_RATE, 0)) THEN NULL ELSE D.FEE_RATE END AS FEE_RATE, ");
			sb.append("       D.FEE_RATE AS GTC_FEE_RATE, "); //長效單直接傳入費率
			sb.append("       D.GTC_YN, ");
//			sb.append("       TO_CHAR(D.GTC_START_DATE, 'YYYYMMDD') AS GTC_START_DATE, ");
//			sb.append("       TO_CHAR(D.GTC_END_DATE, 'YYYYMMDD') AS GTC_END_DATE, ");
			sb.append("       D.GTC_START_DATE, ");
			sb.append("       D.GTC_END_DATE, ");
			sb.append("       M.TRUST_TRADE_TYPE, ");
			sb.append("	      M.FLAG_NUMBER, ");
			sb.append("		  NVL(M.IS_WEB, 'N') AS IS_WEB ");
			sb.append("FROM TBSOT_TRADE_MAIN M ");
			sb.append("INNER JOIN TBSOT_BN_TRADE_D D ON D.TRADE_SEQ = M.TRADE_SEQ ");
			sb.append("WHERE M.TRADE_SEQ = :TRADESEQ ");
		}
		// query condition
		condition.setObject("TRADESEQ", tradeSeq);
		condition.setQueryString(sb.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(condition);

		MainInfoBean mainVO = new MainInfoBean();
		if (CollectionUtils.isNotEmpty(list)) {
			mainVO.setCUST_ID((String) list.get(0).get("CUST_ID"));
			mainVO.setCUST_NAME((String) list.get(0).get("CUST_NAME"));
			mainVO.setSEQ_NO((BigDecimal) list.get(0).get("SEQ_NO"));
			mainVO.setTRADE_DATE((Date) list.get(0).get("TRADE_DATE"));
			mainVO.setBATCH_SEQ((String) list.get(0).get("BATCH_SEQ"));
			mainVO.setMARKET_TYPE(((Character) list.get(0).get("MARKET_TYPE")).toString());
			mainVO.setTRUST_CURR_TYPE(((Character) list.get(0).get("TRUST_CURR_TYPE")).toString());
			mainVO.setTRUST_ACCT((String) list.get(0).get("TRUST_ACCT"));
			mainVO.setTRUST_AMT((BigDecimal) list.get(0).get("TRUST_AMT"));
			mainVO.setTRUST_UNIT((BigDecimal) list.get(0).get("TRUST_UNIT"));
			mainVO.setPROD_ID((String) list.get(0).get("PROD_ID"));
			mainVO.setDEBIT_ACCT((String) list.get(0).get("DEBIT_ACCT"));
			mainVO.setCREDIT_ACCT((String) list.get(0).get("CREDIT_ACCT"));
			mainVO.setBRANCH_NBR((String) list.get(0).get("BRANCH_NBR")); // 受理分行
			mainVO.setMODIFIER((String) list.get(0).get("MODIFIER"));
			mainVO.setPROD_CURR((String) list.get(0).get("PROD_CURR"));
			mainVO.setCERTIFICATE_ID((String) list.get(0).get("CERTIFICATE_ID"));
			mainVO.setPURCHASE_AMT((BigDecimal) list.get(0).get("PURCHASE_AMT"));
			mainVO.setREC_SEQ((String) list.get(0).get("REC_SEQ"));
			mainVO.setNARRATOR_ID((String) list.get(0).get("NARRATOR_ID"));
			mainVO.setENTRUST_TYPE(list.get(0).get("ENTRUST_TYPE") == null ? null : list.get(0).get("ENTRUST_TYPE").toString());
			mainVO.setENTRUST_AMT((BigDecimal) list.get(0).get("ENTRUST_AMT"));
			mainVO.setREF_VAL((BigDecimal) list.get(0).get("REF_VAL"));
			mainVO.setREF_VAL_DATE((Date) list.get(0).get("REF_VAL_DATE"));
			mainVO.setBOSS_ID((String) list.get(0).get("BOSS_ID")); // 主管員編
			mainVO.setBOND_VALUE((BigDecimal) list.get(0).get("BOND_VALUE"));
			mainVO.setFLAG_NUMBER((String) list.get(0).get("FLAG_NUMBER"));
			if (StringUtils.equals("1", prodType)) { // SN
				mainVO.setREDEEM_AMT((BigDecimal) list.get(0).get("REDEEM_AMT"));
				mainVO.setFEE_RATE((BigDecimal) list.get(0).get("FEE_RATE"));
				mainVO.setFEE_TYPE(((Character) list.get(0).get("FEE_TYPE")).toString());
				mainVO.setAUTH_ID(ObjectUtils.toString(list.get(0).get("AUTH_ID"))); // 授權交易人員ID
				mainVO.setHNWC_BUY(ObjectUtils.toString(list.get(0).get("HNWC_BUY"))); // 限高資產申購註記
			} else if (StringUtils.equals("2", prodType)) { // 海外債
				mainVO.setFEE_TYPE(((Character) list.get(0).get("FEE_TYPE")).toString());
				mainVO.setFEE_RATE((BigDecimal) list.get(0).get("FEE_RATE"));
				mainVO.setDEFAULT_FEE_RATE((BigDecimal) list.get(0).get("DEFAULT_FEE_RATE"));
				mainVO.setMGM_FEE((BigDecimal) list.get(0).get("FEE"));
				mainVO.setGTC_FEE_RATE((BigDecimal) list.get(0).get("GTC_FEE_RATE"));
				mainVO.setGTC_YN((String) list.get(0).get("GTC_YN"));
				mainVO.setGTC_END_DATE((Date) list.get(0).get("GTC_END_DATE"));
				mainVO.setGTC_START_DATE((Date) list.get(0).get("GTC_START_DATE"));
				mainVO.setIS_WEB((String) list.get(0).get("IS_WEB"));
			}

			mainVO.setTRUST_TRADE_TYPE(list.get(0).get("TRUST_TRADE_TYPE").toString());
		}

		return mainVO;
	}

	/**
	 * 海外債/SN申購檢核、確認 for js client
	 *
	 * 使用電文: NJBRVA9
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBPurchaseBN(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBPurchaseBN(body));
	}

	/**
	 * 海外債/SN申購檢核、確認
	 *
	 * 使用電文: NJBRVA9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBPurchaseBN(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		Map<String, String> trustTSMap = xmlInfo.doGetVariable("SOT.TRUST_TS", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String prodType = sot707InputVO.getProdType(); // 產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); // 交易序號
		String checkType = sot707InputVO.getCheckType(); // 電文確認碼

		// 欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}

		// 由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_SN_PURCHASE);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NJBRVA9InputVO txBodyVO = new NJBRVA9InputVO();
		esbUtilInputVO.setNjbrva9InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType); // 確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false)); // 申請日期
		// txBodyVO.setApplyDate("01071017"); //申請日期
		txBodyVO.setKeyinNo(StringUtils.equals(mainVO.getIS_WEB(), "Y") ? "" : mainVO.getBATCH_SEQ()); // 理專登錄編號//快速申購於風控頁檢核不須送批號
		txBodyVO.setType(mainVO.getMARKET_TYPE()); // 初級或次級
		txBodyVO.setCustNo(mainVO.getCUST_ID()); // 身份証號
		// 2020-01-15 modify by ocean : WMS-CR-20191009-01_金錢信託套表需求申請單, 交易類別為金錢信託時，CUST_ID傳送99331241
		txBodyVO.setCustNo((StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE()) ? mainVO.getCUST_ID() : trustTSMap.get("M_CUSTNO"))); //身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE()); // 信託業務別
		txBodyVO.setTrustAcct(cbsservice.checkAcctLength(mainVO.getTRUST_ACCT())); // 信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); // 債券代號
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); // 票面價值
		txBodyVO.setTxAcct(cbsservice.checkAcctLength(mainVO.getDEBIT_ACCT())); // 扣款帳號
		txBodyVO.setRcvAcct(cbsservice.checkAcctLength(mainVO.getCREDIT_ACCT())); // 收益帳號

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		txBodyVO.setBranchNo(branchNbr); // 受理分行

		txBodyVO.setKeyinId(mainVO.getMODIFIER()); // 鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR()); // 委託面額幣別
		txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); // 委託面額
		if (StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE())) {
			txBodyVO.setTapeNo(mainVO.getREC_SEQ()); //錄音序號
		}
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID()); // 解說專員

		if (StringUtils.equals("1", prodType)) { // SN
			txBodyVO.setTxBoss(mainVO.getBOSS_ID()); // 主管
//			if(StringUtils.equals("Y", mainVO.getHNWC_BUY())) { //商品限高資產申購註記
				txBodyVO.setFiller(mainVO.getAUTH_ID()); //授權交易人員
				if(StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE())) {
					//#1695 貸轉投
					String filler = cbsservice.padRight(txBodyVO.getFiller() == null ? "" : txBodyVO.getFiller(), 11, " ");
					String flagNumber = mainVO.getFLAG_NUMBER() == null ? " " : mainVO.getFLAG_NUMBER();
					txBodyVO.setFiller(filler + flagNumber);
				}
//			}
			txBodyVO.setTxType(mainVO.getENTRUST_TYPE()); // 限價方式
			txBodyVO.setTxFeeType("1"); // 手續費議價
			if(mainVO.getTRUST_TRADE_TYPE().equals("M")){
				txBodyVO.setTxFeeType(mainVO.getFEE_TYPE()); // 手續費議價
				txBodyVO.setTxFeeRate(new EsbUtil().decimalPadding(mainVO.getFEE_RATE(), 5)); // 手續費費率
				txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); // 委買單價
			}
			
		} else if (StringUtils.equals("2", prodType)) { // 海外債
			txBodyVO.setTxType(mainVO.getENTRUST_TYPE()); // 限價方式
			// 限價方式為1(限價)，才需要放值
			if (StringUtils.equals("1", mainVO.getENTRUST_TYPE())) {
				txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); // 委買單價
			}
			txBodyVO.setTxFeeType(mainVO.getFEE_TYPE()); // 手續費議價
			txBodyVO.setTxFeeRate(new EsbUtil().decimalPadding(mainVO.getFEE_RATE(), 5)); // 手續費費率
			if(StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE())) {
				//#1695 貸轉投
				String flagNumber = mainVO.getFLAG_NUMBER() == null ? " " : mainVO.getFLAG_NUMBER();
				txBodyVO.setFiller("           " + flagNumber);
			}

		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVA9OutputVO njbrva9Output = esbUtilOutputVO.getNjbrva9OutputVO();

			// 若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(njbrva9Output);

			//回傳下行電文資料儲存DB
			//網行銀快速申購不須回寫NJBRVA9回傳資料
			if (!isErr && !StringUtils.equals(mainVO.getIS_WEB(), "Y")) {
				purchaseETFUpdateDB(prodType, tradeSeq, mainVO.getSEQ_NO(), njbrva9Output, mainVO.getDEFAULT_FEE_RATE(), mainVO.getTRUST_AMT());
			}

			// 若有專業投資人提示訊息
			sot707OutputVO.setWarningCode(new ArrayList<String>());
			if (StringUtils.equals("Y", njbrva9Output.getTxMsgCode())) {
				if (sot707InputVO.getPurchaseAmt().compareTo(new BigDecimal("3000000")) == -1) { // 當申購金額小於3000000時，需顯示提示訊息
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_007");
				}
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			} else if (StringUtils.equals("A", njbrva9Output.getTxMsgCode())) {
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			}
		}

		return sot707OutputVO;
	}
	
	/**
	 * 海外債/SN申購檢核、確認_OBU
	 *
	 * 使用電文: AJBRVA9 理專鍵機債券申購交易_OBU
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBPurchaseBN_OBU(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBPurchaseBN_OBU(body));
	}
	
	/**
	 * 海外債/SN申購檢核、確認
	 *
	 * 使用電文: AJBRVA9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBPurchaseBN_OBU(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		Map<String, String> trustTSMap = xmlInfo.doGetVariable("SOT.TRUST_TS", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();
		
		String prodType = sot707InputVO.getProdType(); 		// 產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); 		// 交易序號
		String checkType = sot707InputVO.getCheckType(); 	// 電文確認碼
		
		// 欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}
		
		// 由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);
		
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, AJBRVA9);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());
		
		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		// body
		AJBRVA9InputVO txBodyVO = new AJBRVA9InputVO();
		esbUtilInputVO.setAjbrva9InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType); // 確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false)); // 申請日期
		// txBodyVO.setApplyDate("01071017"); //申請日期
		txBodyVO.setKeyinNo(StringUtils.equals(mainVO.getIS_WEB(), "Y") ? "" : mainVO.getBATCH_SEQ()); // 理專登錄編號//快速申購於風控頁檢核不須送批號
		txBodyVO.setType(mainVO.getMARKET_TYPE()); // 初級或次級
		txBodyVO.setCustNo(mainVO.getCUST_ID()); // 身份証號
		// 2020-01-15 modify by ocean : WMS-CR-20191009-01_金錢信託套表需求申請單, 交易類別為金錢信託時，CUST_ID傳送99331241
		txBodyVO.setCustNo((StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE()) ? mainVO.getCUST_ID() : trustTSMap.get("M_CUSTNO"))); //身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE()); // 信託業務別
		txBodyVO.setTrustAcct(cbsservice.checkAcctLength(mainVO.getTRUST_ACCT())); // 信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); // 債券代號
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); // 票面價值
		txBodyVO.setTxAcct(cbsservice.checkAcctLength(mainVO.getDEBIT_ACCT())); // 扣款帳號
		txBodyVO.setRcvAcct(cbsservice.checkAcctLength(mainVO.getCREDIT_ACCT())); // 收益帳號
		
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		txBodyVO.setBranchNo(branchNbr); // 受理分行
		
		txBodyVO.setKeyinId(mainVO.getMODIFIER()); // 鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR()); // 委託面額幣別
		txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); // 委託面額
		if (StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE())) {
			txBodyVO.setTapeNo(mainVO.getREC_SEQ()); //錄音序號
		}
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID()); // 解說專員
		
		if (StringUtils.equals("1", prodType)) { // SN
			txBodyVO.setTxBoss(mainVO.getBOSS_ID()); // 主管
//			if(StringUtils.equals("Y", mainVO.getHNWC_BUY())) { //商品限高資產申購註記
			txBodyVO.setFiller(mainVO.getAUTH_ID()); //授權交易人員
			if(StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE())) {
				//#1695 貸轉投
				String filler = cbsservice.padRight(txBodyVO.getFiller() == null ? "" : txBodyVO.getFiller(), 11, " ");
				String flagNumber = mainVO.getFLAG_NUMBER() == null ? " " : mainVO.getFLAG_NUMBER();
				txBodyVO.setFiller(filler + flagNumber);
			}
//			}
			txBodyVO.setTxType(mainVO.getENTRUST_TYPE()); // 限價方式
			txBodyVO.setTxFeeType("1"); // 手續費議價
			if(mainVO.getTRUST_TRADE_TYPE().equals("M")){
				txBodyVO.setTxFeeType(mainVO.getFEE_TYPE()); // 手續費議價
				txBodyVO.setTxFeeRate(new EsbUtil().decimalPadding(mainVO.getFEE_RATE(), 5)); // 手續費費率
				txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); // 委買單價
			}
			
		} else if (StringUtils.equals("2", prodType)) { // 海外債
			txBodyVO.setTxType(mainVO.getENTRUST_TYPE()); // 限價方式
			// 限價方式為1(限價)，才需要放值
			if (StringUtils.equals("1", mainVO.getENTRUST_TYPE())) {
				txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); // 委買單價
			}
			txBodyVO.setTxFeeType(mainVO.getFEE_TYPE()); // 手續費議價
			txBodyVO.setTxFeeRate(new EsbUtil().decimalPadding(mainVO.getFEE_RATE(), 5)); // 手續費費率
			if(StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE())) {
				//#1695 貸轉投
				String flagNumber = mainVO.getFLAG_NUMBER() == null ? " " : mainVO.getFLAG_NUMBER();
				txBodyVO.setFiller("           " + flagNumber);
			}
			
		}
		
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			AJBRVA9OutputVO ajbrva9Output = esbUtilOutputVO.getAjbrva9OutputVO();
			
			// 若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(ajbrva9Output);
			
			//回傳下行電文資料儲存DB
			//網行銀快速申購不須回寫AJBRVA9回傳資料
			if (!isErr && !StringUtils.equals(mainVO.getIS_WEB(), "Y")) {
				purchaseETFUpdateDB_OBU(prodType, tradeSeq, mainVO.getSEQ_NO(), ajbrva9Output, mainVO.getDEFAULT_FEE_RATE(), mainVO.getTRUST_AMT());
			}
			
			// 若有專業投資人提示訊息
			sot707OutputVO.setWarningCode(new ArrayList<String>());
			if (StringUtils.equals("Y", ajbrva9Output.getTxMsgCode())) {
				if (sot707InputVO.getPurchaseAmt().compareTo(new BigDecimal("3000000")) == -1) { // 當申購金額小於3000000時，需顯示提示訊息
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_007");
				}
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			} else if (StringUtils.equals("A", ajbrva9Output.getTxMsgCode())) {
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			}
		}
		
		return sot707OutputVO;
	}

	/**
	 * 海外債/SN申購檢核、確認下行電文回壓資料庫欄位
	 *
	 * @param prodType
	 * @param tradeSeq
	 * @param njbrva9Output
	 * @param defaultFeeRate
	 * @param trustAmt
	 * @throws JBranchException
	 */
	private void purchaseETFUpdateDB(String prodType, String tradeSeq, BigDecimal seqNo, NJBRVA9OutputVO njbrva9Output, BigDecimal defaultFeeRate, BigDecimal trustAmt) throws JBranchException {

		dam = getDataAccessManager();

		// SN
		if (StringUtils.equals("1", prodType)) {
			TBSOT_SN_TRADE_DPK pk = new TBSOT_SN_TRADE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(seqNo);
			TBSOT_SN_TRADE_DVO vo = new TBSOT_SN_TRADE_DVO();

			// find data by pks
			vo = (TBSOT_SN_TRADE_DVO) dam.findByPKey(TBSOT_SN_TRADE_DVO.TABLE_UID, pk);

			BigDecimal feeRate = new EsbUtil().decimalPoint(njbrva9Output.getTxFeeRate(), 5);
			BigDecimal fee = new EsbUtil().decimalPoint(njbrva9Output.getTxFee1(), 2);
			BigDecimal fee2 = new EsbUtil().decimalPoint(njbrva9Output.getTxFee2(), 2);
			BigDecimal entrustAmt = new EsbUtil().decimalPoint(njbrva9Output.getTxPrice(), 9);

			vo.setFEE(fee);
			vo.setFEE_RATE(feeRate);
			vo.setPAYABLE_FEE(fee2);
			vo.setCERTIFICATE_ID(njbrva9Output.getTrustNo());
			vo.setENTRUST_AMT(entrustAmt);

			dam.update(vo);
		}
		// 海外債
		else if (StringUtils.equals("2", prodType)) {
			TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(seqNo);
			TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();

			// find data by pks
			vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);

			BigDecimal feeRate = new EsbUtil().decimalPoint(njbrva9Output.getTxFeeRate(), 5);
			BigDecimal fee = new EsbUtil().decimalPoint(njbrva9Output.getTxFee1(), 2);
			BigDecimal fee2 = new EsbUtil().decimalPoint(njbrva9Output.getTxFee2(), 2);

			vo.setFEE_RATE(feeRate);
			vo.setFEE(fee);
			vo.setFEE_DISCOUNT((defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null);
			double feeDiscount10 = 10.0;// 10折不寫入資料庫
			if (vo.getFEE_DISCOUNT() != null && vo.getFEE_DISCOUNT().doubleValue() == feeDiscount10) {
				vo.setFEE_DISCOUNT(null);
			}
			vo.setCERTIFICATE_ID(njbrva9Output.getTrustNo());
			vo.setPAYABLE_FEE(fee2);
			vo.setTOT_AMT(trustAmt.add(fee).add(fee2));

			dam.update(vo);
		}
	}
	
	/**
	 * 海外債/SN申購檢核、確認下行電文回壓資料庫欄位_OBU
	 *
	 * @param prodType
	 * @param tradeSeq
	 * @param njbrva9Output
	 * @param defaultFeeRate
	 * @param trustAmt
	 * @throws JBranchException
	 */
	private void purchaseETFUpdateDB_OBU(String prodType, String tradeSeq, BigDecimal seqNo, AJBRVA9OutputVO ajbrva9Output, BigDecimal defaultFeeRate, BigDecimal trustAmt) throws JBranchException {
		
		dam = getDataAccessManager();
		
		// SN
		if (StringUtils.equals("1", prodType)) {
			TBSOT_SN_TRADE_DPK pk = new TBSOT_SN_TRADE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(seqNo);
			TBSOT_SN_TRADE_DVO vo = new TBSOT_SN_TRADE_DVO();
			
			// find data by pks
			vo = (TBSOT_SN_TRADE_DVO) dam.findByPKey(TBSOT_SN_TRADE_DVO.TABLE_UID, pk);
			
			BigDecimal feeRate = new EsbUtil().decimalPoint(ajbrva9Output.getTxFeeRate(), 5);
			BigDecimal fee = new EsbUtil().decimalPoint(ajbrva9Output.getTxFee1(), 2);
			BigDecimal fee2 = new EsbUtil().decimalPoint(ajbrva9Output.getTxFee2(), 2);
			BigDecimal entrustAmt = new EsbUtil().decimalPoint(ajbrva9Output.getTxPrice(), 9);
			
			vo.setFEE(fee);
			vo.setFEE_RATE(feeRate);
			vo.setPAYABLE_FEE(fee2);
			vo.setCERTIFICATE_ID(ajbrva9Output.getTrustNo());
			vo.setENTRUST_AMT(entrustAmt);
			
			dam.update(vo);
		}
		// 海外債
		else if (StringUtils.equals("2", prodType)) {
			TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(seqNo);
			TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();
			
			// find data by pks
			vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);
			
			BigDecimal feeRate = new EsbUtil().decimalPoint(ajbrva9Output.getTxFeeRate(), 5);
			BigDecimal fee = new EsbUtil().decimalPoint(ajbrva9Output.getTxFee1(), 2);
			BigDecimal fee2 = new EsbUtil().decimalPoint(ajbrva9Output.getTxFee2(), 2);
			
			vo.setFEE_RATE(feeRate);
			vo.setFEE(fee);
			vo.setFEE_DISCOUNT((defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null);
			double feeDiscount10 = 10.0;// 10折不寫入資料庫
			if (vo.getFEE_DISCOUNT() != null && vo.getFEE_DISCOUNT().doubleValue() == feeDiscount10) {
				vo.setFEE_DISCOUNT(null);
			}
			vo.setCERTIFICATE_ID(ajbrva9Output.getTrustNo());
			vo.setPAYABLE_FEE(fee2);
			vo.setTOT_AMT(trustAmt.add(fee).add(fee2));
			
			dam.update(vo);
		}
	}

	/**
	 * 海外債長效單申購檢核、確認 for js client
	 *
	 * 使用電文: NJBRVC9
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBPurchaseBN_GTC(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBPurchaseBN_GTC(body));
	}

	/**
	 * 海外債長效單申購檢核、確認
	 *
	 * 使用電文: NJBRVC9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBPurchaseBN_GTC(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String prodType = sot707InputVO.getProdType(); 		// 產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); 		// 交易序號
		String checkType = sot707InputVO.getCheckType(); 	// 電文確認碼

		// 欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}

		// 由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, BOND_GTC_PURCHASE);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NJBRVC9InputVO txBodyVO = new NJBRVC9InputVO();
		esbUtilInputVO.setNjbrvc9InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType); // 確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false)); // 申請日期
		txBodyVO.setKeyinNo(mainVO.getBATCH_SEQ()); // 理專登錄編號
		txBodyVO.setType(mainVO.getMARKET_TYPE()); // 初級或次級
		txBodyVO.setCustNo(mainVO.getCUST_ID()); // 身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE()); // 信託業務別
		txBodyVO.setTrustAcct(mainVO.getTRUST_ACCT()); // 信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); // 債券代號
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); // 票面價值
		txBodyVO.setTxAcct(mainVO.getDEBIT_ACCT()); // 扣款帳號
		txBodyVO.setRcvAcct(mainVO.getCREDIT_ACCT()); // 收益帳號

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		// 受理分行
//		txBodyVO.setBranchNo(mainVO.getBRANCH_NBR()); 										// 受理分行
		txBodyVO.setKeyinId(mainVO.getMODIFIER()); 											// 鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR()); 											// 委託面額幣別
		txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); 		// 委託面額
		txBodyVO.setTapeNo(mainVO.getREC_SEQ()); 											// 錄音序號
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID()); 									// 解說專員
		txBodyVO.setTxType("2");															// 限價方式：固定放 2.市價
		txBodyVO.setGtcEndDate(this.toChineseYearMMdd(mainVO.getGTC_END_DATE(), false));
		
		// 限價，才需要放值
		if (!StringUtils.equals("2", mainVO.getENTRUST_TYPE())) {
			txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9));	// 委買單價：『限價』需有價格、『市價』NULL
		}
		
		txBodyVO.setTxFeeType(mainVO.getFEE_TYPE()); 										// 手續費議價
		txBodyVO.setTxFeeRate(new EsbUtil().decimalPadding(mainVO.getGTC_FEE_RATE(), 5));	// 手續費費率
		
		if(StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE())) {
			/**
			 * 第1位   ：客戶申貸紀錄			(Y/N/空白)
			 * 第2位   ：長效單/預約單			(空白：長效單 1：預約單)
			 * 第3-10位：預約單/長效單生效起日	9(08)
			 * **/
			String flagNumber = mainVO.getFLAG_NUMBER() == null ? " " : mainVO.getFLAG_NUMBER();	// #1695：貸轉投
			String gtcYN = mainVO.getGTC_YN().equals("Y") ? " " : "1";
			String gtcStartDate = mainVO.getGTC_START_DATE() == null ? "" : this.toChineseYearMMdd(mainVO.getGTC_START_DATE());
			String filler = flagNumber + gtcYN + gtcStartDate;
			
			txBodyVO.setFiller(filler);
		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVC9OutputVO njbrvc9Output = esbUtilOutputVO.getNjbrvc9OutputVO();

			// 若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(njbrvc9Output);

			// 回傳下行電文資料儲存DB
			if (!isErr) {
				purchaseBondUpdateDB_GTC(prodType, tradeSeq, mainVO.getSEQ_NO(), njbrvc9Output, mainVO.getDEFAULT_FEE_RATE());
			}

			// 若有專業投資人提示訊息
			sot707OutputVO.setWarningCode(new ArrayList<String>());
			if (StringUtils.equals("Y", njbrvc9Output.getTxMsgCode())) {
				if (sot707InputVO.getPurchaseAmt().compareTo(new BigDecimal("3000000")) == -1) { // 當申購金額小於3000000時，需顯示提示訊息
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_007");
				}
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			} else if (StringUtils.equals("A", njbrvc9Output.getTxMsgCode())) {
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			}

			// 委買單價與最新報價不符合
			if (StringUtils.equals("Y", njbrvc9Output.getTxMsgCode1())) {
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_012");
			}
		}

		return sot707OutputVO;
	}
	
	/**
	 * 海外債長效單申購檢核、確認_OBU
	 *
	 * 使用電文: AJBRVC9 理專鍵機債券長效單申購交易OBU
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBPurchaseBN_GTC_OBU(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBPurchaseBN_GTC_OBU(body));
	}
	
	/**
	 * 海外債長效單申購檢核、確認_OBU
	 *
	 * 使用電文: AJBRVC9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBPurchaseBN_GTC_OBU(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();
		
		String prodType = sot707InputVO.getProdType(); 		// 產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); 		// 交易序號
		String checkType = sot707InputVO.getCheckType(); 	// 電文確認碼
		
		// 欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}
		
		// 由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);
		
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, AJBRVC9);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());
		
		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		// body
		AJBRVC9InputVO txBodyVO = new AJBRVC9InputVO();
		esbUtilInputVO.setAjbrvc9InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType); // 確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false)); // 申請日期
		txBodyVO.setKeyinNo(mainVO.getBATCH_SEQ()); // 理專登錄編號
		txBodyVO.setType(mainVO.getMARKET_TYPE()); // 初級或次級
		txBodyVO.setCustNo(mainVO.getCUST_ID()); // 身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE()); // 信託業務別
		txBodyVO.setTrustAcct(mainVO.getTRUST_ACCT()); // 信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); // 債券代號
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); // 票面價值
		txBodyVO.setTxAcct(mainVO.getDEBIT_ACCT()); // 扣款帳號
		txBodyVO.setRcvAcct(mainVO.getCREDIT_ACCT()); // 收益帳號
		
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		txBodyVO.setBranchNo(branchNbr); 													// 受理分行
//		txBodyVO.setBranchNo(mainVO.getBRANCH_NBR()); 										// 受理分行
		
		txBodyVO.setKeyinId(mainVO.getMODIFIER()); 											// 鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR()); 											// 委託面額幣別
		txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); 		// 委託面額
		txBodyVO.setTapeNo(mainVO.getREC_SEQ()); 											// 錄音序號
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID()); 									// 解說專員
		txBodyVO.setTxType(mainVO.getGTC_YN());
		txBodyVO.setGtcEndDate(this.toChineseYearMMdd(mainVO.getGTC_END_DATE(), false));
		txBodyVO.setTxType("2");															// 限價方式：固定放 2.市價
		
		// 限價，才需要放值
		if (!StringUtils.equals("2", mainVO.getENTRUST_TYPE())) {
			txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9));	// 委買單價：『限價』需有價格、『市價』NULL
		}
		
		txBodyVO.setTxFeeType(mainVO.getFEE_TYPE()); 										// 手續費議價
		txBodyVO.setTxFeeRate(new EsbUtil().decimalPadding(mainVO.getGTC_FEE_RATE(), 5));	// 手續費費率
		
		if(StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE())) {
			/**
			 * 第1位   ：客戶申貸紀錄			(Y/N/空白)
			 * 第2位   ：長效單/預約單			(空白：長效單 1：預約單)
			 * 第3-10位：預約單/長效單生效起日	9(08)
			 * **/
			String flagNumber = mainVO.getFLAG_NUMBER() == null ? " " : mainVO.getFLAG_NUMBER();	// #1695：貸轉投
			String gtcYN = mainVO.getGTC_YN().equals("Y") ? " " : "1";
			String gtcStartDate = mainVO.getGTC_START_DATE() == null ? "" : this.toChineseYearMMdd(mainVO.getGTC_START_DATE());
			String filler = flagNumber + gtcYN + gtcStartDate;
			
			txBodyVO.setFiller(filler);
		}
		
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			AJBRVC9OutputVO ajbrvc9Output = esbUtilOutputVO.getAjbrvc9OutputVO();
			
			// 若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(ajbrvc9Output);
			
			// 回傳下行電文資料儲存DB
			if (!isErr) {
				purchaseBondUpdateDB_GTC_OBU(prodType, tradeSeq, mainVO.getSEQ_NO(), ajbrvc9Output, mainVO.getDEFAULT_FEE_RATE());
			}
			
			// 若有專業投資人提示訊息
			sot707OutputVO.setWarningCode(new ArrayList<String>());
			if (StringUtils.equals("Y", ajbrvc9Output.getTxMsgCode())) {
				if (sot707InputVO.getPurchaseAmt().compareTo(new BigDecimal("3000000")) == -1) { // 當申購金額小於3000000時，需顯示提示訊息
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_007");
				}
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			} else if (StringUtils.equals("A", ajbrvc9Output.getTxMsgCode())) {
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			}
			
			// 委買單價與最新報價不符合
			if (StringUtils.equals("Y", ajbrvc9Output.getTxMsgCode1())) {
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_012");
			}
		}
		
		return sot707OutputVO;
	}

	/**
	 * 海外債長效單申購檢核、確認下行電文回壓資料庫欄位
	 *
	 * @param prodType
	 * @param tradeSeq
	 * @param njbrva9Output
	 * @param defaultFeeRate
	 * @param trustAmt
	 * @throws JBranchException
	 */
	private void purchaseBondUpdateDB_GTC(String prodType, String tradeSeq, BigDecimal seqNo, NJBRVC9OutputVO njbrvc9Output, BigDecimal defaultFeeRate) throws JBranchException {
		dam = getDataAccessManager();

		TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(seqNo);
		TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();

		// find data by pks
		vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);

		BigDecimal feeRate = new EsbUtil().decimalPoint(njbrvc9Output.getTxFeeRate(), 5);
		BigDecimal fee = new EsbUtil().decimalPoint(njbrvc9Output.getTxFee1(), 2);

		vo.setFEE_RATE(feeRate);
		vo.setFEE(fee);
		vo.setFEE_DISCOUNT((defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null);
		double feeDiscount10 = 10.0;// 10折不寫入資料庫
		if (vo.getFEE_DISCOUNT() != null && vo.getFEE_DISCOUNT().doubleValue() == feeDiscount10) {
			vo.setFEE_DISCOUNT(null);
		}

		dam.update(vo);
	}
	
	/**
	 * 海外債長效單申購檢核、確認下行電文回壓資料庫欄位
	 *
	 * @param prodType
	 * @param tradeSeq
	 * @param njbrva9Output
	 * @param defaultFeeRate
	 * @param trustAmt
	 * @throws JBranchException
	 */
	private void purchaseBondUpdateDB_GTC_OBU(String prodType, String tradeSeq, BigDecimal seqNo, AJBRVC9OutputVO ajbrvc9Output, BigDecimal defaultFeeRate) throws JBranchException {
		dam = getDataAccessManager();
		
		TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(seqNo);
		TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();
		
		// find data by pks
		vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);
		
		BigDecimal feeRate = new EsbUtil().decimalPoint(ajbrvc9Output.getTxFeeRate(), 5);
		BigDecimal fee = new EsbUtil().decimalPoint(ajbrvc9Output.getTxFee1(), 2);
		
		vo.setFEE_RATE(feeRate);
		vo.setFEE(fee);
		vo.setFEE_DISCOUNT((defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null);
		double feeDiscount10 = 10.0;// 10折不寫入資料庫
		if (vo.getFEE_DISCOUNT() != null && vo.getFEE_DISCOUNT().doubleValue() == feeDiscount10) {
			vo.setFEE_DISCOUNT(null);
		}
		
		dam.update(vo);
	}

	/**
	 * 海外債金錢信託申購檢核、確認 for js client
	 *
	 * 使用電文: NJBRVX2
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBPurchaseBN_TRUST(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBPurchaseBN_TRUST(body));
	}

	/**
	 * 海外債金錢信託申購檢核、確認
	 *
	 * 使用電文: NJBRVA9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBPurchaseBN_TRUST(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String prodType = sot707InputVO.getProdType(); //產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); //交易序號
		String checkType = sot707InputVO.getCheckType(); //電文確認碼

		//欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}

		//由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NJBRVX2);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		//body
		NJBRVX2InputVO txBodyVO = new NJBRVX2InputVO();
		esbUtilInputVO.setNjbrvx2InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType); //確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false)); //申請日期
		txBodyVO.setKeyinNo(mainVO.getBATCH_SEQ()); //理專登錄編號
		txBodyVO.setType(mainVO.getMARKET_TYPE()); //初級或次級
		txBodyVO.setCustNo(mainVO.getCUST_ID()); //身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE()); //信託業務別
		txBodyVO.setTrustAcct(""); //信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); //債券代號
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); //票面價值 
		txBodyVO.setTxAcct(""); //扣款帳號
		txBodyVO.setRcvAcct(""); //收益帳號

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		txBodyVO.setBranchNo(branchNbr); //受理分行

		txBodyVO.setKeyinId(mainVO.getMODIFIER()); //鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR()); //委託面額幣別
		txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); //委託面額
		txBodyVO.setTapeNo(mainVO.getREC_SEQ()); //錄音序號
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID()); //解說專員

		if (StringUtils.equals("1", prodType)) { //SN
			txBodyVO.setTxBoss(mainVO.getBOSS_ID()); //主管 
			txBodyVO.setTxType(mainVO.getENTRUST_TYPE()); //限價方式
			txBodyVO.setTxFeeType(mainVO.getFEE_TYPE()); //手續費議價 
			txBodyVO.setTxFeeRate(new EsbUtil().decimalPadding(mainVO.getFEE_RATE(), 5)); //手續費費率
			txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); // 委買單價
		} else if (StringUtils.equals("2", prodType)) { //海外債
			txBodyVO.setTxType(mainVO.getENTRUST_TYPE()); //限價方式
			//限價方式為1(限價)，才需要放值
			if (StringUtils.equals("1", mainVO.getENTRUST_TYPE())) {
				txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); //委買單價
			}
			txBodyVO.setTxFeeType(mainVO.getFEE_TYPE()); //手續費議價

			if (!StringUtils.equals("1", mainVO.getFEE_TYPE())) {
				txBodyVO.setTxFeeRate(new EsbUtil().decimalPadding(mainVO.getFEE_RATE(), 5)); //手續費費率
			}
		}

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVX2OutputVO njbrvx2Output = esbUtilOutputVO.getNjbrvx2OutputVO();

			//若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(njbrvx2Output);

			//回傳下行電文資料儲存DB TODO
			if (!isErr) {
				purchaseETFUpdateDB_TRUST(prodType, tradeSeq, mainVO.getSEQ_NO(), njbrvx2Output, mainVO.getDEFAULT_FEE_RATE(), mainVO.getTRUST_AMT());
			}

			//若有專業投資人提示訊息
			sot707OutputVO.setWarningCode(new ArrayList<String>());
			if (StringUtils.equals("Y", njbrvx2Output.getTxMsgCode())) {
				if (sot707InputVO.getPurchaseAmt().compareTo(new BigDecimal("3000000")) == -1) { //當申購金額小於3000000時，需顯示提示訊息
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_007");
				}
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			} else if (StringUtils.equals("A", njbrvx2Output.getTxMsgCode())) {
				sot707OutputVO.getWarningCode().add("ehl_02_SOT_008");
			}
		}

		return sot707OutputVO;
	}

	/**
	 * 海外債金錢信託申購檢核、確認下行電文回壓資料庫欄位
	 *
	 */
	private void purchaseETFUpdateDB_TRUST(String prodType, String tradeSeq, BigDecimal seqNo, NJBRVX2OutputVO njbrvx2Output, BigDecimal defaultFeeRate, BigDecimal trustAmt) throws JBranchException {

		dam = getDataAccessManager();

		//SN
		if (StringUtils.equals("1", prodType)) {
			TBSOT_SN_TRADE_DPK pk = new TBSOT_SN_TRADE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(seqNo);
			TBSOT_SN_TRADE_DVO vo = new TBSOT_SN_TRADE_DVO();

			//find data by pks
			vo = (TBSOT_SN_TRADE_DVO) dam.findByPKey(TBSOT_SN_TRADE_DVO.TABLE_UID, pk);

			BigDecimal feeRate = new EsbUtil().decimalPoint(njbrvx2Output.getTxFeeRate(), 5);
			BigDecimal fee = new EsbUtil().decimalPoint(njbrvx2Output.getTxFee1(), 2);
			BigDecimal fee2 = new EsbUtil().decimalPoint(njbrvx2Output.getTxFee2(), 2);
			BigDecimal entrustAmt = new EsbUtil().decimalPoint(njbrvx2Output.getTxPrice(), 9);

			vo.setFEE(fee);
			vo.setFEE_RATE(feeRate);
			vo.setPAYABLE_FEE(fee2);
			vo.setCERTIFICATE_ID(njbrvx2Output.getTrustNo());
			vo.setENTRUST_AMT(entrustAmt);

			dam.update(vo);
		}
		//海外債
		else if (StringUtils.equals("2", prodType)) {
			TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(seqNo);
			TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();

			//find data by pks
			vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);

			BigDecimal feeRate = new EsbUtil().decimalPoint(njbrvx2Output.getTxFeeRate(), 5);
			BigDecimal fee = new EsbUtil().decimalPoint(njbrvx2Output.getTxFee1(), 2);
			BigDecimal fee2 = new EsbUtil().decimalPoint(njbrvx2Output.getTxFee2(), 2);

			vo.setFEE_RATE(feeRate);
			vo.setFEE(fee);
			vo.setFEE_DISCOUNT((defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null);
			double feeDiscount10 = 10.0;//10折不寫入資料庫
			if (vo.getFEE_DISCOUNT() != null && vo.getFEE_DISCOUNT().doubleValue() == feeDiscount10) {
				vo.setFEE_DISCOUNT(null);
			}
			vo.setCERTIFICATE_ID(njbrvx2Output.getTrustNo());
			vo.setPAYABLE_FEE(fee2);
			vo.setTOT_AMT(trustAmt.add(fee).add(fee2));

			dam.update(vo);
		}
	}

	/**
	 * 海外債/SN贖回檢核、確認 for js client
	 *
	 * 使用電文: NJBRVB9
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBRedeemBN(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBRedeemBN(body));
	}

	/**
	 * 海外債/SN贖回檢核、確認
	 *
	 * 使用電文: NJBRVB9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBRedeemBN(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		Map<String, String> trustTSMap = xmlInfo.doGetVariable("SOT.TRUST_TS", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String prodType = sot707InputVO.getProdType(); // 產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); // 交易序號
		String checkType = sot707InputVO.getCheckType(); // 電文確認碼

		// 欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}

		// 由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_SN_REDEEM);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NJBRVB9InputVO txBodyVO = new NJBRVB9InputVO();
		esbUtilInputVO.setNjbrvb9InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType); // 確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false)); // 申請日期
		txBodyVO.setKeyinNo(mainVO.getBATCH_SEQ());
		txBodyVO.setCustNo(mainVO.getCUST_ID()); // 身份証號
		// 2020-01-15 modify by ocean : WMS-CR-20191009-01_金錢信託套表需求申請單, 交易類別為金錢信託時，CUST_ID傳送99331241
		txBodyVO.setCustNo((StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE()) ? mainVO.getCUST_ID() : trustTSMap.get("M_CUSTNO"))); //身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE()); // 信託業務別
		txBodyVO.setTrustAcct(cbsservice.checkAcctLength(mainVO.getTRUST_ACCT())); // 信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); // 債券代號
		txBodyVO.setTrustNo(mainVO.getCERTIFICATE_ID()); // 憑証號碼
		txBodyVO.setTrustVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); // 庫存餘額
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); // 贖回時信託本金欄位放票面價值資料
		txBodyVO.setUnit(mainVO.getTRUST_UNIT()); // 庫存張數
		txBodyVO.setRcvAcct(cbsservice.checkAcctLength(mainVO.getCREDIT_ACCT())); // 入帳帳號

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		txBodyVO.setBranchNo(branchNbr); // 受理分行

		txBodyVO.setKeyinId(mainVO.getMODIFIER()); // 鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR()); // 委託面額幣別
		txBodyVO.setTxType(mainVO.getENTRUST_TYPE()); // 限價方式
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID()); // 解說專員
		txBodyVO.setRefPrice(new EsbUtil().decimalPadding(mainVO.getREF_VAL(), 4)); // 參考報價
		txBodyVO.setRefPriceDate(this.toChineseYearMMdd(mainVO.getREF_VAL_DATE(), false)); // 參考報價日期

		// 限價方式為不為市價，才需要放值
		if (!StringUtils.equals("2", mainVO.getENTRUST_TYPE())) {
			txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); // 委賣單價
		}

		if (StringUtils.equals("1", prodType)) { // SN
			txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getREDEEM_AMT(), 0)); // 委託面額
		} else if (StringUtils.equals("2", prodType)) { // 海外債
			txBodyVO.setUnit(new EsbUtil().decimalPadding(mainVO.getTRUST_UNIT(), 0)); // 庫存張數
			txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); // 委託面額
		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVB9OutputVO njbrvb9Output = esbUtilOutputVO.getNjbrvb9OutputVO();

			// 若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(njbrvb9Output);

			// 回傳下行電文資料儲存DB
			if (!isErr) {
				redeemBNUpdateDB(prodType, tradeSeq, njbrvb9Output, mainVO);

				// 若信託帳號暫停交易，回傳警示訊息
				sot707OutputVO.setWarningCode(new ArrayList<String>());
				if (StringUtils.equals("Y", njbrvb9Output.getType1())) {
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_009");
				}
			}
		}

		return sot707OutputVO;
	}
	/**
	 * 海外債/SN贖回檢核、確認_OBU
	 *
	 * 使用電文: AJBRVB9 理專鍵機債券贖回交易OBU
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBRedeemBN_OBU(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBRedeemBN_OBU(body));
	}
	
	/**
	 * 海外債/SN贖回檢核、確認
	 *
	 * 使用電文: AJBRVB9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBRedeemBN_OBU(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		Map<String, String> trustTSMap = xmlInfo.doGetVariable("SOT.TRUST_TS", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();
		
		String prodType = sot707InputVO.getProdType(); // 產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); // 交易序號
		String checkType = sot707InputVO.getCheckType(); // 電文確認碼
		
		// 欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}
		
		// 由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);
		
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, AJBRVB9);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());
		
		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		// body
		AJBRVB9InputVO txBodyVO = new AJBRVB9InputVO();
		esbUtilInputVO.setAjbrvb9InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType); // 確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false)); // 申請日期
		txBodyVO.setKeyinNo(mainVO.getBATCH_SEQ());
		txBodyVO.setCustNo(mainVO.getCUST_ID()); // 身份証號
		// 2020-01-15 modify by ocean : WMS-CR-20191009-01_金錢信託套表需求申請單, 交易類別為金錢信託時，CUST_ID傳送99331241
		txBodyVO.setCustNo((StringUtils.equals("S", mainVO.getTRUST_TRADE_TYPE()) ? mainVO.getCUST_ID() : trustTSMap.get("M_CUSTNO"))); //身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE()); // 信託業務別
		txBodyVO.setTrustAcct(cbsservice.checkAcctLength(mainVO.getTRUST_ACCT())); // 信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); // 債券代號
		txBodyVO.setTrustNo(mainVO.getCERTIFICATE_ID()); // 憑証號碼
		txBodyVO.setTrustVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); // 庫存餘額
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); // 贖回時信託本金欄位放票面價值資料
		txBodyVO.setUnit(mainVO.getTRUST_UNIT()); // 庫存張數
		txBodyVO.setRcvAcct(cbsservice.checkAcctLength(mainVO.getCREDIT_ACCT())); // 入帳帳號
		
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		txBodyVO.setBranchNo(branchNbr); // 受理分行
		
		txBodyVO.setKeyinId(mainVO.getMODIFIER()); // 鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR()); // 委託面額幣別
		txBodyVO.setTxType(mainVO.getENTRUST_TYPE()); // 限價方式
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID()); // 解說專員
		txBodyVO.setRefPrice(new EsbUtil().decimalPadding(mainVO.getREF_VAL(), 4)); // 參考報價
		txBodyVO.setRefPriceDate(this.toChineseYearMMdd(mainVO.getREF_VAL_DATE(), false)); // 參考報價日期
		
		// 限價方式為不為市價，才需要放值
		if (!StringUtils.equals("2", mainVO.getENTRUST_TYPE())) {
			txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); // 委賣單價
		}
		
		if (StringUtils.equals("1", prodType)) { // SN
			txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getREDEEM_AMT(), 0)); // 委託面額
		} else if (StringUtils.equals("2", prodType)) { // 海外債
			txBodyVO.setUnit(new EsbUtil().decimalPadding(mainVO.getTRUST_UNIT(), 0)); // 庫存張數
			txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); // 委託面額
		}
		
		/**
		 * 預留欄位	Filler	X(60)
		 * 1-7：參考報價(整數3小數4)
		 * 8-15：參考報價日期
		 * **/
		String refVal = new EsbUtil().decimalPadding(mainVO.getREF_VAL(), 3, 4);
		String refValDate = this.toChineseYearMMdd(mainVO.getREF_VAL_DATE());
		txBodyVO.setFiller(refVal + refValDate);
		
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			AJBRVB9OutputVO ajbrvb9Output = esbUtilOutputVO.getAjbrvb9OutputVO();
			
			// 若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(ajbrvb9Output);
			
			// 回傳下行電文資料儲存DB
			if (!isErr) {
				redeemBNUpdateDB_OBU(prodType, tradeSeq, ajbrvb9Output, mainVO);
				
				// 若信託帳號暫停交易，回傳警示訊息
				sot707OutputVO.setWarningCode(new ArrayList<String>());
				if (StringUtils.equals("Y", ajbrvb9Output.getType1())) {
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_009");
				}
			}
		}
		
		return sot707OutputVO;
	}

	private void redeemBNUpdateDB(String prodType, String tradeSeq, NJBRVB9OutputVO njbrvb9Output, MainInfoBean mainVO) throws JBranchException {
		dam = getDataAccessManager();
		String sql = null;
		QueryConditionIF condition = null;

		// 海外債
		if (StringUtils.equals("2", prodType)) {
			TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(mainVO.getSEQ_NO());
			TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();

			// find data by pks
			vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);
			BigDecimal d100 = BigDecimal.valueOf(100);
			BigDecimal payableFee = new EsbUtil().decimalPoint(njbrvb9Output.getBackFee(), 2);
			BigDecimal refVal = (mainVO.getREF_VAL() != null && mainVO.getREF_VAL().compareTo(BigDecimal.ZERO) != 0) ? mainVO.getREF_VAL().divide(d100) : BigDecimal.ZERO;
			BigDecimal purchaseAmt = (mainVO.getPURCHASE_AMT() != null && mainVO.getPURCHASE_AMT().compareTo(BigDecimal.ZERO) != 0) ? mainVO.getPURCHASE_AMT() : BigDecimal.ZERO;
			BigDecimal mgmfee = (mainVO.getMGM_FEE() != null && mainVO.getMGM_FEE().compareTo(BigDecimal.ZERO) != 0) ? mainVO.getMGM_FEE() : BigDecimal.ZERO;

			vo.setPAYABLE_FEE(payableFee);
			vo.setTOT_AMT((refVal.multiply(purchaseAmt)).add(payableFee).subtract(mgmfee));

			dam.update(vo);
		}
	}
	private void redeemBNUpdateDB_OBU(String prodType, String tradeSeq, AJBRVB9OutputVO ajbrvb9Output, MainInfoBean mainVO) throws JBranchException {
		dam = getDataAccessManager();
		// 海外債
		if (StringUtils.equals("2", prodType)) {
			TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
			pk.setTRADE_SEQ(tradeSeq);
			pk.setSEQ_NO(mainVO.getSEQ_NO());
			TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();
			
			// find data by pks
			vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);
			BigDecimal d100 = BigDecimal.valueOf(100);
			BigDecimal payableFee = new EsbUtil().decimalPoint(ajbrvb9Output.getBackFee(), 2);
			BigDecimal refVal = (mainVO.getREF_VAL() != null && mainVO.getREF_VAL().compareTo(BigDecimal.ZERO) != 0) ? mainVO.getREF_VAL().divide(d100) : BigDecimal.ZERO;
			BigDecimal purchaseAmt = (mainVO.getPURCHASE_AMT() != null && mainVO.getPURCHASE_AMT().compareTo(BigDecimal.ZERO) != 0) ? mainVO.getPURCHASE_AMT() : BigDecimal.ZERO;
			BigDecimal mgmfee = (mainVO.getMGM_FEE() != null && mainVO.getMGM_FEE().compareTo(BigDecimal.ZERO) != 0) ? mainVO.getMGM_FEE() : BigDecimal.ZERO;
			
			vo.setPAYABLE_FEE(payableFee);
			vo.setTOT_AMT((refVal.multiply(purchaseAmt)).add(payableFee).subtract(mgmfee));
			
			dam.update(vo);
		}
	}

	/**
	 * 海外債/SN長效單贖回檢核、確認 for js client
	 *
	 * 使用電文: NJBRVD9
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBRedeemBN_GTC(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBRedeemBN_GTC(body));
	}

	/**
	 * 海外債長效單贖回檢核、確認
	 *
	 * 使用電文: NJBRVD9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBRedeemBN_GTC(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String prodType = sot707InputVO.getProdType(); // 產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); // 交易序號
		String checkType = sot707InputVO.getCheckType(); // 電文確認碼

		// 欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}

		// 由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, BOND_GTC_REDEEM);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NJBRVD9InputVO txBodyVO = new NJBRVD9InputVO();
		esbUtilInputVO.setNjbrvd9InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType); // 確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false)); // 申請日期
		txBodyVO.setKeyinNo(mainVO.getBATCH_SEQ());
		txBodyVO.setCustNo(mainVO.getCUST_ID()); // 身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE()); // 信託業務別
		txBodyVO.setTrustAcct(mainVO.getTRUST_ACCT()); // 信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); // 債券代號
		txBodyVO.setTrustNo(mainVO.getCERTIFICATE_ID()); // 憑証號碼
		txBodyVO.setTrustVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); // 庫存餘額
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); // 贖回時信託本金欄位放票面價值資料
		txBodyVO.setRcvAcct(mainVO.getCREDIT_ACCT()); // 入帳帳號

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		txBodyVO.setBranchNo(branchNbr); // 受理分行

		txBodyVO.setKeyinId(mainVO.getMODIFIER()); // 鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR()); // 委託面額幣別
		
		String entrustType = "";
		if (mainVO.getENTRUST_TYPE().equals("1")) {
			// 限價才需帶委賣單價
			txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); // 委賣單價
			entrustType = "2";	// 限價一樣帶"2"
		} else {
			entrustType = mainVO.getENTRUST_TYPE();
		}
		txBodyVO.setTxType(entrustType); // 限價方式
		
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID()); // 解說專員
		txBodyVO.setUnit(new EsbUtil().decimalPadding(mainVO.getTRUST_UNIT(), 0)); // 庫存張數
		txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); // 委託面額
		txBodyVO.setGtcEndDate(this.toChineseYearMMdd(mainVO.getGTC_END_DATE(), false)); // 長效單迄日

		/**
		 * Filler
		 * 第1位  ：長效單/預約單			(空白：長效單 1：預約單)
		 * 第2-9位：預約單/長效單生效起日	9(08)
		 * **/
		String gtcYN = mainVO.getGTC_YN().equals("Y") ? " " : "1";
		String gtcStartDate = mainVO.getGTC_START_DATE() == null ? "" : this.toChineseYearMMdd(mainVO.getGTC_START_DATE());
		String filler = gtcYN + gtcStartDate;
		txBodyVO.setFiller(filler);
		
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVD9OutputVO njbrvd9Output = esbUtilOutputVO.getNjbrvd9OutputVO();

			// 若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(njbrvd9Output);

			// 回傳下行電文資料儲存DB
			if (!isErr) {
				redeemBNUpdateDB_GTC(prodType, tradeSeq, njbrvd9Output, mainVO);

				// 若信託帳號暫停交易，回傳警示訊息
				sot707OutputVO.setWarningCode(new ArrayList<String>());
				if (StringUtils.equals("Y", njbrvd9Output.getType1())) {
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_009");
				}
			}
		}

		return sot707OutputVO;
	}
	
	/**
	 * 海外債/SN長效單贖回檢核、確認_OBU
	 *
	 * 使用電文: AJBRVD9 理專鍵機債券長效單贖回交易OBU
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void verifyESBRedeemBN_GTC_OBU(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBRedeemBN_GTC_OBU(body));
	}
	
	/**
	 * 海外債長效單贖回檢核、確認_OBU
	 *
	 * 使用電文: AJBRVD9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO verifyESBRedeemBN_GTC_OBU(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();
		
		String prodType = sot707InputVO.getProdType();		// 產品類別
		String tradeSeq = sot707InputVO.getTradeSeq(); 		// 交易序號
		String checkType = sot707InputVO.getCheckType(); 	// 電文確認碼
		
		// 欄位檢核
		if (StringUtils.isBlank(prodType) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("產品類別、交易序號或電文確認碼未輸入");
		}
		
		// 由產品類別與交易序號取得主檔資料作為電文上行
		MainInfoBean mainVO = mainInfo(prodType, tradeSeq);
		
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, AJBRVD9);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());
		
		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		// body
		AJBRVD9InputVO txBodyVO = new AJBRVD9InputVO();
		esbUtilInputVO.setAjbrvd9InputVO(txBodyVO);
		txBodyVO.setCheckCode(checkType);													// 確認碼
		txBodyVO.setApplyDate(this.toChineseYearMMdd(mainVO.getTRADE_DATE(), false));		// 申請日期
		txBodyVO.setKeyinNo(mainVO.getBATCH_SEQ());
		txBodyVO.setCustNo(mainVO.getCUST_ID());											// 身份証號
		txBodyVO.setTrustType(mainVO.getTRUST_CURR_TYPE());									// 信託業務別
		txBodyVO.setTrustAcct(mainVO.getTRUST_ACCT());										// 信託帳號
		txBodyVO.setBondNo(mainVO.getPROD_ID()); 											// 債券代號
		txBodyVO.setTrustNo(mainVO.getCERTIFICATE_ID()); 									// 憑証號碼
		txBodyVO.setTrustVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0));	// 庫存餘額
		txBodyVO.setBondVal(new EsbUtil().decimalPadding(mainVO.getBOND_VALUE(), 0)); 		// 贖回時信託本金欄位放票面價值資料
		txBodyVO.setRcvAcct(mainVO.getCREDIT_ACCT()); 										// 入帳帳號
		
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = mainVO.getBRANCH_NBR();
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
		txBodyVO.setBranchNo(branchNbr); 													// 受理分行
		
		txBodyVO.setKeyinId(mainVO.getMODIFIER());											// 鍵機櫃員
		txBodyVO.setTxCur(mainVO.getPROD_CURR());											// 委託面額幣別
//		txBodyVO.setTxType(mainVO.getENTRUST_TYPE());										// 限價方式
		txBodyVO.setTxExTeller(mainVO.getNARRATOR_ID());									// 解說專員
//		txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9));		// 委賣單價
		txBodyVO.setUnit(new EsbUtil().decimalPadding(mainVO.getTRUST_UNIT(), 0));			// 庫存張數
		txBodyVO.setTxVal(new EsbUtil().decimalPadding(mainVO.getPURCHASE_AMT(), 0)); 		// 委託面額
		txBodyVO.setGtcEndDate(this.toChineseYearMMdd(mainVO.getGTC_END_DATE(), false));	// 長效單迄日
		
		String entrustType = "";
		if (mainVO.getENTRUST_TYPE().equals("1")) {
			// 限價才需帶委賣單價
			txBodyVO.setTxPrice(new EsbUtil().decimalPadding(mainVO.getENTRUST_AMT(), 9)); 	// 委賣單價
			entrustType = "2";	// 限價一樣帶"2"
		} else {
			entrustType = mainVO.getENTRUST_TYPE();
		}
		txBodyVO.setTxType(entrustType); // 限價方式
		
		/**
		 * Filler
		 * 第1位  ：長效單/預約單			(空白：長效單 1：預約單)
		 * 第2-9位：預約單/長效單生效起日	9(08)
		 * **/
		String gtcYN = mainVO.getGTC_YN().equals("Y") ? " " : "1";
		String gtcStartDate = mainVO.getGTC_START_DATE() == null ? "" : this.toChineseYearMMdd(mainVO.getGTC_START_DATE());
		String filler = gtcYN + gtcStartDate;
		txBodyVO.setFiller(filler);
		
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			AJBRVD9OutputVO ajbrvd9Output = esbUtilOutputVO.getAjbrvd9OutputVO();
			
			// 若有回傳錯誤訊息ERR_COD,將錯誤碼及錯誤訊息拋出
			Boolean isErr = checkError(ajbrvd9Output);
			
			// 回傳下行電文資料儲存DB
			if (!isErr) {
				redeemBNUpdateDB_GTC_OBU(prodType, tradeSeq, ajbrvd9Output, mainVO);
				
				// 若信託帳號暫停交易，回傳警示訊息
				sot707OutputVO.setWarningCode(new ArrayList<String>());
				if (StringUtils.equals("Y", ajbrvd9Output.getType1())) {
					sot707OutputVO.getWarningCode().add("ehl_02_SOT_009");
				}
			}
		}
		return sot707OutputVO;
	}

	private void redeemBNUpdateDB_GTC(String prodType, String tradeSeq, NJBRVD9OutputVO njbrvd9Output, MainInfoBean mainVO) throws Exception {
		dam = getDataAccessManager();
		String sql = null;
		QueryConditionIF condition = null;

		TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(mainVO.getSEQ_NO());
		TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();

		// find data by pks
		vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);
		BigDecimal refVal = new EsbUtil().decimalPoint(njbrvd9Output.getRefVal(), 9);
		Date refValDate = new EsbUtil().toAdYearMMdd(njbrvd9Output.getRefValDate(), false);

		vo.setREF_VAL(refVal);
		vo.setREF_VAL_DATE(new Timestamp(refValDate.getTime()));

		dam.update(vo);
	}
	
	private void redeemBNUpdateDB_GTC_OBU(String prodType, String tradeSeq, AJBRVD9OutputVO ajbrvd9Output, MainInfoBean mainVO) throws Exception {
		dam = getDataAccessManager();
		String sql = null;
		QueryConditionIF condition = null;
		
		TBSOT_BN_TRADE_DPK pk = new TBSOT_BN_TRADE_DPK();
		pk.setTRADE_SEQ(tradeSeq);
		pk.setSEQ_NO(mainVO.getSEQ_NO());
		TBSOT_BN_TRADE_DVO vo = new TBSOT_BN_TRADE_DVO();
		
		// find data by pks
		vo = (TBSOT_BN_TRADE_DVO) dam.findByPKey(TBSOT_BN_TRADE_DVO.TABLE_UID, pk);
		BigDecimal refVal = new EsbUtil().decimalPoint(ajbrvd9Output.getRefVal(), 9);
		Date refValDate = new EsbUtil().toAdYearMMdd(ajbrvd9Output.getRefValDate(), false);
		
		vo.setREF_VAL(refVal);
		vo.setREF_VAL_DATE(new Timestamp(refValDate.getTime()));
		
		dam.update(vo);
	}

	/**
	 * 海外債金錢信託產品庫存資料查詢 for js client
	 *
	 * 使用電文: NJBRVX3
	 */
	public void getCustAssetBondData_TRUST(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getCustAssetBondData_TRUST(body));
	}

	/**
	 * 海外債金錢信託產品庫存資料查詢 包含：可贖回資料 使用電文: NJBRVX3
	 */
	public SOT707OutputVO getCustAssetBondData_TRUST(Object body) throws Exception {

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> trustTSMap = xmlInfo.doGetVariable("SOT.TRUST_TS", FormatHelper.FORMAT_3);

		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String custID = sot707InputVO.getCustId();
		String debitACCT = StringUtils.trim(sot707InputVO.getDebitAcct());

		//欄位檢核
		if (StringUtils.isBlank(custID) || StringUtils.isBlank(debitACCT)) {
			throw new JBranchException("客戶ID未輸入或契約編號未選擇");
		}

		//init util
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, NJBRVX3);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		//body
		NJBRVX3InputVO txBodyVO = new NJBRVX3InputVO();
		esbUtilInputVO.setNjbrvx3InputVO(txBodyVO);
		txBodyVO.setCustId(trustTSMap.get("M_CUSTNO")); //客戶ID
		txBodyVO.setDebitACCT(debitACCT);

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		//根據產品類別以及查詢條件回傳庫存資料
		List<CustAssetBondVO> assetList = new ArrayList<>();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVX3OutputVO outputVO = esbUtilOutputVO.getNjbrvx3OutputVO();

			List<NJBRVX3OutputVODetails> details = outputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NJBRVX3OutputVODetails>() : details;

			//取得庫存資料
			for (NJBRVX3OutputVODetails detail : details) {
				Date applyDate = new EsbUtil().toAdYearMMdd(detail.getApplyDate(), false);
				Date refPriceDate = new EsbUtil().toAdYearMMdd(detail.getRefPriceDate(), false);
				Date newInterestDate = new EsbUtil().toAdYearMMdd(detail.getNewInterestDate(), false);

				if (StringUtils.isNotEmpty(detail.getTrustNo())) {
					//符合資料放入庫存CustAssetBondVO
					CustAssetBondVO asset = new CustAssetBondVO();
					asset.setTrustNo(detail.getTrustNo()); //憑証單號
					asset.setBondNo(detail.getBondNo()); //債券代號
					asset.setBondName(detail.getBondName()); //債券名稱
					asset.setCurCode(detail.getCurCode()); //幣別
					asset.setUnit(new EsbUtil().decimalPoint(detail.getUnit(), 0)); //庫存張數
					asset.setTrustVal(new EsbUtil().decimalPoint(detail.getTrustVal(), 0)); //庫存面額	
					asset.setTrustType(detail.getTrustType()); //信託業務別
					asset.setTrustAcct(detail.getTrustAcct()); //信託帳號
					asset.setBondType(detail.getBondType()); //債券種類
					asset.setBondVal(new EsbUtil().decimalPoint(detail.getBondVal(), 0)); //票面價值
					asset.setRefPrice(new EsbUtil().decimalPoint(detail.getRefPrice(), 4)); //參考報價(市價)
					asset.setRefPriceDate(refPriceDate); //參考報價日期
					asset.setApplyDate(applyDate); //申購日期
					asset.setTrustAmt(new EsbUtil().decimalPoint(detail.getTrustAmt(), 4)); //信託本金
					asset.setTrustFeeRate(new EsbUtil().decimalPoint(detail.getTrustFeeRate(), 5)); //信託管理費率
					asset.setPayableFeeRate(new EsbUtil().decimalPoint(detail.getPayableFee(), 9)); //應收前手息率
					asset.setPayableFee(new EsbUtil().decimalPoint(detail.getTrustVal(), 0).multiply(new EsbUtil().decimalPoint(detail.getPayableFee(), 9)).divide(new BigDecimal("100"))); //應收前手息
					asset.setAccuInterest(new EsbUtil().decimalPoint(detail.getAccuInterest(), 4)); //累積配息
					asset.setStorageStatus(detail.getStorageStatus()); //狀態
					asset.setNewInterestDate(newInterestDate); //最近一期配息日期
					asset.setNewAccuInterest(new EsbUtil().decimalPoint(detail.getNewAccuInterest(), 4)); //最近一期配息金額

					//2018.01.04 add by Carley(已付前手息,應收前手息,含息報酬率三欄的來源調整為電文來源 #4106)
					asset.setFrontfee1(new EsbUtil().decimalPoint(detail.getFrontfee1(), 2)); //已付前手息
					asset.setFrontfee2(new EsbUtil().decimalPoint(detail.getFrontfee2(), 2)); //應收前手息
					asset.setSign(detail.getSign()); //含累積現金配息報酬率正負號(空白為正)
					asset.setIntRate(new EsbUtil().decimalPoint(detail.getIntRate(), 2)); //含累積現金配息報酬率

					asset.setTrustTS("M"); //S=特金,M=金錢信託
					assetList.add(asset);
				}
			}
		}

		//查無資料則拋出錯誤訊息
		if (CollectionUtils.isEmpty(assetList)) {
			logger.error("SOT707查無資料");
			throw new JBranchException("ehl_01_common_009");
		}

		sot707OutputVO.setCustAssetBondList(assetList);

		return sot707OutputVO;
	}

	/**
	 * 海外債/SN產品庫存資料查詢 for js client
	 *
	 * 使用電文: NJBRVB1
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void getCustAssetBondData(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getCustAssetBondData(body));
	}

	/**
	 * 海外債/SN產品庫存資料查詢 包含：可贖回資料 使用電文: NJBRVB1-DBU 使用電文: AJBRVB1-OBU
	 * 2022.04.20 #1060 AJBRVB1功能修復 By Sam Tu
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO getCustAssetBondData(Object body) throws Exception {
		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String custID = sot707InputVO.getCustId();
		String prodType = sot707InputVO.getProdType();
		String prodID = sot707InputVO.getProdId();
		String prodName = sot707InputVO.getProdName();
		Date startDate = sot707InputVO.getStartDate();
		Date endDate = sot707InputVO.getEndDate();

		// 欄位檢核
		if (StringUtils.isBlank(custID) || StringUtils.isBlank(prodType)) {
			throw new JBranchException("客戶ID或產品類別未輸入");
		}

		// init util
		ESBUtilInputVO esbUtilInputVO;
		//#1060，避免電文只會送NJBRVB1
		if(StringUtils.isBlank(sot707InputVO.getIsOBU())){
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			sot707InputVO.setIsOBU(sot701.isObu(custID) ? "Y" : "N");
		}
		if (StringUtils.equals("Y", sot707InputVO.getIsOBU())) {
			esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_SN_ASSETS_OBU);
		} else {
			esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_SN_ASSETS_DBU);
		}
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		if (StringUtils.equals("Y", sot707InputVO.getIsOBU())) {
			AJBRVB1InputVO txBodyVO = new AJBRVB1InputVO();
			esbUtilInputVO.setAjbrvb1InputVO(txBodyVO);
			txBodyVO.setCustId(custID); // 客戶ID
		} else {
			NJBRVB1InputVO txBodyVO = new NJBRVB1InputVO();
			esbUtilInputVO.setNjbrvb1InputVO(txBodyVO);
			txBodyVO.setCustId(custID); // 客戶ID
		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		// 根據產品類別以及查詢條件回傳庫存資料
		List<CustAssetBondVO> assetList = new ArrayList<>();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVB1OutputVO outputVO;// DBU OBU 共用VO
			if (StringUtils.equals("Y", sot707InputVO.getIsOBU())) {
				outputVO = esbUtilOutputVO.getAjbrvb1OutputVO();// OBU
			} else {
				outputVO = esbUtilOutputVO.getNjbrvb1OutputVO();// DBU
			}

			List<NJBRVB1OutputVODetials> details = outputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NJBRVB1OutputVODetials>() : details;

			// 取得庫存資料
			for (NJBRVB1OutputVODetials detail : details) {
				String bondType = detail.getBondType();
				String bondNo = detail.getBondNo();
				String bondName = detail.getBondName();
				Date applyDate = new EsbUtil().toAdYearMMdd(detail.getApplyDate(), false);
				Date refPriceDate = new EsbUtil().toAdYearMMdd(detail.getRefPriceDate(), false);
				Date newInterestDate = new EsbUtil().toAdYearMMdd(detail.getNewInterestDate(), false);

				// 排除條件
				// 限制以產品類別取得資料
				Boolean notQuilifiedBondType = !(("1".equals(prodType) && "SN".equals(bondType)) || ("2".equals(prodType) && !"SN".equals(bondType))) ? Boolean.TRUE : Boolean.FALSE;
				// 若有輸入產品ID，則只取該產品ID
				Boolean notQuilifiedProdID = (StringUtils.isNotBlank(prodID))
				// ? !StringUtils.equals(prodID, bondNo) : Boolean.FALSE;
				? !bondNo.contains(prodID)
						: Boolean.FALSE;
				// 若有輸入產品名稱，則需取得產品名稱包含傳入參數資料
				Boolean notQuilifiedProdName = (StringUtils.isNotBlank(prodName) && StringUtils.isNotBlank(bondName)) ? !bondName.contains(prodName) : Boolean.FALSE;
				// 若有輸入申購起日，則申購日期需大於等於申購起日
				Boolean notQuilifiedStartDate = (startDate != null && applyDate.before(startDate)) ? Boolean.TRUE : Boolean.FALSE;
				// 若有輸入申購迄日，則申購日期需小於等於申購迄日
				Boolean notQuilifiedEndDate = (endDate != null && applyDate.after(endDate)) ? Boolean.TRUE : Boolean.FALSE;

				if (notQuilifiedBondType || notQuilifiedProdID || notQuilifiedProdName || notQuilifiedStartDate || notQuilifiedEndDate) {
					continue;
				}
				// logger.debug("參考報價日期：" + detail.getRefPriceDate());
				// 符合資料放入庫存CustAssetBondVO
				CustAssetBondVO asset = new CustAssetBondVO();
				asset.setTrustNo(detail.getTrustNo()); // 憑証單號
				asset.setBondNo(detail.getBondNo()); // 債券代號
				asset.setBondName(detail.getBondName()); // 債券名稱
				asset.setCurCode(detail.getCurCode()); // 幣別
				asset.setUnit(new EsbUtil().decimalPoint(detail.getUnit(), 0)); // 庫存張數
				// NJBRVB1給怪值
				try {
					asset.setTrustVal(new EsbUtil().decimalPoint(detail.getTrustVal(), 0)); // 庫存面額
				} catch (Exception e) {
					asset.setTrustVal(new BigDecimal(0));
				}
				asset.setTrustType(detail.getTrustType()); // 信託業務別
				asset.setTrustAcct(detail.getTrustAcct()); // 信託帳號
				asset.setBondType(detail.getBondType()); // 債券種類
				asset.setBondVal(new EsbUtil().decimalPoint(detail.getBondVal(), 0)); // 票面價值
				asset.setRefPrice(new EsbUtil().decimalPoint(detail.getRefPrice(), 4)); // 參考報價(市價)
				asset.setRefPriceDate(refPriceDate); // 參考報價日期
				asset.setApplyDate(applyDate); // 申購日期
				asset.setTrustAmt(new EsbUtil().decimalPoint(detail.getTrustAmt(), 4)); // 信託本金
				asset.setTrustFeeRate(new EsbUtil().decimalPoint(detail.getTrustFeeRate(), 5)); // 信託管理費率
				asset.setPayableFeeRate(new EsbUtil().decimalPoint(detail.getPayableFee(), 9)); // 應收前手息率
				// NJBRVB1給怪值
				try {
					asset.setPayableFee(new EsbUtil().decimalPoint(detail.getTrustVal(), 0).multiply(new EsbUtil().decimalPoint(detail.getPayableFee(), 9)).divide(new BigDecimal("100"))); // 應收前手息
				} catch (Exception e) {
					asset.setPayableFee(new BigDecimal(0));
				}
			    asset.setAccuInterest(new EsbUtil().decimalPoint(detail.getAccuInterest(), 4)); // 累積配息
				

				asset.setStorageStatus(detail.getStorageStatus()); // 狀態
				asset.setNewInterestDate(newInterestDate); // 最近一期配息日期
				asset.setNewAccuInterest(new EsbUtil().decimalPoint(detail.getNewAccuInterest(), 4)); // 最近一期配息金額

				// 2018.01.04 add by Carley(已付前手息,應收前手息,含息報酬率三欄的來源調整為電文來源 #4106)
				asset.setFrontfee1(new EsbUtil().decimalPoint(detail.getFrontfee1(), 2)); // 已付前手息
				asset.setFrontfee2(new EsbUtil().decimalPoint(detail.getFrontfee2(), 2)); // 應收前手息
				asset.setSign(detail.getSign()); // 含累積現金配息報酬率正負號(空白為正)
				asset.setIntRate(new EsbUtil().decimalPoint(detail.getIntRate(), 2)); // 含累積現金配息報酬率

				asset.setTrustTS("S");																	//S=特金,M=金錢信託
//				asset.setIsPledged(StringUtils.isEmpty(detail.getCmkType()) ? "N" : StringUtils.equals(detail.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N");	//質借圈存註記

				assetList.add(asset);
			}
		}

		// 查無資料則拋出錯誤訊息
		if (CollectionUtils.isEmpty(assetList)) {
			logger.error("SOT707查無資料:" + prodID);
			throw new JBranchException("ehl_01_common_009");
		}

		sot707OutputVO.setCustAssetBondList(assetList);

		return sot707OutputVO;
	}

	/**
	 * 海外債/SN產品參考報價資料查詢 for js client
	 *
	 * 使用電文: NJBRVA1
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void getProdRefVal(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getProdRefVal(body));
	}

	/**
	 * 海外債/SN產品參考報價資料查詢
	 *
	 * 使用電文: NJBRVA1
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO getProdRefVal(Object body) throws Exception {
		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String custID = sot707InputVO.getCustId();
		String prodID = sot707InputVO.getProdId();

		// 欄位檢核
		if (StringUtils.isBlank(custID) || StringUtils.isBlank(prodID)) {
			throw new JBranchException("客戶ID或產品代碼未輸入");
		}

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_SN_QUG);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NJBRVA1InputVO txBodyVO = new NJBRVA1InputVO();
		esbUtilInputVO.setNjbrva1InputVO(txBodyVO);
		txBodyVO.setCustId(custID); // 客戶ID
		txBodyVO.setBondNo(prodID); // 商品代碼

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVA1OutputVO njbrva1OutputVO = esbUtilOutputVO.getNjbrva1OutputVO();

			List<NJBRVA1OutputVODetails> details = njbrva1OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NJBRVA1OutputVODetails>() : details;

			// output datas
			List<ProdRefValVO> prodRefValVOs = new ArrayList<>();
			sot707OutputVO.setProdRefVal(prodRefValVOs);

			for (NJBRVA1OutputVODetails detail : details) {
				ProdRefValVO prodRefValVO = new ProdRefValVO();
				prodRefValVO.setCustId(njbrva1OutputVO.getCustId());
				prodRefValVO.setBondNo(njbrva1OutputVO.getBondNo());
				prodRefValVO.setBondName(detail.getBondName());
				prodRefValVO.setProdRiskLv(detail.getProdRiskLv());
				prodRefValVO.setProdMinBuyAmt(new EsbUtil().decimalPoint(detail.getProdMinBuyAmt(), 0));//modify by paul 改為手動轉date
				prodRefValVO.setProdMinGrdAmt(new EsbUtil().decimalPoint(detail.getProdMinGrdAmt(), 0));//modify by paul 改為手動轉date
				prodRefValVO.setProdCurr(detail.getProdCurr());
				prodRefValVO.setRefVal(new EsbUtil().decimalPoint(detail.getRefVal(), 4));
				prodRefValVO.setRefValDate(new EsbUtil().toAdYearMMdd(detail.getRefValDate(), false));

				prodRefValVOs.add(prodRefValVO);
			}
		}

		return sot707OutputVO;
	}

	/**
	 * 海外債/SN產品首購資料查詢 for js client
	 *
	 * 使用電文: NJBRVA3
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getIsCustFirstTrade(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getIsCustFirstTrade(body));
	}

	/**
	 * 海外債/SN產品首購資料查詢
	 *
	 * 使用電文: NJBRVA3
	 *
	 * @param body
	 * @throws Exception
	 */
	public Boolean getIsCustFirstTrade(Object body) throws Exception {
		sot707InputVO = (SOT707InputVO) body;

		String custID = sot707InputVO.getCustId();
		String prodID = sot707InputVO.getProdId();

		// 欄位檢核
		if (StringUtils.isBlank(custID) || StringUtils.isBlank(prodID)) {
			throw new JBranchException("客戶ID或產品代碼未輸入");
		}

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_SN_FIRST_TRADE);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NJBRVA3InputVO txBodyVO = new NJBRVA3InputVO();
		esbUtilInputVO.setNjbrva3InputVO(txBodyVO);
		txBodyVO.setCustId(custID);
		txBodyVO.setBondNo(prodID);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		Boolean isFirstTrade = Boolean.FALSE;

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVA3OutputVO njbrva3OutputVO = esbUtilOutputVO.getNjbrva3OutputVO();

			List<NJBRVA3OutputVODetails> details = njbrva3OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NJBRVA3OutputVODetails>() : details;

			for (NJBRVA3OutputVODetails detail : details) {
				String haveTradeRec = detail.getHaveTradeRec();
				// String haveTradeRecTody = detail.getHaveTradeRecToday();

				isFirstTrade = (StringUtils.isNotBlank(haveTradeRec) && StringUtils.equals("Y", haveTradeRec)) ? Boolean.TRUE : Boolean.FALSE;
			}
		}

		return isFirstTrade;
	}
	
	/**
	 * 海外債/SN產品首購資料查詢_OBU
	 *
	 * 使用電文: AJBRVA3 理專鍵機債券首購查詢OBU
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getIsCustFirstTradeOBU(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getIsCustFirstTradeOBU(body));
	}
	
	/**
	 * 海外債/SN產品首購資料查詢_OBU
	 *
	 * 使用電文: AJBRVA3
	 *
	 * @param body
	 * @throws Exception
	 */
	public Boolean getIsCustFirstTradeOBU(Object body) throws Exception {
		sot707InputVO = (SOT707InputVO) body;
		
		String custID = sot707InputVO.getCustId();
		String prodID = sot707InputVO.getProdId();
		
		// 欄位檢核
		if (StringUtils.isBlank(custID) || StringUtils.isBlank(prodID)) {
			throw new JBranchException("客戶ID或產品代碼未輸入");
		}
		
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, AJBRVA3);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());
		
		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		// body
		AJBRVA3InputVO txBodyVO = new AJBRVA3InputVO();
		esbUtilInputVO.setAjbrva3InputVO(txBodyVO);
		txBodyVO.setCustId(custID);
		txBodyVO.setBondNo(prodID);
		
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		Boolean isFirstTrade = Boolean.FALSE;
		
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			AJBRVA3OutputVO ajbrva3OutputVO = esbUtilOutputVO.getAjbrva3OutputVO();
			
			List<AJBRVA3OutputVODetails> details = ajbrva3OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<AJBRVA3OutputVODetails>() : details;
			
			for (AJBRVA3OutputVODetails detail : details) {
				String haveTradeRec = detail.getHaveTradeRec();
				// String haveTradeRecTody = detail.getHaveTradeRecToday();
				
				isFirstTrade = (StringUtils.isNotBlank(haveTradeRec) && StringUtils.equals("Y", haveTradeRec)) ? Boolean.TRUE : Boolean.FALSE;
			}
		}
		
		return isFirstTrade;
	}

	/**
	 * 海外債/SN產品庫存資料查詢 for js client
	 *
	 * 使用電文: NFVIPA
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void getNewCustAssetBondData(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getNewCustAssetBondData(body));
	}

	/**
	 * 海外債/SN產品庫存資料查詢 包含：可贖回、不可贖回資料 使用電文: NFVIPA
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO getNewCustAssetBondData(Object body) throws Exception {
		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String custID = sot707InputVO.getCustId();
		String prodType = sot707InputVO.getProdType();
		String prodID = sot707InputVO.getProdId();
		String prodName = sot707InputVO.getProdName();
		Date startDate = sot707InputVO.getStartDate();
		Date endDate = sot707InputVO.getEndDate();
		String isOBU = sot707InputVO.getIsOBU();

		// 欄位檢核
		if (StringUtils.isBlank(custID) || StringUtils.isBlank(prodType)) {
			throw new JBranchException("客戶ID或產品類別未輸入");
		}

		// init util
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_ASSETS);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		if (sot707InputVO.getProdType().equals("1")) {
			// SN商品
			NFVIPAInputVO txBodyVO = new NFVIPAInputVO();
			esbUtilInputVO.setNfvipaInputVO(txBodyVO);
			txBodyVO.setCUSID(custID); // 客戶ID
			txBodyVO.setFUNCTION("SN");
			if (isOBU.equals("Y")) {
				txBodyVO.setUNIT("O");
			} else {
				txBodyVO.setUNIT("D");
			}
		} else {
			// 海外債 sot707InputVO.getProdType()應為2
			NFVIPAInputVO txBodyVO = new NFVIPAInputVO();
			esbUtilInputVO.setNfvipaInputVO(txBodyVO);
			txBodyVO.setCUSID(custID); // 客戶ID
			txBodyVO.setFUNCTION("UJ");
			if (isOBU.equals("Y")) {
				txBodyVO.setUNIT("O");
			} else {
				txBodyVO.setUNIT("D");
			}
		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		// 根據產品類別以及查詢條件回傳庫存資料
		List<CustAssetBondVO> assetList = new ArrayList<>();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFVIPAOutputVO outputVO;// DBU OBU 共用VO
			outputVO = esbUtilOutputVO.getNfvipaOutputVO();

			List<NFVIPAOutputVODetails> details = outputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NFVIPAOutputVODetails>() : details;

			// 取得庫存資料
			for (NFVIPAOutputVODetails detail : details) {
				String bondType = detail.getV0100();
				String bondNo = detail.getV0103();
				String bondName = detail.getV0104();
				Date applyDate = new EsbUtil().toAdYearMMdd(detail.getV0134(), false);
				Date refPriceDate = new EsbUtil().toAdYearMMdd(detail.getV0111(), false);
				Date newInterestDate = null;
				if("00000000".equals(detail.getV0127())) {
					newInterestDate = null;
				} else {
					newInterestDate = new EsbUtil().toAdYearMMdd(detail.getV0127(), false);
				}
				

				// 排除條件
				// 限制以產品類別取得資料
				Boolean notQuilifiedBondType = !(("1".equals(prodType) && "SN".equals(bondType)) || ("2".equals(prodType) && !"SN".equals(bondType))) ? Boolean.TRUE : Boolean.FALSE;
				// 若有輸入產品ID，則只取該產品ID
				Boolean notQuilifiedProdID = (StringUtils.isNotBlank(prodID))
				// ? !StringUtils.equals(prodID, bondNo) : Boolean.FALSE;
				? !bondNo.contains(prodID)
						: Boolean.FALSE;
				// 若有輸入產品名稱，則需取得產品名稱包含傳入參數資料
				Boolean notQuilifiedProdName = (StringUtils.isNotBlank(prodName) && StringUtils.isNotBlank(bondName)) ? !bondName.contains(prodName) : Boolean.FALSE;
				// 若有輸入申購起日，則申購日期需大於等於申購起日
				Boolean notQuilifiedStartDate = (startDate != null && applyDate.before(startDate)) ? Boolean.TRUE : Boolean.FALSE;
				// 若有輸入申購迄日，則申購日期需小於等於申購迄日
				Boolean notQuilifiedEndDate = (endDate != null && applyDate.after(endDate)) ? Boolean.TRUE : Boolean.FALSE;

				if (notQuilifiedBondType || notQuilifiedProdID || notQuilifiedProdName || notQuilifiedStartDate || notQuilifiedEndDate) {
					continue;
				}
				// logger.debug("參考報價日期：" + detail.getRefPriceDate());
				// 符合資料放入庫存CustAssetBondVO
				CustAssetBondVO asset = new CustAssetBondVO();
				asset.setTrustNo(detail.getV0105()); // 憑証單號
				asset.setBondNo(bondNo); // 債券代號
				asset.setBondName(bondName); // 債券名稱
				asset.setCurCode(detail.getV0106()); // 幣別
				asset.setTrustVal(new EsbUtil().decimalPoint(detail.getV0133(), 0)); // 庫存面額
				asset.setRefPrice(new EsbUtil().decimalPoint(detail.getV0110(), 9)); // 參考報價(市價)
				asset.setApplyDate(applyDate); // 申購日期
				asset.setTrustAmt(new EsbUtil().decimalPoint(detail.getV0109(), 4)); // 信託本金
				asset.setPayableFee(new EsbUtil().decimalPoint(detail.getV0130(), 0)); // 應收前手息
				asset.setAccuInterest(new EsbUtil().decimalPoint(detail.getV0121(), 2)); // 累積配息
				asset.setStorageStatus("1"); // 狀態
				asset.setNewInterestDate(newInterestDate); // 最近一期配息日期
				asset.setNewAccuInterest(new EsbUtil().decimalPoint(detail.getV0128(), 4)); // 最近一期配息金額
				// 2018.01.04 add by Carley(已付前手息,應收前手息,含息報酬率三欄的來源調整為電文來源 #4106)
				asset.setFrontfee1(new EsbUtil().decimalPoint(detail.getV0129(), 2)); // 已付前手息
				asset.setFrontfee2(new EsbUtil().decimalPoint(detail.getV0130(), 2)); // 應收前手息
				asset.setSign(detail.getV0131()); // 含累積現金配息報酬率正負號(空白為正)
				asset.setIntRate(new EsbUtil().decimalPoint(detail.getV0132(), 2)); // 含累積現金配息報酬率
//				asset.setIsPledged(StringUtils.isEmpty(detail.getV0135()) ? "N" : "Y");	//質借圈存註記
				asset.setTrustTS("S");																	//S=特金,M=金錢信託

				assetList.add(asset);
			}
		}

		// 查無資料則拋出錯誤訊息
		if (CollectionUtils.isEmpty(assetList)) {
			logger.error("SOT707查無資料:" + prodID);
			throw new JBranchException("ehl_01_common_009");
		}

		sot707OutputVO.setCustAssetBondList(assetList);

		return sot707OutputVO;
	}

	/**
	 * 海外債長效單資料查詢 for js client
	 *
	 * 使用電文: NJBRVC1(DBU)/AJBRVC1(OBU)
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void getBondGTCData(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getBondGTCData(body));
	}

	/**
	 * 海外債長效單資料查詢
	 *
	 * 使用電文: NJBRVC1(DBU)/AJBRVC1(OBU)
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO getBondGTCData(Object body) throws Exception {
		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String custID = sot707InputVO.getCustId();
		String prodType = sot707InputVO.getProdType(); // B:申購 S:贖回
		String prodID = sot707InputVO.getProdId();
		Date startDate = sot707InputVO.getStartDate();
		Date endDate = sot707InputVO.getEndDate();
		String isOBU = sot707InputVO.getIsOBU();

		// init util
		ESBUtilInputVO esbUtilInputVO;
		if (StringUtils.equals("Y", isOBU)) {
			esbUtilInputVO = getTxInstance(ESB_TYPE, BOND_GTC_DATA_OBU);
		} else {
			esbUtilInputVO = getTxInstance(ESB_TYPE, BOND_GTC_DATA_DBU);
		}
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		if (StringUtils.equals("Y", isOBU)) {
			AJBRVC1InputVO txBodyVO = new AJBRVC1InputVO();
			esbUtilInputVO.setAjbrvc1InputVO(txBodyVO);
			txBodyVO.setCustId(custID);
			txBodyVO.setTxnType(prodType);
			txBodyVO.setBondNo(prodID);
			txBodyVO.setStartDate(this.toChineseYearMMdd(startDate, false));
			txBodyVO.setEndDate(this.toChineseYearMMdd(endDate, false));
		} else {
			NJBRVC1InputVO txBodyVO = new NJBRVC1InputVO();
			esbUtilInputVO.setNjbrvc1InputVO(txBodyVO);
			txBodyVO.setCustId(custID);
			txBodyVO.setTxnType(prodType);
			txBodyVO.setBondNo(prodID);
			txBodyVO.setStartDate(this.toChineseYearMMdd(startDate, false));
			txBodyVO.setEndDate(this.toChineseYearMMdd(endDate, false));
		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		// 回傳長效單資料
		List<BondGTCDataVO> dataList = new ArrayList<>();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVC1OutputVO outputVO;
			if (StringUtils.equals("Y", isOBU)) {
				outputVO = esbUtilOutputVO.getAjbrvc1OutputVO();
			} else {
				outputVO = esbUtilOutputVO.getNjbrvc1OutputVO();
			}
			List<NJBRVC1OutputVODetails> details = outputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NJBRVC1OutputVODetails>() : details;

			// 取得長效單明細資料
			for (NJBRVC1OutputVODetails detail : details) {
				BondGTCDataVO gtcData = new BondGTCDataVO();
				gtcData.setTxDate(new EsbUtil().toAdYearMMdd(detail.getTxDate(), false));
				gtcData.setCustId(detail.getCustId().trim());
				gtcData.setTxnType(detail.getTxnType());
				gtcData.setBondNo(detail.getBondNo());
				gtcData.setBondName(detail.getBondName());
				gtcData.setTxPrice(new EsbUtil().decimalPoint(detail.getTxPrice(), 9));
				gtcData.setTxEndDate(new EsbUtil().toAdYearMMdd(detail.getTxEndDate(), false));
				gtcData.setGtcNo(detail.getGtcNo());
				gtcData.setEntrustDate(new EsbUtil().toAdYearMMdd(detail.getEntrustDate(), false));
				gtcData.setTrustNo(detail.getTrustNo());
				gtcData.setEntrustStatus(detail.getEntrustStatus());
				gtcData.setEXPStartDt(new EsbUtil().toAdYearMMdd(detail.getEXPStartDt(), false));
				gtcData.setTxCurr1(detail.getTxCurr1());
				gtcData.setTxVal(new BigDecimal(detail.getTxVal()).toString());
				gtcData.setTxType2(detail.getTxType2());
				gtcData.setPriceType(detail.getPriceType());
				dataList.add(gtcData);
			}
		}

		// 查無資料則拋出錯誤訊息
		if (CollectionUtils.isEmpty(dataList)) {
			logger.error("SOT707 getBondGTCData 查無資料: CUST_ID = " + custID);
			throw new JBranchException("ehl_01_common_009");
		}

		sot707OutputVO.setBondGTCDataList(dataList);

		return sot707OutputVO;
	}

	/**
	 * 海外債長效單明細資料查詢 for js client
	 *
	 * 使用電文: NJBRV2(DBU)/AJBRVC2(OBU)
	 *
	 * @param body
	 * @param header
	 * @return
	 * @throws Exception
	 */
	public void getBondGTCDataDetail(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getBondGTCDataDetail(body));
	}

	/**
	 * 海外債長效單明細資料查詢
	 *
	 * 使用電文: NJBRVC2(DBU)/AJBRVC2(OBU)
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT707OutputVO getBondGTCDataDetail(Object body) throws Exception {
		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String gtcNo = sot707InputVO.getGtcNo().trim();
		String isOBU = sot707InputVO.getIsOBU();

		// init util
		ESBUtilInputVO esbUtilInputVO;
		if (StringUtils.equals("Y", isOBU)) {
			esbUtilInputVO = getTxInstance(ESB_TYPE, BOND_GTC_DATA_DETAIL_OBU);
		} else {
			esbUtilInputVO = getTxInstance(ESB_TYPE, BOND_GTC_DATA_DETAIL_DBU);
		}
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		if (StringUtils.equals("Y", isOBU)) {
			AJBRVC2InputVO txBodyVO = new AJBRVC2InputVO();
			esbUtilInputVO.setAjbrvc2InputVO(txBodyVO);
			txBodyVO.setGtcNo(gtcNo);
		} else {
			NJBRVC2InputVO txBodyVO = new NJBRVC2InputVO();
			esbUtilInputVO.setNjbrvc2InputVO(txBodyVO);
			txBodyVO.setGtcNo(gtcNo);
		}

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		// 回傳長效單資料
		List<BondGTCDataDetailVO> dataList = new ArrayList<>();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVC2OutputVO outputVO;
			if (StringUtils.equals("Y", isOBU)) {
				outputVO = esbUtilOutputVO.getAjbrvc2OutputVO();
			} else {
				outputVO = esbUtilOutputVO.getNjbrvc2OutputVO();
			}

			List<NJBRVC2OutputVODetails> details = outputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NJBRVC2OutputVODetails>() : details;

			// 取得長效單明細資料
			for (NJBRVC2OutputVODetails detail : details) {
				BondGTCDataDetailVO gtcDetailData = new BondGTCDataDetailVO();
				gtcDetailData.setBatchEffDate(new EsbUtil().toAdYearMMdd(detail.getBatchEffDate(), false));
				gtcDetailData.setBatchEffTime(detail.getBatchEffTime());
				gtcDetailData.setEntrustStatus(detail.getEntrustStatus());

				dataList.add(gtcDetailData);
			}
		}

		// 查無資料則拋出錯誤訊息
		if (CollectionUtils.isEmpty(dataList)) {
			logger.error("SOT707 getBondGTCDataDetail 查無資料: GtcNo = " + gtcNo);
			throw new JBranchException("ehl_01_common_009");
		}

		sot707OutputVO.setBondGTCDataDetailList(dataList);

		return sot707OutputVO;
	}
	
	/**
	 * 網路快速下單 – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息 發送電文EBPMN 需傳入參數：tradeSeq
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void sendESBWebPurchaseBN(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(sendESBWebPurchaseBN(body));
	}

	/**
	 * 網路快速下單 – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息 發送電文EBPMN 需傳入參數：tradeSeq
	 *
	 * @param body
	 * @throws Exception
	 */
	public SOT707OutputVO sendESBWebPurchaseBN(Object body) throws Exception {
		sot707InputVO = (SOT707InputVO) body;
		sot707OutputVO = new SOT707OutputVO();

		String tradeSeq = sot707InputVO.getTradeSeq(); // 下單交易序號

		if (StringUtils.isBlank(tradeSeq)) {
			throw new JBranchException("下單交易序號未輸入");
		}

		StringBuffer sql = new StringBuffer("select distinct M.CUST_ID, D.BATCH_SEQ ");
		sql.append(" from TBSOT_TRADE_MAIN M ");
		sql.append(" inner join TBSOT_BN_TRADE_D D on D.TRADE_SEQ = M.TRADE_SEQ ");
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
			txBodyVO.setNotifyType("04"); // 通知類型
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
			txBodyVO.setNotifyType("04"); // 通知類型
			txBodyVO.setCustId(detail.get("CUST_ID").toString()); // 客戶ID
			txBodyVO.setApplySeq(detail.get("BATCH_SEQ").toString()); // 申購批號
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				EBPMN2OutputVO ebpmn2OutputVO = esbUtilOutputVO.getEbpmn2OutputVO();
				// 回傳錯誤訊息
				Boolean isErr = checkError(ebpmn2OutputVO);
			}
		}
		return sot707OutputVO;
	}
	
}
