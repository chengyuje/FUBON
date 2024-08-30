package com.systex.jbranch.app.server.fps.ins132;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS132OutputVO  extends PagingOutputVO{

	private List calFamilyGapList;

	public List getCalFamilyGapList() {
		return calFamilyGapList;
	}

	public void setCalFamilyGapList(List calFamilyGapList) {
		this.calFamilyGapList = calFamilyGapList;
	}
}
