package com.systex.jbranch.app.server.fps.crm829;

import com.systex.jbranch.fubon.commons.esb.vo.nmp8yb.CustAssetNMP8YB;

import java.math.BigDecimal;
import java.util.Date;

public class CustAssetNano extends CustAssetNMP8YB {
	private int 		yearLimit;
	private String 		target;
	private Date 		StarDate;
	private String 		Type;
	private BigDecimal 	Amt;
	private String 		RiskPref;
	private String 		Exchange;
	private String 		ChargeDate;
	private String 		Strategy;
	private String 		Currency;
	private BigDecimal 	MaximumInvestmentAmount;
    private String 		SelectedCards;
    private String 		SelectedCardsName;
    private String 		CumulativeMultiple;
    private String 		IsCumulativeSwitchOn;
    private BigDecimal 	CumulativeAmount;
    private String 		ModifyTime;
	
	public String getExchange() {
		return Exchange;
	}
	public void setExchange(String exchange) {
		Exchange = exchange;
	}
	public String getRiskPref() {
		return RiskPref;
	}
	public void setRiskPref(String riskPref) {
		RiskPref = riskPref;
	}
	public int getYearLimit() {
		return yearLimit;
	}
	public void setYearLimit(int yearLimit) {
		this.yearLimit = yearLimit;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Date getStarDate() {
		return StarDate;
	}
	public void setStarDate(Date starDate) {
		StarDate = starDate;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public BigDecimal getAmt() {
		return Amt;
	}
	public void setAmt(BigDecimal amt) {
		Amt = amt;
	}
	public String getChargeDate() {
		return ChargeDate;
	}
	public void setChargeDate(String chargeDate) {
		ChargeDate = chargeDate;
	}
	public String getStrategy() {
		return Strategy;
	}
	public void setStrategy(String strategy) {
		Strategy = strategy;
	}
	public String getCurrency() {
		return Currency;
	}
	public void setCurrency(String currency) {
		Currency = currency;
	}
	public BigDecimal getMaximumInvestmentAmount() {
		return MaximumInvestmentAmount;
	}
	public void setMaximumInvestmentAmount(BigDecimal maximumInvestmentAmount) {
		MaximumInvestmentAmount = maximumInvestmentAmount;
	}
	public String getSelectedCards() {
		return SelectedCards;
	}
	public void setSelectedCards(String selectedCards) {
		SelectedCards = selectedCards;
	}
	public String getSelectedCardsName() {
		return SelectedCardsName;
	}
	public void setSelectedCardsName(String selectedCardsName) {
		SelectedCardsName = selectedCardsName;
	}
	public String getCumulativeMultiple() {
		return CumulativeMultiple;
	}
	public void setCumulativeMultiple(String cumulativeMultiple) {
		CumulativeMultiple = cumulativeMultiple;
	}
	public String getIsCumulativeSwitchOn() {
		return IsCumulativeSwitchOn;
	}
	public void setIsCumulativeSwitchOn(String isCumulativeSwitchOn) {
		IsCumulativeSwitchOn = isCumulativeSwitchOn;
	}
	public BigDecimal getCumulativeAmount() {
		return CumulativeAmount;
	}
	public void setCumulativeAmount(BigDecimal cumulativeAmount) {
		CumulativeAmount = cumulativeAmount;
	}
	public String getModifyTime() {
		return ModifyTime;
	}
	public void setModifyTime(String modifyTime) {
		ModifyTime = modifyTime;
	}
	
}
