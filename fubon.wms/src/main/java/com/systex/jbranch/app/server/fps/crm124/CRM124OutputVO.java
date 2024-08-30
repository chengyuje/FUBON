package com.systex.jbranch.app.server.fps.crm124;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM124OutputVO extends PagingOutputVO{
	private List resultList;
	private List holidayList;
	private List aoList;
	private List salesplanList;
	private List edit_remind_List;
	private List braList;

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getHolidayList() {
		return holidayList;
	}
	public void setHolidayList(List holidayList) {
		this.holidayList = holidayList;
	}
	public List getAoList() {
		return aoList;
	}
	public void setAoList(List aoList) {
		this.aoList = aoList;
	}
	public List getSalesplanList() {
		return salesplanList;
	}
	public void setSalesplanList(List salesplanList) {
		this.salesplanList = salesplanList;
	}
	public List getEdit_remind_List() {
		return edit_remind_List;
	}
	public void setEdit_remind_List(List edit_remind_List) {
		this.edit_remind_List = edit_remind_List;
	}
	public List getBraList() {
		return braList;
	}
	public void setBraList(List braList) {
		this.braList = braList;
	}
	
}
