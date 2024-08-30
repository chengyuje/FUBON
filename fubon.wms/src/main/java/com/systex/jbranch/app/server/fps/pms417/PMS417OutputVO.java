package com.systex.jbranch.app.server.fps.pms417;
 
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS417OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList; //分頁用
	private List<Map<String, Object>> totalList; //csv用

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

}
