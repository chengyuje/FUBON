package com.systex.jbranch.app.server.fps.org111;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG111OrgRoleVO extends PagingInputVO{
 
	private String EMP_ID;            //員工編號
	private String EMP_NAME;          //員工姓名
	private String ROLE_ID;           //角色
	private String isPrimaryRole;     //是否為主要角色
	private String paramType;
	private String ptypeName;
	private String ptypeBuss;
	private String worksType;
	private String roleMaintain;
	private String queryType;
	
	public String getEMP_ID() {
		return EMP_ID;
	}
	
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	
	public String getEMP_NAME() {
		return EMP_NAME;
	}
	
	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}
	
	public String getROLE_ID() {
		return ROLE_ID;
	}
	
	public void setROLE_ID(String rOLE_ID) {
		ROLE_ID = rOLE_ID;
	}
	
	public String getIsPrimaryRole() {
		return isPrimaryRole;
	}

	public void setIsPrimaryRole(String isPrimaryRole) {
		this.isPrimaryRole = isPrimaryRole;
	}
	
	public String getParamType() {
		return paramType;
	}
	
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	
	public String getPtypeName() {
		return ptypeName;
	}
	
	public void setPtypeName(String ptypeName) {
		this.ptypeName = ptypeName;
	}
	
	public String getPtypeBuss() {
		return ptypeBuss;
	}
	
	public void setPtypeBuss(String ptypeBuss) {
		this.ptypeBuss = ptypeBuss;
	}
	
	public String getWorksType() {
		return worksType;
	}
	
	public void setWorksType(String worksType) {
		this.worksType = worksType;
	}
	
	public String getRoleMaintain() {
		return roleMaintain;
	}
	
	public void setRoleMaintain(String roleMaintain) {
		this.roleMaintain = roleMaintain;
	}
	
	public String getQueryType() {
		return queryType;
	}
	
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
}
