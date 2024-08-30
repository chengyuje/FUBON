package com.systex.jbranch.fubon.commons.esb.vo.njbrvc3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * SOT330 海外債成交結果查詢
 * 
 * @author SamTu
 * @date 2020.11.05
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVC3InputVO {

	@XmlElement
	private String CustId; // 身份証ID
	@XmlElement
	private String TxnType; // 交易類別 B:申購 S:贖回
	@XmlElement
	private String BondNo; // 債券代號
	@XmlElement
	private String StartDate; // 交易起日
	@XmlElement
	private String EndDate; // 交易迄日
	public String getCustId() {
		return CustId;
	}
	public void setCustId(String custId) {
		CustId = custId;
	}
	public String getTxnType() {
		return TxnType;
	}
	public void setTxnType(String txnType) {
		TxnType = txnType;
	}
	public String getBondNo() {
		return BondNo;
	}
	public void setBondNo(String bondNo) {
		BondNo = bondNo;
	}
	public String getStartDate() {
		return StartDate;
	}
	public void setStartDate(String startDate) {
		StartDate = startDate;
	}
	public String getEndDate() {
		return EndDate;
	}
	public void setEndDate(String endDate) {
		EndDate = endDate;
	}
	
	

}
