package com.systex.jbranch.app.server.fps.crm235;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM235InputVO extends CRM230InputVO {

	//海外債商品篩選
	private String currency_std_id;           //商品幣別
	private String bond_nbr;                  //商品代號
	private Date year_of_maturity;            //到期年度
	private String prod_name;                 //商品名稱
	private String riskcate_id;               //商品風險等級
	private String instition_of_flotation;    //發行機構
	private String bond_cate_id;              //債券類型
	private String rtn_rate_wd_min;   		  //報酬率(%)(起)
	private String rtn_rate_wd_max;  		  //報酬率(%)(迄)
	
	public String getCurrency_std_id() {
		return currency_std_id;
	}
	public void setCurrency_std_id(String currency_std_id) {
		this.currency_std_id = currency_std_id;
	}
	public String getBond_nbr() {
		return bond_nbr;
	}
	public void setBond_nbr(String bond_nbr) {
		this.bond_nbr = bond_nbr;
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
	public String getInstition_of_flotation() {
		return instition_of_flotation;
	}
	public void setInstition_of_flotation(String instition_of_flotation) {
		this.instition_of_flotation = instition_of_flotation;
	}
	public String getBond_cate_id() {
		return bond_cate_id;
	}
	public void setBond_cate_id(String bond_cate_id) {
		this.bond_cate_id = bond_cate_id;
	}
	public String getRtn_rate_wd_min() {
		return rtn_rate_wd_min;
	}
	public void setRtn_rate_wd_min(String rtn_rate_wd_min) {
		this.rtn_rate_wd_min = rtn_rate_wd_min;
	}
	public String getRtn_rate_wd_max() {
		return rtn_rate_wd_max;
	}
	public void setRtn_rate_wd_max(String rtn_rate_wd_max) {
		this.rtn_rate_wd_max = rtn_rate_wd_max;
	}
	
}
