package com.systex.jbranch.app.server.fps.prd131;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD131OutputVO extends PagingOutputVO {
	private String bond_name;
	private List resultList;
	
	
	public String getBond_name() {
		return bond_name;
	}
	public void setBond_name(String bond_name) {
		this.bond_name = bond_name;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
