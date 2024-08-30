package com.systex.jbranch.app.server.fps.ins610;

import java.util.Date;

public class INS610InputVO {
	
	private String status;
	private String branchAreaId; //營運區
	private String branchId;     //分行
	private String empId;        //理專員編
	private String insCode;     //規劃險種
	private String custId;
	
	private Date SD;       //建立時間(起)
	private Date ED;       //建立時間(迄)
	
	INS610InputVO() {}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBranchAreaId() {
		return branchAreaId;
	}

	public void setBranchAreaId(String branchAreaId) {
		this.branchAreaId = branchAreaId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getInsCode() {
		return insCode;
	}

	public void setInsCode(String insCode) {
		this.insCode = insCode;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public Date getsCreDate() {
		return SD;
	}

	public void setsCreDate(Date sCreDate) {
		this.SD = sCreDate;
	}

	public Date geteCreDate() {
		return ED;
	}

	public void seteCreDate(Date eCreDate) {
		this.ED = eCreDate;
	};
	
}
