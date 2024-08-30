package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class AFP910ProductVO implements Serializable{
	public AFP910ProductVO(){
		super();
	}
	
	private String prodID; 		//產品代碼
	private String prodName; 	//產品名稱
	private String prodType;	//產品種類
	private String prodCat; 	//資產類別
	private String riskFlag; 	//風險標籤
	private String currency;	//幣別
	private String layer1; 		//第一層
	private String layer2; 		//第二層
	private String layer3; 		//第三層
	private String layer4; 		//第四層
	private String marketSugg1;	//市場建議
	private String marketSugg3;	//市場建議
	private String marketSugg6;	//市場建議
	private String isMain; 		//主力產品
	private String issuerID;
	private String issuer;
	private String layer4Name; 		//第四層中文
	
	public String getLayer4Name() {
		return layer4Name;
	}
	public void setLayer4Name(String layer4Name) {
		this.layer4Name = layer4Name;
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
	public String getProdType() {
		return prodType;
	}
	public void setProdType(String prodType) {
		this.prodType = prodType;
	}
	public String getProdCat() {
		return prodCat;
	}
	public void setProdCat(String prodCat) {
		this.prodCat = prodCat;
	}
	public String getRiskFlag() {
		return riskFlag;
	}
	public void setRiskFlag(String riskFlag) {
		this.riskFlag = riskFlag;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
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
	public String getIsMain() {
		return isMain;
	}
	public void setIsMain(String isMain) {
		this.isMain = isMain;
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
	public String getMarketSugg1() {
		return marketSugg1;
	}
	public void setMarketSugg1(String marketSugg1) {
		this.marketSugg1 = marketSugg1;
	}
	public String getMarketSugg3() {
		return marketSugg3;
	}
	public void setMarketSugg3(String marketSugg3) {
		this.marketSugg3 = marketSugg3;
	}
	public String getMarketSugg6() {
		return marketSugg6;
	}
	public void setMarketSugg6(String marketSugg6) {
		this.marketSugg6 = marketSugg6;
	}
	
}
