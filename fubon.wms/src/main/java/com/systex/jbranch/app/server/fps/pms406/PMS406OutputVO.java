package com.systex.jbranch.app.server.fps.pms406;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS406OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> totalList;
	private String isHeadMgr;
	private String isMaintenancePRI;

	public String getIsMaintenancePRI() {
		return isMaintenancePRI;
	}

	public void setIsMaintenancePRI(String isMaintenancePRI) {
		this.isMaintenancePRI = isMaintenancePRI;
	}

	public String getIsHeadMgr() {
		return isHeadMgr;
	}

	public void setIsHeadMgr(String isHeadMgr) {
		this.isHeadMgr = isHeadMgr;
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
