package com.systex.jbranch.app.server.fps.sot620;

import java.math.BigDecimal;
import java.util.Map;

import com.systex.jbranch.app.server.fps.sot714.WMSHACRDataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT620InputVO extends PagingInputVO{
	
	private String custID;
	private String prodType;
	private String prodID;
	private String prodName;
	private BigDecimal buyAmt;
	private String currCode;
	private Map<String, Object> addProdData;
	private WMSHACRDataVO trialRateData;
	
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getProdType() {
		return prodType;
	}
	public void setProdType(String prodType) {
		this.prodType = prodType;
	}
	public String getProdID() {
		return prodID;
	}
	public void setProdID(String prodID) {
		this.prodID = prodID;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public BigDecimal getBuyAmt() {
		return buyAmt;
	}
	public void setBuyAmt(BigDecimal buyAmt) {
		this.buyAmt = buyAmt;
	}
	public String getCurrCode() {
		return currCode;
	}
	public void setCurrCode(String currCode) {
		this.currCode = currCode;
	}
	public Map<String, Object> getAddProdData() {
		return addProdData;
	}
	public void setAddProdData(Map<String, Object> addProdData) {
		this.addProdData = addProdData;
	}
	public WMSHACRDataVO getTrialRateData() {
		return trialRateData;
	}
	public void setTrialRateData(WMSHACRDataVO trialRateData) {
		this.trialRateData = trialRateData;
	}
	
}
