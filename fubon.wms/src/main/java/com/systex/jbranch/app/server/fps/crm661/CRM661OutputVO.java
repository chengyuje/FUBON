package com.systex.jbranch.app.server.fps.crm661;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM661OutputVO extends PagingOutputVO {
	private List resultList;
	private List resultList_rel;
	private List resultList_rel_add;
	private List resultList_rel_set;
	private List resultList_rel_rpy;
	private String rel_delete;
	
	private List resultList2;

	public List getResultList2() {
		return resultList2;
	}
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getResultList_rel() {
		return resultList_rel;
	}
	public void setResultList_rel(List resultList_rel) {
		this.resultList_rel = resultList_rel;
	}
	public List getResultList_rel_add() {
		return resultList_rel_add;
	}
	public void setResultList_rel_add(List resultList_rel_add) {
		this.resultList_rel_add = resultList_rel_add;
	}
	public List getResultList_rel_set() {
		return resultList_rel_set;
	}
	public void setResultList_rel_set(List resultList_rel_set) {
		this.resultList_rel_set = resultList_rel_set;
	}
	public List getResultList_rel_rpy() {
		return resultList_rel_rpy;
	}
	public void setResultList_rel_rpy(List resultList_rel_rpy) {
		this.resultList_rel_rpy = resultList_rel_rpy;
	}
	public String getRel_delete() {
		return rel_delete;
	}
	public void setRel_delete(String rel_delete) {
		this.rel_delete = rel_delete;
	}

}
