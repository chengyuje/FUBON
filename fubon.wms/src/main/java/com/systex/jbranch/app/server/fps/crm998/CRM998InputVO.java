package com.systex.jbranch.app.server.fps.crm998;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * 客戶等級例外處理功能接收物件
 * @author Eli
 * @date 20180202
 * 
 */
@Component
public class CRM998InputVO {
	/****查詢條件****/
    private String custId;
    private String braNbr;
    private String areaId;
    private String centerId;
	private String aoCode;
    private String apprStatus;
    private Date applDateStart;
    private Date applDateEnd;
    /**************/
    
    public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getBraNbr() {
		return braNbr;
	}

	public void setBraNbr(String braNbr) {
		this.braNbr = braNbr;
	}

	public String getAoCode() {
		return aoCode;
	}

	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}

	public String getApprStatus() {
		return apprStatus;
	}

	public void setApprStatus(String apprStatus) {
		this.apprStatus = apprStatus;
	}

	public Date getApplDateStart() {
		return applDateStart;
	}

	public void setApplDateStart(Date applDateStart) {
		this.applDateStart = applDateStart;
	}

	public Date getApplDateEnd() {
		return applDateEnd;
	}

	public void setApplDateEnd(Date applDateEnd) {
		this.applDateEnd = applDateEnd;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}
}
