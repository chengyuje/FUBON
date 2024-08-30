package com.systex.jbranch.fubon.commons.esb.vo.nmi002;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by CathyTang on 2018/12/21.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMI002OutputVODetails {
	@XmlElement
	private String StarDate;
    @XmlElement
	private String YearLimit;
    @XmlElement
	private String Strategy;
    @XmlElement
	private String RiskPref;
    @XmlElement
	private String Type;
    @XmlElement
	private String TargetAmt;
    @XmlElement
	private String Acct;
    @XmlElement
	private String Amt;
    @XmlElement
	private String ChargeDate;
    @XmlElement
	private String NextChargeDate;
    @XmlElement
	private String Status;
    @XmlElement
	private String EviNum;
    @XmlElement
	private String Charge;
    @XmlElement
	private String Coupon;
    @XmlElement
	private String IsNeedApprove;
    @XmlElement
	private String PlanName;
    @XmlElement
	private String Target;
    @XmlElement
	private String Exchange;
    @XmlElement
	private String DivType;
    @XmlElement
    private String MaximumInvestmentAmount;
    @XmlElement
    private String SelectedCards;
    @XmlElement
    private String CumulativeMultiple;
    @XmlElement
    private String IsCumulativeSwitchOn;
    @XmlElement
    private String CumulativeAmount;
    
    
	public String getExchange() {
		return Exchange;
	}
	public void setExchange(String exchange) {
		Exchange = exchange;
	}
	public String getStarDate() {
		return StarDate;
	}
	public void setStarDate(String starDate) {
		StarDate = starDate;
	}
	public String getYearLimit() {
		return YearLimit;
	}
	public void setYearLimit(String yearLimit) {
		YearLimit = yearLimit;
	}
	public String getStrategy() {
		return Strategy;
	}
	public void setStrategy(String strategy) {
		Strategy = strategy;
	}
	public String getRiskPref() {
		return RiskPref;
	}
	public void setRiskPref(String riskPref) {
		RiskPref = riskPref;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getTargetAmt() {
		return TargetAmt;
	}
	public void setTargetAmt(String targetAmt) {
		TargetAmt = targetAmt;
	}
	public String getAcct() {
		return Acct;
	}
	public void setAcct(String acct) {
		Acct = acct;
	}
	public String getAmt() {
		return Amt;
	}
	public void setAmt(String amt) {
		Amt = amt;
	}
	public String getChargeDate() {
		return ChargeDate;
	}
	public void setChargeDate(String chargeDate) {
		ChargeDate = chargeDate;
	}
	public String getNextChargeDate() {
		return NextChargeDate;
	}
	public void setNextChargeDate(String nextChargeDate) {
		NextChargeDate = nextChargeDate;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getEviNum() {
		return EviNum;
	}
	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}
	public String getCharge() {
		return Charge;
	}
	public void setCharge(String charge) {
		Charge = charge;
	}
	public String getCoupon() {
		return Coupon;
	}
	public void setCoupon(String coupon) {
		Coupon = coupon;
	}
	public String getIsNeedApprove() {
		return IsNeedApprove;
	}
	public void setIsNeedApprove(String isNeedApprove) {
		IsNeedApprove = isNeedApprove;
	}
	public String getPlanName() {
		return PlanName;
	}
	public void setPlanName(String planName) {
		PlanName = planName;
	}
	public String getTarget() {
		return Target;
	}
	public void setTarget(String target) {
		Target = target;
	}
	public String getDivType() {
		return DivType;
	}
	public void setDivType(String divType) {
		DivType = divType;
	}
	public String getMaximumInvestmentAmount() {
		return MaximumInvestmentAmount;
	}
	public void setMaximumInvestmentAmount(String maximumInvestmentAmount) {
		MaximumInvestmentAmount = maximumInvestmentAmount;
	}
	public String getSelectedCards() {
		return SelectedCards;
	}
	public void setSelectedCards(String selectedCards) {
		SelectedCards = selectedCards;
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
	public String getCumulativeAmount() {
		return CumulativeAmount;
	}
	public void setCumulativeAmount(String cumulativeAmount) {
		CumulativeAmount = cumulativeAmount;
	}
    
        
}
