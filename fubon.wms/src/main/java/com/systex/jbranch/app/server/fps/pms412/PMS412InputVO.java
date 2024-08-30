package com.systex.jbranch.app.server.fps.pms412;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS412InputVO extends PagingInputVO{	
 private String	 region_center_id;    //區域中心
 private String	branch_area_id ; 	//營運區
 private String	branch_nbr;			//分行
 private String	emp_id ; 			//員編
 private Date	sCreDate;     
 
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
public Date getsCreDate() {
	return sCreDate;
}
public void setsCreDate(Date sCreDate) {
	this.sCreDate = sCreDate;
}

	
	
	
}
