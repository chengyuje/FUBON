package com.systex.jbranch.app.server.fps.fps210;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FPS210OutputVO {
  public FPS210OutputVO(){}
  
  private List<Map<String, Object>> outputList;
  private List<Map<String, Object>> cashList;
  private List<Map<String, Object>> custList;
  private String newFlag;
  private boolean hasInvest;
  
  private String prevBusiDay;
  
  private BigDecimal cashPreparePct;

  public List<Map<String, Object>> getOutputList() {
    return outputList;
  }

  public void setOutputList(List<Map<String, Object>> outputList) {
    this.outputList = outputList;
  }

  public List<Map<String, Object>> getCashList() {
    return cashList;
  }

  public void setCashList(List<Map<String, Object>> cashList) {
    this.cashList = cashList;
  }

  public String getNewFlag() {
    return newFlag;
  }

  public void setNewFlag(String newFlag) {
    this.newFlag = newFlag;
  }

  public boolean isHasInvest() {
    return hasInvest;
  }

  public void setHasInvest(boolean hasInvest) {
    this.hasInvest = hasInvest;
  }

  public List<Map<String, Object>> getCustList() {
    return custList;
  }

  public void setCustList(List<Map<String, Object>> custList) {
    this.custList = custList;
  }

	public BigDecimal getCashPreparePct() {
		return cashPreparePct;
	}
	
	public void setCashPreparePct(BigDecimal cashPreparePct) {
		this.cashPreparePct = cashPreparePct;
	}

	public String getPrevBusiDay() {
		return prevBusiDay;
	}

	public void setPrevBusiDay(String prevBusiDay) {
		this.prevBusiDay = prevBusiDay;
	}
}
