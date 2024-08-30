package com.systex.jbranch.app.server.fps.crm151;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM151OutputVO {
	private List login;
	private List resultList;

	public List getLogin() {
		return login;
	}
	public void setLogin(List login) {
		this.login = login;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	
}
