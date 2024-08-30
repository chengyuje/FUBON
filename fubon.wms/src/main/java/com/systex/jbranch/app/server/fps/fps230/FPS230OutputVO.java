package com.systex.jbranch.app.server.fps.fps230;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FPS230OutputVO {
  public FPS230OutputVO() {
  }

  private boolean isHis;
  private List<Map<String, Object>> outputList;
  private List<Map<String, Object>> fxRateList;
  private List<Map<String, Object>> initModelPortfolioList;
  private List<Map<String, Object>> hisStocksList;
  private List<Map<String, Object>> historyModelList;
  private List<Map<String, Object>> featureDescription;
  private boolean hasInvest;
  private String[] errorList;
  private Double volatility;
  
  private Map<String, Object> stockRiskLevel;
  
  // 市場概況
  private String market;
  
  private BigDecimal[] suggestPct;

  public boolean isHis() {
    return isHis;
  }

  public void setHis(boolean isHis) {
    this.isHis = isHis;
  }

  public List<Map<String, Object>> getOutputList() {
    return outputList;
  }

  public void setOutputList(List<Map<String, Object>> outputList) {
    this.outputList = outputList;
  }

  public List<Map<String, Object>> getFxRateList() {
    return fxRateList;
  }

  public void setFxRateList(List<Map<String, Object>> fxRateList) {
    this.fxRateList = fxRateList;
  }

  public List<Map<String, Object>> getInitModelPortfolioList() {
    return initModelPortfolioList;
  }

  public void setInitModelPortfolioList(
      List<Map<String, Object>> initModelPortfolioList) {
    this.initModelPortfolioList = initModelPortfolioList;
  }

  public List<Map<String, Object>> getHisStocksList() {
    return hisStocksList;
  }

  public void setHisStocksList(List<Map<String, Object>> hisStocksList) {
    this.hisStocksList = hisStocksList;
  }

  public List<Map<String, Object>> getFeatureDescription() {
    return featureDescription;
  }

  public void setFeatureDescription(List<Map<String, Object>> featureDescription) {
    this.featureDescription = featureDescription;
  }

  public boolean isHasInvest() {
    return hasInvest;
  }

  public void setHasInvest(boolean hasInvest) {
    this.hasInvest = hasInvest;
  }

  public List<Map<String, Object>> getHistoryModelList() {
    return historyModelList;
  }

  public void setHistoryModelList(List<Map<String, Object>> historyModelList) {
    this.historyModelList = historyModelList;
  }

  public String[] getErrorList() {
    return errorList;
  }

  public void setErrorList(String[] errorList) {
    this.errorList = errorList;
  }

  public Double getVolatility() {
    return volatility;
  }

  public void setVolatility(Double volatility) {
    this.volatility = volatility;
  }

	public String getMarket() {
		return market;
	}
	
	public void setMarket(String market) {
		this.market = market;
	}

	public BigDecimal[] getSuggestPct() {
		return suggestPct;
	}

	public void setSuggestPct(BigDecimal[] suggestPct) {
		this.suggestPct = suggestPct;
	}

	public Map<String, Object> getStockRiskLevel() {
		return stockRiskLevel;
	}

	public void setStockRiskLevel(Map<String, Object> stockRiskLevel) {
		this.stockRiskLevel = stockRiskLevel;
	}
	
	
}
