package com.systex.jbranch.app.server.fps.sot710;

import java.math.BigDecimal;

public class AvailBalanceVO {
	private BigDecimal Occur;	      // Number	資料筆數
	private String TradeAcct;         // String	存款帳號
	private String TradeCur;          // String	幣別
	private BigDecimal SellAmt;       // Number	在途款總額
	private BigDecimal TDaySAmt;      // Number	今日賣出金額
	private BigDecimal SellUseAmt;    // String	在途款圈存金額
	private BigDecimal BankUseAmt;    // Number	ETF銀行圈存金額
	
	/* 2018.10.15 add by Mimi */
	private String TrxMarketCode;     // String 市場代碼
	
	public BigDecimal getOccur() {
		return Occur;
	}
	public void setOccur(BigDecimal occur) {
		Occur = occur;
	}
	public String getTradeAcct() {
		return TradeAcct;
	}
	public void setTradeAcct(String tradeAcct) {
		TradeAcct = tradeAcct;
	}
	public String getTradeCur() {
		return TradeCur;
	}
	public void setTradeCur(String tradeCur) {
		TradeCur = tradeCur;
	}
	public BigDecimal getSellAmt() {
		return SellAmt;
	}
	public void setSellAmt(BigDecimal sellAmt) {
		SellAmt = sellAmt;
	}
	public BigDecimal getTDaySAmt() {
		return TDaySAmt;
	}
	public void setTDaySAmt(BigDecimal tDaySAmt) {
		TDaySAmt = tDaySAmt;
	}
	public BigDecimal getSellUseAmt() {
		return SellUseAmt;
	}
	public void setSellUseAmt(BigDecimal sellUseAmt) {
		SellUseAmt = sellUseAmt;
	}
	public BigDecimal getBankUseAmt() {
		return BankUseAmt;
	}
	public void setBankUseAmt(BigDecimal bankUseAmt) {
		BankUseAmt = bankUseAmt;
	}
	
	public String getTrxMarketCode(){
		return TrxMarketCode;		       
	}
	
	public void setTrxMaketCode(String trxMarketCode){
		TrxMarketCode = trxMarketCode;
	}

}
