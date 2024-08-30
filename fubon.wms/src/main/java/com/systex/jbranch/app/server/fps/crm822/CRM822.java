package com.systex.jbranch.app.server.fps.crm822;

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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrq01.NRBRQ01InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrq01.NRBRQ01OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrq01.NRBRQ01OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.uk084n.UK084NInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.uk084n.UK084NOutputDetailVO;
import com.systex.jbranch.fubon.commons.esb.vo.uk084n.UK084NOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm822
 * 
 * @author moron
 * @date 2016/06/14
 * @spec null
 */
@Component("crm822")
@Scope("request")
public class CRM822 extends EsbUtil {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM822.class);
	/* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
	
	public void getCustAcct (Object body, IPrimitiveMap header) throws Exception {
		CRM822InputVO inputVO = (CRM822InputVO) body;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		dam = this.getDataAccessManager();
		
        String custID = inputVO.getCust_id(); //客戶ID

        try{
			List<ESBUtilOutputVO> esbUtilOutputVO = getCustAcct(custID);

			for(ESBUtilOutputVO vo : esbUtilOutputVO){
            	
            	NRBRQ01OutputVO nrbrq01OutputVO = vo.getNrbrq01OutputVO();
                List<NRBRQ01OutputDetailsVO> details = nrbrq01OutputVO.getDetails();
    	        details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NRBRQ01OutputDetailsVO>() : details;
    	        
    	        for(NRBRQ01OutputDetailsVO detail : details) {
    	        	Map<String, Object> map = new HashMap<String, Object>();
    	        	
    	        	if(detail.getTrustAcct() != null) {
                		map.put("TrustAcct", detail.getTrustAcct());               			
    	        	}
    	        	
    	        	result.add(map); 	
    	        }	        
            }
			this.sendRtnObject(result);
        
        }catch(Exception e){
    		logger.debug("發送電文失敗:客戶ID = " + custID);
    		logger.debug("ESB error:=NRBRQ01" + StringUtil.getStackTraceAsString(e));
    	}        
	}

	public List<ESBUtilOutputVO> getCustAcct(String custID) throws Exception {
		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, EsbSotCons.NRBRQ01_DATA);
		esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setHFMTID("0001");		//Header.‧HFMTID =0001：固定填入，提供AS400判斷格式用。
		txHead.setDefaultTxHead();

		//body
		NRBRQ01InputVO txBodyVO = new NRBRQ01InputVO();
		esbUtilInputVO.setNrbrq01InputVO(txBodyVO);
		txBodyVO.setCustId(custID);
		txBodyVO.setTrustAcct("A");

		//發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);
		return esbUtilOutputVO;
	}

	public void inquire (Object body, IPrimitiveMap header) throws Exception {
		CRM822InputVO inputVO = (CRM822InputVO) body;
		CRM822OutputVO outputVO = new CRM822OutputVO();

		String custID = inputVO.getCust_id(); 		//客戶ID
		String curAcc = inputVO.getCurAcc();		//信託帳號
		try{
			outputVO.setResultList(inquire(custID, curAcc, inputVO.getStartDt(), inputVO.getEndDt()));
            this.sendRtnObject(outputVO);
        
        }catch(Exception e){
    		logger.debug("發送電文失敗:客戶ID = " + custID);
    		logger.debug("ESB error:=NRBRQ01" + StringUtil.getStackTraceAsString(e));
    	}  
	}

	/**
	 * 查詢歷史交易明細
	 * @param custID
	 * @param curAcc 帳戶
	 * @param startDt 時間（起）
	 * @param endDt 時間（迄）
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> inquire(String custID, String curAcc, Date startDt, Date endDt) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String start = sdf.format(startDt);   //起日
		String end = sdf.format(endDt);  	 //迄日

		List<Map<String, Object>> result = new ArrayList<>();
		EsbUtil esb = new EsbUtil();

		//init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, EsbSotCons.NRBRQ01_DATA);
		esbUtilInputVO.setModule(thisClaz+new Object(){}.getClass().getEnclosingMethod().getName());

		//head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setHFMTID("0001");		//Header.‧HFMTID =0001：固定填入，提供AS400判斷格式用。
		txHead.setDefaultTxHead();

		//body
		NRBRQ01InputVO txBodyVO = new NRBRQ01InputVO();
		esbUtilInputVO.setNrbrq01InputVO(txBodyVO);
		txBodyVO.setCustId(custID);
		txBodyVO.setCurAcc(curAcc);
		txBodyVO.setStartDt(start);
		txBodyVO.setEndDt(end);
		txBodyVO.setTrustAcct("0");

		//發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);

		for(ESBUtilOutputVO vo : esbUtilOutputVO){
			TxHeadVO headVO = vo.getTxHeadVO();
			String hfmtid = StringUtils.isBlank(headVO.getHFMTID()) ? "" : headVO.getHFMTID().trim();

			NRBRQ01OutputVO nrbrq01OutputVO = vo.getNrbrq01OutputVO();
			List<NRBRQ01OutputDetailsVO> details = nrbrq01OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NRBRQ01OutputDetailsVO>() : details;

			for(NRBRQ01OutputDetailsVO detail : details) {

				Map<String, Object> map = new HashMap<String, Object>();

				if(StringUtils.isNotBlank(detail.getTradeDate())){		//交易市場日期
					String trade_date = detail.getTradeDate();
					String tradeDate = trade_date.substring(0, 4) + "/" + trade_date.substring(4, 6) + "/" + trade_date.substring(6);
					map.put("TradeDate", tradeDate);
				}

				if(StringUtils.isNotBlank(detail.getEntrustDate())){ 	//委託日期
					String entrust_date = detail.getEntrustDate();
					String entrustDate = entrust_date.substring(0, 4) + "/" + entrust_date.substring(4, 6) + "/" + entrust_date.substring(6);
					map.put("EntrustDate", entrustDate);
				}

				if("0001".equals(hfmtid)) {				//買入交易
					map.put("TradeType", "買入");
				}else if("0002".equals(hfmtid)) {		//賣出交易
					map.put("TradeType", "賣出");
				}else if("0003".equals(hfmtid)) {		//除權
					map.put("TradeType", "除權");
				}else if("0004".equals(hfmtid)) {		//除息
					map.put("TradeType", "除息");
				}

				if(StringUtils.isNotBlank(detail.getTradeDateEnd())){	//委託有效市場日期
					String trade_date_end = detail.getTradeDateEnd();
					String tradeDateEnd = trade_date_end.substring(0, 4) + "/" + trade_date_end.substring(4, 6) + "/" + trade_date_end.substring(6);
					map.put("TradeDateEnd", tradeDateEnd);
				}
				if(StringUtils.isNotBlank(detail.getTradeDateEnd())){	//委託有效市場日期
					String trade_date_end = detail.getTradeDateEnd();
					String tradeDateEnd = trade_date_end.substring(0, 4) + "/" + trade_date_end.substring(4, 6) + "/" + trade_date_end.substring(6);
					map.put("TradeDateEnd", tradeDateEnd);
				}
				

				map.put("EntrustStatus", detail.getEntrustStatus());						//委託狀態(1成交0未成交2公司活動終止)
				map.put("ProductName", detail.getProductName());							//商品名稱
				map.put("TrxMarket", detail.getTrxMarket());								//交易市場
				map.put("CurCode", detail.getCurCode());									//商品幣別
				map.put("TradeAmt", esb.decimalPoint(detail.getTradeAmt(), 0));				//成交股數
				map.put("TradePrice", esb.decimalPoint(detail.getTradePrice(), 6));			//成交單價
				map.put("TradeCost", esb.decimalPoint(detail.getTradeCost(), 2));			//成交金額

				//明細
				map.put("EntrustAmt", esb.decimalPoint(detail.getEntrustAmt(), 0));			//委託股數
				map.put("TradeFee", esb.decimalPoint(detail.getTradeFee(), 2));				//手續費
				map.put("EntrustCur", detail.getEntrustCur());								//交易幣別
				map.put("DividendCur", detail.getDividendCur());							//商品幣別(除息)
				map.put("OtherFee", esb.decimalPoint(detail.getOtherFee(), 2));				//其他費用
				map.put("TradeTax", esb.decimalPoint(detail.getTradeTax(), 2));				//交易稅
				map.put("TrustType", "N".equals(detail.getTrustType()) ? "台幣" : "外幣");	//信託種類 N= 台幣  Y= 外幣
				map.put("TotalAmt", esb.decimalPoint(detail.getTotalAmt(), 2));				//實付金額、實收金額
				map.put("TrustAcct", detail.getTrustAcct());								//信託帳號
				map.put("TrustFee", esb.decimalPoint(detail.getTrustFee(), 2));				//信管費
				map.put("InsuranceNo", detail.getInsuranceNo());							//商品代號

				if(StringUtils.isNotBlank(detail.getDistributeDate())){						//分配日期
					String distribute_date = detail.getDistributeDate();
					String distributeDate = distribute_date.substring(0, 4) + "/" + distribute_date.substring(4, 6) + "/" + distribute_date.substring(6);
					map.put("DistributeDate", distributeDate);
				}

				map.put("Inventory", esb.decimalPoint(detail.getInventory(), 0));			//基準日庫存股數

				if(StringUtils.isNotBlank(detail.getRecordDate())){							//基準日期
					String record_date = detail.getRecordDate();
					String recordDate = record_date.substring(0, 4) + "/" + record_date.substring(4, 6) + "/" + record_date.substring(6);
					map.put("RecordDate", recordDate);
				}

				map.put("DistributeRate", esb.decimalPoint(detail.getDistributeRate(), 6));	//稅後配股率
				map.put("StockRate", esb.decimalPoint(detail.getStockRate(), 0));			//實得股數
				map.put("Dividend", esb.decimalPoint(detail.getDividend(), 6));				//每股股息
				map.put("DistributeAmt", esb.decimalPoint(detail.getDistributeAmt(), 2));	//分配金額
				map.put("ReferenceRate", esb.decimalPoint(detail.getReferenceRate(), 4));	//參考匯率
				map.put("ReceiveAmt", esb.decimalPoint(detail.getReceiveAmt(), 2));			//實得金額
				map.put("TaxRate", esb.decimalPoint(detail.getTaxRate(), 2));				//扣稅率

				//2017.11.16 add by Carley---下行電文新增欄位
				map.put("TradeCostAcct", detail.getTradeCostAcct());						//扣款帳號
				map.put("TradeEarnAcct", detail.getTradeEarnAcct());						//入帳帳號
				map.put("TradeCostBal", esb.decimalPoint(detail.getTradeCostBal(), 2));		//投資成本
				map.put("ELAmt", esb.decimalPoint(detail.getELAmt(), 2));					//損益
				map.put("ReturnRateSign", detail.getReturnRateSign());						//報酬率正負
				map.put("ReturnRate", esb.decimalPoint(detail.getReturnRate(), 2));			//報酬率
				
				//20230629_#1536_VOC議題 - 海外ETF股票顯示含息投資報酬率
				if(StringUtils.isNotBlank(detail.getPayDay())){						//實付扣款日
					String pay_day = detail.getPayDay();
					String PayDay = pay_day.substring(0, 4) + "/" + pay_day.substring(4, 6) + "/" + pay_day.substring(6);
					map.put("PayDay", PayDay);
				}
				if(StringUtils.isNotBlank(detail.getSellPayDay())){						//實得入帳日
					String sell_pay_day = detail.getSellPayDay();
					String SellPayDay = sell_pay_day.substring(0, 4) + "/" + sell_pay_day.substring(4, 6) + "/" + sell_pay_day.substring(6);
					map.put("SellPayDay", SellPayDay);
				}
				map.put("DividendSell", esb.decimalPoint(detail.getDividend(), 2));				//成交股數累積配息
				map.put("ELAmtSign", detail.getELAmtSign());						//損益正負

				result.add(map);

//    	        	if(!"0".equals(detail.getEntrustStatus())){					//委託狀態(1成交0未成交2公司活動終止) EntrustStatus不為0，都要顯示。(#0002723)
//
//    	        	}
			}
		}
		return result;
	}

	//查詢入帳帳號
	public void getAcctNbr (Object body, IPrimitiveMap header) throws Exception {
		CRM822InputVO inputVO = (CRM822InputVO) body;
		CRM822OutputVO outputVO = new CRM822OutputVO();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        
        StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT ACCT_NBR FROM TBCRM_NRBRQ11P WHERE CUST_ID = :cust_id ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		
		this.sendRtnObject(outputVO);
		
	}
	
	//入扣帳明細查詢
	public void inquireAccDetails (Object body, IPrimitiveMap header) throws Exception {
		CRM822InputVO inputVO = (CRM822InputVO) body;
		CRM822OutputVO outputVO = new CRM822OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
		
		StringBuffer sb = new StringBuffer();
		sb.append("select trunc(add_months( :sdate, 3)) as STARTDATE from dual ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("sdate", inputVO.getStartDt());
		List<Map<String, Object>> sdateList = dam.exeQuery(queryCondition);
		Date sdate = (Date) sdateList.get(0).get("STARTDATE");
		
		if(sdate != null){
			if(inputVO.getEndDt().getTime() > sdate.getTime()){
				throw new JBranchException("資料期間不可以超過3個月");
			}
		}
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
		StringBuffer sql = new StringBuffer();		
		sql.append("SELECT ACCT_NBR, TO_DATE(DEAL_DATE, 'YYYY/MM/DD') AS DEAL_DATE, TYPE, TRUSTS_TYPE, ");
		sql.append("ORG_PRIFIT_AND_LOSS, ORG_AMT, ORG_CUR, DELIVERY_PRIFIT_AND_LOSS, DELIVERY_AMT, ");
		sql.append("DELIVERY_CUR, RATE, TO_DATE(DELIVERY_DATE, 'YYYY/MM/DD') AS DELIVERY_DATE, ");
		sql.append("PURCHASING_POWER_YN FROM TBCRM_NRBRQ11P WHERE ACCT_NBR = :acct_nbr ");
		sql.append("AND DEAL_DATE BETWEEN TO_CHAR(:startDt, 'YYYYMMDD') AND TO_CHAR(:endDt, 'YYYYMMDD') ");
		
		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("acct_nbr", inputVO.getCurAcc());		// 入扣帳號   
		queryCondition.setObject("startDt", inputVO.getStartDt());		// 成交日期 (起)  
		queryCondition.setObject("endDt", inputVO.getEndDt());			// 成交日期 (迄)
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		
		this.sendRtnObject(outputVO);
		
	}
	
	/**
	 * #1913
	 * 取得海外ETF/海外股票類總資產
	 * 電文 => UK084N 
	 */
	public void getETFStockDeposit(Object body, IPrimitiveMap header) throws Exception {
		CRM822InputVO inputVO = (CRM822InputVO) body;
		CRM822OutputVO outputVO = new CRM822OutputVO();

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, EsbSotCons.UK084N);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		UK084NInputVO txBody = new UK084NInputVO();
		esbUtilInputVO.setUk084nInputVO(txBody);
		txBody.setCustId(inputVO.getCust_id());
		
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);
		
		UK084NOutputVO uk084nOutputVO = esbUtilOutputVO.get(0).getUk084nOutputvo();
		List<UK084NOutputDetailVO> details = uk084nOutputVO.getDetails();
		String etfStockAmount = "0";
		if (CollectionUtils.isNotEmpty(details)) {
			etfStockAmount = details.get(0).getAmount();
		}
		outputVO.setEtfStockAmount(new BigDecimal(etfStockAmount));
		this.sendRtnObject(outputVO);
	}
}
