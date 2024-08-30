package com.systex.jbranch.app.server.fps.pms107;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS107InputVO extends PagingInputVO {

	private String sTime;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String EMP_ID;
	private String YEARMON;
	private String supb;
	private String sup;
	private String faia;
	private String reportDate;
	

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getSupb() {
		return supb;
	}

	public String getSup() {
		return sup;
	}

	public void setSupb(String supb) {
		this.supb = supb;
	}

	public void setSup(String sup) {
		this.sup = sup;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public String getYEARMON() {
		return YEARMON;
	}

	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public void setYEARMON(String yEARMON) {
		YEARMON = yEARMON;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
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

	public String getFaia() {
		return faia;
	}

	public void setFaia(String faia) {
		this.faia = faia;
	}

	
}
