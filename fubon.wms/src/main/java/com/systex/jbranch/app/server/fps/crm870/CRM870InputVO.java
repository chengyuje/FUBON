package com.systex.jbranch.app.server.fps.crm870;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM870InputVO extends PagingInputVO {

	private String custID;
	private Integer tabSheet;
	private Date startDate;
	private Date endDate;
	
	private String prdID;
	private String rptType;

	public String getRptType() {
		return rptType;
	}

	public void setRptType(String rptType) {
		this.rptType = rptType;
	}

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
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

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public Integer getTabSheet() {
		return tabSheet;
	}

	public void setTabSheet(Integer tabSheet) {
		this.tabSheet = tabSheet;
	}

}
