package com.systex.jbranch.app.server.fps.cmmgr004;

import java.math.BigDecimal;
import java.util.List;

public class CMMGR004IT01InputVO {

	public CMMGR004IT01InputVO() {
	}

	private String scheduleid;
	private String schedulename;
	private String description;
	private String cronexpression;
	private BigDecimal repeat;
	private BigDecimal repeatinterval;
	private String processor;
	private String scheduleparameter;
	private String lasttry;
	private String isscheduled;
	private BigDecimal startjob;
	private List joblist; 
	private String isuse;
	
	private List jobconList;
	
	private String type;//數據操作類型
	private String executetime;
	
	private String calendar;
	
	public List getJobconList() {
		return jobconList;
	}

	public void setJobconList(List jobconList) {
		this.jobconList = jobconList;
	}

	public String getIsuse() {
		return isuse;
	}

	public void setIsuse(String isuse) {
		this.isuse = isuse;
	}

	public String getScheduleid() {
		return scheduleid;
	}

	public void setScheduleid(String scheduleid) {
		this.scheduleid = scheduleid;
	}

	public String getSchedulename() {
		return schedulename;
	}

	public void setSchedulename(String schedulename) {
		this.schedulename = schedulename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCronexpression() {
		return cronexpression;
	}

	public void setCronexpression(String cronexpression) {
		this.cronexpression = cronexpression;
	}

	public BigDecimal getRepeat() {
		return repeat;
	}

	public void setRepeat(BigDecimal repeat) {
		this.repeat = repeat;
	}

	public BigDecimal getRepeatinterval() {
		return repeatinterval;
	}

	public void setRepeatinterval(BigDecimal repeatinterval) {
		this.repeatinterval = repeatinterval;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getScheduleparameter() {
		return scheduleparameter;
	}

	public void setScheduleparameter(String scheduleparameter) {
		this.scheduleparameter = scheduleparameter;
	}

	public String getLasttry() {
		return lasttry;
	}

	public void setLasttry(String lasttry) {
		this.lasttry = lasttry;
	}

	public String getIsscheduled() {
		return isscheduled;
	}

	public void setIsscheduled(String isscheduled) {
		this.isscheduled = isscheduled;
	}

	public BigDecimal getStartjob() {
		return startjob;
	}

	public void setStartjob(BigDecimal startjob) {
		this.startjob = startjob;
	}

	public List getJoblist() {
		return joblist;
	}

	public void setJoblist(List joblist) {
		this.joblist = joblist;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getExecutetime() {
		return executetime;
	}
	public void setExecutetime(String executetime) {
		this.executetime = executetime;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}
}
