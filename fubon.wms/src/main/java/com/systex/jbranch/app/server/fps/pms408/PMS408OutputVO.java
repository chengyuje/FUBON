package com.systex.jbranch.app.server.fps.pms408;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS408OutputVO extends PagingOutputVO {
	
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> totalList;
	private String isHeadMgr;
	private String isSpecialHeadMgr; // 內控管理科人員 or 財管績效管理科人員 Y/N
	
	private List<Map<String, Object>> compareDtlList;

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

	public String getIsHeadMgr() {
		return isHeadMgr;
	}

	public void setIsHeadMgr(String isHeadMgr) {
		this.isHeadMgr = isHeadMgr;
	}

	public List<Map<String, Object>> getCompareDtlList() {
		return compareDtlList;
	}

	public void setCompareDtlList(List<Map<String, Object>> compareDtlList) {
		this.compareDtlList = compareDtlList;
	}

	public String getIsSpecialHeadMgr() {
		return isSpecialHeadMgr;
	}

	public void setIsSpecialHeadMgr(String isSpecialHeadMgr) {
		this.isSpecialHeadMgr = isSpecialHeadMgr;
	}
	
}
