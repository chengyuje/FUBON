package com.systex.jbranch.app.server.fps.sot705;

import java.math.BigDecimal;
import java.util.Date;

public class CustOrderETFVO {
	private String tradeStatus  ;   //  交易狀態
	private	String	trustAcct	;  	//	信託帳號
	private	String	trustType	;  	//	信託種類 N= 台幣  Y= 外幣
	private	String	insuranceNo	;  	//	商品代號
	private	BigDecimal	entrustAmt	;  	//	委託股數
	private	String	curCode	;  	//	商品幣別
	private	Date	entrustDate	;  	//	委託日期
	private	BigDecimal	tradePrice	;  	//	成交報價
	private	BigDecimal	tradeCost	;  	//	成交扣款金額
	private	String	entrustCur	;  	//	交易幣別
	private	String	insuranceName	;  	//	商品名稱
	private	String	state	;  	//	"委託結果 0 =>委託 1=>委託成功 3=>刪單(取消)"
	private	BigDecimal	tradeAmt	;  	//	成交股數
	private	BigDecimal	entrustNo	;  	//	電子化序號
	private	String	canceled	;  	//	可否取消
	private	String	trxMarket	;  	//	交易市場
	private	BigDecimal	entrustPrice	;  	//	委託價格
	private	Date	trxMarketDat	;  	//	交易市場日期
	private	String	costAcc	;  	//	扣款帳號
	private	BigDecimal	creditLoadAmt	;  	//	圈存總金額
	private	String	orderType	;  	//	"委託方式""0"" 約定限價 ""1"" 市價單  ""2"" 限價單 (LiMiT order) 簡稱 LMT（限定價格） ""3"" 停損單 (SToP order) 簡稱 STP 或 (Stop Loss Order) 簡稱 SLO ""4"" 限價停損單 (Stop Limit Order)   ""5"" 收盤市價單 (Market On Close order ) 簡稱 MO"
	private	String	productType	;  	//	"商品類型 “E“ :ETF “S”:股票
	private	Date	entrustCancelDate	;  	//	取消日期
	private	Date	tradeDateEnd	;  	//	截止市場交易日期
	private	String	countType	;  	//	整股/零股
	private	String	entrustType	;  	//	"委託類型B: 買進 S: 賣出"
	private	String	tradeAcct	;  	//	入扣帳號
	
	
	
	public String getTradeStatus() {
		return tradeStatus;
	}
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	public String getTrustAcct() {
		return trustAcct;
	}
	public void setTrustAcct(String trustAcct) {
		this.trustAcct = trustAcct;
	}
	public String getTrustType() {
		return trustType;
	}
	public void setTrustType(String trustType) {
		this.trustType = trustType;
	}
	public String getInsuranceNo() {
		return insuranceNo;
	}
	public void setInsuranceNo(String insuranceNo) {
		this.insuranceNo = insuranceNo;
	}
	public BigDecimal getEntrustAmt() {
		return entrustAmt;
	}
	public void setEntrustAmt(BigDecimal entrustAmt) {
		this.entrustAmt = entrustAmt;
	}
	public String getCurCode() {
		return curCode;
	}
	public void setCurCode(String curCode) {
		this.curCode = curCode;
	}
	public Date getEntrustDate() {
		return entrustDate;
	}
	public void setEntrustDate(Date entrustDate) {
		this.entrustDate = entrustDate;
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
	public String getEntrustCur() {
		return entrustCur;
	}
	public void setEntrustCur(String entrustCur) {
		this.entrustCur = entrustCur;
	}
	public String getInsuranceName() {
		return insuranceName;
	}
	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}
	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}
	public BigDecimal getEntrustNo() {
		return entrustNo;
	}
	public void setEntrustNo(BigDecimal entrustNo) {
		this.entrustNo = entrustNo;
	}
	public String getCanceled() {
		return canceled;
	}
	public void setCanceled(String canceled) {
		this.canceled = canceled;
	}
	public String getTrxMarket() {
		return trxMarket;
	}
	public void setTrxMarket(String trxMarket) {
		this.trxMarket = trxMarket;
	}
	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}
	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}
	public Date getTrxMarketDat() {
		return trxMarketDat;
	}
	public void setTrxMarketDat(Date trxMarketDat) {
		this.trxMarketDat = trxMarketDat;
	}
	public String getCostAcc() {
		return costAcc;
	}
	public void setCostAcc(String costAcc) {
		this.costAcc = costAcc;
	}
	public BigDecimal getCreditLoadAmt() {
		return creditLoadAmt;
	}
	public void setCreditLoadAmt(BigDecimal creditLoadAmt) {
		this.creditLoadAmt = creditLoadAmt;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public Date getEntrustCancelDate() {
		return entrustCancelDate;
	}
	public void setEntrustCancelDate(Date entrustCancelDate) {
		this.entrustCancelDate = entrustCancelDate;
	}
	public Date getTradeDateEnd() {
		return tradeDateEnd;
	}
	public void setTradeDateEnd(Date tradeDateEnd) {
		this.tradeDateEnd = tradeDateEnd;
	}
	public String getCountType() {
		return countType;
	}
	public void setCountType(String countType) {
		this.countType = countType;
	}
	public String getEntrustType() {
		return entrustType;
	}
	public void setEntrustType(String entrustType) {
		this.entrustType = entrustType;
	}
	public String getTradeAcct() {
		return tradeAcct;
	}
	public void setTradeAcct(String tradeAcct) {
		this.tradeAcct = tradeAcct;
	}
	
	
	

}
