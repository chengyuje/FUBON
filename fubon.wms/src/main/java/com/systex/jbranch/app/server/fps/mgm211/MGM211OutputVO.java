package com.systex.jbranch.app.server.fps.mgm211;

import java.util.List;


import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class MGM211OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> giftList;
	private List<Map<String, Object>> resultList;
	private String pdfUrl;
	
	public List<Map<String, Object>> getGiftList() {
		return giftList;
	}

	public void setGiftList(List<Map<String, Object>> giftList) {
		this.giftList = giftList;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String getPdfUrl() {
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}
	
}