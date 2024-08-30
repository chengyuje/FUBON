package com.systex.jbranch.app.server.fps.pms359;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS359OutputVO extends PagingOutputVO {
	private List resultList; //主查詢資訊
	private List totalList;
	private List<Map<String, String>> orgList;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getTotalList() {
		return totalList;
	}

	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}

	public List<Map<String, String>> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<Map<String, String>> orgList) {
		this.orgList = orgList;
	}
}
