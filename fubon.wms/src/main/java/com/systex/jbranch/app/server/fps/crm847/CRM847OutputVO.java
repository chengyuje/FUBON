package com.systex.jbranch.app.server.fps.crm847;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM847OutputVO extends PagingOutputVO {
	private List resultList;	//存單質借
	private List resultList1;	//信託質借
	
	
	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getResultList1() {
		return resultList1;
	}

	public void setResultList1(List resultList1) {
		this.resultList1 = resultList1;
	}

	
}
