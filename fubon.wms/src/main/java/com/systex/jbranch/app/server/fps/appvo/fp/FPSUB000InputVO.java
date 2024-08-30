package com.systex.jbranch.app.server.fps.appvo.fp;

import java.util.List;
import java.util.Map;

public class FPSUB000InputVO {

	public FPSUB000InputVO() {
		
	}
	
	public String loginID;//登入者
	public String loginRole;//登入者角色
	public List<Map<String,String>> AvailBranchList;//可查看分行清單
	public String custID;//客戶ID
	
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public String getLoginRole() {
		return loginRole;
	}
	public void setLoginRole(String loginRole) {
		this.loginRole = loginRole;
	}
	public List<Map<String, String>> getAvailBranchList() {
		return AvailBranchList;
	}
	public void setAvailBranchList(List<Map<String, String>> availBranchList) {
		AvailBranchList = availBranchList;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
}