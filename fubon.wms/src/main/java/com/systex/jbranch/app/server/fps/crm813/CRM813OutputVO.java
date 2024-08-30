package com.systex.jbranch.app.server.fps.crm813;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM813OutputVO extends PagingOutputVO {
	private List resultList;
	private List resultList2;
	private HashMap<String,BigDecimal> ex_map;
	
	

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

	public HashMap<String, BigDecimal> getEx_map() {
		return ex_map;
	}

	public void setEx_map(HashMap<String, BigDecimal> ex_map) {
		this.ex_map = ex_map;
	}



}
