package com.systex.jbranch.app.server.fps.org243;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG243OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> resultList;
	
	private String emptyColumnMessage;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String getEmptyColumnMessage() {
		return emptyColumnMessage;
	}

	public void setEmptyColumnMessage(String emptyColumnMessage) {
		this.emptyColumnMessage = emptyColumnMessage;
	}

}
