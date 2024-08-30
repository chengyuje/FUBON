package com.systex.jbranch.app.server.fps.prd233;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD233InputVO extends PagingInputVO {
	private String type;
	private String prd_id;
	private Date sDate;
	private Date eDate;
	private String cname;
	
	private List<Map<String, Object>> idList;
	private String doc_name;
	private String fileName;
	private String realfileName;
	private String web;
	private Date doc_sDate;
	private Date doc_eDate;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public List<Map<String, Object>> getIdList() {
		return idList;
	}
	public void setIdList(List<Map<String, Object>> idList) {
		this.idList = idList;
	}
	public String getDoc_name() {
		return doc_name;
	}
	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
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
