package com.systex.jbranch.app.server.fps.org460;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG460InputVO extends PagingInputVO{
	
	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
	
	private String AO_JOB_RANK = "";
	private String PERF_EFF_DATE  = "";
	private List<Map<String, String>> EXPORT_LST = null;
	
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

	public List<Map<String, String>> getEXPORT_LST() {
		return EXPORT_LST;
	}

	public void setEXPORT_LST(List<Map<String, String>> eXPORT_LST) {
		EXPORT_LST = eXPORT_LST;
	}

	public String getAO_JOB_RANK() {
		return AO_JOB_RANK;
	}
	
	public void setAO_JOB_RANK(String aO_JOB_RANK) {
		AO_JOB_RANK = aO_JOB_RANK;
	}
	
	public String getPERF_EFF_DATE() {
		return PERF_EFF_DATE;
	}
	
	public void setPERF_EFF_DATE(String pERF_EFF_DATE) {
		PERF_EFF_DATE = pERF_EFF_DATE;
	}
}
