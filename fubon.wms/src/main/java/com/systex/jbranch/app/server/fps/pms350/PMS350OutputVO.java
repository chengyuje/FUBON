package com.systex.jbranch.app.server.fps.pms350;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS350OutputVO extends PagingOutputVO {
	private List resultList; //查詢資訊
	private List resultList2;
	private List totalList;
	private List errorList;
	private List list; // 暫存csv list
	private List csvList; // csv查詢list
	private List namelist; // 報表名稱 list
	private List typelist; // 報表類型 list
	private List deptlist; // 提供單位 list
	private List deptNamelist; // 提供單位名稱 list
	private List countList; //資料筆數list
	private String reportId;
	private String uploadFlag;
	private String[] line_name_array;
	
	public String getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public List getCountList() {
		return countList;
	}

	public void setCountList(List countList) {
		this.countList = countList;
	}

	public List getDeptNamelist() {
		return deptNamelist;
	}

	public void setDeptNamelist(List deptNamelist) {
		this.deptNamelist = deptNamelist;
	}

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public List getNamelist() {
		return namelist;
	}

	public void setNamelist(List namelist) {
		this.namelist = namelist;
	}

	public List getCsvList() {
		return csvList;
	}

	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
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

	public List getErrorList() {
		return errorList;
	}

	public void setErrorList(List errorList) {
		this.errorList = errorList;
	}

	public List getTypelist() {
		return typelist;
	}

	public void setTypelist(List typelist) {
		this.typelist = typelist;
	}

	public List getDeptlist() {
		return deptlist;
	}

	public void setDeptlist(List deptlist) {
		this.deptlist = deptlist;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String[] getLine_name_array() {
		return line_name_array;
	}

	public void setLine_name_array(String[] line_name_array) {
		this.line_name_array = line_name_array;
	}
	
}
