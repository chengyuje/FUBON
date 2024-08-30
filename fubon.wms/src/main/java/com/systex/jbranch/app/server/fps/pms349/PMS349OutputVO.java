package com.systex.jbranch.app.server.fps.pms349;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS349OutputVO extends PagingOutputVO {
	private List resultList;	//分頁LIST
	private List list;			//暫存匯出LIST
	private List csvList; 		//CSV的LIST

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
