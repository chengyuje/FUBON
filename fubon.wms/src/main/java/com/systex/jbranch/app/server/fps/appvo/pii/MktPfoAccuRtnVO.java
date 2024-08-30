package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

//研究員市場投資組合歷史累積報酬率走勢資料VO
public class MktPfoAccuRtnVO implements Serializable { 
	private List advisorList; 		//研究員清單

	public List getAdvisorList() {
		return advisorList;
	}

	public void setAdvisorList(List advisorList) {
		this.advisorList = advisorList;
	}
	
}
