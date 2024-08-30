package com.systex.jbranch.app.server.fps.pms418;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS418OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> totalList;
	private List<Map<String, Object>> reportList;
	private List<Map<String, Object>> detailList;
	private List<Map<String, Object>> excludeList;

	public List<Map<String, Object>> getExcludeList() {
		return excludeList;
	}

	public void setExcludeList(List<Map<String, Object>> excludeList) {
		this.excludeList = excludeList;
	}

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

	public List<Map<String, Object>> getReportList() {
		return reportList;
	}

	public void setReportList(List<Map<String, Object>> reportList) {
		this.reportList = reportList;
	}

	public List<Map<String, Object>> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<Map<String, Object>> detailList) {
		this.detailList = detailList;
	}

}
