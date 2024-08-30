package com.systex.jbranch.app.server.fps.pms309;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS309InputVO extends PagingInputVO{	
	private String srchDate;
	private Date sCreDate;
	private String dataMonth_S;
	private String dataMonth_E;
	private String rc_id;    
	private String op_id;
	private String br_id;
	private String srchType;
	
	private String previewType;         //報表類型-暫傳空白
	private String reportDate;          //YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag;              //只顯示理專
	private String psFlag;              //只顯示PS
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	
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
	public String getPreviewType() {
		return previewType;
	}
	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
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
	public String getSrchDate() {
		return srchDate;
	}
	public void setSrchDate(String srchDate) {
		this.srchDate = srchDate;
	}
	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public String getDataMonth_S() {
		return dataMonth_S;
	}
	public void setDataMonth_S(String dataMonth_S) {
		this.dataMonth_S = dataMonth_S;
	}
	public String getDataMonth_E() {
		return dataMonth_E;
	}
	public void setDataMonth_E(String dataMonth_E) {
		this.dataMonth_E = dataMonth_E;
	}
	public String getRc_id() {
		return rc_id;
	}
	public void setRc_id(String rc_id) {
		this.rc_id = rc_id;
	}
	public String getOp_id() {
		return op_id;
	}
	public void setOp_id(String op_id) {
		this.op_id = op_id;
	}
	public String getBr_id() {
		return br_id;
	}
	public void setBr_id(String br_id) {
		this.br_id = br_id;
	}
	public String getSrchType() {
		return srchType;
	}
	public void setSrchType(String srchType) {
		this.srchType = srchType;
	}	
	
}
