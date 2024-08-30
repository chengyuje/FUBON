package com.systex.jbranch.app.server.fps.sqm410;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SQM410OutputVO extends PagingOutputVO {
	
	private List<Map<String,Object>> resultList;
	private List<Map<String,Object>> qtnTypeList;
	private String jobTitleName;
	private List<Map<String,Object>> agentList;  //代理人的資料清單
	
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String getJobTitleName() {
		return jobTitleName;
	}

	public void setJobTitleName(String jobTitleName) {
		this.jobTitleName = jobTitleName;
	}

	public List<Map<String, Object>> getQtnTypeList() {
		return qtnTypeList;
	}

	public void setQtnTypeList(List<Map<String, Object>> qtnTypeList) {
		this.qtnTypeList = qtnTypeList;
	}

	public List<Map<String, Object>> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<Map<String, Object>> agentList) {
		this.agentList = agentList;
	}
		
}
