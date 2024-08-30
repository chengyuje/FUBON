package com.systex.jbranch.app.server.fps.iot200;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT200OutputVO extends PagingInputVO{
	private List resultList;    //電子要保書通報送件查詢結果
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
