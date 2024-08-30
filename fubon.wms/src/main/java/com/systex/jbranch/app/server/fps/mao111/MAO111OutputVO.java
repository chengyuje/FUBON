package com.systex.jbranch.app.server.fps.mao111;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class MAO111OutputVO extends PagingOutputVO {
	private List resultList;
	private List addList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getAddList() {
		return addList;
	}
	public void setAddList(List addList) {
		this.addList = addList;
	}

}
