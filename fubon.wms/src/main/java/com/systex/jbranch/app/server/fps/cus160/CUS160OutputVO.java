package com.systex.jbranch.app.server.fps.cus160;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CUS160OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List aoEmpList;
	
	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	
	public List getAoEmpList() {
		return aoEmpList;
	}
	
	public void setAoEmpList(List aoEmpList) {
		this.aoEmpList = aoEmpList;
	}
	
	
}
