package com.systex.jbranch.fubon.commons.esb.vo.njbrvc1;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.systex.jbranch.fubon.commons.esb.vo.njbrvb1.NJBRVB1OutputVODetials;

/**
 * 
 * 長效單查詢下行電文
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVC1OutputVO {
	@XmlElement
    private String SPRefId;   //傳送序號
	@XmlElement
    private String CustId;   //身分證ID
    @XmlElement
    private String TxnType;   //交易類別 B:申購 S:贖回
    @XmlElement
    private String BondNo; //債券代號
    @XmlElement
    private String StartDate;    //交易起日
    @XmlElement
    private String EndDate;    //交易迄日
    @XmlElement
    private String Occur;   //筆數
    @XmlElement(name = "TxRepeat")
    private List<NJBRVC1OutputVODetails> details;
    
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
	public String getOccur() {
		return Occur;
	}
	public void setOccur(String occur) {
		Occur = occur;
	}
	public List<NJBRVC1OutputVODetails> getDetails() {
		return details;
	}
	public void setDetails(List<NJBRVC1OutputVODetails> details) {
		this.details = details;
	}   
    
}
