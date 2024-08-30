package com.systex.jbranch.app.server.fps.sqm120;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SQM120OutputVO extends PagingOutputVO {

	private List resultList; //主查詢資訊 包含修改
	private List totalList;
	private List<Map<String, String>> orgList; //主查詢資訊 
	private List<Map<String, String>> ansTypeList; //答案類型 xml資訊
	private List<Map<String, String>> ansTypePushList;

	public List<Map<String, String>> getAnsTypePushList() {
		return ansTypePushList;
	}

	public void setAnsTypePushList(List<Map<String, String>> ansTypePushList) {
		this.ansTypePushList = ansTypePushList;
	}

	public List<Map<String, String>> getAnsTypeList() {
		return ansTypeList;
	}

	public void setAnsTypeList(List<Map<String, String>> ansTypeList) {
		this.ansTypeList = ansTypeList;
	}

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
