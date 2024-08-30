package com.systex.jbranch.app.server.fps.iot410;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT410OutputVO extends PagingOutputVO {
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> rejectList;
	private Boolean showMsg;

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Map<String, Object>> getRejectList() {
		return rejectList;
	}

	public void setRejectList(List<Map<String, Object>> rejectList) {
		this.rejectList = rejectList;
	}

	public Boolean getShowMsg() {
		return showMsg;
	}

	public void setShowMsg(Boolean showMsg) {
		this.showMsg = showMsg;
	}
	
}
