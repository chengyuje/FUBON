package com.systex.jbranch.app.server.fps.fps950;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS950InputVO extends PagingInputVO {
	private String param_no;
	private Date date;
	private String market_overview;
	private List<Map<String, Object>> totalList;
	private String status;
	
	private String fileName;
	private String fileRealName;
	
	
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
	public String getMarket_overview() {
		return market_overview;
	}
	public void setMarket_overview(String market_overview) {
		this.market_overview = market_overview;
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
}