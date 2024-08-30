package com.systex.jbranch.fubon.commons.esb.vo.nr074n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Rick on 2017/11/02.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NR074NInputVO {
	@XmlElement
	private String AcctId16;
	
	@XmlElement
	private String CustPswd32;
	
	@XmlElement
	private String CustId;
	
	@XmlElement
	private String CurAcctId;
	
	@XmlElement
	private String CurAcctName;

	public String getAcctId16() {
		return AcctId16;
	}

	public void setAcctId16(String acctId16) {
		AcctId16 = acctId16;
	}

	public String getCustPswd32() {
		return CustPswd32;
	}

	public void setCustPswd32(String custPswd32) {
		CustPswd32 = custPswd32;
	}

	public String getCustId() {
		return CustId;
	}

	public void setCustId(String custId) {
		CustId = custId;
	}

	public String getCurAcctId() {
		return CurAcctId;
	}

	public void setCurAcctId(String curAcctId) {
		CurAcctId = curAcctId;
	}

	public String getCurAcctName() {
		return CurAcctName;
	}

	public void setCurAcctName(String curAcctName) {
		CurAcctName = curAcctName;
	}
	
	
}
