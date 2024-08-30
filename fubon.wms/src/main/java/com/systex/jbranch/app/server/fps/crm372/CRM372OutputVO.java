package com.systex.jbranch.app.server.fps.crm372;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM372OutputVO extends PagingOutputVO {

	private List<Map<String,Object>> resultList;
	private List<String> errorList;
	private String prj_code; 	// 專案代碼
	private String error;

	public List<Map<String,Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}

	public String getPrj_code() {
		return prj_code;
	}

	public void setPrj_code(String prj_code) {
		this.prj_code = prj_code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
	
}
