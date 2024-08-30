package com.systex.jbranch.app.server.fps.cus160;

import java.sql.Timestamp;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CUS160InputVO extends PagingInputVO{
	
	private String custID;
	private String custName;
	private Timestamp sDate;
	private String taskSource;
	private String taskTitle;
	private String taskMemo;
	private String sHour;
	private String sMinute;
	private String eHour;
	private String eMinute;
	
	private String branchID;
	private String status; //未過期:01 已過期:02
	private String taskStatus;
	private Timestamp eDate;
	private String empID;
	private String aoCode;
	
	public String getAoCode() {
		return aoCode;
	}

	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public Timestamp geteDate() {
		return eDate;
	}

	public void seteDate(Timestamp eDate) {
		this.eDate = eDate;
	}

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Timestamp getsDate() {
		return sDate;
	}

	public void setsDate(Timestamp sDate) {
		this.sDate = sDate;
	}

	public String getTaskSource() {
		return taskSource;
	}

	public void setTaskSource(String taskSource) {
		this.taskSource = taskSource;
	}

	public String getCustID() {
		return custID;
	}
	
	public void setCustID(String custID) {
		this.custID = custID;
	}
	
	public String getCustName() {
		return custName;
	}
	
	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public String getTaskTitle() {
		return taskTitle;
	}
	
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
	
	public String getTaskMemo() {
		return taskMemo;
	}
	
	public void setTaskMemo(String taskMemo) {
		this.taskMemo = taskMemo;
	}
	
	public String getsHour() {
		return sHour;
	}
	
	public void setsHour(String sHour) {
		this.sHour = sHour;
	}
	
	public String getsMinute() {
		return sMinute;
	}
	
	public void setsMinute(String sMinute) {
		this.sMinute = sMinute;
	}
	
	public String geteHour() {
		return eHour;
	}
	
	public void seteHour(String eHour) {
		this.eHour = eHour;
	}
	
	public String geteMinute() {
		return eMinute;
	}
	
	public void seteMinute(String eMinute) {
		this.eMinute = eMinute;
	}
}
