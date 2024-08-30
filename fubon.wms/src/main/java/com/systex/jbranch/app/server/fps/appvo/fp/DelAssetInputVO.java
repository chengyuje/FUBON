package com.systex.jbranch.app.server.fps.appvo.fp;

import java.io.Serializable;

public class DelAssetInputVO implements Serializable{

	private String custID;// 資產調整VO.客戶ID
	private String ptype;// 資產調整VO.資產類別
	private String txnNo;// 資產調整VO.信託帳戶
	private String prdID;// 資產調整VO.產品代碼
	private String txnid;//呼叫的交易代號
	
	public String getTxnid() {
		return txnid;
	}
	
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	
	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	
	public String getTxnNo() {
		return txnNo;
	}

	public void setTxnNo(String txnNo) {
		this.txnNo = txnNo;
	}

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}

}