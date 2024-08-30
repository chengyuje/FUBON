package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class AFP910GetProdInputVO implements Serializable{
	
	public AFP910GetProdInputVO(){
		super();
	}
	
	private String prodCode;		//產品代碼
	private String prodName;		//產品名稱關鍵字
	private String prodType;		//產品種類
	private String layer1;			//第一層
	private String layer2;			//第 二層
	private String layer3;			//第三層
	private String layer4;			//第四層
	private String currency;		//幣別
	private String prodRiskFlag;	//產品風險標籤
	private String marketSugg;		//市場建議
	private String invPurpose;		//投組風格
	
	public String getInvPurpose() {
		return invPurpose;
	}
	public void setInvPurpose(String invPurpose) {
		this.invPurpose = invPurpose;
	}
	public String getProdCode() {
		return prodCode;
	}
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
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
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getProdRiskFlag() {
		return prodRiskFlag;
	}
	public void setProdRiskFlag(String prodRiskFlag) {
		this.prodRiskFlag = prodRiskFlag;
	}
	public String getMarketSugg() {
		return marketSugg;
	}
	public void setMarketSugg(String marketSugg) {
		this.marketSugg = marketSugg;
	}
}
