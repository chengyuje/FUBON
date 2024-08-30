package com.systex.jbranch.app.server.fps.prd500;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD500InputVO extends PagingInputVO {
	private String link_url;
	private String link_url2;
	private String link_url3;
	
	
	public String getLink_url() {
		return link_url;
	}
	public void setLink_url(String link_url) {
		this.link_url = link_url;
	}
	public String getLink_url2() {
		return link_url2;
	}
	public void setLink_url2(String link_url2) {
		this.link_url2 = link_url2;
	}
	public String getLink_url3() {
		return link_url3;
	}
	public void setLink_url3(String link_url3) {
		this.link_url3 = link_url3;
	}
}