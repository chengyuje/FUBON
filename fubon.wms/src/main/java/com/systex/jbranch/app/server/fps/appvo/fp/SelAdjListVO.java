package com.systex.jbranch.app.server.fps.appvo.fp;

import java.util.List;


public class SelAdjListVO {
	private List mlist; 		//調整內容清單
	private List bfASRtoList; 	//調整前資產類別配置比清單(最多8筆資料)
	private List afASRtoList; 	//調整後資產類別配置比清單(最多8筆資料)
	private List bfSBRtoList; 	//調整前資產股債比清單(最多就3筆資料)
	private List afSBRtoList; 	//調整前資產股債比清單(最多就3筆資料)
	private List bfMKRtoList; 	//調整前資產市場比清單
	private List afMKRtoList; 	//調整後資產市場比清單
	private List bfRKRtoList; 	//調整前風險屬性比清單(最多就5筆資料)
	private List afRKRtoList; 	//調整後風險屬性比清單(最多就5筆資料)
	private List bfCURtoList; 	//調整前幣別配置比清單
	private List afCURtoList; 	//調整後幣別配置比清單
	
	public List getMlist() {
		return mlist;
	}
	public void setMlist(List mlist) {
		this.mlist = mlist;
	}
	public List getBfASRtoList() {
		return bfASRtoList;
	}
	public void setBfASRtoList(List bfASRtoList) {
		this.bfASRtoList = bfASRtoList;
	}
	public List getAfASRtoList() {
		return afASRtoList;
	}
	public void setAfASRtoList(List afASRtoList) {
		this.afASRtoList = afASRtoList;
	}
	public List getBfSBRtoList() {
		return bfSBRtoList;
	}
	public void setBfSBRtoList(List bfSBRtoList) {
		this.bfSBRtoList = bfSBRtoList;
	}
	public List getAfSBRtoList() {
		return afSBRtoList;
	}
	public void setAfSBRtoList(List afSBRtoList) {
		this.afSBRtoList = afSBRtoList;
	}
	public List getBfMKRtoList() {
		return bfMKRtoList;
	}
	public void setBfMKRtoList(List bfMKRtoList) {
		this.bfMKRtoList = bfMKRtoList;
	}
	public List getAfMKRtoList() {
		return afMKRtoList;
	}
	public void setAfMKRtoList(List afMKRtoList) {
		this.afMKRtoList = afMKRtoList;
	}
	public List getBfRKRtoList() {
		return bfRKRtoList;
	}
	public void setBfRKRtoList(List bfRKRtoList) {
		this.bfRKRtoList = bfRKRtoList;
	}
	public List getAfRKRtoList() {
		return afRKRtoList;
	}
	public void setAfRKRtoList(List afRKRtoList) {
		this.afRKRtoList = afRKRtoList;
	}
	public List getBfCURtoList() {
		return bfCURtoList;
	}
	public void setBfCURtoList(List bfCURtoList) {
		this.bfCURtoList = bfCURtoList;
	}
	public List getAfCURtoList() {
		return afCURtoList;
	}
	public void setAfCURtoList(List afCURtoList) {
		this.afCURtoList = afCURtoList;
	}

}
