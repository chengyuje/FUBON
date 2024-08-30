package com.systex.jbranch.app.server.fps.appvo.ins;

import java.util.Date;

public class InsCustVO {
	private String custID;		//客戶ID(即要保人ID)
	private String custName;	//客戶姓名(即要保人姓名)
	private String custRiskAttr;//客戶風險屬性
	private String insuredID;	//被保人ID
	private String insuredName;	//被保人姓名
	private Date insuredBirthDate;//被保人生日
	private Number insuredAge;	//保險年齡
	private String gender;		//性別 (M:男性，F:女性)
	private String marriage;	//婚姻狀況 (1:未婚，2:已婚，3:其他)
	private StatementVO statement;//需求問卷資料
	
	public Date getInsuredBirthDate() {
		return insuredBirthDate;
	}
	public void setInsuredBirthDate(Date insuredBirthDate) {
		this.insuredBirthDate = insuredBirthDate;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustRiskAttr() {
		return custRiskAttr;
	}
	public void setCustRiskAttr(String custRiskAttr) {
		this.custRiskAttr = custRiskAttr;
	}
	public String getInsuredID() {
		return insuredID;
	}
	public void setInsuredID(String insuredID) {
		this.insuredID = insuredID;
	}
	public String getInsuredName() {
		return insuredName;
	}
	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}
	
	public Number getInsuredAge() {
		return insuredAge;
	}
	public void setInsuredAge(Number insuredAge) {
		this.insuredAge = insuredAge;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMarriage() {
		return marriage;
	}
	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}
	public StatementVO getStatement() {
		return statement;
	}
	public void setStatement(StatementVO statement) {
		this.statement = statement;
	}
	
}