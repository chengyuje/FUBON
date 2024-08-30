package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

public class AFP910GetProdOutputVO implements Serializable{
	public AFP910GetProdOutputVO(){
		super();
	}
				 
	private List<AFP910ProductVO> prodList;    //產品列表

	public List<AFP910ProductVO> getProdList() {
		return prodList;
	}
	public void setProdList(List<AFP910ProductVO> prodList) {
		this.prodList = prodList;
	}
	
}
