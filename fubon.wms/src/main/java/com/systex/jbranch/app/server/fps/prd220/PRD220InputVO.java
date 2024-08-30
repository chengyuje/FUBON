package com.systex.jbranch.app.server.fps.prd220;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD220InputVO extends PagingInputVO {
	private String prd_id;
	private String ptype;
	
	private String doc_id;
	private String doc_name;
	private String type;
	private String fileName;
	private String realfileName;
	private String web;
	private String doc_type;
	private String shared;
	private Date doc_sDate;
	private Date doc_eDate;
	
	
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	public String getDoc_id() {
		return doc_id;
	}
	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}
	public String getDoc_name() {
		return doc_name;
	}
	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRealfileName() {
		return realfileName;
	}
	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public String getDoc_type() {
		return doc_type;
	}
	public void setDoc_type(String doc_type) {
		this.doc_type = doc_type;
	}
	public String getShared() {
		return shared;
	}
	public void setShared(String shared) {
		this.shared = shared;
	}
	public Date getDoc_sDate() {
		return doc_sDate;
	}
	public void setDoc_sDate(Date doc_sDate) {
		this.doc_sDate = doc_sDate;
	}
	public Date getDoc_eDate() {
		return doc_eDate;
	}
	public void setDoc_eDate(Date doc_eDate) {
		this.doc_eDate = doc_eDate;
	}
}
