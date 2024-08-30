package com.systex.jbranch.app.server.fps.pms510;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS510InputVO extends PagingInputVO {

	private String sCreDate;
	private String reportHierarchy;
	private String deptID;
	
	private List<String> titleBrh;
	private List<String> titleColName;
	private List<String> titleCol;
	private List<Map<String, Object>> exportList;
	
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

	public List<Map<String, Object>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, Object>> exportList) {
		this.exportList = exportList;
	}

	public String getsCreDate() {
		return sCreDate;
	}
	
	public void setsCreDate(String sCreDate) {
		this.sCreDate = sCreDate;
	}
	
	public String getReportHierarchy() {
		return reportHierarchy;
	}
	
	public void setReportHierarchy(String reportHierarchy) {
		this.reportHierarchy = reportHierarchy;
	}
	
	public String getDeptID() {
		return deptID;
	}
	
	public void setDeptID(String deptID) {
		this.deptID = deptID;
	}

}
