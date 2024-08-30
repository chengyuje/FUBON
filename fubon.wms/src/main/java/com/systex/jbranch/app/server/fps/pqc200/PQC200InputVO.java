package com.systex.jbranch.app.server.fps.pqc200;

import java.math.BigDecimal;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PQC200InputVO extends PagingInputVO {

	// PQC200
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String u_emp_Id;

	private String prdType;
	private String prdID;
	private String reportType;
	
	private String SEQNO;
	
	// PQC200_APPLY
	private String searchPrdType;
	private String searchPrdID;
	private String searchStartDate;
	private String searchEndDate;
	
	private String addStartDate;
	private String addEndDate;
	private String TOTAL_QUOTA_TYPE;
	private String TOTAL_QUOTA;
	
	private String custId;
	private String custName;
	
	private BigDecimal applyQuota;
	
	// PQC200_
	
	public String getAddStartDate() {
		return addStartDate;
	}

	public String getU_emp_Id() {
		return u_emp_Id;
	}

	public void setU_emp_Id(String u_emp_Id) {
		this.u_emp_Id = u_emp_Id;
	}

	public String getSEQNO() {
		return SEQNO;
	}

	public void setSEQNO(String sEQNO) {
		SEQNO = sEQNO;
	}

	public BigDecimal getApplyQuota() {
		return applyQuota;
	}

	public void setApplyQuota(BigDecimal applyQuota) {
		this.applyQuota = applyQuota;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public void setAddStartDate(String addStartDate) {
		this.addStartDate = addStartDate;
	}

	public String getAddEndDate() {
		return addEndDate;
	}

	public void setAddEndDate(String addEndDate) {
		this.addEndDate = addEndDate;
	}

	public String getTOTAL_QUOTA_TYPE() {
		return TOTAL_QUOTA_TYPE;
	}

	public void setTOTAL_QUOTA_TYPE(String tOTAL_QUOTA_TYPE) {
		TOTAL_QUOTA_TYPE = tOTAL_QUOTA_TYPE;
	}

	public String getTOTAL_QUOTA() {
		return TOTAL_QUOTA;
	}

	public void setTOTAL_QUOTA(String tOTAL_QUOTA) {
		TOTAL_QUOTA = tOTAL_QUOTA;
	}

	public String getSearchPrdType() {
		return searchPrdType;
	}

	public void setSearchPrdType(String searchPrdType) {
		this.searchPrdType = searchPrdType;
	}

	public String getSearchPrdID() {
		return searchPrdID;
	}

	public void setSearchPrdID(String searchPrdID) {
		this.searchPrdID = searchPrdID;
	}

	public String getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(String searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public String getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(String searchEndDate) {
		this.searchEndDate = searchEndDate;
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

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getPrdType() {
		return prdType;
	}

	public void setPrdType(String prdType) {
		this.prdType = prdType;
	}

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

}
