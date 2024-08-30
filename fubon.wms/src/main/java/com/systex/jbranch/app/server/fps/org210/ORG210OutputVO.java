package com.systex.jbranch.app.server.fps.org210;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG210OutputVO extends PagingOutputVO {
	private List deptDetail = null;
	private List deptEmpLst = null;
	private List subDeptLst = null;
	private List parentLst = null;
	private List empWODeptLst = null;
	private List roleLst = null;
	
	public List getDeptDetail() {
		return deptDetail;
	}
	public void setDeptDetail(List deptDetail) {
		this.deptDetail = deptDetail;
	}
	public List getDeptEmpLst() {
		return deptEmpLst;
	}
	public void setDeptEmpLst(List deptEmpLst) {
		this.deptEmpLst = deptEmpLst;
	}
	public List getSubDeptLst() {
		return subDeptLst;
	}
	public void setSubDeptLst(List subDeptLst) {
		this.subDeptLst = subDeptLst;
	}
	public List getParentLst() {
		return parentLst;
	}
	public void setParentLst(List parentLst) {
		this.parentLst = parentLst;
	}
	public List getEmpWODeptLst() {
		return empWODeptLst;
	}
	public void setEmpWODeptLst(List empWODeptLst) {
		this.empWODeptLst = empWODeptLst;
	}
	public List getRoleLst() {
		return roleLst;
	}
	public void setRoleLst(List roleLst) {
		this.roleLst = roleLst;
	}

	
}
