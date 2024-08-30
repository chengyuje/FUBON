package com.systex.jbranch.app.server.fps.crm110;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM110InputVO extends PagingInputVO{
	
	private String cust_id;
	private String cust_name;
	private String ao_code;
	private String role;
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}



}
