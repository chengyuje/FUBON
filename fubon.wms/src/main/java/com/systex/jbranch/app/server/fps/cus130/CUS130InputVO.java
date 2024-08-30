package com.systex.jbranch.app.server.fps.cus130;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CUS130InputVO extends PagingInputVO {
	private BigDecimal seq;
	private String ivgPlanName;
	private String ivgType;
	private String ivgPlanType;
	private Date ivgStartDate;
	private Date ivgEndDate;
	private String creatorID;
	private String creatorName;
	private String ivgPlanDesc;
	private String setType;
	private String fileID;
	private String fileName;
	private String fileRealName;
	private String custFileName;
	private String custFileRealName;
	private List<Map<String, Object>> totalList;
	private List<String> empList;
	private List<Map<String, Object>> custList;
	private List<Map<String, Object>> memberList;
	private List<Map<String, Object>> selectList;
	private List<Map<String, Object>> type4_empList;
	private List<Map<String, Object>> listBase;
	private Boolean startDisable;
	private String branch;
	
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
	public String getIvgPlanDesc() {
		return ivgPlanDesc;
	}
	public void setIvgPlanDesc(String ivgPlanDesc) {
		this.ivgPlanDesc = ivgPlanDesc;
	}
	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
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
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	public String getCustFileName() {
		return custFileName;
	}
	public void setCustFileName(String custFileName) {
		this.custFileName = custFileName;
	}
	public String getCustFileRealName() {
		return custFileRealName;
	}
	public void setCustFileRealName(String custFileRealName) {
		this.custFileRealName = custFileRealName;
	}
	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}
	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}
	public List<String> getEmpList() {
		return empList;
	}
	public void setEmpList(List<String> empList) {
		this.empList = empList;
	}
	public List<Map<String, Object>> getCustList() {
		return custList;
	}
	public void setCustList(List<Map<String, Object>> custList) {
		this.custList = custList;
	}
	public List<Map<String, Object>> getMemberList() {
		return memberList;
	}
	public void setMemberList(List<Map<String, Object>> memberList) {
		this.memberList = memberList;
	}
	public List<Map<String, Object>> getSelectList() {
		return selectList;
	}
	public void setSelectList(List<Map<String, Object>> selectList) {
		this.selectList = selectList;
	}
	public List<Map<String, Object>> getType4_empList() {
		return type4_empList;
	}
	public void setType4_empList(List<Map<String, Object>> type4_empList) {
		this.type4_empList = type4_empList;
	}
	public List<Map<String, Object>> getListBase() {
		return listBase;
	}
	public void setListBase(List<Map<String, Object>> listBase) {
		this.listBase = listBase;
	}
	public Boolean getStartDisable() {
		return startDisable;
	}
	public void setStartDisable(Boolean startDisable) {
		this.startDisable = startDisable;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
}