package com.systex.jbranch.app.server.fps.pms354;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS354OutputVO extends PagingOutputVO {
	private List totalList;
	private List resultList;
	private List resultList2;
	
	private List totalprd;
	private List totalbal;
	
	private List colList;
	private List colList2;
	
	//營運區  區域中心 合計
	private List eachSet;
	
	
	private String aocode;
	

	public List getTotalprd() {
		return totalprd;
	}

	public void setTotalprd(List totalprd) {
		this.totalprd = totalprd;
	}

	public List getTotalbal() {
		return totalbal;
	}

	public void setTotalbal(List totalbal) {
		this.totalbal = totalbal;
	}

	public String getAocode() {
		return aocode;
	}

	public void setAocode(String aocode) {
		this.aocode = aocode;
	}

	public List getTotalList() {
		return totalList;
	}

	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getColList() {
		return colList;
	}

	public void setColList(List colList) {
		this.colList = colList;
	}

	public List getColList2() {
		return colList2;
	}

	public void setColList2(List colList2) {
		this.colList2 = colList2;
	}

	public List getEachSet() {
		return eachSet;
	}

	public void setEachSet(List eachSet) {
		this.eachSet = eachSet;
	}

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	

}
