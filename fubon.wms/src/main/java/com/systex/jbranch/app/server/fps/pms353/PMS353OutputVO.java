package com.systex.jbranch.app.server.fps.pms353;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS353OutputVO extends PagingOutputVO {

	private List resultList; //通用查詢結果資訊LIST
	private List header; //存表頭LIST
	private List collist;	//存內容LIST

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getHeader() {
		return header;
	}

	public void setHeader(List header) {
		this.header = header;
	}

	public List getCollist() {
		return collist;
	}

	public void setCollist(List collist) {
		this.collist = collist;
	}
}
