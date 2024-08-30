package com.systex.jbranch.fubon.commons.esb.vo.nmp8yb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by CathyTang on 2018/12/20.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMP8YBInputVO {
    @XmlElement
	private String CustId;
    @XmlElement
    private String CurAcctName;
    @XmlElement
    private String FUNC;

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
	public String getFUNC() {
		return FUNC;
	}
	public void setFUNC(String fUNC) {
		FUNC = fUNC;
	}
	
}
