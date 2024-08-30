package com.systex.jbranch.app.server.fps.appvo.fp;

import java.util.List;


public class SelAssetFundAllVO {
	private List bmList; 		//正向市場明細資料清單
	private List bdList; 		//正向市場資產明細資料清單
	private List smList; 		//負向市場明細資料清單
	private List sdList; 		//負向市場資產明細資料清單
	
	public List getBmList() {
		return bmList;
	}
	public void setBmList(List bmList) {
		this.bmList = bmList;
	}
	public List getBdList() {
		return bdList;
	}
	public void setBdList(List bdList) {
		this.bdList = bdList;
	}
	public List getSmList() {
		return smList;
	}
	public void setSmList(List smList) {
		this.smList = smList;
	}
	public List getSdList() {
		return sdList;
	}
	public void setSdList(List sdList) {
		this.sdList = sdList;
	}
}
