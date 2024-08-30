package com.systex.jbranch.app.server.fps.crm831;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM831InputVO extends PagingInputVO{
	private String cust_id;
	private String ins_type;
	private String policy_nbr_str;
	private String policy_nbr;
	private String policy_seq;
	private String id_dup;
	private String year_mon;
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getIns_type() {
		return ins_type;
	}
	public void setIns_type(String ins_type) {
		this.ins_type = ins_type;
	}
	public String getPolicy_nbr() {
		return policy_nbr;
	}
	public void setPolicy_nbr(String policy_nbr) {
		this.policy_nbr = policy_nbr;
	}
	public String getPolicy_nbr_str() {
		return policy_nbr_str;
	}
	public void setPolicy_nbr_str(String policy_nbr_str) {
		this.policy_nbr_str = policy_nbr_str;
	}
	public String getPolicy_seq() {
		return policy_seq;
	}
	public void setPolicy_seq(String policy_seq) {
		this.policy_seq = policy_seq;
	}
	public String getId_dup() {
		return id_dup;
	}
	public void setId_dup(String id_dup) {
		this.id_dup = id_dup;
	}
	public String getYear_mon() {
		return year_mon;
	}
	public void setYear_mon(String year_mon) {
		this.year_mon = year_mon;
	}
}
