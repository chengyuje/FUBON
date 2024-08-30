package com.systex.jbranch.app.server.fps.prd150;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD150InputVO extends PagingInputVO {
	private String type;
	private String cust_id;
	private String si_id;
	private String si_name;
	private String si_type;
	private String currency;
	private String maturity;
	private String rate_guaran;
	private String risk_level;
	private String pi_YN;
	private String hnwc_YN;
	private String obu_YN;
	private String prod_type;
	
	private Date sDate;
	private Date eDate;
	private Date s2Date;
	private Date e2Date;
	
	private String project;
	private String customer_level;
	
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
	public String getSi_id() {
		return si_id;
	}
	public void setSi_id(String si_id) {
		this.si_id = si_id;
	}
	public String getSi_name() {
		return si_name;
	}
	public void setSi_name(String si_name) {
		this.si_name = si_name;
	}
	public String getSi_type() {
		return si_type;
	}
	public void setSi_type(String si_type) {
		this.si_type = si_type;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getMaturity() {
		return maturity;
	}
	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}
	public String getRate_guaran() {
		return rate_guaran;
	}
	public void setRate_guaran(String rate_guaran) {
		this.rate_guaran = rate_guaran;
	}
	public String getRisk_level() {
		return risk_level;
	}
	public void setRisk_level(String risk_level) {
		this.risk_level = risk_level;
	}
	public String getPi_YN() {
		return pi_YN;
	}
	public void setPi_YN(String pi_YN) {
		this.pi_YN = pi_YN;
	}
	public String getHnwc_YN() {
		return hnwc_YN;
	}
	public void setHnwc_YN(String hnwc_YN) {
		this.hnwc_YN = hnwc_YN;
	}
	public String getObu_YN() {
		return obu_YN;
	}
	public void setObu_YN(String obu_YN) {
		this.obu_YN = obu_YN;
	}
	public String getProd_type() {
		return prod_type;
	}
	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	public Date getS2Date() {
		return s2Date;
	}
	public void setS2Date(Date s2Date) {
		this.s2Date = s2Date;
	}
	public Date getE2Date() {
		return e2Date;
	}
	public void setE2Date(Date e2Date) {
		this.e2Date = e2Date;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getCustomer_level() {
		return customer_level;
	}
	public void setCustomer_level(String customer_level) {
		this.customer_level = customer_level;
	}
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	
}
