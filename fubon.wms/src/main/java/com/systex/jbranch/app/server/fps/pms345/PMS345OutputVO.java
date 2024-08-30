package com.systex.jbranch.app.server.fps.pms345;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS345OutputVO extends PagingOutputVO{
	private List resultList; //分頁查詢list
	private List list;		//全部查詢結果list
	private List csvList;   //csv 暫存list
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
