package com.systex.jbranch.app.server.fps.crm123;

import java.util.List;

public class CRM123OutputVO {
	private List countList;
	private List countInvestList;      //募集
	private List countRestList;        //休市
	private List countDividendList;    //配息
	private List countExpiryList;      //到期 
	private List resultList;
	
	public List getCountList() {
		return countList;
	}
	public void setCountList(List countList) {
		this.countList = countList;
	}
	public List getCountInvestList() {
		return countInvestList;
	}
	public void setCountInvestList(List countInvestList) {
		this.countInvestList = countInvestList;
	}
	public List getCountRestList() {
		return countRestList;
	}
	public void setCountRestList(List countRestList) {
		this.countRestList = countRestList;
	}
	public List getCountDividendList() {
		return countDividendList;
	}
	public void setCountDividendList(List countDividendList) {
		this.countDividendList = countDividendList;
	}
	public List getCountExpiryList() {
		return countExpiryList;
	}
	public void setCountExpiryList(List countExpiryList) {
		this.countExpiryList = countExpiryList;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}


	
}
