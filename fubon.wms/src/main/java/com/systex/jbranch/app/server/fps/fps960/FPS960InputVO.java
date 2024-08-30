package com.systex.jbranch.app.server.fps.fps960;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS960InputVO extends PagingInputVO {
	private String param_no;
	private Date date;
	private String setType;
	private List<Map<String, Object>> chkRole;
	private String fileName;
	private String realfileName;
	
	private String status;
	private List<Map<String, Object>> review_list;
	
	
	public String getParam_no() {
		return param_no;
	}
	public void setParam_no(String param_no) {
		this.param_no = param_no;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
	}
	public List<Map<String, Object>> getChkRole() {
		return chkRole;
	}
	public void setChkRole(List<Map<String, Object>> chkRole) {
		this.chkRole = chkRole;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRealfileName() {
		return realfileName;
	}
	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Map<String, Object>> getReview_list() {
		return review_list;
	}
	public void setReview_list(List<Map<String, Object>> review_list) {
		this.review_list = review_list;
	}
}