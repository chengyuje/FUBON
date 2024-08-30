package com.systex.jbranch.app.server.fps.pms314;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS314OutputVO extends PagingOutputVO{
	private List resultList;
	private List totalList;

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
}
