package com.systex.jbranch.platform.formatter;

public class P3Formatter {
	/**
	 * Format uniID String
	 * @param uniID 123456789C
	 * @return 123456789C
	 */
	public String format(String uniID) {
		String newUniID = "date length error!";
		if (uniID.length() == 9) 
			newUniID = uniID.substring(0, 8)+"-"+uniID.substring(8);
		else if (uniID.length() == 8)
			newUniID = uniID;
		return newUniID;
	}
	
	public static void main(String [] args) {
		P3Formatter p3 = new P3Formatter();
		System.out.println(p3.format("12345678"));
		System.out.println(p3.format("12345678C"));
	}
}
