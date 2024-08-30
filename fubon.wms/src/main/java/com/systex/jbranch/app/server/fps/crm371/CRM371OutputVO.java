package com.systex.jbranch.app.server.fps.crm371;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM371OutputVO extends PagingOutputVO {
	private List ao_list;
	private List prj_list;
	private List prj_list2; //十保專案名稱
	private List resultList;
	private String resultList2;
	
	public List getPrj_list2() {
		return prj_list2;
	}
	public void setPrj_list2(List prj_list2) {
		this.prj_list2 = prj_list2;
	}
	public List getAo_list() {
		return ao_list;
	}
	public void setAo_list(List ao_list) {
		this.ao_list = ao_list;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getPrj_list() {
		return prj_list;
	}
	public void setPrj_list(List prj_list) {
		this.prj_list = prj_list;
	}
	public String getResultList2() {
		return resultList2;
	}
	public void setResultList2(String resultList2) {
		this.resultList2 = resultList2;
	}
}