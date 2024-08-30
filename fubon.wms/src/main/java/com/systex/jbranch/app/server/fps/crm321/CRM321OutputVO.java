package com.systex.jbranch.app.server.fps.crm321;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM321OutputVO extends PagingOutputVO {
	private List FCHList;
	private List showList;
	private String code;
	
	
	public List getFCHList() {
		return FCHList;
	}
	public void setFCHList(List fCHList) {
		FCHList = fCHList;
	}
	public List getShowList() {
		return showList;
	}
	public void setShowList(List showList) {
		this.showList = showList;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
