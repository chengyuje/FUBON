package com.systex.jbranch.app.server.fps.crm361;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM361OutputVO extends PagingOutputVO {
	private List ao_list;
	private List prj_list;
	private List resultList;
	private String resultList2;
	private String errorMsg;
	
	public List getAo_list() {
		return ao_list;
	}
	public void setAo_list(List ao_list) {
		this.ao_list = ao_list;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public String getResultList2() {
		return resultList2;
	}
	public void setResultList2(String resultList2) {
		this.resultList2 = resultList2;
	}
	public List getPrj_list() {
		return prj_list;
	}
	public void setPrj_list(List prj_list) {
		this.prj_list = prj_list;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
