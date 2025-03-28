package com.systex.jbranch.app.server.fps.crm860;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM860InputVO extends PagingInputVO {
	private String custID;
	private String prdType;
	private Date startDate;
	private Date endDate;
	private String isOBU;
	
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getPrdType() {
		return prdType;
	}
	public void setPrdType(String prdType) {
		this.prdType = prdType;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getIsOBU() {
		return isOBU;
	}
	public void setIsOBU(String isOBU) {
		this.isOBU = isOBU;
	}
}