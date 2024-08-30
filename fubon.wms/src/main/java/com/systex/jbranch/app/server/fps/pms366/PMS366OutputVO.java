package com.systex.jbranch.app.server.fps.pms366;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS366OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> csvList;
	private List<Map<String, Object>> totalList;
	
	private List<Map<String, Object>> dtl_1_List;
	private List<Map<String, Object>> dtl_2_List;
	
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getCsvList() {
		return csvList;
	}

	public void setCsvList(List<Map<String, Object>> csvList) {
		this.csvList = csvList;
	}

	public List<Map<String, Object>> getTotalList() {
		return totalList;
	}

	public void setTotalList(List<Map<String, Object>> totalList) {
		this.totalList = totalList;
	}

	public List<Map<String, Object>> getDtl_1_List() {
		return dtl_1_List;
	}

	public void setDtl_1_List(List<Map<String, Object>> dtl_1_List) {
		this.dtl_1_List = dtl_1_List;
	}

	public List<Map<String, Object>> getDtl_2_List() {
		return dtl_2_List;
	}

	public void setDtl_2_List(List<Map<String, Object>> dtl_2_List) {
		this.dtl_2_List = dtl_2_List;
	}

}
