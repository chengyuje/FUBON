package com.systex.jbranch.app.server.fps.sot708;

import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SIINFOVO;
import com.systex.jbranch.app.common.fps.table.TBPRD_SIVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_FCI_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_SI_TRADE_DVO;
import com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAINVO;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1OutputVODetials;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq20.SDACTQ20InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq20.SDACTQ20OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq3.SDACTQ3InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq3.SDACTQ3OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq4.SDACTQ4InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq4.SDACTQ4OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq4.SDACTQ4OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq5.SDACTQ5InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sdactq5.SDACTQ5OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.SI_ASSETS_DATA;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.SI_PURCHASE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.SI_REDEEM;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.FCI_PURCHASE;

/**
 * Created by SebastianWu on 2016/9/26.
 * 
 * revised by Cathy Tang on 2016/11/07
 * 1: 上行電文數字格式修正：數字格式呼叫ESBUtil.decimalPadding：去除小數點,並用0向右補足小數位長度
 * 2: 加上贖回電文
 * 
 */
@Component("sot708")
@Scope("request")
public class SOT708 extends EsbUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataAccessManager dam = null;
    private SOT708InputVO sot708InputVO;
    private SOT708OutputVO sot708OutputVO;
    private String thisClaz = this.getClass().getSimpleName() + ".";
    
    SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

    /* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;

    private void checkError(Object obj) throws JBranchException {
        String errCode = null;
        String errTxt = null;

        if (obj instanceof SDACTQ3OutputVO) {
            errCode = ((SDACTQ3OutputVO) obj).getERRID();
            errTxt = ((SDACTQ3OutputVO) obj).getERRTXT();
        } else if (obj instanceof SDACTQ5OutputVO) {
        	errCode = ((SDACTQ5OutputVO) obj).getERRID();
            errTxt = ((SDACTQ5OutputVO) obj).getERRTXT();
        } else if (obj instanceof SDACTQ20OutputVO) {
            errCode = ((SDACTQ20OutputVO) obj).getERRID();
            errTxt = ((SDACTQ20OutputVO) obj).getERRTXT();
        }

        if (StringUtils.isNotBlank(errCode) && !StringUtils.equals("0000", errCode)) {
            throw new JBranchException("錯誤代碼: " + errCode + " - " + errTxt);
        }
    }

    private MainInfoBean getMainInfo(String tradeSeq) throws JBranchException {
        String sql = null;

        MainInfoBean mainBean = new MainInfoBean();
        TBSOT_TRADE_MAINVO mainVO = new TBSOT_TRADE_MAINVO();
        TBSOT_SI_TRADE_DVO siTradeDVo = new TBSOT_SI_TRADE_DVO();
        TBORG_MEMBERVO memberVO = new TBORG_MEMBERVO();
        TBPRD_SIINFOVO siInfoVO = new TBPRD_SIINFOVO();
        TBPRD_SIVO siVO = new TBPRD_SIVO();

        mainBean.setTbsotTradeMainvo(mainVO);
        mainBean.setTbsotSiTradeDvo(siTradeDVo);
        mainBean.setTborgMembervo(memberVO);
        mainBean.setTbprdSiInfovo(siInfoVO);
        mainBean.setTbprdSivo(siVO);

        sql = new StringBuffer(
                "select  M.CUST_ID, " +
                        "        M.REC_SEQ, " +
                        "        M.BRANCH_NBR, " +
                        "        D.*, " +
                        "		 S.VALUE_DATE, " +
                        "		 SI.HNWC_BUY " +
                        "  from TBSOT_TRADE_MAIN M " +
                        " 	inner join TBSOT_SI_TRADE_D D on D.TRADE_SEQ = M.TRADE_SEQ " +
                        " 	left outer join TBPRD_SIINFO S on S.PRD_ID = D.PROD_ID " +
                        " 	left outer join TBPRD_SI SI on SI.PRD_ID = D.PROD_ID " +
                        " where M.TRADE_SEQ = :TRADE_SEQ "
        ).toString();

        dam = getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        condition.setObject("TRADE_SEQ", tradeSeq);
        condition.setQueryString(sql);

        List<Map> results = dam.exeQuery(condition);

        if (CollectionUtils.isNotEmpty(results)) {
            mainVO.setCUST_ID((String) results.get(0).get("CUST_ID"));
            mainVO.setREC_SEQ((String) results.get(0).get("REC_SEQ"));
            mainVO.setBRANCH_NBR((String) results.get(0).get("BRANCH_NBR"));
            siInfoVO.setVALUE_DATE((Timestamp) results.get(0).get("VALUE_DATE"));
            siTradeDVo.setPROD_ID((String) results.get(0).get("PROD_ID"));
            siTradeDVo.setDEBIT_ACCT((String) results.get(0).get("DEBIT_ACCT"));
            siTradeDVo.setPROD_ACCT((String) results.get(0).get("PROD_ACCT"));
            siTradeDVo.setPURCHASE_AMT((BigDecimal) results.get(0).get("PURCHASE_AMT"));
            siTradeDVo.setNARRATOR_ID((String) results.get(0).get("NARRATOR_ID"));
            siTradeDVo.setRECEIVED_NO((String) results.get(0).get("RECEIVED_NO"));
            siTradeDVo.setENTRUST_TYPE(CharUtils.toString((Character) results.get(0).get("ENTRUST_TYPE")));
            siTradeDVo.setREF_VAL_DATE((Timestamp) results.get(0).get("REF_VAL_DATE"));
            siTradeDVo.setREF_VAL((BigDecimal) results.get(0).get("REF_VAL"));
            siTradeDVo.setENTRUST_AMT((BigDecimal) results.get(0).get("ENTRUST_AMT"));
            siTradeDVo.setBOSS_ID((String)results.get(0).get("BOSS_ID"));
            siTradeDVo.setAUTH_ID(ObjectUtils.toString(results.get(0).get("AUTH_ID")));
            siVO.setHNWC_BUY(ObjectUtils.toString(results.get(0).get("HNWC_BUY")));
        }
       
        return mainBean;
    }

    /**
     * SI申購確認
     *
     * 使用電文: SDACTQ3
     *
     * @param body
     * @throws Exception
     */
    public void verifyESBPurchaseSI(Object body) throws Exception {
        sot708InputVO = (SOT708InputVO) body;

        String checkType = sot708InputVO.getCheckType();    //電文確認碼
        String tradeSeq = sot708InputVO.getTradeSeq();      //下單交易序號

        //欄位檢核
        if (StringUtils.isBlank(checkType) || StringUtils.isBlank(tradeSeq)) {
            throw new JBranchException("電文確認碼或下單交易序號未輸入");
        }

        //由交易序號取得交易資料
        MainInfoBean main = getMainInfo(tradeSeq);

        //fetch java bean
        TBSOT_TRADE_MAINVO mainVO = main.getTbsotTradeMainvo();
        TBSOT_SI_TRADE_DVO siTradeDVO = main.getTbsotSiTradeDvo();
        TBORG_MEMBERVO memberVO = main.getTborgMembervo();
        TBPRD_SIVO siVO = main.getTbprdSivo();

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, SI_PURCHASE);
        esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        esbUtilInputVO.setTxHeadVO(txHead);
        txHead.setDefaultTxHead();

        //body
        SDACTQ3InputVO txBodyVO = new SDACTQ3InputVO();
        esbUtilInputVO.setSdactq3InputVO(txBodyVO);
        txBodyVO.setIVID(mainVO.getCUST_ID());              //客戶證號
        txBodyVO.setIVBRH(mainVO.getBRANCH_NBR());           //推薦分行別
        txBodyVO.setRECBRH(mainVO.getBRANCH_NBR());          //錄音分行別
        txBodyVO.setSDPRD(siTradeDVO.getPROD_ID());         //產品編號
        txBodyVO.setIVCUAC(siTradeDVO.getDEBIT_ACCT());     //活存帳號
        txBodyVO.setIVTDAC(siTradeDVO.getPROD_ACCT());      //定存帳號
        txBodyVO.setIVAMT2(new EsbUtil().decimalPadding(siTradeDVO.getPURCHASE_AMT(), 2));   //簽約金額
        txBodyVO.setAGENT(siTradeDVO.getNARRATOR_ID());     //解說專員
        txBodyVO.setRECNO(mainVO.getREC_SEQ());             //錄音序號 
        txBodyVO.setTXBOSS(siTradeDVO.getBOSS_ID());        //覆核主管
//        if(StringUtils.equals("Y", siVO.getHNWC_BUY())) { //商品限高資產申購註記
			txBodyVO.setTRADERID(siTradeDVO.getAUTH_ID()); //授權交易人員
//		}
        
        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
            SDACTQ3OutputVO sdactq3OutputVO = esbUtilOutputVO.getSdactq3OutputVO();

            //回傳訊息檢查
            checkError(sdactq3OutputVO);
        }
    }

    /**
     * SI贖回確認
     *
     * 使用電文: SIBRVB9
     * #2224_SI贖回SDACTQ5推薦分行別邏輯調整
     * @param body
     * @throws Exception
     */
    public void verifyESBRedeemSI(Object body) throws Exception {
        sot708InputVO = (SOT708InputVO) body;

        String checkType = sot708InputVO.getCheckType();    //電文確認碼
        String tradeSeq = sot708InputVO.getTradeSeq();      //下單交易序號        
        
        //欄位檢核
        if (StringUtils.isBlank(checkType) || StringUtils.isBlank(tradeSeq)) {
            throw new JBranchException("電文確認碼或下單交易序號未輸入");
        }

      //由交易序號取得交易資料
        MainInfoBean main = getMainInfo(tradeSeq);

        //fetch java bean
        TBSOT_TRADE_MAINVO mainVO = main.getTbsotTradeMainvo();
        TBSOT_SI_TRADE_DVO siTradeDVO = main.getTbsotSiTradeDvo();
        TBORG_MEMBERVO memberVO = main.getTborgMembervo();
        TBPRD_SIINFOVO siInfoVO = main.getTbprdSiInfovo();

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, SI_REDEEM);
        esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        esbUtilInputVO.setTxHeadVO(txHead);
        txHead.setDefaultTxHead();

        //body
        SDACTQ5InputVO txBodyVO = new SDACTQ5InputVO();
        esbUtilInputVO.setSdactq5InputVO(txBodyVO);
        txBodyVO.setTYPE(getType((Date) siInfoVO.getVALUE_DATE()));	//贖回類型
        txBodyVO.setIVID(mainVO.getCUST_ID());              //客戶證號
        txBodyVO.setSDPRD(siTradeDVO.getPROD_ID());			//產品編號
        txBodyVO.setIVBRH(null == sot708InputVO.getIvBrh() ?  mainVO.getBRANCH_NBR() : sot708InputVO.getIvBrh());           //推薦分行別
        txBodyVO.setIVRNO(siTradeDVO.getRECEIVED_NO());     //收件編號
        txBodyVO.setIVCUAC(siTradeDVO.getDEBIT_ACCT());     //活存帳號
        txBodyVO.setIVTDAC(siTradeDVO.getPROD_ACCT());      //定存帳號
        txBodyVO.setOPTION(siTradeDVO.getENTRUST_TYPE());	//限價方式
        txBodyVO.setSDDTE(sdfYYYYMMDD.format((Date) siTradeDVO.getREF_VAL_DATE()));	//參考贖回報價日期
        txBodyVO.setSDAMT3(new EsbUtil().decimalPadding(siTradeDVO.getREF_VAL(), 4));   //參考贖回報價
        //限價方式非市價，才需要放值
        if(!StringUtils.equals("4", siTradeDVO.getENTRUST_TYPE())) {
        	txBodyVO.setSDSELL(new EsbUtil().decimalPadding(siTradeDVO.getENTRUST_AMT(), 4));   //委託贖回價格
        }

        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);

        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
            SDACTQ5OutputVO sdactq5OutputVO = esbUtilOutputVO.getSdactq5OutputVO();

            //回傳訊息檢查
            checkError(sdactq5OutputVO);
        }
    }
    
    /**
     * 贖回類型為：(系統日期 >= VALUE_DATE) ? "2" : "1"
     * 1.活轉定前 2.活轉定後
     * 拿實際鍵機日和產品起息日相比
     * 		鍵機日 >= 起息日 請送 2.活轉定後
     * 		鍵機日 <  起息日 請送 1.活轉定前
     * @param valueDate
     * @return
     */
    private String getType(Date valueDate) {
    	Date sysDate = new Date();
    	
    	String strSysDate = sdfYYYYMMDD.format((Date) sysDate);
    	String strValueDate = sdfYYYYMMDD.format((Date) valueDate);
    	
    	return (strSysDate.compareTo(strValueDate) < 0) ? "1" : "2";
    	
    }
    
    /**
     * SI產品庫存資料查詢
     * for js client
     *
     * 使用電文: SDACTQ4
     *
     * @param body
     * @param header
     * @throws Exception
     */
    public void getCustAssetSIData(Object body, IPrimitiveMap header) throws Exception {
        sendRtnObject(this.getCustAssetSIData(body));
    }

    /**
     * SI產品庫存資料查詢
     *
     * 使用電文: SDACTQ4
     *
     * @param body
     * @throws Exception
     */
    public SOT708OutputVO getCustAssetSIData(Object body) throws Exception {
        sot708InputVO = (SOT708InputVO) body;
        sot708OutputVO = new SOT708OutputVO();

        String custID = sot708InputVO.getCustId();      //客戶ID
        //optional params
        String prodID = sot708InputVO.getProdId();      //商品代號
        String prodName = sot708InputVO.getProdName();  //商品名稱
        Date   startDate = sot708InputVO.getStartDate();//申購起日
        Date   endDate = sot708InputVO.getEndDate();    //申購迄日

        //欄位檢核
        if(StringUtils.isBlank(custID)){
            throw new JBranchException("客戶ID未輸入");
        }

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, SI_ASSETS_DATA);
        esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        esbUtilInputVO.setTxHeadVO(txHead);
        txHead.setDefaultTxHead();

        //body
        SDACTQ4InputVO txBodyVO = new SDACTQ4InputVO();
        esbUtilInputVO.setSdactq4InputVO(txBodyVO);
        txBodyVO.setIVID(custID);

        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
        List<CustAssetSIVO> assetList = new ArrayList< CustAssetSIVO >();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
            SDACTQ4OutputVO sdactq4OutputVO = esbUtilOutputVO.getSdactq4OutputVO();
        	String id = sdactq4OutputVO.getID();
        	
            List<SDACTQ4OutputDetailsVO> details = sdactq4OutputVO.getDetails();
            
            //根據產品類別以及查詢條件回傳庫存資料
            //List custAssetsDatas = custAssetSIDataUpdate(sdactq4OutputVO, prodID, prodName, startDate, endDate); 2017/08/02 將程式納入同個method
            
            if(CollectionUtils.isNotEmpty(details)){
                for(SDACTQ4OutputDetailsVO detail : details){

                	//排除條件
                    //若有輸入產品ID，則只取該產品ID
                	Boolean failProdID = (StringUtils.isNotBlank(prodID) && !detail.getSDPRD().contains(prodID))
                            ? Boolean.TRUE : Boolean.FALSE;

                    //若有輸入產品名稱，則取得產品名稱包含傳入參數資料
                    String prdcnm = detail.getPRDCNM();
                    Boolean failProdNAme = (StringUtils.isNotBlank(prodName) &&
                            StringUtils.isNotBlank(prdcnm) && !prdcnm.contains(prodName))
                            ? Boolean.TRUE : Boolean.FALSE;

                    Date invend = (StringUtils.isNotBlank(detail.getINVEND())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getINVEND()) : null;
                    //若有輸入申購起日，則申購日期需大於等於申購起日
                    Boolean failStartDate = (startDate!=null && invend!=null && invend.before(startDate))
                            ? Boolean.TRUE : Boolean.FALSE;

                    //若有輸入申購迄日，則申購日期需小於等於申購迄日
                    Boolean failEndDate = (endDate!=null && invend!=null && invend.after(endDate))
                            ? Boolean.TRUE : Boolean.FALSE;

                    //符合資料放入庫存VO中
                    if(!failProdID && !failProdNAme && !failStartDate && !failEndDate){
                        CustAssetSIVO asset = new CustAssetSIVO();
                        asset.setID(id);                                //客戶證號
                        asset.setIVRNO(detail.getIVRNO());              //收件編號　
                        asset.setSDPRD(detail.getSDPRD());              //商品代號
                        asset.setPRDCNM(detail.getPRDCNM());            //商品名稱
                        asset.setIVCUCY(detail.getIVCUCY());            //商品幣別
                        asset.setIVAMT2(new EsbUtil().decimalPoint(detail.getIVAMT2(), 2));            //庫存金額
                        asset.setINVEND((StringUtils.isNotBlank(detail.getINVEND())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getINVEND()) : null);            //交易日
                        asset.setDEPSTR((StringUtils.isNotBlank(detail.getDEPSTR())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getDEPSTR()) : null);            //發行日 起息日
                        asset.setDEPEND((StringUtils.isNotBlank(detail.getDEPEND())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getDEPEND()) : null);            //到期日
                        //最新配息日
                        if(StringUtils.isNotBlank(detail.getPLADTE()) && !StringUtils.equals("20000000", detail.getPLADTE())) { 
                        	asset.setPLADTE(new SimpleDateFormat("yyyyMMdd").parse(detail.getPLADTE()));
                        } else {
                        	asset.setPLADTE(null);	
                        }
                        asset.setINTRATE(new EsbUtil().decimalPoint(detail.getINTRATE(), 4));          //最新配息率
                        asset.setACMINTRATE(new EsbUtil().decimalPoint(detail.getACMINTRATE(), 4));    //累積配息率
                        asset.setSDAMT3(new EsbUtil().decimalPoint(detail.getSDAMT3(), 4));            //最新報價
                        asset.setINTAMT(new EsbUtil().decimalPoint(detail.getINTAMT(), 4));            //含息報價
                        asset.setINTROR(new EsbUtil().decimalPoint(detail.getINTROR(), 4));            //含息報酬率
                        asset.setINTROR_S(detail.getINTROR_S());		//含息報酬率正負
                        asset.setNMVLU(detail.getNMVLU());				//產品風險等級
                        asset.setF01NPD(detail.getF01NPD());            //到期保本率
                        asset.setMTYEAR(new EsbUtil().decimalPoint(detail.getMTYEAR(), 2));            //到期年限
                        asset.setLRDAMT(new EsbUtil().decimalPoint(detail.getLRDAMT(), 2));            //最低贖回金額
                        asset.setRDUTAMT(new EsbUtil().decimalPoint(detail.getRDUTAMT(), 0));          //贖回單位金額
                        asset.setINTFR(new EsbUtil().decimalPoint(detail.getINTFR(), 0));              //配息頻率
                        asset.setSDAMT3DATE((StringUtils.isNotBlank(detail.getSDAMT3DATE())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getSDAMT3DATE()) : null);	//最新報價日
                        asset.setIVBRH(detail.getIVBRH());              //分行別(收件行)
                        asset.setIVCUAC(detail.getIVCUAC());            //扣款帳號
                        asset.setIVTDAC(detail.getIVTDAC());            //組合式商品帳號
                        asset.setTYPE(detail.getTYPE());				//狀態
                        
                        assetList.add(asset);
                    }
                }
            }
            sot708OutputVO.setCustAssetSIList(assetList);
        }

        return sot708OutputVO;
    }

    /**
     * 根據產品類別以及查詢條件回傳庫存資料
     *
     * @param sdactq4OutputVO
     * @param prodID
     * @param prodName
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException 
     */
    private List<CustAssetSIVO> custAssetSIDataUpdate(SDACTQ4OutputVO sdactq4OutputVO, String prodID,
                                                      String prodName, Date startDate, Date endDate) throws JBranchException, ParseException {
        List<CustAssetSIVO> assetList = new ArrayList< CustAssetSIVO >();
        List<SDACTQ4OutputDetailsVO> details = sdactq4OutputVO.getDetails();
        String id = sdactq4OutputVO.getID();

        if(CollectionUtils.isNotEmpty(details)){
            for(SDACTQ4OutputDetailsVO detail : details){
                //排除條件
                //若有輸入產品ID，則只取該產品ID
//                Boolean failProdID = (StringUtils.isNotBlank(prodID) && !StringUtils.equals(prodID, detail.getSDPRD()))
            	Boolean failProdID = (StringUtils.isNotBlank(prodID) && !detail.getSDPRD().contains(prodID))
                        ? Boolean.TRUE : Boolean.FALSE;

                //若有輸入產品名稱，則取得產品名稱包含傳入參數資料
                String prdcnm = detail.getPRDCNM();
                Boolean failProdNAme = (StringUtils.isNotBlank(prodName) &&
                        StringUtils.isNotBlank(prdcnm) && !prdcnm.contains(prodName))
                        ? Boolean.TRUE : Boolean.FALSE;

                Date invend = (StringUtils.isNotBlank(detail.getINVEND())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getINVEND()) : null;
                //若有輸入申購起日，則申購日期需大於等於申購起日
                Boolean failStartDate = (startDate!=null && invend!=null && invend.before(startDate))
                        ? Boolean.TRUE : Boolean.FALSE;

                //若有輸入申購迄日，則申購日期需小於等於申購迄日
                Boolean failEndDate = (endDate!=null && invend!=null && invend.after(endDate))
                        ? Boolean.TRUE : Boolean.FALSE;

                //符合資料放入庫存VO中
                if(!failProdID && !failProdNAme && !failStartDate && !failEndDate){
                    CustAssetSIVO asset = new CustAssetSIVO();
                    asset.setID(id);                                //客戶證號
                    asset.setIVRNO(detail.getIVRNO());              //收件編號　
                    asset.setSDPRD(detail.getSDPRD());              //商品代號
                    asset.setPRDCNM(detail.getPRDCNM());            //商品名稱
                    asset.setIVCUCY(detail.getIVCUCY());            //商品幣別
                    asset.setIVAMT2(new EsbUtil().decimalPoint(detail.getIVAMT2(), 2));            //庫存金額
                    asset.setINVEND((StringUtils.isNotBlank(detail.getINVEND())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getINVEND()) : null);            //交易日
                    asset.setDEPSTR((StringUtils.isNotBlank(detail.getDEPSTR())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getDEPSTR()) : null);            //發行日 起息日
                    asset.setDEPEND((StringUtils.isNotBlank(detail.getDEPEND())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getDEPEND()) : null);            //到期日
                    asset.setPLADTE((StringUtils.isNotBlank(detail.getPLADTE())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getPLADTE()) : null);            //最新配息日
                    asset.setINTRATE(new EsbUtil().decimalPoint(detail.getINTRATE(), 4));          //最新配息率
                    asset.setACMINTRATE(new EsbUtil().decimalPoint(detail.getACMINTRATE(), 4));    //累積配息率
                    asset.setSDAMT3(new EsbUtil().decimalPoint(detail.getSDAMT3(), 4));            //最新報價
                    asset.setINTAMT(new EsbUtil().decimalPoint(detail.getINTAMT(), 4));            //含息報價
                    asset.setINTROR(new EsbUtil().decimalPoint(detail.getINTROR(), 4));            //含息報酬率
                    asset.setNMVLU(detail.getNMVLU());              //產品風險等級
                    asset.setF01NPD(detail.getF01NPD());            //到期保本率
                    asset.setMTYEAR(new EsbUtil().decimalPoint(detail.getMTYEAR(), 2));            //到期年限
                    asset.setLRDAMT(new EsbUtil().decimalPoint(detail.getLRDAMT(), 2));            //最低贖回金額
                    asset.setRDUTAMT(new EsbUtil().decimalPoint(detail.getRDUTAMT(), 0));          //贖回單位金額
                    asset.setINTFR(new EsbUtil().decimalPoint(detail.getINTFR(), 0));              //配息頻率
                    asset.setSDAMT3DATE((StringUtils.isNotBlank(detail.getSDAMT3DATE())) ? new SimpleDateFormat("yyyyMMdd").parse(detail.getSDAMT3DATE()) : null);	//最新報價日
                    asset.setIVBRH(detail.getIVBRH());              //分行別(收件行)
                    asset.setIVCUAC(detail.getIVCUAC());            //扣款帳號
                    asset.setIVTDAC(detail.getIVTDAC());            //組合式商品帳號
                    asset.setTYPE(detail.getTYPE());				//狀態
                    
                    assetList.add(asset);
                }
            }
        }

//        if(CollectionUtils.isEmpty(assetList)){
//        	logger.error("SOT708查無資料:"+prodID);
//            throw new JBranchException("ehl_01_common_009");
//        }

        return assetList;
    }
    
    private List<Map<String, Object>> getFCIInfo(String tradeSeq) throws JBranchException {
    	dam = getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	StringBuffer sql = new StringBuffer();

        sql.append("SELECT M.CUST_ID, M.REC_SEQ, M.BRANCH_NBR, M.IS_OBU, D.*, ");
        sql.append(" CASE WHEN D.PROD_CURR = 'CNH' THEN 'CNY' ELSE D.PROD_CURR END AS PROD_CURR_C, ");
        sql.append(" TO_CHAR(D.TRADE_DATE, 'YYYYMMDD') AS TRADE_DATE_C, ");
        sql.append(" TO_CHAR(D.VALUE_DATE, 'YYYYMMDD') AS VALUE_DATE_C, ");
        sql.append(" TO_CHAR(D.SPOT_DATE, 'YYYYMMDD') AS SPOT_DATE_C, ");
        sql.append(" TO_CHAR(D.EXPIRE_DATE, 'YYYYMMDD') AS EXPIRE_DATE_C ");
        sql.append(" FROM TBSOT_TRADE_MAIN M ");
        sql.append(" LEFT JOIN TBSOT_FCI_TRADE_D D on D.TRADE_SEQ = M.TRADE_SEQ ");
        sql.append(" WHERE M.TRADE_SEQ = :TRADE_SEQ ");       
        condition.setObject("TRADE_SEQ", tradeSeq);
        condition.setQueryString(sql.toString());

        List<Map<String, Object>> results = dam.exeQuery(condition);    
        return results;
    }
    
    /**
     * FCI申購檢核確認電文
     * 使用電文: SDACTQ20
     * @param body
     * @throws Exception
     */
    public void verifyESBPurchaseFCI(Object body) throws Exception {
        sot708InputVO = (SOT708InputVO) body;

        String checkType = sot708InputVO.getCheckType();    //電文確認碼
        String tradeSeq = sot708InputVO.getTradeSeq();      //下單交易序號

        //欄位檢核
        if (StringUtils.isBlank(checkType) || StringUtils.isBlank(tradeSeq)) {
            throw new JBranchException("電文確認碼或下單交易序號未輸入");
        }

        //由交易序號取得交易資料
        List<Map<String, Object>> fciInfo = getFCIInfo(tradeSeq);

        //init util
        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, FCI_PURCHASE);
        esbUtilInputVO.setModule(thisClaz + new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        esbUtilInputVO.setTxHeadVO(txHead);
        txHead.setDefaultTxHead();

        //body
        SDACTQ20InputVO txBodyVO = new SDACTQ20InputVO();
        esbUtilInputVO.setSdactq20InputVO(txBodyVO);
        txBodyVO.setCOMFIRM(checkType);
        txBodyVO.setINV_TARGET(ObjectUtils.toString(fciInfo.get(0).get("TARGET_CURR_ID")));
        txBodyVO.setCURR_PAIR(ObjectUtils.toString(fciInfo.get(0).get("PROD_CURR_C")));
        txBodyVO.setRISKCATE_ID(ObjectUtils.toString(fciInfo.get(0).get("PROD_RISK_LV")));
        txBodyVO.setPROD_MONS(new BigDecimal(fciInfo.get(0).get("MON_PERIOD").toString()));
        txBodyVO.setMIN_BUY_AMT(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("PROD_MIN_BUY_AMT"), 2));
        txBodyVO.setUNIT_BUY_AMT(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("PROD_MIN_GRD_AMT"), 0));
        txBodyVO.setSTRIKE_PRICE(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("STRIKE_PRICE"), 4));
        txBodyVO.setFA_PROFIT(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("RM_PROFEE"), 6));
        txBodyVO.setFTP_AMT(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("FTP_RATE"), 4)); 
        txBodyVO.setPURCHASE_DATE(ObjectUtils.toString(fciInfo.get(0).get("TRADE_DATE_C")));
        txBodyVO.setCHARGE_DATE(ObjectUtils.toString(fciInfo.get(0).get("VALUE_DATE_C")));
        txBodyVO.setEXCHANGE_DATE(ObjectUtils.toString(fciInfo.get(0).get("SPOT_DATE_C")));
        txBodyVO.setDUE_DATE(ObjectUtils.toString(fciInfo.get(0).get("EXPIRE_DATE_C")));
        txBodyVO.setPROD_DAYS(ObjectUtils.toString(fciInfo.get(0).get("INT_DATES")));
        txBodyVO.setGT_STRIKE_RATE(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("PRD_PROFEE_RATE"), 2));
        txBodyVO.setGT_DIVIDEND_AMT(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("PRD_PROFEE_AMT"), 2));
        txBodyVO.setLT_STRIKE_RATE(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("LESS_PROFEE_RATE"), 2));
        txBodyVO.setLT_DIVIDENT_AMT(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("LESS_PROFEE_AMT"), 2));
        txBodyVO.setOBU_YN(ObjectUtils.toString(fciInfo.get(0).get("IS_OBU")));
        txBodyVO.setTRADER_CHARGE(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("TRADER_CHARGE"), 2));
        txBodyVO.setIVID(ObjectUtils.toString(fciInfo.get(0).get("CUST_ID")));			//客戶證號
        txBodyVO.setIVBRH(ObjectUtils.toString(fciInfo.get(0).get("BRANCH_NBR")));      //推薦分行別
        txBodyVO.setRECBRH(ObjectUtils.toString(fciInfo.get(0).get("BRANCH_NBR")));     //錄音分行別
        txBodyVO.setSDPRD("");         													//產品編號
        txBodyVO.setIVCUAC(ObjectUtils.toString(fciInfo.get(0).get("DEBIT_ACCT")));     //活存帳號 扣款帳號
        txBodyVO.setIVTDAC(ObjectUtils.toString(fciInfo.get(0).get("PROD_ACCT")));      //定存帳號 組合式商品帳號
        txBodyVO.setIVAMT2(new EsbUtil().decimalPadding((BigDecimal)fciInfo.get(0).get("PURCHASE_AMT"), 2));   //申購金額
        txBodyVO.setAGENT(ObjectUtils.toString(fciInfo.get(0).get("NARRATOR_ID")));     //解說專員
        txBodyVO.setRECNO(ObjectUtils.toString(fciInfo.get(0).get("REC_SEQ")));         //錄音序號 
        txBodyVO.setTXBOSS(ObjectUtils.toString(fciInfo.get(0).get("BOSS_ID")));        //覆核主管
        txBodyVO.setTRADERID(ObjectUtils.toString(fciInfo.get(0).get("AUTH_ID"))); 		//授權交易人員
        
        //發送電文
        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
            SDACTQ20OutputVO sdactq20OutputVO = esbUtilOutputVO.getSdactq20OutputVO();

            //回傳訊息檢查
            checkError(sdactq20OutputVO);
        }
    }
}