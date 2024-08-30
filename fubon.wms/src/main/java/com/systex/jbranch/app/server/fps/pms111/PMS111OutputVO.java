package com.systex.jbranch.app.server.fps.pms111;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS111OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> execStatisticsList;
	private List<Map<String, Object>> noContentDtlList;
	private List<Map<String, Object>> sDateList;
	private List<Map<String, Object>> eDateList;
	
	private List<Map<String, Object>> rep_execStatisticsList;

	public List<Map<String, Object>> getRep_execStatisticsList() {
		return rep_execStatisticsList;
	}

	public void setRep_execStatisticsList(List<Map<String, Object>> rep_execStatisticsList) {
		this.rep_execStatisticsList = rep_execStatisticsList;
	}

	public List<Map<String, Object>> getsDateList() {
		return sDateList;
	}

	public void setsDateList(List<Map<String, Object>> sDateList) {
		this.sDateList = sDateList;
	}

	public List<Map<String, Object>> geteDateList() {
		return eDateList;
	}

	public void seteDateList(List<Map<String, Object>> eDateList) {
		this.eDateList = eDateList;
	}

	public List<Map<String, Object>> getExecStatisticsList() {
		return execStatisticsList;
	}

	public void setExecStatisticsList(List<Map<String, Object>> execStatisticsList) {
		this.execStatisticsList = execStatisticsList;
	}

	public List<Map<String, Object>> getNoContentDtlList() {
		return noContentDtlList;
	}

	public void setNoContentDtlList(List<Map<String, Object>> noContentDtlList) {
		this.noContentDtlList = noContentDtlList;
	}

}
