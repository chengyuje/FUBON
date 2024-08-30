package com.systex.jbranch.app.server.fps.pms349;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS349InputVO extends PagingInputVO {

	private String EMP_ID; // 員編
	private String LOSS_CAT;
	private String DATA_DATE;
	private String AO_CODE;
	private String ao_code; // 理專
	private Date eCreDate; // 起日
	private String region_center_id; // 區域中心
	private String branch_area_id; // 營運區
	private String branch_nbr; // 分行
	private String reportDate;
	private String cust_id; //客戶ID
	

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getEMP_ID() {
		return EMP_ID;
	}

	public String getLOSS_CAT() {
		return LOSS_CAT;
	}

	public void setLOSS_CAT(String lOSS_CAT) {
		LOSS_CAT = lOSS_CAT;
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


	public String getAo_code() {
		return ao_code;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public String getRegion_center_id() {
		return region_center_id;
	}

	public String getBranch_area_id() {
		return branch_area_id;
	}

	public String getBranch_nbr() {
		return branch_nbr;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}

	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}

	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public String getAO_CODE() {
		return AO_CODE;
	}

	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

}
