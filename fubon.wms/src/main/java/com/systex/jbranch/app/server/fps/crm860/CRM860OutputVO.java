package com.systex.jbranch.app.server.fps.crm860;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM860OutputVO extends PagingOutputVO {
	private List prdList;
	private List curList;
	private List divList;
	
	
	public List getPrdList() {
		return prdList;
	}
	public void setPrdList(List prdList) {
		this.prdList = prdList;
	}
	public List getCurList() {
		return curList;
	}
	public void setCurList(List curList) {
		this.curList = curList;
	}
	public List getDivList() {
		return divList;
	}
	public void setDivList(List divList) {
		this.divList = divList;
	}
}