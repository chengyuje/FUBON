package com.systex.jbranch.app.server.fps.pms107;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS107OutputVO extends PagingOutputVO{
	private List resultList;
	private List resultList2;
	private List ymList;
	private List faiaList;
	
	private List list;
	
	
	public List getYmList() {
		return ymList;
	}
	public void setYmList(List ymList) {
		this.ymList = ymList;
	}
	public List getFaiaList() {
		return faiaList;
	}
	public void setFaiaList(List faiaList) {
		this.faiaList = faiaList;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getResultList() {
		return resultList;
	}
	public List getResultList2() {
		return resultList2;
	}
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
