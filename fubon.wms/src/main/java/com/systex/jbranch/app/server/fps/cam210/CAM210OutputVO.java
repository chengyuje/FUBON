package com.systex.jbranch.app.server.fps.cam210;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM210OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> resultList2;
	private List<Map<String, Object>> aolist;
	private List<Map<String, Object>> cnameList;
	private List<Map<String, Object>> branchList;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getResultList2() {
		return resultList2;
	}

	public void setResultList2(List<Map<String, Object>> resultList2) {
		this.resultList2 = resultList2;
	}

	public List<Map<String, Object>> getAolist() {
		return aolist;
	}

	public void setAolist(List<Map<String, Object>> aolist) {
		this.aolist = aolist;
	}

	public List<Map<String, Object>> getCnameList() {
		return cnameList;
	}

	public void setCnameList(List<Map<String, Object>> cnameList) {
		this.cnameList = cnameList;
	}

	public List<Map<String, Object>> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<Map<String, Object>> branchList) {
		this.branchList = branchList;
	}

}
