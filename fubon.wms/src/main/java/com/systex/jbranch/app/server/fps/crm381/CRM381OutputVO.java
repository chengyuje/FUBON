package com.systex.jbranch.app.server.fps.crm381;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM381OutputVO extends PagingOutputVO {
	private List ao_list;
	private List resultList1;
	private List resultList2;
	private List resultList3;
	
	
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
	public List getResultList3() {
		return resultList3;
	}
	public void setResultList3(List resultList3) {
		this.resultList3 = resultList3;
	}
	public List getAo_list() {
		return ao_list;
	}
	public void setAo_list(List ao_list) {
		this.ao_list = ao_list;
	}
}
