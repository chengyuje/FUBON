package com.systex.jbranch.app.server.fps.pms433;

import java.util.List;

public class PMS433OutputVO {
	private List resultList; //主查詢
	private List detailList; //明細
	
	private List dateList;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getDateList() {
		return dateList;
	}

	public void setDateList(List dateList) {
		this.dateList = dateList;
	}

	public List getDetailList() {
		return detailList;
	}

	public void setDetailList(List detailList) {
		this.detailList = detailList;
	}
	
	
	

}
