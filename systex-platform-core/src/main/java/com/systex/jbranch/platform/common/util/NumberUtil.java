package com.systex.jbranch.platform.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberUtil {

	public static String format(int number, String pattern) {
		NumberFormat nf = new DecimalFormat(pattern);
		return nf.format(number);
	}
	
	public static String format(BigDecimal number, String pattern) {
		NumberFormat nf = new DecimalFormat(pattern);
		return nf.format(number);
	}

	public static Number parse(String number, String pattern) throws ParseException {
		NumberFormat nf = new DecimalFormat(pattern);
		return nf.parse(number);
	}
}
