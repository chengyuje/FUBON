package com.systex.jbranch.app.server.fps.appvo.vo;

import java.math.BigDecimal;

public class AssetVO {

	private BigDecimal fee12Mon;//近一年累計手收金額
	private BigDecimal creditNo;//信用卡數
	private BigDecimal creditAmt;//信用卡額度
	private BigDecimal twdDepositTotal;//台幣存款總額
	private BigDecimal forDepositTotal;//外幣存款總額
	
	private BigDecimal houLoanAUM;//房貸放款總額
	private BigDecimal creLoanAUM;//信貸放款總額
	private BigDecimal totalAUM;//資產總額
	private BigDecimal fundTotal;//基金總額
	private BigDecimal ratioAUM;//基金總額
	//暫時保留
	private BigDecimal loanAUM;//放款餘額
	public BigDecimal getFee12Mon() {
		return fee12Mon;
	}
	public void setFee12Mon(BigDecimal fee12Mon) {
		this.fee12Mon = fee12Mon;
	}
	public BigDecimal getCreditNo() {
		return creditNo;
	}
	public void setCreditNo(BigDecimal creditNo) {
		this.creditNo = creditNo;
	}
	public BigDecimal getCreditAmt() {
		return creditAmt;
	}
	public void setCreditAmt(BigDecimal creditAmt) {
		this.creditAmt = creditAmt;
	}
	public BigDecimal getTwdDepositTotal() {
		return twdDepositTotal;
	}
	public void setTwdDepositTotal(BigDecimal twdDepositTotal) {
		this.twdDepositTotal = twdDepositTotal;
	}
	public BigDecimal getForDepositTotal() {
		return forDepositTotal;
	}
	public void setForDepositTotal(BigDecimal forDepositTotal) {
		this.forDepositTotal = forDepositTotal;
	}
	public BigDecimal getHouLoanAUM() {
		return houLoanAUM;
	}
	public void setHouLoanAUM(BigDecimal houLoanAUM) {
		this.houLoanAUM = houLoanAUM;
	}
	public BigDecimal getCreLoanAUM() {
		return creLoanAUM;
	}
	public void setCreLoanAUM(BigDecimal creLoanAUM) {
		this.creLoanAUM = creLoanAUM;
	}
	public BigDecimal getTotalAUM() {
		return totalAUM;
	}
	public void setTotalAUM(BigDecimal totalAUM) {
		this.totalAUM = totalAUM;
	}
	public BigDecimal getFundTotal() {
		return fundTotal;
	}
	public void setFundTotal(BigDecimal fundTotal) {
		this.fundTotal = fundTotal;
	}
	public BigDecimal getRatioAUM() {
		return ratioAUM;
	}
	public void setRatioAUM(BigDecimal ratioAUM) {
		this.ratioAUM = ratioAUM;
	}
	public BigDecimal getLoanAUM() {
		return loanAUM;
	}
	public void setLoanAUM(BigDecimal loanAUM) {
		this.loanAUM = loanAUM;
	}
}
