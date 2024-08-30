package com.systex.jbranch.app.server.fps.cam211;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CAM211OutputVO extends PagingOutputVO {

	private List resultList;
	private List resultList2;
	private List aolist;

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getAolist() {
		return aolist;
	}

	public void setAolist(List aolist) {
		this.aolist = aolist;
	}
}
