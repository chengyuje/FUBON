package com.systex.jbranch.platform.server.info.fubonbranch;

import com.systex.jbranch.platform.common.dataManager.Branch;

public class WmsBranch extends Branch {
	
	private String region_center_id;
	private String region_center_name;
	private String branch_area_id;
	private String branch_area_name;
	
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getRegion_center_name() {
		return region_center_name;
	}
	public void setRegion_center_name(String region_center_name) {
		this.region_center_name = region_center_name;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getBranch_area_name() {
		return branch_area_name;
	}
	public void setBranch_area_name(String branch_area_name) {
		this.branch_area_name = branch_area_name;
	}
	
}
