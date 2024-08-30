package com.systex.jbranch.app.server.fps.crm234;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM234InputVO extends CRM230InputVO{
	
	private Date txn_date_bgn;                //成交日期(起)
	private Date txn_date_end;                //成交日期(迄)
	private String prod_name;                 //商品名稱
	private String stock_code;                //交易所
	private String prod_id;                   //商品代碼
	private String prd_type;                  //投資標的類型
	private String currency_std_id;           //商品幣別
	private String stock_area;                //投資區域
	private String riskcate_id;               //商品風險等級
	private String stock_attribute;           //商品類型
	private String ref_abs_ret_rate_twd_bgn;  //報酬率(起)
	private String ref_abs_ret_rate_twd_end;  //報酬率(迄)
	private String pi_buy;                    //專業投資人商品
	private String take_prft_pt_bgn;          //客戶停損點(%)(起)
	private String take_prft_pt_end;          //客戶停損點(%)(迄)
	private String com27;                     //信託類別
	private String stop_loss_pt_bgn;          //客戶停利點(%)(起)
	private String stop_loss_pt_end;          //客戶停利點(%)(迄)
	
	public Date getTxn_date_bgn() {
		return txn_date_bgn;
	}
	public void setTxn_date_bgn(Date txn_date_bgn) {
		this.txn_date_bgn = txn_date_bgn;
	}
	public Date getTxn_date_end() {
		return txn_date_end;
	}
	public void setTxn_date_end(Date txn_date_end) {
		this.txn_date_end = txn_date_end;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public String getStock_code() {
		return stock_code;
	}
	public void setStock_code(String stock_code) {
		this.stock_code = stock_code;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getPrd_type() {
		return prd_type;
	}
	public void setPrd_type(String prd_type) {
		this.prd_type = prd_type;
	}
	public String getCurrency_std_id() {
		return currency_std_id;
	}
	public void setCurrency_std_id(String currency_std_id) {
		this.currency_std_id = currency_std_id;
	}
	public String getStock_area() {
		return stock_area;
	}
	public void setStock_area(String stock_area) {
		this.stock_area = stock_area;
	}
	public String getRiskcate_id() {
		return riskcate_id;
	}
	public void setRiskcate_id(String riskcate_id) {
		this.riskcate_id = riskcate_id;
	}
	public String getStock_attribute() {
		return stock_attribute;
	}
	public void setStock_attribute(String stock_attribute) {
		this.stock_attribute = stock_attribute;
	}
	public String getRef_abs_ret_rate_twd_bgn() {
		return ref_abs_ret_rate_twd_bgn;
	}
	public void setRef_abs_ret_rate_twd_bgn(String ref_abs_ret_rate_twd_bgn) {
		this.ref_abs_ret_rate_twd_bgn = ref_abs_ret_rate_twd_bgn;
	}
	public String getRef_abs_ret_rate_twd_end() {
		return ref_abs_ret_rate_twd_end;
	}
	public void setRef_abs_ret_rate_twd_end(String ref_abs_ret_rate_twd_end) {
		this.ref_abs_ret_rate_twd_end = ref_abs_ret_rate_twd_end;
	}
	public String getPi_buy() {
		return pi_buy;
	}
	public void setPi_buy(String pi_buy) {
		this.pi_buy = pi_buy;
	}
	public String getTake_prft_pt_bgn() {
		return take_prft_pt_bgn;
	}
	public void setTake_prft_pt_bgn(String take_prft_pt_bgn) {
		this.take_prft_pt_bgn = take_prft_pt_bgn;
	}
	public String getTake_prft_pt_end() {
		return take_prft_pt_end;
	}
	public void setTake_prft_pt_end(String take_prft_pt_end) {
		this.take_prft_pt_end = take_prft_pt_end;
	}
	public String getCom27() {
		return com27;
	}
	public void setCom27(String com27) {
		this.com27 = com27;
	}
	public String getStop_loss_pt_bgn() {
		return stop_loss_pt_bgn;
	}
	public void setStop_loss_pt_bgn(String stop_loss_pt_bgn) {
		this.stop_loss_pt_bgn = stop_loss_pt_bgn;
	}
	public String getStop_loss_pt_end() {
		return stop_loss_pt_end;
	}
	public void setStop_loss_pt_end(String stop_loss_pt_end) {
		this.stop_loss_pt_end = stop_loss_pt_end;
	}
}
