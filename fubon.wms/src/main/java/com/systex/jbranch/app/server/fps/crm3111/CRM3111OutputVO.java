package com.systex.jbranch.app.server.fps.crm3111;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM3111OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> newaoCode;
	private List<Map<String, Object>> resultList;

	public List<Map<String, Object>> getNewaoCode() {
		return newaoCode;
	}

	public void setNewaoCode(List<Map<String, Object>> newaoCode) {
		this.newaoCode = newaoCode;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

}
