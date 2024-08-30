package com.systex.jbranch.app.server.fps.pms361u;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS361UOutputVO extends PagingOutputVO {
	private List resultList;
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}