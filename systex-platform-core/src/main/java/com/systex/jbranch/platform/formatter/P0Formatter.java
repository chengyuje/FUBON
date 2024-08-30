package com.systex.jbranch.platform.formatter;

public class P0Formatter {
	/**
	 * Format account String
	 * @param date 999999999999
	 * @return 999-999-99999-9
	 */
	public String format(String account) {
		String newDate = "date length error!";
		if (account.length() == 12) 
			newDate = account.substring(0, 3)+"-"+account.substring(3, 6)
			+"-"+account.substring(6, 11)+"-"+account.substring(11);
		return newDate;
	}
	
	public static void main(String [] args) {
		P0Formatter p0 = new P0Formatter();
		System.out.println(p0.format("999999999999"));
	}
}
