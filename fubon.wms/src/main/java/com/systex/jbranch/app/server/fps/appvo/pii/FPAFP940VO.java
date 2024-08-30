package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

public class FPAFP940VO implements Serializable{
	public FPAFP940VO(){
		super();
	}
	
	private List mkReportList;  //41類市場報告列表

	public List getMkReportList() {
		return mkReportList;
	}
	public void setMkReportList(List mkReportList) {
		this.mkReportList = mkReportList;
	}

}
