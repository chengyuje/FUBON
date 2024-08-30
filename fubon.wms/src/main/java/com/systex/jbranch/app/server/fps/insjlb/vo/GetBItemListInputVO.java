package com.systex.jbranch.app.server.fps.insjlb.vo;

public class GetBItemListInputVO {

	public GetBItemListInputVO() {
		super();
	}

	private String insCustID;	// 客戶ID
	private String yearROC;		// 民國年(如果為空，則預設為當年)

	public String getInsCustID() {
		return insCustID;
	}
	public void setInsCustID(String insCustID) {
		this.insCustID = insCustID;
	}
	public String getYearROC() {
		return yearROC;
	}
	public void setYearROC(String yearROC) {
		this.yearROC = yearROC;
	}
}
