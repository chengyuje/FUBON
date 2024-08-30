package com.systex.jbranch.app.server.fps.pms422;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS422OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> reportList;
	
	private List<Map<String, Object>> dtlList;

	public List<Map<String, Object>> getDtlList() {
		return dtlList;
	}

	public void setDtlList(List<Map<String, Object>> dtlList) {
		this.dtlList = dtlList;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getReportList() {
		return reportList;
	}

	public void setReportList(List<Map<String, Object>> reportList) {
		this.reportList = reportList;
	}

}
