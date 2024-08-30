package com.systex.jbranch.app.server.fps.fps324;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FPS324OutputVO extends PagingOutputVO {
  public FPS324OutputVO() {
  }

  private List<Map<String, Object>> rate;
  private int avgAge;
  private boolean hasData;
  private int checkName;
  private String marketOverview;
  private String planDate;

  private List<Map<String, Object>> outputList;
  private List<Map<String, Object>> recommendList;
  private List<Map<String, Object>> settingList;
  private List<Map<String, Object>> featureDescription;

  public List<Map<String, Object>> getRate(){
    return rate;
  }

  public void setRate(List<Map<String, Object>> rate){
    this.rate = rate;
  }

  public int getAvgAge(){
    return avgAge;
  }

  public void setAvgAge(int avgAge){
    this.avgAge = avgAge;
  }

  public boolean isHasData(){
    return hasData;
  }

  public void setHasData(boolean hasData){
    this.hasData = hasData;
  }

  public void setOutputList(List<Map<String, Object>> outputList){
    this.outputList = outputList;
  }

  public List<Map<String, Object>> getOutputList(){
    return outputList;
  }

  public void setSettingList(List<Map<String, Object>> settingList){
    this.settingList = settingList;
  }

  public List<Map<String, Object>> getSettingList(){
    return settingList;
  }

  public int getCheckName(){
    return checkName;
  }

  public void setCheckName(int checkName){
    this.checkName = checkName;
  }

  public String getMarketOverview(){
    return marketOverview;
  }

  public void setMarketOverview(String marketOverview){
    this.marketOverview = marketOverview;
  }

  public String getPlanDate(){
    return planDate;
  }

  public void setPlanDate(String planDate){
    this.planDate = planDate;
  }

  public List<Map<String, Object>> getFeatureDescription(){
    return featureDescription;
  }

  public void setFeatureDescription(List<Map<String, Object>> featureDescription){
    this.featureDescription = featureDescription;
  }

  public List<Map<String, Object>> getRecommendList(){
    return recommendList;
  }

  public void setRecommendList(List<Map<String, Object>> recommendList){
    this.recommendList = recommendList;
  }

}
