package com.systex.jbranch.app.server.fps.sot330;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * SOT330 海外債成交結果查詢
 * 
 * @author SamTu
 * @date 2020.11.05
 */
public class SOT330InputVO extends PagingInputVO {

	private String custID; //客戶ID
	private String tradeType; //交易種類 B:申購 S:贖回
	private String prodID; //標的名稱
	private Date sDate; //起日
	private Date eDate; //迄日
	
	private Boolean isOBU;
	
	public Boolean getIsOBU() {
		return isOBU;
	}
	public void setIsOBU(Boolean isOBU) {
		this.isOBU = isOBU;
	}
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

	
}
