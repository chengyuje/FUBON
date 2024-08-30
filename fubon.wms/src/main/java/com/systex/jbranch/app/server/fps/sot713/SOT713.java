package com.systex.jbranch.app.server.fps.sot713;

import static com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer.ESB_TYPE;
import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_CLOSING_PRICE;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBPRD_ETF_CLOSING_PRICEPK;
import com.systex.jbranch.app.common.fps.table.TBPRD_ETF_CLOSING_PRICEVO;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr098n.NR098NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr098n.NR098NOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nr098n.NR098NOutputVODetails;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Created by SebastianWu on 2016/10/18.
 */
@Repository("sot713")
@Scope("prototype")
public class SOT713 extends BizLogic {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String thisClaz = this.getClass().getSimpleName()+".";
    private DataAccessManager dam = null;
    private EsbUtil esbUtil;

    /**
     * 海外ETF/股票收盤價資料取得
     * AM09:00~ PM12:00每整點發一次，每日四次抓回存在DB中
     *
     * 使用電文: NR098N
     * @throws Exception 
     *
     * @throws Exception
     */
    public void getETFClosingPrice(Object body, IPrimitiveMap<?> header) throws JBranchException, Exception {   
    	esbUtil = (EsbUtil) PlatformContext.getBean("esbUtil");
    	
        //init util
        ESBUtilInputVO esbUtilInputVO = esbUtil.getTxInstance(ESB_TYPE, ETF_CLOSING_PRICE);
        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

        //head
        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
        txHead.setDefaultTxHead();        
        esbUtilInputVO.setTxHeadVO(txHead);


        //body
        NR098NInputVO txBodyVO = new NR098NInputVO();
        esbUtilInputVO.setNr098NInputVO(txBodyVO);

        //發送電文
        List<ESBUtilOutputVO> vos = esbUtil.send(esbUtilInputVO);        
        NR098NOutputVO nr098NOutputVO = new NR098NOutputVO();
        
        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
            nr098NOutputVO = esbUtilOutputVO.getNr098NOutputVO();
            
            List<NR098NOutputVODetails> details = nr098NOutputVO.getDetails();
	        details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NR098NOutputVODetails>() : details;
            
	        if(CollectionUtils.isNotEmpty(details)) {
	        	mergerETFClosingPrice(details);
	        }
        }
    }
    

    /**
     * 將海外ETF/股票收盤價資料與DB做Merge
     *
     * @param nr098NOutputVO
     * @throws ParseException 
     */
    private void mergerETFClosingPrice(List<NR098NOutputVODetails> details) throws JBranchException, ParseException {
    	dam = getDataAccessManager();
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Timestamp sysdate = new Timestamp(sd.parse(sd.format(new Date())).getTime());
        TBPRD_ETF_CLOSING_PRICEVO vo = null;

        for(NR098NOutputVODetails detail : details) {
        	detail.setProductNo(detail.getProductNo() == null ? null : detail.getProductNo().replaceAll("\\s*　*$", ""));
	        //ProductNo非null或空白字元(包含全形空白字元)時進入
	        if(StringUtils.isBlank(detail.getProductNo()))
	        	return;
	        
	        TBPRD_ETF_CLOSING_PRICEPK pk = new TBPRD_ETF_CLOSING_PRICEPK();
        	pk.setPRODUCT_NO(detail.getProductNo());
            pk.setSOU_DATE(sysdate);
            vo = (TBPRD_ETF_CLOSING_PRICEVO) dam.findByPKey(TBPRD_ETF_CLOSING_PRICEVO.TABLE_UID, pk);
            
            if(vo != null) {
            	logger.info("detail : " + detail.getExchangeNo().trim());
            	vo.setEXCHANGE_NO(detail.getExchangeNo().trim());
	            vo.setINSURANCE_NAME(detail.getInsuranceName().trim());
	            vo.setCUR_CODE(detail.getCurCode().trim());
	            vo.setTRX_MARKET(detail.getTrxMarket().trim());
	            vo.setEND_TIME(detail.getEndtime().trim());
	            vo.setDATE_08(StringUtils.isEmpty(detail.getDate08()) ? null : new Timestamp(sd.parse(detail.getDate08()).getTime()));
	            vo.setCUR_AMT(new EsbUtil().decimalPoint(detail.getCurAmt(), 6));
	            vo.setLOW_MOUNT(new EsbUtil().decimalPoint(detail.getLowmount(), 0));
	            vo.setLOW_AMT(new EsbUtil().decimalPoint(detail.getLowAmt(), 0));
	            vo.setTRUST_KIND(detail.getTrustKind().trim());
	            vo.setPRO_CUS(detail.getProcus().trim());
	            vo.setPRODUCT_RISK(detail.getProductRisk().trim());
	            vo.setUSE_POINT(detail.getUsepoint().trim());
	            vo.setPRODUCT_TYPE_2(detail.getProductType2().trim());
	            vo.setTRADE_TYPE(detail.getTradeType().trim());
	            vo.setW8BEN_CODE(detail.getW8bencode().trim());
	            vo.setMARKET_DATE(StringUtils.isEmpty(detail.getMarketDate()) ? null : new Timestamp(sd.parse(detail.getMarketDate()).getTime()));
	            vo.setPD_TYPE(detail.getPDType1().trim());
	            vo.setLMT_PRICE_BUY(new EsbUtil().decimalPoint(detail.getLmtPriceBuy(), 6));
	            vo.setLMT_PRICE_SELL(new EsbUtil().decimalPoint(detail.getLmtPriceSell(), 6));
	            vo.setPROD_COUNTRY(detail.getProdCountry().trim());
	            vo.setPRODUT_COM(detail.getProdutCom().trim());
	            vo.setPRICD_TYPE(detail.getPricdType().trim());
	            vo.setSTART_TIME(detail.getStartTime().trim());
	            vo.setPRODUCT_TYPE(detail.getProductType().trim());
	            vo.setFUB_START_TIME(detail.getFubStartTime().trim());
	            vo.setFUB_END_TIME(detail.getFubEndTime().trim());
	            vo.setTRX_MARKET_CODE(detail.getTrxMarketCode().trim());
	            vo.setDIVIDE_START_TIME(detail.getDivideStartTime().trim());
	            vo.setDIVIDE_END_TIME(detail.getDivideEndTime().trim());
	            vo.setSELL_BY_MKT(detail.getSellByMKT().trim());
	            vo.setSHORT_PD_NAME(detail.getShortPdName().trim());
	            vo.setDBU_OBU(detail.getDbuObu().trim());
	            
                dam.update(vo);
            } else {
                vo = new TBPRD_ETF_CLOSING_PRICEVO(pk);
                vo.setcomp_id(pk);
                                
                vo.setEXCHANGE_NO(detail.getExchangeNo().trim());
	            vo.setINSURANCE_NAME(detail.getInsuranceName().trim());
	            vo.setCUR_CODE(detail.getCurCode().trim());
	            vo.setTRX_MARKET(detail.getTrxMarket().trim());
	            vo.setEND_TIME(detail.getEndtime().trim());
	            vo.setDATE_08(StringUtils.isEmpty(detail.getDate08()) ? null : new Timestamp(sd.parse(detail.getDate08()).getTime()));
	            vo.setCUR_AMT(new EsbUtil().decimalPoint(detail.getCurAmt(), 6));
	            vo.setLOW_MOUNT(new EsbUtil().decimalPoint(detail.getLowmount(), 0));
	            vo.setLOW_AMT(new EsbUtil().decimalPoint(detail.getLowAmt(), 0));
	            vo.setTRUST_KIND(detail.getTrustKind().trim());
	            vo.setPRO_CUS(detail.getProcus().trim());
	            vo.setPRODUCT_RISK(detail.getProductRisk().trim());
	            vo.setUSE_POINT(detail.getUsepoint().trim());
	            vo.setPRODUCT_TYPE_2(detail.getProductType2().trim());
	            vo.setTRADE_TYPE(detail.getTradeType().trim());
	            vo.setW8BEN_CODE(detail.getW8bencode().trim());
	            vo.setMARKET_DATE(StringUtils.isEmpty(detail.getMarketDate()) ? null : new Timestamp(sd.parse(detail.getMarketDate()).getTime()));
	            vo.setPD_TYPE(detail.getPDType1().trim());
	            vo.setLMT_PRICE_BUY(new EsbUtil().decimalPoint(detail.getLmtPriceBuy(), 6));
	            vo.setLMT_PRICE_SELL(new EsbUtil().decimalPoint(detail.getLmtPriceSell(), 6));
	            vo.setPROD_COUNTRY(detail.getProdCountry().trim());
	            vo.setPRODUT_COM(detail.getProdutCom().trim());
	            vo.setPRICD_TYPE(detail.getPricdType().trim());
	            vo.setSTART_TIME(detail.getStartTime().trim());
	            vo.setPRODUCT_TYPE(detail.getProductType().trim());
	            vo.setFUB_START_TIME(detail.getFubStartTime().trim());
	            vo.setFUB_END_TIME(detail.getFubEndTime().trim());
	            vo.setTRX_MARKET_CODE(detail.getTrxMarketCode().trim());
	            vo.setDIVIDE_START_TIME(detail.getDivideStartTime().trim());
	            vo.setDIVIDE_END_TIME(detail.getDivideEndTime().trim());
	            vo.setSELL_BY_MKT(detail.getSellByMKT().trim());
	            vo.setSHORT_PD_NAME(detail.getShortPdName().trim());
	            vo.setDBU_OBU(detail.getDbuObu().trim());
	            
                dam.create(vo);
            } 	
        }
    }
}
