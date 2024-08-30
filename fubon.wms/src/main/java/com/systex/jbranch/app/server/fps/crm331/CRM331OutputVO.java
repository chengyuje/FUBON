package com.systex.jbranch.app.server.fps.crm331;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM331OutputVO extends PagingOutputVO{
	private List resultList;
	private String resultList2;
	private String role_name;
	

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public String getResultList2() {
		return resultList2;
	}
	public void setResultList2(String resultList2) {
		this.resultList2 = resultList2;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
}