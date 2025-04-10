package com.systex.jbranch.app.server.fps.pms427;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS427InputVO extends PagingInputVO {

	private String sCreDate;
	private String eCreDate;
	private String dataMon;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	
	private String memLoginFlag;
	
	private List<Map<String, Object>> exportList;
	
	private List<Map<String, Object>> list;

	private String from181;
	
	private String FILE_NAME;
	private String ACTUAL_FILE_NAME;
	private List<Map<String, Object>> AO84MList;
	
	private String noteStatus;
	private String selectRoleID;
	
	private String uhrmRC;
	private String uhrmOP;
	
	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String geteCreDate() {
		return eCreDate;
	}

	public void seteCreDate(String eCreDate) {
		this.eCreDate = eCreDate;
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

	public List<Map<String, Object>> getAO84MList() {
		return AO84MList;
	}

	public void setAO84MList(List<Map<String, Object>> aO84MList) {
		AO84MList = aO84MList;
	}

	public String getFrom181() {
		return from181;
	}

	public void setFrom181(String from181) {
		this.from181 = from181;
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

	public List<Map<String, Object>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, Object>> exportList) {
		this.exportList = exportList;
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

	public String getDataMon() {
		return dataMon;
	}

	public void setDataMon(String dataMon) {
		this.dataMon = dataMon;
	}

}
