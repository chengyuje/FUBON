package com.systex.jbranch.app.server.fps.org150;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG150OutputVO extends PagingOutputVO {
	private List resignMemberLst    = null;
	private List regionCenterLst = null;
	private List opAreaLst       = null;
	private List branchLst       = null;
	
	public List getResignMemberLst() {
		return resignMemberLst;
	}
	public void setResignMemberLst(List resignMemberLst) {
		this.resignMemberLst = resignMemberLst;
	}
	public List getRegionCenterLst() {
		return regionCenterLst;
	}
	public void setRegionCenterLst(List regionCenterLst) {
		this.regionCenterLst = regionCenterLst;
	}
	public List getOpAreaLst() {
		return opAreaLst;
	}
	public void setOpAreaLst(List opAreaLst) {
		this.opAreaLst = opAreaLst;
	}
	public List getBranchLst() {
		return branchLst;
	}
	public void setBranchLst(List branchLst) {
		this.branchLst = branchLst;
	}
	
}
