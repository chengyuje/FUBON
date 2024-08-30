package com.systex.jbranch.app.server.fps.appvo.fp;

import java.math.BigDecimal;

public class FPAAL002InputVO {

	private String trfType;//轉換類型
	private String prdID;//商品ID
	private String prdName;//商品名稱
	private String ccy;//計價幣別
	private String txnNo;//憑證編號
	private BigDecimal trustAmt;//原始信託本金
	private BigDecimal trustPUnit;//持有單位
	private String trustType;//信託方式
	private BigDecimal avalAmt;//	原幣參考現值
	private BigDecimal twdPstAmt;//	台幣參考現值
	private BigDecimal exRate;//參考匯率
	private BigDecimal prdLastNav;//淨值資料
	private String adjMethod;//調整方法
	private String custID;
	private String groupCode;
	private String navDate;//淨值日期
	
	public String getPrdID() {
		return prdID;
	}
	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}
	public String getPrdName() {
		return prdName;
	}
	public void setPrdName(String prdName) {
		this.prdName = prdName;
	}
	public String getCcy() {
		return ccy;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public BigDecimal getTrustAmt() {
		return trustAmt;
	}
	public void setTrustAmt(BigDecimal trustAmt) {
		this.trustAmt = trustAmt;
	}
	public BigDecimal getTrustPUnit() {
		return trustPUnit;
	}
	public void setTrustPUnit(BigDecimal trustPUnit) {
		this.trustPUnit = trustPUnit;
	}
	public String getTrustType() {
		return trustType;
	}
	public void setTrustType(String trustType) {
		this.trustType = trustType;
	}
	public BigDecimal getAvalAmt() {
		return avalAmt;
	}
	public void setAvalAmt(BigDecimal avalAmt) {
		this.avalAmt = avalAmt;
	}
	public BigDecimal getTwdPstAmt() {
		return twdPstAmt;
	}
	public void setTwdPstAmt(BigDecimal twdPstAmt) {
		this.twdPstAmt = twdPstAmt;
	}
	public BigDecimal getExRate() {
		return exRate;
	}
	public void setExRate(BigDecimal exRate) {
		this.exRate = exRate;
	}
	public BigDecimal getPrdLastNav() {
		return prdLastNav;
	}
	public void setPrdLastNav(BigDecimal prdLastNav) {
		this.prdLastNav = prdLastNav;
	}
	public String getAdjMethod() {
		return adjMethod;
	}
	public void setAdjMethod(String adjMethod) {
		this.adjMethod = adjMethod;
	}
	public void setTrfType(String trfType) {
		this.trfType = trfType;
	}
	public String getTrfType() {
		return trfType;
	}
	public void setTxnNo(String txnNo) {
		this.txnNo = txnNo;
	}
	public String getTxnNo() {
		return txnNo;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getNavDate() {
		return navDate;
	}
	public void setNavDate(String navDate) {
		this.navDate = navDate;
	}

}
