package com.systex.jbranch.platform.formatter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DIFormatter {

	/**
	 * Format Date String
	 * @param date 99999999
	 * @return 9999.99.99
	 */
	public String format(String date) {
		String newDate = "date length error!";
		if (date.length() == 8) 
			newDate = date.substring(0, 4)+"."+date.substring(4, 6)+"."+date.substring(6, 8);
		return newDate;
	}
	
	public static void main(String [] args) {
		DIFormatter df = new DIFormatter();
		System.out.println(df.format("99999999"));
	}
}
