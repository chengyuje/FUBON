package com.systex.jbranch.fubon.commons.esb.vo.nmi002;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by CathyTang on 2018/12/21.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMI002InputVO {
    @XmlElement
	private String EviNum;	//契約編號

	public String getEviNum() {
		return EviNum;
	}

	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}

	
}
