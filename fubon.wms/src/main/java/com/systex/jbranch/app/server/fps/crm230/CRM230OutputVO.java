package com.systex.jbranch.app.server.fps.crm230;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.FubonPagingOutputVO;

public class CRM230OutputVO extends FubonPagingOutputVO {
	private List resultList;
	private List totalList;
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getTotalList() {
		return totalList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
}
