package com.systex.jbranch.app.server.fps.crm616;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM616InputVO extends PagingInputVO{
	private String cust_id;
	private String data_year;

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getData_year() {
		return data_year;
	}

	public void setData_year(String data_year) {
		this.data_year = data_year;
	}

	
}
