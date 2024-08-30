package com.systex.jbranch.app.server.fps.crm231;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM231InputVO extends CRM230InputVO{
	//所有商品
	private String prod_type;         //商品類別
	private String rtn_rate_wd_bgn;   //報酬率(%)(起)
	private String rtn_rate_wd_end;   //報酬率(%)(迄)
	private String prod_id;           //商品代碼
	private String now_amt_twd_bgn;   //台幣參考現值(起)
	private String now_amt_twd_end;   //台幣參考現值(迄)
	private String prod_name;         //商品名稱
	private String cur_id;            //投資幣別
	private String take_prft_pt_bgn;  //客戶停利點(%)(起)
	private String take_prft_pt_end;  //客戶停利點(%)(迄)
	private String stop_loss_pt_bgn;  //客戶停損點(%)(起)
	private String stop_loss_pt_end;  //客戶停損點(%)(迄)
	
//	//信用卡
//	private String hold_card_flg;     //是否持有有效信用卡
//	private String ms_type;           //持有正/附卡
//	private Date spec_date;           //特定日期
//	private String opp_ms_type;       //無有效...者
//	private String spec_check_yn;     //有無特定日期
	
	private Boolean prod_flag;
	
	public String getProd_type() {
		return prod_type;
	}
	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
	}
	public String getRtn_rate_wd_bgn() {
		return rtn_rate_wd_bgn;
	}
	public void setRtn_rate_wd_bgn(String rtn_rate_wd_bgn) {
		this.rtn_rate_wd_bgn = rtn_rate_wd_bgn;
	}
	public String getRtn_rate_wd_end() {
		return rtn_rate_wd_end;
	}
	public void setRtn_rate_wd_end(String rtn_rate_wd_end) {
		this.rtn_rate_wd_end = rtn_rate_wd_end;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getNow_amt_twd_bgn() {
		return now_amt_twd_bgn;
	}
	public void setNow_amt_twd_bgn(String now_amt_twd_bgn) {
		this.now_amt_twd_bgn = now_amt_twd_bgn;
	}
	public String getNow_amt_twd_end() {
		return now_amt_twd_end;
	}
	public void setNow_amt_twd_end(String now_amt_twd_end) {
		this.now_amt_twd_end = now_amt_twd_end;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public String getCur_id() {
		return cur_id;
	}
	public void setCur_id(String cur_id) {
		this.cur_id = cur_id;
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
//	public String getHold_card_flg() {
//		return hold_card_flg;
//	}
//	public void setHold_card_flg(String hold_card_flg) {
//		this.hold_card_flg = hold_card_flg;
//	}
//	public String getMs_type() {
//		return ms_type;
//	}
//	public void setMs_type(String ms_type) {
//		this.ms_type = ms_type;
//	}
//	public Date getSpec_date() {
//		return spec_date;
//	}
//	public void setSpec_date(Date spec_date) {
//		this.spec_date = spec_date;
//	}
//	public String getOpp_ms_type() {
//		return opp_ms_type;
//	}
//	public void setOpp_ms_type(String opp_ms_type) {
//		this.opp_ms_type = opp_ms_type;
//	}
//	public String getSpec_check_yn() {
//		return spec_check_yn;
//	}
//	public void setSpec_check_yn(String spec_check_yn) {
//		this.spec_check_yn = spec_check_yn;
//	}
	public Boolean getProd_flag() {
		return prod_flag;
	}
	public void setProd_flag(Boolean prod_flag) {
		this.prod_flag = prod_flag;
	}
	
}
