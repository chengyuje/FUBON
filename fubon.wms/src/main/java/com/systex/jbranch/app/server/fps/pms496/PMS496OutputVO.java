package com.systex.jbranch.app.server.fps.pms496;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS496OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> resultList; 	//分頁用
	private List<Map<String, Object>> totalList;	//csv用
	private String isMaintenancePRI;

	public String getIsMaintenancePRI() {
		return isMaintenancePRI;
	}
	
	public void setIsMaintenancePRI(String isMaintenancePRI) {
		this.isMaintenancePRI = isMaintenancePRI;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}

	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}
	
}
