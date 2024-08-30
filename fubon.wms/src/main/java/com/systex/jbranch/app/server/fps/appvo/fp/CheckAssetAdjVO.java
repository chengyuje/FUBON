package com.systex.jbranch.app.server.fps.appvo.fp;

import java.io.Serializable;
import java.util.List;

public class CheckAssetAdjVO implements Serializable{

	private String custID;// 資產調整VO.客戶ID
	private String prodType;// prodType
	private int adjCnt;// 重覆筆數
	private String txnNo;//呼叫的交易代碼
	
	public String getTxnNo() {
		return txnNo;
	}
	
	public void setTxnNo(String txnNo) {
		this.txnNo = txnNo;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public int getAdjCnt() {
		return adjCnt;
	}

	public void setAdjCnt(int adjCnt) {
		this.adjCnt = adjCnt;
	}
}