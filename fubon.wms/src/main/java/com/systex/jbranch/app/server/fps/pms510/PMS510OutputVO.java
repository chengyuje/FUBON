package com.systex.jbranch.app.server.fps.pms510;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS510OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;
	
	private List<String> titleBrh;
	private List<String> titleColName;
	private List<String> titleCol;
	
	private List<Map<String, Object>> deptList;

	public List<Map<String, Object>> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<Map<String, Object>> deptList) {
		this.deptList = deptList;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<String> getTitleBrh() {
		return titleBrh;
	}

	public void setTitleBrh(List<String> titleBrh) {
		this.titleBrh = titleBrh;
	}

	public List<String> getTitleColName() {
		return titleColName;
	}

	public void setTitleColName(List<String> titleColName) {
		this.titleColName = titleColName;
	}

	public List<String> getTitleCol() {
		return titleCol;
	}

	public void setTitleCol(List<String> titleCol) {
		this.titleCol = titleCol;
	}

}
