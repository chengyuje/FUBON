package com.systex.jbranch.app.server.fps.crm845;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM845InputVO extends PagingInputVO{
	private String cust_id;
	private String branch_nbr;
	private String txt_no;
	
	//
	private String func_cod;
	
	
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getTxt_no() {
		return txt_no;
	}
	public void setTxt_no(String txt_no) {
		this.txt_no = txt_no;
	}
}
