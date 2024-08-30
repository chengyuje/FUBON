package com.systex.jbranch.common.cmrpt001;

import java.util.List;

public class CMRPT001InputVO {
	
	private String reportId; //報表ID
	private String deviceId; //設備ID
	private String isFinished; //報表完成否 Y/N
	private List<String> reportData; //報表資料
	private int startPage; //查詢起始頁
	private int endPage; //查詢結束頁
	
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getIsFinished() {
		return isFinished;
	}
	public void setIsFinished(String isFinished) {
		this.isFinished = isFinished;
	}
	public List<String> getReportData() {
		return reportData;
	}
	public void setReportData(List<String> reportData) {
		this.reportData = reportData;
	}
	public int getStartPage() {
		return startPage;
	}
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}
	public int getEndPage() {
		return endPage;
	}
	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}
	
	
}
