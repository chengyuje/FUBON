package com.systex.jbranch.app.server.fps.crm160;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM160OutputVO extends PagingOutputVO {
	private List privilege;
	private List resultList;
	
	public List getPrivilege() {
		return privilege;
	}

	public void setPrivilege(List privilege) {
		this.privilege = privilege;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

}
