package com.systex.jbranch.app.server.fps.prd160;

import java.math.BigDecimal;
import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD160InputVO extends PagingInputVO {
	private BigDecimal key_no;
	private String type;
	private String cust_id;
	private String ins_id;
	private String ins_name;
	private BigDecimal insprd_annual;
	private String ins_type;
	private Date sale_sdate;
	private Date sale_edate;
	private String obu_buy;
	private String gua_annual;
	private String is_annuity;
	private String is_increasing;
	private String is_repay;
	private String is_rate_change;
	private String curr_cd;
	private String main_rider;
	private String is_inv;
	
	
	public BigDecimal getKey_no() {
		return key_no;
	}
	public void setKey_no(BigDecimal key_no) {
		this.key_no = key_no;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getIns_id() {
		return ins_id;
	}
	public void setIns_id(String ins_id) {
		this.ins_id = ins_id;
	}
	public String getIns_name() {
		return ins_name;
	}
	public void setIns_name(String ins_name) {
		this.ins_name = ins_name;
	}
	public BigDecimal getInsprd_annual() {
		return insprd_annual;
	}
	public void setInsprd_annual(BigDecimal insprd_annual) {
		this.insprd_annual = insprd_annual;
	}
	public String getIns_type() {
		return ins_type;
	}
	public void setIns_type(String ins_type) {
		this.ins_type = ins_type;
	}
	public Date getSale_sdate() {
		return sale_sdate;
	}
	public void setSale_sdate(Date sale_sdate) {
		this.sale_sdate = sale_sdate;
	}
	public Date getSale_edate() {
		return sale_edate;
	}
	public void setSale_edate(Date sale_edate) {
		this.sale_edate = sale_edate;
	}
	public String getObu_buy() {
		return obu_buy;
	}
	public void setObu_buy(String obu_buy) {
		this.obu_buy = obu_buy;
	}
	public String getGua_annual() {
		return gua_annual;
	}
	public void setGua_annual(String gua_annual) {
		this.gua_annual = gua_annual;
	}
	public String getIs_annuity() {
		return is_annuity;
	}
	public void setIs_annuity(String is_annuity) {
		this.is_annuity = is_annuity;
	}
	public String getIs_increasing() {
		return is_increasing;
	}
	public void setIs_increasing(String is_increasing) {
		this.is_increasing = is_increasing;
	}
	public String getIs_repay() {
		return is_repay;
	}
	public void setIs_repay(String is_repay) {
		this.is_repay = is_repay;
	}
	public String getIs_rate_change() {
		return is_rate_change;
	}
	public void setIs_rate_change(String is_rate_change) {
		this.is_rate_change = is_rate_change;
	}
	public String getCurr_cd() {
		return curr_cd;
	}
	public void setCurr_cd(String curr_cd) {
		this.curr_cd = curr_cd;
	}
	public String getMain_rider() {
		return main_rider;
	}
	public void setMain_rider(String main_rider) {
		this.main_rider = main_rider;
	}
	public String getIs_inv() {
		return is_inv;
	}
	public void setIs_inv(String is_inv) {
		this.is_inv = is_inv;
	}
}