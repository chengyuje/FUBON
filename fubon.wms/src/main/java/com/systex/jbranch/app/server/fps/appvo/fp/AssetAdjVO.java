package com.systex.jbranch.app.server.fps.appvo.fp;

import java.io.Serializable;
import java.util.List;

public class AssetAdjVO implements Serializable{

	private String custID;// 資產調整VO.客戶ID
	private String calSeq;// 資產調整VO.試算流水號
	private List adjList;// 資產調整VO.調整清單
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

	public String getCalSeq() {
		return calSeq;
	}

	public void setCalSeq(String calSeq) {
		this.calSeq = calSeq;
	}

	public List getAdjList() {
		return adjList;
	}

	public void setAdjList(List adjList) {
		this.adjList = adjList;
	}
}