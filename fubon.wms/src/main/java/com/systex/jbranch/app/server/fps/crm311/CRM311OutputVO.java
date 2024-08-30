package com.systex.jbranch.app.server.fps.crm311;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM311OutputVO extends PagingOutputVO {
	
	private List resultList1,resultList2;

	public List getResultList1() {
		return resultList1;
	}

	public void setResultList1(List resultList1) {
		this.resultList1 = resultList1;
	}

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	

}
