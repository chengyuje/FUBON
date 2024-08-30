package com.systex.jbranch.app.server.fps.org270;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/*
 * #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
 */
public class ORG270InputVO extends PagingInputVO {

	private String FILE_NAME;
	private String ACTUAL_FILE_NAME;
	private List<Map<String, Object>> TEAM_LST;
	
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	
	private List<Map<String, Object>> exportList;

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

	public List<Map<String, Object>> getTEAM_LST() {
		return TEAM_LST;
	}

	public void setTEAM_LST(List<Map<String, Object>> tEAM_LST) {
		TEAM_LST = tEAM_LST;
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

}
