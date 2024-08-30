package com.systex.jbranch.app.server.fps.crm351;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM351InputVO extends PagingInputVO{
	private String pri_id;
	private String cust_id;
	private String cust_name;
	private String region_center_id;
	private String branch_area_id;
	private String bra_nbr;
	private String ao_code;
	private String con_degree;
	private String vip_degree;
	private String contact_cust;
	private String oth_contact_info;
	private String cust_ho_note;
	private String check;
	private List<Map<String, Object>> row_data;
	
	
	public String getPri_id() {
		return pri_id;
	}
	public void setPri_id(String pri_id) {
		this.pri_id = pri_id;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getBra_nbr() {
		return bra_nbr;
	}
	public void setBra_nbr(String bra_nbr) {
		this.bra_nbr = bra_nbr;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getCon_degree() {
		return con_degree;
	}
	public void setCon_degree(String con_degree) {
		this.con_degree = con_degree;
	}
	public String getVip_degree() {
		return vip_degree;
	}
	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}
	public String getContact_cust() {
		return contact_cust;
	}
	public void setContact_cust(String contact_cust) {
		this.contact_cust = contact_cust;
	}
	public String getOth_contact_info() {
		return oth_contact_info;
	}
	public void setOth_contact_info(String oth_contact_info) {
		this.oth_contact_info = oth_contact_info;
	}
	public String getCust_ho_note() {
		return cust_ho_note;
	}
	public void setCust_ho_note(String cust_ho_note) {
		this.cust_ho_note = cust_ho_note;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}
	public List<Map<String, Object>> getRow_data() {
		return row_data;
	}
	public void setRow_data(List<Map<String, Object>> row_data) {
		this.row_data = row_data;
	}
}
