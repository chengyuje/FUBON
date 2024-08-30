package com.systex.jbranch.app.server.fps.iot190;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT190OutputVO extends PagingOutputVO{
	private List resultList;	  //搜索結果
	private List list;
	
	
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
