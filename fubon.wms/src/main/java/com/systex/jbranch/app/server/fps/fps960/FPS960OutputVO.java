package com.systex.jbranch.app.server.fps.fps960;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FPS960OutputVO extends PagingOutputVO {
	private List resultList;
	private List errorList;
	private List errorList2;
	private Boolean haveAuth;
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
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
	public Boolean getHaveAuth() {
		return haveAuth;
	}
	public void setHaveAuth(Boolean haveAuth) {
		this.haveAuth = haveAuth;
	}
}