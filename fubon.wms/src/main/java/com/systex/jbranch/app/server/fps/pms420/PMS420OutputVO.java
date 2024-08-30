package com.systex.jbranch.app.server.fps.pms420;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS420OutputVO extends PagingOutputVO {
	private List resultList;	
	private String countYN;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getCountYN() {
		return countYN;
	}

	public void setCountYN(String countYN) {
		this.countYN = countYN;
	}
	
}
