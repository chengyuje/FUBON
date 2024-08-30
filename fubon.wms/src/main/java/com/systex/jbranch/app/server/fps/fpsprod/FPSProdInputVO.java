package com.systex.jbranch.app.server.fps.fpsprod;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPSProdInputVO extends PagingInputVO {

  private String type;
  // 推薦?
  private String isRanked;
  private String riskType;
  private String OBU;
  private String isPro;

  // MFD
  private String fund_id;
  private String fund_name;
  private String currency;
  private String dividend_type;
  private String dividend_fre;
  private String fund_type;
  private String inv_area;
  private String inv_target;
  private String trust_com;
  private String fund_subject;
  private String fund_project;
  private String fund_customer_level;

  // ETF
  private String etfID;
  private String etfName;
  // private String currency; duplicate with fund
  private String riskLev;
  private String company;
  private String country;
  private String strategy;
  private String invType;
  private String etf_project;
  private String etf_customer_level;

  // Bond
  private String bondID;
  private String bondName;
  // private String currency; duplicate with fund
  private String bondCate;
  private String faceVal;
  private String maturity;
  private String YTM;
  private String ratingSP;
  // private String riskLev; duplicate with ETF
  private String bondCustLevel;
  private String bondProject;

  // SI SN
  private String SIID;
  private String SIName;
  private String SNID;
  private String SNName;
  private String si_project;
  private String si_customer_level;
  // private String currency; duplicate with fund
  // private String maturity; duplicate with bond
  private String snCustLevel;
  private String snProject;

  private String rateGuarantee;
  // private String riskLev; duplicate with ETF

  // SavingIns InvestIns
  private String insID;
  private String insName;
  private String insAnnual;
  private String insType;
  private String insGuarntee;
  private String insCurrency;
  private String isMain;
  private String isCom01;
  private String isIncreasing;
  private String isRepay;
  private String isRateChange;
  private String keyNo;

  private String stockBondType;

  public String getOBU() {
    return OBU;
  }

  public void setOBU(String oBU) {
    OBU = oBU;
  }

  public String getIsPro() {
    return isPro;
  }

  public void setIsPro(String isPro) {
    this.isPro = isPro;
  }

  public String getRiskType() {
    return riskType;
  }

  public void setRiskType(String riskType) {
    this.riskType = riskType;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getIsRanked() {
    return isRanked;
  }

  public void setIsRanked(String isRanked) {
    this.isRanked = isRanked;
  }

  public String getFund_id() {
    return fund_id;
  }

  public void setFund_id(String fund_id) {
    this.fund_id = fund_id;
  }

  public String getFund_name() {
    return fund_name;
  }

  public void setFund_name(String fund_name) {
    this.fund_name = fund_name;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getDividend_type() {
    return dividend_type;
  }

  public void setDividend_type(String dividend_type) {
    this.dividend_type = dividend_type;
  }

  public String getDividend_fre() {
    return dividend_fre;
  }

  public void setDividend_fre(String dividend_fre) {
    this.dividend_fre = dividend_fre;
  }

  public String getFund_type() {
    return fund_type;
  }

  public void setFund_type(String fund_type) {
    this.fund_type = fund_type;
  }

  public String getInv_area() {
    return inv_area;
  }

  public void setInv_area(String inv_area) {
    this.inv_area = inv_area;
  }

  public String getInv_target() {
    return inv_target;
  }

  public void setInv_target(String inv_target) {
    this.inv_target = inv_target;
  }

  public String getTrust_com() {
    return trust_com;
  }

  public void setTrust_com(String trust_com) {
    this.trust_com = trust_com;
  }

  public String getInsID() {
    return insID;
  }

  public void setInsID(String insID) {
    this.insID = insID;
  }

  public String getInsName() {
    return insName;
  }

  public void setInsName(String insName) {
    this.insName = insName;
  }

  public String getInsAnnual() {
    return insAnnual;
  }

  public void setInsAnnual(String insAnnual) {
    this.insAnnual = insAnnual;
  }

  public String getInsType() {
    return insType;
  }

  public void setInsType(String insType) {
    this.insType = insType;
  }

  public String getInsGuarntee() {
    return insGuarntee;
  }

  public void setInsGuarntee(String insGuarntee) {
    this.insGuarntee = insGuarntee;
  }

  public String getInsCurrency() {
    return insCurrency;
  }

  public void setInsCurrency(String insCurrency) {
    this.insCurrency = insCurrency;
  }

  public String getIsMain() {
    return isMain;
  }

  public void setIsMain(String isMain) {
    this.isMain = isMain;
  }

  public String getIsCom01() {
    return isCom01;
  }

  public void setIsCom01(String isCom01) {
    this.isCom01 = isCom01;
  }

  public String getKeyNo() {
    return keyNo;
  }

  public void setKeyNo(String keyNo) {
    this.keyNo = keyNo;
  }

  public String getEtfID() {
    return etfID;
  }

  public void setEtfID(String etfID) {
    this.etfID = etfID;
  }

  public String getEtfName() {
    return etfName;
  }

  public void setEtfName(String etfName) {
    this.etfName = etfName;
  }

  public String getRiskLev() {
    return riskLev;
  }

  public void setRiskLev(String riskLev) {
    this.riskLev = riskLev;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getStrategy() {
    return strategy;
  }

  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }

  public String getInvType() {
    return invType;
  }

  public void setInvType(String invType) {
    this.invType = invType;
  }

  public String getBondID() {
    return bondID;
  }

  public void setBondID(String bondID) {
    this.bondID = bondID;
  }

  public String getBondName() {
    return bondName;
  }

  public void setBondName(String bondName) {
    this.bondName = bondName;
  }

  public String getBondCate() {
    return bondCate;
  }

  public void setBondCate(String bondCate) {
    this.bondCate = bondCate;
  }

  public String getFaceVal() {
    return faceVal;
  }

  public void setFaceVal(String faceVal) {
    this.faceVal = faceVal;
  }

  public String getMaturity() {
    return maturity;
  }

  public void setMaturity(String maturity) {
    this.maturity = maturity;
  }

  public String getYTM() {
    return YTM;
  }

  public void setYTM(String YTM) {
    this.YTM = YTM;
  }

  public String getRatingSP() {
    return ratingSP;
  }

  public void setRatingSP(String ratingSP) {
    this.ratingSP = ratingSP;
  }

  public String getSIID() {
    return SIID;
  }

  public void setSIID(String sIID) {
    SIID = sIID;
  }

  public String getSIName() {
    return SIName;
  }

  public void setSIName(String sIName) {
    SIName = sIName;
  }

  public String getRateGuarantee() {
    return rateGuarantee;
  }

  public void setRateGuarantee(String rateGuarantee) {
    this.rateGuarantee = rateGuarantee;
  }

  public String getSNID() {
    return SNID;
  }

  public void setSNID(String sNID) {
    SNID = sNID;
  }

  public String getSNName() {
    return SNName;
  }

  public void setSNName(String sNName) {
    SNName = sNName;
  }

  public String getIsIncreasing() {
    return isIncreasing;
  }

  public void setIsIncreasing(String isIncreasing) {
    this.isIncreasing = isIncreasing;
  }

  public String getIsRepay() {
    return isRepay;
  }

  public void setIsRepay(String isRepay) {
    this.isRepay = isRepay;
  }

  public String getIsRateChange() {
    return isRateChange;
  }

  public void setIsRateChange(String isRateChange) {
    this.isRateChange = isRateChange;
  }

  public String getStockBondType() {
	return stockBondType;
  }

  public void setStockBondType(String stockBondType) {
	this.stockBondType = stockBondType;
  }

public String getFund_subject() {
	return fund_subject;
}

public void setFund_subject(String fund_subject) {
	this.fund_subject = fund_subject;
}

public String getFund_project() {
	return fund_project;
}

public void setFund_project(String fund_project) {
	this.fund_project = fund_project;
}

public String getFund_customer_level() {
	return fund_customer_level;
}

public void setFund_customer_level(String fund_customer_level) {
	this.fund_customer_level = fund_customer_level;
}

public String getEtf_project() {
	return etf_project;
}

public void setEtf_project(String etf_project) {
	this.etf_project = etf_project;
}

public String getEtf_customer_level() {
	return etf_customer_level;
}

public void setEtf_customer_level(String etf_customer_level) {
	this.etf_customer_level = etf_customer_level;
}


  public String getBondCustLevel() {
    return bondCustLevel;
  }

  public void setBondCustLevel(String bondCustLevel) {
    this.bondCustLevel = bondCustLevel;
  }

  public String getBondProject() {
    return bondProject;
  }

  public void setBondProject(String bondProject) {
    this.bondProject = bondProject;
  }

public String getSi_project() {
	return si_project;
}

public void setSi_project(String si_project) {
	this.si_project = si_project;
}

public String getSi_customer_level() {
	return si_customer_level;
}

public void setSi_customer_level(String si_customer_level) {
	this.si_customer_level = si_customer_level;
}

  public String getSnCustLevel() {
    return snCustLevel;
  }

  public void setSnCustLevel(String snCustLevel) {
    this.snCustLevel = snCustLevel;
  }

  public String getSnProject() {
    return snProject;
  }

  public void setSnProject(String snProject) {
    this.snProject = snProject;
  }
}
