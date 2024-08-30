package com.systex.jbranch.app.server.fps.ins810;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS_CUST_MASTIutputVO extends PagingInputVO{

	private String CUST_ID;
	private String CUST_NAME;
	private String GENDER;
	private Date BIRTH_DATE;
	private String MARRIAGE_STAT;
	private String FB_CUST;
	
	private String birthDay;
	public String getCUST_ID() {
		return CUST_ID;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public String getGENDER() {
		return GENDER;
	}
	public Date getBIRTH_DATE() {
		return BIRTH_DATE;
	}
	public String getMARRIAGE_STAT() {
		return MARRIAGE_STAT;
	}
	public String getFB_CUST() {
		return FB_CUST;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public void setGENDER(String gENDER) {
		GENDER = gENDER;
	}
	public void setBIRTH_DATE(Date bIRTH_DATE) {
		BIRTH_DATE = bIRTH_DATE;
	}
	public void setMARRIAGE_STAT(String mARRIAGE_STAT) {
		MARRIAGE_STAT = mARRIAGE_STAT;
	}
	public void setFB_CUST(String fB_CUST) {
		FB_CUST = fB_CUST;
	}
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	
	
}
