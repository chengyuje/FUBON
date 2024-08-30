package com.systex.jbranch.app.server.fps.prd111;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

//基金動態鎖利單筆申購
public class PRD111InputVO extends PagingInputVO {
	private String custID;
	private String prodId;// 商品代號  (搜尋/儲存)
	private String prodName; // 基金名稱
	private String prodIdC1;// 商品代號  (搜尋/儲存)
	private String prodNameC1; // 基金名稱
	private String prodIdC2;// 商品代號  (搜尋/儲存)
	private String prodNameC2; // 基金名稱
	private String prodIdC3;// 商品代號  (搜尋/儲存)
	private String prodNameC3; // 基金名稱
	private String prodCurr; // 計價幣別
	private String prodCurrC1; // 計價幣別
	private String prodCurrC2; // 計價幣別
	private String prodCurrC3; // 計價幣別
	private String prodRiskLv; // 產品風險等級
	private String prodRiskLvC1; // 產品風險等級
	private String prodRiskLvC2; // 產品風險等級
	private String prodRiskLvC3; // 產品風險等級
	private String hnwcYN;
	private String hnwcServiceYN;
	private String isPrintSOT819;
	private String dynamicType;			//動態鎖利類別 M:母基金 C:子基金
	private String prodIdM;				//動態鎖利母基金代碼
	private String sameSerialYN;		//動態鎖利同系列基金
	private String sameSerialProdId;	//動態鎖利同系列基金
	private String dynamicProdCurrM; //動態鎖利母基金計價幣別
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getProdIdC1() {
		return prodIdC1;
	}
	public void setProdIdC1(String prodIdC1) {
		this.prodIdC1 = prodIdC1;
	}
	public String getProdNameC1() {
		return prodNameC1;
	}
	public void setProdNameC1(String prodNameC1) {
		this.prodNameC1 = prodNameC1;
	}
	public String getProdIdC2() {
		return prodIdC2;
	}
	public void setProdIdC2(String prodIdC2) {
		this.prodIdC2 = prodIdC2;
	}
	public String getProdNameC2() {
		return prodNameC2;
	}
	public void setProdNameC2(String prodNameC2) {
		this.prodNameC2 = prodNameC2;
	}
	public String getProdIdC3() {
		return prodIdC3;
	}
	public void setProdIdC3(String prodIdC3) {
		this.prodIdC3 = prodIdC3;
	}
	public String getProdNameC3() {
		return prodNameC3;
	}
	public void setProdNameC3(String prodNameC3) {
		this.prodNameC3 = prodNameC3;
	}
	public String getDynamicType() {
		return dynamicType;
	}
	public void setDynamicType(String dynamicType) {
		this.dynamicType = dynamicType;
	}
	public String getProdIdM() {
		return prodIdM;
	}
	public void setProdIdM(String prodIdM) {
		this.prodIdM = prodIdM;
	}
	public String getSameSerialYN() {
		return sameSerialYN;
	}
	public void setSameSerialYN(String sameSerialYN) {
		this.sameSerialYN = sameSerialYN;
	}
	public String getSameSerialProdId() {
		return sameSerialProdId;
	}
	public void setSameSerialProdId(String sameSerialProdId) {
		this.sameSerialProdId = sameSerialProdId;
	}
	public String getDynamicProdCurrM() {
		return dynamicProdCurrM;
	}
	public void setDynamicProdCurrM(String dynamicProdCurrM) {
		this.dynamicProdCurrM = dynamicProdCurrM;
	}
	public String getProdCurrC1() {
		return prodCurrC1;
	}
	public void setProdCurrC1(String prodCurrC1) {
		this.prodCurrC1 = prodCurrC1;
	}
	public String getProdCurrC2() {
		return prodCurrC2;
	}
	public void setProdCurrC2(String prodCurrC2) {
		this.prodCurrC2 = prodCurrC2;
	}
	public String getProdCurrC3() {
		return prodCurrC3;
	}
	public void setProdCurrC3(String prodCurrC3) {
		this.prodCurrC3 = prodCurrC3;
	}
	public String getProdRiskLvC1() {
		return prodRiskLvC1;
	}
	public void setProdRiskLvC1(String prodRiskLvC1) {
		this.prodRiskLvC1 = prodRiskLvC1;
	}
	public String getProdRiskLvC2() {
		return prodRiskLvC2;
	}
	public void setProdRiskLvC2(String prodRiskLvC2) {
		this.prodRiskLvC2 = prodRiskLvC2;
	}
	public String getProdRiskLvC3() {
		return prodRiskLvC3;
	}
	public void setProdRiskLvC3(String prodRiskLvC3) {
		this.prodRiskLvC3 = prodRiskLvC3;
	}
	public String getHnwcYN() {
		return hnwcYN;
	}
	public void setHnwcYN(String hnwcYN) {
		this.hnwcYN = hnwcYN;
	}
	public String getHnwcServiceYN() {
		return hnwcServiceYN;
	}
	public void setHnwcServiceYN(String hnwcServiceYN) {
		this.hnwcServiceYN = hnwcServiceYN;
	}
	public String getIsPrintSOT819() {
		return isPrintSOT819;
	}
	public void setIsPrintSOT819(String isPrintSOT819) {
		this.isPrintSOT819 = isPrintSOT819;
	}
	public String getProdCurr() {
		return prodCurr;
	}
	public void setProdCurr(String prodCurr) {
		this.prodCurr = prodCurr;
	}
	public String getProdRiskLv() {
		return prodRiskLv;
	}
	public void setProdRiskLv(String prodRiskLv) {
		this.prodRiskLv = prodRiskLv;
	}
	
	
}
