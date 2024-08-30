package com.systex.jbranch.app.server.fps.crm232;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM232InputVO extends CRM230InputVO {

	//基金商品篩選
	
	private String tier1; 					 	//類別
	private String tier2;  						//投資市場別
	private String tier3;  						//投資市場別
	private String inv_way_nbr;  				//投資方法
	private String deduct_yn;  					//扣款狀態
	private String fund_cname;  				//主基金名稱
	private String ref_abs_ret_rate_twd_min;  	//報酬率
	private String ref_abs_ret_rate_twd_max;  	//報酬率
	private String lipper_id;  					//主基金代碼
	private String rtn_rate_wd_min;  			//含息報酬率
	private String rtn_rate_wd_max;  			//含息報酬率
	private String fundcname_a;  				//基金名稱
	private String stop_loss_pt_min;  			//客戶停損點
	private String stop_loss_pt_max;  			//客戶停損點
	private String fund_code;  					//基金代碼
	private String take_prft_pt_min;  			//客戶停利點
	private String take_prft_pt_max;  			//客戶停利點
	private String valu_crcy_type;  			//計價幣別
	private String dividend_frequency;  		//配息頻率
	private String inv_crcy_type;  				//信託類別
	private String riskcate_id;  				//商品風險等級
	private Date sign_date_bgn;  				//申購區間
	private Date sign_date_end;  				//申購區間
	
	public String getTier1() {
		return tier1;
	}
	public void setTier1(String tier1) {
		this.tier1 = tier1;
	}
	public String getTier2() {
		return tier2;
	}
	public void setTier2(String tier2) {
		this.tier2 = tier2;
	}
	public String getTier3() {
		return tier3;
	}
	public void setTier3(String tier3) {
		this.tier3 = tier3;
	}
	public String getInv_way_nbr() {
		return inv_way_nbr;
	}
	public void setInv_way_nbr(String inv_way_nbr) {
		this.inv_way_nbr = inv_way_nbr;
	}
	public String getDeduct_yn() {
		return deduct_yn;
	}
	public void setDeduct_yn(String deduct_yn) {
		this.deduct_yn = deduct_yn;
	}
	public String getFund_cname() {
		return fund_cname;
	}
	public void setFund_cname(String fund_cname) {
		this.fund_cname = fund_cname;
	}
	public String getRef_abs_ret_rate_twd_min() {
		return ref_abs_ret_rate_twd_min;
	}
	public void setRef_abs_ret_rate_twd_min(String ref_abs_ret_rate_twd_min) {
		this.ref_abs_ret_rate_twd_min = ref_abs_ret_rate_twd_min;
	}
	public String getRef_abs_ret_rate_twd_max() {
		return ref_abs_ret_rate_twd_max;
	}
	public void setRef_abs_ret_rate_twd_max(String ref_abs_ret_rate_twd_max) {
		this.ref_abs_ret_rate_twd_max = ref_abs_ret_rate_twd_max;
	}
	public String getLipper_id() {
		return lipper_id;
	}
	public void setLipper_id(String lipper_id) {
		this.lipper_id = lipper_id;
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
	public String getFundcname_a() {
		return fundcname_a;
	}
	public void setFundcname_a(String fundcname_a) {
		this.fundcname_a = fundcname_a;
	}
	public String getStop_loss_pt_min() {
		return stop_loss_pt_min;
	}
	public void setStop_loss_pt_min(String stop_loss_pt_min) {
		this.stop_loss_pt_min = stop_loss_pt_min;
	}
	public String getStop_loss_pt_max() {
		return stop_loss_pt_max;
	}
	public void setStop_loss_pt_max(String stop_loss_pt_max) {
		this.stop_loss_pt_max = stop_loss_pt_max;
	}
	public String getFund_code() {
		return fund_code;
	}
	public void setFund_code(String fund_code) {
		this.fund_code = fund_code;
	}
	public String getTake_prft_pt_min() {
		return take_prft_pt_min;
	}
	public void setTake_prft_pt_min(String take_prft_pt_min) {
		this.take_prft_pt_min = take_prft_pt_min;
	}
	public String getTake_prft_pt_max() {
		return take_prft_pt_max;
	}
	public void setTake_prft_pt_max(String take_prft_pt_max) {
		this.take_prft_pt_max = take_prft_pt_max;
	}
	public String getValu_crcy_type() {
		return valu_crcy_type;
	}
	public void setValu_crcy_type(String valu_crcy_type) {
		this.valu_crcy_type = valu_crcy_type;
	}
	public String getDividend_frequency() {
		return dividend_frequency;
	}
	public void setDividend_frequency(String dividend_frequency) {
		this.dividend_frequency = dividend_frequency;
	}
	public String getInv_crcy_type() {
		return inv_crcy_type;
	}
	public void setInv_crcy_type(String inv_crcy_type) {
		this.inv_crcy_type = inv_crcy_type;
	}
	public String getRiskcate_id() {
		return riskcate_id;
	}
	public void setRiskcate_id(String riskcate_id) {
		this.riskcate_id = riskcate_id;
	}
	public Date getSign_date_bgn() {
		return sign_date_bgn;
	}
	public void setSign_date_bgn(Date sign_date_bgn) {
		this.sign_date_bgn = sign_date_bgn;
	}
	public Date getSign_date_end() {
		return sign_date_end;
	}
	public void setSign_date_end(Date sign_date_end) {
		this.sign_date_end = sign_date_end;
	}	
}
