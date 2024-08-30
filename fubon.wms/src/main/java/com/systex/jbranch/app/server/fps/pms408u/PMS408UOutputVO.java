package com.systex.jbranch.app.server.fps.pms408u;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS408UOutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;
	private String isHeadMgr;

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

	public String getIsHeadMgr() {
		return isHeadMgr;
	}

	public void setIsHeadMgr(String isHeadMgr) {
		this.isHeadMgr = isHeadMgr;
	}

		
	
}
