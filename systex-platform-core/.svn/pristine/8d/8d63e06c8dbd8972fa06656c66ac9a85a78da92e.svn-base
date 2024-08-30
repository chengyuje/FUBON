package com.systex.jbranch.platform.formatter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class T1Formatter implements IDateFormatter {

	public String format(Date date) 
	{
		if(date!=null)
		{
			Calendar calendar=Calendar.getInstance();
			
			calendar.setTimeInMillis(date.getTime());
			String str=String.format("%d/%02d/%02d-%02d:%02d:%02d",
					calendar.get(Calendar.YEAR)-1911,
					calendar.get(Calendar.MONTH)+1,
					calendar.get(Calendar.DATE),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND));
			
			return StringUtils.leftPad(str, 18);
		}
		return "";
	}

	public Timestamp parse(String value) throws ParseException 
	{
		value=StringUtils.trim(value);
		if(value!=null)
		{
			value=StringUtils.replaceChars(value, "/-: " ,"" );
			value=StringUtils.leftPad(value, 13, "0");
			
			if(NumberUtils.isNumber(value) && Long.valueOf(value).longValue()==0)
				return null;
			
			int year=Integer.valueOf(StringUtils.substring(value, 0, 3)).intValue();
			int month=Integer.valueOf(StringUtils.substring(value, 3, 5)).intValue();
			int date=Integer.valueOf(StringUtils.substring(value, 5, 7)).intValue();
			int hour=Integer.valueOf(StringUtils.substring(value, 7, 9)).intValue();
			int minute=Integer.valueOf(StringUtils.substring(value, 9, 11)).intValue();
			int second=Integer.valueOf(StringUtils.substring(value, 11, 13)).intValue();
			
			Calendar calendar=Calendar.getInstance();
			
			calendar.set(Calendar.YEAR, year+1911);
			calendar.set(Calendar.MONTH, month-1);
			calendar.set(Calendar.DATE, date);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, second);
			
			return new Timestamp(calendar.getTimeInMillis());
		}
		return null;
	}

}
