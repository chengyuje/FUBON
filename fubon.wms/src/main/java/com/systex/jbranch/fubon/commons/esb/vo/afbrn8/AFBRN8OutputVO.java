package com.systex.jbranch.fubon.commons.esb.vo.afbrn8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AFBRN8OutputVO {
	@XmlElement
	private String ERR_COD;//錯誤碼
	@XmlElement
	private String ERR_TXT;//錯誤訊息
	
	
	public String getERR_COD() {
		return ERR_COD;
	}
	public void setERR_COD(String eRR_COD) {
		ERR_COD = eRR_COD;
	}
	public String getERR_TXT() {
		return ERR_TXT;
	}
	public void setERR_TXT(String eRR_TXT) {
		ERR_TXT = eRR_TXT;
	}


}
