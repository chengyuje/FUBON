package com.systex.jbranch.app.server.fps.crm612;

import java.sql.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM612InputVO extends PagingInputVO{
	private String cust_id;
	private String ao_code;
	
	private String assets_id;
	private String assets_name;
	private String assets_amt;
	private String assets_note;
	
	private String group_id;
	private String group_name;
	private String addgroup;
	
	private String decis;
	private String com_experience;
	private Date ao_best_visit_datetime;

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
	public String getAssets_id() {
		return assets_id;
	}
	public void setAssets_id(String assets_id) {
		this.assets_id = assets_id;
	}
	public String getAssets_name() {
		return assets_name;
	}
	public void setAssets_name(String assets_name) {
		this.assets_name = assets_name;
	}
	public String getAssets_amt() {
		return assets_amt;
	}
	public void setAssets_amt(String assets_amt) {
		this.assets_amt = assets_amt;
	}
	public String getAssets_note() {
		return assets_note;
	}
	public void setAssets_note(String assets_note) {
		this.assets_note = assets_note;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getAddgroup() {
		return addgroup;
	}
	public void setAddgroup(String addgroup) {
		this.addgroup = addgroup;
	}
	public String getDecis() {
		return decis;
	}
	public void setDecis(String decis) {
		this.decis = decis;
	}
	public String getCom_experience() {
		return com_experience;
	}
	public void setCom_experience(String com_experience) {
		this.com_experience = com_experience;
	}
	public Date getAo_best_visit_datetime() {
		return ao_best_visit_datetime;
	}
	public void setAo_best_visit_datetime(Date ao_best_visit_datetime) {
		this.ao_best_visit_datetime = ao_best_visit_datetime;
	}


	
}
