package com.systex.jbranch.app.server.fps.cus140;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class CUS140OutputVO extends PagingOutputVO {
	private List resultList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
