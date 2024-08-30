package com.systex.jbranch.app.server.fps.sot705;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/9/21.
 */
public class CustAssetETFMonVO {
	private String ContractId; //契約編號	
	private String ProdId; //金錢信託商品代碼	
	private String ExtProdId; //外部股票商品代碼
	private BigDecimal StockNum; //庫存股數
	private BigDecimal AvlStockNum; //可贖股數
	private String Currency; //交易幣別
	private String AccountNo; //交易幣主運用帳號
	private String PrdType; //E:ETF S:Stock
	private String ProdName;
	
	
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
	public BigDecimal getStockNum() {
		return StockNum;
	}
	public void setStockNum(BigDecimal stockNum) {
		StockNum = stockNum;
	}
	public BigDecimal getAvlStockNum() {
		return AvlStockNum;
	}
	public void setAvlStockNum(BigDecimal avlStockNum) {
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
	public String getPrdType() {
		return PrdType;
	}
	public void setPrdType(String prdType) {
		PrdType = prdType;
	}
	public String getProdName() {
		return ProdName;
	}
	public void setProdName(String prodName) {
		ProdName = prodName;
	}
}
