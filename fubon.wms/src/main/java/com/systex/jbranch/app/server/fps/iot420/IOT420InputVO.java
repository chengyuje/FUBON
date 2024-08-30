package com.systex.jbranch.app.server.fps.iot420;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT420InputVO extends PagingInputVO {
	private String[] assign_seq;
	
	private String prematch_seq;
	private String status;
	private String case_id;
	private String cust_id;
	private String review_status;
	private String call_person;
	private Date s_apply_date;
	private Date e_apply_date;
	private String recruit_id;
	private Boolean fromIOT421;
	private String assign;
	
	private List<Map<String, Object>> resultList;
	
	public String[] getAssign_seq() {
		return assign_seq;
	}

	public void setAssign_seq(String[] assign_seq) {
		this.assign_seq = assign_seq;
	}

	public String getPrematch_seq() {
		return prematch_seq;
	}

	public void setPrematch_seq(String prematch_seq) {
		this.prematch_seq = prematch_seq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCase_id() {
		return case_id;
	}

	public void setCase_id(String case_id) {
		this.case_id = case_id;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getReview_status() {
		return review_status;
	}

	public void setReview_status(String review_status) {
		this.review_status = review_status;
	}

	public String getCall_person() {
		return call_person;
	}

	public void setCall_person(String call_person) {
		this.call_person = call_person;
	}

	public Date getS_apply_date() {
		return s_apply_date;
	}

	public void setS_apply_date(Date s_apply_date) {
		this.s_apply_date = s_apply_date;
	}

	public Date getE_apply_date() {
		return e_apply_date;
	}

	public void setE_apply_date(Date e_apply_date) {
		this.e_apply_date = e_apply_date;
	}

	public String getRecruit_id() {
		return recruit_id;
	}

	public void setRecruit_id(String recruit_id) {
		this.recruit_id = recruit_id;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public Boolean getFromIOT421() {
		return fromIOT421;
	}

	public void setFromIOT421(Boolean fromIOT421) {
		this.fromIOT421 = fromIOT421;
	}

	public String getAssign() {
		return assign;
	}

	public void setAssign(String assign) {
		this.assign = assign;
	}
	
}
