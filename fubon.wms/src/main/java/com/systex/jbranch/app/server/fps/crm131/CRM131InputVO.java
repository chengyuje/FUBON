package com.systex.jbranch.app.server.fps.crm131;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM131InputVO extends PagingInputVO {

	private String emp_id;
	private String mroleid;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String TAKE_CARE_MATCH_YN;
	private String TAKE_CARE_DUE_YN;
	private List<Map<String, Object>> resultList;
	private String pri_id;

	private String memLoginFlag;

	public String getPri_id() {
		return pri_id;
	}

	public void setPri_id(String pri_id) {
		this.pri_id = pri_id;
	}

	public String getMemLoginFlag() {
		return memLoginFlag;
	}

	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getMroleid() {
		return mroleid;
	}

	public void setMroleid(String mroleid) {
		this.mroleid = mroleid;
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

	public String getTAKE_CARE_MATCH_YN() {
		return TAKE_CARE_MATCH_YN;
	}

	public void setTAKE_CARE_MATCH_YN(String tAKE_CARE_MATCH_YN) {
		TAKE_CARE_MATCH_YN = tAKE_CARE_MATCH_YN;
	}

	public String getTAKE_CARE_DUE_YN() {
		return TAKE_CARE_DUE_YN;
	}

	public void setTAKE_CARE_DUE_YN(String tAKE_CARE_DUE_YN) {
		TAKE_CARE_DUE_YN = tAKE_CARE_DUE_YN;
	}

}
