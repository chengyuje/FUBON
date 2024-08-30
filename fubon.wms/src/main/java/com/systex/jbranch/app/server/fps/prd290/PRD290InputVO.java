package com.systex.jbranch.app.server.fps.prd290;

import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD290InputVO extends PagingInputVO {
	private String prd_id;
	private String prd_id2;
	private String fund;
	private String fund_month;
	private String type;
	private Date sDate;
	private Date eDate;
	private float target;
	
	
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getPrd_id2() {
		return prd_id2;
	}
	public void setPrd_id2(String prd_id2) {
		this.prd_id2 = prd_id2;
	}
	public String getFund() {
		return fund;
	}
	public void setFund(String fund) {
		this.fund = fund;
	}
	public String getFund_month() {
		return fund_month;
	}
	public void setFund_month(String fund_month) {
		this.fund_month = fund_month;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public float getTarget() {
		return target;
	}
	public void setTarget(float target) {
		this.target = target;
	}
}
