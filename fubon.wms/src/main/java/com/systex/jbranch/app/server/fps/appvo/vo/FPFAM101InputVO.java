package com.systex.jbranch.app.server.fps.appvo.vo;

import java.util.List;

public class FPFAM101InputVO {
	
	//基金商品清單
	private List fundList;
	
	//點選比較區間的基金
	private String rdgPrdID;

	public List getFundList() {
		return fundList;
	}

	public void setFundList(List fundList) {
		this.fundList = fundList;
	}

	public String getRdgPrdID() {
		return rdgPrdID;
	}

	public void setRdgPrdID(String rdgPrdID) {
		this.rdgPrdID = rdgPrdID;
	}
}
