package com.systex.jbranch.app.server.fps.cus150;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CUS150InputVO extends PagingInputVO{
	private BigDecimal seq;
	private String ivgPlanName;
	private String ivgType;
	private String ivgPlanType;
	private Date ivgStartDate;
	private Date ivgEndDate;
	private String creatorID;
	private String creatorName;
	private String fileID;
	private String fileName;
	private List<Map<String, Object>> listBase;
	private List<Map<String, Object>> custList;
	
	
	public BigDecimal getSeq() {
		return seq;
	}
	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}
	public String getIvgPlanName() {
		return ivgPlanName;
	}
	public void setIvgPlanName(String ivgPlanName) {
		this.ivgPlanName = ivgPlanName;
	}
	public String getIvgType() {
		return ivgType;
	}
	public void setIvgType(String ivgType) {
		this.ivgType = ivgType;
	}
	public String getIvgPlanType() {
		return ivgPlanType;
	}
	public void setIvgPlanType(String ivgPlanType) {
		this.ivgPlanType = ivgPlanType;
	}
	public Date getIvgStartDate() {
		return ivgStartDate;
	}
	public void setIvgStartDate(Date ivgStartDate) {
		this.ivgStartDate = ivgStartDate;
	}
	public Date getIvgEndDate() {
		return ivgEndDate;
	}
	public void setIvgEndDate(Date ivgEndDate) {
		this.ivgEndDate = ivgEndDate;
	}
	public String getCreatorID() {
		return creatorID;
	}
	public void setCreatorID(String creatorID) {
		this.creatorID = creatorID;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getFileID() {
		return fileID;
	}
	public void setFileID(String fileID) {
		this.fileID = fileID;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<Map<String, Object>> getListBase() {
		return listBase;
	}
	public void setListBase(List<Map<String, Object>> listBase) {
		this.listBase = listBase;
	}
	public List<Map<String, Object>> getCustList() {
		return custList;
	}
	public void setCustList(List<Map<String, Object>> custList) {
		this.custList = custList;
	}
}
