package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

//研究員清單輸出資料VO
public class AdvisorListVO implements Serializable {
	
	private List advisorList; 		//研究員員編

	public List getAdvisorList() {
		return advisorList;
	}

	public void setAdvisorList(List advisorList) {
		this.advisorList = advisorList;
	}
	
}
