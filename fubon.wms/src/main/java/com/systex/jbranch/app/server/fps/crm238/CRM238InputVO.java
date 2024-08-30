package com.systex.jbranch.app.server.fps.crm238;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM238InputVO extends CRM230InputVO{
	
	//	外匯雙享利(DCI)
	private String valu_crcy_type;               //商品幣別
	private String mpng_crcy_type;               //相對幣別
	private String prod_id;                      //商品代號
	private String hedging_yn;                   //保值/不保值
	private String transfer_yn;                  //是否轉換
	private Date expiry_date_bgn;				 //比價日(起)
	private Date expiry_date_end;				 //比價日(迄)
	private Date due_date_bgn;                   //到期日(起)
	private Date due_date_end;                   //到期日(迄)
	private String risk_level;                   //商品風險等級
	
	public String getValu_crcy_type() {
		return valu_crcy_type;
	}
	public void setValu_crcy_type(String valu_crcy_type) {
		this.valu_crcy_type = valu_crcy_type;
	}
	public String getMpng_crcy_type() {
		return mpng_crcy_type;
	}
	public void setMpng_crcy_type(String mpng_crcy_type) {
		this.mpng_crcy_type = mpng_crcy_type;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getHedging_yn() {
		return hedging_yn;
	}
	public void setHedging_yn(String hedging_yn) {
		this.hedging_yn = hedging_yn;
	}
	public String getTransfer_yn() {
		return transfer_yn;
	}
	public void setTransfer_yn(String transfer_yn) {
		this.transfer_yn = transfer_yn;
	}
	public Date getExpiry_date_bgn() {
		return expiry_date_bgn;
	}
	public void setExpiry_date_bgn(Date expiry_date_bgn) {
		this.expiry_date_bgn = expiry_date_bgn;
	}
	public Date getExpiry_date_end() {
		return expiry_date_end;
	}
	public void setExpiry_date_end(Date expiry_date_end) {
		this.expiry_date_end = expiry_date_end;
	}
	public Date getDue_date_bgn() {
		return due_date_bgn;
	}
	public void setDue_date_bgn(Date due_date_bgn) {
		this.due_date_bgn = due_date_bgn;
	}
	public Date getDue_date_end() {
		return due_date_end;
	}
	public void setDue_date_end(Date due_date_end) {
		this.due_date_end = due_date_end;
	}
	public String getRisk_level() {
		return risk_level;
	}
	public void setRisk_level(String risk_level) {
		this.risk_level = risk_level;
	}
	
}
