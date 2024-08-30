package com.systex.jbranch.app.server.fps.pms423;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS423InputVO extends PagingInputVO {
	private String data_date;
	private String report_type;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String fileName;
	
	public String getData_date() {
		return data_date;
	}
	
	public void setData_date(String data_date) {
		this.data_date = data_date;
	}
	
	public String getReport_type() {
		return report_type;
	}
	
	public void setReport_type(String report_type) {
		this.report_type = report_type;
	}
	
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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
