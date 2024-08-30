package com.systex.jbranch.app.server.fps.pms407;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS407InputVO extends PagingInputVO {
	private Date rptYearMon;    
    private String fileName;
    private String ao_code      ;// 理專
	private String branch_nbr ;// 分行
	private String region_center_id ;// 區域中心
	private String branch_area_id  ;// 營運區
	private String sCreDate ;// 時間
	private String ptype;
	
	
	public Date getRptYearMon() {
		return rptYearMon;
	}
	public void setRptYearMon(Date rptYearMon) {
		this.rptYearMon = rptYearMon;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
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
	public String getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
}