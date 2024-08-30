package com.systex.jbranch.app.server.fps.crm613;


import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM613OutputVO extends PagingOutputVO{
	
	
	private List resultList;
	private List resultList2;
	private List<Map<String, Object>> uhrmChgLogList;
	
	public List<Map<String, Object>> getUhrmChgLogList() {
		return uhrmChgLogList;
	}
	
	public void setUhrmChgLogList(List<Map<String, Object>> uhrmChgLogList) {
		this.uhrmChgLogList = uhrmChgLogList;
	}
	
	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	
	public List getResultList2() {
		return resultList2;
	}
	
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	
	
}