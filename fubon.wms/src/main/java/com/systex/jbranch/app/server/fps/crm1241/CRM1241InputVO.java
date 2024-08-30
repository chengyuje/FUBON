package com.systex.jbranch.app.server.fps.crm1241;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM1241InputVO extends PagingInputVO{
	private Date viewDate;
	
	private String seq;
	private String product_name;
	private String ptype;
	private String prd_id;
	private String sell_out_yn;
	private String visit_memo_as;
	private String visit_memo_ao;
	private String visit_purpose;
	private String visit_purpose_other;
	private String key_issue;
	private String querytype;
	private String complete_yn;
	private String emp_id;
	

	public Date getViewDate() {
		return viewDate;
	}
	public void setViewDate(Date viewDate) {
		this.viewDate = viewDate;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getSell_out_yn() {
		return sell_out_yn;
	}
	public void setSell_out_yn(String sell_out_yn) {
		this.sell_out_yn = sell_out_yn;
	}
	public String getVisit_memo_as() {
		return visit_memo_as;
	}
	public void setVisit_memo_as(String visit_memo_as) {
		this.visit_memo_as = visit_memo_as;
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
	public String getVisit_memo_ao() {
		return visit_memo_ao;
	}
	public void setVisit_memo_ao(String visit_memo_ao) {
		this.visit_memo_ao = visit_memo_ao;
	}
	public String getQuerytype() {
		return querytype;
	}
	public void setQuerytype(String querytype) {
		this.querytype = querytype;
	}
	public String getComplete_yn() {
		return complete_yn;
	}
	public void setComplete_yn(String complete_yn) {
		this.complete_yn = complete_yn;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	
}
