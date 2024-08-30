package com.systex.jbranch.platform.formatter;

public class P5Formatter {
	/**
	 * Format account String
	 * @param account 9999999
	 * @return 999-999-9
	 */
	public String format(String account) {
		String newAccount = "date length error!";
		if (account.length() == 7) 
			newAccount = account.substring(0, 3)+"-"+account.substring(3, 6)
			+"-"+account.substring(6);
		return newAccount;
	}
	
	public static void main(String [] args) {
		P5Formatter p5 = new P5Formatter();
		System.out.println(p5.format("9999999"));
	}
}
