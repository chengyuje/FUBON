package com.systex.jbranch.fubon.commons.cbs.vo._060503_060503;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS060503OutputDetailsVO {

	private String email_addr; //電子郵件地址
	private String startfr; //開始
	private String DefaultString1; //Grid(10):     資料
	
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
	public String getDefaultString1() {
		return DefaultString1;
	}
	public void setDefaultString1(String defaultString1) {
		DefaultString1 = defaultString1;
	}
	
	

}
