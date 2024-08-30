package com.systex.jbranch.app.server.fps.cus110;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CUS110InputVO extends PagingInputVO{
	private List<String> custID;
	private List<String> aoID;
	private String recipientType;
	private String fileName;
	private String contact;
	private String email;
	private String subject;
	private String subjectTxt;
	private String contentList;
	private String centerTextarea;
	private List annexID;
	
	private BigDecimal seq;
	private String status;
	private String confirmNAME;
	
	
	public List<String> getCustID() {
		return custID;
	}
	public void setCustID(List<String> custID) {
		this.custID = custID;
	}
	public List<String> getAoID() {
		return aoID;
	}
	public void setAoID(List<String> aoID) {
		this.aoID = aoID;
	}
	public String getRecipientType() {
		return recipientType;
	}
	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSubjectTxt() {
		return subjectTxt;
	}
	public void setSubjectTxt(String subjectTxt) {
		this.subjectTxt = subjectTxt;
	}
	public String getContentList() {
		return contentList;
	}
	public void setContentList(String contentList) {
		this.contentList = contentList;
	}
	public String getCenterTextarea() {
		return centerTextarea;
	}
	public void setCenterTextarea(String centerTextarea) {
		this.centerTextarea = centerTextarea;
	}
	public List getAnnexID() {
		return annexID;
	}
	public void setAnnexID(List annexID) {
		this.annexID = annexID;
	}
	public BigDecimal getSeq() {
		return seq;
	}
	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getConfirmNAME() {
		return confirmNAME;
	}
	public void setConfirmNAME(String confirmNAME) {
		this.confirmNAME = confirmNAME;
	}
}