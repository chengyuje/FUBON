package com.systex.jbranch.app.server.fps.pms365;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS365InputVO extends PagingInputVO {

	private String reportDate;
	private String sCreDate;

	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	
	private String yyyymm;
	private String aoCode;
	private String aumTime;
	private String aumType;
	private String branchNbr;

	private List<Map<String, Object>> exportList;
	
	private String NOT_EXIST_UHRM;	// 排除高端
	private String NOT_EXIST_BS;	// 排除銀證
	
	private String memLoginFlag;
	
	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public String getNOT_EXIST_UHRM() {
		return NOT_EXIST_UHRM;
	}

	public void setNOT_EXIST_UHRM(String nOT_EXIST_UHRM) {
		NOT_EXIST_UHRM = nOT_EXIST_UHRM;
	}

	public String getNOT_EXIST_BS() {
		return NOT_EXIST_BS;
	}

	public void setNOT_EXIST_BS(String nOT_EXIST_BS) {
		NOT_EXIST_BS = nOT_EXIST_BS;
	}

	public List<Map<String, Object>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, Object>> exportList) {
		this.exportList = exportList;
	}

	public String getBranchNbr() {
		return branchNbr;
	}

	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}

	public String getYyyymm() {
		return yyyymm;
	}

	public void setYyyymm(String yyyymm) {
		this.yyyymm = yyyymm;
	}

	public String getAoCode() {
		return aoCode;
	}

	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}

	public String getAumTime() {
		return aumTime;
	}

	public void setAumTime(String aumTime) {
		this.aumTime = aumTime;
	}

	public String getAumType() {
		return aumType;
	}

	public void setAumType(String aumType) {
		this.aumType = aumType;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
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

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

}
