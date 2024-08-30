package com.systex.jbranch.app.server.fps.crm3b1;

import java.util.List;

public class CRM3B1OutputVO {
	
	private List resultList;
	private String CUST_NAME;
	private String errMsg;
	

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	
}
