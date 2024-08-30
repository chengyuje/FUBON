package com.systex.jbranch.fubon.commons.esb.vo.nr070n;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NR070NInputVO {
	@XmlElement
	private String AcctId16; // 帳號
	@XmlElement
	private String CustPswd32; // 密碼
	@XmlElement
	private String CustId; // 客戶ID
	@XmlElement
	private String CurAcctId; // 使用者代號
	@XmlElement
	private String CurAcctName; // 戶名代號
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
