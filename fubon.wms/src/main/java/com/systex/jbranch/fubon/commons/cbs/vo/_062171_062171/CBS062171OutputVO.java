package com.systex.jbranch.fubon.commons.cbs.vo._062171_062171;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS062171OutputVO {
	
	private String IDType;			// 證件號碼
	private String IDNo;			// 客戶號碼
	private String DayNumber; 		// 查詢天
	
	@XmlElement(name = "TxRepeat")
	private List<CBS062171OutputDetailsVO> details;
	
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
	public List<CBS062171OutputDetailsVO> getDetail() {
		return details;
	}
	public void setDetail(List<CBS062171OutputDetailsVO> details) {
		this.details = details;
	}
	
}
