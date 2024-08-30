package com.systex.jbranch.app.server.fps.pms429;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS429OutputVO extends PagingOutputVO {
	
	private List resultList;
	private String validateRecseqMsg;

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public String getValidateRecseqMsg() {
		return validateRecseqMsg;
	}

	public void setValidateRecseqMsg(String validateRecseqMsg) {
		this.validateRecseqMsg = validateRecseqMsg;
	}

}
