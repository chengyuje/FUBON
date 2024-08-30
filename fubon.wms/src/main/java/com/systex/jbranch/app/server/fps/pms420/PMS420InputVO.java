package com.systex.jbranch.app.server.fps.pms420;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS420InputVO extends PagingInputVO {

	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String sCreDate;
	
	
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
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getCre_Date() {
		return sCreDate;
	}
	public void setCre_Date(String sCreDate) {
		this.sCreDate = sCreDate;
	}
}
