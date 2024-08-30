package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.math.BigDecimal;

public class FPAFP910DetailVO implements Serializable {

	private String layer1;//第一層
	private String layer2;//第二層
	private String layer3;//第三層
	private String layer4;//第四層
	private String prodID;//建議標的代碼
	private String prodName;//建議標的名稱	
	private BigDecimal allocationPerc;//配置比率 (%)
	private String currency;//幣別
	private String riskFlag;//風險標籤
	private boolean isDeleted;//刪除Flag
	private String issuerID;
	private String issuer;
	private String prodType;
	private String avgVip;//投資客戶等級(1: M2 ; 2:M3)
	
	public String getProdType() {
		return prodType;
	}
	public void setProdType(String prodType) {
		this.prodType = prodType;
	}
	public String getLayer1() {
		return layer1;
	}
	public void setLayer1(String layer1) {
		this.layer1 = layer1;
	}
	public String getLayer2() {
		return layer2;
	}
	public void setLayer2(String layer2) {
		this.layer2 = layer2;
	}
	public String getLayer3() {
		return layer3;
	}
	public void setLayer3(String layer3) {
		this.layer3 = layer3;
	}
	public String getLayer4() {
		return layer4;
	}
	public void setLayer4(String layer4) {
		this.layer4 = layer4;
	}
	public String getProdID() {
		return prodID;
	}
	public void setProdID(String prodID) {
		this.prodID = prodID;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public BigDecimal getAllocationPerc() {
		return allocationPerc;
	}
	public void setAllocationPerc(BigDecimal allocationPerc) {
		this.allocationPerc = allocationPerc;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getRiskFlag() {
		return riskFlag;
	}
	public void setRiskFlag(String riskFlag) {
		this.riskFlag = riskFlag;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getIssuerID() {
		return issuerID;
	}
	public void setIssuerID(String issuerID) {
		this.issuerID = issuerID;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getAvgVip() {
		return avgVip;
	}
	public void setAvgVip(String avgVip) {
		this.avgVip = avgVip;
	}	
}
