package com.systex.jbranch.app.server.fps.mao151;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MAO151InputVO extends PagingInputVO{
	
	private String emp_id;
	private String role;
	
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	
}
