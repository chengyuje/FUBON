package com.systex.jbranch.fubon.commons.esb.vo.nmvp4a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 台外幣活存明細查詢
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NMVP4AOutputDetailsVO {
	@XmlElement
	private String ACNO;	//帳號
	@XmlElement
	private String CUR;		//幣別
	@XmlElement
	private String VALUE;	//餘額
	
	public String getACNO() {
		return ACNO;
	}
	public void setACNO(String aCNO) {
		ACNO = aCNO;
	}
	public String getCUR() {
		return CUR;
	}
	public void setCUR(String cUR) {
		CUR = cUR;
	}
	public String getVALUE() {
		return VALUE;
	}
	public void setVALUE(String vALUE) {
		VALUE = vALUE;
	}
	
}