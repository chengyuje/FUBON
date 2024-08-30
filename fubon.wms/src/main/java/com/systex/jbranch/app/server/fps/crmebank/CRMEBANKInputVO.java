package com.systex.jbranch.app.server.fps.crmebank;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRMEBANKInputVO extends PagingInputVO{
	private String PARAM_CODE;
	private String PARAM_DESC;
	private String PARAM_NAME;
	
	public String getPARAM_CODE() {
		return PARAM_CODE;
	}
	public void setPARAM_CODE(String pARAM_CODE) {
		PARAM_CODE = pARAM_CODE;
	}
	public String getPARAM_DESC() {
		return PARAM_DESC;
	}
	public void setPARAM_DESC(String pARAM_DESC) {
		PARAM_DESC = pARAM_DESC;
	}
	public String getPARAM_NAME() {
		return PARAM_NAME;
	}
	public void setPARAM_NAME(String pARAM_NAME) {
		PARAM_NAME = pARAM_NAME;
	}

	
}
