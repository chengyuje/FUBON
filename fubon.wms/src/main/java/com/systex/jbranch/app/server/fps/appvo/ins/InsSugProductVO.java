package com.systex.jbranch.app.server.fps.appvo.ins;

import java.math.BigDecimal;

public class InsSugProductVO{
	private String allProductsID;	//資訊源代碼
	private String prodID;			//商品代碼
	private String prodName;		//商品名稱
	private String corpNM;			//保險公司
	private BigDecimal basProtect;	//(基本)保額
	private String coverCaculUnitDesc; //保單計價單位  [2015.10.27] modified by William: 因應保險資訊源新增
	private int coverCaculUnit; 	//單位基數  [2015.10.30] modified by William: 因應保險資訊源新增
	private String protect;			//保額(可能會帶註記，所以訂為String)
	private BigDecimal gusPremiums;	//預估年繳保費
	private String repayment;		//還本金額(可能會帶註記，所以訂為String)
	private Integer dailyPay;		//住院日額
	private Integer timesPay;		//實支實付上限
	private Integer oncePay;		//一次給付
	private String isFeatured;		//是否精選
	private String isAnnuities;		//是否年金
	private	String premTerm;		//繳費年期
	private	String accuTerm;		//保障年期
	
	public String getAllProductsID() {
		return allProductsID;
	}
	public void setAllProductsID(String allProductsID) {
		this.allProductsID = allProductsID;
	}
	public String getProdID() {
		return prodID;
	}
	public void setProdID(String prodID) {
		this.prodID = prodID;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getCorpNM() {
		return corpNM;
	}
	public void setCorpNM(String corpNM) {
		this.corpNM = corpNM;
	}
	public BigDecimal getBasProtect() {
		return basProtect;
	}
	public void setBasProtect(BigDecimal basProtect) {
		this.basProtect = basProtect;
	}
	public String getCoverCaculUnitDesc() {
		return coverCaculUnitDesc;
	}
	public void setCoverCaculUnitDesc(String coverCaculUnitDesc) {
		this.coverCaculUnitDesc = coverCaculUnitDesc;
	}
	public int getCoverCaculUnit() {
		return coverCaculUnit;
	}
	public void setCoverCaculUnit(int coverCaculUnit) {
		this.coverCaculUnit = coverCaculUnit;
	}
	public String getProtect() {
		return protect;
	}
	public void setProtect(String protect) {
		this.protect = protect;
	}
	public BigDecimal getGusPremiums() {
		return gusPremiums;
	}
	public void setGusPremiums(BigDecimal gusPremiums) {
		this.gusPremiums = gusPremiums;
	}
	public String getRepayment() {
		return repayment;
	}
	public void setRepayment(String repayment) {
		this.repayment = repayment;
	}
	public Integer getDailyPay() {
		return dailyPay;
	}
	public void setDailyPay(Integer dailyPay) {
		this.dailyPay = dailyPay;
	}
	public Integer getTimesPay() {
		return timesPay;
	}
	public void setTimesPay(Integer timesPay) {
		this.timesPay = timesPay;
	}
	public Integer getOncePay() {
		return oncePay;
	}
	public void setOncePay(Integer oncePay) {
		this.oncePay = oncePay;
	}
	public String getIsFeatured() {
		return isFeatured;
	}
	public void setIsFeatured(String isFeatured) {
		this.isFeatured = isFeatured;
	}
	public String getIsAnnuities() {
		return isAnnuities;
	}
	public void setIsAnnuities(String isAnnuities) {
		this.isAnnuities = isAnnuities;
	}
	public String getPremTerm() {
		return premTerm;
	}
	public void setPremTerm(String premTerm) {
		this.premTerm = premTerm;
	}
	public String getAccuTerm() {
		return accuTerm;
	}
	public void setAccuTerm(String accuTerm) {
		this.accuTerm = accuTerm;
	}
}