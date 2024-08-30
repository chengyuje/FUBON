package com.systex.jbranch.app.server.fps.org111;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG111OutputVO extends PagingOutputVO {

	private List empDataLst;   //人員資料清單
	private List empRoleLst;   //角色清單
	private List empCertLst;   //證照資料清單
	private List insCertLst;
	private List resultList;   //照片
	private List rolist;
	private List roleList;
	private List paramList;
	private boolean containParam;
	
	private String queryType;
	
	private String loginPrivilegeID;
	
	public List getInsCertLst() {
		return insCertLst;
	}

	public void setInsCertLst(List insCertLst) {
		this.insCertLst = insCertLst;
	}

	public String getLoginPrivilegeID() {
		return loginPrivilegeID;
	}

	public void setLoginPrivilegeID(String loginPrivilegeID) {
		this.loginPrivilegeID = loginPrivilegeID;
	}

	public List getEmpDataLst() {
		return empDataLst;
	}
	
	public void setEmpDataLst(List empDataLst) {
		this.empDataLst = empDataLst;
	}
	
	public List getEmpRoleLst() {
		return empRoleLst;
	}
	
	public void setEmpRoleLst(List empRoleLst) {
		this.empRoleLst = empRoleLst;
	}
	
	public List getEmpCertLst() {
		return empCertLst;
	}
	
	public void setEmpCertLst(List empCertLst) {
		this.empCertLst = empCertLst;
	}
	
	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	
	public List getRolist() {
		return rolist;
	}
	
	public void setRolist(List rolist) {
		this.rolist = rolist;
	}
	
	public List getRoleList() {
		return roleList;
	}
	
	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}
	
	public List getParamList() {
		return paramList;
	}
	
	public void setParamList(List paramList) {
		this.paramList = paramList;
	}
	
	public boolean isContainParam() {
		return containParam;
	}
	
	public void setContainParam(boolean containParam) {
		this.containParam = containParam;
	}
	
	public String getQueryType() {
		return queryType;
	}
	
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

}
