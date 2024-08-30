package com.systex.jbranch.app.server.fps.cmmgr001;

import java.util.List;
import java.util.Map;

public class CMMGR001OutputVO {
	
	public CMMGR001OutputVO()
	{
	}
	private String roleid;
	private String rolename;
	private String approvalLevel;
	private String mainPage;
	private List<CMMGR001OutputVO2> functionList;
	private List<Map<String,Object>> roleList;
	private List<Map<String,Object>> groupList;
	private List<String> allowList;
	
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public List<CMMGR001OutputVO2> getFunctionList() {
		return functionList;
	}
	public void setFunctionList(List<CMMGR001OutputVO2> functionList) {
		this.functionList = functionList;
	}
	public void setRoleList(List<Map<String,Object>> roleList) {
		this.roleList = roleList;
	}
	public List<Map<String,Object>> getRoleList() {
		return roleList;
	}
	public void setGroupList(List<Map<String,Object>> groupList) {
		this.groupList = groupList;
	}
	public List<Map<String,Object>> getGroupList() {
		return groupList;
	}
	public String getApprovalLevel() {
		return approvalLevel;
	}
	public void setApprovalLevel(String approvalLevel) {
		this.approvalLevel = approvalLevel;
	}
	public String getMainPage() {
		return mainPage;
	}
	public void setMainPage(String mainPage) {
		this.mainPage = mainPage;
	}
	public void setAllowList(List<String> allowList) {
		this.allowList = allowList;
	}
	public List<String> getAllowList() {
		return allowList;
	}
	
	


}
