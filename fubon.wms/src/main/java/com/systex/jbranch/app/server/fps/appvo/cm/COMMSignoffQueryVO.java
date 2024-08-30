package com.systex.jbranch.app.server.fps.appvo.cm;

public class COMMSignoffQueryVO {
	
	private String seqNO;//版次
	private String recordStatus;//狀態
	private String dataType;//簽核 or 檢視
	
	public String getSeqNO() {
		return seqNO;
	}
	public void setSeqNO(String seqNO) {
		this.seqNO = seqNO;
	}
	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
