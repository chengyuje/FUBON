package com.systex.jbranch.app.server.fps.insjlb.ws.client.vo;
public class AllPolicyOBJ {
	private java.lang.String PolicyID;
	private short LastNUM;
	private java.lang.String CustPolicy;
	private short CountNUM;
	private short StartAge;
	private java.lang.String TableName;
	public void setPolicyID (java.lang.String PolicyID){
		this.PolicyID = PolicyID;
	}
	public java.lang.String getPolicyID(){
		return this.PolicyID;
	}
	public void setLastNUM (short LastNUM){
		this.LastNUM = LastNUM;
	}
	public short getLastNUM(){
		return this.LastNUM;
	}
	public void setCustPolicy (java.lang.String CustPolicy){
		this.CustPolicy = CustPolicy;
	}
	public java.lang.String getCustPolicy(){
		return this.CustPolicy;
	}
	public void setCountNUM (short CountNUM){
		this.CountNUM = CountNUM;
	}
	public short getCountNUM(){
		return this.CountNUM;
	}
	public void setStartAge (short StartAge){
		this.StartAge = StartAge;
	}
	public short getStartAge(){
		return this.StartAge;
	}
	public void setTableName (java.lang.String TableName){
		this.TableName = TableName;
	}
	public java.lang.String getTableName(){
		return this.TableName;
	}
}