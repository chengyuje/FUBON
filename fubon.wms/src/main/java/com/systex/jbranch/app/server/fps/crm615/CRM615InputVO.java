package com.systex.jbranch.app.server.fps.crm615;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM615InputVO extends PagingInputVO {
	private String type;
	private String cust_id;
	private String ao_code;
	private String year;
	private String month;
	
	
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
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
}
