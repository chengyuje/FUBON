package com.systex.jbranch.app.server.fps.org200;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG200OutputVO extends PagingOutputVO {
	private List deptDetail = null;
	private List subDeptLst = null;
	private List parentLst = null;
	
	public List getDeptDetail() {
		return deptDetail;
	}
	public void setDeptDetail(List deptDetail) {
		this.deptDetail = deptDetail;
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

	
}
