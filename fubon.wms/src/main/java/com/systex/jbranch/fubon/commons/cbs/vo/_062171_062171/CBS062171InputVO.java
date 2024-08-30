package com.systex.jbranch.fubon.commons.cbs.vo._062171_062171;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS062171InputVO {
	
	private String IDType; 		// 證件號碼
	private String IDNo; 		// 客戶號碼
	private String DayNumber; 	// 撥貸件數天數查詢
	
	public String getIDType() {
		return IDType;
	}
	public void setIDType(String iDType) {
		IDType = iDType;
	}
	public String getIDNo() {
		return IDNo;
	}
	public void setIDNo(String iDNo) {
		IDNo = iDNo;
	}
	public String getDayNumber() {
		return DayNumber;
	}
	public void setDayNumber(String dayNumber) {
		DayNumber = dayNumber;
	}
}
