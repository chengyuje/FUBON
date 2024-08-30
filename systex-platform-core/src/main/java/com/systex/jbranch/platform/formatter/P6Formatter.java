package com.systex.jbranch.platform.formatter;

public class P6Formatter {
	/**
	 * Format accNo String
	 * @param accNo XXXXXXXXX
	 * @return XXXX-XX-XX-X
	 */
	public String format(String accNo) {
		String newAccNo = "date length error!";
		if (accNo.length() == 9) 
			newAccNo = accNo.substring(0, 4)+"-"+accNo.substring(4, 6)
			+"-"+accNo.substring(6, 8)+"-"+accNo.substring(8);
		return newAccNo;
	}
	
	public static void main(String [] args) {
		P6Formatter p6 = new P6Formatter();
		System.out.println(p6.format("XXXXXXXXX"));
	}
}
