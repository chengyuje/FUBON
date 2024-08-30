package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class MktEFCurveVO implements Serializable {
	
	private BigDecimal maxPfoRtn; 		//效率前緣最大年化報酬率
	private BigDecimal minPfoRtn; 		//效率前緣最小年化報酬率
	private List efList; 		//效率前緣曲線清單

	public BigDecimal getMaxPfoRtn() {
		return maxPfoRtn;
	}

	public void setMaxPfoRtn(BigDecimal maxPfoRtn) {
		this.maxPfoRtn = maxPfoRtn;
	}

	public BigDecimal getMinPfoRtn() {
		return minPfoRtn;
	}

	public void setMinPfoRtn(BigDecimal minPfoRtn) {
		this.minPfoRtn = minPfoRtn;
	}

	public List getEfList() {
		return efList;
	}

	public void setEfList(List efList) {
		this.efList = efList;
	}
	
}
