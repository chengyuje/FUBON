package com.systex.jbranch.app.server.fps.sot703;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT703OutputVO extends PagingOutputVO {
	private List astList1;		//單筆庫存
	private List astList2;		//定期定額庫存
	private List astList3;		//定期不定額庫存
	private List astList4;		//定存轉基金庫存(已下架，但仍可能有庫存)
	private List astList5;		//基金套餐庫存(已下架，但仍可能有庫存)
	private String errorCode;	//錯誤代碼
	private String errorMsg;	//錯誤訊息
	private String ExchangeCurr;//基金變更[換匯電文]回傳 幣別
	private String ExchangeAmt1;//基金變更[換匯電文]回傳 庫存金額1
	private String ExchangeAmt2;//基金變更[換匯電文]回傳 庫存金額2
	private String ExchangeAmt3;//基金變更[換匯電文]回傳 庫存金額3
	private List custAssetFundList;
	
	/*
	 * 判斷是否為短期交易(SOT130)
	 * add by Brian
	 */
	private String Short_1; 
	private String Short_2; 
	private String Short_3; 
	/*
	 * 判斷是否為短期交易(SOT140)
	 * add by Brian
	 */
	private String SHORT_1; 
	private String SHORT_2; 
	
	public List getAstList1() {
		return astList1;
	}
	public void setAstList1(List astList1) {
		this.astList1 = astList1;
	}
	public List getAstList2() {
		return astList2;
	}
	public void setAstList2(List astList2) {
		this.astList2 = astList2;
	}
	public List getAstList3() {
		return astList3;
	}
	public void setAstList3(List astList3) {
		this.astList3 = astList3;
	}
	public List getAstList4() {
		return astList4;
	}
	public void setAstList4(List astList4) {
		this.astList4 = astList4;
	}
	public List getAstList5() {
		return astList5;
	}
	public void setAstList5(List astList5) {
		this.astList5 = astList5;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	 
	public String getExchangeAmt1() {
		return ExchangeAmt1;
	}
	public void setExchangeAmt1(String exchangeAmt1) {
		ExchangeAmt1 = exchangeAmt1;
	}
	public String getExchangeAmt2() {
		return ExchangeAmt2;
	}
	public void setExchangeAmt2(String exchangeAmt2) {
		ExchangeAmt2 = exchangeAmt2;
	}
	public String getExchangeAmt3() {
		return ExchangeAmt3;
	}
	public void setExchangeAmt3(String exchangeAmt3) {
		ExchangeAmt3 = exchangeAmt3;
	}
	public List getCustAssetFundList() {
		return custAssetFundList;
	}
	public void setCustAssetFundList(List custAssetFundList) {
		this.custAssetFundList = custAssetFundList;
	}
	
	public String getExchangeCurr() {
		return ExchangeCurr;
	}
	public void setExchangeCurr(String exchangeCurr) {
		ExchangeCurr = exchangeCurr;
	}
	
	public String getShort_1() {
		return Short_1;
	}
	public void setShort_1(String short_1) {
		Short_1 = short_1;
	}
	public String getShort_2() {
		return Short_2;
	}
	public void setShort_2(String short_2) {
		Short_2 = short_2;
	}
	public String getShort_3() {
		return Short_3;
	}
	public void setShort_3(String short_3) {
		Short_3 = short_3;
	}
	
	public String getSHORT_1() {
		return SHORT_1;
	}
	public void setSHORT_1(String sHORT_1) {
		SHORT_1 = sHORT_1;
	}
	public String getSHORT_2() {
		return SHORT_2;
	}
	public void setSHORT_2(String sHORT_2) {
		SHORT_2 = sHORT_2;
	}
	@Override
	public String toString() {
		return "SOT703OutputVO [astList1=" + astList1 + ", astList2="
				+ astList2 + ", astList3=" + astList3 + ", astList4="
				+ astList4 + ", astList5=" + astList5 + ", errorCode="
				+ errorCode + ", errorMsg=" + errorMsg + ", ExchangeAmt1=" + ExchangeAmt1
				+ ", ExchangeAmt2=" + ExchangeAmt2 + ", ExchangeAmt3=" + ExchangeAmt3 + ", ExchangeCurr=" + ExchangeCurr + ", custAssetFundList="
				+ custAssetFundList + "]";
	}
}
