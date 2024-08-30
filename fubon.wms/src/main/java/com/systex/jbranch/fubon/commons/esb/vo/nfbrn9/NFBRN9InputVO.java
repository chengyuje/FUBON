package com.systex.jbranch.fubon.commons.esb.vo.nfbrn9;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/10/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRN9InputVO {
    @XmlElement
	private String CustId;

	public String getCustId() {
		return CustId;
	}
	public void setCustId(String custId) {
		CustId = custId;
	}
}
