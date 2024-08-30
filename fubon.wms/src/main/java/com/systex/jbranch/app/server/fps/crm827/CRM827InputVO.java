package com.systex.jbranch.app.server.fps.crm827;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM827InputVO extends PagingInputVO {
	private String cust_id;
	private String prod_id;
	private String contract_no;
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getContract_no() {
		return contract_no;
	}
	public void setContract_no(String contract_no) {
		this.contract_no = contract_no;
	}
}
