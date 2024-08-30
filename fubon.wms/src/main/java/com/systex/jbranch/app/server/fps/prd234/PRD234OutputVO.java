package com.systex.jbranch.app.server.fps.prd234;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD234OutputVO extends PagingOutputVO {
	private String eid;
	private Boolean ifExist;
	private List resultList;
	private List errorList;
	private List errorList2;
	private List errorList3;
	private List errorList4;
	
	
	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public Boolean getIfExist() {
		return ifExist;
	}
	public void setIfExist(Boolean ifExist) {
		this.ifExist = ifExist;
	}
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
	public List getErrorList3() {
		return errorList3;
	}
	public void setErrorList3(List errorList3) {
		this.errorList3 = errorList3;
	}
	public List getErrorList4() {
		return errorList4;
	}
	public void setErrorList4(List errorList4) {
		this.errorList4 = errorList4;
	}
}