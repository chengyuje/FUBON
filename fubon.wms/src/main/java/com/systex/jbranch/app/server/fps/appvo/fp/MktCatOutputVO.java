package com.systex.jbranch.app.server.fps.appvo.fp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MktCatOutputVO implements Serializable {
	
	//未清楚的list
	private List mktCatList;
	private List<Map<String,Object>> PfoCreateDate;

	public List<Map<String, Object>> getPfoCreateDate() {
		return PfoCreateDate;
	}

	public void setPfoCreateDate(List<Map<String, Object>> pfoCreateDate) {
		PfoCreateDate = pfoCreateDate;
	}

	public List getMktCatList() {
		return mktCatList;
	}

	public void setMktCatList(List mktCatList) {
		this.mktCatList = mktCatList;
	}
}