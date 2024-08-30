package com.systex.jbranch.app.server.fps.pms227;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS227InputVO extends PagingInputVO {
	
	private String sTime;
	private String branch;
	private String region;
	private String op;
	private String REF_ID;
	private String TP_ID;
	private String fileName;
	private String realfileName;
	private String uploadName;
	private List<Map<String, Object>> csvList;

	public List<Map<String, Object>> getCsvList() {
		return csvList;
	}

	public void setCsvList(List<Map<String, Object>> csvList) {
		this.csvList = csvList;
	}

	public String getUploadName() {
		return uploadName;
	}

	public void setUploadName(String uploadName) {
		this.uploadName = uploadName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRealfileName() {
		return realfileName;
	}

	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}

	public String getREF_ID() {
		return REF_ID;
	}

	public void setREF_ID(String rEF_ID) {
		REF_ID = rEF_ID;
	}

	public String getTP_ID() {
		return TP_ID;
	}

	public void setTP_ID(String tP_ID) {
		TP_ID = tP_ID;
	}

	public String getBranch() {
		return branch;
	}

	public String getRegion() {
		return region;
	}

	public String getOp() {
		return op;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setOp(String op) {
		this.op = op;
	}
}
