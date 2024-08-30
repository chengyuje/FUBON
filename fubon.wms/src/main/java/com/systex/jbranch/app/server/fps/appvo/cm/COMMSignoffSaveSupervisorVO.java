package com.systex.jbranch.app.server.fps.appvo.cm;

public class COMMSignoffSaveSupervisorVO {
	private String txnID;//模組代號
	private String seqNO;//版次序號
	private String supervisorID;//主管ID
	private String supervisorName;//主管姓名
	private String supervisorBank;//主管分行
	private String empType;//員工角色
	private String remark;//簽核意見
	
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
	public String getSupervisorID() {
		return supervisorID;
	}
	public void setSupervisorID(String supervisorID) {
		this.supervisorID = supervisorID;
	}
	public String getSupervisorName() {
		return supervisorName;
	}
	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}
	public String getSupervisorBank() {
		return supervisorBank;
	}
	public void setSupervisorBank(String supervisorBank) {
		this.supervisorBank = supervisorBank;
	}
	public String getEmpType() {
		return empType;
	}
	public void setEmpType(String empType) {
		this.empType = empType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}