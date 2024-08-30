package com.systex.jbranch.app.server.fps.prd110;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD110OutputVO extends PagingOutputVO {
	private String fund_name;
	private List resultList;

	private boolean isRecNeeded; 
	private String isFirstTrade; // 是否是首購 Y/N
	private String specialCust;  // 特定客戶 Y/N

	public String getFund_name() {
		return fund_name;
	}

	public void setFund_name(String fund_name) {
		this.fund_name = fund_name;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public boolean isRecNeeded() {
		return isRecNeeded;
	}

	public void setRecNeeded(boolean isRecNeeded) {
		this.isRecNeeded = isRecNeeded;
	}

	public String getIsFirstTrade() {
		return isFirstTrade;
	}

	public void setIsFirstTrade(String isFirstTrade) {
		this.isFirstTrade = isFirstTrade;
	}

	public String getSpecialCust() {
		return specialCust;
	}

	public void setSpecialCust(String specialCust) {
		this.specialCust = specialCust;
	}

}
