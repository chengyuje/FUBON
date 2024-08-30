package com.systex.jbranch.app.server.fps.org120;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class ORG120OutputVO extends PagingOutputVO{
	
	private List aoLst;			// 理專列表
//	private List aoCodeLst;	
	private List dataList;		// 查詢結果
	private List modList;		// 修改頁查詢結果
	private List freeAoCodeList;
	private List reviewList;

	public List getFreeAoCodeList() {
		return freeAoCodeList;
	}
	public void setFreeAoCodeList(List freeAoCodeList) {
		this.freeAoCodeList = freeAoCodeList;
	}
	public List getAoLst() {
		return aoLst;
	}
	public void setAoLst(List aoLst) {
		this.aoLst = aoLst;
	}
//	public List getAoCodeLst() {
//		return aoCodeLst;
//	}
//	public void setAoCodeLst(List aoCodeLst) {
//		this.aoCodeLst = aoCodeLst;
//	}
	public List getDataList() {
		return dataList;
	}
	public void setDataList(List dataList) {
		this.dataList = dataList;
	}
	public List getModList() {
		return modList;
	}
	public void setModList(List modList) {
		this.modList = modList;
	}
	public List getReviewList() {
		return reviewList;
	}
	public void setReviewList(List reviewList) {
		this.reviewList = reviewList;
	}
}
