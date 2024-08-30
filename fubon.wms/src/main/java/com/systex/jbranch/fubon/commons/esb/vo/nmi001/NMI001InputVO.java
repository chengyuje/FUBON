package com.systex.jbranch.fubon.commons.esb.vo.nmi001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMI001InputVO {
    @XmlElement
	private String CustId;			// 客戶ID
    
    @XmlElement
    private String CurAcctName;		// 歸戶代碼

	public String getCustId() {
		return CustId;
	}

	public void setCustId(String custId) {
		CustId = custId;
	}

	public String getCurAcctName() {
		return CurAcctName;
	}

	public void setCurAcctName(String curAcctName) {
		CurAcctName = curAcctName;
	}
    
}
