package com.systex.jbranch.fubon.commons.esb.vo.nmi001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMI001OutputVODetails {
	@XmlElement
	private String EviNum;		// 契約編號
	
    @XmlElement
	private String CardStatus;	// 卡片標籤（1：贖回中）

	public String getEviNum() {
		return EviNum;
	}

	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}

	public String getCardStatus() {
		return CardStatus;
	}

	public void setCardStatus(String cardStatus) {
		CardStatus = cardStatus;
	}
   
}
