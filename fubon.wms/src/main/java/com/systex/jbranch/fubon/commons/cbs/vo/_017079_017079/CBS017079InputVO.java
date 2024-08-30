package com.systex.jbranch.fubon.commons.cbs.vo._017079_017079;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS017079InputVO {

	private String accntNumber1; // 帳號
	private String term1; // 期數
	private String term2; // 期數
	private String opt; // 選項
	private String YrTerm; //學期別
	
	
	public String getAccntNumber1() {
		return accntNumber1;
	}
	public void setAccntNumber1(String accntNumber1) {
		this.accntNumber1 = accntNumber1;
	}
	public String getTerm1() {
		return term1;
	}
	public void setTerm1(String term1) {
		this.term1 = term1;
	}
	public String getTerm2() {
		return term2;
	}
	public void setTerm2(String term2) {
		this.term2 = term2;
	}
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	public String getYrTerm() {
		return YrTerm;
	}
	public void setYrTerm(String yrTerm) {
		YrTerm = yrTerm;
	}
	

	
}
