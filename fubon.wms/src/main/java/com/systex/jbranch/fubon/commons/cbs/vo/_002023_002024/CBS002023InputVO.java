package com.systex.jbranch.fubon.commons.cbs.vo._002023_002024;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS002023InputVO {

	private String accntNumber1; //存款透支帳號
	private String overdraftOption1;
	private String DefInteger1;
	private String DefInteger2;
	private String FacNo;
	private String filler;
	private String currency;
	
	public String getAccntNumber1() {
		return accntNumber1;
	}
	public void setAccntNumber1(String accntNumber1) {
		this.accntNumber1 = accntNumber1;
	}
	public String getOverdraftOption1() {
		return overdraftOption1;
	}
	public void setOverdraftOption1(String overdraftOption1) {
		this.overdraftOption1 = overdraftOption1;
	}
	public String getDefInteger1() {
		return DefInteger1;
	}
	public void setDefInteger1(String defInteger1) {
		DefInteger1 = defInteger1;
	}
	public String getDefInteger2() {
		return DefInteger2;
	}
	public void setDefInteger2(String defInteger2) {
		DefInteger2 = defInteger2;
	}
	public String getFacNo() {
		return FacNo;
	}
	public void setFacNo(String facNo) {
		FacNo = facNo;
	}
	public String getFiller() {
		return filler;
	}
	public void setFiller(String filler) {
		this.filler = filler;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	

}
