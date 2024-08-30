package com.systex.jbranch.app.server.fps.iot420;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT420OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> resultList;
	private Boolean showAssign;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public Boolean getShowAssign() {
		return showAssign;
	}

	public void setShowAssign(Boolean showAssign) {
		this.showAssign = showAssign;
	}
	
}
