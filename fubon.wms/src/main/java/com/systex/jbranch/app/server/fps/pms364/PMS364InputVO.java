package com.systex.jbranch.app.server.fps.pms364;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS364InputVO extends PagingInputVO {
	
	private String sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	
	private String NOT_EXIST_UHRM;	// 排除高端
	private String NOT_EXIST_BS;	// 排除銀證
	
	private String yyyymm;
	private String branchNbr;
	private String custAO;
	private String type;
	
	private List<Map<String, Object>> exportDtlList;
	private List<Map<String, String>> exportList;

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

	public List<Map<String, String>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, String>> exportList) {
		this.exportList = exportList;
	}

	public List<Map<String, Object>> getExportDtlList() {
		return exportDtlList;
	}

	public void setExportDtlList(List<Map<String, Object>> exportDtlList) {
		this.exportDtlList = exportDtlList;
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

	public String getCustAO() {
		return custAO;
	}

	public void setCustAO(String custAO) {
		this.custAO = custAO;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
