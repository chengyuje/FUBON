package com.systex.jbranch.app.server.fps.crm997;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM997OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> addToList;
	
	
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getAddToList() {
		return addToList;
	}
	public void setAddToList(List<Map<String, Object>> addToList) {
		this.addToList = addToList;
	}
	
	
}
