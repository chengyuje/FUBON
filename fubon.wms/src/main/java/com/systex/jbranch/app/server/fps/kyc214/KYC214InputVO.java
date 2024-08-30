package com.systex.jbranch.app.server.fps.kyc214;


import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class KYC214InputVO extends PagingInputVO{
	
	private String QUESTION_DESC;
	private Date sDate;
	private Date eDate;
	private String QUESTION_VERSION;
	private String EXAM_VERSION;
	private List<Map<String,Object>> selectData;
	private List<Map<String,Object>> preview_data;
	
	
	public List<Map<String, Object>> getPreview_data() {
		return preview_data;
	}
	public void setPreview_data(List<Map<String, Object>> preview_data) {
		this.preview_data = preview_data;
	}
	public String getEXAM_VERSION() {
		return EXAM_VERSION;
	}
	public void setEXAM_VERSION(String eXAM_VERSION) {
		EXAM_VERSION = eXAM_VERSION;
	}
	public List<Map<String, Object>> getSelectData() {
		return selectData;
	}
	public void setSelectData(List<Map<String, Object>> selectData) {
		this.selectData = selectData;
	}
	public String getQUESTION_VERSION() {
		return QUESTION_VERSION;
	}
	public void setQUESTION_VERSION(String qUESTION_VERSION) {
		QUESTION_VERSION = qUESTION_VERSION;
	}
	public String getQUESTION_DESC() {
		return QUESTION_DESC;
	}
	public void setQUESTION_DESC(String qUESTION_DESC) {
		QUESTION_DESC = qUESTION_DESC;
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
	
	
	
}
