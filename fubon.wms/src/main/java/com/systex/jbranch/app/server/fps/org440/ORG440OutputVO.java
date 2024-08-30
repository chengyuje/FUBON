package com.systex.jbranch.app.server.fps.org440;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG440OutputVO extends PagingOutputVO {
	
	private List aoCntLst;
	private List reportLst;
	
	private String year;
	private String toDay;

	public List getReportLst() {
		return reportLst;
	}

	public void setReportLst(List reportLst) {
		this.reportLst = reportLst;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getToDay() {
		return toDay;
	}

	public void setToDay(String toDay) {
		this.toDay = toDay;
	}

	public List getAoCntLst() {
		return aoCntLst;
	}

	public void setAoCntLst(List aoCntLst) {
		this.aoCntLst = aoCntLst;
	}
}
