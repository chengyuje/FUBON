package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.math.BigDecimal;

//研究員市場投資組合配比輸出資料VO
public class AdvDistributionVO implements Serializable {
	
	private String advisorID;		//研究員員編
	private BigDecimal weight; 		//市場投資組合配比比例
	private boolean hasSetting;		//有無市場配比設定
	
	public String getAdvisorID() {
		return advisorID;
	}
	public void setAdvisorID(String advisorID) {
		this.advisorID = advisorID;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public boolean isHasSetting() {
		return hasSetting;
	}
	public void setHasSetting(boolean hasSetting) {
		this.hasSetting = hasSetting;
	}
	
}
