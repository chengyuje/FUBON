package com.systex.jbranch.fubon.commons.esb.vo.nmi003;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/02/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NMI003InputVO {
    @XmlElement
	private String EviNum;	//契約編號

	public String getEviNum() {
		return EviNum;
	}

	public void setEviNum(String eviNum) {
		EviNum = eviNum;
	}
	
}
