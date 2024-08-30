package com.systex.jbranch.fubon.commons.esb.vo.nmvp9a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.StringUtils;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NMVP9AOutputDetailsVO {

	@XmlElement
	private String ContractId; //契約編號	
	@XmlElement
	private String ProdId; //金錢信託商品代碼	
	@XmlElement
	private String ExtProdId; //外部股票商品代碼
	@XmlElement
	private String StockNum; //庫存股數
	@XmlElement
	private String AvlStockNum; //可贖股數
	@XmlElement
	private String Currency; //交易幣別
	@XmlElement
	private String AccountNo; //交易幣主運用帳號
	
	
	public String getContractId() {
		return ContractId;
	}
	public void setContractId(String contractId) {
		ContractId = contractId;
	}
	public String getProdId() {
		return ProdId;
	}
	public void setProdId(String prodId) {
		ProdId = prodId;
	}
	public String getExtProdId() {
		return ExtProdId;
	}
	public void setExtProdId(String extProdId) {
		ExtProdId = extProdId;
	}
	public String getStockNum() {
		return StockNum;
	}
	public void setStockNum(String stockNum) {
		StockNum = stockNum;
	}
	public String getAvlStockNum() {
		return AvlStockNum;
	}
	public void setAvlStockNum(String avlStockNum) {
		AvlStockNum = avlStockNum;
	}
	public String getCurrency() {
		return Currency;
	}
	public void setCurrency(String currency) {
		Currency = currency;
	}
	public String getAccountNo() {
		return AccountNo;
	}
	public void setAccountNo(String accountNo) {
		AccountNo = accountNo;
	}
		
}