package com.systex.jbranch.fubon.commons.esb.vo.nr098n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NR098NOutputVODetails {
	@XmlElement
	private String ExchangeNo;      //交易所代號
    @XmlElement
	private String ProductNo;       //商品代號
    @XmlElement
	private String InsuranceName;   //商品名稱
    @XmlElement
	private String CurCode;         //幣別
    @XmlElement
	private String TrxMarket;       //交易市場
    @XmlElement
	private String Endtime;         //交易截止時間
    @XmlElement
	private String Date08;       //收盤價日期
    @XmlElement
	private String CurAmt;      //收盤價
    @XmlElement
	private String Lowmount;    //最少交易單位
    @XmlElement
	private String LowAmt;      //最少交易金額
    @XmlElement
	private String TrustKind;       //信託業務別
    @XmlElement
	private String Procus;          //專業投資人
    @XmlElement
	private String ProductRisk;     //資產風險別
    @XmlElement
	private String Usepoint;        //有效小數位數
    @XmlElement
	private String ProductType2;    //新舊商品
    @XmlElement
	private String TradeType;       //禁止交易
    @XmlElement
	private String W8bencode;       //W-8BEN
    @XmlElement
	private String MarketDate;  //交易市場日期
    @XmlElement
	private String PDType1;         //商品分類
    @XmlElement
	private String LmtPriceBuy; //約定限價買
    @XmlElement
	private String LmtPriceSell;//約定限價賣
    @XmlElement
	private String ProdCountry;     //投資地區
    @XmlElement
	private String ProdutCom;       //發行公司
    @XmlElement
	private String PricdType;       //是否報價
    @XmlElement
	private String StartTime;       //交易開始時間
    @XmlElement
	private String ProductType;     //商品類型
    @XmlElement
	private String FubStartTime;    //約定限價起始時間
    @XmlElement
	private String FubEndTime;      //約定限價結束時間
    @XmlElement
	private String TrxMarketCode;   //市場代碼
    @XmlElement
	private String DivideStartTime; //零股時間起
    @XmlElement
	private String DivideEndTime;   //零股時間迄
    @XmlElement
	private String SellByMKT;       //賣出可否使用市價
    @XmlElement
	private String ShortPdName;     //存摺簡稱
    @XmlElement
	private String DbuObu;          //DBU/OBU
	public String getExchangeNo() {
		return ExchangeNo;
	}
	public void setExchangeNo(String exchangeNo) {
		ExchangeNo = exchangeNo;
	}
	public String getProductNo() {
		return ProductNo;
	}
	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}
	public String getInsuranceName() {
		return InsuranceName;
	}
	public void setInsuranceName(String insuranceName) {
		InsuranceName = insuranceName;
	}
	public String getCurCode() {
		return CurCode;
	}
	public void setCurCode(String curCode) {
		CurCode = curCode;
	}
	public String getTrxMarket() {
		return TrxMarket;
	}
	public void setTrxMarket(String trxMarket) {
		TrxMarket = trxMarket;
	}
	public String getEndtime() {
		return Endtime;
	}
	public void setEndtime(String endtime) {
		Endtime = endtime;
	}
	public String getDate08() {
		return Date08;
	}
	public void setDate08(String date08) {
		Date08 = date08;
	}
	public String getCurAmt() {
		return CurAmt;
	}
	public void setCurAmt(String curAmt) {
		CurAmt = curAmt;
	}
	public String getLowmount() {
		return Lowmount;
	}
	public void setLowmount(String lowmount) {
		Lowmount = lowmount;
	}
	public String getLowAmt() {
		return LowAmt;
	}
	public void setLowAmt(String lowAmt) {
		LowAmt = lowAmt;
	}
	public String getTrustKind() {
		return TrustKind;
	}
	public void setTrustKind(String trustKind) {
		TrustKind = trustKind;
	}
	public String getProcus() {
		return Procus;
	}
	public void setProcus(String procus) {
		Procus = procus;
	}
	public String getProductRisk() {
		return ProductRisk;
	}
	public void setProductRisk(String productRisk) {
		ProductRisk = productRisk;
	}
	public String getUsepoint() {
		return Usepoint;
	}
	public void setUsepoint(String usepoint) {
		Usepoint = usepoint;
	}
	public String getProductType2() {
		return ProductType2;
	}
	public void setProductType2(String productType2) {
		ProductType2 = productType2;
	}
	public String getTradeType() {
		return TradeType;
	}
	public void setTradeType(String tradeType) {
		TradeType = tradeType;
	}
	public String getW8bencode() {
		return W8bencode;
	}
	public void setW8bencode(String w8bencode) {
		W8bencode = w8bencode;
	}
	public String getMarketDate() {
		return MarketDate;
	}
	public void setMarketDate(String marketDate) {
		MarketDate = marketDate;
	}
	public String getPDType1() {
		return PDType1;
	}
	public void setPDType1(String pDType1) {
		PDType1 = pDType1;
	}
	public String getLmtPriceBuy() {
		return LmtPriceBuy;
	}
	public void setLmtPriceBuy(String lmtPriceBuy) {
		LmtPriceBuy = lmtPriceBuy;
	}
	public String getLmtPriceSell() {
		return LmtPriceSell;
	}
	public void setLmtPriceSell(String lmtPriceSell) {
		LmtPriceSell = lmtPriceSell;
	}
	public String getProdCountry() {
		return ProdCountry;
	}
	public void setProdCountry(String prodCountry) {
		ProdCountry = prodCountry;
	}
	public String getProdutCom() {
		return ProdutCom;
	}
	public void setProdutCom(String produtCom) {
		ProdutCom = produtCom;
	}
	public String getPricdType() {
		return PricdType;
	}
	public void setPricdType(String pricdType) {
		PricdType = pricdType;
	}
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getProductType() {
		return ProductType;
	}
	public void setProductType(String productType) {
		ProductType = productType;
	}
	public String getFubStartTime() {
		return FubStartTime;
	}
	public void setFubStartTime(String fubStartTime) {
		FubStartTime = fubStartTime;
	}
	public String getFubEndTime() {
		return FubEndTime;
	}
	public void setFubEndTime(String fubEndTime) {
		FubEndTime = fubEndTime;
	}
	public String getTrxMarketCode() {
		return TrxMarketCode;
	}
	public void setTrxMarketCode(String trxMarketCode) {
		TrxMarketCode = trxMarketCode;
	}
	public String getDivideStartTime() {
		return DivideStartTime;
	}
	public void setDivideStartTime(String divideStartTime) {
		DivideStartTime = divideStartTime;
	}
	public String getDivideEndTime() {
		return DivideEndTime;
	}
	public void setDivideEndTime(String divideEndTime) {
		DivideEndTime = divideEndTime;
	}
	public String getSellByMKT() {
		return SellByMKT;
	}
	public void setSellByMKT(String sellByMKT) {
		SellByMKT = sellByMKT;
	}
	public String getShortPdName() {
		return ShortPdName;
	}
	public void setShortPdName(String shortPdName) {
		ShortPdName = shortPdName;
	}
	public String getDbuObu() {
		return DbuObu;
	}
	public void setDbuObu(String dbuObu) {
		DbuObu = dbuObu;
	}
}
