package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.Date;

public class SetInsCustInfoInputVO {
	
	public SetInsCustInfoInputVO()
	{
	}
	
	private String insCustID;			// 客戶ID
	private String insCTCBCust;			// 是否為中信客戶
	private String insCustName;			// 客戶姓名
	private Date insCustBirthday;		// 客戶生日
	private String insCustGender;		// 客戶性別
	private String insCustMarriageStatus;// 客戶婚姻狀況

	public String getInsCustID() {
		return insCustID;
	}
	public void setInsCustID(String insCustID) {
		this.insCustID = insCustID;
	}
	public String getInsCTCBCust() {
		return insCTCBCust;
	}
	public void setInsCTCBCust(String insCTCBCust) {
		this.insCTCBCust = insCTCBCust;
	}
	public String getInsCustName() {
		return insCustName;
	}
	public void setInsCustName(String insCustName) {
		this.insCustName = insCustName;
	}
	public Date getInsCustBirthday() {
		return insCustBirthday;
	}
	public void setInsCustBirthday(Date insCustBirthday) {
		this.insCustBirthday = insCustBirthday;
	}
	public String getInsCustGender() {
		return insCustGender;
	}
	public void setInsCustGender(String insCustGender) {
		this.insCustGender = insCustGender;
	}
	public String getInsCustMarriageStatus() {
		return insCustMarriageStatus;
	}
	public void setInsCustMarriageStatus(String insCustMarriageStatus) {
		this.insCustMarriageStatus = insCustMarriageStatus;
	}
}
