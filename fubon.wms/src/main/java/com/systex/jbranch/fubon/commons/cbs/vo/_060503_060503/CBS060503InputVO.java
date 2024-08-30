package com.systex.jbranch.fubon.commons.cbs.vo._060503_060503;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS060503InputVO {

	private String email_addr; //電子郵件地址
	private String startfr; //開始
	
	public String getEmail_addr() {
		return email_addr;
	}
	public void setEmail_addr(String email_addr) {
		this.email_addr = email_addr;
	}
	public String getStartfr() {
		return startfr;
	}
	public void setStartfr(String startfr) {
		this.startfr = startfr;
	}

	

}
