package com.systex.jbranch.app.server.fps.crm683;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM683InputVO extends PagingInputVO{
	private String cust_id;
	private String contract_no;
	private Boolean isListed;
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getContract_no() {
		return contract_no;
	}
	public void setContract_no(String contract_no) {
		this.contract_no = contract_no;
	}
	public Boolean getIsListed() {
		return isListed;
	}
	public void setIsListed(Boolean isListed) {
		this.isListed = isListed;
	}

	
}
