package com.systex.jbranch.app.server.fps.prd179;

import java.util.List;
import java.util.Map;

public class PRD179InputVO {
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String loginRole;
	private String CASETYPE; //案件類型
	private String ACCEPTID; //受理編號
	private String POLICY_TYPE_2; //保險產品類型
	private String POLICY_NO; //保單號碼
	private String POLICYSTATUS; //保單狀況
	private String CNAME; //保險公司
	private String SALES_ID; //招攬人ID
	private String PAYER_APPL_ID; //要保人ID
	private String PRODUCTID; //產品代號
	private String PROJECTID; //專案代號
	private String INSURED_ID; //被保人ID
	private String SAPPLY_DATE; //要保書填寫日起日
	private String EAPPLY_DATE; //要保書填寫日訖日
	private List<Map<String, Object>> regionList;
	private List<Map<String, Object>> areaList;
	private List<Map<String, Object>> branchList;
	private List<Map<String, Object>> aoCodeList;
	
	private List<Map<String, Object>> exportList; //匯出時傳入的list
	
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
	public String getCASETYPE() {
		return CASETYPE;
	}
	public void setCASETYPE(String cASETYPE) {
		CASETYPE = cASETYPE;
	}
	public String getACCEPTID() {
		return ACCEPTID;
	}
	public void setACCEPTID(String aCCEPTID) {
		ACCEPTID = aCCEPTID;
	}
	public String getPOLICY_TYPE_2() {
		return POLICY_TYPE_2;
	}
	public void setPOLICY_TYPE_2(String pOLICY_TYPE_2) {
		POLICY_TYPE_2 = pOLICY_TYPE_2;
	}
	public String getPOLICY_NO() {
		return POLICY_NO;
	}
	public void setPOLICY_NO(String pOLICY_NO) {
		POLICY_NO = pOLICY_NO;
	}
	public String getPOLICYSTATUS() {
		return POLICYSTATUS;
	}
	public void setPOLICYSTATUS(String pOLICYSTATUS) {
		POLICYSTATUS = pOLICYSTATUS;
	}
	public String getCNAME() {
		return CNAME;
	}
	public void setCNAME(String cNAME) {
		CNAME = cNAME;
	}
	public String getSALES_ID() {
		return SALES_ID;
	}
	public void setSALES_ID(String sALES_ID) {
		SALES_ID = sALES_ID;
	}
	public String getPAYER_APPL_ID() {
		return PAYER_APPL_ID;
	}
	public void setPAYER_APPL_ID(String pAYER_APPL_ID) {
		PAYER_APPL_ID = pAYER_APPL_ID;
	}
	public String getPRODUCTID() {
		return PRODUCTID;
	}
	public void setPRODUCTID(String pRODUCTID) {
		PRODUCTID = pRODUCTID;
	}
	public String getPROJECTID() {
		return PROJECTID;
	}
	public void setPROJECTID(String pROJECTID) {
		PROJECTID = pROJECTID;
	}
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public String getSAPPLY_DATE() {
		return SAPPLY_DATE;
	}
	public void setSAPPLY_DATE(String sAPPLY_DATE) {
		SAPPLY_DATE = sAPPLY_DATE;
	}
	public String getEAPPLY_DATE() {
		return EAPPLY_DATE;
	}
	public void setEAPPLY_DATE(String eAPPLY_DATE) {
		EAPPLY_DATE = eAPPLY_DATE;
	}
	public List<Map<String, Object>> getExportList() {
		return exportList;
	}
	public void setExportList(List<Map<String, Object>> exportList) {
		this.exportList = exportList;
	}

}
