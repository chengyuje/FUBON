package com.systex.jbranch.app.server.fps.appvo.cm;

public class COMMSignoffOpinionQueryVO {
	private String txnID;//模組代號
	private String seqNO;//版次序號
	private String dataType;//狀態 D: 一般瀏覽狀態  S: 主管簽核狀態
	public String getTxnID() {
		return txnID;
	}
	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}
	public String getSeqNO() {
		return seqNO;
	}
	public void setSeqNO(String seqNO) {
		this.seqNO = seqNO;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
}
