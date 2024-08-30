package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class MktPfoQueryInputVO implements Serializable {
	private String dataYM;		//資料年月
	private int queryType; 		//查詢方式
	private String advisorID;	//研究員員編
	
	public String getAdvisorID() {
		return advisorID;
	}
	public void setAdvisorID(String advisorID) {
		this.advisorID = advisorID;
	}
	public String getDataYM() {
		return dataYM;
	}
	public void setDataYM(String dataYM) {
		this.dataYM = dataYM;
	}
	public int getQueryType() {
		return queryType;
	}
	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}
	
}
