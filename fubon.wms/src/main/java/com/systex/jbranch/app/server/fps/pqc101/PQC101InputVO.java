package com.systex.jbranch.app.server.fps.pqc101;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PQC101InputVO extends PagingInputVO {

	private String prdType;
	private String prdID;
	private String reportType;

	private String queryType;
	private String updType;

	private List<Map<String, Object>> basicUpdList;
	private List<Map<String, Object>> orgUpdList;

	public List<Map<String, Object>> getBasicUpdList() {
		return basicUpdList;
	}

	public void setBasicUpdList(List<Map<String, Object>> basicUpdList) {
		this.basicUpdList = basicUpdList;
	}

	public List<Map<String, Object>> getOrgUpdList() {
		return orgUpdList;
	}

	public void setOrgUpdList(List<Map<String, Object>> orgUpdList) {
		this.orgUpdList = orgUpdList;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getUpdType() {
		return updType;
	}

	public void setUpdType(String updType) {
		this.updType = updType;
	}

	public String getPrdType() {
		return prdType;
	}

	public void setPrdType(String prdType) {
		this.prdType = prdType;
	}

	public String getPrdID() {
		return prdID;
	}

	public void setPrdID(String prdID) {
		this.prdID = prdID;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

}
