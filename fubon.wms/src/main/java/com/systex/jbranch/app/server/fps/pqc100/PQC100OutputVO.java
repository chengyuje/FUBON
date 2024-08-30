package com.systex.jbranch.app.server.fps.pqc100;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PQC100OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> activePrdList;
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> totalList;

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

	public List<Map<String, Object>> getActivePrdList() {
		return activePrdList;
	}

	public void setActivePrdList(List<Map<String, Object>> activePrdList) {
		this.activePrdList = activePrdList;
	}

}
