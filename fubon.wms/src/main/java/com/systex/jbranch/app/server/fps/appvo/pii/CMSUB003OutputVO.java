package com.systex.jbranch.app.server.fps.appvo.pii;

import java.util.List;
import java.io.Serializable;

public class CMSUB003OutputVO implements Serializable{
	public CMSUB003OutputVO(){
		super();
	}
	
	private List custList;  //客戶基本資料清單

	private String specialCust;//是否為特殊客戶
	
	public List getCustList() {
		return custList;
	}

	public void setCustList(List custList) {
		this.custList = custList;
	}
	
	public String getSpecialCust() {
		return specialCust;
	}

	public void setSpecialCust(String specialCust) {
		this.specialCust = specialCust;
	}

	

}
