package com.systex.jbranch.app.server.fps.org121;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG121InputVO extends PagingInputVO{
	
	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
	private String empID;
	private String ao_code;
	private String REVIEW_STATUS;
	
	private String typeOne;
	private Date aoPerfEffDate;
	private List<Map<String, Object>> aoList;
	
	private String uhrm_code;
	
	public String getUhrm_code() {
		return uhrm_code;
	}

	public void setUhrm_code(String uhrm_code) {
		this.uhrm_code = uhrm_code;
	}

	public Date getAoPerfEffDate() {
		return aoPerfEffDate;
	}

	public void setAoPerfEffDate(Date aoPerfEffDate) {
		this.aoPerfEffDate = aoPerfEffDate;
	}

	public String getTypeOne() {
		return typeOne;
	}

	public void setTypeOne(String typeOne) {
		this.typeOne = typeOne;
	}

	public List<Map<String, Object>> getAoList() {
		return aoList;
	}

	public void setAoList(List<Map<String, Object>> aoList) {
		this.aoList = aoList;
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

	public String getEmpID() {
		return empID;
	}
	
	public void setEmpID(String empID) {
		this.empID = empID;
	}
	
	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getREVIEW_STATUS() {
		return REVIEW_STATUS;
	}
	
	public void setREVIEW_STATUS(String rEVIEW_STATUS) {
		REVIEW_STATUS = rEVIEW_STATUS;
	}
	
}
