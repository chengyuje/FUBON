package com.systex.jbranch.app.server.fps.cus110;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CUS110OutputVO extends PagingOutputVO {
	
	private List resultList;
	
	private List custData;
	
	private String message;

	public List getCustData() {
		return custData;
	}

	public void setCustData(List custData) {
		this.custData = custData;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}