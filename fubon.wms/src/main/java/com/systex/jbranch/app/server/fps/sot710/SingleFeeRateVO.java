package com.systex.jbranch.app.server.fps.sot710;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

public class SingleFeeRateVO {
	
	private String applySeq;		//議價編號
	private String authEmpId;		//覆核主管員編
	private Date authDate;			//覆核完成日期
	private Date authTime;			//覆核完成時間
	private String prodId;			//商品代碼
	private BigDecimal unit;		//委託數量
	private BigDecimal price;		//委託價格
	private BigDecimal feeRate;		//折扣費率
	private BigDecimal feeDiscount;	//折扣數
	private BigDecimal fee;			//手續費金額
	private String trustCurrType;	//信託業務別 Y：外幣 N：台幣
	private String seqUseCode;		//優惠碼是否使用 (Y/ )
	private Date applyDate;			//申請日期
	private Date applyTime;			//申請時間
	private String discountType;	//折扣方式 1:BY 費率 2:BY 折扣
	
	public String getApplySeq() {
		return applySeq;
	}
	public void setApplySeq(String applySeq) {
		this.applySeq = applySeq;
	}
	public String getAuthEmpId() {
		return authEmpId;
	}
	public void setAuthEmpId(String authEmpId) {
		this.authEmpId = authEmpId;
	}
	public Date getAuthDate() {
		return authDate;
	}
	public void setAuthDate(Date authDate) {
		this.authDate = authDate;
	}
	public Date getAuthTime() {
		return authTime;
	}
	public void setAuthTime(Date authTime) {
		this.authTime = authTime;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public BigDecimal getUnit() {
		return unit;
	}
	public void setUnit(BigDecimal unit) {
		this.unit = unit;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}
	public BigDecimal getFeeDiscount() {
		return feeDiscount;
	}
	public void setFeeDiscount(BigDecimal feeDiscount) {
		this.feeDiscount = feeDiscount;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public String getTrustCurrType() {
		return trustCurrType;
	}
	public void setTrustCurrType(String trustCurrType) {
		this.trustCurrType = trustCurrType;
	}
	public String getSeqUseCode() {
		return seqUseCode;
	}
	public void setSeqUseCode(String seqUseCode) {
		this.seqUseCode = seqUseCode;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public String getDiscountType() {
		return discountType;
	}
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	
}
