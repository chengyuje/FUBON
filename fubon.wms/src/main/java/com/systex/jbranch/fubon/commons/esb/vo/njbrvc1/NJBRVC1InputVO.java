package com.systex.jbranch.fubon.commons.esb.vo.njbrvc1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 長效單查詢上行電文
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVC1InputVO {
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
