package com.systex.jbranch.app.server.fps.pms416;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS416InputVO extends PagingInputVO {
	
	private String dataMon;
	private String region_center_id; // 業務處
	private String branch_area_id; // 營運區
	private String branch_nbr; // 分行
	private String aoCode; // 理專
	private String freqType; // 進出頻率
	private List<Map<String, String>> exportList;
	
	private String memLoginFlag;
	
	private List<Map<String, Object>> list;
	
	private String from181;
	
	public String getFrom181() {
		return from181;
	}

	public void setFrom181(String from181) {
		this.from181 = from181;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
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

	public String getDataMon() {
		return dataMon;
	}

	public void setDataMon(String dataMon) {
		this.dataMon = dataMon;
	}

	public String getFreqType() {
		return freqType;
	}

	public void setFreqType(String freqType) {
		this.freqType = freqType;
	}

	public List<Map<String, String>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, String>> exportList) {
		this.exportList = exportList;
	}

}
