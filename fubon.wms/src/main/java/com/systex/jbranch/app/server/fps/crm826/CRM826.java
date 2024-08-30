package com.systex.jbranch.app.server.fps.crm826;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm826
 * 
 * @author moron, Jacky Wu
 * @date 2016/06/14, 2016/12/07
 * @spec null
 */
@Component("crm826")
@Scope("request")
public class CRM826 extends EsbUtil {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM826.class);
	 /* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName()+".";
    
//	public void inquire(Object body, IPrimitiveMap header) throws Exception {
//
//		CRM826InputVO inputVO = (CRM826InputVO) body;
//		CRM826OutputVO return_VO = new CRM826OutputVO();
//	        
//	    String custID = inputVO.getCust_id();
//	    
//	    //init util
//        ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, BKDCD003_CUST_DATA);
//
//        esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());   
//	       
//        //head
//        TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
//        esbUtilInputVO.setTxHeadVO(txHead);
//        txHead.setDefaultTxHead();
//	    
//        //body
//        BKDCD003InputVO txBodyVO = new BKDCD003InputVO();
//        esbUtilInputVO.setBkdcd003InputVO(txBodyVO);
//        txBodyVO.setCUSTID(custID); //客戶ID
//        txBodyVO.setTradeStatus("1");
//        
//        //發送電文
//        List<ESBUtilOutputVO> vos = send(esbUtilInputVO);   
//
//        //根據產品類別以及查詢條件回傳庫存資料
//        List<CustAssetDCIVO> assetList = new ArrayList<>();    
//
//        for(ESBUtilOutputVO esbUtilOutputVO : vos) {
//        	BKDCD003OutputVO bkdcd003OutputVO = esbUtilOutputVO.getBkdcd003OutputVO();
//        	
//        	List<BKDCD003OutputDetailVO> details = bkdcd003OutputVO.getDetails();
//            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<BKDCD003OutputDetailVO>() : details;
//
//            //取得庫存資料
//            for (BKDCD003OutputDetailVO detail : details) {
//               
//                //符合資料放入庫存CustAssetBondVO
//            	CustAssetDCIVO asset = new CustAssetDCIVO();
//            	
//            	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//            	
//            	Date trade_date = sdf1.parse(detail.getTRADEDATE());
//            	Date expirydate = sdf2.parse(detail.getEXPIRYDATE());
//            	Date deliveryDate = sdf2.parse(detail.getDELIVERYDATE());
//            	Date payDate = sdf2.parse(detail.getPAYDATE());
//            	
//            	asset.setTranID(detail.getTRANID());     								  	//交易編號
//            	if(StringUtils.equals(detail.getBREAKEVENID(), "0")){
//            		asset.setProdName("外匯雙享利");
//            	}else{
//            		asset.setProdName("保值型外匯雙享利");
//            	}
//
//            	asset.setTrade_date(trade_date);         								  	//交易日期
//            	asset.setDcdAmount(getBigDecimal(detail.getDCDAMOUNT()));	//交易金額
//            	asset.setCurrency(detail.getCURRENCY());                               	  	//商品幣別
//            	asset.setMappingCurrency(detail.getMAPPINGCURRENCY());						//相對幣別
//            	asset.setExpirydate(expirydate);											//比價日
//            	asset.setDeliveryDate(deliveryDate);										//到期日
//            	asset.setYield(getBigDecimal(detail.getYIELD()));			//商品收益率
//            	//CURRENCYCHANGE是否幣轉為   	0放交易幣別 	1放相對幣別
//            	if(StringUtils.equals(detail.getCURRENCYCHANGE(), "0")){
//            		asset.setTurn_currency(detail.getCURRENCY());
//            	}else if(StringUtils.equals(detail.getCURRENCYCHANGE(), "1")){
//            		asset.setTurn_currency(detail.getMAPPINGCURRENCY());
//            	}
//            	asset.setEarnAmount(getBigDecimal(detail.getEARNAMOUNT())); //收益金額
//            	asset.setDeliveryDateAmount(getBigDecimal(detail.getDELIVERYDATEAMOUNT()));//到期返還金額
//            	asset.setDcdAcount(detail.getDCDACCOUNT());									 //組合式商品帳號
//            	asset.setStrike(getBigDecimal(detail.getSTRIKE()));		 //履約價
//            	asset.setStrike2(getBigDecimal(detail.getSTRIKE2()));		 //觸發匯率
//            	asset.setPayDate(payDate); 													 //起息日
//            	asset.setSpotrate(getBigDecimal(detail.getSPOTRATE()));	 //即期匯率
//            	asset.setExpiryDateSpotrate(getBigDecimal(detail.getSPOTRATE()));//比價時匯率
//            	asset.setCustomerAccount(detail.getCUSTOMERACCOUNT());  //扣款帳號
//            	
//            	//系統日 > 到期日    0:未到期  1:到期
//            	Date now = new Date();
//            	if(now.after(deliveryDate)){
//            		asset.setTradeStatus("0");
//            	}else{
//            		asset.setTradeStatus("1");
//            	}
//                assetList.add(asset);
//                
//            }
//        }  
//	        
//	        //查無資料則拋出錯誤訊息
//	        if (CollectionUtils.isEmpty(assetList)) {
//	            throw new JBranchException("ehl_01_common_009查無資料");
//	        }
//	        
//	        return_VO.setResultList(assetList);
//	        
//	        this.sendRtnObject(return_VO);
//	}
//	
//	private BigDecimal getBigDecimal(String val){
//		if(StringUtils.isNotBlank(val)){
//			return new BigDecimal(val); 
//		}else{
//			return null;
//		}
//			
//	}
	
    //查詢
  	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
  		CRM826InputVO inputVO = (CRM826InputVO) body;
  		CRM826OutputVO outputVO = new CRM826OutputVO();
  		
  		dam = this.getDataAccessManager();
  		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
  		StringBuffer sql = new StringBuffer();
  		sql.append("SELECT PROD_ID, TXN_AMT, VALU_CRCY_TYPE, MPNG_CRCY_TYPE, TXN_DATE, EXPIRY_DATE, ")
  		   .append("DUE_DATE, YIELD_RATE, CRCY_CHGE_STATUS, DEL_AMT, DED_ACC_NBR, ACC_NBR, STRIKE, ")
  		   .append("REF_PROFIT, CASE WHEN GRNT_TYPE = '0' THEN '外匯雙享利' ELSE '保值型外匯雙享利' END AS PRD_NAME, ")
  		   .append("CASE WHEN CRCY_CHGE_STATUS IS NULL THEN NULL ")//沒有幣轉就不顯示
  		   .append("     WHEN CRCY_CHGE_STATUS = '0' THEN VALU_CRCY_TYPE ")//不執行幣轉
  		   .append("     WHEN CRCY_CHGE_STATUS = '2' THEN VALU_CRCY_TYPE ")//保本幣轉要用原投資幣別
  		   .append("      ELSE MPNG_CRCY_TYPE END AS CRCY, ")
  		   .append("PAY_DATE, SPOT_RATE, EXPIRY_DATE_SPOT_RATE, TRADE_STATUS, STRIKE2 ")
  		   .append("FROM TBCRM_AST_INV_DCI_OTH WHERE 1 = 1 ")
  		   //.append(" AND (DUE_DATE IS NULL OR DUE_DATE >= TRUNC(SYSDATE) ) ") //已到期的客戶資料不顯示
  		   .append(" ");
  		// where
		sql.append("AND CUST_ID = :cust_id ");
		condition.setObject("cust_id", inputVO.getCust_id());

  		condition.setQueryString(sql.toString());		
  		List resultList = dam.exeQuery(condition);
  		outputVO.setResultList(resultList); // data
  		
  		this.sendRtnObject(outputVO);
  	}
    
}