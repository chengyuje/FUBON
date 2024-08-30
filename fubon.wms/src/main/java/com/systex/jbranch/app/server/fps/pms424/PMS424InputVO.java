package com.systex.jbranch.app.server.fps.pms424;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS424InputVO extends PagingInputVO {

	private String yyyymm;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String memLoginFlag;
	private List<Map<String, Object>> list;
	private String from181;
	private Date sDate;
	private Date eDate;
	private String sourceOfDemand;

	private String noteStatus;
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

	public String getNoteStatus() {
		return noteStatus;
	}

	public void setNoteStatus(String noteStatus) {
		this.noteStatus = noteStatus;
	}

	public String getSourceOfDemand() {
		return sourceOfDemand;
	}

	public void setSourceOfDemand(String sourceOfDemand) {
		this.sourceOfDemand = sourceOfDemand;
	}

	public String getYyyymm() {
		return yyyymm;
	}

	public void setYyyymm(String yyyymm) {
		this.yyyymm = yyyymm;
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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public String getFrom181() {
		return from181;
	}

	public void setFrom181(String from181) {
		this.from181 = from181;
	}

	public Date getsDate() {
		return sDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

}
