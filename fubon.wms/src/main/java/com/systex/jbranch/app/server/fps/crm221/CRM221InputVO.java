package com.systex.jbranch.app.server.fps.crm221;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.crm210.CRM210InputVO;

public class CRM221InputVO extends CRM210InputVO {
	private String role;
	private List<Map<String, String>> aolist;
	private List<Map<String, String>> branch_list;
	private String ao_code;
	private String cust_id;
	private String cust_name;
	private String ao_03;
	private String ao_04;
	private String ao_05;
	//	private String 銷售人員;

	private String uEmpID;

	public String getuEmpID() {
		return uEmpID;
	}

	public void setuEmpID(String uEmpID) {
		this.uEmpID = uEmpID;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<Map<String, String>> getAolist() {
		return aolist;
	}

	public void setAolist(List<Map<String, String>> aolist) {
		this.aolist = aolist;
	}

	public List<Map<String, String>> getBranch_list() {
		return branch_list;
	}

	public void setBranch_list(List<Map<String, String>> branch_list) {
		this.branch_list = branch_list;
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

	public String getAo_03() {
		return ao_03;
	}

	public void setAo_03(String ao_03) {
		this.ao_03 = ao_03;
	}

	public String getAo_04() {
		return ao_04;
	}

	public void setAo_04(String ao_04) {
		this.ao_04 = ao_04;
	}

	public String getAo_05() {
		return ao_05;
	}

	public void setAo_05(String ao_05) {
		this.ao_05 = ao_05;
	}
}
