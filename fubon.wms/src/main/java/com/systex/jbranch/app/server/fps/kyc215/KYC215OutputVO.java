package com.systex.jbranch.app.server.fps.kyc215;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC215OutputVO extends PagingOutputVO{
	
	private List<Map<String,Object>> ResultList;
	private List<Map<String,Object>> WList;
	private List<Map<String,Object>> CList;
	
	
	public List<Map<String, Object>> getResultList() {
		return ResultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		ResultList = resultList;
	}
	public List<Map<String,Object>> getWList() {
		return WList;
	}
	public void setWList(List<Map<String,Object>> wList) {
		WList = wList;
	}
	public List<Map<String,Object>> getCList() {
		return CList;
	}
	public void setCList(List<Map<String,Object>> cList) {
		CList = cList;
	}

}
