package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.math.BigDecimal;
import java.util.List;

public class GetBItemListOutputVO {

	public GetBItemListOutputVO() {
		super();
	}

	private String insCustID;		// 客戶ID
	private String yearROC;			// 民國年
	private BigDecimal yearFee;		// 家庭年保險金額
	private BigDecimal yearReturn;	// 家庭年還本金額
	private List lstBitem;			// 家庭成員各類保障

	public String getInsCustID() {
		return insCustID;
	}
	public void setInsCustID(String insCustID) {
		this.insCustID = insCustID;
	}
	public String getYearROC() {
		return yearROC;
	}
	public void setYearROC(String yearROC) {
		this.yearROC = yearROC;
	}
	public BigDecimal getYearFee() {
		return yearFee;
	}
	public void setYearFee(BigDecimal yearFee) {
		this.yearFee = yearFee;
	}
	public BigDecimal getYearReturn() {
		return yearReturn;
	}
	public void setYearReturn(BigDecimal yearReturn) {
		this.yearReturn = yearReturn;
	}
	public List getLstBitem() {
		return lstBitem;
	}
	public void setLstBitem(List lstBitem) {
		this.lstBitem = lstBitem;
	}
}
