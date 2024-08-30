package com.systex.jbranch.app.server.fps.pms302;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專即時速報InputVO<br>
 * Comments Name : PMS303InputVO.java<br>
 * Author :frank<br>
 * Date :2016年07月01日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS302InputVO extends PagingInputVO {
	private Date sCreDate;
	private String data_date;
	//新版可視範圍使用
	private String previewType;         //報表類型-暫傳空白
	private String reportDate;          //YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag;              //只顯示理專
	private String psFlag;              //只顯示PS
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	//新版可視範圍 end
	private String ao_code;
	private String emp_id;
	private  String srchType  ;// 當日、MTD
	private  String tx_ym  ;// MTD帶入月份
	
	
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

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getData_date() {
		return data_date;
	}

	public void setData_date(String data_date) {
		this.data_date = data_date;
	}

	public String getSrchType() {
		return srchType;
	}

	public void setSrchType(String srchType) {
		this.srchType = srchType;
	}

	public String getTx_ym() {
		return tx_ym;
	}

	public void setTx_ym(String tx_ym) {
		this.tx_ym = tx_ym;
	}
	
}
