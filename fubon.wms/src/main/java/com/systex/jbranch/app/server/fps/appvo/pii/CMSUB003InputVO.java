package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class CMSUB003InputVO implements Serializable{
	public CMSUB003InputVO(){
		super();
	}
	
	private String inCustId; //客戶ID

	public String getInCustId() {
		return inCustId;
	}

	public void setInCustId(String inCustId) {
		this.inCustId = inCustId;
	}


}
