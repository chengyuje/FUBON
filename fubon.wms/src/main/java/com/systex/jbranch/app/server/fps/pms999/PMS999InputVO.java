package com.systex.jbranch.app.server.fps.pms999;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS999InputVO extends PagingInputVO {

	private Date sDate;
	private Date eDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String reportToTable;
	
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

	public Date getsDate() {
		return sDate;
	}

	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}

	public Date geteDate() {
		return eDate;
	}

	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}

	public String getReportToTable() {
		return reportToTable;
	}

	public void setReportToTable(String reportToTable) {
		this.reportToTable = reportToTable;
	}

}
