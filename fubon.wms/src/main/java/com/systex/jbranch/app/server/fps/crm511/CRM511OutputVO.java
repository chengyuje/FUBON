package com.systex.jbranch.app.server.fps.crm511;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM511OutputVO extends PagingOutputVO {
	private String qstn_id;
	private List resultList;
	
	
	public String getQstn_id() {
		return qstn_id;
	}
	public void setQstn_id(String qstn_id) {
		this.qstn_id = qstn_id;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
