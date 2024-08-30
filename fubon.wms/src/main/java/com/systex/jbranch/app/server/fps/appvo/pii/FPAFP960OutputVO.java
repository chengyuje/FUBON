package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

public class FPAFP960OutputVO implements Serializable{

	private List<FPAFP960DataVO> investRiskList;

	public FPAFP960OutputVO() {
		super();
	}
	public List<FPAFP960DataVO> getInvestRiskList() {
		return investRiskList;
	}
	public void setInvestRiskList(List<FPAFP960DataVO> investRiskList) {
		this.investRiskList = investRiskList;
	}
}
