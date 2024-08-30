package com.systex.jbranch.app.server.fps.pms356;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS356OutputVO extends PagingOutputVO {
	private List resultList; // 分頁查詢
	private List list; 		// csv查詢
	private List csvList; 	// csv暫存檔

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
