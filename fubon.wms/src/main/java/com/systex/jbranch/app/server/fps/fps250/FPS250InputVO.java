package com.systex.jbranch.app.server.fps.fps250;

import java.util.Date;

import com.systex.jbranch.app.server.fps.fps200.FPS200InputVO;

public class FPS250InputVO extends FPS200InputVO {
  public FPS250InputVO() {
  }

  // private String planStatus;
  private Date SD;
  private Date ED;
  private String isDisable;
  // print 
  private String SEQNO;
  
  public String getSEQNO() {
    return SEQNO;
  }
  public void setSEQNO(String sEQNO) {
    SEQNO = sEQNO;
  }
  public Date getSD() {
    return SD;
  }
  public void setSD(Date sD) {
    SD = sD;
  }
  public Date getED() {
    return ED;
  }
  public void setED(Date eD) {
    ED = eD;
  }
  public String isDisable() {
    return isDisable;
  }
  public void setDisable(String isDisable) {
    this.isDisable = isDisable;
  }
  
  
}