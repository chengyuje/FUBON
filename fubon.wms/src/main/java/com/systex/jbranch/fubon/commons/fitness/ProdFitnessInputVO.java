package com.systex.jbranch.fubon.commons.fitness;

import java.util.Date;

public class ProdFitnessInputVO {
	private String custID;
	private Date IOTApplyDate; //要保書申請日

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
	
}