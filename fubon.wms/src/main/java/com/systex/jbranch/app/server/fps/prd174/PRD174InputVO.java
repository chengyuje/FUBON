package com.systex.jbranch.app.server.fps.prd174;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD174InputVO extends PagingInputVO{
	
	private String Q_TYPE;
	private String Q_ID;
	private String fileName;
	private List list;
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getQ_ID() {
		return Q_ID;
	}

	public void setQ_ID(String q_ID) {
		Q_ID = q_ID;
	}

	public String getQ_TYPE() {
		return Q_TYPE;
	}

	public void setQ_TYPE(String q_TYPE) {
		Q_TYPE = q_TYPE;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
	
	
	
}
