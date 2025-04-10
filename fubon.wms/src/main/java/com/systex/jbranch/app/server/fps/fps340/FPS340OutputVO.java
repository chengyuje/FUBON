package com.systex.jbranch.app.server.fps.fps340;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;

public class FPS340OutputVO {
  public FPS340OutputVO(){}
  
  private List<Map<String, Object>> outputList;
  private List<Map<String, Object>> fxRateList;
  private List<Map<String, Object>> headList;
  private List<Map<String, Object>> initModelPortfolioList;
  private List<Map<String, Object>> warningList;
  private List<Map<String, Object>> briefList;
  private ResultObj hisPerformanceList;
  private List<Map<String, Object>> yearRateList;
  private List<Map<String, Object>> MFDPerformanceList;
  private List<Map<String, Object>> rptPicture;
  private List<Map<String, Object>> achivedParamList;
  private List<Map<String, Object>> redemptionList;
  
  private String planDate ;
  private String recoVolaility;//波動度DB
  
  private Double volatility;
  private Double fullYearVolatility;
  private Double historyYRate;

  private boolean hasInvest;
  private List<Map<String, Object>> manualList;
  private String preBusiDay;

  public boolean isHasInvest() {
  	return hasInvest;
  }
  
  public void setHasInvest(boolean hasInvest) {
  	this.hasInvest = hasInvest;
  }
  
  public List<Map<String, Object>> getManualList() {
  	return manualList;
  }

  public void setManualList(List<Map<String, Object>> manualList) {
  	this.manualList = manualList;
  }
  
  public List<Map<String, Object>> getWarningList() {
  	return warningList;
  }
  
  public void setWarningList(List<Map<String, Object>> warningList) {
  	this.warningList = warningList;
  }
  
  public List<Map<String, Object>> getBriefList() {
  	return briefList;
  }
  
  public void setBriefList(List<Map<String, Object>> briefList) {
  	this.briefList = briefList;
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

  public List<Map<String, Object>> getHeadList() {
    return headList;
  }

  public void setHeadList(
      List<Map<String, Object>> headList) {
    this.headList = headList;
  }

  public List<Map<String, Object>> getInitModelPortfolioList() {
    return initModelPortfolioList;
  }

  public void setInitModelPortfolioList(
      List<Map<String, Object>> initModelPortfolioList) {
    this.initModelPortfolioList = initModelPortfolioList;
  }

  public ResultObj getHisPerformanceList() {
    return hisPerformanceList;
  }

  public void setHisPerformanceList(ResultObj hisPerformanceList) {
    this.hisPerformanceList = hisPerformanceList;
  }

  public List<Map<String, Object>> getYearRateList() {
    return yearRateList;
  }

  public void setYearRateList(List<Map<String, Object>> yearRateList) {
    this.yearRateList = yearRateList;
  }

  public List<Map<String, Object>> getMFDPerformanceList() {
    return MFDPerformanceList;
  }

  public void setMFDPerformanceList(List<Map<String, Object>> mFDPerformanceList) {
    MFDPerformanceList = mFDPerformanceList;
  }

  public List<Map<String, Object>> getRptPicture() {
    return rptPicture;
  }

  public void setRptPicture(List<Map<String, Object>> rptPicture) {
    this.rptPicture = rptPicture;
  }

  public String getPlanDate() {
	  return planDate;
  }

  public void setPlanDate(String planDate) {
	  this.planDate = planDate;
  }

  public String getRecoVolaility() {
	  return recoVolaility;
  }

  public void setRecoVolaility(String recoVolaility) {
	  this.recoVolaility = recoVolaility;
  }  

  public Double getVolatility() {
	  return volatility;
  }

  public void setVolatility(Double volatility) {
	  this.volatility = volatility;
  }
  
  public Double getFullYearVolatility() {
	  return fullYearVolatility;
  }

  public void setFullYearVolatility(Double fullYearVolatility) {
	  this.fullYearVolatility = fullYearVolatility;
  }

  public Double getHistoryYRate() {
	  return historyYRate;
  }

  public void setHistoryYRate(Double historyYRate) {
	  this.historyYRate = historyYRate;
  }

  public List<Map<String, Object>> getAchivedParamList() {
	  return achivedParamList;
  }

  public void setAchivedParamList(List<Map<String, Object>> achivedParamList) {
	  this.achivedParamList = achivedParamList;
  }

  public List<Map<String, Object>> getRedemptionList() {
	  return redemptionList;
  }

  public void setRedemptionList(List<Map<String, Object>> redemptionList) {
	  this.redemptionList = redemptionList;
  }

public String getPreBusiDay() {
	return preBusiDay;
}

public void setPreBusiDay(String preBusiDay) {
	this.preBusiDay = preBusiDay;
}

}
