package com.systex.jbranch.app.server.fps.ins400;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS400InputVO extends PagingInputVO{
	private String cust_id;
	private String child_Name;
	private List<Map<String,Object>> list;

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getChild_Name() {
		return child_Name;
	}

	public void setChild_Name(String child_Name) {
		this.child_Name = child_Name;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}


	
}
