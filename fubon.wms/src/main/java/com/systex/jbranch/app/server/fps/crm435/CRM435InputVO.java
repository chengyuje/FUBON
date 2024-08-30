package com.systex.jbranch.app.server.fps.crm435;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM435InputVO extends PagingInputVO {
	private String apply_seq ; //議價編號
	private String auth_emp_id ; //簽核者員工編號

	public String getApply_seq() {
		return apply_seq;
	}

	public void setApply_seq(String apply_seq) {
		this.apply_seq = apply_seq;
	}

	public String getAuth_emp_id() {
		return auth_emp_id;
	}

	public void setAuth_emp_id(String auth_emp_id) {
		this.auth_emp_id = auth_emp_id;
	}
}
