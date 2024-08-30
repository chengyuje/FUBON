package com.systex.jbranch.fubon.commons.esb.vo.eb202674;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class EB202674InputVO {
	@XmlElement
	private String FunCode;	//功能
	@XmlElement
	private String AcctId12;	//存款帳號
	@XmlElement
	private String Item;	//項目
	@XmlElement
	private String StartDt;	//起日
	@XmlElement
	private String EndDt;	//迄日
	
	
	public String getFunCode() {
		return FunCode;
	}
	public void setFunCode(String funCode) {
		FunCode = funCode;
	}
	public String getAcctId12() {
		return AcctId12;
	}
	public void setAcctId12(String acctId12) {
		AcctId12 = acctId12;
	}
	public String getItem() {
		return Item;
	}
	public void setItem(String item) {
		Item = item;
	}
	public String getStartDt() {
		return StartDt;
	}
	public void setStartDt(String startDt) {
		StartDt = startDt;
	}
	public String getEndDt() {
		return EndDt;
	}
	public void setEndDt(String endDt) {
		EndDt = endDt;
	}
	

}