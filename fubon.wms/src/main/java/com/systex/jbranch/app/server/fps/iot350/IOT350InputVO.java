package com.systex.jbranch.app.server.fps.iot350;

import java.sql.Date;
import java.sql.Timestamp;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT350InputVO extends PagingInputVO {
	
	private String insId;
	private String custId;
	private String insuredId;
	private Date applyDateFrom;
	private Date applyDateTo;
	
	public String getInsId() {
		return insId;
	}
	public void setInsId(String insId) {
		this.insId = insId;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getInsuredId() {
		return insuredId;
	}
	public void setInsuredId(String insuredId) {
		this.insuredId = insuredId;
	}
	public Date getApplyDateFrom() {
		return applyDateFrom;
	}
	public Date getApplyDateTo() {
		return applyDateTo;
	}
	public void setApplyDateFrom(Date applyDateFrom) {
		this.applyDateFrom = applyDateFrom;
	}
	public void setApplyDateTo(Date applyDateTo) {
		this.applyDateTo = applyDateTo;
	}

}
