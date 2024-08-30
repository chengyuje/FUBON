package com.systex.jbranch.app.server.fps.crm211;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.crm210.CRM210InputVO;

public class CRM211InputVO extends CRM210InputVO {
	private String role;
	private List<Map<String, String>> aolist;
	private String ao_code;
	private String cust_id;
	private String cust_name;
	private String uEmpID;

	public String getuEmpID() {
		return uEmpID;
	}

	public void setuEmpID(String uEmpID) {
		this.uEmpID = uEmpID;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public List<Map<String, String>> getAolist() {
		return aolist;
	}

	public void setAolist(List<Map<String, String>> aolist) {
		this.aolist = aolist;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
