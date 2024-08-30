package com.systex.jbranch.app.server.fps.sot701.ruler;

import java.util.Set;

/**
 * @author Eli
 * @date 20180822
 *
 */
public abstract class AcctRuler {
	protected String acct;
	protected String acctCat;
	protected String prodType;
	protected String wa_x_type;
	protected String wa_x_icat;
	
	/**
	 * 區分帳號前須設置參數
	 * @param acct
	 * @param acctCat
	 * @param prodType
	 */
	public void setInfo(String acct, String acctCat, String prodType) {
		this.acct = acct;
		this.acctCat = acctCat;
		this.prodType = prodType;
	}
	
	/**
	 * #0978
	 * 區分帳號前須設置參數
	 * @param acct
	 * @param acctCat
	 * @param prodType
	 * @param wa_x_type
	 * @param wa_x_icat
	 */
	public void setInfo(String acct, String acctCat, String prodType, String wa_x_type, String wa_x_icat) {
		this.acct = acct;
		this.acctCat = acctCat;
		this.prodType = prodType;
		this.wa_x_type = wa_x_type;
		this.wa_x_icat = wa_x_icat;
	}

	/**
	 *  依照規則區分為信託帳號與扣款帳號
	 * @param trustAcctList
	 * @param debitAcctList
	 * @throws Exception
	 */
	public void classifyAcct(Set trustAcctList, Set debitAcctList) throws Exception {
		if (exclude()) return;
		if (fitTrust()) trustAcctList.add(acct);
		if (fitDebit()) debitAcctList.add(acct);
	}
	
	/** 帳號符合下列條件者，將不選擇 **/
	protected abstract boolean exclude(); 
	
	protected abstract String pkind1();
	protected abstract String pkind2();
	protected abstract String pkind3();
	/** 信託帳號處理邏輯 **/
	protected abstract boolean fitTrust();
	/** 扣款帳號處理邏輯 **/
	protected abstract boolean fitDebit(); 
}
