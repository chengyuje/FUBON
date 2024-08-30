package com.systex.jbranch.fubon.commons.esb.vo.nfei001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFEI001InputVO {
	
	
	@XmlElement
	private String CustId; //身份証ID

	public String getCustId() {
		return CustId;
	}

	public void setCustId(String custId) {
		CustId = custId;
	}
	

	
	

}
