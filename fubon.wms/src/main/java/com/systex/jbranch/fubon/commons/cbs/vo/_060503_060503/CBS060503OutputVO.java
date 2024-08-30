package com.systex.jbranch.fubon.commons.cbs.vo._060503_060503;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS060503OutputVO {

	private String email_addr; //電子郵件地址
	private String startfr; //開始
	
	//電文特殊規格用
	private Boolean isuse = true;
	
	@XmlElement(name = "TxRepeat")
    private List<CBS060503OutputDetailsVO> details;
	
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
	public List<CBS060503OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<CBS060503OutputDetailsVO> details) {
		this.details = details;
	}
	public Boolean getIsuse() {
		return isuse;
	}
	public void setIsuse(Boolean isuse) {
		this.isuse = isuse;
	}
	

	
}
