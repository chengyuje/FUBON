package com.systex.jbranch.app.server.fps.sot710;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_BALANCE_QUERY;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_BEST_FEE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_DEFAULT_FEE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_PERIOD_BARGAIN;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_PERIOD_FEE_RATE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SINGLE_BARGAIN;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SINGLE_FEE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_SINGLE_FEE_RATE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr070n.NR070NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr070n.NR070NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr070n.NR070NOutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva1.NRBRVA1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva1.NRBRVA1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva2.NRBRVA2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva2.NRBRVA2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva3.NRBRVA3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva3.NRBRVA3OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrva3.NRBRVA3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc1.NRBRVC1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc1.NRBRVC1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc2.NRBRVC2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc2.NRBRVC2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc3.NRBRVC3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc3.NRBRVC3OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc3.NRBRVC3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc4.NRBRVC4InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc4.NRBRVC4OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrvc4.NRBRVC4OutputVO;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * sot710
 * 
 * @author ocean
 * @date 2016/09/19
 * @spec 海外ETF_股票手續費&優專方式
 *
 * @version 2016/10/19 Sebastian; 將前後端使用方法拆開 增加for js client使用之接口
 *          {@link SOT710#getPeriodFeeRate(Object, IPrimitiveMap)}
 * 
 *          2016/11/04 revised by Cathy 新增PeriodFeeRateVO & SingleFeeRateVO
 *          getFeeTypeData, getPeriodFeeRate, getSingleFeeRate電文回傳資料需要逐筆取得正確資料型態
 *
 * @version 2016/12/28 Sebastian add method singleBargainModify
 * 
 */
@Component("sot710")
@Scope("request")
public class SOT710 extends EsbUtil {
	@Autowired
	private CBSService cbsservice;
	private DataAccessManager dam = null;

	private SOT710InputVO sot710InputVO;
	private SOT710OutputVO sot710OutputVO;

	private EsbUtil esbUtil = new EsbUtil();

	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfHHMMSS = new SimpleDateFormat("HHmmss");

	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;

	/**
	 * 錯誤檢核
	 *
	 * @param obj
	 * @return
	 * @throws JBranchException
	 */
	private SOT710OutputVO checkError(Object obj) throws JBranchException {

		sot710OutputVO = new SOT710OutputVO();

		String errCode = null;
		String errTxt = null;
		String txFlag = "Y";
		Boolean isErr = Boolean.FALSE;

		if (obj instanceof NRBRVC2OutputVO) {
			errCode = ((NRBRVC2OutputVO) obj).getERR_COD();
			errTxt = ((NRBRVC2OutputVO) obj).getERR_TXT();
			txFlag = ((NRBRVC2OutputVO) obj).getTX_FLG();
		} else if (obj instanceof NRBRVA1OutputVO) {
			errCode = ((NRBRVA1OutputVO) obj).getERR_COD();
			errTxt = ((NRBRVA1OutputVO) obj).getERR_TXT();
		} else if (obj instanceof NRBRVA2OutputVO) {
			errCode = ((NRBRVA2OutputVO) obj).getERR_COD();
			errTxt = ((NRBRVA2OutputVO) obj).getERR_TXT();
		} else if (obj instanceof NRBRVA3OutputVO) {
			errCode = ((NRBRVA3OutputVO) obj).getERR_COD();
			errTxt = ((NRBRVA3OutputVO) obj).getERR_TXT();
		} else if (obj instanceof NRBRVC1OutputVO) {
			errCode = ((NRBRVC1OutputVO) obj).getERR_COD();
			errTxt = ((NRBRVC1OutputVO) obj).getERR_TXT();
		} else if (obj instanceof NRBRVC3OutputVO) {
			errCode = ((NRBRVC3OutputVO) obj).getERR_COD();
			errTxt = ((NRBRVC3OutputVO) obj).getERR_TXT();
		} else if (obj instanceof NRBRVC4OutputVO) {
			errCode = ((NRBRVC4OutputVO) obj).getERR_COD();
			errTxt = ((NRBRVC4OutputVO) obj).getERR_TXT();
		}

		if (StringUtils.isNotBlank(errCode) && !StringUtils.equals("0000", errCode)) {
			sot710OutputVO.setErrorCode(errCode);
			sot710OutputVO.setErrorTxt(errTxt);

			isErr = Boolean.TRUE;

		}

		return sot710OutputVO;
	}

	/**
	 * 海外ETF/股票表定手續費率資料查詢
	 *
	 * 使用電文: NRBRVA1
	 *
	 * @param body
	 * @return NRBRVA1InputVO
	 */
	public void getDefaultFeeRate(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getDefaultFeeRate(body));
	}

	public SOT710OutputVO getDefaultFeeRate(Object body) throws Exception {

		sot710InputVO = (SOT710InputVO) body;
		sot710OutputVO = new SOT710OutputVO();

		String trustAcct = sot710InputVO.getTrustAcct();
		String prodID = sot710InputVO.getProdId();
		String trustCurrType = sot710InputVO.getTrustCurrType();

		// 電文代號
		String htxtid = ETF_DEFAULT_FEE;

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		if (StringUtils.isBlank(trustAcct) || StringUtils.isBlank(prodID) || StringUtils.isBlank(trustCurrType)) {
			throw new JBranchException("遺漏必要欄位");
		}

		//body
		NRBRVA1InputVO nrbrva1InputVO = new NRBRVA1InputVO();
		nrbrva1InputVO.setTRUST_ACCT_BRANCH(cbsservice.getAcctBra(sot710InputVO.getCustID(), trustAcct)); //FOR CBS改帳號分行抓取範圍
		nrbrva1InputVO.setACCT_NO1(trustAcct);
		nrbrva1InputVO.setPROD_ID(prodID);
		nrbrva1InputVO.setTRUST_CURR_TYPE(trustCurrType);
		esbUtilInputVO.setNrbrva1InputVO(nrbrva1InputVO);

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NRBRVA1OutputVO nrbrva1OutputVO = esbUtilOutputVO.getNrbrva1OutputVO();

			//若回傳錯誤，放入outputVO中
			//        	Boolean isErr = checkError(nrbrva1OutputVO);
			sot710OutputVO = checkError(nrbrva1OutputVO);

			sot710OutputVO.setDefaultFeeRate(esbUtil.decimalPoint(nrbrva1OutputVO.getDEFAULT_FEE_RATE(), 5));
		}

		return sot710OutputVO;
	}

	/**
	 * 海外ETF/股票最優手續費率資料查詢
	 *
	 * 使用電文: NRBRVA2
	 *
	 * @param body
	 * @return NRBRVA2InputVO
	 */
	public void getBestFeeRate(Object body) throws Exception {

		sot710InputVO = (SOT710InputVO) body;
		sot710OutputVO = new SOT710OutputVO();

		if (null != sot710InputVO.getUnitNum() && null != sot710InputVO.getDefaultFeeRate()) {
			String custId = sot710InputVO.getCustID();
			String trustAcct = sot710InputVO.getTrustAcct();
			String tradeSubType = sot710InputVO.getBuySell();
			String prodId = sot710InputVO.getProdId();
			String unitNum = String.valueOf(esbUtil.decimalPadding(sot710InputVO.getUnitNum(), 0));
			//String entrustType = sot710InputVO.getEntrustType();
			String entrustAmt = String.valueOf(esbUtil.decimalPadding(sot710InputVO.getEntrustAmt(), 6));
			String dueDate = sdfYYYYMMDD.format(sot710InputVO.getDueDate());
			String trustCurrType = sot710InputVO.getTrustCurrType();
			String defaultFeeRate = String.valueOf(esbUtil.decimalPadding(sot710InputVO.getDefaultFeeRate(), 5));

			// 電文代號
			String htxtid = ETF_BEST_FEE;

			//init util
			ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
			esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());

			//head
			TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
			txHead.setDefaultTxHead();
			esbUtilInputVO.setTxHeadVO(txHead);

			//body
			NRBRVA2InputVO nrbrva2InputVO = new NRBRVA2InputVO();
			nrbrva2InputVO.setCUST_ID(custId);
			if(StringUtils.equals("M", sot710InputVO.getTrustTS())) { //金錢信託
				nrbrva2InputVO.setTRUST_ACCT_BRANCH("999"); //固定放999
				nrbrva2InputVO.setACCT_NO1(""); //固定放空值
			} else { //特金
				nrbrva2InputVO.setTRUST_ACCT_BRANCH(cbsservice.getAcctBra(sot710InputVO.getCustID(), trustAcct)); //FOR CBS改帳號分行抓取範圍
				nrbrva2InputVO.setACCT_NO1(trustAcct);
			}
			nrbrva2InputVO.setBUYSELL(tradeSubType);
			nrbrva2InputVO.setPROD_ID(prodId);
			nrbrva2InputVO.setBUY_UNIT(unitNum);
			nrbrva2InputVO.setENTRUST_TYPE(entrustAmt);
			nrbrva2InputVO.setDUE_DATE(dueDate);
			nrbrva2InputVO.setDEFAULT_FEE_RATE(defaultFeeRate);
			nrbrva2InputVO.setTRUST_CURR_TYPE(trustCurrType);
			esbUtilInputVO.setNrbrva2InputVO(nrbrva2InputVO);
			//發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NRBRVA2OutputVO nrbrva2OutputVO = esbUtilOutputVO.getNrbrva2OutputVO();

				//若回傳錯誤，放入outputVO中
				//            	Boolean isErr = checkError(nrbrva2OutputVO);
				sot710OutputVO = checkError(nrbrva2OutputVO);

				sot710OutputVO.setBestFeeRate(StringUtil.isEmpty(nrbrva2OutputVO.getDEFAULT_BEST_FEE_RATE()) ? null : esbUtil.decimalPoint(nrbrva2OutputVO.getDEFAULT_BEST_FEE_RATE(), 5));
			}
		} else {
			throw new JBranchException("遺漏必要欄位");
		}

		sendRtnObject(sot710OutputVO);
	}

	/**
	 * 海外ETF/股票事先單次議價手續費率資料查詢
	 *
	 * 使用電文:
	 *
	 * @param body
	 * @return NRBRVA3InputVO
	 */
	public void getFeeTypeData(Object body) throws Exception {

		sot710InputVO = (SOT710InputVO) body;
		sot710OutputVO = new SOT710OutputVO();

		String custID = sot710InputVO.getCustID();

		// 電文代號
		String htxtid = ETF_SINGLE_FEE;

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		if (StringUtils.isBlank(custID)) {
			throw new JBranchException("遺漏必要欄位");
		}

		//body
		NRBRVA3InputVO nrbrva3InputVO = new NRBRVA3InputVO();
		nrbrva3InputVO.setCUST_ID(custID); //客戶ID
		esbUtilInputVO.setNrbrva3InputVO(nrbrva3InputVO);

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NRBRVA3OutputVO nrbrva3OutputVO = esbUtilOutputVO.getNrbrva3OutputVO();

			//回傳單次議價資料
			List<SingleFeeRateVO> singleList = new ArrayList<SingleFeeRateVO>();

			if (!StringUtil.isBlank(nrbrva3OutputVO.getERR_TXT())) {
				//若回傳錯誤，放入outputVO中
				//              Boolean isErr = checkError(nrbrva3OutputVO);
				sot710OutputVO = checkError(nrbrva3OutputVO);
			} else {
				//逐筆讀取單次議價資料
				List<NRBRVA3OutputDetailsVO> details = nrbrva3OutputVO.getDetails();
				details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NRBRVA3OutputDetailsVO>() : details;
				for (NRBRVA3OutputDetailsVO detail : details) {
					//NRBRVA3OutputDetailsVO單次議價資料轉換至SingleFeeRateVO中
					SingleFeeRateVO singleFeeRate = new SingleFeeRateVO();

					singleFeeRate.setProdId(detail.getPROD_ID());
					singleFeeRate.setApplySeq(detail.getAPPLY_SEQ());
					singleFeeRate.setUnit(BigDecimal.valueOf(Long.parseLong(detail.getUNIT())));
					singleFeeRate.setPrice(esbUtil.decimalPoint(detail.getPRICE(), 2));
					singleFeeRate.setFeeRate(esbUtil.decimalPoint(detail.getFEE_RATE(), 5));
					singleFeeRate.setFeeDiscount(esbUtil.decimalPoint(detail.getFEE_DISCOUNT(), 2));
					singleFeeRate.setFee(esbUtil.decimalPoint(detail.getFEE(), 2));
					singleFeeRate.setDiscountType(detail.getDISCOUNT_TYPE());

					singleList.add(singleFeeRate);
				}

				sot710OutputVO.setFeeTypeList(singleList);

			}
		}

		sendRtnObject(sot710OutputVO);
	}

	/**
	 * 海外ETF/股票期間議價申請
	 *
	 * 使用電文: NRBRVC1
	 *
	 * @param body
	 * @return NRBRVC1InputVO
	 */
	public void periodBargainApply(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.periodBargainApply(body));
	}

	public SOT710OutputVO periodBargainApply(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		WorkStation ws = DataManager.getWorkStation(uuid);
		sot710InputVO = (SOT710InputVO) body;
		sot710OutputVO = new SOT710OutputVO();

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT B.*, ");
		sb.append("       CASE WHEN UB.EMP_ID IS NOT NULL THEN UB.BRANCH_NBR ELSE E.DEPT_ID END AS DEPT_ID, ");
		sb.append("       H.AUTH_DATE, ");
		sb.append("       H.AUTH_EMP_ID ");
		sb.append("FROM TBCRM_BRG_APPLY_PERIOD B ");
		sb.append("LEFT OUTER JOIN TBORG_MEMBER E ON E.EMP_ID = B.CREATOR ");
		sb.append("LEFT OUTER JOIN TBORG_UHRM_BRH UB ON E.EMP_ID = UB.EMP_ID ");
		sb.append("LEFT OUTER JOIN TBCRM_BRG_APPROVAL_HISTORY H ON H.APPLY_SEQ = B.APPLY_SEQ AND H.APPROVAL_SEQ = (SELECT MAX(APPROVAL_SEQ) FROM TBCRM_BRG_APPROVAL_HISTORY WHERE APPLY_SEQ = :applySeq) ");
		sb.append("WHERE B.APPLY_SEQ = :applySeq ");
		queryCondition.setObject("applySeq", sot710InputVO.getApplySEQ());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		// 電文代號
		String htxtid = ETF_PERIOD_BARGAIN;

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		//body
		NRBRVC1InputVO nrbrvc1InputVO = new NRBRVC1InputVO();
		if (list.size() > 0) {

			Date lastupdate = sdfdate.parse(list.get(0).get("LASTUPDATE").toString());
			String lastupdate_time = list.get(0).get("LASTUPDATE").toString().substring(11, 19).replace(":", "");

			nrbrvc1InputVO.setEMP_ID(StringUtils.equals("3", sot710InputVO.getCheckCode()) ? (String) list.get(0).get("AUTH_EMP_ID") : (String) getCommonVariable(FubonSystemVariableConsts.LOGINID));
			nrbrvc1InputVO.setCONFIRM(sot710InputVO.getCheckCode());
			nrbrvc1InputVO.setCUST_ID((String) list.get(0).get("CUST_ID"));

			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNbr = (String) list.get(0).get("DEPT_ID");
			if (StringUtils.equals(branchNbr, branchChgMap.get("BS").toString())) {
				branchNbr = branchChgMap.get("DEFAULT").toString();
			}
			nrbrvc1InputVO.setBRANCH_NBR(branchNbr);

			nrbrvc1InputVO.setAUTH_EMP_ID((null != list.get(0).get("AUTH_EMP_ID") ? (String) list.get(0).get("AUTH_EMP_ID") : (((String) list.get(0).get("APPLE_SETUP_TYPE")).equals("9") ? (String) list.get(0).get("CREATOR") : null)));
			nrbrvc1InputVO.setAUTH_DATE((null != list.get(0).get("AUTH_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(0).get("AUTH_DATE")) : (StringUtils.equals(list.get(0).get("HIGHEST_AUTH_LV").toString(), "0") ? this.toChineseYearMMdd(lastupdate) : null)));
			nrbrvc1InputVO.setAUTH_TIME((null != list.get(0).get("AUTH_DATE") ? sdfHHMMSS.format((Timestamp) list.get(0).get("AUTH_DATE")) : (StringUtils.equals(list.get(0).get("HIGHEST_AUTH_LV").toString(), "0") ? lastupdate_time : null)));

			nrbrvc1InputVO.setBRG_BEGIN_DATE((null != list.get(0).get("BRG_BEGIN_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(0).get("BRG_BEGIN_DATE")) : null));
			nrbrvc1InputVO.setBRG_END_DATE((null != list.get(0).get("BRG_END_DATE") ? sdfYYYYMMDD.format((Timestamp) list.get(0).get("BRG_END_DATE")) : null));
			nrbrvc1InputVO.setBRG_REASON((String) list.get(0).get("BRG_REASON"));
			nrbrvc1InputVO.setBUY_HK_MRK(esbUtil.decimalPadding((BigDecimal) list.get(0).get("BUY_HK_MRK"), 2));
			nrbrvc1InputVO.setBUY_US_MRK(esbUtil.decimalPadding((BigDecimal) list.get(0).get("BUY_US_MRK"), 2));
			nrbrvc1InputVO.setBUY_UK_MRK(esbUtil.decimalPadding((BigDecimal) list.get(0).get("BUY_UK_MRK"), 2));
			nrbrvc1InputVO.setBUY_JP_MRK(esbUtil.decimalPadding((BigDecimal) list.get(0).get("BUY_JP_MRK"), 2));
			nrbrvc1InputVO.setSELL_HK_MRK(esbUtil.decimalPadding((BigDecimal) list.get(0).get("SELL_HK_MRK"), 2));
			nrbrvc1InputVO.setSELL_US_MRK(esbUtil.decimalPadding((BigDecimal) list.get(0).get("SELL_US_MRK"), 2));
			nrbrvc1InputVO.setSELL_UK_MRK(esbUtil.decimalPadding((BigDecimal) list.get(0).get("SELL_UK_MRK"), 2));
			nrbrvc1InputVO.setSELL_JP_MRK(esbUtil.decimalPadding((BigDecimal) list.get(0).get("SELL_JP_MRK"), 2));
			nrbrvc1InputVO.setAPPLY_DATE(sdfYYYYMMDD.format((Timestamp) list.get(0).get("CREATETIME")));
			nrbrvc1InputVO.setAPPLY_TIME(sdfHHMMSS.format((Timestamp) list.get(0).get("CREATETIME")));

			esbUtilInputVO.setNrbrvc1InputVO(nrbrvc1InputVO);

			//發送電文
			List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

			for (ESBUtilOutputVO esbUtilOutputVO : vos) {
				NRBRVC1OutputVO nrbrvc1OutputVO = esbUtilOutputVO.getNrbrvc1OutputVO();

				//若回傳錯誤，放入outputVO中
				//	            if (checkError(nrbrvc1OutputVO)) break;
				sot710OutputVO = checkError(nrbrvc1OutputVO);
			}
		}

		return sot710OutputVO;
	}

	/**
	 * 海外ETF/股票單次議價申請
	 *
	 * 使用電文: NRBRVC2
	 *
	 * @param body
	 * @return NRBRVC2InputVO
	 */
	public void singleBargainApply(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.singleBargainApply(body));
	}

	public SOT710OutputVO singleBargainApply(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot710InputVO = (SOT710InputVO) body;
		sot710OutputVO = new SOT710OutputVO();
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT B.*, E.DEPT_ID, H.AUTH_DATE, H.AUTH_EMP_ID ");
		sb.append(" FROM TBCRM_BRG_APPLY_SINGLE B ");
		sb.append("   LEFT OUTER JOIN TBORG_MEMBER E ON E.EMP_ID = B.CREATOR ");
		sb.append("   LEFT OUTER JOIN TBCRM_BRG_APPROVAL_HISTORY H ON H.APPLY_SEQ = B.APPLY_SEQ ");
		sb.append("      AND H.APPROVAL_SEQ = (SELECT MAX(APPROVAL_SEQ) FROM TBCRM_BRG_APPROVAL_HISTORY WHERE APPLY_SEQ = :applySeq) ");
		sb.append(" WHERE B.APPLY_SEQ = :applySeq ");
		queryCondition.setObject("applySeq", sot710InputVO.getApplySEQ());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		// 電文代號
		String htxtid = ETF_SINGLE_BARGAIN;

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		//body
		NRBRVC2InputVO nrbrvc2InputVO = new NRBRVC2InputVO();
		if (list.size() > 0) {
			nrbrvc2InputVO.setAPPLY_SEQ((String) list.get(0).get("APPLY_SEQ"));
			nrbrvc2InputVO.setEMP_ID((String) list.get(0).get("MODIFIER"));
			nrbrvc2InputVO.setCONFIRM(sot710InputVO.getCheckCode());
			nrbrvc2InputVO.setCUST_ID((String) list.get(0).get("CUST_ID"));

			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNbr = (String) list.get(0).get("DEPT_ID");
			if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
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
			nrbrvc2InputVO.setBRANCH_NBR(branchNbr);

			String applyType = CharUtils.toString((Character) list.get(0).get("APPLY_TYPE"));
			//nrbrvc2InputVO.setBUYSELL("4".equals(applyType) ? "B" : ("5".equals(applyType) ? "S" : ""));
			nrbrvc2InputVO.setPROD_ID((String) list.get(0).get("PROD_ID"));
			nrbrvc2InputVO.setTRADE_DATE(sdfYYYYMMDD.format((Timestamp) list.get(0).get("LASTUPDATE")));
			nrbrvc2InputVO.setUNIT(esbUtil.decimalPadding((BigDecimal) list.get(0).get("ENTRUST_UNIT"), 0));
			nrbrvc2InputVO.setPRICE(esbUtil.decimalPadding((BigDecimal) list.get(0).get("ENTRUST_AMT"), 2));
			nrbrvc2InputVO.setFEE_RATE(esbUtil.decimalPadding((BigDecimal) list.get(0).get("FEE_RATE"), 5));
			nrbrvc2InputVO.setFEE_DISCOUNT(esbUtil.decimalPadding((BigDecimal) list.get(0).get("FEE_DISCOUNT"), 2));
			nrbrvc2InputVO.setFEE(esbUtil.decimalPadding((BigDecimal) list.get(0).get("FEE"), 2));
			nrbrvc2InputVO.setTRUST_CURR_TYPE(((Character) list.get(0).get("TRUST_CURR_TYPE")).toString());
			nrbrvc2InputVO.setDISCOUNT_TYPE(((Character) list.get(0).get("DISCOUNT_TYPE")).toString());
			nrbrvc2InputVO.setAPPLY_DATE(sdfYYYYMMDD.format((Timestamp) list.get(0).get("LASTUPDATE")));
			nrbrvc2InputVO.setAPPLY_TIME(sdfHHMMSS.format((Timestamp) list.get(0).get("LASTUPDATE")));
			
			// 無覆核資訊 AND CONFIRM=覆核 AND 該申請為高端 則客製覆核資訊 BY OCEAN -> 君榮要求 202112
			nrbrvc2InputVO.setAUTH_EMP_ID((null != list.get(0).get("AUTH_EMP_ID") ? (String) list.get(0).get("AUTH_EMP_ID")                      : ((((String) list.get(0).get("APPLE_SETUP_TYPE")).equals("9") && StringUtils.equals(sot710InputVO.getCheckCode(), "3")) ? (String) list.get(0).get("C_BRANCH_NBR") + (String) list.get(0).get("CREATOR") : null)));
			nrbrvc2InputVO.setAUTH_DATE  ((null != list.get(0).get("AUTH_DATE"))  ? sdfYYYYMMDD.format((Timestamp) list.get(0).get("AUTH_DATE")) : ((((String) list.get(0).get("APPLE_SETUP_TYPE")).equals("9") && StringUtils.equals(sot710InputVO.getCheckCode(), "3")) ? sdfYYYYMMDD.format((Timestamp) list.get(0).get("LASTUPDATE"))                  : null));
			nrbrvc2InputVO.setAUTH_TIME  ((null != list.get(0).get("AUTH_DATE"))  ? sdfHHMMSS.format((Timestamp) list.get(0).get("AUTH_DATE"))   : ((((String) list.get(0).get("APPLE_SETUP_TYPE")).equals("9") && StringUtils.equals(sot710InputVO.getCheckCode(), "3")) ? sdfHHMMSS.format((Timestamp) list.get(0).get("LASTUPDATE"))                    : null));

			esbUtilInputVO.setNrbrvc2InputVO(nrbrvc2InputVO);
		}

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NRBRVC2OutputVO nrbrvc2OutputVO = esbUtilOutputVO.getNrbrvc2OutputVO();

			//若回傳錯誤，放入outputVO中
			//          if (checkError(nrbrvc2OutputVO)) break;
			sot710OutputVO = checkError(nrbrvc2OutputVO);
		}

		return sot710OutputVO;
	}

	/**
	 * 海外ETF/股票期間議價查詢 for js client
	 *
	 * 使用電文: NRBRVC3
	 *
	 * @param body
	 * @return NRBRVC3InputVO
	 */
	public void getPeriodFeeRate(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getPeriodFeeRate(body));
	}

	/**
	 * 海外ETF/股票期間議價查詢
	 *
	 * 使用電文: NRBRVC3
	 *
	 * @param body
	 * @return NRBRVC3InputVO
	 */
	public SOT710OutputVO getPeriodFeeRate(Object body) throws Exception {

		sot710InputVO = (SOT710InputVO) body;
		sot710OutputVO = new SOT710OutputVO();

		String custID = sot710InputVO.getCustID();
		String empID = sot710InputVO.getEmpID();
		Date startDate = sot710InputVO.getStartDate();
		Date endDate = sot710InputVO.getEndDate();

		// 電文代號
		String htxtid = ETF_PERIOD_FEE_RATE;

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		//body
		NRBRVC3InputVO nrbrvc3InputVO = new NRBRVC3InputVO();
		nrbrvc3InputVO.setCUST_ID(custID);
		//        nrbrvc3InputVO.setEMP_ID(empID);	目前不須傳送EMP_ID欄位，才會回傳全部資料
		if (null != startDate)
			nrbrvc3InputVO.setAPPLY_BEGIN_DATE(sdfYYYYMMDD.format(startDate));
		if (null != endDate)
			nrbrvc3InputVO.setAPPLY_END_DATE(sdfYYYYMMDD.format(endDate));
		esbUtilInputVO.setNrbrvc3InputVO(nrbrvc3InputVO);

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		//回傳期間議價資料
		List<PeriodFeeRateVO> periodList = new ArrayList<PeriodFeeRateVO>();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NRBRVC3OutputVO nrbrvc3OutputVO = esbUtilOutputVO.getNrbrvc3OutputVO();

			if (!StringUtil.isBlank(nrbrvc3OutputVO.getERR_TXT())) {
				//若回傳錯誤，放入outputVO中
				//              Boolean isErr = checkError(nrbrvc3OutputVO);
				sot710OutputVO = checkError(nrbrvc3OutputVO);
			} else {
				//逐筆讀取期間議價資料
				List<NRBRVC3OutputDetailsVO> details = nrbrvc3OutputVO.getDetails();
				details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NRBRVC3OutputDetailsVO>() : details;
				for (NRBRVC3OutputDetailsVO detail : details) {
					//NRBRVC3OutputDetailsVO期間議價資料轉換至PeriodFeeRateVO中
					PeriodFeeRateVO periodFeeRate = new PeriodFeeRateVO();

					periodFeeRate.setAuthEmpId(detail.getAUTH_EMP_ID());
					periodFeeRate.setAuthDate(StringUtils.isBlank(detail.getAUTH_DATE()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getAUTH_DATE()));
					//                  periodFeeRate.setAuthTime(StringUtils.isBlank(detail.getAUTH_TIME()) ? null : new SimpleDateFormat("HHmmss").parse(detail.getAUTH_TIME()));
					periodFeeRate.setAuthDate("00000000".equals(detail.getAUTH_DATE()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getEND_DATE()));
					periodFeeRate.setBrgBeginDate(StringUtils.isBlank(detail.getBRG_BEGIN_DATE()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getBRG_BEGIN_DATE()));
					periodFeeRate.setBrgEndDate(StringUtils.isBlank(detail.getBRG_END_DATE()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getBRG_END_DATE()));
					periodFeeRate.setBrgReason(detail.getBRG_REASON());
					periodFeeRate.setBuyHKMrk(esbUtil.decimalPoint(detail.getBUY_HK_MRK(), 2));
					periodFeeRate.setBuyUSMrk(esbUtil.decimalPoint(detail.getBUY_US_MRK(), 2));  
					periodFeeRate.setBuyUKMrk(esbUtil.decimalPoint(detail.getBUY_UK_MRK(), 2));
					periodFeeRate.setBuyJPMrk(esbUtil.decimalPoint(detail.getBUY_JP_MRK(), 2));
					periodFeeRate.setSellHKMrk(esbUtil.decimalPoint(detail.getSELL_HK_MRK(), 2));
					periodFeeRate.setSellUSMrk(esbUtil.decimalPoint(detail.getSELL_US_MRK(), 2));
					periodFeeRate.setSellUKMrk(esbUtil.decimalPoint(detail.getSELL_UK_MRK(), 2));
					periodFeeRate.setSellJPMrk(esbUtil.decimalPoint(detail.getSELL_JP_MRK(), 2));
					periodFeeRate.setApplyDate(StringUtils.isBlank(detail.getAPPLY_DATE()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getAPPLY_DATE()));
					periodFeeRate.setApplyTime(StringUtils.isBlank(detail.getAPPLY_TIME()) ? null : new SimpleDateFormat("HHmmss").parse(detail.getAPPLY_TIME()));

					/** 2017/11/22 add by Carley增加下行電文判斷已終止的議價 **/
					periodFeeRate.setEndDate("00000000".equals(detail.getEND_DATE()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getEND_DATE()));

					periodList.add(periodFeeRate);
				}

				sot710OutputVO.setPeriodFeeRateList(periodList);
			}
		}

		return sot710OutputVO;
	}

	/**
	 * 海外ETF/股票單次議價查詢
	 *
	 * 使用電文: NRBRVC4
	 *
	 * @param body
	 * @return NRBRVC4InputVO
	 */

	public SOT710OutputVO getSingleFeeRate(Object body) throws Exception {

		sot710InputVO = (SOT710InputVO) body;
		sot710OutputVO = new SOT710OutputVO();

		String custID = sot710InputVO.getCustID();
		String empID = sot710InputVO.getEmpID();
		Date startDate = sot710InputVO.getStartDate();
		Date endDate = sot710InputVO.getEndDate();

		// 電文代號
		String htxtid = ETF_SINGLE_FEE_RATE;

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, htxtid);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		//body
		NRBRVC4InputVO nrbrvc4InputVO = new NRBRVC4InputVO();
		nrbrvc4InputVO.setCUST_ID(custID);
		nrbrvc4InputVO.setEMP_ID(empID);
		nrbrvc4InputVO.setAPPLY_BEGIN_DATE(sdfYYYYMMDD.format(startDate));
		nrbrvc4InputVO.setAPPLY_END_DATE(sdfYYYYMMDD.format(endDate));
		esbUtilInputVO.setNrbrvc4InputVO(nrbrvc4InputVO);

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		//回傳單次議價資料
		List<SingleFeeRateVO> singleList = new ArrayList<SingleFeeRateVO>();

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NRBRVC4OutputVO nrbrvc4OutputVO = esbUtilOutputVO.getNrbrvc4OutputVO();

			if (!StringUtil.isBlank(nrbrvc4OutputVO.getERR_TXT())) {
				//若回傳錯誤，放入outputVO中
				//              Boolean isErr = checkError(nrbrvc4OutputVO);
				sot710OutputVO = checkError(nrbrvc4OutputVO);
			} else {
				//逐筆讀取單次議價資料
				List<NRBRVC4OutputDetailsVO> details = nrbrvc4OutputVO.getDetails();
				details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NRBRVC4OutputDetailsVO>() : details;
				for (NRBRVC4OutputDetailsVO detail : details) {
					//NRBRVC4OutputDetailsVO單次議價資料轉換至SingleFeeRateVO中
					SingleFeeRateVO singleFeeRate = new SingleFeeRateVO();

					singleFeeRate.setApplySeq(detail.getAPPLY_SEQ());
					singleFeeRate.setAuthEmpId(detail.getAUTH_EMP_ID());
					singleFeeRate.setAuthDate(StringUtils.isBlank(detail.getAUTH_DATE()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getAUTH_DATE()));
					singleFeeRate.setAuthTime(StringUtils.isBlank(detail.getAUTH_TIME()) ? null : new SimpleDateFormat("HHmmss").parse(detail.getAUTH_TIME()));
					singleFeeRate.setProdId(detail.getPROD_ID());
					singleFeeRate.setUnit(BigDecimal.valueOf(Long.parseLong(detail.getUNIT())));
					singleFeeRate.setPrice(esbUtil.decimalPoint(detail.getPRICE(), 2));
					singleFeeRate.setFeeRate(esbUtil.decimalPoint(detail.getFEE_RATE(), 5));
					singleFeeRate.setFeeDiscount(esbUtil.decimalPoint(detail.getFEE_DISCOUNT(), 2));
					singleFeeRate.setFee(esbUtil.decimalPoint(detail.getFEE(), 2));
					singleFeeRate.setTrustCurrType(detail.getTRUST_CURR_TYPE());
					singleFeeRate.setSeqUseCode(detail.getSEQ_USECODE());
					singleFeeRate.setApplyDate(StringUtils.isBlank(detail.getAPPLY_DATE()) ? null : new SimpleDateFormat("yyyyMMdd").parse(detail.getAPPLY_DATE()));
					singleFeeRate.setApplyTime(StringUtils.isBlank(detail.getAPPLY_TIME()) ? null : new SimpleDateFormat("HHmmss").parse(detail.getAPPLY_TIME()));
					singleFeeRate.setDiscountType(detail.getDISCOUNT_TYPE());

					singleList.add(singleFeeRate);
				}

				sot710OutputVO.setSingleFeeRateList(singleList);

			}
		}

		return sot710OutputVO;
	}

	/**
	 * 海外ETF/股票單次議價修改
	 *
	 * 使用電文: NRBRVC2 for js client
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void singleBargainModify(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.singleBargainModify(body));
	}

	/**
	 * 海外ETF/股票單次議價修改
	 *
	 * 使用電文: NRBRVC2
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT710OutputVO singleBargainModify(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		SOT710InputVO inputVO = (SOT710InputVO) body;

		String applySeq = inputVO.getApplySEQ();
		String custId = inputVO.getCustID();
		String prodId = inputVO.getProdId();
		String applyType = inputVO.getApplyType();
		BigDecimal entrustUnit = inputVO.getEntrustUnit();
		BigDecimal entrustAmt = inputVO.getEntrustPrice();
		String tradeDate = inputVO.getTradeDate();
		BigDecimal feeRate = inputVO.getFeeRate();
		String trustCurrType = inputVO.getTrustCurrType();
		String discountType = inputVO.getDiscountType();
		BigDecimal feeDiscount = inputVO.getFeeDiscount();

		//String buySell = (StringUtils.isNotBlank(applyType)) ? (StringUtils.equals(applyType, "4")) ? "B" : (StringUtils.equals(applyType, "5")) ? "S" : "" : "";

		//get current date and time
		Date date = new Date();
		String sysdate = this.toChineseYearMMdd(date);
		String systime = new SimpleDateFormat("HHmmss").format(date);

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_SINGLE_BARGAIN);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		//body
		NRBRVC2InputVO nrbrvc2InputVO = new NRBRVC2InputVO();
		nrbrvc2InputVO.setAPPLY_SEQ(applySeq);
		nrbrvc2InputVO.setEMP_ID((String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		nrbrvc2InputVO.setCONFIRM("5");
		nrbrvc2InputVO.setCUST_ID(custId);

		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH);
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
		nrbrvc2InputVO.setBRANCH_NBR(branchNbr);

		//nrbrvc2InputVO.setBUYSELL(buySell);
		nrbrvc2InputVO.setPROD_ID(prodId);
		nrbrvc2InputVO.setTRADE_DATE(tradeDate);
		//      nrbrvc2InputVO.setUNIT(entrustUnit);
		nrbrvc2InputVO.setUNIT(esbUtil.decimalPadding(entrustUnit, 0));
		//      nrbrvc2InputVO.setPRICE(entrustAmt);
		nrbrvc2InputVO.setPRICE(esbUtil.decimalPadding(entrustAmt, 2));
		nrbrvc2InputVO.setFEE_RATE(esbUtil.decimalPadding(feeRate, 5));
		//      nrbrvc2InputVO.setFEE_DISCOUNT(null);
		nrbrvc2InputVO.setFEE_DISCOUNT(esbUtil.decimalPadding(feeDiscount, 2));
		nrbrvc2InputVO.setFEE(null);
		nrbrvc2InputVO.setTRUST_CURR_TYPE(trustCurrType);
		nrbrvc2InputVO.setDISCOUNT_TYPE(discountType);
		nrbrvc2InputVO.setAPPLY_DATE(sysdate);
		nrbrvc2InputVO.setAPPLY_TIME(systime);
		nrbrvc2InputVO.setAUTH_TIME(null);
		esbUtilInputVO.setNrbrvc2InputVO(nrbrvc2InputVO);

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NRBRVC2OutputVO nrbrvc2OutputVO = esbUtilOutputVO.getNrbrvc2OutputVO();

			//          if (checkError(nrbrvc2OutputVO)) break;
			sot710OutputVO = checkError(nrbrvc2OutputVO);
		}

		return sot710OutputVO;
	}

	/**
	 * 海外ETF/股票可用餘額查詢 發送電文NR070N
	 *
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public void getAvailBalance(Object body) throws Exception {
		SOT710InputVO inputVO = (SOT710InputVO) body;
		SOT710OutputVO outputVO = new SOT710OutputVO();
		String custId = inputVO.getCustID();
		String debitAcrSct = inputVO.getDebitAcct(); //扣款帳號

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, ETF_BALANCE_QUERY);
		esbUtilInputVO.setModule(this.getClass().getSimpleName() + "/" + Thread.currentThread().getStackTrace()[1].getMethodName());
		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		// body
		NR070NInputVO nr070nInputVO = new NR070NInputVO();
		nr070nInputVO.setCustId(custId);
		nr070nInputVO.setAcctId16(debitAcrSct);
		esbUtilInputVO.setNr070nInputVO(nr070nInputVO);

		// 發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		List<AvailBalanceVO> abVos = new ArrayList<AvailBalanceVO>();
		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NR070NOutputVO nr070nOutputVO = esbUtilOutputVO.getNr070nOutputVO();
			List<NR070NOutputVODetailsVO> detailsVOs = nr070nOutputVO.getDetails();

			if (detailsVOs != null) {
				for (NR070NOutputVODetailsVO detailsVO : detailsVOs) {
					AvailBalanceVO abvo = new AvailBalanceVO();
					abvo.setOccur(BigDecimal.valueOf(Long.parseLong(nr070nOutputVO.getOccur())));
					abvo.setTradeAcct(detailsVO.getTradeAcct());
					abvo.setTradeCur(detailsVO.getTradeCur());
					abvo.setSellAmt(BigDecimal.valueOf(Long.parseLong(detailsVO.getSellAmt())).divide(new BigDecimal(100).setScale(2)));
					abvo.setTDaySAmt(BigDecimal.valueOf(Long.parseLong(detailsVO.getTDaySAmt())).divide(new BigDecimal(100).setScale(2)));
					abvo.setSellUseAmt(BigDecimal.valueOf(Long.parseLong(detailsVO.getSellUseAmt())).divide(new BigDecimal(100).setScale(2)));
					abvo.setBankUseAmt(BigDecimal.valueOf(Long.parseLong(detailsVO.getBankUseAmt())).divide(new BigDecimal(100).setScale(2)));
					abvo.setTrxMaketCode(detailsVO.getTrxMarketCode());
					abVos.add(abvo);
				}
			}

		}
		outputVO.setAvailBalanceList(abVos);
		sendRtnObject(outputVO);
	}
}