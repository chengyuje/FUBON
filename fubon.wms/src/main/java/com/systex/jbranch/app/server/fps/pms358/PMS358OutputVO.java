package com.systex.jbranch.app.server.fps.pms358;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS358OutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;
	private List aoDetail;
	private List brDetail;
	private List resultDlist;
	
	private String roleType;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getTotalList() {
		return totalList;
	}

	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}

	public List getAoDetail() {
		return aoDetail;
	}

	public void setAoDetail(List aoDetail) {
		this.aoDetail = aoDetail;
	}

	public List getBrDetail() {
		return brDetail;
	}

	public void setBrDetail(List brDetail) {
		this.brDetail = brDetail;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public List getResultDlist() {
		return resultDlist;
	}

	public void setResultDlist(List resultDlist) {
		this.resultDlist = resultDlist;
	}

}
