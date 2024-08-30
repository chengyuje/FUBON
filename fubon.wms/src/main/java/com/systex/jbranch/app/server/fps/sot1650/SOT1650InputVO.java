package com.systex.jbranch.app.server.fps.sot1650;

import com.systex.jbranch.app.server.fps.sot110.SOT110InputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

//基金動態鎖利單筆申購
public class SOT1650InputVO extends SOT110InputVO {
	 
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
	private BigDecimal purchaseAmtC1; // 申購金額  
	private BigDecimal purchaseAmtC2; // 申購金額  
	private BigDecimal purchaseAmtC3; // 申購金額
	private BigDecimal purchaseAmtC4; // 申購金額  
	private BigDecimal purchaseAmtC5; // 申購金額  
	private String eviNumTypeC1;
	private String eviNumTypeC2;
	private String eviNumTypeC3;
	private String eviNumTypeC4;
	private String eviNumTypeC5;
	private String transDate;
	private String chgStatusYN;
	private String chgAmountYN;
	private String chgTransDateYN;
	private String chgAddProdYN;
	private String fProdStatusC1;
	private String fProdStatusC2;
	private String fProdStatusC3;
	private String fProdStatusC4;
	private String fProdStatusC5;
	private BigDecimal fPurchaseAmtC1;
	private BigDecimal fPurchaseAmtC2;
	private BigDecimal fPurchaseAmtC3;
	private BigDecimal fPurchaseAmtC4;
	private BigDecimal fPurchaseAmtC5;
	private String fTransDate1;
	private String fTransDate2;
	private String fTransDate3;
	private String fTransDate4;
	private String fTransDate5;
	private String fTransDate6;
	//新增子基金
	private String fProdIdC1;// 商品代號  (搜尋/儲存)
	private String fProdNameC1; // 基金名稱
	private String fProdIdC2;// 商品代號  (搜尋/儲存)
	private String fProdNameC2; // 基金名稱
	private String fProdIdC3;// 商品代號  (搜尋/儲存)
	private String fProdNameC3; // 基金名稱
	private String fProdIdC4;// 商品代號  (搜尋/儲存)
	private String fProdNameC4; // 基金名稱
	private String fProdIdC5;// 商品代號  (搜尋/儲存)
	private String fProdNameC5; // 基金名稱
	private String fProdRiskLvC1; // 產品風險等級
	private String fProdRiskLvC2; // 產品風險等級
	private String fProdRiskLvC3; // 產品風險等級
	private String fProdRiskLvC4; // 產品風險等級
	private String fProdRiskLvC5; // 產品風險等級
	private BigDecimal fAddprodAmtC1; // 申購金額  
	private BigDecimal fAddprodAmtC2; // 申購金額  
	private BigDecimal fAddprodAmtC3; // 申購金額
	private BigDecimal fAddprodAmtC4; // 申購金額  
	private BigDecimal fAddprodAmtC5; // 申購金額  
	private BigDecimal fProdMinBuyAmtC1;
	private BigDecimal fProdMinBuyAmtC2;
	private BigDecimal fProdMinBuyAmtC3;
	private BigDecimal fProdMinBuyAmtC4;
	private BigDecimal fProdMinBuyAmtC5;
	private BigDecimal fProdMinGrdAmtC1;
	private BigDecimal fProdMinGrdAmtC2;
	private BigDecimal fProdMinGrdAmtC3;
	private BigDecimal fProdMinGrdAmtC4;
	private BigDecimal fProdMinGrdAmtC5;
	
	private String certificateID; //憑證編號
	private String confirm; //確認碼	1.檢核 , 空白：確認
	private String needHnwcRiskValueYN; //需要做越級適配檢核：有新增子基金、有恢復扣款、有變更金額且增加扣款金額、有變更扣款日期且有增加次數
	
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
	public String getCertificateID() {
		return certificateID;
	}
	public void setCertificateID(String certificateID) {
		this.certificateID = certificateID;
	}
	public String getEviNumTypeC1() {
		return eviNumTypeC1;
	}
	public void setEviNumTypeC1(String eviNumTypeC1) {
		this.eviNumTypeC1 = eviNumTypeC1;
	}
	public String getEviNumTypeC2() {
		return eviNumTypeC2;
	}
	public void setEviNumTypeC2(String eviNumTypeC2) {
		this.eviNumTypeC2 = eviNumTypeC2;
	}
	public String getEviNumTypeC3() {
		return eviNumTypeC3;
	}
	public void setEviNumTypeC3(String eviNumTypeC3) {
		this.eviNumTypeC3 = eviNumTypeC3;
	}
	public String getEviNumTypeC4() {
		return eviNumTypeC4;
	}
	public void setEviNumTypeC4(String eviNumTypeC4) {
		this.eviNumTypeC4 = eviNumTypeC4;
	}
	public String getEviNumTypeC5() {
		return eviNumTypeC5;
	}
	public void setEviNumTypeC5(String eviNumTypeC5) {
		this.eviNumTypeC5 = eviNumTypeC5;
	}
	public String getChgStatusYN() {
		return chgStatusYN;
	}
	public void setChgStatusYN(String chgStatusYN) {
		this.chgStatusYN = chgStatusYN;
	}
	public String getChgAmountYN() {
		return chgAmountYN;
	}
	public void setChgAmountYN(String chgAmountYN) {
		this.chgAmountYN = chgAmountYN;
	}
	public String getChgTransDateYN() {
		return chgTransDateYN;
	}
	public void setChgTransDateYN(String chgTransDateYN) {
		this.chgTransDateYN = chgTransDateYN;
	}
	public String getChgAddProdYN() {
		return chgAddProdYN;
	}
	public void setChgAddProdYN(String chgAddProdYN) {
		this.chgAddProdYN = chgAddProdYN;
	}
	public String getfProdStatusC1() {
		return fProdStatusC1;
	}
	public void setfProdStatusC1(String fProdStatusC1) {
		this.fProdStatusC1 = fProdStatusC1;
	}
	public String getfProdStatusC2() {
		return fProdStatusC2;
	}
	public void setfProdStatusC2(String fProdStatusC2) {
		this.fProdStatusC2 = fProdStatusC2;
	}
	public String getfProdStatusC3() {
		return fProdStatusC3;
	}
	public void setfProdStatusC3(String fProdStatusC3) {
		this.fProdStatusC3 = fProdStatusC3;
	}
	public String getfProdStatusC4() {
		return fProdStatusC4;
	}
	public void setfProdStatusC4(String fProdStatusC4) {
		this.fProdStatusC4 = fProdStatusC4;
	}
	public String getfProdStatusC5() {
		return fProdStatusC5;
	}
	public void setfProdStatusC5(String fProdStatusC5) {
		this.fProdStatusC5 = fProdStatusC5;
	}
	public BigDecimal getfPurchaseAmtC1() {
		return fPurchaseAmtC1;
	}
	public void setfPurchaseAmtC1(BigDecimal fPurchaseAmtC1) {
		this.fPurchaseAmtC1 = fPurchaseAmtC1;
	}
	public BigDecimal getfPurchaseAmtC2() {
		return fPurchaseAmtC2;
	}
	public void setfPurchaseAmtC2(BigDecimal fPurchaseAmtC2) {
		this.fPurchaseAmtC2 = fPurchaseAmtC2;
	}
	public BigDecimal getfPurchaseAmtC3() {
		return fPurchaseAmtC3;
	}
	public void setfPurchaseAmtC3(BigDecimal fPurchaseAmtC3) {
		this.fPurchaseAmtC3 = fPurchaseAmtC3;
	}
	public BigDecimal getfPurchaseAmtC4() {
		return fPurchaseAmtC4;
	}
	public void setfPurchaseAmtC4(BigDecimal fPurchaseAmtC4) {
		this.fPurchaseAmtC4 = fPurchaseAmtC4;
	}
	public BigDecimal getfPurchaseAmtC5() {
		return fPurchaseAmtC5;
	}
	public void setfPurchaseAmtC5(BigDecimal fPurchaseAmtC5) {
		this.fPurchaseAmtC5 = fPurchaseAmtC5;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getfProdIdC1() {
		return fProdIdC1;
	}
	public void setfProdIdC1(String fProdIdC1) {
		this.fProdIdC1 = fProdIdC1;
	}
	public String getfProdNameC1() {
		return fProdNameC1;
	}
	public void setfProdNameC1(String fProdNameC1) {
		this.fProdNameC1 = fProdNameC1;
	}
	public String getfProdIdC2() {
		return fProdIdC2;
	}
	public void setfProdIdC2(String fProdIdC2) {
		this.fProdIdC2 = fProdIdC2;
	}
	public String getfProdNameC2() {
		return fProdNameC2;
	}
	public void setfProdNameC2(String fProdNameC2) {
		this.fProdNameC2 = fProdNameC2;
	}
	public String getfProdIdC3() {
		return fProdIdC3;
	}
	public void setfProdIdC3(String fProdIdC3) {
		this.fProdIdC3 = fProdIdC3;
	}
	public String getfProdNameC3() {
		return fProdNameC3;
	}
	public void setfProdNameC3(String fProdNameC3) {
		this.fProdNameC3 = fProdNameC3;
	}
	public String getfProdIdC4() {
		return fProdIdC4;
	}
	public void setfProdIdC4(String fProdIdC4) {
		this.fProdIdC4 = fProdIdC4;
	}
	public String getfProdNameC4() {
		return fProdNameC4;
	}
	public void setfProdNameC4(String fProdNameC4) {
		this.fProdNameC4 = fProdNameC4;
	}
	public String getfProdIdC5() {
		return fProdIdC5;
	}
	public void setfProdIdC5(String fProdIdC5) {
		this.fProdIdC5 = fProdIdC5;
	}
	public String getfProdNameC5() {
		return fProdNameC5;
	}
	public void setfProdNameC5(String fProdNameC5) {
		this.fProdNameC5 = fProdNameC5;
	}
	public String getfProdRiskLvC1() {
		return fProdRiskLvC1;
	}
	public void setfProdRiskLvC1(String fProdRiskLvC1) {
		this.fProdRiskLvC1 = fProdRiskLvC1;
	}
	public String getfProdRiskLvC2() {
		return fProdRiskLvC2;
	}
	public void setfProdRiskLvC2(String fProdRiskLvC2) {
		this.fProdRiskLvC2 = fProdRiskLvC2;
	}
	public String getfProdRiskLvC3() {
		return fProdRiskLvC3;
	}
	public void setfProdRiskLvC3(String fProdRiskLvC3) {
		this.fProdRiskLvC3 = fProdRiskLvC3;
	}
	public String getfProdRiskLvC4() {
		return fProdRiskLvC4;
	}
	public void setfProdRiskLvC4(String fProdRiskLvC4) {
		this.fProdRiskLvC4 = fProdRiskLvC4;
	}
	public String getfProdRiskLvC5() {
		return fProdRiskLvC5;
	}
	public void setfProdRiskLvC5(String fProdRiskLvC5) {
		this.fProdRiskLvC5 = fProdRiskLvC5;
	}
	public BigDecimal getfAddprodAmtC1() {
		return fAddprodAmtC1;
	}
	public void setfAddprodAmtC1(BigDecimal fAddprodAmtC1) {
		this.fAddprodAmtC1 = fAddprodAmtC1;
	}
	public BigDecimal getfAddprodAmtC2() {
		return fAddprodAmtC2;
	}
	public void setfAddprodAmtC2(BigDecimal fAddprodAmtC2) {
		this.fAddprodAmtC2 = fAddprodAmtC2;
	}
	public BigDecimal getfAddprodAmtC3() {
		return fAddprodAmtC3;
	}
	public void setfAddprodAmtC3(BigDecimal fAddprodAmtC3) {
		this.fAddprodAmtC3 = fAddprodAmtC3;
	}
	public BigDecimal getfAddprodAmtC4() {
		return fAddprodAmtC4;
	}
	public void setfAddprodAmtC4(BigDecimal fAddprodAmtC4) {
		this.fAddprodAmtC4 = fAddprodAmtC4;
	}
	public BigDecimal getfAddprodAmtC5() {
		return fAddprodAmtC5;
	}
	public void setfAddprodAmtC5(BigDecimal fAddprodAmtC5) {
		this.fAddprodAmtC5 = fAddprodAmtC5;
	}
	public BigDecimal getfProdMinBuyAmtC1() {
		return fProdMinBuyAmtC1;
	}
	public void setfProdMinBuyAmtC1(BigDecimal fProdMinBuyAmtC1) {
		this.fProdMinBuyAmtC1 = fProdMinBuyAmtC1;
	}
	public BigDecimal getfProdMinBuyAmtC2() {
		return fProdMinBuyAmtC2;
	}
	public void setfProdMinBuyAmtC2(BigDecimal fProdMinBuyAmtC2) {
		this.fProdMinBuyAmtC2 = fProdMinBuyAmtC2;
	}
	public BigDecimal getfProdMinBuyAmtC3() {
		return fProdMinBuyAmtC3;
	}
	public void setfProdMinBuyAmtC3(BigDecimal fProdMinBuyAmtC3) {
		this.fProdMinBuyAmtC3 = fProdMinBuyAmtC3;
	}
	public BigDecimal getfProdMinBuyAmtC4() {
		return fProdMinBuyAmtC4;
	}
	public void setfProdMinBuyAmtC4(BigDecimal fProdMinBuyAmtC4) {
		this.fProdMinBuyAmtC4 = fProdMinBuyAmtC4;
	}
	public BigDecimal getfProdMinBuyAmtC5() {
		return fProdMinBuyAmtC5;
	}
	public void setfProdMinBuyAmtC5(BigDecimal fProdMinBuyAmtC5) {
		this.fProdMinBuyAmtC5 = fProdMinBuyAmtC5;
	}
	public BigDecimal getfProdMinGrdAmtC1() {
		return fProdMinGrdAmtC1;
	}
	public void setfProdMinGrdAmtC1(BigDecimal fProdMinGrdAmtC1) {
		this.fProdMinGrdAmtC1 = fProdMinGrdAmtC1;
	}
	public BigDecimal getfProdMinGrdAmtC2() {
		return fProdMinGrdAmtC2;
	}
	public void setfProdMinGrdAmtC2(BigDecimal fProdMinGrdAmtC2) {
		this.fProdMinGrdAmtC2 = fProdMinGrdAmtC2;
	}
	public BigDecimal getfProdMinGrdAmtC3() {
		return fProdMinGrdAmtC3;
	}
	public void setfProdMinGrdAmtC3(BigDecimal fProdMinGrdAmtC3) {
		this.fProdMinGrdAmtC3 = fProdMinGrdAmtC3;
	}
	public BigDecimal getfProdMinGrdAmtC4() {
		return fProdMinGrdAmtC4;
	}
	public void setfProdMinGrdAmtC4(BigDecimal fProdMinGrdAmtC4) {
		this.fProdMinGrdAmtC4 = fProdMinGrdAmtC4;
	}
	public BigDecimal getfProdMinGrdAmtC5() {
		return fProdMinGrdAmtC5;
	}
	public void setfProdMinGrdAmtC5(BigDecimal fProdMinGrdAmtC5) {
		this.fProdMinGrdAmtC5 = fProdMinGrdAmtC5;
	}
	public String getfTransDate1() {
		return fTransDate1;
	}
	public void setfTransDate1(String fTransDate1) {
		this.fTransDate1 = fTransDate1;
	}
	public String getfTransDate2() {
		return fTransDate2;
	}
	public void setfTransDate2(String fTransDate2) {
		this.fTransDate2 = fTransDate2;
	}
	public String getfTransDate3() {
		return fTransDate3;
	}
	public void setfTransDate3(String fTransDate3) {
		this.fTransDate3 = fTransDate3;
	}
	public String getfTransDate4() {
		return fTransDate4;
	}
	public void setfTransDate4(String fTransDate4) {
		this.fTransDate4 = fTransDate4;
	}
	public String getfTransDate5() {
		return fTransDate5;
	}
	public void setfTransDate5(String fTransDate5) {
		this.fTransDate5 = fTransDate5;
	}
	public String getfTransDate6() {
		return fTransDate6;
	}
	public void setfTransDate6(String fTransDate6) {
		this.fTransDate6 = fTransDate6;
	}
	public String getNeedHnwcRiskValueYN() {
		return needHnwcRiskValueYN;
	}
	public void setNeedHnwcRiskValueYN(String needHnwcRiskValueYN) {
		this.needHnwcRiskValueYN = needHnwcRiskValueYN;
	}
		
}
