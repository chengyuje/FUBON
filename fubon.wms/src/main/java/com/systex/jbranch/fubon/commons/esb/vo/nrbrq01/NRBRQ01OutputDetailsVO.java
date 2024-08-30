package com.systex.jbranch.fubon.commons.esb.vo.nrbrq01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Carley on 2017/05/16.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NRBRQ01OutputDetailsVO {
	@XmlElement
	private String Occur;			//資料筆數
	@XmlElement
	private String TrustAcct;		//信託帳號
	@XmlElement
	private String TrustType;		//信託種類 N= 台幣  Y= 外幣
	@XmlElement
	private String InsuranceNo;		//商品代號
	@XmlElement
	private String EntrustAmt;		//委託股數
	@XmlElement
	private String DividendCur;		//商品幣別(除息)
	@XmlElement
	private String CurCode;			//商品幣別(除息:交易幣別)
	@XmlElement
	private String EntrustDate; 	//委託日期
	@XmlElement
	private String TradePrice;		//成交單價
	@XmlElement
	private String TradeCost;		//成交金額
	@XmlElement
	private String EntrustCur;		//交易幣別
	@XmlElement
	private String TradeAmt;		//成交股數
	@XmlElement
	private String TradeDate;		//交易市場日期
	@XmlElement
	private String TradeFee;		//手續費
	@XmlElement
	private String OtherFee;		//其他費用
	@XmlElement
	private String TotalAmt;		//實付金額、實收金額
	@XmlElement
	private String TrxMarket;		//交易市場
	@XmlElement
	private String EntrustStatus;	//委託狀態 1成交0未成交2公司活動終止
	@XmlElement
	private String ProductName;		//商品名稱
	@XmlElement
	private String TradeTax;		//交易稅
	@XmlElement
	private String TradeDateEnd;	//委託有效市場日期
	@XmlElement
	private String TrustFee;		//信管費	
	@XmlElement
	private String Inventory;		//基準日庫存股數
	@XmlElement
	private String DistributeRate;	//稅後配股率
	@XmlElement
	private String RecordDate;		//基準日期
	@XmlElement
	private String StockRate;		//實得股數
	@XmlElement
	private String DistributeDate;	//分配日期
	@XmlElement
	private String Dividend;		//每股股息
	@XmlElement
	private String ReceiveAmt;		//實得金額
	@XmlElement
	private String TaxRate;			//扣稅率
	@XmlElement
	private String ReferenceRate;	//參考匯率
	@XmlElement
	private String DistributeAmt;	//分配金額
	
	//2017.11.16 add by Carley---下行電文新增欄位
	@XmlElement
	private String TradeCostAcct;	//扣款帳號--for買入交易
	@XmlElement
	private String TradeEarnAcct;	//入帳帳號--for賣出交易
	@XmlElement
	private String TradeCostBal;	//投資成本(總長度13位含2位小數)--for賣出交易
	@XmlElement
	private String ELAmt;			//損益(總長度13位含2位小數)--for賣出交易
	@XmlElement
	private String ReturnRateSign;	//報酬率正負--for賣出交易
	@XmlElement
	private String ReturnRate;		//報酬率(總長度6位含2位小數)--for賣出交易
	/*
	 * 20230731_#1536_VOC議題 - 海外ETF股票顯示含息投資報酬率
	 */
	@XmlElement
	private String PayDay;		//實付扣款日
	@XmlElement
	private String SellPayDay;		//實得入帳日
	@XmlElement
	private String ELAmtSign;		//損益正負
	
	public String getOccur() {
		return Occur;
	}
	public void setOccur(String occur) {
		Occur = occur;
	}
	public String getTrustAcct() {
		return TrustAcct;
	}
	public void setTrustAcct(String trustAcct) {
		TrustAcct = trustAcct;
	}
	public String getTrustType() {
		return TrustType;
	}
	public void setTrustType(String trustType) {
		TrustType = trustType;
	}
	public String getInsuranceNo() {
		return InsuranceNo;
	}
	public void setInsuranceNo(String insuranceNo) {
		InsuranceNo = insuranceNo;
	}
	public String getEntrustAmt() {
		return EntrustAmt;
	}
	public void setEntrustAmt(String entrustAmt) {
		EntrustAmt = entrustAmt;
	}
	public String getDividendCur() {
		return DividendCur;
	}
	public void setDividendCur(String dividendCur) {
		DividendCur = dividendCur;
	}
	public String getCurCode() {
		return CurCode;
	}
	public void setCurCode(String curCode) {
		CurCode = curCode;
	}
	public String getEntrustDate() {
		return EntrustDate;
	}
	public void setEntrustDate(String entrustDate) {
		EntrustDate = entrustDate;
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
	public String getEntrustCur() {
		return EntrustCur;
	}
	public void setEntrustCur(String entrustCur) {
		EntrustCur = entrustCur;
	}
	public String getTradeAmt() {
		return TradeAmt;
	}
	public void setTradeAmt(String tradeAmt) {
		TradeAmt = tradeAmt;
	}
	public String getTradeDate() {
		return TradeDate;
	}
	public void setTradeDate(String tradeDate) {
		TradeDate = tradeDate;
	}
	public String getTradeFee() {
		return TradeFee;
	}
	public void setTradeFee(String tradeFee) {
		TradeFee = tradeFee;
	}
	public String getOtherFee() {
		return OtherFee;
	}
	public void setOtherFee(String otherFee) {
		OtherFee = otherFee;
	}
	public String getTotalAmt() {
		return TotalAmt;
	}
	public void setTotalAmt(String totalAmt) {
		TotalAmt = totalAmt;
	}
	public String getTrxMarket() {
		return TrxMarket;
	}
	public void setTrxMarket(String trxMarket) {
		TrxMarket = trxMarket;
	}
	public String getEntrustStatus() {
		return EntrustStatus;
	}
	public void setEntrustStatus(String entrustStatus) {
		EntrustStatus = entrustStatus;
	}
	public String getProductName() {
		return ProductName;
	}
	public void setProductName(String productName) {
		ProductName = productName;
	}
	public String getTradeTax() {
		return TradeTax;
	}
	public void setTradeTax(String tradeTax) {
		TradeTax = tradeTax;
	}
	public String getTradeDateEnd() {
		return TradeDateEnd;
	}
	public void setTradeDateEnd(String tradeDateEnd) {
		TradeDateEnd = tradeDateEnd;
	}
	public String getTrustFee() {
		return TrustFee;
	}
	public void setTrustFee(String trustFee) {
		TrustFee = trustFee;
	}
	public String getInventory() {
		return Inventory;
	}
	public void setInventory(String inventory) {
		Inventory = inventory;
	}
	public String getDistributeRate() {
		return DistributeRate;
	}
	public void setDistributeRate(String distributeRate) {
		DistributeRate = distributeRate;
	}
	public String getRecordDate() {
		return RecordDate;
	}
	public void setRecordDate(String recordDate) {
		RecordDate = recordDate;
	}
	public String getStockRate() {
		return StockRate;
	}
	public void setStockRate(String stockRate) {
		StockRate = stockRate;
	}
	public String getDistributeDate() {
		return DistributeDate;
	}
	public void setDistributeDate(String distributeDate) {
		DistributeDate = distributeDate;
	}
	public String getDividend() {
		return Dividend;
	}
	public void setDividend(String dividend) {
		Dividend = dividend;
	}
	public String getReceiveAmt() {
		return ReceiveAmt;
	}
	public void setReceiveAmt(String receiveAmt) {
		ReceiveAmt = receiveAmt;
	}
	public String getTaxRate() {
		return TaxRate;
	}
	public void setTaxRate(String taxRate) {
		TaxRate = taxRate;
	}
	public String getReferenceRate() {
		return ReferenceRate;
	}
	public void setReferenceRate(String referenceRate) {
		ReferenceRate = referenceRate;
	}
	public String getDistributeAmt() {
		return DistributeAmt;
	}
	public void setDistributeAmt(String distributeAmt) {
		DistributeAmt = distributeAmt;
	}
	public String getTradeCostAcct() {
		return TradeCostAcct;
	}
	public void setTradeCostAcct(String tradeCostAcct) {
		TradeCostAcct = tradeCostAcct;
	}
	public String getTradeEarnAcct() {
		return TradeEarnAcct;
	}
	public void setTradeEarnAcct(String tradeEarnAcct) {
		TradeEarnAcct = tradeEarnAcct;
	}
	public String getTradeCostBal() {
		return TradeCostBal;
	}
	public void setTradeCostBal(String tradeCostBal) {
		TradeCostBal = tradeCostBal;
	}
	public String getELAmt() {
		return ELAmt;
	}
	public void setELAmt(String eLAmt) {
		ELAmt = eLAmt;
	}
	public String getReturnRateSign() {
		return ReturnRateSign;
	}
	public void setReturnRateSign(String returnRateSign) {
		ReturnRateSign = returnRateSign;
	}
	public String getReturnRate() {
		return ReturnRate;
	}
	public void setReturnRate(String returnRate) {
		ReturnRate = returnRate;
	}
	public String getPayDay() {
		return PayDay;
	}
	public void setPayDay(String payDay) {
		PayDay = payDay;
	}
	public String getSellPayDay() {
		return SellPayDay;
	}
	public void setSellPayDay(String sellPayDay) {
		SellPayDay = sellPayDay;
	}
	public String getELAmtSign() {
		return ELAmtSign;
	}
	public void setELAmtSign(String eLAmtSign) {
		ELAmtSign = eLAmtSign;
	}
	
}