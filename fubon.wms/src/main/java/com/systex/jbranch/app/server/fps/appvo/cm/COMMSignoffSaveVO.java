package com.systex.jbranch.app.server.fps.appvo.cm;

public class COMMSignoffSaveVO {
	private String funcType;//執行動作
	private String txnID;	//模組代號
	private String seqNO;	//版次序號
	private String txnType; //參數類型
	private String empID;	//經辦ID
	private String empName;	//經辦姓名
	private String recordStatus;//狀態
	private	COMMSignoffSaveSupervisorVO commSignoffSaveSupervisorVO; //簽核主管資料

	public String getFuncType() {
		return funcType;
	}
	public void setFuncType(String funcType) {
		this.funcType = funcType;
	}
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
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getEmpID() {
		return empID;
	}
	public void setEmpID(String empID) {
		this.empID = empID;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	public COMMSignoffSaveSupervisorVO getCommSignoffSaveSupervisorVO() {
		return commSignoffSaveSupervisorVO;
	}
	public void setCommSignoffSaveSupervisorVO(
			COMMSignoffSaveSupervisorVO commSignoffSaveSupervisorVO) {
		this.commSignoffSaveSupervisorVO = commSignoffSaveSupervisorVO;
	}
}
