package com.systex.jbranch.app.server.fps.pms313;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS313OutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;
	private String rptType;

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

	public String getRptType() {
		return rptType;
	}

	public void setRptType(String rptType) {
		this.rptType = rptType;
	}
}
