package com.systex.jbranch.app.server.fps.fps810;

public class FPS810InputVO {
  public FPS810InputVO(){}
  
  private boolean isOffice;
  private String planType;
  private String regionID;
	private String areaID;
	private String branchID;
  
  public boolean getIsOffice() {
    return isOffice;
  }

  public void setIsOffice(boolean isOffice) {
    this.isOffice = isOffice;
  }

  public String getPlanType() {
    return planType;
  }

  public void setPlanType(String planType) {
    this.planType = planType;
  }

  public String getRegionID() {
    return regionID;
  }
  
  public void setRegionID(String regionID) {
    this.regionID = regionID;
  }
  
  public String getAreaID() {
    return areaID;
  }
  
  public void setAreaID(String areaID) {
    this.areaID = areaID;
  }
  
  public String getBranchID() {
    return branchID;
  }
  
  public void setBranchID(String branchID) {
    this.branchID = branchID;
  }

	
}