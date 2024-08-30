package com.systex.jbranch.app.server.fps.pms417;

import java.util.List; 
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS417InputVO extends PagingInputVO {

//	private String dataMon;
	
	private String importSDate;
	private String importEDate;
	
	private String region_center_id; // 業務處
	private String branch_area_id; // 營運區
	private String branch_nbr; // 分行
	private String aoCode; // 理專
	private String memLoginFlag;

	private List<Map<String, String>> exportList;
	
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

	public String getAoCode() {
		return aoCode;
	}

	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}

	public List<Map<String, String>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, String>> exportList) {
		this.exportList = exportList;
	}

}
