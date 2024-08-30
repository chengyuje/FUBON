package com.systex.jbranch.app.server.fps.org140;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG140InputVO extends PagingInputVO{
	
	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
	private String empId;           //員工編號
	private String empName;         //員工姓名
	private String probation;       //過試用期
	private Date onboardDateStart;  //入行日(起)
	private Date onboardDateEnd;    //入行日(迄)
//	private List chkCode;           //被選取證照編號
	private String certID;
	private String privilegeID;
	
	public String getPrivilegeID() {
		return privilegeID;
	}

	public void setPrivilegeID(String privilegeID) {
		this.privilegeID = privilegeID;
	}

	public String getCertID() {
		return certID;
	}

	public void setCertID(String certID) {
		this.certID = certID;
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

	public String getEmpId() {
		return empId;
	}
	
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
	public String getEmpName() {
		return empName;
	}
	
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	
	public String getProbation() {
		return probation;
	}
	
	public void setProbation(String probation) {
		this.probation = probation;
	}
	
	public Date getOnboardDateStart() {
		return onboardDateStart;
	}
	
	public void setOnboardDateStart(Date onboardDateStart) {
		this.onboardDateStart = onboardDateStart;
	}
	
	public Date getOnboardDateEnd() {
		return onboardDateEnd;
	}
	
	public void setOnboardDateEnd(Date onboardDateEnd) {
		this.onboardDateEnd = onboardDateEnd;
	}

}
