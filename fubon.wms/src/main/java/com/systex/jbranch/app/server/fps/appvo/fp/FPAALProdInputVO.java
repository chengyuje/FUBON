package com.systex.jbranch.app.server.fps.appvo.fp;

import java.io.Serializable;

public class FPAALProdInputVO  implements Serializable{
	private Integer adjMethod;// 交易類別
	private String prdID;//商品代碼
	private String pname;// 商品名稱
	private String ptype;// 商品分類 備注——MFD:基金、DMF:動態鎖利、PFO:投資組合套餐、BND:債卷、ETF:ETF、DCN:DCN、INS:保險
	private String mktCatID;// 內部市場分類代號
	private String isBestChoice; // 精選商品 備註：Y/N
	private String isPrimarySale; // 主推商品 備註：Y/N
	private String forRetirement; // 適合老年退休 備注：Y/N
	private String forEducation; // 適合子女教育基金 備註：Y/N
	private String forBuyHouse; // 適合購屋計畫
	private String forSAVINGS; // 適合穩定儲蓄
	private String forDIVIDEND; // 適合固定配息
	private String forWoman; // 適合女性平台
	private String forElderly; //適合銀髮樂齡
	private String riskLevel;	//風險等級		:<=
	private String mktCatStatus;	//市場狀況	B:加碼,S:減碼
	private String insType;		//險種類別
	private String txnid;//呼叫的交易代號
	
	public String getTxnid() {
		return txnid;
	}	
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	public Integer getAdjMethod() {
		return adjMethod;
	}
	public void setAdjMethod(Integer adjMethod) {
		this.adjMethod = adjMethod;
	}
	public String getPrdID() {
		return prdID;
	}
	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	public String getMktCatID() {
		return mktCatID;
	}
	public void setMktCatID(String mktCatID) {
		this.mktCatID = mktCatID;
	}
	public String getIsBestChoice() {
		return isBestChoice;
	}
	public void setIsBestChoice(String isBestChoice) {
		this.isBestChoice = isBestChoice;
	}
	public String getIsPrimarySale() {
		return isPrimarySale;
	}
	public void setIsPrimarySale(String isPrimarySale) {
		this.isPrimarySale = isPrimarySale;
	}
	public String getForRetirement() {
		return forRetirement;
	}
	public void setForRetirement(String forRetirement) {
		this.forRetirement = forRetirement;
	}
	public String getForEducation() {
		return forEducation;
	}
	public void setForEducation(String forEducation) {
		this.forEducation = forEducation;
	}
	public String getForBuyHouse() {
		return forBuyHouse;
	}
	public void setForBuyHouse(String forBuyHouse) {
		this.forBuyHouse = forBuyHouse;
	}
	public String getForSAVINGS() {
		return forSAVINGS;
	}
	public void setForSAVINGS(String forSAVINGS) {
		this.forSAVINGS = forSAVINGS;
	}
	public String getForDIVIDEND() {
		return forDIVIDEND;
	}
	public void setForDIVIDEND(String forDIVIDEND) {
		this.forDIVIDEND = forDIVIDEND;
	}
	public String getForWoman() {
		return forWoman;
	}
	public void setForWoman(String forWoman) {
		this.forWoman = forWoman;
	}
	public String getForElderly() {
		return forElderly;
	}
	public void setForElderly(String forElderly) {
		this.forElderly = forElderly;
	}
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	public String getMktCatStatus() {
		return mktCatStatus;
	}
	public void setMktCatStatus(String mktCatStatus) {
		this.mktCatStatus = mktCatStatus;
	}
	public String getInsType() {
		return insType;
	}
	public void setInsType(String insType) {
		this.insType = insType;
	}
	
	
}
