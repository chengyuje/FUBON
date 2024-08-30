package com.systex.jbranch.app.server.fps.cmmgr010;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR010OutputVO extends PagingOutputVO {

	public CMMGR010OutputVO() {
	}

	private String resultType;
	private List resultList;


	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

}
