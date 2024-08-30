package com.systex.jbranch.app.server.fps.crm911;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM911OutputVO extends PagingOutputVO{
	private List resultList;
	private List privilegedID;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getPrivilegedID() {
		return privilegedID;
	}
	public void setPrivilegedID(List privilegedID) {
		this.privilegedID = privilegedID;
	}
	
}
