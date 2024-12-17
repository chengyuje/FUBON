package com.systex.jbranch.app.server.fps.crm82B;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM82BInputVO extends PagingInputVO {
	private String cust_id;
	private String prod_id;
	
	public String getPrd_id() {
		return prod_id;
	}
	public void setPrd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
}
