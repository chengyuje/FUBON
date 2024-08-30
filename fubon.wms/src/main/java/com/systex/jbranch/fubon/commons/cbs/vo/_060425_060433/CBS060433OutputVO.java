package com.systex.jbranch.fubon.commons.cbs.vo._060425_060433;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS060433OutputVO {
	private Boolean isuse = true;

	@XmlElement(name = "TxRepeat")
	private List<CBS060433OutputDetailsVO> details;

	private String filler1;
	private String Action;
	private String CustNo;
	private String filler2;
	private String PageCount1;
	private String RejectedStatus;
	private String filler5;
	private String CustName;
	private String Remarks1;
	private String Remarks2;
	private String filler6;
	private String IDNo;
	private String IDType;
	private String Memo1;
	private String Memo2;
	private String SubAccountInfo;
	private String SubAcctNo1;
	private String SubAccountInfo2;
	private String SubAcctNo2;
	private String NotifyDate1;
	private String NotifyDate2;

	
	public List<CBS060433OutputDetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<CBS060433OutputDetailsVO> details) {
		this.details = details;
	}

	public String getFiller1() {
		return filler1;
	}

	public void setFiller1(String filler1) {
		this.filler1 = filler1;
	}

	public String getAction() {
		return Action;
	}

	public void setAction(String action) {
		Action = action;
	}

	public String getCustNo() {
		return CustNo;
	}

	public void setCustNo(String custNo) {
		CustNo = custNo;
	}

	public String getFiller2() {
		return filler2;
	}

	public void setFiller2(String filler2) {
		this.filler2 = filler2;
	}

	public String getPageCount1() {
		return PageCount1;
	}

	public void setPageCount1(String pageCount1) {
		PageCount1 = pageCount1;
	}

	public String getRejectedStatus() {
		return RejectedStatus;
	}

	public void setRejectedStatus(String rejectedStatus) {
		RejectedStatus = rejectedStatus;
	}

	public String getFiller5() {
		return filler5;
	}

	public void setFiller5(String filler5) {
		this.filler5 = filler5;
	}

	public String getCustName() {
		return CustName;
	}

	public void setCustName(String custName) {
		CustName = custName;
	}

	public String getRemarks1() {
		return Remarks1;
	}

	public void setRemarks1(String remarks1) {
		Remarks1 = remarks1;
	}

	public String getRemarks2() {
		return Remarks2;
	}

	public void setRemarks2(String remarks2) {
		Remarks2 = remarks2;
	}

	public String getFiller6() {
		return filler6;
	}

	public void setFiller6(String filler6) {
		this.filler6 = filler6;
	}

	public String getIDNo() {
		return IDNo;
	}

	public void setIDNo(String iDNo) {
		IDNo = iDNo;
	}

	public String getIDType() {
		return IDType;
	}

	public void setIDType(String iDType) {
		IDType = iDType;
	}

	public String getMemo1() {
		return Memo1;
	}

	public void setMemo1(String memo1) {
		Memo1 = memo1;
	}

	public String getMemo2() {
		return Memo2;
	}

	public void setMemo2(String memo2) {
		Memo2 = memo2;
	}

	public String getSubAccountInfo() {
		return SubAccountInfo;
	}

	public void setSubAccountInfo(String subAccountInfo) {
		SubAccountInfo = subAccountInfo;
	}

	public String getSubAcctNo1() {
		return SubAcctNo1;
	}

	public void setSubAcctNo1(String subAcctNo1) {
		SubAcctNo1 = subAcctNo1;
	}

	public String getSubAccountInfo2() {
		return SubAccountInfo2;
	}

	public void setSubAccountInfo2(String subAccountInfo2) {
		SubAccountInfo2 = subAccountInfo2;
	}

	public String getSubAcctNo2() {
		return SubAcctNo2;
	}

	public void setSubAcctNo2(String subAcctNo2) {
		SubAcctNo2 = subAcctNo2;
	}

	public String getNotifyDate1() {
		return NotifyDate1;
	}

	public void setNotifyDate1(String notifyDate1) {
		NotifyDate1 = notifyDate1;
	}

	public String getNotifyDate2() {
		return NotifyDate2;
	}

	public void setNotifyDate2(String notifyDate2) {
		NotifyDate2 = notifyDate2;
	}

	public Boolean getIsuse() {
		return isuse;
	}

	public void setIsuse(Boolean isuse) {
		this.isuse = isuse;
	}

}
