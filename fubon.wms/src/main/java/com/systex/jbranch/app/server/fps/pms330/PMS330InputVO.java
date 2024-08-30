package com.systex.jbranch.app.server.fps.pms330;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
/**
 * 
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專各級客戶數增減 InputVO<br>
 * Comments Name : PMS330InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年05月24日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS330InputVO extends PagingInputVO{

	
	private Date sCreDate;
	private String rc_id;   //區域中心
	private String op_id;	//營運區
	private String br_id;	//分行
	private String ao_code; //理專
	private String emp_id;	//員編
	private String previewType;         //報表類型-暫傳空白
	private String reportDate;          //YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag;              //只顯示理專
	private String psFlag;              //只顯示PS
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	
	
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
	public Date getsCreDate() {
		return sCreDate;
	}
	public String getRc_id() {
		return rc_id;
	}
	public String getOp_id() {
		return op_id;
	}
	public String getBr_id() {
		return br_id;
	}
	public String getAo_code() {
		return ao_code;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public void setRc_id(String rc_id) {
		this.rc_id = rc_id;
	}
	public void setOp_id(String op_id) {
		this.op_id = op_id;
	}
	public void setBr_id(String br_id) {
		this.br_id = br_id;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	

}
