package com.systex.jbranch.app.server.fps.org430;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG430InputVO extends PagingInputVO {
	
	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
	private List<Map<String, String>> EXPORT_LST = null;
	
	private String year;
	private String toDay;
	private String nowMonth;
	private String nextMonth;
	private String next2Month;
	
	public String getRegion_center_id() {
		return region_center_id;
	}

	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}

	public String getBranch_area_id() {
		return branch_area_id;
	}

	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
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
