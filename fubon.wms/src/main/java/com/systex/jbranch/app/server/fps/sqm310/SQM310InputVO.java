package com.systex.jbranch.app.server.fps.sqm310;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM310InputVO extends PagingInputVO {
	private List<Map<String, Object>> totalList;
	//
	private Date sDate;
	private Date eDate;
	private String lead_type;
	private String camp_name;
	private String closed;
	
	
	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}
	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
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
	public String getLead_type() {
		return lead_type;
	}
	public void setLead_type(String lead_type) {
		this.lead_type = lead_type;
	}
	public String getCamp_name() {
		return camp_name;
	}
	public void setCamp_name(String camp_name) {
		this.camp_name = camp_name;
	}
	public String getClosed() {
		return closed;
	}
	public void setClosed(String closed) {
		this.closed = closed;
	}
}