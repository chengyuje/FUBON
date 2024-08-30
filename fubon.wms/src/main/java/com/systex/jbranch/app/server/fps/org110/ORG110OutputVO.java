package com.systex.jbranch.app.server.fps.org110;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG110OutputVO extends PagingOutputVO {
	
	private List orgMemberLst;
	private List regionCenterLst;
	private List opAreaLst;
	private List branchLst;
	private List titleLst;
	private List reviewList;
	
	public List getReviewList() {
		return reviewList;
	}
	
	public void setReviewList(List reviewList) {
		this.reviewList = reviewList;
	}
	
	public List getOrgMemberLst() {
		return orgMemberLst;
	}
	
	public void setOrgMemberLst(List orgMemberLst) {
		this.orgMemberLst = orgMemberLst;
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
	
	public List getTitleLst() {
		return titleLst;
	}
	
	public void setTitleLst(List titleLst) {
		this.titleLst = titleLst;
	}
	
}
