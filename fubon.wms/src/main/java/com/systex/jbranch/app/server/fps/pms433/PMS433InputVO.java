package com.systex.jbranch.app.server.fps.pms433;

import java.util.List;

public class PMS433InputVO {
	private String sCreDate; //資料統計月份
	private String trade_source; //交易管道
	private String call_result; //外撥結果
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String reportDate; // YYYYMMDD(日報) YYYYMM(月報)
	private String selected_date; // 
	private String cust_id; // 
	
	private List resultList; //主查詢
	
	//明細查詢
	private String seq; //流水號
	
	//更新
	private List modifyList;
	
	public String getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}
	public String getTrade_source() {
		return trade_source;
	}
	public void setTrade_source(String trade_source) {
		this.trade_source = trade_source;
	}
	public String getCall_result() {
		return call_result;
	}
	public void setCall_result(String call_result) {
		this.call_result = call_result;
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
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public List getModifyList() {
		return modifyList;
	}
	public void setModifyList(List modifyList) {
		this.modifyList = modifyList;
	}
	public String getSelected_date() {
		return selected_date;
	}
	public void setSelected_date(String selected_date) {
		this.selected_date = selected_date;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	
}
