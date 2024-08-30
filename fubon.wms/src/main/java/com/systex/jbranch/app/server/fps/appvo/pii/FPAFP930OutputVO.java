package com.systex.jbranch.app.server.fps.appvo.pii;

import java.util.List;
import java.io.Serializable;

public class FPAFP930OutputVO implements Serializable{
	public FPAFP930OutputVO(){
		super();
	}
	
	private List<AFP930SuggVO> AFP930SuggList;  //市場建議列表

	public List<AFP930SuggVO> getAFP930SuggList() {
		return AFP930SuggList;
	}
	public void setAFP930SuggList(List<AFP930SuggVO> suggList) {
		AFP930SuggList = suggList;
	}

}
