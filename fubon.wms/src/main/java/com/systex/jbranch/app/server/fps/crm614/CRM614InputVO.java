package com.systex.jbranch.app.server.fps.crm614;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM614InputVO extends PagingInputVO{
	private String cust_name;
	private String cust_id;
	private String bra_nbr;
	private String vip_degree;
	private String ao_code;
//	private String 專屬理專;
	
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getBra_nbr() {
		return bra_nbr;
	}
	public void setBra_nbr(String bra_nbr) {
		this.bra_nbr = bra_nbr;
	}
	public String getVip_degree() {
		return vip_degree;
	}
	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

}
