package com.systex.jbranch.app.server.fps.testsoap;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class TESTSOAPOutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;
	private String resultSoapData;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String getResultSoapData() {
		return resultSoapData;
	}

	public void setResultSoapData(String resultSoapData) {
		this.resultSoapData = resultSoapData;
	}
}
