package com.systex.jbranch.app.server.fps.appvo.pii;

import java.util.List;
import java.io.Serializable;

public class FPAFP980VO implements Serializable{
	public FPAFP980VO(){
		super();
	}

	private List marketRankList;     //市場評等與占比業務參數VO

	public List getMarketRankList() {
		return marketRankList;
	}

	public void setMarketRankList(List marketRankList) {
		this.marketRankList = marketRankList;
	}
}
