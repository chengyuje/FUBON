package com.systex.jbranch.app.server.fps.fps325;

public class FPS325PrdInputVO {
	private String PRD_ID;
	private String PTYPE;
	private String PRD_CNAME;
	private String CURRENCY_TYPE;
	private Double INV_PERCENT;
	private Double LowMoney;
	private String TARGETS;

	public String getPRD_ID() {
		return PRD_ID;
	}
	public void setPRD_ID(String pRD_ID) {
		PRD_ID = pRD_ID;
	}	
	public String getPTYPE() {
		return PTYPE;
	}
	public void setPTYPE(String pTYPE) {
		PTYPE = pTYPE;
	}
	public String getPRD_CNAME() {
		return PRD_CNAME;
	}
	public void setPRD_CNAME(String pRD_CNAME) {
		PRD_CNAME = pRD_CNAME;
	}
	public String getCURRENCY_TYPE() {
		return CURRENCY_TYPE;
	}
	public void setCURRENCY_TYPE(String cURRENCY_TYPE) {
		CURRENCY_TYPE = cURRENCY_TYPE;
	}
	public Double getINV_PERCENT() {
		return INV_PERCENT;
	}
	public void setINV_PERCENT(Double iNV_PERCENT) {
		INV_PERCENT = iNV_PERCENT;
	}
	public Double getLowMoney() {
		return LowMoney;
	}
	public void setLowMoney(Double lowMoney) {
		LowMoney = lowMoney;
	}
	public String getTARGETS() {
		return TARGETS;
	}
	public void setTARGETS(String tARGETS) {
		TARGETS = tARGETS;
	}

}
