package com.systex.jbranch.app.server.fps.appvo.pii;

import java.util.List;
import java.io.Serializable;

public class FPAFP991VO implements Serializable{
	public FPAFP991VO(){
		super();
	}

	private List invstProLossList;     //投資期望停損停利標準業務參數VO

	public List getInvstProLossList() {
		return invstProLossList;
	}

	public void setInvstProLossList(List invstProLossList) {
		this.invstProLossList = invstProLossList;
	}


}
