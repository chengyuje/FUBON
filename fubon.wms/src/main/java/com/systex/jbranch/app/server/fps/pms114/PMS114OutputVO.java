package com.systex.jbranch.app.server.fps.pms114;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS114OutputVO extends PagingOutputVO {

	private List<Map<String, Object>> qryList;
	private List<Map<String, Object>> rep_qryList;

	public List<Map<String, Object>> getRep_qryList() {
		return rep_qryList;
	}

	public void setRep_qryList(List<Map<String, Object>> rep_qryList) {
		this.rep_qryList = rep_qryList;
	}

	public List<Map<String, Object>> getQryList() {
		return qryList;
	}

	public void setQryList(List<Map<String, Object>> qryList) {
		this.qryList = qryList;
	}

}
