package com.systex.jbranch.app.server.fps.fps940;

import java.math.BigDecimal;
import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS940InputVO extends PagingInputVO {
	private String param_no;
	private Date date;
	//
	private BigDecimal plan_amt_1;
	private BigDecimal plan_amt_2;
	private BigDecimal gen_lead_para1;
	private BigDecimal gen_lead_para2;
	private BigDecimal fail_status;
	private BigDecimal efficient_limit;
	private BigDecimal efficient_points;
	private String deposit_curr;
	private BigDecimal fund_aum;
	private BigDecimal deposit_aum;
	private BigDecimal sisn_base_purchase;
	private BigDecimal bond_base_purchase;
	private BigDecimal exchange_rate;
	private BigDecimal spp_achive_rate_1;
	private BigDecimal spp_achive_rate_2;
	private BigDecimal rf_rate;
	private BigDecimal available_amt;
	// 特定目的-留遊學
	private BigDecimal university_fee_1;
	private BigDecimal university_fee_2;
	private BigDecimal university_fee_3;
	private BigDecimal university_cost_1;
	private BigDecimal university_cost_2;
	private BigDecimal university_cost_3;
	private BigDecimal graduated_fee_1;
	private BigDecimal graduated_fee_2;
	private BigDecimal graduated_fee_3;
	private BigDecimal graduated_cost_1;
	private BigDecimal graduated_cost_2;
	private BigDecimal graduated_cost_3;
	private BigDecimal doctoral_fee_1;
	private BigDecimal doctoral_fee_2;
	private BigDecimal doctoral_fee_3;
	private BigDecimal doctoral_cost_1;
	private BigDecimal doctoral_cost_2;
	private BigDecimal doctoral_cost_3;
	// 特定目的-留遊學
	private String feature_desc;
	private String status;
	
	// add by Carley 2018/12/19
	private BigDecimal cash_prepare_age_1;
	private BigDecimal cash_prepare_age_2;
	private BigDecimal cash_prepare_age_3;
	private BigDecimal cash_prepare_1;
	private BigDecimal cash_prepare_2;
	private BigDecimal cash_prepare_3;
	private BigDecimal cash_prepare_4;
	
	public String getParam_no() {
		return param_no;
	}
	public void setParam_no(String param_no) {
		this.param_no = param_no;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public BigDecimal getPlan_amt_1() {
		return plan_amt_1;
	}
	public void setPlan_amt_1(BigDecimal plan_amt_1) {
		this.plan_amt_1 = plan_amt_1;
	}
	public BigDecimal getPlan_amt_2() {
		return plan_amt_2;
	}
	public void setPlan_amt_2(BigDecimal plan_amt_2) {
		this.plan_amt_2 = plan_amt_2;
	}
	public BigDecimal getGen_lead_para1() {
		return gen_lead_para1;
	}
	public void setGen_lead_para1(BigDecimal gen_lead_para1) {
		this.gen_lead_para1 = gen_lead_para1;
	}
	public BigDecimal getGen_lead_para2() {
		return gen_lead_para2;
	}
	public void setGen_lead_para2(BigDecimal gen_lead_para2) {
		this.gen_lead_para2 = gen_lead_para2;
	}
	public BigDecimal getFail_status() {
		return fail_status;
	}
	public void setFail_status(BigDecimal fail_status) {
		this.fail_status = fail_status;
	}
	public BigDecimal getEfficient_limit() {
		return efficient_limit;
	}
	public void setEfficient_limit(BigDecimal efficient_limit) {
		this.efficient_limit = efficient_limit;
	}
	public BigDecimal getEfficient_points() {
		return efficient_points;
	}
	public void setEfficient_points(BigDecimal efficient_points) {
		this.efficient_points = efficient_points;
	}
	public String getDeposit_curr() {
		return deposit_curr;
	}
	public void setDeposit_curr(String deposit_curr) {
		this.deposit_curr = deposit_curr;
	}
	public BigDecimal getFund_aum() {
		return fund_aum;
	}
	public void setFund_aum(BigDecimal fund_aum) {
		this.fund_aum = fund_aum;
	}
	public BigDecimal getDeposit_aum() {
		return deposit_aum;
	}
	public void setDeposit_aum(BigDecimal deposit_aum) {
		this.deposit_aum = deposit_aum;
	}
	public BigDecimal getSisn_base_purchase() {
		return sisn_base_purchase;
	}
	public void setSisn_base_purchase(BigDecimal sisn_base_purchase) {
		this.sisn_base_purchase = sisn_base_purchase;
	}
	public BigDecimal getBond_base_purchase() {
		return bond_base_purchase;
	}
	public void setBond_base_purchase(BigDecimal bond_base_purchase) {
		this.bond_base_purchase = bond_base_purchase;
	}
	public BigDecimal getExchange_rate() {
		return exchange_rate;
	}
	public void setExchange_rate(BigDecimal exchange_rate) {
		this.exchange_rate = exchange_rate;
	}
	public BigDecimal getSpp_achive_rate_1() {
		return spp_achive_rate_1;
	}
	public void setSpp_achive_rate_1(BigDecimal spp_achive_rate_1) {
		this.spp_achive_rate_1 = spp_achive_rate_1;
	}
	public BigDecimal getSpp_achive_rate_2() {
		return spp_achive_rate_2;
	}
	public void setSpp_achive_rate_2(BigDecimal spp_achive_rate_2) {
		this.spp_achive_rate_2 = spp_achive_rate_2;
	}
	public BigDecimal getRf_rate() {
		return rf_rate;
	}
	public void setRf_rate(BigDecimal rf_rate) {
		this.rf_rate = rf_rate;
	}
	public BigDecimal getAvailable_amt() {
		return available_amt;
	}
	public void setAvailable_amt(BigDecimal available_amt) {
		this.available_amt = available_amt;
	}
	public BigDecimal getUniversity_fee_1() {
		return university_fee_1;
	}
	public void setUniversity_fee_1(BigDecimal university_fee_1) {
		this.university_fee_1 = university_fee_1;
	}
	public BigDecimal getUniversity_fee_2() {
		return university_fee_2;
	}
	public void setUniversity_fee_2(BigDecimal university_fee_2) {
		this.university_fee_2 = university_fee_2;
	}
	public BigDecimal getUniversity_fee_3() {
		return university_fee_3;
	}
	public void setUniversity_fee_3(BigDecimal university_fee_3) {
		this.university_fee_3 = university_fee_3;
	}
	public BigDecimal getUniversity_cost_1() {
		return university_cost_1;
	}
	public void setUniversity_cost_1(BigDecimal university_cost_1) {
		this.university_cost_1 = university_cost_1;
	}
	public BigDecimal getUniversity_cost_2() {
		return university_cost_2;
	}
	public void setUniversity_cost_2(BigDecimal university_cost_2) {
		this.university_cost_2 = university_cost_2;
	}
	public BigDecimal getUniversity_cost_3() {
		return university_cost_3;
	}
	public void setUniversity_cost_3(BigDecimal university_cost_3) {
		this.university_cost_3 = university_cost_3;
	}
	public BigDecimal getGraduated_fee_1() {
		return graduated_fee_1;
	}
	public void setGraduated_fee_1(BigDecimal graduated_fee_1) {
		this.graduated_fee_1 = graduated_fee_1;
	}
	public BigDecimal getGraduated_fee_2() {
		return graduated_fee_2;
	}
	public void setGraduated_fee_2(BigDecimal graduated_fee_2) {
		this.graduated_fee_2 = graduated_fee_2;
	}
	public BigDecimal getGraduated_fee_3() {
		return graduated_fee_3;
	}
	public void setGraduated_fee_3(BigDecimal graduated_fee_3) {
		this.graduated_fee_3 = graduated_fee_3;
	}
	public BigDecimal getGraduated_cost_1() {
		return graduated_cost_1;
	}
	public void setGraduated_cost_1(BigDecimal graduated_cost_1) {
		this.graduated_cost_1 = graduated_cost_1;
	}
	public BigDecimal getGraduated_cost_2() {
		return graduated_cost_2;
	}
	public void setGraduated_cost_2(BigDecimal graduated_cost_2) {
		this.graduated_cost_2 = graduated_cost_2;
	}
	public BigDecimal getGraduated_cost_3() {
		return graduated_cost_3;
	}
	public void setGraduated_cost_3(BigDecimal graduated_cost_3) {
		this.graduated_cost_3 = graduated_cost_3;
	}
	public BigDecimal getDoctoral_fee_1() {
		return doctoral_fee_1;
	}
	public void setDoctoral_fee_1(BigDecimal doctoral_fee_1) {
		this.doctoral_fee_1 = doctoral_fee_1;
	}
	public BigDecimal getDoctoral_fee_2() {
		return doctoral_fee_2;
	}
	public void setDoctoral_fee_2(BigDecimal doctoral_fee_2) {
		this.doctoral_fee_2 = doctoral_fee_2;
	}
	public BigDecimal getDoctoral_fee_3() {
		return doctoral_fee_3;
	}
	public void setDoctoral_fee_3(BigDecimal doctoral_fee_3) {
		this.doctoral_fee_3 = doctoral_fee_3;
	}
	public BigDecimal getDoctoral_cost_1() {
		return doctoral_cost_1;
	}
	public void setDoctoral_cost_1(BigDecimal doctoral_cost_1) {
		this.doctoral_cost_1 = doctoral_cost_1;
	}
	public BigDecimal getDoctoral_cost_2() {
		return doctoral_cost_2;
	}
	public void setDoctoral_cost_2(BigDecimal doctoral_cost_2) {
		this.doctoral_cost_2 = doctoral_cost_2;
	}
	public BigDecimal getDoctoral_cost_3() {
		return doctoral_cost_3;
	}
	public void setDoctoral_cost_3(BigDecimal doctoral_cost_3) {
		this.doctoral_cost_3 = doctoral_cost_3;
	}
	public String getFeature_desc() {
		return feature_desc;
	}
	public void setFeature_desc(String feature_desc) {
		this.feature_desc = feature_desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getCash_prepare_age_1() {
		return cash_prepare_age_1;
	}
	public void setCash_prepare_age_1(BigDecimal cash_prepare_age_1) {
		this.cash_prepare_age_1 = cash_prepare_age_1;
	}
	public BigDecimal getCash_prepare_age_2() {
		return cash_prepare_age_2;
	}
	public void setCash_prepare_age_2(BigDecimal cash_prepare_age_2) {
		this.cash_prepare_age_2 = cash_prepare_age_2;
	}
	public BigDecimal getCash_prepare_age_3() {
		return cash_prepare_age_3;
	}
	public void setCash_prepare_age_3(BigDecimal cash_prepare_age_3) {
		this.cash_prepare_age_3 = cash_prepare_age_3;
	}
	public BigDecimal getCash_prepare_1() {
		return cash_prepare_1;
	}
	public void setCash_prepare_1(BigDecimal cash_prepare_1) {
		this.cash_prepare_1 = cash_prepare_1;
	}
	public BigDecimal getCash_prepare_2() {
		return cash_prepare_2;
	}
	public void setCash_prepare_2(BigDecimal cash_prepare_2) {
		this.cash_prepare_2 = cash_prepare_2;
	}
	public BigDecimal getCash_prepare_3() {
		return cash_prepare_3;
	}
	public void setCash_prepare_3(BigDecimal cash_prepare_3) {
		this.cash_prepare_3 = cash_prepare_3;
	}
	public BigDecimal getCash_prepare_4() {
		return cash_prepare_4;
	}
	public void setCash_prepare_4(BigDecimal cash_prepare_4) {
		this.cash_prepare_4 = cash_prepare_4;
	}
}