package com.systex.jbranch.platform.formatter;

public class P2Formatter {
	/**
	 * Format ID String
	 * @param date A123456789C
	 * @return A123456789C
	 */
	public String format(String id) {
		String newID = "date length error!";
		if (id.length() == 11) 
			newID = id.substring(0, 10)+"-"+id.substring(10);
		else if (id.length() == 10)
			newID = id;
		return newID;
	}
	
	public static void main(String [] args) {
		P2Formatter p2 = new P2Formatter();
		System.out.println(p2.format("A123456789"));
		System.out.println(p2.format("A123456789C"));
	}
}
