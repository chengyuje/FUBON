package com.systex.jbranch.app.server.fps.kyc216;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC216OutputVO extends PagingOutputVO{
	
	private List LRateList;

	public List getLRateList() {
		return LRateList;
	}

	public void setLRateList(List lRateList) {
		LRateList = lRateList;
	}
}
