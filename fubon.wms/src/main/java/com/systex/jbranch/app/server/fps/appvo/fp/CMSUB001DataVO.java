package com.systex.jbranch.app.server.fps.appvo.fp;

import java.util.List;

public class CMSUB001DataVO {
	
	private String loginID;
	private String loginRole;
	private List availBranchList;
	private String custID;
	private String custName;	
	
	public List getAvailBranchList() {
		return availBranchList;
	}
	public void setAvailBranchList(List availBranchList) {
		this.availBranchList = availBranchList;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getLoginRole() {
		return loginRole;
	}
	public void setLoginRole(String loginRole) {
		this.loginRole = loginRole;
	}
}
