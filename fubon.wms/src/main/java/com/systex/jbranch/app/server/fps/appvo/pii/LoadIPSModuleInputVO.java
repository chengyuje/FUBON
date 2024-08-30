package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class LoadIPSModuleInputVO implements Serializable {

	private String action;		//功能
	private String ipsNo;		//投資意向書編號
	private IPSDataVO ipsData;	//預覽時若還未存檔，則傳入VO；IPS資料不由DB讀取
	private String custid;	//客戶基本資料
	private String transactionID;//交易代號
	private String transactionName;//交易名稱
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getIpsNo() {
		return ipsNo;
	}
	public void setIpsNo(String ipsNo) {
		this.ipsNo = ipsNo;
	}
	public IPSDataVO getIpsData() {
		return ipsData;
	}
	public void setIpsData(IPSDataVO ipsData) {
		this.ipsData = ipsData;
	}
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getTransactionName() {
		return transactionName;
	}
	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
	
	
}
