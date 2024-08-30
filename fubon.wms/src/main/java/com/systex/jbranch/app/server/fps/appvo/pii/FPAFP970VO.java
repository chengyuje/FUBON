package com.systex.jbranch.app.server.fps.appvo.pii;

import java.util.List;
import java.io.Serializable;

public class FPAFP970VO implements Serializable{
	public FPAFP970VO(){
		super();
	}

	private List straProLossList;     //投資策略停損停利標準業務參數VO

	public List getStraProLossList() {
		return straProLossList;
	}

	public void setStraProLossList(List straProLossList) {
		this.straProLossList = straProLossList;
	}
	
}
