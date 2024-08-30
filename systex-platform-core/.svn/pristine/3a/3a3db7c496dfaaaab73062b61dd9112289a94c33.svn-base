package com.systex.jbranch.platform.common.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

public class DateUtil {

	public static String format(String pattern) {
		
		return DateFormatUtils.format(Calendar.getInstance(), pattern);
	}
	
	public static String format(Calendar cal, String pattern) {
		return DateFormatUtils.format(cal, pattern);
	}

	public static String format(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}

	public static String format(long millis, String pattern) {
		return DateFormatUtils.format(millis, pattern);
	}

	public static Date parseDate(String date, String pattern)
			throws ParseException {
		int rocYearStart = pattern.indexOf('Y');
		if(rocYearStart != -1){
			int rocYearEnd = pattern.lastIndexOf('Y');
			String rocYearString = date.substring(rocYearStart, rocYearEnd + 1);
			String rocYearPattern = pattern.substring(rocYearStart, rocYearEnd + 1);
			int adYear = Integer.parseInt(rocYearString) + 1911;
			String md = date.replace(rocYearString, "");//月日
			String mdPattern = pattern.replace(rocYearPattern, "");
			String adDate = adYear + md;

			return DateUtils.parseDate(adDate, new String[]{"yyyy" + mdPattern});
		}
		return DateUtils.parseDate(date, new String[] { pattern });
	}

	public static Date replaceTime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		for(int type : new int[]{Calendar.HOUR_OF_DAY , Calendar.MINUTE , Calendar.SECOND , Calendar.MILLISECOND})
			cal.set(type , 0);
		return cal.getTime();
	}
}
