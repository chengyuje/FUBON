package com.systex.jbranch.app.server.fps.pqc100;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PQC100InputVO extends PagingInputVO {

	private String prdType;
	private String prdID;
	private String reportType;
	
	private String FILE_NAME        = "";
	private String ACTUAL_FILE_NAME = "";

	private String delStartDate;
	private String delEndDate;
	private String delPrdType;
	private String delPrdID;
	
	public String getDelStartDate() {
		return delStartDate;
	}

	public void setDelStartDate(String delStartDate) {
		this.delStartDate = delStartDate;
	}

	public String getDelEndDate() {
		return delEndDate;
	}

	public void setDelEndDate(String delEndDate) {
		this.delEndDate = delEndDate;
	}

	public String getDelPrdType() {
		return delPrdType;
	}

	public void setDelPrdType(String delPrdType) {
		this.delPrdType = delPrdType;
	}

	public String getDelPrdID() {
		return delPrdID;
	}

	public void setDelPrdID(String delPrdID) {
		this.delPrdID = delPrdID;
	}

	public String getFILE_NAME() {
		return FILE_NAME;
	}

	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}

	public String getACTUAL_FILE_NAME() {
		return ACTUAL_FILE_NAME;
	}

	public void setACTUAL_FILE_NAME(String aCTUAL_FILE_NAME) {
		ACTUAL_FILE_NAME = aCTUAL_FILE_NAME;
	}

	public String getPrdType() {
		return prdType;
	}

	public void setPrdType(String prdType) {
		this.prdType = prdType;
	}

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

}
