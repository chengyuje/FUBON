package com.systex.jbranch.app.server.fps.crm236;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM236InputVO extends CRM230InputVO{
	
	//組合式商品
	private String crcy_type;             //商品幣別
	private String rate_guaranteepay;     //保本率
	private String prod_id;               //商品代號
	private Date year_of_maturity;        //到期年度
	private String prod_name;             //商品名稱
	private String riskcate_id;           //商品風險等級
	private String inv_target_type;       //連結標的類型
	private String sdamt3_bgn;            //報價範圍
	private String sdamt3_end;            //報價範圍
	private String currency_exchange;     //幣轉
	private String sdamt3_wd_bgn;         //含息報價範圍
	private String sdamt3_wd_end;         //含息報價範圍
	
	public String getCrcy_type() {
		return crcy_type;
	}
	public void setCrcy_type(String crcy_type) {
		this.crcy_type = crcy_type;
	}
	public String getRate_guaranteepay() {
		return rate_guaranteepay;
	}
	public void setRate_guaranteepay(String rate_guaranteepay) {
		this.rate_guaranteepay = rate_guaranteepay;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public Date getYear_of_maturity() {
		return year_of_maturity;
	}
	public void setYear_of_maturity(Date year_of_maturity) {
		this.year_of_maturity = year_of_maturity;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public String getRiskcate_id() {
		return riskcate_id;
	}
	public void setRiskcate_id(String riskcate_id) {
		this.riskcate_id = riskcate_id;
	}
	public String getInv_target_type() {
		return inv_target_type;
	}
	public void setInv_target_type(String inv_target_type) {
		this.inv_target_type = inv_target_type;
	}
	public String getSdamt3_bgn() {
		return sdamt3_bgn;
	}
	public void setSdamt3_bgn(String sdamt3_bgn) {
		this.sdamt3_bgn = sdamt3_bgn;
	}
	public String getSdamt3_end() {
		return sdamt3_end;
	}
	public void setSdamt3_end(String sdamt3_end) {
		this.sdamt3_end = sdamt3_end;
	}
	public String getCurrency_exchange() {
		return currency_exchange;
	}
	public void setCurrency_exchange(String currency_exchange) {
		this.currency_exchange = currency_exchange;
	}
	public String getSdamt3_wd_bgn() {
		return sdamt3_wd_bgn;
	}
	public void setSdamt3_wd_bgn(String sdamt3_wd_bgn) {
		this.sdamt3_wd_bgn = sdamt3_wd_bgn;
	}
	public String getSdamt3_wd_end() {
		return sdamt3_wd_end;
	}
	public void setSdamt3_wd_end(String sdamt3_wd_end) {
		this.sdamt3_wd_end = sdamt3_wd_end;
	}
	
}
