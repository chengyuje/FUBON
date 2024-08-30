package com.systex.jbranch.app.server.fps.pms347;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS347OutputVO extends PagingOutputVO {
	private List resultList; // 結果list
	private List list; // 暫存csv list
	private List csvList; // csv查詢list

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
