package com.systex.jbranch.app.server.fps.sqm220;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SQM220InputVO extends PagingInputVO {
	
	private String yearMon;
	private String branchNbr;
	private String qtnType;
	private List<Map<String,String>> paramList;
	public List<Map<String, String>> getParamList() {
		return paramList;
	}
	public void setParamList(List<Map<String, String>> paramList) {
		this.paramList = paramList;
	}
	private List<Map<String, Object>> list;
	public String getYearMon() {
		return yearMon;
	}
	public void setYearMon(String yearMon) {
		this.yearMon = yearMon;
	}
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	public String getQtnType() {
		return qtnType;
	}
	public void setQtnType(String qtnType) {
		this.qtnType = qtnType;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}