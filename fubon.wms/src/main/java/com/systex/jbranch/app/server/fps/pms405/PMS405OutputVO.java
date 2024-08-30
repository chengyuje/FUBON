package com.systex.jbranch.app.server.fps.pms405;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS405OutputVO extends PagingOutputVO {
	private List resultList;   //主查詢資訊
	private List list;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
}
