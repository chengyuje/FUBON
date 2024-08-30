package com.systex.jbranch.app.server.fps.crm237;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM237InputVO extends CRM230InputVO{
	
	/**境外結構型商品  **/
	private String inv_crcy_type;               //商品幣別
	private String rate_guaranteepay;		    //保本率
	private String bond_nbr;					//商品代號
	private Date year_of_maturity;				//到期年度
	private String prod_name;					//商品名稱
	private String riskcate_id;					//商品風險等級
	private String investment_targets;			//連結標的類型
	private String sell_price_bgn;				//報價範圍
	private String sell_price_end;				//報價範圍
	private String currency_exchange;			//幣轉
	private String sdamt3_wd_bgn;				//含息報價範圍
	private String sdamt3_wd_end;				//含息報價範圍
	private String instition_of_flotation ;		//發行機構
	
	public String getInv_crcy_type() {
		return inv_crcy_type;
	}
	public void setInv_crcy_type(String inv_crcy_type) {
		this.inv_crcy_type = inv_crcy_type;
	}
	public String getRate_guaranteepay() {
		return rate_guaranteepay;
	}
	public void setRate_guaranteepay(String rate_guaranteepay) {
		this.rate_guaranteepay = rate_guaranteepay;
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
	public String getInvestment_targets() {
		return investment_targets;
	}
	public void setInvestment_targets(String investment_targets) {
		this.investment_targets = investment_targets;
	}
	public String getSell_price_bgn() {
		return sell_price_bgn;
	}
	public void setSell_price_bgn(String sell_price_bgn) {
		this.sell_price_bgn = sell_price_bgn;
	}
	public String getSell_price_end() {
		return sell_price_end;
	}
	public void setSell_price_end(String sell_price_end) {
		this.sell_price_end = sell_price_end;
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
	public String getInstition_of_flotation() {
		return instition_of_flotation;
	}
	public void setInstition_of_flotation(String instition_of_flotation) {
		this.instition_of_flotation = instition_of_flotation;
	}
}
