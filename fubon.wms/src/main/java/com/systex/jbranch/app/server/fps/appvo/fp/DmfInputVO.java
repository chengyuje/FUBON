package com.systex.jbranch.app.server.fps.appvo.fp;

import java.io.Serializable;

public class DmfInputVO implements Serializable {
	
	//動態鎖利系列公司編號
	private String dmfPrdID;
	private String isAvaiable;
	private String txnid;//父模組ID
	
	
	public String getTxnid() {
		return txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	public String getIsAvaiable() {
		return isAvaiable;
	}

	public void setIsAvaiable(String isAvaiable) {
		this.isAvaiable = isAvaiable;
	}

	public String getDmfPrdID() {
		return dmfPrdID;
	}

	public void setDmfPrdID(String dmfPrdID) {
		this.dmfPrdID = dmfPrdID;
	}
}