package com.systex.jbranch.app.server.fps.prd141;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD141InputVO extends PagingInputVO {
	private String cust_id;
	private String prd_id;
	private List<Map<String, String>> downloadList;
	private Date sDate;
	private Date eDate;
	
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public List<Map<String, String>> getDownloadList() {
		return downloadList;
	}
	public void setDownloadList(List<Map<String, String>> downloadList) {
		this.downloadList = downloadList;
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
