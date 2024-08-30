package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.math.BigDecimal;

public class FPAFP960DataVO implements Serializable {
	private String investRisk;		//投資風險屬性
	private String assetType;	//檢核定義
	private BigDecimal riskRate;	//風險值 (%)
	
	public String getInvestRisk() {
		return investRisk;
	}
	public void setInvestRisk(String investRisk) {
		this.investRisk = investRisk;
	}
	public BigDecimal getRiskRate() {
		return riskRate;
	}
	public void setRiskRate(BigDecimal riskRate) {
		this.riskRate = riskRate;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	
}
