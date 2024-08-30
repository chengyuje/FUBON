package com.systex.jbranch.app.server.fps.cus130;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class CUS130OutputVO extends PagingOutputVO {
	private List resultList;
	private List resultList2;
	private List resultList3;
	private List resultList4;
	private List sameList;
	private List errorList;
	private List errorList2;
	private String errorMsg;
	private String fileUrl;
	private Boolean canAddRoleFlag;
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getResultList2() {
		return resultList2;
	}
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	public List getResultList3() {
		return resultList3;
	}
	public void setResultList3(List resultList3) {
		this.resultList3 = resultList3;
	}
	public List getResultList4() {
		return resultList4;
	}
	public void setResultList4(List resultList4) {
		this.resultList4 = resultList4;
	}
	public List getSameList() {
		return sameList;
	}
	public void setSameList(List sameList) {
		this.sameList = sameList;
	}
	public List getErrorList() {
		return errorList;
	}
	public void setErrorList(List errorList) {
		this.errorList = errorList;
	}
	public List getErrorList2() {
		return errorList2;
	}
	public void setErrorList2(List errorList2) {
		this.errorList2 = errorList2;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public Boolean getCanAddRoleFlag() {
		return canAddRoleFlag;
	}
	public void setCanAddRoleFlag(Boolean canAddRoleFlag) {
		this.canAddRoleFlag = canAddRoleFlag;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}