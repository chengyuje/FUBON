package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

public class GetInsPremInputVO {
	private String funcType;
	private String mainPayYear;
	private String mainCovYear;
	private List lstInsData;
	
	public String getFuncType() {
		return funcType;
	}
	public void setFuncType(String funcType) {
		this.funcType = funcType;
	}
	public String getMainPayYear() {
		return mainPayYear;
	}
	public void setMainPayYear(String mainPayYear) {
		this.mainPayYear = mainPayYear;
	}
	public String getMainCovYear() {
		return mainCovYear;
	}
	public void setMainCovYear(String mainCovYear) {
		this.mainCovYear = mainCovYear;
	}
	public List getLstInsData() {
		return lstInsData;
	}
	public void setLstInsData(List lstInsData) {
		this.lstInsData = lstInsData;
	}
	
	
}
