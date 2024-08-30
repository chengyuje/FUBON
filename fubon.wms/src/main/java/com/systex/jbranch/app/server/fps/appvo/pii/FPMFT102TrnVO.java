package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FPMFT102TrnVO  implements Serializable{
	private String prdType;	//資產類別
	private String trnOutPrdID;	//基金集團代碼
	private List<Map<String,Object>> prdList;	//符合條件的商品清單
	
	public String getPrdType() {
		return prdType;
	}
	public void setPrdType(String prdType) {
		this.prdType = prdType;
	}
	public String getTrnOutPrdID() {
		return trnOutPrdID;
	}
	public void setTrnOutPrdID(String trnOutPrdID) {
		this.trnOutPrdID = trnOutPrdID;
	}
	public List<Map<String, Object>> getPrdList() {
		return prdList;
	}
	public void setPrdList(List<Map<String, Object>> prdList) {
		this.prdList = prdList;
	}
}
