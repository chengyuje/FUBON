package com.systex.jbranch.app.server.fps.mao231;


import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MAO231InputVO extends PagingInputVO{
	private String region_center_id;
	private String branch_area_id;
	private String branch_nbr;
	private String emp_id;
	private String email_id;
	private String reply_type;
	private Date use_date_bgn;
	private Date use_date_end;
	private String seq;
	
	
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
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public Date getUse_date_bgn() {
		return use_date_bgn;
	}
	public void setUse_date_bgn(Date use_date_bgn) {
		this.use_date_bgn = use_date_bgn;
	}
	public Date getUse_date_end() {
		return use_date_end;
	}
	public void setUse_date_end(Date use_date_end) {
		this.use_date_end = use_date_end;
	}
	public String getReply_type() {
		return reply_type;
	}
	public void setReply_type(String reply_type) {
		this.reply_type = reply_type;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}

	
}
