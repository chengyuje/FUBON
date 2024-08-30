package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

public class GetInsCompareInputVO {
	/**險種清單：商品ID(資訊源商品id集合)*/
	private List<String> lstInsProd;

	public List<String> getLstInsProd() {
		return lstInsProd;
	}

	public void setLstInsProd(List<String> lstInsProd) {
		this.lstInsProd = lstInsProd;
	}
	
}
