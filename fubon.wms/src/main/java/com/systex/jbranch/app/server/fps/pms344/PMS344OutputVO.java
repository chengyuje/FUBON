package com.systex.jbranch.app.server.fps.pms344;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS344OutputVO extends PagingOutputVO {
	private List resultList; // 分頁list
	private List resultList2; // 匯出Csv全部資訊list
	private List List; // 匯出暫存csv專用
	private List DateList;  //撈區list
	private String element; // 前端 會存放table名稱

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public List getList() {
		return List;
	}

	public void setList(List list) {
		List = list;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getDateList() {
		return DateList;
	}

	public void setDateList(List dateList) {
		DateList = dateList;
	}
}
