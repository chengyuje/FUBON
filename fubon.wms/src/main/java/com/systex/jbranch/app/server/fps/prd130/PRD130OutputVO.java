package com.systex.jbranch.app.server.fps.prd130;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD130OutputVO extends PagingOutputVO {
	private String bond_name;
	private List resultList;

	private boolean isRecNeeded;
	private String isFirstTrade; // 是否是首購 Y/N
	private String specialCust;  // 特定客戶 Y/N

	public String getBond_name() {
		return bond_name;
	}

	public void setBond_name(String bond_name) {
		this.bond_name = bond_name;
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
