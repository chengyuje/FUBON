package com.systex.jbranch.app.server.fps.pms423;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class PMS423OutputVO extends PagingOutputVO {
	private List resultList;
	private String successMsg;

	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getSuccessMsg() {
		return successMsg;
	}

	public void setSuccessMsg(String successMsg) {
		this.successMsg = successMsg;
	}
	
}
