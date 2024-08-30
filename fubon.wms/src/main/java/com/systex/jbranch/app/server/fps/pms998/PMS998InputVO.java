package com.systex.jbranch.app.server.fps.pms998;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS998InputVO extends PagingInputVO {

	// 查詢條件
	private String importSDate;
	private String importEDate;

	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;

	private String EMP_ID;

	// 功能
	private String actionType;
	
	// 核可/退回=刪除
	private String seq;

	// 新增
	private String branchNbr;
	private String empID;
	private String custID;
	private String custName;
	private String c2eRelation;
	private String prove;
	private String note;
	
	private String memLoginFlag;
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

	//#2042 匯出
	private List<Map<String, Object>> list;

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getImportSDate() {
		return importSDate;
	}

	public void setImportSDate(String importSDate) {
		this.importSDate = importSDate;
	}

	public String getImportEDate() {
		return importEDate;
	}

	public void setImportEDate(String importEDate) {
		this.importEDate = importEDate;
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

	public String getEMP_ID() {
		return EMP_ID;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getBranchNbr() {
		return branchNbr;
	}

	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
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

	public String getC2eRelation() {
		return c2eRelation;
	}

	public void setC2eRelation(String c2eRelation) {
		this.c2eRelation = c2eRelation;
	}

	public String getProve() {
		return prove;
	}

	public void setProve(String prove) {
		this.prove = prove;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

}
