package com.systex.jbranch.app.server.fps.mkt110;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MKT110InputVO extends PagingInputVO{
	private BigDecimal seq;
	private String bType;
	private String pType;
	private String subject;
	private Date sDate;
	private Date eDate;
	private String orgn;
	private String contact;
	private List<String> chkCode;
	private String content;
	private List<Map<String,String>> fileName;
	private List<Map<String,String>> realfileName;
	private List<Map<String,String>> web;
	private String pictureName;
	private String realpictureName;
	private String exipicture;
	private List<String> delId;
	private String onefileName;
	private byte[] attach;
	
	private String status;
	private String pri_id;
	private List<Map<String, Object>> review_list;
	
	
	public BigDecimal getSeq() {
		return seq;
	}
	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}
	public String getbType() {
		return bType;
	}
	public void setbType(String bType) {
		this.bType = bType;
	}
	public String getpType() {
		return pType;
	}
	public void setpType(String pType) {
		this.pType = pType;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
	public String getOrgn() {
		return orgn;
	}
	public void setOrgn(String orgn) {
		this.orgn = orgn;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public List<String> getChkCode() {
		return chkCode;
	}
	public void setChkCode(List<String> chkCode) {
		this.chkCode = chkCode;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<Map<String, String>> getFileName() {
		return fileName;
	}
	public void setFileName(List<Map<String, String>> fileName) {
		this.fileName = fileName;
	}
	public List<Map<String, String>> getRealfileName() {
		return realfileName;
	}
	public void setRealfileName(List<Map<String, String>> realfileName) {
		this.realfileName = realfileName;
	}
	public List<Map<String, String>> getWeb() {
		return web;
	}
	public void setWeb(List<Map<String, String>> web) {
		this.web = web;
	}
	public String getPictureName() {
		return pictureName;
	}
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}
	public String getRealpictureName() {
		return realpictureName;
	}
	public void setRealpictureName(String realpictureName) {
		this.realpictureName = realpictureName;
	}
	public String getExipicture() {
		return exipicture;
	}
	public void setExipicture(String exipicture) {
		this.exipicture = exipicture;
	}
	public List<String> getDelId() {
		return delId;
	}
	public void setDelId(List<String> delId) {
		this.delId = delId;
	}
	public String getOnefileName() {
		return onefileName;
	}
	public void setOnefileName(String onefileName) {
		this.onefileName = onefileName;
	}
	public byte[] getAttach() {
		return attach;
	}
	public void setAttach(byte[] attach) {
		this.attach = attach;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPri_id() {
		return pri_id;
	}
	public void setPri_id(String pri_id) {
		this.pri_id = pri_id;
	}
	public List<Map<String, Object>> getReview_list() {
		return review_list;
	}
	public void setReview_list(List<Map<String, Object>> review_list) {
		this.review_list = review_list;
	}
}