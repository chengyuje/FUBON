package com.systex.jbranch.app.server.fps.ins120;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS120OutputVO extends PagingInputVO {
	private List resultList; //結果清單

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}	
	
}
