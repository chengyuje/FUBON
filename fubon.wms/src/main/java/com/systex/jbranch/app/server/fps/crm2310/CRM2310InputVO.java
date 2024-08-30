package com.systex.jbranch.app.server.fps.crm2310;

import java.sql.Date;

import com.systex.jbranch.app.server.fps.crm230.CRM230InputVO;

public class CRM2310InputVO extends CRM230InputVO{
	
	//定存
	private String crcy_type;  //商品幣別
	private String cd_samt;    //存單金額(起)
	private String cd_eamt;    //存單金額(迄)
	private Date value_sDate;  //起息日(起)
	private Date value_eDate;  //起息日(迄)
	private Date due_sDate;    //到期日(起)
	private Date due_eDate;    //到期日(迄)
	
	public String getCrcy_type() {
		return crcy_type;
	}
	public void setCrcy_type(String crcy_type) {
		this.crcy_type = crcy_type;
	}
	public Date getValue_sDate() {
		return value_sDate;
	}
	public void setValue_sDate(Date value_sDate) {
		this.value_sDate = value_sDate;
	}
	public Date getValue_eDate() {
		return value_eDate;
	}
	public void setValue_eDate(Date value_eDate) {
		this.value_eDate = value_eDate;
	}
	public String getCd_samt() {
		return cd_samt;
	}
	public void setCd_samt(String cd_samt) {
		this.cd_samt = cd_samt;
	}
	public String getCd_eamt() {
		return cd_eamt;
	}
	public void setCd_eamt(String cd_eamt) {
		this.cd_eamt = cd_eamt;
	}
	public Date getDue_sDate() {
		return due_sDate;
	}
	public void setDue_sDate(Date due_sDate) {
		this.due_sDate = due_sDate;
	}
	public Date getDue_eDate() {
		return due_eDate;
	}
	public void setDue_eDate(Date due_eDate) {
		this.due_eDate = due_eDate;
	}
}
