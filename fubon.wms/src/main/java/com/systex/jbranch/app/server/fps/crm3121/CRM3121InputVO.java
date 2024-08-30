package com.systex.jbranch.app.server.fps.crm3121;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM3121InputVO extends PagingInputVO{
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String new_ao_code;
	private Date limit_s_date;
	private Date limit_e_date;
	private List<Map<String, Object>> list;
	
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getNew_ao_code() {
		return new_ao_code;
	}
	public void setNew_ao_code(String new_ao_code) {
		this.new_ao_code = new_ao_code;
	}
	public Date getLimit_s_date() {
		return limit_s_date;
	}
	public void setLimit_s_date(Date limit_s_date) {
		this.limit_s_date = limit_s_date;
	}
	public Date getLimit_e_date() {
		return limit_e_date;
	}
	public void setLimit_e_date(Date limit_e_date) {
		this.limit_e_date = limit_e_date;
	}
	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}


}
