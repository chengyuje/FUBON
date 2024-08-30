package com.systex.jbranch.app.server.fps.ins910;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS910OutputVO extends PagingOutputVO{
	private BigDecimal para_no;
	private String cal_desc;
	private String status;
	private List suggestList;
	private List reportList;
	private String insdata_key_no;
	// old code
	private List resultList;
	
	
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
	public String getInsdata_key_no() {
		return insdata_key_no;
	}
	public void setInsdata_key_no(String insdata_key_no) {
		this.insdata_key_no = insdata_key_no;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}