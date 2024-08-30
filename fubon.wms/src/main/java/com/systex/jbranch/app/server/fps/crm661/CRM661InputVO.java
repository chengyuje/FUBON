package com.systex.jbranch.app.server.fps.crm661;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM661InputVO extends PagingInputVO{
	private String cust_id;
	private String cust_name;
	private String ao_code;
	private String rel_cust_id;
	private String rel_type;
	private String rel_type_oth;
	private String seq;
	private String rel_status;
	private String rel_rpy_type;
	private String join_srv_cust_id;
	private List<Map<String, Object>> rel_set_list;
	private List cust_list;
	private String cust_id_m;
	
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
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getRel_cust_id() {
		return rel_cust_id;
	}
	public void setRel_cust_id(String rel_cust_id) {
		this.rel_cust_id = rel_cust_id;
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
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getRel_status() {
		return rel_status;
	}
	public void setRel_status(String rel_status) {
		this.rel_status = rel_status;
	}
	public String getRel_rpy_type() {
		return rel_rpy_type;
	}
	public void setRel_rpy_type(String rel_rpy_type) {
		this.rel_rpy_type = rel_rpy_type;
	}
	public String getJoin_srv_cust_id() {
		return join_srv_cust_id;
	}
	public void setJoin_srv_cust_id(String join_srv_cust_id) {
		this.join_srv_cust_id = join_srv_cust_id;
	}
	public List<Map<String, Object>> getRel_set_list() {
		return rel_set_list;
	}
	public void setRel_set_list(List<Map<String, Object>> rel_set_list) {
		this.rel_set_list = rel_set_list;
	}
	public String getCust_id_m() {
		return cust_id_m;
	}
	public void setCust_id_m(String cust_id_m) {
		this.cust_id_m = cust_id_m;
	}
	public List getCust_list() {
		return cust_list;
	}
	public void setCust_list(List cust_list) {
		this.cust_list = cust_list;
	}
	
	
}
