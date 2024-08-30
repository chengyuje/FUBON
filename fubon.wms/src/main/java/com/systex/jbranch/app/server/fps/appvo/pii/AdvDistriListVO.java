package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

public class AdvDistriListVO implements Serializable {
	
	private List allocList; 		//研究員配比清單

	public List getAllocList() {
		return allocList;
	}

	public void setAllocList(List allocList) {
		this.allocList = allocList;
	}
	
}
