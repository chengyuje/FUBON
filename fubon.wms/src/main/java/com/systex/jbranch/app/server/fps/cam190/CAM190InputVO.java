package com.systex.jbranch.app.server.fps.cam190;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM190InputVO extends PagingInputVO {
	private String regionID;
	private String opID;
	private String branchID;
	private String custID;
	private String custName;
	private String aoCode;
	private String campName;
	private String leadStatus;
	private String vipDegree;
	private String leadDateRange;
	private String leadType;
	private String campPurpose;
	private String campType;
	private String conDegree;
	private String customTabType;
	private List<Map<String, Object>> custom_list;
	private String sfaLeadID;
	
	
	public String getRegionID() {
		return regionID;
	}
	public void setRegionID(String regionID) {
		this.regionID = regionID;
	}
	public String getOpID() {
		return opID;
	}
	public void setOpID(String opID) {
		this.opID = opID;
	}
	public String getBranchID() {
		return branchID;
	}
	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getAoCode() {
		return aoCode;
	}
	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}
	public String getCampName() {
		return campName;
	}
	public void setCampName(String campName) {
		this.campName = campName;
	}
	public String getLeadStatus() {
		return leadStatus;
	}
	public void setLeadStatus(String leadStatus) {
		this.leadStatus = leadStatus;
	}
	public String getVipDegree() {
		return vipDegree;
	}
	public void setVipDegree(String vipDegree) {
		this.vipDegree = vipDegree;
	}
	public String getLeadDateRange() {
		return leadDateRange;
	}
	public void setLeadDateRange(String leadDateRange) {
		this.leadDateRange = leadDateRange;
	}
	public String getLeadType() {
		return leadType;
	}
	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}
	public String getCampPurpose() {
		return campPurpose;
	}
	public void setCampPurpose(String campPurpose) {
		this.campPurpose = campPurpose;
	}
	public String getCampType() {
		return campType;
	}
	public void setCampType(String campType) {
		this.campType = campType;
	}
	public String getConDegree() {
		return conDegree;
	}
	public void setConDegree(String conDegree) {
		this.conDegree = conDegree;
	}
	public String getCustomTabType() {
		return customTabType;
	}
	public void setCustomTabType(String customTabType) {
		this.customTabType = customTabType;
	}
	public List<Map<String, Object>> getCustom_list() {
		return custom_list;
	}
	public void setCustom_list(List<Map<String, Object>> custom_list) {
		this.custom_list = custom_list;
	}
	public String getSfaLeadID() {
		return sfaLeadID;
	}
	public void setSfaLeadID(String sfaLeadID) {
		this.sfaLeadID = sfaLeadID;
	}
}