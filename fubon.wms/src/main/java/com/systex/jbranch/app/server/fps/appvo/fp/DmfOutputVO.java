package com.systex.jbranch.app.server.fps.appvo.fp;

import java.io.Serializable;
import java.util.List;

public class DmfOutputVO implements Serializable {
	
	//動態鎖利系列母子公司清單
	private List dmfList;
	private List dmfPrdList;

	public List getDmfPrdList() {
		return dmfPrdList;
	}

	public void setDmfPrdList(List dmfPrdList) {
		this.dmfPrdList = dmfPrdList;
	}

	public List getDmfList() {
		return dmfList;
	}

	public void setDmfList(List dmfList) {
		this.dmfList = dmfList;
	}
}