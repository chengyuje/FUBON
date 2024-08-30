package com.systex.jbranch.app.server.fps.sot130;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT130OutputVO extends PagingOutputVO {
	private String tradeSEQ;
	private List<Map<String, Object>> custDTL;
	private List<Map<String, Object>> cartList;
	private List<Map<String, Object>> prodDTL;
	private List<Map<String, Object>> singleFeeRateList;
	private String errorMsg;
	private String warningMsg;
	
	private String isFirstTrade;
	private String ageUnder70Flag;
	private String eduJrFlag;
	private String healthFlag;
	private String custRemarks;		//客戶註記
	private boolean recNeeded; 		//是否需錄音
	private String isBanker;		//是否行員
	/*
	 * 判斷是否為短期交易
	 * add by Brian
	 */
	private String Short_1; 
	private String Short_2; 
	private String Short_3; 
	
	public boolean getRecNeeded() {
		return recNeeded;
	}
	public void setRecNeeded(boolean recNeeded) {
		this.recNeeded = recNeeded;
	}
	public List<Map<String, Object>> getSingleFeeRateList() {
		return singleFeeRateList;
	}
	public void setSingleFeeRateList(List<Map<String, Object>> singleFeeRateList) {
		this.singleFeeRateList = singleFeeRateList;
	}
	public String getIsFirstTrade() {
		return isFirstTrade;
	}
	public void setIsFirstTrade(String isFirstTrade) {
		this.isFirstTrade = isFirstTrade;
	}
	public String getAgeUnder70Flag() {
		return ageUnder70Flag;
	}
	public void setAgeUnder70Flag(String ageUnder70Flag) {
		this.ageUnder70Flag = ageUnder70Flag;
	}
	public String getEduJrFlag() {
		return eduJrFlag;
	}
	public void setEduJrFlag(String eduJrFlag) {
		this.eduJrFlag = eduJrFlag;
	}
	public String getHealthFlag() {
		return healthFlag;
	}
	public void setHealthFlag(String healthFlag) {
		this.healthFlag = healthFlag;
	}	
	public String getCustRemarks() {
		return custRemarks;
	}
	public void setCustRemarks(String custRemarks) {
		this.custRemarks = custRemarks;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public String getWarningMsg() {
		return warningMsg;
	}
	public void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}
	public String getTradeSEQ() {
		return tradeSEQ;
	}
	public void setTradeSEQ(String tradeSEQ) {
		this.tradeSEQ = tradeSEQ;
	}
	public List<Map<String, Object>> getCustDTL() {
		return custDTL;
	}
	public void setCustDTL(List<Map<String, Object>> custDTL) {
		this.custDTL = custDTL;
	}
	public List<Map<String, Object>> getCartList() {
		return cartList;
	}
	public void setCartList(List<Map<String, Object>> cartList) {
		this.cartList = cartList;
	}
	public List<Map<String, Object>> getProdDTL() {
		return prodDTL;
	}
	public void setProdDTL(List<Map<String, Object>> prodDTL) {
		this.prodDTL = prodDTL;
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
	public String getIsBanker() {
		return isBanker;
	}
	public void setIsBanker(String isBanker) {
		this.isBanker = isBanker;
	}
}
