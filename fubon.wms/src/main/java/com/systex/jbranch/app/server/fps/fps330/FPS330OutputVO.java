package com.systex.jbranch.app.server.fps.fps330;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FPS330OutputVO extends PagingOutputVO {
  public FPS330OutputVO() {
  }

  private ResultObj outputList;
  private Double volatility;
  private List<Map<String, Object>> yearRateList;
  private String errCode;

  public ResultObj getOutputList() {
    return outputList;
  }

  public void setOutputList(ResultObj outputList) {
    this.outputList = outputList;
  }

  public List<Map<String, Object>> getYearRateList() {
    return yearRateList;
  }

  public void setYearRateList(List<Map<String, Object>> yearRateList) {
    this.yearRateList = yearRateList;
  }

  public Double getVolatility() {
    return volatility;
  }

  public void setVolatility(Double volatility) {
    this.volatility = volatility;
  }

  public String getErrCode() {
    return errCode;
  }

  public void setErrCode(String errCode) {
    this.errCode = errCode;
  }

}
