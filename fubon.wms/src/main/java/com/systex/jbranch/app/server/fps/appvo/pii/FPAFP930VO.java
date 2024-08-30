package com.systex.jbranch.app.server.fps.appvo.pii;

import java.util.List;
import java.io.Serializable;

public class FPAFP930VO implements Serializable{
	public FPAFP930VO(){
		super();
	}

	private List modelPortfolioList;     //Model Portfolio資料
	
	public List getModelPortfolioList() {
		return modelPortfolioList;
	}
	public void setModelPortfolioList(List modelPortfolioList) {
		this.modelPortfolioList = modelPortfolioList;
	}	
	
}
