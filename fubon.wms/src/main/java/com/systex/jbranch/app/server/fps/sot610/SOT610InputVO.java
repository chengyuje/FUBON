package com.systex.jbranch.app.server.fps.sot610;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT610InputVO extends PagingInputVO{
	
	private String custID;
	private String prodType;
	private String tradeType;
	private String tradeStatus;
	private String isBaraginNeeded;
	private String bargainFeeFlag;
	private String prodID;
	private Date sDate;
	private Date eDate;
	private String tradeSeq; //下單交易序號

	private String trustTradeType;
		
	public String getTrustTradeType() {
		return trustTradeType;
	}

	public void setTrustTradeType(String trustTradeType) {
		this.trustTradeType = trustTradeType;
	}
	
	public String getIsBaraginNeeded() {
		return isBaraginNeeded;
	}

	public void setIsBaraginNeeded(String isBaraginNeeded) {
		this.isBaraginNeeded = isBaraginNeeded;
	}

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
	
	public String getTradeType() {
		return tradeType;
	}
	
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	public String getTradeStatus() {
		return tradeStatus;
	}
	
	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	
	public String getBargainFeeFlag() {
		return bargainFeeFlag;
	}
	
	public void setBargainFeeFlag(String bargainFeeFlag) {
		this.bargainFeeFlag = bargainFeeFlag;
	}
	
	public String getProdID() {
		return prodID;
	}
	
	public void setProdID(String prodID) {
		this.prodID = prodID;
	}
	
	public Date getsDate() {
		return sDate;
	}
	
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	
	public Date geteDate() {
		return eDate;
	}
	
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	
	public String getTradeSeq() {
		return tradeSeq;
	}

	public void setTradeSeq(String tradeSeq) {
		this.tradeSeq = tradeSeq;
	}
	
}
