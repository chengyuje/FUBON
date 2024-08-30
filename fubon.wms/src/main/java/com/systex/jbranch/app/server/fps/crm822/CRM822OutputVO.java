package com.systex.jbranch.app.server.fps.crm822;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM822OutputVO extends PagingOutputVO{
	private List resultList_etf;
	private List resultList_stock;
	private List resultList;
	
	private BigDecimal etfStockAmount = new BigDecimal(0);
	
	public List getResultList_etf() {
		return resultList_etf;
	}
	
	public void setResultList_etf(List resultList_etf) {
		this.resultList_etf = resultList_etf;
	}
	
	public List getResultList_stock() {
		return resultList_stock;
	}
	
	public void setResultList_stock(List resultList_stock) {
		this.resultList_stock = resultList_stock;
	}
	
	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public BigDecimal getAmount() {
		return etfStockAmount;
	}

	public void setAmount(BigDecimal amount) {
		this.etfStockAmount = amount;
	}

	public BigDecimal getEtfStockAmount() {
		return etfStockAmount;
	}

	public void setEtfStockAmount(BigDecimal etfStockAmount) {
		this.etfStockAmount = etfStockAmount;
	}
	
}
