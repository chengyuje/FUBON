package com.systex.jbranch.app.server.fps.pms418;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS418InputVO extends PagingInputVO {

	private Date sCreDate;
	private Date eCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String needConfirmYN;

	private List<Map<String, Object>> list;
	private List<Map<String, Object>> list2;

	private String memLoginFlag;

	private String txnDay;
	private String taskNM;
	private String ipAddr;
	private String custID;

	private String ipAddrSearch;
	private String custIDSearch;
	private String noteStatus;

	private String ipAddrDel;
	private String branchNbrDel;

	private String FILE_NAME;
	private String ACTUAL_FILE_NAME;

	private List<Map<String, Object>> exportList;
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

	public List<Map<String, Object>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, Object>> exportList) {
		this.exportList = exportList;
	}

	public String getFILE_NAME() {
		return FILE_NAME;
	}

	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}

	public String getACTUAL_FILE_NAME() {
		return ACTUAL_FILE_NAME;
	}

	public void setACTUAL_FILE_NAME(String aCTUAL_FILE_NAME) {
		ACTUAL_FILE_NAME = aCTUAL_FILE_NAME;
	}

	public String getIpAddrDel() {
		return ipAddrDel;
	}

	public void setIpAddrDel(String ipAddrDel) {
		this.ipAddrDel = ipAddrDel;
	}

	public String getBranchNbrDel() {
		return branchNbrDel;
	}

	public void setBranchNbrDel(String branchNbrDel) {
		this.branchNbrDel = branchNbrDel;
	}

	public String getNoteStatus() {
		return noteStatus;
	}

	public void setNoteStatus(String noteStatus) {
		this.noteStatus = noteStatus;
	}

	public String getIpAddrSearch() {
		return ipAddrSearch;
	}

	public void setIpAddrSearch(String ipAddrSearch) {
		this.ipAddrSearch = ipAddrSearch;
	}

	public String getCustIDSearch() {
		return custIDSearch;
	}

	public void setCustIDSearch(String custIDSearch) {
		this.custIDSearch = custIDSearch;
	}

	public String getTxnDay() {
		return txnDay;
	}

	public void setTxnDay(String txnDay) {
		this.txnDay = txnDay;
	}

	public String getTaskNM() {
		return taskNM;
	}

	public void setTaskNM(String taskNM) {
		this.taskNM = taskNM;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
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

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public List<Map<String, Object>> getList2() {
		return list2;
	}

	public void setList2(List<Map<String, Object>> list2) {
		this.list2 = list2;
	}

	public String getNeedConfirmYN() {
		return needConfirmYN;
	}

	public void setNeedConfirmYN(String needConfirmYN) {
		this.needConfirmYN = needConfirmYN;
	}

}
