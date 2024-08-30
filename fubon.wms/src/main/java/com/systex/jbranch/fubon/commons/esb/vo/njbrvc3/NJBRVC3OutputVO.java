package com.systex.jbranch.fubon.commons.esb.vo.njbrvc3;

import java.util.List;

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
public class NJBRVC3OutputVO {

	@XmlElement
	private String SPRefId; // 傳送序號
	@XmlElement
	private String CustId; // 身份証ID
	@XmlElement
	private String TxnType1; // 交易類別 B:申購 S:贖回
	@XmlElement
	private String BondNo; // 債券代號
	@XmlElement
	private String StartDate; // 交易起日
	@XmlElement
	private String EndDate; // 交易迄日
	@XmlElement
	private String Occur; // 筆數
	@XmlElement(name = "TxRepeat")
    private List<NJBRVC3OutputDetailsVO> details;
	public String getSPRefId() {
		return SPRefId;
	}
	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
	}
	public String getCustId() {
		return CustId;
	}
	public void setCustId(String custId) {
		CustId = custId;
	}
	public String getTxnType1() {
		return TxnType1;
	}
	public void setTxnType1(String txnType1) {
		TxnType1 = txnType1;
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
	public String getOccur() {
		return Occur;
	}
	public void setOccur(String occur) {
		Occur = occur;
	}
	public List<NJBRVC3OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<NJBRVC3OutputDetailsVO> details) {
		this.details = details;
	}
	
	

}
