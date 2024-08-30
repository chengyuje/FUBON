package com.systex.jbranch.app.server.fps.ins100;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS100OutputVO extends PagingInputVO {
	
	
	private List<Map<String, Object>> resultList; 	//健診對象清單
	private List policyList;	//保單號碼清單

	
	
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public List getPolicyList() {
		return policyList;
	}
	public void setPolicyList(List policyList) {
		this.policyList = policyList;
	}	
	
}
