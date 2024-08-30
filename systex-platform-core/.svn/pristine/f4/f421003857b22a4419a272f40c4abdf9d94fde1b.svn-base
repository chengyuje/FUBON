package com.systex.jbranch.platform.formatter;

public class P7Formatter {
	/**
	 * Format trmseq String
	 * @param trmseq 99999
	 * @return 999-99
	 */
	public String format(String trmseq) {
		String newTrmseq = "date length error!";
		if (trmseq.length() == 5) 
			newTrmseq = trmseq.substring(0, 3)+"-"+trmseq.substring(3, 5);
		return newTrmseq;
	}
	
	public static void main(String [] args) {
		P7Formatter p7 = new P7Formatter();
		System.out.println(p7.format("99999"));
	}
}
