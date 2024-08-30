package com.systex.jbranch.app.server.fps.prd140;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD140OutputVO extends PagingOutputVO {
	private String sn_name;
	private List resultList;
	
	
	
	public String getSn_name() {
		return sn_name;
	}
	public void setSn_name(String sn_name) {
		this.sn_name = sn_name;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
