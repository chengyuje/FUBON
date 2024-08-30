package com.systex.jbranch.app.server.fps.cmmgr002;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CMMGR002InputVO extends PagingInputVO {
	private String priID;
	private String priName;
	private String roleID;
	private String roleName;
	private List<?> roleList;
	
	public String getPriID() {
		return priID;
	}
	public void setPriID(String priID) {
		this.priID = priID;
	}
	public String getPriName() {
		return priName;
	}
	public void setPriName(String priName) {
		this.priName = priName;
	}
	public String getRoleID() {
		return roleID;
	}
	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public List<?> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<?> roleList) {
		this.roleList = roleList;
	}
	
}
