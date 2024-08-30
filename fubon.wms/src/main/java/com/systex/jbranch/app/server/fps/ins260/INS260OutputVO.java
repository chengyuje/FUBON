package com.systex.jbranch.app.server.fps.ins260;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS260OutputVO extends PagingOutputVO {
	private List resultList;
	

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}