package com.systex.jbranch.app.server.fps.pms401;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS401OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList; //主查詢資訊 包含修改
	private List<Map<String, Object>> totalList;
	private List<Map<String, String>> orgList; //主查詢資訊 

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}

	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}

	public List<Map<String, String>> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<Map<String, String>> orgList) {
		this.orgList = orgList;
	}

}
