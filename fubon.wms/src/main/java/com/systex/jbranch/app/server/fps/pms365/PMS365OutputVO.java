package com.systex.jbranch.app.server.fps.pms365;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS365OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> csvList;
	private List<Map<String, Object>> totalList;
	
	private List<Map<String, Object>> dtlList;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getCsvList() {
		return csvList;
	}

	public void setCsvList(List<Map<String, Object>> csvList) {
		this.csvList = csvList;
	}

	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}

	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}

	public List<Map<String, Object>> getDtlList() {
		return dtlList;
	}

	public void setDtlList(List<Map<String, Object>> dtlList) {
		this.dtlList = dtlList;
	}

}
