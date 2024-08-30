package com.systex.jbranch.app.server.fps.fps320;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS320InputVO extends PagingInputVO{
	private String planId;
	private String PLANNAME;      //特定目的命名
	private int PLANHEAD;         //投資天期(年)
	private BigDecimal ONETIME;   //投資金額方式(元/單筆)
	private BigDecimal PERMONTH;  //投資金額方式(元/月定額)
	private BigDecimal TARGET;    //目標金額
	private String CUST_RISK_ATR; //客戶風險屬性

	
	
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public String getPLANNAME() {
		return PLANNAME;
	}
	public void setPLANNAME(String pLANNAME) {
		PLANNAME = pLANNAME;
	}
	public int getPLANHEAD() {
		return PLANHEAD;
	}
	public void setPLANHEAD(int pLANHEAD) {
		PLANHEAD = pLANHEAD;
	}
	public BigDecimal getONETIME() {
		return ONETIME;
	}
	public void setONETIME(BigDecimal oNETIME) {
		ONETIME = oNETIME;
	}
	public BigDecimal getPERMONTH() {
		return PERMONTH;
	}
	public void setPERMONTH(BigDecimal pERMONTH) {
		PERMONTH = pERMONTH;
	}
	public BigDecimal getTARGET() {
		return TARGET;
	}
	public void setTARGET(BigDecimal tARGET) {
		TARGET = tARGET;
	}
	public String getCUST_RISK_ATR() {
		return CUST_RISK_ATR;
	}
	public void setCUST_RISK_ATR(String cUST_RISK_ATR) {
		CUST_RISK_ATR = cUST_RISK_ATR;
	}
	
	
	@Override
	public String toString() {
		return "FPS320InputVO [PLANNAME=" + PLANNAME + ", PLANHEAD=" + PLANHEAD
				+ ", ONETIME=" + ONETIME + ", PERMONTH=" + PERMONTH
				+ ", TARGET=" + TARGET + ", CUST_RISK_ATR=" + CUST_RISK_ATR + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
	
}