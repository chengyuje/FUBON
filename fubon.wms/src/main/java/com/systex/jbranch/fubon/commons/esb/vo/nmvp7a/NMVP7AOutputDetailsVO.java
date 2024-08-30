package com.systex.jbranch.fubon.commons.esb.vo.nmvp7a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NMVP7AOutputDetailsVO {
	
	@XmlElement
	private String MIS_FIRST_FLAG;
	
	@XmlElement
	private String DAY_TRAD_FLAG;

	public String getMIS_FIRST_FLAG() {
		return MIS_FIRST_FLAG;
	}

	public void setMIS_FIRST_FLAG(String mIS_FIRST_FLAG) {
		MIS_FIRST_FLAG = mIS_FIRST_FLAG;
	}

	public String getDAY_TRAD_FLAG() {
		return DAY_TRAD_FLAG;
	}

	public void setDAY_TRAD_FLAG(String dAY_TRAD_FLAG) {
		DAY_TRAD_FLAG = dAY_TRAD_FLAG;
	}
	
}