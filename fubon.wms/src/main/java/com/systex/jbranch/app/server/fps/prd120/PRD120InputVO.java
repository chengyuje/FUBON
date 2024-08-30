package com.systex.jbranch.app.server.fps.prd120;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD120InputVO extends PagingInputVO {
	private String type;
	private String cust_id;
	private String etf_code;
	private String etf_name;
	private String risk_level;
	private String currency;
	private String pi_YN;
	private String country;
	private String tactics;
	private String invest_type;
	private String company;
	
	private String stock_code;
	private String stock_name;
	private String stock_type;
	private String industry_type;
	
	// upload
	private String prd_id;
	private String ptype;
	private String subsystem_type;
	private String doc_type;
	//
	private String stock_bond_type;
	
	//#1404 標籤
	private String customer_level;
	private String project;
	
	private String trustTS; //M:金錢信託 S:特金
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getEtf_code() {
		return etf_code;
	}
	public void setEtf_code(String etf_code) {
		this.etf_code = etf_code;
	}
	public String getEtf_name() {
		return etf_name;
	}
	public void setEtf_name(String etf_name) {
		this.etf_name = etf_name;
	}
	public String getRisk_level() {
		return risk_level;
	}
	public void setRisk_level(String risk_level) {
		this.risk_level = risk_level;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPi_YN() {
		return pi_YN;
	}
	public void setPi_YN(String pi_YN) {
		this.pi_YN = pi_YN;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getTactics() {
		return tactics;
	}
	public void setTactics(String tactics) {
		this.tactics = tactics;
	}
	public String getInvest_type() {
		return invest_type;
	}
	public void setInvest_type(String invest_type) {
		this.invest_type = invest_type;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getStock_code() {
		return stock_code;
	}
	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}
	public String getStock_name() {
		return stock_name;
	}
	public void setStock_name(String stock_name) {
		this.stock_name = stock_name;
	}
	public String getStock_type() {
		return stock_type;
	}
	public void setStock_type(String stock_type) {
		this.stock_type = stock_type;
	}
	public String getIndustry_type() {
		return industry_type;
	}
	public void setIndustry_type(String industry_type) {
		this.industry_type = industry_type;
	}
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	public String getSubsystem_type() {
		return subsystem_type;
	}
	public void setSubsystem_type(String subsystem_type) {
		this.subsystem_type = subsystem_type;
	}
	public String getDoc_type() {
		return doc_type;
	}
	public void setDoc_type(String doc_type) {
		this.doc_type = doc_type;
	}
	public String getStock_bond_type() {
		return stock_bond_type;
	}
	public void setStock_bond_type(String stock_bond_type) {
		this.stock_bond_type = stock_bond_type;
	}
	public String getCustomer_level() {
		return customer_level;
	}
	public void setCustomer_level(String customer_level) {
		this.customer_level = customer_level;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	
}
