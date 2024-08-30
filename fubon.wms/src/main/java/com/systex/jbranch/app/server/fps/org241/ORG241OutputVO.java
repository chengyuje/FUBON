package com.systex.jbranch.app.server.fps.org241;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG241OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> resultList;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

}
