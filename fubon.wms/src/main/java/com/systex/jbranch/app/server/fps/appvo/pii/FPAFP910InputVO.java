package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class FPAFP910InputVO implements Serializable{
	private String investorType;		//投資人類型
	private String investPurpose;		//投資目的
	private String investRiskAttr;		//投資風險屬性
	private String investVIP;			//投資客戶等級
	
	public String getInvestVIP() {
		return investVIP;
	}
	public void setInvestVIP(String investVIP) {
		this.investVIP = investVIP;
	}
	public String getInvestorType() {
		return investorType;
	}
	public void setInvestorType(String investorType) {
		this.investorType = investorType;
	}
	public String getInvestPurpose() {
		return investPurpose;
	}
	public void setInvestPurpose(String investPurpose) {
		this.investPurpose = investPurpose;
	}
	public String getInvestRiskAttr() {
		return investRiskAttr;
	}
	public void setInvestRiskAttr(String investRiskAttr) {
		this.investRiskAttr = investRiskAttr;
	}
}
