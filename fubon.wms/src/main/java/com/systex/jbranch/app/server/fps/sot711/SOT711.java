package com.systex.jbranch.app.server.fps.sot711;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.FEE_RATE_DATA;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.NJBRVX1;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.AJBRVA2;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva2.AJBRVA2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajbrva2.AJBRVA2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva2.NJBRVA2InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrva2.NJBRVA2OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx1.NJBRVX1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvx1.NJBRVX1OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njweea60.NJWEEA60InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njweea60.NJWEEA60OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Created by SebastianWu on 2016/9/29.
 * 
 * revised by Cathy Tang on 2016/10/24
 * 上行電文數字格式修正
 * 數字格式呼叫ESBUtil.decimalPadding：去除小數點,並用0向右補足小數位長度
 */
@Component("sot711")
@Scope("request")
public class SOT711 extends EsbUtil {
	@Autowired
	private CBSService cbsservice;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataAccessManager dam = null;
    private SOT711InputVO sot711InputVO;
    private SOT711OutputVO sot711OutputVO;
    private String thisClaz = this.getClass().getSimpleName()+".";

    /* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;

    /**
     * 海外債表定手續費率、最優手續費率、應付前手息率資料查詢
     * for js client
     *
     * 使用電文: NJBRVA2
     *
     * @param body
     * @param header
     * @return
     */
    public void getDefaultFeeRateData(Object body, IPrimitiveMap header) throws Exception {
    	sot711InputVO = (SOT711InputVO) body;
    	String isOBU = StringUtils.isBlank(sot711InputVO.getIsOBU()) ? "" : sot711InputVO.getIsOBU();
    	if (isOBU.equals("Y")) {
    		sendRtnObject(this.getDefaultFeeRateDataOBU(body));
    	} else {
    		sendRtnObject(this.getDefaultFeeRateData(body));
    	}
    }

    /**
     * 海外債表定手續費率、最優手續費率、應付前手息率資料查詢
     *
     * 使用電文: NJBRVA2
     *
     * @param body
     * @return
     */
    public SOT711OutputVO getDefaultFeeRateData(Object body) throws Exception {
        sot711InputVO = (SOT711InputVO) body;
        sot711OutputVO = new SOT711OutputVO();

        String custId = sot711InputVO.getCustId(); //身份証ID
        String bondNo = sot711InputVO.getBondNo(); //債券代號
        String priceType = sot711InputVO.getPriceType(); //限價方式
        BigDecimal entrustAmt = sot711InputVO.getEntrustAmt(); //委託價格
        BigDecimal purchaseAmt = sot711InputVO.getPurchaseAmt(); //申購面額
        String txFeeType = sot711InputVO.getTxFeeType(); //手續費議價
        String trustAcct = sot711InputVO.getTrustAcct(); //信託帳號

        //欄位檢核
        if(StringUtils.isBlank(custId) || StringUtils.isBlank(bondNo) || StringUtils.isBlank(priceType.toString())
                || entrustAmt==null || purchaseAmt==null || StringUtils.isBlank(txFeeType.toString())
                || StringUtils.isBlank(trustAcct)){
            throw new JBranchException("遺漏必入欄位");
        }

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, FEE_RATE_DATA);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);
        
        //body
        NJBRVA2InputVO txBodyVO = new NJBRVA2InputVO();
        txBodyVO.setCustId(custId);
        txBodyVO.setBondNo(bondNo);
        txBodyVO.setPriceType(priceType);
        txBodyVO.setEntrustAmt(new EsbUtil().decimalPadding(entrustAmt, 4));
        txBodyVO.setPurchaseAmt(purchaseAmt);
        txBodyVO.setTxFeeType(txFeeType);
        txBodyVO.setTrustAcct(cbsservice.checkAcctLength(trustAcct));
        esbUtilInputVO.setNjbrva2InputVO(txBodyVO);
        
        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
            NJBRVA2OutputVO njbrva2OuptuptVO = esbUtilOutputVO.getNjbrva2OutputVO();

            //電文回傳取第一筆資料即可
            if (CollectionUtils.isNotEmpty(njbrva2OuptuptVO.getDetails())) {
                sot711OutputVO.setDefaultFeeRate(new EsbUtil().decimalPoint(njbrva2OuptuptVO.getDetails().get(0).getDefaultFeeRate(), 5));
                sot711OutputVO.setBestFeeRate(new EsbUtil().decimalPoint(njbrva2OuptuptVO.getDetails().get(0).getBestFeeRate(), 5));
                sot711OutputVO.setPayableFeeRate(new EsbUtil().decimalPoint(njbrva2OuptuptVO.getDetails().get(0).getPayableFeeRate(), 9));
            }
        }

		return sot711OutputVO;
	}
    
    /**
     * AJBRVA2_理專鍵機債券手續費議價_OBU
     *
     * 使用電文: AJBRVA2
     *
     * @param body
     * @param header
     * @return
     */
    public void getDefaultFeeRateDataOBU(Object body, IPrimitiveMap header) throws Exception {
    	sendRtnObject(this.getDefaultFeeRateDataOBU(body));
    }
    
    /**
     * 海外債表定手續費率、最優手續費率、應付前手息率資料查詢
     *
     * 使用電文: AJBRVA2
     *
     * @param body
     * @return
     */
    public SOT711OutputVO getDefaultFeeRateDataOBU(Object body) throws Exception {
    	sot711InputVO = (SOT711InputVO) body;
    	sot711OutputVO = new SOT711OutputVO();
    	
    	String custId = sot711InputVO.getCustId(); 					// 身份証ID
    	String bondNo = sot711InputVO.getBondNo();					// 債券代號
    	String priceType = sot711InputVO.getPriceType();			// 限價方式
    	BigDecimal entrustAmt = sot711InputVO.getEntrustAmt();		// 委託價格
    	BigDecimal purchaseAmt = sot711InputVO.getPurchaseAmt();	// 申購面額
    	String txFeeType = sot711InputVO.getTxFeeType(); 			// 手續費議價
    	String trustAcct = sot711InputVO.getTrustAcct(); 			// 信託帳號
    	
    	//欄位檢核
    	if(StringUtils.isBlank(custId) || StringUtils.isBlank(bondNo) || StringUtils.isBlank(priceType.toString())
    			|| entrustAmt==null || purchaseAmt==null || StringUtils.isBlank(txFeeType.toString())
    			|| StringUtils.isBlank(trustAcct)){
    		throw new JBranchException("遺漏必入欄位");
    	}
    	
    	//init util
    	ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, AJBRVA2);
    	esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());
    	
    	//head
    	TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
    	txHead.setDefaultTxHead();
    	esbUtilInputVO.setTxHeadVO(txHead);
    	
    	//body
    	AJBRVA2InputVO txBodyVO = new AJBRVA2InputVO();
    	txBodyVO.setCustId(custId);
    	txBodyVO.setBondNo(bondNo);
    	txBodyVO.setPriceType(priceType);
    	txBodyVO.setEntrustAmt(new EsbUtil().decimalPadding(entrustAmt, 4));
    	txBodyVO.setPurchaseAmt(purchaseAmt);
    	txBodyVO.setTxFeeType(txFeeType);
    	txBodyVO.setTrustAcct(cbsservice.checkAcctLength(trustAcct));
    	esbUtilInputVO.setAjbrva2InputVO(txBodyVO);
    	
    	//發送電文
    	List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
    	
    	for(ESBUtilOutputVO esbUtilOutputVO : vos) {
    		AJBRVA2OutputVO ajbrva2OuptuptVO = esbUtilOutputVO.getAjbrva2OutputVO();
    		
    		//電文回傳取第一筆資料即可
    		if (CollectionUtils.isNotEmpty(ajbrva2OuptuptVO.getDetails())) {
    			sot711OutputVO.setDefaultFeeRate(new EsbUtil().decimalPoint(ajbrva2OuptuptVO.getDetails().get(0).getDefaultFeeRate(), 5));
    			sot711OutputVO.setBestFeeRate(new EsbUtil().decimalPoint(ajbrva2OuptuptVO.getDetails().get(0).getBestFeeRate(), 5));
    			sot711OutputVO.setPayableFeeRate(new EsbUtil().decimalPoint(ajbrva2OuptuptVO.getDetails().get(0).getPayableFeeRate(), 9));
    		}
    	}
    	return sot711OutputVO;
    }

	/*
	 * 海外債_金錢信託-表定手續費率、最優手續費率、應付前手息率資料查詢
	 * for js client
	 * 
	 * 使用電文: NJBRVX1
	 */
	public void getDefaultFeeRateDataByTrust(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getDefaultFeeRateDataByTrust(body));
	}

	/*
	 * 海外債_金錢信託-表定手續費率、最優手續費率、應付前手息率資料查詢
	 * 
	 * 使用電文: NJBRVX1
	 */
	public SOT711OutputVO getDefaultFeeRateDataByTrust(Object body) throws Exception {

		sot711InputVO = (SOT711InputVO) body;
		sot711OutputVO = new SOT711OutputVO();

		String custId = sot711InputVO.getCustId(); //身份証ID
		String bondNo = sot711InputVO.getBondNo(); //債券代號
		String priceType = sot711InputVO.getPriceType(); //限價方式
		BigDecimal entrustAmt = sot711InputVO.getEntrustAmt(); //委託價格
		BigDecimal purchaseAmt = sot711InputVO.getPurchaseAmt(); //申購面額
		String txFeeType = sot711InputVO.getTxFeeType(); //手續費議價
		String trustAcct = sot711InputVO.getTrustAcct(); //信託帳號

		//欄位檢核
		if (StringUtils.isBlank(custId) || StringUtils.isBlank(bondNo) || StringUtils.isBlank(priceType.toString()) || entrustAmt == null || purchaseAmt == null || StringUtils.isBlank(txFeeType.toString()) || StringUtils.isBlank(trustAcct)) {
			throw new JBranchException("遺漏必入欄位");
		}

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, NJBRVX1);
		esbUtilInputVO.setModule(thisClaz + new Object() {}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		txHead.setDefaultTxHead();
		esbUtilInputVO.setTxHeadVO(txHead);

		//body
		NJBRVX1InputVO txBodyVO = new NJBRVX1InputVO();
		txBodyVO.setCustId(custId);
		txBodyVO.setBondNo(bondNo);
		txBodyVO.setPriceType(priceType);
		txBodyVO.setEntrustAmt((new EsbUtil().decimalPadding(entrustAmt, 4)).toString());
		txBodyVO.setPurchaseAmt(new EsbUtil().decimalPadding(purchaseAmt, 11, 0));
		txBodyVO.setTxFeeType(txFeeType);
		txBodyVO.setTrustAcct("");
		esbUtilInputVO.setNjbrvx1InputVO(txBodyVO);

		//發送電文
		List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

		for (ESBUtilOutputVO esbUtilOutputVO : vos) {
			NJBRVX1OutputVO njbrvx1OuptuptVO = esbUtilOutputVO.getNjbrvx1OutputVO();

			if (CollectionUtils.isNotEmpty(njbrvx1OuptuptVO.getDetails())) {
				
				sot711OutputVO.setDefaultFeeRate(new EsbUtil().decimalPoint(njbrvx1OuptuptVO.getDetails().get(0).getDefaultFeeRate(), 5));
				sot711OutputVO.setBestFeeRate(new EsbUtil().decimalPoint(njbrvx1OuptuptVO.getDetails().get(0).getBestFeeRate(), 5));
				sot711OutputVO.setPayableFeeRate(new EsbUtil().decimalPoint(njbrvx1OuptuptVO.getDetails().get(0).getPayableFeeRate(), 9));
			}
		}

		return sot711OutputVO;
	}
	
	/*
	 * 海外債_快速申購-表定手續費率、最優手續費率、應付前手息率資料查詢
	 * for js client
	 * 
	 * 使用電文: NJWEEA60
	 */
	public void getDefaultFeeRateDataWeb(Object body, IPrimitiveMap header) throws Exception {
		sendRtnObject(this.getDefaultFeeRateDataWeb(body));
	}
	
	/**
     * 海外債_網行銀快速申購：表定手續費率、最優手續費率、應付前手息率資料查詢
     *
     * 使用電文: NJWEEA60
     *
     * @param body
     * @return
     */
    public SOT711OutputVO getDefaultFeeRateDataWeb(Object body) throws Exception {
        sot711InputVO = (SOT711InputVO) body;
        sot711OutputVO = new SOT711OutputVO();
        SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");

        String custId = sot711InputVO.getCustId(); //身份証ID
        String bondNo = sot711InputVO.getBondNo(); //債券代號
        String priceType = sot711InputVO.getPriceType(); //限價方式
        BigDecimal entrustAmt = sot711InputVO.getEntrustAmt(); //委託價格
        BigDecimal purchaseAmt = sot711InputVO.getPurchaseAmt(); //申購面額
        String txFeeType = sot711InputVO.getTxFeeType(); //手續費議價
        String trustAcct = sot711InputVO.getTrustAcct(); //信託帳號
        String debitAcct = sot711InputVO.getDebitAcct(); //扣款帳號

        //欄位檢核
        if(StringUtils.isBlank(custId) || StringUtils.isBlank(bondNo) || StringUtils.isBlank(priceType.toString())
                || entrustAmt==null || purchaseAmt==null || StringUtils.isBlank(txFeeType.toString())
                || StringUtils.isBlank(trustAcct) || StringUtils.isBlank(debitAcct)){
            throw new JBranchException("遺漏必入欄位");
        }

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, "NJWEEA60");
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        SOT701InputVO sot701InputVO = new SOT701InputVO();
		sot701InputVO.setCustID(custId);
		sot701InputVO.setTrustAcct(trustAcct);
		sot701InputVO.setDebitAcct(debitAcct);
		String curAcctName = sot701.getFC032659AcnoCode(sot701InputVO);// 查詢：網銀取得戶名電文FC032659
		
        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();
        esbUtilInputVO.setTxHeadVO(txHead);
        
        //body
        NJWEEA60InputVO txBodyVO = new NJWEEA60InputVO();
        txBodyVO.setCurAcctName(curAcctName);
        txBodyVO.setCustId(custId);
        txBodyVO.setBondNo(bondNo);
        txBodyVO.setPriceType(priceType);
        txBodyVO.setEntrustAmt(new EsbUtil().decimalPadding(entrustAmt, 4));
        txBodyVO.setPurchaseAmt(purchaseAmt);
        txBodyVO.setTxFeeType(txFeeType);
        txBodyVO.setTxAcct(cbsservice.checkAcctLength(debitAcct));
        txBodyVO.setChannel("WEB");
        esbUtilInputVO.setNjweea60InputVO(txBodyVO);
        
        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
        	NJWEEA60OutputVO njweea60OuptuptVO = esbUtilOutputVO.getNjweea60OutputVO();

            //電文回傳取第一筆資料即可
            if (CollectionUtils.isNotEmpty(njweea60OuptuptVO.getDetails())) {
                sot711OutputVO.setDefaultFeeRate(new EsbUtil().decimalPoint(njweea60OuptuptVO.getDetails().get(0).getDefaultFeeRate(), 5));
                sot711OutputVO.setBestFeeRate(new EsbUtil().decimalPoint(njweea60OuptuptVO.getDetails().get(0).getBestFeeRate(), 5));
                sot711OutputVO.setPayableFeeRate(new EsbUtil().decimalPoint(njweea60OuptuptVO.getDetails().get(0).getPayableFeeRate(), 9));
            }
        }

		return sot711OutputVO;
	}
}
