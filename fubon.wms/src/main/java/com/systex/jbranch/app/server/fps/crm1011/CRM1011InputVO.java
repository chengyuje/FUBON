package com.systex.jbranch.app.server.fps.crm1011;

import java.math.BigDecimal;
import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM1011InputVO extends PagingInputVO{
	private BigDecimal id;
	private String title;
	private String creator;
	private Date sDate;
	private Date eDate;
	private String content;
	private String msg_level;
	private String display;
	private String new_msg_yn;
	
	
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMsg_level() {
		return msg_level;
	}
	public void setMsg_level(String msg_level) {
		this.msg_level = msg_level;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getNew_msg_yn() {
		return new_msg_yn;
	}
	public void setNew_msg_yn(String new_msg_yn) {
		this.new_msg_yn = new_msg_yn;
	}
}
