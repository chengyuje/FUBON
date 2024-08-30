package com.systex.jbranch.fubon.commons.esb.vo.nmvp4a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 台外幣活存明細查詢
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMVP4AInputVO {
	@XmlElement
	private String CONTRACT_NO;	//契約編號

	public String getCONTRACT_NO() {
		return CONTRACT_NO;
	}

	public void setCONTRACT_NO(String cONTRACT_NO) {
		CONTRACT_NO = cONTRACT_NO;
	}
	
	
	
}