package com.systex.jbranch.app.server.fps.crm621;

import java.util.ArrayList;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM621OutputVO extends PagingOutputVO {
	private List resultList;
	private List resultList_A;
	private List resultList_message;
	private String custTxFlag;
	private String CUST_NAME;
	
	private List phoneList;
	private List addrList;
	private List mailList;
	
	private String hangphone;
	private String telphone;
	
	private String mail;
	private String addr;
	
	
	
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getTelphone() {
		return telphone;
	}
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	public String getHangphone() {
		return hangphone;
	}
	public void setHangphone(String hangphone) {
		this.hangphone = hangphone;
	}
	public List getPhoneList() {
		return phoneList;
	}
	public void setPhoneList(List phoneList) {
		this.phoneList = phoneList;
	}
	public List getAddrList() {
		return addrList;
	}
	public void setAddrList(List addrList) {
		this.addrList = addrList;
	}
	public List getMailList() {
		return mailList;
	}
	public void setMailList(List mailList) {
		this.mailList = mailList;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getResultList_A() {
		return resultList_A;
	}
	public void setResultList_A(List resultList_A) {
		this.resultList_A = resultList_A;
	}
	public List getResultList_message() {
		return resultList_message;
	}
	public void setResultList_message(List resultList_message) {
		this.resultList_message = resultList_message;
	}
	public String getCustTxFlag() {
		return custTxFlag;
	}
	public void setCustTxFlag(String custTxFlag) {
		this.custTxFlag = custTxFlag;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	
}
