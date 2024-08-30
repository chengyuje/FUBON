package com.systex.jbranch.app.server.fps.jsb100;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class JSB100OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> resultList;
	private String STATUS;
	private String SEQ_NO;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getSEQ_NO() {
		return SEQ_NO;
	}

	public void setSEQ_NO(String sEQ_NO) {
		SEQ_NO = sEQ_NO;
	}
	
}
