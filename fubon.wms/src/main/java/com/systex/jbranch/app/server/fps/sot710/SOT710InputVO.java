package com.systex.jbranch.app.server.fps.sot710;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 *
 * @version 2016/12/28 Sebastian
 * add three feild applyType, entrustUnit, entrustPrice
 */
public class SOT710InputVO extends PagingInputVO {
	
	private String custID;
	private String trustAcct;
	private String debitAcct;			//扣款帳號
	private String tradeSubType;
	private String prodId;
	private BigDecimal unitNum;
	//private String entrustType;
	private BigDecimal entrustAmt;
	private Date dueDate;
	private String trustCurrType;
	private BigDecimal defaultFeeRate;
	
	private String buySell;
	
	private String applySEQ;
	private String checkCode;
	
	private String empID;
	private Date startDate;
	private Date endDate;


	private String applyType;
	private BigDecimal entrustUnit;
	private BigDecimal entrustPrice;
	
	private String tradeDate;
	private BigDecimal feeRate;
	private String discountType;
	private BigDecimal feeDiscount;
	private String trustTS; //S:特金 M:金錢信託

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getApplySEQ() {
		return applySEQ;
	}

	public void setApplySEQ(String applySEQ) {
		this.applySEQ = applySEQ;
	}

	public String getBuySell() {
		return buySell;
	}

	public void setBuySell(String buySell) {
		this.buySell = buySell;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getTradeSubType() {
		return tradeSubType;
	}

	public void setTradeSubType(String tradeSubType) {
		this.tradeSubType = tradeSubType;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public BigDecimal getUnitNum() {
		return unitNum;
	}

	public void setUnitNum(BigDecimal unitNum) {
		this.unitNum = unitNum;
	}

//	public String getEntrustType() {
//		return entrustType;
//	}
//
//	public void setEntrustType(String entrustType) {
//		this.entrustType = entrustType;
//	}

	public BigDecimal getEntrustAmt(){
		return entrustAmt;
	}
	
	public void setEntrustAmt(BigDecimal entrustAmt){
		this.entrustAmt = entrustAmt;
	}
	
	public String getTrustAcct() {
		return trustAcct;
	}

	public void setTrustAcct(String trustAcct) {
		this.trustAcct = trustAcct;
	}
	
	public String getDebitAcct() {
		return debitAcct;
	}

	public void setDebitAcct(String debitAcct) {
		this.debitAcct = debitAcct;
	}

	public String getTrustCurrType() {
		return trustCurrType;
	}

	public void setTrustCurrType(String trustCurrType) {
		this.trustCurrType = trustCurrType;
	}

	public BigDecimal getDefaultFeeRate() {
		return defaultFeeRate;
	}

	public void setDefaultFeeRate(BigDecimal defaultFeeRate) {
		this.defaultFeeRate = defaultFeeRate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public BigDecimal getEntrustUnit() {
		return entrustUnit;
	}

	public void setEntrustUnit(BigDecimal entrustUnit) {
		this.entrustUnit = entrustUnit;
	}

	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}

	public BigDecimal getFeeDiscount() {
		return feeDiscount;
	}

	public void setFeeDiscount(BigDecimal feeDiscount) {
		this.feeDiscount = feeDiscount;
	}

	public String getTrustTS() {
		return trustTS;
	}

	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	
}
