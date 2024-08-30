package com.systex.jbranch.app.server.fps.crm825;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM825OutputVO extends PagingOutputVO{
	private List<Map<String,Object>> resultList;
	private List<Map<String,Object>> txnList;  //交易明細
	private List<Map<String,Object>> divList; //配息明細
	private List<Map<String,Object>> priceList; //報價
	private List<Map<String,Object>> bds060List; //TBPMS_BDS060
	private List<Map<String,Object>> sninfoList; //TBPRD_SNINFO
	
	public List<Map<String,Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String,Object>> resultList) {
		this.resultList = resultList;
	}
	public List<Map<String,Object>> getTxnList() {
		return txnList;
	}
	public void setTxnList(List<Map<String,Object>> txnList) {
		this.txnList = txnList;
	}
	public List<Map<String,Object>> getDivList() {
		return divList;
	}
	public void setDivList(List<Map<String,Object>> divList) {
		this.divList = divList;
	}
	public List<Map<String, Object>> getPriceList() {
		return priceList;
	}
	public void setPriceList(List<Map<String, Object>> priceList) {
		this.priceList = priceList;
	}
	public List<Map<String, Object>> getBds060List() {
		return bds060List;
	}
	public void setBds060List(List<Map<String, Object>> bds060List) {
		this.bds060List = bds060List;
	}
	public List<Map<String, Object>> getSninfoList() {
		return sninfoList;
	}
	public void setSninfoList(List<Map<String, Object>> sninfoList) {
		this.sninfoList = sninfoList;
	}
}