package com.systex.jbranch.app.server.fps.crm151;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM151InputVO  extends PagingInputVO{
	private String Role;
	private String emp_id;
	private String priID;
	
	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getRole() {
		return Role;
	}

	public void setRole(String role) {
		Role = role;
	}

	public String getPriID() {
		return priID;
	}

	public void setPriID(String priID) {
		this.priID = priID;
	}	
	
}
