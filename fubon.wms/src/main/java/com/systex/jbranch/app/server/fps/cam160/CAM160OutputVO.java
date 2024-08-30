package com.systex.jbranch.app.server.fps.cam160;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM160OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List resultList_his;
	private List leadsList;

	public List getResultList_his() {
		return resultList_his;
	}

	public List getLeadsList() {
		return leadsList;
	}

	public void setLeadsList(List leadsList) {
		this.leadsList = leadsList;
	}

	public void setResultList_his(List resultList_his) {
		this.resultList_his = resultList_his;
	}

	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
