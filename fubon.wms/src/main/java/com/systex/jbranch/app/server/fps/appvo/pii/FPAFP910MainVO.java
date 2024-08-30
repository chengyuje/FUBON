package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FPAFP910MainVO implements Serializable {

	private String updateStatus;//異動狀態
	private String investorType;//投資人類型
	private String investPurpose;//投資目的
	private String investRiskAttr;//投資風險屬性
	private BigDecimal suggStockPerc;//建議占股比率 (%)
	private BigDecimal suggBondPerc;//建議占債比率 (%)
	private BigDecimal suggCurrencyPerc;//建議占貨幣比率 (%)
	private List<Map<String, Object>> suggList;//建議清單
	private String goalDesc; //投資組合目標
	public String getUpdateStatus() {
		return updateStatus;
	}
	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}
	public String getInvestorType() {
		return investorType;
	}
	public void setInvestorType(String investorType) {
		this.investorType = investorType;
	}
	public String getInvestPurpose() {
		return investPurpose;
	}
	public void setInvestPurpose(String investPurpose) {
		this.investPurpose = investPurpose;
	}
	public String getInvestRiskAttr() {
		return investRiskAttr;
	}
	public void setInvestRiskAttr(String investRiskAttr) {
		this.investRiskAttr = investRiskAttr;
	}
	public BigDecimal getSuggStockPerc() {
		return suggStockPerc;
	}
	public void setSuggStockPerc(BigDecimal suggStockPerc) {
		this.suggStockPerc = suggStockPerc;
	}
	public BigDecimal getSuggBondPerc() {
		return suggBondPerc;
	}
	public void setSuggBondPerc(BigDecimal suggBondPerc) {
		this.suggBondPerc = suggBondPerc;
	}
	public BigDecimal getSuggCurrencyPerc() {
		return suggCurrencyPerc;
	}
	public void setSuggCurrencyPerc(BigDecimal suggCurrencyPerc) {
		this.suggCurrencyPerc = suggCurrencyPerc;
	}
	
	public String getGoalDesc() {
		return goalDesc;
	}
	public void setGoalDesc(String goalDesc) {
		this.goalDesc = goalDesc;
	}
	public List<Map<String, Object>> getSuggList() {
		return suggList;
	}
	public void setSuggList(List<Map<String, Object>> suggList) {
		this.suggList = suggList;
	}
}
