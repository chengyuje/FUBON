package com.systex.jbranch.fubon.commons.esb.vo.nfbrx9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRX9InputVO {
	
	@XmlElement
	private String CustId;

	@XmlElement
	private String DebitACCT;

	public String getCustId() {
		return CustId;
	}

	public void setCustId(String custId) {
		CustId = custId;
	}

	public String getDebitACCT() {
		return DebitACCT;
	}

	public void setDebitACCT(String debitACCT) {
		DebitACCT = debitACCT;
	}

}