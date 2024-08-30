package com.systex.jbranch.app.server.fps.crm124;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM124InputVO extends PagingInputVO{
	private Date date;
	private String emp_id;
	private String onsite_brh;
	private String new_onsite_brh;
	private Date on_date;
	private Date new_on_date;
	private String seq;
	private String new_onsite_period;
	private String year;
	private String month;
	private String chg_reason;
	private String chg_reason_oth;
	private List<Map<String, Object>> dateList;
	private String task_cust_id;
	private String salesplan_seq;
	private Date task_date;
	private String task_source;
	private String task_sourse_oth;
	private String task_stime;
	private String task_etime;
	private String task_title;
	private String task_emp_id;
	private String task_ao_code;
	private String task_memo;
	
	private String visit_purpose;
	private String visit_purpose_other;
	private String key_issue;
	private String role_id;
	private String faia_type;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public Date getOn_date() {
		return on_date;
	}
	public void setOn_date(Date on_date) {
		this.on_date = on_date;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Date getNew_on_date() {
		return new_on_date;
	}
	public void setNew_on_date(Date new_on_date) {
		this.new_on_date = new_on_date;
	}
	public String getChg_reason() {
		return chg_reason;
	}
	public void setChg_reason(String chg_reason) {
		this.chg_reason = chg_reason;
	}
	public String getChg_reason_oth() {
		return chg_reason_oth;
	}
	public void setChg_reason_oth(String chg_reason_oth) {
		this.chg_reason_oth = chg_reason_oth;
	}
	public String getTask_cust_id() {
		return task_cust_id;
	}
	public void setTask_cust_id(String task_cust_id) {
		this.task_cust_id = task_cust_id;
	}
	public String getSalesplan_seq() {
		return salesplan_seq;
	}
	public void setSalesplan_seq(String salesplan_seq) {
		this.salesplan_seq = salesplan_seq;
	}
	public Date getTask_date() {
		return task_date;
	}
	public void setTask_date(Date task_date) {
		this.task_date = task_date;
	}
	public String getTask_source() {
		return task_source;
	}
	public void setTask_source(String task_source) {
		this.task_source = task_source;
	}
	public String getTask_stime() {
		return task_stime;
	}
	public void setTask_stime(String task_stime) {
		this.task_stime = task_stime;
	}
	public String getTask_etime() {
		return task_etime;
	}
	public void setTask_etime(String task_etime) {
		this.task_etime = task_etime;
	}
	public String getTask_title() {
		return task_title;
	}
	public void setTask_title(String task_title) {
		this.task_title = task_title;
	}
	public String getTask_emp_id() {
		return task_emp_id;
	}
	public void setTask_emp_id(String task_emp_id) {
		this.task_emp_id = task_emp_id;
	}
	public String getTask_ao_code() {
		return task_ao_code;
	}
	public void setTask_ao_code(String task_ao_code) {
		this.task_ao_code = task_ao_code;
	}
	public String getTask_memo() {
		return task_memo;
	}
	public void setTask_memo(String task_memo) {
		this.task_memo = task_memo;
	}
	public String getNew_onsite_period() {
		return new_onsite_period;
	}
	public void setNew_onsite_period(String new_onsite_period) {
		this.new_onsite_period = new_onsite_period;
	}
	public String getOnsite_brh() {
		return onsite_brh;
	}
	public void setOnsite_brh(String onsite_brh) {
		this.onsite_brh = onsite_brh;
	}
	public String getNew_onsite_brh() {
		return new_onsite_brh;
	}
	public void setNew_onsite_brh(String new_onsite_brh) {
		this.new_onsite_brh = new_onsite_brh;
	}
	public List<Map<String, Object>> getDateList() {
		return dateList;
	}
	public void setDateList(List<Map<String, Object>> dateList) {
		this.dateList = dateList;
	}
	public String getTask_sourse_oth() {
		return task_sourse_oth;
	}
	public void setTask_sourse_oth(String task_sourse_oth) {
		this.task_sourse_oth = task_sourse_oth;
	}
	public String getVisit_purpose() {
		return visit_purpose;
	}
	public void setVisit_purpose(String visit_purpose) {
		this.visit_purpose = visit_purpose;
	}
	public String getVisit_purpose_other() {
		return visit_purpose_other;
	}
	public void setVisit_purpose_other(String visit_purpose_other) {
		this.visit_purpose_other = visit_purpose_other;
	}
	public String getKey_issue() {
		return key_issue;
	}
	public void setKey_issue(String key_issue) {
		this.key_issue = key_issue;
	}
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	public String getFaia_type() {
		return faia_type;
	}
	public void setFaia_type(String faia_type) {
		this.faia_type = faia_type;
	}
	
	
	
}
