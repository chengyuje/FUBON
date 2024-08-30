package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FPAFP910OutputVO implements Serializable {

	private List<Map<String, Object>> modelPortfolioList;//Model Portfolio資料
	private List<Map<String, Object>>  marketOne;
	private List<Map<String, Object>>  marketTwo;
	private List<Map<String, Object>>  marketThree;
	private List<Map<String, Object>>  marketFour;
	private String stockRatio;	//股
	private String bondRatio;	//債
	private String cashRatio;	//貨幣
	
	public String getStockRatio() {
		return stockRatio;
	}
	public void setStockRatio(String stockRatio) {
		this.stockRatio = stockRatio;
	}
	public String getBondRatio() {
		return bondRatio;
	}
	public void setBondRatio(String bondRatio) {
		this.bondRatio = bondRatio;
	}
	public String getCashRatio() {
		return cashRatio;
	}
	public void setCashRatio(String cashRatio) {
		this.cashRatio = cashRatio;
	}
	public List<Map<String, Object>> getModelPortfolioList() {
		return modelPortfolioList;
	}
	public void setModelPortfolioList(List<Map<String, Object>> modelPortfolioList) {
		this.modelPortfolioList = modelPortfolioList;
	}
	public List<Map<String, Object>> getMarketOne() {
		return marketOne;
	}
	public void setMarketOne(List<Map<String, Object>> marketOne) {
		this.marketOne = marketOne;
	}
	public List<Map<String, Object>> getMarketTwo() {
		return marketTwo;
	}
	public void setMarketTwo(List<Map<String, Object>> marketTwo) {
		this.marketTwo = marketTwo;
	}
	public List<Map<String, Object>> getMarketThree() {
		return marketThree;
	}
	public void setMarketThree(List<Map<String, Object>> marketThree) {
		this.marketThree = marketThree;
	}
	public List<Map<String, Object>> getMarketFour() {
		return marketFour;
	}
	public void setMarketFour(List<Map<String, Object>> marketFour) {
		this.marketFour = marketFour;
	}

	
}
