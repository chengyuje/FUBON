package com.systex.jbranch.app.server.fps.fps220;

import java.math.BigDecimal;

import com.systex.jbranch.app.server.fps.fps200.FPS200InputVO;

public class FPS220InputVO extends FPS200InputVO{
  public FPS220InputVO(){}

  private BigDecimal depositPct; 
  private BigDecimal fixedPct; 
  private BigDecimal stockPct;
  
  public BigDecimal getDepositPct() {
    return depositPct;
  }
  
  public void setDepositPct(BigDecimal depositPct) {
    this.depositPct = depositPct;
  }
  
  public BigDecimal getFixedPct() {
    return fixedPct;
  }
  
  public void setFixedPct(BigDecimal fixedPct) {
    this.fixedPct = fixedPct;
  }
  
  public BigDecimal getStockPct() {
    return stockPct;
  }
  
  public void setStockPct(BigDecimal stockPct) {
    this.stockPct = stockPct;
  }

}