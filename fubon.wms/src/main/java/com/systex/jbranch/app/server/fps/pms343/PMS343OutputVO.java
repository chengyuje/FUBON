package com.systex.jbranch.app.server.fps.pms343;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS343OutputVO extends PagingOutputVO {
	private List resultList; // 分頁用
	private List totalList; // csv用途
	private int rt;

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

	public int getRt() {
		return rt;
	}

	public void setRt(int rt) {
		this.rt = rt;
	}
}
