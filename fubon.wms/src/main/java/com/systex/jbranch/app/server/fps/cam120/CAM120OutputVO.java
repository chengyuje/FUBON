package com.systex.jbranch.app.server.fps.cam120;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM120OutputVO extends PagingOutputVO {
	private List resultList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
