package com.systex.jbranch.app.server.fps.crm331;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.crm210.CRM210InputVO;

public class CRM331InputVO extends CRM210InputVO {
	private String role;
	private List<Map<String,String>> aolist;
	private List<Map<String,String>> branch_list;
	private String ao_code;
	private String re_ao_code;
	private String cust_id;
	private String cust_name;
	private String ao_03;
	private String ao_04;
	private String ao_05;
	private String chg_frq;
	private String process_mode;
	private String new_branch_nbr;
	private String new_ao_code;
	private String new_ao_type;
	private String apl_reason;
	private String apl_reason_oth;
	private List<Map<String, Object>> confirm_list;
	private String rm_id;
	private Date sDate;
	private Date eDate;
	
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<Map<String, String>> getAolist() {
		return aolist;
	}
	public void setAolist(List<Map<String, String>> aolist) {
		this.aolist = aolist;
	}
	public List<Map<String, String>> getBranch_list() {
		return branch_list;
	}
	public void setBranch_list(List<Map<String, String>> branch_list) {
		this.branch_list = branch_list;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getRe_ao_code() {
		return re_ao_code;
	}
	public void setRe_ao_code(String re_ao_code) {
		this.re_ao_code = re_ao_code;
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
	public String getAo_03() {
		return ao_03;
	}
	public void setAo_03(String ao_03) {
		this.ao_03 = ao_03;
	}
	public String getAo_04() {
		return ao_04;
	}
	public void setAo_04(String ao_04) {
		this.ao_04 = ao_04;
	}
	public String getAo_05() {
		return ao_05;
	}
	public void setAo_05(String ao_05) {
		this.ao_05 = ao_05;
	}
	public String getChg_frq() {
		return chg_frq;
	}
	public void setChg_frq(String chg_frq) {
		this.chg_frq = chg_frq;
	}
	public String getProcess_mode() {
		return process_mode;
	}
	public void setProcess_mode(String process_mode) {
		this.process_mode = process_mode;
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
	public String getNew_ao_type() {
		return new_ao_type;
	}
	public void setNew_ao_type(String new_ao_type) {
		this.new_ao_type = new_ao_type;
	}
	public String getApl_reason() {
		return apl_reason;
	}
	public void setApl_reason(String apl_reason) {
		this.apl_reason = apl_reason;
	}
	public String getApl_reason_oth() {
		return apl_reason_oth;
	}
	public void setApl_reason_oth(String apl_reason_oth) {
		this.apl_reason_oth = apl_reason_oth;
	}
	public List<Map<String, Object>> getConfirm_list() {
		return confirm_list;
	}
	public void setConfirm_list(List<Map<String, Object>> confirm_list) {
		this.confirm_list = confirm_list;
	}
	public String getRm_id() {
		return rm_id;
	}
	public void setRm_id(String rm_id) {
		this.rm_id = rm_id;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	
}
