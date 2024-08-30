package com.systex.jbranch.app.server.fps.crm3121;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM3121OutputVO extends PagingOutputVO {
	private List ao_list;
	private List resultList;
	private List resultList2;
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getResultList2() {
		return resultList2;
	}
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	public List getAo_list() {
		return ao_list;
	}
	public void setAo_list(List ao_list) {
		this.ao_list = ao_list;
	}
}
