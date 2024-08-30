package com.systex.jbranch.app.server.fps.pms301;

import java.util.List;

import oracle.sql.DATE;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS301OutputVO  extends PagingOutputVO{
	private List resultList;
	private List sumAllList;   ///[營運區][區域中心]合計
	private List list;
	private List csvList;   //csv 
	private List cList; //產出日
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}
	public List getSumAllList() {
		return sumAllList;
	}
	public void setSumAllList(List sumAllList) {
		this.sumAllList = sumAllList;
	}
	public List getcList() {
		return cList;
	}
	public void setcList(List cList) {
		this.cList = cList;
	}
	
	
}
