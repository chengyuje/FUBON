package com.systex.jbranch.app.server.fps.ins200;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS200InputVO extends PagingInputVO{
	
	private String CUST_ID;
	private Long BIRTHDAY;
	private String PARA_TYPE;
	private String CARE_WAY;
	private Date planSDate;
	private Date planEDate;
	
	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public Long getBIRTHDAY() {
		return BIRTHDAY;
	}

	public void setBIRTHDAY(Long bIRTHDAY) {
		BIRTHDAY = bIRTHDAY;
	}

	public String getPARA_TYPE() {
		return PARA_TYPE;
	}

	public void setPARA_TYPE(String pARA_TYPE) {
		PARA_TYPE = pARA_TYPE;
	}

	public String getCARE_WAY() {
		return CARE_WAY;
	}

	public void setCARE_WAY(String cARE_WAY) {
		CARE_WAY = cARE_WAY;
	}

	public Date getPlanSDate() {
		return planSDate;
	}

	public void setPlanSDate(Date planSDate) {
		this.planSDate = planSDate;
	}

	public Date getPlanEDate() {
		return planEDate;
	}

	public void setPlanEDate(Date planEDate) {
		this.planEDate = planEDate;
	}

	
}
