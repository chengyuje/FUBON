package com.systex.jbranch.app.server.fps.sot1630;

import com.systex.jbranch.app.server.fps.sot110.SOT110InputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

//基金動態鎖利單筆申購
public class SOT1630InputVO extends SOT110InputVO {
	 
	private String prodIdC1;// 商品代號  (搜尋/儲存)
	private String prodNameC1; // 基金名稱
	private String prodIdC2;// 商品代號  (搜尋/儲存)
	private String prodNameC2; // 基金名稱
	private String prodIdC3;// 商品代號  (搜尋/儲存)
	private String prodNameC3; // 基金名稱
	private String prodIdC4;// 商品代號  (搜尋/儲存)
	private String prodNameC4; // 基金名稱
	private String prodIdC5;// 商品代號  (搜尋/儲存)
	private String prodNameC5; // 基金名稱
	private String prodRiskLvC1; // 產品風險等級
	private String prodRiskLvC2; // 產品風險等級
	private String prodRiskLvC3; // 產品風險等級
	private String prodRiskLvC4; // 產品風險等級
	private String prodRiskLvC5; // 產品風險等級
	private BigDecimal prodMinGrdAmt;   // 最低累進金額
	private BigDecimal purchaseAmtC1; // 申購金額  
	private BigDecimal purchaseAmtC2; // 申購金額  
	private BigDecimal purchaseAmtC3; // 申購金額
	private BigDecimal purchaseAmtC4; // 申購金額  
	private BigDecimal purchaseAmtC5; // 申購金額  
	private String redeemType; // 贖回方式
	private BigDecimal rdmUnit; // 原單位數
	private BigDecimal numUnits; // 贖回單位數
	private BigDecimal presentVal; // 參考現值
	private String certificateID; //憑證編號
	private String confirm; //確認碼	1.檢核 , 空白：確認
	
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
	public BigDecimal getProdMinGrdAmt() {
		return prodMinGrdAmt;
	}
	public void setProdMinGrdAmt(BigDecimal prodMinGrdAmt) {
		this.prodMinGrdAmt = prodMinGrdAmt;
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
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	public String getProdIdC4() {
		return prodIdC4;
	}
	public void setProdIdC4(String prodIdC4) {
		this.prodIdC4 = prodIdC4;
	}
	public String getProdNameC4() {
		return prodNameC4;
	}
	public void setProdNameC4(String prodNameC4) {
		this.prodNameC4 = prodNameC4;
	}
	public String getProdIdC5() {
		return prodIdC5;
	}
	public void setProdIdC5(String prodIdC5) {
		this.prodIdC5 = prodIdC5;
	}
	public String getProdNameC5() {
		return prodNameC5;
	}
	public void setProdNameC5(String prodNameC5) {
		this.prodNameC5 = prodNameC5;
	}
	public String getProdRiskLvC4() {
		return prodRiskLvC4;
	}
	public void setProdRiskLvC4(String prodRiskLvC4) {
		this.prodRiskLvC4 = prodRiskLvC4;
	}
	public String getProdRiskLvC5() {
		return prodRiskLvC5;
	}
	public void setProdRiskLvC5(String prodRiskLvC5) {
		this.prodRiskLvC5 = prodRiskLvC5;
	}
	public BigDecimal getPurchaseAmtC4() {
		return purchaseAmtC4;
	}
	public void setPurchaseAmtC4(BigDecimal purchaseAmtC4) {
		this.purchaseAmtC4 = purchaseAmtC4;
	}
	public BigDecimal getPurchaseAmtC5() {
		return purchaseAmtC5;
	}
	public void setPurchaseAmtC5(BigDecimal purchaseAmtC5) {
		this.purchaseAmtC5 = purchaseAmtC5;
	}
	public String getRedeemType() {
		return redeemType;
	}
	public void setRedeemType(String redeemType) {
		this.redeemType = redeemType;
	}
	public BigDecimal getRdmUnit() {
		return rdmUnit;
	}
	public void setRdmUnit(BigDecimal rdmUnit) {
		this.rdmUnit = rdmUnit;
	}
	public BigDecimal getNumUnits() {
		return numUnits;
	}
	public void setNumUnits(BigDecimal numUnits) {
		this.numUnits = numUnits;
	}
	public BigDecimal getPresentVal() {
		return presentVal;
	}
	public void setPresentVal(BigDecimal presentVal) {
		this.presentVal = presentVal;
	}
	public String getCertificateID() {
		return certificateID;
	}
	public void setCertificateID(String certificateID) {
		this.certificateID = certificateID;
	}
		
}
