package com.systex.jbranch.app.server.fps.pms422;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PMS422InputVO extends PagingInputVO {

	private Date start;
	private Date end;
	private String snapDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String sourceOfDemand;
	private String custID;

	private String outCustID1;
	private String inCustID1;
	private String inCustID2;
	private String actStartDate;
	private String actEndDate;

	private String empID; // sheet1 查詢條件：行員id
	private String noteStatus; // sheet2 查詢條件：已回覆/未回覆
	private String outCustID; // sheet2 查詢條件：轉出id
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

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getNoteStatus() {
		return noteStatus;
	}

	public void setNoteStatus(String noteStatus) {
		this.noteStatus = noteStatus;
	}

	public String getOutCustID() {
		return outCustID;
	}

	public void setOutCustID(String outCustID) {
		this.outCustID = outCustID;
	}

	public String getOutCustID1() {
		return outCustID1;
	}

	public void setOutCustID1(String outCustID1) {
		this.outCustID1 = outCustID1;
	}

	public String getInCustID1() {
		return inCustID1;
	}

	public void setInCustID1(String inCustID1) {
		this.inCustID1 = inCustID1;
	}

	public String getInCustID2() {
		return inCustID2;
	}

	public void setInCustID2(String inCustID2) {
		this.inCustID2 = inCustID2;
	}

	public String getActStartDate() {
		return actStartDate;
	}

	public void setActStartDate(String actStartDate) {
		this.actStartDate = actStartDate;
	}

	public String getActEndDate() {
		return actEndDate;
	}

	public void setActEndDate(String actEndDate) {
		this.actEndDate = actEndDate;
	}

	public String getSourceOfDemand() {
		return sourceOfDemand;
	}

	public void setSourceOfDemand(String sourceOfDemand) {
		this.sourceOfDemand = sourceOfDemand;
	}

	/** 是否需要主管覆核 **/
	private String needConfirmYN;

	/** 使用者編輯的資料 **/
	private List<Map<String, Object>> editedList;

	private String memLoginFlag;

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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public List<Map<String, Object>> getEditedList() {
		return editedList;
	}

	public void setEditedList(List<Map<String, Object>> editedList) {
		this.editedList = editedList;
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

	public String getSnapDate() {
		return snapDate;
	}

	public void setSnapDate(String snapDate) {
		this.snapDate = snapDate;
	}

}
