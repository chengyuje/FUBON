package com.systex.jbranch.app.server.fps.prd120;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD120OutputVO extends PagingOutputVO {
	private List resultList;
	private List countryList;
	private List tacticsList;
	private List investList;
	private List companyList;
	private List stockList;
	private List industryList;
	

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getCountryList() {
		return countryList;
	}
	public void setCountryList(List countryList) {
		this.countryList = countryList;
	}
	public List getTacticsList() {
		return tacticsList;
	}
	public void setTacticsList(List tacticsList) {
		this.tacticsList = tacticsList;
	}
	public List getInvestList() {
		return investList;
	}
	public void setInvestList(List investList) {
		this.investList = investList;
	}
	public List getCompanyList() {
		return companyList;
	}
	public void setCompanyList(List companyList) {
		this.companyList = companyList;
	}
	public List getStockList() {
		return stockList;
	}
	public void setStockList(List stockList) {
		this.stockList = stockList;
	}
	public List getIndustryList() {
		return industryList;
	}
	public void setIndustryList(List industryList) {
		this.industryList = industryList;
	}
}
