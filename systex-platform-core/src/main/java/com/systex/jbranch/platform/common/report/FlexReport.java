package com.systex.jbranch.platform.common.report;

public class FlexReport implements ReportIF {
	
	private String establishTime = null;
	private String reportName = null;
	private double speendingTime = 0;
	private String location = null;
	private boolean merge ;
	private Object otherParames;
	
	public boolean isMerge() {
		return merge;
	}

	public void setMerge(boolean merge){
		this.merge = merge;
	}
	
	public  String getEstablishTime() {
		// TODO Auto-generated method stub
		return establishTime;
	}

	public String getReportName() {
		// TODO Auto-generated method stub
		return reportName;
	}

	public double getSpeendingTime() {
		// TODO Auto-generated method stub
		return speendingTime;
	}

	public String getLocation() {
		// TODO Auto-generated method stub
		return location;
	}

	public void setEstablishTime(String establishTime) {
		this.establishTime = establishTime;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public void setSpeendingTime(double speendingTime) {
		this.speendingTime = speendingTime;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getReportTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the otherParames
	 */
	public Object getOtherParames() {
		return otherParames;
	}

	/**
	 * @param otherParames the otherParames to set
	 */
	public void setOtherParames(Object otherParames) {
		this.otherParames = otherParames;
	}
	

}
