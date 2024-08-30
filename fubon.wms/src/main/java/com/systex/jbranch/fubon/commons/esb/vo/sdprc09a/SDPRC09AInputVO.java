package com.systex.jbranch.fubon.commons.esb.vo.sdprc09a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by JackyWu on 2016/12/22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDPRC09AInputVO {
	@XmlElement
	private String ID; //身分證字號

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
}
