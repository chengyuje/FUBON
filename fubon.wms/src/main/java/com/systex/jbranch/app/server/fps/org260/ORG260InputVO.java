package com.systex.jbranch.app.server.fps.org260;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG260InputVO extends PagingInputVO {
	
	private String fileName;
	private String actualFileName;
	private String uEmpID;
	
	private String prjID; // 以上傳代號查詢完整上傳記錄
	
	private List<Map<String, Object>> exportList;
	
	private String regionCenterID;
	private String branchAreaID;
	
	public String getBranchAreaID() {
		return branchAreaID;
	}

	public void setBranchAreaID(String branchAreaID) {
		this.branchAreaID = branchAreaID;
	}

	public String getRegionCenterID() {
		return regionCenterID;
	}

	public void setRegionCenterID(String regionCenterID) {
		this.regionCenterID = regionCenterID;
	}

	public String getuEmpID() {
		return uEmpID;
	}

	public List<Map<String, Object>> getExportList() {
		return exportList;
	}

	public void setExportList(List<Map<String, Object>> exportList) {
		this.exportList = exportList;
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

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getActualFileName() {
		return actualFileName;
	}
	
	public void setActualFileName(String actualFileName) {
		this.actualFileName = actualFileName;
	}
	
}
