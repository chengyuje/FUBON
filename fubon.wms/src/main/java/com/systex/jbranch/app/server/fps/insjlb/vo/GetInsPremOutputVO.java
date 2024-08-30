package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.math.BigDecimal;
import java.util.List;

public class GetInsPremOutputVO {
	private String insco;//保險公司編號
	private String insco_name;//保險公司名稱
	private String prod_id;//商品代號
	private String prod_name;//商品名稱
	private String premterm;//繳費年其
	private short year;//保險年度
	private String quantity;//保額
	private BigDecimal premium;//年化保費
	private BigDecimal repay;//當年度生存還本金
	private String payType; // 繳別
	private List lstCoverAgePrem;//逐年保障領回清單
	private List lstExpression;//給付項目
	
	private String prodKeyno;// 資訊源商品代碼
	private String coverunit;// 保額單位
	private List lstLogTable; //錯誤訊息
	
	public String getInsco() {
		return insco;
	}
	public void setInsco(String insco) {
		this.insco = insco;
	}
	public String getInsco_name() {
		return insco_name;
	}
	public void setInsco_name(String insco_name) {
		this.insco_name = insco_name;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public String getPremterm() {
		return premterm;
	}
	public void setPremterm(String premterm) {
		this.premterm = premterm;
	}
	public short getYear() {
		return year;
	}
	public void setYear(short year) {
		this.year = year;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getPremium() {
		return premium;
	}
	public void setPremium(BigDecimal premium) {
		this.premium = premium;
	}
	public BigDecimal getRepay() {
		return repay;
	}
	public void setRepay(BigDecimal repay) {
		this.repay = repay;
	}
	public List getLstCoverAgePrem() {
		return lstCoverAgePrem;
	}
	public void setLstCoverAgePrem(List lstCoverAgePrem) {
		this.lstCoverAgePrem = lstCoverAgePrem;
	}
	public String getProdKeyno() {
		return prodKeyno;
	}
	public void setProdKeyno(String prodKeyno) {
		this.prodKeyno = prodKeyno;
	}
	public String getCoverunit() {
		return coverunit;
	}
	public void setCoverunit(String coverunit) {
		this.coverunit = coverunit;
	}
	public List getLstLogTable() {
		return lstLogTable;
	}
	public void setLstLogTable(List lstLogTable) {
		this.lstLogTable = lstLogTable;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public List getLstExpression() {
		return lstExpression;
	}
	public void setLstExpression(List lstExpression) {
		this.lstExpression = lstExpression;
	}

	
	
}
