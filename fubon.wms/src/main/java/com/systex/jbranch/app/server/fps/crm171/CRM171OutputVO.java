package com.systex.jbranch.app.server.fps.crm171;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM171OutputVO extends PagingOutputVO{
	private List resultList;
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
