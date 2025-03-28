package com.systex.jbranch.app.server.fps.crm662;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM662InputVO extends PagingInputVO {
	
	private String cust_id;
	private String cust_name;
	private String prv_cust_id;
	private String ao_code;
	private String rel_cust_id;
	private String rel_type;
	private String rel_type_oth;
	private String prv_mbr_no;
	private String prv_mbr_no_new;
	private String prv_list_length;
	private String prv_status;
	private String seq;
	private String prv_nbr_yn;
	private String prv_rpy_type;
	private String prv_apl_type;
	private String cust_id_m;
	private String cust_id_s;
	private String VA_type;
	private String vip_degree;
	private List<Map<String, Object>> prv_sort_list;
	private List<Map<String, Object>> add_list_prv;
	private List<Map<String, Object>> familyAumList;

	//
	private String max_apl_ppl;
	private String aum_total;
	
	//
	private String cust_id_m_dc;

	public String getCust_id_m_dc() {
		return cust_id_m_dc;
	}

	public void setCust_id_m_dc(String cust_id_m_dc) {
		this.cust_id_m_dc = cust_id_m_dc;
	}

	public List<Map<String, Object>> getAdd_list_prv() {
		return add_list_prv;
	}

	public void setAdd_list_prv(List<Map<String, Object>> add_list_prv) {
		this.add_list_prv = add_list_prv;
	}

	public String getVip_degree() {
		return vip_degree;
	}

	public void setVip_degree(String vip_degree) {
		this.vip_degree = vip_degree;
	}

	public String getMax_apl_ppl() {
		return max_apl_ppl;
	}

	public void setMax_apl_ppl(String max_apl_ppl) {
		this.max_apl_ppl = max_apl_ppl;
	}

	public String getAum_total() {
		return aum_total;
	}

	public void setAum_total(String aum_total) {
		this.aum_total = aum_total;
	}

	//

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getAo_code() {
		return ao_code;
	}

	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getRel_cust_id() {
		return rel_cust_id;
	}

	public void setRel_cust_id(String rel_cust_id) {
		this.rel_cust_id = rel_cust_id;
	}

	public String getPrv_cust_id() {
		return prv_cust_id;
	}

	public void setPrv_cust_id(String prv_cust_id) {
		this.prv_cust_id = prv_cust_id;
	}

	public String getRel_type() {
		return rel_type;
	}

	public void setRel_type(String rel_type) {
		this.rel_type = rel_type;
	}

	public String getRel_type_oth() {
		return rel_type_oth;
	}

	public void setRel_type_oth(String rel_type_oth) {
		this.rel_type_oth = rel_type_oth;
	}

	public String getPrv_mbr_no() {
		return prv_mbr_no;
	}

	public void setPrv_mbr_no(String prv_mbr_no) {
		this.prv_mbr_no = prv_mbr_no;
	}

	public String getPrv_list_length() {
		return prv_list_length;
	}

	public void setPrv_list_length(String prv_list_length) {
		this.prv_list_length = prv_list_length;
	}

	public String getPrv_status() {
		return prv_status;
	}

	public void setPrv_status(String prv_status) {
		this.prv_status = prv_status;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getPrv_nbr_yn() {
		return prv_nbr_yn;
	}

	public void setPrv_nbr_yn(String prv_nbr_yn) {
		this.prv_nbr_yn = prv_nbr_yn;
	}

	public String getPrv_apl_type() {
		return prv_apl_type;
	}

	public void setPrv_apl_type(String prv_apl_type) {
		this.prv_apl_type = prv_apl_type;
	}

	public String getCust_id_m() {
		return cust_id_m;
	}

	public void setCust_id_m(String cust_id_m) {
		this.cust_id_m = cust_id_m;
	}

	public String getCust_id_s() {
		return cust_id_s;
	}

	public void setCust_id_s(String cust_id_s) {
		this.cust_id_s = cust_id_s;
	}

	public String getPrv_rpy_type() {
		return prv_rpy_type;
	}

	public void setPrv_rpy_type(String prv_rpy_type) {
		this.prv_rpy_type = prv_rpy_type;
	}

	public String getPrv_mbr_no_new() {
		return prv_mbr_no_new;
	}

	public void setPrv_mbr_no_new(String prv_mbr_no_new) {
		this.prv_mbr_no_new = prv_mbr_no_new;
	}

	public List<Map<String, Object>> getPrv_sort_list() {
		return prv_sort_list;
	}

	public void setPrv_sort_list(List<Map<String, Object>> prv_sort_list) {
		this.prv_sort_list = prv_sort_list;
	}

	public String getVA_type() {
		return VA_type;
	}

	public void setVA_type(String vA_type) {
		VA_type = vA_type;
	}

	public List<Map<String, Object>> getFamilyAumList() {
		return familyAumList;
	}

	public void setFamilyAumList(List<Map<String, Object>> familyAumList) {
		this.familyAumList = familyAumList;
	}

}
