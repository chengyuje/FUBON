package com.systex.jbranch.platform.reportserver.service.vo;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class ExchangeResponseVO {
	
	private String establishTime = null;
	private String reportName = null;
	private double speendingTime = 0.0;
	private String location = null;
	private boolean merge = false;
	private long genId = 0L;
	private String status = "";
	private String errorMsg = "";
	
	
	private DataHandler reportFile = null;

	public String getEstablishTime() {
		return establishTime;
	}

	public void setEstablishTime(String establishTime) {
		this.establishTime = establishTime;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public double getSpeendingTime() {
		return speendingTime;
	}

	public void setSpeendingTime(double speendingTime) {
		this.speendingTime = speendingTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean getMerge() {
		return merge;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	public long getGenId() {
		return genId;
	}

	public void setGenId(long genId) {
		this.genId = genId;
	}

	public DataHandler getReportData() {
		return reportFile;
	}

	@XmlMimeType("application/octet-stream")
	public void setReportData(DataHandler reportFile) {
		this.reportFile = reportFile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "establishTime="+establishTime+",reportName="+reportName+",speendingTime="+speendingTime+",location="+location+",merge="+merge+",genId="+genId+",status="+status+",reportFile="+reportFile;
	}
}
