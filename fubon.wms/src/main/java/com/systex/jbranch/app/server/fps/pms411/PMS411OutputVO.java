package com.systex.jbranch.app.server.fps.pms411;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS411OutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;

	private List csvList;   //csv
	
	
	
	public List getCsvList() {
		return csvList;
	}

	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}

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
