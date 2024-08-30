package com.systex.jbranch.app.server.fps.ins940;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS940OutputVO extends PagingInputVO {
	private BigDecimal para_no;
	private String cal_desc;
	private String status;
	private List diseaseList;
	private List ltcareList;
	private List suggestList;
	private List reportList;
	
	
	public BigDecimal getPara_no() {
		return para_no;
	}
	public void setPara_no(BigDecimal para_no) {
		this.para_no = para_no;
	}
	public String getCal_desc() {
		return cal_desc;
	}
	public void setCal_desc(String cal_desc) {
		this.cal_desc = cal_desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List getDiseaseList() {
		return diseaseList;
	}
	public void setDiseaseList(List diseaseList) {
		this.diseaseList = diseaseList;
	}
	public List getLtcareList() {
		return ltcareList;
	}
	public void setLtcareList(List ltcareList) {
		this.ltcareList = ltcareList;
	}
	public List getSuggestList() {
		return suggestList;
	}
	public void setSuggestList(List suggestList) {
		this.suggestList = suggestList;
	}
	public List getReportList() {
		return reportList;
	}
	public void setReportList(List reportList) {
		this.reportList = reportList;
	}
}