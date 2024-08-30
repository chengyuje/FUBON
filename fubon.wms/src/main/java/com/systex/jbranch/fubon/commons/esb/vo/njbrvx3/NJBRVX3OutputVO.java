package com.systex.jbranch.fubon.commons.esb.vo.njbrvx3;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVX3OutputVO {
	
	@XmlElement
	private String SPRefId;

	@XmlElement
	private String CustId;

	@XmlElement
	private String DebitACCT;

	@XmlElement
	private String Occur;

	@XmlElement(name="TxRepeat")
	private List<NJBRVX3OutputVODetails> details;

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

	public String getDebitACCT() {
		return DebitACCT;
	}

	public void setDebitACCT(String debitACCT) {
		DebitACCT = debitACCT;
	}

	public String getOccur() {
		return Occur;
	}

	public void setOccur(String occur) {
		Occur = occur;
	}

	public List<NJBRVX3OutputVODetails> getDetails() {
		return details;
	}

	public void setDetails(List<NJBRVX3OutputVODetails> details) {
		this.details = details;
	}
	
}