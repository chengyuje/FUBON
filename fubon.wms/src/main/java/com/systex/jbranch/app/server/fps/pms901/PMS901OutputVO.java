package com.systex.jbranch.app.server.fps.pms901;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS901OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;	//Q3投資年期上升2級以上

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

}
