package com.systex.jbranch.app.server.fps.kyc212;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC212OutputVO extends PagingOutputVO{
	
	private List Weights;

	public List getWeights() {
		return Weights;
	}

	public void setWeights(List weights) {
		Weights = weights;
	}
	
	
}
