package com.systex.jbranch.app.server.fps.fps200;

import java.math.BigDecimal;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS200InputVO extends PagingInputVO {

  private String custRisk;
  private String step;

  private BigDecimal liquidAst;
  // private BigDecimal deposit;
  private BigDecimal invProd;
  private BigDecimal otherProd;
  private BigDecimal otherBank;
  // total
  private BigDecimal planAmt;

  private BigDecimal depositAmt;
  private BigDecimal fixedAmt;
  private BigDecimal stockAmt;
  private BigDecimal otherAmt;

  // 歷史規劃查詢
  private String planStatus;

  // Juan
  private String empID;
  private String sppType;
  private String action;
  private String riskType;
  private String custID;
  private String planID;
  private String checkType;
  // cust
  private String[] aoCode;
  private String branchID;
  private String OBU;
  private String isPro;
  
  private String stockBondType;

  public String getIsPro() {
    return isPro;
  }

  public String getOBU() {
    return OBU;
  }

  public void setOBU(String oBU) {
    OBU = oBU;
  }

  public void setIsPro(String isPro) {
    this.isPro = isPro;
  }

  public String getCustID() {
    return custID;
  }

  public void setCustID(String custID) {
    this.custID = custID;
  }

  public String getRiskType() {
    return riskType;
  }

  public void setRiskType(String riskType) {
    this.riskType = riskType;
  }

  public String getCustRisk() {
    return custRisk;
  }

  public void setCustRisk(String custRisk) {
    this.custRisk = custRisk;
  }

  public String getPlanID() {
    return planID;
  }

  public void setPlanID(String planID) {
    this.planID = planID;
  }

  public String getStep() {
    return step;
  }

  public void setStep(String step) {
    this.step = step;
  }

  public BigDecimal getLiquidAst() {
    return liquidAst;
  }

  public void setLiquidAst(BigDecimal liquidAst) {
    this.liquidAst = liquidAst;
  }

  public BigDecimal getInvProd() {
    return invProd;
  }

  public void setInvProd(BigDecimal invProd) {
    this.invProd = invProd;
  }

  public BigDecimal getOtherProd() {
    return otherProd;
  }

  public void setOtherProd(BigDecimal otherProd) {
    this.otherProd = otherProd;
  }

  public BigDecimal getOtherBank() {
    return otherBank;
  }

  public void setOtherBank(BigDecimal otherBank) {
    this.otherBank = otherBank;
  }

  public BigDecimal getPlanAmt() {
    return planAmt;
  }

  public void setPlanAmt(BigDecimal planAmt) {
    this.planAmt = planAmt;
  }

  public BigDecimal getDepositAmt() {
    return depositAmt;
  }

  public void setDepositAmt(BigDecimal depositAmt) {
    this.depositAmt = depositAmt;
  }

  public BigDecimal getFixedAmt() {
    return fixedAmt;
  }

  public void setFixedAmt(BigDecimal fixedAmt) {
    this.fixedAmt = fixedAmt;
  }

  public BigDecimal getStockAmt() {
    return stockAmt;
  }

  public void setStockAmt(BigDecimal stockAmt) {
    this.stockAmt = stockAmt;
  }

  public BigDecimal getOtherAmt() {
    return otherAmt;
  }

  public void setOtherAmt(BigDecimal otherAmt) {
    this.otherAmt = otherAmt;
  }

  public String getPlanStatus() {
    return planStatus;
  }

  public void setPlanStatus(String planStatus) {
    this.planStatus = planStatus;
  }

  // Juan

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String[] getAoCode() {
    return aoCode;
  }

  public void setAoCode(String[] aoCode) {
    this.aoCode = aoCode;
  }

  public String getBranchID() {
    return branchID;
  }

  public void setBranchID(String branchID) {
    this.branchID = branchID;
  }

  public String getCheckType() {
    return checkType;
  }

  public void setCheckType(String checkType) {
    this.checkType = checkType;
  }

  public String getSppType() {
    return sppType;
  }

  public void setSppType(String sppType) {
    this.sppType = sppType;
  }

  public String getEmpID() {
    return empID;
  }

  public void setEmpID(String empID) {
    this.empID = empID;
  }

  public String getStockBondType() {
	return stockBondType;
  }

  public void setStockBondType(String stockBondType) {
	this.stockBondType = stockBondType;
  }

}
