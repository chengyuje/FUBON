package com.systex.jbranch.app.server.fps.prd231;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD231OutputVO extends PagingOutputVO {
	private String eid;
	private Boolean ifExist;
	private List resultList;
	private List mktTier1List;
	private List mktTier2List;
	private List mktTier3List;
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
	public List getMktTier1List() {
		return mktTier1List;
	}
	public void setMktTier1List(List mktTier1List) {
		this.mktTier1List = mktTier1List;
	}
	public List getMktTier2List() {
		return mktTier2List;
	}
	public void setMktTier2List(List mktTier2List) {
		this.mktTier2List = mktTier2List;
	}
	public List getMktTier3List() {
		return mktTier3List;
	}
	public void setMktTier3List(List mktTier3List) {
		this.mktTier3List = mktTier3List;
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