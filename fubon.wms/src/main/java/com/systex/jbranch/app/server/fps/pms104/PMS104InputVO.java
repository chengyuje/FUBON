package com.systex.jbranch.app.server.fps.pms104;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS104InputVO extends PagingInputVO{	
	private String sTime;
	private String sCreDate;
	private String EMP_ID;	//員編
	private String custID;	//客戶id
	
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String reportDate;
	private String DATA_DATE;


	public String getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}
	
	public String getDATA_DATE() {
		return DATA_DATE;
	}
	public void setDATA_DATE(String dATA_DATE) {
		DATA_DATE = dATA_DATE;
	}
	
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	
	
	public String getsTime() {
		return sTime;
	}
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
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

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	
	
	
	
	
}
