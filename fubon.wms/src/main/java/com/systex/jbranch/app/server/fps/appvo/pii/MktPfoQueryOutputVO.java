package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

public class MktPfoQueryOutputVO implements Serializable { 
	private List mktPfoList; 		//市場投資組合清單	

	public List getMktPfoList() {
		return mktPfoList;
	}

	public void setMktPfoList(List mktPfoList) {
		this.mktPfoList = mktPfoList;
	}
	
}
