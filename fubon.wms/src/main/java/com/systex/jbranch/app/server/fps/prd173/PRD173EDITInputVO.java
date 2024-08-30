package com.systex.jbranch.app.server.fps.prd173;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD173EDITInputVO extends PagingInputVO{
	
	private String FXD_KEYNO;
	private String PROD_NAME;
	private String PROD_PERIOD;
	private Date EFFECT_DATE;
	private Date EXPIRY_DATE;
	
	
	public String getFXD_KEYNO() {
		return FXD_KEYNO;
	}
	public void setFXD_KEYNO(String fXD_KEYNO) {
		FXD_KEYNO = fXD_KEYNO;
	}
	public String getPROD_NAME() {
		return PROD_NAME;
	}
	public void setPROD_NAME(String pROD_NAME) {
		PROD_NAME = pROD_NAME;
	}
	public String getPROD_PERIOD() {
		return PROD_PERIOD;
	}
	public void setPROD_PERIOD(String pROD_PERIOD) {
		PROD_PERIOD = pROD_PERIOD;
	}
	public Date getEFFECT_DATE() {
		return EFFECT_DATE;
	}
	public void setEFFECT_DATE(Date eFFECT_DATE) {
		EFFECT_DATE = eFFECT_DATE;
	}
	public Date getEXPIRY_DATE() {
		return EXPIRY_DATE;
	}
	public void setEXPIRY_DATE(Date eXPIRY_DATE) {
		EXPIRY_DATE = eXPIRY_DATE;
	}
	
	

}
