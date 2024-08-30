package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class FPAFP930InputVO implements Serializable{
	public FPAFP930InputVO(){
		super();
	}
	
	private String prodCat; //資產類別

	public String getProdCat() {
		return prodCat;
	}
	public void setProdCat(String prodCat) {
		this.prodCat = prodCat;
	}
}
