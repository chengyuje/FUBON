package com.systex.jbranch.app.server.fps.crm825;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM825InputVO extends PagingInputVO {
	private String prod_id;
	private String cust_id; // 客戶ID
	private String cert_id; // 憑證編號
	private List<String> prod_id_list;

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getCert_id() {
		return cert_id;
	}

	public void setCert_id(String cert_id) {
		this.cert_id = cert_id;
	}

	public String getProd_id() {
		return prod_id;
	}

	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}

	public List<String> getProd_id_list() {
		return prod_id_list;
	}

	public void setProd_id_list(List<String> prod_id_list) {
		this.prod_id_list = prod_id_list;
	}
}
