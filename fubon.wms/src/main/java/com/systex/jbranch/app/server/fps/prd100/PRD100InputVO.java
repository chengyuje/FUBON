package com.systex.jbranch.app.server.fps.prd100;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD100InputVO extends PagingInputVO {
	private String bossEmpID;
	private String bossEmpPWD;
	private String prodType;
	private String tradeType;
	private String seniorAuthType;
	private String custID;
	private String matchDate;
	private String invalidMsgC;
	private String invalidMsgF;
	private String trustTS;
	
	public String getBossEmpID() {
		return bossEmpID;
	}
	public void setBossEmpID(String bossEmpID) {
		this.bossEmpID = bossEmpID;
	}
	public String getBossEmpPWD() {
		return bossEmpPWD;
	}
	public void setBossEmpPWD(String bossEmpPWD) {
		this.bossEmpPWD = bossEmpPWD;
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
	public String getSeniorAuthType() {
		return seniorAuthType;
	}
	public void setSeniorAuthType(String seniorAuthType) {
		this.seniorAuthType = seniorAuthType;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getMatchDate() {
		return matchDate;
	}
	public void setMatchDate(String matchDate) {
		this.matchDate = matchDate;
	}
	public String getInvalidMsgC() {
		return invalidMsgC;
	}
	public void setInvalidMsgC(String invalidMsgC) {
		this.invalidMsgC = invalidMsgC;
	}
	public String getInvalidMsgF() {
		return invalidMsgF;
	}
	public void setInvalidMsgF(String invalidMsgF) {
		this.invalidMsgF = invalidMsgF;
	}
	public String getTrustTS() {
		return trustTS;
	}
	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
	
}
