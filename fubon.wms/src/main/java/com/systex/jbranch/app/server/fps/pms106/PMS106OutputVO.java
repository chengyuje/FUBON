package com.systex.jbranch.app.server.fps.pms106;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS106OutputVO extends PagingOutputVO{
	private List resultList;
	private List resultList2;
	private List totalList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getTotalList() {
		return totalList;
	}
	public List getResultList2() {
		return resultList2;
	}
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
}
