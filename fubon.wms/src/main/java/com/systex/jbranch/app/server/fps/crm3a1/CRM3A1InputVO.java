package com.systex.jbranch.app.server.fps.crm3a1;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CRM3A1InputVO {
	
	private String PRJ_ID;
	private String CUST_ID;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String loginRole;
	private Date reportDate;
	
	private List<Map<String, Object>> regionList;
	private List<Map<String, Object>> areaList;
	private List<Map<String, Object>> branchList;
	private List<Map<String, Object>> aoCodeList;
	
	//update來的參數
	private String seq;
	private Date recDate;
	private String recSeq;
	private String fileName;
	private String fileRealName;
	
	public String getPRJ_ID() {
		return PRJ_ID;
	}
	public void setPRJ_ID(String pRJ_ID) {
		PRJ_ID = pRJ_ID;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getLoginRole() {
		return loginRole;
	}
	public void setLoginRole(String loginRole) {
		this.loginRole = loginRole;
	}
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}

	public Date getRecDate() {
		return recDate;
	}
	public void setRecDate(Date recDate) {
		this.recDate = recDate;
	}
	public String getRecSeq() {
		return recSeq;
	}
	public void setRecSeq(String recSeq) {
		this.recSeq = recSeq;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<Map<String, Object>> getBranchList() {
		return branchList;
	}
	public void setBranchList(List<Map<String, Object>> branchList) {
		this.branchList = branchList;
	}
	public List<Map<String, Object>> getAoCodeList() {
		return aoCodeList;
	}
	public void setAoCodeList(List<Map<String, Object>> aoCodeList) {
		this.aoCodeList = aoCodeList;
	}
	public List<Map<String, Object>> getRegionList() {
		return regionList;
	}
	public void setRegionList(List<Map<String, Object>> regionList) {
		this.regionList = regionList;
	}
	public List<Map<String, Object>> getAreaList() {
		return areaList;
	}
	public void setAreaList(List<Map<String, Object>> areaList) {
		this.areaList = areaList;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	
	

}
