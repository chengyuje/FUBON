package com.systex.jbranch.fubon.commons.seniorValidation;

import java.util.Date;

public class SeniorValidationInputVO {
	private String custID;
	private String insuredID;
	private String payerID;
	private Date IOTApplyDate; //要保書申請日
	private Date IOTApplyDate3; //要保書申請日前三個工作天
	
	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public Date getIOTApplyDate() {
		return IOTApplyDate;
	}

	public void setIOTApplyDate(Date iOTApplyDate) {
		IOTApplyDate = iOTApplyDate;
	}

	public String getInsuredID() {
		return insuredID;
	}

	public void setInsuredID(String insuredID) {
		this.insuredID = insuredID;
	}

	public String getPayerID() {
		return payerID;
	}

	public void setPayerID(String payerID) {
		this.payerID = payerID;
	}

	public Date getIOTApplyDate3() {
		return IOTApplyDate3;
	}

	public void setIOTApplyDate3(Date iOTApplyDate3) {
		IOTApplyDate3 = iOTApplyDate3;
	}
	
}