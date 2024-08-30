package com.systex.jbranch.app.server.fps.marquee;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MARQUEEInputVO extends PagingInputVO {
	private Long scrl_id;
	private String scrl_title;
	private String scrl_desc;
	private Date scrl_start_dt;
	private Date scrl_end_dt;
	private String msg_type;
	private String msg_new;
	private String scrl_stop;
	private String creator;
	
	public Long getScrl_id() {
		return scrl_id;
	}
	public void setScrl_id(Long scrl_id) {
		this.scrl_id = scrl_id;
	}
	public String getScrl_title() {
		return scrl_title;
	}
	public void setScrl_title(String scrl_title) {
		this.scrl_title = scrl_title;
	}
	public String getScrl_desc() {
		return scrl_desc;
	}
	public void setScrl_desc(String scrl_desc) {
		this.scrl_desc = scrl_desc;
	}
	public Date getScrl_start_dt() {
		return scrl_start_dt;
	}
	public void setScrl_start_dt(Date scrl_start_dt) {
		this.scrl_start_dt = scrl_start_dt;
	}
	public Date getScrl_end_dt() {
		return scrl_end_dt;
	}
	public void setScrl_end_dt(Date scrl_end_dt) {
		this.scrl_end_dt = scrl_end_dt;
	}
	public String getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}
	public String getMsg_new() {
		return msg_new;
	}
	public void setMsg_new(String msg_new) {
		this.msg_new = msg_new;
	}
	public String getScrl_stop() {
		return scrl_stop;
	}
	public void setScrl_stop(String scrl_stop) {
		this.scrl_stop = scrl_stop;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
}
