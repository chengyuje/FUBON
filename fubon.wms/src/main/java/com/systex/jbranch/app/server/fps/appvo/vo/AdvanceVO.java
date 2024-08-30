package com.systex.jbranch.app.server.fps.appvo.vo;

public class AdvanceVO {

	private String userLogEmpID;//建立者員編
	private String empName;//建立者姓名
	private String logDateTime;//製表時間
	private String descProduct;//投資標的
	private String descProdRisk;//產品風險等級
	private String descCustRisk;//客戶屬性
	//暫時保留
	private String acFlag;//主動推介類型
	private String acFlagTime;//主動推介異動時間
	private String pcFlag;//專業投資人類型
	private String pcFlagTime;//專業投資人異動時間
	public String getUserLogEmpID() {
		return userLogEmpID;
	}
	public void setUserLogEmpID(String userLogEmpID) {
		this.userLogEmpID = userLogEmpID;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getLogDateTime() {
		return logDateTime;
	}
	public void setLogDateTime(String logDateTime) {
		this.logDateTime = logDateTime;
	}
	public String getDescProduct() {
		return descProduct;
	}
	public void setDescProduct(String descProduct) {
		this.descProduct = descProduct;
	}
	public String getDescProdRisk() {
		return descProdRisk;
	}
	public void setDescProdRisk(String descProdRisk) {
		this.descProdRisk = descProdRisk;
	}
	public String getDescCustRisk() {
		return descCustRisk;
	}
	public void setDescCustRisk(String descCustRisk) {
		this.descCustRisk = descCustRisk;
	}
	public String getAcFlag() {
		return acFlag;
	}
	public void setAcFlag(String acFlag) {
		this.acFlag = acFlag;
	}
	public String getAcFlagTime() {
		return acFlagTime;
	}
	public void setAcFlagTime(String acFlagTime) {
		this.acFlagTime = acFlagTime;
	}
	public String getPcFlag() {
		return pcFlag;
	}
	public void setPcFlag(String pcFlag) {
		this.pcFlag = pcFlag;
	}
	public String getPcFlagTime() {
		return pcFlagTime;
	}
	public void setPcFlagTime(String pcFlagTime) {
		this.pcFlagTime = pcFlagTime;
	}
}
