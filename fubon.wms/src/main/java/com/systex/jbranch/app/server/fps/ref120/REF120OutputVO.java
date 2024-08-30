package com.systex.jbranch.app.server.fps.ref120;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class REF120OutputVO extends PagingOutputVO {
	
	private List resultList;
	private String loginSysRole;

	public String getLoginSysRole() {
		return loginSysRole;
	}

	public void setLoginSysRole(String loginSysRole) {
		this.loginSysRole = loginSysRole;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
