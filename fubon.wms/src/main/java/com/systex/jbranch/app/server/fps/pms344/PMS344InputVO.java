package com.systex.jbranch.app.server.fps.pms344;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS344InputVO extends PagingInputVO {

	private String element; // 查詢 table 名稱
	private String flag;

	
	private String eTime; // 年月
	/***** 可試範圍專用START *****/
	private String previewType;         //報表類型-暫傳空白
	private String reportDate;          //YYYYMMDD(日報) YYYYMM(月報)
	private String aoFlag;              //只顯示理專
	private String psFlag;              //只顯示PS
	private Date sCreDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String ao_code;
	private String dataMonth; // 日期

	/***** 可試範圍專用END *****/
	
	

	public String geteTime() {
		return eTime;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public void seteTime(String eTime) {
		this.eTime = eTime;
	}


	public String getPreviewType() {
		return previewType;
	}

	public String getReportDate() {
		return reportDate;
	}

	public String getAoFlag() {
		return aoFlag;
	}

	public String getPsFlag() {
		return psFlag;
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

	public String getAo_code() {
		return ao_code;
	}

	public String getDataMonth() {
		return dataMonth;
	}

	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public void setAoFlag(String aoFlag) {
		this.aoFlag = aoFlag;
	}

	public void setPsFlag(String psFlag) {
		this.psFlag = psFlag;
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

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public void setDataMonth(String dataMonth) {
		this.dataMonth = dataMonth;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}


}
