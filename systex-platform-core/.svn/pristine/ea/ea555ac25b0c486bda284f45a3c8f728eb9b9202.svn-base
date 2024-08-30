package com.systex.jbranch.platform.formatter;

import com.systex.jbranch.platform.common.util.StringUtil;

public class P1Formatter {
	/**
	 * Format account String
	 * @param account 999999999999999
	 * @return 999-999-99999-9
	 */
	public String format(String account) {
		String newAccount = account;
		if (account.length() > 15) {
			account = account.substring(0, 15);
		}else if(account.length() < 15){
			account = StringUtil.rightPad(account, 15, " ");
		}
			newAccount = account.substring(0, 3)+"-"+account.substring(3, 7)
			+"-"+account.substring(7, 12)+"-"+account.substring(12,13)+"-"+account.substring(13);
		return newAccount;
	}
	
	public static void main(String [] args) {
		P1Formatter p1 = new P1Formatter();
		System.out.println(p1.format("999999999999999"));
	}
}
