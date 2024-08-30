package com.systex.jbranch.app.server.fps.ref111;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class REF111OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;
	private Integer deleteSize = 0;
	private Integer insertSize = 0;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public Integer getDeleteSize() {
		return deleteSize;
	}

	public void setDeleteSize(Integer deleteSize) {
		this.deleteSize = deleteSize;
	}

	public Integer getInsertSize() {
		return insertSize;
	}

	public void setInsertSize(Integer insertSize) {
		this.insertSize = insertSize;
	}
	
}
