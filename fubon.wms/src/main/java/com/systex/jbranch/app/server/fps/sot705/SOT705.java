package com.systex.jbranch.app.server.fps.sot705;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.CMKTYPE_MK03;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ASSETS;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ASSETS_MON;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_DUE_DATE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ESB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ESB_M;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NR074N_DATA;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NR080N_DATA;

import java.math.BigDecimal;
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

import com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DPK;
import com.systex.jbranch.app.common.fps.table.TBSOT_ETF_TRADE_DVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfvipa.NFVIPAOutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp9a.NMVP9AInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp9a.NMVP9AOutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nmvp9a.NMVP9AOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr074n.NR074NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr074n.NR074NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr074n.NR074NOutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr080n.NR080NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr080n.NR080NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr080n.NR080NOutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr096n.NR096NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr096n.NR096NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr096n.NR096NOutputVODetails;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva9.NRBRVA9InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva9.NRBRVA9OutputVO;
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
 * Created by SebastianWu on 2016/9/21.
 * 
 * revised by Cathy Tang on 2016/10/24 上行電文日期與數字格式修正 日期格式為西元年月日：yyyyMMdd
 * 數字格式呼叫ESBUtil.decimalPadding：去除小數點,並用0向右補足小數位長度
 */
@Component("sot705")
@Scope("request")
public class SOT705 extends EsbUtil {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;
	private SOT705InputVO sot705InputVO;
	private SOT705OutputVO sot705OutputVO;
	private String thisClaz = this.getClass().getSimpleName() + ".";

	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    
	/**
	 * 根據電文回傳錯誤訊息，放入SOT705OutputVO中 若最上層有錯誤訊息，則回傳最上層；否則回傳細項第一個錯誤訊息
	 * 
	 * @param obj
	 * @return
	 * @throws JBranchException
	 */
	private Boolean checkError(Object obj) throws JBranchException {
		String errCode = null;
		String errTxt = null;
		String txFlag = "Y";
		Boolean isErr = Boolean.FALSE;

		if (obj instanceof NRBRVA9OutputVO) { // 申購、贖回
			NRBRVA9OutputVO vo = (NRBRVA9OutputVO) obj;

			errCode = StringUtils.isNotBlank(vo.getERR_COD()) ? vo.getERR_COD() : StringUtils.isNotBlank(vo.getERR_CODE_1()) ? vo.getERR_CODE_1() : StringUtils.isNotBlank(vo.getERR_CODE_2()) ? vo.getERR_CODE_2() : null;

			errTxt = StringUtils.isNotBlank(vo.getERR_TXT()) ? vo.getERR_TXT() : StringUtils.isNotBlank(vo.getERR_MSG_1()) ? vo.getERR_MSG_1() : StringUtils.isNotBlank(vo.getERR_MSG_2()) ? vo.getERR_MSG_2() : null;

			txFlag = vo.getTX_FLG();
		} else if (obj instanceof NFVIPAOutputVO) { // 庫存

		} else if (obj instanceof NR080NOutputVO) {
			NR080NOutputVO vo = (NR080NOutputVO) obj;
			errCode = StringUtils.isNotBlank(vo.getErrorCode()) ? vo.getErrorCode() : null;
			errTxt = StringUtils.isNotBlank(vo.getErrorMsg()) ? vo.getErrorMsg() : null;
		}

		if (StringUtils.isNotBlank(errCode)) {
			sot705OutputVO.setErrorCode(errCode);
			sot705OutputVO.setErrorMsg(errTxt);
			isErr = Boolean.TRUE;
		} else if (!StringUtils.equals("Y", txFlag)) {
			// 若不可承作交易
			sot705OutputVO.setErrorCode("ehl_01_sot705_001");
			sot705OutputVO.setErrorMsg("此客戶不可承作此交易");
			isErr = Boolean.TRUE;
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
			if (StringUtils.isNotBlank(sot705OutputVO.getErrorCode())) {
				throw new JBranchException(sot705OutputVO.getErrorCode() + ": " + sot705OutputVO.getErrorMsg());
			}
		}
	}

	/**
	 * 回專業投資人警語
	 *
	 * @param nrbrva9OutputVO
	 * @return
	 */
	private String getWarmingMsg(NRBRVA9OutputVO nrbrva9OutputVO) {
		String msg = null;

		// if (StringUtils.isNotBlank(nrbrva9OutputVO.getPROCUST_1())) {
		// msg = nrbrva9OutputVO.getSTOCK_NAME_1() + ":"
		// + nrbrva9OutputVO.getPROCUST_1() + "。";
		// }
		// modify by Brian
		if ("Y".equals(nrbrva9OutputVO.getPROCUST_1())) {
			msg = nrbrva9OutputVO.getSTOCK_NAME_1() + ":請舉證於本行ＡＵＭ大於等值ＴＷＤ１,５００ 萬 。";
		}

		if (StringUtils.isNotBlank(nrbrva9OutputVO.getPROCUST_2())) {
			msg += nrbrva9OutputVO.getSTOCK_NAME_2() + ":" + nrbrva9OutputVO.getPROCUST_2();
		}

		return msg;
	}

	/**
	 * 下行電文資料寫入資料庫
	 *
	 * @param seqNo
	 * @param tradeSeq
	 * @param nrbrva9OutputVO
	 * @param batch
	 * @param defaultFeeRate
	 * @throws DAOException
	 */
	private void updateTBSOT_ETF_TRADE_D(String seqNo, String tradeSeq, NRBRVA9OutputVO nrbrva9OutputVO, Integer batch, BigDecimal defaultFeeRate) throws JBranchException {
		dam = getDataAccessManager();

		TBSOT_ETF_TRADE_DPK pk = new TBSOT_ETF_TRADE_DPK();
		pk.setSEQ_NO(new BigDecimal(seqNo));
		pk.setTRADE_SEQ(tradeSeq);
		TBSOT_ETF_TRADE_DVO vo = new TBSOT_ETF_TRADE_DVO(pk);

		// find data by pks
		vo = (TBSOT_ETF_TRADE_DVO) dam.findByPKey(TBSOT_ETF_TRADE_DVO.TABLE_UID, pk);

		BigDecimal entrustAmt;
		BigDecimal feeRate;
		BigDecimal fee;
		BigDecimal feeDiscount;
		BigDecimal taxFee;
		BigDecimal mtnFee;
		BigDecimal otherFee;
		BigDecimal totAmt;
		String groupOfa = "";
		if (batch != null && 2 == batch) {
			if ("1".equals(nrbrva9OutputVO.getTYPE_2().trim())) {
				entrustAmt = new BigDecimal("0");
			} else {
				entrustAmt = new EsbUtil().decimalPoint(nrbrva9OutputVO.getPRICE_2(), 6);
			}
			feeRate = new EsbUtil().decimalPoint(nrbrva9OutputVO.getFEE_RATE_2(), 5);
			fee = new EsbUtil().decimalPoint(nrbrva9OutputVO.getFEE_2(), 2);
			feeDiscount = (defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
			groupOfa = nrbrva9OutputVO.getGROUP_OFA_2();
			taxFee = new EsbUtil().decimalPoint(nrbrva9OutputVO.getTAXFEE_2(), 2);
			mtnFee = new EsbUtil().decimalPoint(nrbrva9OutputVO.getMTNFEE_2(), 2);
			otherFee = new EsbUtil().decimalPoint(nrbrva9OutputVO.getOTHERFEE_2(), 2);
			totAmt = new EsbUtil().decimalPoint(nrbrva9OutputVO.getAMT_2(), 2);
		} else {
			if ("1".equals(nrbrva9OutputVO.getTYPE_1().trim())) {
				entrustAmt = new BigDecimal("0");
			} else {
				entrustAmt = new EsbUtil().decimalPoint(nrbrva9OutputVO.getPRICE_1(), 6);
			}
			feeRate = new EsbUtil().decimalPoint(nrbrva9OutputVO.getFEE_RATE_1(), 5);
			fee = new EsbUtil().decimalPoint(nrbrva9OutputVO.getFEE_1(), 2);
			feeDiscount = (defaultFeeRate != null && defaultFeeRate.compareTo(BigDecimal.ZERO) != 0) ? feeRate.divide(defaultFeeRate, 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN) : null;
			groupOfa = nrbrva9OutputVO.getGROUP_OFA_1();
			taxFee = new EsbUtil().decimalPoint(nrbrva9OutputVO.getTAXFEE_1(), 2);
			mtnFee = new EsbUtil().decimalPoint(nrbrva9OutputVO.getMTNFEE_1(), 2);
			otherFee = new EsbUtil().decimalPoint(nrbrva9OutputVO.getOTHERFEE_1(), 2);
			totAmt = new EsbUtil().decimalPoint(nrbrva9OutputVO.getAMT_1(), 2);
		}
		vo.setENTRUST_AMT(entrustAmt);
		vo.setFEE_RATE(feeRate);
		vo.setFEE(fee);
		vo.setFEE_DISCOUNT(feeDiscount);
		vo.setGROUP_OFA(groupOfa);
		vo.setTAX_FEE(taxFee);
		vo.setMTN_FEE(mtnFee);
		vo.setOTHER_FEE(otherFee);
		vo.setTOT_AMT(totAmt);
		dam.update(vo);
	}

	/**
	 * 查詢主檔資料
	 *
	 * @param seqNo
	 * @param tradeSeq
	 * @return
	 * @throws JBranchException
	 */
	private List<Map> mainInfo(String seqNo, String tradeSeq) throws JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("select M.CUST_ID, M.IS_OBU, M.BRANCH_NBR, NVL(M.TRUST_TRADE_TYPE, 'S') AS TRUST_TRADE_TYPE, D.*, NVL(ETF.STOCK_CODE, STK.STOCK_CODE) STOCK_CODE, M.FLAG_NUMBER ");
		sql.append(" from TBSOT_TRADE_MAIN M ");
		sql.append(" inner join TBSOT_ETF_TRADE_D D on D.TRADE_SEQ = M.TRADE_SEQ ");
		sql.append(" left join TBPRD_ETF ETF on ETF.PRD_ID = D.PROD_ID ");
		sql.append(" left join TBPRD_STOCK STK on STK.PRD_ID = D.PROD_ID ");
		sql.append(" where M.TRADE_SEQ = :TRADESEQ and D.SEQ_NO = :SEQNO ");

		condition.setObject("TRADESEQ", tradeSeq);
		condition.setObject("SEQNO", seqNo);
		condition.setQueryString(sql.toString());

		return dam.exeQuery(condition);
	}

	/**
	 * 依照批號取得明細資料
	 *
	 * @return
	 * @throws JBranchException
	 */
	public List<Map> batchInfo(String tradeSeq, String batchSeq) throws JBranchException {
		dam = getDataAccessManager();

		String sql = new StringBuffer(" select D.*,  " + "       NVL(ETF.STOCK_CODE, STK.STOCK_CODE) STOCK_CODE " + "   from TBSOT_ETF_TRADE_D D " + "   left join TBPRD_ETF ETF on ETF.PRD_ID = D.PROD_ID " + "   left join TBPRD_STOCK STK on STK.PRD_ID = D.PROD_ID " + "  where D.TRADE_SEQ = :TRADESEQ  " + "    and D.BATCH_SEQ = :BATCH_SEQ " + "  order by D.BATCH_NO ").toString();

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADESEQ", tradeSeq);
		condition.setObject("BATCH_SEQ", batchSeq);
		condition.setQueryString(sql);

		return dam.exeQuery(condition);
	}

	/**
	 * 海外ETF/股票申購檢核 for js client
	 *
	 * 使用電文: NRBRVA9
	 *
	 * @param body
	 * @param header
	 */
	public void verifyESBPurchaseETF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBPurchaseETF(body));
	}

	/**
	 * 海外ETF/股票申購檢核
	 *
	 * 使用電文: NRBRVA9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT705OutputVO verifyESBPurchaseETF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot705InputVO = (SOT705InputVO) body;
		sot705OutputVO = new SOT705OutputVO();
		String seqNo = sot705InputVO.getSeqNo(); // 平台序號
		String tradeSeq = sot705InputVO.getTradeSeq(); // 交易序號
		String checkType = sot705InputVO.getCheckType(); // 電文確認碼
		String trustTS = sot705InputVO.getTrustTS();	//S=特金/M=金錢信託

		// 欄位檢核
		if (StringUtils.isBlank(seqNo) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("平台序號、交易序號或電文確認碼未輸入");
		}

		// 由交易序號取得主檔資料作為電文上行
		List<Map> results = mainInfo(seqNo, tradeSeq);

		// 上行欄位
		String custID = null; // 客戶ID
		String branch = null; // 分行別
		String kind = null; // 信託業務別
		String buysell = null; // 買賣別
		String txDate = null; // 委託日期
		String startDate1 = null; // 交易市場日期
		String endDate1 = null; // 交易市場日期長效單迄日
		String acctNo1 = null; // 信託帳號
		String dbuObu = null; // DBU/OBU
		String acttNo3 = null; // 扣款/入帳帳號
		String confirm = null; // 確認碼
		String batchNo = null; // 批號
		String empID = null; // 建機人員
		String refereeID = null; // 推薦人
		String type = null; // 交易類型
		String feeRate1 = null; // 手續費率_1
		String exgCod1 = null; // 交易所代號_1
		String stockCod1 = null; // 商品代號_1
		String amount1 = null; // 下單數量_1
		String type1 = null; // 委託方式_1
		String price1 = null; // 委託價格_1
		String earnAcct = null; // 收益帳號
		String isPL1 = null; // 是否停損停利_1
		String takeProfitPerc1 = null; // 停利點_1
		String takeProfitSym1 = null; // 停利
		String stopLossPerc1 = null; // 停損點_1
		String stopLossSym1 = null; // 停損符號_1
		String bargainApplySeq1 = null; // 議價編號_1
		BigDecimal defaultFeeRate = null; // 表定手續費率
		String feeType = null; // 手續費優惠方式
		String loanCode = null; // 90天內的貸款紀錄

		BigDecimal percentage = null; // 百分比
		String systemDate = getSystemDate();
		if (CollectionUtils.isNotEmpty(results)) {
			custID = (String) results.get(0).get("CUST_ID");
			
			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNbr = (String) results.get(0).get("BRANCH_NBR");
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
			branch = branchNbr; // TBSOT_TRADE_MAIN.BRANCH_NBR
			
			kind = String.valueOf(results.get(0).get("TRUST_CURR_TYPE"));
			buysell = String.valueOf(results.get(0).get("TRADE_SUB_TYPE"));
			txDate = systemDate;
			startDate1 = systemDate;
			endDate1 = sdfYYYYMMDD.format((Date) results.get(0).get("DUE_DATE"));
			acctNo1 = (String) results.get(0).get("TRUST_ACCT");
			dbuObu = cbsservice.checkOBU(acctNo1.trim());  //OBU檢核改用帳號
			acttNo3 = (String) results.get(0).get("DEBIT_ACCT");
			acttNo3 = cbsservice.checkAcctLength(acttNo3);
			confirm = (StringUtils.equals("1", checkType)) ? "1" : "";
			batchNo = (String) results.get(0).get("BATCH_SEQ");
			empID = (String) results.get(0).get("MODIFIER");
			refereeID = (String) results.get(0).get("NARRATOR_ID");
			feeRate1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) results.get(0).get("FEE_RATE"), 5));
			exgCod1 = (String) results.get(0).get("STOCK_CODE");
			stockCod1 = (String) results.get(0).get("PROD_ID");
			amount1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) results.get(0).get("UNIT_NUM"), 0));
			type1 = String.valueOf(results.get(0).get("ENTRUST_TYPE"));
			price1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) results.get(0).get("ENTRUST_AMT"), 6));
			earnAcct = (String) results.get(0).get("CREDIT_ACCT");
			isPL1 = (results.get(0).get("TAKE_PROFIT_PERC") != null || results.get(0).get("STOP_LOSS_PERC") != null) ? "Y" : "N";
			percentage = (BigDecimal) results.get(0).get("TAKE_PROFIT_PERC");
			takeProfitSym1 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
			percentage = (BigDecimal) results.get(0).get("STOP_LOSS_PERC");
			stopLossSym1 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
			takeProfitPerc1 = results.get(0).get("TAKE_PROFIT_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) results.get(0).get("TAKE_PROFIT_PERC")).abs(), 2));
			stopLossPerc1 = results.get(0).get("STOP_LOSS_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) results.get(0).get("STOP_LOSS_PERC")).abs(), 2));
			feeType = String.valueOf(results.get(0).get("FEE_TYPE"));
			loanCode = results.get(0).get("FLAG_NUMBER") == null ? null : (String) results.get(0).get("FLAG_NUMBER");
			if (!StringUtils.equals("A", feeType)) {
				// 申請議價時，檢核電文不須送議價編號，因為在主機中此議價編號還沒有鍵機；要等到下一步時才會鍵機
				bargainApplySeq1 = (String) results.get(0).get("BARGAIN_APPLY_SEQ");
			}
			defaultFeeRate = (BigDecimal) results.get(0).get("DEFAULT_FEE_RATE");
		}

		// init util
		//特金：NRBRVA9  金錢信託：NRBRVT9
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, StringUtils.equals("M", trustTS) ? ETF_ESB_M : ETF_ESB);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "." + new Object(){}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NRBRVA9InputVO txBodyVO = new NRBRVA9InputVO();
		esbUtilInputVO.setNrbrva9InputVO(txBodyVO);
		txBodyVO.setCUST_ID(custID);
		txBodyVO.setBRANCH(branch);
		txBodyVO.setKIND(kind);
		txBodyVO.setBUYSELL(buysell);
		txBodyVO.setDAYTYPE("0");
		txBodyVO.setTX_DATE(txDate);
		txBodyVO.setSTART_DATE_1(startDate1);
		txBodyVO.setEND_DATE_1(endDate1);
		//金錢信託檢核電文不傳信託帳號
		if(!StringUtils.equals("M", (String) results.get(0).get("TRUST_TRADE_TYPE"))) {
			txBodyVO.setACCT_NO1(acctNo1);
			
			//#1695 貸轉投
			txBodyVO.setLoanCode(loanCode);		
		}
		txBodyVO.setDBUOBU(dbuObu);
		txBodyVO.setACCT_ID(custID);
		txBodyVO.setACCT_NO3(acttNo3);
		txBodyVO.setPAY_TYPE("3");
		txBodyVO.setCONFIRM(confirm);
		txBodyVO.setBATCH_NO(batchNo);
		txBodyVO.setEMP_ID(empID);
		txBodyVO.setREFEREE_ID(refereeID);
		txBodyVO.setTYPE("1");
		txBodyVO.setFEE_RATE_1(feeRate1);
		txBodyVO.setEXG_COD_1(exgCod1);
		txBodyVO.setSTOCK_COD_1(stockCod1);
		txBodyVO.setAMOUNT_1(amount1);
		txBodyVO.setTYPE_1(getEntrustTypeUp(type1));// 轉成電文要編號
		txBodyVO.setPRICE_1(price1);
		txBodyVO.setEarnAcct(earnAcct);
		txBodyVO.setIS_PL_1(isPL1);
		txBodyVO.setTAKE_PROFIT_PERC_1(takeProfitPerc1);
		txBodyVO.setTAKE_PROFIT_SYM_1(takeProfitSym1);
		txBodyVO.setSTOP_LOSS_PERC_1(stopLossPerc1);
		txBodyVO.setSTOP_LOSS_SYM_1(stopLossSym1);
		txBodyVO.setBARGAIN_APPLY_SEQ_1(bargainApplySeq1);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		NRBRVA9OutputVO nrbrva9OutputVO = new NRBRVA9OutputVO();
		
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			if(StringUtils.equals("M", trustTS)) {
				nrbrva9OutputVO = esbUtilOutputVO.getNrbrvt9OutputVO(); //金錢信託
			} else {
				nrbrva9OutputVO = esbUtilOutputVO.getNrbrva9OutputVO(); //特金
			}

			// 若沒有錯誤，回傳下行電文資料儲存DB
			if (!checkError(nrbrva9OutputVO)) {
				updateTBSOT_ETF_TRADE_D(seqNo, tradeSeq, nrbrva9OutputVO, null, defaultFeeRate);
			}

			// 若有回專業投資人警語，要傳回前端顯示訊息
			String msg = getWarmingMsg(nrbrva9OutputVO);
			sot705OutputVO.setWarningMsg(msg);
			sot705OutputVO.setEarnAcct(nrbrva9OutputVO.getEarnAcct());
		}

		return sot705OutputVO;
	}

	/**
	 * 海外ETF/股票申購確認 for js client
	 *
	 * 使用電文: NRBRVA9
	 *
	 * @param body
	 * @throws Exception
	 */
	public void confirmESBPurchaseETF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.confirmESBPurchaseETF(body));
	}

	/**
	 * 海外ETF/股票申購確認
	 *
	 * 使用電文: NRBRVA9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT705OutputVO confirmESBPurchaseETF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		Map<String, String> trustTSMap = xmlInfo.doGetVariable("SOT.TRUST_TS", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot705InputVO = (SOT705InputVO) body;
		sot705OutputVO = new SOT705OutputVO();
		String tradeSeq = sot705InputVO.getTradeSeq(); // 交易序號
		String checkType = sot705InputVO.getCheckType(); // 電文確認碼
		String trustTS = sot705InputVO.getTrustTS();	//S=特金/M=金錢信託
		String seqNo = null; // 平台序號
		String msg = null; // 專業投資人警語

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("交易序號或電文確認碼未輸入");
		}
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// 由交易序號取得主檔資料作為電文上行
		String sql = new StringBuffer("select * " + "  from TBSOT_TRADE_MAIN " + " where TRADE_SEQ = :TRADESEQ ").toString();

		condition.setObject("TRADESEQ", tradeSeq);
		condition.setQueryString(sql);
		List<Map> mains = dam.exeQuery(condition);

		// 由交易序號取得批號資料作為電文上行
		sql = new StringBuffer("select BATCH_SEQ, " + "       count(1) as BATCH_COUNT " + "  from TBSOT_ETF_TRADE_D " + " where TRADE_SEQ = :TRADESEQ " + " group by BATCH_SEQ ").toString();
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADESEQ", tradeSeq);
		condition.setQueryString(sql);
		List<Map> details = dam.exeQuery(condition);

		// 以批號分批發送電文
		for (int idx = 0; idx <= details.size() - 1; idx++) {
			// 依照批號取得明細資料
			List<Map> batchs = batchInfo(tradeSeq, (String) details.get(idx).get("BATCH_SEQ"));

			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, StringUtils.equals("M", trustTS) ? ETF_ESB_M : ETF_ESB);
			esbUtilInputVO.setModule(thisClaz + new Object() {}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			esbUtilInputVO.setTxHeadVO(txHead);
			txHead.setDefaultTxHead();

			// 上行欄位
			// 每一批號最多只會有兩筆資料,取得第一筆與第二筆資料(若有第二筆)
			String custID = null; // 客戶ID
			String branch = null; // 分行別
			String kind = null; // 信託業務別
			String buysell = null; // 買賣別
			String dayType = null; // 委託期間
			String txDate = null; // 委託日期
			String startDate1 = null; // 交易市場日期
			String endDate1 = null; // 交易市場日期長效單迄日
			String acctNo1 = null; // 信託帳號
			String dbuobu = null; // DBU/OBU
			String acctID = null; // 扣款ID
			String acctNo3 = null; // 扣款/入帳帳號
			String payType = null; // 繳款方式
			String confirm = null; // 確認碼
			String batchNo = null; // 批號
			String empID = null; // 建機人員
			String refereeID = null; // 推薦人
			String type = null; // 交易類型
			String feeRate1 = null; // 手續費率_1
			BigDecimal defaultFeeRate1 = null; // 表定手續費_1
			String exgCod1 = null; // 交易所代號_1
			String stockCod1 = null; // 商品代號_1
			String groupIfa1 = null; // 優惠團體代碼_1
			String amount1 = null; // 下單數量_1
			String type1 = null; // 委託方式_1
			String price1 = null; // 委託價格_1
			String feeRate2 = null; // 手續費率_2
			BigDecimal defaultFeeRate2 = null; // 表定手續費_2
			String exgCod2 = null; // 交易所代號_2
			String stockCod2 = null; // 商品代號_2
			String groupIfa2 = null; // 優惠團體代碼_2
			String amount2 = null; // 下單數量_2
			String type2 = null; // 委託方式_2
			String price2 = null; // 委託價格_2
			String earnAcct = null; // 收益帳號
			String recSeq = null; // 錄音序號
			String isPl1 = null; // 是否停損停利_1
			String takeProfitPerc1 = null; // 停利點_1
			String takeProfitSym1 = null; // 停利符號_1
			String stopLossPerc1 = null; // 停損點_1
			String stopLossSym1 = null; // 停損符號_1
			String bargainApplySeq1 = null; // 議價編號_1
			String takeProfitPerc2 = null; // 是否停損停利_2
			String takeProfitSym2 = null; // 停利點_2
			String stopLossPerc2 = null; // 停利符號_2
			String stopLossSym2 = null; // 停損點_2
			String isPl2 = null; // 停損符號_2
			String bargainApplySeq2 = null; // 議價編號_2
			String loanCode = null; // 90天內的貸款紀錄

			BigDecimal percentage = null; // 百分比
			String systemDate = getSystemDate();
			if (CollectionUtils.isNotEmpty(batchs)) {
				custID = (CollectionUtils.isEmpty(mains)) ? null : (String) mains.get(0).get("CUST_ID");
				
				// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
				// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
				String branchNbr = null;
				if (CollectionUtils.isEmpty(mains)) {
					branchNbr = null;
				} else {
					branchNbr = (String) mains.get(0).get("BRANCH_NBR");
					
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
				}
				branch = branchNbr;
				
				kind = String.valueOf(batchs.get(0).get("TRUST_CURR_TYPE"));
				buysell = String.valueOf(batchs.get(0).get("TRADE_SUB_TYPE"));
				dayType = "0";
				txDate = systemDate;
				startDate1 = systemDate;
				endDate1 = sdfYYYYMMDD.format((Date) batchs.get(0).get("DUE_DATE"));
				acctNo1 = (String) batchs.get(0).get("TRUST_ACCT");
				dbuobu = cbsservice.checkOBU(acctNo1.trim()); //OBU檢核改用帳號
				acctID = (CollectionUtils.isEmpty(mains)) ? null : (String) mains.get(0).get("CUST_ID");
				acctNo3 = (String) batchs.get(0).get("DEBIT_ACCT");
				acctNo3 = cbsservice.checkAcctLength(acctNo3);
				payType = "3";
				confirm = (StringUtils.equals("1", checkType)) ? "1" : "";
				batchNo = (String) batchs.get(0).get("BATCH_SEQ");
				empID = (String) batchs.get(0).get("MODIFIER");
				refereeID = (String) batchs.get(0).get("NARRATOR_ID");
				feeRate1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(0).get("FEE_RATE"), 5));
				defaultFeeRate1 = (BigDecimal) batchs.get(0).get("DEFAULT_FEE_RATE");
				exgCod1 = (String) batchs.get(0).get("STOCK_CODE");
				stockCod1 = (String) batchs.get(0).get("PROD_ID");
				groupIfa1 = (String) batchs.get(0).get("GROUP_OFA");
				amount1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(0).get("UNIT_NUM"), 0));
				type1 = String.valueOf(batchs.get(0).get("ENTRUST_TYPE"));
				price1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(0).get("ENTRUST_AMT"), 6));
				earnAcct = (String) batchs.get(0).get("CREDIT_ACCT");
				recSeq = (CollectionUtils.isEmpty(mains)) ? null : (String) mains.get(0).get("REC_SEQ");
				isPl1 = (batchs.get(0).get("TAKE_PROFIT_PERC") != null || batchs.get(0).get("STOP_LOSS_PERC") != null) ? "Y" : "N";
				percentage = (BigDecimal) batchs.get(0).get("TAKE_PROFIT_PERC");
				takeProfitSym1 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
				percentage = (BigDecimal) batchs.get(0).get("STOP_LOSS_PERC");
				stopLossSym1 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
				bargainApplySeq1 = (String) batchs.get(0).get("BARGAIN_APPLY_SEQ");
				takeProfitPerc1 = batchs.get(0).get("TAKE_PROFIT_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) batchs.get(0).get("TAKE_PROFIT_PERC")).abs(), 2));
				stopLossPerc1 = batchs.get(0).get("STOP_LOSS_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) batchs.get(0).get("STOP_LOSS_PERC")).abs(), 2));
				loanCode = (CollectionUtils.isEmpty(mains)) ?  null : (String) mains.get(0).get("FLAG_NUMBER") == null ? null : (String) mains.get(0).get("FLAG_NUMBER");
				if (batchs.size() > 1) {
					feeRate2 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(1).get("FEE_RATE"), 5));
					defaultFeeRate2 = (BigDecimal) batchs.get(1).get("DEFAULT_FEE_RATE");
					exgCod2 = (String) batchs.get(1).get("STOCK_CODE");
					stockCod2 = (String) batchs.get(1).get("PROD_ID");
					groupIfa2 = (String) batchs.get(1).get("GROUP_OFA");
					amount2 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(1).get("UNIT_NUM"), 0));
					type2 = String.valueOf(batchs.get(1).get("ENTRUST_TYPE"));
					price2 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(1).get("ENTRUST_AMT"), 6));
					percentage = (BigDecimal) batchs.get(1).get("TAKE_PROFIT_PERC");
					takeProfitSym2 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
					percentage = (BigDecimal) batchs.get(1).get("STOP_LOSS_PERC");
					stopLossSym2 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
					isPl2 = (batchs.get(1).get("TAKE_PROFIT_PERC") != null || batchs.get(1).get("STOP_LOSS_PERC") != null) ? "Y" : "N";
					bargainApplySeq2 = (String) batchs.get(1).get("BARGAIN_APPLY_SEQ");
					takeProfitPerc2 = batchs.get(1).get("TAKE_PROFIT_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) batchs.get(1).get("TAKE_PROFIT_PERC")).abs(), 2));
					stopLossPerc2 = batchs.get(1).get("STOP_LOSS_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) batchs.get(1).get("STOP_LOSS_PERC")).abs(), 2));
				}
			}

			// body
			NRBRVA9InputVO txBodyVO = new NRBRVA9InputVO();
			esbUtilInputVO.setNrbrva9InputVO(txBodyVO);
			txBodyVO.setCUST_ID(!(StringUtils.equals("M", (String) mains.get(0).get("TRUST_TRADE_TYPE"))) ? custID : trustTSMap.get("M_CUSTNO"));
			txBodyVO.setBRANCH(branch);
			txBodyVO.setKIND(kind);
			txBodyVO.setBUYSELL(buysell);
			txBodyVO.setDAYTYPE(dayType);
			txBodyVO.setTX_DATE(txDate);
			txBodyVO.setSTART_DATE_1(startDate1);
			txBodyVO.setEND_DATE_1(endDate1);
			txBodyVO.setACCT_NO1(acctNo1);
			txBodyVO.setDBUOBU(dbuobu);
			txBodyVO.setACCT_ID(!(StringUtils.equals("M", (String) mains.get(0).get("TRUST_TRADE_TYPE"))) ? acctID : trustTSMap.get("M_CUSTNO"));
			txBodyVO.setACCT_NO3(acctNo3);
			txBodyVO.setPAY_TYPE(payType);
			txBodyVO.setCONFIRM(confirm);
			txBodyVO.setBATCH_NO(batchNo);
			txBodyVO.setEMP_ID(empID);
			txBodyVO.setREFEREE_ID(refereeID);
			txBodyVO.setTYPE("1");
			txBodyVO.setFEE_RATE_1(feeRate1);
			txBodyVO.setEXG_COD_1(exgCod1);
			txBodyVO.setSTOCK_COD_1(stockCod1);
			txBodyVO.setGROUP_IFA_1(groupIfa1);
			txBodyVO.setAMOUNT_1(amount1);
			txBodyVO.setTYPE_1(getEntrustTypeUp(type1));// 轉成電文要編號
			txBodyVO.setPRICE_1(price1);
			txBodyVO.setFEE_RATE_2(feeRate2);
			txBodyVO.setEXG_COD_2(exgCod2);
			txBodyVO.setSTOCK_COD_2(stockCod2);
			txBodyVO.setGROUP_IFA_2(groupIfa2);
			txBodyVO.setAMOUNT_2(amount2);
			txBodyVO.setTYPE_2(getEntrustTypeUp(type2));// 轉成電文要編號
			txBodyVO.setPRICE_2(price2);
			txBodyVO.setEarnAcct(earnAcct);
			
			//金錢信託中申購ETF錄音序號不必上送給AS400
			if(!(StringUtils.equals("M", (String) mains.get(0).get("TRUST_TRADE_TYPE")))) {
				txBodyVO.setREC_SEQ(recSeq);
				
				//#1695 貸轉投
				txBodyVO.setLoanCode(loanCode);
			}
			txBodyVO.setIS_PL_1(isPl1);
			txBodyVO.setTAKE_PROFIT_PERC_1(takeProfitPerc1);
			txBodyVO.setTAKE_PROFIT_SYM_1(takeProfitSym1);
			txBodyVO.setSTOP_LOSS_PERC_1(stopLossPerc1);
			txBodyVO.setSTOP_LOSS_SYM_1(stopLossSym1);
			txBodyVO.setBARGAIN_APPLY_SEQ_1(bargainApplySeq1);
			txBodyVO.setIS_PL_2(isPl2);
			txBodyVO.setTAKE_PROFIT_PERC_2(takeProfitPerc2);
			txBodyVO.setTAKE_PROFIT_SYM_2(takeProfitSym2);
			txBodyVO.setSTOP_LOSS_PERC_2(stopLossPerc2);
			txBodyVO.setSTOP_LOSS_SYM_2(stopLossSym2);
			txBodyVO.setBARGAIN_APPLY_SEQ_2(bargainApplySeq2);

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
			NRBRVA9OutputVO nrbrva9OutputVO = new NRBRVA9OutputVO();

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				if(StringUtils.equals("M", trustTS)) {
					nrbrva9OutputVO = esbUtilOutputVO.getNrbrvt9OutputVO(); //金錢信託
				} else {
					nrbrva9OutputVO = esbUtilOutputVO.getNrbrva9OutputVO(); //特金
				}

				// 若有回傳錯誤訊息ERR_COD,則throw exception，帶出錯誤碼及錯誤訊息
				throwError(nrbrva9OutputVO);

				// 回傳下行電文資料儲存DB
				if (CollectionUtils.isNotEmpty(batchs)) {
					seqNo = batchs.get(0).get("SEQ_NO").toString();
					updateTBSOT_ETF_TRADE_D(seqNo, tradeSeq, nrbrva9OutputVO, 1, defaultFeeRate1);

					if (batchs.size() > 1) {
						seqNo = batchs.get(1).get("SEQ_NO").toString();
						updateTBSOT_ETF_TRADE_D(seqNo, tradeSeq, nrbrva9OutputVO, 2, defaultFeeRate2);
					}
				}

				// 若有回專業投資人警語，要傳回前端顯示訊息
				msg += getWarmingMsg(nrbrva9OutputVO);
			}
			// 回傳專業投資人警語
			sot705OutputVO.setWarningMsg(msg);
		}

		return sot705OutputVO;
	}

	/**
	 * 海外ETF/股票贖回檢核 for js client
	 *
	 * 使用電文: NRBRVA9
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void verifyESBRedeemETF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.verifyESBRedeemETF(body));
	}

	/**
	 * 海外ETF/股票贖回檢核
	 *
	 * 使用電文: NRBRVA9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT705OutputVO verifyESBRedeemETF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot705InputVO = (SOT705InputVO) body;
		sot705OutputVO = new SOT705OutputVO();

		String seqNo = sot705InputVO.getSeqNo(); // 平台序號
		String tradeSeq = sot705InputVO.getTradeSeq(); // 交易序號
		String checkType = sot705InputVO.getCheckType(); // 電文確認碼
		String trustTS = sot705InputVO.getTrustTS();	//S=特金/M=金錢信託

		// 欄位檢核
		if (StringUtils.isBlank(seqNo) || StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("平台序號、交易序號或電文確認碼未輸入");
		}

		// 由交易序號取得主檔資料作為電文上行
		List<Map> list = mainInfo(seqNo, tradeSeq);

		// init util
		//特金：NRBRVA9  金錢信託：NRBRVT9
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, StringUtils.equals("M", trustTS) ? ETF_ESB_M : ETF_ESB);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// 上行欄位
		String custID = null; // 客戶ID
		String branch = null; // 分行別
		String kind = null; // 信託業務別
		String buysell = null; // 買賣別
		String dayType = null; // 委託期間
		String txDate = null; // 委託日期
		String startDate1 = null; // 交易市場日期
		String endDate1 = null; // 交易市場日期長效單迄日
		String acctNo1 = null; // 信託帳號
		String dbuobu = null; // DBU/OBU
		String acctID = null; // 扣款ID
		String acctNo3 = null; // 扣款/入帳帳號
		String payType = null; // 繳款方式
		String confirm = null; // 確認碼
		String batchNo = null; // 批號
		String empID = null; // 建機人員
		String refereeID = null; // 推薦人
		String type = null; // 交易類型
		String feeRate1 = null; // 手續費率_1
		BigDecimal defaultFeeRate = null; // 表定手續費率
		String exgCod1 = null; // 交易所代號_1
		String stockCod1 = null; // 商品代號_1
		String amount1 = null; // 下單數量_1
		String type1 = null; // 委託方式_1
		String price1 = null; // 委託價格_1
		String bargainApplySeq1 = null; // 議價編號_1
		String feeType = null; // 手續費優惠方式
		String loanCode = null; // 90天內的貸款紀錄
		String systemDate = getSystemDate();
		if (CollectionUtils.isNotEmpty(list)) {
			custID = (String) list.get(0).get("CUST_ID");
			
			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNbr = (String) list.get(0).get("BRANCH_NBR");
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
			branch = branchNbr;// TBSOT_TRADE_MAIN.BRANCH_NBR
			
			kind = String.valueOf(list.get(0).get("TRUST_CURR_TYPE"));
			buysell = String.valueOf(list.get(0).get("TRADE_SUB_TYPE"));
			dayType = "0";
			txDate = systemDate;
			startDate1 = systemDate;
			endDate1 = sdfYYYYMMDD.format((Date) list.get(0).get("DUE_DATE"));
			acctNo1 = (String) list.get(0).get("TRUST_ACCT");
			dbuobu = cbsservice.checkOBU(acctNo1.trim()); //OBU檢核改用帳號
			acctID = (String) list.get(0).get("CUST_ID");
			acctNo3 = (String) list.get(0).get("CREDIT_ACCT");
			payType = "3";
			confirm = (StringUtils.equals("1", checkType)) ? "1" : "";
			batchNo = (String) list.get(0).get("BATCH_SEQ");
			empID = (String) list.get(0).get("MODIFIER");
			refereeID = (String) list.get(0).get("NARRATOR_ID");
			type = String.valueOf(list.get(0).get("SELL_TYPE"));
			feeRate1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) list.get(0).get("FEE_RATE"), 5));
			defaultFeeRate = (BigDecimal) list.get(0).get("DEFAULT_FEE_RATE");
			exgCod1 = (String) list.get(0).get("STOCK_CODE");
			stockCod1 = (String) list.get(0).get("PROD_ID");
			amount1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) list.get(0).get("UNIT_NUM"), 0));
			type1 = String.valueOf(list.get(0).get("ENTRUST_TYPE"));
			price1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) list.get(0).get("ENTRUST_AMT"), 6));
			feeType = String.valueOf(list.get(0).get("FEE_TYPE"));
			loanCode = list.get(0).get("FLAG_NUMBER") == null ? null : (String) list.get(0).get("FLAG_NUMBER");
			if (!StringUtils.equals("A", feeType)) {
				// 申請議價時，檢核電文不須送議價編號，因為在主機中此議價編號還沒有鍵機；要等到下一步時才會鍵機
				bargainApplySeq1 = (String) list.get(0).get("BARGAIN_APPLY_SEQ");
			}
		}

		// body
		NRBRVA9InputVO txBodyVO = new NRBRVA9InputVO();
		esbUtilInputVO.setNrbrva9InputVO(txBodyVO);
		txBodyVO.setCUST_ID(custID);
		txBodyVO.setBRANCH(branch);
		txBodyVO.setKIND(kind);
		txBodyVO.setBUYSELL(buysell);
		txBodyVO.setDAYTYPE(dayType);
		txBodyVO.setTX_DATE(txDate);
		txBodyVO.setSTART_DATE_1(startDate1);
		txBodyVO.setEND_DATE_1(endDate1);
		//金錢信託檢核電文不傳信託帳號
		if(!StringUtils.equals("M", (String) list.get(0).get("TRUST_TRADE_TYPE"))) {
			txBodyVO.setACCT_NO1(acctNo1.trim()); // 00703177035018
		}
		txBodyVO.setDBUOBU(dbuobu);
		txBodyVO.setACCT_ID(acctID);
		txBodyVO.setACCT_NO3(acctNo3.trim());
		// txBodyVO.setACCT_NO3("00703177035018");
		txBodyVO.setPAY_TYPE(payType);
		txBodyVO.setCONFIRM(confirm);
		txBodyVO.setBATCH_NO(batchNo);
		txBodyVO.setEMP_ID(empID);
		txBodyVO.setREFEREE_ID(refereeID);
		txBodyVO.setTYPE(type);
		txBodyVO.setFEE_RATE_1(feeRate1);
		txBodyVO.setEXG_COD_1(exgCod1);
		txBodyVO.setSTOCK_COD_1(stockCod1);
		txBodyVO.setAMOUNT_1(amount1);
		txBodyVO.setTYPE_1(getEntrustTypeUp(type1));// 轉成電文要編號
		txBodyVO.setPRICE_1(price1);
		txBodyVO.setBARGAIN_APPLY_SEQ_1(bargainApplySeq1);
		txBodyVO.setIS_PL_1("N");
		txBodyVO.setTAKE_PROFIT_PERC_1("0");
		txBodyVO.setTAKE_PROFIT_SYM_1("+");
		txBodyVO.setSTOP_LOSS_PERC_1("0");
		txBodyVO.setSTOP_LOSS_SYM_1("+");
		

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		NRBRVA9OutputVO nrbrva9OutputVO = new NRBRVA9OutputVO();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			if(StringUtils.equals("M", trustTS)) {
				nrbrva9OutputVO = esbUtilOutputVO.getNrbrvt9OutputVO(); //金錢信託
			} else {
				nrbrva9OutputVO = esbUtilOutputVO.getNrbrva9OutputVO(); //特金
			}

			// 若沒有錯誤，回傳下行電文資料儲存DB
			if (!checkError(nrbrva9OutputVO)) {
				updateTBSOT_ETF_TRADE_D(seqNo, tradeSeq, nrbrva9OutputVO, null, defaultFeeRate);
			}

			// 若有回專業投資人警語，要傳回前端顯示訊息
			sot705OutputVO.setWarningMsg(getWarmingMsg(nrbrva9OutputVO));
		}

		return sot705OutputVO;
	}

	/**
	 * 海外ETF/股票贖回確認 for js client
	 *
	 * 使用電文: NRBRVA9
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void confirmESBRedeemETF(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.confirmESBRedeemETF(body));
	}

	/**
	 * 海外ETF/股票贖回確認
	 *
	 * 使用電文: NRBRVA9
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT705OutputVO confirmESBRedeemETF(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		Map<String, String> trustTSMap = xmlInfo.doGetVariable("SOT.TRUST_TS", FormatHelper.FORMAT_3);

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot705InputVO = (SOT705InputVO) body;
		sot705OutputVO = new SOT705OutputVO();

		String tradeSeq = sot705InputVO.getTradeSeq(); // 交易序號
		String checkType = sot705InputVO.getCheckType(); // 電文確認碼
		String trustTS = sot705InputVO.getTrustTS();	//S=特金/M=金錢信託
		String seqNo = null; // 平台序號
		String msg = null; // 專業投資人警語

		// 欄位檢核
		if (StringUtils.isBlank(tradeSeq) || StringUtils.isBlank(checkType)) {
			throw new JBranchException("交易序號或電文確認碼未輸入");
		}
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		// 由交易序號取得主檔資料作為電文上行
		String sql = new StringBuffer("select * " + "  from TBSOT_TRADE_MAIN " + " where TRADE_SEQ = :TRADESEQ ").toString();

		condition.setObject("TRADESEQ", tradeSeq);
		condition.setQueryString(sql);
		List<Map> mains = dam.exeQuery(condition);

		sql = new StringBuffer("select BATCH_SEQ, " + "       count(1) as BATCH_COUNT " + "  from TBSOT_ETF_TRADE_D " + " where TRADE_SEQ = :TRADESEQ " + " group by BATCH_SEQ ").toString();

		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("TRADESEQ", tradeSeq);
		condition.setQueryString(sql);
		List<Map> details = dam.exeQuery(condition);

		for (int idx = 0; idx <= details.size() - 1; idx++) {
			// 依照批號取得明細資料
			List<Map> batchs = batchInfo(tradeSeq, (String) details.get(idx).get("BATCH_SEQ"));

			// init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, StringUtils.equals("M", trustTS) ? ETF_ESB_M : ETF_ESB);
			esbUtilInputVO.setModule(new Object() {
			}.getClass().getEnclosingMethod().getName());

			// head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			esbUtilInputVO.setTxHeadVO(txHead);
			txHead.setDefaultTxHead();

			// 上行欄位
			String custID = null; // 客戶ID
			String branch = null; // 分行別
			String kind = null; // 信託業務別
			String buysell = null; // 買賣別
			String dayType = null; // 委託期間
			String txDate = null; // 委託日期
			String startDate1 = null; // 交易市場日期
			String endDate1 = null; // 交易市場日期長效單迄日
			String acctNo1 = null; // 信託帳號
			String dbuobu = null; // DBU/OBU
			String acctID = null; // 扣款ID
			String acctNo3 = null; // 扣款/入帳帳號
			String payType = null; // 繳款方式
			String confirm = null; // 確認碼
			String batchNo = null; // 批號
			String empID = null; // 建機人員
			String refereeID = null; // 推薦人
			String type = null; // 交易類型
			String feeRate1 = null; // 手續費率_1
			BigDecimal defaultFeeRate1 = null; // 表定手續費率_1
			String exgCod1 = null; // 交易所代號_1
			String stockCod1 = null; // 商品代號_1
			String groupIfa1 = null; // 優惠團體代碼_1
			String amount1 = null; // 下單數量_1
			String type1 = null; // 委託方式_1
			String price1 = null; // 委託價格_1
			String feeRate2 = null; // 手續費率_2
			BigDecimal defaultFeeRate2 = null; // 表定手續費率_2
			String exgCod2 = null; // 交易所代號_2
			String stockCod2 = null; // 商品代號_2
			String groupIfa2 = null; // 優惠團體代碼_2
			String amount2 = null; // 下單數量_2
			String type2 = null; // 委託方式_2
			String price2 = null; // 委託價格_2
			String bargainApplySeq1 = null; // 議價編號_1
			String bargainApplySeq2 = null; // 議價編號_2
			String isPl1 = null; // 是否停損停利_1
			String takeProfitPerc1 = null; // 停利點_1
			String takeProfitSym1 = null; // 停利符號_1
			String stopLossPerc1 = null; // 停損點_1
			String stopLossSym1 = null; // 停損符號_1
			String takeProfitPerc2 = null; // 是否停損停利_2
			String takeProfitSym2 = null; // 停利點_2
			String stopLossPerc2 = null; // 停利符號_2
			String stopLossSym2 = null; // 停損點_2
			String isPl2 = null; // 停損符號_2
			String loanCode = null; // 90天內的貸款紀錄

			BigDecimal percentage = null; // 百分比
			String systemDate = getSystemDate();
			if (CollectionUtils.isNotEmpty(batchs)) {
				custID = (String) mains.get(0).get("CUST_ID");
				
				// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
				// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
				String branchNbr = (String) mains.get(0).get("BRANCH_NBR");
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
				branch = branchNbr;
				
				kind = String.valueOf(batchs.get(0).get("TRUST_CURR_TYPE"));
				buysell = String.valueOf(batchs.get(0).get("TRADE_SUB_TYPE"));
				dayType = "0";
				txDate = systemDate;
				startDate1 = systemDate;
				endDate1 = sdfYYYYMMDD.format((Date) batchs.get(0).get("DUE_DATE"));
				acctNo1 = (String) batchs.get(0).get("TRUST_ACCT");
				dbuobu = cbsservice.checkOBU(acctNo1.trim()); //OBU檢核改用帳號
				acctID = custID;
				acctNo3 = (String) batchs.get(0).get("CREDIT_ACCT");
				payType = "3";
				confirm = (StringUtils.equals("1", checkType)) ? "Y" : "";
				batchNo = (String) batchs.get(0).get("BATCH_SEQ");
				empID = (String) batchs.get(0).get("MODIFIER");
				refereeID = (String) batchs.get(0).get("NARRATOR_ID");
				type = String.valueOf(batchs.get(0).get("SELL_TYPE"));
				feeRate1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(0).get("FEE_RATE"), 5));
				defaultFeeRate1 = (BigDecimal) batchs.get(0).get("DEFAULT_FEE_RATE");
				exgCod1 = (String) batchs.get(0).get("STOCK_CODE");
				stockCod1 = (String) batchs.get(0).get("PROD_ID");
				groupIfa1 = (String) batchs.get(0).get("GROUP_OFA");
				amount1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(0).get("UNIT_NUM"), 0));
				type1 = String.valueOf(batchs.get(0).get("ENTRUST_TYPE"));
				price1 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(0).get("ENTRUST_AMT"), 6));
				bargainApplySeq1 = (String) batchs.get(0).get("BARGAIN_APPLY_SEQ");
				isPl1 = (batchs.get(0).get("TAKE_PROFIT_PERC") != null || batchs.get(0).get("STOP_LOSS_PERC") != null) ? "Y" : "N";
				percentage = (BigDecimal) batchs.get(0).get("TAKE_PROFIT_PERC");
				takeProfitSym1 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
				percentage = (BigDecimal) batchs.get(0).get("STOP_LOSS_PERC");
				stopLossSym1 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
				bargainApplySeq1 = (String) batchs.get(0).get("BARGAIN_APPLY_SEQ");
				takeProfitPerc1 = batchs.get(0).get("TAKE_PROFIT_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) batchs.get(0).get("TAKE_PROFIT_PERC")).abs(), 2));
				stopLossPerc1 = batchs.get(0).get("STOP_LOSS_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) batchs.get(0).get("STOP_LOSS_PERC")).abs(), 2));
				loanCode = (CollectionUtils.isEmpty(mains)) ?  null : (String) mains.get(0).get("FLAG_NUMBER") == null ? null : (String) mains.get(0).get("FLAG_NUMBER");
				if (batchs.size() > 1) {
					feeRate2 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(1).get("FEE_RATE"), 5));
					defaultFeeRate2 = (BigDecimal) batchs.get(1).get("DEFAULT_FEE_RATE");
					exgCod2 = (String) batchs.get(1).get("STOCK_CODE");
					stockCod2 = (String) batchs.get(1).get("PROD_ID");
					groupIfa2 = (String) batchs.get(1).get("GROUP_OFA");
					amount2 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(1).get("UNIT_NUM"), 0));
					type2 = String.valueOf(batchs.get(1).get("ENTRUST_TYPE"));
					price2 = String.valueOf(new EsbUtil().decimalPadding((BigDecimal) batchs.get(1).get("ENTRUST_AMT"), 6));
					bargainApplySeq2 = (String) batchs.get(1).get("BARGAIN_APPLY_SEQ");
					percentage = (BigDecimal) batchs.get(1).get("TAKE_PROFIT_PERC");
					takeProfitSym2 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
					percentage = (BigDecimal) batchs.get(1).get("STOP_LOSS_PERC");
					stopLossSym2 = (percentage != null && percentage.compareTo(BigDecimal.ZERO) >= 0) ? "+" : "-";
					isPl2 = (batchs.get(1).get("TAKE_PROFIT_PERC") != null || batchs.get(1).get("STOP_LOSS_PERC") != null) ? "Y" : "N";
					takeProfitPerc2 = batchs.get(1).get("TAKE_PROFIT_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) batchs.get(1).get("TAKE_PROFIT_PERC")).abs(), 2));
					stopLossPerc2 = batchs.get(1).get("STOP_LOSS_PERC") == null ? "0" : String.valueOf(new EsbUtil().decimalPadding(((BigDecimal) batchs.get(1).get("STOP_LOSS_PERC")).abs(), 2));
				}
			}

			// body
			NRBRVA9InputVO txBodyVO = new NRBRVA9InputVO();
			esbUtilInputVO.setNrbrva9InputVO(txBodyVO);
			txBodyVO.setCUST_ID(!(StringUtils.equals("M", (String) mains.get(0).get("TRUST_TRADE_TYPE"))) ? custID : trustTSMap.get("M_CUSTNO"));
			txBodyVO.setBRANCH(branch);
			txBodyVO.setKIND(kind);
			txBodyVO.setBUYSELL(buysell);
			txBodyVO.setDAYTYPE(dayType);
			txBodyVO.setTX_DATE(txDate);
			txBodyVO.setSTART_DATE_1(startDate1);
			txBodyVO.setEND_DATE_1(endDate1);
			txBodyVO.setACCT_NO1(acctNo1);
			txBodyVO.setDBUOBU(dbuobu);
			txBodyVO.setACCT_ID(!(StringUtils.equals("M", (String) mains.get(0).get("TRUST_TRADE_TYPE"))) ? acctID : trustTSMap.get("M_CUSTNO"));
			txBodyVO.setACCT_NO3(acctNo3);
			txBodyVO.setPAY_TYPE(payType);
			txBodyVO.setCONFIRM(confirm);
			txBodyVO.setBATCH_NO(batchNo);
			txBodyVO.setEMP_ID(empID);
			txBodyVO.setREFEREE_ID(refereeID);
			txBodyVO.setTYPE(type);
			txBodyVO.setFEE_RATE_1(feeRate1);
			txBodyVO.setEXG_COD_1(exgCod1);
			txBodyVO.setSTOCK_COD_1(stockCod1);
			txBodyVO.setGROUP_IFA_1(groupIfa1);
			txBodyVO.setAMOUNT_1(amount1);
			txBodyVO.setTYPE_1(getEntrustTypeUp(type1));// 轉成電文要編號
			txBodyVO.setPRICE_1(price1);
			txBodyVO.setFEE_RATE_2(feeRate2);
			txBodyVO.setEXG_COD_2(exgCod2);
			txBodyVO.setSTOCK_COD_2(stockCod2);
			txBodyVO.setGROUP_IFA_2(groupIfa2);
			txBodyVO.setAMOUNT_2(amount2);
			txBodyVO.setTYPE_2(getEntrustTypeUp(type2));// 轉成電文要編號
			txBodyVO.setPRICE_2(price2);
			txBodyVO.setBARGAIN_APPLY_SEQ_1(bargainApplySeq1);
			txBodyVO.setBARGAIN_APPLY_SEQ_2(bargainApplySeq2);
			txBodyVO.setIS_PL_1(isPl1);
			txBodyVO.setTAKE_PROFIT_PERC_1(takeProfitPerc1);
			txBodyVO.setTAKE_PROFIT_SYM_1(takeProfitSym1);
			txBodyVO.setSTOP_LOSS_PERC_1(stopLossPerc1);
			txBodyVO.setSTOP_LOSS_SYM_1(stopLossSym1);
			txBodyVO.setIS_PL_2(isPl2);
			txBodyVO.setTAKE_PROFIT_PERC_2(takeProfitPerc2);
			txBodyVO.setTAKE_PROFIT_SYM_2(takeProfitSym2);
			txBodyVO.setSTOP_LOSS_PERC_2(stopLossPerc2);
			txBodyVO.setSTOP_LOSS_SYM_2(stopLossSym2);

			// 發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
			NRBRVA9OutputVO nrbrva9OutputVO = new NRBRVA9OutputVO();

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				if(StringUtils.equals("M", trustTS)) {
					nrbrva9OutputVO = esbUtilOutputVO.getNrbrvt9OutputVO(); //金錢信託
				} else {
					nrbrva9OutputVO = esbUtilOutputVO.getNrbrva9OutputVO(); //特金
				}

				// 若有回傳錯誤訊息ERR_COD,則throw exception，帶出錯誤碼及錯誤訊息
				throwError(nrbrva9OutputVO);

				// 回傳下行電文資料儲存DB
				seqNo = batchs.get(0).get("SEQ_NO").toString();
				updateTBSOT_ETF_TRADE_D(seqNo, tradeSeq, nrbrva9OutputVO, 1, defaultFeeRate1);

				if (batchs.size() > 1) {
					seqNo = batchs.get(1).get("SEQ_NO").toString();
					updateTBSOT_ETF_TRADE_D(seqNo, tradeSeq, nrbrva9OutputVO, 2, defaultFeeRate2);
				}

				// 若有回專業投資人警語，要傳回前端顯示訊息
				msg += getWarmingMsg(nrbrva9OutputVO);
			}
			// 回傳專業投資人警語
			sot705OutputVO.setWarningMsg(msg);
		}

		return sot705OutputVO;
	}

	/**
	 * 海外ETF/股票產品庫存資料查詢 for js client
	 *
	 * 使用電文: NFVIPA
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustAssetETFData(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getCustAssetETFData(body));
	}

	/**
	 * auto fill obu info.
	 * #1875: 資產負債總覽OBU判斷邏輯調整  SamTu 2024.01.24
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustAssetETFDataCheckObu(Object body, IPrimitiveMap header) throws Exception {

		sot705InputVO = (SOT705InputVO) body;
		String custID = sot705InputVO.getCustId(); // 客戶ID
		String obuFlag = sot705InputVO.getObuFlag();

		if (StringUtils.isNotBlank(obuFlag)) {
			sot705InputVO.setIsOBU("Y".equals(obuFlag));
		} else {
			SOT701 sot701 = (SOT701) PlatformContext.getBean(SOT701.class.getSimpleName().toLowerCase());
			sot705InputVO.setIsOBU(sot701.isObu(custID));
		}

		sendRtnObject(this.getCustAssetETFData(sot705InputVO));
	}

	/**
	 * 海外ETF/股票產品庫存資料查詢
	 *
	 * 使用電文: NFVIPA
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT705OutputVO getCustAssetETFData(Object body) throws Exception {
		sot705InputVO = (SOT705InputVO) body;
		sot705OutputVO = new SOT705OutputVO();

		String custID = sot705InputVO.getCustId(); // 客戶ID
		Boolean isObu = sot705InputVO.getIsOBU(); // 是否為OBU客戶
		Boolean isInTran = (sot705InputVO.getIsInTran() == null ? Boolean.FALSE : sot705InputVO.getIsInTran()); // 是否包含在途資料；true->回傳庫存+在途
		//		Boolean isOnlyInTran = (sot705InputVO.getIsOnlyInTran() == null ? Boolean.FALSE : sot705InputVO.getIsOnlyInTran()); // 是否只回傳在途資料; true->只回傳在途資料

		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("未輸入客戶ID");
		}

		// SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		// SOT701InputVO sot701InputVO = new SOT701InputVO();
		// sot701InputVO.setCustID(custID);
		// String curAcctName =
		// sot701.getFC032659AcnoCode(sot701InputVO);//查詢：取得戶名電文 電文FC032659

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_ASSETS);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NFVIPAInputVO txBodyVO = new NFVIPAInputVO();
		esbUtilInputVO.setNfvipaInputVO(txBodyVO);
		txBodyVO.setFUNCTION("UK");
		// txBodyVO.setRANGE(curAcctName); //不傳這個戶名欄位，回傳所有庫資料
		txBodyVO.setCUSID(custID);
		txBodyVO.setUNIT((isObu != null && isObu) ? "O" : "D");

		// 發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);

		// ETF庫存+在途清單
		List<CustAssetETFVO> etfList = new ArrayList<CustAssetETFVO>();
		// ETF庫存清單
		List<CustAssetETFVO> eList = new ArrayList<CustAssetETFVO>();
		// Stock庫存+在途清單
		List<CustAssetETFVO> stockList = new ArrayList<CustAssetETFVO>();
		// Stock庫存清單
		List<CustAssetETFVO> sList = new ArrayList<CustAssetETFVO>();
		//賣出委託交易資訊
		List<CustAssetETFVO> onTradeList = new ArrayList<CustAssetETFVO>();

		for (ESBUtilOutputVO vo : esbUtilOutputVO) {
			NFVIPAOutputVO nfvipaOutputVO = vo.getNfvipaOutputVO();
			// System.out.println("[TEST] size for each vo: "+nfvipaOutputVO.getDetails().size());

			// 若有回傳錯誤訊息ERR_COD,則throw exception，帶出錯誤碼及錯誤訊息
			checkError(nfvipaOutputVO);

			if (nfvipaOutputVO.getRANGE() != null) {

				// RANGE="0001"為庫存，其他為在途 (!isInTran：只需要庫存	isInTran：庫存 + 在途)
				if ((!isInTran && "0001".equals(nfvipaOutputVO.getRANGE())) || isInTran) {

					// 根據產品類別以及查詢條件回傳庫存資料
					List<NFVIPAOutputVODetails> details = nfvipaOutputVO.getDetails();
					details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NFVIPAOutputVODetails>() : details;

					for (NFVIPAOutputVODetails detail : details) {
						// detail庫存資料轉換至CustAssetETFVO中
						CustAssetETFVO custAssetETFVO = new CustAssetETFVO();

						if ("0001".equals(nfvipaOutputVO.getRANGE())) {
							custAssetETFVO.setAssetType(nfvipaOutputVO.getRANGE());
							custAssetETFVO.setTrxMarket(detail.getTrxMarket());
							custAssetETFVO.setInsuranceNo(detail.getInsuranceNo());
							custAssetETFVO.setCurCode(detail.getCurCode());
							custAssetETFVO.setNumber(new EsbUtil().decimalPoint(detail.getNumber(), 0));
							custAssetETFVO.setState(detail.getState());
							custAssetETFVO.setEntrustCur(detail.getEntrustCur());
							custAssetETFVO.setAvgBuyingPrice(new EsbUtil().decimalPoint(detail.getAvgBuyingPrice(), 6));
							custAssetETFVO.setCurAmt(new EsbUtil().decimalPoint(detail.getCurAmt(), 6));
							custAssetETFVO.setForCurBal(new EsbUtil().decimalPoint(detail.getForCurBal(), 2));
							custAssetETFVO.setAcctBal(new EsbUtil().decimalPoint(detail.getAcctBal(), 2));
							custAssetETFVO.setDate08(StringUtils.isEmpty(detail.getDate08()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getDate08()));						
							custAssetETFVO.setReferenceRate(new EsbUtil().decimalPoint(detail.getReferenceRate(), 4));						
     						custAssetETFVO.setTrustBusinessType(detail.getTrustBusinessType());
							custAssetETFVO.setTrustAcct(detail.getTrustAcct());
							custAssetETFVO.setReturnRateSign(detail.getReturnRateSign());
							custAssetETFVO.setReturnRate(new EsbUtil().decimalPoint(detail.getReturnRate(), 2));
							custAssetETFVO.setProductType2(detail.getProductType2());
							custAssetETFVO.setProductType(detail.getProductType());
							custAssetETFVO.setProductName(detail.getProductName());
							custAssetETFVO.setForCurCost(new EsbUtil().decimalPoint(detail.getForCurCost(), 2));
							custAssetETFVO.setAcctBalCost(new EsbUtil().decimalPoint(detail.getAcctBalCost(), 0));
							custAssetETFVO.setTradeDateEnd(StringUtils.isEmpty(detail.getTradeDateEnd()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getTradeDateEnd()));
							custAssetETFVO.setIsPledged(StringUtils.isEmpty(detail.getCmkType()) ? "N" : StringUtils.equals(detail.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); //質借圈存註記
							custAssetETFVO.setReturnRateSign2(detail.getReturnRateSign2());
							custAssetETFVO.setReturnRate2(new EsbUtil().decimalPoint(detail.getReturnRate2(), 2));
							custAssetETFVO.setDividend(new EsbUtil().decimalPoint(detail.getDividend(), 2));

						} else if ("0002".equals(nfvipaOutputVO.getRANGE())) { // 買入在途
							custAssetETFVO.setAssetType(nfvipaOutputVO.getRANGE());
							custAssetETFVO.setTrxMarket(detail.getTrxMarket());
							custAssetETFVO.setInsuranceNo(detail.getInsuranceNo());
							custAssetETFVO.setCurCode(detail.getCurCode());
							custAssetETFVO.setState(detail.getState());
							custAssetETFVO.setEntrustCur(detail.getEntrustCur());
							custAssetETFVO.setResult(detail.getResult());
							custAssetETFVO.setEntrustDate(StringUtils.isEmpty(detail.getEntrustDate()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getEntrustDate()));
							custAssetETFVO.setTradeDate(StringUtils.isEmpty(detail.getTradeDate()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getTradeDate()));
							custAssetETFVO.setEntrustAmt(new EsbUtil().decimalPoint(detail.getEntrustAmt(), 0));
							custAssetETFVO.setEntrustPrice(new EsbUtil().decimalPoint(detail.getEntrustPrice(), 6));
							//							custAssetETFVO.setChannelType(detail.getChannelType());
							custAssetETFVO.setChannelType(" ".equals(detail.getChannelType()) || "".equals(detail.getChannelType()) ? "S" : detail.getChannelType());
							custAssetETFVO.setTrustType(detail.getTrustType());
							custAssetETFVO.setTrustAcct(detail.getTrustAcct());
							custAssetETFVO.setTradeAmt(new EsbUtil().decimalPoint(detail.getTradeAmt(), 0));
							custAssetETFVO.setTradePrice(new EsbUtil().decimalPoint(detail.getTradePrice(), 6));
							custAssetETFVO.setTradeCost(new EsbUtil().decimalPoint(detail.getTradeCost(), 2));
							custAssetETFVO.setProductName(detail.getProductName());
							custAssetETFVO.setAcctBalCost(new EsbUtil().decimalPoint(detail.getAcctBalCost(), 0));
							custAssetETFVO.setProductType(detail.getProductType());
							custAssetETFVO.setTradeDateEnd(StringUtils.isEmpty(detail.getTradeDateEnd()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getTradeDateEnd()));
							custAssetETFVO.setIsPledged(StringUtils.isEmpty(detail.getCmkType()) ? "N" : StringUtils.equals(detail.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); //質借圈存註記

						} else if (nfvipaOutputVO.getRANGE().matches("0003|0004")) {
							custAssetETFVO.setAssetType(nfvipaOutputVO.getRANGE());
							custAssetETFVO.setTrxMarket(detail.getTrxMarket());
							custAssetETFVO.setInsuranceNo(detail.getInsuranceNo());
							custAssetETFVO.setCurCode(detail.getCurCode());
							custAssetETFVO.setState(detail.getState());
							custAssetETFVO.setEntrustCur(detail.getEntrustCur());
							custAssetETFVO.setResult(detail.getResult());
							custAssetETFVO.setEntrustDate(StringUtils.isEmpty(detail.getEntrustDate()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getEntrustDate()));
							custAssetETFVO.setTradeDate(StringUtils.isEmpty(detail.getTradeDate()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getTradeDate()));
							custAssetETFVO.setEntrustAmt(new EsbUtil().decimalPoint(detail.getEntrustAmt(), 0));
							custAssetETFVO.setEntrustPrice(new EsbUtil().decimalPoint(detail.getEntrustPrice(), 6));
							//							custAssetETFVO.setChannelType(detail.getChannelType());
							custAssetETFVO.setChannelType(detail.getChannelType() == null || "".equals(detail.getChannelType()) || "".equals(detail.getChannelType()) ? "S" : detail.getChannelType());
							custAssetETFVO.setTrustType(detail.getTrustType());
							custAssetETFVO.setTrustAcct(detail.getTrustAcct());
							custAssetETFVO.setTradeAmt(new EsbUtil().decimalPoint(detail.getTradeAmt(), 0));
							custAssetETFVO.setTradePrice(new EsbUtil().decimalPoint(detail.getTradePrice(), 6));
							custAssetETFVO.setTradeCost(new EsbUtil().decimalPoint(detail.getTradeCost(), 2));
							custAssetETFVO.setForCurBal(new EsbUtil().decimalPoint(detail.getForCurBal(), 2));
							custAssetETFVO.setCurAmt(new EsbUtil().decimalPoint(detail.getCurAmt(), 6));
							custAssetETFVO.setDate08(StringUtils.isEmpty(detail.getDate08()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getDate08()));
							custAssetETFVO.setProductName(detail.getProductName());
							custAssetETFVO.setAcctBalCost(new EsbUtil().decimalPoint(detail.getAcctBalCost(), 0));
							custAssetETFVO.setAcctBal(new EsbUtil().decimalPoint(detail.getAcctBal(), 0));
							custAssetETFVO.setOrderType(detail.getOrderType());
							custAssetETFVO.setProductType(detail.getProductType());
							custAssetETFVO.setTradeDateEnd(StringUtils.isEmpty(detail.getTradeDateEnd()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getTradeDateEnd()));
							custAssetETFVO.setIsPledged(StringUtils.isEmpty(detail.getCmkType()) ? "N" : StringUtils.equals(detail.getCmkType().trim(), CMKTYPE_MK03) ? "Y" : "N"); //質借圈存註記
							custAssetETFVO.setReturnRateSign2(detail.getReturnRateSign2());
							custAssetETFVO.setReturnRate2(new EsbUtil().decimalPoint(detail.getReturnRate2(), 2));
							custAssetETFVO.setDividend(new EsbUtil().decimalPoint(detail.getDividend(), 2));
							if("0003".equals(nfvipaOutputVO.getRANGE())) {
								if("1".equals(detail.getState()) && "1".equals(detail.getResult())) {
									custAssetETFVO.setOnTradeMemo("賣出已成交未撥款");
								} else if ("1".equals(detail.getState()) && "2".equals(detail.getResult())) {
									custAssetETFVO.setOnTradeMemo("賣出交易已取消(部分成交)未撥款");
								}
							} else if ("0004".equals(nfvipaOutputVO.getRANGE())) {
								if("1".equals(detail.getState()) && "1".equals(detail.getResult())) {
									custAssetETFVO.setOnTradeMemo("賣出委託中");
								}
							} 
						} else if (nfvipaOutputVO.getRANGE().matches("0006")) { //賣出在途使用金額
							custAssetETFVO.setAssetType(nfvipaOutputVO.getRANGE());
							custAssetETFVO.setOccur(new EsbUtil().decimalPoint(nfvipaOutputVO.getOCCUR(), 0));
							custAssetETFVO.setStockUForCurBal(new EsbUtil().decimalPoint(detail.getStockUForCurBal(), 2));
							custAssetETFVO.setStockUBal(new EsbUtil().decimalPoint(detail.getStockUBal(), 0));
						} else if (nfvipaOutputVO.getRANGE().matches("0007")) { //股票/ETF圈存金額
							custAssetETFVO.setAssetType(nfvipaOutputVO.getRANGE());
							custAssetETFVO.setOccur(new EsbUtil().decimalPoint(nfvipaOutputVO.getOCCUR(), 0));
							custAssetETFVO.setStockCForCurBal(new EsbUtil().decimalPoint(detail.getStockCForCurBal(), 2));
							custAssetETFVO.setStockCBal(new EsbUtil().decimalPoint(detail.getStockCBal(), 0));
						} else if (nfvipaOutputVO.getRANGE().matches("0008")) { //股票/ETF買進委託未成交金額
							custAssetETFVO.setAssetType(nfvipaOutputVO.getRANGE());
							custAssetETFVO.setOccur(new EsbUtil().decimalPoint(nfvipaOutputVO.getOCCUR(), 0));
							custAssetETFVO.setTrmCurBal(new EsbUtil().decimalPoint(detail.getTrmCurBal(), 2));
							custAssetETFVO.setTrmBal(new EsbUtil().decimalPoint(detail.getTrmBal(), 0));
						} else if (nfvipaOutputVO.getRANGE().matches("0009")) { //股票/ETF在途款金額 (本日在途款總額)
							custAssetETFVO.setAssetType(nfvipaOutputVO.getRANGE());
							custAssetETFVO.setOccur(new EsbUtil().decimalPoint(nfvipaOutputVO.getOCCUR(), 0));
							custAssetETFVO.setStockTForCurBal(new EsbUtil().decimalPoint(detail.getStockTForCurBal(), 2));
							custAssetETFVO.setStockTBal(new EsbUtil().decimalPoint(detail.getStockTBal(), 0));
						}

						// 將ETF及股票資料分別放入庫存List中
						String productType = detail.getProductType();

						// ETF
						if (StringUtils.equals("E", productType)) {
							etfList.add(custAssetETFVO);
							if ("0001".equals(nfvipaOutputVO.getRANGE()))
								eList.add(custAssetETFVO);
						}
						// Stock
						else if (StringUtils.equals("S", productType)) {
							stockList.add(custAssetETFVO);
							if ("0001".equals(nfvipaOutputVO.getRANGE()))
								sList.add(custAssetETFVO);
						} else {
							onTradeList.add(custAssetETFVO);
						}
					}
				}
			}
		}

		// 回復庫存查詢資料
		sot705OutputVO.setCustAssetETFList(etfList);
		sot705OutputVO.setCustAssetStockList(stockList);
		sot705OutputVO.setOnTradeList(onTradeList);
		sot705OutputVO.seteList(eList);
		sot705OutputVO.setsList(sList);

		return sot705OutputVO;
	}

	/**
	 * 海外ETF/股票產品交易指示到期日查詢 for js client
	 *
	 * 使用電文: NR096N
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getTradeDueDate(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getTradeDueDate(body));
	}

	/**
	 * 海外ETF/股票產品交易指示到期日查詢
	 *
	 * 使用電文: NR096N
	 *
	 * @param body
	 * @throws Exception
	 */
	public SOT705OutputVO getTradeDueDate(Object body) throws Exception {
		sot705InputVO = (SOT705InputVO) body;
		sot705OutputVO = new SOT705OutputVO();

		String custID = sot705InputVO.getCustId(); // 客戶D
		String stockCode = sot705InputVO.getStockCode(); // 交易所代號

		if (StringUtils.isBlank(custID) || StringUtils.isBlank(stockCode)) {
			throw new JBranchException("客戶ID或交易所代號未輸入");
		}

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_DUE_DATE);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NR096NInputVO txBodyVO = new NR096NInputVO();
		esbUtilInputVO.setNr096NInputVO(txBodyVO);
		txBodyVO.setCustId(custID);
		txBodyVO.setExchangeNo(stockCode);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		// NR096NOutputVO資料轉換至CustAssetETFVO中
		List tradeDuteDates = new ArrayList();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NR096NOutputVO nr096NOutputVO = esbUtilOutputVO.getNr096NOutputVO();

			// 日期資料 (多筆)
			List<NR096NOutputVODetails> txMkDateList = nr096NOutputVO.getDetails();
			if (CollectionUtils.isNotEmpty(txMkDateList)) {
				for (NR096NOutputVODetails detail : txMkDateList) {
					Date txMkDate1 = (StringUtils.isNotBlank(detail.getTxMkDate1())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate1()) : null;
					Date txMkDate2 = (StringUtils.isNotBlank(detail.getTxMkDate2())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate2()) : null;
					Date txMkDate3 = (StringUtils.isNotBlank(detail.getTxMkDate3())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate3()) : null;
					Date txMkDate4 = (StringUtils.isNotBlank(detail.getTxMkDate4())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate4()) : null;
					Date txMkDate5 = (StringUtils.isNotBlank(detail.getTxMkDate5())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate5()) : null;
					Date txMkDate6 = (StringUtils.isNotBlank(detail.getTxMkDate6())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate6()) : null;
					Date txMkDate7 = (StringUtils.isNotBlank(detail.getTxMkDate7())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate7()) : null;
					Date txMkDate8 = (StringUtils.isNotBlank(detail.getTxMkDate8())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate8()) : null;
					Date txMkDate9 = (StringUtils.isNotBlank(detail.getTxMkDate9())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate9()) : null;
					Date txMkDate10 = (StringUtils.isNotBlank(detail.getTxMkDate10())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getTxMkDate10()) : null;

					tradeDuteDates.add(txMkDate1);
					tradeDuteDates.add(txMkDate2);
					tradeDuteDates.add(txMkDate3);
					tradeDuteDates.add(txMkDate4);
					tradeDuteDates.add(txMkDate5);
					tradeDuteDates.add(txMkDate6);
					tradeDuteDates.add(txMkDate7);
					tradeDuteDates.add(txMkDate8);
					tradeDuteDates.add(txMkDate9);
					tradeDuteDates.add(txMkDate10);
				}
			}
		}

		sot705OutputVO.setTradeDueDate(tradeDuteDates);
		return sot705OutputVO;
	}

	/**
	 * 
	 * 系統日期
	 * 
	 * @return
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	//FOR CBS測試日期
	private String getSystemDate() throws DAOException, JBranchException {
		return cbsservice.getCBSTestDate().substring(0, 8);
	}

	/**
	 * 委託方式 將DB ENTRUST_TYPE資料轉成電文要的 TDS705 參照XML參數SOT.ENTRUST_TYPE_UP
	 * 取得參數編碼為1stBatch.ENTRUST_TYPE對照的參數值
	 * 
	 * 1=>0 2=>1 3=>2 4=>2
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String getEntrustTypeUp(String ENTRUST_TYPE) throws JBranchException {
		if (StringUtils.isNotBlank(ENTRUST_TYPE)) {
			XmlInfo xmlInfo = new XmlInfo();
			String ESB_ENTRUST_TYPE = (String) xmlInfo.getVariable("SOT.ENTRUST_TYPE_UP", ENTRUST_TYPE, "F3");
			if (StringUtils.isNotBlank(ESB_ENTRUST_TYPE))
				return ESB_ENTRUST_TYPE;
		}
		logger.error(String.format("not found SOT.ENTRUST_TYPE_UP:%s", ENTRUST_TYPE));
		return null;
	}

	/**
	 * 海外ETF/股票委託交易資料查詢 發送電文NR080N 需傳入參數：custId
	 */
	public List<CustOrderETFVO> getCustOrderETFData(Object body) throws Exception {
		SOT705InputVO inputVO = (SOT705InputVO) body;
		String custId = inputVO.getCustId();

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NR080N_DATA);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());
		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		// body
		NR080NInputVO nr080nInputVO = new NR080NInputVO();
		nr080nInputVO.setCustId(custId);
		nr080nInputVO.setCurAcctName("WMSG");
		// nr080nInputVO.setHDRVQ1("NFDRVFPQ");
		esbUtilInputVO.setNr080nInputVO(nr080nInputVO);
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		List<CustOrderETFVO> cOETFVos = new ArrayList();
		System.out.println("start for:" + vos.size());
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NR080NOutputVO nr080nOutputVO = esbUtilOutputVO.getNr080nOutputVO();
			// 若有回傳錯誤訊息ERR_COD,則throw exception，帶出錯誤碼及錯誤訊息
			throwError(nr080nOutputVO);
			TxHeadVO txHeadVOOutput = esbUtilOutputVO.getTxHeadVO();
			// System.out.println("yo:"+txHeadVOOutput.getHFMTID());
			// 根據不同委託類型判斷要留下哪種類型的資料
			String hfmtid = txHeadVOOutput.getHFMTID().trim();
			System.out.println("inputVO.getQueryType():" + inputVO.getQueryType() + " hfmtid:" + hfmtid);
			String queryType = inputVO.getQueryType();
			if ("SOT230".equals(queryType)) {
				if (!("0001".equals(hfmtid) || "0002".equals(hfmtid))) {
					continue;
				}
			}
			if ("SOT240".equals(queryType)) {
				if (!"0003".equals(hfmtid)) {
					continue;
				}
			}

			List<NR080NOutputVODetailsVO> detailsVOList = nr080nOutputVO.getDetails();
			// System.out.println(detailsVOList);
			if (detailsVOList != null) {
				String entrustType = "0001".equals(txHeadVOOutput.getHFMTID().trim()) ? "B" : "0002".equals(txHeadVOOutput.getHFMTID().trim()) ? "S" : "0003".equals(txHeadVOOutput.getHFMTID().trim()) ? "C" : "";
				String tradeStatus = "0001".equals(txHeadVOOutput.getHFMTID().trim()) ? "B" : "0002".equals(txHeadVOOutput.getHFMTID().trim()) ? "S" : "0003".equals(txHeadVOOutput.getHFMTID().trim()) ? "C" : "";
				for (NR080NOutputVODetailsVO detailsVO : detailsVOList) {
					CustOrderETFVO cOETFVo = new CustOrderETFVO();
					cOETFVo.setTradeStatus(tradeStatus);
					cOETFVo.setTradeAcct(detailsVO.getTradeAcct());
					cOETFVo.setTrustAcct(detailsVO.getTrustAcct());
					cOETFVo.setTrustType(detailsVO.getTrustType());
					cOETFVo.setInsuranceNo(detailsVO.getInsuranceNo());
					cOETFVo.setEntrustAmt(BigDecimal.valueOf(Long.parseLong(detailsVO.getEntrustAmt() == null || "".equals(detailsVO.getEntrustAmt().trim()) ? "0" : detailsVO.getEntrustAmt().trim())));
					cOETFVo.setCurCode(detailsVO.getCurCode());
					cOETFVo.setEntrustDate(detailsVO.getEntrustDate() == null || "".equals(detailsVO.getEntrustDate().trim()) ? null : sdfYYYYMMDD.parse(detailsVO.getEntrustDate()));
					cOETFVo.setTradePrice(BigDecimal.valueOf(Long.parseLong(detailsVO.getTradePrice() == null || "".equals(detailsVO.getTradePrice().trim()) ? "0" : "".equals(detailsVO.getTradePrice().trim()) ? "0" : detailsVO.getTradePrice())).divide(new BigDecimal(1000000)).setScale(6));

					cOETFVo.setTradeCost(BigDecimal.valueOf(Long.parseLong(detailsVO.getTradeCost() == null || "".equals(detailsVO.getTradeCost().trim()) ? "0" : detailsVO.getTradeCost().trim())).divide(new BigDecimal(1000000)).setScale(6));
					cOETFVo.setEntrustCur(detailsVO.getEntrustCur());
					cOETFVo.setInsuranceName(detailsVO.getInsuranceName());
					cOETFVo.setState(detailsVO.getState());
					cOETFVo.setTradeAmt(BigDecimal.valueOf(Long.parseLong(detailsVO.getTradeAmt() == null || "".equals(detailsVO.getTradeAmt().trim()) ? "0" : detailsVO.getTradeAmt().trim())));
					cOETFVo.setEntrustNo(BigDecimal.valueOf(Long.parseLong(detailsVO.getEntrustNo() == null || "".equals(detailsVO.getEntrustNo().trim()) ? "0" : detailsVO.getEntrustNo().trim())));
					cOETFVo.setCanceled(detailsVO.getCanceled());
					cOETFVo.setTrxMarket(detailsVO.getTrxMarket());
					cOETFVo.setEntrustPrice(new BigDecimal(detailsVO.getEntrustPrice() == null || "".equals(detailsVO.getEntrustPrice().trim()) ? "0" : detailsVO.getEntrustPrice().trim()).divide(new BigDecimal(1000000)).setScale(6));
					cOETFVo.setTrxMarketDat(detailsVO.getTrxMarketDat() == null ? null : sdfYYYYMMDD.parse(detailsVO.getTrxMarketDat()));
					cOETFVo.setCostAcc(detailsVO.getCostAcc());
					cOETFVo.setCreditLoadAmt(BigDecimal.valueOf(Long.parseLong(detailsVO.getCreditLoadAmt() == null || "".equals(detailsVO.getCreditLoadAmt().trim()) ? "0" : detailsVO.getCreditLoadAmt().trim())).divide(new BigDecimal(1000000)).setScale(6));
					cOETFVo.setOrderType(detailsVO.getOrderType());
					cOETFVo.setProductType(detailsVO.getProductType());
					cOETFVo.setEntrustCancelDate(detailsVO.getEntrustCancelDate() == null || "".equals(detailsVO.getEntrustCancelDate().trim()) ? null : sdfYYYYMMDD.parse(detailsVO.getEntrustCancelDate()));
					cOETFVo.setTradeDateEnd(detailsVO.getTradeDateEnd() == null || "".equals(detailsVO.getTradeDateEnd().trim()) ? null : sdfYYYYMMDD.parse(detailsVO.getTradeDateEnd()));
					cOETFVo.setCountType(detailsVO.getCountType());
					cOETFVo.setEntrustType("0003".equals(txHeadVOOutput.getHFMTID().trim()) ? detailsVO.getEntrustType() : entrustType);
					cOETFVo.setTradeAcct(detailsVO.getTradeAcct());
					cOETFVo.setTrustAcct(detailsVO.getTrustAcct());
					// System.out.println(detailsVO.getInsuranceName()+":"+detailsVO.getEntrustPrice());
					cOETFVos.add(cOETFVo);
				}
			}

		}
		return cOETFVos;
	}

	/**
	 * 海外ETF/股票委託交易資料查詢 發送電文NR074N 需傳入參數：custId
	 */
	public List<CustFillETFVO> getCustFillETFData(Object body) throws Exception {
		SOT705InputVO inputVO = (SOT705InputVO) body;
		String custId = inputVO.getCustId();

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NR074N_DATA);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());
		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		// body
		NR074NInputVO nr074nInputVO = new NR074NInputVO();
		nr074nInputVO.setCustId(custId);
		nr074nInputVO.setCurAcctName("WMSG");

		esbUtilInputVO.setNr074nInputVO(nr074nInputVO);
		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		List<CustFillETFVO> cFETFVos = new ArrayList();
		System.out.println("start for:" + vos.size());
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NR074NOutputVO nr074nOutputVO = esbUtilOutputVO.getNr074nOutputVO();
			// 若有回傳錯誤訊息ERR_COD,則throw exception，帶出錯誤碼及錯誤訊息
			throwError(nr074nOutputVO);
			TxHeadVO txHeadVOOutput = esbUtilOutputVO.getTxHeadVO();
			List<NR074NOutputVODetailsVO> detailVOList = nr074nOutputVO.getDetails();

			if (detailVOList != null) {
				for (NR074NOutputVODetailsVO detailVO : detailVOList) {
					String entrustType = "0001".equals(txHeadVOOutput.getHFMTID().trim()) ? "B" : "0002".equals(txHeadVOOutput.getHFMTID().trim()) ? "S" : "";
					CustFillETFVO cFETFVo = new CustFillETFVO();
					cFETFVo.setProdcutType("E".equals(detailVO.getProdcutType().trim()) ? "ETF" : "S".equals(detailVO.getProdcutType().trim()) ? "STOCK" : detailVO.getProdcutType());
					cFETFVo.setTrxMarket(detailVO.getTrxMarket());
					cFETFVo.setInsuranceNo(detailVO.getInsuranceNo());
					cFETFVo.setCurCode(detailVO.getCurCode());
					cFETFVo.setState(detailVO.getState());
					cFETFVo.setEntrustCur(detailVO.getEntrustCur());
					cFETFVo.setResult(detailVO.getResult());
					cFETFVo.setEntrustDate(detailVO.getEntrustDate() == null || "".equals(detailVO.getEntrustDate().trim()) ? null : sdfYYYYMMDD.parse(detailVO.getEntrustDate()));
					cFETFVo.setTradeDate(detailVO.getTradeDate() == null || "".equals(detailVO.getTradeDate().trim()) ? null : sdfYYYYMMDD.parse(detailVO.getTradeDate()));
					cFETFVo.setEntrustAmt(BigDecimal.valueOf(Long.parseLong(detailVO.getEntrustAmt() == null || "".equals(detailVO.getEntrustAmt().trim()) ? "0" : detailVO.getEntrustAmt().trim())));
					cFETFVo.setEntrustPrice(BigDecimal.valueOf(Long.parseLong(detailVO.getEntrustPrice() == null || "".equals(detailVO.getEntrustPrice().trim()) ? "0" : detailVO.getEntrustPrice().trim())).divide(new BigDecimal(1000000)).setScale(6));

					cFETFVo.setChannelType("".equals(detailVO.getChannelType().trim()) ? "S" : detailVO.getChannelType().trim());
					cFETFVo.setTrustType(detailVO.getTrustType());
					cFETFVo.setTrustAcct(detailVO.getTrustAcct());
					cFETFVo.setTradeAmt(BigDecimal.valueOf(Long.parseLong(detailVO.getTradeAmt() == null || "".equals(detailVO.getTradeAmt().trim()) ? "0" : detailVO.getTradeAmt().trim())));
					cFETFVo.setTradePrice(BigDecimal.valueOf(Long.parseLong(detailVO.getTradePrice() == null || "".equals(detailVO.getTradePrice().trim()) ? "0" : detailVO.getTradePrice().trim())).divide(new BigDecimal(1000000)).setScale(6));
					cFETFVo.setTradeCost(BigDecimal.valueOf(Long.parseLong(detailVO.getTradeCost() == null || "".equals(detailVO.getTradeCost().trim()) ? "0" : detailVO.getTradeCost().trim())).divide(new BigDecimal(1000000)).setScale(6));
					cFETFVo.setProductName(detailVO.getProductName());
					cFETFVo.setTradeDateEnd(detailVO.getTradeDateEnd() == null ? null : sdfYYYYMMDD.parse(detailVO.getTradeDateEnd()));
					cFETFVo.setTradeCostAcct(detailVO.getTradeCostAcct());
					cFETFVo.setTradeCostFee(BigDecimal.valueOf(Long.parseLong(detailVO.getTradeCostFee() == null || "".equals(detailVO.getTradeCostFee().trim()) ? "0" : detailVO.getTradeCostFee().trim())).divide(new BigDecimal(100)).setScale(2));
					cFETFVo.setTradeCostOtFee(BigDecimal.valueOf(Long.parseLong(detailVO.getTradeCostOtFee() == null || "".equals(detailVO.getTradeCostOtFee().trim()) ? "0" : detailVO.getTradeCostOtFee().trim())).divide(new BigDecimal(100).setScale(2)));
					cFETFVo.setForCurBal(BigDecimal.valueOf(Long.parseLong(detailVO.getForCurBal() == null || "".equals(detailVO.getForCurBal().trim()) ? "0" : detailVO.getForCurBal().trim())));
					cFETFVo.setCurAmt(BigDecimal.valueOf(Long.parseLong(detailVO.getCurAmt() == null || "".equals(detailVO.getCurAmt().trim()) ? "0" : detailVO.getCurAmt().trim())));
					cFETFVo.setDate08(detailVO.getDate08() == null ? null : sdfYYYYMMDD.parse(detailVO.getDate08()));
					cFETFVo.setOrderType(detailVO.getOrderType());
					cFETFVo.setTradeTrustFee(BigDecimal.valueOf(Long.parseLong(detailVO.getTradeTrustFee() == null ? "0" : detailVO.getTradeTrustFee().trim())).divide(new BigDecimal(100)).setScale(2));
					cFETFVo.setTradeSellAcct(detailVO.getTradeSellAcct());
					cFETFVo.setEntrustType(entrustType);
					cFETFVo.setTradeSellFee(BigDecimal.valueOf(Long.parseLong(detailVO.getTradeSellFee() == null || "".equals(detailVO.getTradeSellFee().trim()) ? "0" : detailVO.getTradeSellFee().trim())).divide(new BigDecimal(100).setScale(2)));
					cFETFVo.setTradeSellOtFee(BigDecimal.valueOf(Long.parseLong(detailVO.getTradeSellOtFee() == null || "".equals(detailVO.getTradeSellOtFee().trim()) ? "0" : detailVO.getTradeSellOtFee().trim())).divide(new BigDecimal(100).setScale(2)));
					cFETFVos.add(cFETFVo);
				}
			}
		}
		return cFETFVos;
	}
	
	/**
	 * 金錢信託_海外ETF/股票產品庫存資料查詢 for js client
	 * 使用電文: NMVP9A
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getCustAssetETFMonData(Object body, IPrimitiveMap header) throws Exception {
		SOT705InputVO inputVO = (SOT705InputVO) body;
		sendRtnObject(this.getCustAssetETFMonData(inputVO));
	}
	
	/**
	 * 金錢信託_海外ETF/股票產品庫存資料查詢
	 * 使用電文: NMVP9A
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT705OutputVO getCustAssetETFMonData(SOT705InputVO inputVO) throws Exception {
		sot705OutputVO = new SOT705OutputVO();

		String custID = inputVO.getCustId(); // 客戶ID
		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("未輸入客戶ID");
		}

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_ASSETS_MON);
		esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		// body
		NMVP9AInputVO txBodyVO = new NMVP9AInputVO();
		esbUtilInputVO.setNmvp9aInputVO(txBodyVO);
		txBodyVO.setCUST_ID(custID);

		// 發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);

		// ETF庫存+在途清單
		List<CustAssetETFMonVO> assetList = new ArrayList<CustAssetETFMonVO>();
		
		for (ESBUtilOutputVO vo : esbUtilOutputVO) {
			NMVP9AOutputVO nmvp9aOutputVO = vo.getNmvp9aOutputVO();
			List<NMVP9AOutputDetailsVO> details = nmvp9aOutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NMVP9AOutputDetailsVO>() : details;
			
			for(NMVP9AOutputDetailsVO item : details) {
				CustAssetETFMonVO asset = new CustAssetETFMonVO();				
				asset.setAccountNo(item.getAccountNo().trim());
				asset.setAvlStockNum(new EsbUtil().decimalPoint(item.getAvlStockNum(), 4));
				asset.setContractId(item.getContractId().trim());
				asset.setCurrency(item.getCurrency().trim());
				asset.setExtProdId(item.getExtProdId().trim());
				asset.setProdId(item.getProdId().trim());
				asset.setStockNum(new EsbUtil().decimalPoint(item.getStockNum(), 4));
				Map<String, String> prdMap = getETFStockName(item.getExtProdId().trim());
				asset.setPrdType(StringUtils.isBlank(prdMap.get("ETF_NAME")) ? "S" : "E");
				asset.setProdName(StringUtils.isBlank(prdMap.get("ETF_NAME")) ? prdMap.get("STOCK_NAME") : prdMap.get("ETF_NAME"));
				
				assetList.add(asset);
			}
		}

		// 回復庫存查詢資料
		sot705OutputVO.setCustAssetETFMonList(assetList);
		return sot705OutputVO;
	}

	/***
	 * 回傳金錢信託庫存是ETF還是Stock
	 * @param prodId
	 * @return S: STOCK E: ETF
	 * @throws JBranchException
	 */
	public Map<String, String> getETFStockName(String prodId) throws JBranchException {
		dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		Map<String, String> map = new HashMap<String, String>();
		
		String sql = "SELECT ETF_CNAME FROM TBPRD_ETF WHERE PRD_ID = :prodId ";		
		condition.setObject("prodId", prodId);
		condition.setQueryString(sql);
		List<Map<String, Object>> list = dam.exeQuery(condition);
		map.put("ETF_NAME", (String) (CollectionUtils.isEmpty(list) ? "" : org.apache.commons.lang.ObjectUtils.toString(list.get(0).get("ETF_CNAME"))));
		
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = "SELECT STOCK_CNAME FROM TBPRD_STOCK WHERE PRD_ID = :prodId ";		
		condition.setObject("prodId", prodId);
		condition.setQueryString(sql);
		list = dam.exeQuery(condition);
		map.put("STOCK_NAME", (String) (CollectionUtils.isEmpty(list) ? "" : org.apache.commons.lang.ObjectUtils.toString(list.get(0).get("STOCK_CNAME"))));
		
		return map;		
	}
	
}
