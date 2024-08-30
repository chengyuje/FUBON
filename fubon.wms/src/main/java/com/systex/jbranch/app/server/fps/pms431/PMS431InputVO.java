package com.systex.jbranch.app.server.fps.pms431;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PMS431InputVO extends PagingInputVO {

	private String sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;

	private String aoEmpCID;
	private String highRiskInvType;

	private String noteStatus;

	private String memLoginFlag;
	private String needConfirmYN;

	private String snapYYYYMM;
	private String aoBranchNBR;
	private String aoEmpID;
	private String kindType;

	/** 使用者編輯的資料 **/
	private List<Map<String, Object>> editedList;
	private String selectRoleID;
	
	private String uhrmRC;
	private String uhrmOP;
	
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
	
	public String getSelectRoleID() {
		return selectRoleID;
	}

	public void setSelectRoleID(String selectRoleID) {
		this.selectRoleID = selectRoleID;
	}

	public String getAoEmpCID() {
		return aoEmpCID;
	}

	public void setAoEmpCID(String aoEmpCID) {
		this.aoEmpCID = aoEmpCID;
	}

	public List<Map<String, Object>> getEditedList() {
		return editedList;
	}

	public void setEditedList(List<Map<String, Object>> editedList) {
		this.editedList = editedList;
	}

	public String getSnapYYYYMM() {
		return snapYYYYMM;
	}

	public void setSnapYYYYMM(String snapYYYYMM) {
		this.snapYYYYMM = snapYYYYMM;
	}

	public String getAoBranchNBR() {
		return aoBranchNBR;
	}

	public void setAoBranchNBR(String aoBranchNBR) {
		this.aoBranchNBR = aoBranchNBR;
	}

	public String getAoEmpID() {
		return aoEmpID;
	}

	public void setAoEmpID(String aoEmpID) {
		this.aoEmpID = aoEmpID;
	}

	public String getKindType() {
		return kindType;
	}

	public void setKindType(String kindType) {
		this.kindType = kindType;
	}

	public String getNeedConfirmYN() {
		return needConfirmYN;
	}

	public void setNeedConfirmYN(String needConfirmYN) {
		this.needConfirmYN = needConfirmYN;
	}

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
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

	public String getHighRiskInvType() {
		return highRiskInvType;
	}

	public void setHighRiskInvType(String highRiskInvType) {
		this.highRiskInvType = highRiskInvType;
	}

	public String getNoteStatus() {
		return noteStatus;
	}

	public void setNoteStatus(String noteStatus) {
		this.noteStatus = noteStatus;
	}
}
