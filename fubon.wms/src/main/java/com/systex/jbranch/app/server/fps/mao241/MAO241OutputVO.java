package com.systex.jbranch.app.server.fps.mao241;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class MAO241OutputVO extends PagingOutputVO {
	private List resultList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

}
