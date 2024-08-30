package com.systex.jbranch.app.server.fps.insjlb.ws.client.vo;
public class PolicyColumn {
	private java.lang.String PayType;
	private short SignYY;
	private short SignMM;
	private short SignDD;
	private java.lang.String PolicyNO;
	private java.lang.String PolicyID;
	private java.lang.String PolicyStatus;
	public void setPayType (java.lang.String PayType){
		this.PayType = PayType;
	}
	public java.lang.String getPayType(){
		return this.PayType;
	}
	public void setSignYY (short SignYY){
		this.SignYY = SignYY;
	}
	public short getSignYY(){
		return this.SignYY;
	}
	public void setSignMM (short SignMM){
		this.SignMM = SignMM;
	}
	public short getSignMM(){
		return this.SignMM;
	}
	public void setSignDD (short SignDD){
		this.SignDD = SignDD;
	}
	public short getSignDD(){
		return this.SignDD;
	}
	public void setPolicyNO (java.lang.String PolicyNO){
		this.PolicyNO = PolicyNO;
	}
	public java.lang.String getPolicyNO(){
		return this.PolicyNO;
	}
	public void setPolicyID (java.lang.String PolicyID){
		this.PolicyID = PolicyID;
	}
	public java.lang.String getPolicyID(){
		return this.PolicyID;
	}
	public void setPolicyStatus (java.lang.String PolicyStatus){
		this.PolicyStatus = PolicyStatus;
	}
	public java.lang.String getPolicyStatus(){
		return this.PolicyStatus;
	}
}