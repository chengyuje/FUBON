package com.systex.jbranch.app.server.fps.crm1241;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM1241OutputVO extends PagingOutputVO{
	private List resultList;
	private List resultList_product;
	private List resultList_record;
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getResultList_product() {
		return resultList_product;
	}
	public void setResultList_product(List resultList_product) {
		this.resultList_product = resultList_product;
	}
	public List getResultList_record() {
		return resultList_record;
	}
	public void setResultList_record(List resultList_record) {
		this.resultList_record = resultList_record;
	}
	
}
