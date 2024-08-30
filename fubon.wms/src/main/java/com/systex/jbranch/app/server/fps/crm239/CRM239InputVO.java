package com.systex.jbranch.app.server.fps.crm239;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM239InputVO extends CRM230InputVO{
	
	/** 保險商品  **/
	private String prd_id;                  //保險代碼
	private String crcy_type;               //幣別
	private String prod_name;               //保險名稱
	private String term_cnt;                //檔期
	private String inv_target_no;           //投資型連結標的代碼
	private Date policy_active_date_s;		//到期年度
	private Date policy_active_date_e;		//到期年度
	private String ins_source;				//保險進件來源
	private String ins_companyNum;			//保險公司代碼
	private String ins_type;				//險種類別
	
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getCrcy_type() {
		return crcy_type;
	}
	public void setCrcy_type(String crcy_type) {
		this.crcy_type = crcy_type;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public String getTerm_cnt() {
		return term_cnt;
	}
	public void setTerm_cnt(String term_cnt) {
		this.term_cnt = term_cnt;
	}
	public String getInv_target_no() {
		return inv_target_no;
	}
	public void setInv_target_no(String inv_target_no) {
		this.inv_target_no = inv_target_no;
	}
	public Date getPolicy_active_date_s() {
		return policy_active_date_s;
	}
	public void setPolicy_active_date_s(Date policy_active_date_s) {
		this.policy_active_date_s = policy_active_date_s;
	}
	public Date getPolicy_active_date_e() {
		return policy_active_date_e;
	}
	public void setPolicy_active_date_e(Date policy_active_date_e) {
		this.policy_active_date_e = policy_active_date_e;
	}
	public String getIns_source() {
		return ins_source;
	}
	public void setIns_source(String ins_source) {
		this.ins_source = ins_source;
	}
	public String getIns_companyNum() {
		return ins_companyNum;
	}
	public void setIns_companyNum(String ins_companyNum) {
		this.ins_companyNum = ins_companyNum;
	}
	public String getIns_type() {
		return ins_type;
	}
	public void setIns_type(String ins_type) {
		this.ins_type = ins_type;
	}
	
}
