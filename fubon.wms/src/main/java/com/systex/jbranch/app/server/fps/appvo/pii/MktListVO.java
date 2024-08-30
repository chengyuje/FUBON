package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MktListVO implements Serializable { 
	private List mktCatList; 		//市場分類清單
	private List mktIdxList; 		//市場指數清單
	private List mktIdxCatList; 	//市場指數分類清單
	private List<Map<String,Object>> prodMKTlist;
	
	
	public List<Map<String, Object>> getProdMKTlist() {
		return prodMKTlist;
	}
	public void setProdMKTlist(List<Map<String, Object>> prodMKTlist) {
		this.prodMKTlist = prodMKTlist;
	}
	public List getMktCatList() {
		return mktCatList;
	}
	public void setMktCatList(List mktCatList) {
		this.mktCatList = mktCatList;
	}
	public List getMktIdxList() {
		return mktIdxList;
	}
	public void setMktIdxList(List mktIdxList) {
		this.mktIdxList = mktIdxList;
	}
	public List getMktIdxCatList() {
		return mktIdxCatList;
	}
	public void setMktIdxCatList(List mktIdxCatList) {
		this.mktIdxCatList = mktIdxCatList;
	}	
}
