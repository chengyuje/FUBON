package com.systex.jbranch.app.server.fps.pms309;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS309OutputVO extends PagingOutputVO{
	private List resultList;
	private String srchType;
	private List<Map<String, String>> orgList;
	private List totalList; // 全行總計
	private List regionCenterList; // 業務處總計
	private List branchAreaList; // 區總計
	private List paramList; 
	
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
	public String getSrchType() {
		return srchType;
	}
	public void setSrchType(String srchType) {
		this.srchType = srchType;
	}
	public List<Map<String, String>> getOrgList() {
		return orgList;
	}
	public void setOrgList(List<Map<String, String>> orgList) {
		this.orgList = orgList;
	}
	public void setRegionCenterList(List regionCenterList) {
		this.regionCenterList = regionCenterList;
	}
	public List getRegionCenterList() {
		return regionCenterList;
	}
	public void setBranchAreaList(List branchAreaList) {
		this.branchAreaList = branchAreaList;
	}
	public List getBranchAreaList() {
		return branchAreaList;
	}
	public void setParamList(List paramList) {
		this.paramList = paramList;
	}
	public List getParamList() {
		return paramList;
	}
}
