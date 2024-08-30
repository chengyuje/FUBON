package com.systex.jbranch.app.server.fps.fpsutils;

import java.math.BigDecimal;
import java.util.List;

public class FPSUtilsPdfInputVO {
  public FPSUtilsPdfInputVO() {
  }

  private String     fileName;
  private String     tempFileName;
  private BigDecimal printSEQ;
  private String     custID;
  private String     planID;
  private String     sppType;
  private String     action;
  private String	isFps410;
  private String	aoCode;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getTempFileName() {
    return tempFileName;
  }

  public void setTempFileName(String tempFileName) {
    this.tempFileName = tempFileName;
  }

  public BigDecimal getPrintSEQ() {
    return printSEQ;
  }

  public void setPrintSEQ(BigDecimal printSEQ) {
    this.printSEQ = printSEQ;
  }

  public String getCustID() {
    return custID;
  }

  public void setCustID(String custID) {
    this.custID = custID;
  }

  public String getPlanID() {
    return planID;
  }

  public void setPlanID(String planID) {
    this.planID = planID;
  }

  public String getSppType() {
    return sppType;
  }

  public void setSppType(String sppType) {
    this.sppType = sppType;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

public String getIsFps410() {
	return isFps410;
}

public void setIsFps410(String isFps410) {
	this.isFps410 = isFps410;
}

public String getAoCode() {
	return aoCode;
}

public void setAoCode(String aoCode) {
	this.aoCode = aoCode;
}

}
