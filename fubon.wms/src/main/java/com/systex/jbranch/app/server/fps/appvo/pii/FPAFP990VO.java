package com.systex.jbranch.app.server.fps.appvo.pii;

import java.util.List;
import java.io.Serializable;

public class FPAFP990VO implements Serializable{
	public FPAFP990VO(){
		super();
	}

	private List prodProLossList;     //產品風險停損停利標準業務參數VO

	public List getProdProLossList() {
		return prodProLossList;
	}

	public void setProdProLossList(List prodProLossList) {
		this.prodProLossList = prodProLossList;
	}

	
}
