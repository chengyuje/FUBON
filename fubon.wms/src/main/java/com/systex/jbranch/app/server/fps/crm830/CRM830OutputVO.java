package com.systex.jbranch.app.server.fps.crm830;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM830OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> resultListJSB;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getResultListJSB() {
		return resultListJSB;
	}
	public void setResultListJSB(List<Map<String, Object>> resultListJSB) {
		this.resultListJSB = resultListJSB;
	}
	
}
