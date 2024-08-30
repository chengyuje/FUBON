package com.systex.jbranch.app.server.fps.ins441;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS441OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List compList;		// 保險公司下拉選單
	private List prdList;		// 險種下拉選單
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getCompList() {
		return compList;
	}
	public void setCompList(List compList) {
		this.compList = compList;
	}
	public List getPrdList() {
		return prdList;
	}
	public void setPrdList(List prdList) {
		this.prdList = prdList;
	}
	
}
