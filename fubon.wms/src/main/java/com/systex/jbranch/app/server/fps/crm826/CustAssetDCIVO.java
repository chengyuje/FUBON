package com.systex.jbranch.app.server.fps.crm826;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Jacky Wu on 2016/12/7.
 */
public class CustAssetDCIVO {
	private String tranID;   						//交易編號
	private String prodName;                        //產品名稱
	private BigDecimal dcdAmount;        			//交易金額      
	private String currency;        				//商品幣別      
	private String mappingCurrency;        			//相對幣別      
	private Date trade_date; 						//交易日
	private Date expirydate;        				//比價日        
	private Date deliveryDate;        				//到期日
	private BigDecimal yield;        				//產品收益率
	private String turn_currency;                   //幣別、到期幣別、轉換幣別
	private BigDecimal earnAmount;                  //收益金額
	private String currencyChange;        			//幣轉狀態      
	private BigDecimal deliveryDateAmount;        	//到期返還金額   	
	private BigDecimal spotrate; 					//即期匯率
	private BigDecimal strike;        				//履約價
	private BigDecimal strike2;        				//解發匯率
	private BigDecimal expiryDateSpotrate;			//比價時匯率
	private String tradeStatus;        				//交易狀態      1:到期  0:未到期
	private String dcdAcount;						//組合式商品帳號
	private Date payDate;                           //起息日
	private BigDecimal breakEvenID;        			//保值型 
	private String customerAccount;                 //扣款帳號
	
	private Date marketEvalationDate;        		//市場評估日    
	private BigDecimal interestAmount;        		//利息金額      
	private BigDecimal compareToAmount;        		//與本金之百分比
	private BigDecimal totalFee;        			//總費用        
	private String lsForm;        					//FORMAT_COD    
	private BigDecimal dcdAmountD;        			//參考台幣價格  
	
	
	
	public String getCustomerAccount() {
		return customerAccount;
	}
	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}
	public BigDecimal getStrike2() {
		return strike2;
	}
	public void setStrike2(BigDecimal strike2) {
		this.strike2 = strike2;
	}
	public String getDcdAcount() {
		return dcdAcount;
	}
	public void setDcdAcount(String dcdAcount) {
		this.dcdAcount = dcdAcount;
	}
	public String getTurn_currency() {
		return turn_currency;
	}
	public void setTurn_currency(String turn_currency) {
		this.turn_currency = turn_currency;
	}
	public BigDecimal getEarnAmount() {
		return earnAmount;
	}
	public void setEarnAmount(BigDecimal earnAmount) {
		this.earnAmount = earnAmount;
	}
	
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getTranID() {
		return tranID;
	}
	public void setTranID(String tranID) {
		this.tranID = tranID;
	}
	public Date getTrade_date() {
		return trade_date;
	}
	public void setTrade_date(Date trade_date) {
		this.trade_date = trade_date;
	}
	public BigDecimal getSpotrate() {
		return spotrate;
	}
	public void setSpotrate(BigDecimal spotrate) {
		this.spotrate = spotrate;
	}
	public Date getExpirydate() {
		return expirydate;
	}
	public void setExpirydate(Date expirydate) {
		this.expirydate = expirydate;
	}
	public BigDecimal getDeliveryDateAmount() {
		return deliveryDateAmount;
	}
	public void setDeliveryDateAmount(BigDecimal deliveryDateAmount) {
		this.deliveryDateAmount = deliveryDateAmount;
	}
	public Date getMarketEvalationDate() {
		return marketEvalationDate;
	}
	public void setMarketEvalationDate(Date marketEvalationDate) {
		this.marketEvalationDate = marketEvalationDate;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public BigDecimal getStrike() {
		return strike;
	}
	public void setStrike(BigDecimal strike) {
		this.strike = strike;
	}
	public BigDecimal getExpiryDateSpotrate() {
		return expiryDateSpotrate;
	}
	public void setExpiryDateSpotrate(BigDecimal expiryDateSpotrate) {
		this.expiryDateSpotrate = expiryDateSpotrate;
	}
	public BigDecimal getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}
	public BigDecimal getCompareToAmount() {
		return compareToAmount;
	}
	public void setCompareToAmount(BigDecimal compareToAmount) {
		this.compareToAmount = compareToAmount;
	}
	public String getMappingCurrency() {
		return mappingCurrency;
	}
	public void setMappingCurrency(String mappingCurrency) {
		this.mappingCurrency = mappingCurrency;
	}
	public BigDecimal getDcdAmount() {
		return dcdAmount;
	}
	public void setDcdAmount(BigDecimal dcdAmount) {
		this.dcdAmount = dcdAmount;
	}
	public BigDecimal getYield() {
		return yield;
	}
	public void setYield(BigDecimal yield) {
		this.yield = yield;
	}
	public String getCurrencyChange() {
		return currencyChange;
	}
	public void setCurrencyChange(String currencyChange) {
		this.currencyChange = currencyChange;
	}
	public BigDecimal getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}
	public String getTradeStatus() {
		return tradeStatus;
	}
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	public String getLsForm() {
		return lsForm;
	}
	public void setLsForm(String lsForm) {
		this.lsForm = lsForm;
	}
	public BigDecimal getDcdAmountD() {
		return dcdAmountD;
	}
	public void setDcdAmountD(BigDecimal dcdAmountD) {
		this.dcdAmountD = dcdAmountD;
	}
	public BigDecimal getBreakEvenID() {
		return breakEvenID;
	}
	public void setBreakEvenID(BigDecimal breakEvenID) {
		this.breakEvenID = breakEvenID;
	}
	
	
}
