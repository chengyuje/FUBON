package com.systex.jbranch.app.server.fps.mtc110;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class MTC110OutputVO extends PagingOutputVO{
	private List<Map<String,Object>> resultList;
	private String CON_NO;

	public List<Map<String,Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}

	public String getCON_NO() {
		return CON_NO;
	}

	public void setCON_NO(String cON_NO) {
		CON_NO = cON_NO;
	}
	
}
	
