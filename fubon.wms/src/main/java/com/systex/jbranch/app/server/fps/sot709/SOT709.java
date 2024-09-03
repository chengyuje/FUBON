package com.systex.jbranch.app.server.fps.sot709;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NFEE001_DATA;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_DBU;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_DBU_BARGAIN_INQUIRE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_DBU_INTERVAL_BARGAIN_INQUIRE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_DBU_SINGLE_BARGAIN_INQUIRE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_ESB;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_OBU_BARGAIN_INQUIRE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_OBU_INTERVAL_BARGAIN_INQUIRE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_OBU_PROCESSING_FEE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_OBU_SINGLE_BARGAIN_INQUIRE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_PROCESSING_FEE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NF_SINGLE_BARGAIN_INQUIRE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.OBU_NF_SINGLE_BARGAIN_INQUIRE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.XF_PROCESSING_FEE;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712OutputVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.afbrn8.AFBRN8InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.afbrn8.AFBRN8OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn4.NFBRN4InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn4.NFBRN4OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn6.NFBRN6InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn6.NFBRN6OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn7.NFBRN7InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn7.NFBRN7OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn8.NFBRN8InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfbrn8.NFBRN8OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee001.NFEE001InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee001.NFEE001OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee001.NFEE001OutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee002.NFEE002InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee002.NFEE002OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee002.NFEE002OutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee086.NFEE086InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee086.NFEE086OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nfee086.NFEE086OutputVODetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn085n.VN085NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn085n.VN085NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.vn085n.VN085NOutputVODetailsVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("sot709")
@Scope("request")
public class SOT709 extends EsbUtil{
	@Autowired
	CBSService cbsservice;
	@Autowired
	private SOT701 sot701;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private DataAccessManager dam = null;
	private SOT709InputVO sot709InputVO;
	private SOT709OutputVO sot709OutputVO;
    private String thisClaz = this.getClass().getSimpleName()+".";

	/* const */
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	
    /**
     * 基金表定手續費以及最優手續費查詢
     *
     * for js client
     * 使用電文: NFBRN1Z
     *
     * @param body
     * @param header
     */
	public void getDefaultFeeRate (Object body, IPrimitiveMap header) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709InputVO.setBranchNbr(cbsservice.getAcctBra(sot709InputVO.getCustId(),sot709InputVO.getTrustAcct()));
		sot709OutputVO = new SOT709OutputVO();
		sot709OutputVO.setDefaultFeeRates(this.getDefaultFeeRate(sot709InputVO).getDefaultFeeRates());
		sendRtnObject(sot709OutputVO);
	}
    /**
     * 基金表定手續費以及最優手續費查詢
     *
     * 使用電文: NFBRN7(特金)/NFBRX7(金錢信託)
     *
     * @param body
     * @throws Exception
     */
	public SOT709OutputVO getDefaultFeeRate(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		DefaultFeeRateVO defaultfeerate = new DefaultFeeRateVO();
		Date effDate = new Date();
		
		if (sot709InputVO.getTradeDate() == null) {
			SOT712InputVO inputVO_712 = new SOT712InputVO();
			SOT712OutputVO outputVO_712 = new SOT712OutputVO();
			inputVO_712.setProdType("NF");
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");
			outputVO_712 = sot712.getEffDate(inputVO_712);
			effDate  = outputVO_712.getTradeDate();
		} else {
			effDate = sot709InputVO.getTradeDate();
			logger.debug("getDefaultFeeRate() EFF_DATE/709InputVO.tradeDate :" + sot709InputVO.getTradeDate()); //表定手續費生效日期資料為畫面上的交易日期
		}
		String custId = sot709InputVO.getCustId();
		String branchNbr = sot709InputVO.getBranchNbr();
		String trustCurrType = sot709InputVO.getTrustCurrType();
		String tradeSubType = sot709InputVO.getTradeSubType();
		String prodId = sot709InputVO.getProdId();
		//String groupIfa = sot709InputVO.getGroupIfa();
		BigDecimal purchaseAmtL = sot709InputVO.getPurchaseAmtL();
		BigDecimal purchaseAmtM = sot709InputVO.getPurchaseAmtM();
		BigDecimal purchaseAmtH = sot709InputVO.getPurchaseAmtH();
		//String bthCoupon = sot709InputVO.getBthCoupon();
		String autoCx = sot709InputVO.getAutoCx();
		//String bargainApplySeq = sot709InputVO.getBargainApplySeq();
		Date sysdate = new Date();
		boolean isObu = sot701.isObu(custId);
		//init util
		ESBUtilInputVO esbUtilInputVO;
		if(StringUtils.equals(sot709InputVO.getTrustTS(), "M")){
			esbUtilInputVO = getTxInstance(ESB_TYPE, XF_PROCESSING_FEE);
		}else{
			esbUtilInputVO = getTxInstance(ESB_TYPE, isObu? NF_OBU_PROCESSING_FEE : NF_PROCESSING_FEE);
		}
		esbUtilInputVO.setModule("SOT709.getDefaultFeeRate");
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		NFBRN7InputVO nfbrn7InputVO = new NFBRN7InputVO();
		nfbrn7InputVO.setCUST_ID(custId);
		if(!StringUtils.equals(sot709InputVO.getTrustTS(), "M")){
			nfbrn7InputVO.setBRANCH_NBR(branchNbr);
			
		}
		
		nfbrn7InputVO.setTRUST_CURR_TYPE(trustCurrType);
		nfbrn7InputVO.setTRADE_SUB_TYPE("0"+tradeSubType);
//		nfbrn7InputVO.setTRADE_DATE(this.toChineseYearMMdd(sysdate));//格式9(08) 要用民國年,如今日: 2016/10/18 => 01051018
		nfbrn7InputVO.setEFF_DATE(this.toChineseYearMMdd(effDate));
		nfbrn7InputVO.setTRADE_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate()));  //FOR CBS測試日期修改
//		nfbrn7InputVO.setEFF_DATE(cbsservice.toChineseYearMMdd(cbsservice.getCBSTestDate())); //FOR CBS測試日期修改
		nfbrn7InputVO.setPROD_ID(prodId);
		//nfbrn7InputVO.setGROUP_IFA(groupIfa);
		nfbrn7InputVO.setPURCHASE_AMT_L(new EsbUtil().decimalPadding(purchaseAmtL, 2));
		nfbrn7InputVO.setPURCHASE_AMT_M(new EsbUtil().decimalPadding(purchaseAmtM, 2));
		nfbrn7InputVO.setPURCHASE_AMT_H(new EsbUtil().decimalPadding(purchaseAmtH, 2));
		//nfbrn7InputVO.setBTH_COUPON(bthCoupon);
		nfbrn7InputVO.setAUTO_CX(autoCx);
		//nfbrn7InputVO.setBARGAIN_APPLY_SEQ(bargainApplySeq);
		esbUtilInputVO.setNfbrn7InputVO(nfbrn7InputVO);
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for(ESBUtilOutputVO esbUtilOutputVO :vos) {
			NFBRN7OutputVO nfbrn7OutputVO;
			if(StringUtils.equals(sot709InputVO.getTrustTS(), "M")){ 
				nfbrn7OutputVO = esbUtilOutputVO.getNfbrx7OutputVO();//金錢信託
			}else{
				nfbrn7OutputVO = isObu? esbUtilOutputVO.getAfbrn7OutputVO(): esbUtilOutputVO.getNfbrn7OutputVO();//特金信託
			}
			

			if (nfbrn7OutputVO != null) {
				String errCode = nfbrn7OutputVO.getERR_COD();
				String errTxt = nfbrn7OutputVO.getERR_TXT();
	
				if (StringUtils.isNotBlank(errCode)) {
					sot709OutputVO.setErrorCode(errCode);
					sot709OutputVO.setErrorMsg(errTxt);
				} else {
					//將電文傳回 DefaultFeeRateVO
					defaultfeerate.setDefaultFeeRateL(new EsbUtil().decimalPoint(nfbrn7OutputVO.getDEFAULT_FEE_RATE(), 3));
					defaultfeerate.setDefaultFeeRateM(new EsbUtil().decimalPoint(nfbrn7OutputVO.getDEFAULT_FEE_RATE_M(), 3));
					defaultfeerate.setDefaultFeeRateH(new EsbUtil().decimalPoint(nfbrn7OutputVO.getDEFAULT_FEE_RATE_H(), 3));
					defaultfeerate.setFeeL(new EsbUtil().decimalPoint(nfbrn7OutputVO.getFEE(), 2));
					defaultfeerate.setFeeRateL(new EsbUtil().decimalPoint(nfbrn7OutputVO.getFEE_RATE(), 3));
					defaultfeerate.setFeeM(new EsbUtil().decimalPoint(nfbrn7OutputVO.getFEE_M(), 2));
					defaultfeerate.setFeeRateM(new EsbUtil().decimalPoint(nfbrn7OutputVO.getFEE_RATE_M(), 3));
					defaultfeerate.setFeeH(new EsbUtil().decimalPoint(nfbrn7OutputVO.getFEE_H(), 2));
					defaultfeerate.setFeeRateH(new EsbUtil().decimalPoint(nfbrn7OutputVO.getFEE_RATE_H(), 3));
					defaultfeerate.setTrustCurr(nfbrn7OutputVO.getTRUST_CURR());
					defaultfeerate.setGroupOfa(nfbrn7OutputVO.getGROUP_OFA());
					sot709OutputVO.setDefaultFeeRates(defaultfeerate);
				}
			}
		}
		
		return sot709OutputVO;
	}
	
	
	
    /**
     * 基金期間議價查詢
     *
     * for js client
     * 使用電文: NFEE002
     *
     * @param body
     * @param header
     */
	public void getPeriodFeeRate (Object body, IPrimitiveMap header) throws Exception {
		sot709OutputVO = new SOT709OutputVO();
		sot709OutputVO.setPeriodFeeRateList(this.getPeriodFeeRate(body).getPeriodFeeRateList());
		sendRtnObject(sot709OutputVO);
	}	
    /**
     * 基金期間議價查詢
     *
     * 使用電文: NFEE002
     *
     * @param body
     * @return 
     * @throws Exception
     */
	public SOT709OutputVO getPeriodFeeRate(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		
		boolean isObu = sot701.isObu(sot709InputVO.getCustId());
		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, isObu? NF_OBU_BARGAIN_INQUIRE: NF_DBU_BARGAIN_INQUIRE);
		esbUtilInputVO.setModule("SOT709.getPeriodFeeRate");
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		NFEE002InputVO nfee002InputVO = new NFEE002InputVO();
		nfee002InputVO.setCustId(sot709InputVO.getCustId());
		nfee002InputVO.setType(sot709InputVO.getType());
		esbUtilInputVO.setNfee002InputVO(nfee002InputVO);
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		List<PeriodFeeRateVO> feeRateList = new ArrayList<PeriodFeeRateVO>();
		
		for(ESBUtilOutputVO esbUtilOutputVO :vos) {
			NFEE002OutputVO nfee002OutputVO = isObu? esbUtilOutputVO.getAfee002OutputVO(): esbUtilOutputVO.getNfee002OutputVO();

			List<NFEE002OutputVODetailsVO> details = nfee002OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NFEE002OutputVODetailsVO>() : details;

			SimpleDateFormat SDFYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
			for (NFEE002OutputVODetailsVO detailVO : details) {
				
				Date apply_date = new EsbUtil().toAdYearMMdd(detailVO.getAPPLY_DATE(), false);
						
				//議價查詢會有起訖日；下單查詢不會有起訖日，議價效期會以最後日期為主
				if ((sot709InputVO.getStartDate() != null && sot709InputVO.getEndDate() != null && 
						apply_date.getTime() >= sot709InputVO.getStartDate().getTime() &&
						apply_date.getTime() <= sot709InputVO.getEndDate().getTime()) || 
					(sot709InputVO.getStartDate() == null || sot709InputVO.getEndDate() == null)) {
					
					PeriodFeeRateVO feeRate = new PeriodFeeRateVO();
					feeRate.setApplyDate(new EsbUtil().toAdYearMMdd(detailVO.getAPPLY_DATE(), false));
					feeRate.setBrgBeginDate(new EsbUtil().toAdYearMMdd(detailVO.getBRG_BEGIN_DATE(), false));
					feeRate.setBrgEndDate(new EsbUtil().toAdYearMMdd(detailVO.getBRG_END_DATE(), false));
					
					feeRate.setDmtStock(new EsbUtil().decimalPoint(detailVO.getDMT_STOCK(), 3));
					feeRate.setDmtBond(new EsbUtil().decimalPoint(detailVO.getDMT_BOND(), 3));
					feeRate.setDmtBalanced(new EsbUtil().decimalPoint(detailVO.getDMT_BALANCED(), 3));
					feeRate.setFrnStock(new EsbUtil().decimalPoint(detailVO.getFRN_STOCK(), 3));
					feeRate.setFrnBond(new EsbUtil().decimalPoint(detailVO.getFRN_BOND(), 3));
					feeRate.setFrnBalanced(new EsbUtil().decimalPoint(detailVO.getFRN_BALANCED(), 3));
					
					feeRate.setBrgReason(detailVO.getBRG_REASON());
					//覆核日期(不為零表已覆核)
					if (StringUtils.isNotBlank(detailVO.getAUTH_DATE()) && !"00000000".equals(detailVO.getAUTH_DATE())) {
						feeRate.setAuthDate(new EsbUtil().toAdYearMMdd(detailVO.getAUTH_DATE(), false));						
					}
					//終止日期(不為零表已終止)
					if (StringUtils.isNotBlank(detailVO.getTERMINATE_DATE()) && !"00000000".equals(detailVO.getTERMINATE_DATE())) {
						feeRate.setTerminateDate(new EsbUtil().toAdYearMMdd(detailVO.getTERMINATE_DATE(), false));						
					}
					feeRateList.add(feeRate);	
				}
			}

			sot709OutputVO.setPeriodFeeRateList(feeRateList);
		}

		return sot709OutputVO;
	}
	
	
	/**
     * 基金單次議價查詢
     *
     * for js client
     * 使用電文: VN085N
     *
     * @param body
     * @param header
     */
	public void getSingleFeeRateESB (Object body, IPrimitiveMap header) throws Exception {
		sot709OutputVO = new SOT709OutputVO();
		sot709OutputVO.setSingleFeeRateList(this.getSingleFeeRateESB(body).getSingleFeeRateList());
		sendRtnObject(sot709OutputVO);
	}

    /**
     * 基金單次議價查詢
     *
     * 使用電文: VN085N
     *
     * @param body
     * @return 
     * @throws Exception
     */
	public SOT709OutputVO getSingleFeeRateESB(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		
		//init util
		Boolean isOBU = sot701.isObu(sot709InputVO.getCustId());
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, isOBU? OBU_NF_SINGLE_BARGAIN_INQUIRE : NF_SINGLE_BARGAIN_INQUIRE);
		
		
		
		esbUtilInputVO.setModule("SOT709.getSingleFeeRate");
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		VN085NInputVO vn085nInputVO = new VN085NInputVO();
		vn085nInputVO.setCustId(sot709InputVO.getCustId());
		if(sot709InputVO.getStartDate() == null){
			vn085nInputVO.setStartDate("00000000");
		}else{
			vn085nInputVO.setStartDate(this.toChineseYearMMdd(sot709InputVO.getStartDate(),true)); // 格式9(07)
		}
		if(sot709InputVO.getEndDate() == null){
			vn085nInputVO.setEndDate("99999999");
		}else{
			vn085nInputVO.setEndDate(this.toChineseYearMMdd(sot709InputVO.getEndDate(),true));
		}
		esbUtilInputVO.setVn085nInputVO(vn085nInputVO);
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		List<SingleFeeRateVO> feeRateList = new ArrayList<SingleFeeRateVO>();
		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			VN085NOutputVO vn085nOutputVO = isOBU? esbUtilOutputVO.getAfee003OutputVO() : esbUtilOutputVO.getVn085nOutputVO() ;
			List<VN085NOutputVODetailsVO> details = vn085nOutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<VN085NOutputVODetailsVO>() : details;

			for (VN085NOutputVODetailsVO detailVO : details) {
				//下單：只需取得還未使用過且已覆核完成的議價資料
				//議價：取得全部資料
				if ((sot709InputVO.isNoUsedOnly() && detailVO.getEviNum().isEmpty() && StringUtils.isNotBlank(detailVO.getAppMgr())) 
						|| !sot709InputVO.isNoUsedOnly()) {
					SingleFeeRateVO feeRate = new SingleFeeRateVO();
					feeRate.setBeneDate(new BigDecimal(detailVO.getBeneDate()));
					feeRate.setBeneCode(detailVO.getBeneCode());
					feeRate.setEviNum(detailVO.getEviNum());
					feeRate.setTrustBranch(detailVO.getTrustBranch());
					feeRate.setFundNo(detailVO.getFundNo());
					feeRate.setInvestCur(detailVO.getInvestCur());
					feeRate.setInvestAmt(new EsbUtil().decimalPoint(detailVO.getInvestAmt(), 2));
					feeRate.setFundRate(new EsbUtil().decimalPoint(detailVO.getFundRate(), 3));
					feeRate.setBrgType(detailVO.getBrgType());
					feeRate.setFeeRate(new EsbUtil().decimalPoint(detailVO.getFeeRate(), 5));
					feeRate.setFixFee(new EsbUtil().decimalPoint(detailVO.getFixFee(), 2));
					feeRate.setCrtBrh(detailVO.getCrtBrh());
					feeRate.setTellerld(detailVO.getTellerId());
					feeRate.setAppMgr(detailVO.getAppMgr());
					feeRate.setCrtTime(new BigDecimal(detailVO.getCrtTime()));
					feeRate.setDynamicYN(detailVO.getDynamicYN());
					feeRateList.add(feeRate);
				}
			}

			sot709OutputVO.setSingleFeeRateList(feeRateList);
		}

		return sot709OutputVO;
	}

	/**
     * 基金單次單筆議價查詢(下單用)
     *
     * 使用電文: VN085N
     *
     * @param body
     * @return 
     * @return 
     * @throws Exception
     */
	public SOT709OutputVO getSingleFeeRate(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		sot709InputVO.setStartDate(null);
		sot709InputVO.setEndDate(null);
		sot709InputVO.setNoUsedOnly(true);
		sot709OutputVO = getSingleFeeRateESB(sot709InputVO);
		return sot709OutputVO;
	}
	
    /**
     * 基金單次單筆議價查詢(議價用)
     *
     * 使用電文: VN085N
     *
     * @param body
     * @return 
     * @return 
     * @return 
     * @throws Exception
     */
	public SOT709OutputVO getSingleFeeRate4Bar(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		sot709InputVO.setNoUsedOnly(false);
		sot709OutputVO = getSingleFeeRateESB(sot709InputVO);
		return sot709OutputVO;
	}
	
	
	
	/**
	  * 基金DBU單次議價申請
	  *
	  * for js client
	  * 使用電文: NFBRN6
	  *
	  * @param body
	  * @param header
	  */
		public void singleBargainApply (Object body, IPrimitiveMap header) throws Exception {
			sot709OutputVO = this.singleBargainApply(body);
			sendRtnObject(sot709OutputVO);
		}
	
    /**
     * 基金DBU單次單筆議價申請
     *
     * 使用電文: NFBRN6
     * 2021-08-03 #0703 SamTu
     *
     * @param body
     * @return 
     * @throws Exception
     */
	public SOT709OutputVO singleBargainApply(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		List<Map<String, Object>> recSetList = new ArrayList<Map<String,Object>>();
		if(!sot709InputVO.getApplySeq().substring(0, 1).equals("M") || 
				(sot709InputVO.getApplySeq().substring(0, 1).equals("M") && StringUtils.equals(sot709InputVO.getCheckCode(), "1"))){ 		
		//由議價編號取得資料
		try {
			dam = getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select B.*,E.DEPT_ID,H.AUTH_DATE,H.AUTH_EMP_ID from TBCRM_BRG_APPLY_SINGLE B ");
			sb.append(" left outer join TBORG_MEMBER E on E.EMP_ID = B.MODIFIER ");
			sb.append(" left outer join TBCRM_BRG_APPROVAL_HISTORY H on H.APPLY_SEQ = B.APPLY_SEQ ");
			sb.append("      and H.APPROVAL_SEQ = (select max(APPROVAL_SEQ) from TBCRM_BRG_APPROVAL_HISTORY where APPLY_SEQ = :applySeq) ");
			sb.append(" where B.APPLY_SEQ = :applySeq ");			
			qc.setObject("applySeq", sot709InputVO.getApplySeq());

			qc.setQueryString(sb.toString());
			recSetList = dam.exeQuery(qc);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
		}
		String custId = recSetList.get(0).get("CUST_ID").toString();
		//init util
		boolean isObu = sot701.isObu(custId);
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE,
			isObu ? NF_OBU_SINGLE_BARGAIN_INQUIRE: NF_DBU_SINGLE_BARGAIN_INQUIRE);
		esbUtilInputVO.setModule("SOT709.singleBargainApply");
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		NFBRN6InputVO nfbrn6InputVO = new NFBRN6InputVO();
		nfbrn6InputVO.setCONFIRM(sot709InputVO.getCheckCode());
		nfbrn6InputVO.setAPPLY_SEQ(recSetList.get(0).get("APPLY_SEQ").toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		Date lastupdate = sdf.parse(recSetList.get(0).get("LASTUPDATE").toString());
		nfbrn6InputVO.setTRADE_DATE(this.toChineseYearMMdd(lastupdate));
		if(sot709InputVO.getCheckCode().toString().matches("1|2|5")){
			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNbr = recSetList.get(0).get("DEPT_ID").toString();
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
			nfbrn6InputVO.setBRANCH_NBR(branchNbr);
			
			nfbrn6InputVO.setPROD_ID(recSetList.get(0).get("PROD_ID").toString());
			nfbrn6InputVO.setCUST_ID(recSetList.get(0).get("CUST_ID").toString());
			nfbrn6InputVO.setAMT(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("PURCHASE_AMT"), 2));
			nfbrn6InputVO.setCURRENCY(recSetList.get(0).get("TRUST_CURR").toString());
			nfbrn6InputVO.setFEE_RATE(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FEE_RATE"), 5));
			nfbrn6InputVO.setAUTH_EMP_ID(recSetList.get(0).get("MODIFIER").toString());
			Date lastupdate_date = sdfdate.parse(recSetList.get(0).get("LASTUPDATE").toString());
			nfbrn6InputVO.setAUTH_DATE(this.toChineseYearMMdd(lastupdate_date));
			String lastupdate_time = recSetList.get(0).get("LASTUPDATE").toString().substring(11, 19).replace(":", "");
			nfbrn6InputVO.setAUTH_TIME(lastupdate_time);
			if(recSetList.get(0).get("BRG_REASON") != null){
				nfbrn6InputVO.setREMARKS(recSetList.get(0).get("BRG_REASON").toString());				
			}
			nfbrn6InputVO.setDYNAMIC_YN(StringUtils.equals("6", recSetList.get(0).get("APPLY_TYPE").toString()) ? "Y" : "");
		}else if("3".equals(sot709InputVO.getCheckCode().toString())){
			nfbrn6InputVO.setAUTH_EMP_ID(null != recSetList.get(0).get("AUTH_EMP_ID") ? recSetList.get(0).get("AUTH_EMP_ID").toString() : (StringUtils.equals(recSetList.get(0).get("HIGHEST_AUTH_LV").toString(), "0") ? recSetList.get(0).get("CREATOR").toString() : null));
			nfbrn6InputVO.setAUTH_DATE(null != recSetList.get(0).get("AUTH_DATE") ? this.toChineseYearMMdd(sdfdate.parse(recSetList.get(0).get("AUTH_DATE").toString())) : this.toChineseYearMMdd(new Date()));
			nfbrn6InputVO.setAUTH_TIME(null != recSetList.get(0).get("AUTH_DATE") ? recSetList.get(0).get("AUTH_DATE").toString().substring(11, 19).replace(":", "") : (new Date()).toString().substring(11, 19).replace(":", ""));
			if(null != recSetList.get(0).get("BRG_REASON")){
				nfbrn6InputVO.setREMARKS(recSetList.get(0).get("BRG_REASON").toString());				
			}
			nfbrn6InputVO.setDYNAMIC_YN(StringUtils.equals("6", recSetList.get(0).get("APPLY_TYPE").toString()) ? "Y" : "");
		}
		esbUtilInputVO.setNfbrn6InputVO(nfbrn6InputVO);
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN6OutputVO nfbrn6OutputVO = isObu? esbUtilOutputVO.getAfbrn6OutputVO(): esbUtilOutputVO.getNfbrn6OutputVO();

			String errCode = nfbrn6OutputVO.getERR_COD();
			String errTxt = nfbrn6OutputVO.getERR_TXT();

			if (StringUtils.isNotBlank(errCode)) {
				sot709OutputVO.setErrorCode(errCode);
				sot709OutputVO.setErrorMsg(errTxt);
			}
		}
		}
		return sot709OutputVO;
	}

    /**
      * 基金期間議價申請傳送電文
      *
      * 使用電文: NFBRN8/AFBRN8
      *
      * @param esbId
	  * @param applySeq
	  * @param checkCode
      * @return
      * @throws Exception
      */
	public SOT709OutputVO periodBargainApply(String esbId,String applySeq,String checkCode) throws Exception {
//		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		List<Map<String, Object>> recSetList = new ArrayList<Map<String,Object>>();
		//由議價編號取得資料
		try {
			dam = getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			
			//NFBRN8/AFBRN8 (基金)上行電文中，折數欄位需 * 10。故另外拉出查詢結果
			sb.append("select B.*, ");
			sb.append("       E.DEPT_ID, ");
			sb.append("       H.AUTH_DATE, ");
			sb.append("       H.AUTH_EMP_ID, ");
			sb.append("       DMT_STOCK*10    DMT_STOCK10, ");
			sb.append("       DMT_BOND*10     DMT_BOND10, ");
			sb.append("       DMT_BALANCED*10 DMT_BALANCED10, ");
			sb.append("       FRN_STOCK*10    FRN_STOCK10, ");
			sb.append("       FRN_BOND*10     FRN_BOND10, ");
			sb.append("       FRN_BALANCED*10 FRN_BALANCED10 ");
			sb.append("from TBCRM_BRG_APPLY_PERIOD B ");
			sb.append("left outer join TBORG_MEMBER E on E.EMP_ID = B.CREATOR ");
			sb.append("left outer join TBCRM_BRG_APPROVAL_HISTORY H on H.APPLY_SEQ = B.APPLY_SEQ ");
			sb.append("                and H.APPROVAL_SEQ = (select max(APPROVAL_SEQ) from TBCRM_BRG_APPROVAL_HISTORY where APPLY_SEQ = :applySeq) ");
			sb.append("where B.APPLY_SEQ = :applySeq ");			
			qc.setObject("applySeq", applySeq);
			qc.setQueryString(sb.toString());
			
			recSetList = dam.exeQuery(qc);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		
		Date brg_begin_date = sdf.parse(recSetList.get(0).get("BRG_BEGIN_DATE").toString());
		Date brg_end_date = sdf.parse(recSetList.get(0).get("BRG_END_DATE").toString());
		Date lastupdate = sdfdate.parse(recSetList.get(0).get("LASTUPDATE").toString());
		String lastupdate_time = recSetList.get(0).get("LASTUPDATE").toString().substring(11, 19).replace(":", "");
		
		Date auth_date = null;
		String auth_time = null;
		if (checkCode.matches("3|5")) {
			auth_date = (null == recSetList.get(0).get("AUTH_DATE")) ? null : sdfdate.parse(recSetList.get(0).get("AUTH_DATE").toString());
			auth_time = (null == recSetList.get(0).get("AUTH_DATE")) ? null : recSetList.get(0).get("AUTH_DATE").toString().substring(11, 19).replace(":", "");
		}
		
		switch (esbId) {
			case "NFBRN8":
				//init util
				ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NF_DBU_INTERVAL_BARGAIN_INQUIRE);
				esbUtilInputVO.setModule("SOT709.periodBargainApply");

				//head
				TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
				txHead.setDefaultTxHead();
				esbUtilInputVO.setTxHeadVO(txHead);

				//body
				NFBRN8InputVO nfbrn8InputVO = new NFBRN8InputVO();
				nfbrn8InputVO.setCONFIRM(checkCode);
				nfbrn8InputVO.setCUST_ID(recSetList.get(0).get("CUST_ID").toString());
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sdfdate = new SimpleDateFormat("yyyy-MM-dd");
				nfbrn8InputVO.setBRG_BEGIN_DATE(this.toChineseYearMMdd(brg_begin_date));
				nfbrn8InputVO.setBRG_END_DATE(this.toChineseYearMMdd(brg_end_date));
				if (checkCode.matches("1|2")) {
					nfbrn8InputVO.setAUTH_EMP_ID(recSetList.get(0).get("MODIFIER").toString());
					nfbrn8InputVO.setAUTH_DATE(this.toChineseYearMMdd(lastupdate));
					nfbrn8InputVO.setAUTH_TIME(lastupdate_time);
					nfbrn8InputVO.setDMT_STOCK(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_STOCK10") , 2));
					nfbrn8InputVO.setFRN_STOCK(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_STOCK10"), 2));
					nfbrn8InputVO.setDMT_BOND(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_BOND10"), 2));
					nfbrn8InputVO.setFRN_BOND(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_BOND10"), 2));
					nfbrn8InputVO.setDMT_BALANCED(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_BALANCED10"), 2));
					nfbrn8InputVO.setFRN_BALANCED(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_BALANCED10"), 2));
//					if(recSetList.get(0).get("BRG_REASON") != null){
////						nfbrn8InputVO.setBRG_REASON(recSetList.get(0).get("BRG_REASON").toString());
//						nfbrn8InputVO.setBRG_REASON("");	//個金分行業務管理系統拋送期間議價給AS400時，欄位說明的地方，送「空白」欄位(#4021)
//					}
				} else if (checkCode.matches("3|5")) {
					nfbrn8InputVO.setAUTH_EMP_ID(StringUtils.equals("0", recSetList.get(0).get("HIGHEST_AUTH_LV").toString()) ? recSetList.get(0).get("MODIFIER").toString() : recSetList.get(0).get("AUTH_EMP_ID").toString());
					nfbrn8InputVO.setAUTH_DATE(StringUtils.equals("0", recSetList.get(0).get("HIGHEST_AUTH_LV").toString()) ? this.toChineseYearMMdd(lastupdate): this.toChineseYearMMdd(auth_date));
					nfbrn8InputVO.setAUTH_TIME(StringUtils.equals("0", recSetList.get(0).get("HIGHEST_AUTH_LV").toString()) ? lastupdate_time : auth_time);
//					if(recSetList.get(0).get("BRG_REASON") != null){
////						nfbrn8InputVO.setBRG_REASON(recSetList.get(0).get("BRG_REASON").toString());
//						nfbrn8InputVO.setBRG_REASON("");	//個金分行業務管理系統拋送期間議價給AS400時，欄位說明的地方，送「空白」欄位(#4021)
//					}
				}
				esbUtilInputVO.setNfbrn8InputVO(nfbrn8InputVO);

				//發送電文
				List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

				for (ESBUtilOutputVO esbUtilOutputVO : vos) {
					NFBRN8OutputVO nfbrn8OutputVO = esbUtilOutputVO.getNfbrn8OutputVO();

					String errCode = nfbrn8OutputVO.getERR_COD();
					String errTxt = nfbrn8OutputVO.getERR_TXT();

					if (StringUtils.isNotBlank(errCode)) {
						sot709OutputVO.setErrorCode(errCode);
						sot709OutputVO.setErrorMsg(errTxt);
					}
				}
				break;
				case "AFBRN8":
					//init util
					ESBUtilInputVO esbUtilInputVO1 = getTxInstance(ESB_TYPE, NF_OBU_INTERVAL_BARGAIN_INQUIRE);
					esbUtilInputVO1.setModule("SOT709.periodBargainApply");
					//head
					TxHeadVO txHead1 = esbUtilInputVO1.getTxHeadVO();
					txHead1.setDefaultTxHead();
					esbUtilInputVO1.setTxHeadVO(txHead1);

					//body
					AFBRN8InputVO afbrn8InputVO = new AFBRN8InputVO();
					afbrn8InputVO.setCONFIRM(checkCode);
					afbrn8InputVO.setCUST_ID(recSetList.get(0).get("CUST_ID").toString());
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sdfdate = new SimpleDateFormat("yyyy-MM-dd");
					afbrn8InputVO.setBRG_BEGIN_DATE(this.toChineseYearMMdd(brg_begin_date));
					afbrn8InputVO.setBRG_END_DATE(this.toChineseYearMMdd(brg_end_date));
					if ("2".equals(checkCode)) {
						afbrn8InputVO.setAUTH_EMP_ID(recSetList.get(0).get("MODIFIER").toString());
						afbrn8InputVO.setAUTH_DATE(this.toChineseYearMMdd(lastupdate));
						afbrn8InputVO.setAUTH_TIME(lastupdate_time);
						afbrn8InputVO.setDMT_STOCK(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_STOCK10"), 2));
						afbrn8InputVO.setFRN_STOCK(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_STOCK10"), 2));
						afbrn8InputVO.setDMT_BOND(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_BOND10"), 2));
						afbrn8InputVO.setFRN_BOND(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_BOND10"), 2));
						afbrn8InputVO.setDMT_BALANCED(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_BALANCED10"), 2));
						afbrn8InputVO.setFRN_BALANCED(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_BALANCED10"), 2));
//						if(recSetList.get(0).get("BRG_REASON") != null){
////							afbrn8InputVO.setBRG_REASON(recSetList.get(0).get("BRG_REASON").toString());
//							afbrn8InputVO.setBRG_REASON("");	//個金分行業務管理系統拋送期間議價給AS400時，欄位說明的地方，送「空白」欄位(#4021)
//						}
					} else if ("3".equals(checkCode) || "5".equals(checkCode)) {
						afbrn8InputVO.setAUTH_EMP_ID(StringUtils.equals("0", recSetList.get(0).get("HIGHEST_AUTH_LV").toString()) ? recSetList.get(0).get("MODIFIER").toString() : recSetList.get(0).get("AUTH_EMP_ID").toString());
						afbrn8InputVO.setAUTH_DATE(StringUtils.equals("0", recSetList.get(0).get("HIGHEST_AUTH_LV").toString()) ? this.toChineseYearMMdd(lastupdate): this.toChineseYearMMdd(auth_date));
						afbrn8InputVO.setAUTH_TIME(StringUtils.equals("0", recSetList.get(0).get("HIGHEST_AUTH_LV").toString()) ? lastupdate_time : auth_time);
//						if(recSetList.get(0).get("BRG_REASON") != null){
////							afbrn8InputVO.setBRG_REASON(recSetList.get(0).get("BRG_REASON").toString());	
//							afbrn8InputVO.setBRG_REASON("");	//個金分行業務管理系統拋送期間議價給AS400時，欄位說明的地方，送「空白」欄位(#4021)
//						}
					} else if ("1".equals(checkCode)) {
						afbrn8InputVO.setDMT_STOCK(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_STOCK10"), 2));
						afbrn8InputVO.setFRN_STOCK(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_STOCK10"), 2));
						afbrn8InputVO.setDMT_BOND(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_BOND10"), 2));
						afbrn8InputVO.setFRN_BOND(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_BOND10"), 2));
						afbrn8InputVO.setDMT_BALANCED(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("DMT_BALANCED10"), 2));
						afbrn8InputVO.setFRN_BALANCED(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("FRN_BALANCED10"), 2));
						afbrn8InputVO.setAUTH_TIME(lastupdate_time);
//						if(recSetList.get(0).get("BRG_REASON") != null){
////							afbrn8InputVO.setBRG_REASON(recSetList.get(0).get("BRG_REASON").toString());
//							afbrn8InputVO.setBRG_REASON("");	//個金分行業務管理系統拋送期間議價給AS400時，欄位說明的地方，送「空白」欄位(#4021)
//						}
					}
					esbUtilInputVO1.setAfbrn8InputVO(afbrn8InputVO);

					//發送電文
					List<ESBUtilOutputVO> vos1 = send(esbUtilInputVO1);

					for(ESBUtilOutputVO esbUtilOutputVO1 : vos1) {
						AFBRN8OutputVO afbrn8OutputVO = esbUtilOutputVO1.getAfbrn8OutputVO();

						String errCode1 = afbrn8OutputVO.getERR_COD();
						String errTxt1 = afbrn8OutputVO.getERR_TXT();

						if (StringUtils.isNotBlank(errCode1)) {
							sot709OutputVO.setErrorCode(errCode1);
							sot709OutputVO.setErrorMsg(errTxt1);
						}
					}
				break;
			default:
				break;


		}

		return sot709OutputVO;
	}
	
	 /**
	  * 基金DBU期間議價申請
	  *
	  * 使用電文: NFBRN8
	  *
	  * @param body
	 * @return 
	  * @return 
	  * @throws Exception
	  */
	public SOT709OutputVO periodBargainApplyDBU(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		periodBargainApply("NFBRN8", sot709InputVO.getApplySeq().toString(), sot709InputVO.getCheckCode().toString());
		return sot709OutputVO;
	}
	
	 /**
	  * 基金OBU期間議價申請
	  *
	  * 使用電文: AFBRN8
	  *
	  * @param body
	 * @return 
	  * @return 
	  * @throws Exception
	  */
	public SOT709OutputVO periodBargainApplyOBU(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		periodBargainApply("AFBRN8", sot709InputVO.getApplySeq().toString(), sot709InputVO.getCheckCode().toString());
		return sot709OutputVO;
	}

	 /**
	  * 基金單次定期(不)定額議價查詢
	  *
	  * 使用電文: NFEE086
	  *
	  * @param body
	 * @return 
	  * @return 
	  * @throws Exception
	  */
	public SOT709OutputVO getSingleRegFeeRateESB(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		getDataAccessManager();
		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NF_ESB);
		esbUtilInputVO.setModule("SOT709.getSingleRegFeeRate");
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		NFEE086InputVO nfee086InputVO = new NFEE086InputVO();
		nfee086InputVO.setCustId(sot709InputVO.getCustId());
		if(sot709InputVO.getStartDate() == null){
			nfee086InputVO.setStartDate("00000000");
		}else{
			nfee086InputVO.setStartDate(this.toChineseYearMMdd(sot709InputVO.getStartDate(), true));
		}
		if(sot709InputVO.getEndDate() == null){
			nfee086InputVO.setEndDate("99999999");
		}else{
			nfee086InputVO.setEndDate(this.toChineseYearMMdd(sot709InputVO.getEndDate(), true));
		}
		esbUtilInputVO.setNfee086InputVO(nfee086InputVO);
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		List<SingleFeeRateVO> feeRateList = new ArrayList<SingleFeeRateVO>();
		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFEE086OutputVO nfee086OutputVO = esbUtilOutputVO.getNfee086OutputVO();

			List<NFEE086OutputVODetailsVO> details = nfee086OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NFEE086OutputVODetailsVO>() : details;

			for (NFEE086OutputVODetailsVO detailVO : details) {
				//下單：只需取得還未使用過且已覆核完成的議價資料
				//議價：取得全部資料
				if ((sot709InputVO.isNoUsedOnly() && detailVO.getEviNum().isEmpty() && StringUtils.isNotBlank(detailVO.getAppMgr())) 
						|| !sot709InputVO.isNoUsedOnly()) {
					SingleFeeRateVO feeRate = new SingleFeeRateVO();
					feeRate.setBeneDate(new BigDecimal(detailVO.getBeneDate()));
					feeRate.setBeneCode(detailVO.getBeneCode());
					feeRate.setEviNum(detailVO.getEviNum());
					feeRate.setTrustBranch(detailVO.getTrustBranch());
					feeRate.setFundNo(detailVO.getFundNo());
					feeRate.setInvestCur(detailVO.getInvestCur());
					feeRate.setInvestAmt(new EsbUtil().decimalPoint(detailVO.getInvestAmt(), 2));
					feeRate.setGroupCode(detailVO.getProCode());
					feeRate.setCrtBrh(detailVO.getCrtBrh());
					feeRate.setTellerld(detailVO.getTellerId());
					feeRate.setAppMgr(detailVO.getAppMgr());
					feeRate.setCrtTime(new BigDecimal(detailVO.getCrtTime()));
					feeRateList.add(feeRate);
				}
			}

			sot709OutputVO.setSingleFeeRateList(feeRateList);
		}

		return sot709OutputVO;
	}
	
	 /**
	  * 基金單次定期(不)定額議價查詢(下單用)
	  *
	  * 使用電文: NFEE086
	  *
	  * @param body
	 * @return 
	 * @return 
	  * @return 
	  * @throws Exception
	  */
	public SOT709OutputVO getSingleRegFeeRate(Object body) throws Exception {
			sot709InputVO = (SOT709InputVO) body;
			sot709OutputVO = new SOT709OutputVO();
			sot709InputVO.setStartDate(null);
			sot709InputVO.setEndDate(null);
			sot709InputVO.setNoUsedOnly(true);
			sot709OutputVO = getSingleRegFeeRateESB(sot709InputVO);
			return sot709OutputVO;
	}
	
	 /**
	  * 基金單次定期(不)定額議價查詢(議價用)
	  *
	  * 使用電文: NFEE086
	  *
	  * @param body
	 * @return 
	 * @return 
	 * @return 
	  * @return 
	  * @throws Exception
	  */
	public SOT709OutputVO getSingleRegFeeRate4Bar(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		sot709InputVO.setNoUsedOnly(false);
		sot709OutputVO = getSingleRegFeeRateESB(sot709InputVO);
		return sot709OutputVO;
	}
	
    /**
     * 基金DBU單次定期(不)定額議價申請
     *
     * 使用電文: NFBRN4
     *
     * @param body
     * @return 
     * @return 
     * @throws Exception
     */
	public SOT709OutputVO singleRegBargainApply(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		List<Map<String, Object>> recSetList = new ArrayList<Map<String,Object>>();
		//由議價編號取得資料
		try {
			dam = getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select B.*,E.DEPT_ID,H.AUTH_DATE,H.AUTH_EMP_ID from TBCRM_BRG_APPLY_SINGLE B ");
			sb.append("   left outer join TBORG_MEMBER E on E.EMP_ID = B.MODIFIER ");
			sb.append("   left outer join TBCRM_BRG_APPROVAL_HISTORY H on H.APPLY_SEQ = B.APPLY_SEQ ");
			sb.append("        and H.APPROVAL_SEQ = (select max(APPROVAL_SEQ) from TBCRM_BRG_APPROVAL_HISTORY where APPLY_SEQ = :applySeq) ");
			sb.append(" where B.APPLY_SEQ = :applySeq ");			
			qc.setObject("applySeq", sot709InputVO.getApplySeq());
			qc.setQueryString(sb.toString());
			recSetList = dam.exeQuery(qc);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
		}
		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NF_DBU);
		esbUtilInputVO.setModule("SOT709.singleRegBargainApply");
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		NFBRN4InputVO nfbrn4InputVO = new NFBRN4InputVO();
		nfbrn4InputVO.setCONFIRM(sot709InputVO.getCheckCode());
		nfbrn4InputVO.setAPPLY_SEQ(recSetList.get(0).get("APPLY_SEQ").toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		Date lastupdate = sdfdate.parse(recSetList.get(0).get("LASTUPDATE").toString());
		nfbrn4InputVO.setTRADE_DATE(this.toChineseYearMMdd(lastupdate));
		if(sot709InputVO.getCheckCode().toString().matches("1|2|5")){
			// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
			// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
			String branchNbr = recSetList.get(0).get("DEPT_ID").toString();
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
			nfbrn4InputVO.setBRANCH_NBR(branchNbr);
			
			nfbrn4InputVO.setPROD_ID(recSetList.get(0).get("PROD_ID").toString());
			if(StringUtils.equals("M", sot709InputVO.getApplySeq().substring(0, 1))){  // 金錢信託_基金定期定額_申請議價
				nfbrn4InputVO.setCUST_ID("99331241");
			}else{
				nfbrn4InputVO.setCUST_ID(recSetList.get(0).get("CUST_ID").toString());
			}
			
			nfbrn4InputVO.setAMT(new EsbUtil().decimalPadding((BigDecimal) recSetList.get(0).get("PURCHASE_AMT"), 2));
			nfbrn4InputVO.setCURRENCY(recSetList.get(0).get("TRUST_CURR").toString());
			Map<String, String> gmRoleMap =  new XmlInfo().doGetVariable("CRM.SINGLE_REG_DISCOUNT", FormatHelper.FORMAT_3);
			String pro_code = "";
			for (Entry<String, String> e : gmRoleMap.entrySet()) {
				String value = e.getValue();
				if (value.equals((((BigDecimal) recSetList.get(0).get("FEE_DISCOUNT")).setScale(1, BigDecimal.ROUND_UP)).toString())) {
					pro_code = e.getKey();
				}
			}
			nfbrn4InputVO.setPRO_CODE(pro_code);
			nfbrn4InputVO.setAUTH_EMP_ID(recSetList.get(0).get("MODIFIER").toString());
			Date lastupdate_date = sdfdate.parse(recSetList.get(0).get("LASTUPDATE").toString());
			nfbrn4InputVO.setAUTH_DATE(this.toChineseYearMMdd(lastupdate_date));
			String lastupdate_time = recSetList.get(0).get("LASTUPDATE").toString().substring(11, 19).replace(":", "");
			nfbrn4InputVO.setAUTH_TIME(lastupdate_time);
			if(recSetList.get(0).get("BRG_REASON") != null){
				nfbrn4InputVO.setREMARKS(recSetList.get(0).get("BRG_REASON").toString());							
			}
		}else if("4".equals(sot709InputVO.getCheckCode().toString())){
			nfbrn4InputVO.setAUTH_EMP_ID(recSetList.get(0).get("MODIFIER").toString());
			Date lastupdate_date = sdfdate.parse(recSetList.get(0).get("LASTUPDATE").toString());
			nfbrn4InputVO.setAUTH_DATE(this.toChineseYearMMdd(lastupdate_date));
			String lastupdate_time = recSetList.get(0).get("LASTUPDATE").toString().substring(11, 19).replace(":", "");
			nfbrn4InputVO.setAUTH_TIME(lastupdate_time);
		}else if("3".equals(sot709InputVO.getCheckCode().toString())){
			nfbrn4InputVO.setAUTH_EMP_ID(recSetList.get(0).get("MODIFIER").toString());
			Date lastupdate_date = sdfdate.parse(recSetList.get(0).get("LASTUPDATE").toString());
			nfbrn4InputVO.setAUTH_DATE(this.toChineseYearMMdd(lastupdate_date));
			String lastupdate_time = recSetList.get(0).get("LASTUPDATE").toString().substring(11, 19).replace(":", "");
			nfbrn4InputVO.setAUTH_TIME(lastupdate_time);
			if(recSetList.get(0).get("BRG_REASON") != null){
				nfbrn4InputVO.setREMARKS(recSetList.get(0).get("BRG_REASON").toString());				
			}
		}
		esbUtilInputVO.setNfbrn4InputVO(nfbrn4InputVO);
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN4OutputVO nfbrn4OutputVO = esbUtilOutputVO.getNfbrn4OutputVO();

			String errCode = nfbrn4OutputVO.getERR_COD();
			String errTxt = nfbrn4OutputVO.getERR_TXT();

			if (StringUtils.isNotBlank(errCode)) {
				sot709OutputVO.setErrorCode(errCode);
				sot709OutputVO.setErrorMsg(errTxt);
			}
		}

		return sot709OutputVO;
	}

	/**
	 * 基金DBU單次單筆議價修改
	 *
	 * for js client
	 * 使用電文: NFBRN6
	 *
	 * @param {@link SOT709InputVO#cu}
	 * @param header
	 * @return
	 * @throws Exception
     */
	public void singleBargainModify(Object body, IPrimitiveMap header) throws Exception {
		sot709OutputVO = singleBargainModify(body);
		sendRtnObject(sot709OutputVO);
	}

	/**
	 * 基金DBU單次單筆議價修改
	 *
	 * for js client
	 * 使用電文: NFBRN6
	 *
	 * @param body
	 * @return
	 * @throws Exception
     */
	public SOT709OutputVO singleBargainModify(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();
		String applySeq = sot709InputVO.getApplySeq();
		String custId = sot709InputVO.getCustId();
		String prodId = sot709InputVO.getProdId();
		BigDecimal purchaseAmt = sot709InputVO.getPurchaseAmt();
		String trustCurr = sot709InputVO.getTrustCurr();
		BigDecimal feeRate = sot709InputVO.getFee_rate();
		//get current date and time
		Date date = new Date();
		String sysdate = this.toChineseYearMMdd(date);
		String systime = new SimpleDateFormat("HHmmss").format(date);

		//init util
		Boolean isOBU = sot701.isObu(custId);
		ESBUtilInputVO esbUtilInputVO;
		esbUtilInputVO = getTxInstance(ESB_TYPE, isOBU? NF_OBU_SINGLE_BARGAIN_INQUIRE : NF_DBU_SINGLE_BARGAIN_INQUIRE);
		esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txhead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txhead);
		txhead.setDefaultTxHead();

		//body
		NFBRN6InputVO txBodyVO = new NFBRN6InputVO();
		esbUtilInputVO.setNfbrn6InputVO(txBodyVO);
		txBodyVO.setCONFIRM("5");			//交易項目
		txBodyVO.setAPPLY_SEQ(applySeq);	//議價編號
		txBodyVO.setTRADE_DATE(sysdate);								//優惠日
		
//		txBodyVO.setBRANCH_NBR((String)getUserVariable(FubonSystemVariableConsts.LOGINBRH));	//交易分行
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH).toString();
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
		txBodyVO.setBRANCH_NBR(branchNbr);	//交易分行
		
		txBodyVO.setCUST_ID(custId);    	//客戶ID
		txBodyVO.setPROD_ID(prodId);		//基金代號/基金套餐
//		txBodyVO.setAMT(purchaseAmt);   	//申購金額
		txBodyVO.setAMT(new EsbUtil().decimalPadding(purchaseAmt, 2));
		txBodyVO.setCURRENCY(trustCurr);	//申購幣別
		txBodyVO.setFEE_RATE(new EsbUtil().decimalPadding(feeRate, 5)); // 折數
//		txBodyVO.setAUTH_EMP_ID((String)getUserVariable(FubonSystemVariableConsts.LOGINID));	//登入者員編
		txBodyVO.setAUTH_EMP_ID(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());	//登入者員編
		
		txBodyVO.setAUTH_DATE(sysdate);		//鍵機/覆核日期
		txBodyVO.setAUTH_TIME(systime);		//鍵機/覆核時間
		txBodyVO.setREMARKS("");			//備註
		txBodyVO.setDYNAMIC_YN(sot709InputVO.getDynamicYN()); //動態鎖利註記
		
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN6OutputVO nfbrn6OutputVO = isOBU? esbUtilOutputVO.getAfbrn6OutputVO() : esbUtilOutputVO.getNfbrn6OutputVO() ;
		    
			String errCode = nfbrn6OutputVO.getERR_COD();
			String errTxt = nfbrn6OutputVO.getERR_TXT();

			if (StringUtils.isNotBlank(errCode)) {
				sot709OutputVO.setErrorCode(errCode);
				sot709OutputVO.setErrorMsg(errTxt);
			}
		}

		return sot709OutputVO;
	}

    /**
     * 基金DBU單次小額議價修改
     *
     * for js client
     * 使用電文: NFBRN4
     *
     * @param body
     * @param header
     * @throws Exception
     */
	public void singleRegBargainModify(Object body, IPrimitiveMap header) throws Exception {
		sot709OutputVO = singleRegBargainModify(body);
		sendRtnObject(sot709OutputVO);
	}

    /**
     * 基金DBU單次小額議價修改
     *
     * 使用電文: NFBRN4
     *
     * @param body
     * @return
     * @throws Exception
     */
	public SOT709OutputVO singleRegBargainModify(Object body) throws Exception {

		initUUID();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2); //UHRM
		Map<String, String> branchChgMap = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE", FormatHelper.FORMAT_3);
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();	

		String applySeq = sot709InputVO.getApplySeq();
		String custId = sot709InputVO.getCustId();
		String prodId = sot709InputVO.getProdId();
		BigDecimal purchaseAmt = sot709InputVO.getPurchaseAmt();
		String trustCurr = sot709InputVO.getTrustCurr();
		String proCode = sot709InputVO.getProCode();

		//get current date and time
		Date date = new Date();
		String sysdate = this.toChineseYearMMdd(date);
		String systime = new SimpleDateFormat("HHmmss").format(date);

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NF_DBU);
		esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txhead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txhead);
		txhead.setDefaultTxHead();

		//body
		NFBRN4InputVO txBodyVO = new NFBRN4InputVO();
		esbUtilInputVO.setNfbrn4InputVO(txBodyVO);
		txBodyVO.setCONFIRM("5");			//交易項目
		txBodyVO.setAPPLY_SEQ(applySeq);	//議價編號
		txBodyVO.setTRADE_DATE(sysdate);	//優惠日
		
//		txBodyVO.setBRANCH_NBR((String)getUserVariable(FubonSystemVariableConsts.LOGINBRH));			//交易分行
		// 20190617/mantis:6592/WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P5/modify by ocean/若組織為031，則帶715
		// 20200325/mantis:0561/WMS-CR-20210311-01_因應銀證組織調整商品議價及人員證照查詢功能/modify by ocean/若組織為175，則帶715
		String branchNbr = SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH).toString();
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
		txBodyVO.setBRANCH_NBR(branchNbr);	//交易分行
		
		txBodyVO.setCUST_ID(custId);    	//客戶ID
		txBodyVO.setPROD_ID(prodId);		//基金代號/基金套餐
//		txBodyVO.setAMT(purchaseAmt);   	//申購金額
		txBodyVO.setAMT(new EsbUtil().decimalPadding(purchaseAmt, 2));
		txBodyVO.setCURRENCY(trustCurr);	//申購幣別
		txBodyVO.setPRO_CODE(proCode);		//專案碼
//		txBodyVO.setAUTH_EMP_ID((String)getUserVariable(FubonSystemVariableConsts.LOGINID));			//登入者員編
		txBodyVO.setAUTH_EMP_ID(SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID).toString());	//登入者員編
		txBodyVO.setAUTH_DATE(sysdate);		//鍵機/覆核日期
		txBodyVO.setAUTH_TIME(systime);		//鍵機/覆核時間
		txBodyVO.setREMARKS("");			//備註

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for(ESBUtilOutputVO esbUtilOutputVO : vos) {
			NFBRN4OutputVO nfbrn4OutputVO = esbUtilOutputVO.getNfbrn4OutputVO();

			String errCode = nfbrn4OutputVO.getERR_COD();
			String errTxt = nfbrn4OutputVO.getERR_TXT();

			if (StringUtils.isNotBlank(errCode)) {
				sot709OutputVO.setErrorCode(errCode);
				sot709OutputVO.setErrorMsg(errTxt);
			}
		}

		return sot709OutputVO;
	}
	
	/**
	 * 次數型團體優惠手續費查詢	
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void getMeteringFeeRate(Object body, IPrimitiveMap header) throws Exception {
		sot709OutputVO = getMeteringFeeRate(body);
		sendRtnObject(sot709OutputVO);
	}
	
	/**
	 * 次數型團體優惠手續費查詢
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public SOT709OutputVO getMeteringFeeRate(Object body) throws Exception {
		sot709InputVO = (SOT709InputVO) body;
		sot709OutputVO = new SOT709OutputVO();		
		Date sysdate = new Date();
		
		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NFEE001_DATA);
		esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());
		
		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);
		
		//body
		NFEE001InputVO nfee001InputVO = new NFEE001InputVO();
		nfee001InputVO.setCustId(sot709InputVO.getCustId());
		//起訖日都放系統日，表示今日有效的手續費率
		nfee001InputVO.setBDate(this.toChineseYearMMdd(sysdate, true));
		nfee001InputVO.setEDate(this.toChineseYearMMdd(sysdate, true));
		esbUtilInputVO.setNfee001InputVO(nfee001InputVO);
				
		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
		
		List<MeteringFeeRateVO> feeRateList = new ArrayList<MeteringFeeRateVO>();
		
		for(ESBUtilOutputVO esbUtilOutputVO :vos) {
			NFEE001OutputVO nfee001OutputVO = esbUtilOutputVO.getNfee001OutputVO();
			List<NFEE001OutputVODetailsVO> details = nfee001OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NFEE001OutputVODetailsVO>() : details;
						
			for (NFEE001OutputVODetailsVO detailVO : details) {
				MeteringFeeRateVO feeRate = new MeteringFeeRateVO();
				feeRate.setEffBeginDate(this.toAdYearMMdd(detailVO.getN0101(), false));
				feeRate.setEffEndDate(this.toAdYearMMdd(detailVO.getN0102(), false));
				feeRate.setCntSingle(detailVO.getN0103());
				feeRate.setCntReg(detailVO.getN0104());
				feeRate.setWebSingle(detailVO.getN0105());
				feeRate.setWebSingle(detailVO.getN0106());
				feeRate.setTotalCount(new BigDecimal(detailVO.getN0107()));
				feeRate.setUsedCount(new BigDecimal(detailVO.getN0108()));
				feeRate.setGroupName(detailVO.getN0109());
				
				feeRateList.add(feeRate);
			}
			
			sot709OutputVO.setMeteringFeeRateList(feeRateList);
		}
		
		return sot709OutputVO;
	}
}
