package com.systex.jbranch.app.server.fps.cam210;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM210InputVO extends PagingInputVO {

	private String branchID;
	private String aoCode;
	private String aoList;
	private String branch;
	private String regionCenterName;
	private String branch_Name;
	private String branchNbr;
	private String campaignId;
	private String campaignName;
	private String channel;
	private String custId;
	private String empId;
	private String op;
	private String otherReason;
	private String reason;
	private String region;
	private String stepId;
	private String toDoList;
	private String leadType;
	private String campType;
	private Date camp_sDate;
	private Date camp_eDate;
	private Date camp_esDate;
	private Date camp_eeDate;

	private String tabSheet;

	private List<Map<String, Object>> reSetLeList;
	
	private String uhrmRC;
	private String uhrmOP;
	
	private String uEmpId;
	
	public String getuEmpId() {
		return uEmpId;
	}

	public void setuEmpId(String uEmpId) {
		this.uEmpId = uEmpId;
	}

	public String getUhrmRC() {
		return uhrmRC;
	}

	public void setUhrmRC(String uhrmRC) {
		this.uhrmRC = uhrmRC;
	}

	public String getUhrmOP() {
		return uhrmOP;
	}

	public void setUhrmOP(String uhrmOP) {
		this.uhrmOP = uhrmOP;
	}

	public String getBranchID() {
		return branchID;
	}

	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}

	public List<Map<String, Object>> getReSetLeList() {
		return reSetLeList;
	}

	public void setReSetLeList(List<Map<String, Object>> reSetLeList) {
		this.reSetLeList = reSetLeList;
	}

	public String getTabSheet() {
		return tabSheet;
	}

	public void setTabSheet(String tabSheet) {
		this.tabSheet = tabSheet;
	}

	public String getAoCode() {
		return aoCode;
	}

	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}

	public String getAoList() {
		return aoList;
	}

	public void setAoList(String aoList) {
		this.aoList = aoList;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getRegionCenterName() {
		return regionCenterName;
	}

	public void setRegionCenterName(String regionCenterName) {
		this.regionCenterName = regionCenterName;
	}

	public String getBranch_Name() {
		return branch_Name;
	}

	public void setBranch_Name(String branch_Name) {
		this.branch_Name = branch_Name;
	}

	public String getBranchNbr() {
		return branchNbr;
	}

	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getOtherReason() {
		return otherReason;
	}

	public void setOtherReason(String otherReason) {
		this.otherReason = otherReason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStepId() {
		return stepId;
	}

	public void setStepId(String stepId) {
		this.stepId = stepId;
	}

	public String getToDoList() {
		return toDoList;
	}

	public void setToDoList(String toDoList) {
		this.toDoList = toDoList;
	}

	public String getLeadType() {
		return leadType;
	}

	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}

	public String getCampType() {
		return campType;
	}

	public void setCampType(String campType) {
		this.campType = campType;
	}

	public Date getCamp_sDate() {
		return camp_sDate;
	}

	public void setCamp_sDate(Date camp_sDate) {
		this.camp_sDate = camp_sDate;
	}

	public Date getCamp_eDate() {
		return camp_eDate;
	}

	public void setCamp_eDate(Date camp_eDate) {
		this.camp_eDate = camp_eDate;
	}

	public Date getCamp_esDate() {
		return camp_esDate;
	}

	public void setCamp_esDate(Date camp_esDate) {
		this.camp_esDate = camp_esDate;
	}

	public Date getCamp_eeDate() {
		return camp_eeDate;
	}

	public void setCamp_eeDate(Date camp_eeDate) {
		this.camp_eeDate = camp_eeDate;
	}

}