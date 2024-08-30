package com.systex.jbranch.app.server.fps.pms348;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS348OutputVO extends PagingOutputVO {
	private List resultList; // 查詢結果
	private List list; // 匯出暫存CSV
	private List csvList; // csv用LIST

	public List getList() {
		return list;
	}

	public List getCsvList() {
		return csvList;
	}

	public void setList(List list) {
		this.list = list;
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

}
