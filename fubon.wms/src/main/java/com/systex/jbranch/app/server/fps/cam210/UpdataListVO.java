package com.systex.jbranch.app.server.fps.cam210;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class UpdataListVO extends PagingInputVO {

	private String aoList;
	private String cust_id;
	private String branch_Name;
	private String branch_nbr;
	private String toDoList;
	private String reason;
	private String otherReason;

	public String getAoList() {
		return aoList;
	}

	public void setAoList(String aoList) {
		this.aoList = aoList;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getBranch_Name() {
		return branch_Name;
	}

	public void setBranch_Name(String branch_Name) {
		this.branch_Name = branch_Name;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public String getToDoList() {
		return toDoList;
	}

	public void setToDoList(String toDoList) {
		this.toDoList = toDoList;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getOtherReason() {
		return otherReason;
	}

	public void setOtherReason(String otherReason) {
		this.otherReason = otherReason;
	}
}
