package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class PeriodOutputVO implements Serializable {
	private String maxPeriodStartDate;		//最大資料共同有效區間起日
	private String maxPeriodEndDate; 		//最大資料共同有效區間迄日
	
	public String getMaxPeriodStartDate() {
		return maxPeriodStartDate;
	}
	public void setMaxPeriodStartDate(String maxPeriodStartDate) {
		this.maxPeriodStartDate = maxPeriodStartDate;
	}
	public String getMaxPeriodEndDate() {
		return maxPeriodEndDate;
	}
	public void setMaxPeriodEndDate(String maxPeriodEndDate) {
		this.maxPeriodEndDate = maxPeriodEndDate;
	}	
}
