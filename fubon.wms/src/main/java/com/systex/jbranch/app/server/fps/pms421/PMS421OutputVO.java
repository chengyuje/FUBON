package com.systex.jbranch.app.server.fps.pms421;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS421OutputVO extends PagingOutputVO {
	private List resultList;
	private List DATE_LIST;
	private List SERVICE_LIST;
	private List EXECUTE_TIME;
	private String successMsg;
	
	
	public List getEXECUTE_TIME() {
		return EXECUTE_TIME;
	}

	public void setEXECUTE_TIME(List eXECUTE_TIME) {
		EXECUTE_TIME = eXECUTE_TIME;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getDATE_LIST() {
		return DATE_LIST;
	}

	public void setDATE_LIST(List dATE_LIST) {
		DATE_LIST = dATE_LIST;
	}

	public List getSERVICE_LIST() {
		return SERVICE_LIST;
	}

	public void setSERVICE_LIST(List sERVICE_LIST) {
		SERVICE_LIST = sERVICE_LIST;
	}
	public String getSuccessMsg() {
		return successMsg;
	}

	public void setSuccessMsg(String successMsg) {
		this.successMsg = successMsg;
	}
}
