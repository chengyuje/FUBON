package com.systex.jbranch.fubon.commons.cbs.vo._017050_017000;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS017200OutputVO {

	private String accntNumber1;
	private String function;
	private String due_date;
	private String due_amt;
	public String getAccntNumber1() {
		return accntNumber1;
	}
	public void setAccntNumber1(String accntNumber1) {
		this.accntNumber1 = accntNumber1;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getDue_date() {
		return due_date;
	}
	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}
	public String getDue_amt() {
		return due_amt;
	}
	public void setDue_amt(String due_amt) {
		this.due_amt = due_amt;
	}
	
	

}
