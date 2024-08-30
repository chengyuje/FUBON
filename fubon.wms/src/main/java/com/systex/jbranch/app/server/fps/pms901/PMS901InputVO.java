package com.systex.jbranch.app.server.fps.pms901;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS901InputVO extends PagingInputVO{
	private Date sDate;
	private Date eDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String setupCategory;
	private String setupProd;
	private String setupOrg;
	
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
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
	public String getSetupCategory() {
		return setupCategory;
	}
	public void setSetupCategory(String setupCategory) {
		this.setupCategory = setupCategory;
	}
	public String getSetupProd() {
		return setupProd;
	}
	public void setSetupProd(String setupProd) {
		this.setupProd = setupProd;
	}
	public String getSetupOrg() {
		return setupOrg;
	}
	public void setSetupOrg(String setupOrg) {
		this.setupOrg = setupOrg;
	}
	
	
}
