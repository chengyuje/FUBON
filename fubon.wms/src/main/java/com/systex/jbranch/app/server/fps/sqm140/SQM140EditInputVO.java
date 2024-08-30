package com.systex.jbranch.app.server.fps.sqm140;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM140EditInputVO extends PagingInputVO{	
	
	private String qtnType;
	private String seq;
	private String data_date;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String cust_id;
	private String cust_name;
	private String resp_note;
	private String qst_version;
	private String trade_date;
	
	public String getQtnType() {
		return qtnType;
	}

	public void setQtnType(String qtnType) {
		this.qtnType = qtnType;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getData_date() {
		return data_date;
	}

	public void setData_date(String data_date) {
		this.data_date = data_date;
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

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getResp_note() {
		return resp_note;
	}

	public void setResp_note(String resp_note) {
		this.resp_note = resp_note;
	}

	public String getQst_version() {
		return qst_version;
	}

	public void setQst_version(String qst_version) {
		this.qst_version = qst_version;
	}

	public String getTrade_date() {
		return trade_date;
	}

	public void setTrade_date(String trade_date) {
		this.trade_date = trade_date;
	}
	
			
}
