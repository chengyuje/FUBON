package com.systex.jbranch.app.server.fps.chjlb001;

public class CHJLB001InputVO {

	public CHJLB001InputVO() {
	}

	// public Long txnSEQ; // 交易序號
	public String txnID; // 交易序號
	public String custID; // 客戶ID
	public String custName; // 客戶名稱
	public String branchID; // 分行代碼
	public String rmCode; // 員工代號
	public String actSou; // 互動來源

	public String getTxnID() {
		return txnID;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}

	public String actItem; // 互動項目
	public String subItem; // 追蹤項目(行銷活動名稱)
	public String purpose; // 互動目的
	public String status; // 互動狀態
	public String description; // 互動說明
	public String souDate; // 資料來源日期--YYYY/MM/DD
	// ++++ 2012/12/17 by jiangfei for TBSFA_SAM_CUST_INTERACT changed.
	public String isTrace; // 是否須追蹤
	public String isTCBCust; // 是否為本行客戶

	// ---- 2012/12/17 by jiangfei for TBSFA_SAM_CUST_INTERACT changed.
	// public Long getTxnSEQ() {
	// return txnSEQ;
	// }
	public String getIsTrace() {
		return isTrace;
	}

	public void setIsTrace(String isTrace) {
		this.isTrace = isTrace;
	}

	public String getIsTCBCust() {
		return isTCBCust;
	}

	public void setIsTCBCust(String isTCBCust) {
		this.isTCBCust = isTCBCust;
	}

	// public void setTxnSEQ(Long txnSEQ) {
	// this.txnSEQ = txnSEQ;
	// }
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

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public String getRmCode() {
		return rmCode;
	}

	public void setRmCode(String rmCode) {
		this.rmCode = rmCode;
	}

	public String getActSou() {
		return actSou;
	}

	public void setActSou(String actSou) {
		this.actSou = actSou;
	}

	public String getActItem() {
		return actItem;
	}

	public void setActItem(String actItem) {
		this.actItem = actItem;
	}

	public String getSubItem() {
		return subItem;
	}

	public void setSubItem(String subItem) {
		this.subItem = subItem;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSouDate() {
		return souDate;
	}

	public void setSouDate(String souDate) {
		this.souDate = souDate;
	}
}
