package com.systex.jbranch.app.server.fps.org430;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG430OutputVO extends PagingOutputVO {
	
	private List aoCntLst;
	private List reportLst;
	
	private String year;
	private String toDay;
	private String nowMonth;
	private String nextMonth;
	private String next2Month;

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

	public String getNowMonth() {
		return nowMonth;
	}

	public void setNowMonth(String nowMonth) {
		this.nowMonth = nowMonth;
	}

	public String getNextMonth() {
		return nextMonth;
	}

	public void setNextMonth(String nextMonth) {
		this.nextMonth = nextMonth;
	}

	public String getNext2Month() {
		return next2Month;
	}

	public void setNext2Month(String next2Month) {
		this.next2Month = next2Month;
	}

	public List getAoCntLst() {
		return aoCntLst;
	}

	public void setAoCntLst(List aoCntLst) {
		this.aoCntLst = aoCntLst;
	}
}
