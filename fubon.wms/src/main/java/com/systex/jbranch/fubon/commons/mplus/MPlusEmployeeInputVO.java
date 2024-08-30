package com.systex.jbranch.fubon.commons.mplus;

import org.springframework.beans.factory.annotation.Autowired;


abstract public class MPlusEmployeeInputVO implements MPlusInputVOInf{
	//企業帳號
	private String account;
	
	/* 企業帳號密碼*/
	private String password;
	
	private String secretKey;
	
	/* 比對員工資料的鍵值
	 * 1: By email
	 * 2: By 員工編號 
	 * action=modify 必填*/
	private String alterKey;


	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAlterKey() {
		return alterKey;
	}
	public void setAlterKey(String alterKey) {
		this.alterKey = alterKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	
	public void setSecretKey(String secretKey){
		this.secretKey = secretKey;
	}
}
