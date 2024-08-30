package com.systex.jbranch.app.server.fps.sot312;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT312InputVO extends PagingInputVO{
	
	private String custID;
	private String tradeType;
	private String prodID;
	private Date sDate;
	private Date eDate;
	private String GtcNo;
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
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
	public String getGtcNo() {
		return GtcNo;
	}
	public void setGtcNo(String gtcNo) {
		GtcNo = gtcNo;
	}
	
}
