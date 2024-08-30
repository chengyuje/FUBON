package com.systex.jbranch.app.server.fps.pms415;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS415InputVO extends PagingInputVO {
	private Date applyDateS;
	private Date applyDateE;
	private String region_center_id; // 業務處
	private String branch_area_id;   // 營運區
	private String branch_nbr;       // 分行
	private String ao_code;          // 理專
	private String cust_id;          // 要保人
	private String ins_id;           // 保險文件編號
	private String emp_id;           // 員編
	private String reportDate;
	// 新版可視範圍用
	private String previewType; // 報表類型-暫傳空白
	private String aoFlag; // 只顯示理專
	private String psFlag; // 只顯示PS
	
	public Date getApplyDateS() {
		return applyDateS;
	}
	public void setApplyDateS(Date applyDateS) {
		this.applyDateS = applyDateS;
	}
	public Date getApplyDateE() {
		return applyDateE;
	}
	public void setApplyDateE(Date applyDateE) {
		this.applyDateE = applyDateE;
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
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getIns_id() {
		return ins_id;
	}
	public void setIns_id(String ins_id) {
		this.ins_id = ins_id;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public String getPreviewType() {
		return previewType;
	}
	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}
	public String getAoFlag() {
		return aoFlag;
	}
	public void setAoFlag(String aoFlag) {
		this.aoFlag = aoFlag;
	}
	public String getPsFlag() {
		return psFlag;
	}
	public void setPsFlag(String psFlag) {
		this.psFlag = psFlag;
	}

}
