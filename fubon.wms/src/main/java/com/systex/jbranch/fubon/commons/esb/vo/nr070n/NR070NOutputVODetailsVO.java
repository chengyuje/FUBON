package com.systex.jbranch.fubon.commons.esb.vo.nr070n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NR070NOutputVODetailsVO {

	@XmlElement
	private String TradeAcct; // 存款帳號
	@XmlElement
	private String TradeCur; // 幣別
	@XmlElement
	private String SellAmt; // 在途款總額
	@XmlElement
	private String TDaySAmt; // 今日賣出金額
	@XmlElement
	private String SellUseAmt; // 在途款圈存金額
	@XmlElement
	private String BankUseAmt; // ETF銀行圈存金額
	
	/* 2018.10.15 add by Mimi */
	@XmlElement
	private String TrxMarketCode; // ETF銀行圈存金額
	
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
	public String getSellAmt() {
		return SellAmt;
	}
	public void setSellAmt(String sellAmt) {
		SellAmt = sellAmt;
	}
	public String getTDaySAmt() {
		return TDaySAmt;
	}
	public void setTDaySAmt(String tDaySAmt) {
		TDaySAmt = tDaySAmt;
	}
	public String getSellUseAmt() {
		return SellUseAmt;
	}
	public void setSellUseAmt(String sellUseAmt) {
		SellUseAmt = sellUseAmt;
	}
	public String getBankUseAmt() {
		return BankUseAmt;
	}
	public void setBankUseAmt(String bankUseAmt) {
		BankUseAmt = bankUseAmt;
	}
	
	public String getTrxMarketCode(){
		return TrxMarketCode;
	}
	public void setTrxMarketCode(String trxMarketCode){
		TrxMarketCode = trxMarketCode;
	}

}
