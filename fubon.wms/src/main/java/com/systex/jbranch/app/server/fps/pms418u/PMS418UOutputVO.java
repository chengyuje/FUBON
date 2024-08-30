package com.systex.jbranch.app.server.fps.pms418u;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS418UOutputVO extends PagingOutputVO{
	private List resultList;
	private List totalList;
	private List <Map<String, Object>> reportList;
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getTotalList() {
		return totalList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
	public void setReportList(List<Map<String, Object>> reportList) {
		this.reportList = reportList;
	}
	public List<Map<String, Object>> getReportList() {
		return reportList;
	}
}
