package com.systex.jbranch.app.server.fps.crm3b1;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CRM3B1InputVO {
	
	private String CUST_ID;
	private BigDecimal SEQ_KEY_NO;
	private String SAVE_TYPE;
	private String DEL_YN;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String loginRole;
	private String fileName;
	
	private List<Map<String, Object>> regionList;
	private List<Map<String, Object>> areaList;
	private List<Map<String, Object>> branchList;
	private List<Map<String, Object>> aoCodeList;
	
	//update來的參數
	private String seq;
	
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public BigDecimal getSEQ_KEY_NO() {
		return SEQ_KEY_NO;
	}
	public void setSEQ_KEY_NO(BigDecimal sEQ_KEY_NO) {
		SEQ_KEY_NO = sEQ_KEY_NO;
	}
	public String getSAVE_TYPE() {
		return SAVE_TYPE;
	}
	public void setSAVE_TYPE(String sAVE_TYPE) {
		SAVE_TYPE = sAVE_TYPE;
	}
	public String getDEL_YN() {
		return DEL_YN;
	}
	public void setDEL_YN(String dEL_YN) {
		DEL_YN = dEL_YN;
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
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
		

}
