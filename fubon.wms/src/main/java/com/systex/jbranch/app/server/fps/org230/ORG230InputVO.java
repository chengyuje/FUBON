package com.systex.jbranch.app.server.fps.org230;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG230InputVO extends PagingInputVO {

	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
	private String FILE_NAME        = "";
	private String ACTUAL_FILE_NAME = "";
	
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
	
	public String getFILE_NAME() {
		return FILE_NAME;
	}
	
	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}
	
	public String getACTUAL_FILE_NAME() {
		return ACTUAL_FILE_NAME;
	}
	
	public void setACTUAL_FILE_NAME(String aCTUAL_FILE_NAME) {
		ACTUAL_FILE_NAME = aCTUAL_FILE_NAME;
	}
}
