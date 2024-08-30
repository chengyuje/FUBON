package com.systex.jbranch.app.server.fps.fps220;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;

public class FPS220OutputVO {
  public FPS220OutputVO(){}
  
  private List<Map<String, Object>> outputList;
  private List<Map<String, Object>> initModelPortfolioList;
  private String market;
  private boolean hasInvest;
  private String recoVolatility;
  private Double stockVolatility;
  private int cntFixed;

  public boolean isHasInvest() {
    return hasInvest;
  }

  public void setHasInvest(boolean hasInvest) {
    this.hasInvest = hasInvest;
  }

  public List<Map<String, Object>> getOutputList() {
    return outputList;
  }

  public void setOutputList(List<Map<String, Object>> outputList) {
    this.outputList = outputList;
  }

  public String getMarket() {
    return market;
  }

  public void setMarket(String market) {
    this.market = market;
  }

  public String getRecoVolatility() {
    return recoVolatility;
  }

  public void setRecoVolatility(String recoVolatility) {
    this.recoVolatility = recoVolatility;
  }

  public Double getStockVolatility() {
    return stockVolatility;
  }

  public void setStockVolatility(Double stockVolatility) {
    this.stockVolatility = stockVolatility;
  }

  public List<Map<String, Object>> getInitModelPortfolioList() {
    return initModelPortfolioList;
  }

  public void setInitModelPortfolioList(
      List<Map<String, Object>> initModelPortfolioList) {
    this.initModelPortfolioList = initModelPortfolioList;
  }

  public int getCntFixed() {
    return cntFixed;
  }

  public void setCntFixed(int cntFixed) {
    this.cntFixed = cntFixed;
  }
  
  
  
}
