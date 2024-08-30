package com.systex.jbranch.fubon.commons.esb.vo.njweea70;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJWEEA70OutputDetailVO {
	@XmlElement
	private String TxtDt;		// 日期(民國年) ex.01130607
	
	@XmlElement
	private String TxtCode;		// 營業日註記_空白：非營業日、Y：營業日

	public String getTxtDt() {
		return TxtDt;
	}

	public void setTxtDt(String txtDt) {
		TxtDt = txtDt;
	}

	public String getTxtCode() {
		return TxtCode;
	}

	public void setTxtCode(String txtCode) {
		TxtCode = txtCode;
	}
	
}