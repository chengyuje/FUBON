package com.systex.jbranch.app.server.fps.sot222;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot705.CustAssetETFVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT222OutputVO extends PagingOutputVO {
	
	private List<CustAssetETFVO> etfList;
	private List<CustAssetETFVO> stockList;
	
	private String errorMsg;
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<CustAssetETFVO> getEtfList() {
		return etfList;
	}

	public void setEtfList(List<CustAssetETFVO> list) {
		this.etfList = list;
	}

	public List<CustAssetETFVO> getStockList() {
		return stockList;
	}

	public void setStockList(List<CustAssetETFVO> list) {
		this.stockList = list;
	}

}
