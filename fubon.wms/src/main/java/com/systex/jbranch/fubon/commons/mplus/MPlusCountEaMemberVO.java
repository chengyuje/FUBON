package com.systex.jbranch.fubon.commons.mplus;

import com.systex.jbranch.fubon.commons.mplus.cons.MPlusCons;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.MultipartEntity;
import com.systex.jbranch.fubon.commons.mplus.multipartEmpty.SecretKeyType;

public class MPlusCountEaMemberVO implements MPlusInputVOInf{
	//企業帳號
	private String account;
	private String secretKey;
	
	/* 企業帳號密碼
	 * 需加密*/
	@MultipartEntity(secretKeyType = SecretKeyType.METHOD , secretKeyValue = MPlusCons.GET_AES_KEY_MEHOTD)
	private String password;

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

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
