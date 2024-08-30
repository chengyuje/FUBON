package com.systex.jbranch.app.server.fps.crm211;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM211OutputVO extends PagingOutputVO{
	private List resultList;
	
	private String errorMsg;
	
	private boolean showMsg;
	
	

	public boolean isShowMsg() {
		return showMsg;
	}
	public void setShowMsg(boolean showMsg) {
		this.showMsg = showMsg;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
