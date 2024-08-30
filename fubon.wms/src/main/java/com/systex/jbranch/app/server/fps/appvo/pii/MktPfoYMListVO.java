package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

public class MktPfoYMListVO implements Serializable { 
	private List ymList; 		//資料年月清單

	public List getYmList() {
		return ymList;
	}

	public void setYmList(List ymList) {
		this.ymList = ymList;
	}
	
}
