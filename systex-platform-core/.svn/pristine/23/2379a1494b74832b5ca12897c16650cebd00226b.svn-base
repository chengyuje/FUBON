package com.systex.jbranch.platform.formatter;

public class T3Formatter {
	/**
	 * Format Date String
	 * @param date 999999
	 * @return 99:99:99
	 */
	public String format(String date) {
		String newDate = "date length error!";
		if (date.length() == 6) 
			newDate = date.substring(0, 2)+":"+date.substring(2, 4)+":"+date.substring(4, 6);
		return newDate;
	}
	
	public static void main(String [] args) {
		T3Formatter t3 = new T3Formatter();
		System.out.println(t3.format("999999"));
	}
}
