package com.systex.jbranch.app.server.fps.crm131;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM131OutputVO extends PagingOutputVO {
	private List<Map<String,Object>> resultList;
	private List<Map<String,Object>> resultList2;
	private List<Map<String,Object>> resultList3;
	private List<Map<String,Object>> resultList4;
	private List<Map<String,Object>> resultList5;
	private List<Map<String,Object>> privilege;
	private List ao_list;
	
	
	public List getAo_list() {
		return ao_list;
	}

	public void setAo_list(List ao_list) {
		this.ao_list = ao_list;
	}

	public List<Map<String,Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String,Object>> getResultList2() {
		return resultList2;
	}

	public void setResultList2(List<Map<String,Object>> resultList2) {
		this.resultList2 = resultList2;
	}

	public List<Map<String,Object>> getPrivilege() {
		return privilege;
	}

	public void setPrivilege(List<Map<String,Object>> privilege) {
		this.privilege = privilege;
	}

	public List<Map<String,Object>> getResultList3() {
		return resultList3;
	}

	public void setResultList3(List<Map<String,Object>> resultList3) {
		this.resultList3 = resultList3;
	}

	public List<Map<String,Object>> getResultList4() {
		return resultList4;
	}

	public void setResultList4(List<Map<String,Object>> resultList4) {
		this.resultList4 = resultList4;
	}

	public List<Map<String,Object>> getResultList5() {
		return resultList5;
	}

	public void setResultList5(List<Map<String,Object>> resultList5) {
		this.resultList5 = resultList5;
	}

		
}
