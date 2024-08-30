package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;

public class FPAALProdOutputVO implements Serializable{
	//商品資料清單
	private List prodList;
	//投顧建議6M
	private int displayPeriod;
	
	public List getProdList() {
		return prodList;
	}
	public void setProdList(List prodList) {
		this.prodList = prodList;
	}
	public int getDisplayPeriod() {
		return displayPeriod;
	}
	public void setDisplayPeriod(int displayPeriod) {
		this.displayPeriod = displayPeriod;
	}
}
