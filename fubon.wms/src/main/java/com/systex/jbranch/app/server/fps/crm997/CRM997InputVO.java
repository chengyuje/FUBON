package com.systex.jbranch.app.server.fps.crm997;

import java.util.Date;

public class CRM997InputVO{
	private String custId; 		//客戶ID
	private String potential_level;//潛力等級
	private String experience_level;//體驗等級
	private Date experience_begin_date;//體驗開始日
	private Date experience_end_date;//體驗結束日
	private String rptVersion; //報表版本
	private Date   createtime; //建立時間
	private String creator;	   //建立人員	
	private String modifier;   //最後修改人員
	private String last_end_date; //最後修改日期
	private String setting_type;
	private String fileName;
	
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getPotential_level() {
		return potential_level;
	}
	public void setPotential_level(String potential_level) {
		this.potential_level = potential_level;
	}
	public String getExperience_level() {
		return experience_level;
	}
	public void setExperience_level(String experience_level) {
		this.experience_level = experience_level;
	}
	public Date getExperience_begin_date() {
		return experience_begin_date;
	}
	public void setExperience_begin_date(Date experience_begin_date) {
		this.experience_begin_date = experience_begin_date;
	}
	public Date getExperience_end_date() {
		return experience_end_date;
	}
	public void setExperience_end_date(Date experience_end_date) {
		this.experience_end_date = experience_end_date;
	}
	public String getRptVersion() {
		return rptVersion;
	}
	public void setRptVersion(String rptVersion) {
		this.rptVersion = rptVersion;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getLast_end_date() {
		return last_end_date;
	}
	public void setLast_end_date(String last_end_date) {
		this.last_end_date = last_end_date;
	}
	public String getSetting_type() {
		return setting_type;
	}
	public void setSetting_type(String setting_type) {
		this.setting_type = setting_type;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	

}