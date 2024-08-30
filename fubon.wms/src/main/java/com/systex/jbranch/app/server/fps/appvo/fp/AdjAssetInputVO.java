package com.systex.jbranch.app.server.fps.appvo.fp;

import java.math.BigDecimal;

public class AdjAssetInputVO {

	private String custID;// 客戶ID
	private String calSeq;// 資產調整VO.試算流水號
//	private String reqType;// 計劃類別
	private String ptype;// 資產類別
//	private String reqAssetType;// 需求套餐類別
//	private String trustNo;// 憑證編號
	private String txnNo;//憑證編號
	private String prdID;// 產品ID
	private String trfPrdID;// 轉入產品id
	private String adjMethod;// 調整方法
	private String adjType;// 調整方向
	private BigDecimal trustAmt;// 原始信託金額
	private BigDecimal trustPUnit;// 原始信託單位數
	private String trustType;// 信託方式
	private String trustCcy;// 申購幣別
	private BigDecimal adjAmtOrg;// 原幣調整金額
	private BigDecimal adjAmtTwd;// 台幣調整金額
	private BigDecimal adjPUnit;// 贖回單位數
//	private String isDmfParent;// 動態鎖利母基金標記
	private BigDecimal prdLastNav;
	private Long seq;
	private String pname;
	private String riskLevel;
	private String txnid;//呼叫的交易代號
	
	public String getTxnid() {
		return txnid;
	}
	
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	public String getTxnNo() {
		return txnNo;
	}
	public void setTxnNo(String txnNo) {
		this.txnNo = txnNo;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String name) {
		pname = name;
	}
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCalSeq() {
		return calSeq;
	}
	public void setCalSeq(String calSeq) {
		this.calSeq = calSeq;
	}
	public String getPrdID() {
		return prdID;
	}
	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}
	public String getTrfPrdID() {
		return trfPrdID;
	}
	public void setTrfPrdID(String trfPrdID) {
		this.trfPrdID = trfPrdID;
	}
	public String getAdjMethod() {
		return adjMethod;
	}
	public void setAdjMethod(String adjMethod) {
		this.adjMethod = adjMethod;
	}
	public String getAdjType() {
		return adjType;
	}
	public void setAdjType(String adjType) {
		this.adjType = adjType;
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
	public String getTrustCcy() {
		return trustCcy;
	}
	public void setTrustCcy(String trustCcy) {
		this.trustCcy = trustCcy;
	}
	public BigDecimal getAdjAmtOrg() {
		return adjAmtOrg;
	}
	public void setAdjAmtOrg(BigDecimal adjAmtOrg) {
		this.adjAmtOrg = adjAmtOrg;
	}
	public BigDecimal getAdjAmtTwd() {
		return adjAmtTwd;
	}
	public void setAdjAmtTwd(BigDecimal adjAmtTwd) {
		this.adjAmtTwd = adjAmtTwd;
	}
	public BigDecimal getAdjPUnit() {
		return adjPUnit;
	}
	public void setAdjPUnit(BigDecimal adjPUnit) {
		this.adjPUnit = adjPUnit;
	}
	public BigDecimal getPrdLastNav() {
		return prdLastNav;
	}
	public void setPrdLastNav(BigDecimal prdLastNav) {
		this.prdLastNav = prdLastNav;
	}
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}

}