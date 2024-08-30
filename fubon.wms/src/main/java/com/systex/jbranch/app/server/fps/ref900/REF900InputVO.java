package com.systex.jbranch.app.server.fps.ref900;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class REF900InputVO extends PagingInputVO {
	private String seq;
	private Date startDate;
	private Date endDate;
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String sales_person;
	private String sales_name;
	private String user_id;
	private String user_name;
	private String status;
	private String cust_id;
	private String cust_name;
	private String ref_prod;
	private String reason;
	
	
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
	public String getBranch_nbr() {
		return branch_nbr;
	}
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	public String getSales_person() {
		return sales_person;
	}
	public void setSales_person(String sales_person) {
		this.sales_person = sales_person;
	}
	public String getSales_name() {
		return sales_name;
	}
	public void setSales_name(String sales_name) {
		this.sales_name = sales_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getRef_prod() {
		return ref_prod;
	}
	public void setRef_prod(String ref_prod) {
		this.ref_prod = ref_prod;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}