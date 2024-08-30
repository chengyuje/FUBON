package com.systex.jbranch.app.server.fps.crmebank;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRMEBANKOutputVO extends PagingOutputVO{
	private List welcomeList;
	
	public List getWelcomeList() {
		return welcomeList;
	}
	public void setWelcomeList(List welcomeList) {
		this.welcomeList = welcomeList;
	}
}
