package com.systex.jbranch.app.server.fps.kyc620;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC620OutputVO extends PagingOutputVO{
	private List resultList;
	
	private List list;
	private List rolelist;

	private List AoCntLst;
	public List getAoCntLst() {
		return AoCntLst;
	}
	public void setAoCntLst(List aoCntLst) {
		AoCntLst = aoCntLst;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getRolelist() {
		return rolelist;
	}
	public void setRolelist(List rolelist) {
		this.rolelist = rolelist;
	}
}
