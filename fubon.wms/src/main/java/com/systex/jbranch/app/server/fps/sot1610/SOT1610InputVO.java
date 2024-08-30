package com.systex.jbranch.app.server.fps.sot1610;

import com.systex.jbranch.app.server.fps.sot110.SOT110InputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

//基金動態鎖利單筆申購
public class SOT1610InputVO extends SOT110InputVO {
	 
	private String prodIdC1;// 商品代號  (搜尋/儲存)
	private String prodNameC1; // 基金名稱
	private String prodIdC2;// 商品代號  (搜尋/儲存)
	private String prodNameC2; // 基金名稱
	private String prodIdC3;// 商品代號  (搜尋/儲存)
	private String prodNameC3; // 基金名稱
	private String prodCurrC1; // 計價幣別
	private String prodCurrC2; // 計價幣別
	private String prodCurrC3; // 計價幣別
	private String prodRiskLvC1; // 產品風險等級
	private String prodRiskLvC2; // 產品風險等級
	private String prodRiskLvC3; // 產品風險等級
	private BigDecimal prodMinBuyAmtC1; // 最低申購金額
	private BigDecimal prodMinBuyAmtC2; // 最低申購金額
	private BigDecimal prodMinBuyAmtC3; // 最低申購金額
	private BigDecimal prodMinGrdAmt;   // 最低累進金額
	private BigDecimal prodMinGrdAmtC1; // 最低累進金額
	private BigDecimal prodMinGrdAmtC2; // 最低累進金額
	private BigDecimal prodMinGrdAmtC3; // 最低累進金額
	private BigDecimal purchaseAmtC1; // 申購金額  
	private BigDecimal purchaseAmtC2; // 申購金額  
	private BigDecimal purchaseAmtC3; // 申購金額  
	private String transDate1; // 轉換日期1 Y/N 
	private String transDate2; // 轉換日期6 Y/N 
	private String transDate3; // 轉換日期11 Y/N 
	private String transDate4; // 轉換日期16 Y/N 
	private String transDate5; // 轉換日期21 Y/N 
	private String transDate6; // 轉換日期26 Y/N 
	private BigDecimal engagedROI; // 約定報酬率
	private String confirm; //確認碼	1.檢核 , 空白：確認
	private String dynamicType;			//動態鎖利類別 M:母基金 C:子基金
	private String prodIdM;				//動態鎖利母基金代碼
	private String sameSerialYN;		//動態鎖利同系列基金
	private String sameSerialProdId;	//動態鎖利同系列基金
	private String dynamicProdCurrM; //動態鎖利母基金計價幣別
	
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
	public BigDecimal getProdMinBuyAmtC1() {
		return prodMinBuyAmtC1;
	}
	public void setProdMinBuyAmtC1(BigDecimal prodMinBuyAmtC1) {
		this.prodMinBuyAmtC1 = prodMinBuyAmtC1;
	}
	public BigDecimal getProdMinBuyAmtC2() {
		return prodMinBuyAmtC2;
	}
	public void setProdMinBuyAmtC2(BigDecimal prodMinBuyAmtC2) {
		this.prodMinBuyAmtC2 = prodMinBuyAmtC2;
	}
	public BigDecimal getProdMinBuyAmtC3() {
		return prodMinBuyAmtC3;
	}
	public void setProdMinBuyAmtC3(BigDecimal prodMinBuyAmtC3) {
		this.prodMinBuyAmtC3 = prodMinBuyAmtC3;
	}
	public BigDecimal getProdMinGrdAmt() {
		return prodMinGrdAmt;
	}
	public void setProdMinGrdAmt(BigDecimal prodMinGrdAmt) {
		this.prodMinGrdAmt = prodMinGrdAmt;
	}
	public BigDecimal getProdMinGrdAmtC1() {
		return prodMinGrdAmtC1;
	}
	public void setProdMinGrdAmtC1(BigDecimal prodMinGrdAmtC1) {
		this.prodMinGrdAmtC1 = prodMinGrdAmtC1;
	}
	public BigDecimal getProdMinGrdAmtC2() {
		return prodMinGrdAmtC2;
	}
	public void setProdMinGrdAmtC2(BigDecimal prodMinGrdAmtC2) {
		this.prodMinGrdAmtC2 = prodMinGrdAmtC2;
	}
	public BigDecimal getProdMinGrdAmtC3() {
		return prodMinGrdAmtC3;
	}
	public void setProdMinGrdAmtC3(BigDecimal prodMinGrdAmtC3) {
		this.prodMinGrdAmtC3 = prodMinGrdAmtC3;
	}
	public BigDecimal getPurchaseAmtC1() {
		return purchaseAmtC1;
	}
	public void setPurchaseAmtC1(BigDecimal purchaseAmtC1) {
		this.purchaseAmtC1 = purchaseAmtC1;
	}
	public BigDecimal getPurchaseAmtC2() {
		return purchaseAmtC2;
	}
	public void setPurchaseAmtC2(BigDecimal purchaseAmtC2) {
		this.purchaseAmtC2 = purchaseAmtC2;
	}
	public BigDecimal getPurchaseAmtC3() {
		return purchaseAmtC3;
	}
	public void setPurchaseAmtC3(BigDecimal purchaseAmtC3) {
		this.purchaseAmtC3 = purchaseAmtC3;
	}
	public String getTransDate1() {
		return transDate1;
	}
	public void setTransDate1(String transDate1) {
		this.transDate1 = transDate1;
	}
	public String getTransDate2() {
		return transDate2;
	}
	public void setTransDate2(String transDate2) {
		this.transDate2 = transDate2;
	}
	public String getTransDate3() {
		return transDate3;
	}
	public void setTransDate3(String transDate3) {
		this.transDate3 = transDate3;
	}
	public String getTransDate4() {
		return transDate4;
	}
	public void setTransDate4(String transDate4) {
		this.transDate4 = transDate4;
	}
	public String getTransDate5() {
		return transDate5;
	}
	public void setTransDate5(String transDate5) {
		this.transDate5 = transDate5;
	}
	public String getTransDate6() {
		return transDate6;
	}
	public void setTransDate6(String transDate6) {
		this.transDate6 = transDate6;
	}
	public BigDecimal getEngagedROI() {
		return engagedROI;
	}
	public void setEngagedROI(BigDecimal engagedROI) {
		this.engagedROI = engagedROI;
	}
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
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
		
}
