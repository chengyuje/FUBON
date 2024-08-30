package com.systex.jbranch.app.server.fps.org260;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG260OutputVO extends PagingOutputVO {
	
	private List<Map<String, String>> uhrmList;
	private String prjID;
	private List<Map<String, String>> resultList; 
	private String uEmpID;

	private String regionCenterID;
	private String branchAreaID;
	
	public String getRegionCenterID() {
		return regionCenterID;
	}

	public void setRegionCenterID(String regionCenterID) {
		this.regionCenterID = regionCenterID;
	}

	public String getBranchAreaID() {
		return branchAreaID;
	}

	public void setBranchAreaID(String branchAreaID) {
		this.branchAreaID = branchAreaID;
	}

	public String getuEmpID() {
		return uEmpID;
	}

	public void setuEmpID(String uEmpID) {
		this.uEmpID = uEmpID;
	}

	public String getPrjID() {
		return prjID;
	}

	public void setPrjID(String prjID) {
		this.prjID = prjID;
	}

	public List<Map<String, String>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, String>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, String>> getUhrmList() {
		return uhrmList;
	}

	public void setUhrmList(List<Map<String, String>> uhrmList) {
		this.uhrmList = uhrmList;
	}

}
