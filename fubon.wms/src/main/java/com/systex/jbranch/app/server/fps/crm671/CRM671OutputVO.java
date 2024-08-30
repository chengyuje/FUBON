package com.systex.jbranch.app.server.fps.crm671;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM671OutputVO extends PagingOutputVO {
	private List<Map<String,Object>> resultList;
	private List<Map<String,Object>> resultList2;
	private List<Map<String,Object>> resultList3;
	private List<Map<String,Object>> amt;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getResultList2() {
		return resultList2;
	}
	public void setResultList2(List<Map<String, Object>> resultList2) {
		this.resultList2 = resultList2;
	}
	public List<Map<String, Object>> getResultList3() {
		return resultList3;
	}
	public void setResultList3(List<Map<String, Object>> resultList3) {
		this.resultList3 = resultList3;
	}
	public List<Map<String, Object>> getAmt() {
		return amt;
	}
	public void setAmt(List<Map<String, Object>> amt) {
		this.amt = amt;
	}
	
}
