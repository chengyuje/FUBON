package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

public class GetFamilyGapsOutputVO {

	public GetFamilyGapsOutputVO() {
		super();
	}

	private List lstFamilyGaps;	// 家庭缺口資訊

	public List getLstFamilyGaps() {
		return lstFamilyGaps;
	}
	public void setLstFamilyGaps(List lstFamilyGaps) {
		this.lstFamilyGaps = lstFamilyGaps;
	}
}
