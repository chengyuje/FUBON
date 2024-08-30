package com.systex.jbranch.app.server.fps.ins610;

import java.util.List;
import java.util.Map;

public class INS610OutputVO {
	
	private String planRatio; //規劃成效率=查詢案件中狀態為已投保之筆數/查詢案件筆數
	private List<Map<String, Object>> resultList; // 查詢結果
	
	INS610OutputVO() {}

	public String getPlanRatio() {
		return planRatio;
	}

	public void setPlanRatio(String planRatio) {
		this.planRatio = planRatio;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	
}
