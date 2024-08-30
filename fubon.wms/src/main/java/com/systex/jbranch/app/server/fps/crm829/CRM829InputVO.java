package com.systex.jbranch.app.server.fps.crm829;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM829InputVO extends PagingInputVO {
	private String cust_id;
	private String getSumYN;	//由CRM681呼叫="Y"，不須取得明細資料queryPotInfo
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getGetSumYN() {
		return getSumYN;
	}
	public void setGetSumYN(String getSumYN) {
		this.getSumYN = getSumYN;
	}
	
}
