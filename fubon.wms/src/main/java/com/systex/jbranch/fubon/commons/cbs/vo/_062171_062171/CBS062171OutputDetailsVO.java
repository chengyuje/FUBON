package com.systex.jbranch.fubon.commons.cbs.vo._062171_062171;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS062171OutputDetailsVO {
	
	private String CycleNumber;		// 撥貸件數天數查詢 (7、30、60、90、指定)
	private String FlagNumber;		// 撥貸註記(Y/N)
	private String CntNumber;		// 撥貸筆數
	private String AmtNumber;		// 撥貸金額
	
	public String getCycleNumber() {
		return CycleNumber;
	}
	public void setCycleNumber(String cycleNumber) {
		CycleNumber = cycleNumber;
	}
	public String getFlagNumber() {
		return FlagNumber;
	}
	public void setFlagNumber(String flagNumber) {
		FlagNumber = flagNumber;
	}
	public String getCntNumber() {
		return CntNumber;
	}
	public void setCntNumber(String cntNumber) {
		CntNumber = cntNumber;
	}
	public String getAmtNumber() {
		return AmtNumber;
	}
	public void setAmtNumber(String amtNumber) {
		AmtNumber = amtNumber;
	}
	
}
