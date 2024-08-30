package com.systex.jbranch.app.server.fps.crm241;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM241InputVO extends PagingInputVO{
	private String center_id;    //區域
	private String area_id;      //營運區
	private String bra_nbr;      //分行
	private String query_type;   //查詢類別
	private String query_month;  //查詢月份
	private String inq_ao_code;  //查詢AO
	
	public String getCenter_id() {
		return center_id;
	}
	public void setCenter_id(String center_id) {
		this.center_id = center_id;
	}
	public String getArea_id() {
		return area_id;
	}
	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}
	public String getBra_nbr() {
		return bra_nbr;
	}
	public void setBra_nbr(String bra_nbr) {
		this.bra_nbr = bra_nbr;
	}
	public String getQuery_type() {
		return query_type;
	}
	public void setQuery_type(String query_type) {
		this.query_type = query_type;
	}
	public String getQuery_month() {
		return query_month;
	}
	public void setQuery_month(String query_month) {
		this.query_month = query_month;
	}
	public String getInq_ao_code() {
		return inq_ao_code;
	}
	public void setInq_ao_code(String ao_code) {
		this.inq_ao_code = ao_code;
	}
	
}
