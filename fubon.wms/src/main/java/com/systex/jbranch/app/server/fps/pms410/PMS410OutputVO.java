package com.systex.jbranch.app.server.fps.pms410;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PMS410OutputVO extends PagingOutputVO {
	private List resultList;
	private List totalList;
    /*0002230*/
	private List resultList2;
	

	private Boolean SHOW; 
	
	
	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	public Boolean getSHOW() {
		return SHOW;
	}

	public void setSHOW(Boolean sHOW) {
		SHOW = sHOW;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getTotalList() {
		return totalList;
	}

	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
}
