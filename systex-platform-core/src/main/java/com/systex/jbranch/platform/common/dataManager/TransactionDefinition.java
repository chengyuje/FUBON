package com.systex.jbranch.platform.common.dataManager;
/**
 *　存放各交易所需要的資料。<BR>
 * @author Eric.Lin
 *
 */

public class TransactionDefinition{
	private String 	txnCode="";
	private String 	txnName="";
	private String  sysType="";
	private String  crdb   ="";
	private String  jrnType =""; 
	
    public String getJrnType() {
		return jrnType;
	}
	public void setJrnType(String jrnType) {
		this.jrnType = jrnType;
	}
	public TransactionDefinition(){
    	
    }
	public String getTxnCode() {
		return txnCode;
	}

	public void setTxnCode(String txnCode) {
		this.txnCode = txnCode;
	}

	public String getTxnName() {
		return txnName;
	}

	public void setTxnName(String txnName) {
		this.txnName = txnName;
	}

	public String getSysType() {
		return sysType;
	}

	public void setSysType(String sysType) {
		this.sysType = sysType;
	}

	public String getCrdb() {
		return crdb;
	}

	public void setCrdb(String crdb) {
		this.crdb = crdb;
	}	
}
