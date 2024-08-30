package com.systex.jbranch.platform.formatter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.math.NumberUtils;

public abstract class DateFormatter implements IDateFormatter {
	
	public enum DataType
	{
		N,
		X
	}
	
	protected SimpleDateFormat dateFormat;
	protected DataType datatype;

	public DateFormatter(String pattern,DataType datatype)
	{
		dateFormat=new SimpleDateFormat(pattern);
		this.datatype=datatype;
	}
	
	protected abstract String formatNull();

	public String format(Date date)
	{
		if(date==null)
			return formatNull();

		return dateFormat.format(date);
	}
	
	public Timestamp parse(String value)throws ParseException
	{
		try
		{
			if(value==null)
				return null;
			
			value=value.trim();
			
			if(NumberUtils.isNumber(value) && Integer.valueOf(value).intValue()==0)
			{
				return null;
			}
			else if("".equals(value))
			{
				return null;
			}
			return new Timestamp(dateFormat.parse(value).getTime());
		}
		catch(Exception e)
		{
			throw new ParseException(e);
		}
	}
}