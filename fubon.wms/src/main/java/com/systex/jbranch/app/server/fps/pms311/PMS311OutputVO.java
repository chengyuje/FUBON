package com.systex.jbranch.app.server.fps.pms311;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS311OutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;
	private String type;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
