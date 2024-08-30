package com.systex.jbranch.app.server.fps.fps930;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS930InputVO extends PagingInputVO {
	private String param_no;
	private String alert_type;
	private Date date;
	private List<Map<String, Object>> totalList;
	private String status;
	
	
	public String getParam_no() {
		return param_no;
	}
	public void setParam_no(String param_no) {
		this.param_no = param_no;
	}
	public String getAlert_type() {
		return alert_type;
	}
	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}
	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}