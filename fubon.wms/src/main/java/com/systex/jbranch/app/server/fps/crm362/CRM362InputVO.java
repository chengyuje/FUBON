package com.systex.jbranch.app.server.fps.crm362;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM362InputVO extends PagingInputVO{
	private String prj_id;
	private String trs_type;
	private String org_branch_nbr;
	private String org_ao_code;
	private String cust_id;
	private String con_degree;
	private String chg_frq;
	private String cust_name;
	private String vip_degree;
	private String match_yn;
	private String temp_yn;
	private String new_branch_nbr;
	private String new_ao_code;
	private String process_type;
	private List<Map<String, Object>> apply_list;
	private Map<String, Object> ao_object;
	private String flag;
	
	private String prj_code; 	// 專案代碼
	private String prd_name; 	// 專案名稱
	
	private List flaggedList;
	
	public String getPrj_id() {
		return prj_id;
	}
	public void setPrj_id(String prj_id) {
		this.prj_id = prj_id;
	}
	public String getTrs_type() {
		return trs_type;
	}
	public void setTrs_type(String trs_type) {
		this.trs_type = trs_type;
	}
	public String getOrg_branch_nbr() {
		return org_branch_nbr;
	}
	public void setOrg_branch_nbr(String org_branch_nbr) {
		this.org_branch_nbr = org_branch_nbr;
	}
	public String getOrg_ao_code() {
		return org_ao_code;
	}
	public void setOrg_ao_code(String org_ao_code) {
		this.org_ao_code = org_ao_code;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getCon_degree() {
		return con_degree;
	}
	public void setCon_degree(String con_degree) {
		this.con_degree = con_degree;
	}
	public String getChg_frq() {
		return chg_frq;
	}
	public void setChg_frq(String chg_frq) {
		this.chg_frq = chg_frq;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getVip_degree() {
		return vip_degree;
	}
	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}
	public String getMatch_yn() {
		return match_yn;
	}
	public void setMatch_yn(String match_yn) {
		this.match_yn = match_yn;
	}
	public String getTemp_yn() {
		return temp_yn;
	}
	public void setTemp_yn(String temp_yn) {
		this.temp_yn = temp_yn;
	}
	public String getNew_branch_nbr() {
		return new_branch_nbr;
	}
	public void setNew_branch_nbr(String new_branch_nbr) {
		this.new_branch_nbr = new_branch_nbr;
	}
	public String getNew_ao_code() {
		return new_ao_code;
	}
	public void setNew_ao_code(String new_ao_code) {
		this.new_ao_code = new_ao_code;
	}
	public String getProcess_type() {
		return process_type;
	}
	public void setProcess_type(String process_type) {
		this.process_type = process_type;
	}
	public List<Map<String, Object>> getApply_list() {
		return apply_list;
	}
	public void setApply_list(List<Map<String, Object>> apply_list) {
		this.apply_list = apply_list;
	}
	public Map<String, Object> getAo_object() {
		return ao_object;
	}
	public void setAo_object(Map<String, Object> ao_object) {
		this.ao_object = ao_object;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getPrj_code() {
		return prj_code;
	}
	public void setPrj_code(String prj_code) {
		this.prj_code = prj_code;
	}
	public String getPrd_name() {
		return prd_name;
	}
	public void setPrd_name(String prd_name) {
		this.prd_name = prd_name;
	}
	public List getFlaggedList() {
		return flaggedList;
	}
	public void setFlaggedList(List flaggedList) {
		this.flaggedList = flaggedList;
	}
	
}