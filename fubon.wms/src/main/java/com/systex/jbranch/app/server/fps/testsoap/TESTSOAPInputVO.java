package com.systex.jbranch.app.server.fps.testsoap;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class TESTSOAPInputVO extends PagingInputVO{
	private String soapRequestData;
	private String url;
	private String soapAction;
	private String contentType = "";
	
	private List<Map<String, Object>> list;

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public String getSoapRequestData() {
		return soapRequestData;
	}

	public void setSoapRequestData(String soapRequestData) {
		this.soapRequestData = soapRequestData;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
