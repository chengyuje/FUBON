package com.systex.jbranch.app.server.fps.cam230;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM230OutputVO extends PagingOutputVO {
	
	private List resultList;
	private Integer totalNum;
	private Integer successNum;
	private Integer failureNum;
	
	private List fileList;
	private Integer assignRow;

	public Integer getAssignRow() {
		return assignRow;
	}

	public void setAssignRow(Integer assignRow) {
		this.assignRow = assignRow;
	}

	public List getFileList() {
		return fileList;
	}

	public void setFileList(List fileList) {
		this.fileList = fileList;
	}

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	public Integer getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(Integer successNum) {
		this.successNum = successNum;
	}

	public Integer getFailureNum() {
		return failureNum;
	}

	public void setFailureNum(Integer failureNum) {
		this.failureNum = failureNum;
	}

	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
