package com.systex.jbranch.app.server.fps.ref900;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class REF900OutputVO extends PagingOutputVO {
	private List resultList;
	private List errorMsg;
	private List errorMsg2;
	
	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(List errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List getErrorMsg2() {
		return errorMsg2;
	}
	public void setErrorMsg2(List errorMsg2) {
		this.errorMsg2 = errorMsg2;
	}
}