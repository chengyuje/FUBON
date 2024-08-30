package com.systex.jbranch.app.server.fps.sot701;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FC032153DataVO {
	
	private String custID;
	private String url;
	private String change;
	private String Type;
	private String CUST_ADDR_1; //通訊地址
	private String EMAIL_ADDR;	//電子郵件
	private List<Map<String, Object>> befData;
	private List<Map<String, Object>> aftData;
	private String errmsg;
	private String CUST_DATA;//上送TP032675客戶基本資料
	private HashMap phoneData;//電話資料

	
	public HashMap getPhoneData() {
		return phoneData;
	}
	public void setPhoneData(HashMap phoneData) {
		this.phoneData = phoneData;
	}
	public String getCUST_DATA() {
		return CUST_DATA;
	}
	public void setCUST_DATA(String cUST_DATA) {
		CUST_DATA = cUST_DATA;
	}	
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getCUST_ADDR_1() {
		return CUST_ADDR_1;
	}
	public String getEMAIL_ADDR() {
		return EMAIL_ADDR;
	}
	public void setCUST_ADDR_1(String cUST_ADDR_1) {
		CUST_ADDR_1 = cUST_ADDR_1;
	}
	public void setEMAIL_ADDR(String eMAIL_ADDR) {
		EMAIL_ADDR = eMAIL_ADDR;
	}
	public String getCustID() {
		return custID;
	}
	public String getUrl() {
		return url;
	}
	public String getChange() {
		return change;
	}
	public List<Map<String, Object>> getBefData() {
		return befData;
	}
	public List<Map<String, Object>> getAftData() {
		return aftData;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setChange(String change) {
		this.change = change;
	}
	public void setBefData(List<Map<String, Object>> befData) {
		this.befData = befData;
	}
	public void setAftData(List<Map<String, Object>> aftData) {
		this.aftData = aftData;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
	
	
	
}
