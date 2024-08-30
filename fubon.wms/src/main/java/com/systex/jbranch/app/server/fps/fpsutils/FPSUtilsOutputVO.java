package com.systex.jbranch.app.server.fps.fpsutils;

public class FPSUtilsOutputVO {

  public FPSUtilsOutputVO() {
    isError = false;
  }

  private boolean  isError;

  private String[] invalidPrdID;

  public boolean isError() {
    return isError;
  }

  public void setError(boolean isError) {
    this.isError = isError;
  }

  public String[] getInvalidPrdID() {
    return invalidPrdID;
  }

  public void setInvalidPrdID(String[] invalidPrdID) {
    this.invalidPrdID = invalidPrdID;
  }

}
