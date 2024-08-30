package com.systex.jbranch.app.server.fps.sot1640;

import com.systex.jbranch.app.server.fps.sot110.SOT110InputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

//基金動態鎖利單筆申購
public class SOT1640InputVO extends SOT110InputVO {
	 
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
	private String transferType; // 轉換方式
	private BigDecimal numUnits; // 單位數
	private BigDecimal numUnitsC1; // 單位數
	private BigDecimal numUnitsC2; // 單位數
	private BigDecimal numUnitsC3; // 單位數
	private BigDecimal numUnitsC4; // 單位數
	private BigDecimal numUnitsC5; // 單位數
	private BigDecimal presentVal; // 參考現值
	private String certificateID; //憑證編號
	private String confirm; //確認碼	1.檢核 , 空白：確認
	private String inProdId; //可轉換母基金
	private String inProdName;
	private String inProdRiskLv;
	private String inProdCurr;
	private String inProdC1YN;
	private String inProdC2YN;
	private String inProdC3YN;
	private String inProdC4YN;
	private String inProdC5YN;
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
	public BigDecimal getNumUnits() {
		return numUnits;
	}
	public void setNumUnits(BigDecimal numUnits) {
		this.numUnits = numUnits;
	}
	public BigDecimal getNumUnitsC1() {
		return numUnitsC1;
	}
	public void setNumUnitsC1(BigDecimal numUnitsC1) {
		this.numUnitsC1 = numUnitsC1;
	}
	public BigDecimal getNumUnitsC2() {
		return numUnitsC2;
	}
	public void setNumUnitsC2(BigDecimal numUnitsC2) {
		this.numUnitsC2 = numUnitsC2;
	}
	public BigDecimal getNumUnitsC3() {
		return numUnitsC3;
	}
	public void setNumUnitsC3(BigDecimal numUnitsC3) {
		this.numUnitsC3 = numUnitsC3;
	}
	public BigDecimal getNumUnitsC4() {
		return numUnitsC4;
	}
	public void setNumUnitsC4(BigDecimal numUnitsC4) {
		this.numUnitsC4 = numUnitsC4;
	}
	public BigDecimal getNumUnitsC5() {
		return numUnitsC5;
	}
	public void setNumUnitsC5(BigDecimal numUnitsC5) {
		this.numUnitsC5 = numUnitsC5;
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
	public String getInProdId() {
		return inProdId;
	}
	public void setInProdId(String inProdId) {
		this.inProdId = inProdId;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getInProdName() {
		return inProdName;
	}
	public void setInProdName(String inProdName) {
		this.inProdName = inProdName;
	}
	public String getInProdRiskLv() {
		return inProdRiskLv;
	}
	public void setInProdRiskLv(String inProdRiskLv) {
		this.inProdRiskLv = inProdRiskLv;
	}
	public String getInProdCurr() {
		return inProdCurr;
	}
	public void setInProdCurr(String inProdCurr) {
		this.inProdCurr = inProdCurr;
	}
	public String getInProdC1YN() {
		return inProdC1YN;
	}
	public void setInProdC1YN(String inProdC1YN) {
		this.inProdC1YN = inProdC1YN;
	}
	public String getInProdC2YN() {
		return inProdC2YN;
	}
	public void setInProdC2YN(String inProdC2YN) {
		this.inProdC2YN = inProdC2YN;
	}
	public String getInProdC3YN() {
		return inProdC3YN;
	}
	public void setInProdC3YN(String inProdC3YN) {
		this.inProdC3YN = inProdC3YN;
	}
	public String getInProdC4YN() {
		return inProdC4YN;
	}
	public void setInProdC4YN(String inProdC4YN) {
		this.inProdC4YN = inProdC4YN;
	}
	public String getInProdC5YN() {
		return inProdC5YN;
	}
	public void setInProdC5YN(String inProdC5YN) {
		this.inProdC5YN = inProdC5YN;
	}
	public String getDynamicProdCurrM() {
		return dynamicProdCurrM;
	}
	public void setDynamicProdCurrM(String dynamicProdCurrM) {
		this.dynamicProdCurrM = dynamicProdCurrM;
	}
		
}
