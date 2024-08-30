package com.systex.jbranch.fubon.commons.esb.vo.nfei002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFEI002InputVO {
	
	
	@XmlElement
	private String VQACID; //身份証ID
	@XmlElement
	private String VQIDTY; //Id type
	@XmlElement
	private String VQKIND; //資料種類  1現況 2歷史資料
	
	
	public String getVQACID() {
		return VQACID;
	}
	public void setVQACID(String vQACID) {
		VQACID = vQACID;
	}
	public String getVQIDTY() {
		return VQIDTY;
	}
	public void setVQIDTY(String vQIDTY) {
		VQIDTY = vQIDTY;
	}
	public String getVQKIND() {
		return VQKIND;
	}
	public void setVQKIND(String vQKIND) {
		VQKIND = vQKIND;
	}
	
	

}
