package com.systex.jbranch.app.server.fps.iot200;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT200InputVO extends PagingInputVO{	
	private Date sApplyDate;   //申請日起日
	private Date eApplyDate;   //申請日迄日
	private String areaID;     //營運區ID
	private String branchID;   //分行ID
	private String custID;     //要保人ID
	private String caseID;     //案件編號
	private String insuredID;  //被保人ID
	private String status;	   //案件狀態
	private String insPrdID;   //險種代碼
	private String fileName;   
	private String realfileName; 
	
	public Date getsApplyDate() {
		return sApplyDate;
	}
	public void setsApplyDate(Date sApplyDate) {
		this.sApplyDate = sApplyDate;
	}
	public Date geteApplyDate() {
		return eApplyDate;
	}
	public void seteApplyDate(Date eApplyDate) {
		this.eApplyDate = eApplyDate;
	}
	public String getAreaID() {
		return areaID;
	}
	public void setAreaID(String areaID) {
		this.areaID = areaID;
	}
	public String getBranchID() {
		return branchID;
	}
	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCaseID() {
		return caseID;
	}
	public void setCaseID(String caseID) {
		this.caseID = caseID;
	}
	public String getInsuredID() {
		return insuredID;
	}
	public void setInsuredID(String insuredID) {
		this.insuredID = insuredID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getInsPrdID() {
		return insPrdID;
	}
	public void setInsPrdID(String insPrdID) {
		this.insPrdID = insPrdID;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRealfileName() {
		return realfileName;
	}
	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}
}
