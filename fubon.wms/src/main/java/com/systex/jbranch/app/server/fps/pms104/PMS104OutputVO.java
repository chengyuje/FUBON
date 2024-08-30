package com.systex.jbranch.app.server.fps.pms104;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS104OutputVO extends PagingOutputVO{
	private List resultList; //查詢畫面list
	private List list;
	private List csvList;
	
	private List resultList2; //查詢畫面list(總計)

	
	
	public List getResultList2() {
		return resultList2;
	}
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getResultList() {
		return resultList;
	}
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
