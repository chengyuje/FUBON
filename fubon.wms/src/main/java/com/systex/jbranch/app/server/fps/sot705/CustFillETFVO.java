package com.systex.jbranch.app.server.fps.sot705;

import java.math.BigDecimal;
import java.util.Date;

public class CustFillETFVO {
	private String prodcutType;// 商品類型 “E“ :ETF “S”:股票
	private String trxMarket;// 交易市場
	private String insuranceNo;// 商品代號
	private String curCode;// 商品幣別
	private String state;// 在途狀態 1= 買進委託中
	private String entrustCur;// 交易幣別
	private String result;// 委託結果 1= 交易已成交未扣款 2= 交易已成交已扣款 未分配 3= 交易已取消(部分成交)未扣款
							// 4= 交易已取消(部分成交)已扣款未分配
	private Date entrustDate;// 委託日期 1050301台灣委託日期，若為長效單放第一天
	private Date tradeDate;// 市場交易日期
	private BigDecimal entrustAmt;// 委託股數
	private BigDecimal entrustPrice;// 委託價格 總長度13位含6位小數
	private String channelType;// 通路別 “ “ => 臨櫃 “S” =>臨櫃 “W” => 網銀 “P” =>CTI “M”
								// =>行銀
	private String trustType;// 信託業務別 Y或” “ =>外幣 N =>台幣
	private String trustAcct;// 信託帳號
	private BigDecimal tradeAmt;// 成交股數
	private BigDecimal tradePrice;// 成交報價 總長度13位含6位小數
	private BigDecimal tradeCost;// 成交金額 總長度13位含6位小數
	private String productName;// 商品名稱
	private Date tradeDateEnd;// 截止市場交易日期
	private String tradeCostAcct;// 扣款帳號
	private BigDecimal tradeCostFee;// 預估手續費 總長度13位含2位小數
	private BigDecimal tradeCostOtFee;// 預估其他費用 總長度13位含2位小數
	private BigDecimal forCurBal;// 外幣參考市值
	private BigDecimal curAmt;// 參考收盤價
	private Date date08;// 收盤報價日期
	private String orderType;// 委託方式
	private BigDecimal tradeTrustFee;// 預估信管費 總長度13位含2位小數
	private String tradeSellAcct;// 入帳帳號
	private String entrustType;// 委託類型 B: 買進 S: 賣出
	private BigDecimal tradeSellFee;
	private BigDecimal tradeSellOtFee;
	
	public String getProdcutType() {
		return prodcutType;
	}
	public void setProdcutType(String prodcutType) {
		this.prodcutType = prodcutType;
	}
	public String getTrxMarket() {
		return trxMarket;
	}
	public void setTrxMarket(String trxMarket) {
		this.trxMarket = trxMarket;
	}
	public String getInsuranceNo() {
		return insuranceNo;
	}
	public void setInsuranceNo(String insuranceNo) {
		this.insuranceNo = insuranceNo;
	}
	public String getCurCode() {
		return curCode;
	}
	public void setCurCode(String curCode) {
		this.curCode = curCode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getEntrustCur() {
		return entrustCur;
	}
	public void setEntrustCur(String entrustCur) {
		this.entrustCur = entrustCur;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Date getEntrustDate() {
		return entrustDate;
	}
	public void setEntrustDate(Date entrustDate) {
		this.entrustDate = entrustDate;
	}
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public BigDecimal getEntrustAmt() {
		return entrustAmt;
	}
	public void setEntrustAmt(BigDecimal entrustAmt) {
		this.entrustAmt = entrustAmt;
	}
	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}
	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getTrustType() {
		return trustType;
	}
	public void setTrustType(String trustType) {
		this.trustType = trustType;
	}
	public String getTrustAcct() {
		return trustAcct;
	}
	public void setTrustAcct(String trustAcct) {
		this.trustAcct = trustAcct;
	}
	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}
	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}
	public BigDecimal getTradePrice() {
		return tradePrice;
	}
	public void setTradePrice(BigDecimal tradePrice) {
		this.tradePrice = tradePrice;
	}
	public BigDecimal getTradeCost() {
		return tradeCost;
	}
	public void setTradeCost(BigDecimal tradeCost) {
		this.tradeCost = tradeCost;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Date getTradeDateEnd() {
		return tradeDateEnd;
	}
	public void setTradeDateEnd(Date tradeDateEnd) {
		this.tradeDateEnd = tradeDateEnd;
	}
	public String getTradeCostAcct() {
		return tradeCostAcct;
	}
	public void setTradeCostAcct(String tradeCostAcct) {
		this.tradeCostAcct = tradeCostAcct;
	}
	public BigDecimal getTradeCostFee() {
		return tradeCostFee;
	}
	public void setTradeCostFee(BigDecimal tradeCostFee) {
		this.tradeCostFee = tradeCostFee;
	}
	public BigDecimal getTradeCostOtFee() {
		return tradeCostOtFee;
	}
	public void setTradeCostOtFee(BigDecimal tradeCostOtFee) {
		this.tradeCostOtFee = tradeCostOtFee;
	}
	public BigDecimal getForCurBal() {
		return forCurBal;
	}
	public void setForCurBal(BigDecimal forCurBal) {
		this.forCurBal = forCurBal;
	}
	public BigDecimal getCurAmt() {
		return curAmt;
	}
	public void setCurAmt(BigDecimal curAmt) {
		this.curAmt = curAmt;
	}
	public Date getDate08() {
		return date08;
	}
	public void setDate08(Date date08) {
		this.date08 = date08;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public BigDecimal getTradeTrustFee() {
		return tradeTrustFee;
	}
	public void setTradeTrustFee(BigDecimal tradeTrustFee) {
		this.tradeTrustFee = tradeTrustFee;
	}
	public String getTradeSellAcct() {
		return tradeSellAcct;
	}
	public void setTradeSellAcct(String tradeSellAcct) {
		this.tradeSellAcct = tradeSellAcct;
	}
	public String getEntrustType() {
		return entrustType;
	}
	public void setEntrustType(String entrustType) {
		this.entrustType = entrustType;
	}
	public BigDecimal getTradeSellFee() {
		return tradeSellFee;
	}
	public void setTradeSellFee(BigDecimal tradeSellFee) {
		this.tradeSellFee = tradeSellFee;
	}
	public BigDecimal getTradeSellOtFee() {
		return tradeSellOtFee;
	}
	public void setTradeSellOtFee(BigDecimal tradeSellOtFee) {
		this.tradeSellOtFee = tradeSellOtFee;
	}

}
