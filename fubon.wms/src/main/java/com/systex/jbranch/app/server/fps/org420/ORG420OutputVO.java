package com.systex.jbranch.app.server.fps.org420;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG420OutputVO extends PagingOutputVO {
	
	private List empLeftJobLst;
	private List reportLst;
	private String toDay;

	public List getReportLst() {
		return reportLst;
	}

	public void setReportLst(List reportLst) {
		this.reportLst = reportLst;
	}

	public String getToDay() {
		return toDay;
	}

	public void setToDay(String toDay) {
		this.toDay = toDay;
	}

	public List getEmpLeftJobLst() {
		return empLeftJobLst;
	}

	public void setEmpLeftJobLst(List empLeftJobLst) {
		this.empLeftJobLst = empLeftJobLst;
	}
}
