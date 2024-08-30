package com.systex.jbranch.app.server.fps.cmmgr004;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR004OutputVO extends PagingOutputVO{
	
	public CMMGR004OutputVO()
	{
	}
	
	private List resultList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

}
