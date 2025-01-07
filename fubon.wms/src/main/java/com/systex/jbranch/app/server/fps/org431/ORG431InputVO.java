package com.systex.jbranch.app.server.fps.org431;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG431InputVO extends PagingInputVO {
	
	private List<Map<String, String>> EXPORT_LST = null;
	
	private String year;
	private String toDay;
	private String nowMonth;
	private String nextMonth;
	private String next2Month;
	
	private String uhrmRC;
	private String uhrmOP;

	public String getUhrmRC() {
		return uhrmRC;
	}

	public void setUhrmRC(String uhrmRC) {
		this.uhrmRC = uhrmRC;
	}

	public String getUhrmOP() {
		return uhrmOP;
	}

	public void setUhrmOP(String uhrmOP) {
		this.uhrmOP = uhrmOP;
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

	public List<Map<String, String>> getEXPORT_LST() {
		return EXPORT_LST;
	}
	
	public void setEXPORT_LST(List<Map<String, String>> eXPORT_LST) {
		EXPORT_LST = eXPORT_LST;
	}
}
