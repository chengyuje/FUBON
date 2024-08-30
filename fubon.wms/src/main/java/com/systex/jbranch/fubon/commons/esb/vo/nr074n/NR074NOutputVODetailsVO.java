package com.systex.jbranch.fubon.commons.esb.vo.nr074n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NR074NOutputVODetailsVO {

	@XmlElement
	private String ProdcutType; // 商品類型
	@XmlElement
	private String TrxMarket; // 交易市場
	@XmlElement
	private String InsuranceNo; // 商品代號
	@XmlElement
	private String CurCode; // 商品幣別
	@XmlElement
	private String State; // 在途狀態
	@XmlElement
	private String EntrustCur; // 交易幣別
	@XmlElement
	private String Result; // 委託結果
	@XmlElement
	private String EntrustDate; // 委託日期
	@XmlElement
	private String TradeDate; // 市場交易日期
	@XmlElement
	private String EntrustAmt; // 委託股數
	@XmlElement
	private String EntrustPrice; // 委託價格
	@XmlElement
	private String ChannelType; // 通路別
	@XmlElement
	private String TrustType; // 信託業務別
	@XmlElement
	private String TrustAcct; // 信託帳號
	@XmlElement
	private String TradeAmt; // 成交股數
	@XmlElement
	private String TradePrice; // 成交報價
	@XmlElement
	private String TradeCost; // 成交金額
	@XmlElement
	private String ProductName; // 商品名稱
	@XmlElement
	private String TradeDateEnd; // 截止市場交易日期
	@XmlElement
	private String TradeCostAcct; // 扣款帳號
	@XmlElement
	private String TradeCostFee; // 預估手續費
	@XmlElement
	private String TradeCostOtFee; // 預估其他費用
	@XmlElement
	private String ForCurBal; // 外幣參考市值
	@XmlElement
	private String CurAmt; // 參考收盤價
	@XmlElement
	private String Date08; // 收盤報價日期
	@XmlElement
	private String OrderType; // 委託方式
	@XmlElement
	private String TradeSellFee; // 預估手續費
	@XmlElement
	private String TradeSellOtFee; // 預估其他費用
	@XmlElement
	private String TradeTrustFee; // 預估信管費
	@XmlElement
	private String TradeSellAcct; //入帳帳號

	public String getProdcutType() {
		return ProdcutType;
	}
	public void setProdcutType(String prodcutType) {
		ProdcutType = prodcutType;
	}
	public String getTrxMarket() {
		return TrxMarket;
	}
	public void setTrxMarket(String trxMarket) {
		TrxMarket = trxMarket;
	}
	public String getInsuranceNo() {
		return InsuranceNo;
	}
	public void setInsuranceNo(String insuranceNo) {
		InsuranceNo = insuranceNo;
	}
	public String getCurCode() {
		return CurCode;
	}
	public void setCurCode(String curCode) {
		CurCode = curCode;
	}
	public String getState() {
		return State;
	}
	public void setState(String state) {
		State = state;
	}
	public String getEntrustCur() {
		return EntrustCur;
	}
	public void setEntrustCur(String entrustCur) {
		EntrustCur = entrustCur;
	}
	public String getResult() {
		return Result;
	}
	public void setResult(String result) {
		Result = result;
	}
	public String getEntrustDate() {
		return EntrustDate;
	}
	public void setEntrustDate(String entrustDate) {
		EntrustDate = entrustDate;
	}
	public String getTradeDate() {
		return TradeDate;
	}
	public void setTradeDate(String tradeDate) {
		TradeDate = tradeDate;
	}
	public String getEntrustAmt() {
		return EntrustAmt;
	}
	public void setEntrustAmt(String entrustAmt) {
		EntrustAmt = entrustAmt;
	}
	public String getEntrustPrice() {
		return EntrustPrice;
	}
	public void setEntrustPrice(String entrustPrice) {
		EntrustPrice = entrustPrice;
	}
	public String getChannelType() {
		return ChannelType;
	}
	public void setChannelType(String channelType) {
		ChannelType = channelType;
	}
	public String getTrustType() {
		return TrustType;
	}
	public void setTrustType(String trustType) {
		TrustType = trustType;
	}
	public String getTrustAcct() {
		return TrustAcct;
	}
	public void setTrustAcct(String trustAcct) {
		TrustAcct = trustAcct;
	}
	public String getTradeAmt() {
		return TradeAmt;
	}
	public void setTradeAmt(String tradeAmt) {
		TradeAmt = tradeAmt;
	}
	public String getTradePrice() {
		return TradePrice;
	}
	public void setTradePrice(String tradePrice) {
		TradePrice = tradePrice;
	}
	public String getTradeCost() {
		return TradeCost;
	}
	public void setTradeCost(String tradeCost) {
		TradeCost = tradeCost;
	}
	public String getProductName() {
		return ProductName;
	}
	public void setProductName(String productName) {
		ProductName = productName;
	}
	public String getTradeDateEnd() {
		return TradeDateEnd;
	}
	public void setTradeDateEnd(String tradeDateEnd) {
		TradeDateEnd = tradeDateEnd;
	}
	public String getTradeCostAcct() {
		return TradeCostAcct;
	}
	public void setTradeCostAcct(String tradeCostAcct) {
		TradeCostAcct = tradeCostAcct;
	}
	public String getTradeCostFee() {
		return TradeCostFee;
	}
	public void setTradeCostFee(String tradeCostFee) {
		TradeCostFee = tradeCostFee;
	}
	public String getTradeCostOtFee() {
		return TradeCostOtFee;
	}
	public void setTradeCostOtFee(String tradeCostOtFee) {
		TradeCostOtFee = tradeCostOtFee;
	}
	public String getForCurBal() {
		return ForCurBal;
	}
	public void setForCurBal(String forCurBal) {
		ForCurBal = forCurBal;
	}
	public String getCurAmt() {
		return CurAmt;
	}
	public void setCurAmt(String curAmt) {
		CurAmt = curAmt;
	}
	public String getDate08() {
		return Date08;
	}
	public void setDate08(String date08) {
		Date08 = date08;
	}
	public String getOrderType() {
		return OrderType;
	}
	public void setOrderType(String orderType) {
		OrderType = orderType;
	}
	public String getTradeSellFee() {
		return TradeSellFee;
	}
	public void setTradeSellFee(String tradeSellFee) {
		TradeSellFee = tradeSellFee;
	}
	public String getTradeSellOtFee() {
		return TradeSellOtFee;
	}
	public void setTradeSellOtFee(String tradeSellOtFee) {
		TradeSellOtFee = tradeSellOtFee;
	}
	public String getTradeTrustFee() {
		return TradeTrustFee;
	}
	public void setTradeTrustFee(String tradeTrustFee) {
		TradeTrustFee = tradeTrustFee;
	}
	public String getTradeSellAcct() {
		return TradeSellAcct;
	}
	public void setTradeSellAcct(String tradeSellAcct) {
		TradeSellAcct = tradeSellAcct;
	}

}
