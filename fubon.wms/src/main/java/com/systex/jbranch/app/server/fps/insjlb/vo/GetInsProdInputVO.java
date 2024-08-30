package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.math.BigDecimal;

public class GetInsProdInputVO {

	private String prodCode; 		// 保險商品代號(Prod_Code)
	private String insCo; 			// 保險公司代號
	private String insCoName; 		// 保險公司名稱
	private String isMainBCall; 	// 是否為主力
	private String custInsAge; 		// 客戶保險年齡
	private String isCoverAll; 		// 保障期間
	private String isRemitFee; 		// 是否豁免保費
	private String isAddAmt; 		// 是否遞增保額
	private String isReturn; 		// 是否還本
	private String returnWay; 		// 還本方式
	private String isSmallAmt; 		// 是否為小額
	private String isBStructure; 	// 是否為B股架構
	private String isPayTax; 		// 是否有給附稅額效果
	private String isUnit; 			// 金額是否為單位保額
	private BigDecimal amtRange; 	// 保險金額
	private String planType; 		// 適合的規劃類型
	private String subPlanType; 	// 適合的子規劃類型
	private String assetSubPlanTypeCode;  // 資產險種子類別代碼
	
	public GetInsProdInputVO() {
		super();
	}
	public String getProdCode() {
		return prodCode;
	}
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	public String getInsCo() {
		return insCo;
	}
	public void setInsCo(String insCo) {
		this.insCo = insCo;
	}
	public String getInsCoName() {
		return insCoName;
	}
	public void setInsCoName(String insCoName) {
		this.insCoName = insCoName;
	}
	public String getIsMainBCall() {
		return isMainBCall;
	}
	public void setIsMainBCall(String isMainBCall) {
		this.isMainBCall = isMainBCall;
	}
	public String getIsCoverAll() {
		return isCoverAll;
	}
	public void setIsCoverAll(String isCoverAll) {
		this.isCoverAll = isCoverAll;
	}
	public String getIsRemitFee() {
		return isRemitFee;
	}
	public void setIsRemitFee(String isRemitFee) {
		this.isRemitFee = isRemitFee;
	}
	public String getIsAddAmt() {
		return isAddAmt;
	}
	public void setIsAddAmt(String isAddAmt) {
		this.isAddAmt = isAddAmt;
	}
	public String getIsReturn() {
		return isReturn;
	}
	public void setIsReturn(String isReturn) {
		this.isReturn = isReturn;
	}
	public String getReturnWay() {
		return returnWay;
	}
	public void setReturnWay(String returnWay) {
		this.returnWay = returnWay;
	}
	public String getIsSmallAmt() {
		return isSmallAmt;
	}
	public void setIsSmallAmt(String isSmallAmt) {
		this.isSmallAmt = isSmallAmt;
	}
	public String getIsBStructure() {
		return isBStructure;
	}
	public void setIsBStructure(String isBStructure) {
		this.isBStructure = isBStructure;
	}
	public String getIsPayTax() {
		return isPayTax;
	}
	public void setIsPayTax(String isPayTax) {
		this.isPayTax = isPayTax;
	}
	public String getIsUnit() {
		return isUnit;
	}
	public void setIsUnit(String isUnit) {
		this.isUnit = isUnit;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getSubPlanType() {
		return subPlanType;
	}
	public void setSubPlanType(String subPlanType) {
		this.subPlanType = subPlanType;
	}
	public BigDecimal getAmtRange() {
		return amtRange;
	}
	public void setAmtRange(BigDecimal amtRange) {
		this.amtRange = amtRange;
	}
	public String getCustInsAge() {
		return custInsAge;
	}
	public void setCustInsAge(String custInsAge) {
		this.custInsAge = custInsAge;
	}
	public String getAssetSubPlanTypeCode() {
		return assetSubPlanTypeCode;
	}
	public void setAssetSubPlanTypeCode(String assetSubPlanTypeCode) {
		this.assetSubPlanTypeCode = assetSubPlanTypeCode;
	}
}
