package com.systex.jbranch.app.server.fps.pms346;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS346OutputVO extends PagingOutputVO {
	private List resultList; // 分頁查詢LIST
	private List list; // 暫存CSV LIST
	private List csvList; // csv 查詢LIST
	private List pwList;
	private String encodedUrl;

	public String getEncodedUrl() {
		return encodedUrl;
	}

	public void setEncodedUrl(String encodedUrl) {
		this.encodedUrl = encodedUrl;
	}

	public List getPwList() {
		return pwList;
	}

	public void setPwList(List pwList) {
		this.pwList = pwList;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public List getResultList() {
		return resultList;
	}

	public List getCsvList() {
		return csvList;
	}

	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
