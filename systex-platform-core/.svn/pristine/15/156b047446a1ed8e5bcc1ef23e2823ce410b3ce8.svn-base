package com.systex.jbranch.platform.formatter;

public class DJFormatter {
	/**
	 * Format Date String
	 * @param date 9999999
	 * @return 999.99.99
	 */
	public String format(String date) {
		String newDate = "date length error!";
		if (date.length() == 7) 
			newDate = date.substring(0, 3)+"."+date.substring(3, 5)+"."+date.substring(5, 7);
		return newDate;
	}
	
	public static void main(String [] args) {
		DJFormatter dj = new DJFormatter();
		System.out.println(dj.format("9999999"));
	}
}
